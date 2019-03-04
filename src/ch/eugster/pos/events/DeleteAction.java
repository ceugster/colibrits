/*
 * Created on 01.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.events;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ch.eugster.pos.Messages;
import ch.eugster.pos.client.gui.Frame;
import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.db.Key;
import ch.eugster.pos.db.Receipt;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DeleteAction extends ModeDependendAction implements ListSelectionListener
{
	private static final long serialVersionUID = 0l;
	
	/**
	 * @param context
	 * @param key
	 */
	public DeleteAction(UserPanel context, Key key)
	{
		super(context, key);
		this.init(key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 */
	public DeleteAction(UserPanel context, Key key, String name)
	{
		super(context, key, name);
		this.init(key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 * @param icon
	 */
	public DeleteAction(UserPanel context, Key key, String name, Icon icon)
	{
		super(context, key, name, icon);
		this.init(key);
	}
	
	private void init(Key key)
	{
	}
	
	public void actionPerformed(ActionEvent e)
	{
		boolean doit = true;
		this.setPosEventListeners();
		if (this.currentTable != null)
		{
			if (this.context.getMode().equals(UserPanel.CONTEXT_MODE_POS) && this.currentTable.getSelectedRow() == -1)
			{
				if (MessageDialog.showSimpleDialog(Frame.getMainFrame(), Messages.getString("DeleteAction.L_u00F6schbest_u00E4tigung_2"), Messages
								.getString("DeleteAction.Soll_der_Beleg_gel_u00F6scht_werden__1"), 1) != MessageDialog.BUTTON_YES)
				{
					doit = false;
				}
			}
		}
		if (doit)
		{
			if (this.context.getReceiptModel().getReceipt().status == Receipt.RECEIPT_STATE_PARKED)
			{
				this.context.getReceiptModel().getReceipt().delete();
				this.context.getReceiptModel().prepareReceipt();
			}
			else
			{
				super.actionPerformed(e);
			}
		}
	}
	
	private void setPosEventListeners()
	{
		this.removePosEventListeners();
		
		if (this.context.getMode().equals(UserPanel.CONTEXT_MODE_POS))
		{
			if (this.currentTable != null)
			{
				if (this.currentTable.getSelectedRowCount() == 0)
				{
					this.addPosEventListener(this.context.getReceiptModel());
				}
				else
				{
					this.addPosEventListener(this.context.getReceiptModel().getPositionTableModel());
				}
			}
		}
		else if (this.context.getMode().equals(UserPanel.CONTEXT_MODE_PAY))
		{
			this.addPosEventListener(this.context.getReceiptModel().getPaymentTableModel());
		}
	}
	
	public void modeChangePerformed(ModeChangeEvent e)
	{
		if (!(this.currentTable == null))
		{
			this.currentTable.getSelectionModel().removeListSelectionListener(this);
		}
		
		if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_POS) || e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_PAY))
		{
			if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_POS))
			{
				this.currentTable = this.context.getReceiptModel().getPositionTableModel().getTable();
				this.currentTable.getSelectionModel().addListSelectionListener(this);
			}
			else if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_PAY))
			{
				this.currentTable = this.context.getReceiptModel().getPaymentTableModel().getTable();
				this.currentTable.getSelectionModel().addListSelectionListener(this);
			}
			if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_POS) || e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_PAY))
			{
				this.enable(e.getRequestedMode());
			}
			else
			{
				super.modeChangePerformed(e);
			}
		}
	}
	
	public void valueChanged(ListSelectionEvent e)
	{
		this.enable(this.context.getMode());
	}
	
	public void enable(Integer state)
	{
		Receipt receipt = this.context.getReceiptModel().getReceipt();
		JTable positions = this.context.getReceiptModel().getPositionTableModel().getTable();
		JTable payments = this.context.getReceiptModel().getPaymentTableModel().getTable();
		if (state.equals(UserPanel.CONTEXT_MODE_POS))
		{
			// 10117 Ergänzt durch Prüfung, ob Kunde auf Beleg
			// if (positions.getRowCount() == 0 && payments.getRowCount() == 0)
			// {
			if (positions.getRowCount() == 0 && payments.getRowCount() == 0 && !receipt.hasCustomer())
			{
				this.putValue("enabled", new Boolean(false)); //$NON-NLS-1$
			}
			else
			{
				this.putValue("enabled", new Boolean(true)); //$NON-NLS-1$
			}
		}
		else if (state.equals(UserPanel.CONTEXT_MODE_PAY))
		{
			// 10117 Ergänzt durch Prüfung, ob Kunde auf Beleg
			// if (payments.getRowCount() == 0) {
			if (payments.getRowCount() == 0 && !receipt.hasCustomer())
			{
				this.putValue("enabled", new Boolean(false)); //$NON-NLS-1$
			}
			else
			{
				this.putValue("enabled", new Boolean(true)); //$NON-NLS-1$
			}
		}
	}
	
	private void removePosEventListeners()
	{
		this.posEventListeners.clear();
	}
	
	private JTable currentTable = null;
}
