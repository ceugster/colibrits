/*
 * Created on 20.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.client.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.eugster.pos.db.CustomKey;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.Key;
import ch.eugster.pos.db.PaymentType;
import ch.eugster.pos.db.Tab;
import ch.eugster.pos.events.Action;
import ch.eugster.pos.events.ReceiptChangeEvent;
import ch.eugster.pos.events.ReceiptChangeListener;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PaymentTypeBlock extends ABlock implements ChangeListener
{
	
	public static final long serialVersionUID = 0l;
	
	/**
	 * 
	 */
	public PaymentTypeBlock(UserPanel context)
	{
		super(new BorderLayout(), context);
		this.init();
	}
	
	private void init()
	{
		this.add(this.buildPaymentTypePanels(), BorderLayout.CENTER);
	}
	
	public JPanel buildPaymentTypePanels()
	{
		JPanel panel = new JPanel(new BorderLayout());
		ch.eugster.pos.db.Block block = ch.eugster.pos.db.Block.getByClass(this.getClass().getName());
		if (block != null)
		{
			Tab[] tabs = Tab.getByBlock(block);
			if (tabs.length == 0)
			{
				return panel;
			}
			else if (tabs.length == 1 && tabs[0].title.equals("")) { //$NON-NLS-1$
				if (tabs[0].rows != 0 || tabs[0].columns != 0)
				{
					JPanel p = new JPanel(new GridLayout(tabs[0].rows, tabs[0].columns));
					this.addButtons(tabs[0], p);
					panel.add(p, BorderLayout.CENTER);
				}
			}
			else
			{
				this.foreignCurrency = new ForeignCurrency[tabs.length];
				
				this.tabbedPane = new TabbedPane(this.context);
				this.tabbedPane.setFont(this.getFont().deriveFont(block.fontStyle, (float) block.fontSize));
				
				for (int i = 0; i < tabs.length; i++)
				{
					if (tabs[i].rows != 0 || tabs[i].columns != 0)
					{
						JPanel p = new JPanel(new GridLayout(tabs[i].rows, tabs[i].columns));
						this.tabbedPane.addTab(tabs[i].title, p);
						if (tabs[i].defaultTabPayment)
						{
							this.defaultTabPayment = i;
						}
						this.foreignCurrency[i] = this.addButtons(tabs[i], p);
					}
				}
				this.tabbedPane.setTabColors();
				
				panel.add(this.tabbedPane, BorderLayout.CENTER);
				this.tabbedPane.addChangeListener(this);
				this.context.getReceiptModel().addReceiptChangeListener(this);
			}
		}
		return panel;
	}
	
	private ForeignCurrency addButtons(Tab tab, JPanel p)
	{
		ForeignCurrency tabCurrency = null;
		
		CustomKey[] keys = CustomKey.getByTab(tab.getId());
		int rows = tab.rows;
		int cols = tab.columns;
		Key[][] keyPlaces = new Key[rows][cols];
		for (int i = 0; i < keys.length; i++)
		{
			int row = keys[i].row;
			int col = keys[i].column;
			if (row < rows && col < cols)
			{
				keyPlaces[row][col] = keys[i];
			}
		}
		for (int i = 0; i < keyPlaces.length; i++)
		{
			for (int j = 0; j < keyPlaces[i].length; j++)
			{
				if (keyPlaces[i][j] == null)
				{
					JPanel dummy = new JPanel();
					dummy.setRequestFocusEnabled(false);
					p.add(dummy);
				}
				else
				{
					Key key = keyPlaces[i][j];
					JComponent comp = key.createButton(this.context, this);
					if (comp == null)
					{
						comp = new JPanel();
					}
					else
					{
						PosButton b = (PosButton) comp;
						ForeignCurrency buttonCurrency = this.getCurrency(b);
						if (tabCurrency == null)
						{
							tabCurrency = buttonCurrency;
						}
						else if (buttonCurrency != null)
						{
							if (!tabCurrency.equals(buttonCurrency))
							{
								tabCurrency = null;
							}
						}
					}
					comp.setRequestFocusEnabled(false);
					p.add(comp);
				}
			}
		}
		return tabCurrency;
	}
	
	private ForeignCurrency getCurrency(PosButton b)
	{
		Action a = (Action) b.getAction();
		Object o = a.getParent(Action.POS_KEY_PAYMENT_TYPE);
		if (o == null)
		{
			return null;
		}
		else if (o instanceof PaymentType)
		{
			PaymentType type = (PaymentType) o;
			return type.getForeignCurrency();
		}
		else
		{
			return null;
		}
	}
	
	public ForeignCurrency getTabCurrency()
	{
		return this.foreignCurrency[this.tabbedPane.getSelectedIndex()];
	}
	
	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource() instanceof TabbedPane)
		{
			TabbedPane pane = (TabbedPane) e.getSource();
			if (this.foreignCurrency[pane.getSelectedIndex()] != null)
			{
				this.context.setCurrentForeignCurrency(this.foreignCurrency[pane.getSelectedIndex()]);
				this.fireReceiptChangePerformedEvent();
			}
		}
	}
	
	public void fireReceiptChangePerformedEvent()
	{
		ReceiptChangeListener[] listeners = (ReceiptChangeListener[]) this.receiptChangeListeners.toArray(new ReceiptChangeListener[0]);
		for (int i = 0; i < listeners.length; i++)
		{
			listeners[i].receiptChangePerformed(new ReceiptChangeEvent(this.context.getReceiptModel()));
		}
	}
	
	public boolean addReceiptChangeListener(ReceiptChangeListener listener)
	{
		return this.receiptChangeListeners.add(listener);
	}
	
	public boolean removeReceiptChangeListener(ReceiptChangeListener listener)
	{
		return this.receiptChangeListeners.remove(listener);
	}
	
	private ForeignCurrency[] foreignCurrency;
	private ArrayList receiptChangeListeners = new ArrayList();
}
