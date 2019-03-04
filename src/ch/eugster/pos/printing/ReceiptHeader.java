/*
 * Created on 26.01.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.printing;

import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.jdom.Element;

import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.devices.printers.POSPrinter;
import ch.eugster.pos.util.NumberUtility;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ReceiptHeader extends ReceiptSection
{
	
	/**
	 * 
	 */
	public ReceiptHeader(Element element, Element receipt)
	{
		super(element, receipt);
		this.printLogo = new Boolean(element.getAttributeValue("printlogo")).booleanValue(); //$NON-NLS-1$
		this.logo = NumberUtility.parseInt(1, element.getAttributeValue("logo")); //$NON-NLS-1$
		this.logoMode = NumberUtility.parseInt(0, element.getAttributeValue("logomode")); //$NON-NLS-1$
		
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
	
	@Override
	public void print(POSPrinter printer, Receipt receipt)
	{
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Header wird gedruckt...");
		this.print(printer, receipt, this.printLogo, this.logo, this.logoMode);
		
	}
	
	public void print(POSPrinter printer, Receipt receipt, boolean printLogo, int logo, int logomode)
	{
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Druckvorgang läuft...");
		this.setCharacterSettings(printer);
		
		if (printLogo)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Logo drucken...");
			printer.printLogo(logo, this.logoMode);
			printer.print(System.getProperty("line.separator")); //$NON-NLS-1$
		}
		
		if (Database.getCurrent().equals(Database.getTutorial()))
		{
			printer.println("******************************************");
			printer.println("**  S C H U L U N G    S C H U L U N G  **");
			printer.println("******************************************");
		}
		
		for (ReceiptRow row : this.rows)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Headerzeilen drucken...");
			ReceiptColumn[] columns = row.getColumns();
			String[] values = new String[columns.length];
			for (int j = 0; j < columns.length; j++)
			{
				if (columns[j].getValue() != null)
				{
					if (columns[j].getValue().equals("receipt.number")) { //$NON-NLS-1$
						values[j] = receipt.getFormattedNumber();
					}
					else if (columns[j].getValue().equals("receipt.date")) { //$NON-NLS-1$
						SimpleDateFormat df = new SimpleDateFormat(columns[j].getPattern());
						values[j] = df.format(receipt.getDate());
					}
					else if (columns[j].getValue().equals("receipt.time")) { //$NON-NLS-1$
						SimpleDateFormat df = new SimpleDateFormat(columns[j].getPattern());
						values[j] = df.format(receipt.getTime());
					}
					else
					{
						values[j] = columns[j].getValue();
					}
				}
				else
				{
					values[j] = ""; //$NON-NLS-1$
				}
			}
			if (values[0].equals("receipt.header.text")) { //$NON-NLS-1$
				for (String element : this.text)
				{
					row.print(printer, element);
				}
			}
			else if (values[0].equals("receipt.header.salespoint")) { //$NON-NLS-1$
				row.print(printer, receipt.getSalespoint().name);
			}
			else
			{
				row.print(printer, values);
			}
		}
	}
	
	public String getText(Receipt receipt)
	{
		StringBuffer printOut = new StringBuffer();
		for (ReceiptRow row : this.rows)
		{
			ReceiptColumn[] columns = row.getColumns();
			String[] values = new String[columns.length];
			for (int j = 0; j < columns.length; j++)
			{
				if (columns[j].getValue() != null)
				{
					if (columns[j].getValue().equals("receipt.number")) { //$NON-NLS-1$
						values[j] = receipt.getFormattedNumber();
					}
					else if (columns[j].getValue().equals("receipt.date")) { //$NON-NLS-1$
						SimpleDateFormat df = new SimpleDateFormat(columns[j].getPattern());
						values[j] = df.format(receipt.getDate());
					}
					else if (columns[j].getValue().equals("receipt.time")) { //$NON-NLS-1$
						SimpleDateFormat df = new SimpleDateFormat(columns[j].getPattern());
						values[j] = df.format(receipt.getTime());
					}
					else
					{
						values[j] = columns[j].getValue();
					}
				}
				else
				{
					values[j] = ""; //$NON-NLS-1$
				}
			}
			if (values[0].equals("receipt.header.text")) { //$NON-NLS-1$
				for (String element : this.text)
				{
					printOut = printOut.append(row.getText(element));
				}
			}
			else if (values[0].equals("receipt.header.salespoint")) { //$NON-NLS-1$
				printOut = printOut.append(row.getText(receipt.getSalespoint().name));
			}
			else
			{
				printOut = printOut.append(row.getText(values));
			}
		}
		return printOut.toString();
	}
	
	private boolean printLogo = false;
	private int logo = 1;
	private int logoMode = 0;
	private String[] text;
}
