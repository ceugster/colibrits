/*
 * Created on 26.01.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.printing;

import org.jdom.Element;

import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.devices.printers.POSPrinter;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ReceiptCustomer extends ReceiptSection
{
	
	/**
	 * 
	 */
	public ReceiptCustomer(Element element, Element receipt)
	{
		super(element, receipt);
		
		String s = element.getAttributeValue("text"); //$NON-NLS-1$
		if (s != null)
		{
			this.text = s.split("[|]");
			//			StringTokenizer t = new StringTokenizer(s, "|"); //$NON-NLS-1$
			// text = new String[t.countTokens()];
			// int i = 0;
			for (int i = 0; i < this.text.length; i++)
			{
				// text[i] = t.nextToken();
				if (this.text[i].equals("."))
				{
					this.text[i] = "";
				}
				// i++;
			}
		}
	}
	
	public void print(POSPrinter printer, Receipt receipt)
	{
		this.setCharacterSettings(printer);
		
		// 10171 Einführen einer Arbeitskopie von Text, weil sonst keine
		// Ersetzung weiter unten vorgenommen wird.
		String[] printingText = new String[this.text.length];
		for (int i = 0; i < this.text.length; i++)
		{
			printingText[i] = this.text[i];
		}
		// 10171
		
		// 10173
		// if (ProductServer.getInstance().getCustomer(new
		// Integer(receipt.getCustomerId()))) {
		// Customer customer = ProductServer.getInstance().getCustomerObject();
		// 10173
		for (int i = 0; i < this.rows.length; i++)
		{
			ReceiptRow row = this.rows[i];
			ReceiptColumn[] columns = row.getColumns();
			String[] values = new String[columns.length];
			for (int j = 0; j < columns.length; j++)
			{
				if (columns[j].getValue() != null)
				{
					values[j] = columns[j].getValue();
				}
				else
				{
					values[j] = ""; //$NON-NLS-1$
				}
			}
			if (values[0].equals("receipt.customer.text")) { //$NON-NLS-1$
				// 10171
				for (int k = 0; k < printingText.length; k++)
				{
					printingText[k] = printingText[k].replaceAll("<Nummer>", receipt.getCustomerId());
					// 10173
					printingText[k] = printingText[k]
									.replaceAll("<Konto>", receipt.getCustomer().getFormattedAccount());
					// 10173
					this.rows[i].print(printer, printingText[k]);
				}
				// 10171
			}
			else
			{
				this.rows[i].print(printer, values);
			}
		}
		// }
	}
	
	public String getText(Receipt receipt)
	{
		StringBuffer printOut = new StringBuffer();
		
		String[] printingText = new String[this.text.length];
		for (int i = 0; i < this.text.length; i++)
		{
			printingText[i] = this.text[i];
		}
		for (int i = 0; i < this.rows.length; i++)
		{
			ReceiptRow row = this.rows[i];
			ReceiptColumn[] columns = row.getColumns();
			String[] values = new String[columns.length];
			for (int j = 0; j < columns.length; j++)
			{
				if (columns[j].getValue() != null)
				{
					values[j] = columns[j].getValue();
				}
				else
				{
					values[j] = ""; //$NON-NLS-1$
				}
			}
			if (values[0].equals("receipt.customer.text")) { //$NON-NLS-1$
				for (int k = 0; k < printingText.length; k++)
				{
					printingText[k] = printingText[k].replaceAll("<Nummer>", receipt.getCustomerId());
					printingText[k] = printingText[k]
									.replaceAll("<Konto>", receipt.getCustomer().getFormattedAccount());
					printOut = printOut.append(this.rows[i].getText(printingText[k]));
				}
			}
			else
			{
				printOut = printOut.append(this.rows[i].getText(values));
			}
		}
		return printOut.toString();
	}
	
	private String[] text;
}
