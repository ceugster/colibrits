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
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import ch.eugster.pos.db.Connection;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.events.InitializationListener;
import ch.eugster.pos.util.Path;

public class CorrectReceipts implements InitializationListener
{
	
	private String version = "0.2";
	private CommandLine commandLine = null;
	// private NumberFormat nf = null;
	// private Receipt receipt;
	
	private Date from = null;
	private Date to = null;
	private boolean correct = false;
	
	public CorrectReceipts(String[] args)
	{
		Options options = this.createOptions();
		this.readCommandLine(options, args);
		if (this.verifyCommandLine())
		{
			this.doWork(options);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		new CorrectReceipts(args);
	}
	
	private void doWork(Options options)
	{
		String helpText = "CorrectReceipts [ -h | <v | -f<yyyy.mm.dd> [-t<yyyy.mm.dd>]]";
		if (this.commandLine.hasOption("h"))
		{
			HelpFormatter hf = new HelpFormatter();
			hf.printHelp(helpText, options);
		}
		else if (this.commandLine.hasOption("v"))
		{
			System.out.println("program 'CorrectReceipts' Version " + this.version
							+ " (Copyright by Christian Eugster)");
		}
		else
		{
			this.connect();
			this.test();
			this.disconnect();
		}
	}
	
	private Options createOptions()
	{
		Options options = new Options();
		options.addOption(this.createOption("c", "correct", false, "Ab Datum (Format 'JJJJ.MM.TT').", false, null));
		options.addOption(this.createOption("f", "from", true, "Ab Datum (Format 'JJJJ.MM.TT').", false, null));
		options.addOption(this.createOption("t", "to", true, "Bis Datum (Format: 'JJJJ.MM.TT').", false, null));
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
	
	private boolean verifyCommandLine()
	{
		DateFormat dateFormat = SimpleDateFormat.getDateInstance();
		
		if (this.commandLine.hasOption('f'))
		{
			this.from = this.getDate(this.commandLine.getOptionValue('f'));
			if (this.from != null)
			{
				Calendar calendar = GregorianCalendar.getInstance();
				calendar.setTime(this.from);
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				this.from = calendar.getTime();
			}
		}
		
		if (this.commandLine.hasOption('t'))
		{
			this.to = this.getDate(this.commandLine.getOptionValue('t'));
			if (this.to != null)
			{
				Calendar calendar = GregorianCalendar.getInstance();
				calendar.setTime(this.to);
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				calendar.add(Calendar.DATE, 1);
				this.to = calendar.getTime();
			}
		}
		
		this.correct = this.commandLine.hasOption('c');
		
		System.out.println("Folgende Optionen wurden gewählt:");
		if (this.from != null) System.out.println("Startdatum (inklusiv): " + dateFormat.format(this.from));
		if (this.to != null) System.out.println("Enddatum     (exklusiv): " + dateFormat.format(this.to));
		System.out.println("Automatisch korrigieren: " + (this.correct ? "JA" : "NEIN"));
		
		System.out.print("Prüflauf starten? ");
		try
		{
			int read = System.in.read();
			return read == 'j' || read == 'J';
		}
		catch (IOException e)
		{
			return false;
		}
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
	
	private Date getDate(String dateString)
	{
		DateFormat dateFormat = SimpleDateFormat.getDateInstance();
		try
		{
			Calendar calendar = GregorianCalendar.getInstance();
			calendar.setTime(dateFormat.parse(dateString));
			return calendar.getTime();
		}
		catch (ParseException e)
		{
			return null;
		}
	}
	
	private void test()
	{
		Collection positionsToDelete = new ArrayList();
		int count = 0;
		NumberFormat numberFormat = DecimalFormat.getNumberInstance();
		DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
		Iterator iterator = Receipt.selectToCorrect(this.from, this.to);
		while (iterator.hasNext())
		{
			Receipt receipt = (Receipt) iterator.next();
			System.out.println(++count);
			double difference = receipt.getAmount() - receipt.getPayment();
			if (difference > 0.009)
			{
				System.out.println("Fehler in Beleg " + receipt.getNumber() + " vom "
								+ dateFormat.format(receipt.timestamp));
				System.out.println("       Differenz " + numberFormat.format(difference));
				
				Position[] positions = receipt.getPositionsAsArray();
				for (int i = 0; i < positions.length; i++)
				{
					double diff = positions[i].getAmount() - difference;
					if (Math.abs(diff) < 0.001)
					{
						System.out.println("Position mit Id " + positions[i].getId() + " zum Löschen markiert.");
						positionsToDelete.add(positions[i]);
					}
				}
			}
		}
		
		if (this.correct)
		{
			if (positionsToDelete.size() > 0)
			{
				System.out.print("Markierte Positionen löschen (" + positionsToDelete.size() + ")? ");
				try
				{
					int read = System.in.read();
					if (read == 'J' || read == 'j')
					{
						Position[] positions = (Position[]) positionsToDelete.toArray(new Position[0]);
						for (int i = 0; i < positions.length; i++)
						{
							positions[i].delete();
						}
					}
				}
				catch (IOException e)
				{
					return;
				}
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
