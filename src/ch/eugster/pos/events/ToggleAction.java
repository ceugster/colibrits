/*
 * Created on 04.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.events;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;

import ch.eugster.pos.Messages;
import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.db.Key;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ToggleAction extends ModeChangeAction implements ReceiptChangeListener
{
	
	public static final long serialVersionUID = 0l;
	
	/**
	 * @param context
	 * @param key
	 */
	public ToggleAction(UserPanel context, Key key)
	{
		super(context, key);
		this.init(key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 */
	public ToggleAction(UserPanel context, Key key, String name)
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
	public ToggleAction(UserPanel context, Key key, String name, Icon icon)
	{
		super(context, key, name, icon);
		this.init(key);
	}
	
	private void init(Key key)
	{
		this.context.getReceiptModel().getPositionTableModel().addReceiptChangeListener(this);
	}
	
	public void actionPerformed(ActionEvent a)
	{
		if (this.context.getMode().equals(UserPanel.CONTEXT_MODE_PAY))
		{
			this.fireModeChangeEvent(new ModeChangeEvent(UserPanel.CONTEXT_MODE_POS));
		}
		else if (this.context.getMode().equals(UserPanel.CONTEXT_MODE_POS))
		{
			this.fireModeChangeEvent(new ModeChangeEvent(UserPanel.CONTEXT_MODE_PAY));
		}
	}
	
	public void modeChangePerformed(ModeChangeEvent e)
	{
		super.modeChangePerformed(e);
		if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_PAY))
		{
			// putValue("enabled", new
			// Boolean(context.getReceiptModel().getPositionTableModel
			// ().getRowCount() != 0));
			this.putValue(Action.NAME, Messages.getString("ToggleAction.Positionen_1")); //$NON-NLS-1$
			this.putValue("bgcolor", new Color(this.key.bgRed, this.key.bgGreen, this.key.bgBlue)); //$NON-NLS-1$
		}
		if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_POS))
		{
			// putValue("enabled", new
			// Boolean(context.getReceiptModel().getPositionTableModel
			// ().getRowCount() != 0));
			this.putValue(Action.NAME, Messages.getString("ToggleAction.Zahlungen_3")); //$NON-NLS-1$
		}
	}
	
	public void receiptChangePerformed(ReceiptChangeEvent e)
	{
		// putValue("enabled", new
		// Boolean(context.getReceiptModel().getPositionTableModel
		// ().getRowCount() != 0));
	}
	
}
