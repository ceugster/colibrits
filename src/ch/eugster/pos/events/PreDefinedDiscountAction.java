/*
 * Created on 01.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.events;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.db.CustomKey;
import ch.eugster.pos.db.Key;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PreDefinedDiscountAction extends DiscountAction
{
	
	public static final long serialVersionUID = 0l;
	
	/**
	 * @param context
	 * @param key
	 */
	public PreDefinedDiscountAction(UserPanel context, Key key)
	{
		super(context, key);
		this.init(key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 */
	public PreDefinedDiscountAction(UserPanel context, Key key, String name)
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
	public PreDefinedDiscountAction(UserPanel context, Key key, String name, Icon icon)
	{
		super(context, key, name, icon);
		this.init(key);
	}
	
	private void init(Key key)
	{
		if (key instanceof CustomKey)
		{
			Double value = ((CustomKey) key).value;
			this.putValue(Action.POS_KEY_DISCOUNT, value);
		}
		this.addPosEventListener(this.context.getReceiptModel());
	}
	
	/**
	 * @param actionEvent
	 */
	public void actionPerformed(ActionEvent actionEvent)
	{
		if (!this.getActionType().equals(Action.POS_ACTION_NO_ACTION))
		{
			PosEvent p = new PosEvent(this);
			PosEventListener[] l = (PosEventListener[]) this.posEventListeners.toArray(new PosEventListener[0]);
			for (int i = 0; i < l.length; i++)
			{
				l[i].posEventPerformed(p);
			}
		}
	}
}
