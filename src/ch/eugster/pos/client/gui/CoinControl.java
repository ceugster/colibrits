/*
 * Created on 05.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.client.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import ch.eugster.pos.db.Coin;
import ch.eugster.pos.db.FixKey;
import ch.eugster.pos.db.Key;
import ch.eugster.pos.events.ModeChangeEvent;
import ch.eugster.pos.events.ModeChangeListener;
import ch.eugster.pos.util.NumberUtility;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CoinControl extends ABlock implements PropertyChangeListener
{
	private static final long serialVersionUID = 0l;
	
	/**
	 * @param layout
	 * @param context
	 */
	public CoinControl(UserPanel context)
	{
		super(context);
		this.coin = new Coin();
		Key[] keys = FixKey.getByBlock(CoinCounter.class.getName());
		
		this.setLayout(new GridLayout(1, keys.length + 1));
		// JPanel panel1 = new JPanel();
		// panel1.setLayout(new GridLayout(1, 2));
		// add(panel1);
		
		this.buttons = new PosButton[keys.length];
		for (int i = 0; i < this.buttons.length; i++)
		{
			this.buttons[i] = keys[i].createButton(context);
			if (keys[i].className.equals("ch.eugster.pos.events.ClearAllCoinsAction")) this.buttons[i].addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					PropertyChangeEvent e = new PropertyChangeEvent(event.getSource(), "clear", null, null);
					CoinControl.this.propertyChange(e);
				}
			});
			this.add(this.buttons[i]);
		}
		
		JPanel panelTotalAmount = new JPanel(new BorderLayout());
		this.add(panelTotalAmount);
		this.labelTotalAmount = new JLabel();
		this.labelTotalAmount.setHorizontalAlignment(SwingConstants.TRAILING);
		panelTotalAmount.add(this.labelTotalAmount, BorderLayout.CENTER);
		panelTotalAmount.add(new JLabel("  "), BorderLayout.EAST); //$NON-NLS-1$
		
		this.setAmount();
		
		this.addModeChangeListener(context);
	}
	
	public void setAmount()
	{
		this.coin.amount = this.coin.quantity * this.coin.value;
		this.labelTotalAmount.setText(NumberUtility.formatDouble(this.coin.amount, 2, 2, true));
	}
	
	public double getAmount()
	{
		return this.coin.amount;
	}
	
	public void propertyChange(PropertyChangeEvent e)
	{
		if (e.getPropertyName().equals("amount")) { //$NON-NLS-1$
			this.coin.amount -= ((Double) e.getOldValue()).doubleValue();
			this.coin.amount += ((Double) e.getNewValue()).doubleValue();
			this.coin.amount = NumberUtility.round(this.coin.amount, 0.01d);
			this.labelTotalAmount.setText(NumberUtility.formatDouble(this.coin.amount, 2, 2, true));
		}
		if (e.getPropertyName().equals("clear")) { //$NON-NLS-1$
			this.firePropertyChange("clear", null, null); //$NON-NLS-1$
		}
	}
	
	public void fireModeChangeEvent(ModeChangeEvent e)
	{
		ModeChangeListener[] l = (ModeChangeListener[]) this.modeChangeListeners.toArray(new ModeChangeListener[0]);
		for (int i = 0; i < l.length; i++)
		{
			l[i].modeChangePerformed(e);
		}
	}
	
	private Coin coin;
	private PosButton[] buttons;
	private JLabel labelTotalAmount;
}
