/*
 * Created on 07.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.core;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

import org.eclipse.swt.widgets.TableItem;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.Payment;
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.statistics.gui.DateRangeGroup;
import ch.eugster.pos.statistics.gui.ReceiptListComposite;
import ch.eugster.pos.statistics.gui.SalespointComposite;
import ch.eugster.pos.swt.PrintDestinationGroup;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ReceiptListStatistics extends Statistics
{
	
	/**
	 * @param salespointComposite
	 * @param dateRangeGroup
	 * @param printDestinationGroup
	 * @param properties
	 */
	public ReceiptListStatistics(SalespointComposite salespointComposite, DateRangeGroup dateRangeGroup,
					PrintDestinationGroup printDestinationGroup, Properties properties,
					ReceiptListComposite receiptListComposite)
	{
		super(salespointComposite, dateRangeGroup, printDestinationGroup, properties);
		this.receiptListComposite = receiptListComposite;
	}
	
	protected String getSelectionTextDateRange()
	{
		StringBuffer sb = new StringBuffer(Messages.getString("Statistics.Datumsbereich___7")); //$NON-NLS-1$
		SimpleDateFormat sdf = new SimpleDateFormat(Messages.getString("Statistics.dd.MM.yyyy_8")); //$NON-NLS-1$
		// GregorianCalendar gc = new GregorianCalendar();
		sb.append(sdf.format(this.from));
		return sb.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.statistics.data.Statistics#setOptions()
	 */
	protected void setOptions()
	{
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.statistics.data.Statistics#setReport()
	 */
	protected void setReport()
	{
		this.reportTemplate = "ReceiptList"; //$NON-NLS-1$
		this.reportDesignName = "ReceiptList"; //$NON-NLS-1$
	}
	
	protected boolean configureReport()
	{
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.statistics.data.Statistics#selectData()
	 */
	protected Iterator selectData()
	{
		// Salespoint[] salespoints = null;
		// if (!sc.areAllSalespointsSelected()) {
		// salespoints = sc.getSelectedSalespoints();
		// }
		
		TableItem[] receipts = this.receiptListComposite.getReceiptTable().getTable().getItems();
		for (int i = 0; i < receipts.length; i++)
		{
			this.list.add(receipts[i].getData());
		}
		
		return this.list.iterator();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.statistics.data.Statistics#computeOutput(java.util.Iterator
	 * )
	 */
	protected JRDataSource computeOutput(Iterator iterator)
	{
		ArrayList list = new ArrayList();
		this.betrag = 0d;
		while (iterator.hasNext())
		{
			Receipt r = (Receipt) iterator.next();
			if (r.status == Receipt.RECEIPT_STATE_SERIALIZED)
			{
				this.addPositions(list, r);
				this.addPayments(list, r);
			}
		}
		
		Row[] rows = (Row[]) list.toArray(new Row[0]);
		list.clear();
		// Arrays.sort(rows);
		
		return new JRMapArrayDataSource(rows);
	}
	
	private void addPositions(ArrayList list, Receipt r)
	{
		Position[] p = r.getPositionsAsArray();
		for (int i = 0; i < p.length; i++)
		{
			this.addPosition(list, p[i]);
		}
	}
	
	private void addPosition(ArrayList list, Position p)
	{
		Row row = new Row(p);
		list.add(row);
	}
	
	private void addPayments(ArrayList list, Receipt r)
	{
		Payment[] p = r.getPaymentsAsArray();
		for (int i = 0; i < p.length; i++)
		{
			// 10226
			// if (!p[i].getPaymentTypeId().equals(PaymentType.BACK_ID)) {
			// addPayment(list, p[i]);
			// }
			if (!p[i].isBack())
			{
				this.addPayment(list, p[i]);
			}
			// 10226
		}
		for (int i = 0; i < p.length; i++)
		{
			// 10226
			// if (p[i].getPaymentTypeId().equals(PaymentType.BACK_ID)) {
			// addPayment(list, p[i]);
			// }
			if (p[i].isBack())
			{
				this.addPayment(list, p[i]);
			}
			// 10226
		}
	}
	
	private void addPayment(ArrayList list, Payment p)
	{
		Row row = new Row(p);
		list.add(row);
	}
	
	protected Hashtable getMoreParameters(Hashtable ht)
	{
		return ht;
	}
	
	private class Row extends HashMap implements Comparable
	{
		public static final long serialVersionUID = 0l;
		
		public Row(Position p)
		{
			this.setPosition(p);
		}
		
		public Row(Payment p)
		{
			this.setPayment(p);
		}
		
		public void setPosition(Position p)
		{
			this.put("receiptid", p.getReceiptId());
			this.put("receipt", p.getReceipt().getFormattedNumber());
			this.put("date", new Timestamp(p.getReceipt().getDate().getTime()));
			this.put("salespoint", p.getReceipt().getSalespoint().name);
			this.put("user", p.getReceipt().getUser().username);
			this.put("key", "position");
			this.put("text", this.computeText(p));
			this.put("price", new Double(p.getPrice()));
			this.put("quantity", new Integer(p.getQuantity()));
			this.put("discount", new Double(p.getDiscount() * 100));
			this.put("amount1", new Double(p.getAmount()));
			this.put("quotation", Double.toString(p.getCurrentTax().percentage) + "%");
			this.put("taxcode", p.getCurrentTax().getTax().code.substring(0, 1));
			this.put("amount2", new Double(p.getTaxAmount()));
		}
		
		private String computeText(Position p)
		{
			StringBuffer whole = new StringBuffer();
			StringBuffer article = new StringBuffer();
			if (p.productId != null && p.productId.length() > 0 && !p.productId.equals("0"))
			{
				article.append(p.productId);
			}
			StringBuffer book = new StringBuffer();
			if (p.author != null && !p.author.equals(""))
			{
				book.append(p.author + ", ");
			}
			if (p.title != null && !p.title.equals(""))
			{
				if (book.length() > 0)
				{
					book.append(": ");
				}
				book.append(p.title);
			}
			if (article.length() > 0)
			{
				article.append(": ");
				article.append(book);
			}
			
			StringBuffer group = new StringBuffer();
			if (article.length() > 0)
			{
				group.append(" (");
			}
			if (p.getProductGroup().galileoId != null && p.getProductGroup().galileoId.length() > 0)
			{
				group.append(p.getProductGroup().galileoId);
			}
			if (p.getProductGroup().name != null && p.getProductGroup().name.length() > 0)
			{
				if (group.length() > 0)
				{
					group.append(" ");
				}
				group.append(p.getProductGroup().name);
			}
			if (article.length() > 0)
			{
				group.append(")");
			}
			
			whole.append(article);
			whole.append(group);
			return whole.toString();
		}
		
		public void setPayment(Payment p)
		{
			this.put("receiptid", p.getReceiptId());
			this.put("receipt", p.getReceipt().getFormattedNumber());
			this.put("date", new Timestamp(p.getReceipt().getDate().getTime()));
			this.put("salespoint", p.getReceipt().getSalespoint().name);
			this.put("user", p.getReceipt().getUser().username);
			this.put("key", "payment");
			this.put("text", this.computeText(p));
			this.put("price", null);
			this.put("quantity", null);
			this.put("discount", null);
			this.put("amount1", new Double(p.getAmount()));
			this.put("quotation", p.getQuotation() != 1d ? Double.toString(p.getQuotation()) : null);
			this.put("taxcode", null);
			this.put("amount2", p.getAmountFC() != p.getAmount() ? new Double(p.getAmountFC()) : null);
		}
		
		private String computeText(Payment p)
		{
			if (p.getForeignCurrency().getId().equals(ForeignCurrency.getDefaultCurrency().getId()))
			{
				if (p.isBack())
					return "Rückgeld";
				else
					return p.getPaymentType().name;
			}
			else
			{
				if (p.isBack())
					return "Rückgeld " + p.getForeignCurrency().name;
				else
					return p.getForeignCurrency().name;
			}
		}
		
		public int compareTo(Object other)
		{
			return 0;
		}
	}
	
	private ReceiptListComposite receiptListComposite;
	// private boolean printReversedReceipts = false;
	private ArrayList list = new ArrayList();
	double betrag = 0d;
}
