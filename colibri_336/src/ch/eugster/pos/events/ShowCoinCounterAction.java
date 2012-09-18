/*
 * Created on 05.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.events;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.Icon;

import ch.eugster.pos.client.gui.Frame;
import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.Key;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ShowCoinCounterAction extends ModeDependendAction implements ModeChangeRequest, IFailOverState
{
	
	public static final long serialVersionUID = 0l;
	
	/**
	 * @param context
	 * @param key
	 */
	public ShowCoinCounterAction(UserPanel context, Key key)
	{
		super(context, key);
		this.init(key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 */
	public ShowCoinCounterAction(UserPanel context, Key key, String name)
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
	public ShowCoinCounterAction(UserPanel context, Key key, String name, Icon icon)
	{
		super(context, key, name, icon);
		this.init(key);
	}
	
	public void init(Key key)
	{
		this.addModeChangeListener(this.context);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.client.event.StateChangeRequest#fireStateChangeEvent(ch
	 * .eugster.pos.client.event.StateChangeEvent)
	 */
	public void fireModeChangeEvent(ModeChangeEvent e)
	{
		ModeChangeListener[] listeners = (ModeChangeListener[]) this.modeChangeListeners.toArray(new ModeChangeListener[0]);
		for (int i = 0; i < listeners.length; i++)
		{
			listeners[i].modeChangePerformed(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.client.event.StateChangeRequest#addStateChangeListener
	 * (ch.eugster.pos.client.event.StateChangeListener)
	 */
	public boolean addModeChangeListener(ModeChangeListener l)
	{
		return this.modeChangeListeners.add(l);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.client.event.StateChangeRequest#removeStateChangeListener
	 * (ch.eugster.pos.client.event.StateChangeListener)
	 */
	public boolean removeModeChangeListener(ModeChangeListener l)
	{
		return this.modeChangeListeners.remove(l);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (Database.getCurrent().equals(Database.getTemporary()))
		{
			MessageDialog.showSimpleDialog(Frame.getMainFrame(), "Funktion nicht aktiv", "Diese Funktion ist im Failover Modus nicht aktiv.", MessageDialog.TYPE_INFORMATION);
		}
		else
		{
			ModeChangeEvent event = new ModeChangeEvent(UserPanel.CONTEXT_MODE_SETTLEMENT);
			this.fireModeChangeEvent(event);
		}
	}
	
	public void modeChangePerformed(ModeChangeEvent e)
	{
		super.modeChangePerformed(e);
		if (this.getValue("enabled").equals(new Boolean(true)))
		{
			if (this.context.getUser().status > 1)
			{
				this.putValue("enabled", new Boolean(false)); //$NON-NLS-1$
			}
		}
	}
	
	private ArrayList modeChangeListeners = new ArrayList();
}
