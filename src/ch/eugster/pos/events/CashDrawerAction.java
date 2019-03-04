/*
 * Created on 14.08.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.events;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import ch.eugster.pos.client.gui.TabPanel;
import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.db.Key;
import ch.eugster.pos.printing.ReceiptPrinter;

/**
 * @author Administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CashDrawerAction extends ModeDependendAction
{
	private static final long serialVersionUID = 0l;
	
	/**
	 * @param context
	 * @param key
	 */
	public CashDrawerAction(UserPanel context, Key key)
	{
		super(context, key);
		this.init(key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 */
	public CashDrawerAction(UserPanel context, Key key, String name)
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
	public CashDrawerAction(UserPanel context, Key key, String name, Icon icon)
	{
		super(context, key, name, icon);
		this.init(key);
	}
	
	private void init(Key key)
	{
		int diff = Action.POS_ACTION_CASHDRAWER_0.intValue();
		int actionType = key.actionType.intValue();
		this.putValue(Action.POS_KEY_DRAWER_NUMBER, new Integer(actionType - diff));
	}
	
	public void actionPerformed(ActionEvent e)
	{
		ReceiptPrinter rp = ((TabPanel) this.context.getParent()).getReceiptPrinter();
		rp.getPrinter().kickOutDrawer(((Integer) this.getValue(Action.POS_KEY_DRAWER_NUMBER)).intValue());
		super.actionPerformed(e);
	}
	
}
