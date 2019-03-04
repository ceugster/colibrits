/*
 * Created on 25.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.events;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Icon;

import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.client.model.PaymentModel;
import ch.eugster.pos.client.model.PositionModel;
import ch.eugster.pos.client.model.ReceiptChildModel;
import ch.eugster.pos.db.Key;
import ch.eugster.pos.db.PaymentType;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class EnterAction extends ModeChangeAction implements ReceiptChildChangeListener
{
	public static final long serialVersionUID = 0l;
	
	/**
	 * @param context
	 * @param key
	 */
	public EnterAction(UserPanel context, Key key)
	{
		super(context, key);
		this.init(key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 */
	public EnterAction(UserPanel context, Key key, String name)
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
	public EnterAction(UserPanel context, Key key, String name, Icon icon)
	{
		super(context, key, name, icon);
		this.init(key);
	}
	
	/**
	 * @param key
	 */
	public void init(Key key)
	{
		this.context.getReceiptModel().getPositionModel().addReceiptChildChangeListener(this);
		this.context.getReceiptModel().getPaymentModel().addReceiptChildChangeListener(this);
	}
	
	/**
	 * @param actionEvent
	 */
	public void actionPerformed(ActionEvent e)
	{
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Enter pressed...");
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Position mode " + this.context.getMode());
		if (this.context.getMode().equals(UserPanel.CONTEXT_MODE_POS))
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Position mode...");
			this.positionAction(e);
		}
		else if (this.context.getMode().equals(UserPanel.CONTEXT_MODE_PAY))
		{
			this.paymentAction(e);
		}
		this.context.getNumericBlock().moveValue();
	}
	
	private void positionAction(ActionEvent e)
	{
		String value = this.context.getNumericBlock().readValue();
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Entered value: " + (value == null ? "Null" : value));
		PositionModel pm = this.context.getReceiptModel().getPositionModel();
		if (value.trim().length() == 0 && pm.isFresh())
		{
			// In den Zahlungsmodus gehen...
			this.fireModeChangeEvent(new ModeChangeEvent(UserPanel.CONTEXT_MODE_PAY));
			return;
		}
		
		if (!this.addChild(this.context.getReceiptModel().getPositionModel(), value, e))
		{
			// if (value.length() > 6)
			// {
			this.context.getChildrenBlock().getPositionBlock()
							.setValues(this.context.getReceiptModel().getPositionModel());
			this.context.getChildrenBlock().getPositionBlock()
							.display(this.context.getReceiptModel().getPositionModel());
			// }
			// else
			// {
			// if
			// (this.context.getReceiptModel().getCurrentPositionField().equals(PositionModel.FIELD_QUANTITY))
			// {
			//					this.context.getChildrenBlock().getPositionBlock().getButton("quantity").doClick(); //$NON-NLS-1$
			// }
			// else if
			// (this.context.getReceiptModel().getCurrentPositionField().equals(PositionModel.FIELD_PRICE))
			// {
			//					this.context.getChildrenBlock().getPositionBlock().getButton("price").doClick(); //$NON-NLS-1$
			// }
			// }
		}
	}
	
	private void paymentAction(ActionEvent e)
	{
		String value = this.context.getNumericBlock().readValue();
		PaymentModel pm = this.context.getReceiptModel().getPaymentModel();
		if (!pm.isComplete())
		{
			if (value.equals("")) { //$NON-NLS-1$
				this.fireModeChangeEvent(new ModeChangeEvent(UserPanel.CONTEXT_MODE_POS));
				return;
			}
			else
			{
				double d = this.context.getNumericBlock().moveAmount();
				if (pm.verifyAmount(d))
				{
					pm.setPaymentType(PaymentType.getPaymentTypeCash());
					pm.setForeignCurrencyAmount(d);
				}
			}
		}
		
		if (pm.isComplete())
		{
			this.addChild(this.context.getReceiptModel().getPaymentModel(), value, e);
		}
	}
	
	public boolean addChild(PositionModel pm, String value, ActionEvent e)
	{
		if (!value.equals("")) { //$NON-NLS-1$
			if (!pm.setData(value))
			{
				this.context.getNumericBlock().display(pm);
			}
		}
		
		boolean isComplete = pm.isComplete();
		if (isComplete)
		{
			this.addPosEventListener(this.context.getReceiptModel().getPositionTableModel());
			this.putActionType(Action.POS_ACTION_STORE_ENTRY);
			super.actionPerformed(e);
			this.removePosEventListener(this.context.getReceiptModel().getPositionTableModel());
			return isComplete;
		}
		else if (pm.getPosition().productId.length() > 0)
		{
			if (!pm.onlyPriceIsMissing())
			{
				Toolkit.getDefaultToolkit().beep();
			}
		}
		return false;
	}
	
	public void addChild(PaymentModel pm, String value, ActionEvent e)
	{
		if (pm.isComplete())
		{
			this.addPosEventListener(this.context.getReceiptModel().getPaymentTableModel());
			super.actionPerformed(e);
			this.removePosEventListener(this.context.getReceiptModel().getPaymentTableModel());
		}
	}
	
	public void modeChangePerformed(ModeChangeEvent e)
	{
		if (e.getSource(ModeChangeEvent.KEY_NUMERIC_BLOCK).equals(ModeChangeEvent.KEY_NUMERIC_BLOCK))
		{
			if (this.context.getMode().equals(UserPanel.CONTEXT_MODE_POS))
			{
				this.setState(this.context.getReceiptModel().getPositionModel());
			}
			else if (this.context.getMode().equals(UserPanel.CONTEXT_MODE_PAY))
			{
				this.setState(this.context.getReceiptModel().getPaymentModel());
			}
		}
		else if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_POS))
		{
			this.setState(this.context.getReceiptModel().getPositionModel());
		}
		else if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_PAY))
		{
			this.setState(this.context.getReceiptModel().getPaymentModel());
		}
		else
		{
			this.setEnabled(false);
		}
	}
	
	// public boolean verifyAmount(Double amount)
	// {
	// return this.verifyAmount(amount,
	// ForeignCurrency.getDefaultCurrency().getCurrency());
	// }
	
	// public boolean verifyAmount(Double amount, Currency currency)
	// {
	// NumberFormat nf = NumberFormat.getCurrencyInstance();
	// nf.setCurrency(currency);
	// String value = nf.format(Math.abs(amount.doubleValue()));
	// if (Math.abs(amount.doubleValue()) > this.maxAmount)
	// {
	// MessageDialog.showSimpleDialog(Frame.getMainFrame(), "Betrag",
	// "Der Betrag " + value + " ist zu hoch.", MessageDialog.TYPE_INFORMATION);
	// return false;
	// }
	// int answer = MessageDialog.BUTTON_YES;
	// if (Math.abs(amount.doubleValue()) > this.maxPaymentAmount)
	// {
	// answer = MessageDialog.showSimpleDialog(Frame.getMainFrame(), "Betrag",
	// "Der Betrag " + value
	// + " ist sehr hoch. Wollen Sie ihn trotzdem verwenden?",
	// MessageDialog.TYPE_QUESTION);
	// }
	// return answer == MessageDialog.BUTTON_YES ? true : false;
	// }
	
	public void receiptChildChangePerformed(ReceiptChildChangeEvent e)
	{
		this.setState(e.getReceiptChildModel());
	}
	
	public void setState(ReceiptChildModel rcm)
	{
		if (rcm.isComplete())
		{
			this.putValue(Action.NAME, EnterAction.INPUT);
			this.setEnabled(true);
		}
		else
		{
			if (rcm instanceof PositionModel)
			{
				PositionModel pm = (PositionModel) rcm;
				if (pm.isComplete())
				{
					this.putValue(Action.NAME, EnterAction.PAYMENTS);
				}
				else if (pm.isFresh())
				{
					this.putValue(Action.NAME, EnterAction.PAYMENTS);
				}
				else if (PositionModel.getCurrentField(pm.getPosition()) == PositionModel.FIELD_QUANTITY)
				{
					this.putValue(Action.NAME, this.context.getChildrenBlock().getPositionBlock()
									.getButton("quantity").getText()); //$NON-NLS-1$
				}
				else if (PositionModel.getCurrentField(pm.getPosition()) == PositionModel.FIELD_PRICE)
				{
					this.putValue(Action.NAME, this.context.getChildrenBlock().getPositionBlock()
									.getButton("price").getText()); //$NON-NLS-1$
				}
				this.setEnabled(true);
			}
			else if (rcm instanceof PaymentModel)
			{
				String value = this.context.getNumericBlock().readValue();
				if (value.equals("")) { //$NON-NLS-1$
					this.putValue(Action.NAME, EnterAction.POSITIONS);
				}
				else
				{
					this.putValue(Action.NAME, EnterAction.INPUT);
				}
				this.setEnabled(true);
			}
		}
	}
	
	private static final String POSITIONS = "Positionen"; //$NON-NLS-1$
	private static final String PAYMENTS = "Zahlungen"; //$NON-NLS-1$
	private static final String INPUT = "Eingabe"; //$NON-NLS-1$
}
