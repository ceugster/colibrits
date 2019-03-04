/*
 * Created on 02.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.client.model;

import java.text.NumberFormat;
import java.util.Currency;

import javax.swing.JTable;

import ch.eugster.pos.Messages;
import ch.eugster.pos.client.gui.Frame;
import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.Payment;
import ch.eugster.pos.db.PaymentType;
import ch.eugster.pos.db.ReceiptChild;
import ch.eugster.pos.db.Table;
import ch.eugster.pos.events.Action;
import ch.eugster.pos.events.ClearAction;
import ch.eugster.pos.events.DeleteAction;
import ch.eugster.pos.events.ICheckPaymentAmount;
import ch.eugster.pos.events.MessageDialog;
import ch.eugster.pos.events.PaymentChangeAction;
import ch.eugster.pos.events.PosEvent;
import ch.eugster.pos.events.ReceiptChildChangeEvent;
import ch.eugster.pos.util.Config;
import ch.eugster.pos.util.NumberUtility;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PaymentModel extends ReceiptChildModel implements ICheckPaymentAmount
{
	
	public PaymentModel(ReceiptModel receiptModel, JTable table)
	{
		super(receiptModel, table);
		this.init(Payment.getInstance(receiptModel.getReceipt()));
	}
	
	public PaymentModel(ReceiptModel receiptModel, JTable table, Payment payment)
	{
		super(receiptModel, table);
		this.init(payment);
	}
	
	private void init(Payment payment)
	{
		this.maxRange = Math.abs(Config.getInstance().getInputDefaultMaxPriceRange());
		this.maxAmount = Math.abs(Config.getInstance().getInputDefaultMaxPriceAmount());
		
		this.setPayment(payment);
	}
	
	public void setReceiptChild()
	{
		this.setPayment();
	}
	
	public void setReceiptChild(ReceiptChild child)
	{
		this.setPayment((Payment) child);
	}
	
	public void setPayment(Payment payment)
	{
		this.child = payment;
		this.fireReceiptChildChangeEvent(new ReceiptChildChangeEvent(this));
	}
	
	public void setPayment()
	{
		this.setPayment(Payment.getInstance(this.receiptModel.getReceipt()));
	}
	
	public Payment getPayment()
	{
		return (Payment) this.child;
	}
	
	public void posEventPerformed(PosEvent e)
	{
		Action a = e.getPosAction();
		if (a instanceof PaymentChangeAction)
		{
			if (a.getActionType().equals(Action.POS_ACTION_SET_PAYMENT_TYPE))
			{
				double amount = ((Double) a.getValue(Action.POS_KEY_AMOUNT)).doubleValue();
				if (this.verifyAmount(amount))
				{
					this.setPaymentType((PaymentType) a.getValue(Action.POS_KEY_PAYMENT_TYPE));
					this.setForeignCurrencyAmount(amount);
				}
			}
		}
		else if (a instanceof ClearAction)
		{
			if (a.getValue(Action.POS_KEY_ACTION_TYPE).equals(Action.POS_ACTION_CLEAR))
			{
				this.setPayment();
			}
		}
		else if (a instanceof DeleteAction)
		{
			if (a.getValue(Action.POS_KEY_ACTION_TYPE).equals(Action.POS_ACTION_DELETE_ENTRY))
			{
				this.setPayment();
			}
		}
		this.fireReceiptChildChangeEvent(new ReceiptChildChangeEvent(this));
	}
	
	public boolean verifyAmount(double amount)
	{
		return this.verifyAmount(amount, ForeignCurrency.getDefaultCurrency().getCurrency());
	}
	
	public boolean verifyAmount(double amount, Currency currency)
	{
		double testAmount = Math.abs(amount);
		NumberFormat nf = NumberFormat.getCurrencyInstance();
		nf.setCurrency(currency);
		String value = nf.format(testAmount);
		if (testAmount > this.maxRange)
		{
			MessageDialog.showSimpleDialog(Frame.getMainFrame(), "Betrag", "Der Betrag " + value + " ist zu hoch.", MessageDialog.TYPE_INFORMATION);
			return false;
		}
		int answer = MessageDialog.BUTTON_YES;
		if (testAmount > this.maxAmount)
		{
			answer = MessageDialog.showSimpleDialog(Frame.getMainFrame(), "Betrag", "Der Betrag " + value + " ist sehr hoch. Wollen Sie ihn trotzdem verwenden?", MessageDialog.TYPE_QUESTION);
		}
		return answer == MessageDialog.BUTTON_YES ? true : false;
	}
	
	public void setForeignCurrencyAmount(double amount)
	{
		((Payment) this.child).setAmountFC(NumberUtility.round(amount, ReceiptChildModel.roundFactorAmount));
		this.fireReceiptChildChangeEvent(new ReceiptChildChangeEvent(this));
	}
	
	public double getForeignCurrencyAmount()
	{
		return ((Payment) this.child).getAmountFC();
	}
	
	public void setAmount(double amount, double quotation)
	{
		((Payment) this.child).setAmount(NumberUtility.round(amount * quotation, ReceiptChildModel.roundFactorAmount));
	}
	
	public double getAmount()
	{
		return ((Payment) this.child).getAmount();
	}
	
	// private void setForeignCurrency(ForeignCurrency foreignCurrency) {
	// ((Payment) child).setForeignCurrency(foreignCurrency);
	// this.fireReceiptChildChangeEvent(new ReceiptChildChangeEvent(this));
	// }
	
	public void setQuotation(double quotation)
	{
		((Payment) this.child).setQuotation(NumberUtility.round(quotation, 0.000001D));
	}
	
	public double getQuotation()
	{
		return ((Payment) this.child).getQuotation();
	}
	
	public void setPaymentType(PaymentType paymentType)
	{
		((Payment) this.child).setPaymentType(paymentType);
	}
	
	public PaymentType getPaymentType()
	{
		return ((Payment) this.child).getPaymentType();
	}
	
	public String getName()
	{
		return ((Payment) this.child).getPaymentType().name;
	}
	
	// **********************************************
	
	public static String getValueAt(Payment payment, int index)
	{
		String value = ""; //$NON-NLS-1$
		switch (index)
		{
			case 0:
				value = PaymentModel.getDisplayName(payment);
				break;
			case 1:
				value = PaymentModel.getDisplayForeignCurrencyAmount(payment);
				break;
			case 2:
				value = PaymentModel.getDisplayQuotation(payment);
				break;
			case 3:
				value = PaymentModel.getDisplayAmount(payment);
				break;
		}
		return value;
	}
	
	public static String getDisplayAmount(Payment payment)
	{
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMinimumFractionDigits(ForeignCurrency.getDefaultCurrency().getCurrency().getDefaultFractionDigits());
		nf.setMaximumFractionDigits(ForeignCurrency.getDefaultCurrency().getCurrency().getDefaultFractionDigits());
		nf.setGroupingUsed(true);
		return nf.format(payment.getAmount());
	}
	
	public static String getDisplayQuotation(Payment payment)
	{
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMinimumFractionDigits(0);
		nf.setMaximumFractionDigits(6);
		nf.setGroupingUsed(false);
		return nf.format(payment.getQuotation());
	}
	
	public static String getDisplayForeignCurrencyAmount(Payment payment)
	{
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMinimumFractionDigits(payment.getForeignCurrency().getCurrency().getDefaultFractionDigits());
		nf.setMaximumFractionDigits(payment.getForeignCurrency().getCurrency().getDefaultFractionDigits());
		nf.setGroupingUsed(true);
		return nf.format(payment.getAmountFC());
	}
	
	public static String getDisplayName(Payment payment)
	{
		String name = ""; //$NON-NLS-1$
		if (payment.getQuotation() == 1D)
		{
			name = payment.getPaymentType().name;
		}
		else
		{
			name = payment.getForeignCurrency().code.concat(" (" //$NON-NLS-1$
							.concat("1:") //$NON-NLS-1$
											.concat(NumberUtility.formatDouble(payment.getQuotation(), 6, 6, true)).concat(")")); //$NON-NLS-1$
		}
		return name;
	}
	
	public static String getMessage(Payment payment)
	{
		String s = ""; //$NON-NLS-1$
		if (UserPanel.getCurrent() != null && UserPanel.getCurrent().getReceiptModel().balance())
		{
			s = Messages.getString("PaymentModel.Abschliessen_1"); //$NON-NLS-1$
		}
		else if (payment.getPaymentTypeId() == null || payment.getPaymentTypeId().equals(Table.ZERO_VALUE))
		{
			s = Messages.getString("PaymentModel.Zahlungsart_oder_W_u00E4hrung__7"); //$NON-NLS-1$
		}
		else if (payment.getAmount() == 0)
		{
			s = Messages.getString("PaymentModel.Erhaltener_Betrag__8"); //$NON-NLS-1$
		}
		else if (payment.getQuotation() == 0)
		{
			s = Messages.getString("PaymentModel.Kurs__9"); //$NON-NLS-1$
		}
		else if (payment.isComplete())
		{
			s = Messages.getString("PaymentModel.Speichern_der_Zahlung__Eingabe_dr_u00FCcken_10"); //$NON-NLS-1$
		}
		return s;
	}
	
	public static Integer getCurrentField(Payment payment)
	{
		Integer i = PaymentModel.FIELD_EMPTY;
		if (payment.getAmount() == 0)
		{
			i = PaymentModel.FIELD_AMOUNT;
		}
		else if (payment.getQuotation() == 0)
		{
			i = PaymentModel.FIELD_QUOTATION;
		}
		return i;
	}
	
	public static String[] getValues(Payment p)
	{
		String[] columns = new String[PaymentModel.columnNames.length];
		columns[0] = PaymentModel.getDisplayName(p);
		columns[1] = PaymentModel.getDisplayQuotation(p);
		columns[2] = PaymentModel.getDisplayAmount(p);
		return columns;
	}
	
	public static String[] getColumnNames()
	{
		return PaymentModel.columnNames;
	}
	
	private static String[] columnNames =
	{ Messages.getString("PaymentModel.Bezeichnung_11"), //$NON-NLS-1$
					Messages.getString("PaymentModel.Kurs_12"), //$NON-NLS-1$
					Messages.getString("PaymentModel.Betrag_13") }; //$NON-NLS-1$
	
	public static final Integer FIELD_EMPTY = new Integer(0);
	public static final Integer FIELD_AMOUNT = new Integer(1);
	public static final Integer FIELD_QUOTATION = new Integer(2);
}
