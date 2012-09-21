/*
 * Created on 12.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Logger;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.statistics.gui.DateRangeGroup;
import ch.eugster.pos.statistics.gui.ReceiptStatisticsComposite;
import ch.eugster.pos.statistics.gui.SalespointComposite;
import ch.eugster.pos.swt.PrintDestinationGroup;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ReceiptStatistics extends Statistics
{
	private Hashtable salespoints;
	private Hashtable years;
	
	/**
	 * @param salespointComposite
	 * @param dateRangeGroup
	 * @param printDestinationGroup
	 */
	public ReceiptStatistics(SalespointComposite salespointComposite, DateRangeGroup dateRangeGroup,
					PrintDestinationGroup printDestinationGroup, Properties properties,
					ReceiptStatisticsComposite receiptStatisticsComposite)
	{
		super(salespointComposite, dateRangeGroup, printDestinationGroup, properties);
		this.receiptStatisticsComposite = receiptStatisticsComposite;
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
		if (this.receiptStatisticsComposite.getGroupSelection() == 1)
		{
			this.reportTemplate = "ReceiptStatisticsYear"; //$NON-NLS-1$
			this.reportDesignName = "ReceiptStatisticsYear"; //$NON-NLS-1$
		}
		else
		{
			this.reportTemplate = "ReceiptStatisticsSalespoint"; //$NON-NLS-1$
			this.reportDesignName = "ReceiptStatisticsSalespoint"; //$NON-NLS-1$
		}
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
		return Position.selectReceiptStatistics(salespoints, this.from, this.to,
						this.receiptStatisticsComposite.getGroupSelection());
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
		this.years = new Hashtable();
		this.salespoints = new Hashtable();
		/*
		 * Zuerst die aus der Datenbank gezogenen Datensätze mit den Basisdaten
		 * in eine Hashtable abfüllen...
		 */
		while (iterator.hasNext())
		{
			Object[] item = (Object[]) iterator.next();
			
			this.addItem(item);
			
			Logger.getLogger("colibri").info("Daten einfügen");
			Logger.getLogger("colibri").info("Daten in Liste übertragen");
		}
		
		if (this.receiptStatisticsComposite.getGroupSelection() == 0)
		{
			Enumeration ss = this.salespoints.elements();
			while (ss.hasMoreElements())
			{
				Hashtable years = (Hashtable) ss.nextElement();
				Enumeration ys = years.elements();
				while (ys.hasMoreElements())
				{
					list.add(ys.nextElement());
				}
			}
		}
		else if (this.receiptStatisticsComposite.getGroupSelection() == 1)
		{
			Enumeration ys = this.years.elements();
			while (ys.hasMoreElements())
			{
				Hashtable salespoints = (Hashtable) ys.nextElement();
				Enumeration ss = salespoints.elements();
				while (ss.hasMoreElements())
				{
					list.add(ss.nextElement());
				}
			}
		}
		
		return new JRMapArrayDataSource(list.toArray(new Object[0]));
	}
	
	private void addItem(Object[] item)
	{
		if (this.receiptStatisticsComposite.getGroupSelection() == 0)
		{
			this.addItemToSalespoint(item);
		}
		else if (this.receiptStatisticsComposite.getGroupSelection() == 1)
		{
			this.addItemToYear(item);
		}
	}
	
	private void addItemToSalespoint(Object[] item)
	{
		// Nach Kasse
		Hashtable salespoint = (Hashtable) this.salespoints.get(item[0]);
		if (salespoint == null)
		{
			Hashtable sp = new Hashtable();
			sp.put(item[1], new Row(item));
			this.salespoints.put(item[0], sp);
		}
		else
		{
			Row row = (Row) salespoint.get(item[1]);
			if (row == null)
			{
				row = new Row(item);
				salespoint.put(item[1], row);
			}
			else
			{
				row.add(item);
			}
		}
	}
	
	private void addItemToYear(Object[] item)
	{
		// Nach Jahr
		Hashtable year = (Hashtable) this.years.get(item[1]);
		if (year == null)
		{
			Hashtable y = new Hashtable();
			y.put(item[0], new Row(item));
			this.years.put(item[1], y);
		}
		else
		{
			Row row = (Row) year.get(item[0]);
			if (row == null)
			{
				row = new Row(item);
				year.put(item[0], row);
			}
			else
			{
				row.add(item);
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
		private static final long serialVersionUID = 0l;
		
		public Row(Object[] item)
		{
			this.put("salespoint", item[0]); //$NON-NLS-1$ 
			this.put("year", item[1]); //$NON-NLS-1$ 
			this.put("salespointId", item[2]); //$NON-NLS-1$ 
			this.put("receipts", new Integer(1)); //$NON-NLS-1$
			this.put("count", item[4]); //$NON-NLS-1$
			this.put("amount", item[5]); //$NON-NLS-1$
		}
		
		public void add(Object[] item)
		{
			int receipts = ((Integer) this.get("receipts")).intValue();
			this.put("receipts", new Integer(++receipts));
			double count = ((Double) this.get("count")).doubleValue();
			count += ((Double) item[4]).doubleValue();
			this.put("count", new Double(count));
			double amount = ((Double) this.get("amount")).doubleValue();
			amount += ((Double) item[5]).doubleValue();
			this.put("amount", new Double(amount));
		}
		
		public int compareTo(Object other)
		{
			Row row = (Row) other;
			int result = ((String) this.get("salespoint")).compareTo((String) row.get("salespoint")); //$NON-NLS-1$ //$NON-NLS-2$
			if (result == 0)
			{
				return ((Integer) this.get("year")).compareTo((Integer) row.get("year")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else
			{
				return result;
			}
		}
	}
	
	private ReceiptStatisticsComposite receiptStatisticsComposite;
	// private int weekday = -1;
	//
	// private long startHour = 24;
	// private long endHour = 0;
	// private int start = 84;
	// private int end = 708;
	// private int height = 12;
}
