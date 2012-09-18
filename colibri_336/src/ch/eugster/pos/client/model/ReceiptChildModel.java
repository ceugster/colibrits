/*
 * Created on 09.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.client.model;

import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.ReceiptChild;
import ch.eugster.pos.events.PosEventListener;
import ch.eugster.pos.events.ReceiptChangeEvent;
import ch.eugster.pos.events.ReceiptChangeListener;
import ch.eugster.pos.events.ReceiptChildChangeEvent;
import ch.eugster.pos.events.ReceiptChildChangeListener;
import ch.eugster.pos.util.Config;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class ReceiptChildModel implements ItemStateInformationProvider, PosEventListener, ListSelectionListener, ReceiptChangeListener
{
	
	public ReceiptChildModel(ReceiptModel receiptModel, JTable table)
	{
		this.init(receiptModel, table);
	}
	
	protected void init(ReceiptModel model, JTable tbl)
	{
		ReceiptChildModel.defaultCurrency = ForeignCurrency.getDefaultCurrency();
		ReceiptChildModel.roundFactorAmount = Config.getInstance().getRoundFactorAmount();
		ReceiptChildModel.roundFactorTax = Config.getInstance().getRoundFactorTax();
		this.receiptModel = model;
		
		this.table = tbl;
		this.table.getSelectionModel().addListSelectionListener(this);
	}
	
	public ReceiptChild getReceiptChild()
	{
		return this.child;
	}
	
	public void receiptChangePerformed(ReceiptChangeEvent e)
	{
		if (e.getEventType().equals(ReceiptModel.RECEIPT_INITIALIZED))
		{
			this.setReceiptChild();
		}
		else if (e.getEventType().equals(ReceiptModel.RECEIPT_CHILD_ADDED))
		{
			this.showPutCustomerMessage(e);
			if (this.table.getSelectedRow() == -1)
			{
				this.setReceiptChild();
			}
			else
			{
				this.setReceiptChild(this.receiptModel.getReceipt().getChildAt(this.table.getSelectedRow(), this.child.getClass()));
			}
		}
		else if (e.getEventType().equals(ReceiptModel.RECEIPT_CHILD_REMOVED))
		{
			if (this.table.getSelectedRow() == -1)
			{
				this.setReceiptChild();
			}
			else
			{
				this.setReceiptChild(this.receiptModel.getReceipt().getChildAt(this.table.getSelectedRow(), this.child.getClass()));
			}
		}
		else if (e.getEventType().equals(ReceiptModel.RECEIPT_CHILD_CHANGED))
		{
			this.showPutCustomerMessage(e);
			if (this.table.getSelectedRow() == -1)
			{
				this.setReceiptChild();
			}
			else
			{
				this.setReceiptChild(this.receiptModel.getReceipt().getChildAt(this.table.getSelectedRow(), this.child.getClass()));
			}
		}
	}
	
	public int getState()
	{
		int state = ReceiptChildModel.CHILD_STATE_NO_CHANGE;
		if (this.isComplete())
		{
			state = ReceiptChildModel.CHILD_STATE_COMPLETE;
		}
		else
		{
			state = ReceiptChildModel.CHILD_STATE_CLEAN;
		}
		return state;
	}
	
	public boolean isComplete()
	{
		return this.child.isComplete();
	}
	
	protected void setReceiptChild(ReceiptChild receiptChild)
	{
		this.child = receiptChild;
	}
	
	protected abstract void setReceiptChild();
	
	protected void showPutCustomerMessage(ReceiptChangeEvent e)
	{
	}
	
	public void addReceiptChildChangeListener(ReceiptChildChangeListener l)
	{
		if (!this.receiptChildChangeListeners.contains(l))
		{
			this.receiptChildChangeListeners.add(l);
		}
	}
	
	public void removeReceiptChildChangeListener(ReceiptChildChangeListener l)
	{
		if (this.receiptChildChangeListeners.contains(l))
		{
			this.receiptChildChangeListeners.remove(l);
		}
	}
	
	public void fireReceiptChildChangeEvent(ReceiptChildChangeEvent e)
	{
		ReceiptChildChangeListener[] l = (ReceiptChildChangeListener[]) this.receiptChildChangeListeners.toArray(new ReceiptChildChangeListener[0]);
		
		for (int i = 0; i < l.length; i++)
		{
			l[i].receiptChildChangePerformed(e);
		}
	}
	
	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting()) return;
		
		ReceiptChildChangeEvent sce = new ReceiptChildChangeEvent(this);
		ListSelectionModel lsm = (ListSelectionModel) e.getSource();
		if (lsm.isSelectionEmpty())
		{
			this.setReceiptChild();
		}
		else
		{
			this.setReceiptChild(this.receiptModel.getReceipt().getChildAt(this.table.getSelectedRow(), this.child.getClass()));
		}
		this.fireReceiptChildChangeEvent(sce);
	}
	
	protected int maxQuantityRange;
	protected int maxQuantityAmount;
	protected double maxRange;
	protected double maxAmount;
	
	protected ReceiptModel receiptModel;
	protected JTable table;
	protected ReceiptChild child;
	protected ArrayList receiptChildChangeListeners = new ArrayList();
	
	public static Config cfg = Config.getInstance();
	public static double roundFactorAmount;
	public static double roundFactorTax;
	public static ForeignCurrency defaultCurrency;
	
	public static final int CHILD_STATE_NO_CHANGE = 0;
	public static final int CHILD_STATE_CLEAN = 1;
	public static final int CHILD_STATE_COMPLETE = 2;
	
}
