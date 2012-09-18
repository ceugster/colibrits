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
import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.db.CustomKey;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.Key;
import ch.eugster.pos.db.Payment;
import ch.eugster.pos.db.PaymentType;
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.product.ProductServer;

/**
 * @author Administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class StoreReceiptAction extends ModeDependendAction implements ReceiptChangeListener
{
	
	public static final long serialVersionUID = 0l;
	
	protected ShowMessageListener messageListener;
	
	/**
	 * @param context
	 * @param key
	 */
	public StoreReceiptAction(UserPanel context, Key key)
	{
		super(context, key);
		this.init(key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 */
	public StoreReceiptAction(UserPanel context, Key key, String name)
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
	public StoreReceiptAction(UserPanel context, Key key, String name, Icon icon)
	{
		super(context, key, name, icon);
		this.init(key);
	}
	
	private void init(Key key)
	{
		if (key instanceof CustomKey) this.paymentType = ((CustomKey) key).getPaymentType();
		
		this.putValue(Action.POS_KEY_DRAWER_NUMBER, new Integer(key.actionType.intValue() - Action.POS_ACTION_STORE_RECEIPT.intValue() - 1));
		
		this.context.getReceiptModel().getPaymentTableModel().addReceiptChangeListener(this);
		this.context.getReceiptModel().getPositionTableModel().addReceiptChangeListener(this);
		this.addPosEventListener(this.context.getReceiptModel());
		this.setShowMessageListener(Frame.getMainFrame());
	}
	
	public void setCashDrawer(int number)
	{
		this.putValue(Action.POS_KEY_DRAWER_NUMBER, new Integer(number));
	}
	
	public int getCashDrawer()
	{
		int drawer = -1;
		Object obj = this.getValue(Action.POS_KEY_DRAWER_NUMBER);
		if (!(obj == null))
		{
			drawer = ((Integer) this.getValue(Action.POS_KEY_DRAWER_NUMBER)).intValue();
		}
		return drawer;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (this.context.getReceiptModel().getPositionTableModel().getRowCount() > 0)
		{
			if (this.context.getReceiptModel().balance())
			{
				// 10375
				this.testForInputOrWithdraw(this.context.getReceiptModel().getReceipt());
				super.actionPerformed(e);
				if (ProductServer.getInstance().getMessage().length() > 0)
				{
					ShowMessageEvent event = new ShowMessageEvent("Der Beleg enthält eine bezahlte Rechnung, die nicht ordnungsgemäss verbucht werden konnte. Die Nachricht von Galileo lautet:\n"
									+ ProductServer.getInstance().getMessage(), "Bezahlte Rechnung", MessageDialog.TYPE_INFORMATION);
					this.messageListener.showMessage(event);
					ProductServer.getInstance().setMessage("");
				}
			}
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
	
	public void setState(AbstractEvent e)
	{
		if (this.context.getReceiptModel().getPositionTableModel().getRowCount() > 0)
		{
			if (this.context.getReceiptModel().balance())
			{
				ForeignCurrency currency = null;
				Receipt receipt = this.context.getReceiptModel().getReceipt();
				Position[] positions = receipt.getPositionsAsArray();
				for (int i = 0; i < positions.length; i++)
				{
					int type = positions[i].getProductGroup().type;
					if (type == ProductGroup.TYPE_INPUT || type == ProductGroup.TYPE_WITHDRAW)
					{
						if (currency == null)
						{
							currency = positions[i].getProductGroup().getForeignCurrency();
							break;
						}
					}
				}
				
				if (currency == null)
					this.putValue("enabled", new Boolean(true)); //$NON-NLS-1$
				else
				{
					PaymentType type = ((CustomKey) this.getKey()).getPaymentType();
					if (currency.getId().equals(ForeignCurrency.getDefaultCurrency().getId()))
					{
						if (type.getForeignCurrency().getId().equals(currency.getId()))
							this.putValue("enabled", new Boolean(currency.getId().equals(PaymentType.getPaymentTypeCash().getId())));
						else
							this.putValue("enabled", new Boolean(false));
					}
					else
						this.putValue("enabled", new Boolean(currency.getId().equals(type.getForeignCurrency().getId())));
				}
			}
			else
			{
				this.putValue("enabled", new Boolean(false)); //$NON-NLS-1$
			}
		}
		else
		{
			this.putValue("enabled", new Boolean(false)); //$NON-NLS-1$
		}
	}
	
	public void setShowMessageListener(ShowMessageListener listener)
	{
		this.messageListener = listener;
	}
	
	/*
	 * 10375
	 */
	protected void testForInputOrWithdraw(Receipt r)
	{
		boolean isInputOrWithdraw = false;
		Position[] positions = r.getPositionsAsArray();
		for (int i = 0; i < positions.length; i++)
		{
			int type = positions[i].getProductGroup().type;
			if (type == ProductGroup.TYPE_INPUT || type == ProductGroup.TYPE_WITHDRAW) isInputOrWithdraw = true;
		}
		if (isInputOrWithdraw)
		{
			Payment[] payments = r.getPaymentsAsArray();
			for (int i = 0; i < payments.length; i++)
			{
				((Payment) r.getPayments().elementAt(i)).isInputOrWithdraw = true;
			}
		}
	}
	
	protected PaymentType paymentType;
}
