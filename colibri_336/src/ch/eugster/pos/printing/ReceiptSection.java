/*
 * Created on 26.01.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.printing;

import org.jdom.Element;

import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.db.ReceiptChild;
import ch.eugster.pos.devices.printers.POSPrinter;
import ch.eugster.pos.util.NumberUtility;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public abstract class ReceiptSection implements IReceiptSection
{
	
	public ReceiptSection(Element element, Element receipt)
	{
		super();
		this.font = NumberUtility.parseInt(0, receipt.getAttributeValue("font")); //$NON-NLS-1$
		this.font = NumberUtility.parseInt(this.font, element.getAttributeValue("font")); //$NON-NLS-1$
		this.rowCount = NumberUtility.parseInt(0, element.getAttributeValue("rows")); //$NON-NLS-1$
		Element[] rowList = (Element[]) element.getChildren("row").toArray(new Element[0]); //$NON-NLS-1$
		this.rows = new ReceiptRow[this.rowCount];
		
		for (int i = 0; i < Math.min(this.rowCount, rowList.length); i++)
		{
			int index = Integer.parseInt(rowList[i].getAttributeValue("id")); //$NON-NLS-1$
			if (index <= i)
			{
				this.rows[index] = new ReceiptRow(rowList[i], this);
			}
		}
	}
	
	protected void setCharacterSettings(POSPrinter printer)
	{
		this.setCharacterSettings(printer, this.printMode, this.font, this.emphasized);
	}
	
	protected void setCharacterSettings(POSPrinter printer, int printmode, int font, boolean emphasized)
	{
		printer.selectPrintMode(printmode);
		printer.selectCharacterFont(font);
		printer.setEmphasized(emphasized);
	}
	
	public void print(POSPrinter printer, Receipt receipt)
	{
	}
	
	public void print(POSPrinter printer, ReceiptChild child)
	{
	}
	
	public int getRowCount()
	{
		return this.rowCount;
	}
	
	public int getFont()
	{
		return this.font;
	}
	
	public boolean getEmphasized()
	{
		return this.emphasized;
	}
	
	public int getPrintMode()
	{
		return this.printMode;
	}
	
	protected int rowCount;
	protected ReceiptRow[] rows;
	protected int font;
	protected boolean emphasized;
	protected int printMode;
	
}
