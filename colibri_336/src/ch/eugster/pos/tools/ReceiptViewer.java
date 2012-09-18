package ch.eugster.pos.tools;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabFolderListener;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.jdom.Element;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.Standard;
import ch.eugster.pos.db.User;
import ch.eugster.pos.db.Version;
import ch.eugster.pos.printing.ReceiptPrinter;
import ch.eugster.pos.swt.PrintDestinationGroup;
import ch.eugster.pos.swt.ReceiptTreeComposite;
import ch.eugster.pos.swt.Resources;
import ch.eugster.pos.swt.UserVerificationDialog;
import ch.eugster.pos.swt.UserVerificationInputValidator;
import ch.eugster.pos.util.Config;
import ch.eugster.pos.util.OjbRepositoryHandler;
import ch.eugster.pos.util.Path;

public class ReceiptViewer extends ApplicationWindow implements Listener, ISelectionChangedListener
{
	
	private static final String version = "0.1";
	
	public ReceiptViewer(Shell parentShell)
	{
		super(parentShell);
		this.receiptButton = null;
		this.tabItems = new Hashtable();
		this.addStatusLine();
	}
	
	protected Control createContents(Composite parent)
	{
		this.getShell().setText("ColibriTS Beleg Viewer V. " + ReceiptViewer.version);
		
		Resources.initialize();
		
		this.createMenus();
		
		Composite main = new Composite(parent, 0);
		main.setLayoutData(new GridData(1808));
		main.setLayout(this.getStandardGridLayout());
		
		SashForm sash = new SashForm(main, 256);
		sash.setLayoutData(new GridData(1808));
		sash.setLayout(new GridLayout());
		
		Composite east = new Composite(sash, 0);
		east.setLayoutData(new GridData(1808));
		east.setLayout(this.getStandardGridLayout());
		
		this.receiptTreeComposite = new ReceiptTreeComposite(east, 0, ReceiptViewer.properties);
		this.receiptTreeComposite.setLayoutData(new GridData(1808));
		this.receiptTreeComposite.addListener(8, this);
		
		this.tabFolder = new CTabFolder(sash, 0);
		this.tabFolder.addCTabFolderListener(new CTabFolderListener()
		{
			public void itemClosed(CTabFolderEvent ctabfolderevent)
			{
			}
		});
		this.tabFolder.setLayoutData(new GridData(1808));
		this.tabFolder.addListener(13, this);
		
		Composite south = new Composite(main, 0);
		south.setLayoutData(new GridData(768));
		south.setLayout(this.getStandardGridLayout(2, false));
		
		Composite emptyPart = new Composite(south, 0);
		emptyPart.setLayoutData(new GridData(768));
		RowLayout emptyLayout = new RowLayout();
		emptyLayout.marginTop = 5;
		emptyLayout.marginBottom = 5;
		emptyPart.setLayout(emptyLayout);
		
		this.buttonBar = new Composite(south, 0);
		this.buttonBar.setLayoutData(new GridData(128));
		RowLayout rowLayout = new RowLayout();
		rowLayout.marginTop = 5;
		rowLayout.marginBottom = 5;
		rowLayout.marginLeft = 5;
		rowLayout.marginRight = 0;
		rowLayout.spacing = 5;
		rowLayout.type = 256;
		rowLayout.wrap = false;
		rowLayout.justify = true;
		this.buttonBar.setLayout(rowLayout);
		
		Element prt = Config.getInstance().getPosPrinter();
		ReceiptPrinter printer = ReceiptPrinter.getInstance(prt);
		
		this.cancelButton = new Button(this.buttonBar, 8);
		this.cancelButton.setText(Messages.getString("Main.Beenden_13"));
		this.cancelButton.addListener(13, this);
		
		return parent;
	}
	
	private void createMenus()
	{
		Menu bar = new Menu(this.getShell(), 2);
		this.getShell().setMenuBar(bar);
		
		MenuItem fileItem = new MenuItem(bar, 64);
		fileItem.setText(Messages.getString("MainWindow.&Datei_28"));
		
		Menu fileSubMenu = new Menu(this.getShell(), 4);
		fileItem.setMenu(fileSubMenu);
		MenuItem changeUserItem = new MenuItem(fileSubMenu, 8);
		changeUserItem.addListener(13, new Listener()
		{
			public void handleEvent(Event e)
			{
				UserVerificationInputValidator validator = new UserVerificationInputValidator(new Integer[]
				{ new Integer(0), new Integer(1) });
				UserVerificationDialog dialog = new UserVerificationDialog(ReceiptViewer.this.getShell(), Messages
								.getString("MainWindow.Benutzerwechsel_29"), Messages
								.getString("MainWindow.Geben_Sie_Benutzername_und_Passwort_ein._30"), Messages
								.getString("MainWindow._31"), validator);
				dialog.setBlockOnOpen(true);
				dialog.open();
			}
		});
		changeUserItem.setText(Messages.getString("MainWindow.Benutzer&wechsel_tCtrl+W_32"));
		changeUserItem.setAccelerator(0x40057);
		
		new MenuItem(fileSubMenu, 2);
		MenuItem versionItem = new MenuItem(fileSubMenu, 64);
		versionItem.setText("Version");
		versionItem.addListener(13, new Listener()
		{
			public void handleEvent(Event e)
			{
				org.eclipse.jface.dialogs.MessageDialog.openInformation(ReceiptViewer.getInstance().getShell(),
								"Version", "Version " + Version.version());
			}
		});
		
		new MenuItem(fileSubMenu, 2);
		MenuItem exitItem = new MenuItem(fileSubMenu, 8);
		exitItem.addListener(13, new Listener()
		{
			public void handleEvent(Event e)
			{
				ReceiptViewer.this.close();
			}
		});
		exitItem.setText(Messages.getString("MainWindow.&Beenden_tCtrl+B_33"));
		exitItem.setAccelerator(0x40042);
	}
	
	protected Point getInitialSize()
	{
		Point size = ReceiptViewer.dimension;
		String sizeX = ReceiptViewer.properties.getProperty("sizeX");
		String sizeY = ReceiptViewer.properties.getProperty("sizeY");
		if (sizeX != null && sizeY != null) try
		{
			size.x = new Integer(sizeX).intValue();
			size.y = new Integer(sizeY).intValue();
		}
		catch (NumberFormatException e)
		{
			size = ReceiptViewer.dimension;
		}
		return this.getShell().computeSize(size.x, size.y, true);
	}
	
	protected Point getInitialLocation()
	{
		int displayWidth = Display.getCurrent().getClientArea().width;
		int displayHeight = Display.getCurrent().getClientArea().height;
		Point initialSize = this.getInitialSize();
		Point location = new Point(0, 0);
		String locationX = ReceiptViewer.properties.getProperty("locationX");
		String locationY = ReceiptViewer.properties.getProperty("locationY");
		if (locationX != null && locationY != null) try
		{
			int width = new Integer(locationX).intValue();
			int height = new Integer(locationY).intValue();
			location = new Point(width, height);
		}
		catch (NumberFormatException e)
		{
			location.x = (displayWidth - initialSize.x) / 2;
			location.y = (displayHeight - initialSize.y) / 2;
		}
		return location;
	}
	
	public void selectionChanged(SelectionChangedEvent e)
	{
		e.getSource().equals(this.receiptTreeComposite.getReceiptTreeViewer());
	}
	
	public void handleEvent(Event e)
	{
		if (e.type == SWT.MouseDoubleClick && e.widget == this.receiptTreeComposite && e.item instanceof TreeItem)
		{
			TreeItem item = (TreeItem) e.item;
			CTabItem items[] = this.tabFolder.getItems();
			int index = -1;
			for (int i = 0; i < items.length; i++)
				if (items[i].getText().equals(item.getText())) index = i;
			
			if (index == -1)
			{
				if (item.getData() instanceof File)
				{
					File file = (File) item.getData();
					if (file.isFile())
					{
						CTabItem receiptItem = new CTabItem(this.tabFolder, 0);
						receiptItem.setText(file.getName());
						this.receiptComposite = new ReceiptComposite(this.tabFolder, file, 0);
						receiptItem.setControl(this.receiptComposite);
						this.tabFolder.setSelection(receiptItem);
					}
				}
			}
			else
			{
				this.tabFolder.setSelection(index);
			}
		}
		else if (e.type == SWT.Selection)
		{
			if (e.widget.equals(this.cancelButton))
			{
				this.close();
			}
		}
		else
		{
			System.out.println();
		}
	}
	
	private GridLayout getStandardGridLayout()
	{
		return this.getStandardGridLayout(1, false);
	}
	
	private GridLayout getStandardGridLayout(int cols, boolean equalWidth)
	{
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.numColumns = cols;
		layout.makeColumnsEqualWidth = equalWidth;
		return layout;
	}
	
	private static boolean testDefaultPath(String fullPath, boolean create)
	{
		boolean exists = true;
		File file = new File(fullPath);
		if (file.isDirectory()) return exists;
		if (!file.exists())
		{
			if (create)
			{
				if (!file.mkdirs())
				{
					exists = false;
				}
			}
			else
			{
				exists = false;
			}
		}
		else
		{
			exists = true;
		}
		return exists;
	}
	
	private static void connect()
	{
		Path.getInstance().getClass();
		System.setProperty("OJB.properties", Path.getInstance().ojbDir.concat("OJB.properties"));
		Element standard = Config.getInstance().getDatabaseStandard();
		Database.setStandard(new Standard(standard));
		Database.setCurrent(Database.getStandard());
		if (!Database.getCurrent().openConnection())
		{
			Toolkit.getDefaultToolkit().beep();
			Display display = new Display();
			Shell shell = new Shell(display);
			org.eclipse.jface.dialogs.MessageDialog
							.openError(
											shell,
											Messages.getString("Main.Databankfehler_17"),
											Messages
															.getString("Main.Die_Datenbankverbindung_konnte_nicht_hergestellt_werden._Das_Programm_wird_verlassen._18"));
			System.exit(-1);
		}
	}
	
	private static void setLocale(String language, String country)
	{
		Locale.setDefault(new Locale(language, country));
	}
	
	private static void setLocale()
	{
		Element localeElement = Config.getInstance().getLocale();
		ReceiptViewer
						.setLocale(localeElement.getAttributeValue("language"), localeElement
										.getAttributeValue("country"));
	}
	
	public static ReceiptViewer getInstance()
	{
		if (ReceiptViewer.me == null)
		{
			ReceiptViewer.me = new ReceiptViewer(null);
			ReceiptViewer.me.create();
			ReceiptViewer.me.setBlockOnOpen(true);
			ReceiptViewer.me.open();
		}
		return ReceiptViewer.me;
	}
	
	public static void main(String args[]) throws IOException
	{
		Version.setRunningProgram(3);
		ReceiptViewer.loadProperties();
		OjbRepositoryHandler.updateRepositoryUser();
		ReceiptViewer.connect();
		ReceiptViewer.setLocale();
		if (ReceiptViewer.verifyUser())
		{
			ReceiptViewer.getInstance();
			System.exit(0);
		}
	}
	
	public static boolean verifyUser()
	{
		Display display = new Display();
		Shell shell = new Shell(display);
		UserVerificationInputValidator validator = new UserVerificationInputValidator(new Integer[]
		{ new Integer(0), new Integer(1) });
		UserVerificationDialog getUser = new UserVerificationDialog(shell, Messages.getString("App.Benutzername_5"),
						Messages.getString("App.Geben_Sie_Benutzername_und_Passwort_ein__6"), Messages
										.getString("App._7"), validator);
		getUser.create();
		getUser.setBlockOnOpen(true);
		if (getUser.open() == 0 && User.getCurrentUser() != null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean close()
	{
		if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.getShell(), Messages
						.getString("Main.Beenden_23"), Messages.getString("Main.Wollen_Sie_das_Programm_beenden__24")))
		{
			return false;
		}
		else
		{
			this.saveProperties();
			return super.close();
		}
	}
	
	private static void loadProperties() throws IOException
	{
		Properties defaultProperties = new Properties();
		defaultProperties.setProperty("root", "save");
		ReceiptViewer.properties = new Properties(defaultProperties);
		File file = new File(Path.getInstance().receiptBrowserIniFile);
		try
		{
			FileInputStream in = new FileInputStream(file);
			ReceiptViewer.properties.load(in);
		}
		catch (FileNotFoundException filenotfoundexception)
		{
		}
		catch (IOException e)
		{
			org.eclipse.jface.dialogs.MessageDialog.openError(null, "Dateizugriffsfehler",
							"Die Datei \"receiptbrowser.properties\" im Verzeichnis " + Path.getInstance().DIR_CFG
											+ " konnte nicht gelesen werden. Das Programm wird beendet.");
			throw e;
		}
	}
	
	private void saveProperties()
	{
		ReceiptViewer.properties.setProperty("locationX", new Integer(this.getShell().getLocation().x).toString());
		ReceiptViewer.properties.setProperty("locationY", new Integer(this.getShell().getLocation().y).toString());
		ReceiptViewer.properties.setProperty("sizeX", new Integer(this.getShell().getSize().x).toString());
		ReceiptViewer.properties.setProperty("sizeY", new Integer(this.getShell().getSize().y).toString());
		File file = new File(Path.getInstance().receiptBrowserIniFile);
		try
		{
			if (!file.exists()) file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			ReceiptViewer.properties.store(out, null);
			out.close();
		}
		catch (FileNotFoundException filenotfoundexception)
		{
		}
		catch (IOException e)
		{
			org.eclipse.jface.dialogs.MessageDialog.openError(null, Messages.getString("Main.Dateizugriffsfehler_81"),
							Messages.getString("Main.Die_Eigenschaftendatei___statistics.ini___im_Verzeichnis__82")
											+ Path.getInstance().DIR_CFG
											+ Messages.getString("Main._konnte_nicht_aktualisiert_werden._83"));
		}
	}
	
	public CTabFolder getTabFolder()
	{
		return this.tabFolder;
	}
	
	public String getProperty(String key)
	{
		return ReceiptViewer.properties.getProperty(key);
	}
	
	private static final Point dimension = new Point(720, 540);
	private static ReceiptViewer me;
	private static Properties properties;
	
	private ReceiptTreeComposite receiptTreeComposite;
	private PrintDestinationGroup printDestinationGroup;
	private CTabFolder tabFolder;
	private ReceiptComposite receiptComposite;
	private Composite buttonBar;
	private Button receiptButton;
	private Button printButton;
	private Button cancelButton;
	private Hashtable tabItems;
	
}
