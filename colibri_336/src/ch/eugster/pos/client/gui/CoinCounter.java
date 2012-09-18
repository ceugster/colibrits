/*
 * Created on 05.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ch.eugster.pos.db.Coin;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.util.NumberUtility;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CoinCounter extends ABlock implements PropertyChangeListener
{
	private static final long serialVersionUID = 0l;
	
	/**
	 * @param layout
	 * @param context
	 */
	public CoinCounter(LayoutManager layout, UserPanel context)
	{
		super(layout, context);
		this.init();
	}
	
	/**
	 * @param context
	 */
	public CoinCounter(UserPanel context)
	{
		super(context);
		this.init();
	}
	
	// 10226
	// private void init() {
	// coins = Coin.getAll();
	//
	// CoinControl coinControl = new CoinControl(context);
	//		
	// setLayout(new GridLayout(coins.length + 1, 1));
	//		
	// items = new CoinItem[coins.length];
	// for (int i = 0; i < coins.length; i++) {
	// items[i] = new CoinItem(coins[i], coinControl, context);
	// add(items[i]);
	// }
	//		
	// add(coinControl);
	// }
	
	private void init()
	{
		BorderLayout layout = new BorderLayout();
		layout.setHgap(0);
		layout.setVgap(0);
		this.setLayout(layout);
		
		ch.eugster.pos.db.Block block = ch.eugster.pos.db.Block.getByClass(this.getClass().getName());
		
		Coin[] coins = Coin.getAll();
		
		this.currencies = new Hashtable();
		for (int i = 0; i < coins.length; i++)
		{
			ArrayList list;
			Object object = this.currencies.get(coins[i].getForeignCurrency().code);
			if (object == null)
				list = new ArrayList();
			else
				list = (ArrayList) object;
			
			list.add(coins[i]);
			
			this.currencies.put(coins[i].getForeignCurrency().code, list);
		}
		
		Set keys = this.currencies.keySet();
		String[] codes = (String[]) keys.toArray(new String[0]);
		Arrays.sort(codes);
		
		this.coinControl = new CoinControl(this.context);
		this.coinControl.setMinimumSize(new Dimension(100, 100));
		this.coinControl.setPreferredSize(new Dimension(100, 100));
		
		if (codes.length == 0)
		{
			return;
		}
		else if (codes.length == 1)
		{
			
			ArrayList list = (ArrayList) this.currencies.get(codes[0]);
			coins = (Coin[]) list.toArray(new Coin[0]);
			Arrays.sort(coins, new CoinComparator());
			
			JPanel p = new JPanel(new GridLayout(coins.length + 1, 1));
			
			this.items = new CoinItem[coins.length];
			for (int i = 0; i < coins.length; i++)
			{
				this.items[i] = new CoinItem(coins[i], this.coinControl, this.context);
				this.coinControl.addPropertyChangeListener(this.items[i]);
				p.add(this.items[i]);
			}
			p.add(this.coinControl);
			
			this.add(p, BorderLayout.CENTER);
		}
		else
		{
			this.tabbedPane = new TabbedPane(this.context);
			this.tabbedPane.setFont(this.getFont().deriveFont(block.fontStyle, (float) block.fontSize));
			
			for (int i = 0; i < codes.length; i++)
			{
				ArrayList list = (ArrayList) this.currencies.get(codes[i]);
				coins = (Coin[]) list.toArray(new Coin[0]);
				Arrays.sort(coins, new CoinComparator());
				
				JPanel p = new JPanel(new GridLayout(coins.length + 1, 1));
				this.tabbedPane.addTab(codes[i], p);
				
				CurrencySumLabel sumLabel = new CurrencySumLabel(coins[0].getForeignCurrency());
				
				this.items = new CoinItem[coins.length];
				for (int j = 0; j < coins.length; j++)
				{
					this.items[j] = new CoinItem(coins[j], this.coinControl, this.context);
					this.items[j].addPropertyChangeListener(sumLabel);
					this.coinControl.addPropertyChangeListener(this.items[j]);
					sumLabel.addPropertyChangeListener(this.items[j]);
					p.add(this.items[j]);
				}
				p.add(sumLabel);
			}
			
			this.defaultTabPosition = 0;
			this.tabbedPane.setTabColors();
			
			this.add(this.tabbedPane, BorderLayout.CENTER);
			this.add(this.coinControl, BorderLayout.SOUTH);
			
			this.context.getReceiptModel().addReceiptChangeListener(this);
		}
	}
	
	// 10226
	
	public Hashtable getCurrencyCoins()
	{
		return this.currencies;
	}
	
	public void propertyChange(PropertyChangeEvent e)
	{
		if (e.getPropertyName().equals("clearAll"))
		{
			this.coinControl.propertyChange(new PropertyChangeEvent(this, "clear", null, null));
		}
	}
	
	// 10226
	private class CoinComparator implements Comparator
	{
		public int compare(Object obj1, Object obj2)
		{
			Coin c1 = (Coin) obj1;
			Coin c2 = (Coin) obj2;
			
			int cf = c1.getForeignCurrency().code.compareTo(c2.getForeignCurrency().code);
			if (cf == 0)
				return new Double(c2.value).compareTo(new Double(c1.value));
			else
				return cf;
		}
	}
	
	// 10226
	
	private class CurrencySumLabel extends JPanel implements PropertyChangeListener
	{
		private static final long serialVersionUID = 0l;
		
		private NumberFormat nf;
		private JLabel amountLabel;
		private double value = 0;
		
		public CurrencySumLabel(ForeignCurrency currency)
		{
			this.nf = DecimalFormat.getNumberInstance();
			this.nf.setMaximumFractionDigits(currency.getCurrency().getDefaultFractionDigits());
			this.nf.setMinimumFractionDigits(currency.getCurrency().getDefaultFractionDigits());
			
			GridLayout layout = new GridLayout(1, 2);
			layout.setHgap(5);
			layout.setVgap(5);
			this.setLayout(layout);
			
			JPanel left = new JPanel();
			left.setLayout(new GridLayout(1, 2));
			
			JButton button = new JButton("Leeren");
			button.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					CurrencySumLabel.this.firePropertyChange("clear", null, null);
				}
			});
			left.add(button);
			
			JLabel label = new JLabel(currency.code + " " + currency.name);
			label.setHorizontalAlignment(JLabel.RIGHT);
			left.add(label);
			
			this.add(left);
			
			this.amountLabel = new JLabel("0.00");
			this.amountLabel.setHorizontalAlignment(JLabel.RIGHT);
			this.add(this.amountLabel);
		}
		
		public void setAmount(double amount)
		{
			amount = NumberUtility.round(amount, 0.01);
			this.amountLabel.setText(this.nf.format(amount));
		}
		
		public void propertyChange(PropertyChangeEvent event)
		{
			if (event.getPropertyName().equals("amount_foreign_currency"))
			{
				Double oldAmount = (Double) event.getOldValue();
				Double newAmount = (Double) event.getNewValue();
				this.value += newAmount.doubleValue() - oldAmount.doubleValue();
				this.amountLabel.setText(this.nf.format(this.value));
			}
		}
	}
	
	public CoinControl getCoinControl()
	{
		return this.coinControl;
	}
	
	// 10226
	
	// private Coin[] coins;
	private CoinControl coinControl;
	private Hashtable currencies;
	private CoinItem[] items;
}
