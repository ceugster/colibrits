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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import ch.eugster.pos.db.Coin;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.util.NumberUtility;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CoinItem extends ABlock implements PropertyChangeListener
{
	private static final long serialVersionUID = 0l;
	
	/**
	 * @param layout
	 * @param context
	 */
	public CoinItem(Coin coin, CoinControl coinControl, UserPanel context)
	{
		super(context);
		this.coin = coin;
		
		this.setBorder(BorderFactory.createEtchedBorder());
		this.setLayout(new GridLayout(1, 2));
		JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayout(1, 2));
		this.add(panel1);
		
		this.btnQuantity = new JButton();
		this.btnQuantity.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				CoinItem.this.setQuantity(0);
			}
		});
		this.btnQuantity.setHorizontalAlignment(SwingConstants.TRAILING);
		JPanel panelQuantity = new JPanel(new BorderLayout());
		panelQuantity.add(this.btnQuantity, BorderLayout.CENTER);
		panelQuantity.add(new JLabel("  "), BorderLayout.EAST); //$NON-NLS-1$
		panel1.add(panelQuantity);
		
		this.btnValue = new JButton(NumberUtility.formatDouble(coin.value, 2, 2, true));
		this.btnValue.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				CoinItem.this.setQuantity();
			}
		});
		panel1.add(this.btnValue);
		
		this.labelAmount = new JLabel();
		this.labelAmount.setHorizontalAlignment(SwingConstants.TRAILING);
		JPanel panelAmount = new JPanel(new BorderLayout());
		panelAmount.add(this.labelAmount, BorderLayout.CENTER);
		panelAmount.add(new JLabel("  "), BorderLayout.EAST); //$NON-NLS-1$
		this.add(panelAmount);
		
		this.addPropertyChangeListener(coinControl);
	}
	
	public void setQuantity(int quantity)
	{
		this.coin.quantity = quantity;
		this.btnQuantity.setText(this.coin.quantity == 0 ? "" : NumberUtility.formatInteger(this.coin.quantity, true));
		this.setAmount();
	}
	
	private void setQuantity()
	{
		int quantity = this.context.getNumericBlock().moveQuantity();
		this.setQuantity(quantity == 0 ? ++this.coin.quantity : quantity);
	}
	
	public int getQuantity()
	{
		return this.coin.quantity;
	}
	
	private void setAmount()
	{
		double oldAmount = this.coin.amount;
		double newAmount = NumberUtility.round(this.coin.quantity * this.coin.value, 2);
		this.coin.amount = newAmount;
		this.labelAmount.setText(this.coin.amount == 0d ? "" : NumberUtility.formatDouble(this.coin.amount, 2, 2, true));
		this.firePropertyChange("amount_foreign_currency", new Double(oldAmount), new Double(this.coin.amount)); //$NON-NLS-1$
		// 10226 Nun auf Landeswährung umrechnen...
		double lOldAmount = oldAmount;
		double lNewAmount = this.coin.amount;
		if (!this.coin.getForeignCurrency().equals(ForeignCurrency.getDefaultCurrency()))
		{
			double quotation = this.coin.getForeignCurrency().quotation;
			lOldAmount = oldAmount * quotation;
			lNewAmount = newAmount * quotation;
		}
		this.firePropertyChange("amount", lOldAmount, lNewAmount); //$NON-NLS-1$
		// 10226
	}
	
	public double getAmount()
	{
		return this.coin.amount;
	}
	
	public double getValue()
	{
		return this.coin.value;
	}
	
	public void clear()
	{
		this.setQuantity(0);
	}
	
	public void propertyChange(PropertyChangeEvent e)
	{
		if (e.getPropertyName().equals("clear")) { //$NON-NLS-1$
			this.clear();
		}
	}
	
	private Coin coin;
	// private JLabel labelQuantity;
	private JButton btnValue;
	private JButton btnQuantity;
	private JLabel labelAmount;
}
