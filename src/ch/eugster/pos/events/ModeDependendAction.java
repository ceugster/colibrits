/*
 * Created on 07.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.events;

import javax.swing.Icon;

import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.db.Key;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ModeDependendAction extends Action implements ModeChangeListener
{
	public static final long serialVersionUID = 0l;
	
	/**
	 * @param context
	 * @param key
	 */
	public ModeDependendAction(UserPanel context, Key key)
	{
		super(context, key);
		this.init(key);
	}
	
	/**
	 * @param name
	 */
	public ModeDependendAction(UserPanel context, Key key, String name)
	{
		super(context, key, name);
		this.init(key);
	}
	
	/**
	 * @param name
	 * @param icon
	 */
	public ModeDependendAction(UserPanel context, Key key, String name, Icon icon)
	{
		super(context, key, name, icon);
		this.init(key);
	}
	
	private void init(Key key)
	{
		this.context.addModeChangeListener(this);
	}
	
	public void modeChangePerformed(ModeChangeEvent e)
	{
		if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_NO_CHANGE))
		{
			return;
		}
		
		if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_LOCK))
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
			this.putValue("enabled", new Boolean(true)); //$NON-NLS-1$
		}
	}
	
}
