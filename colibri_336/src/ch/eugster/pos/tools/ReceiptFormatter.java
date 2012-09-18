/*
 * Created on 29.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.tools;

import org.jdom.Element;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.printing.ReceiptCustomer;
import ch.eugster.pos.printing.ReceiptFooter;
import ch.eugster.pos.printing.ReceiptHeader;
import ch.eugster.pos.printing.ReceiptPayment;
import ch.eugster.pos.printing.ReceiptPosition;
import ch.eugster.pos.printing.ReceiptSubtotal;
import ch.eugster.pos.printing.ReceiptTax;
import ch.eugster.pos.util.Config;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ReceiptFormatter
{
	
	private ReceiptFormatter()
	{
		Element receipt = Config.getInstance().getDocument().getRootElement().getChild("receipt"); //$NON-NLS-1$
		this.header = new ReceiptHeader(receipt.getChild("header"), receipt); //$NON-NLS-1$
		this.position = new ReceiptPosition(receipt.getChild("position"), receipt); //$NON-NLS-1$
		this.subtotal = new ReceiptSubtotal(receipt.getChild("subtotal"), receipt); //$NON-NLS-1$
		this.payment = new ReceiptPayment(receipt.getChild("payment"), receipt); //$NON-NLS-1$
		//		this.total = new ReceiptTotal(receipt.getChild("total"), receipt); //$NON-NLS-1$
		this.tax = new ReceiptTax(receipt.getChild("tax"), receipt); //$NON-NLS-1$
		this.footer = new ReceiptFooter(receipt.getChild("footer"), receipt); //$NON-NLS-1$
		// 10157
		this.customer = new ReceiptCustomer(receipt.getChild("customer"), receipt); //$NON-NLS-1$
		// 10157
		//		this.doCut = new Boolean(receipt.getAttributeValue("cut")).booleanValue(); //$NON-NLS-1$
		//		this.cutType = receipt.getAttributeValue("cut-type"); //$NON-NLS-1$
		//		this.lfBeforeCut = NumberUtility.parseInt(6, receipt.getAttributeValue("lfbeforecut")); //$NON-NLS-1$
		this.delimit = new Boolean(receipt.getAttributeValue("delimit")).booleanValue(); //$NON-NLS-1$
		this.delimiter = receipt.getAttributeValue("delimiter"); //$NON-NLS-1$
		
		// Element voucher =
		// Config.getInstance().getDocument().getRootElement().getChild("voucher");
		// this.voucherPrintLogo = new
		// Boolean(voucher.getAttributeValue("printlogo")).booleanValue();
		//		this.voucherLogo = NumberUtility.parseInt(1, voucher.getAttributeValue("logo")); //$NON-NLS-1$
		//		this.voucherLogoMode = NumberUtility.parseInt(0, voucher.getAttributeValue("logomode")); //$NON-NLS-1$
		
	}
	
	public static ReceiptFormatter getInstance()
	{
		if (ReceiptFormatter.receiptPrinter == null)
		{
			ReceiptFormatter.receiptPrinter = new ReceiptFormatter();
		}
		return ReceiptFormatter.receiptPrinter;
	}
	
	// private String[] getTokens(String data)
	// {
	// if (data == null)
	// {
	// return new String[]
	//			{ "" }; //$NON-NLS-1$
	// }
	//		StringTokenizer t = new StringTokenizer(data, "|"); //$NON-NLS-1$
	// int i = t.countTokens();
	// String[] s = new String[i];
	// for (int j = 0; j < i; j++)
	// {
	// s[j] = t.nextToken();
	// }
	// return s;
	// }
	//	
	public String getText(Receipt receipt)
	{
		StringBuffer printOut = new StringBuffer();
		if (receipt.status == Receipt.RECEIPT_STATE_REVERSED)
		{
			String storno = Messages.getString("ReceiptPrinter._S_T_O_R_N_O__1"); //$NON-NLS-1$
			int cols = (ReceiptFormatter.COLUMN_COUNT - storno.length()) / 2;
			String stars = this.format("*", cols); //$NON-NLS-1$
			printOut = printOut.append(this.format(stars + storno + stars, ReceiptFormatter.COLUMN_COUNT));
		}
		
		printOut = printOut.append("\n" + this.header.getText(receipt));
		if (this.delimit)
		{
			printOut = printOut.append("\n" + this.format(this.delimiter, ReceiptFormatter.COLUMN_COUNT) + "\n");
		}
		
		printOut = printOut.append(this.position.getText(receipt));
		if (this.delimit)
		{
			printOut = printOut.append("\n" + this.format(this.delimiter, ReceiptFormatter.COLUMN_COUNT) + "\n");
		}
		
		printOut = printOut.append(this.subtotal.getText(receipt));
		if (this.delimit)
		{
			printOut = printOut.append("\n" + this.format(this.delimiter, ReceiptFormatter.COLUMN_COUNT) + "\n");
		}
		
		printOut = printOut.append(this.payment.getText(receipt));
		if (this.delimit)
		{
			printOut = printOut.append("\n" + this.format(this.delimiter, ReceiptFormatter.COLUMN_COUNT) + "\n");
		}
		
		printOut = printOut.append(this.tax.getText(receipt));
		if (this.delimit)
		{
			printOut = printOut.append("\n" + this.format(this.delimiter, ReceiptFormatter.COLUMN_COUNT) + "\n");
		}
		
		printOut = printOut.append(this.footer.getText(receipt));
		if (this.delimit)
		{
			printOut = printOut.append("\n" + this.format(this.delimiter, ReceiptFormatter.COLUMN_COUNT) + "\n");
		}
		
		// 10157
		if (receipt.hasCustomer())
		{
			if (receipt.getCustomer().hasCard())
			{
				printOut = printOut.append(this.customer.getText(receipt));
				if (this.delimit)
				{
					printOut = printOut
									.append("\n" + this.format(this.delimiter, ReceiptFormatter.COLUMN_COUNT) + "\n");
				}
			}
		}
		// 10157
		
		if (receipt.status == Receipt.RECEIPT_STATE_REVERSED)
		{
			String storno = Messages.getString("ReceiptPrinter._S_T_O_R_N_O__3"); //$NON-NLS-1$
			int cols = (ReceiptFormatter.COLUMN_COUNT - storno.length()) / 2;
			String stars = this.format("*", cols); //$NON-NLS-1$
			printOut = printOut.append(this.format(stars + storno + stars, ReceiptFormatter.COLUMN_COUNT) + "\n");
		}
		return printOut.toString();
	}
	
	private String format(String s, int length)
	{
		if (s == null)
		{
			s = " "; //$NON-NLS-1$
		}
		else if (s.equals("")) { //$NON-NLS-1$
			s = " "; //$NON-NLS-1$
		}
		while (s.length() < length)
		{
			s = s.concat(s);
		}
		return s.substring(0, length);
	}
	
	private ReceiptHeader header;
	private ReceiptPosition position;
	private ReceiptSubtotal subtotal;
	private ReceiptPayment payment;
	// private ReceiptTotal total;
	private ReceiptTax tax;
	private ReceiptFooter footer;
	private ReceiptCustomer customer;
	
	// private boolean voucherPrintLogo;
	// private int voucherLogo;
	// private int voucherLogoMode;
	
	// private boolean doCut;
	// private String cutType;
	// private int lfBeforeCut;
	private boolean delimit = true;
	private String delimiter = "*"; //$NON-NLS-1$
	
	private static ReceiptFormatter receiptPrinter = null;
	
	public static final int COLUMN_COUNT = 42;
	
}
