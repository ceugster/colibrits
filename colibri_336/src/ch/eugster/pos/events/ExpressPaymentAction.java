/*
 * Created on 01.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.events;

import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import java.util.Currency;

import javax.swing.Icon;

import ch.eugster.pos.client.gui.Frame;
import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.client.model.ReceiptModel;
import ch.eugster.pos.db.CustomKey;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.Key;
import ch.eugster.pos.db.Payment;
import ch.eugster.pos.db.PaymentType;
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.util.Config;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ExpressPaymentAction extends ModeDependendAction implements ICheckPaymentAmount
{
	public final static long serialVersionUID = 0l;
	
	/**
	 * @param context
	 * @param key
	 */
	public ExpressPaymentAction(UserPanel context, Key key)
	{
		super(context, key);
		this.init(key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 */
	public ExpressPaymentAction(UserPanel context, Key key, String name)
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
	public ExpressPaymentAction(UserPanel context, Key key, String name, Icon icon)
	{
		super(context, key, name, icon);
		this.init(key);
	}
	
	private void init(Key key)
	{
		this.maxAmount = Math.abs(Config.getInstance().getInputDefaultMaxPaymentRange());
		this.maxPaymentAmount = Math.abs(Config.getInstance().getInputDefaultMaxPaymentAmount());
		this.showMessageListener = Frame.getMainFrame();
		this.addPosEventListener(this.context.getReceiptModel());
	}
	
	public void actionPerformed(ActionEvent e)
	{
		ReceiptModel rm = this.context.getReceiptModel();
		Receipt r = rm.getReceipt();
		PaymentType pt = (PaymentType) this.getValue(Action.POS_KEY_PAYMENT_TYPE);
		
		double value = 0d;
		if (this.key instanceof CustomKey)
		{
			value = ((CustomKey) this.key).value.doubleValue();
		}
		if (value != 0d)
		{
			this.context.setCurrentForeignCurrency(pt.getForeignCurrency());
			Payment payment = Payment.getInstance(r);
			payment.setPaymentType(pt);
			payment.setAmountFC(value);
			rm.getPaymentTableModel().store(payment);
		}
		else
		{
			double input = this.context.getNumericBlock().moveAmount();
			if (input == 0d)
			{
				if (pt.getForeignCurrency().getId().equals(ForeignCurrency.getDefaultCurrency().getId()))
				{
					if (!rm.balance())
					{
						this.context.setCurrentForeignCurrency(pt.getForeignCurrency());
						Payment payment = Payment.getInstance(r);
						payment.setPaymentType(pt);
						payment.setAmount(rm.getDifference());
						rm.getPaymentTableModel().store(payment);
					}
					else
					{
						this.context.setCurrentForeignCurrency(pt.getForeignCurrency());
					}
				}
				else
				{
					/*
					 * Es wurde eine Fremdwährungstaste ohne Betragseingabe oder
					 * Fixbetrag betätigt
					 */
					if (this.context.getCurrentForeignCurrency().getId().equals(pt.getForeignCurrencyId()))
					{
						this.context.setCurrentForeignCurrency(ForeignCurrency.getDefaultCurrency());
					}
					else
					{
						this.context.setCurrentForeignCurrency(pt.getForeignCurrency());
					}
				}
			}
			else
			{
				if (this.verifyAmount(input, pt.getForeignCurrency().getCurrency()))
				{
					this.context.setCurrentForeignCurrency(pt.getForeignCurrency());
					Payment payment = Payment.getInstance(r);
					payment.setPaymentType(pt);
					payment.setAmountFC(input);
					rm.getPaymentTableModel().store(payment);
				}
			}
		}
	}
	
	public boolean verifyAmount(double amount)
	{
		return this.verifyAmount(amount, ForeignCurrency.getDefaultCurrency().getCurrency());
	}
	
	public boolean verifyAmount(double amount, Currency currency)
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance();
		nf.setCurrency(currency);
		String value = nf.format(Math.abs(amount));
		if (Math.abs(amount) > this.maxAmount)
		{
			ShowMessageEvent event = new ShowMessageEvent("Der Betrag " + value + " ist zu hoch.", "Betrag", MessageDialog.TYPE_INFORMATION);
			this.showMessageListener.showMessage(event);
			return false;
		}
		int answer = MessageDialog.BUTTON_YES;
		if (Math.abs(amount) > this.maxPaymentAmount)
		{
			ShowMessageEvent event = new ShowMessageEvent("Der Betrag " + value + " ist sehr hoch. Wollen Sie ihn trotzdem verwenden?", "Betrag", MessageDialog.TYPE_QUESTION);
			answer = this.showMessageListener.showMessage(event);
		}
		return answer == MessageDialog.BUTTON_YES ? true : false;
	}
	
	public void setShowMessageListener(ShowMessageListener listener)
	{
		this.showMessageListener = listener;
	}
	
	public void modeChangePerformed(ModeChangeEvent e)
	{
		super.modeChangePerformed(e);
		if (this.getValue("enabled").equals(new Boolean(true)))
		{
			Receipt receipt = this.context.getReceiptModel().getReceipt();
			Position[] positions = receipt.getPositionsAsArray();
			for (int i = 0; i < positions.length; i++)
			{
				int type = positions[i].getProductGroup().type;
				if (type == ProductGroup.TYPE_INPUT || type == ProductGroup.TYPE_WITHDRAW)
				{
					this.putValue("enabled", new Boolean(false)); //$NON-NLS-1$
				}
			}
		}
	}
	
	private double maxAmount;
	private double maxPaymentAmount;
	private ShowMessageListener showMessageListener;
}
