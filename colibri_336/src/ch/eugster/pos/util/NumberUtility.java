/*
 * Created on 30.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.util;

import java.text.NumberFormat;
import java.util.Currency;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class NumberUtility
{
	
	/**
	 * 
	 */
	public NumberUtility()
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
			i = defaultValue;
		}
		return i;
	}
	
	public static long parseLong(long defaultValue, String s)
	{
		long i = 0;
		try
		{
			i = Long.parseLong(s);
		}
		catch (NumberFormatException e)
		{
			i = defaultValue;
		}
		return i;
	}
	
	public static float parseFloat(float defaultValue, String s)
	{
		float i = 0f;
		try
		{
			i = Float.parseFloat(s);
		}
		catch (NumberFormatException e)
		{
			i = defaultValue;
		}
		return i;
	}
	
	public static double parseDouble(double defaultValue, String s)
	{
		double i = 0d;
		try
		{
			i = Double.parseDouble(s);
		}
		catch (NumberFormatException e)
		{
			i = defaultValue;
		}
		return i;
	}
	
	public static double round(double amount, double factor)
	{
		if (factor == 0d)
		{
			factor = 0.01d;
		}
		
		double f = Math.round(1 / factor);
		double g = amount * f;
		double h = g + 0.51d;
		double i = Math.floor(h);
		return i / f;
	}
	
	public static double round(double amount, int fractionDigits)
	{
		double f = new Integer(fractionDigits).doubleValue();
		double power = Math.pow(10d, f);
		double b = amount * power;
		double c = Math.round(b);
		double d = c / power;
		return d;
	}
	
	public static String formatDefaultCurrency(double amount, boolean useGrouping, boolean useCurrency)
	{
		NumberFormat currency = useCurrency ? NumberFormat.getCurrencyInstance() : NumberFormat.getNumberInstance();
		currency.setGroupingUsed(useGrouping);
		currency.setMinimumFractionDigits(NumberUtility.getDefaultCurrency().getDefaultFractionDigits());
		currency.setMaximumFractionDigits(NumberUtility.getDefaultCurrency().getDefaultFractionDigits());
		currency.setCurrency(NumberUtility.getDefaultCurrency());
		return currency.format(amount);
	}
	
	public static String formatCurrency(NumberFormat nf, Currency currency, double amount, boolean useGrouping)
	{
		nf.setGroupingUsed(useGrouping);
		nf.setMinimumFractionDigits(currency.getDefaultFractionDigits());
		nf.setMaximumFractionDigits(currency.getDefaultFractionDigits());
		nf.setCurrency(currency);
		return nf.format(amount);
	}
	
	public static String formatDouble(double amount, int minimumFractionDigits, int maximumFractionDigits, boolean useGrouping)
	{
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setGroupingUsed(useGrouping);
		nf.setMinimumFractionDigits(minimumFractionDigits);
		nf.setMaximumFractionDigits(maximumFractionDigits);
		return nf.format(amount);
	}
	
	public static String formatPercent(double number, int minimumFractionDigits, int maximumFractionDigits)
	{
		NumberFormat nf = NumberFormat.getPercentInstance();
		nf.setMinimumFractionDigits(minimumFractionDigits);
		nf.setMaximumFractionDigits(maximumFractionDigits);
		return nf.format(number);
	}
	
	public static String formatInteger(int number, boolean useGrouping)
	{
		NumberFormat nf = NumberFormat.getIntegerInstance();
		nf.setGroupingUsed(useGrouping);
		return nf.format(number);
	}
	
	public static Currency getDefaultCurrency()
	{
		if (NumberUtility.defaultCurrency == null)
		{
			
			NumberUtility.defaultCurrency = Currency.getInstance(Config.getInstance().getDefaultLocale());
		}
		return NumberUtility.defaultCurrency;
	}
	
	private static Currency defaultCurrency = null;
}
