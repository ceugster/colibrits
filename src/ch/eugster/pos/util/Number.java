/*
 * Created on 30.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.util;

import java.text.NumberFormat;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Number
{
	
	/**
	 * 
	 */
	public Number()
	{
		super();
	}
	
	public static int parseInt(int defaultValue, String s)
	{
		int i = 0;
		try
		{
			i = Integer.parseInt(s);
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			i = defaultValue;
		}
		return i;
	}
	
	public static Double round(Double amount)
	{
		return new Double(Math.round(amount.doubleValue() / Number.factor.doubleValue()) * Number.factor.doubleValue());
	}
	
	public static Double round(Double amount, Double factor)
	{
		return new Double(Math.round(amount.doubleValue() / factor.doubleValue()) * factor.doubleValue());
	}
	
	public static double round(double amount)
	{
		return Math.round(amount / Number.factor.doubleValue()) * Number.factor.doubleValue();
	}
	
	public static double round(double amount, double factor)
	{
		return Math.round(amount / factor) * factor;
	}
	
	public static String formatCurrency(NumberFormat currency, Double amount, boolean useGrouping)
	{
		currency.setGroupingUsed(useGrouping);
		return currency.format(amount);
	}
	
	public static String formatDouble(Double amount, int minimumFractionDigits, int maximumFractionDigits,
					boolean useGrouping)
	{
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setGroupingUsed(useGrouping);
		nf.setMinimumFractionDigits(minimumFractionDigits);
		nf.setMaximumFractionDigits(maximumFractionDigits);
		return nf.format(amount);
	}
	
	public static String formatPercent(Double number, int minimumFractionDigits, int maximumFractionDigits)
	{
		NumberFormat nf = NumberFormat.getPercentInstance();
		nf.setMinimumFractionDigits(minimumFractionDigits);
		nf.setMaximumFractionDigits(maximumFractionDigits);
		return nf.format(number.doubleValue());
	}
	
	public static String formatInteger(Integer number, boolean useGrouping)
	{
		NumberFormat nf = NumberFormat.getIntegerInstance();
		nf.setGroupingUsed(useGrouping);
		return nf.format(number);
	}
	
	public static void setFactor(Double f)
	{
		Number.factor = f;
	}
	
	public static Double getFactor()
	{
		return Number.factor;
	}
	
	private static Double factor = new Double(0.05);
	
	// private static final Double DEFAULT_FACTOR = new Double(0.05);
}
