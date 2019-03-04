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

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.zip.GZIPInputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import ch.eugster.pos.InvalidValueException;
import ch.eugster.pos.db.Connection;
import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.Payment;
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.util.Path;

public class Import
{
	
	private String version = "0.4";
	private CommandLine commandLine = null;
	// private NumberFormat nf = null;
	
	// private Properties properties = null;
	// private File propertiesFile;
	
	// private Hashtable pg = null;
	// private Hashtable recap = null;
	
	// private Long number = null;
	private Date fromDate = null;
	private Date toDate = null;
	
	private int count = 0;
	
	private PrintStream output = null;
	private SAXBuilder builder = null;
	
	// private final DateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
	// private final DateFormat st = new SimpleDateFormat("HH-mm-ss");
	// private final DateFormat ts = SimpleDateFormat.getDateTimeInstance();
	
	private static final String PATH_SAVE = System.getProperty("user.dir").concat(
					File.separator.concat("save".concat(File.separator)));
	
	// private static final String PATH_FAILOVER =
	// Import.PATH_SAVE.concat("failover");
	
	public Import(String[] args)
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
		new Import(args);
	}
	
	private void doWork(Options options)
	{
		String helpText = "import [ -h | -v | [-n<number> | [-s<date> -e<date>] | -f | -r | -c | -d | -g | -i | -l ]]";
		if (this.commandLine.hasOption("h"))
		{
			HelpFormatter hf = new HelpFormatter();
			hf.printHelp(helpText, options);
		}
		else if (this.commandLine.hasOption("v"))
		{
			System.out.println("program 'import' Version " + this.version + " (Copyright by Christian Eugster)");
		}
		else if (!this.commandLine.hasOption('n')
						&& !(this.commandLine.hasOption('s') && this.commandLine.hasOption('e')))
		{
			HelpFormatter hf = new HelpFormatter();
			hf.printHelp(helpText, options);
		}
		else
		{
			this.openLogFile();
			this.connect();
			this.moveData();
			this.disconnect();
			this.closeLogFile();
		}
	}
	
	private Options createOptions()
	{
		Options options = new Options();
		options.addOption(this.createOption("c", "count", false,
						"Die zu verarbeitenden Belege werden nur gezählt, ein Import wird nicht durchgeführt.", false,
						null));
		options.addOption(this.createOption("e", "enddate", true,
						"Enddatum. Belege bis zu diesem Datum sollen verarbeitet werden.\nFormat: jjjj-mm-ttThh:mm:ss",
						false, GregorianCalendar.getInstance()));
		options.addOption(this.createOption("d", "delete", false,
						"Dateien der importierten Belege löschen. Mögliche Werte: [no], yes", false, null));
		options.addOption(this.createOption("f", "failover", false,
						"Alle Dateien im Verzeichnis 'failover' importieren.", false, null));
		options.addOption(this.createOption("i", "ignore", false, "Belege nicht speichern.", false, null));
		options.addOption(this.createOption("g", "galileo", false, "Daten in Galileo aktualisieren.", false, null));
		options.addOption(this.createOption("h", "help", false, "Hilfe anzeigen.", false, null));
		options.addOption(this.createOption("l", "log", false,
						"Protokolliert die vorgenommenen Änderungen in die Datei 'ImportTool.log' im log-Verzeichnis.",
						false, null));
		options.addOption(this.createOption("n", "number", true,
						"Nur den Beleg mit der angegebenen Nummer soll verarbeitet werden.", false, null));
		options.addOption(this.createOption("s", "startdate", true,
						"Beginndatum. Belege ab diesem Datum sollen verarbeitet werden.\nFormat: jjjj-mm-ttThh:mm:ss",
						false, GregorianCalendar.getInstance()));
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
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	private void verifyCommandLine()
	{
		if (this.commandLine.hasOption('s'))
		{
			this.fromDate = this.getFromDate();
		}
		if (this.commandLine.hasOption('e'))
		{
			this.toDate = this.getToDate();
		}
	}
	
	private Date getFromDate()
	{
		Date date = null;
		String input = this.commandLine.getOptionValue('s').replace('T', ' ');
		try
		{
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			date = df.parse(input);
		}
		catch (java.text.ParseException e)
		{
			System.err.println("Das Beginndatum muss im Format 'jjjj-mm-ddThh:mm:ss angegeben werden.");
			System.exit(-1);
		}
		return date;
	}
	
	private Date getToDate()
	{
		Date date = null;
		String input = this.commandLine.getOptionValue('e').replace('T', ' ');
		try
		{
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			date = df.parse(input);
		}
		catch (java.text.ParseException e)
		{
			System.err.println("Das Enddatum muss im Format 'jjjj-mm-ddThh:mm:ss angegeben werden.");
			System.exit(-1);
		}
		return date;
	}
	
	// private void moveData()
	// {
	// if (commandLine.hasOption('n'))
	// {
	// moveReceiptByNumber();
	// }
	// else
	// {
	// moveDateRange();
	// }
	// }
	
	private void moveData()
	{
		File saveDir = new File(Import.PATH_SAVE);
		if (saveDir.isDirectory())
		{
			File[] directories = this.getDirectories(saveDir);
			if (directories.length == 0)
			{
				System.err.println("Es wurden keine zu verarbeitenden Dateien gefunden.");
				System.exit(-1);
			}
			else
			{
				this.loopDirectories(directories);
			}
		}
		else
		{
			System.err.println("Das Objekt '" + saveDir.getName() + "' ist kein Verzeichnis.");
			System.exit(-1);
		}
	}
	
	// private void moveDateRange()
	// {
	// File saveDir = new File(Import.PATH_SAVE);
	// if (saveDir.isDirectory())
	// {
	// File[] directories = this.getDirectories(saveDir);
	// if (directories.length == 0)
	// {
	// System.err.println("Es wurden keine zu verarbeitenden Dateien gefunden.");
	// System.exit(-1);
	// }
	// else
	// {
	// this.loopDirectories(directories);
	// }
	// }
	// else
	// {
	// System.err.println("Das Objekt '" + saveDir.getName() +
	// "' ist kein Verzeichnis.");
	// System.exit(-1);
	// }
	// }
	
	private File[] getDirectories(File parentDir)
	{
		// File[] directories = null;
		if (this.commandLine.hasOption("f"))
		{
			return parentDir.listFiles(new FailoverDirectoryFileFilter());
		}
		else
		{
			return parentDir.listFiles(new DateDirectoryFileFilter(this.fromDate, this.toDate));
		}
	}
	
	private void loopDirectories(File[] directories)
	{
		FileFilter filter;
		if (this.commandLine.hasOption('n'))
		{
			filter = new ReceiptNumberFileFilter();
		}
		else
		{
			filter = new ReceiptDateRangeFileFilter();
		}
		
		for (int i = 0; i < directories.length; i++)
		{
			File[] files = directories[i].listFiles(filter);
			if (files.length > 0)
			{
				for (int j = 0; j < files.length; j++)
				{
					this.count++;
					if (this.commandLine.hasOption('c'))
					{
						this.write("Datei: " + files[j].getName() + " zählen...Ok!");
					}
					else
					{
						this.write("Datei: " + files[j].getName() + " lesen...");
						Document document = this.buildDocument(files[j]);
						this.write("Ok! erstellen...");
						Receipt receipt = this.buildReceipt(document, files[j]);
						this.write("Ok! prüfen...");
						
						if (this.commandLine.hasOption('g'))
						{
							this.write(" in Galileo buchen...");
							receipt.bookGalileo(true);
							this.write(" Ok!");
						}
						
						if (this.testReceipt(receipt))
						{
							this.write("Ok! bereits vorhanden...");
							if (this.receiptExists(receipt))
							{
								this.write("Ja! ");
								if (this.commandLine.hasOption('d'))
								{
									this.write(" löschen...");
									files[j].delete();
									this.write("Ok!");
								}
							}
							else
							{
								this.write("Nein! ");
								if (!this.commandLine.hasOption('i'))
								{
									this.write(" speichern...");
									DBResult result = receipt.store();
									if (result.getErrorCode() == 0)
									{
										this.write("Ok! ");
										if (this.commandLine.hasOption('d'))
										{
											this.write(" löschen...");
											files[j].delete();
											this.write("Ok!");
										}
									}
									else
									{
										this.write("Fehler!");
									}
								}
							}
						}
					}
					this.writeln("");
				}
			}
		}
		System.out.println("Verarbeitete Dateien: " + this.count);
	}
	
	/**
	 * 
	 * Datenbankverbindung herstellen
	 */
	private void connect()
	{
		if (!this.commandLine.hasOption('c'))
		{
			System.setProperty("OJB.properties", Path.getInstance().ojbDir.concat(Path.getInstance().FILE_OJBCFG)); //$NON-NLS-1$
			System.out.print("Verbindung zur Datenbank herstellen...");
			this.write("Verbindung zur Datenbank herstellen...");
			Database.load();
			Database.setCurrent(Database.getStandard());
			if (Database.getCurrent().openConnection())
			{
				System.out.println("Ok!");
				this.writeln("Ok!");
			}
			else
			{
				System.err.println("");
				System.err.println("Es konnte keine Verbindung zur Datenbank hergestellt werden.");
				System.exit(-1);
			}
		}
	}
	
	private void disconnect()
	{
		if (!this.commandLine.hasOption('c'))
		{
			System.out.print("Verbindung zur Datenbank schliessen...");
			this.write("Verbindung zur Datenbank schliessen...");
			Connection[] connections = Database.getConnections();
			for (int i = 0; i < connections.length; i++)
			{
				if (connections[i].getBroker() != null)
				{
					connections[i].getBroker().close();
				}
			}
			System.out.println("Ok!");
			this.writeln("Ok!");
		}
	}
	
	private Document buildDocument(File file)
	{
		InputStream in = this.openFile(file);
		Document document = this.buildDocument(in, file);
		this.close(in, file);
		return document;
	}
	
	private InputStream openFile(File file)
	{
		InputStream in = null;
		
		try
		{
			if (file.getName().endsWith(".xml.zip"))
			{
				InputStream inStream = new FileInputStream(file);
				in = new GZIPInputStream(inStream);
			}
			else
			{
				in = new FileInputStream(file);
			}
		}
		catch (IOException e)
		{
			String msg = "Die Datei '" + file.getName() + "' kann nicht geöffnet werden.";
			this.writeln("Fehler!");
			this.writeln("   " + msg);
			this.writeln("   " + e.getLocalizedMessage());
			System.err.println(msg);
			System.err.println(e.getLocalizedMessage());
			System.exit(-1);
		}
		return in;
	}
	
	private void close(InputStream in, File file)
	{
		try
		{
			in.close();
		}
		catch (IOException e)
		{
			String msg = "Die Datei '" + file.getName() + "' kann nicht geschlossen werden.";
			this.writeln("Fehler!");
			this.writeln("   " + msg);
			this.writeln("   " + e.getLocalizedMessage());
			System.err.println(msg);
			System.err.println(e.getLocalizedMessage());
			System.exit(-1);
		}
	}
	
	private SAXBuilder getBuilder()
	{
		if (this.builder == null)
		{
			this.builder = new SAXBuilder();
		}
		return this.builder;
	}
	
	private Document buildDocument(InputStream in, File file)
	{
		Document document = null;
		try
		{
			document = this.getBuilder().build(in, Path.getInstance().cfgDir.concat("receipt.dtd"));
		}
		catch (IOException e)
		{
			String msg = "Die Datei '" + file.getName() + "' kann nicht geschlossen werden.";
			this.writeln("Fehler!");
			this.writeln("   " + msg);
			this.writeln("   " + e.getLocalizedMessage());
			System.err.println(msg);
			System.err.println(e.getLocalizedMessage());
			System.exit(-1);
		}
		catch (JDOMException e)
		{
			String msg = "Die Datei '" + file.getName() + "' kann nicht geschlossen werden.";
			this.writeln("Fehler!");
			this.writeln("   " + msg);
			this.writeln("   " + e.getLocalizedMessage());
			System.err.println(msg);
			System.err.println(e.getLocalizedMessage());
			System.exit(-1);
		}
		return document;
	}
	
	private Receipt buildReceipt(Document document, File file)
	{
		Element root = document.getRootElement();
		Receipt receipt = Receipt.getEmptyReceipt();
		receipt = this.setRecordAttributes(receipt, root, file);
		
		Element positionsList = root.getChild("positions");
		Element[] positions = (Element[]) positionsList.getChildren("position").toArray(new Element[0]);
		for (int i = 0; i < positions.length; i++)
		{
			Position position = Position.getEmptyInstance();
			position = this.setRecordAttributes(receipt, position, positions[i], file, i + 1);
			receipt.addPosition(position);
		}
		
		Element paymentsList = root.getChild("payments");
		Element[] payments = (Element[]) paymentsList.getChildren("payment").toArray(new Element[0]);
		for (int i = 0; i < payments.length; i++)
		{
			Payment payment = Payment.getEmptyInstance();
			payment = this.setRecordAttributes(receipt, payment, payments[i], file, i + 1);
			receipt.addPayment(payment);
		}
		return receipt;
	}
	
	private Receipt setRecordAttributes(Receipt receipt, Element element, File file)
	{
		try
		{
			receipt.setRecordAttributes(element, true, false);
		}
		catch (InvalidValueException e)
		{
			String msg = "Die Datei '" + file.getName() + "' enthält ungültige Werte:";
			this.writeln("Fehler!");
			this.writeln("   " + msg);
			this.writeln("   " + e.getLocalizedMessage());
			System.err.println(msg);
			System.err.println(e.getLocalizedMessage());
			System.exit(-1);
		}
		return receipt;
	}
	
	private Position setRecordAttributes(Receipt receipt, Position position, Element element, File file, int count)
	{
		try
		{
			position.setRecordAttributes(receipt, element, false);
		}
		catch (InvalidValueException e)
		{
			String msg = "Die Datei '" + file.getName() + "' enthält ungültige Werte in der " + count + ". Position:";
			this.writeln("Fehler!");
			this.writeln("   " + msg);
			this.writeln("   " + e.getLocalizedMessage());
			System.err.println(msg);
			System.err.println(e.getLocalizedMessage());
			System.exit(-1);
		}
		return position;
	}
	
	private Payment setRecordAttributes(Receipt receipt, Payment payment, Element element, File file, int count)
	{
		try
		{
			payment.setRecordAttributes(receipt, element, false);
		}
		catch (InvalidValueException e)
		{
			String msg = "Die Datei '" + file.getName() + "' enthält ungültige Werte in der " + count + ". Zahlung:";
			this.writeln("Fehler!");
			this.writeln("   " + msg);
			this.writeln("   " + e.getLocalizedMessage());
			System.err.println(msg);
			System.err.println(e.getLocalizedMessage());
			System.exit(-1);
		}
		return payment;
	}
	
	private boolean testReceipt(Receipt receipt)
	{
		if (this.fromDate != null)
		{
			if (receipt.timestamp.before(this.fromDate))
			{
				return false;
			}
		}
		if (this.toDate != null)
		{
			if (receipt.timestamp.after(this.toDate))
			{
				return false;
			}
		}
		return true;
	}
	
	private boolean receiptExists(Receipt receipt)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("number", receipt.getNumber());
		Query query = QueryFactory.newQuery(Receipt.class, criteria);
		return Database.getCurrent().getBroker().getCount(query) == 0 ? false : true;
	}
	
	private void openLogFile()
	{
		if (this.commandLine.hasOption('l'))
		{
			System.out.print("Protokolldatei öffnen...");
			File file = new File(Path.getInstance().logDir.concat(File.separator.concat("ImportTool.log")));
			
			if (file.exists())
			{
				file.delete();
			}
			try
			{
				DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
				this.output = new PrintStream(new FileOutputStream(file));
				this.output.println("ImportTool Protokoll vom " + format.format(new Date()));
				this.output.println("--------------------------------------------");
				this.output.println("Einstellungen: ");
				if (this.commandLine.hasOption('n'))
					this.output.println("-n Belegnummer: " + this.commandLine.getOptionValue('n'));
				if (this.commandLine.hasOption('s'))
					this.output.println("-s Startdatum: " + this.commandLine.getOptionValue('s'));
				if (this.commandLine.hasOption('e'))
					this.output.println("-e Enddatum: " + this.commandLine.getOptionValue('e'));
				if (this.commandLine.hasOption('c'))
					this.output.println("-c Probelauf: Nur Anzahl der zu importierenden Belege zählen");
				if (this.commandLine.hasOption('d')) this.output.println("-d Dateien importierter Belege löschen");
				if (this.commandLine.hasOption('f')) this.output.println("-f Failover-Belege importieren");
				if (this.commandLine.hasOption('g')) this.output.println("-g in Galileo verbuchen");
				if (this.commandLine.hasOption('l')) this.output.println("-l Protokollieren");
				this.output.println("--------------------------------------------");
			}
			catch (IOException e)
			{
				String msg = "Die Protokolldatei '" + file.getName() + "' konnte nicht angelegt werden.";
				this.writeln("Fehler!");
				this.writeln("   " + msg);
				this.writeln("   " + e.getLocalizedMessage());
				System.err.println(msg);
				System.err.println(e.getLocalizedMessage());
				System.exit(-1);
			}
			System.out.println("Ok!");
		}
	}
	
	private void closeLogFile()
	{
		if (this.commandLine.hasOption('l'))
		{
			if (this.commandLine.hasOption('c'))
			{
				this.writeln("Verarbeitete Dateien: " + this.count);
			}
			System.out.print("Protokolldatei schliessen...");
			
			DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			this.output.println("---------------------------------------");
			this.output.println("Ende des Protokolls " + format.format(new Date()));
			
			this.output.flush();
			this.output.close();
			this.output = null;
			
			System.out.println("Ok!");
		}
	}
	
	private void write(String line)
	{
		// if (!commandLine.hasOption('c')) {
		if (this.commandLine.hasOption('l'))
		{
			this.output.print(line);
			if (!this.output.equals(System.out)) System.out.print(line);
		}
		// }
	}
	
	private void writeln(String line)
	{
		// if (!commandLine.hasOption('c')) {
		if (this.commandLine.hasOption('l'))
		{
			this.output.println(line);
			if (!this.output.equals(System.out)) System.out.println(line);
		}
		// }
	}
	
	public class ReceiptDateRangeFileFilter implements FileFilter
	{
		public boolean accept(File file)
		{
			if (!file.exists())
			{
				return false;
			}
			if (file.isDirectory())
			{
				return false;
			}
			if (file.getName().length() != 23 && file.getName().length() != 26 && file.getName().length() != 27)
			{
				return false;
			}
			if (!file.getName().startsWith("1"))
			{
				return false;
			}
			if (file.getName().length() == 23)
			{
				if (file.getName().endsWith(".xml"))
				{
					if (!file.getName().endsWith("std.xml") && !file.getName().endsWith("tmp.xml"))
					{
						return false;
					}
				}
				else
				{
					return false;
				}
			}
			else if (file.getName().length() == 26)
			{
				if (file.getName().endsWith(".xml.zip"))
				{
					if (!file.getName().endsWith("std.xml.zip") && !file.getName().endsWith("tmp.xml.zip"))
					{
						return false;
					}
				}
				else
				{
					return false;
				}
			}
			return true;
		}
	}
	
	public class ReceiptNumberFileFilter implements FileFilter
	{
		public boolean accept(File file)
		{
			if (!file.exists())
			{
				return false;
			}
			if (file.isDirectory())
			{
				return false;
			}
			if (file.getName().length() != 23 && file.getName().length() != 26 && file.getName().length() != 27)
			{
				return false;
			}
			if (!file.getName().startsWith("1"))
			{
				return false;
			}
			if (file.getName().length() == 23)
			{
				if (file.getName().endsWith(".xml"))
				{
					if (!file.getName().endsWith("std.xml") && !file.getName().endsWith("tmp.xml"))
					{
						return false;
					}
				}
				else
				{
					return false;
				}
			}
			else if (file.getName().length() == 26)
			{
				if (file.getName().endsWith(".xml.zip"))
				{
					if (!file.getName().endsWith("std.xml.zip") && !file.getName().endsWith("tmp.xml.zip"))
					{
						return false;
					}
				}
				else
				{
					return false;
				}
			}
			if (!file.getName().substring(0, Import.this.commandLine.getOptionValue('n').length()).equals(
							Import.this.commandLine.getOptionValue('n')))
			{
				return false;
			}
			return true;
		}
	}
	
	public class FailoverDirectoryFileFilter implements FileFilter
	{
		public boolean accept(File file)
		{
			if (!file.exists())
			{
				return false;
			}
			if (!file.isDirectory())
			{
				return false;
			}
			if (!file.getName().equals("failover"))
			{
				return false;
			}
			return true;
		}
	}
	
	public class DateDirectoryFileFilter implements FileFilter
	{
		private Calendar from = GregorianCalendar.getInstance();
		private Calendar to = GregorianCalendar.getInstance();
		private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		public DateDirectoryFileFilter(Date fromDate, Date toDate)
		{
			this.from.setTime(fromDate);
			this.from.set(Calendar.HOUR_OF_DAY, 0);
			this.from.set(Calendar.MINUTE, 0);
			this.from.set(Calendar.SECOND, 0);
			
			this.to.setTime(toDate);
			this.to.set(Calendar.HOUR_OF_DAY, 0);
			this.to.set(Calendar.MINUTE, 0);
			this.to.set(Calendar.SECOND, 0);
		}
		
		public boolean accept(File file)
		{
			Date startDate = null;
			Date endDate = null;
			
			if (!file.exists())
			{
				return false;
			}
			if (!file.isDirectory())
			{
				return false;
			}
			if (file.getName().length() != 10)
			{
				return false;
			}
			try
			{
				Calendar startCalendar = GregorianCalendar.getInstance();
				startCalendar.setTime(this.dateFormat.parse(file.getName()));
				startCalendar.add(Calendar.SECOND, 1);
				startDate = startCalendar.getTime();
				
				endDate = this.dateFormat.parse(file.getName());
			}
			catch (ParseException e)
			{
				return false;
			}
			if (this.from != null && startDate.before(this.from.getTime()))
			{
				return false;
			}
			if (this.to != null && endDate.after(this.to.getTime()))
			{
				return false;
			}
			return true;
		}
	}
}
