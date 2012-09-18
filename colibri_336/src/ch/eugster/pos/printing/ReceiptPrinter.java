/*
 * Created on 29.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.printing;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.jdom.Element;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.devices.printers.POSPrinter;
import ch.eugster.pos.util.Config;
import ch.eugster.pos.util.NumberUtility;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ReceiptPrinter
{
	
	private ReceiptPrinter(Element posPrinter)
	{
		this();
		this.loadPrinter(posPrinter);
	}
	
	private ReceiptPrinter()
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
		this.doCut = new Boolean(receipt.getAttributeValue("cut")).booleanValue(); //$NON-NLS-1$
		this.cutType = receipt.getAttributeValue("cut-type"); //$NON-NLS-1$
		this.lfBeforeCut = NumberUtility.parseInt(6, receipt.getAttributeValue("lfbeforecut")); //$NON-NLS-1$
		this.delimit = new Boolean(receipt.getAttributeValue("delimit")).booleanValue(); //$NON-NLS-1$
		this.delimiter = receipt.getAttributeValue("delimiter"); //$NON-NLS-1$
		
		Element voucher = Config.getInstance().getDocument().getRootElement().getChild("voucher");
		this.voucherPrintLogo = new Boolean(voucher.getAttributeValue("printlogo")).booleanValue();
		this.voucherLogo = NumberUtility.parseInt(1, voucher.getAttributeValue("logo")); //$NON-NLS-1$
		this.voucherLogoMode = NumberUtility.parseInt(0, voucher.getAttributeValue("logomode")); //$NON-NLS-1$
		
	}
	
	public static ReceiptPrinter getInstance(Element element)
	{
		if (ReceiptPrinter.receiptPrinter == null)
		{
			ReceiptPrinter.receiptPrinter = new ReceiptPrinter(element);
		}
		return ReceiptPrinter.receiptPrinter;
	}
	
	private void loadPrinter(Element element)
	{
		this.printer = this.createPOSPrinter(element);
	}
	
	private POSPrinter createPOSPrinter(Element el)
	{
		POSPrinter pos = null;
		try
		{
			Class cls = Class.forName(el.getAttributeValue("class")); //$NON-NLS-1$
			Class[] params = new Class[1];
			params[0] = Element.class;
			Constructor c = cls.getConstructor(params);
			Object[] ps = new Object[params.length];
			ps[0] = el;
			pos = (POSPrinter) c.newInstance(ps);
			// 2005-11-20
			// if (!pos.isPOSPrinterUsed()) {
			// pos = null;
			// }
			// 2005-11-20
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
			//			LogManager.getLogManager().getLogger("colibri").severe(Messages.getString("ReceiptPrinter.Instantiierung_fehlgeschlagen__Klasse__16") + el.getAttributeValue("class") + Messages.getString("ReceiptPrinter._nicht_gefunden.__18") + e.getLocalizedMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			System.exit(-22);
		}
		catch (NoSuchMethodException e)
		{
			//			LogManager.getLogManager().getLogger("colibri").severe(Messages.getString("ReceiptPrinter.Instantiierung_fehlgeschlagen__Kein_entsprechender_Konstruktor_fuer_Klasse__20") + el.getAttributeValue("class") + ". " + e.getLocalizedMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			System.exit(-22);
		}
		catch (IllegalAccessException e)
		{
			//			LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
			System.exit(-22);
		}
		catch (InvocationTargetException e)
		{
			//			LogManager.getLogManager().getLogger("colibri").severe(Messages.getString("ReceiptPrinter.Instantiierung_fehlgeschlagen__Kein_entsprechender_Konstruktor_fuer_Klasse__25") + el.getAttributeValue("class") + ". " + e.getLocalizedMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			System.exit(-22);
		}
		catch (InstantiationException e)
		{
			//			LogManager.getLogManager().getLogger("colibri").severe(Messages.getString("ReceiptPrinter.Instantiierung_fehlgeschlagen___29") + e.getLocalizedMessage()); //$NON-NLS-1$ //$NON-NLS-2$
			System.exit(-22);
		}
		return pos;
		
	}
	
	public POSPrinter getPrinter()
	{
		return this.printer;
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
	
	public void cleanup()
	{
		// 10012
		if (this.printer.isUsed())
		{
			this.printer.closePort();
		}
	}
	
	public void print(POSPrinter printer, Receipt[] receipts)
	{
		for (int i = 0; i < receipts.length; i++)
		{
			this.print(printer, receipts[i]);
		}
	}
	
	public void print(POSPrinter printer, Receipt receipt)
	{
		if (receipt.status == Receipt.RECEIPT_STATE_REVERSED)
		{
			String storno = Messages.getString("ReceiptPrinter._S_T_O_R_N_O__1"); //$NON-NLS-1$
			int cols = (printer.getColumnCount(this.header.getFont()) - storno.length()) / 2;
			String stars = ReceiptPrinter.format("*", cols); //$NON-NLS-1$
			printer.println(ReceiptPrinter.format(stars + storno + stars, printer.getColumnCount(this.header.getFont())));
		}
		
		this.header.print(printer, receipt);
		if (this.delimit)
		{
			printer.println(ReceiptPrinter.format(this.delimiter, printer.getColumnCount(this.header.getFont())));
		}
		
		this.position.print(printer, receipt);
		if (this.delimit)
		{
			printer.println(ReceiptPrinter.format(this.delimiter, printer.getColumnCount(this.position.getFont())));
		}
		
		this.subtotal.print(printer, receipt);
		if (this.delimit)
		{
			printer.println(ReceiptPrinter.format(this.delimiter, printer.getColumnCount(this.subtotal.getFont())));
		}
		
		this.payment.print(printer, receipt);
		if (this.delimit)
		{
			printer.println(ReceiptPrinter.format(this.delimiter, printer.getColumnCount(this.payment.getFont())));
		}
		
		// 10382 Build 312
		boolean print = false;
		Position[] positions = receipt.getPositionsAsArray();
		for (int i = 0; i < positions.length; i++)
			if (positions[i].getProductGroup().type != ProductGroup.TYPE_INPUT
							&& positions[i].getProductGroup().type != ProductGroup.TYPE_WITHDRAW)
			{
				print = true;
				break;
			}
		
		if (print)
		{
			this.tax.print(printer, receipt);
			if (this.delimit)
			{
				printer.println(ReceiptPrinter.format(this.delimiter, printer.getColumnCount(this.tax.getFont())));
			}
		}
		// 10382 Build 312
		
		// 10377 Build 310
		if (new Boolean(Config.getInstance().getReceipt().getAttributeValue("take-back-print-signature"))
						.booleanValue())
		{
			if (this.position.printRuecknahmen(printer, receipt) > 0) // 10387
			// Build 315
			{
				if (this.delimit)
				{
					printer.println(ReceiptPrinter.format(this.delimiter,
									printer.getColumnCount(this.payment.getFont())));
				}
			}
		}
		// 10377 Build 310
		
		this.footer.print(printer, receipt);
		if (this.delimit)
		{
			printer.println(ReceiptPrinter.format(this.delimiter, printer.getColumnCount(this.footer.getFont())));
		}
		
		// 10157
		if (receipt.hasCustomer())
		{
			if (receipt.getCustomer().hasCard())
			{
				this.customer.print(printer, receipt);
				if (this.delimit)
				{
					printer.println(ReceiptPrinter.format(this.delimiter,
									printer.getColumnCount(this.customer.getFont())));
				}
			}
		}
		// 10157
		
		if (receipt.status == Receipt.RECEIPT_STATE_REVERSED)
		{
			String storno = Messages.getString("ReceiptPrinter._S_T_O_R_N_O__3"); //$NON-NLS-1$
			int cols = (printer.getColumnCount(this.header.getFont()) - storno.length()) / 2;
			String stars = ReceiptPrinter.format("*", cols); //$NON-NLS-1$
			printer.println(ReceiptPrinter.format(stars + storno + stars, printer.getColumnCount(this.header.getFont())));
		}
		
		if (this.doCut)
		{
			if (this.cutType.equals("partial")) { //$NON-NLS-1$
				printer.partialCut(this.lfBeforeCut);
			}
			else if (this.cutType.equals("full")) { //$NON-NLS-1$
				printer.fullCut(this.lfBeforeCut);
			}
		}
	}
	
	public void printVoucher(POSPrinter printer, Receipt receipt)
	{
		this.header.print(printer, receipt, this.voucherPrintLogo, this.voucherLogo, this.voucherLogoMode);
		if (this.delimit)
		{
			printer.println(ReceiptPrinter.format(this.delimiter, printer.getColumnCount(this.header.getFont())));
		}
		
		printer.println("");
		printer.selectCharacterSizeDoubleHeight();
		printer.setEmphasized(true);
		printer.justifyCenter();
		printer.println("G U T S C H E I N");
		printer.println("=================");
		printer.println("");
		
		double back = -receipt.getBack();
		printer.println(ForeignCurrency.getFormattedAmount(back, ForeignCurrency.getDefaultCurrency()));
		
		printer.selectCharacterSizeNormal();
		
		if (this.doCut)
		{
			if (this.cutType.equals("partial")) { //$NON-NLS-1$
				printer.partialCut(this.lfBeforeCut);
			}
			else if (this.cutType.equals("full")) { //$NON-NLS-1$
				printer.fullCut(this.lfBeforeCut);
			}
		}
	}
	
	public void openCashDrawer(int drawer)
	{
		if (this.printer.isUsed())
		{
			this.printer.kickOutDrawer(drawer);
		}
	}
	
	public static boolean isUsed()
	{
		return ReceiptPrinter.receiptPrinter.getPrinter() == null ? false : ReceiptPrinter.receiptPrinter.getPrinter()
						.isUsed();
	}
	
	public static String format(String s, int length)
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
	
	private POSPrinter printer;
	
	private ReceiptHeader header;
	private ReceiptPosition position;
	private ReceiptSubtotal subtotal;
	private ReceiptPayment payment;
	// private ReceiptTotal total;
	private ReceiptTax tax;
	private ReceiptFooter footer;
	private ReceiptCustomer customer;
	
	private boolean voucherPrintLogo;
	private int voucherLogo;
	private int voucherLogoMode;
	
	private boolean doCut;
	private String cutType;
	private int lfBeforeCut;
	private boolean delimit = true;
	private String delimiter = "*"; //$NON-NLS-1$
	
	private static ReceiptPrinter receiptPrinter = null;
	
}
