/*
 * Created on 08.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.client.gui;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.eugster.pos.events.ModeChangeEvent;
import ch.eugster.pos.events.ModeChangeListener;
import ch.eugster.pos.util.Config;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TabbedPane extends JTabbedPane implements ChangeListener, ModeChangeListener, KeyListener
{
	private static final long serialVersionUID = 0l;
	
	/**
	 * 
	 */
	public TabbedPane(UserPanel context)
	{
		super();
		this.init(context);
	}
	
	/**
	 * @param tabPlacement
	 */
	public TabbedPane(int tabPlacement, UserPanel context)
	{
		super(tabPlacement);
		this.init(context);
	}
	
	/**
	 * @param tabPlacement
	 * @param tabLayoutPolicy
	 */
	public TabbedPane(int tabPlacement, int tabLayoutPolicy, UserPanel context)
	{
		super(tabPlacement, tabLayoutPolicy);
		this.init(context);
	}
	
	private void init(UserPanel context)
	{
		this.context = context;
		this.context.addModeChangeListener(this);
		this.addKeyListener(context);
		this.addChangeListener(this);
		Config config = Config.getInstance();
		this.setFont(this.getFont().deriveFont(config.getFontStyle(config.getTabPanelFont()), config.getFontSize(config.getTabPanelFont())));
		this.bg = Config.getInstance().getTabPanelColorBackground();
		this.fg = Config.getInstance().getTabPanelColorForeground();
		this.bg = Config.getInstance().getTabPanelColorBackground();
	}
	
	public void modeChangePerformed(ModeChangeEvent e)
	{
		if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_LOCK))
		{
			this.setTabsState(false);
		}
		else if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_PARKED_RECEIPT_LIST))
		{
			this.setTabsState(false);
		}
		else if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_CURRENT_RECEIPT_LIST))
		{
			this.setTabsState(false);
		}
		else
		{
			this.setTabsState(true);
		}
	}
	
	public void stateChanged(ChangeEvent e)
	{
		this.setTabColors();
	}
	
	public void setTabColors()
	{
		for (int i = 0; i < this.getTabCount(); i++)
		{
			if (this.getSelectedIndex() == i)
			{
				this.setForegroundAt(i, this.fg);
			}
			else
			{
				this.setForegroundAt(i, this.bg);
			}
		}
	}
	
	protected void setTabsState(boolean enabled)
	{
		for (int i = 0; i < this.getTabCount(); i++)
		{
			this.setEnabledAt(i, enabled);
		}
	}
	
	public void keyPressed(KeyEvent e)
	{
		KeyListener[] l = (KeyListener[]) this.listenerList.getListeners(KeyListener.class);
		for (int i = 0; i < l.length; i++)
		{
			l[i].keyPressed(e);
		}
	}
	
	public void keyReleased(KeyEvent e)
	{
	}
	
	public void keyTyped(KeyEvent e)
	{
	}
	
	private UserPanel context;
	private Color fg;
	private Color bg;
}
