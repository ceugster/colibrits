/*
 * Created on 27.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.data;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.LogManager;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import ch.eugster.pos.Messages;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.statistics.gui.DateRangeGroup;
import ch.eugster.pos.statistics.gui.DayHourComposite;
import ch.eugster.pos.statistics.gui.SalespointComposite;
import ch.eugster.pos.swt.PrintDestinationGroup;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Day14HourStatistics extends Statistics
{
	
	/**
	 * 
	 */
	/**
	 * @param salespointComposite
	 * @param dateRangeGroup
	 * @param printDestinationGroup
	 */
	public Day14HourStatistics(SalespointComposite salespointComposite, DateRangeGroup dateRangeGroup,
					PrintDestinationGroup printDestinationGroup, Properties properties,
					DayHourComposite dayHourComposite)
	{
		super(salespointComposite, dateRangeGroup, printDestinationGroup, properties);
		this.dhc = dayHourComposite;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.statistics.data.Statistics#setOptions()
	 */
	protected void setOptions()
	{
		this.weekday = this.dhc.getDayOfWeek();
	}
	
	protected void setReport()
	{
		String reportName = this.dhc.getReportName();
		this.reportTemplate = reportName;
		this.reportDesignName = reportName;
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
		if (this.sc.areAllSalespointsSelected())
		{
			salespoints = this.sc.getSelectedSalespoints();
		}
		return Receipt.selectDayHourStatisticsRange(salespoints, this.from, this.to, this.weekday);
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
		Hashtable records = new Hashtable();
		/*
		 * Zuerst die aus der Datenbank gezogenen Datensätze mit den Basisdaten
		 * in eine Hashtable abfüllen...
		 */
		while (iterator.hasNext())
		{
			if (LogManager.getLogManager().getLogger("colibri") != null)
			{
				LogManager.getLogManager().getLogger("colibri").info("Daten konvertieren");
			}
			Object[] results = this.convertResults((Object[]) iterator.next());
			// Erste und letzte Tagesstunde für Output speichern...
			int hour = ((Integer) results[1]).intValue();
			if (hour < this.startHour) this.startHour = hour;
			if (this.endHour < hour) this.endHour = hour;
			
			SortMap map = (SortMap) records.get(results[0]);
			if (map == null)
			{
				/*
				 * Record initialisieren...
				 */
				if (LogManager.getLogManager().getLogger("colibri") != null)
				{
					LogManager.getLogManager().getLogger("colibri").info("SortMap initialisieren");
				}
				map = this.initRecord(results);
			}
			else
			{
				if (LogManager.getLogManager().getLogger("colibri") != null)
				{
					LogManager.getLogManager().getLogger("colibri").info("Numerische Daten übertragen");
				}
				map = this.initNumericValue(results, map);
			}
			
			if (LogManager.getLogManager().getLogger("colibri") != null)
			{
				LogManager.getLogManager().getLogger("colibri").info("SortMap In Liste übertragen");
			}
			records.put(results[0], map);
		}
		
		/*
		 * Nun die Daten aus der Hashtable in eine Liste umladen...
		 */
		if (LogManager.getLogManager().getLogger("colibri") != null)
		{
			LogManager.getLogManager().getLogger("colibri").info("In Sortierliste übertragen");
		}
		ArrayList list = new ArrayList();
		Enumeration enumerationeration = records.elements();
		while (enumerationeration.hasMoreElements())
		{
			list.add(enumerationeration.nextElement());
		}
		
		if (LogManager.getLogManager().getLogger("colibri") != null)
		{
			LogManager.getLogManager().getLogger("colibri").info("Daten sortieren");
		}
		Object[] array = list.toArray(new Object[0]);
		Arrays.sort(array);
		
		return new JRMapArrayDataSource(array);
	}
	
	private Object[] convertResults(Object[] results)
	{
		if (results[1] instanceof Long)
		{
			Long hour = (Long) results[1];
			results[1] = new Integer(hour.intValue());
		}
		else if (results[1] instanceof Double)
		{
			Double hour = (Double) results[1];
			results[1] = new Integer(hour.intValue());
		}
		if (results[2] instanceof Long)
		{
			Long year = (Long) results[2];
			results[2] = new Integer(year.intValue());
		}
		else if (results[2] instanceof Double)
		{
			Double year = (Double) results[2];
			results[2] = new Integer(year.intValue());
		}
		return results;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.statistics.data.Statistics#getParameters()
	 */
	protected Hashtable getMoreParameters(Hashtable ht)
	{
		ht.put("weekday", this.getOptionTextDayOfWeek()); //$NON-NLS-1$
		ht.put("name", "Kasse"); //$NON-NLS-1$ //$NON-NLS-2$
		DecimalFormat idf = new DecimalFormat(Messages.getString("DayHourStatistics.00_54")); //$NON-NLS-1$
		for (int i = 0; i < 24; i++)
		{
			String from = idf.format(new Integer(i));
			String to = idf.format(new Integer(i + 1));
			ht.put("h" + new Integer(i).toString(), from + "-" + to); //$NON-NLS-1$ //$NON-NLS-2$
		}
		ht.put("total", Messages.getString("DayHourStatistics.Total_58")); //$NON-NLS-1$ //$NON-NLS-2$
		return ht;
	}
	
	private String getOptionTextDayOfWeek()
	{
		StringBuffer sb = new StringBuffer(Messages.getString("DayHourStatistics.Wochentag___59")); //$NON-NLS-1$
		int day = this.dhc.getDayOfWeek();
		if (day == -1)
		{
			sb.append(Messages.getString("DayHourStatistics.Alle_60")); //$NON-NLS-1$
		}
		else
		{
			sb.append(this.getDayName(day));
		}
		return sb.toString();
	}
	
	private String getDayName(int i)
	{
		String day = ""; //$NON-NLS-1$
		switch (i)
		{
			case 0:
				day = Messages.getString("DayHourStatistics.Montag_62"); //$NON-NLS-1$
			case 1:
				day = Messages.getString("DayHourStatistics.Dienstag_63"); //$NON-NLS-1$
			case 2:
				day = Messages.getString("DayHourStatistics.Mittwoch_64"); //$NON-NLS-1$
			case 3:
				day = Messages.getString("DayHourStatistics.Donnerstag_65"); //$NON-NLS-1$
			case 4:
				day = Messages.getString("DayHourStatistics.Freitag_66"); //$NON-NLS-1$
			case 5:
				day = Messages.getString("DayHourStatistics.Samstag_67"); //$NON-NLS-1$
			case 6:
				day = Messages.getString("DayHourStatistics.Sonntag_68"); //$NON-NLS-1$
		}
		;
		return day;
	}
	
	private SortMap initRecord(Object[] results)
	{
		String[] keys = new String[24];
		/*
		 * General
		 */
		SortMap map = new SortMap();
		map.put(Messages.getString("DayHourStatistics.name_69"), results[0]); //$NON-NLS-1$
		for (int i = 0; i < keys.length; i++)
		{
			keys[i] = ("h" + new Integer(i)).toString(); //$NON-NLS-1$
			map.put(keys[i], new Double(0));
		}
		
		map = this.initNumericValue(results, map);
		
		return map;
	}
	
	private SortMap initNumericValue(Object[] values, SortMap map)
	{
		double sum = ((Double) map.get("h" + values[1].toString())).doubleValue(); //$NON-NLS-1$
		Double amount = new Double(sum + ((Double) values[3]).doubleValue());
		map.put("h" + values[1].toString(), amount); //$NON-NLS-1$
		return map;
	}
	
	public class SortMap extends HashMap implements Comparable
	{
		private static final long serialVersionUID = 0l;
		
		public int compareTo(Object other)
		{
			int i = -1;
			if (other instanceof SortMap)
			{
				SortMap otherMap = (SortMap) other;
				String key = (String) this.get("name"); //$NON-NLS-1$
				String otherKey = (String) otherMap.get("name"); //$NON-NLS-1$
				i = key.compareTo(otherKey);
			}
			return i;
		}
	}
	
	private DayHourComposite dhc;
	private int weekday = -1;
	
	private int startHour = 24;
	private int endHour = 0;
	// private int start = 84;
	// private int end = 708;
	// private int height = 12;
	
}
