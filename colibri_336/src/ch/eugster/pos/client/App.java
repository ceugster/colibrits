/*
 * Created on 10.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.client;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;

import ch.eugster.pos.Messages;
import ch.eugster.pos.client.gui.Frame;
import ch.eugster.pos.client.gui.Splash;
import ch.eugster.pos.db.Connection;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.db.Standard;
import ch.eugster.pos.db.Table;
import ch.eugster.pos.db.Tax;
import ch.eugster.pos.db.User;
import ch.eugster.pos.db.Version;
import ch.eugster.pos.events.InitializationListener;
import ch.eugster.pos.events.MessageDialog;
import ch.eugster.pos.printing.BooksNotFoundListPrinter;
import ch.eugster.pos.product.Code128;
import ch.eugster.pos.product.GalileoProductGroupServer;
import ch.eugster.pos.product.ProductServer;
import ch.eugster.pos.util.Config;
import ch.eugster.pos.util.OjbRepositoryHandler;
import ch.eugster.pos.util.Path;

/**
 * @author Administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class App implements InitializationListener
{
	
	/**
	 * 
	 * @param args
	 *            Die Kommandozeilenargumente
	 */
	public App(String[] args)
	{
		super();
		App.app = this;
		
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO,
						Messages.getString("App.Laden_der_Datenbankinformationen_aus_der_Konfigurationsdatei..._2")); //$NON-NLS-1$ 
		App.fireInitialized(App.value, Messages.getString("App.Datenbankinformationen_werden_geladen..._3")); // 0 //$NON-NLS-1$
		Database.load();
		App.fireInitialized(++App.value, Messages.getString("App.Datenbankinformationen_werden_verarbeitet..._4")); // 1 //$NON-NLS-1$
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO,
						Messages.getString("App.Option_Datenbankwechsel_auf___true___setzen..._6")); //$NON-NLS-1$ 
		Database.setSwitchable(true);
		
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO,
						Messages.getString("App.Die_m_u00F6glichen_Verbindungen_in_Liste_eintragen..._8")); //$NON-NLS-1$ 
		Connection[] cons = Database.getConnections();
		App.fireInitialized(
						++App.value,
						Messages.getString("App.Datenbankverbindung_zu__9") + (cons.length > 0 ? cons[0].getName() + " " : "") + Messages.getString("App.herstellen..._12")); // 2 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		for (int i = 0; i < cons.length; i++)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO,
							Messages.getString("App.Verbindungsaufbau_zur_Datenbank__14") + cons[i].getName()); //$NON-NLS-1$ 
			cons[i].addInitializationListener(this);
			if (!cons[i].openConnection())
			{
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
								.severe(Messages.getString("App.Verbindung_zur_Datenbank__16") + cons[i].getName() + Messages.getString("App._fehlgeschlagen._17")); //$NON-NLS-1$ //$NON-NLS-2$ 
			}
			App.fireInitialized(
							++App.value,
							Messages.getString("App.Datenbankverbindung_zu__18") + (i + 1 < cons.length ? cons[i + 1].getName() + " " : "") + Messages.getString("App.herstellen..._21")); // max 3 Schritte = total 5 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			cons[i].removeInitializationListener(this);
		}
		App.value = 5;
		App.fireInitialized(App.value, Messages.getString("App.Defaultdatenbank_ermitteln_und_setzen..._22")); // 5 Schritte //$NON-NLS-1$
		
		if (App.currentConnection == -1)
		{
			String dflt = Config.getInstance().getDatabaseDefault();
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO,
							Messages.getString("App.Defaultdatenbank_ermitteln..._24")); //$NON-NLS-1$ 
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO,
							Messages.getString("App.Defaultdatenbank_ist_26") + " " + dflt + "."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
			if (dflt.equals("standard")) { //$NON-NLS-1$
				App.currentConnection = 0;
			}
			else if (dflt.equals("temporary")) { //$NON-NLS-1$
				App.currentConnection = 1;
			}
			else if (dflt.equals("tutorial")) { //$NON-NLS-1$
				App.currentConnection = 2;
			}
		}
		App.fireInitialized(++App.value, Messages.getString("App.Datenbankverbindung_pr_u00FCfen..._32")); // 6 //$NON-NLS-1$
		
		if (App.currentConnection == 0)
		{
			/*
			 * Standardverbindung (produktive Umgebung)
			 */
			int result = 0;
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
							.info(Messages.getString("App.Pr_u00FCfen,_ob_eine_Verbindung_zur_Datenbank_34") + " " + Database.getStandard().getName() + " " + Messages.getString("App.besteht..._37")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
			if (Database.getStandard().isConnected())
			{
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
								Messages.getString("App.Verbindung_zur_als_Default_gesetzten_Datenbank_besteht._39")); //$NON-NLS-1$ 
				Database.setCurrent(Database.getStandard());
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
								.info(Messages.getString("App.Defaultdatenbank_auf_41") + " " + Database.getStandard().getName() + " " + Messages.getString("App.setzen..._44")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
				Database.getTemporary().initialize(App.splash, App.value); // 6
				// +
				// 21
				// ,
				// resp
				// .
				// 22
				// Schritte
				// =
				// total
				// 28
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
								Messages.getString("App.Lokale_Ersatzdatenbank_initialisieren..._46")); //$NON-NLS-1$ 
				if (Database.getTemporary().isActive())
				{
					if (Database.getTemporary().isConnected())
					{
						result = 0;
					}
					else
					{
						result = MessageDialog
										.showSimpleDialog(
														App.splash,
														"Verbindung zur Ersatzdatenbank fehlgeschlagen",
														"Die Verbindung zur lokalen Ersatzdatenbank "
																		+ Database.getTemporary().getName()
																		+ " ist fehlgeschlagen. Wollen Sie die Kasse trotzdem starten?",
														1);
						if (result == 2)
						{
							result = 0;
						}
						else
						{
							MessageDialog.showInformation(App.splash, "Programm beenden", "Das Programm wird beendet.",
											0);
							System.exit(-1);
						}
					}
				}
			}
			else
			{
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
								.info(Messages.getString("App.Keine_Verbindung_zur_Datenbank_48") + " " + Database.getStandard().getName() + "."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
								.info(Messages.getString("App.Pr_u00FCfen,_ob_eine_Verbindung_zur_Datenbank_52") + " " + Database.getTemporary().getName() + " " + Messages.getString("App.besteht..._55")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
				if (Database.getTemporary().isConnected())
				{
					Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
									.info(Messages.getString("App.Pr_u00FCfen,_ob_Option_Datenbank_wechseln_gesetzt_ist..._57")); //$NON-NLS-1$ 
					if (Database.isSwitchable())
					{
						Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
										Messages.getString("App.Option_Datenbank_wechseln_ist_gesetzt._59")); //$NON-NLS-1$ 
						Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
										.info(Messages.getString("App.Benutzer_fragen,_ob_Programm_im_lokalen_Modus_gestartet_werden_soll..._61")); //$NON-NLS-1$ 
						result = MessageDialog
										.showSimpleDialog(null,
														Messages.getString("App.Verbindung_fehlgeschlagen_65"), //$NON-NLS-1$
														Messages.getString("App.Die_Verbindung_zur_standardm_u00C3_u00A4ssig_eingerichteten_Datenbank_62") + System.getProperty("line.separator") + //$NON-NLS-1$ //$NON-NLS-2$
																		Database.getStandard().getName()
																		+ " "
																		+ Messages.getString("App._ist_nicht_verf_u00FCgbar._nSoll_versucht_werden,_mit_der_lokalen_Ersatzdatenbank_neine_Verbindung_aufzubauen__64"), //$NON-NLS-1$
														1);
						if (result == 2)
						{
							App.currentConnection = 1;
						}
					}
				}
			}
			App.value = 28;
			App.fireInitialized(App.value,
							Messages.getString("App.Tempor_u00C3_u00A4re_Datenbank_initialisieren..._68")); // 28 //$NON-NLS-1$
			if (result == 1)
			{
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
								.info(Messages.getString("App.Der_Benutzer_hat_dem_Wechsel_nicht_zugestimmt._Das_Programm_wird_verlassen._70")); //$NON-NLS-1$ 
				MessageDialog.showInformation(null,
								Messages.getString("App.Verbindung_fehlgeschlagen_74"), //$NON-NLS-1$
								Messages.getString("App.Die_Verbindung_zur_standardm_u00C3_u00A4ssig_eingerichteten_Datenbank__71") + System.getProperty("line.separator") + //$NON-NLS-1$ //$NON-NLS-2$
												Database.getStandard().getName()
												+ Messages.getString("App._ist_nicht_verf_u00FCgbar._nDas_Programm_wird_verlassen._73"), //$NON-NLS-1$
								0);
				System.exit(-2);
			}
		}
		
		if (App.currentConnection == 1)
		{
			/*
			 * Temporäre Datenbank
			 */
			if (Database.getTemporary().isConnected())
			{
				Database.setCurrent(Database.getTemporary());
				Database.getTemporary().initialize(App.splash, App.value); // 6
				// +
				// 21
				// ,
				// resp
				// .
				// 22
				// Schritte
				// =
				// total
				// 28
			}
			else
			{
				MessageDialog.showInformation(App.splash,
								Messages.getString("App.Konfigurationsfehler_77"), //$NON-NLS-1$
								Messages.getString("App.Die_gew_u00FCnschte_Datenbankverbindung__75") + Database.getTemporary().getName() + Messages.getString("App._ist_nicht_aktiv._nDas_Programm_wird_verlassen._76"), //$NON-NLS-1$
								0);
				System.exit(-2);
			}
		}
		App.fireInitialized(++App.value, Messages.getString("App.Schulungsdatenbankverbindung_pr_u00FCfen..._78")); // 29 //$NON-NLS-1$
		
		if (App.currentConnection == 2)
		{
			/*
			 * Schulungsdatenbank
			 */
			if (Database.getTutorial().isConnected())
			{
				Database.setCurrent(Database.getTutorial());
				Database.getTemporary().initialize(App.splash, App.value); // 6
				// +
				// 21
				// ,
				// resp
				// .
				// 22
				// Schritte
				// =
				// total
				// 28
			}
			else
			{
				MessageDialog.showInformation(App.splash,
								Messages.getString("App.Konfigurationsfehler_81"), //$NON-NLS-1$
								Messages.getString("App.Die_gew_u00FCnschte_Datenbankverbindung__79") + Database.getTutorial().getName() + Messages.getString("App._ist_nicht_aktiv._nDas_Programm_wird_verlassen._80"), //$NON-NLS-1$
								0);
				System.exit(-2);
			}
		}
		if (Database.getCurrent() == null)
		{
			MessageDialog.showInformation(App.splash, Messages.getString("App.Verbindungsfehler_85"), //$NON-NLS-1$
							Messages.getString("App.Es_konnte_keine_Datenbankverbindung_hergestellt_werden._84"), //$NON-NLS-1$
							0);
			System.exit(-1);
		}
		else
		{
			Connection con = Database.getCurrent();
			if (con.isConnected())
			{
			}
			else
			{
				MessageDialog.showInformation(App.splash,
								Messages.getString("App.Verbindungsfehler_93"), //$NON-NLS-1$
								Messages.getString("App.Es_konnte_keine_Datenbankverbindung_hergestellt_werden._Das_Programm_wird_verlassen._92"), //$NON-NLS-1$
								0);
				System.exit(-1);
			}
		}
		
		if (Database.getTemporary().isActive())
		{
			if (Database.getTemporary().isConnected())
			{
				Database.setSwitchable(true);
			}
			else
			{
				// Database.getTemporary().setActive(false);
				Database.setSwitchable(false);
			}
		}
		
		if (Database.getCurrent().equals(Database.getStandard()))
		{
			Standard standard = (Standard) Database.getCurrent();
			standard.updateSalespointStocks();
		}
		
		App.fireInitialized(++App.value, Messages.getString("App.Kassendaten_einlesen..._94")); // 30 //$NON-NLS-1$
		
		Salespoint.setCurrent(Salespoint.getById(new Long(Config.getInstance().getSalespointData()
						.getAttributeValue("id")))); //$NON-NLS-1$
		if (Salespoint.getCurrent() == null)
		{
			MessageDialog.showInformation(
							App.splash,
							"Ungültige Kasse",
							"<HTML>Die im Administrator eingestellte Kassenstation ist ungültig.<BR>Wählen Sie im Administrator eine gültige Kassenstation aus.<br>Das Programm wird verlassen.</HTML>",
							0);
			System.exit(-1);
		}
		
		App.fireInitialized(++App.value,
						Messages.getString("App._u00C3_u0153berpr_u00FCfen_der_Mehrwertsteuers_u00C3_u00A4tze..._100")); // 31 //$NON-NLS-1$
		/*
		 * Benutzer initialisieren Falls kein Defaultbenutzer definiert ist,
		 * wird der Administrator (id = 1) als default gesetzt
		 */
		User.selectDefaultUser();
		
		/*
		 * Die Mehrwertsteuer initialisieren
		 */
		Tax.setCurrentTaxes();
		App.fireInitialized(++App.value, Messages.getString("App.Code128-Daten_initialisieren..._101")); // 32 //$NON-NLS-1$
		
		/*
		 * Code128 Initialisieren
		 */
		Code128.initialize();
		App.fireInitialized(++App.value, Messages.getString("App.Verbindung_zu_Galileo_initialisieren..._104")); // 33 //$NON-NLS-1$
		
		//		Element galserve = Config.getInstance().getProductServer(); //$NON-NLS-1$
		boolean use = Config.getInstance().getProductServerUse();
		if (use)
		{
			
			App.productServer = ProductServer.getInstance();
			if (App.productServer == null)
			{
				MessageDialog.showInformation(
								App.splash,
								Messages.getString("App.Verbindung_zu_Galileo_120"), //$NON-NLS-1$
								"Das Galileo-Objekt konnte nicht ordnungsgemäss instantiiert werden. Das Programm wird beendet.",
								0);
				System.exit(-1);
			}
			
			App.fireInitialized(++App.value, Messages.getString("App.Verbindung_zu_Galileo_aktivieren..._112")); // 34 //$NON-NLS-1$
			if (!App.productServer.isActive())
			{
				String msg = "<html><body>" + Messages.getString("App.text_117"); //$NON-NLS-1$
				boolean shutdown = false;
				if (App.productServer.getUpdate() > 0)
				{
					if (!Table.switchDatabase())
					{
						shutdown = true;
						msg = msg.concat("<br>" + Messages.getString("App._nDas_Programm_wird_verlassen._119") + "</body></html>"); //$NON-NLS-1$
					}
				}
				else
				{
					msg = msg.concat("<br>" + Messages.getString("App.text_118") + "</body></html>"); //$NON-NLS-1$
				}
				MessageDialog.showInformation(App.splash, Messages.getString("App.Verbindung_zu_Galileo_120"), //$NON-NLS-1$
								msg, 0);
				
				if (shutdown)
				{
					this.dispose();
				}
			}
		}
		if (ProductServer.isUsed())
		{
			GalileoProductGroupServer.updateProductGroups();
			App.fireInitialized(++App.value,
							Messages.getString("App.Automatische_Aktualisierung_der_Warengruppen_aus_Galileo..._121")); // 36 //$NON-NLS-1$
		}
		
		App.fireInitialized(++App.value,
						Messages.getString("App.Lokale_Datenbank_auf_vorhandene_Belege_pr_u00FCfen..._124")); // 35 //$NON-NLS-1$
		// DataEqualizer.equalize(); Das sollte nicht hier, sondern im
		// Administrator gemacht werden!!!
		String[] booksNotFound = Database.transferTemporaryData();
		
		App.value = 36;
		App.fireInitialized(App.value, Messages.getString("App.Benutzerschnittstelle_initialisieren..._125")); // 36 //$NON-NLS-1$
		
		try
		{
			UIManager.setLookAndFeel(Config.getInstance().getLookAndFeelClass());
			MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
		}
		catch (ClassNotFoundException e)
		{
		}
		catch (IllegalAccessException e)
		{
		}
		catch (InstantiationException e)
		{
		}
		catch (UnsupportedLookAndFeelException e)
		{
		}
		
		App.frame = new Frame();
		App.frame.setTitle(this.getTitle());
		App.frame.setDefaultCloseOperation(Frame.EXIT_ON_CLOSE);
		App.frame.pack();
		
		if (ProductServer.isUsed() && ProductServer.getInstance().isActive())
		{
			ProductServer.setMessageListener(App.frame);
		}
		Database.addMessageListener(App.frame);
		
		App.frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		App.frame.setVisible(true);
		
		App.fireInitialized(++App.value, "Nicht verbuchte Galileotitel ausdrucken..."); // 34 //$NON-NLS-1$
		new BooksNotFoundListPrinter(App.frame.getTabPanel(), booksNotFound);
	}
	
	public String getTitle()
	{
		return Salespoint.getCurrent().name + " [" + Database.getCurrent().getConnectionString() + "] Version "
						+ Version.version();
	}
	
	public static void main(String[] args)
	{
		App.initLogging();
		
		ApplicationInstanceManager.setApplicationInstanceListener(new ApplicationInstanceListener()
		{
			public void newInstanceCreated()
			{
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning("Neue Instanz entdeckt...");
			}
		});
		if (!ApplicationInstanceManager.registerInstance())
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning(
							"Es wurde bereits eine andere Instanz des Programms gestartet.");
			MessageDialog.showInformation(
							null,
							Messages.getString("App.Meldung_152"), //$NON-NLS-1$
							Messages.getString("App.Eine_andere_Instanz_von_Colibri_wurde_bereits_gestartet._nDas_Programm_wird_beendet._151"),
							0);
			System.exit(15);
		}
		if (App.isAlreadyRunning(Path.getInstance().FILE_COLIBRI_LOCK))
		{
		}
		if (App.isAlreadyRunning(Path.getInstance().FILE_ADMIN_LOCK))
		{
			MessageDialog.showInformation(
							null,
							Messages.getString("App.Meldung_156"), //$NON-NLS-1$
							Messages.getString("App.W_u00C3_u00A4hrend_das_ColibriTS_Administrationsprogramm_l_u00C3_u00A4uft,_ndarf_das_Kassenprogramm_nicht_benutzt_werden._nDas_Programm_wird_beendet._155"),
							0);
			System.exit(15);
		}
		
		App.arguments = args;
		App.parseArgs(App.arguments);
		
		Version.setRunningProgram(Version.COLIBRI);
		
		Config.getInstance();
		
		// net.java.plaf.LookAndFeelPatchManager.initialize();
		
		System.setProperty(
						Messages.getString("App.OJB.properties"), Path.getInstance().ojbDir.concat(Path.getInstance().FILE_OJBCFG)); //$NON-NLS-1$
		
		if (!App.isCompatible())
		{
			MessageDialog.showInformation(
							null,
							Messages.getString("App.Meldung_160"), //$NON-NLS-1$
							Messages.getString("App.Das_Programm_ben_u00C3_u00B6tigt_mindestens_die_Laufzeitumgebung_JRE_1.4.x._159"),
							0);
			System.exit(14);
		}
		
		if (!Path.getInstance().testPaths())
		{
			System.exit(16);
		}
		
		Locale.setDefault(new Locale("de", "CH")); //$NON-NLS-1$ //$NON-NLS-2$
		
		App.splash = new Splash(37 + Version.getMyDataVersion());
		App.splash.setUndecorated(true);
		App.splash.pack();
		App.splash.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - App.splash.getWidth()) / 2,
						(Toolkit.getDefaultToolkit().getScreenSize().height - App.splash.getHeight()) / 2);
		App.splash.setVisible(true);
		
		OjbRepositoryHandler.updateRepositoryUser();
		
		Table.addDatabaseErrorListener(App.splash);
		App.addInitializationListener(App.splash);
		
		App.app = new App(args);
		
		App.splash.setVisible(false);
		App.removeInitializationListener(App.splash);
		Table.removeDatabaseErrorListener(App.splash);
		App.splash.dispose();
	}
	
	private static void parseArgs(String[] args)
	{
		if (args.length > 0)
		{
			for (String arg : args)
			{
				if (arg.startsWith("-connection=", 0)) { //$NON-NLS-1$
					StringTokenizer st = new StringTokenizer(arg);
					while (st.hasMoreTokens())
					{
						st.nextToken();
						String s = st.nextToken();
						if (s.equals("standard")) { //$NON-NLS-1$
							App.currentConnection = App.STANDARD_CONNECTION;
						}
						else if (s.equals("temporary")) { //$NON-NLS-1$
							App.currentConnection = App.TEMPRARY_CONNECTION;
						}
						else if (s.equals("tutorial")) { //$NON-NLS-1$
							App.currentConnection = App.TUTORIAL_CONNECTION;
						}
						else
						{
							App.currentConnection = App.STANDARD_CONNECTION;
						}
					}
				}
			}
		}
	}
	
	private static void initLogging()
	{
		File logDir = new File(Path.getInstance().logDir);
		if (logDir.exists() && logDir.isDirectory())
		{
			// FileFilter filter = new FileFilter()
			// {
			// public boolean accept(File pathname)
			// {
			//					if (pathname.getName().startsWith("colibri")) { //$NON-NLS-1$
			// return false;
			// }
			// return false;
			// }
			// };
			// File[] files = logDir.listFiles(filter);
			// for (int i = 0; i < files.length; i++)
			// {
			// files[i].delete();
			// }
		}
		
		try
		{
			FileHandler fh = new FileHandler(Path.getInstance().logDir.concat(Path.getInstance().FILE_LOG), true);
			fh.setFormatter(new SimpleFormatter());
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).addHandler(fh);
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.parse(Config.getInstance().getLoggingLevel()));
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
							.info("Logging-Level: " + Config.getInstance().getLoggingLevel());
		}
		catch (IOException e)
		{
		}
		catch (SecurityException e)
		{
		}
	}
	
	private static boolean isAlreadyRunning(String lockFileName)
	{
		// Calculate the location of the lock file
		File lockFile = new File(Path.getInstance().lockDir.concat(lockFileName));
		
		// if the lock file already exists, try to delete,
		// assume failure means another eclipse has it open
		if (lockFile.exists()) lockFile.delete();
		return lockFile.exists();
	}
	
	private static boolean isCompatible()
	{
		try
		{
			String jreVersion = System.getProperty("java.version"); //$NON-NLS-1$
			App.Identifier minimum = new App.Identifier(1, 3, 0);
			App.Identifier version = new App.Identifier(jreVersion);
			return version.isGreaterEqualTo(minimum);
		}
		catch (SecurityException e)
		{
			// If the security manager won't allow us to get the system
			// property, continue for
			// now and let things fail later on their own if necessary.
			return true;
		}
		catch (NumberFormatException e)
		{
			// If the version string was in a format that we don't understand,
			// continue and
			// let things fail later on their own if necessary.
			return true;
		}
	}
	
	private static class Identifier
	{
		private static final String DELIM = ". "; //$NON-NLS-1$
		private int major, minor, service;
		
		private Identifier(int major, int minor, int service)
		{
			super();
			this.major = major;
			this.minor = minor;
			this.service = service;
		}
		
		public Identifier(String versionString)
		{
			super();
			StringTokenizer tokenizer = new StringTokenizer(versionString, Identifier.DELIM);
			
			// major
			if (tokenizer.hasMoreTokens()) this.major = Integer.parseInt(tokenizer.nextToken());
			
			// minor
			if (tokenizer.hasMoreTokens()) this.minor = Integer.parseInt(tokenizer.nextToken());
			
			// service
			if (tokenizer.hasMoreTokens()) this.service = Integer.parseInt(tokenizer.nextToken());
		}
		
		/**
		 * Returns true if this id is considered to be greater than or equal to
		 * the given baseline. e.g. 1.2.9 >= 1.3.1 -> false 1.3.0 >= 1.3.1 ->
		 * false 1.3.1 >= 1.3.1 -> true 1.3.2 >= 1.3.1 -> true 2.0.0 >= 1.3.1 ->
		 * true
		 */
		public boolean isGreaterEqualTo(Identifier minimum)
		{
			if (this.major < minimum.major) return false;
			if (this.major > minimum.major) return true;
			// major numbers are equivalent so check minor
			if (this.minor < minimum.minor) return false;
			if (this.minor > minimum.minor) return true;
			// minor numbers are equivalent so check service
			return this.service >= minimum.service;
		}
	}
	
	public void dispose()
	{
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
						Messages.getString("App.Standarddatenbankverbindung_wird_geschlossen..._182")); //$NON-NLS-1$ 
		if (Database.getStandard().isConnected())
		{
			if (Database.getStandard().getBroker().isInTransaction())
			{
				Database.getStandard().getBroker().abortTransaction();
			}
			Database.getStandard().getBroker().close();
		}
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
						Messages.getString("App.Temporaere_Datenbankverbindung_wird_geschlossen..._184")); //$NON-NLS-1$ 
		if (Database.getTemporary().isConnected())
		{
			if (Database.getTemporary().getBroker().isInTransaction())
			{
				Database.getTemporary().getBroker().abortTransaction();
			}
			Database.getTemporary().getBroker().close();
		}
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
						Messages.getString("App.Schulungsdatenbankverbindung_wird_geschlossen..._186")); //$NON-NLS-1$ 
		if (Database.getTutorial().isConnected())
		{
			if (Database.getTutorial().getBroker().isInTransaction())
			{
				Database.getTutorial().getBroker().abortTransaction();
			}
			Database.getTutorial().getBroker().close();
		}
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
						Messages.getString("App.GalileoComServer_wird_geschlossen..._188")); //$NON-NLS-1$ 
		// if (ProductServer.isUsed() &&
		// ProductServer.getInstance().isActive())
		// {
		if (ProductServer.isUsed() && ProductServer.isInitialized())
		{
			// 10125
			ProductServer.getInstance().disconnect();
		}
		if (App.frame != null)
		{
			App.frame.dispose();
		}
		System.exit(0);
	}
	
	public static App getApp()
	{
		return App.app;
	}
	
	public static ProductServer getProductServer()
	{
		return App.productServer;
	}
	
	public Frame getFrame()
	{
		return App.frame;
	}
	
	private static boolean addInitializationListener(InitializationListener l)
	{
		return App.initializationListeners.add(l);
	}
	
	private static boolean removeInitializationListener(InitializationListener l)
	{
		return App.initializationListeners.remove(l);
	}
	
	public static void fireInitialized(int value, String text)
	{
		InitializationListener[] l = (InitializationListener[]) App.initializationListeners
						.toArray(new InitializationListener[0]);
		for (InitializationListener element : l)
		{
			element.initialized(value, text);
		}
	}
	
	public void initialized(String text)
	{
		App.fireInitialized(++App.value, text);
	}
	
	public void initialized(int value, String text)
	{
	}
	
	public static final int STANDARD_CONNECTION = 0;
	public static final int TEMPRARY_CONNECTION = 1;
	public static final int TUTORIAL_CONNECTION = 2;
	
	private static Splash splash;
	private static App app;
	private static ProductServer productServer;
	private static Frame frame;
	private static String[] arguments;
	private static int currentConnection = -1;
	
	private static ArrayList initializationListeners = new ArrayList();
	private static int value = 0;
}