/*
 * Created on 07.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.events;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import ch.eugster.pos.Messages;
import ch.eugster.pos.client.gui.Frame;
import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.client.model.ReceiptChildTableModel;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.Key;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.db.Table;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ParkAction extends ModeChangeAction implements ReceiptChangeListener
{
	
	public static final long serialVersionUID = 0l;
	
	/**
	 * @param context
	 * @param key
	 */
	public ParkAction(UserPanel context, Key key)
	{
		super(context, key);
		this.init(key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 */
	public ParkAction(UserPanel context, Key key, String name)
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
	public ParkAction(UserPanel context, Key key, String name, Icon icon)
	{
		super(context, key, name, icon);
		this.init(key);
	}
	
	private void init(Key key)
	{
		this.putValue(Action.POS_KEY_ACTION_STATE, ParkAction.DISABLED);
		this.context.getReceiptModel().addReceiptChangeListener(this);
		this.context.getReceiptModel().getPositionTableModel().addReceiptChangeListener(this);
		this.context.getReceiptModel().getPaymentTableModel().addReceiptChangeListener(this);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		Integer state = (Integer) this.getValue(Action.POS_KEY_ACTION_STATE);
		if (state.equals(ParkAction.DISABLED))
		{
			return;
		}
		
		if (state.equals(ParkAction.SHOW_PARK_LIST))
		{
			int ok = MessageDialog.BUTTON_YES;
			if (this.context.getReceiptModel().getPositionTableModel().getState() != ReceiptChildTableModel.LIST_STATE_EMPTY
							|| this.context.getReceiptModel().getPositionTableModel().getState() != ReceiptChildTableModel.LIST_STATE_EMPTY)
			{
				ok = MessageDialog.showSimpleDialog(Frame.getMainFrame(), Messages.getString("ParkAction.Parkliste_2"), Messages
								.getString("ParkAction.Der_aktuelle_Beleg_wurde_noch_nicht_gespeichert._Wollen_Sie_die_n_u00C4nderungen_verwerfen_und_die_Parkliste_sehen__1"), 1);
			}
			if (ok == MessageDialog.BUTTON_YES)
			{
				this.putActionType(Action.POS_ACTION_SHOW_PARK_LIST);
				this.addPosEventListener(this.context.getChildrenBlock().getParkedReceiptTableBlock());
				super.actionPerformed(e);
				this.removePosEventListener(this.context.getChildrenBlock().getParkedReceiptTableBlock());
				this.fireModeChangeEvent(new ModeChangeEvent(UserPanel.CONTEXT_MODE_PARKED_RECEIPT_LIST));
			}
		}
		else if (state.equals(ParkAction.PARK_RECEIPT))
		{
			this.putActionType(Action.POS_ACTION_PARK_RECEIPT);
			this.addPosEventListener(this.context.getReceiptModel());
			super.actionPerformed(e);
			this.removePosEventListener(this.context.getReceiptModel());
			this.context.getReceiptModel().addReceiptChangeListener(this.context.getChildrenBlock().getTotalBlock());
			this.context.getReceiptModel().addReceiptChangeListener(this.context.getChildrenBlock().getReceivedBackBlock()); // *
			this.context.getReceiptModel().prepareReceipt();
			this.context.getReceiptModel().removeReceiptChangeListener(this.context.getChildrenBlock().getTotalBlock());
			this.context.getReceiptModel().removeReceiptChangeListener(this.context.getChildrenBlock().getReceivedBackBlock()); // *
		}
	}
	
	public void receiptChangePerformed(ReceiptChangeEvent e)
	{
		if (this.context.getReceiptModel().getState() == Receipt.RECEIPT_STATE_NEW || this.context.getReceiptModel().getState() == Receipt.RECEIPT_STATE_PARKED)
		{
			this.putValue("enabled", new Boolean(true)); //$NON-NLS-1$
		}
		else
		{
			this.putValue("enabled", new Boolean(false)); //$NON-NLS-1$
		}
		this.setText();
	}
	
	public void modeChangePerformed(ModeChangeEvent e)
	{
		if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_LOCK))
		{
			this.putValue("enabled", new Boolean(false)); //$NON-NLS-1$
		}
		else if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_PARKED_RECEIPT_LIST))
		{
			this.putValue("enabled", new Boolean(false)); //$NON-NLS-1$
		}
		else if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_CURRENT_RECEIPT_LIST))
		{
			this.putValue("enabled", new Boolean(false)); //$NON-NLS-1$
		}
		else
		{
			this.putValue("enabled", new Boolean(true)); //$NON-NLS-1$
		}
		this.setText();
	}
	
	private void setText()
	{
		if (this.context.getReceiptModel().getPositionTableModel().getState() == ReceiptChildTableModel.LIST_STATE_EMPTY
						&& this.context.getReceiptModel().getPositionTableModel().getState() == ReceiptChildTableModel.LIST_STATE_EMPTY)
		{
			int count = 0;
			try
			{
				if (!Database.getCurrent().getBroker().isInTransaction())
				{
					Database.getCurrent().getBroker().beginTransaction();
					count = Receipt.select(this.context.getUser(), Receipt.RECEIPT_STATE_PARKED).length;
					Database.getCurrent().getBroker().commitTransaction();
				}
			}
			catch (Exception e)
			{
				if (Database.isSwitchable())
				{
					String msg = "Die Verbindung zur Datenbank " + Database.getCurrent().getName() + " wurde unterbrochen. Es wird versucht, die Verbindung zur Ersatzdatenbank aufzubauen.";
					MessageDialog.showInformation(Frame.getMainFrame(), "Verbindungsfehler", msg, MessageDialog.TYPE_INFORMATION);
					Table.switchDatabase();
				}
				else
				{
					String msg = "Die Verbindung zur Datenbank " + Database.getCurrent().getName() + " wurde unterbrochen. Das Programm wird beendet.";
					MessageDialog.showInformation(Frame.getMainFrame(), "Verbindungsfehler", msg, MessageDialog.TYPE_INFORMATION);
					Frame.getMainFrame().closeApplication(true);
				}
			}
			finally
			{
				this.putValue(Action.NAME, Messages.getString("ParkAction.Parkliste_(_9") + count + ")"); //$NON-NLS-1$ //$NON-NLS-2$
				this.putValue(Action.POS_KEY_ACTION_STATE, ParkAction.SHOW_PARK_LIST);
			}
		}
		else
		{
			this.putValue(Action.NAME, Messages.getString("ParkAction.Parkieren_11")); //$NON-NLS-1$
			this.putValue(Action.POS_KEY_ACTION_STATE, ParkAction.PARK_RECEIPT);
		}
	}
	
	private static final Integer DISABLED = new Integer(0);
	private static final Integer SHOW_PARK_LIST = new Integer(1);
	private static final Integer PARK_RECEIPT = new Integer(2);
}
