/*
 * Created on 02.09.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.events;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import ch.eugster.pos.Messages;
import ch.eugster.pos.client.gui.Frame;
import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.Key;
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.User;
import ch.eugster.pos.util.NumberUtility;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class SalesAction extends ModeDependendAction implements IFailOverState
{
	
	public static final long serialVersionUID = 0l;
	
	/**
	 * @param context
	 * @param key
	 */
	public SalesAction(UserPanel context, Key key)
	{
		super(context, key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 */
	public SalesAction(UserPanel context, Key key, String name)
	{
		super(context, key, name);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 * @param icon
	 */
	public SalesAction(UserPanel context, Key key, String name, Icon icon)
	{
		super(context, key, name, icon);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (Database.getCurrent().equals(Database.getTemporary()))
		{
			MessageDialog.showSimpleDialog(Frame.getMainFrame(), "Funktion nicht aktiv", "Diese Funktion ist im Failover Modus nicht aktiv.", MessageDialog.TYPE_INFORMATION);
		}
		else
		{
			String value = NumberUtility.formatDefaultCurrency(Position.selectSales(), true, true);
			MessageDialog.showInformation(Frame.getMainFrame(), Messages.getString("SalesAction.Umsatz_1"), //$NON-NLS-1$
							value, 0);
			super.actionPerformed(e);
		}
	}
	
	public void modeChangePerformed(ModeChangeEvent e)
	{
		super.modeChangePerformed(e);
		
		if (this.getValue("enabled").equals(new Boolean(true))) //$NON-NLS-1$
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
				if (this.context.getUser().status == User.USER_STATE_ADMINISTRATOR)
				{
					this.putValue("enabled", new Boolean(true)); //$NON-NLS-1$
				}
				else if (this.context.getUser().status == User.USER_STATE_MANAGER)
				{
					this.putValue("enabled", new Boolean(true)); //$NON-NLS-1$
				}
				else
				{
					this.putValue("enabled", new Boolean(false)); //$NON-NLS-1$
				}
			}
		}
	}
}
