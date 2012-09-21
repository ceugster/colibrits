/*
 * Created on 31.08.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.printing;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.ProgressMonitor;

import org.apache.ojb.broker.PBKey;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerFactory;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import ch.eugster.pos.Messages;
import ch.eugster.pos.client.gui.Frame;
import ch.eugster.pos.client.gui.TabPanel;
import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.db.Coin;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.Payment;
import ch.eugster.pos.db.PaymentType;
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.db.Settlement;
import ch.eugster.pos.db.Stock;
import ch.eugster.pos.devices.printers.POSPrinter;
import ch.eugster.pos.events.Action;
import ch.eugster.pos.events.MessageDialog;
import ch.eugster.pos.events.ModeChangeEvent;
import ch.eugster.pos.events.PosEvent;
import ch.eugster.pos.events.PosEventListener;
import ch.eugster.pos.events.SettleDayAction;
import ch.eugster.pos.util.Config;
import ch.eugster.pos.util.NumberUtility;
import ch.eugster.pos.util.Path;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class SettlementPrinter2 implements PosEventListener, Runnable
{
	private Collection settlements = new ArrayList();
	
	private Collection takeBacks = new ArrayList();
	
	private Collection payedInvoices = new ArrayList();
	
	private Collection reversedReceipts = new ArrayList(); // 10438
	
	private DateFormat shortDateFormat = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
	
	/**
	 * 
	 */
	public SettlementPrinter2(UserPanel context)
	{
		this.context = context;
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
		// this.footerFont = NumberUtility.parseInt(0, (String) this.properties
		//				.get("footer.font")); //$NON-NLS-1$
		this.footerCols = NumberUtility.parseInt(0, (String) this.properties.get("footer.columns")); //$NON-NLS-1$
		// this.footerEmphasized = new Boolean((String) this.properties
		//				.get("footer.emphasized")).booleanValue(); //$NON-NLS-1$
		// this.footerPrintmode = NumberUtility.parseInt(0,
		//				(String) this.properties.get("footer.printmode")); //$NON-NLS-1$
		// this.footerJustify = NumberUtility.parseInt(0, (String)
		// this.properties
		//				.get("footer.justify")); //$NON-NLS-1$
		this.footerDelimit = new Boolean((String) this.properties.get("footer.delimit")).booleanValue(); //$NON-NLS-1$
		
		this.productGroupColumnWidth = this.getIntTokens((String) this.properties.get("productGroup.colwidth")); //$NON-NLS-1$
		this.productGroupFont = NumberUtility.parseInt(0, (String) this.properties.get("productGroup.font")); //$NON-NLS-1$
		// this.productGroupCols = NumberUtility.parseInt(0,
		//				(String) this.properties.get("productGroup.columns")); //$NON-NLS-1$
		this.productGroupEmphasized = new Boolean((String) this.properties.get("productGroup.emphasized")).booleanValue(); //$NON-NLS-1$
		this.productGroupPrintmode = NumberUtility.parseInt(0, (String) this.properties.get("productGroup.printmode")); //$NON-NLS-1$
		this.productGroupJustify = NumberUtility.parseInt(0, (String) this.properties.get("productGroup.justify")); //$NON-NLS-1$
		this.productGroupDelimit = new Boolean((String) this.properties.get("productGroup.delimit")).booleanValue(); //$NON-NLS-1$
		
		this.withdrawColumnWidth = this.getIntTokens((String) this.properties.get("withdraw.colwidth")); //$NON-NLS-1$
		this.withdrawFont = NumberUtility.parseInt(0, (String) this.properties.get("withdraw.font")); //$NON-NLS-1$
		// this.withdrawCols = NumberUtility.parseInt(0, (String)
		// this.properties
		//				.get("withdraw.columns")); //$NON-NLS-1$
		this.withdrawEmphasized = new Boolean((String) this.properties.get("withdraw.emphasized")).booleanValue(); //$NON-NLS-1$
		this.withdrawPrintmode = NumberUtility.parseInt(0, (String) this.properties.get("withdraw.printmode")); //$NON-NLS-1$
		this.withdrawJustify = NumberUtility.parseInt(0, (String) this.properties.get("withdraw.justify")); //$NON-NLS-1$
		this.withdrawDelimit = new Boolean((String) this.properties.get("withdraw.delimit")).booleanValue(); //$NON-NLS-1$
		
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
		
		this.cashdrawer = new boolean[NumberUtility.parseInt(0, (String) this.properties.get("drawer.count"))]; //$NON-NLS-1$
		for (int i = 0; i < this.cashdrawer.length; i++)
		{
			this.cashdrawer[i] = true;
		}
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
			//			Logger.getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
		}
		catch (IOException e)
		{
			//			Logger.getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
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
	
	public void posEventPerformed(PosEvent e)
	{
		Integer t = e.getPosAction().getActionType();
		if (t.equals(Action.POS_ACTION_SETTLE_DAY))
		{
			if (this.context.getParent() instanceof TabPanel)
			{
				if (Config.getInstance().getSalespointExport())
				{
					String path = Config.getInstance().getSalespointExportPath();
					File file = new File(path);
					if (Config.getInstance().getSalespointExport() && !file.isDirectory())
					{
						MessageDialog.showInformation(
										Frame.getMainFrame(),
										"Kein Exportpfad", Messages.getString("SettlementPrinter.Das_Exportverzeichnis_ist_ung_u00FCltig._Bevor_Sie_den_Abschluss_durchf_u00FChren_k_u00F6nnen,_1") + //$NON-NLS-1$
														System.getProperty("line.separator") + Messages.getString("SettlementPrinter.m_u00FCssen_Sie_bei_aktivierter_Option___Belege_exportieren___einen_g_u00FCltigen_Exportpfad__n_3") + //$NON-NLS-1$ //$NON-NLS-2$
														Messages.getString("SettlementPrinter.bestimmen._4")
														+ Messages.getString("SettlementPrinter.bestimmen._4"), 0);
						return;
					}
				}
				this.printer = ((TabPanel) this.context.getParent()).getReceiptPrinter().getPrinter();
				if (this.printer != null)
				{
					this.settleDay(e);
				}
			}
		}
	}
	
	private void setCharacterSettings(int font, boolean emphasized, int printmode, int justify)
	{
		this.printer.selectCharacterFont(font);
		this.printer.setEmphasized(emphasized);
		this.printer.selectPrintMode(printmode);
		this.printer.selectJustification(justify);
	}
	
	public boolean receiptsToSettle()
	{
		this.receiptCount = Receipt.countCurrent(Salespoint.getCurrent(), false);
		return this.receiptCount > 0;
	}
	
	/**
	 * 
	 * Der Tagesabschluss wird pro Kasse vorgenommen. Jeder Tagesabschlusslauf
	 * generiert eine eigene Tagesabschlussnummer, die den Tagesabschluss
	 * eindeutig identifiziert. Der Tagesabschlusslauf druckt einen
	 * entsprechenden Beleg auf den POSDrucker aus und markiert anschliessend
	 * die Belege als abgeschlossen. Nur abgeschlossene Belege werden in den
	 * Auswertungen beruecksichtigt. Abgeschlossene Belege koennen daher nicht
	 * mehr geaendert werden.
	 * 
	 * @param e
	 *            PosEvent
	 */
	private void settleDay(PosEvent e)
	{
		if (!(e.getPosAction() instanceof SettleDayAction))
		{
			return;
		}
		
		this.settleDayAction = (SettleDayAction) e.getPosAction();
		
		if (Database.getCurrent().equals(Database.getTemporary()))
		{
			MessageDialog.showInformation(Frame.getMainFrame(), "Ungültige Datenbank",
							"Der Tagesabschluss kann nur in der Standarddatenbank erfolgen.\n"
											+ "Die Verbindung Standarddatenbank kann erst durch einen Neustart\n"
											+ "der Kassenstation wieder aktiviert werden.", 0);
			return;
		}
		
		this.settlement = new Long(GregorianCalendar.getInstance().getTimeInMillis());
		this.date = new Date();
		this.time = new Time(this.date.getTime());
		
		Salespoint.getCurrent().currentDate = TabPanel.today;
		if (!this.testPrint)
		{
			Salespoint.getCurrent().store();
		}
		
		this.receiptCount = Receipt.countCurrent(Salespoint.getCurrent(), false);
		
		if (this.receiptCount > 0 || this.testForCountedCurrencies())
		{
			Thread thread = new Thread(this);
			thread.start();
		}
		else
		{
			MessageDialog.showInformation(Frame.getMainFrame(), "Keine Belege gefunden",
							"Es wurden keine Belege gefunden.", 0);
		}
	}
	
	public void run()
	{
		this.settlements.clear();
		
		this.progress = 0;
		int maxProgress = this.receiptCount + 13;
		this.monitor = new ProgressMonitor(Frame.getMainFrame(), "Tagesabschluss",
						"Der Tagesabschluss wird gestartet...", this.progress, maxProgress);
		
		if (this.receiptCount > 0)
			this.printSettlement(this.settlement, this.date, this.time,
							((Integer) this.settleDayAction.getValue(Action.POS_KEY_DRAWER_NUMBER)).intValue());
		else
		{
			this.printHeader(this.settlement, this.date, this.time, false);
			this.printStock(this.settlement);
			this.printFooter(((Integer) this.settleDayAction.getValue(Action.POS_KEY_DRAWER_NUMBER)).intValue());
		}
		
		if (!this.testPrint)
		{
			if (this.testForCountedCurrencies()) Salespoint.getCurrent().store();
			this.storeSettlements();
			this.context.getCoinCounter().propertyChange(new PropertyChangeEvent(this, "clearAll", null, null));
			/*
			 * 10435 Die Belege werden nach den Tagesabschluss gelöscht
			 */
			Receipt[] receipts = Receipt.select(Salespoint.getCurrent(), Receipt.RECEIPT_STATE_PARKED);
			for (int i = 0; i < receipts.length; i++)
			{
				receipts[i].delete();
			}
			/*
			 * 10435
			 */
		}
		TabPanel.unSettled = Receipt.selectCurrent(Salespoint.getCurrent());
		this.monitor.setProgress(++this.progress);
		this.monitor.close();
		
		if (!this.testPrint)
		{
			ModeChangeEvent event = new ModeChangeEvent(UserPanel.CONTEXT_MODE_POS);
			this.settleDayAction.fireModeChangeEvent(event);
		}
	}
	
	private void storeSettlements()
	{
		Settlement[] settlements = (Settlement[]) this.settlements.toArray(new Settlement[0]);
		for (int i = 0; i < settlements.length; i++)
		{
			settlements[i].setLineNumber(i);
			settlements[i].store();
		}
		
	}
	
	public boolean testForCountedCurrencies()
	{
		Hashtable currencies = this.context.getCoinCounter().getCurrencyCoins();
		Set keys = currencies.keySet();
		String[] codes = (String[]) keys.toArray(new String[0]);
		
		for (int i = 0; i < codes.length; i++)
		{
			ArrayList list = (ArrayList) currencies.get(codes[i]);
			Coin[] coins = (Coin[]) list.toArray(new Coin[0]);
			Coin counted = this.getCounted(coins);
			if (counted.amount != 0d || counted.quantity != 0) return true;
		}
		return false;
	}
	
	public Coin[] getCountedCurrencies()
	{
		ArrayList countedCurrencies = new ArrayList();
		Hashtable currencies = this.context.getCoinCounter().getCurrencyCoins();
		Set keys = currencies.keySet();
		String[] codes = (String[]) keys.toArray(new String[0]);
		
		for (int i = 0; i < codes.length; i++)
		{
			ArrayList list = (ArrayList) currencies.get(codes[i]);
			Coin[] coins = (Coin[]) list.toArray(new Coin[0]);
			Coin counted = this.getCounted(coins);
			if (counted.amount != 0 || counted.quantity != 0) countedCurrencies.add(counted);
		}
		return (Coin[]) countedCurrencies.toArray(new Coin[0]);
	}
	
	private void print(String s)
	{
		this.printer.println(s);
	}
	
	private void computePositions(Position[] positions)
	{
		for (int i = 0; i < positions.length; i++)
		{
			if (positions[i].getProductGroup().type == ProductGroup.TYPE_INCOME)
				this.computePosition(positions[i], this.incomes);
			else if (positions[i].getProductGroup().type == ProductGroup.TYPE_NOT_INCOME)
				this.computePosition(positions[i], this.otherSales);
			else if (positions[i].getProductGroup().type == ProductGroup.TYPE_EXPENSE)
				this.computePosition(positions[i], this.expenses);
			else if (positions[i].getProductGroup().type == ProductGroup.TYPE_INPUT)
				this.computePosition(positions[i], this.inputs);
			else if (positions[i].getProductGroup().type == ProductGroup.TYPE_WITHDRAW)
				this.computePosition(positions[i], this.withdraws);
			
			if (positions[i].getProductGroup().type != ProductGroup.TYPE_INPUT
							&& positions[i].getProductGroup().type != ProductGroup.TYPE_WITHDRAW)
			{
				this.computeTaxes(positions[i], this.taxes);
			}
			
			if (positions[i].isPayedInvoice())
			{
				this.payedInvoices.add(positions[i]);
			}
			if (positions[i].getProductGroup().type != ProductGroup.TYPE_INPUT
							&& positions[i].getProductGroup().type != ProductGroup.TYPE_WITHDRAW)
			{
				if (positions[i].getQuantity() < 0)
				{
					this.takeBacks.add(positions[i]);
				}
			}
		}
	}
	
	private void computePosition(Position position, Hashtable data)
	{
		Settlement settlement = (Settlement) data.get(position.getProductGroupId());
		if (settlement == null)
		{
			settlement = new Settlement(Salespoint.getCurrent(), this.settlement, position, this.receiptCount);
		}
		else
		{
			settlement.setData(position);
		}
		data.put(position.getProductGroupId(), settlement);
	}
	
	private void computeTaxes(Position position, Hashtable data)
	{
		Settlement settlement = (Settlement) data.get(position.getCurrentTaxId());
		if (settlement == null)
		{
			settlement = new Settlement(Salespoint.getCurrent(), this.settlement, position, position.getCurrentTax(),
							this.receiptCount);
		}
		else
		{
			settlement.setData(position, position.getCurrentTax());
		}
		
		data.put(position.getCurrentTaxId(), settlement);
	}
	
	private void computePayments(Payment[] payments)
	{
		for (int i = 0; i < payments.length; i++)
		{
			if (!payments[i].isInputOrWithdraw) this.putPaymentTypes(payments[i], this.payments);
		}
	}
	
	private void printSettlement(Long settlement, Date date, Time time, int drawer)
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
		
		this.payedInvoices.clear();
		this.takeBacks.clear();
		this.reversedReceipts.clear(); // 10438
		
		Database.getCurrent().getBroker().beginTransaction();
		
		Iterator receipts = Receipt.selectCurrentToIterator(Salespoint.getCurrent(), true); // 10436
		if (receipts.hasNext())
		{
			Element root = this.prepareExport(settlement);
			
			while (receipts.hasNext())
			{
				if (this.monitor.isCanceled())
				{
					Database.getCurrent().getBroker().abortTransaction();
					return;
				}
				
				this.monitor.setNote(++this.progress + " von " + this.receiptCount + " Belegen verarbeitet...");
				this.monitor.setProgress(this.progress);
				
				Receipt receipt = (Receipt) receipts.next();
				if (receipt.status == Receipt.RECEIPT_STATE_SERIALIZED)
				{
					this.computePositions(receipt.getPositionsAsArray());
					this.computePayments(receipt.getPaymentsAsArray());
				}
				// 10438
				else if (receipt.status == Receipt.RECEIPT_STATE_REVERSED)
				{
					this.reversedReceipts.add(receipt);
				}
				// 10438
				/*
				 * 10436
				 */
				if (!this.testPrint)
				{
					this.updateReceipt(receipt, settlement, root);
				}
				/*
				 * 10436
				 */
			}
			Database.getCurrent().getBroker().commitTransaction();
			
			this.monitor.setNote("Der Tagesabschluss wird ausgedruckt...");
			this.printHeader(settlement, date, time, true);
			this.monitor.setProgress(++this.progress);
			this.printPositions();
			this.monitor.setProgress(++this.progress);
			this.printPaymentTypes();
			this.monitor.setProgress(++this.progress);
			this.printSummary();
			this.monitor.setProgress(++this.progress);
			this.printWithdraws();
			this.monitor.setProgress(++this.progress);
			this.printTaxes();
			this.monitor.setProgress(++this.progress);
			this.printTakeBacks();
			this.monitor.setProgress(++this.progress);
			this.printPayedInvoices();
			this.monitor.setProgress(++this.progress);
			this.printReversedReceipts();
			this.monitor.setProgress(++this.progress);
			this.printStock(settlement);
			this.monitor.setProgress(++this.progress);
			this.printFooter(drawer);
			this.monitor.setProgress(++this.progress);
			this.saveExport(root);
			this.monitor.setProgress(++this.progress);
		}
		if (Database.getCurrent().getBroker().isInTransaction()) Database.getCurrent().getBroker().abortTransaction();
	}
	
	private void printHeader(Long settlement, Date date, Time time, boolean withSettlement)
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
		
		this.print(Salespoint.getCurrent().name);
		
		if (this.context.getUser().username.length() > 0)
		{
			this.print(this.context.getUser().username);
		}
		
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
				this.settlements.add(settlements[i]);
				
				this.amount += settlements[i].getAmount1();
				items += settlements[i].getQuantity();
				tax += settlements[i].getAmount2();
				
				String s = this.format(settlements[i].getShortText(), this.productGroupColumnWidth[0], 0);
				s = s.concat(this.format("" + settlements[i].getQuantity(), this.productGroupColumnWidth[1], 2)); //$NON-NLS-1$
				s = s.concat(this
								.format("" + NumberUtility.formatDouble(settlements[i].getAmount1(), 2, 2, true), this.productGroupColumnWidth[2], 2)); //$NON-NLS-1$
				s = s.concat(this
								.format("" + NumberUtility.formatDouble(settlements[i].getAmount2(), 2, 2, true), this.productGroupColumnWidth[3], 2)); //$NON-NLS-1$
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
		String s = this.format(
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
				this.settlements.add(settlements[i]);
				
				this.amount += settlements[i].getAmount1();
				items += settlements[i].getQuantity();
				tax += settlements[i].getAmount2();
				
				String s = this.format(settlements[i].getShortText(), this.productGroupColumnWidth[0], 0);
				s = s.concat(this.format("" + settlements[i].getQuantity(), this.productGroupColumnWidth[1], 2)); //$NON-NLS-1$
				s = s.concat(this
								.format("" + NumberUtility.formatDouble(settlements[i].getAmount1(), 2, 2, true), this.productGroupColumnWidth[2], 2)); //$NON-NLS-1$
				s = s.concat(this
								.format("" + NumberUtility.formatDouble(settlements[i].getAmount2(), 2, 2, true), this.productGroupColumnWidth[3], 2)); //$NON-NLS-1$
				this.print(s);
			}
			
			if (this.productGroupDelimit)
			{
				this.print(this.format(this.generalDelimiter, this.headerCols));
			}
			
		}
		String s = this.format(
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
				this.settlements.add(settlements[i]);
				
				this.amount += settlements[i].getAmount1();
				items += settlements[i].getQuantity();
				tax += settlements[i].getAmount2();
				String s = this.format(settlements[i].getShortText(), this.productGroupColumnWidth[0], 0);
				s = s.concat(this.format("" + settlements[i].getQuantity(), this.productGroupColumnWidth[1], 2)); //$NON-NLS-1$
				s = s.concat(this
								.format("" + NumberUtility.formatDouble(settlements[i].getAmount1(), 2, 2, true), this.productGroupColumnWidth[2], 2)); //$NON-NLS-1$
				s = s.concat(this
								.format("" + NumberUtility.formatDouble(settlements[i].getAmount2(), 2, 2, true), this.productGroupColumnWidth[3], 2)); //$NON-NLS-1$
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
	
	private void printWithdraws()
	{
		this.setCharacterSettings(this.withdrawFont, this.withdrawEmphasized, this.withdrawPrintmode,
						this.withdrawJustify);
		
		this.amount = 0d;
		int items = 0;
		double tax = 0d;
		
		this.print("Einlagen/Entnahmen");
		if (this.withdrawDelimit)
		{
			this.print(this.format(this.generalDelimiter, this.headerCols));
		}
		
		Enumeration ip = this.inputs.elements();
		Enumeration wd = this.withdraws.elements();
		Settlement[] settlements = new Settlement[this.inputs.size() + this.withdraws.size()];
		
		for (int i = 0; i < settlements.length; i++)
		{
			if (ip.hasMoreElements())
				settlements[i] = (Settlement) ip.nextElement();
			else if (wd.hasMoreElements()) settlements[i] = (Settlement) wd.nextElement();
		}
		
		if (settlements.length > 0)
		{
			Arrays.sort(settlements);
			for (int i = 0; i < settlements.length; i++)
			{
				this.settlements.add(settlements[i]);
				
				this.amount += settlements[i].getAmount1();
				items += settlements[i].getQuantity();
				tax += settlements[i].getAmount2();
				String s = this.format(settlements[i].getShortText(), this.withdrawColumnWidth[0], 0);
				s = s.concat(this.format("" + settlements[i].getQuantity(), this.withdrawColumnWidth[1], 2)); //$NON-NLS-1$
				s = s.concat(this
								.format("" + NumberUtility.formatDouble(settlements[i].getAmount1(), 2, 2, true), this.withdrawColumnWidth[2], 2)); //$NON-NLS-1$
				s = s.concat(this
								.format("" + NumberUtility.formatDouble(settlements[i].getAmount2(), 2, 2, true), this.withdrawColumnWidth[3], 2)); //$NON-NLS-1$
				this.print(s);
			}
			if (settlements.length > 0)
			{
				if (this.withdrawDelimit)
				{
					this.print(this.format(this.generalDelimiter, this.headerCols));
				}
			}
		}
		
		String s = this.format("Total Einlagen/Entnahmen", this.withdrawColumnWidth[0], 0);
		s = s.concat(this.format("" + items, this.withdrawColumnWidth[1], 2)); //$NON-NLS-1$
		s = s.concat(this.format(
						"" + NumberUtility.formatDouble(this.amount, 2, 2, true), this.withdrawColumnWidth[2], 2)); //$NON-NLS-1$
		s = s.concat(this.format("" + NumberUtility.formatDouble(tax, 2, 2, true), this.withdrawColumnWidth[3], 2)); //$NON-NLS-1$
		this.print(s);
		
		if (this.withdrawDelimit)
		{
			this.print(this.format(this.generalDelimiter, this.headerCols));
		}
		
		this.positionAmount += tax;
		this.positionCount += items;
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
			this.print(Messages.getString("SettlementPrinter.Zahlungsarten_109") + "            " + Messages.getString("SettlementPrinter.Zahlungsarten_1092")); //$NON-NLS-1$
		}
		else
		{
			this.print(Messages.getString("SettlementPrinter.Zahlungsarten_109") + "            " + Messages.getString("SettlementPrinter.Zahlungsarten_1091")); //$NON-NLS-1$
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
				this.settlements.add(settlements[i]);
				
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
					s = s.concat(this
									.format("" + NumberUtility.formatDouble(settlements[i].getAmount1(), 2, 2, true), this.paymentColumnWidth[1], 2)); //$NON-NLS-1$
				}
				if (printQuantity)
				{
					s = s.concat(this
									.format("" + NumberUtility.formatInteger(settlements[i].getQuantity(), true), this.paymentColumnWidth[2], 2)); //$NON-NLS-1$
				}
				else
				{
					s = s.concat(this.format("", this.paymentColumnWidth[2], 2)); //$NON-NLS-1$
				}
				s = s.concat(this
								.format("" + NumberUtility.formatDouble(settlements[i].getAmount2(), 2, 2, true), this.paymentColumnWidth[3], 2)); //$NON-NLS-1$
				this.print(s);
				
			}
			
			if (this.paymentDelimit) this.print(this.format(this.generalDelimiter, this.paymentCols));
		}
		
		String s = this.format(
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
	
	private void printPositionSummary()
	{
		this.setCharacterSettings(this.paymentFont, this.paymentEmphasized, this.paymentPrintmode, this.paymentJustify);
		
		String s = this.format("Total Bewegungen", this.productGroupColumnWidth[0], 0); //$NON-NLS-1$
		s = s.concat(this.format(
						"" + NumberUtility.formatInteger(this.positionCount, true), this.productGroupColumnWidth[1], 2)); //$NON-NLS-1$
		s = s.concat(this
						.format("" + NumberUtility.formatDouble(this.positionAmount, 2, 2, true), this.productGroupColumnWidth[2], 2)); //$NON-NLS-1$
		s = s.concat(this
						.format("" + NumberUtility.formatDouble(this.positionTax, 2, 2, true), this.productGroupColumnWidth[3], 2)); //$NON-NLS-1$
		this.print(s);
		
		if (this.paymentDelimit)
		{
			this.print(this.format(this.generalDelimiter, this.paymentCols));
		}
	}
	
	// private void printPaymentSummary()
	// {
	// this.setCharacterSettings(this.paymentFont, this.paymentEmphasized,
	// this.paymentPrintmode, this.paymentJustify);
	//
	//		String s = this.format("Total Zahlungen", this.paymentColumnWidth[0] + this.paymentColumnWidth[1], 0); //$NON-NLS-1$
	// s = s.concat(this.format(
	//						"" + NumberUtility.formatInteger(this.paymentCount, true), this.paymentColumnWidth[2], 2)); //$NON-NLS-1$
	// s = s.concat(this.format(
	//						"" + NumberUtility.formatDouble(this.paymentAmount, 2, 2, true), this.paymentColumnWidth[3], 2)); //$NON-NLS-1$
	// this.print(s);
	//
	// if (this.paymentDelimit)
	// {
	// this.print(this.format(this.generalDelimiter, this.paymentCols));
	// }
	// }
	
	private void printSummary()
	{
		this.setCharacterSettings(this.paymentFont, this.paymentEmphasized, this.paymentPrintmode, this.paymentJustify);
		
		this.print("Zusammenfassung"); //$NON-NLS-1$
		
		if (this.paymentDelimit)
		{
			this.print(this.format(this.generalDelimiter, this.paymentCols));
		}
		
		String text = "Total Bewegungen";
		this.settlements.add(new Settlement(Salespoint.getCurrent(), this.settlement, Settlement.TYPE_SUMMARY, text,
						this.positionCount, this.positionAmount, this.receiptCount));
		
		String s = this.format(text, this.paymentColumnWidth[0] + this.paymentColumnWidth[1], 0);
		s = s.concat(this.format(
						"" + NumberUtility.formatInteger(this.positionCount, true), this.paymentColumnWidth[2], 2)); //$NON-NLS-1$
		s = s.concat(this
						.format("" + NumberUtility.formatDouble(this.positionAmount, 2, 2, true), this.paymentColumnWidth[3], 2)); //$NON-NLS-1$
		this.print(s);
		
		text = "Total Zahlungen";
		this.settlements.add(new Settlement(Salespoint.getCurrent(), this.settlement, Settlement.TYPE_SUMMARY, text,
						this.paymentCount, this.paymentAmount, this.receiptCount));
		
		s = this.format("Total Zahlungen", this.paymentColumnWidth[0] + this.paymentColumnWidth[1], 0); //$NON-NLS-1$
		s = s.concat(this.format(
						"" + NumberUtility.formatInteger(this.paymentCount, true), this.paymentColumnWidth[2], 2)); //$NON-NLS-1$
		s = s.concat(this.format(
						"" + NumberUtility.formatDouble(this.paymentAmount, 2, 2, true), this.paymentColumnWidth[3], 2)); //$NON-NLS-1$
		this.print(s);
		
		if (this.paymentDelimit)
		{
			this.print(this.format(this.generalDelimiter, this.paymentCols));
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
				this.settlements.add(settlements[i]);
				
				amount += settlements[i].getAmount1();
				items += settlements[i].getQuantity();
				tax += settlements[i].getAmount2();
				
				String s = this.format(settlements[i].getShortText(), this.taxColumnWidth[0], 0);
				s = s.concat(this.format("" + settlements[i].getQuantity(), this.taxColumnWidth[1], 2)); //$NON-NLS-1$
				s = s.concat(this
								.format("" + NumberUtility.formatDouble(settlements[i].getAmount1(), 2, 2, true), this.taxColumnWidth[2], 2)); //$NON-NLS-1$
				s = s.concat(this
								.format("" + NumberUtility.formatDouble(settlements[i].getAmount2(), 2, 2, true), this.taxColumnWidth[3], 2)); //$NON-NLS-1$
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
	
	private void printReversedReceipts()
	{
		Element db = Config.getInstance().getDatabase("standard").getChild("connection");
		PBKey key = new PBKey(db.getAttributeValue("jcd-alias"), db.getAttributeValue("username"),
						db.getAttributeValue("password"));
		PersistenceBroker broker = PersistenceBrokerFactory.createPersistenceBroker(key);
		broker.beginTransaction();
		
		boolean isReversedHeaderPrinted = false;
		
		this.setCharacterSettings(this.taxFont, this.taxEmphasized, this.taxPrintmode, this.taxJustify);
		
		// 10438
		// Iterator receipts =
		// Receipt.selectCurrentReversed(Salespoint.getCurrent());
		Iterator receipts = this.reversedReceipts.iterator();
		// 10438
		if (receipts.hasNext())
		{
			SimpleDateFormat sdfd = new SimpleDateFormat();
			SimpleDateFormat sdft = new SimpleDateFormat();
			
			while (receipts.hasNext())
			{
				Receipt receipt = (Receipt) receipts.next();
				if (receipt.status == Receipt.RECEIPT_STATE_REVERSED)
				{
					if (!isReversedHeaderPrinted)
					{
						isReversedHeaderPrinted = true;
						
						this.print(Messages.getString("SettlementPrinter.Stornierte_Belege_126")); //$NON-NLS-1$
						if (this.reversedDelimit)
						{
							this.print(this.format(this.generalDelimiter, this.reversedCols));
						}
						
						sdfd.applyPattern(this.headerDatePattern);
						sdft.applyPattern(this.headerTimePattern);
					}
					
					this.settlements.add(new Settlement(Salespoint.getCurrent(), this.settlement, receipt,
									this.receiptCount));
					
					String s = this.format(receipt.getFormattedNumber(), this.reversedColumnWidth[0], 0);
					s = s.concat(this.format(sdfd.format(receipt.getDate()), this.reversedColumnWidth[1], 2));
					s = s.concat(this.format(sdft.format(receipt.getTime()), this.reversedColumnWidth[2], 2));
					s = s.concat(this
									.format("" + NumberUtility.formatDouble(receipt.getAmount(), 2, 2, true), this.reversedColumnWidth[3], 2)); //$NON-NLS-1$
					this.print(s);
					
					if (!this.testPrint)
					{
						receipt.setSettlement(this.settlement);
						broker.store(receipt);
					}
				}
			}
			
			if (isReversedHeaderPrinted)
			{
				if (this.reversedDelimit)
				{
					this.print(this.format(this.generalDelimiter, this.reversedCols));
				}
			}
		}
		broker.commitTransaction();
		broker.close();
	}
	
	private void printTakeBacks()
	{
		boolean isTakeBackPrinted = false;
		
		this.setCharacterSettings(this.taxFont, this.taxEmphasized, this.taxPrintmode, this.taxJustify);
		
		Iterator takeBacks = this.takeBacks.iterator();
		while (takeBacks.hasNext())
		{
			Position position = (Position) takeBacks.next();
			if (!isTakeBackPrinted)
			{
				isTakeBackPrinted = true;
				
				this.print("Rücknahmen"); //$NON-NLS-1$
				if (this.reversedDelimit)
				{
					this.print(this.format(this.generalDelimiter, this.reversedCols));
				}
			}
			
			this.settlements.add(new Settlement(Salespoint.getCurrent(), this.settlement, position,
							Settlement.TYPE_TOOK_BACK, this.receiptCount));
			
			String s = this.format(position.getProductGroup().shortname, this.reversedColumnWidth[0]
							+ this.reversedColumnWidth[1] + this.reversedColumnWidth[2], 0);
			s = s.concat(this
							.format("" + NumberUtility.formatDouble(position.getAmount(), 2, 2, true), this.reversedColumnWidth[3], 2)); //$NON-NLS-1$
			this.print(s);
		}
		
		if (isTakeBackPrinted)
		{
			if (this.reversedDelimit)
			{
				this.print(this.format(this.generalDelimiter, this.reversedCols));
			}
		}
	}
	
	private void printPayedInvoices()
	{
		boolean isPayedInvoicesPrinted = false;
		
		this.setCharacterSettings(this.taxFont, this.taxEmphasized, this.taxPrintmode, this.taxJustify);
		
		Iterator payedInvoices = this.payedInvoices.iterator();
		while (payedInvoices.hasNext())
		{
			Position position = (Position) payedInvoices.next();
			if (!isPayedInvoicesPrinted)
			{
				isPayedInvoicesPrinted = true;
				
				this.print("Rechnungen an der Kasse bezahlt"); //$NON-NLS-1$
				if (this.reversedDelimit)
				{
					this.print(this.format(this.generalDelimiter, this.reversedCols));
				}
			}
			
			this.settlements.add(new Settlement(Salespoint.getCurrent(), this.settlement, position,
							Settlement.TYPE_PAYED_INVOICES, this.receiptCount));
			
			String s = this.format(position.getProductGroup().shortname + " " + position.getInvoiceNumber() + " "
							+ this.shortDateFormat.format(position.getInvoiceDate()), this.reversedColumnWidth[0]
							+ this.reversedColumnWidth[1] + this.reversedColumnWidth[2], 0);
			s = s.concat(this
							.format("" + NumberUtility.formatDouble(position.getAmount(), 2, 2, true), this.reversedColumnWidth[3], 2)); //$NON-NLS-1$
			this.print(s);
		}
		
		if (isPayedInvoicesPrinted)
		{
			if (this.reversedDelimit)
			{
				this.print(this.format(this.generalDelimiter, this.reversedCols));
			}
		}
	}
	
	private void printStock(Long settlement)
	{
		this.setCharacterSettings(this.taxFont, this.taxEmphasized, this.taxPrintmode, this.taxJustify);
		
		Hashtable currencies = this.context.getCoinCounter().getCurrencyCoins();
		
		Set keys = currencies.keySet();
		
		String[] codes = (String[]) keys.toArray(new String[0]);
		Arrays.sort(codes);
		
		for (int i = 0; i < codes.length; i++)
		{
			ArrayList list = (ArrayList) currencies.get(codes[i]);
			Coin[] coins = (Coin[]) list.toArray(new Coin[0]);
			Coin counted = this.getCounted(coins);
			
			ForeignCurrency currency = ForeignCurrency.selectByCode(codes[i]);
			Collection payments = Payment.selectNotCounted(Salespoint.getCurrent(), currency);
			Coin takings = new Coin();
			Coin input = new Coin();
			Coin withdraw = new Coin();
			Iterator iterator = payments.iterator();
			while (iterator.hasNext())
			{
				Payment payment = (Payment) iterator.next();
				if (payment.getReceipt().status == Receipt.RECEIPT_STATE_SERIALIZED)
				{
					if (payment.getForeignCurrency().getId().equals(ForeignCurrency.getDefaultCurrency().getId()))
					{
						/**
						 * Es darf nur das Bargeld gezählt werden.
						 */
						if (payment.getPaymentType().getId().equals(PaymentType.getPaymentTypeCash().getId()))
						{
							if (payment.isInputOrWithdraw)
							{
								if (payment.getAmountFC() < 0d)
								{
									withdraw.amount += payment.getAmountFC();
									withdraw.quantity++;
								}
								else
								{
									input.amount += payment.getAmountFC();
									input.quantity++;
								}
							}
							else
							{
								takings.amount += payment.getAmountFC();
								takings.quantity++;
							}
						}
					}
					else
					{
						if (payment.isInputOrWithdraw)
						{
							if (payment.getAmountFC() < 0d)
							{
								withdraw.amount += payment.getAmountFC();
								withdraw.quantity++;
							}
							else
							{
								input.amount += payment.getAmountFC();
								input.quantity++;
							}
						}
						else
						{
							takings.amount += payment.getAmountFC();
							takings.quantity++;
						}
					}
				}
			}
			takings.amount = NumberUtility.round(takings.amount, 0.01d);
			input.amount = NumberUtility.round(input.amount, 0.01d);
			withdraw.amount = NumberUtility.round(withdraw.amount, 0.01d);
			
			Stock stock = Salespoint.getCurrent().getStock(currency);
			// Stock stock = Stock.select(Salespoint.getCurrent(), currency);
			
			if (counted.quantity == 0)
			{
				double oldStock = stock.getStock();
				int quantity = takings.quantity + input.quantity + withdraw.quantity;
				double amount = takings.amount + input.amount + withdraw.amount + oldStock;
				
				this.settlements.add(new Settlement(Salespoint.getCurrent(), this.settlement, i,
								Settlement.CASH_CHECK_BESTAND_SOLL, codes[i], quantity, amount, "Kassabestand",
								this.receiptCount));
				
				String s = this.format(codes[i] + " " + "Kassabestand", this.stockColumnWidth[0], 0); //$NON-NLS-1$
				s = s.concat(this.format(NumberUtility.formatInteger(quantity, true), this.stockColumnWidth[1], 2));
				s = s.concat(this.format(NumberUtility.formatDouble(amount, 2, 2, true), this.stockColumnWidth[2], 2)); // 10183
				this.print(s);
			}
			else
			{
				this.print(codes[i] + " " + Messages.getString("SettlementPrinter.Kassastock_127") + " " + codes[i]); //$NON-NLS-1$
				
				double amount = stock.getStock();
				this.settlements.add(new Settlement(Salespoint.getCurrent(), this.settlement, i,
								Settlement.CASH_CHECK_START, codes[i], amount, Messages
												.getString("SettlementPrinter.Kassastock_129")
												+ " "
												+ (Salespoint.getCurrent().variableStock ? "(var.)" : "(fix)"),
								this.receiptCount));
				
				String s = this.format(
								codes[i]
												+ " " + Messages.getString("SettlementPrinter.Kassastock_129") + " " + (Salespoint.getCurrent().variableStock ? "(var.)" : "(fix)"), this.stockColumnWidth[0] + this.stockColumnWidth[1], 0); //$NON-NLS-1$
				s = s.concat(this.format(NumberUtility.formatDouble(stock.getStock(), 2, 2, true),
								this.stockColumnWidth[2], 2));
				this.print(s);
				
				amount = takings.amount + input.amount + withdraw.amount + stock.getStock();
				this.settlements.add(new Settlement(Salespoint.getCurrent(), this.settlement, i,
								Settlement.CASH_CHECK_BESTAND_SOLL, codes[i], amount, Messages
												.getString("SettlementPrinter.Einnahmen_131"), this.receiptCount));
				
				s = this.format(codes[i] + " " + Messages.getString("SettlementPrinter.Einnahmen_131"), this.stockColumnWidth[0], 0); //$NON-NLS-1$
				s = s.concat(this.format("", this.stockColumnWidth[1], 2)); //$NON-NLS-1$
				s = s.concat(this.format(NumberUtility.formatDouble(amount, 2, 2, true), this.stockColumnWidth[2], 2));
				this.print(s);
				
				amount = counted.amount;
				this.settlements.add(new Settlement(Salespoint.getCurrent(), this.settlement, i,
								Settlement.CASH_CHECK_BESTAND_IST, codes[i], amount, Messages
												.getString("SettlementPrinter.Kassabestand_128"), this.receiptCount));
				
				s = this.format(codes[i] + " " + Messages.getString("SettlementPrinter.Kassabestand_128"), this.stockColumnWidth[0], 0); //$NON-NLS-1$
				s = s.concat(this.format("", this.stockColumnWidth[1], 2));
				s = s.concat(this.format(NumberUtility.formatDouble(counted.amount, 2, 2, true),
								this.stockColumnWidth[2], 2)); // 10183
				this.print(s);
				
				amount = takings.amount;
				this.settlements.add(new Settlement(Salespoint.getCurrent(), this.settlement, i,
								Settlement.CASH_CHECK_CHANGES_SOLL, codes[i], amount, "Bareinnahmen SOLL",
								this.receiptCount));
				
				s = this.format(codes[i] + " " + "Bareinnahmen SOLL", this.stockColumnWidth[0], 0); //$NON-NLS-1$
				s = s.concat(this.format("", this.stockColumnWidth[1], 2)); //$NON-NLS-1$
				s = s.concat(this.format(NumberUtility.formatDouble(takings.amount, 2, 2, true),
								this.stockColumnWidth[2], 2));
				this.print(s);
				
				amount = counted.amount;
				this.settlements.add(new Settlement(Salespoint.getCurrent(), this.settlement, i,
								Settlement.CASH_CHECK_CHANGES_IST, codes[i], amount, "Bareinnahmen IST",
								this.receiptCount));
				
				s = this.format(codes[i] + " " + "Bareinnahmen IST", this.stockColumnWidth[0], 0); //$NON-NLS-1$
				s = s.concat(this.format("", this.stockColumnWidth[1], 2)); //$NON-NLS-1$
				s = s.concat(this.format(NumberUtility.formatDouble(counted.amount - stock.getStock(), 2, 2, true),
								this.stockColumnWidth[2], 2));
				this.print(s);
				
				if (input.quantity > 0 || withdraw.quantity > 0)
				{
					if (input.quantity > 0)
					{
						amount = input.amount;
						this.settlements.add(new Settlement(Salespoint.getCurrent(), this.settlement, i,
										Settlement.CASH_CHECK_INPUTS, codes[i], amount, "Einlagen", this.receiptCount));
						
						s = this.format(codes[i] + " " + "Geldeinlagen Bank", this.stockColumnWidth[0], 0); //$NON-NLS-1$
						s = s.concat(this.format("", this.stockColumnWidth[1], 2)); //$NON-NLS-1$
						s = s.concat(this.format(NumberUtility.formatDouble(input.amount, 2, 2, true),
										this.stockColumnWidth[2], 2));
						this.print(s);
					}
					
					if (withdraw.quantity > 0)
					{
						amount = withdraw.amount;
						this.settlements.add(new Settlement(Salespoint.getCurrent(), this.settlement, i,
										Settlement.CASH_CHECK_WITHDRAWS, codes[i], amount, "Entnahmen",
										this.receiptCount));
						
						s = this.format(codes[i] + " " + "Geldentnahmen Bank", this.stockColumnWidth[0], 0); //$NON-NLS-1$
						s = s.concat(this.format("", this.stockColumnWidth[1], 2)); //$NON-NLS-1$
						s = s.concat(this.format(NumberUtility.formatDouble(withdraw.amount, 2, 2, true),
										this.stockColumnWidth[2], 2));
						this.print(s);
					}
				}
				
				int fractionDigits = stock.getForeignCurrency().getCurrency().getDefaultFractionDigits();
				double diff = takings.amount + input.amount + withdraw.amount + stock.getStock() - counted.amount;
				double difference = NumberUtility.round(diff, fractionDigits); // 10183
				String text = difference == 0d ? "Differenz" : difference < 0d ? "Zuviel in Kasse" : "Zuwenig in Kasse";
				
				this.settlements.add(new Settlement(Salespoint.getCurrent(), this.settlement, i,
								Settlement.CASH_CHECK_DIFFERENCE, codes[i], -difference, text, this.receiptCount));
				
				s = this.format(codes[i] + " " + text, this.stockColumnWidth[0], 0); //$NON-NLS-1$
				s = s.concat(this.format("", this.stockColumnWidth[1], 2)); //$NON-NLS-1$
				s = s.concat(this.format(
								NumberUtility.formatDouble(Math.abs(difference), fractionDigits, fractionDigits, true),
								this.stockColumnWidth[2], 2)); // 10183
				this.print(s);
				
				this.print("");
				
				this.print(codes[i] + " " + Messages.getString("SettlementPrinter.Kassensturz")); //$NON-NLS-1$
				
				Arrays.sort(coins, new CoinComparator());
				
				for (int j = 0; j < coins.length; j++)
				{
					int qnt = 0;
					if (coins[j].quantity > 0)
					{
						qnt += coins[j].quantity;
						
						double value = coins[j].value;
						int quantity = coins[j].quantity;
						amount = coins[j].amount;
						this.settlements.add(new Settlement(Salespoint.getCurrent(), this.settlement, i,
										Settlement.CASH_CHECK_MONEY, codes[i], value, quantity, amount, NumberUtility
														.formatDouble(coins[j].value, fractionDigits, fractionDigits,
																		true), this.receiptCount));
						
						s = this.format(coins[j].getForeignCurrency().code
										+ " "
										+ NumberUtility.formatDouble(coins[j].value, fractionDigits, fractionDigits,
														true), this.stockColumnWidth[0], 0);
						s = s.concat(this.format(NumberUtility.formatInteger(coins[j].quantity, true),
										this.stockColumnWidth[1], 2));
						s = s.concat(this.format(NumberUtility.formatDouble(coins[j].amount, fractionDigits,
										fractionDigits, true), this.stockColumnWidth[2], 2));
						this.print(s);
					}
				}
				
				s = this.format(" ", this.stockColumnWidth[0], 0); //$NON-NLS-1$
				s = s.concat(this.format("", this.stockColumnWidth[1], 0));
				s = s.concat(this.format("", this.stockColumnWidth[2], 0, '-'));
				this.print(s);
				
				s = this.format(codes[i] + " " + Messages.getString("SettlementPrinter.Total_Kassensturz"), this.stockColumnWidth[0], 0); //$NON-NLS-1$
				s = s.concat(this.format("", this.stockColumnWidth[1], 2));
				s = s.concat(this.format(
								NumberUtility.formatDouble(counted.amount, fractionDigits, fractionDigits, true),
								this.stockColumnWidth[2], 2)); // 10183
				this.print(s);
				
				s = this.format(" ", this.stockColumnWidth[0], 0); //$NON-NLS-1$
				s = s.concat(this.format("", this.stockColumnWidth[1], 0));
				s = s.concat(this.format("", this.stockColumnWidth[2], 0, '='));
				this.print(s);
				
				if (!this.testPrint)
				{
					if (Payment.updateCounted(payments, settlement))
					{
						if (Salespoint.getCurrent().variableStock)
						{
							stock.setStock(counted.amount);
							stock.store();
						}
					}
					else
					{
						MessageDialog.showInformation(
										Frame.getMainFrame(),
										"Fehler",
										"Die Aktualisierung der Münzzählung ist fehlgeschlagen.\nDie Zahlungen der Währung "
														+ codes[i]
														+ " konnten\nnicht aktualisiert werden. Bitte wiederholen Sie den Vorgang.",
										0);
					}
				}
			}
			if (this.stockDelimit) this.print(this.format(this.generalDelimiter, this.reversedCols));
			
		}
	}
	
	private Coin getCounted(Coin[] coins)
	{
		Coin coin = new Coin();
		for (int i = 0; i < coins.length; i++)
		{
			coin.setForeignCurrency(coins[i].getForeignCurrency());
			coin.amount += coins[i].amount;
			coin.quantity += coins[i].quantity;
		}
		
		coin.amount = NumberUtility.round(coin.amount, 0.01d);
		return coin;
	}
	
	private void printFooter(int drawer)
	{
		if (this.footerDelimit)
		{
			this.print(this.format(this.generalDelimiter, this.footerCols));
		}
		
		if (this.generalCut)
		{
			if (this.generalCutType.equals("partial"))
			{ //$NON-NLS-1$
				this.printer.partialCut(this.generalLfBeforeCut);
				// Logger.getLogger("colibri").info("      Beleg geschnitten.");
			}
			else if (this.generalCutType.equals("full"))
			{ //$NON-NLS-1$
				this.printer.fullCut(this.generalLfBeforeCut);
				// Logger.getLogger("colibri").info("      Beleg geschnitten.");
			}
		}
		
		// Logger.getLogger("colibri").info("      Schublade öffnen...");
		this.printer.kickOutDrawer(drawer);
		// Logger.getLogger("colibri").info("      Schublade geöffnet.");
	}
	
	private void putPaymentTypes(Payment payment, Hashtable paymentData)
	{
		String prefix;
		Settlement settlement;
		
		if (payment.getForeignCurrency().getId().equals(ForeignCurrency.getDefaultCurrency().getId()))
		{
			if (payment.isBack())
				prefix = payment.getPaymentType().cash ? "pt." : "rpt.";
			else
				prefix = "pt.";
			
			settlement = (Settlement) paymentData.get(prefix + payment.getPaymentType().getId());
			
		}
		else
		{
			prefix = "fc.";
			settlement = (Settlement) paymentData.get(prefix + payment.getForeignCurrency().getId());
		}
		
		if (settlement == null)
		{
			settlement = new Settlement(Salespoint.getCurrent(), this.settlement, payment, this.receiptCount);
		}
		else
		{
			settlement.setData(payment);
		}
		
		if (settlement.getSubtype() == Settlement.SUBTYPE_PAYMENT_PAYMENT_TYPE)
			paymentData.put(prefix + payment.getPaymentType().getId(), settlement);
		else if (settlement.getSubtype() == Settlement.SUBTYPE_PAYMENT_FOREIGN_CURRENCY)
			paymentData.put(prefix + payment.getForeignCurrency().getId(), settlement);
	}
	
	private Element prepareExport(Long settlement)
	{
		boolean export = Config.getInstance().getSalespointExport();
		Element root = new Element("transfer"); //$NON-NLS-1$
		if (export)
		{
			// Logger.getLogger("colibri").info(
			// "      Export vorbereiten...");
			root.setAttribute("salespoint", Salespoint.getCurrent().getId().toString()); //$NON-NLS-1$
			root.setAttribute("date", settlement.toString()); //$NON-NLS-1$
			root.setAttribute("count", new Integer(this.receiptCount).toString()); //$NON-NLS-1$
			// Logger.getLogger("colibri").info(
			// "      Export vorbereitet.");
		}
		return root;
	}
	
	private void saveExport(Element root)
	{
		if (root instanceof Element)
		{
			String path = Config.getInstance().getSalespointExportPath();
			File dir = new File(path);
			if (dir.exists() && dir.isDirectory())
			{
				String name = dir.getAbsolutePath() + File.separator + root.getAttributeValue("salespoint") + //$NON-NLS-1$
								root.getAttributeValue("date") + //$NON-NLS-1$
								".xml"; //$NON-NLS-1$
				try
				{
					File file = new File(name);
					if (!file.exists())
					{
						file.createNewFile();
					}
					OutputStream out = new FileOutputStream(file);
					Document doc = new Document(root);
					doc.setDocType(new DocType("transfer", "transfer.dtd")); //$NON-NLS-1$ //$NON-NLS-2$
					XMLOutputter xmlOut = new XMLOutputter();
					xmlOut.output(doc, out);
					out.close();
				}
				catch (FileNotFoundException e)
				{
					//					Logger.getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
				}
				catch (IOException e)
				{
					//					Logger.getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
				}
			}
		}
	}
	
	private void updateReceipt(Receipt receipt, Long settlement, Element root)
	{
		receipt.setSettlement(settlement);
		receipt.store(false);
		
		if (root instanceof Element)
		{
			root.addContent(receipt.buildJDOMElement(true));
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
	
	// 10226
	private class CoinComparator implements Comparator
	{
		public int compare(Object obj1, Object obj2)
		{
			Coin c1 = (Coin) obj1;
			Coin c2 = (Coin) obj2;
			
			int cf = c1.getForeignCurrency().code.compareTo(c2.getForeignCurrency().code);
			if (cf == 0)
				return new Double(c2.value).compareTo(new Double(c1.value));
			else
				return cf;
		}
	}
	
	// 10226
	
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
	// private int footerFont;
	private int footerCols;
	// private boolean footerEmphasized;
	// private int footerPrintmode;
	// private int footerJustify;
	private boolean footerDelimit;
	
	private int[] productGroupColumnWidth;
	private int productGroupFont;
	// private int productGroupCols;
	private boolean productGroupEmphasized;
	private int productGroupPrintmode;
	private int productGroupJustify;
	private boolean productGroupDelimit;
	
	private int[] withdrawColumnWidth;
	private int withdrawFont;
	// private int withdrawCols;
	private boolean withdrawEmphasized;
	private int withdrawPrintmode;
	private int withdrawJustify;
	private boolean withdrawDelimit;
	
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
	// private int reversedFont;
	private int reversedCols;
	// private boolean reversedEmphasized;
	// private int reversedPrintmode;
	// private int reversedJustify;
	private boolean reversedDelimit;
	
	private int[] stockColumnWidth;
	// private int stockFont;
	// private int stockCols;
	// private boolean stockEmphasized;
	// private int stockPrintmode;
	// private int stockJustify;
	private boolean stockDelimit;
	
	private double positionAmount;
	private int positionCount;
	private double positionTax;
	private double paymentAmount;
	private int paymentCount;
	
	// private Hashtable cashPayments;
	// private Collection stocks;
	
	private boolean[] cashdrawer;
	
	private ProgressMonitor monitor;
	private int progress;
	
	private SettleDayAction settleDayAction;
	private Long settlement;
	private Date date;
	private Time time;
}
