/*
 * Created on 15.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.core;

import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;

import ch.eugster.pos.Messages;
import ch.eugster.pos.util.NumberUtility;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BookkeepingTransferReceiptDetail
{
	
	/**
	 * 
	 */
	public BookkeepingTransferReceiptDetail(SimpleDateFormat sdf)
	{
		super();
		this.sdf = sdf;
	}
	
	public void setCurrency(Currency currency)
	{
		this.currency = currency;
	}
	
	/**
	 * @param account
	 */
	public void setAccount(String account)
	{
		if (account != null && account.length() > 0)
		{
			this.account = account;
		}
	}
	
	/**
	 * @return
	 */
	public String getAccount()
	{
		return this.account;
	}
	
	/**
	 * @param bBookingType
	 */
	public void setBBookingType(Integer bBookingType)
	{
		this.bBookingType = bBookingType;
	}
	
	/**
	 * @return
	 */
	public Integer getBBookingType()
	{
		return this.bBookingType;
	}
	
	/**
	 * @param string
	 */
	public void setCode(String code)
	{
		if (code != null && code.length() > 0)
		{
			this.code = code;
		}
	}
	
	/**
	 * @return
	 */
	public String getCode()
	{
		return this.code;
	}
	
	/**
	 * @param costBookingId
	 */
	public void setCostBookingId(Integer costBookingId)
	{
		this.costBookingId = costBookingId;
	}
	
	/**
	 * @return
	 */
	public Integer getCostBookingId()
	{
		return this.costBookingId;
	}
	
	/**
	 * @param costTypeAccount
	 */
	public void setCostTypeAccount(String costTypeAccount)
	{
		if (costTypeAccount != null && costTypeAccount.length() > 0)
		{
			this.costTypeAccount = costTypeAccount;
		}
	}
	
	/**
	 * @return
	 */
	public String getCostTypeAccount()
	{
		return this.costTypeAccount;
	}
	
	/**
	 * @param date
	 */
	public void setDate(Date date)
	{
		this.date = date;
	}
	
	/**
	 * @return
	 */
	public Date getDate()
	{
		return this.date;
	}
	
	/**
	 * @param flags
	 */
	public void setFlags(Integer flags)
	{
		this.flags = flags;
	}
	
	/**
	 * @return
	 */
	public Integer getFlags()
	{
		return this.flags;
	}
	
	/**
	 * @param grosAmountFC
	 */
	public void setGrosAmountFC(Double grosAmountFC)
	{
		this.grosAmountFC = grosAmountFC;
	}
	
	/**
	 * @return
	 */
	public Double getGrosAmountFC()
	{
		return this.grosAmountFC;
	}
	
	/**
	 * @param netAmount
	 */
	public void setNetAmount(Double netAmount)
	{
		this.netAmount = netAmount;
	}
	
	/**
	 * @return
	 */
	public Double getNetAmount()
	{
		return this.netAmount;
	}
	
	/**
	 * @param opId
	 */
	public void setOpId(String opId)
	{
		if (opId != null && opId.length() > 0)
		{
			this.opId = opId;
		}
	}
	
	/**
	 * @return
	 */
	public String getOpId()
	{
		return this.opId;
	}
	
	/**
	 * @param primaryKey
	 */
	public void setPrimaryKey(Long primaryKey)
	{
		this.primaryKey = primaryKey;
	}
	
	/**
	 * @return
	 */
	public Long getPrimaryKey()
	{
		return this.primaryKey;
	}
	
	/**
	 * @param taxAmount
	 */
	public void setTaxAmount(Double taxAmount)
	{
		this.taxAmount = taxAmount;
	}
	
	/**
	 * @return
	 */
	public Double getTaxAmount()
	{
		return this.taxAmount;
	}
	
	/**
	 * @param taxBookingId
	 */
	public void setTaxBookingId(Integer taxBookingId)
	{
		this.taxBookingId = taxBookingId;
	}
	
	/**
	 * @return
	 */
	public Integer getTaxBookingId()
	{
		return this.taxBookingId;
	}
	
	/**
	 * @param taxId
	 */
	public void setTaxId(String taxId)
	{
		if (taxId != null && taxId.length() > 0)
		{
			this.taxId = taxId;
		}
	}
	
	/**
	 * @return
	 */
	public String getTaxId()
	{
		return this.taxId;
	}
	
	/**
	 * @param text
	 */
	public void setText(String text)
	{
		if (text != null && text.length() > 0)
		{
			this.text = text;
		}
	}
	
	/**
	 * @return
	 */
	public String getText()
	{
		return this.text;
	}
	
	/**
	 * @param text
	 */
	public void setText2(String text)
	{
		if (text != null && text.length() > 0)
		{
			this.text2 = text;
		}
	}
	
	/**
	 * @return
	 */
	public String getText2()
	{
		return this.text2;
	}
	
	/**
	 * @param type
	 */
	public void setType(Integer type)
	{
		this.type = type;
	}
	
	/**
	 * @return
	 */
	public Integer getType()
	{
		return this.type;
	}
	
	/**
	 * @param boolean1
	 */
	public void setBookTax(Integer bookTax)
	{
		this.bookTax = bookTax;
	}
	
	/**
	 * @return
	 */
	public Integer getBookTax()
	{
		return this.bookTax;
	}
	
	public String read()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(Messages.getString("BookkeepingTransferReceiptDetail.{Bk_1")); //$NON-NLS-1$
		
		if (this.account != null)
		{
			sb.append(Messages.getString("BookkeepingTransferReceiptDetail._AccId__2") + this.account); //$NON-NLS-1$
		}
		if (this.bBookingType != null)
		{
			sb.append(Messages.getString("BookkeepingTransferReceiptDetail._BType__3") + this.bBookingType); //$NON-NLS-1$
		}
		if (this.costTypeAccount != null)
		{
			sb.append(Messages.getString("BookkeepingTransferReceiptDetail._CAcc__4") + this.costTypeAccount); //$NON-NLS-1$
		}
		if (this.costBookingId != null)
		{
			sb.append(Messages.getString("BookkeepingTransferReceiptDetail._CIdx__5") + this.costBookingId); //$NON-NLS-1$
		}
		if (this.code != null)
		{
			sb.append(Messages.getString("BookkeepingTransferReceiptDetail._Code__6") + this.code); //$NON-NLS-1$
		}
		if (this.date != null)
		{
			sb.append(Messages.getString("BookkeepingTransferReceiptDetail._Date__7") + this.sdf.format(this.date)); //$NON-NLS-1$
		}
		if (this.flags != null)
		{
			sb.append(Messages.getString("BookkeepingTransferReceiptDetail._Flags__8") + this.flags); //$NON-NLS-1$
		}
		if (this.taxId != null)
		{
			sb.append(Messages.getString("BookkeepingTransferReceiptDetail._TaxId__9") + this.taxId); //$NON-NLS-1$
		}
		if (this.text != null)
		{
			sb.append(Messages.getString("BookkeepingTransferReceiptDetail._Text___10") + this.text + Messages.getString("BookkeepingTransferReceiptDetail.__11")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (this.text2 != null)
		{
			sb.append(Messages.getString("BookkeepingTransferReceiptDetail._Text2___12") + this.text2 + Messages.getString("BookkeepingTransferReceiptDetail.__13")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (this.taxBookingId != null)
		{
			sb.append(Messages.getString("BookkeepingTransferReceiptDetail._TIdx__14") + this.taxBookingId); //$NON-NLS-1$
		}
		if (this.type != null)
		{
			sb.append(Messages.getString("BookkeepingTransferReceiptDetail._Type__15") + this.type); //$NON-NLS-1$
		}
		if (this.netAmount != null)
		{
			sb.append(Messages.getString("BookkeepingTransferReceiptDetail._ValNt__16") + NumberUtility.round(this.netAmount.doubleValue(), this.currency.getDefaultFractionDigits())); //$NON-NLS-1$
		}
		if (this.taxAmount != null)
		{
			sb.append(Messages.getString("BookkeepingTransferReceiptDetail._ValTx__17") + NumberUtility.round(this.taxAmount.doubleValue(), this.currency.getDefaultFractionDigits())); //$NON-NLS-1$
		}
		if (this.grosAmountFC != null)
		{
			sb.append(Messages.getString("BookkeepingTransferReceiptDetail._ValFW__18") + NumberUtility.round(this.grosAmountFC.doubleValue(), this.currency.getDefaultFractionDigits())); //$NON-NLS-1$
		}
		if (this.opId != null)
		{
			sb.append(Messages.getString("BookkeepingTransferReceiptDetail._OpId__19") + this.opId); //$NON-NLS-1$
		}
		if (this.primaryKey != null)
		{
			sb.append(Messages.getString("BookkeepingTransferReceiptDetail._PkKey__20") + this.primaryKey); //$NON-NLS-1$
		}
		if (this.bookTax != null)
		{
			sb.append(Messages.getString("BookkeepingTransferReceiptDetail._MkTxB__21") + this.bookTax); //$NON-NLS-1$
		}
		sb.append(Messages.getString("BookkeepingTransferReceiptDetail.}_22") + System.getProperty(Messages.getString("BookkeepingTransferReceiptDetail.line.separator_23"))); //$NON-NLS-1$ //$NON-NLS-2$
		return sb.toString();
	}
	
	private String account = null; // AccId
	private Integer bBookingType = null; // BType
	private String costTypeAccount = null; // CAcc
	private Integer costBookingId = null; // CIdx
	private String code = null; // Code
	private Date date = null; // Date
	private Integer flags = null; // Flags
	private String taxId = null; // TaxId
	private String text = null; // Text
	private String text2 = null; // Text2
	private Integer taxBookingId = null; // TIdx
	private Integer type = null; // Type
	private Double netAmount = null; // ValNt
	private Double taxAmount = null; // ValTx
	private Double grosAmountFC = null; // ValFW
	private String opId = null; // OpId
	private Long primaryKey = null; // PkKey
	private Integer bookTax = null; // MkTxB
	
	private SimpleDateFormat sdf;
	private Currency currency;
}
