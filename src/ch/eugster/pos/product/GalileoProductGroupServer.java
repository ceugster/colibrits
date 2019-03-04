/*
 * Created on 22.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.product;

import java.util.ArrayList;
import java.util.StringTokenizer;

import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.db.Table;
import ch.eugster.pos.db.Tax;
import ch.eugster.pos.util.Config;

import com4j.ComException;

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
		ArrayList<String> list = new ArrayList<String>();
		String delimiter = "|"; //$NON-NLS-1$
		StringTokenizer st = new StringTokenizer(codes, delimiter);
		while (st.hasMoreTokens())
		{
			list.add(st.nextToken(delimiter));
		}
		return list.toArray(new String[0]);
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
			Iwgserve srv = WgserveFactory.createwgserve();
			if (((Boolean) srv.do_open(new Object[]
			{ Config.getInstance().getGalileoPath() })).booleanValue())
			{
				result = ((Boolean) srv.do_getwglist()).booleanValue();
				if (result)
				{
					Object obj2 = srv.do_getwglist();
					
					String[] codes = GalileoProductGroupServer.convertCodeStringToArray((String) obj2);
					for (String code : codes)
					{
						if (((Boolean) srv.do_getwg(new Object[]
						{ code })).booleanValue())
						{
							ProductGroup group = ProductGroup.selectByGalileoId(code, false);
							String name = (String) srv.wgtext();
							String account = (String) srv.konto();
							result = result ? GalileoProductGroupServer.storeProductGroup(group, code, name, account)
											: false;
							if (Database.getCurrent().equals(Database.getStandard()))
							{
								srv.do_setbestaetigt(new Object[]
								{ code });
							}
						}
					}
				}
				srv.do_close();
			}
			srv.dispose();
			srv = null;
		}
		catch (ComException e)
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
				srv = WgserveFactory.createwgserve();
				if (((Boolean) srv.do_open(new Object[]
				{ Config.getInstance().getGalileoPath() })).booleanValue())
				{
					result = ((Boolean) srv.do_getchangedwglist()).booleanValue();
					if (result)
					{
						Object obj2 = srv.wglist();
						
						String[] codes = GalileoProductGroupServer.convertCodeStringToArray((String) obj2);
						for (String code : codes)
						{
							Boolean found = (Boolean) srv.do_getwg(new Object[]
							{ code });
							if (found.booleanValue())
							{
								ProductGroup group = ProductGroup.selectByGalileoId(code, false);
								String name = (String) srv.wgtext();
								String account = (String) srv.konto();
								result = result ? GalileoProductGroupServer.storeProductGroup(group, code, name,
												account) : false;
								if (Database.getCurrent().equals(Database.getStandard()))
								{
									srv.do_setbestaetigt(new Object[]
									{ code });
								}
							}
						}
					}
					srv.do_close();
				}
				srv.dispose();
				srv = null;
			}
		}
		catch (ComException e)
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
				srv.do_close();
				srv = null;
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
				srv = WgserveFactory.createwgserve();
				if (((Boolean) srv.do_open(new Object[]
				{ Config.getInstance().getGalileoPath() })).booleanValue())
				{
					srv.do_getwg(new Object[]
					{ "XXX" });
					srv.do_getwg(new Object[]
					{ galileoId });
					result = ((Boolean) srv.gefunden()).booleanValue();
					srv.do_close();
				}
				srv.dispose();
				srv = null;
			}
		}
		catch (ComException e)
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
				srv.do_close();
				srv = null;
			}
		}
		return result;
	}
}