/*
 * Created on 23.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.product;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class EAN13
{
	
	public static String getGalileoSearchValue(String ean13)
	{
		String result = null;
		// 10041: Der EAN-Code wird neu als ganzes für die
		// Abfrage in Galileo verwendet. Daher muss kein Substring mehr
		// gezogen werden
		//		if (ean13.substring(0, 3).equals("989")) { //$NON-NLS-1$
		// result = getISBN(ean13);
		// }
		// else {
		result = ean13;
		// }
		return result;
		// return ean13;
	}
	
	public static boolean isISBN(String ean13)
	{
		boolean result = false;
		if (ean13.substring(0, 3).equals(EAN13.PRE_ISBN))
		{
			result = true;
		}
		return result;
	}
	
	public static String getISBN(String ean13)
	{
		String isbn = null;
		if (ean13.substring(0, 3).equals(EAN13.PRE_ORDERED))
		{
			isbn = ISBN.computeISBN(ean13.substring(3, 12));
		}
		return isbn;
	}
	
	public static boolean isCustomerId(String ean13)
	{
		boolean result = false;
		if (ean13.substring(0, 3).equals(EAN13.PRE_CUSTOMER_ID))
		{
			result = true;
		}
		return result;
	}
	
	public static int getCustomerId(String ean13)
	{
		Integer cId = new Integer(0);
		if (ean13.substring(0, 3).equals(EAN13.PRE_CUSTOMER_ID))
		{
			try
			{
				cId = new Integer(ean13.substring(3, 12));
			}
			catch (NumberFormatException e)
			{
			}
		}
		return cId.intValue();
	}
	
	public static String getOrderId(String ean13)
	{
		String orderId = "";
		Long id = new Long(0);
		if (ean13.length() == 13 && ean13.startsWith("989"))
		{
			orderId = ean13.substring(3, 12);
			try
			{
				id = new Long(orderId);
				orderId = id.toString();
			}
			catch (NumberFormatException e)
			{
			}
		}
		return orderId;
	}
	
	public static final String PRE_ISBN = "978";
	public static final String PRE_ORDERED = "989";
	public static final String PRE_CUSTOMER_ID = "992";
}
