/*
 * Created on 08.09.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.product;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Properties;

import ch.eugster.pos.util.Config;
import ch.eugster.pos.util.Path;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class Code128 implements ICode128
{
	
	/**
	 * 
	 */
	public Code128()
	{
		super();
	}
	
	/**
	 * 
	 */
	public Code128(String code128)
	{
		super();
		this.code128 = code128;
	}
	
	public Date getDate(String code)
	{
		int day = new Integer(code.substring(this.dayPart.start, this.dayPart.end)).intValue();
		int month = new Integer(code.substring(this.monthPart.start, this.monthPart.end)).intValue();
		int year = new Integer(code.substring(this.yearPart.start, this.yearPart.end)).intValue();
		if (year < 100)
		{
			Calendar calendar = Calendar.getInstance();
			int centuryYear = calendar.get(Calendar.YEAR);
			GregorianCalendar now = new GregorianCalendar();
			year = centuryYear - now.get(GregorianCalendar.YEAR) + year;
		}
		return new GregorianCalendar(year, month, day).getTime();
	}
	
	public int getSupplier(String code)
	{
		return Integer.parseInt(code.substring(this.supplierPart.start, this.supplierPart.end));
	}
	
	public String getTax(String code)
	{
		return code.substring(this.taxPart.start, this.taxPart.end);
	}
	
	public int getQualifier(String code)
	{
		return Integer.parseInt(code.substring(this.qualifierPart.start, this.qualifierPart.end));
	}
	
	public double getOrdinalPrice(String code)
	{
		String value = code.substring(this.ordinalPricePart.start, this.ordinalPricePart.end);
		value = value.substring(0, 4) + String.valueOf(new DecimalFormatSymbols().getDecimalSeparator())
						+ value.substring(4);
		return Double.parseDouble(value);
	}
	
	public double getNetPrice(String code)
	{
		String value = code.substring(this.netPricePart.start, this.netPricePart.end);
		value = value.substring(0, 4) + String.valueOf(new DecimalFormatSymbols().getDecimalSeparator())
						+ value.substring(4);
		return Double.parseDouble(value);
	}
	
	public String getProductGroup(String code)
	{
		return code.substring(this.productGroupPart.start, this.productGroupPart.end);
	}
	
	public String getArticleCode(String code)
	{
		String articleCode = new Long(code.substring(this.articleCodePart.start, this.articleCodePart.end)).toString();
		
		int q = this.getQualifier(code);
		switch (q)
		{
			case 0:
				// Artikelnummer ist eine ISBN ohne Prüfziffer
				if (articleCode.length() == 9)
				{
					code = articleCode + ISBN.computeChecksum(articleCode);
				}
				else
				{
					code = articleCode;
				}
				break;
			case 1:
				// Artikelnummer ist eine 7-stellige BZ-Nummer
				code = articleCode;
				break;
			case 2:
				// Artikelnummer ist eine lieferanteneigene Artikelnummer
				code = articleCode;
				break;
			default:
				/*
				 * Artikelnummerdefinition unbekannt, daher so wie sie
				 * extrahiert wurde zurückgeben.
				 */
				code = articleCode;
				break;
		}
		if (q != 1)
		{
			while (code.length() < 13)
				code = "0".concat(code);
		}
		return code;
	}
	
	public boolean isUsed()
	{
		return this.use;
	}
	
	public boolean accept(String code128)
	{
		return code128.length() == this.length;
	}
	
	public boolean searchGalileo()
	{
		return this.searchGalileo;
	}
	
	public Properties getProperties()
	{
		return this.properties;
	}
	
	public static void initialize()
	{
		File folder = new File(Path.getInstance().code128Dir);
		File[] files = folder.listFiles(new FileFilter()
		{
			public boolean accept(File file)
			{
				return file.isFile();
			}
		});
		
		Properties[] properties = new Properties[files.length];
		for (int i = 0; i < files.length; i++)
		{
			properties[i] = new Properties();
			try
			{
				FileInputStream fis = new FileInputStream(files[i]);
				properties[i].load(fis);
			}
			catch (IOException e)
			{
				properties[i] = null;
			}
		}
		
		for (int i = 0; i < files.length; i++)
		{
			if (properties[i] != null)
			{
				Code128 code128 = Code128.createCode128Object(properties[i].getProperty("class"));
				code128.use = new Boolean(properties[i].getProperty("use")).booleanValue();
				// boolean search = new
				// Boolean(properties[i].getProperty("galileo")).booleanValue();
				// // 10198
				code128.searchGalileo = new Boolean(properties[i].getProperty("galileo")).booleanValue(); // 10198
				// if (search) { //10198
				if (code128.searchGalileo)
				{ // 10198
					if (!Config.getInstance().getProductServerUse())
					{
						// search = false; //10198
						code128.searchGalileo = false; // 10198
					}
				}
				if (code128.searchGalileo) Code128.galileoSearched = true;
				code128.length = new Integer(properties[i].getProperty("article.end")).intValue();
				code128.isInitialized = true;
				
				code128.dayPart = new Code128Part(Integer.valueOf(properties[i].getProperty("day.start")).intValue(), //$NON-NLS-1$
								Integer.valueOf(properties[i].getProperty("day.end")).intValue()); //$NON-NLS-1$
				code128.monthPart = new Code128Part(Integer
								.valueOf(properties[i].getProperty("month.start")).intValue(), //$NON-NLS-1$
								Integer.valueOf(properties[i].getProperty("month.end")).intValue()); //$NON-NLS-1$
				code128.yearPart = new Code128Part(Integer.valueOf(properties[i].getProperty("year.start")).intValue(), //$NON-NLS-1$
								Integer.valueOf(properties[i].getProperty("year.end")).intValue()); //$NON-NLS-1$
				code128.supplierPart = new Code128Part(Integer
								.valueOf(properties[i].getProperty("supplier.start")).intValue(), //$NON-NLS-1$
								Integer.valueOf(properties[i].getProperty("supplier.end")).intValue()); //$NON-NLS-1$
				code128.taxPart = new Code128Part(Integer.valueOf(properties[i].getProperty("tax.start")).intValue(), //$NON-NLS-1$
								Integer.valueOf(properties[i].getProperty("tax.end")).intValue()); //$NON-NLS-1$
				code128.qualifierPart = new Code128Part(Integer
								.valueOf(properties[i].getProperty("qualifier.start")).intValue(), //$NON-NLS-1$
								Integer.valueOf(properties[i].getProperty("qualifier.end")).intValue()); //$NON-NLS-1$
				code128.ordinalPricePart = new Code128Part(Integer
								.valueOf(properties[i].getProperty("price.start")).intValue(), //$NON-NLS-1$
								Integer.valueOf(properties[i].getProperty("price.end")).intValue()); //$NON-NLS-1$
				code128.netPricePart = new Code128Part(Integer
								.valueOf(properties[i].getProperty("net.start")).intValue(), //$NON-NLS-1$
								Integer.valueOf(properties[i].getProperty("net.end")).intValue()); //$NON-NLS-1$
				code128.productGroupPart = new Code128Part(Integer
								.valueOf(properties[i].getProperty("group.start")).intValue(), //$NON-NLS-1$
								Integer.valueOf(properties[i].getProperty("group.end")).intValue()); //$NON-NLS-1$
				code128.articleCodePart = new Code128Part(Integer
								.valueOf(properties[i].getProperty("article.start")).intValue(), //$NON-NLS-1$
								Integer.valueOf(properties[i].getProperty("article.end")).intValue()); //$NON-NLS-1$
				code128.length = new Integer(properties[i].getProperty("article.end")).intValue();
				
				if (code128.use) Code128.useAny = true;
				
				code128.properties = properties[i];
				Code128.codes.put(Integer.valueOf(properties[i].getProperty("article.end")), code128);
			}
		}
	}
	
	private static Code128 createCode128Object(String className)
	{
		Code128 code = null;
		try
		{
			Class a = Class.forName(className);
			Class[] params = new Class[0];
			Constructor c = a.getConstructor(params);
			Object[] p = new Object[0];
			code = (Code128) c.newInstance(p);
		}
		catch (ClassNotFoundException e)
		{
			//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
		}
		catch (IllegalAccessException e)
		{
			//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
		}
		catch (InstantiationException e)
		{
			//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
		}
		catch (NoSuchMethodException e)
		{
			//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
		}
		catch (InvocationTargetException e)
		{
			//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
		}
		return code;
	}
	
	public static Code128 getCode128(String input)
	{
		// 10050
		// Der Parameter für den gewünschten Code128 muss als
		// Integer übergeben werden.
		// return (Code128)codes.get(new Integer(input.length()).toString());
		return (Code128) Code128.codes.get(new Integer(input.length()));
	}
	
	public static boolean isAnyUsed()
	{
		return Code128.useAny;
	}
	
	public static boolean isGalileoSearched()
	{
		return Code128.galileoSearched;
	}
	
	protected String code128 = null;
	
	protected boolean use = false;
	protected boolean searchGalileo = false;
	protected boolean isInitialized = false;
	protected Properties properties = null;
	
	protected int length;
	protected Code128Part dayPart;
	protected Code128Part monthPart;
	protected Code128Part yearPart;
	protected Code128Part supplierPart;
	protected Code128Part taxPart;
	protected Code128Part qualifierPart;
	protected Code128Part ordinalPricePart;
	protected Code128Part netPricePart;
	protected Code128Part productGroupPart;
	protected Code128Part articleCodePart;
	
	protected static boolean useAny = false;
	protected static boolean galileoSearched = false;
	
	public static Hashtable codes = new Hashtable();
}
