/*
 * Created on 12.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.events;

import javax.swing.Icon;

import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.db.Key;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ReceiptTableListAction extends Action
{
	
	public static final long serialVersionUID = 0l;
	
	/**
	 * @param context
	 * @param key
	 */
	public ReceiptTableListAction(UserPanel context, Key key)
	{
		super(context, key);
		this.init();
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 */
	public ReceiptTableListAction(UserPanel context, Key key, String name)
	{
		super(context, key, name);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 * @param icon
	 */
	public ReceiptTableListAction(UserPanel context, Key key, String name, Icon icon)
	{
		super(context, key, name, icon);
	}
	
	private void init()
	{
	}
	
}
