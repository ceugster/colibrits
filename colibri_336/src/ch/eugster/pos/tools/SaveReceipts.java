/**
 * Main.java
 * 
 * Programm zum extrahieren von Positionen aus der ColibriTS-Datenbank für das
 * Nachführen von Buchungen in Galileo.
 * 
 * 
 * Versionen
 * 
 * 0.3
 * Fehlermeldungen in Logfile eingebaut
 * 
 * 0.4
 * Import von Belegen über die Belegnummer implementiert
 * 
 */
package ch.eugster.pos.tools;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import ch.eugster.pos.client.model.PositionModel;
import ch.eugster.pos.db.Connection;
import ch.eugster.pos.db.Customer;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.Payment;
import ch.eugster.pos.db.PaymentType;
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.db.User;
import ch.eugster.pos.events.InitializationListener;
import ch.eugster.pos.product.Code128;
import ch.eugster.pos.product.ProductServer;
import ch.eugster.pos.util.Path;

public class SaveReceipts implements InitializationListener
{
	
	private String version = "0.3";
	private CommandLine commandLine = null;
	private Receipt receipt;
	private int count = 100;
	private int interval = 5;
	private String articleCode;
	private String customerCode;
	private int counter;
	private boolean canceled = false;
	private ProductServer productServer;
	private Long settlement = new Long(GregorianCalendar.getInstance().getTimeInMillis());
	
	public SaveReceipts(String[] args)
	{
		Options options = this.createOptions();
		this.readCommandLine(options, args);
		this.verifyCommandLine();
		this.doWork(options);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		new SaveReceipts(args);
		System.exit(0);
	}
	
	private void doWork(Options options)
	{
		String helpText = "import [ -h | -v |[-n<integer>] [-i<integer>] [-a<string>] [-c<string>]]";
		if (this.commandLine.hasOption("h"))
		{
			HelpFormatter hf = new HelpFormatter();
			hf.printHelp(helpText, options);
		}
		else if (this.commandLine.hasOption("v"))
		{
			System.out.println("program 'SaveReceipts' Version " + this.version + " (Copyright by Christian Eugster)");
		}
		else
		{
			Logger.getLogger("SaveReceipts").setLevel(Level.INFO);
			if (this.commandLine.hasOption('a') || this.commandLine.hasOption('c'))
			{
				Logger.getLogger("SaveReceipts").info("Datenbankverbindung wird hergestellt.");
				this.connect();
				if (this.openGalileo())
				{
					if (this.select()) this.store();
					this.closeGalileo();
				}
				Logger.getLogger("SaveReceipts").info("Datenbankverbindung wird geschlossen.");
				this.disconnect();
			}
		}
	}
	
	private Options createOptions()
	{
		Options options = new Options();
		options.addOption(this.createOption("a", "article", true,
						"Aktualisiert den Artikel mit dem angegebenen Artikelcode in Galileo.", false, null));
		options.addOption(this.createOption("c", "customer", true,
						"Benutzt den Kunden mit der angegebenen Kundennummer.", false, null));
		options.addOption(this.createOption("n", "number", true,
						"Anzahl Belege, die in die Datenbank geschrieben werden sollen (Vorschlagswert: 100).", false,
						null));
		options.addOption(this.createOption("i", "interval", true,
						"Interval zwischen den einzelnen Schreibvorgängen (in Sekunden, Vorschlagswert: 5).", false,
						null));
		options.addOption(this.createOption("h", "help", false, "Gibt die Optionen auf den Bildschirm aus.", false,
						null));
		options.addOption(this.createOption("v", "version", false,
						"Gibt die Version des Programms auf den Bildschirm aus.", false, null));
		return options;
	}
	
	private Option createOption(String shortName, String longName, boolean argumentRequired, String description,
					boolean isRequired, Object type)
	{
		Option option = new Option(shortName, longName, argumentRequired, description);
		option.setRequired(isRequired);
		return option;
	}
	
	private void readCommandLine(Options options, String[] arguments)
	{
		CommandLineParser clp = new GnuParser();
		try
		{
			this.commandLine = clp.parse(options, arguments);
		}
		catch (org.apache.commons.cli.ParseException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	private void verifyCommandLine()
	{
		if (this.commandLine.getOptionValue('n') != null)
		{
			try
			{
				this.count = Integer.valueOf(this.commandLine.getOptionValue('n')).intValue();
			}
			catch (NumberFormatException e)
			{
				this.count = 100;
			}
		}
		
		if (this.commandLine.getOptionValue('i') != null)
			this.interval = new Integer(this.commandLine.getOptionValue('i')).intValue();
		
		if (this.commandLine.getOptionValue('a') != null) this.articleCode = this.commandLine.getOptionValue('a');
		
		if (this.commandLine.getOptionValue('c') != null) this.customerCode = this.commandLine.getOptionValue('c');
	}
	
	/**
	 * 
	 * Datenbankverbindung herstellen
	 */
	private void connect()
	{
		System.setProperty("OJB.properties", Path.getInstance().ojbDir.concat(Path.getInstance().FILE_OJBCFG)); //$NON-NLS-1$
		System.out.print("Verbindung zur Datenbank herstellen...");
		Database.load();
		Database.setCurrent(Database.getStandard());
		if (Database.getCurrent().openConnection())
		{
			Database.getTemporary().openConnection();
			Database.getTemporary().initialize(this, 0);
			System.out.println("Ok!");
		}
		else
		{
			System.err.println("");
			System.err.println("Es konnte keine Verbindung zur Datenbank hergestellt werden.");
			System.exit(-1);
		}
	}
	
	private void disconnect()
	{
		System.out.print("Verbindung zur Datenbank schliessen...");
		Connection[] connections = Database.getConnections();
		for (int i = 0; i < connections.length; i++)
		{
			if (connections[i].getBroker() != null)
			{
				if (connections[i].getBroker().isInTransaction()) connections[i].getBroker().abortTransaction();
				
				connections[i].getBroker().close();
			}
		}
		System.out.println("Ok!");
	}
	
	private boolean openGalileo()
	{
		boolean connected = false;
		Logger.getLogger("SaveReceipts").info("Verbindung zu Galileo wird hergestellt.");
		this.productServer = ProductServer.getInstance();
		if (this.productServer instanceof ProductServer)
		{
			Logger.getLogger("SaveReceipts").info("Verbindung zu Galileo wurde hergestellt.");
			connected = this.getGalileoData(this.articleCode);
		}
		else
		{
			Logger.getLogger("SaveReceipts").info(
							"Verbindung zu Galileo konnte nicht hergestellt werden, Programm wird beendet.");
		}
		return connected;
	}
	
	private void closeGalileo()
	{
		Logger.getLogger("SaveReceipts").info("Verbindung zu Galileo wird geschlossen.");
		if (this.productServer instanceof ProductServer) this.productServer.close();
	}
	
	private boolean select()
	{
		Logger.getLogger("SaveReceipts").info("Beleg wird generiert.");
		Object object = Receipt.select();
		if (object != null && object instanceof Object[])
		{
			Object[] objects = (Object[]) object;
			if (objects.length > 0)
			{
				this.receipt = Receipt.selectById((Long) objects[0]);
				Salespoint.setCurrent(this.receipt.getSalespoint());
				User.setCurrentUser(this.receipt.getUser());
				if (this.productServer instanceof ProductServer)
				{
					this.receipt.getPayments().clear();
					this.receipt.getPositions().clear();
				}
				return true;
			}
		}
		return false;
	}
	
	private Customer getCustomer(String code)
	{
		Logger.getLogger("SaveReceipts").info("Kunde " + code + " wird gesucht.");
		Customer customer = null;
		String customerCode = code;
		if (code.length() == 13)
		{
			customerCode = code.substring(3, 12);
			Logger.getLogger("SaveReceipts").info(
							"Kundennummer wird aus " + code + " extrahiert: " + customerCode + ".");
		}
		try
		{
			Long.valueOf(customerCode == null ? "" : customerCode);
		}
		catch (NumberFormatException e)
		{
			customerCode = "0";
		}
		if (this.productServer.getCustomer(Integer.valueOf(customerCode)))
		{
			Logger.getLogger("SaveReceipts").info("Kunde " + customerCode + " gefunden.");
			customer = this.productServer.getCustomerObject();
			customer.setId(customerCode);
			System.out.println(customer.getAccount());
		}
		else
		{
			Logger.getLogger("SaveReceipts").info("Kunde " + customerCode + " nicht gefunden.");
		}
		return customer;
	}
	
	private boolean getGalileoData(String code)
	{
		String articleCode = code;
		Code128 code128 = Code128.getCode128(code);
		if (code128 != null)
		{
			articleCode = code128.getArticleCode(code);
			Logger.getLogger("SaveReceipts").info(
							code + " ist ein Code 128, Artikelnummer wird extrahiert: " + articleCode + ".");
		}
		Logger.getLogger("SaveReceipts").info("Artikel " + code + " wird gesucht.");
		boolean found = this.productServer.getItem(articleCode);
		if (found)
		{
			Logger.getLogger("SaveReceipts").info("Artikel " + code + " gefunden.");
			return true;
		}
		return false;
	}
	
	private void store()
	{
		// ProductServer.setUsed(false);
		
		final Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask()
		{
			public void run()
			{
				if (SaveReceipts.this.counter > SaveReceipts.this.count)
				{
					SaveReceipts.this.canceled = true;
					timer.cancel();
				}
				
				SaveReceipts.this.counter++;
				System.out.print(SaveReceipts.this.counter + ". Beleg speichern...");
				
				Receipt receipt = SaveReceipts.this.receipt.clone(false);
				receipt.setCustomer(SaveReceipts.this.getCustomer(SaveReceipts.this.customerCode));
				if (SaveReceipts.this.productServer instanceof ProductServer && SaveReceipts.this.articleCode != null)
				{
					Position position = Position.getInstance(receipt);
					if (SaveReceipts.this.getGalileoData(SaveReceipts.this.articleCode))
					{
						position.productId = SaveReceipts.this.articleCode;
						position.galileoBook = true;
						position.productNumber = SaveReceipts.this.articleCode;
						SaveReceipts.this.productServer.setData(position);
						PositionModel.setText(position);
						receipt.addPosition(position);
						receipt.setSettlement(SaveReceipts.this.settlement);
						Payment payment = Payment.getInstance(receipt);
						payment.setSettlement(SaveReceipts.this.settlement);
						payment.setForeignCurrency(ForeignCurrency.getDefaultCurrency());
						payment.setPaymentType(PaymentType.getPaymentTypeCash());
						payment.setAmount(position.getAmount());
						receipt.addPayment(payment);
						receipt.store(true, true);
					}
				}
				Logger.getLogger("SaveReceipts").info("Zum Beenden beliebige Taste drücken...");
			}
			
			public long scheduledExecutionTime()
			{
				return 0;
			}
		}, 1000, this.interval);
		
		int in = -1;
		while (!this.canceled)
		{
			System.out.println("running...");
			try
			{
				in = System.in.read();
				if (in > -1) timer.cancel();
				return;
			}
			catch (IOException e)
			{
			}
		}
	}
	
	public void initialized(String text)
	{
	}
	
	public void initialized(int value, String text)
	{
	}
	
}
