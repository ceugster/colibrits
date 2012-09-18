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
import ch.eugster.pos.db.Key;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ClearAction extends ModeDependendAction
{
	private static final long serialVersionUID = 0l;
	
	/**
	 * @param context
	 * @param key
	 */
	public ClearAction(UserPanel context, Key key)
	{
		super(context, key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 */
	public ClearAction(UserPanel context, Key key, String name)
	{
		super(context, key, name);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 * @param icon
	 */
	public ClearAction(UserPanel context, Key key, String name, Icon icon)
	{
		super(context, key, name, icon);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (this.context.getMode().equals(UserPanel.CONTEXT_MODE_PAY))
		{
			this.addPosEventListener(this.context.getReceiptModel().getPaymentTableModel());
			this.addPosEventListener(this.context.getReceiptModel().getPaymentModel());
		}
		else if (this.context.getMode().equals(UserPanel.CONTEXT_MODE_POS))
		{
			this.addPosEventListener(this.context.getReceiptModel().getPositionTableModel());
			this.addPosEventListener(this.context.getReceiptModel().getPositionModel());
		}
		
		super.actionPerformed(e);
		
		if (this.context.getMode().equals(UserPanel.CONTEXT_MODE_PAY))
		{
			this.removePosEventListener(this.context.getReceiptModel().getPaymentModel());
			this.removePosEventListener(this.context.getReceiptModel().getPaymentTableModel());
		}
		else if (this.context.getMode().equals(UserPanel.CONTEXT_MODE_POS))
		{
			this.removePosEventListener(this.context.getReceiptModel().getPositionModel());
			this.removePosEventListener(this.context.getReceiptModel().getPositionTableModel());
		}
	}
	
	public void modeChangePerformed(ModeChangeEvent e)
	{
		if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_PARKED_RECEIPT_LIST))
		{
			this.putValue("enabled", new Boolean(false)); //$NON-NLS-1$
		}
		else if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_CURRENT_RECEIPT_LIST))
		{
			this.putValue("enabled", new Boolean(false)); //$NON-NLS-1$
		}
		else
		{
			this.putValue("enabled", new Boolean(true)); //$NON-NLS-1$
		}
	}
	
}
