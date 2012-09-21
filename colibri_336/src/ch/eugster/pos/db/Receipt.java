/*
 * Created on 04.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.db;

import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.apache.ojb.broker.util.collections.RemovalAwareCollection;
import org.jdom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import ch.eugster.pos.InvalidValueException;
import ch.eugster.pos.Messages;
import ch.eugster.pos.product.GalileoServer;
import ch.eugster.pos.product.ProductServer;
import ch.eugster.pos.util.Config;
import ch.eugster.pos.util.NumberUtility;
import ch.eugster.pos.util.XMLLoader;

import com.ibm.bridge2java.ComException;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Receipt extends Table
{
	
	public int status = Receipt.RECEIPT_STATE_NEW;
	
	/**
	 * Nur noch aus Kompatibilitätsgründen anwesend. Wird nicht mehr benutzt.
	 */
	public double amount = Table.DOUBLE_DEFAULT_ZERO;
	
	private String number = ""; //$NON-NLS-1$
	private Long settlement;
	private Long transactionId;
	private Long bookingId;
	private Salespoint salespoint = new Salespoint();
	private Long salespointId = this.salespoint.getId();
	private User user = new User();
	private Long userId = this.user.getId();
	private boolean transferred = false;
	
	private ForeignCurrency foreignCurrency = ForeignCurrency.getDefaultCurrency();
	private Long foreignCurrencyId = this.foreignCurrency.getId();
	
	private RemovalAwareCollection positions = new RemovalAwareCollection();
	private RemovalAwareCollection payments = new RemovalAwareCollection();
	
	private String customerId = ""; //$NON-NLS-1$
	private Customer customer = new Customer();
	
	/**
	 * 
	 */
	private Receipt()
	{
		super();
	}
	
	private Receipt(Salespoint salespoint, User user, ForeignCurrency foreignCurrency)
	{
		this.init(salespoint, user, foreignCurrency);
	}
	
	public static Receipt getEmptyReceipt()
	{
		return new Receipt();
	}
	
	public static Receipt getReceipt(Salespoint salespoint, User user, ForeignCurrency foreignCurrency)
	{
		return new Receipt(salespoint, user, foreignCurrency);
	}
	
	/**
	 * inits a new Receipt
	 * 
	 * @param salespoint
	 *            the current salespoint object
	 * @param user
	 *            the current user
	 */
	private void init(Salespoint salespoint, User user, ForeignCurrency foreignCurrency)
	{
		this.setSalespoint(salespoint);
		this.setUser(user);
		this.setNumber();
		this.setCustomer();
	}
	
	public void setId(Long id)
	{
		super.setId(id);
		Position[] pos = (Position[]) this.positions.toArray(new Position[0]);
		for (int i = 0; i < pos.length; i++)
		{
			pos[i].setId(null);
			pos[i].receiptId = id.longValue() > 0l ? id : null;
		}
		Payment[] pay = (Payment[]) this.payments.toArray(new Payment[0]);
		for (int i = 0; i < pay.length; i++)
		{
			pay[i].setId(null);
			pay[i].receiptId = id.longValue() > 0l ? id : null;
		}
	}
	
	public Date getDate()
	{
		return new Date(this.timestamp.getTime());
	}
	
	public Time getTime()
	{
		return new Time(this.timestamp.getTime());
	}
	
	/**
	 * sets the current salespoint
	 * 
	 * @param salespoint
	 */
	public void setSalespoint(Salespoint salespoint)
	{
		this.salespoint = salespoint;
		this.salespointId = salespoint.getId();
	}
	
	/**
	 * returns the current salespoint
	 * 
	 * @return the current salespoint
	 */
	public Salespoint getSalespoint()
	{
		return this.salespoint;
	}
	
	/**
	 * returns the id of the current salespoint
	 * 
	 * @return the current salespoint id
	 */
	public Long getSalespointId()
	{
		return this.salespointId;
	}
	
	/**
	 * sets the current user
	 * 
	 * @param user
	 *            the current user
	 */
	public void setUser(User user)
	{
		this.user = user;
		this.userId = user.getId();
	}
	
	/**
	 * returns the current user
	 * 
	 * @return the current user
	 */
	public User getUser()
	{
		return this.user;
	}
	
	/**
	 * returns the id of current user
	 * 
	 * @return the id of current user
	 */
	public Long getUserId()
	{
		return this.userId;
	}
	
	// public void setDefaultCurrency(ForeignCurrency currency) {
	// this.defaultCurrency = currency;
	// this.defaultCurrencyId = currency.getId();
	// }
	//
	// public Long getDefaultCurrencyId() {
	// return defaultCurrencyId;
	// }
	//
	// public ForeignCurrency getDefaultCurrency() {
	// return defaultCurrency;
	// }
	//
	public void setDefaultCurrency(ForeignCurrency foreignCurrency)
	{
		this.foreignCurrency = foreignCurrency;
		this.foreignCurrencyId = foreignCurrency.getId();
	}
	
	public Long getDefaultCurrencyId()
	{
		return this.foreignCurrencyId;
	}
	
	public ForeignCurrency getDefaultCurrency()
	{
		return this.foreignCurrency;
	}
	
	/**
	 * sets the current receiptnumber. The receipt number is a String value,
	 * computed from the current time
	 * 
	 */
	public void setNumber()
	{
		this.number = "";
	}
	
	/**
	 * sets the receiptnumber. The receipt number is a String value
	 * 
	 */
	public void setNumber(String number)
	{
		this.number = number;
	}
	
	/**
	 * returns the receipt's number
	 * 
	 * @return the receipts number
	 * @see ch.eugster.pos.db.Receipt#setNumber
	 */
	public String getNumber()
	{
		return this.number;
	}
	
	public String getFormattedNumber()
	{
		int length = Integer.parseInt(Config.getInstance().getReceiptHeaderNumberLength());
		if (length <= 0)
			return this.getNumber();
		else
			return this.getNumber().substring(this.getNumber().length() - length);
	}
	
	/**
	 * sets the settlement
	 * 
	 * @param settlement
	 *            the settlement that is set
	 */
	public void setSettlement(Long settlement)
	{
		this.settlement = settlement;
	}
	
	/**
	 * returns the settlement
	 * 
	 * @return the settlement
	 */
	public Long getSettlement()
	{
		return this.settlement;
	}
	
	public void setTransactionId(Long transactionId)
	{
		this.transactionId = transactionId;
	}
	
	public Long getTransactionId()
	{
		return this.transactionId;
	}
	
	public void setBookingId(Long bookingId)
	{
		this.bookingId = bookingId;
	}
	
	public Long getBookingId()
	{
		return this.bookingId;
	}
	
	/**
	 * returns the amount of the receipt
	 * 
	 * @return the amount of the receipt
	 */
	public double getAmount()
	{
		double a = 0d;
		Position[] p = (Position[]) this.positions.toArray(new Position[0]);
		for (int i = 0; i < p.length; i++)
		{
			a += p[i].getAmount();
		}
		return NumberUtility.round(a, ForeignCurrency.getDefaultCurrency().roundFactor);
	}
	
	/**
	 * returns the sum of values of position.amountFC
	 * 
	 * @return the sum of values of position.amountFC
	 */
	private double getAmountFC()
	{
		double amount = 0d;
		Position[] p = (Position[]) this.positions.toArray(new Position[0]);
		for (int i = 0; i < p.length; i++)
		{
			if (p[i].getProductGroup().type == ProductGroup.TYPE_INPUT)
			{
				amount += p[i].amountFC * p[i].getQuantity();
			}
			else if (p[i].getProductGroup().type == ProductGroup.TYPE_WITHDRAW)
			{
				amount += p[i].amountFC * p[i].getQuantity();
			}
			else
			{
				amount += 0d;
			}
		}
		return amount;
	}
	
	/**
	 * returns the amount of the receipt in foreign currency value
	 * 
	 * @return the amount of the receipt
	 */
	public double getAmountFC(ForeignCurrency currency)
	{
		double amount = this.getAmountFC();
		if (amount == 0d)
			return NumberUtility.round(this.getAmount() / currency.quotation, currency.roundFactor);
		else
			return amount;
	}
	
	/**
	 * gibt das Total der Zahlungen in der Landeswährung zurück
	 * 
	 * @return the total of payments without any backmoney
	 */
	public double getPayment()
	{
		double d = 0d;
		Iterator i = this.payments.iterator();
		while (i.hasNext())
		{
			Payment p = (Payment) i.next();
			d = d + p.getAmount();
		}
		return d;
	}
	
	/**
	 * gibt das Total der Zahlungen in der Landeswährung zurück
	 * 
	 * @return the total of payments without any backmoney
	 */
	// public Double getPaymentDiff() {
	// //TODO
	// double d = 0d;
	// Iterator i = payments.iterator();
	// while (i.hasNext()) {
	// Payment p = (Payment)i.next();
	// d = d + p.getAmountFC() * p.getQuotation();
	// }
	// return new Double(d);
	// }
	/**
	 * gibt das Total der Zahlungen - ohne Rueckgeld - basierend auf der
	 * übergebenen Fremdwaehrung, aber mit den Umrechnungsfaktoren der
	 * Fremdwährung in die Landeswährung konvertiert
	 * 
	 * @return the total of payments without any backmoney
	 */
	// 10230 public Double getPaymentAmountFC(ForeignCurrency foreignCurrency) {
	public double getPaymentAmountFC()
	{
		double d = 0d;
		Iterator i = this.payments.iterator();
		while (i.hasNext())
		{
			Payment p = (Payment) i.next();
			d += p.getAmountFC();
		}
		return d;
	}
	
	public double getPaymentAmountFC(ForeignCurrency currentCurrency)
	{
		double d = 0d;
		Iterator i = this.payments.iterator();
		while (i.hasNext())
		{
			Payment p = (Payment) i.next();
			if (!currentCurrency.getId().equals(ForeignCurrency.getDefaultCurrency()))
			{
				if (p.getForeignCurrency().getId().equals(ForeignCurrency.getDefaultCurrency().getId()))
					d += p.getAmountFC() / currentCurrency.quotation;
				else
					d += p.getAmountFC();
			}
		}
		return d;
	}
	
	/**
	 * returns the total of backmoney
	 * 
	 * @return the total of backmoney
	 */
	public double getBack()
	{
		double d = 0d;
		Iterator i = this.payments.iterator();
		while (i.hasNext())
		{
			Payment p = (Payment) i.next();
			if (p.isBack())
			{
				d = d + p.getAmount();
			}
		}
		return d;
	}
	
	/**
	 * returns the total of backmoney
	 * 
	 * @return the total of backmoney
	 */
	public double getBackFC()
	{
		double d = 0d;
		Iterator i = this.payments.iterator();
		while (i.hasNext())
		{
			Payment p = (Payment) i.next();
			d = d + p.getAmountFC();
		}
		return d;
	}
	
	/**
	 * @return
	 */
	public boolean getTransferred()
	{
		return this.transferred;
	}
	
	/**
	 * examines the subtype of <code>ReceiptChild</code> and adds it to the
	 * list. There are currently two subtypes <code>position</code> and
	 * <code>payment</code>.
	 * 
	 * @param child
	 *            the receipt's child to add
	 * @return <code>true</code> if successfull added
	 * @see ch.eugster.pos.db.Receipt#addPosition
	 * @see ch.eugster.pos.db.Receipt@addPayment
	 */
	public boolean addChild(ReceiptChild child)
	{
		boolean added = false;
		if (child instanceof Position)
		{
			this.addPosition((Position) child);
			added = true;
		}
		else if (child instanceof Payment)
		{
			added = this.addPayment((Payment) child);
		}
		return added;
	}
	
	/**
	 * adds a <code>position</code> to the list
	 * 
	 * @param position
	 *            the <code>position</code> to add
	 * @return <code>true</code> if successfull added
	 */
	public int addPosition(Position position)
	{
		int pos = 0;
		int i = this.exists(position);
		if (i > -1)
		{
			int qty0 = 0;
			if (position.orderId != null && !position.orderId.equals(""))
			{
				qty0 = Position.countOrderedItemsUsed(position.orderId);
				qty0 += position.getOrderedQuantity();
			}
			int qty1 = ((Position) this.positions.get(i)).getQuantity();
			qty1 += position.getQuantity();
			if (position.orderId != null && !position.orderId.equals(""))
				if (qty1 > qty0)
					((Position) this.positions.get(i)).setQuantity(qty0);
				else
					((Position) this.positions.get(i)).setQuantity(qty1);
			else
				((Position) this.positions.get(i)).setQuantity(qty1);
		}
		else
		{
			this.positions.add(pos, position);
		}
		// amount = computeAmount();
		return pos;
	}
	
	private int exists(Position p)
	{
		for (int i = 0; i < this.positions.size(); i++)
		{
			Position tmp = (Position) this.positions.get(i);
			if (tmp.productId.equals(p.productId))
			{
				if (tmp.getProductGroupId().equals(p.getProductGroupId()))
				{
					if (tmp.getCurrentTaxId().equals(p.getCurrentTaxId()))
					{
						if (tmp.ordered == p.ordered)
						{
							if (tmp.orderId.equals(p.orderId))
							{
								if (tmp.getPrice() == p.getPrice())
								{
									if (tmp.getDiscount() == p.getDiscount())
									{
										if (tmp.getQuantity() + p.getQuantity() == 0)
											return -1;
										else
										{
											
											return i;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return -1;
	}
	
	/**
	 * adds a <code>payment</code> to the list
	 * 
	 * @param payment
	 *            the <code>payment</code> to add
	 * @return <code>true</code> if successfull added
	 */
	public boolean addPayment(Payment p)
	{
		boolean added = this.payments.add(p);
		// if (added) {
		// payment = computePayment();
		// paymentFC = computePaymentFC();
		// }
		return added;
	}
	
	/**
	 * replaces an existing child with a new one. The child can be one of the
	 * following subtypes of <code>ReceiptChild</code>: <code>Position</code> or
	 * <code>Payment</code>.
	 * 
	 * @param index
	 *            the index in the list where to find the existing child
	 * @param child
	 *            the new child
	 * @return the exchanged child in the list
	 */
	public ReceiptChild setChild(int index, ReceiptChild child)
	{
		ReceiptChild rc = null;
		if (child instanceof Position)
		{
			rc = this.setPosition(index, (Position) child);
		}
		else if (child instanceof Payment)
		{
			rc = this.setPayment(index, (Payment) child);
		}
		return rc;
	}
	
	/**
	 * replaces an existing Position with a new one.
	 * 
	 * @param index
	 *            the index in the list where to find the existing position
	 * @param p
	 *            the new position
	 * @return the exchanged position in the list
	 */
	public Position setPosition(int index, Position p)
	{
		Position old = (Position) this.positions.get(index);
		if (old != null)
		{
			p.setId(old.getId());
		}
		Position q = (Position) this.positions.set(index, p);
		// amount = computeAmount();
		return q;
	}
	
	/**
	 * replaces an existing Payment with a new one.
	 * 
	 * @param index
	 *            the index in the list where to find the existing payment
	 * @param p
	 *            the new payment
	 * @return the exchanged payment in the list
	 */
	public Payment setPayment(int index, Payment p)
	{
		Payment q = (Payment) this.payments.set(index, p);
		// payment = computePayment();
		// paymentFC = computePaymentFC();
		return q;
	}
	
	/**
	 * removes all existing children. The children can be one of the following
	 * subtypes of <code>ReceiptChild</code>: <code>Position</code> or
	 * <code>Payment</code>.
	 * 
	 * @param cls
	 *            the subclass
	 * @return boolean success
	 */
	public void removeChildren(Class cls)
	{
		if (cls == Position.class)
		{
			this.removePositions();
		}
		else if (cls == Payment.class)
		{
			this.removePayments();
		}
	}
	
	/**
	 * removes all existing positions.
	 */
	public void removePositions()
	{
		this.positions.removeAllElements();
		// amount = computeAmount();
		
	}
	
	/**
	 * removes all existing payments.
	 */
	public void removePayments()
	{
		this.payments.removeAllElements();
		// payment = computePayment();
	}
	
	/**
	 * removes an existing child at a given index. The child can be one of the
	 * following subtypes of <code>ReceiptChild</code>: <code>Position</code> or
	 * <code>Payment</code>.
	 * 
	 * @param index
	 *            the index in the list where to find the existing child
	 * @param cls
	 *            the subclass
	 * @return the removed child in the list
	 */
	public ReceiptChild removeChild(int index, Class cls)
	{
		ReceiptChild rc = null;
		if (cls == Position.class)
		{
			rc = this.removePosition(index);
		}
		else if (cls == Payment.class)
		{
			rc = this.removePayment(index);
		}
		return rc;
	}
	
	/**
	 * removes an existing position at a given index.
	 * 
	 * @param index
	 *            the index in the list where to find the existing position
	 * @return the removed position in the list
	 */
	public Position removePosition(int index)
	{
		Position p = null;
		if (index < this.positions.size())
		{
			p = (Position) this.positions.remove(index);
			if (!(p == null))
			{
				// amount = computeAmount();
			}
		}
		return p;
	}
	
	/**
	 * removes an existing payment at a given index.
	 * 
	 * @param index
	 *            the index in the list where to find the existing payment
	 * @return the removed payment in the list
	 */
	public Payment removePayment(int index)
	{
		Payment p = null;
		if (index < this.payments.size())
		{
			p = (Payment) this.payments.remove(index);
			// if (!(p == null)) {
			// payment = computePayment();
			// paymentFC = computePaymentFC();
			// }
		}
		return p;
	}
	
	public ReceiptChild getChildAt(int index, Class cls)
	{
		ReceiptChild child = null;
		if (cls == Position.class)
		{
			child = this.getPositionAt(index);
		}
		else if (cls == Payment.class)
		{
			child = this.getPaymentAt(index);
		}
		return child;
	}
	
	public Position getPositionAt(int index)
	{
		return (Position) this.positions.get(index);
	}
	
	public Payment getPaymentAt(int index)
	{
		return (Payment) this.payments.get(index);
	}
	
	public Double computeAmount()
	{
		double d = 0d;
		Iterator i = this.positions.iterator();
		while (i.hasNext())
		{
			d += ((Position) i.next()).getAmount();
		}
		return new Double(NumberUtility.round(d, ForeignCurrency.getDefaultCurrency().getCurrency()
						.getDefaultFractionDigits()));
	}
	
	// private Double computePayment()
	// {
	// double d = 0d;
	// Iterator i = this.payments.iterator();
	// while (i.hasNext())
	// {
	// d += ((Payment) i.next()).getAmount();
	// }
	// return new Double(NumberUtility.round(d, ForeignCurrency
	// .getDefaultCurrency().getCurrency().getDefaultFractionDigits()));
	// }
	
	public RemovalAwareCollection getChildren(Class cls)
	{
		RemovalAwareCollection rac = null;
		if (cls == Position.class)
		{
			rac = this.getPositions();
		}
		else if (cls == Payment.class)
		{
			rac = this.getPayments();
		}
		return rac;
	}
	
	public RemovalAwareCollection getPositions()
	{
		return this.positions;
	}
	
	public RemovalAwareCollection getPayments()
	{
		return this.payments;
	}
	
	public ReceiptChild[] getChildrenAsArray(Class cls)
	{
		ReceiptChild[] rc = null;
		if (cls == Position.class)
		{
			rc = this.getPositionsAsArray();
		}
		else if (cls == Payment.class)
		{
			rc = this.getPaymentsAsArray();
		}
		return rc;
	}
	
	public Position[] getPositionsAsArray()
	{
		return (Position[]) this.positions.toArray(new Position[0]);
	}
	
	public Payment[] getPaymentsAsArray()
	{
		return (Payment[]) this.payments.toArray(new Payment[0]);
	}
	
	public boolean isBalanced()
	{
		return this.getAmount() == this.getPayment();
	}
	
	public int getChildrenCount(Class cls)
	{
		int size = 0;
		if (cls == Position.class)
		{
			size = this.getPositionCount();
		}
		else if (cls == Payment.class)
		{
			size = this.getPaymentCount();
		}
		return size;
	}
	
	public int getPositionCount()
	{
		return this.positions.size();
	}
	
	public int getPaymentCount()
	{
		return this.payments.size();
	}
	
	public boolean isRemovable()
	{
		return true;
	}
	
	public void setTransferred(boolean transferred)
	{
		this.transferred = transferred;
	}
	
	public boolean isTransferred()
	{
		return this.transferred;
	}
	
	public ForeignCurrency getMainForeignCurrency()
	{
		List cashdrawers = Config.getInstance().getPosPrinterCashDrawers();
		Element cashdrawer = Config.getInstance().getCashDrawer(cashdrawers, "2"); //$NON-NLS-1$
		
		String currency = ""; //$NON-NLS-1$
		if (new Boolean(cashdrawer.getAttributeValue("use")).booleanValue()) { //$NON-NLS-1$
			currency = cashdrawer.getAttributeValue("currency"); //$NON-NLS-1$
		}
		
		Payment[] p = this.getPaymentsAsArray();
		for (int i = 0; i < p.length; i++)
		{
			if (currency.length() == 0)
			{
				if (p[i].getQuotation() != 1d)
				{
					return p[i].getForeignCurrency();
				}
			}
			else
			{
				if (p[i].getForeignCurrency().code.equals(currency))
				{
					return p[i].getForeignCurrency();
				}
			}
		}
		return ForeignCurrency.getDefaultCurrency();
	}
	
	public DBResult store(boolean updateNumber, boolean updateTimestamp)
	{
		Logger.getLogger("colibri").info("Entered: Receipt.store()");
		this.setNumber(this.extractNumber(updateNumber));
		
		// 10205
		// if (status == Receipt.RECEIPT_STATE_SERIALIZED || status ==
		// Receipt.RECEIPT_STATE_REVERSED) {
		if (this.status == Receipt.RECEIPT_STATE_SERIALIZED || this.status == Receipt.RECEIPT_STATE_REVERSED)
		{
			// 10205
			if (Database.getCurrent().equals(Database.getStandard()))
			{
				this.bookGalileo();
			}
		}
		
		DBResult result = this.store(updateTimestamp);
		
		if (updateNumber)
		{
			Salespoint.getCurrent().setNextReceiptNumber(); // 10193
		}
		
		return result;
	}
	
	public String extractNumber(boolean update)
	{
		String pre = null;
		String salespoint = Receipt.salespointNumberFormat.format(this.getSalespointId());
		String number = null;
		
		if (this.status == Receipt.RECEIPT_STATE_NEW)
		{
			pre = Receipt.PRE_POSITION_CONSTANT_NEW;
			number = Receipt.receiptNumberFormat.format(Salespoint.getCurrent().getNextTemporaryReceiptNumber()
							.longValue());
		}
		else if (this.status == Receipt.RECEIPT_STATE_PARKED)
		{
			pre = Receipt.PRE_POSITION_CONSTANT_PARKED;
			number = Receipt.receiptNumberFormat.format(Salespoint.getCurrent().getNextTemporaryReceiptNumber()
							.longValue());
		}
		else if (this.status == Receipt.RECEIPT_STATE_REVERSED)
		{
			if (Database.getCurrent().equals(Database.getTemporary()))
			{
				pre = Receipt.PRE_POSITION_CONSTANT_FAILOVER;
				number = Receipt.receiptNumberFormat.format(Salespoint.getCurrent().getNextTemporaryReceiptNumber()
								.longValue());
			}
			else
			{
				pre = Receipt.PRE_POSITION_CONSTANT_REVERSED;
				if (update)
				{
					number = Receipt.receiptNumberFormat.format(Salespoint.getCurrent().getNextReceiptNumber()
									.longValue());
				}
				else
				{
					number = this.number.substring(6, this.number.length());
				}
			}
		}
		else if (this.status == Receipt.RECEIPT_STATE_SERIALIZED)
		{
			if (Database.getCurrent().equals(Database.getTemporary()))
			{
				pre = Receipt.PRE_POSITION_CONSTANT_FAILOVER;
				number = Receipt.receiptNumberFormat.format(Salespoint.getCurrent().getNextTemporaryReceiptNumber()
								.longValue());
			}
			else
			{
				pre = Receipt.PRE_POSITION_CONSTANT;
				if (update)
				{
					number = Receipt.receiptNumberFormat.format(Salespoint.getCurrent().getNextReceiptNumber()
									.longValue());
				}
				else
				{
					number = this.number.substring(6, this.number.length());
				}
			}
		}
		return pre + salespoint + number;
	}
	
	public void update()
	{
		this.setDefaultCurrency(ForeignCurrency.getById(this.getDefaultCurrencyId()));
		this.setSalespoint(Salespoint.getById(this.getSalespointId()));
		this.setUser(User.getById(this.getUserId()));
		this.updatePositions();
		this.updatePayments();
	}
	
	private void updatePositions()
	{
		Iterator iterator = this.getPositions().iterator();
		while (iterator.hasNext())
		{
			Position p = (Position) iterator.next();
			p.setProductGroup(ProductGroup.getById(p.getProductGroupId()));
			p.setCurrentTax(p.getProductGroup().getDefaultTax().getCurrentTax());
			p.setReceipt(this);
		}
	}
	
	private void updatePayments()
	{
		Iterator iterator = this.getPayments().iterator();
		while (iterator.hasNext())
		{
			Payment p = (Payment) iterator.next();
			p.setForeignCurrency(ForeignCurrency.getById(p.getForeignCurrencyId()));
			p.setPaymentType(PaymentType.getById(p.getPaymentTypeId()));
			p.setReceipt(this);
		}
	}
	
	public boolean openCashdrawer()
	{
		Iterator iterator = this.getPayments().iterator();
		while (iterator.hasNext())
		{
			Payment p = (Payment) iterator.next();
			if (p.getPaymentType().openCashdrawer)
			{
				return true;
			}
		}
		return false;
	}
	
	public static boolean exist(String key, Object value)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo(key, value);
		Query query = QueryFactory.newQuery(Receipt.class, criteria);
		query.setEndAtIndex(1);
		Collection positions = Table.select(query);
		Iterator i = positions.iterator();
		return i.hasNext();
	}
	
	public static Receipt selectById(Long pk)
	{
		Receipt receipt = new Receipt();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("id", pk); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Receipt.class, criteria);
		Collection receipts = Table.select(query);
		Iterator i = receipts.iterator();
		if (i.hasNext())
		{
			receipt = (Receipt) i.next();
		}
		return receipt;
	}
	
	public static int countByNumber(String number)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("number", number); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Receipt.class, criteria);
		return Database.getCurrent().getBroker().getCount(query);
	}
	
	private static Criteria getSalespointCriteria(Salespoint[] salespoints)
	{
		Criteria criteria = new Criteria();
		if (salespoints != null)
		{
			if (salespoints.length != 0)
			{
				criteria.addEqualTo("salespointId", salespoints[0].getId()); //$NON-NLS-1$
				for (int i = 1; i < salespoints.length; i++)
				{
					Criteria orCriteria = new Criteria();
					orCriteria.addEqualTo("salespointId", salespoints[i].getId()); //$NON-NLS-1$
					criteria.addOrCriteria(orCriteria);
				}
			}
		}
		return criteria;
	}
	
	/**
	 * 
	 * @param number
	 *            Die Belegnummer
	 * @return
	 */
	public static int count(String number)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("number", number); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Receipt.class, criteria);
		return Table.select(query).size();
	}
	
	// 10439
	/**
	 * 
	 * @param number
	 *            Die Belegnummer
	 * @return
	 */
	public static int count(Timestamp timestamp, Long salespointId, String number)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("number", number); //$NON-NLS-1$
		criteria.addEqualTo("salespointId", salespointId);
		criteria.addEqualTo("timestamp", timestamp);
		Query query = QueryFactory.newQuery(Receipt.class, criteria);
		return Table.select(query).size();
	}
	
	// 10439
	/**
	 * 
	 * @param number
	 *            Die Belegnummer
	 * @return
	 */
	public static int count(Long salespointId, String number)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("number", number); //$NON-NLS-1$
		criteria.addEqualTo("salespointId", salespointId);
		Query query = QueryFactory.newQuery(Receipt.class, criteria);
		return Table.select(query).size();
	}
	
	/**
	 * 
	 * @param number
	 *            Die Belegnummer
	 * @return
	 */
	public static long count(Salespoint[] salespoints, Date from, Date to, boolean reversedToo)
	{
		return Receipt.count(salespoints, from, to, reversedToo, false);
	}
	
	/**
	 * 
	 * @param number
	 *            Die Belegnummer
	 * @return
	 */
	public static long count(Salespoint[] salespoints, Date from, Date to, boolean reversedToo, boolean onlySettled)
	{
		String[] fields = new String[]
		{ "count(*)" };
		
		Criteria criteria = new Criteria();
		
		Timestamp ts1 = new Timestamp(from.getTime());
		Timestamp ts2 = new Timestamp(to.getTime());
		criteria.addBetween("timestamp", ts1, ts2); //$NON-NLS-1$
		if (onlySettled) criteria.addNotNull("settlement"); //$NON-NLS-1$
			
		if (salespoints != null) criteria.addAndCriteria(Receipt.getSalespointCriteria(salespoints));
		
		Criteria statusCriteria = new Criteria();
		statusCriteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		
		if (reversedToo)
		{
			Criteria reversedCriteria = new Criteria();
			reversedCriteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_REVERSED)); //$NON-NLS-1$
			statusCriteria.addOrCriteria(reversedCriteria);
		}
		
		Logger.getLogger("colibri").info("Statuskriterium als Kriterium in Abfrage einfügen...");
		criteria.addAndCriteria(statusCriteria);
		
		Logger.getLogger("colibri").info("Abfrage wird aufgebaut...");
		ReportQueryByCriteria query = new ReportQueryByCriteria(Receipt.class, fields, criteria);
		
		long items = 0;
		Logger.getLogger("colibri").info("Ergebnismenge wird in der Datenbank abgefragt...");
		Iterator iter = Database.getCurrent().getBroker().getReportQueryIteratorByQuery(query);
		if (iter != null && iter.hasNext())
		{
			Logger.getLogger("colibri").info("Das Resultat wird umgewandelt...");
			Object[] count = (Object[]) iter.next();
			Logger.getLogger("colibri").info("Datentyp der Variablen ist " + count[0].getClass().getName());
			// 10088
			if (count[0] instanceof Long)
			{
				items = ((Long) count[0]).longValue();
			}
			else if (count[0] instanceof Integer)
			{
				items = ((Integer) count[0]).longValue();
			}
			// 10088
		}
		Logger.getLogger("colibri").info("Rückgabewert an aufrufende Methode zurückgeben und Methode verlassen...");
		return items;
	}
	
	public static int countSettled(Salespoint[] salespoints, Date from, Date to, boolean reversedToo)
	{
		Criteria criteria = new Criteria();
		
		Timestamp ts1 = new Timestamp(from.getTime());
		Timestamp ts2 = new Timestamp(to.getTime());
		criteria.addBetween("timestamp", ts1, ts2); //$NON-NLS-1$
		criteria.addNotNull("settlement"); //$NON-NLS-1$
		if (salespoints != null) criteria.addAndCriteria(Receipt.getSalespointCriteria(salespoints));
		
		Criteria statusCriteria = new Criteria();
		statusCriteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		
		if (reversedToo)
		{
			Criteria reversedCriteria = new Criteria();
			reversedCriteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_REVERSED)); //$NON-NLS-1$
			statusCriteria.addOrCriteria(reversedCriteria);
		}
		
		Logger.getLogger("colibri").info("Statuskriterium als Kriterium in Abfrage einfügen...");
		criteria.addAndCriteria(statusCriteria);
		
		QueryByCriteria query = new QueryByCriteria(Receipt.class, criteria);
		
		return Database.getCurrent().getBroker().getCount(query);
	}
	
	public static Iterator selectSettled(Salespoint[] salespoints, Date from, Date to, boolean reversedToo)
	{
		Criteria criteria = new Criteria();
		
		Timestamp ts1 = new Timestamp(from.getTime());
		Timestamp ts2 = new Timestamp(to.getTime());
		criteria.addBetween("timestamp", ts1, ts2); //$NON-NLS-1$
		criteria.addNotNull("settlement"); //$NON-NLS-1$
		if (salespoints != null) criteria.addAndCriteria(Receipt.getSalespointCriteria(salespoints));
		
		Criteria statusCriteria = new Criteria();
		statusCriteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		
		if (reversedToo)
		{
			Criteria reversedCriteria = new Criteria();
			reversedCriteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_REVERSED)); //$NON-NLS-1$
			statusCriteria.addOrCriteria(reversedCriteria);
		}
		
		Logger.getLogger("colibri").info("Statuskriterium als Kriterium in Abfrage einfügen...");
		criteria.addAndCriteria(statusCriteria);
		
		QueryByCriteria query = new QueryByCriteria(Receipt.class, criteria);
		
		return Database.getCurrent().getBroker().getIteratorByQuery(query);
	}
	
	public static Collection selectSettled(Salespoint[] salespoints, Date from, boolean reversedToo)
	{
		Criteria criteria = new Criteria();
		
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(from);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		
		Timestamp ts1 = new Timestamp(from.getTime());
		Timestamp ts2 = new Timestamp(calendar.getTimeInMillis());
		
		criteria.addBetween("timestamp", ts1, ts2); //$NON-NLS-1$
		criteria.addNotNull("settlement"); //$NON-NLS-1$
		if (salespoints != null) criteria.addAndCriteria(Receipt.getSalespointCriteria(salespoints));
		
		Criteria statusCriteria = new Criteria();
		statusCriteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		
		if (reversedToo)
		{
			Criteria reversedCriteria = new Criteria();
			reversedCriteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_REVERSED)); //$NON-NLS-1$
			statusCriteria.addOrCriteria(reversedCriteria);
		}
		
		Logger.getLogger("colibri").info("Statuskriterium als Kriterium in Abfrage einfügen...");
		criteria.addAndCriteria(statusCriteria);
		
		QueryByCriteria query = new QueryByCriteria(Receipt.class, criteria);
		
		return Database.getCurrent().getBroker().getCollectionByQuery(query);
	}
	
	/**
	 * 
	 * @param user
	 *            Der Benutzer, dessen Belege selektiert werden sollen
	 * @param state
	 *            Der Status der zu selektierenden Belege
	 * @return Receipt[] Rueckgabe eines Arrays vom Typ Receipt
	 */
	public static Receipt[] select(User user, int state)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("status", new Integer(state)); //$NON-NLS-1$
		criteria.addEqualTo("userId", user.getId()); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Receipt.class, criteria);
		Collection receipts = Table.select(query);
		return (Receipt[]) receipts.toArray(new Receipt[0]);
	}
	
	/**
	 * 10435
	 * 
	 * @param salespoint
	 *            Die Kasse, zu der die Belege gesucht werden sollen
	 * @param state
	 *            Der Status der zu selektierenden Belege
	 * @return Receipt[] Rueckgabe eines Arrays vom Typ Receipt
	 */
	public static Receipt[] select(Salespoint salespoint, int state)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("status", new Integer(state)); //$NON-NLS-1$
		criteria.addEqualTo("salespointId", salespoint.getId()); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Receipt.class, criteria);
		Collection receipts = Table.select(query);
		return (Receipt[]) receipts.toArray(new Receipt[0]);
	}
	
	/*
	 * 10435
	 */
	
	/**
	 * 
	 * @return Receipt[] Rueckgabe eines Arrays vom Typ Receipt
	 */
	public static Object select()
	{
		String[] fields = new String[]
		{ "MAX(id)" };
		Object object = null;
		Criteria criteria = new Criteria();
		ReportQueryByCriteria query = new ReportQueryByCriteria(Receipt.class, fields, criteria);
		Iterator iter = Database.getCurrent().getBroker().getReportQueryIteratorByQuery(query);
		if (iter.hasNext()) object = iter.next();
		
		return object;
	}
	
	public static Iterator selectToCorrect(Date from, Date to)
	{
		Criteria criteria = new Criteria();
		if (from instanceof Date && to instanceof Date)
			criteria.addBetween("timestamp", from, to);
		else if (from instanceof Date)
			criteria.addGreaterOrEqualThan("timestamp", from);
		else if (to instanceof Date) criteria.addLessOrEqualThan("timestamp", to);
		
		criteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED));
		
		QueryByCriteria query = QueryFactory.newQuery(Receipt.class, criteria);
		query.addOrderBy("timestamp", false); //$NON-NLS-1$
		return Table.selectToIterator(query);
	}
	
	/**
	 * 
	 * @param salespoints
	 *            Die Kassen, die ausgewertet werden sollen
	 * @param from
	 *            Das Startdatum des Datumbereichs
	 * @param to
	 *            Das Enddatum des Datumbereichs
	 * @return Receipt[] Rueckgabe eines Arrays vom Typ Receipt
	 */
	public static Receipt[] select(Salespoint[] salespoints, Date from, Date to, boolean reversedToo)
	{
		Criteria criteria = new Criteria();
		Criteria statusCriteria = new Criteria();
		Criteria reversedCriteria = new Criteria();
		Criteria salespointCriteria = Receipt.getSalespointCriteria(salespoints);
		
		Timestamp ts1 = new Timestamp(from.getTime());
		Timestamp ts2 = new Timestamp(to.getTime());
		criteria.addBetween("timestamp", ts1, ts2); //$NON-NLS-1$
		criteria.addNotNull("settlement"); //$NON-NLS-1$
		
		statusCriteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		statusCriteria.addAndCriteria(salespointCriteria);
		
		if (reversedToo)
		{
			reversedCriteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_REVERSED)); //$NON-NLS-1$
			reversedCriteria.addAndCriteria(salespointCriteria);
			statusCriteria.addOrCriteria(reversedCriteria);
		}
		
		criteria.addAndCriteria(statusCriteria);
		
		QueryByCriteria query = new ReportQueryByCriteria(Receipt.class, criteria);
		query.addOrderByAscending("timestamp");
		
		Collection receipts = Database.getCurrent().getBroker().getCollectionByQuery(query);
		return (Receipt[]) receipts.toArray(new Receipt[0]);
	}
	
	/**
	 * 
	 * @param salespoints
	 *            Die Kassen, die ausgewertet werden sollen
	 * @param from
	 *            Das Startdatum des Datumbereichs
	 * @param to
	 *            Das Enddatum des Datumbereichs
	 * @return Receipt[] Rueckgabe eines Arrays vom Typ Receipt
	 */
	public static Receipt[] selectAll(Salespoint[] salespoints, Date from, Date to, boolean reversedToo)
	{
		Criteria criteria = new Criteria();
		Criteria statusCriteria = new Criteria();
		Criteria reversedCriteria = new Criteria();
		Criteria salespointCriteria = Receipt.getSalespointCriteria(salespoints);
		
		Timestamp ts1 = new Timestamp(from.getTime());
		Timestamp ts2 = new Timestamp(to.getTime());
		criteria.addBetween("timestamp", ts1, ts2); //$NON-NLS-1$
		
		statusCriteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		statusCriteria.addAndCriteria(salespointCriteria);
		
		if (reversedToo)
		{
			reversedCriteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_REVERSED)); //$NON-NLS-1$
			reversedCriteria.addAndCriteria(salespointCriteria);
			statusCriteria.addOrCriteria(reversedCriteria);
		}
		
		criteria.addAndCriteria(statusCriteria);
		
		QueryByCriteria query = new ReportQueryByCriteria(Receipt.class, criteria);
		query.addOrderByAscending("timestamp");
		
		Collection receipts = Database.getCurrent().getBroker().getCollectionByQuery(query);
		return (Receipt[]) receipts.toArray(new Receipt[0]);
	}
	
	// 10080
	/**
	 * 
	 * @param from
	 *            Das Startdatum des Datumbereichs
	 * @param to
	 *            Das Enddatum des Datumbereichs
	 * @param maxRows
	 *            Maximale Anzahl Datensätze
	 * @return Receipt[] Rueckgabe eines Arrays vom Typ Receipt
	 */
	public static Receipt[] select(Date from, Date to, boolean reversedToo, int maxRows)
	{
		return Receipt.select(from, to, reversedToo, true, maxRows);
	}
	
	// 10080
	// 10080
	/**
	 * 
	 * @param from
	 *            Das Startdatum des Datumbereichs
	 * @param to
	 *            Das Enddatum des Datumbereichs
	 * @param maxRows
	 *            Maximale Anzahl Datensätze
	 * @return Receipt[] Rueckgabe eines Arrays vom Typ Receipt
	 */
	public static Receipt[] select(Date from, Date to, boolean reversedToo, boolean excludeCurrent, int maxRows)
	{
		Criteria criteria = new Criteria();
		
		Timestamp ts1 = new Timestamp(from.getTime());
		Timestamp ts2 = new Timestamp(to.getTime());
		criteria.addBetween("timestamp", ts1, ts2); //$NON-NLS-1$
		
		Criteria statusCriteria = new Criteria();
		statusCriteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		if (excludeCurrent) statusCriteria.addNotNull("settlement"); //$NON-NLS-1$
			
		if (reversedToo)
		{
			Criteria reversedCriteria = new Criteria();
			reversedCriteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_REVERSED)); //$NON-NLS-1$
			statusCriteria.addOrCriteria(reversedCriteria);
		}
		
		criteria.addAndCriteria(statusCriteria);
		
		QueryByCriteria query = new ReportQueryByCriteria(Receipt.class, criteria);
		query.setEndAtIndex(maxRows);
		Collection receipts = Database.getCurrent().getBroker().getCollectionByQuery(query);
		return (Receipt[]) receipts.toArray(new Receipt[0]);
	}
	
	// 10080
	/**
	 * 
	 * @param settlement
	 *            Abschlussnummer
	 * @return Receipt[] Rueckgabe eines Arrays vom Typ Receipt
	 */
	public static Iterator select(Long settlement, boolean reversedToo)
	{
		Criteria criteria = new Criteria();
		
		criteria.addEqualTo("settlement", settlement); //$NON-NLS-1$
		
		Criteria statusCriteria = new Criteria();
		statusCriteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		if (reversedToo)
		{
			Criteria reversedCriteria = new Criteria();
			reversedCriteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_REVERSED)); //$NON-NLS-1$
			statusCriteria.addOrCriteria(reversedCriteria);
		}
		
		criteria.addAndCriteria(statusCriteria);
		
		QueryByCriteria query = new ReportQueryByCriteria(Receipt.class, criteria);
		return Database.getCurrent().getBroker().getIteratorByQuery(query);
	}
	
	public static Long[] selectSettlements(Salespoint[] salespoints, Date from, Date to)
	{
		String[] fields = new String[]
		{ /* 0 */"settlement", //$NON-NLS-1$
		};
		int[] types = new int[]
		{ Types.BIGINT };
		
		Criteria criteria = new Criteria();
		
		if (salespoints != null)
		{
			if (salespoints.length > 0)
			{
				Criteria salespointCriteria = new Criteria();
				salespointCriteria.addEqualTo("salespointId", salespoints[0].getId()); //$NON-NLS-1$
				for (int i = 1; i < salespoints.length; i++)
				{
					Criteria orCriteria = new Criteria();
					orCriteria.addEqualTo("salespointId", salespoints[i].getId()); //$NON-NLS-1$
					salespointCriteria.addOrCriteria(orCriteria);
				}
				criteria.addAndCriteria(salespointCriteria);
			}
		}
		
		Timestamp ts1 = new Timestamp(from.getTime());
		Timestamp ts2 = new Timestamp(to.getTime());
		criteria.addBetween("timestamp", ts1, ts2); //$NON-NLS-1$
		
		criteria.addNotNull("settlement"); //$NON-NLS-1$
		criteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		
		ReportQueryByCriteria query = new ReportQueryByCriteria(Receipt.class, fields, criteria);
		query.addGroupBy(fields[0]);
		query.setJdbcTypes(types);
		
		Iterator iter = Database.getCurrent().getBroker().getReportQueryIteratorByQuery(query);
		if (!iter.hasNext())
		{
			return new Long[0];
		}
		else
		{
			ArrayList list = new ArrayList();
			while (iter.hasNext())
			{
				list.add(iter.next());
			}
			Long[] settlements = new Long[list.size()];
			iter = list.iterator();
			for (int i = 0; iter.hasNext(); i++)
			{
				Object[] row = (Object[]) iter.next();
				settlements[i] = (Long) row[0];
			}
			
			return settlements;
		}
	}
	
	/**
	 * 
	 * @param user
	 *            Der Benutzer, dessen Belege selektiert werden sollen
	 * @param state
	 *            Der Status der zu selektierenden Belege
	 * @return Receipt[] Rueckgabe eines Arrays vom Typ Receipt
	 */
	public static Iterator selectByTransactionId(Long transactionId)
	{
		String[] fields = new String[]
		{ /* 0 */"transactionId", //$NON-NLS-1$
						/* 1 */"salespointId", //$NON-NLS-1$
						/* 2 */"min(timestamp)", //$NON-NLS-1$
						/* 3 */"max(timestamp)" //$NON-NLS-1$
		};
		int[] types = new int[]
		{ Types.BIGINT, Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP };
		
		Criteria criteria = new Criteria();
		if (transactionId == null)
			criteria.addIsNull("transactionId");
		else
			criteria.addEqualTo("transactionId", transactionId); //$NON-NLS-1$
			
		ReportQueryByCriteria report = new ReportQueryByCriteria(Receipt.class, fields, criteria);
		report.addGroupBy(fields[0]);
		report.addGroupBy(fields[1]);
		report.setJdbcTypes(types);
		
		return Database.getCurrent().getBroker().getReportQueryIteratorByQuery(report);
	}
	
	public static int countFromTemporary(Salespoint salespoint, boolean reversedToo)
	{
		String[] fields = new String[]
		{ /* 0 */"count(*)" //$NON-NLS-1$
		};
		int[] types = new int[]
		{ Types.BIGINT };
		
		Criteria criteria = new Criteria();
		criteria.addEqualTo("salespointId", salespoint.getId()); //$NON-NLS-1$
		criteria.addIsNull("settlement"); //$NON-NLS-1$ 
		
		Criteria serializedCriteria = new Criteria();
		serializedCriteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		
		if (reversedToo)
		{
			Criteria reversedCriteria = new Criteria();
			reversedCriteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_REVERSED)); //$NON-NLS-1$
			serializedCriteria.addOrCriteria(reversedCriteria);
		}
		
		criteria.addAndCriteria(serializedCriteria);
		
		ReportQueryByCriteria report = new ReportQueryByCriteria(Receipt.class, fields, criteria);
		report.setJdbcTypes(types);
		
		Iterator iterator = Database.getCurrent().getBroker().getReportQueryIteratorByQuery(report);
		Long count = new Long(0l);
		if (iterator.hasNext())
		{
			Object[] objects = (Object[]) iterator.next();
			count = (Long) objects[0];
		}
		return count.intValue();
	}
	
	// public static Iterator selectFromTemporary(Salespoint salespoint, boolean
	// reversedToo)
	// {
	// String[] fields = new String[]
	//		{ /* 0 */"id", //$NON-NLS-1$
	//						/* 1 */"timestamp", //$NON-NLS-1$
	//						/* 2 */"number", //$NON-NLS-1$
	//						/* 3 */"transactionId", //$NON-NLS-1$
	//						/* 4 */"bookingId", //$NON-NLS-1$
	//						/* 5 */"salespointId", //$NON-NLS-1$
	//						/* 6 */"userId", //$NON-NLS-1$
	//						/* 7 */"foreignCurrencyId", //$NON-NLS-1$
	//						/* 8 */"status", //$NON-NLS-1$
	//						/* 9 */"settlement", //$NON-NLS-1$
	//						/* 10 */"amount", //$NON-NLS-1$
	//						/* 11 */"customerId", //$NON-NLS-1$
	//						/* 12 */"transferred", //$NON-NLS-1$
	//						/* 13 */"deleted" //$NON-NLS-1$
	// };
	// int[] types = new int[]
	// { Types.BIGINT, Types.TIMESTAMP, Types.VARCHAR, Types.BIGINT,
	// Types.BIGINT, Types.BIGINT, Types.BIGINT, Types.BIGINT, Types.INTEGER,
	// Types.BIGINT,
	// Types.DOUBLE, Types.VARCHAR, Types.BIT, Types.BIT };
	//
	// Criteria criteria = new Criteria();
	//		criteria.addEqualTo("salespointId", salespoint.getId()); //$NON-NLS-1$
	//		criteria.addEqualTo("settlement", Table.ZERO_VALUE); //$NON-NLS-1$ 
	//
	// Criteria serializedCriteria = new Criteria();
	//		serializedCriteria.addEqualTo("status", Receipt.RECEIPT_STATE_SERIALIZED); //$NON-NLS-1$
	//
	// if (reversedToo)
	// {
	// Criteria reversedCriteria = new Criteria();
	//			reversedCriteria.addEqualTo("status", Receipt.RECEIPT_STATE_REVERSED); //$NON-NLS-1$
	// serializedCriteria.addOrCriteria(reversedCriteria);
	// }
	//
	// criteria.addAndCriteria(serializedCriteria);
	//
	// ReportQueryByCriteria report = new ReportQueryByCriteria(Receipt.class,
	// fields, criteria);
	// report.setJdbcTypes(types);
	//
	// return
	// Database.getCurrent().getBroker().getReportQueryIteratorByQuery(report);
	// }
	
	/**
	 * 
	 * @param user
	 *            Der Benutzer, dessen Belege selektiert werden sollen
	 * @param state
	 *            Der Status der zu selektierenden Belege
	 * @return Receipt[] Rueckgabe eines Arrays vom Typ Receipt
	 */
	public static Vector selectByUserStateToList(User user, int state)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("status", new Integer(state)); //$NON-NLS-1$
		criteria.addEqualTo("userId", user.getId()); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Receipt.class, criteria);
		return (Vector) Table.select(query);
	}
	
	public static Receipt[] selectBookkeeppingTransferReceipts(Salespoint[] salespoints, Date from, Date to)
	{
		Criteria criteria = new Criteria();
		
		if (salespoints != null)
		{
			if (salespoints.length > 0)
			{
				Criteria salespointCriteria = new Criteria();
				salespointCriteria.addEqualTo("salespointId", salespoints[0].getId()); //$NON-NLS-1$
				// for (int i = 1; i < salespoints.length; i++) // 10441
				for (int i = 0; i < salespoints.length; i++) // 10441
				{
					Criteria orCriteria = new Criteria();
					orCriteria.addEqualTo("salespointId", salespoints[i].getId()); //$NON-NLS-1$
					salespointCriteria.addOrCriteria(orCriteria);
				}
				criteria.addAndCriteria(salespointCriteria);
			}
		}
		
		Timestamp ts1 = new Timestamp(from.getTime());
		Timestamp ts2 = new Timestamp(to.getTime());
		criteria.addBetween("timestamp", ts1, ts2); //$NON-NLS-1$
		
		criteria.addNotNull("settlement"); //$NON-NLS-1$
		criteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		// if (transactionId == null) // 10441
		criteria.addIsNull("transactionId");
		// else // 10441
		//			criteria.addEqualTo("transactionId", transactionId); //$NON-NLS-1$ 	// 10441
		
		Query query = QueryFactory.newQuery(Receipt.class, criteria);
		
		Collection rs = Table.select(query);
		return (Receipt[]) rs.toArray(new Receipt[0]);
	}
	
	public static Iterator selectReceiptStatistics(Salespoint[] salespoints, Date from, Date to, int groupBy)
	{
		String[] fields = new String[]
		{ /* 0 */"salespoint.name", //$NON-NLS-1$
						/* 1 */"year(timestamp)", //$NON-NLS-1$
						/* 2 */"count(id)", //$NON-NLS-1$
						/* 3 */"sum(amount)", //$NON-NLS-1$
						/* 4 */"salespoint.id" //$NON-NLS-1$
		};
		int[] types = new int[]
		{ Types.VARCHAR, Types.INTEGER, Types.BIGINT, Types.DOUBLE, Types.BIGINT };
		
		Criteria criteria = new Criteria();
		
		if (salespoints != null)
		{
			if (salespoints.length > 0)
			{
				Criteria salespointCriteria = new Criteria();
				salespointCriteria.addEqualTo("salespointId", salespoints[0].getId()); //$NON-NLS-1$
				for (int i = 1; i < salespoints.length; i++)
				{
					Criteria orCriteria = new Criteria();
					orCriteria.addEqualTo("salespointId", salespoints[i].getId()); //$NON-NLS-1$
					salespointCriteria.addOrCriteria(orCriteria);
				}
				criteria.addAndCriteria(salespointCriteria);
			}
		}
		
		Timestamp ts1 = new Timestamp(from.getTime());
		Timestamp ts2 = new Timestamp(to.getTime());
		criteria.addBetween("timestamp", ts1, ts2); //$NON-NLS-1$
		
		criteria.addNotNull("settlement"); //$NON-NLS-1$
		criteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		
		ReportQueryByCriteria report = new ReportQueryByCriteria(Receipt.class, fields, criteria);
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
		
		return Database.getCurrent().getBroker().getReportQueryIteratorByQuery(report);
	}
	
	public static Iterator selectDayHourStatisticsRange(Salespoint[] salespoints, Date from, Date to, int weekday)
	{
		String[] fields = new String[]
		{ /* 0 */"salespoint.name", //$NON-NLS-1$
						/* 1 */"hour(timestamp)", //$NON-NLS-1$
						/* 2 */"weekday(timestamp)", //$NON-NLS-1$
						/* 3 */"sum(amount)" //$NON-NLS-1$
		};
		int[] types = new int[]
		{ Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.DOUBLE };
		
		Criteria criteria = new Criteria();
		
		if (salespoints != null)
		{
			if (salespoints.length > 0)
			{
				Criteria salespointCriteria = new Criteria();
				salespointCriteria.addEqualTo("salespointId", salespoints[0].getId()); //$NON-NLS-1$
				for (int i = 1; i < salespoints.length; i++)
				{
					Criteria orCriteria = new Criteria();
					orCriteria.addEqualTo("salespointId", salespoints[i].getId()); //$NON-NLS-1$
					salespointCriteria.addOrCriteria(orCriteria);
				}
				criteria.addAndCriteria(salespointCriteria);
			}
		}
		
		Timestamp ts1 = new Timestamp(from.getTime());
		Timestamp ts2 = new Timestamp(to.getTime());
		criteria.addBetween("timestamp", ts1, ts2); //$NON-NLS-1$
		
		criteria.addNotNull("settlement"); //$NON-NLS-1$
		criteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		
		ReportQueryByCriteria report = new ReportQueryByCriteria(Receipt.class, fields, criteria);
		report.setJdbcTypes(types);
		
		report.addOrderBy(fields[0], true);
		report.addOrderBy(fields[2], true);
		report.addOrderBy(fields[1], true);
		
		report.addGroupBy(fields[0]);
		report.addGroupBy(fields[2]);
		report.addGroupBy(fields[1]);
		
		return Database.getCurrent().getBroker().getReportQueryIteratorByQuery(report);
	}
	
	public static Iterator selectReversed(Salespoint[] salespoints, Date from, Date to)
	{
		String[] fields = new String[]
		{ /* 0 */"number", //$NON-NLS-1$
						/* 1 */"timestamp", //$NON-NLS-1$
						/* 2 */"amount", //$NON-NLS-1$
		};
		
		int[] types = new int[]
		{
		/* 0 */Types.VARCHAR,
		/* 1 */Types.TIMESTAMP,
		/* 2 */Types.DOUBLE };
		
		Criteria criteria = new Criteria();
		
		if (salespoints != null)
		{
			if (salespoints.length > 0)
			{
				Criteria salespointCriteria = new Criteria();
				salespointCriteria.addEqualTo("salespointId", salespoints[0].getId()); //$NON-NLS-1$
				for (int i = 1; i < salespoints.length; i++)
				{
					Criteria orCriteria = new Criteria();
					orCriteria.addEqualTo("salespointId", salespoints[i].getId()); //$NON-NLS-1$
					salespointCriteria.addOrCriteria(orCriteria);
				}
				criteria.addAndCriteria(salespointCriteria);
			}
		}
		
		Timestamp ts1 = new Timestamp(from.getTime());
		Timestamp ts2 = new Timestamp(to.getTime());
		criteria.addBetween("timestamp", ts1, ts2); //$NON-NLS-1$
		
		criteria.addNotNull("settlement"); //$NON-NLS-1$
		criteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_REVERSED)); //$NON-NLS-1$
		
		ReportQueryByCriteria report = new ReportQueryByCriteria(Receipt.class, fields, criteria);
		report.setJdbcTypes(types);
		
		report.addOrderByAscending(fields[1]);
		
		return Database.getCurrent().getBroker().getReportQueryIteratorByQuery(report);
	}
	
	public static Iterator selectReversed(Salespoint salespoint)
	{
		String[] fields = new String[]
		{ /* 0 */"number", //$NON-NLS-1$
						/* 1 */"timestamp", //$NON-NLS-1$
						/* 2 */"amount", //$NON-NLS-1$
		};
		
		int[] types = new int[]
		{
		/* 0 */Types.VARCHAR,
		/* 1 */Types.TIMESTAMP,
		/* 2 */Types.DOUBLE };
		
		Criteria criteria = new Criteria();
		criteria.addEqualTo("salespointId", salespoint.getId()); //$NON-NLS-1$
		criteria.addIsNull("settlement");
		criteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_REVERSED)); //$NON-NLS-1$
		
		ReportQueryByCriteria report = new ReportQueryByCriteria(Receipt.class, fields, criteria);
		report.setJdbcTypes(types);
		
		report.addOrderByAscending(fields[1]);
		
		return Database.getCurrent().getBroker().getReportQueryIteratorByQuery(report);
	}
	
	public static Receipt[] selectCurrent(User user)
	{
		return Receipt.selectCurrent(user, false);
	}
	
	/**
	 * 
	 * Selektiert die Belege eines Benutzers, die noch nicht verbucht sind
	 * 
	 * @param user
	 *            Der Benutzer, dessen Belege selektiert werden sollen
	 * @param reversedToo
	 *            wenn true werden auch die stornierten Belege selektiert
	 * @return Receipt[] Die selektierten Belege werden als Array zurueckgegeben
	 */
	public static Receipt[] selectCurrent(User user, boolean reversedToo)
	{
		Criteria uCrit = new Criteria();
		uCrit.addEqualTo("userId", user.getId()); //$NON-NLS-1$
		uCrit.addIsNull("settlement"); //$NON-NLS-1$ 
		
		Criteria s1Crit = new Criteria();
		s1Crit.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		
		if (reversedToo)
		{
			Criteria s2Crit = new Criteria();
			s2Crit.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_REVERSED)); //$NON-NLS-1$
			s1Crit.addOrCriteria(s2Crit);
		}
		
		uCrit.addAndCriteria(s1Crit);
		//		uCrit.addOrderBy("timestamp", false); //$NON-NLS-1$
		
		QueryByCriteria query = QueryFactory.newQuery(Receipt.class, uCrit);
		query.addOrderBy("timestamp", false); //$NON-NLS-1$
		Collection receipts = Table.select(query);
		return (Receipt[]) receipts.toArray(new Receipt[0]);
	}
	
	/**
	 * 
	 * Selektiert die Belege eines Benutzers, die noch nicht verbucht sind
	 * 
	 * @param user
	 *            Der Benutzer, dessen Belege selektiert werden sollen
	 * @param reversedToo
	 *            wenn true werden auch die stornierten Belege selektiert
	 * @return Receipt[] Die selektierten Belege werden als Array zurueckgegeben
	 */
	public static Receipt[] selectCurrent(Salespoint salespoint, User user, boolean reversedToo)
	{
		Criteria uCrit = new Criteria();
		uCrit.addEqualTo("salespointId", salespoint.getId()); //$NON-NLS-1$
		uCrit.addEqualTo("userId", user.getId()); //$NON-NLS-1$
		
		Criteria s1Crit = new Criteria();
		s1Crit.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		s1Crit.addIsNull("settlement"); //$NON-NLS-1$ 
		
		if (reversedToo)
		{
			Criteria s2Crit = new Criteria();
			s2Crit.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_REVERSED)); //$NON-NLS-1$
			s1Crit.addOrCriteria(s2Crit);
		}
		
		uCrit.addAndCriteria(s1Crit);
		//		uCrit.addOrderBy("timestamp", false); //$NON-NLS-1$
		
		QueryByCriteria query = QueryFactory.newQuery(Receipt.class, uCrit);
		query.addOrderBy("timestamp", false); //$NON-NLS-1$
		Collection receipts = Table.select(query);
		return (Receipt[]) receipts.toArray(new Receipt[0]);
	}
	
	/**
	 * 
	 * Selektiert die Belege einer Kasse, die noch nicht verbucht sind
	 * 
	 * @param user
	 *            Der Benutzer, dessen Belege selektiert werden sollen
	 * @param reversedToo
	 *            wenn true werden auch die stornierten Belege selektiert
	 * @return Receipt[] Die selektierten Belege werden als Array zurueckgegeben
	 */
	public static Receipt[] selectCurrent(Salespoint salespoint, boolean reversedToo)
	{
		QueryByCriteria query = QueryFactory.newQuery(Receipt.class,
						Receipt.createCurrentCriteria(salespoint, reversedToo));
		query.addOrderBy("timestamp", false); //$NON-NLS-1$
		Collection coll = Table.select(query);
		return (Receipt[]) coll.toArray(new Receipt[0]);
	}
	
	private static Criteria createCurrentCriteria(Salespoint salespoint, boolean reversedToo)
	{
		Criteria uCrit = new Criteria();
		uCrit.addEqualTo("salespointId", salespoint.getId()); //$NON-NLS-1$
		uCrit.addIsNull("settlement"); //$NON-NLS-1$ 
		
		Criteria s1Crit = new Criteria();
		s1Crit.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		
		if (reversedToo)
		{
			Criteria s2Crit = new Criteria();
			s2Crit.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_REVERSED)); //$NON-NLS-1$
			s1Crit.addOrCriteria(s2Crit);
		}
		
		uCrit.addAndCriteria(s1Crit);
		
		return uCrit;
	}
	
	public static int countCurrent(Salespoint salespoint, boolean reversedToo)
	{
		QueryByCriteria query = QueryFactory.newQuery(Receipt.class,
						Receipt.createCurrentCriteria(salespoint, reversedToo));
		return Database.getCurrent().getBroker().getCount(query);
	}
	
	private static Criteria createReceiptStatisticsCriteria(Long salespointId, Date from, Date to)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("salespointId", salespointId); //$NON-NLS-1$
		criteria.addBetween("timestamp", from, to);
		criteria.addNotNull("settlement"); //$NON-NLS-1$ 
		criteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$ 
		return criteria;
	}
	
	public static int countReceiptStatistics(Long salespointId, Date from, Date to)
	{
		QueryByCriteria query = QueryFactory.newQuery(Receipt.class,
						Receipt.createReceiptStatisticsCriteria(salespointId, from, to));
		return Database.getCurrent().getBroker().getCount(query);
	}
	
	/**
	 * 
	 * Selektiert die Belege eines Benutzers, die noch nicht verbucht sind
	 * 
	 * @param user
	 *            Der Benutzer, dessen Belege selektiert werden sollen
	 * @param reversedToo
	 *            wenn true werden auch die stornierten Belege selektiert
	 * @return Receipt[] Die selektierten Belege werden als Array zurueckgegeben
	 */
	public static Iterator selectCurrentToIterator(Salespoint salespoint, boolean reversedToo)
	{
		Criteria uCrit = Receipt.createCurrentCriteria(salespoint, reversedToo);
		QueryByCriteria query = QueryFactory.newQuery(Receipt.class, uCrit);
		query.addOrderBy("timestamp", false); //$NON-NLS-1$
		return Table.selectToIterator(query);
	}
	
	public static int selectCurrent(Salespoint salespoint)
	{
		String[] fields = new String[]
		{
		/* 0 */"count(*)" };
		int[] types = new int[]
		{ Types.BIGINT };
		
		Criteria criteria = Receipt.createCurrentCriteria(salespoint, false);
		ReportQueryByCriteria report = new ReportQueryByCriteria(Receipt.class, fields, criteria);
		report.setJdbcTypes(types);
		
		Iterator iter = Database.getCurrent().getBroker().getReportQueryIteratorByQuery(report);
		int count = 0;
		while (iter.hasNext())
		{
			Object[] object = (Object[]) iter.next();
			if (object[0] instanceof Integer)
			{
				Integer m = (Integer) object[0];
				count += m.intValue();
			}
			else if (object[0] instanceof Long)
			{
				Long m = (Long) object[0];
				count += m.intValue();
			}
		}
		
		return count;
	}
	
	public static int selectCurrentOlderThanToday(Salespoint salespoint, Date date)
	{
		String[] fields = new String[]
		{
		/* 0 */"count(*)" };
		int[] types = new int[]
		{ Types.BIGINT };
		
		Criteria criteria = new Criteria();
		criteria.addEqualTo("salespointId", salespoint.getId()); //$NON-NLS-1$
		criteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		criteria.addIsNull("settlement"); //$NON-NLS-1$ 
		criteria.addLessThan("timestamp", date);
		
		ReportQueryByCriteria report = new ReportQueryByCriteria(Receipt.class, fields, criteria);
		report.setJdbcTypes(types);
		
		Iterator iter = Database.getCurrent().getBroker().getReportQueryIteratorByQuery(report);
		int count = 0;
		while (iter.hasNext())
		{
			Object[] object = (Object[]) iter.next();
			if (object[0] instanceof Long)
			{
				Long m = (Long) object[0];
				count += m.intValue();
			}
			else if (object[0] instanceof Integer)
			{
				Integer m = (Integer) object[0];
				count += m.intValue();
			}
		}
		return count;
	}
	
	/**
	 * 
	 * Selektiert die stornierten Belege eines Benutzers, die noch nicht
	 * verbucht sind
	 * 
	 * @param user
	 *            Der Benutzer, dessen stornierte Belege selektiert werden
	 *            sollen
	 * @param reversedToo
	 *            wenn true werden auch die stornierten Belege selektiert
	 * @return Receipt[] Die selektierten Belege werden als Array zurueckgegeben
	 */
	public static Receipt[] selectCurrentReversed(User user)
	{
		Criteria uCrit = new Criteria();
		uCrit.addEqualTo("userId", user.getId()); //$NON-NLS-1$
		
		Criteria s1Crit = new Criteria();
		s1Crit.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_REVERSED)); //$NON-NLS-1$
		s1Crit.addIsNull("settlement"); //$NON-NLS-1$ 
		
		uCrit.addAndCriteria(s1Crit);
		
		//		uCrit.addOrderBy("timestamp", true); //$NON-NLS-1$
		
		QueryByCriteria query = QueryFactory.newQuery(Receipt.class, uCrit);
		query.addOrderBy("timestamp", true); //$NON-NLS-1$
		Collection receipts = Table.select(query);
		return (Receipt[]) receipts.toArray(new Receipt[0]);
	}
	
	/**
	 * 
	 * Selektiert die stornierten Belege eines Benutzers, die noch nicht
	 * verbucht sind
	 * 
	 * @param user
	 *            Der Benutzer, dessen stornierte Belege selektiert werden
	 *            sollen
	 * @param reversedToo
	 *            wenn true werden auch die stornierten Belege selektiert
	 * @return Receipt[] Die selektierten Belege werden als Array zurueckgegeben
	 */
	public static Iterator selectCurrentReversed(Salespoint salespoint)
	{
		Criteria uCrit = new Criteria();
		uCrit.addEqualTo("salespointId", salespoint.getId()); //$NON-NLS-1$
		
		Criteria s1Crit = new Criteria();
		s1Crit.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_REVERSED)); //$NON-NLS-1$
		s1Crit.addIsNull("settlement"); //$NON-NLS-1$ 
		
		uCrit.addAndCriteria(s1Crit);
		
		//		uCrit.addOrderBy("timestamp", true); //$NON-NLS-1$
		
		QueryByCriteria query = QueryFactory.newQuery(Receipt.class, uCrit);
		query.addOrderBy("timestamp", true); //$NON-NLS-1$
		return Table.selectToIterator(query);
	}
	
	/**
	 * 
	 * Selektiert die stornierten Belege eines Benutzers, die noch nicht
	 * verbucht sind
	 * 
	 * @param user
	 *            Der Benutzer, dessen stornierte Belege selektiert werden
	 *            sollen
	 * @param reversedToo
	 *            wenn true werden auch die stornierten Belege selektiert
	 * @return Receipt[] Die selektierten Belege werden als Array zurueckgegeben
	 */
	public static Double getSales(Salespoint salespoint, User user)
	{
		Double amount = new Double(0d);
		Criteria criteria = new Criteria();
		criteria.addEqualTo("salespointId", salespoint.getId()); //$NON-NLS-1$
		criteria.addEqualTo("userId", user.getId()); //$NON-NLS-1$
		criteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		criteria.addIsNull("settlement"); //$NON-NLS-1$ 
		
		ReportQueryByCriteria q = QueryFactory.newReportQuery(Receipt.class, criteria);
		
		//		q.setAttributes(new String[] { "sum(amount)" }); //$NON-NLS-1$
		q.addGroupBy("userId"); //$NON-NLS-1$
		
		Iterator iter = Database.getCurrent().getBroker().getReportQueryIteratorByQuery(q);
		if (iter.hasNext())
		{
			Object[] obj = (Object[]) iter.next();
			amount = (Double) obj[0];
		}
		return amount;
	}
	
	/**
	 * 
	 * Selektiert die stornierten Belege eines Benutzers, die noch nicht
	 * verbucht sind
	 * 
	 * @param Salespoint
	 *            Die aktuelle Kassenstation Id
	 * @param User
	 *            Der Benutzer, dessen Belege selektiert werden sollen
	 * @return Double Der aktuell errechnete Umsatz
	 */
	public static double getSalesSum(Salespoint salespoint, User user)
	{
		double amount = 0d;
		Criteria criteria = new Criteria();
		criteria.addEqualTo("salespointId", salespoint.getId()); //$NON-NLS-1$
		criteria.addEqualTo("userId", user.getId()); //$NON-NLS-1$
		criteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		criteria.addIsNull("settlement"); //$NON-NLS-1$ 
		
		QueryByCriteria q = QueryFactory.newQuery(Receipt.class, criteria);
		Iterator iter = Database.getCurrent().getBroker().getIteratorByQuery(q);
		while (iter.hasNext())
		{
			Receipt receipt = (Receipt) iter.next();
			Position[] positions = receipt.getPositionsAsArray();
			for (int i = 0; i < positions.length; i++)
			{
				if (positions[i].getProductGroup().type == ProductGroup.TYPE_INCOME)
				{
					amount = amount + positions[i].getAmount();
				}
			}
		}
		return amount;
	}
	
	/**
	 * 
	 * Selektiert die Belege der aktuellen Kassenstation, die noch nicht
	 * verbucht sind, und errechnet daraus den aktuellen Umsatz der
	 * Kassenstation.
	 * 
	 * @param Salespoint
	 *            Die aktuelle Kassenstation, für die der Umsatz errechnet
	 *            werden soll.
	 * @return Double Umsatz der aktuellen Kasse
	 */
	public static double getSalesSum(Salespoint salespoint)
	{
		double amount = 0d;
		Criteria criteria = new Criteria();
		criteria.addEqualTo("salespointId", salespoint.getId()); //$NON-NLS-1$
		criteria.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_SERIALIZED)); //$NON-NLS-1$
		criteria.addIsNull("settlement"); //$NON-NLS-1$ 
		
		QueryByCriteria q = QueryFactory.newQuery(Receipt.class, criteria);
		Iterator iter = Database.getCurrent().getBroker().getIteratorByQuery(q);
		while (iter.hasNext())
		{
			Receipt receipt = (Receipt) iter.next();
			Position[] positions = receipt.getPositionsAsArray();
			for (int i = 0; i < positions.length; i++)
			{
				if (positions[i].getProductGroup().type == ProductGroup.TYPE_INCOME)
				{
					amount = amount + positions[i].getAmount();
				}
			}
		}
		return amount;
	}
	
	/**
	 * 
	 * Selektiert die stornierten Belege eines Benutzers, die noch nicht
	 * verbucht sind
	 * 
	 * @param user
	 *            Der Benutzer, dessen stornierte Belege selektiert werden
	 *            sollen
	 * @return Receipt[] Die selektierten Belege
	 */
	public static Receipt[] selectReversed(User user, boolean settled)
	{
		Criteria uCrit = new Criteria();
		uCrit.addEqualTo("userId", user.getId()); //$NON-NLS-1$
		
		Criteria s1Crit = new Criteria();
		s1Crit.addEqualTo("status", new Integer(Receipt.RECEIPT_STATE_REVERSED)); //$NON-NLS-1$
		if (settled)
		{
			s1Crit.addNotNull("settlement"); //$NON-NLS-1$ 
		}
		else
		{
			s1Crit.addIsNull("settlement"); //$NON-NLS-1$ 
		}
		uCrit.addAndCriteria(s1Crit);
		//		uCrit.addOrderBy("timestamp", true); //$NON-NLS-1$
		
		QueryByCriteria query = QueryFactory.newQuery(Receipt.class, uCrit);
		query.addOrderBy("timestamp", true); //$NON-NLS-1$
		Collection receipts = Table.select(query);
		return (Receipt[]) receipts.toArray(new Receipt[0]);
	}
	
	public static Receipt[] selectAll()
	{
		Criteria criteria = new Criteria();
		Query query = QueryFactory.newQuery(Receipt.class, criteria);
		Collection receipts = Table.select(query);
		return (Receipt[]) receipts.toArray(new Receipt[0]);
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
		attributes.addAttribute("", "timestamp", "", "", new Long(this.timestamp.getTime()).toString());
		attributes.addAttribute("", "number", "", "", this.number);
		attributes.addAttribute("", "transaction-id", "", "",
						this.transactionId == null ? "0" : this.transactionId.toString());
		attributes.addAttribute("", "booking-id", "", "", this.bookingId == null ? "0" : this.bookingId.toString());
		attributes.addAttribute("", "salespoint-id", "", "", this.salespoint.exportId);
		attributes.addAttribute("", "user-id", "", "", this.user.username);
		attributes.addAttribute("", "foreign-currency-id", "", "", this.foreignCurrencyId.toString());
		attributes.addAttribute("", "status", "", "", Integer.toString(this.status));
		attributes.addAttribute("", "settlement", "", "", this.settlement == null ? "0" : this.settlement.toString());
		attributes.addAttribute("", "amount", "", "", Double.toString(this.amount));
		attributes.addAttribute("", "customer-id", "", "", this.customerId);
		attributes.addAttribute("", "transferred", "", "", Boolean.toString(this.transferred));
		return attributes;
	}
	
	/*
	 * Export-, Importbereich
	 * 
	 * @author ceugster
	 */
	public Element getJDOMRecordAttributes(boolean setIds, boolean useExportIds)
	{
		Element receipt = new Element("receipt"); //$NON-NLS-1$
		receipt.setAttribute("id", setIds ? this.getId().toString() : "0"); //$NON-NLS-1$ //$NON-NLS-2$
		receipt.setAttribute("timestamp", new Long(this.timestamp.getTime()).toString()); //$NON-NLS-1$
		receipt.setAttribute("number", this.number); //$NON-NLS-1$
		receipt.setAttribute("transaction-id", this.transactionId == null ? "0" : this.transactionId.toString()); //$NON-NLS-1$
		receipt.setAttribute("booking-id", this.bookingId == null ? "0" : this.bookingId.toString()); //$NON-NLS-1$
		if (useExportIds)
		{
			receipt.setAttribute("salespoint-id", this.salespoint.exportId); //$NON-NLS-1$
			receipt.setAttribute("user-id", this.user.username); //$NON-NLS-1$
		}
		else
		{
			receipt.setAttribute("salespoint-id", this.salespoint.getId().toString()); //$NON-NLS-1$
			receipt.setAttribute("user-id", this.user.getId().toString()); //$NON-NLS-1$
		}
		receipt.setAttribute("foreign-currency-id", this.foreignCurrencyId.toString()); //$NON-NLS-1$
		receipt.setAttribute("status", Integer.toString(this.status)); //$NON-NLS-1$
		receipt.setAttribute("settlement", this.settlement == null ? "0" : this.settlement.toString()); //$NON-NLS-1$
		receipt.setAttribute("amount", Double.toString(this.amount)); //$NON-NLS-1$
		receipt.setAttribute("customer-id", this.customerId); //$NON-NLS-1$
		receipt.setAttribute("transferred", Boolean.toString(this.transferred)); //$NON-NLS-1$
		return receipt;
	}
	
	public void setRecordAttributes(Element receipt, boolean setId, boolean useExportIds) throws InvalidValueException
	{
		super.getData(setId, receipt);
		this.timestamp = XMLLoader.getTimestampFromLong(receipt.getAttributeValue("timestamp"));
		this.number = receipt.getAttributeValue("number"); //$NON-NLS-1$
		long value = XMLLoader.getLong(receipt.getAttributeValue("transaction-id")); //$NON-NLS-1$
		this.transactionId = value == 0l ? null : new Long(value);
		value = XMLLoader.getLong(receipt.getAttributeValue("booking-id")); //$NON-NLS-1$
		this.bookingId = value == 0l ? null : new Long(value);
		if (useExportIds)
		{
			this.setSalespoint(Salespoint.selectByExportId(receipt.getAttributeValue("salespoint-id"))); //$NON-NLS-1$
			this.setUser(User.selectByUsername(receipt.getAttributeValue("user-id"), true)); //$NON-NLS-1$
		}
		else
		{
			this.setSalespoint(Salespoint.selectById(new Long(receipt.getAttributeValue("salespoint-id")))); //$NON-NLS-1$
			this.setUser(User.selectById(new Long(receipt.getAttributeValue("user-id")))); //$NON-NLS-1$
		}
		this.setDefaultCurrency(ForeignCurrency.selectById(new Long(XMLLoader.getLong(receipt
						.getAttributeValue("foreign-currency-id"))))); //$NON-NLS-1$
		this.status = XMLLoader.getInt(receipt.getAttributeValue("status")); //$NON-NLS-1$
		value = XMLLoader.getLong(receipt.getAttributeValue("settlement")); //$NON-NLS-1$
		this.settlement = value == 0l ? null : new Long(value);
		this.amount = XMLLoader.getDouble(receipt.getAttributeValue("amount")); //$NON-NLS-1$
		this.customerId = receipt.getAttributeValue("customer-id"); //$NON-NLS-1$
		this.transferred = XMLLoader.getBoolean(receipt.getAttributeValue("transferred")); //$NON-NLS-1$
		if (this.getSalespointId() == null || this.getSalespointId().equals(Table.ZERO_VALUE))
			throw new InvalidValueException("Kasse mit Bezeichnung \"" + receipt.getAttributeValue("salespoint-id")
							+ "\" ungültig.");
		if (this.getDefaultCurrencyId() == null || this.getDefaultCurrencyId().equals(Table.ZERO_VALUE))
			throw new InvalidValueException("Fremdwährung mit der Id \""
							+ receipt.getAttributeValue("foreign-currency-id") + "\" ungültig.");
		if (this.getUserId() == null || this.getUserId().equals(Table.ZERO_VALUE))
			throw new InvalidValueException("Benutzer mit dem Benutzernamen \"" + receipt.getAttributeValue("user-id")
							+ "\" ungültig.");
	}
	
	public static Receipt getReceipt(Element element, boolean setId, boolean useExportId)
	{
		Salespoint salespoint = null;
		User user = null;
		if (useExportId)
		{
			salespoint = Salespoint.selectByExportId(element.getAttributeValue("salespoint-id"));
			user = User.getByUsername(element.getAttributeValue("user-id"));
		}
		else
		{
			salespoint = Salespoint.getById(new Long(element.getAttributeValue("salespoint-id")));
			user = User.getById(new Long(element.getAttributeValue("user-id")));
		}
		Customer customer = new Customer(element.getAttributeValue("customer-id"), "", false, new Double(0d));
		ForeignCurrency currency = ForeignCurrency.getById(new Long(element.getAttributeValue("foreign-currency-id")));
		
		Receipt receipt = Receipt.getEmptyReceipt();
		receipt.setId(setId ? new Long(element.getAttributeValue("id")) : new Long(0l));
		receipt.timestamp = new Timestamp(new Long(element.getAttributeValue("timestamp")).longValue());
		receipt.setNumber(element.getAttributeValue("number"));
		long value = Long.parseLong(element.getAttributeValue("transaction-id"));
		receipt.setTransactionId(value == 0 ? null : new Long(value));
		value = Long.parseLong(element.getAttributeValue("booking-id"));
		receipt.setBookingId(value == 0 ? null : new Long(value));
		receipt.setSalespoint(salespoint);
		receipt.setUser(user);
		receipt.setDefaultCurrency(currency);
		receipt.status = Integer.parseInt(element.getAttributeValue("status"));
		value = Long.parseLong(element.getAttributeValue("settlement"));
		receipt.setSettlement(value == 0 ? null : new Long(value));
		receipt.amount = Double.parseDouble(element.getAttributeValue("amount"));
		receipt.setCustomer(customer);
		receipt.setTransferred(Boolean.getBoolean(element.getAttributeValue("transferred")));
		return receipt;
	}
	
	public Receipt clone(boolean cloneId)
	{
		Receipt copy = Receipt.getEmptyReceipt();
		copy.setId(cloneId ? this.getId() : null);
		copy.timestamp = this.timestamp;
		copy.setNumber(this.getNumber());
		copy.setTransactionId(this.getTransactionId());
		copy.setBookingId(this.getBookingId());
		copy.setSalespoint(this.getSalespoint(this.getSalespointId()));
		copy.setUser(Receipt.getUser(this.getUserId()));
		copy.setDefaultCurrency(Receipt.getForeignCurrency(this.getDefaultCurrencyId()));
		copy.status = this.status;
		copy.setSettlement(this.getSettlement());
		copy.amount = this.amount;
		copy.setCustomer(this.getCustomer());
		copy.setTransferred(true);
		
		Position[] positions = this.getPositionsAsArray();
		for (int i = 0; i < positions.length; i++)
		{
			copy.addPosition(positions[i].clone(copy, cloneId));
		}
		Payment[] payments = this.getPaymentsAsArray();
		for (int i = 0; i < payments.length; i++)
		{
			copy.addPayment(payments[i].clone(copy, cloneId));
		}
		return copy;
	}
	
	public static Element getReceipt(String timestamp, String[] fields)
	{
		Element receipt = new Element("receipt"); //$NON-NLS-1$
		receipt.setAttribute("id", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		receipt.setAttribute("timestamp", timestamp); //$NON-NLS-1$
		receipt.setAttribute("number", fields[15]); //$NON-NLS-1$
		receipt.setAttribute("transaction-id", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		receipt.setAttribute("booking-id", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		receipt.setAttribute("salespoint-id", fields[1]); //$NON-NLS-1$
		receipt.setAttribute("user-id", User.getCurrentUser().getId().toString()); //$NON-NLS-1$
		receipt.setAttribute("foreign-currency-id", ForeignCurrency.getDefaultCurrency().getId().toString()); //$NON-NLS-1$
		receipt.setAttribute(
						"status", fields[12].equals("2") ? Integer.toString(Receipt.RECEIPT_STATE_SERIALIZED) : Integer.toString(Receipt.RECEIPT_STATE_REVERSED)); //$NON-NLS-1$ //$NON-NLS-2$
		receipt.setAttribute("settlement", fields[4]); //$NON-NLS-1$ //TODO
		receipt.setAttribute("amount", fields[16]); //$NON-NLS-1$
		receipt.setAttribute("customer_id", ""); //$NON-NLS-1$
		receipt.setAttribute("transferred", "false"); //$NON-NLS-1$
		
		return receipt;
	}
	
	private static User getUser(Long id)
	{
		User user = null;
		if (id == null)
		{
			user = User.getDefaultUser();
		}
		else
		{
			user = User.getById(id);
			if (user == null || user.getId().equals(null))
			{
				user = User.getDefaultUser();
			}
		}
		return user;
	}
	
	private Salespoint getSalespoint(Long id)
	{
		Salespoint salespoint = null;
		if (id == null)
		{
			salespoint = Salespoint.getCurrent();
		}
		else
		{
			salespoint = Salespoint.getById(id);
			if (salespoint == null || salespoint.getId().equals(null))
			{
				salespoint = Salespoint.getCurrent();
			}
		}
		return salespoint;
	}
	
	private static ForeignCurrency getForeignCurrency(Long id)
	{
		ForeignCurrency foreignCurrency = null;
		if (id == null)
		{
			foreignCurrency = ForeignCurrency.getDefaultCurrency();
		}
		else
		{
			foreignCurrency = ForeignCurrency.getById(id);
			if (foreignCurrency == null || foreignCurrency.getId().equals(null))
			{
				foreignCurrency = ForeignCurrency.getDefaultCurrency();
			}
		}
		return foreignCurrency;
	}
	
	// private static Customer getCustomer(String id)
	// {
	// Customer customer = new Customer();
	// customer.setId(id);
	// return customer;
	// }
	
	/**
	 * 
	 * @return customerId
	 */
	public String getCustomerId()
	{
		return this.customerId;
	}
	
	/**
	 * @return Customer
	 */
	public Customer getCustomer()
	{
		this.hasCustomer();
		return this.customer;
	}
	
	/**
	 *
	 */
	public void setCustomer()
	{
		this.customer = new Customer();
		this.customerId = this.customer.getId();
		this.setUpdateCustomerAccount(Table.BOOLEAN_DEFAULT_FALSE);
		// redrawCustomer();
	}
	
	/**
	 * @param customer
	 */
	public void setCustomer(Customer customer)
	{
		if (customer == null)
			this.setCustomer();
		else
		{
			this.customer = customer;
			this.customerId = customer.getId();
			this.setUpdateCustomerAccount(Table.BOOLEAN_DEFAULT_TRUE);
		}
	}
	
	private void setUpdateCustomerAccount(boolean update)
	{
		Position[] positions = this.getPositionsAsArray();
		for (int i = 0; i < positions.length; i++)
		{
			positions[i].updateCustomerAccount = update;
		}
	}
	
	public boolean hasCustomer()
	{
		boolean hasCustomer = true;
		if (!this.customerId.equals(this.customer.getId()))
		{
			if (this.customerId.length() == 0)
			{
				this.customerId = this.customer.getId();
			}
			else
			{
				this.setCustomer(Customer.getCustomer(this.customerId));
			}
		}
		
		try
		{
			int id = new Integer(this.customerId).intValue();
			hasCustomer = id != 0;
		}
		catch (NumberFormatException e)
		{
			hasCustomer = false;
		}
		finally
		{
		}
		
		return hasCustomer;
	}
	
	// public void redrawCustomer() {
	// if (customer.getId().equals("")) {
	// notifyCustomerChangeListeners();
	// }
	// else {
	// notifyCustomerChangeListeners(customer);
	// }
	// }
	
	public Element buildJDOMElement(boolean setIds)
	{
		Element e = this.getJDOMRecordAttributes(setIds, true);
		Position[] po = this.getPositionsAsArray();
		for (int i = 0; i < po.length; i++)
		{
			Element pe = po[i].getJDOMRecordAttributes(setIds, true);
			e.addContent(pe);
		}
		Payment[] pa = this.getPaymentsAsArray();
		for (int i = 0; i < pa.length; i++)
		{
			Element pe = pa[i].getJDOMRecordAttributes(setIds, true);
			e.addContent(pe);
		}
		return e;
	}
	
	// public static void addCustomerChangeListener(CustomerChangeListener
	// listener) {
	// if (!customerChangeListeners.contains(listener))
	// customerChangeListeners.add(listener);
	// }
	//
	// public static void removeCustomerChangeListener(CustomerChangeListener
	// listener) {
	// customerChangeListeners.remove(listener);
	// }
	//
	// public static void notifyCustomerChangeListeners(Customer customer) {
	// CustomerChangeEvent ev = new CustomerChangeEvent(customer);
	// Iterator iterator = customerChangeListeners.iterator();
	// while (iterator.hasNext()) {
	// ((CustomerChangeListener)iterator.next()).customerChanged(ev);
	// }
	// }
	//
	// public static void notifyCustomerChangeListeners() {
	// CustomerChangeEvent ev = new CustomerChangeEvent();
	// Iterator iterator = customerChangeListeners.iterator();
	// while (iterator.hasNext()) {
	// ((CustomerChangeListener)iterator.next()).customerChanged(ev);
	// }
	// }
	
	public void bookGalileo()
	{
		this.bookGalileo(false);
	}
	
	public void bookGalileo(boolean ignoreIsBooked)
	{
		Logger.getLogger("colibri").info("Entered: Receipt.bookGalileo()");
		boolean isOpen = false;
		/*
		 * Die Positionen, die aus Galileo geholt wurden werden in Galileo
		 * verbucht. Wenn Galileo ohne Warenbewirtschaftung ist, geschieht hier
		 * nichts...
		 */
		Logger.getLogger("colibri").info("Auf aktuelle Datenbank testen (muss Standard sein)...");
		
		if (Database.getCurrent().equals(Database.getStandard()))
		{
			Logger.getLogger("colibri").info("Prüfen, ob Galileo angeschlossen ist...");
			
			if (ProductServer.isUsed() && ProductServer.getInstance() != null)
			{
				Logger.getLogger("colibri").info("Galileo läuft...");
				
				if (ProductServer.getInstance() instanceof GalileoServer)
				{
					if (ProductServer.getInstance().isActive())
					{
						Logger.getLogger("colibri").info("...und ist aktiv...");
						
						boolean galileoBooked = true;
						Position[] positions = this.getPositionsAsArray();
						Logger.getLogger("colibri").info("Positionen des Belegs extrahieren...");
						
						try
						{
							isOpen = ProductServer.getInstance().isOpen();
							if (!isOpen)
							{
								ProductServer.getInstance().open();
							}
							Logger.getLogger("colibri").info(
											"Verbindung zu Galileo öffnen und die Positionen verbuchen...");
							
							for (int i = 0; i < positions.length; i++)
							{
								Logger.getLogger("colibri").info("Prüfen, ob Position zu verbuchen ist...");
								
								if (positions[i].galileoBook)
								{
									boolean book = true;
									if (!ignoreIsBooked)
									{
										// 10427
										// book = !positions[i].galileoBooked ||
										// positions[i].galileoBooked &&
										// this.status ==
										// Receipt.RECEIPT_STATE_REVERSED;
										//
										book = !positions[i].galileoBooked
														&& this.status == Receipt.RECEIPT_STATE_SERIALIZED
														|| positions[i].galileoBooked
														&& this.status == Receipt.RECEIPT_STATE_REVERSED;
										// 10427
									}
									if (book)
									{
										// 10428
										// positions[i].galileoBooked = false;
										// 10428
										
										Logger.getLogger("colibri").info("Start: Verbuchung Position in Galileo...");
										
										galileoBooked = ProductServer.getInstance().update(this.status, positions[i]);
										
										Logger.getLogger("colibri").info("Ende: Verbuchung Position in Galileo...");
										
										positions[i].galileoBooked = this.status == Receipt.RECEIPT_STATE_SERIALIZED ? galileoBooked
														: false;
										
										this.setPosition(i, positions[i]);
										Logger.getLogger("colibri").info(
														"Verbuchung war "
																		+ (positions[i].galileoBooked ? "" : "nicht ")
																		+ "erfolgreich!");
										
									}
								}
							}
							if (!Config.getInstance().getProductServerHold())
							{
								if (!isOpen)
								{
									ProductServer.getInstance().close();
								}
							}
							/*
							 * Wenn die Buchungen in Galileo nicht geklappt
							 * haben, muss die Datenbank auf lokal gewechselt
							 * werden, damit die Positionen beim nächsten
							 * Programmstart verbucht werden können.
							 */
						}
						catch (ComException ce)
						{
							ProductServer.getInstance().catchComException(ce);
						}
						finally
						{
						}
						return;
					}
				}
			}
		}
	}
	
	// private static ArrayList customerChangeListeners = new ArrayList();
	
	private static final String PRE_POSITION_CONSTANT = "1"; // 10193
	private static final String PRE_POSITION_CONSTANT_NEW = "N"; // 10193
	private static final String PRE_POSITION_CONSTANT_PARKED = "P"; // 10193
	private static final String PRE_POSITION_CONSTANT_REVERSED = "S"; // 10193
	private static final String PRE_POSITION_CONSTANT_FAILOVER = "F"; // 10193
	private static final NumberFormat salespointNumberFormat = new DecimalFormat("00000"); // 10193
	private static final NumberFormat receiptNumberFormat = new DecimalFormat("0000000000"); // 10193
	
	public static final int RECEIPT_STATE_NO_CHANGE = 0;
	public static final int RECEIPT_STATE_NEW = 1;
	public static final int RECEIPT_STATE_PARKED = 2;
	public static final int RECEIPT_STATE_REVERSED = 3;
	public static final int RECEIPT_STATE_SERIALIZED = 4;
	
	public static final String[] STATE_TEXT = new String[]
	{ Messages.getString("Receipt.Keine__u00C4nderung_24"), //$NON-NLS-1$
					Messages.getString("Receipt.Neu_25"), //$NON-NLS-1$
					Messages.getString("Receipt.Parkiert_26"), //$NON-NLS-1$
					Messages.getString("Receipt.Storniert_27"), //$NON-NLS-1$
					Messages.getString("Receipt.G_u00FCltig_28") }; //$NON-NLS-1$
}