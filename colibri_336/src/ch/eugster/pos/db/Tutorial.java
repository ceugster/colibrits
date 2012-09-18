/*
 * Created on 16.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.db;

import org.jdom.Element;

import ch.eugster.pos.util.Config;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Tutorial extends Connection
{
	
	/**
	 * 
	 */
	public Tutorial()
	{
		super();
	}
	
	/**
	 * @param Element
	 */
	public Tutorial(Element element)
	{
		super(element);
	}
	
	/**
	 * @param priority
	 * @param active
	 * @param name
	 */
	public Tutorial(String id, String name)
	{
		super(id, name);
	}
	
	protected Element getConnectionData()
	{
		return Config.getInstance().getDatabaseTutorialConnection();
	}
	
	public String getCode()
	{
		return "tut";
	}
	
}
