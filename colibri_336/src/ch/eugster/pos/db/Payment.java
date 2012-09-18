/*
 * Created on 04.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.db;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.jdom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import ch.eugster.pos.InvalidValueException;
import ch.eugster.pos.statistics.data.Translator;
import ch.eugster.pos.util.NumberUtility;
import ch.eugster.pos.util.XMLLoader;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Payment extends ReceiptChild
{
	
	/*
	 * der auf den Rundungsfaktor der Landeswährung gerundete Betrag
	 */
	private double amount = Table.DOUBLE_DEFAULT_ZERO;
	/*
	 * der genaue eingegebene Betrag, nach dem Umrechnungsfaktor 'quotation' in
	 * Landeswährung umgerechnet
	 */
	private double amountFC = Table.DOUBLE_DEFAULT_ZERO;
	/*
	 * Der Umrechnungfaktor - der Kurs der Währung - von der Landes- währung aus
	 * gesehen, der zur Umrechnung benutzt wurde
	 */
	private double quotation = Table.DOUBLE_DEFAULT_ONE;
	/*
	 * Der Rundungsfaktor, der für die Landeswährung zum Zeitpunkt der Erfassung
	 * galt
	 */
	private double roundFactor = 0.01d;
	/*
	 * Der Rundungsfaktor, der für die Fremdwährung zum Zeitpunkt der Erfassung
	 * galt
	 */
	private double roundFactorFC = 0.01d;
	
	public boolean isInputOrWithdraw = false;
	
	private boolean back = false; // 10226
	private Long settlement = null;
	private Long salespointId = null;
	
	private Long paymentTypeId = null;
	private PaymentType paymentType;
	
	private Long foreignCurrencyId = null;
	private ForeignCurrency foreignCurrency;
	
	/**
	 * 
	 */
	protected Payment()
	{
		super(Receipt.getEmptyReceipt());
	}
	
	/**
	 * 
	 */
	protected Payment(Receipt receipt)
	{
		super(receipt);
		this.init(PaymentType.getPaymentTypeCash());
	}
	
	/**
	 * 
	 */
	protected Payment(Receipt receipt, PaymentType type)
	{
		super(receipt);
		this.init(type);
	}
	
	public static Payment getEmptyInstance()
	{
		Payment payment = new Payment();
		payment.init(PaymentType.getPaymentTypeCash());
		return payment;
	}
	
	public static Payment getInstance()
	{
		return new Payment(Receipt.getEmptyReceipt());
	}
	
	public static Payment getInstance(Receipt receipt)
	{
		return new Payment(receipt);
	}
	
	public static Payment getInstance(Receipt receipt, PaymentType paymentType)
	{
		return new Payment(receipt, paymentType);
	}
	
	private void init(PaymentType paymentType)
	{
		this.setPaymentType(paymentType);
		this.setForeignCurrency(paymentType.getForeignCurrency());
		this.salespointId = this.receipt.getSalespointId();
	}
	
	public int compareTo(Object o)
	{
		Payment p = (Payment) o;
		return (int) p.getPaymentType().getId().longValue() - (int) this.getPaymentType().getId().longValue();
	}
	
	public void setAmount(double a)
	{
		this.amount = NumberUtility.round(a, this.roundFactor);
		this.amountFC = NumberUtility.round(a / this.quotation, this.roundFactorFC);
	}
	
	public double getAmount()
	{
		return this.amount;
	}
	
	public boolean isVoucher()
	{
		return this.paymentType.voucher;
	}
	
	public void setAmountFC(double a)
	{
		this.amountFC = NumberUtility.round(a, this.roundFactorFC);
		this.amount = NumberUtility.round(a * this.quotation, this.roundFactor);
	}
	
	public void setAmount(double amount, double amountFC)
	{
		this.amountFC = amountFC;
		this.amount = amount;
	}
	
	public double getAmountFC()
	{
		return this.amountFC;
	}
	
	public void setQuotation(double q)
	{
		this.quotation = q;
	}
	
	public double getQuotation()
	{
		return this.quotation;
	}
	
	/**
	 * @return
	 */
	public double getRoundFactor()
	{
		return this.roundFactor;
	}
	
	/**
	 * @return
	 */
	public double getRoundFactorFC()
	{
		return this.roundFactorFC;
	}
	
	// 10226
	public void setBack(boolean isMoneyBack)
	{
		this.back = isMoneyBack;
	}
	
	public boolean isBack()
	{
		return this.back;
	}
	
	public void setSettlement(Long settlement)
	{
		this.settlement = settlement;
	}
	
	public Long getSettlement()
	{
		return this.settlement;
	}
	
	// 10226
	
	public void setPaymentType(PaymentType type)
	{
		this.paymentType = type;
		this.paymentTypeId = type.getId();
		this.roundFactor = ForeignCurrency.getDefaultCurrency().roundFactor;
		this.setForeignCurrency(type.getForeignCurrency());
	}
	
	public PaymentType getPaymentType()
	{
		return this.paymentType;
	}
	
	public Long getPaymentTypeId()
	{
		return this.paymentTypeId;
	}
	
	public void setForeignCurrency(ForeignCurrency foreignCurrency)
	{
		this.foreignCurrency = foreignCurrency;
		this.foreignCurrencyId = foreignCurrency.getId();
		this.quotation = foreignCurrency.quotation;
		this.roundFactorFC = foreignCurrency.roundFactor;
	}
	
	public ForeignCurrency getForeignCurrency()
	{
		return this.foreignCurrency;
	}
	
	public Long getForeignCurrencyId()
	{
		return this.foreignCurrencyId;
	}
	
	public boolean isRemovable()
	{
		return true;
	}
	
	public static boolean exist(String key, Object value)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo(key, value);
		Query query = QueryFactory.newQuery(Payment.class, criteria);
		query.setEndAtIndex(1);
		Collection payments = Table.select(query);
		Iterator i = payments.iterator();
		return i.hasNext();
	}
	
	public static Payment selectById(Long pk)
	{
		Payment payment = new Payment();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("id", pk); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Payment.class, criteria);
		Collection payments = Table.select(query);
		Iterator i = payments.iterator();
		if (i.hasNext())
		{
			payment = (Payment) i.next();
		}
		return payment;
	}
	
	public static Payment[] selectByReceiptId(Long receiptId)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("receiptId", receiptId); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Payment.class, criteria);
		Collection payments = Table.select(query);
		return (Payment[]) payments.toArray(new Payment[0]);
	}
	
	public static Collection selectNotCounted(Salespoint salespoint, ForeignCurrency currency)
	{
		// String[] fields = new String[]
		// { "" };
		Criteria criteria = new Criteria();
		criteria.addColumnIsNull("settlement");
		criteria.addEqualTo("salespointId", salespoint.getId()); //$NON-NLS-1$
		criteria.addEqualTo("foreignCurrencyId", currency.getId()); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Payment.class, criteria);
		return Table.select(query);
	}
	
	public static boolean updateCounted(Collection payments, Long settlement)
	{
		if (!Payment.updatePayments(payments.iterator(), settlement)) return false;
		
		return true;
	}
	
	private static boolean updatePayments(Iterator iterator, Long settlement)
	{
		while (iterator.hasNext())
		{
			try
			{
				Payment payment = (Payment) iterator.next();
				payment.settlement = settlement;
				payment.store();
			}
			catch (Exception e)
			{
				return false;
			}
		}
		return true;
	}
	
	public static Payment[] selectAll()
	{
		Criteria criteria = new Criteria();
		Query query = QueryFactory.newQuery(Payment.class, criteria);
		Collection payments = Table.select(query);
		return (Payment[]) payments.toArray(new Payment[0]);
	}
	
	public static Iterator selectSettled(Salespoint[] salespoints, Date from, Date to)
	{
		String[] fields = new String[]
		{ /* 0 */"paymentType.id", //$NON-NLS-1$
						/* 1 */"paymentType.name", //$NON-NLS-1$
						/* 2 */"foreignCurrency.id", //$NON-NLS-1$
						/* 3 */"back", //$NON-NLS-1$
						/* 4 */"sum(amount)", //$NON-NLS-1$
						/* 5 */"sum(amountFC)", //$NON-NLS-1$
						/* 6 */"count(*)" //$NON-NLS-1$
		};
		
		int[] types = new int[]
		{
		/* 0 */Types.BIGINT,
		/* 1 */Types.VARCHAR,
		/* 2 */Types.BIGINT,
		/* 3 */Types.BIT,
		/* 4 */Types.DOUBLE,
		/* 5 */Types.DOUBLE,
		/* 6 */Types.INTEGER };
		
		Criteria criteria = new Criteria();
		
		if (salespoints != null)
		{
			if (salespoints.length > 0)
			{
				Criteria salespointCriteria = new Criteria();
				salespointCriteria.addEqualTo("receipt.salespointId", salespoints[0].getId()); //$NON-NLS-1$
				for (int i = 1; i < salespoints.length; i++)
				{
					Criteria orCriteria = new Criteria();
					orCriteria.addEqualTo("receipt.salespointId", salespoints[i].getId()); //$NON-NLS-1$
					salespointCriteria.addOrCriteria(orCriteria);
				}
				criteria.addAndCriteria(salespointCriteria);
			}
		}
		
		if (salespoints != null)
		{
			if (salespoints.length > 0)
			{
				Criteria salespointCriteria = new Criteria();
				salespointCriteria.addEqualTo("receipt.salespointId", salespoints[0].getId()); //$NON-NLS-1$
				for (int i = 1; i < salespoints.length; i++)
				{
					Criteria orCriteria = new Criteria();
					orCriteria.addEqualTo("receipt.salespointId", salespoints[i].getId()); //$NON-NLS-1$
					salespointCriteria.addOrCriteria(orCriteria);
				}
				criteria.addAndCriteria(salespointCriteria);
			}
		}
		
		Timestamp ts1 = new Timestamp(from.getTime());
		Timestamp ts2 = new Timestamp(to.getTime());
		criteria.addBetween("receipt.timestamp", ts1, ts2); //$NON-NLS-1$
		criteria.addEqualTo("isInputOrWithdraw", new Boolean(false));
		
		criteria.addNotNull("receipt.settlement"); //$NON-NLS-1$
		criteria.addEqualTo("receipt.status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		
		ReportQueryByCriteria report = new ReportQueryByCriteria(Payment.class, fields, criteria);
		report.setJdbcTypes(types);
		
		report.addOrderByAscending(fields[0]);
		report.addOrderByAscending(fields[2]);
		report.addOrderByAscending(fields[3]);
		
		report.addGroupBy(fields[0]);
		report.addGroupBy(fields[2]);
		report.addGroupBy(fields[3]);
		
		return Database.getCurrent().getBroker().getReportQueryIteratorByQuery(report);
	}
	
	public boolean isComplete()
	{
		boolean complete = true;
		if (this.paymentTypeId.equals(new Integer(0)))
		{
			complete = false;
		}
		else if (this.amount == 0D)
		{
			complete = false;
		}
		return complete;
	}
	
	/**
	 * @param user
	 * @return
	 */
	public static Payment[] selectCurrent(Salespoint salespoint, User user)
	{
		Criteria criteria = new Criteria();
		criteria.setAlias("receipt"); //$NON-NLS-1$
		criteria.addEqualTo("receipt.status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		if (salespoint != null)
		{
			criteria.addEqualTo("receipt.salespointId", salespoint.getId()); //$NON-NLS-1$
		}
		if (user != null)
		{
			criteria.addEqualTo("receipt.userId", user.getId()); //$NON-NLS-1$
		}
		criteria.addIsNull("receipt.settlement"); //$NON-NLS-1$
		//		criteria.addOrderBy("paymentType.id", true); //$NON-NLS-1$
		
		QueryByCriteria q = QueryFactory.newQuery(Payment.class, criteria);
		q.addOrderBy("paymentType.id", true); //$NON-NLS-1$
		Collection result = Table.select(q);
		return (Payment[]) result.toArray(new Payment[0]);
	}
	
	/*
	 * Export-, Importbereich
	 * 
	 * @author ceugster
	 */
	public Attributes getSAXRecordAttributes()
	{
		AttributesImpl attributes = new AttributesImpl();
		attributes.addAttribute("", "id", "", "", this.getId().toString());
		attributes.addAttribute("", "receipt-id", "", "", this.receiptId.toString());
		attributes.addAttribute("", "payment-type-id", "", "", this.paymentType.exportId);
		attributes.addAttribute("", "foreign-currency-id", "", "", this.foreignCurrency.code);
		attributes.addAttribute("", "quotation", "", "", Double.toString(this.quotation));
		attributes.addAttribute("", "amount", "", "", Double.toString(this.amount));
		attributes.addAttribute("", "amount-fc", "", "", Double.toString(this.amountFC));
		attributes.addAttribute("", "round-factor", "", "", Double.toString(this.roundFactor));
		attributes.addAttribute("", "round-factor-fc", "", "", Double.toString(this.roundFactorFC));
		attributes.addAttribute("", "back", "", "", new Boolean(this.back).toString());
		attributes.addAttribute("", "settlement", "", "", this.settlement == null ? "" : this.settlement.toString());
		attributes
						.addAttribute(
										"", "salespoint-id", "", "", this.salespointId == null ? this.getReceipt().getSalespointId().toString() : this.salespointId.toString()); //$NON-NLS-1$
		attributes.addAttribute("", "is-input-or-withdraw", "", "", Boolean.toString(this.isInputOrWithdraw)); //$NON-NLS-1$
		return attributes;
	}
	
	/*
	 * Export-, Importbereich
	 * 
	 * @author ceugster
	 */
	public Element getJDOMRecordAttributes(boolean setIds, boolean useExportIds)
	{
		Element payment = new Element("payment"); //$NON-NLS-1$
		payment.setAttribute("id", setIds ? this.getId().toString() : "0"); //$NON-NLS-1$ //$NON-NLS-2$
		payment.setAttribute("receipt-id", setIds ? this.receiptId.toString() : "0"); //$NON-NLS-1$ //$NON-NLS-2$
		payment
						.setAttribute(
										"payment-type-id", useExportIds ? this.paymentType.exportId : this.paymentType.getId().toString()); //$NON-NLS-1$
		payment
						.setAttribute(
										"foreign-currency-id", useExportIds ? this.foreignCurrency.code : this.foreignCurrency.getId().toString()); //$NON-NLS-1$
		payment.setAttribute("quotation", Double.toString(this.quotation)); //$NON-NLS-1$
		payment.setAttribute("amount", Double.toString(this.amount)); //$NON-NLS-1$
		payment.setAttribute("amount-fc", Double.toString(this.amountFC)); //$NON-NLS-1$
		payment.setAttribute("round-factor", Double.toString(this.roundFactor)); //$NON-NLS-1$
		payment.setAttribute("round-factor-fc", Double.toString(this.roundFactorFC)); //$NON-NLS-1$
		payment.setAttribute("back", new Boolean(this.back).toString()); //$NON-NLS-1$
		payment.setAttribute("settlement", this.settlement == null ? "" : this.settlement.toString()); //$NON-NLS-1$
		payment
						.setAttribute(
										"salespoint-id", this.salespointId == null ? this.getReceipt().getSalespointId().toString() : this.salespointId.toString()); //$NON-NLS-1$
		payment.setAttribute("is-input-or-withdraw", Boolean.toString(this.isInputOrWithdraw)); //$NON-NLS-1$
		return payment;
	}
	
	public void setRecordAttributes(Receipt r, Element payment, boolean setIds, boolean useExportIds)
					throws InvalidValueException
	{
		if (setIds)
			this.setId(new Long(payment.getAttributeValue("id")));
		else
			this.setId(null);
		
		this.setReceipt(r);
		if (useExportIds)
		{
			this.setPaymentType(PaymentType.selectByExportId(payment.getAttributeValue("payment-type-id"))); //$NON-NLS-1$
			this.setForeignCurrency(ForeignCurrency.selectByCode(payment.getAttributeValue("foreign-currency-id"))); //$NON-NLS-1$
		}
		else
		{
			this.setPaymentType(PaymentType.selectById(new Long(payment.getAttributeValue("payment-type-id")))); //$NON-NLS-1$
			this.setForeignCurrency(ForeignCurrency.selectById(new Long(payment
							.getAttributeValue("foreign-currency-id")))); //$NON-NLS-1$
		}
		this.quotation = XMLLoader.getDouble(payment.getAttributeValue("quotation")); //$NON-NLS-1$
		this.amount = XMLLoader.getDouble(payment.getAttributeValue("amount")); //$NON-NLS-1$
		this.amountFC = XMLLoader.getDouble(payment.getAttributeValue("amount-fc")); //$NON-NLS-1$
		this.roundFactor = XMLLoader.getDouble(payment.getAttributeValue("round-factor")); //$NON-NLS-1$
		this.roundFactorFC = XMLLoader.getDouble(payment.getAttributeValue("round-factor-fc")); //$NON-NLS-1$
		Position[] positions = this.receipt.getPositionsAsArray();
		this.isInputOrWithdraw = XMLLoader.getBoolean(payment.getAttributeValue("is-input-or-withdraw")); //$NON-NLS-1$
		for (int i = 0; i < positions.length; i++)
			if (positions[i].getProductGroup().type == ProductGroup.TYPE_INPUT
							|| positions[i].getProductGroup().type == ProductGroup.TYPE_WITHDRAW)
				this.isInputOrWithdraw = true;
		// 10226
		this.back = XMLLoader.getBoolean(payment.getAttributeValue("back")); //$NON-NLS-1$
		if (payment.getAttributeValue("settlement").equals(""))
			this.settlement = null;
		else
			this.settlement = new Long(XMLLoader.getLong(payment.getAttributeValue("settlement"))); //$NON-NLS-1$
		if (payment.getAttributeValue("salespoint-id").equals(""))
			this.salespointId = null;
		else
			this.salespointId = new Long(XMLLoader.getLong(payment.getAttributeValue("salespoint-id"))); //$NON-NLS-1$
			
		if (this.getPaymentTypeId() == null || this.getPaymentTypeId().equals(Table.ZERO_VALUE))
			throw new InvalidValueException("Zahlungsart mit der ExportId \""
							+ payment.getAttributeValue("payment-type-id") + "\" ungültig.");
		if (this.getForeignCurrencyId() == null || this.getForeignCurrencyId().equals(Table.ZERO_VALUE))
			throw new InvalidValueException("Fremdwährung mit der Id \""
							+ payment.getAttributeValue("foreign-currency-id") + "\" ungültig.");
	}
	
	public void setRecordAttributes(Receipt r, Element payment, boolean useExportIds) throws InvalidValueException
	{
		this.setRecordAttributes(r, payment, false, useExportIds);
	}
	
	public static Element getPaymentData(PaymentType paymentType, ForeignCurrency foreignCurrency, String[] fields,
					Translator translator)
	{
		Element detail = new Element("payment"); //$NON-NLS-1$
		detail.setAttribute("id", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		detail.setAttribute("receipt-id", fields[1]); //$NON-NLS-1$
		detail.setAttribute("payment-type-id", paymentType.getId().toString()); //$NON-NLS-1$
		detail.setAttribute("foreign-currency-id", foreignCurrency.getId().toString()); //$NON-NLS-1$
		detail.setAttribute("quotation", fields[18]); //$NON-NLS-1$
		detail.setAttribute("amount", fields[11]); //$NON-NLS-1$
		detail.setAttribute("amount-fc", fields[17]); //$NON-NLS-1$
		detail.setAttribute("round-factor", Double.toString(ForeignCurrency.getDefaultCurrency().roundFactor)); //$NON-NLS-1$
		detail.setAttribute("round-factor-fc", Double.toString(foreignCurrency.roundFactor)); //$NON-NLS-1$
		detail.setAttribute("back", fields[6].equals("Rückgeld") ? "true" : "false"); //$NON-NLS-1$
		detail.setAttribute("settlement", "1"); //$NON-NLS-1$
		detail.setAttribute("salespoint-id", ""); //$NON-NLS-1$
		detail.setAttribute("is-input-or-withdraw", ""); //$NON-NLS-1$
		return detail;
	}
	
	public Payment clone(Receipt receipt, boolean cloneId)
	{
		Payment copy = new Payment();
		copy.deleted = this.deleted;
		copy.timestamp = this.timestamp;
		copy.setId(cloneId ? this.getId() : null);
		copy.setReceipt(receipt);
		copy.setPaymentType(PaymentType.getById(this.getPaymentTypeId()));
		if (this.getForeignCurrency() == null)
			copy.setForeignCurrency(ForeignCurrency.getDefaultCurrency());
		else
			copy.setForeignCurrency(ForeignCurrency.getById(this.getForeignCurrencyId()));
		copy.setQuotation(this.getQuotation());
		copy.setAmount(this.getAmount());
		copy.roundFactor = this.roundFactor;
		copy.roundFactorFC = this.roundFactorFC;
		copy.setBack(this.isBack());
		copy.settlement = this.settlement;
		copy.salespointId = this.salespointId;
		copy.isInputOrWithdraw = this.isInputOrWithdraw;
		return copy;
	}
}
