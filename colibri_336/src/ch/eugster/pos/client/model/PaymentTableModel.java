/*
 * Created on 24.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.client.model;

import java.awt.Toolkit;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.Payment;
import ch.eugster.pos.db.PaymentType;
import ch.eugster.pos.db.ReceiptChild;
import ch.eugster.pos.events.Action;
import ch.eugster.pos.events.ClearAction;
import ch.eugster.pos.events.DeleteAction;
import ch.eugster.pos.events.EnterAction;
import ch.eugster.pos.events.PosEvent;
import ch.eugster.pos.events.ReceiptChangeEvent;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PaymentTableModel extends ReceiptChildTableModel
{
	private static final long serialVersionUID = 0l;
	
	public PaymentTableModel(ReceiptModel receiptModel)
	{
		super(receiptModel, new JTable());
		this.init();
	}
	
	protected void init()
	{
		this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		
		this.table.getColumnModel().getColumn(1).setPreferredWidth(80);
		this.table.getColumnModel().getColumn(2).setPreferredWidth(40);
		this.table.getColumnModel().getColumn(3).setPreferredWidth(80);
		this.table.getColumnModel().getColumn(0).setPreferredWidth(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - 40 - 80);
	}
	
	public boolean isThereAVoucherPayment()
	{
		Payment[] payments = this.receipt.getPaymentsAsArray();
		for (int i = 0; i < payments.length; i++)
		{
			if (payments[i].isVoucher()) return true;
		}
		return false;
	}
	
	public void posEventPerformed(PosEvent e)
	{
		Action action = e.getPosAction();
		if (action instanceof EnterAction)
		{
			if (action.getActionType().equals(Action.POS_ACTION_STORE_ENTRY))
			{
				this.store(this.receiptModel.getPaymentModel().getReceiptChild());
			}
		}
		else if (action instanceof DeleteAction)
		{
			if (action.getActionType().equals(Action.POS_ACTION_DELETE_ENTRY))
			{
				this.remove(Payment.class);
			}
		}
		else if (action instanceof ClearAction)
		{
			if (action.getActionType().equals(Action.POS_ACTION_CLEAR))
			{
				this.table.clearSelection();
			}
		}
	}
	
	public void removeNotCashPayments()
	{
		boolean changed = false;
		Payment[] p = this.receipt.getPaymentsAsArray();
		for (int i = 0; i < p.length; i++)
		{
			if (!p[i].getPaymentTypeId().equals(PaymentType.CASH_ID))
			{
				this.receipt.removeChild(i, Payment.class);
				ReceiptChangeEvent e = new ReceiptChangeEvent(this.receiptModel);
				e.setReceiptChild(p[i]);
				e.setEventType(ReceiptModel.RECEIPT_CHILD_REMOVED);
				this.fireReceiptChangeEvent(e);
				changed = true;
			}
		}
		if (changed)
		{
		}
	}
	
	public void store(ReceiptChild child)
	{
		ReceiptChangeEvent e = new ReceiptChangeEvent(this.receiptModel);
		int row = this.table.getSelectedRow();
		if (row == -1)
		{
			boolean added = this.receipt.addChild(child);
			if (added)
			{
				int size = this.receipt.getChildrenCount(child.getClass());
				this.fireTableRowsInserted(size - 1, size - 1);
				e.setEventType(ReceiptModel.RECEIPT_CHILD_ADDED);
				e.setReceiptChild(child);
				this.fireReceiptChangeEvent(e);
			}
		}
		else
		{
			this.receipt.setChild(row, child);
			this.table.clearSelection();
			this.fireTableRowsUpdated(row, row);
			e.setEventType(ReceiptModel.RECEIPT_CHILD_CHANGED);
			this.fireReceiptChangeEvent(e);
		}
	}
	
	public Object getValueAt(int row, int col)
	{
		return PaymentModel.getValueAt(this.receipt.getPaymentAt(row), col);
	}
	
	public int getRowCount()
	{
		return this.receipt.getPaymentCount();
	}
	
	public void valueChanged(ListSelectionEvent e)
	{
	}
	
	// public String getColumnName(int columnIndex) {
	// return columnNames[columnIndex];
	// }
	
	// public String[] getColumnNames() {
	// return columnNames;
	// }
	
	public int getColumnCount()
	{
		return PaymentTableModel.columnNames.length;
	}
	
	private static String[] columnNames =
	{ Messages.getString("PaymentTableModel.Zahlungsart_1"), //$NON-NLS-1$
					Messages.getString("PaymentTableModel.FW_2"), //$NON-NLS-1$
					Messages.getString("PaymentTableModel.Kurs_3"), //$NON-NLS-1$
					Messages.getString("PaymentTableModel.Betrag_4") }; //$NON-NLS-1$
}
