/*
 * Created on 05.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.client.gui;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import ch.eugster.pos.client.model.PaymentModel;
import ch.eugster.pos.events.ModeChangeEvent;
import ch.eugster.pos.events.ReceiptChildChangeEvent;
import ch.eugster.pos.events.ReceiptChildChangeListener;
import ch.eugster.pos.util.Config;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PaymentBlock extends ABlock implements ReceiptChildChangeListener, ActionListener, TableModelListener
{
	private static final long serialVersionUID = 0l;
	
	public PaymentBlock(UserPanel context, ReceivedBackBlock receivedBackBlock)
	{
		super(new BorderLayout(), context);
		this.init(receivedBackBlock);
	}
	
	private void init(ReceivedBackBlock receivedBackBlock)
	{
		// Config cfg = Config.getInstance();
		// this.size = cfg.getFontSize(cfg.getDetailBlockFont());
		// this.style = cfg.getFontStyle(cfg.getDetailBlockFont());
		
		if (!Boolean.valueOf(Config.getInstance().getTotalBlock().getAttributeValue("show-always")).booleanValue())
		{
			this.add(receivedBackBlock, BorderLayout.NORTH); // *
		}
		
		this.table = this.context.getReceiptModel().getPaymentTableModel().getTable();
		this.table.setFocusable(false);
		this.add(new JScrollPane(this.table));
		
		this.table.getModel().addTableModelListener(this);
	}
	
	public void actionPerformed(ActionEvent e)
	{
	}
	
	public void scrollToVisible()
	{
		if (!(this.table.getParent() instanceof JViewport))
		{
			return;
		}
		JViewport viewport = (JViewport) this.table.getParent();
		Rectangle rect = this.table.getCellRect(this.table.getSelectedRow(), 0, true);
		Point pt = viewport.getViewPosition();
		rect.setLocation(rect.x - pt.x, rect.y - pt.y);
		viewport.scrollRectToVisible(rect);
	}
	
	public void receiptChildChangePerformed(ReceiptChildChangeEvent e)
	{
		if (e.getReceiptChildModel() instanceof PaymentModel)
		{
			PaymentModel pm = (PaymentModel) e.getReceiptChildModel();
			this.display(pm);
			this.setValues(pm);
		}
	}
	
	public void tableChanged(TableModelEvent e)
	{
		//		if (buttons.get("up") != null) { //$NON-NLS-1$
		//			((PosButton)buttons.get("up")).setEnabled(table.getRowCount() > 0); //$NON-NLS-1$
		// }
		//		if (buttons.get("down") != null) { //$NON-NLS-1$
		//			((PosButton)buttons.get("down")).setEnabled(table.getRowCount() > 0); //$NON-NLS-1$
		// }
	}
	
	public void modeChangePerformed(ModeChangeEvent e)
	{
	}
	
	public void display(PaymentModel pos)
	{
	}
	
	public void setValues(PaymentModel pay)
	{
		// values[0].setText(PaymentModel.getDisplayName(pay.getPayment()));
		// values[1].setText(PaymentModel.getDisplayForeignCurrencyAmount(pay.getPayment()));
		// values[2].setText(PaymentModel.getDisplayAmount(pay.getPayment()));
	}
	
	public void initValues()
	{
		// for (int i = 0; i < values.length; i++) {
		//			values[i].setText(""); //$NON-NLS-1$
		// }
	}
	
	public int getTableEntryCount()
	{
		return this.table.getRowCount();
	}
	
	// private JLabel[] labels = new JLabel[6];
	// private JLabel[] values = new JLabel[6];
	// private Hashtable buttons = new Hashtable();
	private JTable table;
	
	// $0001 2004-11-04 ReceivedBackBlock gezügelt von PaymentBlock ***********
	// private ReceivedBackBlock receivedBackBlock; //*
	// $0001 2004-11-04 ReceivedBackBlock gezügelt von PaymentBlock ***********
	//	
	//	private static final String PAYMENT_TYPE = Messages.getString("PaymentBlock.Zahlungsart_15"); //$NON-NLS-1$
	//	private static final String AMOUNT_FOREIGN_CURRENCY = Messages.getString("PaymentBlock.Betrag_FW_16"); //$NON-NLS-1$
	//	private static final String AMOUNT = Messages.getString("PaymentBlock.Betrag_17"); //$NON-NLS-1$
}
