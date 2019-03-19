/*
 * Created on 26.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.gui;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.jdom.Element;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.db.Standard;
import ch.eugster.pos.db.User;
import ch.eugster.pos.db.Version;
import ch.eugster.pos.printing.ReceiptPrinter;
import ch.eugster.pos.printing.SettlementPrinterFromStatistics;
import ch.eugster.pos.statistics.core.BookkeepingTransfer;
import ch.eugster.pos.statistics.core.Day14HourStatistics;
import ch.eugster.pos.statistics.core.ProductGroupStatistics;
import ch.eugster.pos.statistics.core.ReceiptListData;
import ch.eugster.pos.statistics.core.ReceiptListStatistics;
import ch.eugster.pos.statistics.core.ReceiptStatistics;
import ch.eugster.pos.statistics.core.ReceiptTransfer;
import ch.eugster.pos.statistics.core.SettlementStatistics;
import ch.eugster.pos.statistics.core.SettlementStatistics2;
import ch.eugster.pos.statistics.core.TaxAccountStatistics;
import ch.eugster.pos.statistics.events.IDateChangeListener;
import ch.eugster.pos.statistics.events.ISalespointSelectionListener;
import ch.eugster.pos.swt.PrintDestinationGroup;
import ch.eugster.pos.swt.Resources;
import ch.eugster.pos.swt.UserVerificationDialog;
import ch.eugster.pos.swt.UserVerificationInputValidator;
import ch.eugster.pos.util.Config;
import ch.eugster.pos.util.OjbRepositoryHandler;
import ch.eugster.pos.util.Path;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Main extends ApplicationWindow implements Listener, ISelectionChangedListener
{
	
	private static final Point dimension = new Point(720, 540);
	
	/**
	 * @param parentShell
	 */
	public Main(Shell parentShell)
	{
		super(parentShell);
		this.addStatusLine();
	}
	
	@Override
	protected Control createContents(Composite parent)
	{
		this.getShell().setText("ColibriTS Auswertungen");
		
		Resources.initialize();
		
		this.createMenus();
		
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		main.setLayout(this.getStandardGridLayout());
		
		Composite north = new Composite(main, SWT.NONE);
		north.setLayoutData(new GridData(GridData.FILL_BOTH));
		north.setLayout(this.getStandardGridLayout(2, false));
		/*
		 * Linke Seite
		 */
		Composite east = new Composite(north, SWT.NONE);
		east.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		east.setLayout(this.getStandardGridLayout());
		
		this.salespointComposite = new SalespointComposite(east, SWT.NONE, Main.properties);
		this.salespointComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		this.dateRangeGroup = new DateRangeGroup(east, SWT.NONE, Main.properties);
		this.dateRangeGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		String d = Main.properties.getProperty("destination", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		int destination = Integer.parseInt(d);
		this.printDestinationGroup = new PrintDestinationGroup(east, SWT.NONE, destination);
		this.printDestinationGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		/*
		 * Rechte Seite
		 */
		this.tf = new TabFolder(north, SWT.NONE);
		this.tf.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.tf.addListener(SWT.Selection, this);
		
		this.settlementComposite = new SettlementComposite(this.tf, SWT.NONE, Main.properties,
						this.salespointComposite, this.dateRangeGroup);
		TabItem settlementItem = new TabItem(this.tf, SWT.NONE);
		settlementItem.setText(Messages.getString("Main.Abschluss_5")); //$NON-NLS-1$
		settlementItem.setControl(this.settlementComposite);
		this.settlementComposite.addListener(SWT.Selection, this);
		
		this.productGroupComposite = new ProductGroupComposite(this.tf, SWT.NONE);
		TabItem productGroupItem = new TabItem(this.tf, SWT.NONE);
		productGroupItem.setText(Messages.getString("Main.Warengruppenstatistik_6")); //$NON-NLS-1$
		productGroupItem.setControl(this.productGroupComposite);
		
		this.receiptStatisticsComposite = new ReceiptStatisticsComposite(this.tf, SWT.NONE);
		TabItem receiptStatisticsItem = new TabItem(this.tf, SWT.NONE);
		receiptStatisticsItem.setText("Belegstatistik");
		receiptStatisticsItem.setControl(this.receiptStatisticsComposite);
		
		this.dayHourComposite = new DayHourComposite(this.tf, SWT.NONE);
		TabItem dayHourItem = new TabItem(this.tf, SWT.NONE);
		dayHourItem.setText(Messages.getString("Main.Tagesumsatzstatistik_7")); //$NON-NLS-1$
		dayHourItem.setControl(this.dayHourComposite);
		
		this.taxAccountComposite = new TaxAccountComposite(this.tf, SWT.NONE);
		TabItem taxAccountItem = new TabItem(this.tf, SWT.NONE);
		taxAccountItem.setText(Messages.getString("Main.Mwst-Liste_8")); //$NON-NLS-1$
		taxAccountItem.setControl(this.taxAccountComposite);
		
		this.receiptListComposite = new ReceiptListComposite(this.tf, SWT.NONE, this.salespointComposite,
						this.dateRangeGroup, this.printDestinationGroup, Main.properties);
		TabItem receiptListItem = new TabItem(this.tf, SWT.NONE);
		receiptListItem.setText(Messages.getString("Main.Belegliste_9")); //$NON-NLS-1$
		receiptListItem.setControl(this.receiptListComposite);
		
		this.bookkeepingTransferComposite = new BookkeepingTransferComposite(this.tf, SWT.NONE, Main.properties);
		this.bookkeepingTransferComposite.addListener(IDateChangeListener.DATE_CHANGE_EVENT_TYPE, this.dateRangeGroup);
		this.bookkeepingTransferComposite.addListener(ISalespointSelectionListener.SALESPOINT_SELECTION,
						this.salespointComposite);
		TabItem bookkeepingTransferItem = new TabItem(this.tf, SWT.NONE);
		bookkeepingTransferItem.setText(Messages.getString("Main.Fibu-Transfer_10")); //$NON-NLS-1$
		bookkeepingTransferItem.setControl(this.bookkeepingTransferComposite);
		
		this.transferComposite = new ReceiptTransferComposite(this.tf, SWT.NONE, Main.properties);
		TabItem transferItem = new TabItem(this.tf, SWT.NONE);
		transferItem.setText(Messages.getString("Main.Belegtransfer_11")); //$NON-NLS-1$
		transferItem.setControl(this.transferComposite);
		this.transferComposite.addListener(SWT.Selection, this);
		
		/*
		 * Buttonbar am unteren Rand
		 */
		Composite south = new Composite(main, SWT.NONE);
		south.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		south.setLayout(this.getStandardGridLayout(2, false));
		
		Composite emptyPart = new Composite(south, SWT.NONE);
		emptyPart.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		RowLayout emptyLayout = new RowLayout();
		emptyLayout.marginTop = 5;
		emptyLayout.marginBottom = 5;
		emptyPart.setLayout(emptyLayout);
		
		this.buttonBar = new Composite(south, SWT.NONE);
		this.buttonBar.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		RowLayout rowLayout = new RowLayout();
		rowLayout.marginTop = 5;
		rowLayout.marginBottom = 5;
		rowLayout.marginLeft = 5;
		rowLayout.marginRight = 0;
		rowLayout.spacing = 5;
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.wrap = false;
		rowLayout.justify = true;
		this.buttonBar.setLayout(rowLayout);
		
		Element prt = Config.getInstance().getPosPrinter();
		ReceiptPrinter printer = ReceiptPrinter.getInstance(prt);
		// if (ReceiptPrinter.isUsed())
		// {
		this.receiptButton = new Button(this.buttonBar, SWT.PUSH);
		this.receiptButton.setText("Coupon drucken"); //$NON-NLS-1$
		this.receiptButton.setData("print receipt");
		this.receiptButton.addListener(SWT.Selection, this);
		this.receiptButton.setEnabled(true);
		this.receiptButton.setVisible(true);
		this.receiptListComposite.addListener(SWT.Selection, this);
		// }
		
		this.receiptListButton = new Button(this.buttonBar, SWT.PUSH);
		this.receiptListButton.setText("Liste drucken"); //$NON-NLS-1$
		this.receiptListButton.setData("print receiptlist");
		this.receiptListButton.addListener(SWT.Selection, this);
		this.receiptListButton.setEnabled(false);
		this.receiptListButton.setVisible(false);
		this.receiptListComposite.addListener(SWT.Selection, this);
		
		this.printButton = new Button(this.buttonBar, SWT.PUSH);
		this.printButton.setText(Messages.getString("Main.___Drucken____12")); //$NON-NLS-1$
		if (this.tf.getItem(this.tf.getSelectionIndex()).getControl().equals(this.settlementComposite))
		{
			this.printButton.setEnabled(this.settlementComposite.isValid());
		}
		this.printButton.addListener(SWT.Selection, this);
		this.salespointComposite.getSalespointViewer().addPostSelectionChangedListener(this);
		
		this.cancelButton = new Button(this.buttonBar, SWT.PUSH);
		this.cancelButton.setText(Messages.getString("Main.Beenden_13")); //$NON-NLS-1$
		this.cancelButton.addListener(SWT.Selection, this);
		
		TabItem item = this.tf.getItem(this.tf.getSelectionIndex());
		Control control = item.getControl();
		Event event = new Event();
		event.widget = control;
		event.type = SWT.Selection;
		this.handleEvent(event);
		
		return parent;
	}
	
	private void createMenus()
	{
		/*
		 * Menuzeile
		 */
		Menu bar = new Menu(this.getShell(), SWT.BAR);
		this.getShell().setMenuBar(bar);
		
		/*
		 * 1. Menubereich: Datei
		 */
		MenuItem fileItem = new MenuItem(bar, SWT.CASCADE);
		fileItem.setText(Messages.getString("MainWindow.&Datei_28")); //$NON-NLS-1$
		
		Menu fileSubMenu = new Menu(this.getShell(), SWT.DROP_DOWN);
		fileItem.setMenu(fileSubMenu);
		
		MenuItem changeUserItem = new MenuItem(fileSubMenu, SWT.PUSH);
		changeUserItem.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				UserVerificationInputValidator validator = new UserVerificationInputValidator(new Integer[]
				{ new Integer(0), new Integer(1) });
				UserVerificationDialog dialog = new UserVerificationDialog(
								Main.this.getShell(),
								Messages.getString("MainWindow.Benutzerwechsel_29"), Messages.getString("MainWindow.Geben_Sie_Benutzername_und_Passwort_ein._30"), Messages.getString("MainWindow._31"), validator); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				dialog.setBlockOnOpen(true);
				if (dialog.open() == 0)
				{
					
				}
			}
		});
		changeUserItem.setText(Messages.getString("MainWindow.Benutzer&wechsel_tCtrl+W_32")); //$NON-NLS-1$
		changeUserItem.setAccelerator(SWT.CTRL + 'W');
		
		new MenuItem(fileSubMenu, SWT.SEPARATOR);
		
		MenuItem versionItem = new MenuItem(fileSubMenu, SWT.CASCADE);
		versionItem.setText("Version"); //$NON-NLS-1$
		versionItem.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				org.eclipse.jface.dialogs.MessageDialog.openInformation(Main.getInstance().getShell(), "Version",
								"Version " + Version.version());
			}
		});
		
		new MenuItem(fileSubMenu, SWT.SEPARATOR);
		
		MenuItem exitItem = new MenuItem(fileSubMenu, SWT.PUSH);
		exitItem.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				Main.this.close();
			}
		});
		exitItem.setText(Messages.getString("MainWindow.&Beenden_tCtrl+B_33")); //$NON-NLS-1$
		exitItem.setAccelerator(SWT.CTRL + 'B');
	}
	
	/**
	 * Returns the initial size to use for the shell. The default implementation
	 * returns the preferred size of the shell, using
	 * <code>Shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true)</code>.
	 * 
	 * @return the initial size of the shell
	 */
	@Override
	protected Point getInitialSize()
	{
		// 10108
		Point size = Main.dimension;
		String sizeX = Main.properties.getProperty("sizeX");
		String sizeY = Main.properties.getProperty("sizeY");
		if (sizeX != null && sizeY != null)
		{
			try
			{
				size.x = new Integer(sizeX).intValue();
				size.y = new Integer(sizeY).intValue();
			}
			catch (NumberFormatException e)
			{
				size = Main.dimension;
			}
		}
		return this.getShell().computeSize(size.x, size.y, true);
		// 10108
	}
	
	// 10108
	@Override
	protected Point getInitialLocation(Point initialSize)
	{
		int displayWidth = Display.getCurrent().getClientArea().width;
		int displayHeight = Display.getCurrent().getClientArea().height;
		Point location = new Point(0, 0);
		
		String locationX = Main.properties.getProperty("locationX");
		String locationY = Main.properties.getProperty("locationY");
		if (locationX != null && locationY != null)
		{
			try
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
		}
		return location;
	}
	
	// 10108
	
	public void selectionChanged(SelectionChangedEvent e)
	{
		if (e.getSource().equals(this.salespointComposite.getSalespointViewer()))
		{
			this.printButton.setEnabled(this.salespointComposite.areAllSalespointsSelected()
							|| !e.getSelection().isEmpty());
		}
		else if (e.getSource().equals(this.receiptListComposite))
		{
			System.out.println();
		}
	}
	
	public void handleEvent(Event e)
	{
		if (e.type == SWT.Selection)
		{
			if (e.widget instanceof Button)
			{
				if (e.widget.equals(this.cancelButton))
				{
					this.close();
				}
				else if (e.widget.equals(this.receiptButton))
				{
					if (this.tf.getSelectionIndex() == 0)
					{
						if (this.settlementComposite.getSelectedSettlement().longValue() > 0)
						{
							SettlementPrinterFromStatistics printer = new SettlementPrinterFromStatistics(
											this.settlementComposite.getSelectedSettlement());
							printer.printSettlement();
						}
					}
					else if (this.tf.getSelectionIndex() == 5)
					{
						this.receiptListComposite.printReceipt();
					}
				}
				else if (e.widget.equals(this.printButton))
				{
					TabItem item = this.tf.getItem(this.tf.getSelectionIndex());
					Control control = item.getControl();
					
					if (control instanceof SettlementComposite)
					{
						Control child = ((SettlementComposite) control).getCurrentControl();
						if (child instanceof SettlementNumberComposite)
						{
							SettlementStatistics2 statistics = new SettlementStatistics2(this.salespointComposite,
											this.dateRangeGroup, this.printDestinationGroup, Main.properties,
											this.settlementComposite);
							if (statistics.hasRecords())
								this.run(statistics);
							else
							{
								/*
								 * Keine Records gefunden. Da inzwischen weitere
								 * Optionen dazugekommen sind, kann ich nicht
								 * einfach den Ersatzlauf machen, sondern muss
								 * zuerst prüfen, ob ein gültiger Report
								 * ausgewählt war. Nicht gültig ist der
								 * SettlementDiffs-Report, weil der nur auf
								 * Settlement- Datensätze zugreift, ein Aufruf
								 * von SettlementStatistics daher sinnlos ist.
								 */
								this.run(new SettlementStatistics(this.salespointComposite, this.dateRangeGroup,
												this.printDestinationGroup, Main.properties, this.settlementComposite));
							}
						}
						else if (child instanceof SettlementDateComposite)
						{
							SettlementStatistics2 statistics = new SettlementStatistics2(this.salespointComposite,
											this.dateRangeGroup, this.printDestinationGroup, Main.properties,
											this.settlementComposite);
							if (statistics.hasRecords())
								this.run(statistics);
							else
							{
								/*
								 * Keine Records gefunden. Da inzwischen weitere
								 * Optionen dazugekommen sind, kann ich nicht
								 * einfach den Ersatzlauf machen, sondern muss
								 * zuerst prüfen, ob ein gültiger Report
								 * ausgewählt war. Nicht gültig ist der
								 * SettlementDiffs-Report, weil der nur auf
								 * Settlement- Datensätze zugreift, ein Aufruf
								 * von SettlementStatistics daher sinnlos ist.
								 */
								this.run(new SettlementStatistics(this.salespointComposite, this.dateRangeGroup,
												this.printDestinationGroup, Main.properties, this.settlementComposite));
							}
						}
						else if (child instanceof SettlementReceiptComposite)
						{
							this.run(new SettlementStatistics(this.salespointComposite, this.dateRangeGroup,
											this.printDestinationGroup, Main.properties, this.settlementComposite));
						}
					}
					else if (control instanceof ProductGroupComposite)
					{
						this.run(new ProductGroupStatistics(this.salespointComposite, this.dateRangeGroup,
										this.printDestinationGroup, Main.properties, this.productGroupComposite));
					}
					else if (control instanceof DayHourComposite)
					{
						this.run(new Day14HourStatistics(this.salespointComposite, this.dateRangeGroup,
										this.printDestinationGroup, Main.properties, this.dayHourComposite));
					}
					else if (control instanceof TaxAccountComposite)
					{
						this.run(new TaxAccountStatistics(this.salespointComposite, this.dateRangeGroup,
										this.printDestinationGroup, Main.properties, this.taxAccountComposite));
					}
					else if (control instanceof ReceiptStatisticsComposite)
					{
						this.run(new ReceiptStatistics(this.salespointComposite, this.dateRangeGroup,
										this.printDestinationGroup, Main.properties, this.receiptStatisticsComposite));
					}
					else if (control instanceof ReceiptListComposite)
					{
						ReceiptListComposite rlc = (ReceiptListComposite) control;
						this.run(new ReceiptListData(this.salespointComposite, this.dateRangeGroup,
										this.printDestinationGroup, Main.properties, this.receiptListComposite));
						this.receiptListButton
										.setEnabled(rlc.getReceiptTable().getTable().getItems().length == 0 ? false
														: true);
					}
					else if (control instanceof ReceiptTransferComposite)
					{
						this.run(new ReceiptTransfer(this.salespointComposite, this.dateRangeGroup,
										this.printDestinationGroup, Main.properties, this.transferComposite));
					}
					else if (control instanceof BookkeepingTransferComposite)
					{
						this.run(new BookkeepingTransfer(this.salespointComposite, this.dateRangeGroup,
										this.printDestinationGroup, Main.properties, this.bookkeepingTransferComposite));
					}
				}
				else if (e.widget.equals(this.receiptListButton))
				{
					this.run(new ReceiptListStatistics(this.salespointComposite, this.dateRangeGroup,
									this.printDestinationGroup, Main.properties, this.receiptListComposite));
				}
			}
			else if (e.widget.equals(this.tf))
			{
				TabItem item = this.tf.getItem(this.tf.getSelectionIndex());
				Control control = item.getControl();
				if (control instanceof ITabFolderChild)
				{
					this.printButton.setText(((ITabFolderChild) control).getPrintButtonDesignation());
					this.printButton.setEnabled(((ITabFolderChild) control).isValid());
					this.dateRangeGroup.setToDateEnabled(!control.equals(this.receiptListComposite));
					this.receiptListButton.setVisible(control.equals(this.receiptListComposite));
					if (this.receiptButton != null)
					{
						boolean visible = false;
						boolean enabled = false;
						if (control.equals(this.receiptListComposite))
						{
							enabled = !this.receiptListComposite.getReceiptTable().getSelection().isEmpty();
							visible = true;
						}
						else if (control.equals(this.settlementComposite))
						{
							if (this.settlementComposite.getCurrentControl() instanceof SettlementNumberComposite)
								enabled = true;
							
							visible = true;
						}
						this.receiptButton.setVisible(visible);
						this.receiptButton.setEnabled(enabled);
					}
				}
			}
			else if (e.widget.equals(this.transferComposite))
			{
				this.printButton.setText(this.transferComposite.getPrintButtonDesignation());
			}
			else if (e.widget.equals(this.settlementComposite))
			{
				this.printButton.setEnabled(this.settlementComposite.isValid());
				if (this.settlementComposite.getCurrentControl() instanceof SettlementNumberComposite)
					this.receiptButton
									.setEnabled(SettlementNumberComposite.getInstance().getSelectedSettlement() != null);
				else
					this.receiptButton.setEnabled(false);
			}
			else if (e.widget instanceof Combo)
			{
				this.printButton.setEnabled(this.settlementComposite.isValid());
				if (this.settlementComposite.getCurrentControl() instanceof SettlementNumberComposite)
					this.receiptButton
									.setEnabled(SettlementNumberComposite.getInstance().getSelectedSettlement() != null);
				else
					this.receiptButton.setEnabled(false);
			}
			else if (e.widget.equals(this.receiptListComposite))
			{
				if (this.receiptButton != null)
				{
					this.receiptButton.setEnabled(e.data instanceof Receipt ? true : false);
				}
				ReceiptListComposite rlc = (ReceiptListComposite) e.widget;
				this.receiptListButton.setEnabled(rlc.getReceiptTable().getTable().getItems().length == 0 ? false
								: true);
			}
		}
		else if (e.type == 32000)
		{
			TabItem item = this.tf.getItem(this.tf.getSelectionIndex());
			Control control = item.getControl();
			if (control.equals(this.settlementComposite))
			{
				this.printButton.setEnabled(this.settlementComposite.isValid());
				if (this.settlementComposite.getCurrentControl() instanceof SettlementNumberComposite)
					this.receiptButton
									.setEnabled(SettlementNumberComposite.getInstance().getSelectedSettlement() != null);
				else
					this.receiptButton.setEnabled(false);
			}
		}
	}
	
	private void run(IRunnableWithProgress startItem)
	{
		if (startItem instanceof IRunnableWithProgress)
		{
			try
			{
				ProgressMonitorDialog monitor = new ProgressMonitorDialog(this.getShell());
				monitor.run(false, false, startItem);
				startItem = null;
			}
			catch (InvocationTargetException ite)
			{
				ite.printStackTrace();
			}
			catch (InterruptedException ie)
			{
				ie.printStackTrace();
			}
		}
		
	}
	
	public Button getReceiptButton()
	{
		return this.receiptButton;
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
	
	public IProgressMonitor getProgressMonitor()
	{
		return this.getStatusLineManager().getProgressMonitor();
	}
	
	/**
	 * Die Argumente von der Commandline auswerten
	 * 
	 * @param args
	 *            Stringarray mit an das Programm übergebenen Argumenten
	 */
	private static void parseArguments(String[] args)
	{
		Level level = null;
		
		for (int i = 0; i < args.length; i++)
		{
			/*
			 * Argument -l / --level gefolgt von Parameter. Als gültige
			 * Parameter werden akzeptiert:
			 * 
			 * ALL Integer.MIN_VALUE INFO (800) WARNING (900) SEVERE (1000) OFF
			 * Integer.MAX_VALUE
			 */
			if (args[i].equals("-l") || args[i].equals("--logginglevel")) { //$NON-NLS-1$ //$NON-NLS-2$
				if (!(args.length < i + 1))
				{
					/*
					 * Folgt ein Argument auf -l, resp. --level?
					 */
					try
					{
						level = Level.parse(args[i + 1]);
						i++;
					}
					catch (IllegalArgumentException e)
					{
						level = Level.INFO;
					}
				}
			}
		}
		/*
		 * Falls ein optionales Argument nicht angegeben worden ist, die
		 * entsprechenden Variablen initialisieren
		 */
		if (level == null)
		{
			level = Level.parse("INFO"); //$NON-NLS-1$
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
						Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
										.log(Level.INFO,
														Messages.getString("App.Das_Unterverzeichnis__54") + file.getName() + Messages.getString("App._fehlt._55")); //$NON-NLS-1$ //$NON-NLS-2$ 
						exists = false;
					}
				}
				else
				{
					Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
									.log(Level.INFO,
													Messages.getString("App.Das_Unterverzeichnis__57") + file.getName() + Messages.getString("App._fehlt._58")); //$NON-NLS-1$ //$NON-NLS-2$ 
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
		//		File logFile = new File(Path.getInstance().logDir + "colibri.log"); //$NON-NLS-1$
		// if (logFile.exists() && logFile.isFile())
		// {
		// logFile.delete();
		// }
		// LogManager.getLogManager().addLogger(Logger.getLogger(Logger.GLOBAL_LOGGER_NAME));
		try
		{
			FileHandler fh = new FileHandler(Path.getInstance().logDir.concat(Path.getInstance().FILE_LOG), true);
			fh.setFormatter(new SimpleFormatter());
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).addHandler(fh);
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.parse(Messages.getString("App.ALL_63"))); //$NON-NLS-1$ 
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
	
	private static boolean isCompatible()
	{
		try
		{
			String jreVersion = System.getProperty("java.version"); //$NON-NLS-1$
			Identifier minimum = new Identifier(1, 4, 2);
			Identifier version = new Identifier(jreVersion);
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
	private static void connect()
	{
		System.setProperty("OJB.properties", Path.getInstance().ojbDir.concat(Path.getInstance().FILE_OJBCFG)); //$NON-NLS-1$
		
		Element standard = Config.getInstance().getDatabaseStandard();
		Database.setStandard(new Standard(standard));
		Database.setCurrent(Database.getStandard());
		if (!Database.getCurrent().openConnection())
		{
			Toolkit.getDefaultToolkit().beep();
			Display display = new Display();
			Shell shell = new Shell(display);
			org.eclipse.jface.dialogs.MessageDialog
							.openError(shell,
											Messages.getString("Main.Databankfehler_17"), //$NON-NLS-1$
											Messages.getString("Main.Die_Datenbankverbindung_konnte_nicht_hergestellt_werden._Das_Programm_wird_verlassen._18") //$NON-NLS-1$
							);
			System.exit(-1);
		}
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Verbindungsmethode wird verlassen..."); //$NON-NLS-1$ 
	}
	
	/**
	 * Locale setzen
	 * 
	 */
	private static void setLocale(String language, String country)
	{
		Locale.setDefault(new Locale(language, country));
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(Messages.getString("Main.Locale_ist_gesetzt_20")); //$NON-NLS-1$ 
	}
	
	/**
	 * Locale aus Konfigurationsdatei setzen
	 * 
	 */
	private static void setLocale()
	{
		Element localeElement = Config.getInstance().getLocale();
		Main.setLocale(localeElement.getAttributeValue("language"), localeElement.getAttributeValue("country")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	static class Identifier
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
		
		private Identifier(String versionString)
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
		private boolean isGreaterEqualTo(Identifier minimum)
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
	
	public static Main getInstance()
	{
		if (Main.me == null)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
							Messages.getString("Main.Hauptfenster_wird_instantiiert..._22")); //$NON-NLS-1$ 
			Main.me = new Main(null);
			Main.me.create();
			Main.me.setBlockOnOpen(true);
			Main.me.open();
		}
		return Main.me;
	}
	
	public static void main(String[] args) throws IOException
	{
		Main.parseArguments(args);
		
		Version.setRunningProgram(Version.STATISTICS);
		
		if (Main.initLogging())
		{
			
			Main.loadProperties();
			
			if (Main.isCompatible())
			{
				/*
				 * Vor dem Öffnen der Datenbank OJB aktualisieren
				 */
				OjbRepositoryHandler.updateRepositoryUser();
				
				/*
				 * Datenbank öffnen
				 */
				Main.connect();
				
				/*
				 * Locale setzen
				 */
				Main.setLocale();
				
				if (Main.verifyUser())
				{
					Main.getInstance();
					System.exit(0);
				}
			}
		}
	}
	
	public static boolean verifyUser()
	{
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "Applikation wird instantiiert."); //$NON-NLS-1$ 
		Display display = new Display();
		Shell shell = new Shell(display);
		UserVerificationInputValidator validator = new UserVerificationInputValidator(new Integer[]
		{ new Integer(0), new Integer(1) });
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO,
						Messages.getString("App.Start_Benutzerauthentifizierung..._4")); //$NON-NLS-1$ 
		UserVerificationDialog getUser = new UserVerificationDialog(shell, Messages.getString("App.Benutzername_5"), //$NON-NLS-1$
						Messages.getString("App.Geben_Sie_Benutzername_und_Passwort_ein__6"), //$NON-NLS-1$
						Messages.getString("App._7"), //$NON-NLS-1$
						validator);
		getUser.create();
		getUser.setBlockOnOpen(true);
		if (getUser.open() == 0)
		{
			if (User.getCurrentUser() != null)
			{
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO,
								Messages.getString("App.Benutzerauthentifizierung_erfolgreich._9")); //$NON-NLS-1$ 
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO,
								Messages.getString("App.Hauptfenster_wird_geladen..._11")); //$NON-NLS-1$ 
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean close()
	{
		if (org.eclipse.jface.dialogs.MessageDialog
						.openQuestion(this.getShell(),
										Messages.getString("Main.Beenden_23"), Messages.getString("Main.Wollen_Sie_das_Programm_beenden__24")) == false) { //$NON-NLS-1$ //$NON-NLS-2$
			return false;
		}
		
		this.saveProperties();
		// transferComposite.getReceiptImportComposite().saveTranslators();
		return super.close();
	}
	
	private static void loadProperties() throws IOException
	{
		/*
		 * Einstellungen laden Zuerst die Defaulteinstellungen...
		 */
		Properties defaultProperties = new Properties();
		
		defaultProperties.setProperty(Messages.getString("Main.consolidated_25"), "false"); //$NON-NLS-1$ //$NON-NLS-2$
		defaultProperties.setProperty(Messages.getString("Main.salespoints_27"), ""); //$NON-NLS-1$ //$NON-NLS-2$
		/*
		 * Datumsbereich
		 */
		DateFormat df = new SimpleDateFormat();
		defaultProperties.setProperty("fromDate", df.format(new Date())); //$NON-NLS-1$
		defaultProperties.setProperty("toDate", df.format(new Date())); //$NON-NLS-1$
		/*
		 * Print destination properties
		 */
		defaultProperties.setProperty("destination", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		defaultProperties.setProperty("filetype", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		/*
		 * Current tab properties
		 */
		defaultProperties.setProperty("currentTab", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		/*
		 * Settlements
		 */
		defaultProperties.setProperty("settlement-type", "0");
		defaultProperties.setProperty("date.report.selection", "0");
		defaultProperties.setProperty("discount-list.only-with-discounts", "true");
		defaultProperties.setProperty("date.report.full-selection", "true");
		defaultProperties.setProperty("discount-list.only-with-discounts", "true");
		defaultProperties.setProperty("receipt.report.full-selection", "true");
		/*
		 * Transfer Fibu
		 */
		defaultProperties.setProperty("pattern", "dd.MM.yyyy"); //$NON-NLS-1$ //$NON-NLS-2$
		defaultProperties.setProperty("group", "P"); //$NON-NLS-1$ //$NON-NLS-2$
		defaultProperties.setProperty("mType", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		defaultProperties.setProperty("origin", "3"); //$NON-NLS-1$ //$NON-NLS-2$
		defaultProperties.setProperty("fibu-export-path", ""); //$NON-NLS-1$ //$NON-NLS-2$
		defaultProperties.setProperty("booking-id", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		/*
		 * Import/Export properties
		 */
		defaultProperties.setProperty("transfer-type", "exportieren"); //$NON-NLS-1$ //$NON-NLS-2$
		defaultProperties.setProperty("export-path", Config.getInstance().getSalespointExportPath()); //$NON-NLS-1$ 
		defaultProperties.setProperty("import-path", ""); //$NON-NLS-1$ //$NON-NLS-2$
		defaultProperties.setProperty("oldfashioned-import", new Boolean(false).toString()); //$NON-NLS-1$
		/*
		 * Hilfetexte für die einzelnen Tabs
		 */
		defaultProperties
						.setProperty("settlement.help",
										"Durchführung einer Auswertung\n\n- Wählen Sie die Kasse(n), die in der Auswertung berücksichtigt werden soll(en).\n\n- Wählen Sie den gewünschten Datumsbereich aus\n\n- Wählen Sie die gewünschte Art der Auswertung (selektieren Sie den entsprechenden Tab)\n\n- Wählen Sie die gewünschten Optionen\n\nStarten Sie die Auswertung\n\n\n\n- Einzelne Abschlüsse\n\nWählen Sie aus der Dropdownliste den gewünschten Abschluss aus.\n\n- Datumsbereich\n\nMit aktivierter Checkbox \"Inklusive &Liste der stornierten Belege\" werden die stornierten Belege berücksichtigt.\n\n- Abschlüsse über Periode\n\nWählen Sie gewünschte Auswertung:\n\n- Kassenabschluss gewählte Periode\nKumuliert die Abschlüsse des gewählten Datumsbereichs. Mit aktivierter Checkbox werden sämtliche verfügbaren Informationen der Abschlüsse berücksichtigt.\n\n- Differenzliste\nDie Differenzliste weist Differenzen der Abschlüsse über die gewählte Periode aus. Diese Auswertung eignet sich nur für Perioden, für die gespeicherte Abschlüsse vorhanden sind.\n\n- Rabattliste\nDiese Auswertung zeigt eine Statistik der Rabatte im angegebenen Zeitraum. Mit aktivierter Checkbox \"Nur Positionen mit Rabatt berücksichtigen\" werden nur Positionen berücksichtigt, welche einen Rabatt haben.");
		defaultProperties
						.setProperty("product.group.help",
										"So f\u00FChren Sie eine Warengruppenauswertung durch:\n\n- W\u00E4hlen Sie die Kassen, deren Belege in der Auswertung ber\u00FCcksichtigt werden sollen\n- geben Sie den Zeitraum an, f\u00FCr den die Auswertung gelten soll\n- W\u00E4hlen Sie Optionen nach Ihrem Wunsch:\n-- Auswertung bezogen auf Gesamtumsatz: Prozentzahlen beziehen sich auf die Gesamtumsatzzahlen\n-- Auswertung bezogen auf Warengruppenumerationsatz: die Prozentzahlen beziehen sich auf die jeweilige Warengruppe\n\nDie \u00FCbrigen Optionen sind selbsterkl\u00E4rend.");
		defaultProperties
						.setProperty("receipt.statistics.help",
										"So f\u00FChren Sie eine Belegstatistik durch:\n\n- W\u00E4hlen Sie die Kassen, deren Belege in der Auswertung ber\u00FCcksichtigt werden sollen\n- geben Sie den Zeitraum an, f\u00FCr den die Auswertung gelten soll.\n\n- Eine Belegstatistik gruppiert nach Jahren verdichtet die Werte f\u00FCr jede Kasse innerhalb eines Jahres und liefert Informationen \u00FCber die Entwicklung \u00FCber die Jahre.\n- Eine Belegstatistik gruppiert nach Kassen verdichtet die Werte jeden Jahres einer Kasse und liefert Informationen \u00FCber die Entwicklung der einzelnen Kassen.");
		defaultProperties
						.setProperty("day.hour.help",
										"Die Tagesauswertung liefert Informationen \u00FCber die Ums\u00E4tze im Tagesverlauf einer oder mehrerer Kassen \u00FCber einen frei gew\u00E4hlten Zeitraum hinweg.\n\nSo f\u00FChren Sie eine Tagesauswertung durch:\n\n- W\u00E4hlen Sie die Kassen, deren Belege in der Auswertung ber\u00FCcksichtigt werden sollen\n- geben Sie den Zeitraum an, f\u00FCr den die Auswertung gelten soll.");
		defaultProperties
						.setProperty("tax.account.help",
										"Die Mehrwertsteuer wird eher selten für die Mehrwertsteuerkontrolle benötigt.\n\n- W\u00E4hlen Sie die Kassen, deren Belege in der Auswertung ber\u00FCcksichtigt werden sollen\n- geben Sie den Zeitraum an, f\u00FCr den die Auswertung gelten soll.\n\nStarten Sie anschliessend den Druck.");
		defaultProperties.setProperty("receipt.list.help", "");
		defaultProperties
						.setProperty("bookkeeping.transfer.help",
										"Um Belege in die Finanzbuchhaltung zu transferieren gehen Sie wie folgt vor: \n\nW\u00E4hlen Sie in der Liste links die Kassen, deren Belege transferiert werden sollen\nLegen Sie den Zeitraum (von Datum bis und mit Datum) fest, f\u00FCr den die Belege transferiert werden sollen.\n\nDie vom System vorgeschlagene Transaktionsnummer k\u00F6nnen akzeptieren (empfohlen) oder selber setzen. Achten Sie im zweiten Falle darauf, dass diese in der Finanzbuchhaltung nicht bereits vergeben ist. \nDie Buchungsnummer wird innerhalb einer Transaktion verwendet und identifiziert die Positionen innerhalb der Transaktion.\n\nVergewissern Sie sich, dass das Exportverzeichnis existiert und starten Sie den Datenexport anschliessend.");
		defaultProperties
						.setProperty("transfer.help",
										"Den Belegtransfer ben\u00F6tigen Sie, um beispielsweise Belege aus einer Filiale zu exportieren und sie im Hauptgesch\u00E4ft einzulesen.\n\nSo f\u00FChren Sie einen Belegtransfer durch:\n\nExport:\n- W\u00E4hlen Sie in der Liste links die Kassen, deren Belege transferiert werden sollen\n- Legen Sie den Zeitraum (von Datum bis und mit Datum) fest, f\u00FCr den die Belege transferiert werden sollen\n- Stellen Sie sicher, dass Sie ein g\u00FCltiges Exportverzeichnis ausgew\u00E4hlt haben.\n\nImport\nW\u00E4hlen Sie importieren und geben Sie den Importpfad an. Sollen Belege aus der alten Colibri-Kasse importiert werden, so aktivieren Sie die Checkbox \"Importdateien altes Format (alte Colibri-Version)\".");
		/*
		 * Dann die aktuellen aus der Datei statistics.ini...
		 */
		Main.properties = new Properties(defaultProperties);
		File file = new File(Path.getInstance().statisticsIniFile);
		
		try
		{
			FileInputStream in = new FileInputStream(file);
			Main.properties.load(in);
			Main.properties.setProperty(
							"settlement.help",
							"- Wählen Sie die Kasse(n), die in der Auswertung berücksichtigt werden soll(en).\n- Wählen Sie den gewünschten Datumsbereich aus\n- Wählen Sie die gewünschte Art der Auswertung (selektieren Sie den entsprechenden Tab)\n- Wählen Sie die gewünschten Optionen\n\n\n- Einzelne Abschlüsse\nWählen Sie aus der Dropdownliste den gewünschten Abschluss aus.\n\n- Datumsbereich\nMit aktivierter Checkbox \"Inklusive &Liste der stornierten Belege\" werden die stornierten Belege berücksichtigt.\n\n- Abschlüsse über Periode\nBitte beachten Sie: Diese Auswertung kumuliert die Abschlüsse des fraglichen Zeitraums. Diese Auswertung muss nicht zwingend mit der entsprechenden Auswertung \"Datumsbereich\" übereinstimmen. Als Stichdatum wird hier das Datum der Abschlüsse verwendet, nicht der Belege.\n\nWählen Sie gewünschte Auswertung:\n\n-- Kassenabschluss gewählte Periode\nKumuliert die Abschlüsse des gewählten Datumsbereichs. Mit aktivierter Checkbox werden sämtliche verfügbaren Informationen der Abschlüsse berücksichtigt.\n-- Differenzliste\nDie Differenzliste weist Differenzen der Abschlüsse über die gewählte Periode aus. Diese Auswertung eignet sich nur für Perioden, für die gespeicherte Abschlüsse vorhanden sind.\n-- Rabattliste\nDiese Auswertung zeigt eine Statistik der Rabatte im angegebenen Zeitraum. Mit aktivierter Checkbox \"Nur Positionen mit Rabatt berücksichtigen\" werden nur Positionen berücksichtigt, welche einen Rabatt haben.");
			
		}
		catch (FileNotFoundException e)
		{
		}
		catch (IOException e)
		{
			org.eclipse.jface.dialogs.MessageDialog
							.openError(null,
											Messages.getString("Main.Dateizugrifffehler_56"), //$NON-NLS-1$
											Messages.getString("Main.Die_Datei___statistics.properties___im_Verzeichnis__57") + Path.getInstance().DIR_CFG + Messages.getString("Main._konnte_nicht_gelesen_werden._Das_Programm_wird_beendet._58")); //$NON-NLS-1$ //$NON-NLS-2$
			throw e;
		}
		
	}
	
	private void saveProperties()
	{
		/*
		 * Position and size properties
		 */
		Main.properties.setProperty("locationX", new Integer(this.getShell().getLocation().x).toString()); //$NON-NLS-1$
		Main.properties.setProperty("locationY", new Integer(this.getShell().getLocation().y).toString()); //$NON-NLS-1$
		Main.properties.setProperty("sizeX", new Integer(this.getShell().getSize().x).toString()); //$NON-NLS-1$
		Main.properties.setProperty("sizeY", new Integer(this.getShell().getSize().y).toString()); //$NON-NLS-1$
		/*
		 * Salespoint properties
		 */
		Main.properties.setProperty(
						"consolidated", String.valueOf(this.salespointComposite.areAllSalespointsSelected())); //$NON-NLS-1$
		Main.properties.setProperty(
						"salespoints", this.salespointComposite.getSalespointPropertyFromList(this.salespointComposite.getSelectedSalespoints())); //$NON-NLS-1$
		/*
		 * Daterange properties
		 */
		DateFormat df = new SimpleDateFormat();
		Main.properties.setProperty("fromDate", df.format(this.dateRangeGroup.getFromDate())); //$NON-NLS-1$
		Main.properties.setProperty("toDate", df.format(this.dateRangeGroup.readToDate())); //$NON-NLS-1$
		/*
		 * Print destination properties
		 */
		Main.properties.setProperty("destination", Integer.toString(this.printDestinationGroup.getDestination())); //$NON-NLS-1$
		Main.properties.setProperty("filetype", Integer.toString(this.printDestinationGroup.getFileType())); //$NON-NLS-1$
		/*
		 * Current tab properties
		 */
		Main.properties.setProperty("currentTab", Integer.toString(this.tf.getSelectionIndex())); //$NON-NLS-1$
		/*
		 * settlements
		 */
		Main.properties.setProperty("settlement-type", Integer.toString(this.settlementComposite.getSettlementType())); //$NON-NLS-1$
		Main.properties.setProperty("date.report.selection",
						Integer.toString(this.settlementComposite.getSelectedDateReport()));
		Main.properties.setProperty("date.report.full-selection",
						Boolean.toString(this.settlementComposite.getDateComposite().getFullSelection()));
		Main.properties.setProperty("discount-list.only-with-discounts",
						Boolean.toString(this.settlementComposite.getOnlyWithDiscounts()));
		
		Main.properties.setProperty("receipt.report.full-selection",
						Boolean.toString(this.settlementComposite.getReceiptComposite().getFullSelection()));
		/*
		 * Import/Export properties
		 */
		Main.properties.setProperty("transfer-type", this.transferComposite.getType()); //$NON-NLS-1$
		Main.properties.setProperty("export-path", this.transferComposite.getExportPath()); //$NON-NLS-1$
		Main.properties.setProperty("import-path", this.transferComposite.getImportPath()); //$NON-NLS-1$
		Main.properties.setProperty(
						"oldfashioned-import", Boolean.toString(this.transferComposite.getOldFashionedColibri())); //$NON-NLS-1$
		/*
		 * Fibuexport properties
		 */
		Main.properties.setProperty("fibu-export-path", this.bookkeepingTransferComposite.getExportDirectory()); //$NON-NLS-1$
		Main.properties.setProperty("pattern", Messages.getString("Main.dd.MM.yyyy_72")); //$NON-NLS-1$ //$NON-NLS-2$
		Main.properties.setProperty("group", "P"); //$NON-NLS-1$ //$NON-NLS-2$
		Main.properties.setProperty("mType", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		Main.properties.setProperty("origin", "3"); //$NON-NLS-1$ //$NON-NLS-2$
		Main.properties.setProperty("booking-id", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		
		File file = new File(Path.getInstance().statisticsIniFile);
		FileOutputStream out;
		try
		{
			if (!file.exists())
			{
				file.createNewFile();
			}
			out = new FileOutputStream(file);
			Main.properties.store(out, null);
			out.close();
		}
		catch (FileNotFoundException e)
		{
		}
		catch (IOException e)
		{
			org.eclipse.jface.dialogs.MessageDialog
							.openError(null,
											Messages.getString("Main.Dateizugriffsfehler_81"), //$NON-NLS-1$
											Messages.getString("Main.Die_Eigenschaftendatei___statistics.ini___im_Verzeichnis__82") + Path.getInstance().DIR_CFG + Messages.getString("Main._konnte_nicht_aktualisiert_werden._83") //$NON-NLS-1$ //$NON-NLS-2$
							);
		}
	}
	
	public TabFolder getTabFolder()
	{
		return this.tf;
	}
	
	public String getProperty(String key)
	{
		return Main.properties.getProperty(key);
	}
	
	private static Main me;
	private static Properties properties;
	
	private SalespointComposite salespointComposite;
	private DateRangeGroup dateRangeGroup;
	private PrintDestinationGroup printDestinationGroup;
	private TabFolder tf;
	private SettlementComposite settlementComposite;
	private ProductGroupComposite productGroupComposite;
	private ReceiptStatisticsComposite receiptStatisticsComposite;
	private DayHourComposite dayHourComposite;
	private ReceiptTransferComposite transferComposite;
	private BookkeepingTransferComposite bookkeepingTransferComposite;
	private TaxAccountComposite taxAccountComposite;
	private ReceiptListComposite receiptListComposite;
	private Composite buttonBar;
	private Button receiptButton = null;
	private Button receiptListButton = null;
	private Button printButton;
	private Button cancelButton;
}
