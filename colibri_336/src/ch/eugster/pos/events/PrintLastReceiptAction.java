/*
 * Created on 17.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.events;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import ch.eugster.pos.client.gui.Frame;
import ch.eugster.pos.client.gui.TabPanel;
import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.db.Key;
import ch.eugster.pos.db.Receipt;

/**
 * @author Administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PrintLastReceiptAction extends ModeDependendAction implements ReceiptChangeListener, ReceiptSaveListener
{
	
	public static final long serialVersionUID = 0l;
	
	private Receipt lastReceipt = null;
	
	/**
	 * @param context
	 * @param key
	 */
	public PrintLastReceiptAction(UserPanel context, Key key)
	{
		super(context, key);
		this.init(key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 */
	public PrintLastReceiptAction(UserPanel context, Key key, String name)
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
	public PrintLastReceiptAction(UserPanel context, Key key, String name, Icon icon)
	{
		super(context, key, name, icon);
		this.init(key);
	}
	
	private void init(Key key)
	{
		this.context.getReceiptModel().addReceiptSaveListener(this);
		this.context.getReceiptModel().getPaymentTableModel().addReceiptChangeListener(this);
		this.context.getReceiptModel().getPositionTableModel().addReceiptChangeListener(this);
		this.addPosEventListener(this.context.getReceiptModel());
	}
	
	public Receipt getLastReceipt()
	{
		return this.lastReceipt;
	}
	
	public void setLastReceipt(Receipt receipt)
	{
		this.lastReceipt = receipt;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (this.lastReceipt != null)
		{
			TabPanel panel = Frame.getMainFrame().getTabPanel();
			panel.getReceiptPrinter().print(panel.getReceiptPrinter().getPrinter(), this.lastReceipt);
		}
	}
	
	public void receiptChangePerformed(ReceiptChangeEvent e)
	{
		this.setState(e);
	}
	
	public void modeChangePerformed(ModeChangeEvent e)
	{
		if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_POS) || e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_PAY))
		{
			this.setState(e);
		}
		else
		{
			super.modeChangePerformed(e);
		}
	}
	
	public void receiptSaved(ReceiptSaveEvent event)
	{
		this.lastReceipt = event.getReceipt();
	}
	
	public void setState(AbstractEvent e)
	{
		this.putValue("enabled", new Boolean(this.lastReceipt != null)); //$NON-NLS-1$
	}
	
}
