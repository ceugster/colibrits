/*
 * Created on 22.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.db.Table;
import ch.eugster.pos.db.Tax;
import ch.eugster.pos.galileo.wgserve.Iwgserve;
import ch.eugster.pos.galileo.wgserve.wgserve;
import ch.eugster.pos.util.Config;

import com.ibm.bridge2java.ComException;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class GalileoProductGroupServer
{
	
	private static String[] convertCodeStringToArray(String codes)
	{
		ArrayList list = new ArrayList();
		String delimiter = "|"; //$NON-NLS-1$
		StringTokenizer st = new StringTokenizer(codes, delimiter);
		while (st.hasMoreTokens())
		{
			list.add(st.nextToken(delimiter));
		}
		return (String[]) list.toArray(new String[0]);
	}
	
	private static boolean storeProductGroup(ProductGroup productGroup, String code, String name, String account)
	{
		boolean ok = true;
		if (productGroup.getId() == null || productGroup.getId().equals(Table.ZERO_VALUE))
		{
			productGroup.deleted = false;
			productGroup.isDefault = false;
			productGroup.galileoId = code;
			productGroup.type = ProductGroup.TYPE_INCOME;
			productGroup.optCodeProposal = Config.getInstance().getDefaultOption();
			productGroup.priceProposal = 0d;
			productGroup.quantityProposal = Config.getInstance().getDefaultQuantity();
			productGroup.shortname = productGroup.name;
			productGroup.setDefaultTax(Tax.selectByCode("UR", true)); //$NON-NLS-1$
		}
		else
		{
		}
		productGroup.name = name;
		productGroup.shortname = name;
		productGroup.account = account;
		productGroup.modified = true;
		
		DBResult result = productGroup.store();
		if (result.getErrorCode() != 0)
		{
			result.log();
			ok = false;
		}
		return ok;
	}
	
	public static boolean copyProductGroups()
	{
		boolean result = true;
		try
		{
			Iwgserve srv = new wgserve();
			if (((Boolean) srv.do_open(new Object[]
			{ Config.getInstance().getGalileoPath() })).booleanValue())
			{
				result = ((Boolean) srv.do_getwglist()).booleanValue();
				if (result)
				{
					Object obj2 = srv.getWGLIST();
					
					String[] codes = GalileoProductGroupServer.convertCodeStringToArray((String) obj2);
					for (int i = 0; i < codes.length; i++)
					{
						if (((Boolean) srv.do_getwg(new Object[]
						{ codes[i] })).booleanValue())
						{
							ProductGroup group = ProductGroup.selectByGalileoId(codes[i], false);
							String name = (String) srv.getWGTEXT();
							String account = (String) srv.getKONTO();
							result = result ? GalileoProductGroupServer.storeProductGroup(group, codes[i], name,
											account) : false;
							if (Database.getCurrent().equals(Database.getStandard()))
							{
								srv.do_setbestaetigt(new Object[]
								{ codes[i] });
							}
						}
					}
				}
				srv.do_close();
			}
			srv.Destroy();
			srv = null;
		}
		catch (ComException e)
		{
			result = false;
		}
		catch (IOException e)
		{
			result = false;
		}
		finally
		{
		}
		return result;
	}
	
	public static boolean updateProductGroups()
	{
		boolean result = true;
		Iwgserve srv = null;
		try
		{
			if (ProductServer.isUsed())
			{
				srv = new wgserve();
				if (((Boolean) srv.do_open(new Object[]
				{ Config.getInstance().getGalileoPath() })).booleanValue())
				{
					result = ((Boolean) srv.do_getchangedwglist()).booleanValue();
					if (result)
					{
						Object obj2 = srv.getWGLIST();
						
						String[] codes = GalileoProductGroupServer.convertCodeStringToArray((String) obj2);
						for (int i = 0; i < codes.length; i++)
						{
							Boolean found = (Boolean) srv.do_getwg(new Object[]
							{ codes[i] });
							if (found.booleanValue())
							{
								ProductGroup group = ProductGroup.selectByGalileoId(codes[i], false);
								String name = (String) srv.getWGTEXT();
								String account = (String) srv.getKONTO();
								result = result ? GalileoProductGroupServer.storeProductGroup(group, codes[i], name,
												account) : false;
								if (Database.getCurrent().equals(Database.getStandard()))
								{
									srv.do_setbestaetigt(new Object[]
									{ codes[i] });
								}
							}
						}
					}
					srv.do_close();
				}
				srv.Destroy();
				srv = null;
			}
		}
		catch (ComException e)
		{
			result = false;
		}
		catch (IOException e)
		{
			result = false;
		}
		catch (java.lang.UnsatisfiedLinkError e)
		{
			result = false;
			e.printStackTrace();
		}
		finally
		{
			if (srv != null)
			{
				try
				{
					srv.do_close();
					srv = null;
				}
				catch (IOException ioe)
				{
					result = false;
				}
			}
		}
		return result;
	}
	
	public static boolean galileoIdExists(String galileoId)
	{
		boolean result = true;
		Iwgserve srv = null;
		try
		{
			if (ProductServer.isUsed())
			{
				srv = new wgserve();
				if (((Boolean) srv.do_open(new Object[]
				{ Config.getInstance().getGalileoPath() })).booleanValue())
				{
					srv.do_getwg(new Object[]
					{ "XXX" });
					srv.do_getwg(new Object[]
					{ galileoId });
					result = ((Boolean) srv.getGEFUNDEN()).booleanValue();
					srv.do_close();
				}
				srv.Destroy();
				srv = null;
			}
		}
		catch (ComException e)
		{
			result = false;
		}
		catch (IOException e)
		{
			result = false;
		}
		catch (java.lang.UnsatisfiedLinkError e)
		{
			result = false;
			e.printStackTrace();
		}
		finally
		{
			if (srv != null)
			{
				try
				{
					srv.do_close();
					srv = null;
				}
				catch (IOException ioe)
				{
					result = false;
				}
			}
		}
		return result;
	}
}