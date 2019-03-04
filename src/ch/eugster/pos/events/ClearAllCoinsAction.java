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
public class ClearAllCoinsAction extends ModeDependendAction
{
	private static final long serialVersionUID = 0l;
	
	/**
	 * @param context
	 * @param key
	 */
	public ClearAllCoinsAction(UserPanel context, Key key)
	{
		super(context, key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 */
	public ClearAllCoinsAction(UserPanel context, Key key, String name)
	{
		super(context, key, name);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 * @param icon
	 */
	public ClearAllCoinsAction(UserPanel context, Key key, String name, Icon icon)
	{
		super(context, key, name, icon);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		this.firePropertyChange("clear", null, null);
	}
	
	public void modeChangePerformed(ModeChangeEvent e)
	{
		this.putValue("enabled", new Boolean(true)); //$NON-NLS-1$
	}
	
}
