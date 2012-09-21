/*
 * Created on 27.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
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
import ch.eugster.pos.statistics.gui.ProductGroupComposite;
import ch.eugster.pos.statistics.gui.SalespointComposite;
import ch.eugster.pos.swt.PrintDestinationGroup;
import ch.eugster.pos.util.NumberUtility;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ProductGroupStatistics extends Statistics
{
	
	/**
	 * 
	 */
	public ProductGroupStatistics(SalespointComposite salespointComposite, DateRangeGroup dateRangeGroup,
					PrintDestinationGroup printDestinationGroup, Properties properties,
					ProductGroupComposite productGroupComposite)
	{
		super(salespointComposite, dateRangeGroup, printDestinationGroup, properties);
		this.pgc = productGroupComposite;
	}
	
	protected void setOptions()
	{
		this.comparePreviousYear = this.pgc.previousYearCompare();
	}
	
	protected void setReport()
	{
		if (this.pgc.getType().equals(ProductGroupComposite.TYPE[1][0]))
		{
			this.reportTemplate = this.comparePreviousYear ? "ProductGroupStatisticsWithPreviousYear" : //$NON-NLS-1$
							"ProductGroupStatistics"; //$NON-NLS-1$
		}
		else if (this.pgc.getType().equals(ProductGroupComposite.TYPE[1][1]))
		{
			this.reportTemplate = this.comparePreviousYear ? "ProductGroupStatisticsEachWithPreviousYear" : //$NON-NLS-1$
							"ProductGroupStatisticsEach"; //$NON-NLS-1$
		}
		else if (this.pgc.getType().equals(ProductGroupComposite.TYPE[1][2]))
		{
			this.reportTemplate = "ProductGroupStockOrderStatistics"; //$NON-NLS-1$
		}
		if (this.reportDesignName == null) this.reportDesignName = this.reportTemplate;
	}
	
	protected boolean configureReport()
	{
		return false;
	}
	
	protected Iterator selectData()
	{
		Salespoint[] salespoints = null;
		if (!this.sc.areAllSalespointsSelected())
		{
			salespoints = this.sc.getSelectedSalespoints();
		}
		
		if (this.pgc.getType().equals(ProductGroupComposite.TYPE[1][2]))
		{
			return Position.selectProductGroupStockOrderRange(salespoints, this.from, this.to, this.pgc.getSort(),
							this.pgc.onlyGalileoProductGroups(), this.pgc.withExpenses(), this.pgc.withOtherSales());
		}
		else
		{
			return Position.selectProductGroupStatisticsRange(salespoints, this.from, this.to, this.pgc.getSort(),
							this.pgc.onlyGalileoProductGroups(), this.pgc.withExpenses(), this.pgc.withOtherSales(),
							this.comparePreviousYear);
		}
	}
	
	protected Hashtable getMoreParameters(Hashtable ht)
	{
		ht.put("previousYear", this.getTitlePeriod(true)); //$NON-NLS-1$
		ht.put("currentYear", this.getTitlePeriod(false)); //$NON-NLS-1$
		return ht;
	}
	
	protected String getTitlePeriod(boolean previousYear)
	{
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(this.drg.getFromDate());
		int from = gc.get(GregorianCalendar.YEAR);
		gc.setTime(this.drg.getToDate());
		int to = gc.get(GregorianCalendar.YEAR);
		
		if (previousYear)
		{
			from--;
			to--;
		}
		
		return this.getPeriodString(from, to);
	}
	
	protected String getPeriodString(int from, int to)
	{
		if (from == to)
		{
			return new Integer(from).toString();
		}
		else
		{
			return new Integer(from).toString() + "/" + //$NON-NLS-1$
							new Integer(to).toString();
		}
	}
	
	protected JRDataSource computeOutput(Iterator itr)
	{
		if (this.pgc.getType().equals(ProductGroupComposite.TYPE[1][2]))
		{
			Logger.getLogger("colibri").info("Entering ProductGroupStatistics.computeStockOrderComparison");
			return this.computeStockOrderComparison(itr);
		}
		else
		{
			Logger.getLogger("colibri").info("Entering ProductGroupStatistics.computeProductGroups");
			return this.computeProductGroups(itr);
		}
	}
	
	private JRMapArrayDataSource computeStockOrderComparison(Iterator itr)
	{
		Hashtable records = new Hashtable();
		
		while (itr.hasNext())
		{
			Object[] results = (Object[]) itr.next();
			// Object[] results = convertResults((Object[])itr.next());
			
			String key = results[0].toString() + "." + results[1].toString();
			
			StockOrderSortMap map = (StockOrderSortMap) records.get(key);
			if (map == null)
			{
				/*
				 * Record initialisieren...
				 */
				map = this.initStockOrderRecord(results);
			}
			else
			{
				map = this.putDate(map, results);
			}
			records.put(key, map);
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
	
	private JRMapArrayDataSource computeProductGroups(Iterator itr)
	{
		Hashtable records = new Hashtable();
		Double[] totals = new Double[3];
		
		int p = 0;
		/*
		 * Zuerst die aus der Datenbank gezogenen Datensätze mit den Basisdaten
		 * in eine Hashtable abfüllen...
		 */
		while (itr.hasNext())
		{
			p++;
			// Logger.getLogger("colibri").info("Rufe ProductGroupStatistics.convertResults");
			Object[] results = (Object[]) itr.next();
			// Object[] results = convertResults((Object[])itr.next());
			
			String key = "wg_" + results[0].toString();
			
			SortMap map = (SortMap) records.get(key);
			if (map == null)
			{
				/*
				 * Record initialisieren...
				 */
				// Logger.getLogger("colibri").info("SortMap initialisieren");
				map = this.initRecord(results);
			}
			/*
			 * Aus Datenbank übernommene Daten einlesen...
			 */
			// Logger.getLogger("colibri").info("Daten in SortMap abfüllen");
			map = this.putBasisData(results, map);
			
			// Logger.getLogger("colibri").info("SortMap in die Liste eintragen");
			records.put(key, map);
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
		
		/*
		 * Nun die Totale berechnen...
		 */
		Logger.getLogger("colibri").info("Totale berechnen");
		Iterator listIter = list.iterator();
		while (listIter.hasNext())
		{
			SortMap map = (SortMap) listIter.next();
			totals[0] = this.computeTotal((Double) map.get("l_amount"), totals[0]); //$NON-NLS-1$
			totals[1] = this.computeTotal((Double) map.get("b_amount"), totals[1]); //$NON-NLS-1$
			totals[2] = new Double(NumberUtility.round(totals[0].doubleValue() + totals[1].doubleValue(), .01));
		}
		
		/*
		 * Nun die Zusatzdaten berechnen...
		 */
		listIter = list.iterator();
		while (listIter.hasNext())
		{
			Logger.getLogger("colibri").info("Statistik berechnen");
			if (this.pgc.getType().equals(ProductGroupComposite.TYPE[1][0]))
			{
				this.computeStatistics(totals, (SortMap) listIter.next());
			}
			else if (this.pgc.getType().equals(ProductGroupComposite.TYPE[1][1]))
			{
				this.computeStatistics(totals[2], (SortMap) listIter.next());
			}
		}
		
		Object[] array;
		if (this.noEmptyLines)
		{
			Logger.getLogger("colibri").info("In ArrayList übertragen");
			ArrayList al = new ArrayList();
			Iterator i = list.iterator();
			while (i.hasNext())
			{
				SortMap map = (SortMap) i.next();
				if (!((Double) map.get("t_amount")).equals(new Double(0d))) { //$NON-NLS-1$
					al.add(map);
				}
			}
			array = al.toArray(new Object[0]);
		}
		else
		{
			array = list.toArray(new Object[0]);
		}
		
		Logger.getLogger("colibri").info("Daten sortieren");
		Arrays.sort(array);
		
		return new JRMapArrayDataSource(array);
	}
	
	// private Object[] convertResults(Object[] results) {
	// if (results[0] instanceof Integer) {
	// Integer id = (Integer)results[0];
	// results[0] = new Long(id.longValue());
	// }
	// if (results[3] instanceof Long) {
	// Long id = (Long)results[3];
	// results[3] = new Integer(id.intValue());
	// }
	// results[4] = results[4];
	// results[5] = results[5];
	// if (results[6] instanceof String) {
	// String quantity = (String) results[6];
	// results[6] = new Long(quantity);
	// }
	// else if (results[6] instanceof Integer) {
	// Integer quantity = (Integer) results[6];
	// results[6] = new Long(quantity.longValue());
	// }
	// else if (results[6] instanceof Double) {
	// Double quantity = (Double) results[6];
	// results[6] = new Long(quantity.longValue());
	// }
	// if (results[7] instanceof Integer) {
	// Integer amount = (Integer)results[7];
	// results[7] = new Double(NumberUtility.round(amount.doubleValue(),
	// ForeignCurrency.getDefaultCurrency().roundFactor));
	// }
	// else if (results[7] instanceof Long) {
	// Long amount = (Long)results[7];
	// results[7] = new Double(NumberUtility.round(amount.doubleValue(),
	// ForeignCurrency.getDefaultCurrency().roundFactor));
	// }
	// else {
	// results[7] = new
	// Double(NumberUtility.round(((Double)results[7]).doubleValue(),
	// ForeignCurrency.getDefaultCurrency().roundFactor));
	// }
	// return results;
	// }
	
	private SortMap initRecord(Object[] results)
	{
		String[] keys = new String[4];
		keys[0] = "id"; //$NON-NLS-1$
		keys[1] = "galileoId"; //$NON-NLS-1$
		keys[3] = "name"; //$NON-NLS-1$
		/*
		 * General
		 */
		SortMap map = new SortMap();
		map.put(keys[0], results[0]);
		map.put(keys[1], results[1]);
		map.put(keys[3], results[4]);
		/*
		 * Ladenverkauf
		 */
		map = this.initNumericValues("l_", map); //$NON-NLS-1$
		/*
		 * Besorgungen
		 */
		map = this.initNumericValues("b_", map); //$NON-NLS-1$
		/*
		 * Total
		 */
		map = this.initNumericValues("t_", map); //$NON-NLS-1$
		/*
		 * Durchschnittlicher Verkaufspreis
		 */
		map.put("section_per_item", new Double(0d)); //$NON-NLS-1$
		
		return map;
	}
	
	private SortMap initNumericValues(String prev, SortMap map)
	{
		String[] keys = new String[4];
		keys[0] = prev.concat("quantity"); //$NON-NLS-1$
		keys[1] = prev.concat("amount"); //$NON-NLS-1$
		keys[2] = prev.concat("quantity_prev_year"); //$NON-NLS-1$
		keys[3] = prev.concat("amount_prev_year"); //$NON-NLS-1$
		
		map.put(keys[0], new Long(0));
		map.put(keys[1], new Double(0));
		map.put(keys[2], new Long(0));
		map.put(keys[3], new Double(0));
		return map;
	}
	
	private SortMap putBasisData(Object[] results, SortMap map)
	{
		String prev;
		String post = ""; //$NON-NLS-1$
		
		if (((String) results[2]).equals("B")) { //$NON-NLS-1$
			prev = "b_"; //$NON-NLS-1$
		}
		else
		{
			prev = "l_"; //$NON-NLS-1$
		}
		
		if (!((Boolean) results[5]).booleanValue())
		{
			post = "_prev_year"; //$NON-NLS-1$
		}
		
		String[] keys = new String[3];
		keys[0] = prev + "quantity".concat(post); //$NON-NLS-1$
		keys[1] = prev + "amount".concat(post); //$NON-NLS-1$
		
		map.put(keys[0], results[6]);
		map.put(keys[1], results[7]);
		
		return map;
	}
	
	private Double computeTotal(Double value, Double sum)
	{
		if (sum == null)
		{
			sum = value;
		}
		else
		{
			sum = new Double(sum.doubleValue() + value.doubleValue());
		}
		return sum;
	}
	
	private void computeStatistics(Double[] totals, SortMap map)
	{
		Double zero = new Double(0d);
		Logger.getLogger("colibri").info("l_quantity" + map.get("l_quantity").getClass().getName());
		Long qtyL = (Long) map.get("l_quantity"); //$NON-NLS-1$
		Logger.getLogger("colibri").info("l_amount");
		Double valueL = (Double) map.get("l_amount"); //$NON-NLS-1$
		Logger.getLogger("colibri").info("l_amount_prev_year");
		Double valuePrevYearL = (Double) map.get("l_amount_prev_year"); //$NON-NLS-1$
		Logger.getLogger("colibri").info("l_change_percents");
		Double changePercentsL = valuePrevYearL.equals(zero) ? zero : new Double(NumberUtility.round(
						valueL.doubleValue() / valuePrevYearL.doubleValue() - 1, .0001));
		map.put("l_change_percents", changePercentsL); //$NON-NLS-1$
		Logger.getLogger("colibri").info("l_proportion_group");
		Double portionTurnoverGroup = totals[0].equals(zero) ? zero : new Double(NumberUtility.round(
						valueL.doubleValue() / totals[0].doubleValue(), .0001));
		map.put("l_proportion_group", portionTurnoverGroup); //$NON-NLS-1$
		Logger.getLogger("colibri").info("l_proportion");
		Double portionTurnover = totals[2].equals(zero) ? zero : new Double(NumberUtility.round(valueL.doubleValue()
						/ totals[2].doubleValue(), .0001));
		map.put("l_proportion", portionTurnover); //$NON-NLS-1$
		
		Logger.getLogger("colibri").info("b_quantity");
		Long qtyB = (Long) map.get("b_quantity"); //$NON-NLS-1$
		Logger.getLogger("colibri").info("b_amount");
		Double valueB = (Double) map.get("b_amount"); //$NON-NLS-1$
		Logger.getLogger("colibri").info("b_amount_prev_year");
		Double valuePrevYearB = (Double) map.get("b_amount_prev_year"); //$NON-NLS-1$
		Logger.getLogger("colibri").info("b_change_percents");
		Double changePercentsB = valuePrevYearB.equals(zero) ? zero : new Double(NumberUtility.round(
						valueB.doubleValue() / valuePrevYearB.doubleValue() - 1, .0001));
		map.put("b_change_percents", changePercentsB); //$NON-NLS-1$
		Logger.getLogger("colibri").info("b_proportion_group");
		portionTurnoverGroup = totals[1].equals(zero) ? zero : new Double(NumberUtility.round(valueB.doubleValue()
						/ totals[1].doubleValue(), .0001));
		map.put("b_proportion_group", portionTurnoverGroup); //$NON-NLS-1$
		Logger.getLogger("colibri").info("b_proportion");
		portionTurnover = totals[2].equals(zero) ? zero : new Double(NumberUtility.round(valueB.doubleValue()
						/ totals[2].doubleValue(), .0001));
		map.put("b_proportion", portionTurnover); //$NON-NLS-1$
		
		Logger.getLogger("colibri").info("t_amount");
		Double valueT = new Double(NumberUtility.round(valueL.doubleValue() + valueB.doubleValue(), .01));
		map.put("t_amount", valueT); //$NON-NLS-1$
		Logger.getLogger("colibri").info("t_amount_prev_year");
		Double valuePrevYearT = new Double(NumberUtility.round(
						valuePrevYearL.doubleValue() + valuePrevYearB.doubleValue(), .01));
		map.put("t_amount_prev_year", valuePrevYearT); //$NON-NLS-1$
		Logger.getLogger("colibri").info("t_change_percents");
		Double changePercentsT = valuePrevYearT.equals(zero) ? zero : new Double(NumberUtility.round(
						valueT.doubleValue() / valuePrevYearT.doubleValue() - 1, .01));
		map.put("t_change_percents", changePercentsT); //$NON-NLS-1$
		Logger.getLogger("colibri").info("t_proportion");
		portionTurnover = totals[2].equals(zero) ? zero : new Double(NumberUtility.round(valueT.doubleValue()
						/ totals[2].doubleValue(), .0001));
		map.put("t_proportion", portionTurnover); //$NON-NLS-1$
		
		Logger.getLogger("colibri").info("t_quantity");
		Long quantity = new Long(qtyL.longValue() + qtyB.longValue());
		map.put("t_quantity", quantity); //$NON-NLS-1$
		Logger.getLogger("colibri").info("section_per_item");
		Double price = quantity.equals(zero) ? zero : new Double(NumberUtility.round(
						valueT.doubleValue() / quantity.doubleValue(), .01d));
		map.put("section_per_item", price); //$NON-NLS-1$
	}
	
	private void computeStatistics(Double totals, SortMap map)
	{
		Double zero = new Double(0d);
		Long qtyL = (Long) map.get("l_quantity"); //$NON-NLS-1$
		Double valueL = (Double) map.get("l_amount"); //$NON-NLS-1$
		Double valuePrevYearL = (Double) map.get("l_amount_prev_year"); //$NON-NLS-1$
		Double changePercentsL = valuePrevYearL.equals(zero) ? zero : new Double(NumberUtility.round(
						valueL.doubleValue() / valuePrevYearL.doubleValue() - 1, .0001));
		map.put("l_change_percents", changePercentsL); //$NON-NLS-1$
		
		Long qtyB = (Long) map.get("b_quantity"); //$NON-NLS-1$
		Double valueB = (Double) map.get("b_amount"); //$NON-NLS-1$
		Double valuePrevYearB = (Double) map.get("b_amount_prev_year"); //$NON-NLS-1$
		Double changePercentsB = valuePrevYearB.equals(zero) ? zero : new Double(NumberUtility.round(
						valueB.doubleValue() / valuePrevYearB.doubleValue() - 1, .0001));
		map.put("b_change_percents", changePercentsB); //$NON-NLS-1$
		
		Double valueT = new Double(NumberUtility.round(valueL.doubleValue() + valueB.doubleValue(), .01));
		Double portionTurnoverGroup = valueT.equals(zero) ? zero : new Double(NumberUtility.round(valueL.doubleValue()
						/ valueT.doubleValue(), .0001));
		map.put("l_proportion_group", portionTurnoverGroup); //$NON-NLS-1$
		Double portionTurnover = valueT.equals(zero) ? zero : new Double(NumberUtility.round(valueL.doubleValue()
						/ valueT.doubleValue(), .0001));
		map.put("l_proportion", portionTurnover); //$NON-NLS-1$
		portionTurnoverGroup = valueT.equals(zero) ? zero : new Double(NumberUtility.round(valueB.doubleValue()
						/ valueT.doubleValue(), .0001));
		map.put("b_proportion_group", portionTurnoverGroup); //$NON-NLS-1$
		portionTurnover = valueT.equals(zero) ? zero : new Double(NumberUtility.round(
						valueB.doubleValue() / valueT.doubleValue(), .0001));
		map.put("b_proportion", portionTurnover); //$NON-NLS-1$
		
		map.put("t_amount", valueT); //$NON-NLS-1$
		Double valuePrevYearT = new Double(NumberUtility.round(
						valuePrevYearL.doubleValue() + valuePrevYearB.doubleValue(), .01));
		map.put("t_amount_prev_year", valuePrevYearT); //$NON-NLS-1$
		Double changePercentsT = valuePrevYearT.equals(zero) ? zero : new Double(NumberUtility.round(
						valueT.doubleValue() / valuePrevYearT.doubleValue() - 1, .01));
		map.put("t_change_percents", changePercentsT); //$NON-NLS-1$
		
		portionTurnover = totals.equals(zero) ? zero : new Double(NumberUtility.round(
						valueT.doubleValue() / totals.doubleValue(), .0001));
		map.put("t_proportion", portionTurnover); //$NON-NLS-1$
		
		Long quantity = new Long(qtyL.longValue() + qtyB.longValue());
		map.put("t_quantity", quantity); //$NON-NLS-1$
		Double price = quantity.equals(zero) ? zero : new Double(NumberUtility.round(
						valueT.doubleValue() / quantity.doubleValue(), .01d));
		map.put("section_per_item", price); //$NON-NLS-1$
	}
	
	public class SortMap extends HashMap implements Comparable
	{
		private static final long serialVersionUID = 0l;
		
		public int compareTo(Object other)
		{
			int i = -1;
			if (other instanceof SortMap)
			{
				SortMap m2 = (SortMap) other;
				if (ProductGroupStatistics.this.pgc.getSort().equals(ProductGroupComposite.SORT[1][0]))
				{
					String key1 = (String) this.get("galileoId"); //$NON-NLS-1$
					String key2 = (String) m2.get("galileoId"); //$NON-NLS-1$
					if (key2.equals("")) { //$NON-NLS-1$
						i = -1;
					}
					else if (key1.equals("")) { //$NON-NLS-1$
						i = 1;
					}
					else
					{
						i = key1.compareTo(key2);
					}
				}
				else if (ProductGroupStatistics.this.pgc.getSort().equals(ProductGroupComposite.SORT[1][1]))
				{
					Double key1 = (Double) this.get("t_amount"); //$NON-NLS-1$
					Double key2 = (Double) m2.get("t_amount"); //$NON-NLS-1$
					i = key2.compareTo(key1);
				}
			}
			return i;
		}
	}
	
	public class StockOrderSortMap extends HashMap implements Comparable
	{
		private static final long serialVersionUID = 0l;
		
		public int compareTo(Object other)
		{
			int i = -1;
			if (other instanceof SortMap)
			{
				SortMap m2 = (SortMap) other;
				if (ProductGroupStatistics.this.pgc.getSort().equals(ProductGroupComposite.SORT[1][0]))
				{
					String key1 = (String) this.get("galileoId"); //$NON-NLS-1$
					String key2 = (String) m2.get("galileoId"); //$NON-NLS-1$
					if (key2.equals("")) { //$NON-NLS-1$
						i = -1;
					}
					else if (key1.equals("")) { //$NON-NLS-1$
						i = 1;
					}
					else
					{
						i = key1.compareTo(key2);
					}
				}
				else if (ProductGroupStatistics.this.pgc.getSort().equals(ProductGroupComposite.SORT[1][1]))
				{
					Double key1 = (Double) this.get("t_amount"); //$NON-NLS-1$
					Double key2 = (Double) m2.get("t_amount"); //$NON-NLS-1$
					i = key1.compareTo(key2);
				}
			}
			return i;
		}
	}
	
	private StockOrderSortMap initStockOrderRecord(Object[] results)
	{
		StockOrderSortMap map = new StockOrderSortMap();
		String salespointName = Salespoint.selectById((Long) results[0]).name;
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(this.drg.getFromDate());
		map.put("salespoint", salespointName);
		map.put("firstYear", new Integer(calendar.get(Calendar.YEAR) - 1));
		map.put("lastYear", new Integer(calendar.get(Calendar.YEAR)));
		map.put("firstStockAmount", new Double(0d));
		map.put("lastStockAmount", new Double(0d));
		map.put("firstOrderAmount", new Double(0d));
		map.put("lastOrderAmount", new Double(0d));
		map = this.putName(map, results);
		map = this.putDate(map, results);
		return map;
	}
	
	private StockOrderSortMap putName(StockOrderSortMap map, Object[] results)
	{
		if (results[2] != null && results[2].toString().length() > 0)
		{
			map.put("galileoId", results[2]);
			map.put("productGroup", results[2].toString() + " " + results[3].toString());
		}
		else
		{
			map.put("galileoId", "");
			map.put("productGroup", results[3].toString());
		}
		return map;
	}
	
	private StockOrderSortMap putDate(StockOrderSortMap map, Object[] results)
	{
		boolean year = ((Timestamp) results[6]).after(this.drg.getFromDate())
						&& ((Timestamp) results[6]).before(this.drg.getToDate());
		if (year)
		{
			this.putLastAmount(map, results);
		}
		else
		{
			this.putFirstAmount(map, results);
		}
		return map;
	}
	
	private StockOrderSortMap putFirstAmount(StockOrderSortMap map, Object[] results)
	{
		if (results[4].equals("L"))
		{
			double amount = ((Double) map.get("firstStockAmount")).doubleValue();
			map.put("firstStockAmount", new Double(amount + ((Double) results[7]).doubleValue()));
		}
		if (results[4].equals("B"))
		{
			double amount = ((Double) map.get("firstOrderAmount")).doubleValue();
			map.put("firstOrderAmount", new Double(amount + ((Double) results[7]).doubleValue()));
		}
		return map;
	}
	
	private StockOrderSortMap putLastAmount(StockOrderSortMap map, Object[] results)
	{
		if (results[4].equals("L") || results[4].equals(""))
		{
			double amount = ((Double) map.get("lastStockAmount")).doubleValue();
			map.put("lastStockAmount", new Double(amount + ((Double) results[7]).doubleValue()));
		}
		if (results[4].equals("B"))
		{
			double amount = ((Double) map.get("lastOrderAmount")).doubleValue();
			map.put("lastOrderAmount", new Double(amount + ((Double) results[7]).doubleValue()));
		}
		return map;
	}
	
	private ProductGroupComposite pgc;
	private boolean comparePreviousYear = false;
	private boolean noEmptyLines = false;
}
