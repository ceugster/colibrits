/*
 * Created on 10.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.client.model;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import ch.eugster.pos.client.gui.Frame;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.db.ReceiptChild;
import ch.eugster.pos.events.PosEventListener;
import ch.eugster.pos.events.ReceiptChangeEvent;
import ch.eugster.pos.events.ReceiptChangeListener;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class ReceiptChildTableModel extends AbstractTableModel implements ItemStateInformationProvider, ListSelectionListener, PosEventListener, ReceiptChangeListener, ComponentListener
{
	private static final long serialVersionUID = 0l;
	
	/**
	 * 
	 */
	public ReceiptChildTableModel(ReceiptModel receiptModel, JTable table)
	{
		super();
		this.init(receiptModel, table);
	}
	
	private void init(ReceiptModel model, JTable table)
	{
		this.receiptModel = model;
		this.receipt = this.receiptModel.getReceipt();
		this.table = table;
		this.table.setModel(this);
		this.table.getSelectionModel().addListSelectionListener(this);
		this.table.setAutoscrolls(true);
		this.table.addComponentListener(this);
		Frame.getMainFrame().addComponentListener(this);
	}
	
	public void setReceipt(Receipt receipt)
	{
		this.receipt = receipt;
		this.fireTableDataChanged();
	}
	
	public JTable getTable()
	{
		return this.table;
	}
	
	public void receiptChangePerformed(ReceiptChangeEvent e)
	{
	}
	
	public abstract void store(ReceiptChild child);
	
	public void remove(Class cls)
	{
		int rows = this.table.getRowCount();
		if (rows > 0)
		{
			int row = this.table.getSelectedRow();
			if (row == -1)
			{
				this.receipt.removeChildren(cls);
				this.fireTableRowsDeleted(0, rows - 1);
			}
			else
			{
				this.receipt.removeChild(row, cls);
				this.fireTableRowsDeleted(row, row);
				if (this.table.getSelectedRow() > -1)
				{
					this.table.clearSelection();
				}
			}
			this.fireReceiptChangeEvent(new ReceiptChangeEvent(this.receiptModel, ReceiptModel.RECEIPT_CHILD_REMOVED));
		}
	}
	
	public ReceiptChild get(int index, Class cls)
	{
		return this.receipt.getChildAt(index, cls);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public void fireReceiptChangeEvent(ReceiptChangeEvent e)
	{
		ReceiptChangeListener[] l = (ReceiptChangeListener[]) this.receiptChangeListeners.toArray(new ReceiptChangeListener[0]);
		for (int i = 0; i < l.length; i++)
		{
			l[i].receiptChangePerformed(e);
		}
	}
	
	public void addReceiptChangeListener(ReceiptChangeListener l)
	{
		if (!this.receiptChangeListeners.contains(l))
		{
			this.receiptChangeListeners.add(l);
		}
	}
	
	public void removeReceiptChangeListener(ReceiptChangeListener l)
	{
		if (this.receiptChangeListeners.contains(l))
		{
			this.receiptChangeListeners.remove(l);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.client.gui.StateInformationProvider#getState()
	 */
	public int getState()
	{
		int state = ReceiptChildTableModel.LIST_STATE_NO_CHANGE;
		int rows = this.getRowCount();
		if (rows == 0)
		{
			state = ReceiptChildTableModel.LIST_STATE_EMPTY;
		}
		else
		{
			if (this.table.getSelectedRow() == 0)
			{
				state = ReceiptChildTableModel.LIST_STATE_HAS_ROWS;
			}
			else
			{
				state = ReceiptChildTableModel.LIST_STATE_ROW_SELECTED;
			}
		}
		return state;
	}
	
	public Receipt getReceipt()
	{
		return this.receipt;
	}
	
	public void componentResized(ComponentEvent event)
	{
	}
	
	public void componentHidden(ComponentEvent event)
	{
	}
	
	public void componentMoved(ComponentEvent event)
	{
	}
	
	public void componentShown(ComponentEvent event)
	{
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event
	 * .ListSelectionEvent)
	 */
	public abstract void valueChanged(ListSelectionEvent e);
	
	protected Receipt receipt;
	protected ReceiptModel receiptModel;
	protected JTable table;
	protected ArrayList receiptChangeListeners = new ArrayList();
	
	public static final int LIST_STATE_NO_CHANGE = 0;
	public static final int LIST_STATE_EMPTY = 1;
	public static final int LIST_STATE_HAS_ROWS = 2;
	public static final int LIST_STATE_ROW_SELECTED = 3;
	
}
