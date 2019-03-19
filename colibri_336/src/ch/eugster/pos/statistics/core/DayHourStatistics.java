/*
 * Created on 12.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.core;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.statistics.gui.DateRangeGroup;
import ch.eugster.pos.statistics.gui.DayHourComposite;
import ch.eugster.pos.statistics.gui.SalespointComposite;
import ch.eugster.pos.swt.PrintDestinationGroup;
import ch.eugster.pos.util.Path;
import ch.eugster.pos.util.XMLLoader;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DayHourStatistics extends Statistics
{
	
	/**
	 * @param salespointComposite
	 * @param dateRangeGroup
	 * @param printDestinationGroup
	 */
	public DayHourStatistics(SalespointComposite salespointComposite, DateRangeGroup dateRangeGroup,
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
	@Override
	protected void setOptions()
	{
		this.weekday = this.dhc.getDayOfWeek();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.statistics.data.Statistics#setReport()
	 */
	@Override
	protected void setReport()
	{
		int i = this.pdg.getDestination();
		if (i == PrintDestinationGroup.FILE)
		{
			int j = this.pdg.getFileType();
			switch (j)
			{
				case PrintDestinationGroup.FILE_EXCEL:
					this.reportDesignName = "DayHourStatisticsGrid"; //$NON-NLS-1$
					return;
				case PrintDestinationGroup.FILE_HTML:
					this.reportDesignName = "DayHourStatisticsGrid"; //$NON-NLS-1$
					return;
				case PrintDestinationGroup.FILE_CSV:
					this.reportDesignName = "DayHourStatisticsGrid"; //$NON-NLS-1$
					return;
			}
		}
		
		this.reportTemplate = "DayHourStatisticsTemplate"; //$NON-NLS-1$
		this.reportDesignName = "DayHourStatisticsTemp"; //$NON-NLS-1$
		File file = new File(Path.getInstance().repDir + File.separator + this.reportDesignName + ".xml"); //$NON-NLS-1$
		if (file.exists())
		{
			file.delete();
		}
	}
	
	@Override
	protected boolean configureReport()
	{
		int columnCount = new Long(this.endHour - this.startHour + 1).intValue();
		try
		{
			Document doc = XMLLoader.getDocument(Path.getInstance().repDir + File.separator + this.reportTemplate
							+ ".xml", true); //$NON-NLS-1$
			Element root = doc.getRootElement();
			
			Element pageHeader = root.getChild("pageHeader"); //$NON-NLS-1$
			Element band = pageHeader.getChild("band"); //$NON-NLS-1$
			this.addTitleColumns(columnCount, band);
			
			Element detail = root.getChild("detail"); //$NON-NLS-1$
			band = detail.getChild("band"); //$NON-NLS-1$
			this.addDetailColumns(columnCount, band);
			
			Element summary = root.getChild("summary"); //$NON-NLS-1$
			band = summary.getChild("band"); //$NON-NLS-1$
			this.addSummaryColumns(columnCount, band);
			
			File file = new File(Path.getInstance().repDir + File.separator + this.reportDesignName + ".xml"); //$NON-NLS-1$
			XMLLoader.saveXML(file, doc);
			return true;
		}
		catch (JDOMException e)
		{
			return false;
		}
		catch (IOException e)
		{
			return false;
		}
	}
	
	private void addTitleColumns(int columns, Element band)
	{
		int y = 4;
		int width = (this.end - this.start) / columns;
		int pos = this.start;
		String pattern = ""; //$NON-NLS-1$
		for (long hour = this.startHour; hour <= this.endHour; hour++)
		{
			String data = "$P{h" + String.valueOf(hour) + "}"; //$NON-NLS-1$ //$NON-NLS-2$
			band.addContent(this.createTextField("java.lang.String", data, pattern, pos, y, width, this.height)); //$NON-NLS-1$
			pos += width;
		}
	}
	
	private void addDetailColumns(int columns, Element band)
	{
		int y = 2;
		int width = (this.end - this.start) / columns;
		int pos = this.start;
		String pattern = "#,##0"; //$NON-NLS-1$
		for (long hour = this.startHour; hour <= this.endHour; hour++)
		{
			String data = "$F{h" + String.valueOf(hour) + "}"; //$NON-NLS-1$ //$NON-NLS-2$
			band.addContent(this.createTextField("java.lang.Double", data, pattern, pos, y, width, this.height)); //$NON-NLS-1$
			pos += width;
		}
	}
	
	private void addSummaryColumns(int columns, Element band)
	{
		int y = 8;
		int width = (this.end - this.start) / columns;
		int pos = this.start;
		String pattern = "#,##0"; //$NON-NLS-1$
		for (long hour = this.startHour; hour <= this.endHour; hour++)
		{
			String data = "$V{h" + String.valueOf(hour) + "}"; //$NON-NLS-1$ //$NON-NLS-2$
			band.addContent(this.createTextField("java.lang.Double", data, pattern, pos, y, width, this.height)); //$NON-NLS-1$
			pos += width;
		}
	}
	
	private Element createTextField(String clazz, String data, String pattern, int x, int y, int width, int height)
	{
		Element element = new Element("textField"); //$NON-NLS-1$
		if (!pattern.equals("")) element.setAttribute("pattern", pattern); //$NON-NLS-1$ //$NON-NLS-2$
		element.addContent(this.createReportElement(x, y, width, height));
		element.addContent(this.createTextElement());
		element.addContent(this.createTextFieldExpressionElement(clazz, data));
		return element;
	}
	
	private Element createReportElement(int x, int y, int width, int height)
	{
		Element element = new Element("reportElement"); //$NON-NLS-1$
		element.setAttribute("positionType", "Float"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("x", String.valueOf(x)); //$NON-NLS-1$
		element.setAttribute("y", String.valueOf(y)); //$NON-NLS-1$ 
		element.setAttribute("width", String.valueOf(width)); //$NON-NLS-1$
		element.setAttribute("height", String.valueOf(height)); //$NON-NLS-1$ 
		return element;
	}
	
	private Element createFontElement()
	{
		Element element = new Element("font"); //$NON-NLS-1$
		element.setAttribute("fontName", "Arial"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("size", "8"); //$NON-NLS-1$ //$NON-NLS-2$
		return element;
	}
	
	private Element createTextElement()
	{
		Element element = new Element("textElement"); //$NON-NLS-1$
		element.setAttribute("textAlignment", "Right"); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("verticalAlignment", "Middle"); //$NON-NLS-1$ //$NON-NLS-2$
		element.addContent(this.createFontElement());
		return element;
	}
	
	private Element createTextFieldExpressionElement(String clazz, String data)
	{
		Element element = new Element("textFieldExpression"); //$NON-NLS-1$
		element.setAttribute("class", clazz); //$NON-NLS-1$
		element.addContent(this.createDataString(data));
		return element;
	}
	
	private CDATA createDataString(String data)
	{
		return new CDATA(data);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.statistics.data.Statistics#selectData()
	 */
	@Override
	protected Iterator selectData()
	{
		Salespoint[] salespoints = null;
		if (this.sc.areAllSalespointsSelected())
		{
			salespoints = this.sc.getSelectedSalespoints();
		}
		return Receipt.selectDayHourStatisticsRangeFromPayment(salespoints, this.from, this.to, this.weekday);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.statistics.data.Statistics#computeOutput(java.util.Iterator
	 * )
	 */
	@Override
	protected JRDataSource computeOutput(Iterator iterator)
	{
		Hashtable records = new Hashtable();
		/*
		 * Zuerst die aus der Datenbank gezogenen Datensätze mit den Basisdaten
		 * in eine Hashtable abfüllen...
		 */
		while (iterator.hasNext())
		{
			Object[] results = (Object[]) iterator.next();
			// Erste und letzte Tagesstunde für Output speichern...
			long hour = ((Long) results[1]).longValue();
			if (hour < this.startHour) this.startHour = hour;
			if (this.endHour < hour) this.endHour = hour;
			
			SortMap map = (SortMap) records.get(results[0]);
			if (map == null)
			{
				/*
				 * Record initialisieren...
				 */
				map = this.initRecord(results);
			}
			else
			{
				map = this.initNumericValue(results, map);
			}
			
			records.put(results[0], map);
		}
		
		/*
		 * Nun die Daten aus der Hashtable in eine Liste umladen...
		 */
		ArrayList list = new ArrayList();
		Enumeration enumeration = records.elements();
		while (enumeration.hasMoreElements())
		{
			list.add(enumeration.nextElement());
		}
		
		Object[] array = list.toArray(new Object[0]);
		Arrays.sort(array);
		
		return new JRMapArrayDataSource(array);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.statistics.data.Statistics#getParameters()
	 */
	@Override
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
	
	private long startHour = 24;
	private long endHour = 0;
	private int start = 84;
	private int end = 708;
	private int height = 12;
}
