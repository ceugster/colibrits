/*
 * Created on 04.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.db;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Vector;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.jdom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import ch.eugster.pos.InvalidValueException;
import ch.eugster.pos.Messages;
import ch.eugster.pos.product.EAN13;
import ch.eugster.pos.product.ProductServer;
import ch.eugster.pos.statistics.core.Translator;
import ch.eugster.pos.util.Config;
import ch.eugster.pos.util.NumberUtility;
import ch.eugster.pos.util.XMLLoader;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Position extends ReceiptChild
{
	
	// public Boolean expense = BOOLEAN_DEFAULT_FALSE;
	public int type = 0;
	public boolean galileoBook = Table.BOOLEAN_DEFAULT_FALSE;
	public boolean galileoBooked = Table.BOOLEAN_DEFAULT_FALSE;
	public String optCode = ""; //$NON-NLS-1$
	public String text = ""; //$NON-NLS-1$
	public String productId = ""; //$NON-NLS-1$
	public String author = ""; //$NON-NLS-1$
	public String title = ""; //$NON-NLS-1$
	public String publisher = ""; //$NON-NLS-1$
	public String isbn = ""; //$NON-NLS-1$
	public String bznr = ""; //$NON-NLS-1$
	public String productNumber = ""; //$NON-NLS-1$
	public boolean ordered = Table.BOOLEAN_DEFAULT_FALSE;
	public String orderId = ""; //$NON-NLS-1$
	// 10221
	private boolean payedInvoice = Table.BOOLEAN_DEFAULT_FALSE;
	private Integer invoice = new Integer(0);
	private Date invoiceDate = null;
	
	// 10221
	// 10226
	// Der Mwst-Betrag (quantity * price * (1 - discount) / (100 + Satz) * Satz)
	private double tax;
	// 10226
	/*
	 * stock wird auf true gesetzt, wenn ein Titel von einem Kunden bestellt
	 * worden ist, der Titel dann aber vom Lager genommen wurde
	 * (Lagerabholfach).
	 */
	public boolean stock = Table.BOOLEAN_DEFAULT_FALSE;
	public boolean updateCustomerAccount = Table.BOOLEAN_DEFAULT_FALSE;
	/**
	 * Wird für die Geldentnahmen und -einlagen in Fremdwährung verwendet
	 */
	public double amountFC = 0d;
	
	/*
	 * 10224
	 * 
	 * Dieses Attribut dient lediglich als Gedächtnisstütze im Fall, wo nur
	 * Teilmengen aus einer Sammelbestellung verkauft werden Es dürfen nie mehr
	 * Exemplare getippt werden, als noch auf dem entsprechenden Titel im
	 * Abholfach angegeben sind (Totalmenge - bereits verkaufte Menge) Es wird
	 * NICHT in der Datenbank gespeichert, ist als TRANSIENT!
	 */
	private int orderedQuantity = 0;
	// 10224
	private int quantity = Table.INTEGER_DEFAULT_255;
	private double price = Table.DOUBLE_DEFAULT_ZERO;
	private double discount = Table.DOUBLE_DEFAULT_ZERO;
	private double amount = Table.DOUBLE_DEFAULT_ZERO;
	
	private Long productGroupId;
	private ProductGroup productGroup;
	private Long currentTaxId;
	private CurrentTax currentTax;
	
	private int positionState = Position.STATE_SALE;
	
	/**
	 * 
	 */
	private Position()
	{
		super(Receipt.getEmptyReceipt());
	}
	
	private Position(Receipt receipt)
	{
		super(receipt);
		if (this.currentTax == null) this.currentTax = ProductGroup.getDefaultGroup().getDefaultTax().getCurrentTax();
		this.init(ProductGroup.getEmptyInstance(), this.currentTax);
	}
	
	private Position(Receipt receipt, CurrentTax currentTax, int quantity, String optCode)
	{
		super(receipt);
		this.init(ProductGroup.getEmptyInstance(), currentTax, quantity, optCode);
	}
	
	public static Position getEmptyInstance()
	{
		Position position = new Position();
		position.init(ProductGroup.getDefaultGroup(), ProductGroup.getDefaultGroup().getDefaultTax().getCurrentTax());
		return position;
	}
	
	public static Position getInstance()
	{
		return new Position(Receipt.getEmptyReceipt());
	}
	
	public static Position getInstance(Receipt receipt)
	{
		return new Position(receipt);
	}
	
	public static Position getInstance(Receipt receipt, CurrentTax currentTax, int quantity, String optCode)
	{
		return new Position(receipt, currentTax, quantity, optCode);
	}
	
	private void init(ProductGroup pg, CurrentTax ct)
	{
		this.init(pg, ct, 0, ""); //$NON-NLS-1$
	}
	
	private void init(ProductGroup pg, CurrentTax ct, int quantity, String optCode)
	{
		this.optCode = optCode;
		this.setProductGroup(pg);
		this.setCurrentTax(ct);
		this.setQuantity(quantity);
	}
	
	// 10224
	public void setOrderedQuantity(int quantity)
	{
		this.orderedQuantity = quantity;
	}
	
	public int getOrderedQuantity()
	{
		return this.orderedQuantity;
	}
	
	// 10224
	
	public int compareTo(Object other)
	{
		if (other instanceof Position)
		{
			return this.getId().compareTo(((Position) other).getId());
		}
		return 0;
	}
	
	public void setProductGroup(ProductGroup pg)
	{
		this.productGroup = pg;
		this.productGroupId = pg.getId();
		// expense = new Boolean(pg.type.equals(ProductGroup.TYPE_EXPENSE));
		this.type = pg.type;
		/*
		 * Testen, ob die Steuer (Umsatz/Vorsteuer) mit dem WG-Typ verträglich
		 * ist
		 */
		// if (pg.getDefaultTax() == null || (expense.booleanValue() ==
		// pg.getDefaultTax().getTaxTypeId().equals(new Long(1l)))) {
		if (pg.getDefaultTax() == null || this.type == 2 && pg.getDefaultTax().getTaxType().code.equals("U"))
		{
			Tax tax = null;
			if (pg.type == ProductGroup.TYPE_EXPENSE)
			{
				tax = Tax.getByTypeIdAndRateId(new Long(2l), new Long(3l), false);
			}
			else
			{
				tax = Tax.getByTypeIdAndRateId(new Long(1l), new Long(3l), false);
			}
			this.setCurrentTax(tax.getCurrentTax());
		}
		else
		{
			if (this.productId == null || this.productId.equals("") || this.productId.equals("0")
							|| this.currentTax == null)
			{
				this.setCurrentTax(pg.getDefaultTax().getCurrentTax());
			}
		}
		// if (!pg.getDefaultTaxId().equals(ZERO_ID)) {
		// if (!currentTax.getId().equals(ZERO_ID)) {
		// this.setCurrentTax(pg.getDefaultTax().getCurrentTax());
		// }
		// }
		if (pg.quantityProposal != 0)
		{
			if (pg.type == ProductGroup.TYPE_INPUT)
				this.setQuantity(Math.abs(pg.quantityProposal));
			else if (pg.type == ProductGroup.TYPE_WITHDRAW)
				this.setQuantity(-Math.abs(pg.quantityProposal));
			else if (this.quantity == 0)
				this.setQuantity(pg.quantityProposal);
			else if (this.positionState > 0 && this.quantity == 0) this.setQuantity(pg.quantityProposal);
			
		}
		if (pg.priceProposal != 0d)
		{
			if (this.price == 0d)
			{
				this.setPrice(pg.priceProposal);
			}
		}
		//		if (!pg.optCodeProposal.equals("")) { //$NON-NLS-1$
		//			//			if (optCode.equals("") || optCode.equals("L")) { //$NON-NLS-1$
		// this.optCode = pg.optCodeProposal;
		// // }
		// }
		if (this.price != 0)
		{
			if (this.productGroup.type == ProductGroup.TYPE_EXPENSE)
				this.price = -Math.abs(this.price);
			else if (this.productGroup.type == ProductGroup.TYPE_WITHDRAW)
			{
				// this.price = new Double(-Math.abs(this.price));
			}
			else
				this.price = Math.abs(this.price);
			this.price = NumberUtility.round(this.price, ForeignCurrency.getDefaultCurrency().getCurrency()
							.getDefaultFractionDigits());
		}
		this.calculateAmount();
	}
	
	// 10221
	public void setPayedInvoice(boolean payedInvoice)
	{
		this.payedInvoice = payedInvoice;
	}
	
	// 10221
	
	// 10221
	public boolean isPayedInvoice()
	{
		return this.payedInvoice;
	}
	
	// 10221
	
	// 10221
	public void setInvoiceNumber(Integer number)
	{
		this.invoice = number;
	}
	
	// 10221
	
	// 10221
	public Integer getInvoiceNumber()
	{
		return this.invoice;
	}
	
	// 10221
	
	// 10221
	public void setInvoiceDate(Date date)
	{
		this.invoiceDate = date;
	}
	
	// 10221
	
	// 10221
	public Date getInvoiceDate()
	{
		return this.invoiceDate;
	}
	
	// 10221
	
	public ProductGroup getProductGroup()
	{
		// 10200
		// return productGroup;
		if (this.productGroup == null)
		{
			if (this.productGroupId == null)
			{
				return ProductGroup.getDefaultGroup();
			}
			else
			{
				ProductGroup pg = ProductGroup.getById(this.productGroupId);
				if (pg == null)
				{
					return ProductGroup.getDefaultGroup();
				}
				else
				{
					return pg;
				}
			}
		}
		else
		{
			return this.productGroup;
		}
		// 10200
	}
	
	public Long getProductGroupId()
	{
		return this.productGroupId;
	}
	
	public void setQuantity(int quantity)
	{
		this.quantity = quantity;
		this.calculateAmount();
		this.calculateTax(); // 10226
		this.positionState = this.quantity > 0 ? Position.STATE_SALE : Position.STATE_TAKE_BACK;
	}
	
	public int getQuantity()
	{
		return this.quantity;
	}
	
	private void setPrice(double price)
	{
		// 10123
		double p = Math.abs(price);
		if (this.productGroup.type == ProductGroup.TYPE_EXPENSE)
		{
			p = -Math.abs(p);
		}
		else
		{
			p = Math.abs(p);
		}
		this.price = NumberUtility.round(p, ForeignCurrency.getDefaultCurrency().getCurrency()
						.getDefaultFractionDigits());
		this.calculateAmount(); // 10226
		this.calculateTax(); // 10226
		// 10123
	}
	
	public void setPrice(double price, double priceFC)
	{
		ForeignCurrency curr = this.getProductGroup().getForeignCurrency();
		if (curr.getId().equals(ForeignCurrency.getDefaultCurrency().getId()))
			price = NumberUtility.round(price, Config.getInstance().getRoundFactorAmount());
		else
			price = NumberUtility.round(priceFC * curr.quotation, ForeignCurrency.getDefaultCurrency().roundFactor);
		this.setPrice(price);
		this.calculateAmount();
		this.calculateTax();
		this.amountFC = priceFC;
	}
	
	// 10226
	private void calculateTax()
	{
		/*
		 * Minuszeichen vor qty gesetzt 10371 Build 304 17.03.2009
		 */
		this.tax = NumberUtility.round(-this.amount / (100d + this.currentTax.percentage) * this.currentTax.percentage,
						0.01);
	}
	
	// 10226
	
	public double getPrice()
	{
		return this.price;
	}
	
	public void setDiscount(double discount)
	{
		// this.discount = NumberUtility.round(discount,
		// ForeignCurrency.getDefaultCurrency
		// ().getCurrency().getDefaultFractionDigits() + 2);
		this.discount = discount;
		this.calculateAmount(); // 10429
		this.calculateTax(); // 10226
	}
	
	public double getDiscount()
	{
		return this.discount;
	}
	
	private void calculateAmount()
	{
		this.amount = NumberUtility.round(this.quantity * this.price * (1 - this.discount),
						this.productGroup == null ? 0.01 : this.productGroup.getForeignCurrency().roundFactor);
	}
	
	public void setCurrentTax(CurrentTax ct)
	{
		this.currentTax = ct;
		this.currentTaxId = ct.getId();
		/*
		 * Berechnung ersetzt durch Aufruf von calculateTax() ist konsistenter
		 * 10371 Build 304 17.03.2009
		 */
		this.calculateTax();
	}
	
	public CurrentTax getCurrentTax()
	{
		// 10200
		// return currentTax;
		if (this.currentTax == null)
		{
			if (this.currentTaxId == null)
			{
				return this.productGroup.getDefaultTax().getCurrentTax();
			}
			else
			{
				CurrentTax ct = CurrentTax.getById(this.currentTaxId);
				if (ct == null)
				{
					return this.productGroup.getDefaultTax().getCurrentTax();
				}
				else
				{
					return ct;
				}
			}
		}
		else
		{
			return this.currentTax;
		}
		// 10200
	}
	
	public Long getCurrentTaxId()
	{
		return this.currentTaxId;
	}
	
	/*
	 * 
	 * Gibt den Betrag (quantity price (1d - discount) zurück
	 */
	public double getAmount()
	{
		return this.amount;
	}
	
	/*
	 * 
	 * Gibt den Betrag (quantity amountFC (1d - discount) zurück
	 */
	public double getAmountFC()
	{
		// double value = NumberUtility.round(quantity * amountFC *
		// (1d - discount),
		// productGroup.getForeignCurrency().roundFactor);
		return this.amountFC;
	}
	
	/*
	 * 
	 * Gibt den Betrag (quantity price) zurück
	 */
	public double getGrossAmount()
	{
		return NumberUtility.round(this.quantity * this.price, ForeignCurrency.getDefaultCurrency().roundFactor);
	}
	
	/*
	 * 
	 * Gibt den Betrag (quantity price (1d - discount) - tax) zurück
	 */
	public double getNetAmount()
	{
		return NumberUtility.round(this.getAmount() - this.getTaxAmount(), ForeignCurrency.getDefaultCurrency()
						.getCurrency().getDefaultFractionDigits());
	}
	
	/*
	 * 
	 * Gibt den Betrag (quantity price - (quantity price (1d - discount)) zurück
	 */
	public double getDiscountAmount()
	{
		return NumberUtility.round(this.getGrossAmount() - this.getAmount(),
						ForeignCurrency.getDefaultCurrency().roundFactor);
	}
	
	public double getTaxAmount()
	{
		return NumberUtility.round(this.tax, ForeignCurrency.getDefaultCurrency().getCurrency()
						.getDefaultFractionDigits());
	}
	
	public boolean isRemovable()
	{
		return true;
	}
	
	public boolean isReturnPosition()
	{
		return this.quantity < 0;
	}
	
	// public Object clone() { // 10195
	public Position clone(Receipt receipt, boolean setId)
	{
		Position copy = new Position();
		
		copy.setId(setId ? this.getId() : null);
		copy.deleted = this.deleted;
		copy.timestamp = this.timestamp;
		copy.setReceipt(receipt);
		copy.productId = this.productId;
		copy.setProductGroup(this.getProductGroup());
		copy.setCurrentTax(this.getCurrentTax());
		copy.setQuantity(this.getQuantity());
		copy.setPrice(this.getPrice());
		copy.setDiscount(this.getDiscount());
		// copy.expense = expense;
		copy.type = this.type;
		copy.galileoBook = this.galileoBook;
		copy.galileoBooked = this.galileoBooked;
		copy.optCode = this.optCode;
		copy.author = this.author;
		copy.title = this.title;
		copy.publisher = this.publisher;
		copy.isbn = this.isbn;
		copy.bznr = this.bznr;
		copy.productNumber = this.productNumber;
		copy.ordered = this.ordered;
		copy.orderId = this.orderId;
		copy.stock = this.stock;
		copy.text = this.text;
		copy.updateCustomerAccount = this.updateCustomerAccount;
		copy.orderedQuantity = this.orderedQuantity; // 10224
		copy.tax = this.tax;
		copy.type = this.type;
		copy.amountFC = this.amountFC;
		copy.amount = this.amount;
		
		if (ProductServer.getInstance().isActive() && !setId)
		{
			boolean getGalileoData = this.productId != null && this.productId.length() > 0;
			if (getGalileoData)
			{
				if (this.productId.startsWith(EAN13.PRE_ORDERED))
				{
					if (copy.positionState == Position.STATE_TAKE_BACK)
					{
						getGalileoData = ProductServer.getInstance().isOrderValid(copy.productId);
					}
					else
						getGalileoData = ProductServer.getInstance().getItem(copy.productId);
				}
				else
					getGalileoData = ProductServer.getInstance().getItem(copy.productId);
				
				if (getGalileoData) ProductServer.getInstance().setDataToTransferredPosition(copy);
			}
		}
		
		return copy;
	}
	
	public static boolean exist(String key, Object value)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo(key, value);
		Query query = QueryFactory.newQuery(Position.class, criteria);
		query.setEndAtIndex(1);
		Collection positions = Table.select(query);
		Iterator i = positions.iterator();
		return i.hasNext();
	}
	
	public static Position getPositionByPK(Long pk)
	{
		Position position = new Position();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("id", pk); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Position.class, criteria);
		Collection positions = Table.select(query);
		Iterator i = positions.iterator();
		if (i.hasNext())
		{
			position = (Position) i.next();
		}
		return position;
	}
	
	public static Position[] selectByReceiptId(Long receiptId, boolean deletedToo)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("receiptId", receiptId); //$NON-NLS-1$
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Position.class, criteria);
		Collection positions = Table.select(query);
		return (Position[]) positions.toArray(new Position[0]);
	}
	
	public static Iterator selectProductGroupStatisticsRange(Salespoint[] salespoints, Date from, Date to, String sort,
					boolean onlyGalileo, boolean withExpenses, boolean withOtherSales, boolean compareWithPreviousYear)
	{
		String[] fields = new String[]
		{ /* 0 */"productGroup.id", //$NON-NLS-1$
						/* 1 */"productGroup.galileoId", //$NON-NLS-1$
						/* 2 */"optCode", //$NON-NLS-1$
						/* 3 */"year(receipt.timestamp)", //$NON-NLS-1$
						/* 4 */"productGroup.name", //$NON-NLS-1$
						/* 5 */"receipt.timestamp", //$NON-NLS-1$
						/* 6 */"sum(quantity)", //$NON-NLS-1$
						/* 7 */"sum(amount)" }; //$NON-NLS-1$
		int[] types = new int[]
		{ Types.BIGINT, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.VARCHAR, Types.TIMESTAMP, Types.BIGINT,
						Types.DOUBLE };
		
		Collection collection = new Vector();
		
		Criteria currentCriteria = new Criteria();
		Criteria previousYearCriteria = new Criteria();
		
		if (salespoints != null)
		{
			if (salespoints.length > 0)
			{
				currentCriteria.addAndCriteria(Position.getSalespointCriteria(salespoints));
				previousYearCriteria.addAndCriteria(Position.getSalespointCriteria(salespoints));
			}
		}
		
		Criteria currentDateRangeCriteria = new Criteria();
		Timestamp ts1 = new Timestamp(from.getTime());
		Timestamp ts2 = new Timestamp(to.getTime());
		
		Criteria previousYearDateRangeCriteria = new Criteria();
		GregorianCalendar prevStartDate = new GregorianCalendar();
		prevStartDate.setTimeInMillis(from.getTime());
		prevStartDate.add(GregorianCalendar.YEAR, -1);
		Timestamp ps1 = new Timestamp(prevStartDate.getTimeInMillis());
		GregorianCalendar prevEndDate = new GregorianCalendar();
		prevEndDate.setTimeInMillis(to.getTime());
		prevEndDate.add(GregorianCalendar.YEAR, -1);
		Timestamp ps2 = new Timestamp(prevEndDate.getTimeInMillis());
		
		currentDateRangeCriteria.addBetween("receipt.timestamp", ts1, ts2); //$NON-NLS-1$
		previousYearDateRangeCriteria.addBetween("receipt.timestamp", ps1, ps2); //$NON-NLS-1$
		
		currentCriteria.addAndCriteria(currentDateRangeCriteria);
		previousYearCriteria.addAndCriteria(previousYearDateRangeCriteria);
		
		currentCriteria.addNotNull("receipt.settlement"); //$NON-NLS-1$
		currentCriteria.addNotEqualTo("productGroup.type", new Integer(ProductGroup.TYPE_INPUT));
		currentCriteria.addNotEqualTo("productGroup.type", new Integer(ProductGroup.TYPE_WITHDRAW));
		
		previousYearCriteria.addNotNull("receipt.settlement"); //$NON-NLS-1$
		
		currentCriteria.addEqualTo("receipt.status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		previousYearCriteria.addEqualTo("receipt.status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		
		Criteria selCriteria = null;
		
		if (onlyGalileo)
		{
			selCriteria = new Criteria();
			selCriteria.addNotEqualTo("productGroup.galileoId", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		if (!withOtherSales)
		{
			if (selCriteria == null) selCriteria = new Criteria();
			selCriteria.addNotEqualTo("productGroup.type", new Integer(ProductGroup.TYPE_NOT_INCOME)); //$NON-NLS-1$
		}
		
		if (!withExpenses)
		{
			if (selCriteria == null) selCriteria = new Criteria();
			selCriteria.addNotEqualTo("productGroup.type", new Integer(ProductGroup.TYPE_EXPENSE)); //$NON-NLS-1$
		}
		
		if (selCriteria != null)
		{
			currentCriteria.addAndCriteria(selCriteria);
			previousYearCriteria.addAndCriteria(selCriteria);
		}
		
		ReportQueryByCriteria report = new ReportQueryByCriteria(Position.class, fields, currentCriteria);
		report.addGroupBy(fields[0]);
		report.addGroupBy(fields[2]);
		// report.addGroupBy(fields[3]);
		report.setJdbcTypes(types);
		
		Iterator iterator = Database.getCurrent().getBroker().getReportQueryIteratorByQuery(report);
		while (iterator.hasNext())
		{
			Object[] next = (Object[]) iterator.next();
			next[5] = new Boolean(true);
			collection.add(next);
		}
		
		if (compareWithPreviousYear)
		{
			report = new ReportQueryByCriteria(Position.class, fields, previousYearCriteria);
			report.addGroupBy(fields[0]);
			report.addGroupBy(fields[2]);
			// report.addGroupBy(fields[3]);
			
			iterator = Database.getCurrent().getBroker().getReportQueryIteratorByQuery(report);
			while (iterator.hasNext())
			{
				Object[] next = (Object[]) iterator.next();
				next[5] = new Boolean(false);
				collection.add(next);
			}
		}
		
		return collection.iterator();
	}
	
	private static Criteria getSalespointCriteria(Salespoint[] salespoints)
	{
		Criteria salespointCriteria = new Criteria();
		salespointCriteria.addEqualTo("receipt.salespointId", salespoints[0].getId()); //$NON-NLS-1$
		for (int i = 1; i < salespoints.length; i++)
		{
			Criteria orCriteria = new Criteria();
			orCriteria.addEqualTo("receipt.salespointId", salespoints[i].getId()); //$NON-NLS-1$
			salespointCriteria.addOrCriteria(orCriteria);
		}
		return salespointCriteria;
	}
	
	public static Iterator selectProductGroupStockOrderRange(Salespoint[] salespoints, Date from, Date to,
					String order, boolean onlyGalileo, boolean withExpenses, boolean withOtherSales)
	{
		String[] fields = new String[]
		{ /* 0 */"receipt.salespointId", //$NON-NLS-1$
						/* 1 */"productGroup.id", //$NON-NLS-1$
						/* 2 */"productGroup.galileoId", //$NON-NLS-1$
						/* 3 */"productGroup.name", //$NON-NLS-1$
						/* 4 */"optCode", //$NON-NLS-1$
						/* 5 */"year(receipt.timestamp)", //$NON-NLS-1$
						/* 6 */"receipt.timestamp", //$NON-NLS-1$
						/* 7 */"sum(amount)" }; //$NON-NLS-1$
		int[] types = new int[]
		{ Types.BIGINT, Types.BIGINT, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.TIMESTAMP,
						Types.DOUBLE };
		
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
		
		Criteria dateRangeCriteria = new Criteria();
		Timestamp ts1 = new Timestamp(from.getTime());
		Timestamp ts2 = new Timestamp(to.getTime());
		dateRangeCriteria.addBetween("receipt.timestamp", ts1, ts2); //$NON-NLS-1$
		
		Criteria previousYearCriteria = new Criteria();
		GregorianCalendar prevStartDate = new GregorianCalendar();
		prevStartDate.setTimeInMillis(from.getTime());
		prevStartDate.add(GregorianCalendar.YEAR, -1);
		GregorianCalendar prevEndDate = new GregorianCalendar();
		prevEndDate.setTimeInMillis(to.getTime());
		prevEndDate.add(GregorianCalendar.YEAR, -1);
		Timestamp ps1 = new Timestamp(prevStartDate.getTimeInMillis());
		Timestamp ps2 = new Timestamp(prevEndDate.getTimeInMillis());
		previousYearCriteria.addBetween("receipt.timestamp", ps1, ps2); //$NON-NLS-1$
		dateRangeCriteria.addOrCriteria(previousYearCriteria);
		
		criteria.addAndCriteria(dateRangeCriteria);
		
		criteria.addNotNull("receipt.settlement"); //$NON-NLS-1$
		criteria.addEqualTo("receipt.status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		criteria.addNotEqualTo("productGroup.type", new Integer(ProductGroup.TYPE_INPUT));
		criteria.addNotEqualTo("productGroup.type", new Integer(ProductGroup.TYPE_WITHDRAW));
		
		if (onlyGalileo)
		{
			criteria.addNotEqualTo("productGroup.galileoId", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		if (!withOtherSales)
		{
			criteria.addNotEqualTo("productGroup.type", new Integer(ProductGroup.TYPE_NOT_INCOME)); //$NON-NLS-1$
		}
		
		if (!withExpenses)
		{
			criteria.addNotEqualTo("productGroup.type", new Integer(ProductGroup.TYPE_EXPENSE)); //$NON-NLS-1$
		}
		
		ReportQueryByCriteria report = new ReportQueryByCriteria(Position.class, fields, criteria);
		report.addGroupBy(fields[0]);
		report.addGroupBy(fields[1]);
		report.addGroupBy(fields[4]);
		// report.addGroupBy(fields[5]);
		report.setJdbcTypes(types);
		
		return Database.getCurrent().getBroker().getReportQueryIteratorByQuery(report);
	}
	
	public static int countOrderedItemsUsed(String orderId)
	{
		int sum = 0;
		
		if (ProductServer.isUsed() && ProductServer.getInstance().getUpdate() > 0)
		{
			String[] fields = new String[]
			{
			/* 0 */"sum(quantity)" };
			
			int[] types = new int[]
			{ Types.INTEGER };
			
			Criteria criteria = new Criteria();
			criteria.addEqualTo("orderId", orderId);
			criteria.addEqualTo("receipt.status", new Integer(Receipt.RECEIPT_STATE_PARKED)); //$NON-NLS-1$
			
			ReportQueryByCriteria report = new ReportQueryByCriteria(Position.class, fields, criteria);
			report.setJdbcTypes(types);
			
			Iterator iterator = Database.getCurrent().getBroker().getReportQueryIteratorByQuery(report);
			while (iterator.hasNext())
			{
				Object[] o = (Object[]) iterator.next();
				if (o != null && o[0] != null) sum += ((Integer) o[0]).intValue();
			}
		}
		
		return sum;
	}
	
	public static Iterator selectPositions(Salespoint[] salespoints, Date from, Date to,
					boolean withoutZeroPercentPositions)
	{
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
		
		Criteria dateRangeCriteria = new Criteria();
		Timestamp ts1 = new Timestamp(from.getTime());
		Timestamp ts2 = new Timestamp(to.getTime());
		dateRangeCriteria.addBetween("receipt.timestamp", ts1, ts2); //$NON-NLS-1$
		criteria.addAndCriteria(dateRangeCriteria);
		
		criteria.addNotNull("receipt.settlement"); //$NON-NLS-1$
		criteria.addEqualTo("receipt.status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		if (withoutZeroPercentPositions)
		{
			criteria.addNotEqualTo("currentTax.percentage", new Double(0d));
		}
		
		QueryByCriteria report = new QueryByCriteria(Position.class, criteria);
		return Database.getCurrent().getBroker().getIteratorByQuery(report);
	}
	
	public static int countPositionsBySalespointAndDateRange(Long salespointId, Date from, Date to)
	{
		Criteria criteria = new Criteria();
		
		if (salespointId != null)
		{
			Criteria salespointCriteria = new Criteria();
			salespointCriteria.addEqualTo("receipt.salespointId", salespointId); //$NON-NLS-1$
			// 10116
			criteria.addAndCriteria(salespointCriteria);
			// 10116
		}
		
		Criteria dateRangeCriteria = new Criteria();
		Timestamp ts1 = new Timestamp(from.getTime());
		Timestamp ts2 = new Timestamp(to.getTime());
		dateRangeCriteria.addBetween("receipt.timestamp", ts1, ts2); //$NON-NLS-1$
		criteria.addAndCriteria(dateRangeCriteria);
		
		criteria.addNotNull("receipt.settlement"); //$NON-NLS-1$
		criteria.addEqualTo("receipt.status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		
		QueryByCriteria query = new QueryByCriteria(Position.class, criteria);
		return Database.getCurrent().getBroker().getCount(query);
	}
	
	public static Position[] selectAll(boolean deletedToo)
	{
		Criteria criteria = new Criteria();
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Position.class, criteria);
		Collection positions = Table.select(query);
		return (Position[]) positions.toArray(new Position[0]);
	}
	
	public static int count(ProductGroup group)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("productGroupId", group.getId());
		QueryByCriteria query = QueryFactory.newQuery(Position.class, criteria);
		return Database.getCurrent().getBroker().getCount(query);
	}
	
	public static Position[] selectProductGroupPositions(ProductGroup group)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("productGroupId", group.getId());
		QueryByCriteria query = QueryFactory.newQuery(Position.class, criteria);
		query.setEndAtIndex(100);
		return (Position[]) Database.getCurrent().getBroker().getCollectionByQuery(query).toArray(new Position[0]);
	}
	
	public static Iterator selectSettled(Salespoint[] salespoints, Date from, Date to)
	{
		String[] fields = new String[]
		{ /* 0 */"productGroup.type", //$NON-NLS-1$
						/* 1 */"productGroup.id", //$NON-NLS-1$
						/* 2 */"productGroup.galileoId", //$NON-NLS-1$
						/* 3 */"productGroup.name", //$NON-NLS-1$
						/* 4 */"currentTax.id", //$NON-NLS-1$
						/* 5 */"sum(tax)", //$NON-NLS-1$
						/* 6 */"sum(quantity)", //$NON-NLS-1$
						/* 7 */"sum(amount)", //$NON-NLS-1$
						/* 8 */"amountFC" //$NON-NLS-1$
		};
		
		int[] types = new int[]
		{
		/* 0 */Types.INTEGER,
		/* 1 */Types.BIGINT,
		/* 2 */Types.VARCHAR,
		/* 3 */Types.VARCHAR,
		/* 4 */Types.BIGINT,
		/* 5 */Types.DOUBLE,
		/* 6 */Types.INTEGER,
		/* 7 */Types.DOUBLE,
		/* 8 */Types.DOUBLE };
		
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
		
		Timestamp ts1 = new Timestamp(from.getTime());
		Timestamp ts2 = new Timestamp(to.getTime());
		criteria.addBetween("receipt.timestamp", ts1, ts2); //$NON-NLS-1$
		criteria.addNotNull("receipt.settlement"); //$NON-NLS-1$
		criteria.addEqualTo("receipt.status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		
		ReportQueryByCriteria report = new ReportQueryByCriteria(Position.class, fields, criteria);
		report.setJdbcTypes(types);
		
		report.addOrderByAscending(fields[0]);
		report.addOrderByAscending(fields[1]);
		report.addOrderByAscending(fields[3]);
		report.addOrderByAscending(fields[4]);
		
		report.addGroupBy(fields[1]);
		report.addGroupBy(fields[3]);
		report.addGroupBy(fields[4]);
		
		return Database.getCurrent().getBroker().getReportQueryIteratorByQuery(report);
	}
	
	public static long countProductGroupPositions(ProductGroup group)
	{
		Long m = new Long(0l);
		String[] fields = new String[]
		{ "count(*)" };
		Criteria criteria = new Criteria();
		criteria.addEqualTo("productGroupId", group.getId());
		ReportQueryByCriteria report = new ReportQueryByCriteria(Position.class, fields, criteria, false);
		Iterator iter = Database.getCurrent().getBroker().getReportQueryIteratorByQuery(report);
		if (iter.hasNext())
		{
			Object[] object = (Object[]) iter.next();
			if (object[0] instanceof Long)
			{
				m = (Long) object[0];
			}
			else if (object[0] instanceof Integer)
			{
				m = new Long(((Integer) object[0]).longValue());
			}
		}
		return m.longValue();
	}
	
	public static Position[] selectGalileoBooked(boolean booked)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("galileoBooked", new Boolean(booked)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Position.class, criteria);
		Collection positions = Table.select(query);
		return (Position[]) positions.toArray(new Position[0]);
	}
	
	/**
	 * 
	 * Gibt den Totalbetrag der noch nicht verbuchten, umsatzrelevanten
	 * Positionen zurück
	 * 
	 * @return Double Der Totalbetrag
	 */
	public static double selectSales()
	{
		String[] fields = new String[]
		{ "SUM(amount)" };
		int[] types = new int[]
		{ Types.DOUBLE };
		
		Criteria criteria = new Criteria();
		criteria.addEqualTo("receipt.status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		criteria.addIsNull("receipt.settlement"); //$NON-NLS-1$ 
		criteria.addEqualTo("productGroup.type", new Integer(ProductGroup.TYPE_INCOME)); //$NON-NLS-1$ 
		
		ReportQueryByCriteria q = QueryFactory.newReportQuery(Position.class, criteria);
		q.setAttributes(fields);
		q.setJdbcTypes(types);
		
		double amount = 0d;
		Iterator iter = Database.getCurrent().getBroker().getReportQueryIteratorByQuery(q);
		while (iter.hasNext())
		{
			Object[] obj = (Object[]) iter.next();
			amount += obj[0] instanceof Double ? ((Double) obj[0]).doubleValue() : 0d;
		}
		return amount;
	}
	
	/**
	 * 
	 * Gibt den Totalbetrag der noch nicht verbuchten, umsatzrelevanten
	 * Positionen eines Benutzers zurück
	 * 
	 * @param User
	 *            der Benutzer
	 * @return Double Der Totalbetrag
	 */
	public static double selectSales(User user)
	{
		String[] fields = new String[]
		{ "userId", "SUM(amount)" };
		int[] types = new int[]
		{ Types.BIGINT, Types.DOUBLE };
		
		Criteria criteria = new Criteria();
		criteria.addEqualTo("receipt.userId", user.getId()); //$NON-NLS-1$
		criteria.addEqualTo("receipt.status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		criteria.addIsNull("receipt.settlement"); //$NON-NLS-1$ 
		criteria.addEqualTo("productGroup.type", new Integer(ProductGroup.TYPE_INCOME)); //$NON-NLS-1$ 
		
		ReportQueryByCriteria q = QueryFactory.newReportQuery(Position.class, criteria);
		q.setAttributes(fields);
		q.setJdbcTypes(types);
		q.addOrderByAscending(fields[0]);
		q.addGroupBy(fields[0]);
		
		double amount = 0d;
		Iterator iter = Database.getCurrent().getBroker().getReportQueryIteratorByQuery(q);
		while (iter.hasNext())
		{
			Object[] obj = (Object[]) iter.next();
			amount += obj[1] instanceof Double ? ((Double) obj[1]).doubleValue() : 0d;
		}
		return amount;
	}
	
	/**
	 * 
	 * Gibt den Totalbetrag der noch nicht verbuchten, umsatzrelevanten
	 * Positionen einer Kasse zurück
	 * 
	 * @param Salespoint
	 *            Die Kasse für die der Umsatz zurückgegeben werden soll
	 * @return Double Der Totalbetrag
	 */
	public static double selectSales(Salespoint salespoint)
	{
		String[] fields = new String[]
		{ "salespointId", "SUM(amount)" };
		int[] types = new int[]
		{ Types.BIGINT, Types.DOUBLE };
		
		Criteria criteria = new Criteria();
		criteria.addEqualTo("receipt.salespointId", salespoint.getId()); //$NON-NLS-1$
		criteria.addEqualTo("receipt.status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		criteria.addIsNull("receipt.settlement"); //$NON-NLS-1$ 
		criteria.addEqualTo("productGroup.type", new Integer(ProductGroup.TYPE_INCOME)); //$NON-NLS-1$ 
		
		ReportQueryByCriteria q = QueryFactory.newReportQuery(Position.class, criteria);
		q.setAttributes(fields);
		q.setJdbcTypes(types);
		q.addOrderByAscending(fields[0]);
		q.addGroupBy(fields[0]);
		
		double amount = 0d;
		Iterator iter = Database.getCurrent().getBroker().getReportQueryIteratorByQuery(q);
		while (iter.hasNext())
		{
			Object[] obj = (Object[]) iter.next();
			amount += obj[1] == null ? 0d : ((Double) obj[1]).doubleValue();
		}
		return amount;
	}
	
	public static Iterator selectReceiptStatistics(Salespoint[] salespoints, Date from, Date to, int groupBy)
	{
		String[] fields = new String[]
		{ /* 0 */"receipt.salespoint.name", //$NON-NLS-1$
						/* 1 */"year(receipt.timestamp)", //$NON-NLS-1$
						/* 2 */"receipt.salespointId", //$NON-NLS-1$
						/* 3 */"receiptId", //$NON-NLS-1$
						/* 4 */"sum(quantity)", //$NON-NLS-1$
						/* 5 */"sum(amount)", //$NON-NLS-1$
		};
		int[] types = new int[]
		{ Types.VARCHAR, Types.INTEGER, Types.BIGINT, Types.BIGINT, Types.DOUBLE, Types.DOUBLE };
		
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
		
		Timestamp ts1 = new Timestamp(from.getTime());
		Timestamp ts2 = new Timestamp(to.getTime());
		criteria.addBetween("receipt.timestamp", ts1, ts2); //$NON-NLS-1$
		
		criteria.addNotNull("receipt.settlement"); //$NON-NLS-1$
		criteria.addEqualTo("receipt.status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		criteria.addEqualTo("type", new Integer(ProductGroup.TYPE_INCOME));
		
		ReportQueryByCriteria report = new ReportQueryByCriteria(Position.class, fields, criteria);
		report.setJdbcTypes(types);
		
		if (groupBy == 1)
		{
			report.addOrderBy(fields[1], true);
			report.addOrderBy(fields[0], true);
			
			report.addGroupBy(fields[1]);
			report.addGroupBy(fields[0]);
		}
		else
		{
			report.addOrderBy(fields[0], true);
			report.addOrderBy(fields[1], true);
			
			report.addGroupBy(fields[0]);
			report.addGroupBy(fields[1]);
		}
		report.addOrderBy(fields[3], true);
		report.addGroupBy(fields[3]);
		
		return Database.getCurrent().getBroker().getReportQueryIteratorByQuery(report);
	}
	
	public static int countDiscountRecords(Salespoint[] salespoints, Date from, Date to, boolean onlyWithDiscounts)
	{
		Criteria criteria = new Criteria();
		
		criteria.addBetween("receipt.timestamp", from, to); //$NON-NLS-1$
		criteria.addNotNull("receipt.settlement");
		criteria.addEqualTo("receipt.status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED));
		criteria.addEqualTo("type", new Integer(ProductGroup.TYPE_INCOME));
		if (onlyWithDiscounts) criteria.addNotEqualTo("discount", new Double(0d));
		
		if (salespoints != null) criteria.addAndCriteria(Salespoint.getSalespointCriteria(salespoints));
		
		QueryByCriteria query = new QueryByCriteria(Position.class, criteria);
		
		query.addOrderByAscending("salespointId");
		query.addOrderByAscending("receipt.timestamp");
		
		query.addGroupBy("salespointId");
		query.addGroupBy("YEAR(receipt.timestamp)");
		query.addGroupBy("MONTH(receipt.timestamp)");
		query.addGroupBy("DAYOFMONTH(receipt.timestamp)");
		
		return Database.getCurrent().getBroker().getCount(query);
	}
	
	public static Iterator selectDiscountRecords(Salespoint[] salespoints, Date from, Date to, boolean onlyWithDiscounts)
	{
		String[] fields = new String[]
		{ "receipt.salespointId", "YEAR(receipt.timestamp) AS year", "MONTH(receipt.timestamp) AS month",
						"DAYOFMONTH(receipt.timestamp) AS day", "SUM(amount) AS amount",
						"SUM(ROUND(quantity * price, 2)) AS fullAmount" };
		
		int[] fieldTypes = new int[]
		{ Types.BIGINT, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.DOUBLE, Types.DOUBLE };
		
		Criteria criteria = new Criteria();
		
		criteria.addBetween("receipt.timestamp", from, to); //$NON-NLS-1$
		criteria.addNotNull("receipt.settlement");
		criteria.addEqualTo("receipt.status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED));
		criteria.addEqualTo("type", new Integer(ProductGroup.TYPE_INCOME));
		if (onlyWithDiscounts) criteria.addNotEqualTo("discount", new Double(0d));
		
		if (salespoints != null) criteria.addAndCriteria(Salespoint.getSalespointCriteria(salespoints));
		
		ReportQueryByCriteria query = new ReportQueryByCriteria(Position.class, fields, criteria);
		query.setJdbcTypes(fieldTypes);
		
		query.addOrderByAscending("salespointId");
		query.addOrderByAscending("receipt.timestamp");
		
		query.addGroupBy("salespointId");
		query.addGroupBy("YEAR(receipt.timestamp)");
		query.addGroupBy("MONTH(receipt.timestamp)");
		query.addGroupBy("DAYOFMONTH(receipt.timestamp)");
		
		return Database.getCurrent().getBroker().getReportQueryIteratorByQuery(query);
	}
	
	public static Position[] selectCurrent(Salespoint salespoint, User user, String order, boolean deletedToo)
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
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		// criteria.addOrderBy(order, true);
		QueryByCriteria q = QueryFactory.newReportQuery(Position.class, criteria);
		q.addOrderBy(order, true);
		Collection positions = Table.select(q);
		return (Position[]) positions.toArray(new Position[0]);
	}
	
	public static Double selectSalespointSum(Salespoint salespoint)
	{
		String[] fields = new String[]
		{
		/* 0 */"SUM(amount)" };
		int[] types = new int[]
		{ Types.DOUBLE };
		
		Criteria criteria = new Criteria();
		criteria.setAlias("receipt"); //$NON-NLS-1$
		criteria.addEqualTo("receipt.status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		criteria.addEqualTo("receipt.salespointId", salespoint.getId()); //$NON-NLS-1$
		criteria.addIsNull("receipt.settlement"); //$NON-NLS-1$ 
		
		ReportQueryByCriteria report = new ReportQueryByCriteria(Position.class, fields, criteria);
		report.setJdbcTypes(types);
		
		Iterator iter = Database.getCurrent().getBroker().getReportQueryIteratorByQuery(report);
		double amount = 0d;
		while (iter.hasNext())
		{
			Object[] object = (Object[]) iter.next();
			if (object[0] instanceof Double)
			{
				Double m = (Double) object[0];
				amount += m.doubleValue();
			}
			else if (object[0] instanceof Integer)
			{
				Integer m = (Integer) object[0];
				amount += m.intValue();
			}
		}
		return new Double(amount);
	}
	
	public boolean isComplete()
	{
		boolean complete = true;
		
		if (Database.getCurrent().equals(Database.getTemporary()))
		{
			if (this.productGroup == null || this.productGroup.getId() == null
							|| this.productGroup.getId().equals(Table.ZERO_VALUE))
			{
				complete = false;
			}
		}
		else
		{
			if (this.productGroup == null || this.productGroup.getId() == null
							|| this.productGroup.getId().equals(Table.ZERO_VALUE))
			{
				complete = false;
			}
			else if (ProductServer.isUsed())
			{
				if (ProductServer.getInstance().getUpdate() == 2 && !this.productGroup.galileoId.equals("")) { // 10185 //$NON-NLS-1$
					if (this.productId == null || this.productId.length() == 0)
					{
						complete = false;
					}
				}
			}
		}
		
		if (this.quantity == Table.INTEGER_DEFAULT_ZERO)
		{
			complete = false;
		}
		else if (this.price == Table.DOUBLE_DEFAULT_ZERO)
		{
			complete = false;
		}
		else if (this.currentTaxId == null || this.currentTaxId.equals(Table.ZERO_VALUE))
		{
			complete = false;
		}
		else if (this.optCode.equals("")) { //$NON-NLS-1$
			if (this.productGroup.type != ProductGroup.TYPE_EXPENSE)
			{
				complete = false;
			}
		}
		
		return complete;
	}
	
	public boolean onlyPriceMissing()
	{
		boolean complete = true;
		
		if (Database.getCurrent().equals(Database.getTemporary()))
		{
			// if (productId == null || productId.length() == 0) {
			if (this.productGroup == null || this.productGroup.getId() == null
							|| this.productGroup.getId().equals(Table.ZERO_VALUE))
			{
				// if (Server.getInstance().getUpdate() == 2) {
				// setProductGroup(ProductGroup.getDefaultGroup());
				// }
				// else {
				complete = false;
				// }
			}
			// }
		}
		else
		{
			if (this.productGroup == null || this.productGroup.getId() == null
							|| this.productGroup.getId().equals(Table.ZERO_VALUE))
			{
				complete = false;
			}
			else if (ProductServer.isUsed())
			{
				if (ProductServer.getInstance().getUpdate() > 0 && !this.productGroup.galileoId.equals("")) { //$NON-NLS-1$
					if (this.productId == null || this.productId.length() == 0)
					{
						complete = false;
					}
				}
			}
		}
		
		if (this.quantity == Table.INTEGER_DEFAULT_ZERO)
		{
			complete = false;
		}
		else if (this.currentTaxId == null || this.currentTaxId.equals(Table.ZERO_VALUE))
		{
			complete = false;
		}
		else if (this.optCode.equals("")) { //$NON-NLS-1$
			if (this.productGroup.type != ProductGroup.TYPE_EXPENSE)
			{
				complete = false;
			}
		}
		
		return complete;
	}
	
	public boolean isFresh()
	{
		if ((this.productGroupId == null || this.productGroupId.equals(Table.ZERO_VALUE))
						&& (this.productId == null || this.productId.length() == 0) && this.price == 0d)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/*
	 *
	 */
	public Attributes getSAXRecordAttributes()
	{
		AttributesImpl attributes = new AttributesImpl();
		attributes.addAttribute("", "id", "", "", this.getId().toString());
		attributes.addAttribute("", "receipt-id", "", "", this.receiptId.toString());
		attributes.addAttribute("", "product-id", "", "", this.productId);
		attributes.addAttribute("", "product-group-id", "", "", this.productGroup.exportId);
		attributes.addAttribute("", "tax-id", "", "", this.currentTax.getTax().getId().toString());
		attributes.addAttribute("", "current-tax-id", "", "", new Long(this.currentTax.validationDate.getTime())
						.toString());
		attributes.addAttribute("", "quantity", "", "", Integer.toString(this.quantity));
		attributes.addAttribute("", "price", "", "", Double.toString(this.price));
		attributes.addAttribute("", "discount", "", "", Double.toString(this.discount));
		attributes.addAttribute("", "galileo-book", "", "", Boolean.toString(this.galileoBook));
		attributes.addAttribute("", "galileo-booked", "", "", Boolean.toString(this.galileoBooked));
		attributes.addAttribute("", "opt-code", "", "", this.optCode);
		attributes.addAttribute("", "author", "", "", this.author);
		attributes.addAttribute("", "title", "", "", this.title);
		attributes.addAttribute("", "publisher", "", "", this.publisher);
		attributes.addAttribute("", "isbn", "", "", this.isbn);
		attributes.addAttribute("", "bznr", "", "", this.bznr);
		attributes.addAttribute("", "product-number", "", "", this.productNumber);
		attributes.addAttribute("", "ordered", "", "", Boolean.toString(this.ordered));
		attributes.addAttribute("", "order-id", "", "", this.orderId);
		attributes.addAttribute("", "stock", "", "", Boolean.toString(this.stock));
		attributes.addAttribute("", "update-customer-account", "", "", Boolean.toString(this.updateCustomerAccount));
		attributes.addAttribute("", "payed-invoice", "", "", Boolean.toString(this.payedInvoice));
		attributes.addAttribute("", "invoice", "", "", this.invoice.toString());
		attributes.addAttribute("", "invoice-date", "", "", this.invoiceDate == null ? "" : new Long(this.invoiceDate
						.getTime()).toString());
		attributes.addAttribute("", "tax", "", "", Double.toString(this.tax));
		attributes.addAttribute("", "type", "", "", new Integer(this.type).toString());
		attributes.addAttribute("", "amount-fc", "", "", new Double(this.amountFC).toString());
		attributes.addAttribute("", "amount", "", "", new Double(this.amount).toString());
		return attributes;
	}
	
	/*
	 * Export-, Importbereich
	 * 
	 * @author ceugster
	 */
	public Element getJDOMRecordAttributes(boolean setIds, boolean useExportId)
	{
		Element position = new Element("position"); //$NON-NLS-1$
		position.setAttribute("id", setIds ? this.getId().toString() : "0"); //$NON-NLS-1$ //$NON-NLS-2$
		position.setAttribute("receipt-id", setIds ? this.getReceipt().getId().toString() : "0"); //$NON-NLS-1$ //$NON-NLS-2$
		position.setAttribute("product-id", this.productId); //$NON-NLS-1$
		if (useExportId)
		{
			position.setAttribute("product-group-id", this.productGroup.exportId);
			position.setAttribute("tax-id", this.currentTax.getTax().code.toString());
			position.setAttribute("current-tax-id", new Long(this.currentTax.validationDate.getTime()).toString());
		}
		else
		{
			position.setAttribute("product-group-id", this.productGroup.getId().toString());
			position.setAttribute("tax-id", this.currentTax.getTax().getId().toString());
			position.setAttribute("current-tax-id", this.currentTax.getId().toString());
		}
		position.setAttribute("quantity", Integer.toString(this.quantity)); //$NON-NLS-1$
		position.setAttribute("price", Double.toString(this.price)); //$NON-NLS-1$
		position.setAttribute("discount", Double.toString(this.discount)); //$NON-NLS-1$
		position.setAttribute("galileo-book", Boolean.toString(this.galileoBook)); //$NON-NLS-1$
		position.setAttribute("galileo-booked", Boolean.toString(this.galileoBooked)); //$NON-NLS-1$
		position.setAttribute("opt-code", this.optCode); //$NON-NLS-1$
		position.setAttribute("author", this.author); //$NON-NLS-1$
		position.setAttribute("title", this.title); //$NON-NLS-1$
		position.setAttribute("publisher", this.publisher); //$NON-NLS-1$
		position.setAttribute("isbn", this.isbn); //$NON-NLS-1$
		position.setAttribute("bznr", this.bznr); //$NON-NLS-1$
		position.setAttribute("product-number", this.productNumber); //$NON-NLS-1$
		position.setAttribute("ordered", Boolean.toString(this.ordered)); //$NON-NLS-1$
		position.setAttribute("order-id", this.orderId); //$NON-NLS-1$
		position.setAttribute("stock", Boolean.toString(this.stock)); //$NON-NLS-1$
		position.setAttribute("update-customer-account", Boolean.toString(this.updateCustomerAccount)); //$NON-NLS-1$
		position.setAttribute("payed-invoice", Boolean.toString(this.payedInvoice)); //$NON-NLS-1$
		position.setAttribute("invoice", this.invoice.toString()); //$NON-NLS-1$
		position.setAttribute("invoice-date", this.invoiceDate == null ? "" : new Long(this.invoiceDate.getTime())
						.toString());
		position.setAttribute("tax", Double.toString(this.tax));
		position.setAttribute("type", new Integer(this.type).toString());
		position.setAttribute("amount-fc", new Double(this.amountFC).toString());
		position.setAttribute("amount", new Double(this.amount).toString());
		return position;
	}
	
	public void setRecordAttributes(Receipt r, Element position, boolean setIds, boolean useExportId)
					throws InvalidValueException
	{
		if (setIds)
			this.setId(new Long(position.getAttributeValue("id")));
		else
			this.setId(null); //$NON-NLS-1$
			
		this.setReceipt(r);
		this.productId = position.getAttributeValue("product-id"); //$NON-NLS-1$
		long time = 0l;
		if (useExportId)
		{
			this.setProductGroup(ProductGroup.selectByExportId(position.getAttributeValue("product-group-id"))); //$NON-NLS-1$
			Tax tax = Tax.selectByCode(position.getAttributeValue("tax-id"), true);
			Calendar gc = GregorianCalendar.getInstance();
			gc.setTimeInMillis(new Long(position.getAttributeValue("current-tax-id")).longValue());
			this.setCurrentTax(CurrentTax.selectFromDate(tax, gc.getTime()));
		}
		else
		{
			this.setProductGroup(ProductGroup.selectById(new Long(position.getAttributeValue("product-group-id")))); //$NON-NLS-1$
			this.setCurrentTax(CurrentTax.selectById(new Long(position.getAttributeValue("current-tax-id"))));
		}
		this.quantity = XMLLoader.getInt(position.getAttributeValue("quantity")); //$NON-NLS-1$
		this.price = XMLLoader.getDouble(position.getAttributeValue("price")); //$NON-NLS-1$
		this.discount = XMLLoader.getDouble(position.getAttributeValue("discount")); //$NON-NLS-1$
		this.galileoBook = XMLLoader.getBoolean(position.getAttributeValue("galileo-book")); //$NON-NLS-1$
		this.galileoBooked = XMLLoader.getBoolean(position.getAttributeValue("galileo-booked")); //$NON-NLS-1$
		this.optCode = position.getAttributeValue("opt-code"); //$NON-NLS-1$
		this.author = position.getAttributeValue("author"); //$NON-NLS-1$
		this.title = position.getAttributeValue("title"); //$NON-NLS-1$
		this.publisher = position.getAttributeValue("publisher"); //$NON-NLS-1$
		this.isbn = position.getAttributeValue("isbn"); //$NON-NLS-1$
		this.bznr = position.getAttributeValue("bznr"); //$NON-NLS-1$
		this.productNumber = position.getAttributeValue("product-number"); //$NON-NLS-1$
		this.ordered = XMLLoader.getBoolean(position.getAttributeValue("ordered")); //$NON-NLS-1$
		this.orderId = position.getAttributeValue("order-id"); //$NON-NLS-1$
		this.stock = XMLLoader.getBoolean(position.getAttributeValue("stock")); //$NON-NLS-1$
		this.updateCustomerAccount = XMLLoader.getBoolean(position.getAttributeValue("update-customer-account")); //$NON-NLS-1$
		// 10221
		this.payedInvoice = XMLLoader.getBoolean(position.getAttributeValue("payed-invoice"));
		this.invoice = new Integer(XMLLoader.getInt(position.getAttributeValue("invoice")));
		if (position.getAttributeValue("invoice-date").equals(""))
			this.invoiceDate = null;
		else
		{
			Calendar gc = GregorianCalendar.getInstance();
			gc.setTimeInMillis(XMLLoader.getLong(position.getAttributeValue("invoice-date")));
			this.invoiceDate = gc.getTime();
		}
		this.tax = XMLLoader.getDouble(position.getAttributeValue("tax"));
		this.type = XMLLoader.getInt(position.getAttributeValue("type")); //$NON-NLS-1$
		this.amountFC = XMLLoader.getDouble(position.getAttributeValue("amount-fc"));
		this.amount = XMLLoader.getDouble(position.getAttributeValue("amount"));
		if (this.amount == 0d) this.calculateAmount();
		
		if (this.getProductGroupId() == null || this.getProductGroupId().equals(Table.ZERO_VALUE))
			throw new InvalidValueException("Warengruppe mit der ExportId \""
							+ position.getAttributeValue("product-group-id") + "\" ungültig.");
		if (this.getCurrentTaxId() == null || this.getCurrentTaxId().equals(Table.ZERO_VALUE))
			throw new InvalidValueException("Mehrwertsteuersatz zur Mehrwertsteuer \""
							+ position.getAttributeValue("tax-id") + "\" und mit dem Datum \"" + new Date(time)
							+ "\" ungültig.");
	}
	
	public void setRecordAttributes(Receipt r, Element position, boolean useExportId) throws InvalidValueException
	{
		this.setRecordAttributes(r, position, false, useExportId);
	}
	
	public static Element getPositionData(String taxTypeCode, CurrentTax currentTax, ProductGroup productGroup,
					String[] fields, Translator translator)
	{
		Element detail = new Element("position"); //$NON-NLS-1$
		detail.setAttribute("id", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		detail.setAttribute("receipt-id", fields[1]); //$NON-NLS-1$
		detail.setAttribute("product-id", fields[4]); //$NON-NLS-1$
		detail.setAttribute("product-group-id", productGroup.getId().toString()); //$NON-NLS-1$
		detail.setAttribute("tax-id", currentTax.getTax().code);
		detail.setAttribute("current-tax-id", currentTax.getId().toString()); //$NON-NLS-1$
		detail.setAttribute("quantity", fields[8]); //$NON-NLS-1$
		detail.setAttribute("price", fields[9]); //$NON-NLS-1$
		detail
						.setAttribute(
										"discount", new Double(NumberUtility.round(		new Double(fields[7]).doubleValue() / 100, 2)).toString()); //$NON-NLS-1$
		detail.setAttribute("galileo-book", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		detail.setAttribute("galileo-booked", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		detail.setAttribute("opt-code", fields[6]); //$NON-NLS-1$
		detail.setAttribute("author", fields[5]); //$NON-NLS-1$
		detail.setAttribute("title", ""); //$NON-NLS-1$ //$NON-NLS-2$
		detail.setAttribute("publisher", ""); //$NON-NLS-1$ //$NON-NLS-2$
		detail.setAttribute("isbn", ""); //$NON-NLS-1$ //$NON-NLS-2$
		detail.setAttribute("bznr", ""); //$NON-NLS-1$ //$NON-NLS-2$
		detail.setAttribute("product-number", ""); //$NON-NLS-1$ //$NON-NLS-2$
		detail.setAttribute("ordered", Messages.getString("ReceiptTransfer.false_111")); //$NON-NLS-1$ //$NON-NLS-2$
		detail.setAttribute("order-id", ""); //$NON-NLS-1$ //$NON-NLS-2$
		detail.setAttribute("stock", "false");
		detail.setAttribute("update-customer-account", "false");
		detail.setAttribute("payed-invoice", "false");
		detail.setAttribute("invoice", "");
		detail.setAttribute("invoiceDate", "");
		detail.setAttribute("tax", "false");
		String type = null;
		if (fields[8].startsWith("-"))
			type = "2";
		else if (productGroup.galileoId == null || productGroup.galileoId.equals(""))
			type = "1";
		else
			type = "0";
		detail.setAttribute("type", type); //$NON-NLS-1$
		int qty = Integer.parseInt(fields[8]);
		double price = Double.parseDouble(fields[9]);
		double discount = NumberUtility.round(new Double(fields[7]).doubleValue() / 100, 2);
		Double amount = new Double(qty * price * (1 - discount));
		detail.setAttribute("amount-fc", amount.toString()); //$NON-NLS-1$
		detail.setAttribute("amount", amount.toString()); //$NON-NLS-1$
		return detail;
	}
	
	/**
	 * @return
	 */
	public int getPositionState()
	{
		return this.positionState;
	}
	
	public static final int STATE_SALE = 1;
	public static final int STATE_TAKE_BACK = -1;
}
