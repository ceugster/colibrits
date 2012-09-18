/*
 * Created on 26.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.events;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import ch.eugster.pos.client.gui.Frame;
import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.db.Key;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DiscountAction extends PositionChangeAction
{
	private static final long serialVersionUID = 0l;
	
	/**
	 * @param context
	 * @param key
	 */
	public DiscountAction(UserPanel context, Key key)
	{
		super(context, key);
		this.init(key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 */
	public DiscountAction(UserPanel context, Key key, String name)
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
	public DiscountAction(UserPanel context, Key key, String name, Icon icon)
	{
		super(context, key, name, icon);
		this.init(key);
	}
	
	/**
	 * @param key
	 */
	private void init(Key key)
	{
		this.putActionType(Action.POS_ACTION_SET_DISCOUNT);
		this.addPosEventListener(this.context.getReceiptModel().getPositionTableModel());
	}
	
	/**
	 * @param actionEvent
	 */
	public void actionPerformed(ActionEvent actionEvent)
	{
		if (this.context.getNumericBlock().readDiscount() > 1d)
		{
			MessageDialog.showInformation(Frame.getMainFrame(), "Falsche Eingabe", "Der eingegebene Rabattsatz ist ungültig.", 0);
			this.context.getNumericBlock().moveDiscount();
			return;
		}
		
		double d = this.context.getNumericBlock().moveDiscount();
		this.putValue(Action.POS_KEY_DISCOUNT, new Double(d));
		super.actionPerformed(actionEvent);
	}
}
