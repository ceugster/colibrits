/**
 * Main.java
 * 
 * Programm zum extrahieren von Positionen aus der ColibriTS-Datenbank für das
 * Nachführen von Buchungen in Galileo.
 * 
 * Version 0.8
 * **********************
 * 
 * 
 * Version 0.6 06.05.2007
 * ----------------------
 * Datenbankabfrage ergänzt
 * 
 * 
 * Version 0.7 08.05.2007
 * ----------------------
 * Minuswerte in Preis und Menge auf Pluswerte konvertieren
 * 
 * 
 * Version 0.8 21.05.2007
 * ----------------------
 * Rabatt als Betrag ausgeben
 * 
 * 
 * Version 0.9 22.05.2007
 * ----------------------
 * Zusammenfassung eingebaut
 *
 *
 * Version 0.92 04.06.2007
 * ----------------------
 * Zusammenfassung geordnet
 * Resultat mit ColibriTS abgeglichen
 *
 *
 * Version 0.93 14.06.2007
 * ----------------------
 * Neues Argument a, all eingebaut. Das Argument entscheidet, ob
 * alle Datensätze, unabhängig, ob sie bereits in Galileo verbucht
 * sind, exportiert werden sollen oder nicht.
 * 
 */
package ch.eugster.pos.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.mysql.jdbc.Driver;

public class WGTrans
{
	
	private static String version = "0.93";
	private static CommandLine commandLine = null;
	private static NumberFormat nf = null;
	
	private static Hashtable pg = null;
	private static Hashtable recap = null;
	
	// private static FileWriter writer = null;
	
	private static int posCount = 0;
	
	public WGTrans()
	{
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Options options = WGTrans.createOptions();
		WGTrans.readCommandLine(options, args);
		WGTrans.verifyCommandLine();
		WGTrans.doWork(options);
	}
	
	private static void doWork(Options options)
	{
		if (WGTrans.commandLine.hasOption("h"))
		{
			HelpFormatter hf = new HelpFormatter();
			hf.printHelp("wgtrans [-h|[-e [[-b] [-s] [-d] [-u] [-p] [-l] [-t] [-m]]]", options);
		}
		else if (WGTrans.commandLine.hasOption("v"))
		{
			System.out.println("wgtrans Version " + WGTrans.version + " (Copyright by Christian Eugster)");
		}
		else if (!WGTrans.commandLine.hasOption("e"))
		{
			HelpFormatter hf = new HelpFormatter();
			hf.printHelp("wgtrans [-h|[-e [[-b] [-s] [-d] [-u] [-p] [-l] [-t] [-m]]", options);
		}
		else
		{
			WGTrans.moveData();
		}
	}
	
	private static Options createOptions()
	{
		Options options = new Options();
		options.addOption(WGTrans.createOption("b", "begin", true,
						"Der Beginn des Datumbereichs, der verarbeitet werden soll.\nFormat: tt.mm.yyyy", false,
						GregorianCalendar.getInstance()));
		options.addOption(WGTrans.createOption("e", "end", true,
						"Das Ende des Datumbereichs, der verarbeitet werden soll.\nFormat: tt.mm.yyyy", false,
						GregorianCalendar.getInstance()));
		options.addOption(WGTrans.createOption("s", "server", true,
						"Der Datenbankserver, von dem die Daten abgerufen werden sollen.\nVorschlagswert: localhost",
						false, null));
		options.addOption(WGTrans.createOption("d", "database", true,
						"Die Datenbank, aus der die Daten abgerufen werden sollen.\nVorschlagswert: colibri", false,
						null));
		options.addOption(WGTrans.createOption("h", "help", false, "Hilfe anzeigen.", false, null));
		options
						.addOption(WGTrans
										.createOption(
														"u",
														"username",
														true,
														"Der Benutzername, der für die Verbindung zur Datenbank.\nbenötigt wird. Vorschlagswert: colibri",
														false, null));
		options.addOption(WGTrans.createOption("p", "password", true,
						"Das Passwort, das für die Verbindung zur Datenbank.\nbenötigt wird. Vorschlagswert: colibri",
						false, null));
		options
						.addOption(WGTrans
										.createOption(
														"l",
														"delimiter",
														true,
														"Das Zeichen, mit dem die Felder voneinander abgegrenzt \nwerden sollen Möglich sind alle druckbaren Zeichen und \\t für Tabulator. Vorschlagswert: |",
														false, null));
		options.addOption(WGTrans.createOption("t", "test", false,
						"Die generierten Zeilen werden auf dem Bildschirm ausgegeben.", false, null));
		options.addOption(WGTrans.createOption("v", "version", false,
						"Gibt die Version des Programms auf den Bildschirm aus.", false, null));
		options
						.addOption(WGTrans
										.createOption(
														"r",
														"recap",
														false,
														"Gibt eine Zusammenfassung des Exports aus. Die Protokolldatei hat den Namen \"wgtrans.log\" und wird im aktuellen Verzeichnis abgelegt.",
														false, null));
		options.addOption(WGTrans.createOption("a", "all", false,
						"Es werden alle, auch bereits in Galileo verbuchte Titel, exportiert.", false, null));
		return options;
	}
	
	private static Option createOption(String shortName, String longName, boolean argumentRequired, String description,
					boolean isRequired, Object type)
	{
		Option option = new Option(shortName, longName, argumentRequired, description);
		option.setRequired(isRequired);
		return option;
	}
	
	private static void readCommandLine(Options options, String[] arguments)
	{
		CommandLineParser clp = new GnuParser();
		try
		{
			WGTrans.commandLine = clp.parse(options, arguments);
		}
		catch (org.apache.commons.cli.ParseException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	private static void verifyCommandLine()
	{
		if (WGTrans.commandLine.getOptionValue("e") != null)
		{
			WGTrans.getDate("e");
		}
		if (WGTrans.commandLine.getOptionValue("b") != null)
		{
			WGTrans.getDate("b");
		}
	}
	
	private static Date getDate(String option)
	{
		Date date = null;
		try
		{
			DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
			date = df.parse(WGTrans.commandLine.getOptionValue(option));
		}
		catch (java.text.ParseException e)
		{
			System.err.println("Das Enddatum muss das Format \"tt.mm.jjjj\" haben.");
			System.exit(-1);
		}
		return date;
	}
	
	private static void moveData()
	{
		Connection con = WGTrans.obtainDatabaseConnection();
		ResultSet rst = WGTrans.getDatabaseData(con);
		if (rst != null)
		{
			if (WGTrans.commandLine.hasOption('t'))
			{
				WGTrans.writeToScreen(rst);
			}
			else
			{
				FileWriter writer = WGTrans.openOutputFile("wgtrans.txt");
				WGTrans.writeToOutputFile(rst, writer);
				WGTrans.closeOutputFile(writer);
			}
			WGTrans.closeResultSet(rst);
			
			WGTrans.printRecapitulation();
		}
		
	}
	
	private static Connection obtainDatabaseConnection()
	{
		System.out.println("Verbindung zur Datenbank aufbauen...");
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch (ClassNotFoundException e)
		{
			System.err
							.println("Der Datenbanktreiber \"com.mysql.jdbc.Driver\" konnte nicht gefunden werden. Stellen Sie sicher, dass er sich im Klassenpfad der Anwendung befindet.");
			System.exit(-1);
		}
		
		Connection connection = null;
		try
		{
			DriverManager.registerDriver(new Driver());
			String host = WGTrans.commandLine.getOptionValue("s", "localhost");
			String database = WGTrans.commandLine.getOptionValue("d", "colibri");
			String username = WGTrans.commandLine.getOptionValue("u", "colibri");
			String password = WGTrans.commandLine.getOptionValue("p", "colibri");
			System.out.println("  Server: " + host);
			System.out.println("  Datenbank: " + database);
			System.out.println("  Benutzername: " + username);
			System.out.println("  Passwort: " + password);
			connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database + "?user=" + username
							+ "&password=" + password);
		}
		catch (Exception e)
		{
			System.err
							.println("Es konnte keine Verbindung zur Datenbank hergestellt werden. Stellen Sie sicher, dass die Verbindungsdaten stimmen.");
			System.exit(-1);
		}
		System.out.println("Verbindung hergestellt!");
		return connection;
	}
	
	private static ResultSet getDatabaseData(Connection connection)
	{
		System.out.print("Datenbankabfrage generieren und Datensätze empfangen...");
		String startDate = null;
		String endDate = null;
		ResultSet rst = null;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		if (WGTrans.commandLine.getOptionValue("b") != null)
		{
			startDate = df.format(WGTrans.getDate("b"));
		}
		endDate = df.format(WGTrans.getDate("e"));
		
		StringBuffer sb = new StringBuffer("SELECT ");
		sb = sb.append("p.price AS Preis, t.galileo_id AS Mwst, ");
		sb = sb.append("pg.id AS Wgruppe, pg.name AS WgBez, p.quantity AS Menge, ");
		sb = sb.append("p.discount AS Rabatt, r.timestamp AS Datum, ");
		sb = sb.append("r.number AS Couponnr, r.customer_id AS Kundennr ");
		sb = sb.append("FROM pos_receipt r, pos_position p, pos_product_group pg, ");
		sb = sb.append("pos_tax t, pos_current_tax ct ");
		sb = sb.append("WHERE p.receipt_id = r.id AND p.product_group_id = pg.id AND ");
		sb = sb.append("p.current_tax_id = ct.id AND ct.tax_id = t.id AND ");
		if (startDate != null)
		{
			sb = sb.append("r.timestamp >= '" + startDate + "' AND ");
		}
		sb = sb.append("r.timestamp <= '" + endDate + "' AND ");
		sb = sb.append("r.status = 4 AND ");
		if (WGTrans.commandLine.hasOption("a"))
		{
			sb = sb.append("pg.type = 0;");
		}
		else
		{
			sb = sb.append("p.galileo_booked = 0 AND pg.type = 0;");
		}
		// sb = sb.append("p.galileo_booked = 0 AND pg.type = 0 AND ");
		// sb = sb.append("(p.product_id = '' OR ");
		// sb = sb.append("p.product_id = 0 OR (p.product_id IS NULL))");
		
		try
		{
			PreparedStatement ps = connection.prepareStatement(sb.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_UPDATABLE);
			rst = ps.executeQuery();
		}
		catch (Exception e)
		{
			System.err.println("\n" + sb.toString());
			e.printStackTrace();
			System.exit(-1);
		}
		
		System.out.println("Ok!");
		return rst;
	}
	
	private static FileWriter openOutputFile(String fileName)
	{
		System.out.print("Datendatei öffnen...");
		String filePath = new String(fileName);
		File file = new File(filePath);
		FileWriter writer = null;
		
		if (file.exists())
		{
			file.delete();
		}
		try
		{
			writer = new FileWriter(file);
		}
		catch (IOException e)
		{
			System.err.println("Die Datei \"" + fileName + "\" konnte nicht erstellt werden.");
			e.printStackTrace();
			System.exit(-1);
		}
		System.out.println("Ok!");
		return writer;
	}
	
	private static void closeOutputFile(Writer writer)
	{
		System.out.print("Datendatei schliessen...");
		try
		{
			writer.flush();
			writer.close();
			writer = null;
		}
		catch (IOException e)
		{
			System.err.println("Die Ausgabedatei konnte nicht geschlossen werden.");
			e.printStackTrace();
			System.exit(-1);
		}
		System.out.println("Ok!");
	}
	
	private static void recapitulate(ResultSet rst)
	{
		
		WGTrans.posCount++;
		
		try
		{
			double price = rst.getDouble("Preis");
			int quantity = rst.getInt("Menge");
			double discount = Math.abs(rst.getDouble("Rabatt"));
			double amountBrut = new Double(quantity).doubleValue() * price;
			double amountNet = amountBrut * (1 - discount);
			
			Integer vPositionen = null;
			Integer sPositionen = null;
			Integer vMenge = null;
			Integer sMenge = null;
			Double vBetrag = null;
			Double sBetrag = null;
			
			String wg = (String) WGTrans.pg.get(new Long(rst.getString("Wgruppe")));
			if (wg == null)
			{
				wg = rst.getString("Wgruppe");
				if (quantity < 0 || price < 0d)
				{
					vPositionen = new Integer(0);
					sPositionen = new Integer(1);
					vMenge = new Integer(0);
					sMenge = new Integer(Math.abs(quantity));
					vMenge = new Integer(0);
					sBetrag = new Double(Math.abs(amountNet));
					vBetrag = new Double(0d);
				}
				else
				{
					vPositionen = new Integer(1);
					sPositionen = new Integer(0);
					vMenge = new Integer(Math.abs(quantity));
					sMenge = new Integer(0);
					vBetrag = new Double(Math.abs(amountNet));
					sBetrag = new Double(0d);
				}
			}
			else
			{
				sPositionen = (Integer) WGTrans.recap.get(wg + "SP");
				vPositionen = (Integer) WGTrans.recap.get(wg + "VP");
				sMenge = (Integer) WGTrans.recap.get(wg + "SM");
				vMenge = (Integer) WGTrans.recap.get(wg + "VM");
				sBetrag = (Double) WGTrans.recap.get(wg + "SB");
				vBetrag = (Double) WGTrans.recap.get(wg + "VB");
				
				if (quantity < 0 || price < 0d)
				{
					sPositionen = new Integer(sPositionen.intValue() + 1);
					sMenge = new Integer(sMenge.intValue() + Math.abs(quantity));
					sBetrag = new Double(sBetrag.doubleValue() + Math.abs(amountNet));
				}
				else
				{
					vPositionen = new Integer(vPositionen.intValue() + 1);
					vMenge = new Integer(vMenge.intValue() + Math.abs(quantity));
					vBetrag = new Double(vBetrag.doubleValue() + Math.abs(amountNet));
				}
			}
			
			WGTrans.pg.put(new Long(wg), wg);
			WGTrans.recap.put(wg + "SP", sPositionen);
			WGTrans.recap.put(wg + "VP", vPositionen);
			WGTrans.recap.put(wg + "SM", sMenge);
			WGTrans.recap.put(wg + "VM", vMenge);
			WGTrans.recap.put(wg + "SB", sBetrag);
			WGTrans.recap.put(wg + "VB", vBetrag);
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private static void printRecapitulation()
	{
		boolean printToFile = false;
		FileWriter writer = null;
		
		if (WGTrans.commandLine.hasOption("r"))
		{
			writer = WGTrans.openOutputFile("wgtrans.log");
			printToFile = WGTrans.writeRecapTitleLine(writer);
		}
		
		System.out.println("");
		System.out.println("Zusammenfassung:");
		System.out.println("Eingelesene Positionen: " + WGTrans.posCount);
		System.out.println("");
		
		Long[] keys = (Long[]) WGTrans.pg.keySet().toArray(new Long[0]);
		Arrays.sort(keys);
		for (int i = 0; i < keys.length; i++)
		{
			String key = (String) WGTrans.pg.get(keys[i]);
			
			StringBuffer line = new StringBuffer();
			String wg = key;
			Integer vp = (Integer) WGTrans.recap.get(key + "VP");
			String svp = vp.toString();
			Integer vm = (Integer) WGTrans.recap.get(key + "VM");
			String svm = vm.toString();
			Double vb = (Double) WGTrans.recap.get(key + "VB");
			String svb = WGTrans.nf.format(vb.doubleValue());
			Integer sp = (Integer) WGTrans.recap.get(key + "SP");
			String ssp = sp.toString();
			Integer sm = (Integer) WGTrans.recap.get(key + "SM");
			String ssm = sm.toString();
			Double sb = (Double) WGTrans.recap.get(key + "SB");
			String ssb = WGTrans.nf.format(sb.doubleValue());
			
			line = line.append(wg + "\t");
			line = line.append(svp + "\t");
			line = line.append(svm + "\t");
			line = line.append(svb + "\t");
			line = line.append(ssp + "\t");
			line = line.append(ssm + "\t");
			line = line.append(ssb + "\t");
			line = line.append("\n");
			
			String printLine = line.toString();
			System.out.print(printLine);
			
			if (printToFile)
			{
				printToFile = WGTrans.writeRecapLine(writer, printLine);
			}
		}
		
		if (printToFile)
		{
			WGTrans.closeRecapFile(writer);
		}
		
		System.out.println("Ok!");
	}
	
	private static boolean writeRecapTitleLine(Writer writer)
	{
		try
		{
			Calendar cld = GregorianCalendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
			writer.write("Export-Protokoll WGTrans vom " + sdf.format(cld.getTime()) + "\n");
			writer.write("Zusammenfassung:\n");
			writer.write("Eingelesene Positionen: " + WGTrans.posCount + "\n");
			writer.write("\n");
			
			StringBuffer line = new StringBuffer();
			line = line.append("WG\t");
			line = line.append("V-Positionen\t");
			line = line.append("V-Exemplare\t");
			line = line.append("VP Gesamt\t");
			line = line.append("S-Positionen\t");
			line = line.append("S-Exemplare\t");
			line = line.append("VP Gesamt");
			line = line.append("\n");
			
			writer.write(line.toString());
			
		}
		catch (IOException e)
		{
			return false;
		}
		return true;
	}
	
	private static boolean writeRecapLine(Writer writer, String line)
	{
		try
		{
			writer.write(line);
		}
		catch (IOException e)
		{
			System.err.println("Die Daten konnten nicht geschrieben werden.");
			return false;
		}
		return true;
	}
	
	private static void closeRecapFile(Writer writer)
	{
		try
		{
			writer.flush();
			writer.close();
			writer = null;
		}
		catch (IOException e)
		{
			System.err.println("Die Zusammenfassung konnte nicht gespeichert werden.");
		}
	}
	
	private static void writeToScreen(ResultSet rst)
	{
		String delim = WGTrans.commandLine.getOptionValue("l", "|");
		System.out.println("");
		try
		{
			System.out.println("");
			String title = WGTrans.buildTitleLine(delim);
			System.out.println(title);
			
			rst.beforeFirst();
			while (rst.next())
			{
				System.out.println(WGTrans.getLine(rst, delim));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		System.out.println("");
	}
	
	private static void writeToOutputFile(ResultSet rst, Writer writer)
	{
		String delim = WGTrans.commandLine.getOptionValue("l", "|");
		System.out.println("Daten in Datendatei schreiben (verwendeter Felddelimiter: " + delim + ")...");
		try
		{
			WGTrans.pg = new Hashtable();
			WGTrans.recap = new Hashtable();
			
			System.out.println("  Titelzeile wird geschrieben...");
			String title = WGTrans.buildTitleLine(delim);
			WGTrans.writeLine(writer, title);
			
			System.out.print("  Daten werden geschrieben ");
			rst.beforeFirst();
			while (rst.next())
			{
				System.out.print(".");
				WGTrans.writeLine(writer, WGTrans.getLine(rst, delim));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		System.out.println("Ok!");
	}
	
	private static void closeResultSet(ResultSet rst)
	{
		System.out.print("Datenbankverbindung schliessen...");
		try
		{
			rst.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		System.out.print("Ok!");
	}
	
	private static String getLine(ResultSet rst, String delim)
	{
		return WGTrans.buildLine(rst, delim);
	}
	
	private static String buildTitleLine(String delim)
	{
		StringBuffer line = new StringBuffer();
		line = line.append("Preis");
		line = WGTrans.appendDelimiter(line, delim);
		line = line.append("Mwst");
		line = WGTrans.appendDelimiter(line, delim);
		line = line.append("Wgruppe");
		line = WGTrans.appendDelimiter(line, delim);
		line = line.append("WgBez");
		line = WGTrans.appendDelimiter(line, delim);
		line = line.append("Menge");
		line = WGTrans.appendDelimiter(line, delim);
		line = line.append("Rabatt");
		line = WGTrans.appendDelimiter(line, delim);
		line = line.append("Datum");
		line = WGTrans.appendDelimiter(line, delim);
		line = line.append("Couponnr");
		line = WGTrans.appendDelimiter(line, delim);
		line = line.append("Kundennr");
		line = WGTrans.appendDelimiter(line, delim);
		line = line.append("Typ");
		line = line.append("\r\n");
		return line.toString();
	}
	
	private static String buildLine(ResultSet rst, String delim)
	{
		if (WGTrans.nf == null)
		{
			WGTrans.nf = NumberFormat.getInstance();
			WGTrans.nf.setGroupingUsed(false);
			WGTrans.nf.setMaximumFractionDigits(2);
			WGTrans.nf.setMinimumFractionDigits(2);
		}
		
		StringBuffer line = new StringBuffer();
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		try
		{
			double price = Math.abs(rst.getDouble("Preis"));
			double quantity = Math.abs(rst.getInt("Menge"));
			double discount = Math.abs(rst.getDouble("Rabatt"));
			double amountBrut = quantity * price;
			double amountNet = amountBrut * (1 - discount);
			
			line = line.append(WGTrans.nf.format(price));
			line = WGTrans.appendDelimiter(line, delim);
			line = line.append(rst.getString("Mwst"));
			line = WGTrans.appendDelimiter(line, delim);
			line = line.append(rst.getString("Wgruppe"));
			line = WGTrans.appendDelimiter(line, delim);
			line = line.append(rst.getString("WgBez"));
			line = WGTrans.appendDelimiter(line, delim);
			line = line.append(WGTrans.nf.format(quantity));
			line = WGTrans.appendDelimiter(line, delim);
			line = line.append(WGTrans.nf.format(amountBrut - amountNet));
			line = WGTrans.appendDelimiter(line, delim);
			line = line.append(df.format(rst.getDate("Datum")));
			line = WGTrans.appendDelimiter(line, delim);
			line = line.append(rst.getString("Couponnr"));
			line = WGTrans.appendDelimiter(line, delim);
			String knr = rst.getString("Kundennr");
			if (knr.length() == 0) knr = "0";
			line = line.append(knr);
			line = WGTrans.appendDelimiter(line, delim);
			if (rst.getInt("Menge") < 0 || rst.getDouble("Preis") < 0d)
			{
				line = line.append("S");
			}
			else
			{
				line = line.append("V");
			}
			line = line.append("\r\n");
			
			WGTrans.recapitulate(rst);
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		return line.toString();
	}
	
	private static StringBuffer appendDelimiter(StringBuffer line, String delim)
	{
		if (delim.equals("\\t"))
		{
			line.append("\t");
		}
		else
		{
			line.append(delim);
		}
		return line;
	}
	
	private static void writeLine(Writer writer, String line)
	{
		try
		{
			writer.write(line);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
}
