/*
 * Created on 12.09.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.ojb.broker.PersistenceBrokerFactory;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jdom.Element;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.Standard;
import ch.eugster.pos.db.User;
import ch.eugster.pos.db.Version;
import ch.eugster.pos.swt.UserVerificationDialog;
import ch.eugster.pos.swt.UserVerificationInputValidator;
import ch.eugster.pos.util.Config;
import ch.eugster.pos.util.OjbRepositoryHandler;
import ch.eugster.pos.util.Path;

/**
 * @author Administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class App
{
	
	/**
	 * 
	 */
	public App()
	{
		super();
		try
		{
			if (App.display == null)
			{
				App.display = new Display();
			}
		}
		catch (Error e)
		{
			System.exit(-1);
		}
		
		Shell s = new Shell(App.display);
		UserVerificationInputValidator validator = new UserVerificationInputValidator(new Integer[]
		{ new Integer(0), new Integer(1) });
		UserVerificationDialog getUser = new UserVerificationDialog(s, Messages.getString("App.Benutzername_5"), //$NON-NLS-1$
						Messages.getString("App.Geben_Sie_Benutzername_und_Passwort_ein__6"), //$NON-NLS-1$
						Messages.getString("App._7"), //$NON-NLS-1$
						validator);
		getUser.create();
		getUser.setBlockOnOpen(true);
		if (getUser.open() == 0)
		{
			if (User.getCurrentUser() != null)
			{
				// OleEnvironment.Initialize();
				MainWindow.getInstance();
				// OleEnvironment.UnInitialize();
			}
		}
	}
	
	public static void main(String[] args)
	{
		App.parseArguments(args);
		
		App.initLogging();
		
		if (App.isAlreadyRunning())
		{
			if (LogManager.getLogManager().getLogger("colibri") != null)
			{
				LogManager
								.getLogManager()
								.getLogger("colibri").log(Level.WARNING, Messages.getString("App.Eine_Instanz_des_Programms_laeuft_bereits._13")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			App.alert(Messages.getString("App.Eine_Instanz_des_Administrationsprogramms_laeuft_bereits..._14")); //$NON-NLS-1$
			System.exit(15);
		}
		if (App.colibriIsRunning())
		{
			if (LogManager.getLogManager().getLogger("colibri") != null)
			{
				LogManager
								.getLogManager()
								.getLogger("colibri").log(Level.WARNING, Messages.getString("App.Eine_Instanz_des_Programms_Colibri_laeuft._16")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			App
							.alert(Messages
											.getString("App.Eine_Instanz_des_Kassenprogramms_Colibri_laeuft._Bitte_beenden_Sie_dieses,_bevor_Sie_das_Administrationsprogramm_starten._17")); //$NON-NLS-1$
			System.exit(17);
		}
		
		if (!App.isCompatible())
		{
			if (LogManager.getLogManager().getLogger("colibri") != null)
			{
				LogManager
								.getLogManager()
								.getLogger("colibri").log(Level.SEVERE, Messages.getString("App.Das_Programm_benoetigt_mindestens_die_Laufzeitumgebunt_JRE_1.4.x._19")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			App
							.alert(Messages
											.getString("App.Der_Colibri_Administrator_benoetigt_mindestens_die_Laufzeitumgebunt_JRE_1.4.x._Das_Programm_wird_beendet._20")); //$NON-NLS-1$
			System.exit(14);
		}
		
		if (LogManager.getLogManager().getLogger("colibri") != null)
		{
			LogManager
							.getLogManager()
							.getLogger("colibri").log(Level.INFO, Messages.getString("App.Pruefen_der_Unterverzeichnisse..._22")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (!App.testDefaultPath(Path.getInstance().cfgDir, false))
		{
			if (LogManager.getLogManager().getLogger("colibri") != null)
			{
				LogManager
								.getLogManager()
								.getLogger("colibri").log(Level.SEVERE, Messages.getString("App.Unterverzeichnis__24") + Path.getInstance().cfgDir + Messages.getString("App._fehlt._25")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			App
							.alert(Messages
											.getString("App.Es_wurden_Fehler_in_der_Konfiguration_der_Unterverzeichnisse_festgestellt._Das_Verzeichnis__26") + Path.getInstance().cfgDir + Messages.getString("App._fehlt._Das_Programm_wird_beendet._27")); //$NON-NLS-1$ //$NON-NLS-2$
			System.exit(16);
		}
		if (!App.testDefaultPath(Path.getInstance().ojbDir, false))
		{
			if (LogManager.getLogManager().getLogger("colibri") != null)
			{
				LogManager
								.getLogManager()
								.getLogger("colibri").log(Level.SEVERE, Messages.getString("App.Unterverzeichnis__29") + Path.getInstance().ojbDir + Messages.getString("App._fehlt._30")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			App
							.alert(Messages
											.getString("App.Es_wurden_Fehler_in_der_Konfiguration_der_Unterverzeichnisse_festgestellt._Das_Verzeichnis__31") + Path.getInstance().ojbDir + Messages.getString("App._fehlt._Das_Programm_wird_beendet._32")); //$NON-NLS-1$ //$NON-NLS-2$
			System.exit(16);
		}
		if (!App.testDefaultPath(Path.getInstance().lockDir, false))
		{
			if (LogManager.getLogManager().getLogger("colibri") != null)
			{
				LogManager
								.getLogManager()
								.getLogger("colibri").log(Level.SEVERE, Messages.getString("App.Unterverzeichnis__34") + Path.getInstance().lockDir + Messages.getString("App._fehlt._35")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			App
							.alert(Messages
											.getString("App.Es_wurden_Fehler_in_der_Konfiguration_der_Unterverzeichnisse_festgestellt._Das_Verzeichnis__36") + Path.getInstance().lockDir + Messages.getString("App._fehlt._Das_Programm_wird_beendet._37")); //$NON-NLS-1$ //$NON-NLS-2$
			System.exit(16);
		}
		if (!App.testDefaultPath(Path.getInstance().logDir, false))
		{
			if (LogManager.getLogManager().getLogger("colibri") != null)
			{
				LogManager
								.getLogManager()
								.getLogger("colibri").log(Level.SEVERE, Messages.getString("App.Unterverzeichnis__39") + Path.getInstance().logDir + Messages.getString("App._fehlt._40")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			App
							.alert(Messages
											.getString("App.Es_wurden_Fehler_in_der_Konfiguration_der_Unterverzeichnisse_festgestellt._Das_Verzeichnis__41") + Path.getInstance().logDir + Messages.getString("App._fehlt._Das_Programm_wird_beendet._42")); //$NON-NLS-1$ //$NON-NLS-2$
			System.exit(16);
		}
		if (!App.testDefaultPath(Path.getInstance().iconDir, false))
		{
			if (LogManager.getLogManager().getLogger("colibri") != null)
			{
				LogManager
								.getLogManager()
								.getLogger("colibri").log(Level.SEVERE, Messages.getString("App.Unterverzeichnis__44") + Path.getInstance().iconDir + Messages.getString("App._fehlt._45")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			App
							.alert(Messages
											.getString("App.Es_wurden_Fehler_in_der_Konfiguration_der_Unterverzeichnisse_festgestellt._Das_Verzeichnis__46") + Path.getInstance().iconDir + Messages.getString("App._fehlt._Das_Programm_wird_beendet._47")); //$NON-NLS-1$ //$NON-NLS-2$
			System.exit(16);
		}
		if (LogManager.getLogManager().getLogger("colibri") != null)
		{
			LogManager
							.getLogManager()
							.getLogger("colibri").log(Level.INFO, Messages.getString("App.Unterverzeichnisse_gepr_u00FCft__OK._49")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		Version.setRunningProgram(Version.ADMINISTRATOR);
		
		OjbRepositoryHandler.updateRepositoryUser();
		
		if (!App.connect())
		{
			if (App.startConnectionWizard())
			{
				Config.reload();
				PersistenceBrokerFactory.releaseAllInstances();
				if (!App.connect())
				{
					//alert("Sie haben den Vorgang abgebrochen, das Programm wird verlassen."); //$NON-NLS-1$
					System.exit(-1);
				}
			}
			else
			{
				//alert("Sie haben den Vorgang abgebrochen, das Programm wird verlassen."); //$NON-NLS-1$
				System.exit(-1);
			}
		}
		App.setLocale();
		
		// net.java.plaf.LookAndFeelPatchManager.initialize();
		
		App.app = new App();
	}
	
	/**
	 * Die Argumente von der Commandline auswerten
	 * 
	 * @param args
	 *            Stringarray mit an das Programm uebergebenen Argumenten
	 */
	private static void parseArguments(String[] args)
	{
		for (int i = 0; i < args.length; i++)
		{
			/*
			 * Argument -l / --level gefolgt von Parameter. Als gueltige
			 * Parameter werden akzeptiert:
			 * 
			 * ALL Integer.MIN_VALUE INFO (800) WARNING (900) SEVERE (1000) OFF
			 * Integer.MAX_VALUE
			 */
			if (args[i].equals(Messages.getString("App.-l_50")) || args[i].equals(Messages.getString("App.--logginglevel_51"))) { //$NON-NLS-1$ //$NON-NLS-2$
				if (!(args.length < i + 1))
				{
					/*
					 * Folgt ein Argument auf -l, resp. --level?
					 */
					try
					{
						App.level = Level.parse(args[i + 1]);
						i++;
					}
					catch (IllegalArgumentException e)
					{
					}
				}
			}
		}
		/*
		 * Falls ein optionales Argument nicht angegeben worden ist, die
		 * entsprechenden Variablen initialisieren
		 */
		if (App.level == null)
		{
			App.level = Level.parse(Messages.getString("App.INFO_52")); //$NON-NLS-1$
		}
	}
	
	private static boolean testDefaultPath(String fullPath, boolean create)
	{
		boolean exists = true;
		File file = new File(fullPath);
		if (file.isDirectory())
		{
			return exists;
		}
		else
		{
			if (!file.exists())
			{
				if (create)
				{
					if (!file.mkdirs())
					{
						if (LogManager.getLogManager().getLogger("colibri") != null)
						{
							LogManager
											.getLogManager()
											.getLogger("colibri").log(Level.INFO, Messages.getString("App.Das_Unterverzeichnis__54") + file.getName() + Messages.getString("App._fehlt._55")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						}
						exists = false;
					}
				}
				else
				{
					if (LogManager.getLogManager().getLogger("colibri") != null)
					{
						LogManager
										.getLogManager()
										.getLogger("colibri").log(Level.INFO, Messages.getString("App.Das_Unterverzeichnis__57") + file.getName() + Messages.getString("App._fehlt._58")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
					exists = false;
				}
			}
			else
			{
				exists = true;
			}
		}
		return exists;
	}
	
	private static boolean initLogging()
	{
		File logDir = new File(Path.getInstance().DIR_LOG);
		if (logDir.exists() && logDir.isDirectory())
		{
			FileFilter filter = new FileFilter()
			{
				public boolean accept(File pathname)
				{
					if (pathname.getName().startsWith("colibri")) { //$NON-NLS-1$
						return true;
					}
					return false;
				}
			};
			File[] files = logDir.listFiles(filter);
			for (int i = 0; i < files.length; i++)
			{
				files[i].delete();
			}
		}
		
		LogManager.getLogManager().addLogger(Logger.getLogger("colibri")); //$NON-NLS-1$
		try
		{
			FileHandler fh = new FileHandler(Path.getInstance().logDir.concat(Path.getInstance().FILE_LOG), true);
			fh.setFormatter(new SimpleFormatter());
			if (LogManager.getLogManager().getLogger("colibri") != null)
			{
				LogManager.getLogManager().getLogger("colibri").addHandler(fh); //$NON-NLS-1$
				LogManager.getLogManager()
								.getLogger("colibri").setLevel(Level.parse(Config.getInstance().getLoggingLevel())); //$NON-NLS-1$ 
				LogManager.getLogManager().getLogger("colibri").info(
								"Logging-Level: " + Config.getInstance().getLoggingLevel());
			}
			return true;
		}
		catch (IOException e)
		{
		}
		catch (SecurityException e)
		{
		}
		return false;
	}
	
	private static boolean isAlreadyRunning()
	{
		// Calculate the location of the lock file
		File adminFile = new File(Path.getInstance().FILE_ADMIN_LOCK);
		
		// if the lock files already exist, try to delete,
		// assume failure means another program instance has it open
		if (adminFile.exists()) adminFile.delete();
		return adminFile.exists();
	}
	
	private static boolean colibriIsRunning()
	{
		// Calculate the location of the lock file
		File colibriFile = new File(Path.getInstance().FILE_ADMIN_LOCK);
		
		// if the lock files already exist, try to delete,
		// assume failure means another program instance has it open
		if (colibriFile.exists()) colibriFile.delete();
		return colibriFile.exists();
	}
	
	private static boolean isCompatible()
	{
		try
		{
			String jreVersion = System.getProperty(Messages.getString("App.java.version_70")); //$NON-NLS-1$
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
	
	/**
	 * 
	 * Datenbankverbindung herstellen
	 */
	private static boolean connect()
	{
		if (LogManager.getLogManager().getLogger("colibri") != null)
		{
			LogManager
							.getLogManager()
							.getLogger("colibri").info("Searching for file OJB.properties in path " + Path.getInstance().ojbDir); //$NON-NLS-1$ //$NON-NLS-2$
		}
		System.setProperty("OJB.properties", Path.getInstance().ojbDir.concat(Path.getInstance().FILE_OJBCFG)); //$NON-NLS-1$
		
		if (LogManager.getLogManager().getLogger("colibri") != null)
		{
			LogManager
							.getLogManager()
							.getLogger("colibri").log(Level.INFO, Messages.getString("App.Laden_der_Datenbankinformationen._84")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		Database.load();
		Database.setCurrent(Database.getStandard());
		
		if (LogManager.getLogManager().getLogger("colibri") != null)
		{
			LogManager
							.getLogManager()
							.getLogger("colibri").log(Level.INFO, Messages.getString("App.Verbindungsaufbau_zur_Datenbank__86") + Database.getCurrent().getName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (!Database.getCurrent().openConnection())
		{
			if (LogManager.getLogManager().getLogger("colibri") != null)
			{
				LogManager
								.getLogManager()
								.getLogger("colibri").severe(Messages.getString("App.Verbindung_zur_Datenbank__88") + Database.getCurrent().getName() + Messages.getString("App._fehlgeschlagen._89")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			return false;
		}
		
		if (Database.getCurrent() == null)
		{
			if (LogManager.getLogManager().getLogger("colibri") != null)
			{
				LogManager
								.getLogManager()
								.getLogger("colibri").log(Level.SEVERE, Messages.getString("App.Es_konnte_keine_Datenbankverbindung_hergestellt_werden._91")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			return false;
		}
		if (Database.getCurrent().equals(Database.getStandard()))
		{
			Standard standard = (Standard) Database.getCurrent();
			standard.updateSalespointStocks();
		}
		
		return true;
	}
	
	/**
	 * Locale setzen
	 * 
	 */
	private static void setLocale(String language, String country)
	{
		if (LogManager.getLogManager().getLogger("colibri") != null)
		{
			LogManager
							.getLogManager()
							.getLogger("colibri").log(Level.INFO, Messages.getString("App.Setzen_des_Defaultlocale..._101")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		Locale.setDefault(new Locale(language, country));
		if (LogManager.getLogManager().getLogger("colibri") != null)
		{
			LogManager
							.getLogManager()
							.getLogger("colibri").log(Level.INFO, Messages.getString("App.Defaultlocale_gesetzt..._103")); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Locale aus Konfigurationsdatei setzen
	 * 
	 */
	private static void setLocale()
	{
		Element localeElement = Config.getInstance().getLocale();
		App
						.setLocale(
										localeElement.getAttributeValue(Messages.getString("App.language_105")), localeElement.getAttributeValue(Messages.getString("App.country_106"))); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private static void alert(String msg)
	{
		final Display display = new Display();
		final Shell shell = new Shell(display);
		org.eclipse.jface.dialogs.MessageDialog.openWarning(shell, Messages.getString("App.Fehler_107"), msg); //$NON-NLS-1$
	}
	
	private static boolean startConnectionWizard()
	{
		boolean result = false;
		App.display = new Display();
		Shell s = new Shell(App.display);
		
		ConnectionWizard cw = new ConnectionWizard();
		WizardDialog dialog = new WizardDialog(s, cw);
		dialog.create();
		dialog.setBlockOnOpen(true);
		if (dialog.open() == 0)
		{
			result = true;
		}
		dialog.close();
		dialog = null;
		return result;
	}
	
	private static class Identifier
	{
		private static final String DELIM = Messages.getString("App..__108"); //$NON-NLS-1$
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
	
	public static App app = null;
	public static Level level = null;
	public static Database db = null;
	public static User currentUser = null;
	public static Display display;
	
}
