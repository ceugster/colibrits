/*
 * Created on 26.01.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.printing;

import java.text.DecimalFormat;

import org.jdom.Element;

import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.Payment;
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.db.Stock;
import ch.eugster.pos.devices.printers.POSPrinter;
import ch.eugster.pos.util.NumberUtility;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ReceiptSubtotal extends ReceiptSection
{
	
	/**
	 * @param element
	 */
	public ReceiptSubtotal(Element element, Element receipt)
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
		
		String[][] values = this.format(receipt);
		
		for (int i = 0; i < values.length; i++)
		{
			if (this.rows[i].getId() == 2)
			{
				this.rows[i].print(printer, values[i]);
			}
			else if (this.discount != 0)
			{
				this.rows[i].print(printer, values[i]);
			}
		}
	}
	
	public String getText(Receipt receipt)
	{
		StringBuffer printOut = new StringBuffer();
		String[][] values = this.format(receipt);
		
		for (int i = 0; i < values.length; i++)
		{
			if (this.rows[i].getId() == 2)
			{
				printOut = printOut.append(this.rows[i].getText(values[i]));
			}
			else if (this.discount != 0)
			{
				printOut = printOut.append(this.rows[i].getText(values[i]));
			}
		}
		return printOut.toString();
	}
	
	private void calculateDiscount(Receipt receipt)
	{
		this.total = this.calculateTotal(receipt);
		this.discount = NumberUtility.round(receipt.getAmount() - this.total, 0.01d);
	}
	
	// 10432
	private boolean calculateForeignCurrency(Receipt receipt)
	{
		ForeignCurrency currency = null;
		Stock[] stocks = (Stock[]) receipt.getSalespoint().getStocks().toArray(new Stock[0]);
		for (int i = 0; i < stocks.length; i++)
		{
			if (stocks[i].getForeignCurrency().getId().equals(ForeignCurrency.getDefaultCurrency().getId()))
			{
				this.dc = stocks[i].getForeignCurrency().code;
			}
			else
			{
				currency = stocks[i].getForeignCurrency();
				this.fc = currency.code;
			}
		}
		
		ForeignCurrency fc = null;
		if (currency != null)
		{
			this.amountFC = 0d;
			Payment[] p = receipt.getPaymentsAsArray();
			for (int i = 0; i < p.length; i++)
			{
				if (!p[i].getForeignCurrency().getId().equals(ForeignCurrency.getDefaultCurrency().getId()))
				{
					fc = p[i].getForeignCurrency();
				}
				this.amountFC = this.amountFC
								+ NumberUtility.round(p[i].getAmount() / currency.quotation, currency.roundFactor);
			}
			if (fc == null)
			{
				this.dc = "";
				this.fc = "";
			}
		}
		return fc != null;
	}
	
	// 10432
	
	private double calculateTotal(Receipt receipt)
	{
		double total = 0d;
		Position[] p = receipt.getPositionsAsArray();
		for (int i = 0; i < p.length; i++)
		{
			total = total + p[i].getPrice() * p[i].getQuantity();
		}
		return NumberUtility.round(total, 0.01d);
	}
	
	private String[][] format(Receipt receipt)
	{
		this.calculateDiscount(receipt);
		
		boolean hasForeignCurrency = this.calculateForeignCurrency(receipt);
		
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
					// 10432
					if (columns[j].getValue().equals("receipt.fc")) { //$NON-NLS-1$
						values[i][j] = this.fc;
					}
					else if (columns[j].getValue().equals("receipt.fc.amount")) { //$NON-NLS-1$
						if (hasForeignCurrency)
						{
							DecimalFormat df = new DecimalFormat(columns[j].getPattern());
							values[i][j] = df.format(this.amountFC);
						}
						else
						{
							values[i][j] = "";
						}
					}
					else if (columns[j].getValue().equals("receipt.dc")) { //$NON-NLS-1$
						values[i][j] = this.dc;
					}
					else if (columns[j].getValue().equals("receipt.dc.amount")) { //$NON-NLS-1$
						DecimalFormat df = new DecimalFormat(columns[j].getPattern());
						values[i][j] = df.format(receipt.getAmount());
					}
					// 10432
					else
					{
						if (columns[j].getValue().equals("receipt.total")
										|| columns[j].getValue().equals("receipt.discount"))
						{
							if (this.discount != 0d)
							{
								if (columns[j].getValue().equals("receipt.total")) { //$NON-NLS-1$
									DecimalFormat df = new DecimalFormat(columns[j].getPattern());
									values[i][j] = df.format(this.total);
								}
								else if (columns[j].getValue().equals("receipt.discount")) { //$NON-NLS-1$
									DecimalFormat df = new DecimalFormat(columns[j].getPattern());
									values[i][j] = df.format(this.discount);
								}
							}
						}
						else
						{
							values[i][j] = columns[j].getValue();
						}
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
	
	private double discount;
	private double total;
	
	private String dc;
	private double amount;
	private String fc;
	private double amountFC;
}
