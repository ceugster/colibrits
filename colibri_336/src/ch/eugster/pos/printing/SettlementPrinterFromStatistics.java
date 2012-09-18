/*
 * Created on 31.08.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.printing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.LogManager;

import ch.eugster.pos.Messages;
import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.db.Settlement;
import ch.eugster.pos.devices.printers.POSPrinter;
import ch.eugster.pos.util.Config;
import ch.eugster.pos.util.NumberUtility;
import ch.eugster.pos.util.Path;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class SettlementPrinterFromStatistics
{
	private Settlement[] settlements;
	
	/**
	 * 
	 */
	public SettlementPrinterFromStatistics(Long settlement)
	{
		this.settlement = settlement;
		
		this.properties = new Properties(this.defaultProperties());
		this.properties = this.getProperties();
		
		this.generalCut = new Boolean((String) this.properties.get("general.cut")).booleanValue(); //$NON-NLS-1$
		this.generalCutType = (String) this.properties.get("general.cuttype"); //$NON-NLS-1$
		this.generalLfBeforeCut = NumberUtility.parseInt(6, (String) this.properties.get("general.lfbeforecut")); //$NON-NLS-1$
		
		this.header = this.getTokens(Config.getInstance().getReceiptHeaderText());
		this.headerDatePattern = (String) this.properties.get("header.datepattern"); //$NON-NLS-1$
		this.headerTimePattern = (String) this.properties.get("header.timepattern"); //$NON-NLS-1$
		this.headerFont = NumberUtility.parseInt(0, (String) this.properties.get("header.font")); //$NON-NLS-1$
		this.headerCols = NumberUtility.parseInt(0, (String) this.properties.get("header.columns")); //$NON-NLS-1$
		this.headerEmphasized = new Boolean((String) this.properties.get("header.emphasized")).booleanValue(); //$NON-NLS-1$
		this.headerPrintmode = NumberUtility.parseInt(0, (String) this.properties.get("header.printmode")); //$NON-NLS-1$
		this.headerJustify = NumberUtility.parseInt(0, (String) this.properties.get("header.justify")); //$NON-NLS-1$
		this.headerDelimit = new Boolean((String) this.properties.get("header.delimit")).booleanValue(); //$NON-NLS-1$
		
		this.footer = this.getTokens((String) this.properties.get("footer.data")); //$NON-NLS-1$
		this.footerCols = NumberUtility.parseInt(0, (String) this.properties.get("footer.columns")); //$NON-NLS-1$
		this.footerDelimit = new Boolean((String) this.properties.get("footer.delimit")).booleanValue(); //$NON-NLS-1$
		
		this.productGroupColumnWidth = this.getIntTokens((String) this.properties.get("productGroup.colwidth")); //$NON-NLS-1$
		this.productGroupFont = NumberUtility.parseInt(0, (String) this.properties.get("productGroup.font")); //$NON-NLS-1$
		this.productGroupEmphasized = new Boolean((String) this.properties.get("productGroup.emphasized")).booleanValue(); //$NON-NLS-1$
		this.productGroupPrintmode = NumberUtility.parseInt(0, (String) this.properties.get("productGroup.printmode")); //$NON-NLS-1$
		this.productGroupJustify = NumberUtility.parseInt(0, (String) this.properties.get("productGroup.justify")); //$NON-NLS-1$
		this.productGroupDelimit = new Boolean((String) this.properties.get("productGroup.delimit")).booleanValue(); //$NON-NLS-1$
		
		this.paymentColumnWidth = this.getIntTokens((String) this.properties.get("payment.colwidth")); //$NON-NLS-1$
		this.paymentFont = NumberUtility.parseInt(0, (String) this.properties.get("payment.font")); //$NON-NLS-1$
		this.paymentCols = NumberUtility.parseInt(0, (String) this.properties.get("payment.columns")); //$NON-NLS-1$
		this.paymentEmphasized = new Boolean((String) this.properties.get("payment.emphasized")).booleanValue(); //$NON-NLS-1$
		this.paymentPrintmode = NumberUtility.parseInt(0, (String) this.properties.get("payment.printmode")); //$NON-NLS-1$
		this.paymentJustify = NumberUtility.parseInt(0, (String) this.properties.get("payment.justify")); //$NON-NLS-1$
		this.paymentDelimit = new Boolean((String) this.properties.get("payment.delimit")).booleanValue(); //$NON-NLS-1$
		
		this.taxColumnWidth = this.getIntTokens((String) this.properties.get("tax.colwidth")); //$NON-NLS-1$
		this.taxFont = NumberUtility.parseInt(0, (String) this.properties.get("tax.font")); //$NON-NLS-1$
		this.taxCols = NumberUtility.parseInt(0, (String) this.properties.get("tax.columns")); //$NON-NLS-1$
		this.taxEmphasized = new Boolean((String) this.properties.get("tax.emphasized")).booleanValue(); //$NON-NLS-1$
		this.taxPrintmode = NumberUtility.parseInt(0, (String) this.properties.get("tax.printmode")); //$NON-NLS-1$
		this.taxJustify = NumberUtility.parseInt(0, (String) this.properties.get("tax.justify")); //$NON-NLS-1$
		this.taxDelimit = new Boolean((String) this.properties.get("tax.delimit")).booleanValue(); //$NON-NLS-1$
		
		this.reversedColumnWidth = this.getIntTokens((String) this.properties.get("reversed.colwidth")); //$NON-NLS-1$
		this.reversedCols = NumberUtility.parseInt(0, (String) this.properties.get("reversed.columns")); //$NON-NLS-1$
		this.reversedDelimit = new Boolean((String) this.properties.get("reversed.delimit")).booleanValue(); //$NON-NLS-1$
		
		this.stockColumnWidth = this.getIntTokens((String) this.properties.get("stock.colwidth")); //$NON-NLS-1$
		this.stockDelimit = new Boolean((String) this.properties.get("stock.delimit")).booleanValue(); //$NON-NLS-1$
		
		StringTokenizer st = new StringTokenizer((String) this.properties.get("footer.data"), "|"); //$NON-NLS-1$ //$NON-NLS-2$
		this.footer = new String[st.countTokens()];
		for (int i = 0; st.hasMoreTokens(); i++)
		{
			this.footer[i] = st.nextToken();
		}
		this.footerCols = NumberUtility.parseInt(0, (String) this.properties.get("footer.columns")); //$NON-NLS-1$
		this.footerDelimit = new Boolean((String) this.properties.get("footer.delimit")).booleanValue(); //$NON-NLS-1$
		
		this.printer = ReceiptPrinter.getInstance(Config.getInstance().getPosPrinter()).getPrinter();
	}
	
	private Properties defaultProperties()
	{
		Properties properties = new Properties();
		properties.setProperty("general.cut", "true");
		properties.setProperty("general.cuttype", "partial");
		properties.setProperty("general.lfbeforecut", "6"); //$NON-NLS-1$
		properties.setProperty("header.datepattern", "dd.MM.yyyy"); //$NON-NLS-1$
		properties.setProperty("header.timepattern", "HH:mm"); //$NON-NLS-1$
		properties.setProperty("header.font", "0"); //$NON-NLS-1$
		properties.setProperty("header.columns", "42"); //$NON-NLS-1$
		properties.setProperty("header.emphasized", "true"); //$NON-NLS-1$
		properties.setProperty("header.printmode", "0"); //$NON-NLS-1$
		properties.setProperty("header.justify", "0"); //$NON-NLS-1$
		properties.setProperty("header.delimit", "true"); //$NON-NLS-1$
		properties.setProperty("header.data", ""); //$NON-NLS-1$
		properties.setProperty("footer.data", ""); //$NON-NLS-1$
		properties.setProperty("footer.font", "0"); //$NON-NLS-1$
		properties.setProperty("footer.columns", "42"); //$NON-NLS-1$
		properties.setProperty("footer.emphasized", "true"); //$NON-NLS-1$
		properties.setProperty("footer.printmode", "0"); //$NON-NLS-1$
		properties.setProperty("footer.justify", "0"); //$NON-NLS-1$
		properties.setProperty("footer.delimit", "0"); //$NON-NLS-1$
		properties.setProperty("productGroup.colwidth", "17|4|12|9"); //$NON-NLS-1$
		properties.setProperty("productGroup.font", "0"); //$NON-NLS-1$
		properties.setProperty("productGroup.columns", "42"); //$NON-NLS-1$
		properties.setProperty("productGroup.emphasized", "true"); //$NON-NLS-1$
		properties.setProperty("productGroup.printmode", "0"); //$NON-NLS-1$
		properties.setProperty("productGroup.justify", "0"); //$NON-NLS-1$
		properties.setProperty("productGroup.delimit", "true"); //$NON-NLS-1$
		properties.setProperty("withdraw.colwidth", "17|3|11|11"); //$NON-NLS-1$
		properties.setProperty("withdraw.font", "0"); //$NON-NLS-1$
		properties.setProperty("withdraw.columns", "42"); //$NON-NLS-1$
		properties.setProperty("withdraw.emphasized", "true"); //$NON-NLS-1$
		properties.setProperty("withdraw.printmode", "0"); //$NON-NLS-1$
		properties.setProperty("withdraw.justify", "0"); //$NON-NLS-1$
		properties.setProperty("withdraw.delimit", "true"); //$NON-NLS-1$
		properties.setProperty("payment.colwidth", "16|11|3|12"); //$NON-NLS-1$
		properties.setProperty("payment.font", "0"); //$NON-NLS-1$
		properties.setProperty("payment.columns", "42"); //$NON-NLS-1$
		properties.setProperty("payment.emphasized", "true"); //$NON-NLS-1$
		properties.setProperty("payment.printmode", "0"); //$NON-NLS-1$
		properties.setProperty("payment.justify", "0"); //$NON-NLS-1$
		properties.setProperty("payment.delimit", "true"); //$NON-NLS-1$
		properties.setProperty("tax.colwidth", "17|4|12|9"); //$NON-NLS-1$
		properties.setProperty("tax.font", "0"); //$NON-NLS-1$
		properties.setProperty("tax.columns", "42"); //$NON-NLS-1$
		properties.setProperty("tax.emphasized", "true"); //$NON-NLS-1$
		properties.setProperty("tax.printmode", "0"); //$NON-NLS-1$
		properties.setProperty("tax.justify", "0"); //$NON-NLS-1$
		properties.setProperty("tax.delimit", "true"); //$NON-NLS-1$
		properties.setProperty("reversed.colwidth", "15|11|6|10"); //$NON-NLS-1$
		properties.setProperty("reversed.font", "0"); //$NON-NLS-1$
		properties.setProperty("reversed.columns", "42"); //$NON-NLS-1$
		properties.setProperty("reversed.emphasized", "true"); //$NON-NLS-1$
		properties.setProperty("reversed.printmode", "0"); //$NON-NLS-1$
		properties.setProperty("reversed.justify", "0"); //$NON-NLS-1$
		properties.setProperty("reversed.delimit", "true"); //$NON-NLS-1$
		properties.setProperty("stock.colwidth", "22|4|16"); //$NON-NLS-1$
		properties.setProperty("stock.font", "0"); //$NON-NLS-1$
		properties.setProperty("stock.columns", "42"); //$NON-NLS-1$
		properties.setProperty("stock.emphasized", "true"); //$NON-NLS-1$
		properties.setProperty("stock.printmode", "0"); //$NON-NLS-1$
		properties.setProperty("stock.justify", "0"); //$NON-NLS-1$
		properties.setProperty("stock.delimit", "true"); //$NON-NLS-1$
		properties.setProperty("drawer.count", "1"); //$NON-NLS-1$
		return properties;
	}
	
	private Properties getProperties()
	{
		this.properties = new Properties();
		try
		{
			FileInputStream fis = new FileInputStream(Path.getInstance().cfgDir.concat(File.separator
							.concat("settlement.properties"))); //$NON-NLS-1$
			this.properties.load(fis);
			fis.close();
		}
		catch (FileNotFoundException e)
		{
			//			LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
		}
		catch (IOException e)
		{
			//			LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
		}
		return this.properties;
	}
	
	private String[] getTokens(String data)
	{
		StringTokenizer t = new StringTokenizer(data, "|"); //$NON-NLS-1$
		int i = t.countTokens();
		String[] s = new String[i];
		for (int j = 0; j < i; j++)
		{
			s[j] = t.nextToken();
			if (s[j].equals("."))
			{
				s[j] = "";
			}
		}
		return s;
	}
	
	private int[] getIntTokens(String data)
	{
		StringTokenizer t = new StringTokenizer(data, "|"); //$NON-NLS-1$
		int i = t.countTokens();
		int[] k = new int[i];
		for (int j = 0; j < i; j++)
		{
			k[j] = NumberUtility.parseInt(0, t.nextToken());
		}
		return k;
	}
	
	public void cleanup()
	{
		
	}
	
	private void setCharacterSettings(int font, boolean emphasized, int printmode, int justify)
	{
		this.printer.selectCharacterFont(font);
		this.printer.setEmphasized(emphasized);
		this.printer.selectPrintMode(printmode);
		this.printer.selectJustification(justify);
	}
	
	public void printSettlement()
	{
		Collection settlements = Settlement.selectSettlementsForCouponPrinter(this.settlement);
		if (settlements.size() > 0)
		{
			this.settlements = (Settlement[]) settlements.toArray(new Settlement[0]);
			this.receiptCount = this.settlements[0].getReceipts();
			
			Calendar calendar = GregorianCalendar.getInstance();
			calendar.setTimeInMillis(this.settlement.longValue());
			this.date = new Date();
			this.date.setTime(calendar.getTimeInMillis());
			this.time = new Time(calendar.getTimeInMillis());
			
			if (this.settlements[0].getReceipts() > 0)
			{
				this.printSettlement(this.settlement, this.date, this.time);
			}
			else
			{
				this.printHeader(this.settlements[0].getSalespointId(), this.settlement, this.date, this.time, false);
				this.printStock(this.settlement);
				this.printFooter();
			}
		}
	}
	
	private void print(String s)
	{
		this.printer.println(s);
	}
	
	private void printSettlement(Long settlement, Date date, Time time)
	{
		this.paymentAmount = 0d;
		this.paymentCount = 0;
		
		this.incomes = new Hashtable();
		this.otherSales = new Hashtable();
		this.expenses = new Hashtable();
		this.inputs = new Hashtable();
		this.withdraws = new Hashtable();
		this.payments = new Hashtable();
		this.taxes = new Hashtable();
		
		for (int i = 0; i < this.settlements.length; i++)
		{
			if (this.settlements[i].getType() == Settlement.TYPE_POSITION)
			{
				if (this.settlements[i].getSubtype() == ProductGroup.TYPE_INCOME)
				{
					this.incomes.put(this.settlements[i].getReferenceObjectId(), this.settlements[i]);
				}
				else if (this.settlements[i].getSubtype() == ProductGroup.TYPE_NOT_INCOME)
				{
					this.otherSales.put(this.settlements[i].getReferenceObjectId(), this.settlements[i]);
				}
				else if (this.settlements[i].getSubtype() == ProductGroup.TYPE_EXPENSE)
				{
					this.expenses.put(this.settlements[i].getReferenceObjectId(), this.settlements[i]);
				}
				else if (this.settlements[i].getSubtype() == ProductGroup.TYPE_INPUT)
				{
					this.inputs.put(this.settlements[i].getReferenceObjectId(), this.settlements[i]);
				}
				else if (this.settlements[i].getSubtype() == ProductGroup.TYPE_WITHDRAW)
				{
					this.withdraws.put(this.settlements[i].getReferenceObjectId(), this.settlements[i]);
				}
			}
			else if (this.settlements[i].getType() == Settlement.TYPE_PAYMENT)
			{
				this.payments.put(this.settlements[i].getReferenceClassName()
								+ this.settlements[i].getReferenceObjectId().toString(), this.settlements[i]);
			}
			else if (this.settlements[i].getType() == Settlement.TYPE_TAX)
			{
				this.taxes.put(this.settlements[i].getReferenceObjectId(), this.settlements[i]);
			}
		}
		
		this.printHeader(this.settlements[0].getSalespointId(), settlement, date, time, true);
		this.printPositions();
		this.printPaymentTypes();
		this.printSummary();
		this.printTaxes();
		this.printTakeBacks();
		this.printPayedInvoices();
		this.printReversedReceipts();
		this.printStock(settlement);
		this.printFooter();
	}
	
	private void printHeader(Long salespointId, Long settlement, Date date, Time time, boolean withSettlement)
	{
		this.setCharacterSettings(this.headerFont, this.headerEmphasized, this.headerPrintmode, this.headerJustify);
		
		for (int i = 0; i < this.header.length; i++)
		{
			this.print(this.header[i]);
		}
		
		this.print(""); //$NON-NLS-1$
		if (withSettlement)
			this.print(Messages.getString("SettlementPrinter.Tagesabschluss_97")); //$NON-NLS-1$
		else
			this.print("Kassensturz");
		
		if (this.testPrint)
		{
			this.print("Provisorisch");
		}
		this.print(""); //$NON-NLS-1$
		
		this.print(Salespoint.selectById(salespointId).name);
		
		this.print("");
		
		SimpleDateFormat sdfd = new SimpleDateFormat();
		SimpleDateFormat sdft = new SimpleDateFormat();
		sdfd.applyPattern(this.headerDatePattern);
		sdft.applyPattern(this.headerTimePattern);
		String datum = sdfd.format(date).concat(" "); //$NON-NLS-1$
		datum = datum.concat(sdft.format(time));
		
		if (withSettlement)
		{
			int count = this.headerCols - settlement.toString().length() - datum.length();
			String whiteString = ""; //$NON-NLS-1$
			if (count > 0)
			{
				char[] white = new char[count];
				for (int i = 0; i < white.length; i++)
				{
					white[i] = ' ';
				}
				whiteString = String.valueOf(white);
			}
			
			this.print(settlement.toString() + whiteString + datum);
			// 10257
			this.print("Anzahl Kunden: " + this.receiptCount);
			// 10257
		}
		else
		{
			this.print(datum);
		}
		
		if (this.headerDelimit)
		{
			this.print(this.format(this.generalDelimiter, this.headerCols));
		}
		
	}
	
	private void printPositions()
	{
		this.positionAmount = 0d;
		this.positionCount = 0;
		this.positionTax = 0d;
		
		this.printIncomes();
		this.printOtherSales();
		this.printExpense();
		// this.printWithdraws();
		this.printPositionSummary();
	}
	
	private void printIncomes()
	{
		this.setCharacterSettings(this.productGroupFont, this.productGroupEmphasized, this.productGroupPrintmode,
						this.productGroupJustify);
		
		this.amount = 0d;
		int items = 0;
		double tax = 0d;
		
		this.print(Messages.getString("SettlementPrinter.Warengruppen_101")); //$NON-NLS-1$
		
		/*
		 * Alle Positionen mit Warengruppen vom Typ INCOME (umsatzrelevant)
		 */
		Enumeration data = this.incomes.elements();
		Settlement[] settlements = new Settlement[this.incomes.size()];
		
		for (int i = 0; i < settlements.length; i++)
		{
			settlements[i] = (Settlement) data.nextElement();
		}
		
		if (settlements.length > 0)
		{
			Arrays.sort(settlements);
			for (int i = 0; i < settlements.length; i++)
			{
				this.amount += settlements[i].getAmount1();
				items += settlements[i].getQuantity();
				tax += settlements[i].getAmount2();
				
				String s = this.format(settlements[i].getShortText(), this.productGroupColumnWidth[0], 0);
				s = s.concat(this.format("" + settlements[i].getQuantity(), this.productGroupColumnWidth[1], 2)); //$NON-NLS-1$
				s = s
								.concat(this
												.format(
																""				+ NumberUtility	.formatDouble(	settlements[i]	.getAmount1(), 2, 2, true), this.productGroupColumnWidth[2], 2)); //$NON-NLS-1$
				s = s
								.concat(this
												.format(
																""				+ NumberUtility	.formatDouble(	settlements[i]	.getAmount2(), 2, 2, true), this.productGroupColumnWidth[3], 2)); //$NON-NLS-1$
				this.print(s);
			}
			
			if (this.productGroupDelimit)
			{
				this.print(this.format(this.generalDelimiter, this.headerCols));
			}
		}
		
		/*
		 * Total drucken
		 */
		String s = this
						.format(
										Messages.getString("SettlementPrinter.Total_Warengruppen_105"), this.productGroupColumnWidth[0], 0); //$NON-NLS-1$
		s = s.concat(this.format("" + items, this.productGroupColumnWidth[1], 2)); //$NON-NLS-1$
		s = s.concat(this.format(
						"" + NumberUtility.formatDouble(this.amount, 2, 2, true), this.productGroupColumnWidth[2], 2)); //$NON-NLS-1$
		s = s.concat(this.format("" + NumberUtility.formatDouble(tax, 2, 2, true), this.productGroupColumnWidth[3], 2)); //$NON-NLS-1$
		this.print(s);
		
		if (this.productGroupDelimit)
		{
			this.print(this.format(this.generalDelimiter, this.headerCols));
		}
		
		this.positionAmount += this.amount;
		this.positionCount += items;
		this.positionTax += tax;
	}
	
	private void printOtherSales()
	{
		this.setCharacterSettings(this.productGroupFont, this.productGroupEmphasized, this.productGroupPrintmode,
						this.productGroupJustify);
		
		this.amount = 0d;
		int items = 0;
		double tax = 0d;
		
		this.print(Messages.getString("SettlementPrinter.Sonstiges_101")); //$NON-NLS-1$
		
		Enumeration data = this.otherSales.elements();
		Settlement[] settlements = new Settlement[this.otherSales.size()];
		
		for (int i = 0; i < settlements.length; i++)
		{
			settlements[i] = (Settlement) data.nextElement();
		}
		
		if (settlements.length > 0)
		{
			Arrays.sort(settlements);
			for (int i = 0; i < settlements.length; i++)
			{
				this.amount += settlements[i].getAmount1();
				items += settlements[i].getQuantity();
				tax += settlements[i].getAmount2();
				
				String s = this.format(settlements[i].getShortText(), this.productGroupColumnWidth[0], 0);
				s = s.concat(this.format("" + settlements[i].getQuantity(), this.productGroupColumnWidth[1], 2)); //$NON-NLS-1$
				s = s
								.concat(this
												.format(
																""				+ NumberUtility	.formatDouble(	settlements[i]	.getAmount1(), 2, 2, true), this.productGroupColumnWidth[2], 2)); //$NON-NLS-1$
				s = s
								.concat(this
												.format(
																""				+ NumberUtility	.formatDouble(	settlements[i]	.getAmount2(), 2, 2, true), this.productGroupColumnWidth[3], 2)); //$NON-NLS-1$
				this.print(s);
			}
			
			if (this.productGroupDelimit)
			{
				this.print(this.format(this.generalDelimiter, this.headerCols));
			}
			
		}
		String s = this
						.format(
										Messages.getString("SettlementPrinter.Total_Sonstiges_105"), this.productGroupColumnWidth[0], 0); //$NON-NLS-1$
		s = s.concat(this.format("" + items, this.productGroupColumnWidth[1], 2)); //$NON-NLS-1$
		s = s.concat(this.format(
						"" + NumberUtility.formatDouble(this.amount, 2, 2, true), this.productGroupColumnWidth[2], 2)); //$NON-NLS-1$
		s = s.concat(this.format("" + NumberUtility.formatDouble(tax, 2, 2, true), this.productGroupColumnWidth[3], 2)); //$NON-NLS-1$
		this.print(s);
		
		if (this.productGroupDelimit)
		{
			this.print(this.format(this.generalDelimiter, this.headerCols));
		}
		
		this.positionAmount += this.amount;
		this.positionCount += items;
		this.positionTax += tax;
	}
	
	private void printExpense()
	{
		this.setCharacterSettings(this.productGroupFont, this.productGroupEmphasized, this.productGroupPrintmode,
						this.productGroupJustify);
		
		this.amount = 0d;
		int items = 0;
		double tax = 0d;
		
		this.print(Messages.getString("SettlementPrinter.Ausgaben_101"));
		
		Enumeration data = this.expenses.elements();
		Settlement[] settlements = new Settlement[this.expenses.size()];
		
		for (int i = 0; i < settlements.length; i++)
		{
			settlements[i] = (Settlement) data.nextElement();
		}
		
		if (settlements.length > 0)
		{
			Arrays.sort(settlements);
			for (int i = 0; i < settlements.length; i++)
			{
				this.amount += settlements[i].getAmount1();
				items += settlements[i].getQuantity();
				tax += settlements[i].getAmount2();
				String s = this.format(settlements[i].getShortText(), this.productGroupColumnWidth[0], 0);
				s = s.concat(this.format("" + settlements[i].getQuantity(), this.productGroupColumnWidth[1], 2)); //$NON-NLS-1$
				s = s
								.concat(this
												.format(
																""				+ NumberUtility	.formatDouble(	settlements[i]	.getAmount1(), 2, 2, true), this.productGroupColumnWidth[2], 2)); //$NON-NLS-1$
				s = s
								.concat(this
												.format(
																""				+ NumberUtility	.formatDouble(	settlements[i]	.getAmount2(), 2, 2, true), this.productGroupColumnWidth[3], 2)); //$NON-NLS-1$
				this.print(s);
			}
			if (settlements.length > 0)
			{
				if (this.productGroupDelimit)
				{
					this.print(this.format(this.generalDelimiter, this.headerCols));
				}
			}
		}
		
		String s = this.format(Messages.getString("SettlementPrinter.Total_Ausgaben_105"),
						this.productGroupColumnWidth[0], 0);
		s = s.concat(this.format("" + items, this.productGroupColumnWidth[1], 2)); //$NON-NLS-1$
		s = s.concat(this.format(
						"" + NumberUtility.formatDouble(this.amount, 2, 2, true), this.productGroupColumnWidth[2], 2)); //$NON-NLS-1$
		s = s.concat(this.format("" + NumberUtility.formatDouble(tax, 2, 2, true), this.productGroupColumnWidth[3], 2)); //$NON-NLS-1$
		this.print(s);
		
		if (this.productGroupDelimit)
		{
			this.print(this.format(this.generalDelimiter, this.headerCols));
		}
		
		this.positionAmount += this.amount;
		this.positionCount += items;
		this.positionTax += tax;
	}
	
	private void printPositionSummary()
	{
		this.setCharacterSettings(this.paymentFont, this.paymentEmphasized, this.paymentPrintmode, this.paymentJustify);
		
		String s = this.format("Total Bewegungen", this.productGroupColumnWidth[0], 0); //$NON-NLS-1$
		s = s
						.concat(this
										.format(
														"" + NumberUtility.formatInteger(this.positionCount, true), this.productGroupColumnWidth[1], 2)); //$NON-NLS-1$
		s = s
						.concat(this
										.format(
														""				+ NumberUtility	.formatDouble(	this.positionAmount, 2, 2, true), this.productGroupColumnWidth[2], 2)); //$NON-NLS-1$
		s = s
						.concat(this
										.format(
														"" + NumberUtility.formatDouble(this.positionTax, 2, 2, true), this.productGroupColumnWidth[3], 2)); //$NON-NLS-1$
		this.print(s);
		
		if (this.paymentDelimit)
		{
			this.print(this.format(this.generalDelimiter, this.paymentCols));
		}
	}
	
	private void printPaymentTypes()
	{
		boolean printQuantity = Config.getInstance().getSettlementPrintPaymentQuantity();
		
		this.setCharacterSettings(this.paymentFont, this.paymentEmphasized, this.paymentPrintmode, this.paymentJustify);
		
		int items = 0;
		double amount = 0d;
		double amountFC = 0d;
		
		if (printQuantity)
		{
			this
							.print(Messages.getString("SettlementPrinter.Zahlungsarten_109") + "            " + Messages.getString("SettlementPrinter.Zahlungsarten_1092")); //$NON-NLS-1$
		}
		else
		{
			this
							.print(Messages.getString("SettlementPrinter.Zahlungsarten_109") + "            " + Messages.getString("SettlementPrinter.Zahlungsarten_1091")); //$NON-NLS-1$
		}
		
		Enumeration data = this.payments.elements();
		Settlement[] settlements = new Settlement[this.payments.size()];
		
		for (int i = 0; i < settlements.length; i++)
		{
			settlements[i] = (Settlement) data.nextElement();
		}
		
		if (settlements.length > 0)
		{
			Arrays.sort(settlements);
			for (int i = 0; i < settlements.length; i++)
			{
				items += settlements[i].getQuantity();
				amount += settlements[i].getAmount2();
				amountFC += settlements[i].getAmount1();
				
				String s = this.format(settlements[i].getShortText(), this.paymentColumnWidth[0], 0);
				String afc = this.format(NumberUtility.formatDouble(settlements[i].getAmount1(), 2, 2, true),
								this.paymentColumnWidth[1], 2);
				String lfc = this.format(NumberUtility.formatDouble(settlements[i].getAmount2(), 2, 2, true),
								this.paymentColumnWidth[3], 2);
				if (afc.trim().equals(lfc.trim()))
				{
					s = s.concat(this.format("", this.paymentColumnWidth[1], 2)); //$NON-NLS-1$
				}
				else
				{
					s = s
									.concat(this
													.format(
																	""				+ NumberUtility	.formatDouble(	settlements[i]	.getAmount1(), 2, 2, true), this.paymentColumnWidth[1], 2)); //$NON-NLS-1$
				}
				if (printQuantity)
				{
					s = s
									.concat(this
													.format(
																	""				+ NumberUtility	.formatInteger(	settlements[i]	.getQuantity(), true), this.paymentColumnWidth[2], 2)); //$NON-NLS-1$
				}
				else
				{
					s = s.concat(this.format("", this.paymentColumnWidth[2], 2)); //$NON-NLS-1$
				}
				s = s
								.concat(this
												.format(
																""				+ NumberUtility	.formatDouble(	settlements[i]	.getAmount2(), 2, 2, true), this.paymentColumnWidth[3], 2)); //$NON-NLS-1$
				this.print(s);
				
			}
			
			if (this.paymentDelimit) this.print(this.format(this.generalDelimiter, this.paymentCols));
		}
		
		String s = this
						.format(
										Messages.getString("SettlementPrinter.Total_Zahlungsarten_115"), this.paymentColumnWidth[0] + this.paymentColumnWidth[1], 0); //$NON-NLS-1$
		
		if (printQuantity)
		{
			s = s.concat(this.format("" + NumberUtility.formatInteger(items, true), this.paymentColumnWidth[2], 2)); //$NON-NLS-1$
		}
		else
		{
			s = s.concat(this.format("", this.paymentColumnWidth[2], 2)); //$NON-NLS-1$
		}
		
		s = s.concat(this.format("" + NumberUtility.formatDouble(amount, 2, 2, true), this.paymentColumnWidth[3], 2)); //$NON-NLS-1$
		this.print(s);
		
		if (this.paymentDelimit)
		{
			this.print(this.format(this.generalDelimiter, this.paymentCols));
		}
		
		this.paymentAmount += amount;
		this.paymentCount += items;
	}
	
	private void printSummary()
	{
		this.setCharacterSettings(this.paymentFont, this.paymentEmphasized, this.paymentPrintmode, this.paymentJustify);
		
		boolean print = false;
		
		for (int i = 0; i < this.settlements.length; i++)
		{
			if (this.settlements[i].getType() == Settlement.TYPE_SUMMARY)
			{
				print = true;
				break;
			}
		}
		
		if (print)
		{
			this.print("Zusammenfassung"); //$NON-NLS-1$
			
			if (this.paymentDelimit)
			{
				this.print(this.format(this.generalDelimiter, this.paymentCols));
			}
			
			for (int i = 0; i < this.settlements.length; i++)
			{
				if (this.settlements[i].getType() == Settlement.TYPE_SUMMARY)
				{
					String s = this.format(this.settlements[i].getShortText(), this.paymentColumnWidth[0]
									+ this.paymentColumnWidth[1], 0);
					s = s
									.concat(this
													.format(
																	""				+ NumberUtility	.formatInteger(	this.settlements[i].getQuantity(), true), this.paymentColumnWidth[2], 2)); //$NON-NLS-1$
					s = s
									.concat(this
													.format(
																	""				+ NumberUtility	.formatDouble(	this.settlements[i].getAmount1(), 2, 2, true), this.paymentColumnWidth[3], 2)); //$NON-NLS-1$
					this.print(s);
				}
			}
			
			if (this.paymentDelimit)
			{
				this.print(this.format(this.generalDelimiter, this.paymentCols));
			}
		}
	}
	
	private void printTaxes()
	{
		this.setCharacterSettings(this.taxFont, this.taxEmphasized, this.taxPrintmode, this.taxJustify);
		
		double amount = 0d;
		int items = 0;
		double tax = 0d;
		
		this.print(Messages.getString("SettlementPrinter.Mehrwertsteuer_118")); //$NON-NLS-1$
		
		Enumeration data = this.taxes.elements();
		Settlement[] settlements = new Settlement[this.taxes.size()];
		
		for (int i = 0; i < settlements.length; i++)
		{
			settlements[i] = (Settlement) data.nextElement();
		}
		
		if (settlements.length > 0)
		{
			Arrays.sort(settlements);
			for (int i = 0; i < settlements.length; i++)
			{
				amount += settlements[i].getAmount1();
				items += settlements[i].getQuantity();
				tax += settlements[i].getAmount2();
				
				String s = this.format(settlements[i].getShortText(), this.taxColumnWidth[0], 0);
				s = s.concat(this.format("" + settlements[i].getQuantity(), this.taxColumnWidth[1], 2)); //$NON-NLS-1$
				s = s
								.concat(this
												.format(
																""				+ NumberUtility	.formatDouble(	settlements[i]	.getAmount1(), 2, 2, true), this.taxColumnWidth[2], 2)); //$NON-NLS-1$
				s = s
								.concat(this
												.format(
																""				+ NumberUtility	.formatDouble(	settlements[i]	.getAmount2(), 2, 2, true), this.taxColumnWidth[3], 2)); //$NON-NLS-1$
				this.print(s);
			}
			if (settlements.length > 0)
			{
				if (this.taxDelimit)
				{
					this.print(this.format(this.generalDelimiter, this.taxCols));
				}
			}
		}
		
		String s = this.format(
						Messages.getString("SettlementPrinter.Total_Mehrwertsteuer_122"), this.taxColumnWidth[0], 0); //$NON-NLS-1$
		s = s.concat(this.format("" + items, this.taxColumnWidth[1], 2)); //$NON-NLS-1$
		s = s.concat(this.format("" + NumberUtility.formatDouble(amount, 2, 2, true), this.taxColumnWidth[2], 2)); //$NON-NLS-1$
		s = s.concat(this.format("" + NumberUtility.formatDouble(tax, 2, 2, true), this.taxColumnWidth[3], 2)); //$NON-NLS-1$
		this.print(s);
		
		if (this.taxDelimit)
		{
			this.print(this.format(this.generalDelimiter, this.taxCols));
		}
	}
	
	private void printTakeBacks()
	{
		boolean isTakeBacksPrinted = false;
		
		this.setCharacterSettings(this.taxFont, this.taxEmphasized, this.taxPrintmode, this.taxJustify);
		
		for (int i = 0; i < this.settlements.length; i++)
		{
			if (this.settlements[i].getType() == Settlement.TYPE_TOOK_BACK)
			{
				if (!isTakeBacksPrinted)
				{
					isTakeBacksPrinted = true;
					
					this.print("Rücknahmen"); //$NON-NLS-1$
					if (this.reversedDelimit)
					{
						this.print(this.format(this.generalDelimiter, this.reversedCols));
					}
				}
				
				String s = this.format(this.settlements[i].getShortText(), this.reversedColumnWidth[0]
								+ this.reversedColumnWidth[1] + this.reversedColumnWidth[2], 0);
				s = s
								.concat(this
												.format(
																""				+ NumberUtility	.formatDouble(	this.settlements[i].getAmount1(), 2, 2, true), this.reversedColumnWidth[3], 2)); //$NON-NLS-1$
				this.print(s);
				
				if (isTakeBacksPrinted)
				{
					if (this.reversedDelimit)
					{
						this.print(this.format(this.generalDelimiter, this.reversedCols));
					}
				}
			}
		}
	}
	
	private void printPayedInvoices()
	{
		boolean isPayedInvoicesPrinted = false;
		
		this.setCharacterSettings(this.taxFont, this.taxEmphasized, this.taxPrintmode, this.taxJustify);
		
		for (int i = 0; i < this.settlements.length; i++)
		{
			if (this.settlements[i].getType() == Settlement.TYPE_PAYED_INVOICES)
			{
				if (!isPayedInvoicesPrinted)
				{
					isPayedInvoicesPrinted = true;
					
					this.print("Rechnungen an der Kasse bezahlt"); //$NON-NLS-1$
					if (this.reversedDelimit)
					{
						this.print(this.format(this.generalDelimiter, this.reversedCols));
					}
				}
				
				String s = this.format(this.settlements[i].getShortText(), this.reversedColumnWidth[0]
								+ this.reversedColumnWidth[1] + this.reversedColumnWidth[2], 0);
				s = s
								.concat(this
												.format(
																""				+ NumberUtility	.formatDouble(	this.settlements[i].getAmount1(), 2, 2, true), this.reversedColumnWidth[3], 2)); //$NON-NLS-1$
				this.print(s);
				
				if (isPayedInvoicesPrinted)
				{
					if (this.reversedDelimit)
					{
						this.print(this.format(this.generalDelimiter, this.reversedCols));
					}
				}
			}
		}
	}
	
	private void printReversedReceipts()
	{
		boolean isReversedHeaderPrinted = false;
		
		this.setCharacterSettings(this.taxFont, this.taxEmphasized, this.taxPrintmode, this.taxJustify);
		
		for (int i = 0; i < this.settlements.length; i++)
		{
			if (this.settlements[i].getType() == Settlement.TYPE_REVERSED)
			{
				if (!isReversedHeaderPrinted)
				{
					isReversedHeaderPrinted = true;
					
					this.print(Messages.getString("SettlementPrinter.Stornierte_Belege_126")); //$NON-NLS-1$
					if (this.reversedDelimit)
					{
						this.print(this.format(this.generalDelimiter, this.reversedCols));
					}
				}
				
				String[] splitted = this.settlements[i].getLongText().split(" ");
				String s = null;
				String t = null;
				if (splitted.length > 0) s = this.format(splitted[0], this.reversedColumnWidth[0], 0);
				t = splitted.length > 1 ? splitted[1] : "";
				s = s.concat(this.format(t, this.reversedColumnWidth[1], 2));
				t = splitted.length > 2 ? splitted[2] : "";
				s = s.concat(this.format(t, this.reversedColumnWidth[2], 2));
				s = s
								.concat(this
												.format(
																""				+ NumberUtility	.formatDouble(	this.settlements[i].getAmount1(), 2, 2, true), this.reversedColumnWidth[3], 2)); //$NON-NLS-1$
				this.print(s);
				
				if (isReversedHeaderPrinted)
				{
					if (this.reversedDelimit)
					{
						this.print(this.format(this.generalDelimiter, this.reversedCols));
					}
				}
			}
		}
	}
	
	private void printStock(Long settlement)
	{
		this.setCharacterSettings(this.taxFont, this.taxEmphasized, this.taxPrintmode, this.taxJustify);
		
		int min = Integer.MAX_VALUE;
		int max = -1;
		Collection ss = new ArrayList();
		for (int i = 0; i < this.settlements.length; i++)
		{
			if (this.settlements[i].getType() == Settlement.TYPE_CASH_CHECK)
			{
				if (this.settlements[i].getSubtype() < min) min = this.settlements[i].getSubtype();
				if (this.settlements[i].getSubtype() > max) max = this.settlements[i].getSubtype();
				ss.add(this.settlements[i]);
			}
		}
		
		Settlement[] sss = (Settlement[]) ss.toArray(new Settlement[0]);
		Arrays.sort(sss);
		
		for (int i = min; i <= max; i++)
		{
			Collection w = new ArrayList();
			Iterator iter = ss.iterator();
			while (iter.hasNext())
			{
				Settlement set = (Settlement) iter.next();
				if (set.getSubtype() == i) w.add(set);
			}
			
			Settlement[] sets = (Settlement[]) w.toArray(new Settlement[0]);
			Arrays.sort(sets);
			
			if (sets.length == 1)
			{
				String s = this.format(sets[0].getCode() + " " + "Kassabestand", this.stockColumnWidth[0], 0); //$NON-NLS-1$
				s = s.concat(this.format(NumberUtility.formatInteger(sets[0].getQuantity(), true),
								this.stockColumnWidth[1], 2));
				s = s.concat(this.format(NumberUtility.formatDouble(sets[0].getAmount1(), 2, 2, true),
								this.stockColumnWidth[2], 2)); // 10183
				this.print(s);
			}
			else if (sets.length > 1)
			{
				this
								.print(sets[0].getCode()
												+ " " + Messages.getString("SettlementPrinter.Kassastock_127") + " " + sets[0].getCode()); //$NON-NLS-1$
				
				for (int k = 0; k < sets.length; k++)
				{
					if (sets[k].getCashtype() < Settlement.CASH_CHECK_MONEY)
					{
						double amount = 0d;
						if (sets[k].getCashtype() == Settlement.CASH_CHECK_DIFFERENCE)
							amount = Math.abs(sets[k].getAmount1());
						else
							amount = sets[k].getAmount1();
						String s = this
										.format(
														sets[k].getCode() + " " + sets[k].getShortText(), this.stockColumnWidth[0] + this.stockColumnWidth[1], 0); //$NON-NLS-1$
						s = s.concat(this.format(NumberUtility.formatDouble(amount, 2, 2, true),
										this.stockColumnWidth[2], 2));
						this.print(s);
					}
				}
				
				this.print("");
				
				this.print(sets[0].getCode() + " " + Messages.getString("SettlementPrinter.Kassensturz")); //$NON-NLS-1$
				
				int fractionDigits = Currency.getInstance(sets[0].getCode()).getDefaultFractionDigits();
				double amount = 0d;
				int quantity = 0;
				for (int k = 0; k < sets.length; k++)
				{
					if (sets[k].getCashtype() == Settlement.CASH_CHECK_MONEY)
					{
						amount += sets[k].getAmount1();
						quantity += sets[k].getQuantity();
						String s = this.format(sets[k].getCode() + " " + sets[k].getShortText(),
										this.stockColumnWidth[0], 0);
						s = s.concat(this.format(NumberUtility.formatInteger(sets[k].getQuantity(), true),
										this.stockColumnWidth[1], 2));
						s = s.concat(this.format(NumberUtility.formatDouble(sets[k].getAmount1(), fractionDigits,
										fractionDigits, true), this.stockColumnWidth[2], 2));
						this.print(s);
					}
				}
				
				String s = this.format(" ", this.stockColumnWidth[0], 0); //$NON-NLS-1$
				s = s.concat(this.format("", this.stockColumnWidth[1], 0));
				s = s.concat(this.format("", this.stockColumnWidth[2], 0, '-'));
				this.print(s);
				
				s = this
								.format(
												sets[0].getCode()
																+ " " + Messages.getString("SettlementPrinter.Total_Kassensturz"), this.stockColumnWidth[0], 0); //$NON-NLS-1$
				s = s.concat(this.format("", this.stockColumnWidth[1], 2));
				s = s.concat(this.format(NumberUtility.formatDouble(amount, fractionDigits, fractionDigits, true),
								this.stockColumnWidth[2], 2)); // 10183
				this.print(s);
				
				s = this.format(" ", this.stockColumnWidth[0], 0); //$NON-NLS-1$
				s = s.concat(this.format("", this.stockColumnWidth[1], 0));
				s = s.concat(this.format("", this.stockColumnWidth[2], 0, '='));
				this.print(s);
				
				if (this.stockDelimit) this.print(this.format(this.generalDelimiter, this.reversedCols));
			}
		}
	}
	
	private void printFooter()
	{
		if (this.footerDelimit)
		{
			this.print(this.format(this.generalDelimiter, this.footerCols));
		}
		
		if (this.generalCut)
		{
			if (this.generalCutType.equals("partial"))
			{
				this.printer.partialCut(this.generalLfBeforeCut);
				if (LogManager.getLogManager().getLogger("colibri") != null)
				{
					LogManager.getLogManager().getLogger("colibri").info("      Beleg geschnitten.");
				}
			}
			else if (this.generalCutType.equals("full"))
			{
				this.printer.fullCut(this.generalLfBeforeCut);
				if (LogManager.getLogManager().getLogger("colibri") != null)
				{
					LogManager.getLogManager().getLogger("colibri").info("      Beleg geschnitten.");
				}
			}
		}
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
	
	private String format(String s, int width, int justification)
	{
		return this.format(s, width, justification, ' ');
	}
	
	private String format(String s, int width, int justification, char fill)
	{
		String t = ""; //$NON-NLS-1$
		if (s.length() > width && width > 3)
		{
			t = s.substring(0, width - 3).concat("..."); //$NON-NLS-1$
		}
		else if (s.length() == width)
		{
			t = s;
		}
		else if (s.length() < width)
		{
			char[] b = new char[width - s.length()];
			for (int i = 0; i < b.length; i++)
			{
				b[i] = fill;
			}
			String g = new String(b);
			if (justification == 0)
			{
				t = s.concat(g);
			}
			else if (justification == 1)
			{
				t = t.concat(g.substring(0, g.length() / 2 - 1));
				t = t.concat(s);
				t = t.concat(g.substring(g.length() / 2, g.length() - 1));
			}
			else if (justification == 2)
			{
				t = g.concat(s);
			}
		}
		return t;
	}
	
	/**
	 * @return
	 */
	public boolean isTestPrint()
	{
		return this.testPrint;
	}
	
	/**
	 * @param b
	 */
	public void setTestPrint(boolean b)
	{
		this.testPrint = b;
	}
	
	private Hashtable incomes;
	private Hashtable otherSales;
	private Hashtable expenses;
	private Hashtable inputs;
	private Hashtable withdraws;
	private Hashtable payments;
	private Hashtable taxes;
	
	// private int totalReceiptCount = 0;
	private int receiptCount = 0;
	// private int reversedReceiptCount = 0;
	
	private UserPanel context;
	private Properties properties;
	private POSPrinter printer;
	private boolean testPrint;
	
	private double amount = 0d;
	
	private boolean generalCut = true;
	private String generalCutType = "partial"; //$NON-NLS-1$
	private String generalDelimiter = "-"; //$NON-NLS-1$
	private int generalLfBeforeCut;
	// private boolean generalPrintLogo;
	
	private String[] header;
	private String headerDatePattern;
	private String headerTimePattern;
	private int headerFont;
	private int headerCols;
	private boolean headerEmphasized;
	private int headerPrintmode;
	private int headerJustify;
	private boolean headerDelimit;
	
	private String[] footer;
	private int footerCols;
	private boolean footerDelimit;
	
	private int[] productGroupColumnWidth;
	private int productGroupFont;
	private boolean productGroupEmphasized;
	private int productGroupPrintmode;
	private int productGroupJustify;
	private boolean productGroupDelimit;
	
	private int[] paymentColumnWidth;
	private int paymentFont;
	private int paymentCols;
	private boolean paymentEmphasized;
	private int paymentPrintmode;
	private int paymentJustify;
	private boolean paymentDelimit;
	
	private int[] taxColumnWidth;
	private int taxFont;
	private int taxCols;
	private boolean taxEmphasized;
	private int taxPrintmode;
	private int taxJustify;
	private boolean taxDelimit;
	
	private int[] reversedColumnWidth;
	private int reversedCols;
	private boolean reversedDelimit;
	
	private int[] stockColumnWidth;
	private boolean stockDelimit;
	
	private double positionAmount;
	private int positionCount;
	private double positionTax;
	private double paymentAmount;
	private int paymentCount;
	
	private boolean[] cashdrawer;
	
	private Long settlement;
	private Date date;
	private Time time;
}
