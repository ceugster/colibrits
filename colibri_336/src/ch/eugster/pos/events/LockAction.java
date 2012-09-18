/*
 * Created on 27.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.events;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;

import ch.eugster.pos.Messages;
import ch.eugster.pos.client.App;
import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.db.Key;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LockAction extends ModeChangeAction
{
	
	private static final long serialVersionUID = 0l;
	
	/**
	 * @param context
	 * @param key
	 */
	public LockAction(UserPanel context, Key key)
	{
		super(context, key);
		this.init(key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 */
	public LockAction(UserPanel context, Key key, String name)
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
	public LockAction(UserPanel context, Key key, String name, Icon icon)
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
		if (this.context.getMode().equals(UserPanel.CONTEXT_MODE_LOCK))
		{
			Long posLogin = this.context.getNumericBlock().getPosLogin();
			if (posLogin.equals(this.context.getUser().posLogin))
			{
				this.fireModeChangeEvent(new ModeChangeEvent(UserPanel.CONTEXT_MODE_PREVIOUS));
			}
			else
			{
				if (posLogin.equals(new Integer(0)))
				{
					MessageDialog.showInformation(App.getApp().getFrame(), Messages.getString("LockAction.Eingabefehler_2"), Messages.getString("LockAction.Geben_Sie_Ihren_Anmeldecode_ein._1"), 0);
				}
				else
				{
					MessageDialog.showInformation(App.getApp().getFrame(), Messages.getString("LockAction.Eingabefehler_4"), Messages
									.getString("LockAction.Der_eingegebene_Code_ist_falsch._Geben_Sie_Ihren_Anmeldecode_ein._3"), 0);
				}
			}
		}
		else
		{
			this.fireModeChangeEvent(new ModeChangeEvent(UserPanel.CONTEXT_MODE_LOCK));
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
		
		if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_LOCK))
		{
			this.putValue(Action.NAME, Messages.getString("LockAction.Entsperren_8")); //$NON-NLS-1$
		}
		else
		{
			this.putValue(Action.NAME, Messages.getString("LockAction.Sperren_9")); //$NON-NLS-1$
		}
	}
	
}
