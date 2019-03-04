/*
 * Created on 06.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.events;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.db.Key;
import ch.eugster.pos.db.Receipt;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RemoveReceiptAction extends ModeDependendAction
{
	
	public static final long serialVersionUID = 0l;
	
	/**
	 * @param context
	 * @param key
	 */
	public RemoveReceiptAction(UserPanel context, Key key)
	{
		super(context, key);
		this.init(key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 */
	public RemoveReceiptAction(UserPanel context, Key key, String name)
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
	public RemoveReceiptAction(UserPanel context, Key key, String name, Icon icon)
	{
		super(context, key, name, icon);
		this.init(key);
	}
	
	private void init(Key key)
	{
	}
	
	public void actionPerformed(ActionEvent e)
	{
		
	}
	
	public void modeChangePerformed(ModeChangeEvent e)
	{
		super.modeChangePerformed(e);
		if (this.context.getReceiptModel().getState() == Receipt.RECEIPT_STATE_SERIALIZED)
		{
			this.putValue("enabled", new Boolean(true)); //$NON-NLS-1$
		}
		else
		{
			this.putValue("enabled", new Boolean(false)); //$NON-NLS-1$
		}
	}
}
