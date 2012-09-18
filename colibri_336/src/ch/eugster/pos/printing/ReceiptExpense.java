/*
 * Created on 26.01.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.printing;

import java.text.DecimalFormat;

import org.jdom.Element;

import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.devices.printers.POSPrinter;
import ch.eugster.pos.util.Config;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ReceiptExpense extends ReceiptSection
{
	
	/**
	 * @param element
	 */
	public ReceiptExpense(Element element, Element receipt)
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
		Position[] p = receipt.getPositionsAsArray();
		this.setCharacterSettings(printer, this.printMode, this.font, this.emphasized);
		
		for (int i = 0; i < p.length; i++)
		{
			this.printPosition(printer, p[i]);
		}
		
	}
	
	private void printPosition(POSPrinter printer, Position p)
	{
		int max = 0;
		for (int i = 0; i < this.rows.length; i++)
		{
			if (this.rows[i].getColumns().length > max)
			{
				max = this.rows[i].getColumns().length;
			}
		}
		
		boolean printTitleData = p.productId.length() > 0;
		boolean printOrderData = p.ordered;
		boolean printDiscountData = p.getDiscount() != 0d;
		String[][] values = new String[this.rows.length][max];
		
		for (int i = 0; i < this.rows.length; i++)
		{
			ReceiptRow row = this.rows[i];
			ReceiptColumn[] columns = row.getColumns();
			for (int j = 0; j < columns.length; j++)
			{
				if (columns[j].getValue() != null)
				{
					if (columns[j].getValue().equals("position.galileoid")) { //$NON-NLS-1$
						values[i][j] = p.getProductGroup().galileoId;
					}
					else if (columns[j].getValue().equals("position.productgroup")) { //$NON-NLS-1$
						if (p.getProductGroup().isDefault && p.productId.length() > 0)
						{
							values[i][j] = p.productId;
						}
						else
						{
							values[i][j] = p.getProductGroup().shortname;
						}
					}
					else if (columns[j].getValue().equals("position.price")) { //$NON-NLS-1$
						DecimalFormat df = new DecimalFormat(columns[j].getPattern());
						values[i][j] = df.format(p.getPrice());
					}
					else if (columns[j].getValue().equals("position.quantity")) { //$NON-NLS-1$
						DecimalFormat df = new DecimalFormat(columns[j].getPattern());
						values[i][j] = df.format(p.getQuantity());
					}
					else if (columns[j].getValue().equals("position.amount")) { //$NON-NLS-1$
						DecimalFormat df = new DecimalFormat(columns[j].getPattern());
						values[i][j] = df.format(p.getGrossAmount());
					}
					else if (columns[j].getValue().equals("position.tax")) { //$NON-NLS-1$
						values[i][j] = p.getCurrentTax().getTax().galileoId;
					}
					else if (columns[j].getValue().equals("position.optcode")) { //$NON-NLS-1$
						values[i][j] = p.optCode;
					}
					else if (columns[j].getValue().equals("position.author")) { //$NON-NLS-1$
						if (new Boolean(Config.getInstance().getReceiptPosition()
										.getAttributeValue("print-second-line")).booleanValue())
						{
							if (p.author != null && p.author.length() > 0)
							{
								if (p.title != null && p.title.length() > 0)
								{
									values[i][j] = p.title;
								}
								else
								{
									values[i][j] = p.author;
								}
							}
							else
							{
								if (p.title != null && p.title.length() > 0)
								{
									values[i][j] = p.title;
								}
							}
							// if (p.publisher != null && p.publisher.length() >
							// 0) {
							// values[i][j] += p.publisher;
							// }
						}
					}
					else if (columns[j].getValue().equals("position.title")) { //$NON-NLS-1$
						values[i][j] = p.title;
					}
					else if (columns[j].getValue().equals("position.publisher")) { //$NON-NLS-1$
						values[i][j] = p.publisher;
					}
					else if (columns[j].getValue().equals("position.orderid")) { //$NON-NLS-1$
						values[i][j] = p.orderId;
					}
					else if (columns[j].getValue().equals("position.discount")) { //$NON-NLS-1$
						DecimalFormat df = new DecimalFormat(columns[j].getPattern());
						values[i][j] = df.format(p.getDiscount());
					}
					else if (columns[j].getValue().equals("position.discountamount")) { //$NON-NLS-1$
						DecimalFormat df = new DecimalFormat(columns[j].getPattern());
						values[i][j] = df.format(p.getDiscountAmount());
					}
					else if (columns[j].getValue().equals("position.netamount")) { //$NON-NLS-1$
						DecimalFormat df = new DecimalFormat(columns[j].getPattern());
						values[i][j] = df.format(p.getAmount());
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
			switch (i)
			{
				case 0:
					this.rows[i].print(printer, values[i]);
					break;
				case 1:
					if (printTitleData)
					{
						this.rows[i].print(printer, values[i]);
					}
					break;
				case 2:
					if (printOrderData)
					{
						this.rows[i].print(printer, values[i]);
					}
					break;
				case 3:
					if (printDiscountData)
					{
						this.rows[i].print(printer, values[i]);
					}
					break;
			}
		}
	}
}
