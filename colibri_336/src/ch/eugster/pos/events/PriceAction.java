/*
 * Created on 26.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.events;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.db.Key;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PriceAction extends PositionChangeAction
{
	
	public static final long serialVersionUID = 0l;
	
	/**
	 * @param context
	 * @param key
	 */
	public PriceAction(UserPanel context, Key key)
	{
		super(context, key);
		this.init(key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 */
	public PriceAction(UserPanel context, Key key, String name)
	{
		super(context, key, name);
		this.init(key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 * @param icon
	 */
	public PriceAction(UserPanel context, Key key, String name, Icon icon)
	{
		super(context, key, name, icon);
		this.init(key);
	}
	
	/**
	 * @param key
	 */
	private void init(Key key)
	{
	}
	
	/**
	 * @param actionEvent
	 */
	public void actionPerformed(ActionEvent actionEvent)
	{
		double d = this.context.getNumericBlock().moveAmount();
		this.putValue(Action.POS_KEY_PRICE, new Double(d));
		super.actionPerformed(actionEvent);
	}
	
}
