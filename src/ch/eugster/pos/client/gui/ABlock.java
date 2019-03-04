/*
 * Created on 03.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.client.gui;

import java.awt.LayoutManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JPanel;

import ch.eugster.pos.db.CustomKey;
import ch.eugster.pos.events.ModeChangeEvent;
import ch.eugster.pos.events.ModeChangeListener;
import ch.eugster.pos.events.ModeChangeRequest;
import ch.eugster.pos.events.PosEvent;
import ch.eugster.pos.events.PosEventListener;
import ch.eugster.pos.events.ReceiptChangeEvent;
import ch.eugster.pos.events.ReceiptChangeListener;
import ch.eugster.pos.util.Config;

public abstract class ABlock extends JPanel implements ModeChangeRequest, ModeChangeListener, KeyListener, ReceiptChangeListener, PosEventListener
{
	private static final long serialVersionUID = 0l;
	
	/**
	 * @param layout
	 * @param isDoubleBuffered
	 */
	public ABlock(LayoutManager layout, UserPanel context)
	{
		super(layout);
		this.init(context);
	}
	
	/**
	 * @param layout
	 */
	public ABlock(UserPanel context)
	{
		super();
		this.init(context);
	}
	
	private void init(UserPanel context)
	{
		this.context = context;
		this.config = Config.getInstance();
		context.addModeChangeListener(this);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.client.event.ModeChangeListener#stateChanged(ch.eugster
	 * .pos.client.event.ModeChangeEvent)
	 */
	public void modeChangePerformed(ModeChangeEvent e)
	{
		this.setDefaultTab(e.getRequestedMode());
	}
	
	public void posEventPerformed(PosEvent e)
	{
		if (e.getPosAction().getKey() instanceof CustomKey)
		{
			CustomKey ck = (CustomKey) e.getPosAction().getKey();
			if (ck.setDefaultTab)
			{
				this.setDefaultTab(this.context.getMode());
			}
		}
	}
	
	public void receiptChangePerformed(ReceiptChangeEvent e)
	{
		// setDefaultTab();
	}
	
	protected void setDefaultTab(Integer state)
	{
		if (this.tabbedPane != null)
		{
			if (state.equals(UserPanel.CONTEXT_MODE_POS))
			{
				if (this.defaultTabPosition > -1 && this.defaultTabPosition < this.tabbedPane.getTabCount())
				{
					this.tabbedPane.setSelectedIndex(this.defaultTabPosition);
				}
			}
			else if (state.equals(UserPanel.CONTEXT_MODE_PAY))
			{
				if (this.defaultTabPayment > -1 && this.defaultTabPayment < this.tabbedPane.getTabCount())
				{
					this.tabbedPane.setSelectedIndex(this.defaultTabPayment);
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent e)
	{
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent e)
	{
		KeyListener[] l = this.getKeyListeners();
		for (int i = 0; i < l.length; i++)
		{
			l[i].keyPressed(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent e)
	{
	}
	
	public void fireModeChangeEvent(ModeChangeEvent e)
	{
		ModeChangeListener[] l = (ModeChangeListener[]) this.modeChangeListeners.toArray(new ModeChangeListener[0]);
		for (int i = 0; i < l.length; i++)
		{
			l[i].modeChangePerformed(e);
		}
	}
	
	public boolean addModeChangeListener(ModeChangeListener l)
	{
		return this.modeChangeListeners.add(l);
	}
	
	public boolean removeModeChangeListener(ModeChangeListener l)
	{
		return this.modeChangeListeners.remove(l);
	}
	
	public UserPanel getUserPanel()
	{
		return this.context;
	}
	
	protected ArrayList modeChangeListeners = new ArrayList();
	protected UserPanel context;
	protected TabbedPane tabbedPane;
	protected int defaultTabPosition = -1;
	protected int defaultTabPayment = -1;
	protected Config config;
}
