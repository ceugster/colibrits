/*
 * Created on 26.01.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.printing;

import org.jdom.Element;

import ch.eugster.pos.devices.printers.POSPrinter;
import ch.eugster.pos.util.NumberUtility;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ReceiptRow
{
	
	/**
	 * 
	 */
	public ReceiptRow(Element element, ReceiptSection section)
	{
		super();
		this.section = section;
		this.id = NumberUtility.parseInt(0, element.getAttributeValue("id")); //$NON-NLS-1$
		this.font = NumberUtility.parseInt(section.getFont(), element.getAttributeValue("font")); //$NON-NLS-1$
		//		columnCount = NumberUtility.parseInt(0, element.getAttributeValue("cols")); //$NON-NLS-1$
		Element[] columnList = (Element[]) element.getChildren("col").toArray(new Element[0]); //$NON-NLS-1$
		this.cols = new ReceiptColumn[columnList.length];
		
		for (int i = 0; i < columnList.length; i++)
		{
			int index = Integer.parseInt(columnList[i].getAttributeValue("id")); //$NON-NLS-1$
			if (index <= i)
			{
				this.cols[index] = new ReceiptColumn(columnList[i], this);
				this.rowWidth += this.cols[index].getWidth();
			}
		}
	}
	
	public void print(POSPrinter printer, String[] values)
	{
		boolean print = false;
		for (int i = 0; i < Math.min(values.length, this.cols.length); i++)
		{
			if (values[i] != null && !values[i].equals("")) { //$NON-NLS-1$
				print = true;
			}
		}
		if (print)
		{
			for (int i = 0; i < Math.min(values.length, this.cols.length); i++)
			{
				if (this.cols[i] != null && values[i] != null)
				{
					this.cols[i].print(printer, values[i]);
				}
			}
			printer.print(System.getProperty("line.separator")); //$NON-NLS-1$
		}
	}
	
	public String getText(String[] values)
	{
		StringBuffer printOut = new StringBuffer("");
		boolean print = false;
		for (int i = 0; i < Math.min(values.length, this.cols.length); i++)
		{
			if (values[i] != null && !values[i].equals("")) { //$NON-NLS-1$
				print = true;
			}
		}
		if (print)
		{
			for (int i = 0; i < Math.min(values.length, this.cols.length); i++)
			{
				if (this.cols[i] != null && values[i] != null)
				{
					printOut = printOut.append(this.cols[i].getText(values[i]));
				}
			}
			printOut = printOut.append("\n");
		}
		return printOut.toString();
	}
	
	public void print(POSPrinter printer, String value)
	{
		this.cols[0].print(printer, value);
		printer.print(System.getProperty("line.separator")); //$NON-NLS-1$
	}
	
	public String getText(String value)
	{
		return this.cols[0].getText(value) + "\n";
	}
	
	public int getId()
	{
		return this.id;
	}
	
	public ReceiptColumn[] getColumns()
	{
		return this.cols;
	}
	
	public ReceiptSection getSection()
	{
		return this.section;
	}
	
	public int getFont()
	{
		return this.font;
	}
	
	public int getRowWidth()
	{
		return this.rowWidth;
	}
	
	private ReceiptSection section;
	private int id;
	private int font;
	private ReceiptColumn[] cols;
	private int rowWidth = 0;
}
