/*
 * Created on 27.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.events;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import ch.eugster.pos.client.gui.Frame;
import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.Key;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class UpdateCustomerAccountAction extends PositionChangeAction implements CustomerChangeListener, IFailOverState
{
	
	public static final long serialVersionUID = 0l;
	
	/**
	 * @param context
	 * @param key
	 */
	public UpdateCustomerAccountAction(UserPanel context, Key key)
	{
		super(context, key);
		this.init(key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 */
	public UpdateCustomerAccountAction(UserPanel context, Key key, String name)
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
	public UpdateCustomerAccountAction(UserPanel context, Key key, String name, Icon icon)
	{
		super(context, key, name, icon);
		this.init(key);
	}
	
	private void init(Key key)
	{
		this.context.getReceiptModel().addCustomerChangeListener(this);
	}
	
	public void actionPerformed(ActionEvent event)
	{
		if (Database.getCurrent().equals(Database.getTemporary()))
		{
			MessageDialog.showSimpleDialog(Frame.getMainFrame(), "Funktion nicht aktiv", "Diese Funktion ist im Failover Modus nicht aktiv.",
							MessageDialog.TYPE_INFORMATION);
		}
		else
		{
			super.actionPerformed(event);
		}
	}
	
	public void customerChanged(CustomerChangeEvent event)
	{
		this.putValue("enabled", new Boolean(this.context.getReceiptModel().getReceipt().getCustomerId().length() > 0)); //$NON-NLS-1$
	}
	
	public void modeChangePerformed(ModeChangeEvent e)
	{
		super.modeChangePerformed(e);
		
		if (this.getValue("enabled").equals(new Boolean(true)))
		{
			if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_NO_CHANGE))
			{
				return;
			}
			else if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_LOCK))
			{
				this.putValue("enabled", new Boolean(false)); //$NON-NLS-1$
			}
			else if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_PARKED_RECEIPT_LIST))
			{
				this.putValue("enabled", new Boolean(false)); //$NON-NLS-1$
			}
			else if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_CURRENT_RECEIPT_LIST))
			{
				this.putValue("enabled", new Boolean(false)); //$NON-NLS-1$
			}
			else
			{
				if (UserPanel.getCurrent() != null)
				{
					this.putValue("enabled", new Boolean(this.context.getReceiptModel().getReceipt().getCustomerId().length() > 0)); //$NON-NLS-1$
				}
				else
				{
					this.putValue("enabled", new Boolean(false)); //$NON-NLS-1$
				}
			}
		}
	}
}
