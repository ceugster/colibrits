/*
 * Created on 10.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.client.gui;

import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JPanel;

/**
 * @author Administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Panel extends JPanel implements ActionListener, KeyListener
{
	private static final long serialVersionUID = 0l;
	
	/**
	 * @param layout
	 * @param isDoubleBuffered
	 */
	public Panel(LayoutManager layout, boolean isDoubleBuffered, TabPanel parent)
	{
		super(layout, isDoubleBuffered);
		this.parent = parent;
	}
	
	/**
	 * @param layout
	 */
	public Panel(LayoutManager layout, TabPanel parent)
	{
		super(layout);
		this.parent = parent;
	}
	
	/**
	 * @param isDoubleBuffered
	 */
	public Panel(boolean isDoubleBuffered, TabPanel parent)
	{
		super(isDoubleBuffered);
		this.parent = parent;
	}
	
	/**
	 * 
	 */
	public Panel(TabPanel parent)
	{
		super();
		this.parent = parent;
	}
	
	public void cleanUp()
	{
	}
	
	public void actionPerformed(ActionEvent e)
	{
		this.fireActionEvent(e);
	}
	
	public void fireActionEvent(ActionEvent e)
	{
		Iterator i = this.actionListeners.iterator();
		while (i.hasNext())
		{
			((ActionListener) i.next()).actionPerformed(e);
		}
	}
	
	public void keyPressed(KeyEvent e)
	{
	}
	
	public void keyReleased(KeyEvent e)
	{
	}
	
	public void keyTyped(KeyEvent e)
	{
	}
	
	public boolean addActionListener(ActionListener listener)
	{
		return this.actionListeners.add(listener);
	}
	
	public boolean removeActionListener(ActionListener listener)
	{
		return this.actionListeners.remove(listener);
	}
	
	protected TabPanel parent;
	protected ArrayList actionListeners = new ArrayList();
}
