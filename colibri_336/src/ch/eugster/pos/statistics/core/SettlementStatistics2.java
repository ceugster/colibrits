/*
 * Created on 07.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.db.Settlement;
import ch.eugster.pos.statistics.gui.DateRangeGroup;
import ch.eugster.pos.statistics.gui.SalespointComposite;
import ch.eugster.pos.statistics.gui.SettlementComposite;
import ch.eugster.pos.statistics.gui.SettlementDateComposite;
import ch.eugster.pos.statistics.gui.SettlementNumberComposite;
import ch.eugster.pos.swt.PrintDestinationGroup;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SettlementStatistics2 extends Statistics
{
	
	/**
	 * @param salespointComposite
	 * @param dateRangeGroup
	 * @param printDestinationGroup
	 * @param properties
	 */
	public SettlementStatistics2(SalespointComposite salespointComposite, DateRangeGroup dateRangeGroup,
					PrintDestinationGroup printDestinationGroup, Properties properties,
					SettlementComposite settlementComposite)
	{
		super(salespointComposite, dateRangeGroup, printDestinationGroup, properties);
		this.settlementComposite = settlementComposite;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.statistics.data.Statistics#setOptions()
	 */
	protected void setOptions()
	{
		this.fullSelection = this.settlementComposite.getFullSelection();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.statistics.data.Statistics#setReport()
	 */
	protected void setReport()
	{
		if (this.settlementComposite.getCurrentControl() instanceof SettlementDateComposite)
		{
			SettlementDateComposite cp = (SettlementDateComposite) this.settlementComposite.getCurrentControl();
			int report = cp.getSelectedDateReport();
			switch (report)
			{
				case 0:
				{
					this.reportTemplate = "SettlementStatistics2"; //$NON-NLS-1$
					this.reportDesignName = "SettlementStatistics2"; //$NON-NLS-1$
					break;
				}
				case 1:
				{
					this.reportTemplate = "SettlementDiffs"; //$NON-NLS-1$
					this.reportDesignName = "SettlementDiffs"; //$NON-NLS-1$
					break;
				}
				case 2:
				{
					this.reportTemplate = "SettlementDiscounts"; //$NON-NLS-1$
					this.reportDesignName = "SettlementDiscounts"; //$NON-NLS-1$
					break;
				}
			}
		}
		else
		{
			this.reportTemplate = "SettlementStatistics2"; //$NON-NLS-1$
			this.reportDesignName = "SettlementStatistics2"; //$NON-NLS-1$
		}
	}
	
	protected boolean configureReport()
	{
		return false;
	}
	
	public boolean hasRecords()
	{
		Salespoint[] salespoints = null;
		if (!this.sc.areAllSalespointsSelected())
		{
			salespoints = this.sc.getSelectedSalespoints();
			if (salespoints.length == 0) salespoints = null;
		}
		
		if (this.settlementComposite.getCurrentControl() instanceof SettlementNumberComposite)
		{
			return Settlement.countSettlements(this.settlementComposite.getSelectedSettlement()) > 0;
		}
		else if (this.settlementComposite.getCurrentControl() instanceof SettlementDateComposite)
		{
			SettlementDateComposite cp = (SettlementDateComposite) this.settlementComposite.getCurrentControl();
			int report = cp.getSelectedDateReport();
			switch (report)
			{
				case 0:
				{
					return Settlement.countSettlements(salespoints, this.drg.getFromDate(), this.drg.getToDate(),
									this.settlementComposite.getFullSelection()) > 0;
				}
				case 1:
				{
					return Settlement.countDiffRecords(salespoints, this.drg.getFromDate(), this.drg.getToDate()) > 0;
				}
				case 2:
				{
					return Position.countDiscountRecords(salespoints, this.drg.getFromDate(), this.drg.getToDate(),
									this.settlementComposite.getOnlyWithDiscounts()) > 0;
				}
			}
		}
		return false;
	}
	
	protected Iterator selectData()
	{
		this.rows = new ArrayList();
		
		Salespoint[] salespoints = null;
		if (!this.sc.areAllSalespointsSelected())
		{
			salespoints = this.sc.getSelectedSalespoints();
			if (salespoints.length == 0) salespoints = null;
		}
		
		if (this.settlementComposite.getCurrentControl() instanceof SettlementNumberComposite)
		{
			return Settlement.selectSettlements(this.settlementComposite.getSelectedSettlement());
		}
		else if (this.settlementComposite.getCurrentControl() instanceof SettlementDateComposite)
		{
			SettlementDateComposite cp = (SettlementDateComposite) this.settlementComposite.getCurrentControl();
			int report = cp.getSelectedDateReport();
			switch (report)
			{
				case 0:
				{
					this.count = Settlement.countReceipts(salespoints, this.drg.getFromDate(), this.drg.getToDate());
					return Settlement.selectSettlements(salespoints, this.from, this.to, this.settlementComposite
									.getFullSelection());
				}
				case 1:
				{
					return Settlement.selectDiffRecords(salespoints, this.drg.getFromDate(), this.drg.getToDate());
				}
				case 2:
				{
					return Position.selectDiscountRecords(salespoints, this.drg.getFromDate(), this.drg.getToDate(),
									this.settlementComposite.getOnlyWithDiscounts());
				}
			}
		}
		return null;
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
		if (this.settlementComposite.getCurrentControl() instanceof SettlementDateComposite)
		{
			SettlementDateComposite cp = (SettlementDateComposite) this.settlementComposite.getCurrentControl();
			int report = cp.getSelectedDateReport();
			switch (report)
			{
				case 1:
				{
					Calendar calendar = GregorianCalendar.getInstance();
					Collection diffs = new ArrayList();
					while (iterator.hasNext())
					{
						Object[] object = (Object[]) iterator.next();
						Salespoint salespoint = Salespoint.selectById((Long) object[0]);
						calendar.setTimeInMillis(((Long) object[1]).longValue());
						Map map = new HashMap();
						map.put("salespoint", salespoint.name);
						map.put("settlement", object[1].toString() + " " + object[3].toString());
						map.put("date", calendar.getTime());
						map.put("code", object[2]);
						map.put("type", object[4]);
						map.put("subtype", object[5]);
						map.put("cashtype", object[6]);
						map.put("amount", object[7]);
						diffs.add(map);
					}
					return new JRMapArrayDataSource(diffs.toArray(new Object[0]));
				}
				case 2:
				{
					Collection discounts = new ArrayList();
					while (iterator.hasNext())
					{
						Object[] object = (Object[]) iterator.next();
						Salespoint salespoint = Salespoint.selectById((Long) object[0]);
						Map map = new HashMap();
						map.put("salespoint", salespoint.name);
						map.put("year", object[1]);
						map.put("month", object[2]);
						map.put("day", object[3]);
						map.put("amount", object[4]);
						double fullAmount = ((Double) object[5]).doubleValue();
						double discount = fullAmount - ((Double) object[4]).doubleValue();
						map.put("discount", new Double(discount));
						map.put("percent", fullAmount == 0d ? new Double(0d) : new Double(discount / fullAmount));
						map.put("fullAmount", object[5]);
						discounts.add(map);
					}
					return new JRMapArrayDataSource(discounts.toArray(new Object[0]));
				}
			}
		}
		
		while (iterator.hasNext())
		{
			Object object = iterator.next();
			Object[] row = (Object[]) object;
			this.rows.add(new Row(row));
		}
		
		if (this.settlementComposite.getCurrentControl() instanceof SettlementDateComposite)
		{
			Collection rowsToDelete = new ArrayList();
			Map diffs = new HashMap();
			
			Iterator allRows = this.rows.iterator();
			while (allRows.hasNext())
			{
				Row row = (Row) allRows.next();
				if (row.get("type").equals(new Integer(Settlement.TYPE_CASH_CHECK)))
				{
					if (row.get("cashtype").equals(new Integer(Settlement.CASH_CHECK_DIFFERENCE)))
					{
						rowsToDelete.add(row);
						Row diff = (Row) diffs.get(row.get("subtype"));
						if (diff == null)
						{
							diff = new Row(row);
						}
						else
						{
							double amount = ((Double) diff.get("amount1")).doubleValue();
							amount += ((Double) row.get("amount1")).doubleValue();
							diff.put("amount1", new Double(amount));
						}
						diffs.put(diff.get("subtype"), diff);
					}
					else if (row.get("cashtype").equals(new Integer(9)))
					{
						System.out.println();
					}
				}
			}
			if (rowsToDelete.size() > 0)
			{
				Iterator delete = rowsToDelete.iterator();
				while (delete.hasNext())
				{
					Row row = (Row) delete.next();
					this.rows.remove(row);
				}
			}
			
			Collection values = diffs.values();
			Iterator allValues = values.iterator();
			while (allValues.hasNext())
			{
				Row row = (Row) allValues.next();
				double amount = ((Double) row.get("amount1")).doubleValue();
				if (amount == 0d)
				{
					row.put("text", "Differenz");
				}
				else if (amount < 0d)
				{
					row.put("text", "Zuviel in Kasse");
				}
				else
				{
					row.put("text", "Zuwenig in Kasse");
				}
				row.put("amount1", new Double(Math.abs(amount)));
				this.rows.add(row);
			}
		}
		
		Row[] rows = (Row[]) this.rows.toArray(new Row[0]);
		Arrays.sort(rows);
		return new JRMapArrayDataSource(rows);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.statistics.data.Statistics#getParameters()
	 */
	protected Hashtable getMoreParameters(Hashtable ht)
	{
		String text = null;
		if (this.settlementComposite.getCurrentControl() instanceof SettlementNumberComposite)
		{
			text = "Abschluss: " + this.settlementComposite.getSelectedSettlement().toString();
		}
		else
		{
			text = "";
		}
		ht.put("settlements", text);
		ht.put("receiptCount", "Anzahl Belege: " + this.count);
		return ht;
	}
	
	public boolean getPrintReversedReceipts()
	{
		return this.fullSelection;
	}
	
	public class Row extends HashMap implements Comparable
	{
		public Row(Object[] object)
		{
			this.put("salespointId", object[0]);
			this.put("type", object[1]);
			this.put("subtype", object[2]);
			this.put("cashtype", object[3]);
			this.put("text", object[4]);
			this.put("code", object[5]);
			this.put("value", object[6] == null ? new Double(0d) : object[6]);
			this.put("receipts", object[7]);
			this.put("quantity", object[8]);
			this.put("amount1", object[9]);
			this.put("amount2", object[10]);
		}
		
		public Row(Row row)
		{
			this.put("salespointId", row.get("salespointId"));
			this.put("type", row.get("type"));
			this.put("subtype", row.get("subtype"));
			this.put("cashtype", row.get("cashtype"));
			this.put("text", row.get("text"));
			this.put("code", row.get("code"));
			this.put("value", row.get("value"));
			this.put("receipts", row.get("receipts"));
			this.put("quantity", row.get("quantity"));
			this.put("amount1", row.get("amount1"));
			this.put("amount2", row.get("amount2"));
		}
		
		public int compareTo(Object other)
		{
			if (other instanceof Row)
			{
				Row row = (Row) other;
				if (row.get("type").equals(this.get("type")))
				{
					if (row.get("subtype").equals(this.get("subtype")))
					{
						if (row.get("cashtype").equals(this.get("cashtype")))
						{
							if (row.get("value").equals(this.get("value")))
							{
								return ((String) this.get("text")).compareTo((String) row.get("text"));
							}
							else
							{
								return Double.compare(((Double) row.get("value")).doubleValue(), ((Double) this
												.get("value")).doubleValue());
							}
						}
						else
						{
							return ((Integer) this.get("cashtype")).intValue()
											- ((Integer) row.get("cashtype")).intValue();
						}
					}
					else
					{
						return ((Integer) this.get("subtype")).intValue() - ((Integer) row.get("subtype")).intValue();
					}
				}
				else
				{
					return ((Integer) this.get("type")).intValue() - ((Integer) row.get("type")).intValue();
				}
			}
			return 0;
		}
		
	}
	
	public long getReceiptCount()
	{
		return this.count;
	}
	
	private SettlementComposite settlementComposite;
	private boolean fullSelection = false;
	double betrag = 0d;
	private long count = 0;
	private Collection rows;
}
