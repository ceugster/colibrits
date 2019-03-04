/*
 * Created on 26.01.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.printing;

import java.util.StringTokenizer;

import org.jdom.Element;

import ch.eugster.pos.Messages;
import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.devices.printers.POSPrinter;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ReceiptFooter extends ReceiptSection
{
	
	/**
	 * 
	 */
	public ReceiptFooter(Element element, Element receipt)
	{
		super(element, receipt);
		
		String s = element.getAttributeValue("text"); //$NON-NLS-1$
		if (s != null)
		{
			StringTokenizer t = new StringTokenizer(s, "|"); //$NON-NLS-1$
			this.text = new String[t.countTokens()];
			int i = 0;
			while (t.hasMoreTokens())
			{
				this.text[i] = t.nextToken();
				if (this.text[i].equals("."))
				{
					this.text[i] = "";
				}
				i++;
			}
		}
	}
	
	public void print(POSPrinter printer, Receipt receipt)
	{
		this.setCharacterSettings(printer);
		
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
			if (values[0].equals("receipt.footer.text")) { //$NON-NLS-1$
				for (int k = 0; k < this.text.length; k++)
				{
					this.rows[i].print(printer, this.text[k]);
				}
			}
			else if (values[0].equals("user.name")) { //$NON-NLS-1$
				String un = Messages.getString("ReceiptFooter.Sie_wurden_bedient_von__1") + UserPanel.getCurrent().getUser().username; //$NON-NLS-1$
				this.rows[i].print(printer, un);
			}
			else
			{
				this.rows[i].print(printer, values);
			}
		}
	}
	
	public String getText(Receipt receipt)
	{
		StringBuffer printOut = new StringBuffer();
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
			if (values[0].equals("receipt.footer.text")) { //$NON-NLS-1$
				for (int k = 0; k < this.text.length; k++)
				{
					printOut = printOut.append(this.rows[i].getText(this.text[k]));
				}
			}
			else if (values[0].equals("user.name")) { //$NON-NLS-1$
				String un = Messages.getString("ReceiptFooter.Sie_wurden_bedient_von__1") + UserPanel.getCurrent().getUser().username; //$NON-NLS-1$
				printOut = printOut.append(this.rows[i].getText(un));
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
