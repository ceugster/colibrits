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
public class ReceiptColumn
{
	
	/**
	 * 
	 */
	public ReceiptColumn(Element element, ReceiptRow row)
	{
		super();
		this.row = row;
		this.id = NumberUtility.parseInt(0, element.getAttributeValue("id")); //$NON-NLS-1$
		this.width = NumberUtility.parseInt(0, element.getAttributeValue("width")); //$NON-NLS-1$
		this.align = element.getAttributeValue("align"); //$NON-NLS-1$
		this.pattern = element.getAttributeValue("pattern"); //$NON-NLS-1$
		this.value = element.getText();
	}
	
	public void print(POSPrinter printer, String value)
	{
		if (this.width == 0)
		{
			this.width = printer.getColumnCount(this.row.getFont()) - this.row.getRowWidth();
		}
		if (value == null) value = ""; //$NON-NLS-1$
		if (value.length() >= this.width)
		{
			value = value.substring(0, this.width);
		}
		if (value.length() < this.width)
		{
			String space = this.getSpace(this.width - value.length());
			if (this.align.equals("center")) { //$NON-NLS-1$
				int leadingChars = space.length() / 2;
				String leadingSpace = this.getSpace(leadingChars);
				int trailingChars = space.length() - leadingChars;
				String trailingSpace = this.getSpace(trailingChars);
				value = leadingSpace.concat(value.concat(trailingSpace));
			}
			else if (this.align.equals("right")) { //$NON-NLS-1$
				value = space.concat(value);
			}
			else
			{
				// left ist default
				value = value.concat(space);
			}
		}
		printer.print(value);
	}
	
	public String getText(String value)
	{
		if (this.width == 0)
		{
			this.width = ReceiptColumn.COLUMN_COUNT - this.row.getRowWidth();
		}
		if (value == null) value = ""; //$NON-NLS-1$
		if (value.length() >= this.width)
		{
			value = value.substring(0, this.width);
		}
		if (value.length() < this.width)
		{
			String space = this.getSpace(this.width - value.length());
			if (this.align.equals("center")) { //$NON-NLS-1$
				int leadingChars = space.length() / 2;
				String leadingSpace = this.getSpace(leadingChars);
				int trailingChars = space.length() - leadingChars;
				String trailingSpace = this.getSpace(trailingChars);
				value = leadingSpace.concat(value.concat(trailingSpace));
			}
			else if (this.align.equals("right")) { //$NON-NLS-1$
				value = space.concat(value);
			}
			else
			{
				// left ist default
				value = value.concat(space);
			}
		}
		return value;
	}
	
	private String getSpace(int length)
	{
		char[] chars = new char[length];
		for (int i = 0; i < chars.length; i++)
		{
			chars[i] = ' ';
		}
		return String.valueOf(chars);
	}
	
	public int getId()
	{
		return this.id;
	}
	
	public String getValue()
	{
		return this.value;
	}
	
	public String getPattern()
	{
		return this.pattern != null ? this.pattern : ""; //$NON-NLS-1$
	}
	
	public int getWidth()
	{
		return this.width;
	}
	
	private ReceiptRow row;
	private int id;
	private int width;
	private String align;
	private String value;
	private String pattern;
	
	private static final int COLUMN_COUNT = 42;
}
