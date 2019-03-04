/*
 * Created on 07.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.events;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import ch.eugster.pos.Messages;
import ch.eugster.pos.client.gui.Frame;
import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.client.model.ReceiptChildTableModel;
import ch.eugster.pos.db.Key;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ShowReceiptListAction extends ModeChangeAction
{
	
	public static final long serialVersionUID = 0l;
	
	/**
	 * @param context
	 * @param key
	 */
	public ShowReceiptListAction(UserPanel context, Key key)
	{
		super(context, key);
		this.init(key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 */
	public ShowReceiptListAction(UserPanel context, Key key, String name)
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
	public ShowReceiptListAction(UserPanel context, Key key, String name, Icon icon)
	{
		super(context, key, name, icon);
		this.init(key);
	}
	
	private void init(Key key)
	{
		// context.getReceiptModel().addStateChangeListener(this);
		// context.getReceiptModel().getPositionTableModel().
		// addStateChangeListener(this);
		this.addPosEventListener(this.context.getChildrenBlock().getCurrentReceiptTableBlock());
	}
	
	public void actionPerformed(ActionEvent e)
	{
		int ok = MessageDialog.BUTTON_YES;
		if (this.context.getReceiptModel().getPositionTableModel().getState() != ReceiptChildTableModel.LIST_STATE_EMPTY
						|| this.context.getReceiptModel().getPositionTableModel().getState() != ReceiptChildTableModel.LIST_STATE_EMPTY)
		{
			ok = MessageDialog.showSimpleDialog(Frame.getMainFrame(), Messages.getString("ShowReceiptListAction.Belegliste_2"), Messages
							.getString("ShowReceiptListAction.Der_aktuelle_Beleg_wurde_noch_nicht_gespeichert._nWollen_Sie_die__u00C4nderungen_verwerfen_und_die_Belegliste_einsehen__1"), 1);
		}
		if (ok == MessageDialog.BUTTON_YES)
		{
			super.actionPerformed(e);
			this.fireModeChangeEvent(new ModeChangeEvent(UserPanel.CONTEXT_MODE_CURRENT_RECEIPT_LIST));
		}
	}
	
}
