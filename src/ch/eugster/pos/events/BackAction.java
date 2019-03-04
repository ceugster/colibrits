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

import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.db.Key;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BackAction extends Action implements ModeChangeRequest
{
	private static final long serialVersionUID = 0l;
	
	/**
	 * @param context
	 * @param key
	 */
	public BackAction(UserPanel context, Key key)
	{
		super(context, key);
		this.init(key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 */
	public BackAction(UserPanel context, Key key, String name)
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
	public BackAction(UserPanel context, Key key, String name, Icon icon)
	{
		super(context, key, name, icon);
		this.init(key);
	}
	
	private void init(Key key)
	{
		this.addModeChangeListener(this.context);
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
		return this.stateChangeListeners.add(l);
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
		return this.stateChangeListeners.remove(l);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		ModeChangeEvent event = new ModeChangeEvent(UserPanel.CONTEXT_MODE_PREVIOUS);
		this.fireModeChangeEvent(event);
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
		ModeChangeListener[] l = (ModeChangeListener[]) this.stateChangeListeners.toArray(new ModeChangeListener[0]);
		for (int i = 0; i < l.length; i++)
		{
			l[i].modeChangePerformed(e);
		}
		
	}
	
	private ArrayList stateChangeListeners = new ArrayList();
}
