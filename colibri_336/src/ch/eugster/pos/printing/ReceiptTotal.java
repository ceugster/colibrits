/*
 * Created on 26.01.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.printing;

import java.text.DecimalFormat;

import org.jdom.Element;

import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.devices.printers.POSPrinter;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ReceiptTotal extends ReceiptSection
{
	
	/**
	 * @param element
	 */
	public ReceiptTotal(Element element, Element receipt)
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
		this.setCharacterSettings(printer, this.printMode, this.font, this.emphasized);
		
		int max = 0;
		for (int i = 0; i < this.rows.length; i++)
		{
			if (this.rows[i].getColumns().length > max)
			{
				max = this.rows[i].getColumns().length;
			}
		}
		String[][] values = new String[this.rows.length][max];
		
		for (int i = 0; i < this.rows.length; i++)
		{
			ReceiptRow row = this.rows[i];
			ReceiptColumn[] columns = row.getColumns();
			for (int j = 0; j < columns.length; j++)
			{
				if (columns[j].getValue() != null)
				{
					if (columns[j].getValue().equals("receipt.payment")) { //$NON-NLS-1$
						DecimalFormat df = new DecimalFormat(columns[j].getPattern());
						values[i][j] = df.format(receipt.getPayment());
					}
					else
					{
						values[i][j] = columns[j].getValue();
					}
				}
				else
				{
					values[i][j] = ""; //$NON-NLS-1$
				}
			}
		}
		
		for (int i = 0; i < values.length; i++)
		{
			this.rows[i].print(printer, values[i]);
		}
	}
}
