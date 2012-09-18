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
import ch.eugster.pos.util.NumberUtility;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class StoreReceiptVoucherAction extends StoreReceiptAction implements ICheckPaymentAmount
{
	
	public static final long serialVersionUID = 0l;
	
	/**
	 * @param context
	 * @param key
	 */
	public StoreReceiptVoucherAction(UserPanel context, Key key)
	{
		super(context, key);
		this.init(key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 */
	public StoreReceiptVoucherAction(UserPanel context, Key key, String name)
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
	public StoreReceiptVoucherAction(UserPanel context, Key key, String name, Icon icon)
	{
		super(context, key, name, icon);
		this.init(key);
	}
	
	private void init(Key key)
	{
		/*
		 * Der Listener ReceiptModel wird in der Superklasse registriert
		 */

		this.maxAmount = Config.getInstance().getInputDefaultMaxPaymentRange();
		this.maxPaymentAmount = Config.getInstance().getInputDefaultMaxPaymentAmount();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		ReceiptModel rm = this.context.getReceiptModel();
		Receipt r = rm.getReceipt();
		// 10226
		PaymentType pt = ((CustomKey) this.key).getPaymentType();
		// PaymentType pt = PaymentType.getPaymentVoucher();
		// 10226
		if (this.context.getPaymentTypeBlock().getTabCurrency() != null)
		{
			pt.setForeignCurrency(this.context.getPaymentTypeBlock().getTabCurrency());
		}
		
		if (!r.isBalanced())
		{
			double diff = NumberUtility.round(r.getAmount() - r.getPayment(), r.getDefaultCurrency().getCurrency().getDefaultFractionDigits());
			if (diff > 0d)
			{
				Payment payment = Payment.getInstance(r);
				payment.setPaymentType(pt);
				
				// payment.setForeignCurrency(r.getForeignCurrency());
				
				double total = r.getAmount();
				double amount = r.getPayment();
				
				double input = this.context.getNumericBlock().moveAmount();
				if (input == 0D)
				{
					double a = total - amount;
					a = NumberUtility.round(a, payment.getForeignCurrency().roundFactor);
					payment.setAmount(a);
				}
				else
				{
					if (this.verifyAmount(input))
					{
						payment.setAmountFC(input);
					}
					
				}
				rm.getPaymentTableModel().store(payment);
			}
		}
		// 10375
		this.testForInputOrWithdraw(r);
		
		super.actionPerformed(e);
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
			MessageDialog.showSimpleDialog(Frame.getMainFrame(), "Betrag", "Der Betrag " + value + " ist zu hoch.", MessageDialog.TYPE_INFORMATION);
			return false;
		}
		int answer = MessageDialog.BUTTON_YES;
		if (Math.abs(amount) > this.maxPaymentAmount)
		{
			answer = MessageDialog.showSimpleDialog(Frame.getMainFrame(), "Betrag", "Der Betrag " + value + " ist sehr hoch. Wollen Sie ihn trotzdem verwenden?", MessageDialog.TYPE_QUESTION);
		}
		return answer == MessageDialog.BUTTON_YES ? true : false;
	}
	
	public void setState(AbstractEvent e)
	{
		if (this.context.getReceiptModel().getPositionTableModel().getRowCount() > 0)
		{
			if (this.context.getReceiptModel().getReceipt().getAmount() < this.context.getReceiptModel().getReceipt().getPayment())
			{
				if (this.context.getReceiptModel().getPaymentTableModel().isThereAVoucherPayment() || this.context.getReceiptModel().getPositionTableModel().isThereAGivenBackPosition())
				{
					
					Receipt receipt = this.context.getReceiptModel().getReceipt();
					Position[] positions = receipt.getPositionsAsArray();
					boolean enable = true;
					for (int i = 0; i < positions.length; i++)
					{
						int type = positions[i].getProductGroup().type;
						if (type == ProductGroup.TYPE_INPUT || type == ProductGroup.TYPE_WITHDRAW)
						{
							enable = false;
						}
					}
					this.putValue("enabled", new Boolean(enable)); //$NON-NLS-1$
				}
			}
		}
		else
		{
			this.putValue("enabled", new Boolean(false)); //$NON-NLS-1$
		}
	}
	
	private double maxAmount;
	private double maxPaymentAmount;
}
