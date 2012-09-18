/*
 * Created on 26.01.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.printing;

import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Hashtable;

import org.jdom.Element;

import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.Payment;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.devices.printers.POSPrinter;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ReceiptPayment extends ReceiptSection
{
	
	/**
	 * @param element
	 */
	public ReceiptPayment(Element element, Element receipt)
	{
		super(element, receipt);
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.client.printing.IReceiptSection#print(ch.eugster.pos.devices
	 * .printers.POSPrinter, ch.eugster.pos.db.Receipt)
	 */
	public void print(POSPrinter printer, Receipt receipt)
	{
		PaymentData back = null;
		// System.out.println();
		this.loadPayments(receipt);
		
		this.setCharacterSettings(printer, this.printMode, this.font, this.emphasized);
		
		ReceiptColumn[] columns = this.rows[0].getColumns();
		String[] values = new String[columns.length];
		for (int k = 0; k < columns.length; k++)
		{
			values[k] = this.load(new PaymentData(Payment.getEmptyInstance()), columns[k]);
		}
		this.rows[0].print(printer, values);
		
		Enumeration enumeration = this.usedPaymentTypes.elements();
		while (enumeration.hasMoreElements())
		{
			PaymentData pd = (PaymentData) enumeration.nextElement();
			if (pd.payment.isBack())
			{
				back = pd;
			}
			else
			{
				if (pd.foreignCurrency.getId().equals(ForeignCurrency.getDefaultCurrency().getId()))
				{
					columns = this.rows[1].getColumns();
					values = new String[columns.length];
					
					for (int k = 0; k < columns.length; k++)
					{
						values[k] = this.load(pd, columns[k]);
					}
					this.rows[1].print(printer, values);
				}
				else
				{
					columns = this.rows[2].getColumns();
					values = new String[columns.length];
					
					for (int k = 0; k < columns.length; k++)
					{
						values[k] = this.load(pd, columns[k]);
					}
					this.rows[2].print(printer, values);
				}
			}
		}
		
		if (back != null)
		{
			// back.foreignCurrency = receipt.getForeignCurrency();
			columns = this.rows[3].getColumns();
			values = new String[columns.length];
			
			for (int k = 0; k < columns.length; k++)
			{
				values[k] = this.load(back, columns[k]);
			}
			this.rows[3].print(printer, values);
		}
	}
	
	public String getText(Receipt receipt)
	{
		PaymentData back = null;
		
		this.loadPayments(receipt);
		
		StringBuffer printOut = new StringBuffer();
		ReceiptColumn[] columns = this.rows[0].getColumns();
		String[] values = new String[columns.length];
		for (int k = 0; k < columns.length; k++)
		{
			values[k] = this.load(new PaymentData(Payment.getEmptyInstance()), columns[k]);
		}
		printOut = printOut.append(this.rows[0].getText(values));
		
		Enumeration enumeration = this.usedPaymentTypes.elements();
		while (enumeration.hasMoreElements())
		{
			PaymentData pd = (PaymentData) enumeration.nextElement();
			if (pd.payment.isBack())
			{
				back = pd;
			}
			else
			{
				if (pd.foreignCurrency.getId().equals(ForeignCurrency.getDefaultCurrency().getId()))
				{
					columns = this.rows[1].getColumns();
					values = new String[columns.length];
					
					for (int k = 0; k < columns.length; k++)
					{
						values[k] = this.load(pd, columns[k]);
					}
					printOut = printOut.append(this.rows[1].getText(values));
				}
				else
				{
					columns = this.rows[2].getColumns();
					values = new String[columns.length];
					
					for (int k = 0; k < columns.length; k++)
					{
						values[k] = this.load(pd, columns[k]);
					}
					printOut = printOut.append(this.rows[2].getText(values));
				}
			}
		}
		
		if (back != null)
		{
			// back.foreignCurrency = receipt.getForeignCurrency();
			columns = this.rows[3].getColumns();
			values = new String[columns.length];
			
			for (int k = 0; k < columns.length; k++)
			{
				values[k] = this.load(back, columns[k]);
			}
			printOut = printOut.append(this.rows[3].getText(values));
		}
		
		return printOut.toString();
	}
	
	private void loadPayments(Receipt receipt)
	{
		this.usedPaymentTypes = new Hashtable();
		Payment[] p = receipt.getPaymentsAsArray();
		PaymentData data = null;
		for (int i = 0; i < p.length; i++)
		{
			if (p[i].isBack())
			{
				data = (PaymentData) this.usedPaymentTypes.get("b." + p[i].getPaymentType().getId());
			}
			else if (p[i].getForeignCurrencyId().equals(ForeignCurrency.getDefaultCurrency().getId()))
			{
				data = (PaymentData) this.usedPaymentTypes.get(p[i].getPaymentType().getId());
			}
			else
			{
				data = (PaymentData) this.usedPaymentTypes.get(p[i].getForeignCurrency().getId());
			}
			
			if (data == null)
			{
				data = new PaymentData(p[i]);
			}
			else if (p[i].getPaymentType().voucher && p[i].isBack())
			{
				data = new PaymentData(p[i]);
			}
			else
			{
				data.add(p[i]);
			}
			
			if (p[i].isBack())
			{
				this.usedPaymentTypes.put("b." + p[i].getPaymentType().getId(), data);
			}
			else if (p[i].getForeignCurrencyId().equals(ForeignCurrency.getDefaultCurrency().getId()))
			{
				this.usedPaymentTypes.put(p[i].getPaymentType().getId(), data);
			}
			else
			{
				this.usedPaymentTypes.put(p[i].getForeignCurrency().getId(), data);
			}
		}
	}
	
	private String load(PaymentData data, ReceiptColumn column)
	{
		String value;
		if (column.getValue() != null)
		{
			if (column.getValue().equals("payment.paymenttype")) { //$NON-NLS-1$
				value = data.foreignCurrency.code;
			}
			else if (column.getValue().equals("payment.code")) { //$NON-NLS-1$
				if (data.payment.getForeignCurrency().getId().equals(ForeignCurrency.getDefaultCurrency().getId()))
				{
					value = data.payment.getPaymentType().code;
				}
				else
				{
					value = data.payment.getForeignCurrency().code;
				}
			}
			else if (column.getValue().equals("payment.foreigncurrencyamount")) { //$NON-NLS-1$
				if (data.payment.getQuotation() != 1d)
				{
					DecimalFormat df = new DecimalFormat(column.getPattern());
					df.setGroupingUsed(true);
					value = df.format(data.foreignCurrencyAmount);
				}
				else
				{
					value = ""; //$NON-NLS-1$
				}
			}
			else if (column.getValue().equals("payment.quotation")) { //$NON-NLS-1$
				if (data.payment.getQuotation() != 1d)
				{
					DecimalFormat df = new DecimalFormat(column.getPattern());
					value = df.format(data.payment.getQuotation());
				}
				else
				{
					value = ""; //$NON-NLS-1$
				}
			}
			else if (column.getValue().equals("back.currency")) { //$NON-NLS-1$
			// if
			// (!data.foreignCurrency.code.equals(ForeignCurrency.getDefaultCurrency().code))
			// {
				value = data.foreignCurrency.code;
				// }
				// else
				// {
				//					value = ""; //$NON-NLS-1$
				// }
			}
			else if (column.getValue().equals("payment.amount")) { //$NON-NLS-1$
				DecimalFormat df = new DecimalFormat(column.getPattern());
				value = df.format(data.amount);
			}
			else if (column.getValue().equals("back.money")) { //$NON-NLS-1$
				value = data.text;
			}
			else
			{
				value = column.getValue();
			}
		}
		else
		{
			value = ""; //$NON-NLS-1$
		}
		return value;
	}
	
	private class PaymentData
	{
		private double amount = 0d;
		private double foreignCurrencyAmount = 0d;
		private Payment payment;
		private ForeignCurrency foreignCurrency; // nur für PaymentTypeBack,
		// damit der Kurs und die
		// Währung bekannt ist...
		private String text = "";
		
		private PaymentData(Payment p)
		{
			this.payment = p;
			this.foreignCurrency = p.getForeignCurrency();
			this.add(this.payment);
		}
		
		private void add(Payment payment)
		{
			if (payment.isBack())
			{
				this.text = payment.getPaymentType().voucher ? payment.getPaymentType().code : "Rückgeld";
				this.amount += payment.getAmount();
				this.foreignCurrencyAmount += payment.getAmountFC();
			}
			else
			{
				this.amount += payment.getAmount();
				this.foreignCurrencyAmount += payment.getAmountFC();
			}
		}
	}
	
	Hashtable usedPaymentTypes;
}
