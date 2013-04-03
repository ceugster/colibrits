/*
 * Created on 26.01.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.printing;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.jdom.Element;

import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.devices.printers.POSPrinter;
import ch.eugster.pos.util.Config;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ReceiptPosition extends ReceiptSection
{
	
	/**
	 * @param element
	 */
	public ReceiptPosition(Element element, Element receipt)
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
	@Override
	public void print(POSPrinter printer, Receipt receipt)
	{
		Position[] p = receipt.getPositionsAsArray();
		this.setCharacterSettings(printer, this.printMode, this.font, this.emphasized);
		
		Arrays.sort(p);
		
		for (Position element : p)
		{
			this.printPosition(printer, element);
		}
	}
	
	public int printRuecknahmen(POSPrinter printer, Receipt receipt)
	{
		Position[] p = receipt.getPositionsAsArray();
		this.setCharacterSettings(printer, this.printMode, this.font, this.emphasized);
		
		Arrays.sort(p);
		
		Collection<Position> positions = new ArrayList<Position>();
		for (Position element : p)
		{
			if (element.getQuantity() < 0)
			{
				if (element.getProductGroup().type != ProductGroup.TYPE_INPUT
								&& element.getProductGroup().type != ProductGroup.TYPE_WITHDRAW)
				{
					positions.add(element);
				}
			}
		}
		if (positions.size() > 0)
		{
			printer.println("Rücknahmen");
			printer.println("----------");
			
			Position[] r = positions.toArray(new Position[0]);
			for (Position element : r)
			{
				this.printPosition(printer, element);
			}
			
			printer.println("");
			printer.println("Betrag/Gutschein erhalten:");
			printer.println("");
			printer.println("");
			printer.println("");
			
		}
		// 10387 Build 315
		return positions.size();
	}
	
	public String getText(Receipt receipt)
	{
		StringBuffer printOut = new StringBuffer();
		Position[] p = receipt.getPositionsAsArray();
		Arrays.sort(p);
		
		for (Position element : p)
		{
			printOut = printOut.append(this.getText(element));
		}
		return printOut.toString();
	}
	
	private String[][] formatPosition(Position p)
	{
		int max = 0;
		for (ReceiptRow row : this.rows)
		{
			if (row.getColumns().length > max)
			{
				max = row.getColumns().length;
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
					if (columns[j].getValue().equals("position.galileoid")) { //$NON-NLS-1$
						values[i][j] = p.getProductGroup().galileoId;
					}
					else if (columns[j].getValue().equals("position.productgroup")) { //$NON-NLS-1$
						if (p.isPayedInvoice())
						{
							values[i][j] = "Rg." + p.getInvoiceNumber() + " bez.";
						}
						else if (p.getProductGroup().isDefault && p.productNumber.length() > 0)
						{
							values[i][j] = p.productNumber;
						}
						else
						{
							if (p.getProductGroup().shortname != null && p.getProductGroup().shortname.length() > 0)
							{
								values[i][j] = p.getProductGroup().shortname;
							}
							else if (p.getProductGroup().name != null && p.getProductGroup().name.length() > 0)
							{
								values[i][j] = p.getProductGroup().name;
							}
							else if (p.getProductGroup().galileoId != null
											&& p.getProductGroup().galileoId.length() > 0)
							{
								values[i][j] = p.getProductGroup().galileoId;
							}
							else if (!p.productNumber.equals(""))
							{
								values[i][j] = p.productNumber;
							}
							else
							{
								values[i][j] = "Ohne Bezeichnung";
							}
						}
					}
					else if (columns[j].getValue().equals("position.productid")) { //$NON-NLS-1$
						if (p.isPayedInvoice())
							values[i][j] = "Rg." + p.getInvoiceNumber() + " bez.";
						
						else if (p.productId.length() > 0)
						{
							values[i][j] = p.productId;
						}
						else
						{
							if (p.getProductGroup().shortname != null && p.getProductGroup().shortname.length() > 0)
							{
								values[i][j] = p.getProductGroup().shortname;
							}
							else if (p.getProductGroup().name != null && p.getProductGroup().name.length() > 0)
							{
								values[i][j] = p.getProductGroup().name;
							}
							else if (p.getProductGroup().galileoId != null
											&& p.getProductGroup().galileoId.length() > 0)
							{
								values[i][j] = p.getProductGroup().galileoId;
							}
							else
							{
								values[i][j] = "Ohne Bezeichnung";
							}
						}
					}
					else if (columns[j].getValue().equals("position.price")) { //$NON-NLS-1$
						DecimalFormat df = new DecimalFormat(columns[j].getPattern());
						if (p.type == ProductGroup.TYPE_INPUT || p.type == ProductGroup.TYPE_WITHDRAW)
						{
							if (p.getProductGroup().getForeignCurrency().getId()
											.equals(ForeignCurrency.getDefaultCurrency().getId()))
								values[i][j] = df.format(p.getPrice());
							else
								values[i][j] = df.format(p.amountFC);
						}
						else
						{
							values[i][j] = df.format(p.getPrice());
						}
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
		return values;
	}
	
	private void printPosition(POSPrinter printer, Position p)
	{
		boolean printTitleData = p.productId.length() > 0;
		// boolean printOrderData = p.ordered.booleanValue();
		boolean printDiscountData = p.getDiscount() != 0d;
		
		String[][] values = this.formatPosition(p);
		
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
					// if (printOrderData) {
					// rows[i].print(printer, values[i]);
					// }
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
	
	private String getText(Position p)
	{
		boolean printTitleData = p.productId.length() > 0;
		// boolean printOrderData = p.ordered.booleanValue();
		boolean printDiscountData = p.getDiscount() != 0d;
		
		StringBuffer printOut = new StringBuffer();
		String[][] values = this.formatPosition(p);
		
		for (int i = 0; i < values.length; i++)
		{
			switch (i)
			{
				case 0:
					printOut = printOut.append(this.rows[i].getText(values[i]));
					break;
				case 1:
					if (printTitleData)
					{
						printOut = printOut.append(this.rows[i].getText(values[i]));
					}
					break;
				case 2:
					// if (printOrderData) {
					// rows[i].print(printer, values[i]);
					// }
					break;
				case 3:
					if (printDiscountData)
					{
						printOut = printOut.append(this.rows[i].getText(values[i]));
					}
					break;
			}
		}
		return printOut.toString();
	}
	
}
