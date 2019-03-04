/*
 * Created on 20.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.client.gui;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import ch.eugster.pos.db.Database;
import ch.eugster.pos.events.DatabaseEvent;
import ch.eugster.pos.events.DatabaseListener;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PosButton extends JButton implements DatabaseListener, KeyListener, PropertyChangeListener
{
	private static final long serialVersionUID = 0l;
	
	private Color failoverBackgroundColor;
	
	/**
	 * 
	 */
	public PosButton(UserPanel context)
	{
		super();
		this.init(context);
	}
	
	/**
	 * @param icon
	 */
	public PosButton(UserPanel context, Icon icon)
	{
		super(icon);
		this.init(context);
	}
	
	/**
	 * @param text
	 */
	public PosButton(UserPanel context, String text)
	{
		super(text);
		this.init(context);
	}
	
	/**
	 * @param a
	 */
	public PosButton(UserPanel context, Action a)
	{
		super(a);
		this.init(context);
	}
	
	/**
	 * @param text
	 * @param icon
	 */
	public PosButton(UserPanel context, String text, Icon icon)
	{
		super(text, icon);
		this.init(context);
	}
	
	private void init(UserPanel context)
	{
		this.addKeyListener(context);
	}
	
	public void keyPressed(KeyEvent e)
	{
		KeyListener[] l = this.getKeyListeners();
		for (int i = 0; i < l.length; i++)
		{
			l[i].keyPressed(e);
		}
	}
	
	public void keyTyped(KeyEvent e)
	{
	}
	
	public void keyReleased(KeyEvent e)
	{
	}
	
	public void propertyChange(java.beans.PropertyChangeEvent e)
	{
		Color bgcolor = (Color) this.getAction().getValue("bgcolor"); //$NON-NLS-1$
		if (bgcolor != null)
		{
			this.setBackground(bgcolor);
		}
		if (e.getPropertyName().equals("enabled")) { //$NON-NLS-1$
			if (this.getText().startsWith("<HTML>")) { //$NON-NLS-1$
				if (e.getNewValue().equals(new Boolean(true)))
				{
					this.setForeground(Color.black);
				}
				else
				{
					this.setForeground(Color.gray);
				}
			}
		}
	}
	
	public void databaseErrorOccured(DatabaseEvent e)
	{
		if (e.newConnection.equals(Database.getStandard()))
		{
			this.setBackground((Color) this.getAction().getValue("bgcolor"));
		}
		else
		{
			this.setBackground(this.failoverBackgroundColor);
		}
	}
	
	/**
	 * @return
	 */
	public Color getFailoverBackgroundColor()
	{
		return this.failoverBackgroundColor;
	}
	
	/**
	 * @param color
	 */
	public void setFailoverBackgroundColor(Color color)
	{
		this.failoverBackgroundColor = color;
	}
	
}
