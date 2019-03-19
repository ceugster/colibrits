/*
 * Created on 15.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.core;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import ch.eugster.pos.db.Payment;
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.Receipt;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BookkeepingTransferReceipt
{
	
	/**
	 * 
	 */
	public BookkeepingTransferReceipt(SimpleDateFormat sdf)
	{
		super();
		this.sdf = sdf;
	}
	
	public void setBookingId(Long id)
	{
		this.bookingId = id;
	}
	
	public Long getBookingId()
	{
		return this.bookingId;
	}
	
	public void setDate(Date date)
	{
		this.date = date;
	}
	
	public String getDate()
	{
		return this.sdf.format(this.date);
	}
	
	public void setGroup(String group)
	{
		this.group = group;
	}
	
	public String getGroup()
	{
		return this.group;
	}
	
	public void setType(Integer type)
	{
		this.type = type;
	}
	
	public Integer getType()
	{
		return this.type;
	}
	
	public void setOrigin(Integer origin)
	{
		this.origin = origin;
	}
	
	public Integer getOrigin()
	{
		return this.origin;
	}
	
	public String read()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("{Blg"); //$NON-NLS-1$
		
		if (this.bookingId != null)
		{
			sb.append(" BlgNr=" + this.bookingId); //$NON-NLS-1$
		}
		if (this.date != null)
		{
			sb.append(" Date=" + this.sdf.format(this.date)); //$NON-NLS-1$
		}
		if (this.group != null && this.group.length() > 0)
		{
			sb.append(" Grp=" + this.group); //$NON-NLS-1$
		}
		if (this.type != null)
		{
			sb.append(" MType=" + this.type); //$NON-NLS-1$
		}
		if (this.origin != null)
		{
			sb.append(" Orig=" + this.origin); //$NON-NLS-1$
		}
		sb.append(System.getProperty("line.separator")); //$NON-NLS-1$
		
		for (int i = 0; i < this.details.size(); i++)
		{
			sb.append(((BookkeepingTransferReceiptDetail) this.details.get(i)).read());
		}
		
		sb.append("}" + System.getProperty("line.separator")); //$NON-NLS-1$ //$NON-NLS-2$
		
		return sb.toString();
	}
	
	public void createBookkeepingTransferReceiptDetails(Receipt receipt)
	{
		Position[] positions = receipt.getPositionsAsArray();
		for (int i = 0; i < positions.length; i++)
		{
			this.addDetail(this.createBookkeepingTransferReceiptDetail(receipt, positions[i]));
		}
		
		Payment[] payments = receipt.getPaymentsAsArray();
		for (int i = 0; i < payments.length; i++)
		{
			this.addDetail(this.createBookkeepingTransferReceiptDetail(receipt, payments[i]));
		}
	}
	
	private BookkeepingTransferReceiptDetail createBookkeepingTransferReceiptDetail(Receipt receipt, Position position)
	{
		BookkeepingTransferReceiptDetail detail = new BookkeepingTransferReceiptDetail(this.sdf);
		detail.setCurrency(receipt.getDefaultCurrency().getCurrency());
		detail.setAccount(position.getProductGroup().account);
		detail.setDate(receipt.timestamp);
		detail.setTaxId(position.getCurrentTax().fibuId);
		detail.setText(position.text);
		detail.setType(new Integer(1));
		detail.setNetAmount(new Double(position.getNetAmount()));
		detail.setTaxAmount(new Double(position.getTaxAmount()));
		detail.setBookTax(new Integer(1));
		return detail;
	}
	
	private BookkeepingTransferReceiptDetail createBookkeepingTransferReceiptDetail(Receipt receipt, Payment payment)
	{
		BookkeepingTransferReceiptDetail detail = new BookkeepingTransferReceiptDetail(this.sdf);
		detail.setCurrency(payment.getForeignCurrency().getCurrency());
		detail.setAccount(payment.getPaymentType().account);
		detail.setDate(receipt.timestamp);
		detail.setText(payment.getPaymentType().name);
		detail.setType(new Integer(0));
		detail.setGrosAmountFC(new Double(payment.getAmountFC()));
		detail.setNetAmount(new Double(payment.getAmount()));
		detail.setBookTax(new Integer(0));
		return detail;
	}
	
	public boolean addDetail(BookkeepingTransferReceiptDetail detail)
	{
		return this.details.add(detail);
	}
	
	public boolean removeDetail(BookkeepingTransferReceiptDetail detail)
	{
		return this.details.remove(detail);
	}
	
	HashMap map = new HashMap(); // Name (in Fibu) Default Optional Colibri
	private Long bookingId = null; // BlgNr empty x empty
	private Date date = null; // Date Receipt.timestamp
	private String group = null; // Grp x statistic.properties.group
	private Integer type = null; // MType 0 statistic.properties.type
	private Integer origin = null; // Orig 3 statistic.properties.origin
	
	private ArrayList details = new ArrayList();
	
	public SimpleDateFormat sdf;
}
