/*
 * Created on 07.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import ch.eugster.pos.Messages;
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.statistics.gui.DateRangeGroup;
import ch.eugster.pos.statistics.gui.SalespointComposite;
import ch.eugster.pos.statistics.gui.TaxAccountComposite;
import ch.eugster.pos.swt.PrintDestinationGroup;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TaxAccountStatistics extends Statistics
{
	
	/**
	 * @param salespointComposite
	 * @param dateRangeGroup
	 * @param printDestinationGroup
	 * @param properties
	 */
	public TaxAccountStatistics(SalespointComposite salespointComposite, DateRangeGroup dateRangeGroup,
					PrintDestinationGroup printDestinationGroup, Properties properties,
					TaxAccountComposite taxAccountComposite)
	{
		super(salespointComposite, dateRangeGroup, printDestinationGroup, properties);
		this.tac = taxAccountComposite;
	}
	
	public String getPrintButtonDesignation()
	{
		return Messages.getString("TaxAccountStatistics.Drucken_1"); //$NON-NLS-1$
	}
	
	public String getPrintFileName()
	{
		return Messages.getString("TaxAccountStatistics.Mwst-Liste_2"); //$NON-NLS-1$
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
		this.reportTemplate = "TaxAccounting"; //$NON-NLS-1$
		this.reportDesignName = "TaxAccounting"; //$NON-NLS-1$
		
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
		Salespoint[] salespoints = null;
		if (!this.sc.areAllSalespointsSelected())
		{
			salespoints = this.sc.getSelectedSalespoints();
		}
		
		return Position.selectPositions(salespoints, this.from, this.to, this.tac.isWithoutZeroPercentPositions());
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
		while (iterator.hasNext())
		{
			Position p = (Position) iterator.next();
			Row r = new Row(p);
			list.add(r);
		}
		
		Row[] array = (Row[]) list.toArray(new Row[0]);
		Arrays.sort(array);
		
		return new JRMapArrayDataSource(array);
	}
	
	private String makeKey(Position p)
	{
		if (p.getProductGroup().type == ProductGroup.TYPE_EXPENSE)
		{
			if (p.getQuantity() < 0)
			{
				return p.getProductGroup().account + "|" + p.getCurrentTax().getTax().account; //$NON-NLS-1$
			}
			else
			{
				return p.getCurrentTax().getTax().account + "|" + p.getProductGroup().account; //$NON-NLS-1$
			}
		}
		else
		{
			if (p.getAmount() < 0d)
			{
				return p.getCurrentTax().getTax().account + "|" + p.getProductGroup().account; //$NON-NLS-1$
			}
			else
			{
				return p.getProductGroup().account + "|" + p.getCurrentTax().getTax().account; //$NON-NLS-1$
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.statistics.data.Statistics#getParameters()
	 */
	protected Hashtable getMoreParameters(Hashtable ht)
	{
		return ht;
	}
	
	private class Row extends HashMap implements Comparable
	{
		
		public Row(Position p)
		{
			this.put("date", p.getReceipt().timestamp); //$NON-NLS-1$
			this.put("receipt", p.getReceipt().getNumber()); //$NON-NLS-1$
			if (p.productId == null || p.productId.length() == 0)
			{
				this.put("text", p.getProductGroup().name); //$NON-NLS-1$
			}
			else
			{
				StringBuffer sb = new StringBuffer(""); //$NON-NLS-1$
				sb = this.addAuthor(sb, p.author);
				sb = this.addTitle(sb, p.title);
				sb = this.addPublisher(sb, p.publisher);
				this.put("text", sb.toString()); //$NON-NLS-1$
			}
			
			if (p.getProductGroup().type == ProductGroup.TYPE_EXPENSE)
			{
				if (p.getQuantity() < 0)
				{
					this.put("laccount", p.getProductGroup().account); //$NON-NLS-1$
					this.put("raccount", p.getCurrentTax().getTax().account); //$NON-NLS-1$
				}
				else
				{
					this.put("laccount", p.getCurrentTax().getTax().account); //$NON-NLS-1$
					this.put("raccount", p.getProductGroup().account); //$NON-NLS-1$
				}
			}
			else
			{
				if (p.getAmount() < 0d)
				{
					this.put("laccount", p.getCurrentTax().getTax().account); //$NON-NLS-1$
					this.put("raccount", p.getProductGroup().account); //$NON-NLS-1$
				}
				else
				{
					this.put("laccount", p.getProductGroup().account); //$NON-NLS-1$
					this.put("raccount", p.getCurrentTax().getTax().account); //$NON-NLS-1$
				}
			}
			this.put("price", new Double(p.getPrice())); //$NON-NLS-1$
			this.put("quantity", new Integer(p.getQuantity())); //$NON-NLS-1$
			this.put("discount", new Double(p.getDiscount())); //$NON-NLS-1$
			this.put("amount", new Double(p.getAmount())); //$NON-NLS-1$
			this.put("percentage", new Double(p.getCurrentTax().percentage)); //$NON-NLS-1$
			this.put("taxAmount", new Double(p.getTaxAmount())); //$NON-NLS-1$
		}
		
		private StringBuffer addProductId(StringBuffer sb, String productId)
		{
			if (productId != null)
			{
				sb.append(productId);
			}
			return sb;
		}
		
		private StringBuffer addAuthor(StringBuffer sb, String author)
		{
			if (author != null && author.length() > 0)
			{
				sb = sb.append(author);
				sb = sb.append(": "); //$NON-NLS-1$
			}
			return sb;
		}
		
		private StringBuffer addTitle(StringBuffer sb, String title)
		{
			if (title != null && title.length() > 0)
			{
				sb = sb.append(title);
				sb = sb.append("; "); //$NON-NLS-1$
			}
			return sb;
		}
		
		private StringBuffer addPublisher(StringBuffer sb, String publisher)
		{
			if (publisher != null && publisher.length() > 0)
			{
				sb = sb.append(publisher);
			}
			return sb;
		}
		
		public int compareTo(Object other)
		{
			int i = -1;
			if (other instanceof Row)
			{
				i = ((Timestamp) this.get("date")).compareTo((Timestamp) ((Row) other).get("date"));
			}
			
			return i;
		}
	}
	
	private TaxAccountComposite tac;
}
