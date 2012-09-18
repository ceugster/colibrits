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
import java.text.NumberFormat;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import ch.eugster.pos.db.Connection;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.Payment;
import ch.eugster.pos.db.PaymentType;
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.db.User;
import ch.eugster.pos.events.InitializationListener;
import ch.eugster.pos.product.ProductServer;
import ch.eugster.pos.util.Path;

public class SaveReceipts implements InitializationListener
{
	
	private String version = "0.2";
	private CommandLine commandLine = null;
	private NumberFormat nf = null;
	private Receipt receipt;
	private int count = 100;
	private int interval = 5;
	private String code;
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
		String helpText = "import [ -h | -v |[-c<integer>] [-i<integer>] [-a<string>]]";
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
			this.connect();
			if (this.commandLine.hasOption('a')) this.openGalileo();
			if (this.select()) this.store();
			if (this.commandLine.hasOption('a')) this.closeGalileo();
			this.disconnect();
		}
	}
	
	private Options createOptions()
	{
		Options options = new Options();
		options.addOption(this.createOption("a", "article", true,
						"Aktualisiert den Artikel mit dem angegebenen Artikelcode in Galileo.", false, null));
		options.addOption(this.createOption("c", "count", true,
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
		if (this.commandLine.getOptionValue('c') != null)
			this.count = new Integer(this.commandLine.getOptionValue('c')).intValue();
		
		if (this.commandLine.getOptionValue('i') != null)
			this.interval = new Integer(this.commandLine.getOptionValue('i')).intValue();
		
		if (this.commandLine.getOptionValue('a') != null) this.code = this.commandLine.getOptionValue('a');
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
	
	private void openGalileo()
	{
		this.productServer = ProductServer.getInstance();
		if (this.productServer instanceof ProductServer) System.out.println("Galileo aktiviert.");
	}
	
	private void closeGalileo()
	{
		if (this.productServer instanceof ProductServer) this.productServer.close();
	}
	
	private boolean select()
	{
		System.out.print("Beleg wird generiert...");
		
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
				System.out.println("Ok!");
				return true;
			}
		}
		System.out.println("fehlgeschlagen!");
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
				
				if (SaveReceipts.this.productServer instanceof ProductServer && SaveReceipts.this.code != null)
				{
					if (SaveReceipts.this.productServer.getItem(SaveReceipts.this.code))
					{
						Position position = Position.getInstance(receipt);
						position.productId = SaveReceipts.this.code;
						position.galileoBook = true;
						SaveReceipts.this.productServer.setData(position);
						receipt.addPosition(position);
						receipt.setSettlement(SaveReceipts.this.settlement);
						Payment payment = Payment.getInstance(receipt);
						payment.setSettlement(SaveReceipts.this.settlement);
						payment.setForeignCurrency(ForeignCurrency.getDefaultCurrency());
						payment.setPaymentType(PaymentType.getPaymentTypeCash());
						payment.setAmount(position.getAmount());
						receipt.addPayment(payment);
					}
				}
				receipt.store(true, true);
				System.out.println("Ok!");
				System.out.println("Zum Beenden beliebige Taste drücken...");
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
