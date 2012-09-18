/*
 * Created on 26.01.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.printing;

import java.text.DecimalFormat;
import java.util.Hashtable;

import org.jdom.Element;

import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.db.Tax;
import ch.eugster.pos.devices.printers.POSPrinter;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ReceiptTax extends ReceiptSection
{
	
	/**
	 * @param element
	 */
	public ReceiptTax(Element element, Element receipt)
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
		this.buildUsedTaxes(receipt);
		
		this.setCharacterSettings(printer, this.printMode, this.font, this.emphasized);
		
		Tax[] tax = Tax.getAll(true);
		for (int i = 0; i < tax.length; i++)
		{
			TaxData data = (TaxData) this.usedTaxes.get(tax[i].getId());
			if (data != null)
			{
				for (int j = 0; j < this.rows.length; j++)
				{
					ReceiptColumn[] columns = this.rows[j].getColumns();
					String[] values = new String[columns.length];
					for (int k = 0; k < columns.length; k++)
					{
						if (columns[k].getValue() != null)
						{
							if (columns[k].getValue().equals("tax.name")) { //$NON-NLS-1$
								values[k] = tax[i].getTaxType().name;
							}
							else if (columns[k].getValue().equals("tax.percentage")) { //$NON-NLS-1$
								DecimalFormat df = new DecimalFormat(columns[k].getPattern());
								values[k] = df.format(tax[i].getCurrentTax().percentage / 100);
							}
							else if (columns[k].getValue().equals("tax.baseamount")) { //$NON-NLS-1$
								DecimalFormat df = new DecimalFormat(columns[k].getPattern());
								values[k] = df.format(data.baseAmount);
							}
							else if (columns[k].getValue().equals("tax.amount")) { //$NON-NLS-1$
								DecimalFormat df = new DecimalFormat(columns[k].getPattern());
								values[k] = df.format(-data.taxAmount);
							}
							else if (columns[k].getValue().equals("tax.code")) { //$NON-NLS-1$
								values[k] = tax[i].galileoId;
							}
							else
							{
								values[k] = columns[k].getValue();
							}
						}
						else
						{
							values[k] = ""; //$NON-NLS-1$
						}
					}
					this.rows[j].print(printer, values);
				}
			}
		}
	}
	
	private void buildUsedTaxes(Receipt receipt)
	{
		this.usedTaxes = new Hashtable();
		Position[] p = receipt.getPositionsAsArray();
		for (int i = 0; i < p.length; i++)
		{
			TaxData data = (TaxData) this.usedTaxes.get(p[i].getCurrentTax().getTax().getId());
			if (data == null)
			{
				data = new TaxData(p[i].getAmount(), p[i].getTaxAmount());
			}
			else
			{
				data.baseAmount += p[i].getAmount();
				data.taxAmount += p[i].getTaxAmount();
			}
			this.usedTaxes.put(p[i].getCurrentTax().getTax().getId(), data);
		}
	}
	
	public String getText(Receipt receipt)
	{
		this.buildUsedTaxes(receipt);
		
		StringBuffer printOut = new StringBuffer();
		Tax[] tax = Tax.getAll(true);
		for (int i = 0; i < tax.length; i++)
		{
			TaxData data = (TaxData) this.usedTaxes.get(tax[i].getId());
			if (data != null)
			{
				for (int j = 0; j < this.rows.length; j++)
				{
					ReceiptColumn[] columns = this.rows[j].getColumns();
					String[] values = new String[columns.length];
					for (int k = 0; k < columns.length; k++)
					{
						if (columns[k].getValue() != null)
						{
							if (columns[k].getValue().equals("tax.name")) { //$NON-NLS-1$
								values[k] = tax[i].getTaxType().name;
							}
							else if (columns[k].getValue().equals("tax.percentage")) { //$NON-NLS-1$
								DecimalFormat df = new DecimalFormat(columns[k].getPattern());
								values[k] = df.format(tax[i].getCurrentTax().percentage / 100);
							}
							else if (columns[k].getValue().equals("tax.baseamount")) { //$NON-NLS-1$
								DecimalFormat df = new DecimalFormat(columns[k].getPattern());
								values[k] = df.format(data.baseAmount);
							}
							else if (columns[k].getValue().equals("tax.amount")) { //$NON-NLS-1$
								DecimalFormat df = new DecimalFormat(columns[k].getPattern());
								values[k] = df.format(-data.taxAmount);
							}
							else if (columns[k].getValue().equals("tax.code")) { //$NON-NLS-1$
								values[k] = tax[i].galileoId;
							}
							else
							{
								values[k] = columns[k].getValue();
							}
						}
						else
						{
							values[k] = ""; //$NON-NLS-1$
						}
					}
					printOut = printOut.append(this.rows[j].getText(values));
				}
			}
		}
		return printOut.toString();
	}
	
	private class TaxData
	{
		public double baseAmount = 0d;
		public double taxAmount = 0d;
		
		private TaxData(double baseAmount, double taxAmount)
		{
			this.baseAmount = baseAmount;
			this.taxAmount = taxAmount;
		}
	}
	
	private Hashtable usedTaxes;
}
