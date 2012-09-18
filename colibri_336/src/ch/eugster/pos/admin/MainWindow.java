/*
 * Created on 13.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.logging.LogManager;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import ch.eugster.pos.Constants;
import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.area.coin.CoinActionProvider;
import ch.eugster.pos.admin.area.coin.CoinCurrencyFieldEditorPage;
import ch.eugster.pos.admin.area.coin.CoinCurrencyStore;
import ch.eugster.pos.admin.area.coin.CoinFieldEditorPage;
import ch.eugster.pos.admin.area.coin.CoinStore;
import ch.eugster.pos.admin.area.coin.CoinTreeContentProvider;
import ch.eugster.pos.admin.area.coin.CoinTreeLabelProvider;
import ch.eugster.pos.admin.area.coin.CoinViewerSorter;
import ch.eugster.pos.admin.area.currency.CurrencyActionProvider;
import ch.eugster.pos.admin.area.currency.CurrencyFieldEditorPage;
import ch.eugster.pos.admin.area.currency.CurrencyStore;
import ch.eugster.pos.admin.area.currency.CurrencyTableContentProvider;
import ch.eugster.pos.admin.area.currency.CurrencyTableLabelProvider;
import ch.eugster.pos.admin.area.currency.CurrencyViewerSorter;
import ch.eugster.pos.admin.area.fixkey.FixKeyActionProvider;
import ch.eugster.pos.admin.area.fixkey.FixKeyFieldEditorPage;
import ch.eugster.pos.admin.area.fixkey.FixKeyGroupFieldEditorPage;
import ch.eugster.pos.admin.area.fixkey.FixKeyGroupStore;
import ch.eugster.pos.admin.area.fixkey.FixKeyLabelProvider;
import ch.eugster.pos.admin.area.fixkey.FixKeyStore;
import ch.eugster.pos.admin.area.fixkey.FixKeyTreeContentProvider;
import ch.eugster.pos.admin.area.key.BlockFieldEditorPage;
import ch.eugster.pos.admin.area.key.BlockStore;
import ch.eugster.pos.admin.area.key.KeyActionProvider;
import ch.eugster.pos.admin.area.key.KeyLabelProvider;
import ch.eugster.pos.admin.area.key.KeyTreeContentProvider;
import ch.eugster.pos.admin.area.key.TabFieldEditorPage;
import ch.eugster.pos.admin.area.key.TabStore;
import ch.eugster.pos.admin.area.option.OptionActionProvider;
import ch.eugster.pos.admin.area.option.OptionFieldEditorPage;
import ch.eugster.pos.admin.area.option.OptionStore;
import ch.eugster.pos.admin.area.option.OptionTableContentProvider;
import ch.eugster.pos.admin.area.option.OptionTableLabelProvider;
import ch.eugster.pos.admin.area.payment.PaymentTypeActionProvider;
import ch.eugster.pos.admin.area.payment.PaymentTypeComparer;
import ch.eugster.pos.admin.area.payment.PaymentTypeFieldEditorPage;
import ch.eugster.pos.admin.area.payment.PaymentTypeGroupFieldEditorPage;
import ch.eugster.pos.admin.area.payment.PaymentTypeGroupStore;
import ch.eugster.pos.admin.area.payment.PaymentTypeStore;
import ch.eugster.pos.admin.area.payment.PaymentTypeTreeContentProvider;
import ch.eugster.pos.admin.area.payment.PaymentTypeTreeLabelProvider;
import ch.eugster.pos.admin.area.product.ProductGroupActionProvider;
import ch.eugster.pos.admin.area.product.ProductGroupChangeWizard;
import ch.eugster.pos.admin.area.product.ProductGroupFieldEditorPage;
import ch.eugster.pos.admin.area.product.ProductGroupStore;
import ch.eugster.pos.admin.area.product.ProductGroupTableContentProvider;
import ch.eugster.pos.admin.area.product.ProductGroupTableLabelProvider;
import ch.eugster.pos.admin.area.salespoint.SalespointActionProvider;
import ch.eugster.pos.admin.area.salespoint.SalespointFieldEditorPage;
import ch.eugster.pos.admin.area.salespoint.SalespointStockFieldEditorPage;
import ch.eugster.pos.admin.area.salespoint.SalespointStockStore;
import ch.eugster.pos.admin.area.salespoint.SalespointStore;
import ch.eugster.pos.admin.area.salespoint.SalespointTreeContentProvider;
import ch.eugster.pos.admin.area.salespoint.SalespointTreeLabelProvider;
import ch.eugster.pos.admin.area.tax.CurrentTaxFieldEditorPage;
import ch.eugster.pos.admin.area.tax.CurrentTaxStore;
import ch.eugster.pos.admin.area.tax.TaxActionProvider;
import ch.eugster.pos.admin.area.tax.TaxFieldEditorPage;
import ch.eugster.pos.admin.area.tax.TaxRateFieldEditorPage;
import ch.eugster.pos.admin.area.tax.TaxRateStore;
import ch.eugster.pos.admin.area.tax.TaxStore;
import ch.eugster.pos.admin.area.tax.TaxTreeContentProvider;
import ch.eugster.pos.admin.area.tax.TaxTreeLabelProvider;
import ch.eugster.pos.admin.area.tax.TaxTypeFieldEditorPage;
import ch.eugster.pos.admin.area.tax.TaxTypeStore;
import ch.eugster.pos.admin.area.user.UserActionProvider;
import ch.eugster.pos.admin.area.user.UserFieldEditorPage;
import ch.eugster.pos.admin.area.user.UserStore;
import ch.eugster.pos.admin.area.user.UserTableContentProvider;
import ch.eugster.pos.admin.area.user.UserTableLabelProvider;
import ch.eugster.pos.admin.gui.container.SashForm;
import ch.eugster.pos.admin.gui.container.TableSashForm;
import ch.eugster.pos.admin.gui.container.TreeSashForm;
import ch.eugster.pos.admin.gui.widget.FieldEditorPage;
import ch.eugster.pos.admin.gui.widget.IPageContainer;
import ch.eugster.pos.admin.gui.widget.Page;
import ch.eugster.pos.admin.preference.Code128DataFieldEditorPreferencePage;
import ch.eugster.pos.admin.preference.Code128FieldEditorPreferencePage;
import ch.eugster.pos.admin.preference.ComServerFieldEditorPreferencePage;
import ch.eugster.pos.admin.preference.ComServerGalileoFieldEditorPreferencePage;
import ch.eugster.pos.admin.preference.ConnectionFieldEditorPreferencePage;
import ch.eugster.pos.admin.preference.CurrencyFieldEditorPreferencePage;
import ch.eugster.pos.admin.preference.DatabaseFieldEditorPreferencePage;
import ch.eugster.pos.admin.preference.DefaultInputFieldEditorPreferencePage;
import ch.eugster.pos.admin.preference.DetailBlockFieldEditorPreferencePage;
import ch.eugster.pos.admin.preference.DisplayFieldEditorPreferencePage;
import ch.eugster.pos.admin.preference.DisplayTextFieldEditorPreferencePage;
import ch.eugster.pos.admin.preference.LayoutFieldEditorPreferencePage;
import ch.eugster.pos.admin.preference.LocaleFieldEditorPreferencePage;
import ch.eugster.pos.admin.preference.LoggingFieldEditorPreferencePage;
import ch.eugster.pos.admin.preference.PanelFieldEditorPreferencePage;
import ch.eugster.pos.admin.preference.PeripheryFieldEditorPreferencePage;
import ch.eugster.pos.admin.preference.PreferenceDialog;
import ch.eugster.pos.admin.preference.PreferenceStore;
import ch.eugster.pos.admin.preference.ReceiptCustomerFieldEditorPreferencePage;
import ch.eugster.pos.admin.preference.ReceiptFieldEditorPreferencePage;
import ch.eugster.pos.admin.preference.ReceiptFooterFieldEditorPreferencePage;
import ch.eugster.pos.admin.preference.ReceiptHeaderFieldEditorPreferencePage;
import ch.eugster.pos.admin.preference.SalespointFieldEditorPreferencePage;
import ch.eugster.pos.admin.preference.SettlementFieldEditorPreferencePage;
import ch.eugster.pos.admin.preference.SummaryFieldEditorPreferencePage;
import ch.eugster.pos.admin.preference.TabFieldEditorPreferencePage;
import ch.eugster.pos.admin.preference.VoucherFieldEditorPreferencePage;
import ch.eugster.pos.db.Block;
import ch.eugster.pos.db.Coin;
import ch.eugster.pos.db.CurrentTax;
import ch.eugster.pos.db.FixKey;
import ch.eugster.pos.db.FixKeyGroup;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.Option;
import ch.eugster.pos.db.PaymentType;
import ch.eugster.pos.db.PaymentTypeGroup;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.db.Stock;
import ch.eugster.pos.db.Tab;
import ch.eugster.pos.db.Tax;
import ch.eugster.pos.db.TaxRate;
import ch.eugster.pos.db.TaxType;
import ch.eugster.pos.db.User;
import ch.eugster.pos.db.Version;
import ch.eugster.pos.events.UserChangeEvent;
import ch.eugster.pos.events.UserChangeListener;
import ch.eugster.pos.product.Code128;
import ch.eugster.pos.product.GalileoProductGroupServer;
import ch.eugster.pos.product.GalileoServer;
import ch.eugster.pos.swt.PersistentDBStore;
import ch.eugster.pos.swt.Resources;
import ch.eugster.pos.swt.UserVerificationDialog;
import ch.eugster.pos.swt.UserVerificationInputValidator;
import ch.eugster.pos.util.Config;
import ch.eugster.pos.util.Path;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class MainWindow extends ApplicationWindow implements IPageContainer, SelectionListener, UserChangeListener
{
	
	private static final String TITLE = Messages.getString("MainWindow.Colibri_TS_Administrator_1"); //$NON-NLS-1$
	
	private TabFolder mainFolder = null;
	
	private PersistentDBStore currentStore = null;
	private SashForm currentControl;
	
	/**
	 * Collection of buttons created by the <code>createButton</code> method.
	 */
	private HashMap buttons = new HashMap();
	
	/**
	 * Font metrics to use for determining pixel sizes.
	 */
	private FontMetrics fontMetrics;
	
	/**
	 * Number of horizontal dialog units per character, value <code>4</code>.
	 */
	private static final int HORIZONTAL_DIALOG_UNIT_PER_CHAR = 4;
	
	/**
	 * Number of vertical dialog units per character, value <code>8</code>.
	 */
	private static final int VERTICAL_DIALOG_UNITS_PER_CHAR = 8;
	
	private static final Point dimension = new Point(800, 600);
	
	/**
	 * @param parentShell
	 */
	public MainWindow(Shell parentShell)
	{
		super(parentShell);
		this.addMenuBar();
		this.addToolBar(0);
		this.addStatusLine();
		User.addUserChangeListener(this);
	}
	
	protected Control createContents(Composite parent)
	{
		this.loadProperties();
		int x = Integer.parseInt(this.properties.getProperty("window.width"));
		int y = Integer.parseInt(this.properties.getProperty("window.height"));
		
		Rectangle size = this.getShell().getDisplay().getClientArea();
		if (size.width < x) x = size.width;
		if (size.height < y) y = size.height;
		MainWindow.dimension.x = x;
		MainWindow.dimension.y = y;
		
		Resources.initialize();
		
		GC gc = new GC(parent);
		gc.setFont(parent.getFont());
		this.fontMetrics = gc.getFontMetrics();
		gc.dispose();
		
		this.createMenus();
		
		Composite c = new Composite(parent, SWT.FLAT);
		c.setLayout(new GridLayout());
		
		this.mainFolder = this.createTabFolderArea(c);
		
		// create the dialog area and button bar
		this.createButtonBar(c);
		
		TabItem item = new TabItem(this.mainFolder, SWT.NONE);
		item.setControl(this.createProductGroupControl());
		item.setControl(this.productGroupTableSash);
		item.setText(Messages.getString("MainWindow.Warengruppen_2")); //$NON-NLS-1$
		
		item = new TabItem(this.mainFolder, SWT.NONE);
		item.setControl(this.createPaymentTypeControl());
		item.setText(Messages.getString("MainWindow.Zahlungsarten_3")); //$NON-NLS-1$
		
		item = new TabItem(this.mainFolder, SWT.NONE);
		item.setControl(this.createTaxControl());
		item.setText(Messages.getString("MainWindow.Mehrwertsteuer_4")); //$NON-NLS-1$
		
		item = new TabItem(this.mainFolder, SWT.NONE);
		item.setControl(this.createOptionControl());
		item.setText(Messages.getString("MainWindow.Optionscodes_5")); //$NON-NLS-1$
		
		item = new TabItem(this.mainFolder, SWT.NONE);
		item.setControl(this.createSalespointControl());
		item.setText(Messages.getString("MainWindow.Kassen_6")); //$NON-NLS-1$
		
		item = new TabItem(this.mainFolder, SWT.NONE);
		item.setControl(this.createCoinControl());
		item.setText(Messages.getString("MainWindow.Muenzen_7")); //$NON-NLS-1$
		
		item = new TabItem(this.mainFolder, SWT.NONE);
		item.setControl(this.createCurrencyControl());
		item.setText(Messages.getString("MainWindow.Waehrungen_8")); //$NON-NLS-1$
		
		item = new TabItem(this.mainFolder, SWT.NONE);
		item.setControl(this.createUserControl());
		item.setText(Messages.getString("MainWindow.Benutzer_9")); //$NON-NLS-1$
		
		item = new TabItem(this.mainFolder, SWT.NONE);
		item.setControl(this.createKeyControl());
		item.setControl(this.keyTreeSash);
		item.setText(Messages.getString("MainWindow.Tastenbelegung_10")); //$NON-NLS-1$
		
		item = new TabItem(this.mainFolder, SWT.NONE);
		item.setControl(this.createFixKeyControl());
		item.setText(Messages.getString("MainWindow.Fixtasten_11")); //$NON-NLS-1$
		
		PaymentTypeStore.getInstance().addPaymentTypeChangeListener(CoinTreeContentProvider.getInstance());
		PaymentTypeStore.getInstance().addPaymentTypeChangeListener(CurrencyTableContentProvider.getInstance());
		this.paymentTypeTreeSash.addListener(SWT.Selection, SalespointTreeContentProvider.getInstance());
		
		this.mainFolder.setSelection(0);
		this.setCurrentTabItem(this.mainFolder.getSelection()[0]);
		this.currentControl.getViewer().getControl().setFocus();
		this.mainFolder.addSelectionListener(this);
		
		this.setModes(null, User.getCurrentUser());
		
		return parent;
	}
	
	private Control createProductGroupControl()
	{
		this.productGroupTableSash = new TableSashForm(this.mainFolder, SWT.HORIZONTAL, "productGroup"); //$NON-NLS-1$
		int[] weights = new int[]
		{ new Integer(this.properties.getProperty("productgroup.left")).intValue(),
						new Integer(this.properties.getProperty("productgroup.right")).intValue() };
		this.productGroupTableSash.setWeights(weights);
		Page page = new ProductGroupFieldEditorPage(ProductGroup.class.getName(), Messages
						.getString("MainWindow.Warengruppen_12"), FieldEditorPage.GRID, new ProductGroupStore()); //$NON-NLS-1$
		page.setContainer(this);
		this.productGroupTableSash.addPage(ProductGroup.class.getName(), page);
		this.productGroupTableSash.initialize(new ProductGroupTableContentProvider(),
						new ProductGroupTableLabelProvider());
		this.productGroupTableSash.setActionProvider(new ProductGroupActionProvider(this.productGroupTableSash));
		this.productGroupTableSash.updateMessage();
		this.productGroupTableSash.getViewer().getControl().setFocus();
		this.senders.put(this.productGroupTableSash.getData("identity"), this.productGroupTableSash); //$NON-NLS-1$
		return this.productGroupTableSash;
	}
	
	private Control createPaymentTypeControl()
	{
		this.paymentTypeTreeSash = new TreeSashForm(this.mainFolder, SWT.HORIZONTAL, "paymentType"); //$NON-NLS-1$
		int[] weights = new int[]
		{ new Integer(this.properties.getProperty("paymenttype.left")).intValue(),
						new Integer(this.properties.getProperty("paymenttype.right")).intValue() };
		this.paymentTypeTreeSash.setWeights(weights);
		Page page1 = new PaymentTypeFieldEditorPage(PaymentType.class.getName(), Messages
						.getString("MainWindow.Zahlungsarten_13"), FieldEditorPage.GRID, PaymentTypeStore.getInstance()); //$NON-NLS-1$
		page1.setContainer(this);
		this.paymentTypeTreeSash.addPage(PaymentType.class.getName(), page1);
		Page page2 = new PaymentTypeGroupFieldEditorPage(
						PaymentTypeGroup.class.getName(),
						Messages.getString("MainWindow.Zahlungsartengruppen_14"), FieldEditorPage.GRID, new PaymentTypeGroupStore()); //$NON-NLS-1$
		page2.setContainer(this);
		this.paymentTypeTreeSash.addPage(PaymentTypeGroup.class.getName(), page2);
		this.paymentTypeTreeSash.initialize(new PaymentTypeTreeContentProvider(), new PaymentTypeTreeLabelProvider(),
						new PaymentTypeComparer());
		this.paymentTypeTreeSash.setActionProvider(new PaymentTypeActionProvider(this.paymentTypeTreeSash));
		this.paymentTypeTreeSash.updateMessage();
		this.paymentTypeTreeSash.getViewer().getControl().setFocus();
		this.senders.put(this.paymentTypeTreeSash.getData("identity"), this.paymentTypeTreeSash); //$NON-NLS-1$
		return this.paymentTypeTreeSash;
	}
	
	private Control createTaxControl()
	{
		this.taxTreeSash = new TreeSashForm(this.mainFolder, SWT.HORIZONTAL, "tax"); //$NON-NLS-1$
		int[] weights = new int[]
		{ new Integer(this.properties.getProperty("tax.left")).intValue(),
						new Integer(this.properties.getProperty("tax.right")).intValue() };
		this.taxTreeSash.setWeights(weights);
		Page page1 = new CurrentTaxFieldEditorPage(CurrentTax.class.getName(), Messages
						.getString("MainWindow.Mehrwertsteuerdetail_15"), FieldEditorPage.GRID, new CurrentTaxStore()); //$NON-NLS-1$
		page1.setContainer(this);
		page1.isAddActive = false;
		page1.isRemoveActive = false;
		this.taxTreeSash.addPage(CurrentTax.class.getName(), page1);
		Page page2 = new TaxFieldEditorPage(Tax.class.getName(),
						Messages.getString("MainWindow.Mehrwertsteuer_16"), FieldEditorPage.GRID, new TaxStore()); //$NON-NLS-1$
		page2.setContainer(this);
		page2.isAddActive = false;
		page2.isRemoveActive = false;
		this.taxTreeSash.addPage(Tax.class.getName(), page2);
		Page page3 = new TaxRateFieldEditorPage(TaxRate.class.getName(), Messages
						.getString("MainWindow.Mehrwertsteuergruppe_17"), FieldEditorPage.GRID, new TaxRateStore()); //$NON-NLS-1$
		page3.setContainer(this);
		page3.isAddActive = false;
		page3.isRemoveActive = false;
		this.taxTreeSash.addPage(TaxRate.class.getName(), page3);
		Page page4 = new TaxTypeFieldEditorPage(TaxType.class.getName(), Messages
						.getString("MainWindow.Mehrwertsteuerart_18"), FieldEditorPage.GRID, new TaxTypeStore()); //$NON-NLS-1$
		page4.setContainer(this);
		page4.isAddActive = false;
		page4.isRemoveActive = false;
		this.taxTreeSash.addPage(TaxType.class.getName(), page4);
		this.taxTreeSash.initialize(new TaxTreeContentProvider(), new TaxTreeLabelProvider());
		this.taxTreeSash.setActionProvider(new TaxActionProvider(this.taxTreeSash));
		this.taxTreeSash.updateMessage();
		this.taxTreeSash.getViewer().getControl().setFocus();
		this.senders.put(this.taxTreeSash.getData("identity"), this.taxTreeSash); //$NON-NLS-1$
		return this.taxTreeSash;
	}
	
	private Control createOptionControl()
	{
		this.optionTableSash = new TableSashForm(this.mainFolder, SWT.HORIZONTAL, "option"); //$NON-NLS-1$
		int[] weights = new int[]
		{ new Integer(this.properties.getProperty("option.left")).intValue(),
						new Integer(this.properties.getProperty("option.right")).intValue() };
		this.optionTableSash.setWeights(weights);
		Page page = new OptionFieldEditorPage(Option.class.getName(),
						Messages.getString("MainWindow.Optionscodes_19"), FieldEditorPage.GRID, new OptionStore()); //$NON-NLS-1$
		page.setContainer(this);
		this.optionTableSash.addPage(Option.class.getName(), page);
		this.optionTableSash.initialize(new OptionTableContentProvider(), new OptionTableLabelProvider());
		this.optionTableSash.setActionProvider(new OptionActionProvider(this.optionTableSash));
		this.optionTableSash.updateMessage();
		this.optionTableSash.getViewer().getControl().setFocus();
		this.senders.put(this.optionTableSash.getData("identity"), this.optionTableSash); //$NON-NLS-1$
		return this.optionTableSash;
	}
	
	private Control createSalespointControl()
	{
		this.salespointTreeSash = new TreeSashForm(this.mainFolder, SWT.HORIZONTAL, "salespoint");
		int[] weights = new int[]
		{ new Integer(this.properties.getProperty("salespoint.left")).intValue(),
						new Integer(this.properties.getProperty("salespoint.right")).intValue() };
		this.salespointTreeSash.setWeights(weights);
		Page page1 = new SalespointFieldEditorPage(Salespoint.class.getName(), Messages
						.getString("MainWindow.Kassen_20"), FieldEditorPage.GRID, new SalespointStore()); //$NON-NLS-1$
		page1.setContainer(this);
		page1.isAddActive = true;
		page1.isRemoveActive = true;
		this.salespointTreeSash.addPage(Salespoint.class.getName(), page1);
		
		Page page2 = new SalespointStockFieldEditorPage(Stock.class.getName(),
						"Kassenstock", FieldEditorPage.GRID, new SalespointStockStore()); //$NON-NLS-1$
		page2.setContainer(this);
		page2.isAddActive = false;
		page2.isRemoveActive = false;
		this.salespointTreeSash.addPage(Stock.class.getName(), page2);
		
		SalespointTreeContentProvider contentProvider = SalespointTreeContentProvider.getInstance();
		contentProvider.setViewer(this.salespointTreeSash.getViewer());
		this.salespointTreeSash.initialize(contentProvider, new SalespointTreeLabelProvider());
		this.salespointTreeSash.setActionProvider(new SalespointActionProvider(this.salespointTreeSash));
		this.salespointTreeSash.updateMessage();
		this.salespointTreeSash.getViewer().getControl().setFocus();
		this.senders.put(this.salespointTreeSash.getData("identity"), this.salespointTreeSash); //$NON-NLS-1$
		
		return this.salespointTreeSash;
	}
	
	private Control createCoinControl()
	{
		
		this.coinTreeSash = new TreeSashForm(this.mainFolder, SWT.HORIZONTAL, "coin"); //$NON-NLS-1$
		int[] weights = new int[]
		{ new Integer(this.properties.getProperty("paymenttype.left")).intValue(),
						new Integer(this.properties.getProperty("paymenttype.right")).intValue() };
		this.coinTreeSash.setWeights(weights);
		
		Page page1 = new CoinCurrencyFieldEditorPage(ForeignCurrency.class.getName(),
						"Währung", FieldEditorPage.GRID, new CoinCurrencyStore()); //$NON-NLS-1$
		page1.setContainer(this);
		page1.isAddActive = true;
		page1.isRemoveActive = false;
		this.coinTreeSash.addPage(ForeignCurrency.class.getName(), page1);
		
		Page page2 = new CoinFieldEditorPage(Coin.class.getName(),
						Messages.getString("MainWindow.Muenzen_21"), FieldEditorPage.GRID, new CoinStore()); //$NON-NLS-1$
		page2.setContainer(this);
		page2.isAddActive = false;
		page2.isRemoveActive = true;
		this.coinTreeSash.addPage(Coin.class.getName(), page2);
		
		CoinTreeContentProvider contentProvider = CoinTreeContentProvider.getInstance();
		contentProvider.setViewer(this.coinTreeSash.getViewer());
		this.coinTreeSash.getViewer().setSorter(new CoinViewerSorter());
		this.coinTreeSash.initialize(contentProvider, new CoinTreeLabelProvider());
		this.coinTreeSash.setActionProvider(new CoinActionProvider(this.coinTreeSash));
		this.coinTreeSash.updateMessage();
		this.coinTreeSash.getViewer().getControl().setFocus();
		return this.coinTreeSash;
	}
	
	private Control createCurrencyControl()
	{
		this.currencyTableSash = new TableSashForm(this.mainFolder, SWT.HORIZONTAL, "foreignCurrency"); //$NON-NLS-1$
		int[] weights = new int[]
		{ new Integer(this.properties.getProperty("currency.left")).intValue(),
						new Integer(this.properties.getProperty("currency.right")).intValue() };
		this.currencyTableSash.setWeights(weights);
		Page page = new CurrencyFieldEditorPage(ForeignCurrency.class.getName(), Messages
						.getString("MainWindow.Waehrungen_22"), FieldEditorPage.GRID, new CurrencyStore()); //$NON-NLS-1$
		page.setContainer(this);
		this.currencyTableSash.addPage(ForeignCurrency.class.getName(), page);
		
		CurrencyTableContentProvider ctcp = CurrencyTableContentProvider.getInstance();
		ctcp.setViewer(this.currencyTableSash.getViewer());
		CurrencyTableLabelProvider ctlp = new CurrencyTableLabelProvider();
		CurrencyViewerSorter cvs = new CurrencyViewerSorter(0);
		this.currencyTableSash.initialize(ctcp, ctlp, cvs);
		this.currencyTableSash.setActionProvider(new CurrencyActionProvider(this.currencyTableSash));
		this.currencyTableSash.updateMessage();
		this.currencyTableSash.getViewer().getControl().setFocus();
		Enumeration enumerationeration = this.senders.elements();
		while (enumerationeration.hasMoreElements())
		{
			SashForm sf = (SashForm) enumerationeration.nextElement();
			if (sf.getData("identity").equals("paymentType")) { //$NON-NLS-1$ //$NON-NLS-2$
				sf.addListener(SWT.Selection, (CurrencyFieldEditorPage) page);
			}
		}
		return this.currencyTableSash;
	}
	
	private Control createUserControl()
	{
		this.userTableSash = new TableSashForm(this.mainFolder, SWT.HORIZONTAL, "user"); //$NON-NLS-1$
		int[] weights = new int[]
		{ new Integer(this.properties.getProperty("user.left")).intValue(),
						new Integer(this.properties.getProperty("user.right")).intValue() };
		this.userTableSash.setWeights(weights);
		UserFieldEditorPage page = new UserFieldEditorPage(User.class.getName(), Messages
						.getString("MainWindow.Benutzer_23"), FieldEditorPage.GRID, new UserStore()); //$NON-NLS-1$
		page.setContainer(this);
		this.userTableSash.addPage(User.class.getName(), page);
		this.userTableSash.initialize(new UserTableContentProvider(), new UserTableLabelProvider());
		this.userTableSash.setActionProvider(new UserActionProvider(this.userTableSash));
		this.userTableSash.updateMessage();
		this.userTableSash.getViewer().getControl().setFocus();
		return this.userTableSash;
	}
	
	private Control createKeyControl()
	{
		this.keyTreeSash = new TreeSashForm(this.mainFolder, SWT.HORIZONTAL, "customKey"); //$NON-NLS-1$
		int[] weights = new int[]
		{ new Integer(this.properties.getProperty("key.left")).intValue(),
						new Integer(this.properties.getProperty("key.right")).intValue() };
		this.keyTreeSash.setWeights(weights);
		Page page1 = new BlockFieldEditorPage(Block.class.getName(),
						Messages.getString("MainWindow.Bereich_24"), FieldEditorPage.GRID, new BlockStore()); //$NON-NLS-1$
		page1.setContainer(this);
		page1.isAddActive = false;
		page1.isRemoveActive = false;
		this.keyTreeSash.addPage(Block.class.getName(), page1);
		int left = new Integer(this.properties.getProperty("key.detail.left")).intValue();
		int right = new Integer(this.properties.getProperty("key.detail.right")).intValue();
		weights = new int[]
		{ left, right };
		this.tabFieldEditorPage = new TabFieldEditorPage(Tab.class.getName(), Messages
						.getString("MainWindow.Register_25"), FieldEditorPage.GRID, new TabStore()); //$NON-NLS-1$
		this.tabFieldEditorPage.setContainer(this);
		this.keyTreeSash.addPage(Tab.class.getName(), this.tabFieldEditorPage);
		this.keyTreeSash.initialize(new KeyTreeContentProvider(), new KeyLabelProvider());
		this.keyTreeSash.setActionProvider(new KeyActionProvider(this.keyTreeSash));
		this.keyTreeSash.updateMessage();
		this.keyTreeSash.getViewer().getControl().setFocus();
		Enumeration enumerationeration = this.senders.elements();
		while (enumerationeration.hasMoreElements())
		{
			((SashForm) enumerationeration.nextElement()).addListener(SWT.Selection, this.tabFieldEditorPage
							.getButtonLayoutFieldEditor());
		}
		return this.keyTreeSash;
	}
	
	private Control createFixKeyControl()
	{
		this.fixKeyTreeSash = new TreeSashForm(this.mainFolder, SWT.HORIZONTAL, "fixKey"); //$NON-NLS-1$
		int[] weights = new int[]
		{ new Integer(this.properties.getProperty("fixkey.left")).intValue(),
						new Integer(this.properties.getProperty("fixkey.right")).intValue() };
		this.fixKeyTreeSash.setWeights(weights);
		Page page1 = new FixKeyGroupFieldEditorPage(FixKeyGroup.class.getName(), Messages
						.getString("MainWindow.Bereich_26"), FieldEditorPage.GRID, new FixKeyGroupStore()); //$NON-NLS-1$
		page1.isAddActive = false;
		page1.isRemoveActive = false;
		page1.setContainer(this);
		this.fixKeyTreeSash.addPage(FixKeyGroup.class.getName(), page1);
		Page page2 = new FixKeyFieldEditorPage(FixKey.class.getName(),
						Messages.getString("MainWindow.Fixtasten_27"), FieldEditorPage.GRID, new FixKeyStore()); //$NON-NLS-1$
		page2.isAddActive = false;
		page2.isRemoveActive = false;
		page2.setContainer(this);
		this.fixKeyTreeSash.addPage(FixKey.class.getName(), page2);
		this.fixKeyTreeSash.initialize(new FixKeyTreeContentProvider(), new FixKeyLabelProvider());
		this.fixKeyTreeSash.setActionProvider(new FixKeyActionProvider(this.fixKeyTreeSash));
		this.fixKeyTreeSash.updateMessage();
		this.fixKeyTreeSash.getViewer().getControl().setFocus();
		this.senders.put(this.fixKeyTreeSash.getData("identity"), this.fixKeyTreeSash); //$NON-NLS-1$
		return this.fixKeyTreeSash;
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
								MainWindow.this.getShell(),
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
				MessageDialog.openInformation(MainWindow.getInstance().getShell(), "Version", "Version: "
								+ Version.version() + "\n" + "Datum: " + Version.getVersionDate());
			}
		});
		
		new MenuItem(fileSubMenu, SWT.SEPARATOR);
		
		MenuItem exitItem = new MenuItem(fileSubMenu, SWT.PUSH);
		exitItem.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				MainWindow.this.close();
			}
		});
		exitItem.setText(Messages.getString("MainWindow.&Beenden_tCtrl+B_33")); //$NON-NLS-1$
		exitItem.setAccelerator(SWT.CTRL + 'B');
		
		/*
		 * 2. Menubereich
		 */
		MenuItem areaItem = new MenuItem(bar, SWT.CASCADE);
		areaItem.setText(Messages.getString("MainWindow.&Optionen_34")); //$NON-NLS-1$
		
		Menu areaSubMenu = new Menu(this.getShell(), SWT.DROP_DOWN);
		areaItem.setMenu(areaSubMenu);
		
		MenuItem productGroupItem = new MenuItem(areaSubMenu, SWT.CASCADE);
		productGroupItem.setText("Warengruppen"); //$NON-NLS-1$
		
		Menu productGroupSubMenu = new Menu(this.getShell(), SWT.DROP_DOWN);
		productGroupItem.setMenu(productGroupSubMenu);
		
		MenuItem manageProductGroupsItem = new MenuItem(productGroupSubMenu, SWT.PUSH);
		manageProductGroupsItem.setData(new Integer(0));
		manageProductGroupsItem.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				MainWindow.this.startManageProductGroupWizard();
			}
		});
		manageProductGroupsItem.setText("Warengruppen zuordnen"); //$NON-NLS-1$
		
		MenuItem importProductGroupsItem = new MenuItem(productGroupSubMenu, SWT.PUSH);
		importProductGroupsItem.setData(new Integer(0));
		importProductGroupsItem.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				if (MessageDialog
								.openQuestion(
												MainWindow.this.getShell(),
												Messages.getString("MainWindow.Warengruppen_importieren_1"), Messages.getString("MainWindow.Wollen_Sie_die_Warengruppen_aus_Galileo_importieren__2"))) { //$NON-NLS-1$ //$NON-NLS-2$
					MainWindow.this.importProductGroups();
				}
			}
		});
		importProductGroupsItem.setText(Messages.getString("MainWindow.Warengruppen_&importieren_35")); //$NON-NLS-1$
		
		MenuItem updateProductGroupItems = new MenuItem(productGroupSubMenu, SWT.PUSH);
		updateProductGroupItems.setData(new Integer(1));
		updateProductGroupItems.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				if (MessageDialog.openQuestion(MainWindow.this.getShell(),
								"Warengruppen aktualisieren", "Wollen Sie die Warengruppen aus Galileo aktualisieren?")) { //$NON-NLS-1$ //$NON-NLS-2$
					MainWindow.this.updateProductGroups();
				}
			}
		});
		updateProductGroupItems.setText("Warengruppen &aktualisieren");
		
		/*
		 * Das folgende MenuItem darf nur hinzugefügt werden, wenn der User
		 * Admin-Rechte hat
		 */
		this.updateGalileoItem = new MenuItem(areaSubMenu, SWT.CASCADE);
		this.updateGalileoItem.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				MainWindow.this.updateGalileo();
			}
		});
		this.updateGalileoItem.setText("Galileo aktualisieren...");
		this.updateGalileoItem.setEnabled(User.getCurrentUser().status == User.USER_STATE_ADMINISTRATOR);
		
		this.prefsItem = new MenuItem(bar, SWT.CASCADE);
		this.prefsItem.setText(Messages.getString("MainWindow.&Einstellungen_36")); //$NON-NLS-1$
		this.prefsItem.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				PreferenceDialog dialog = new PreferenceDialog(MainWindow.this.getShell(), new PreferenceManager());
				PreferenceStore store = new PreferenceStore();
				dialog.setPreferenceStore(store);
				/*
				 * Belegtexte
				 */
				PreferencePage page = new ReceiptFieldEditorPreferencePage(
								Messages.getString("MainWindow.Belegstexte"), 1); //$NON-NLS-1$
				page.setPreferenceStore(store);
				dialog.getPreferenceManager().addToRoot(
								new PreferenceNode(Messages.getString("MainWindow.Belegstexte"), page)); //$NON-NLS-1$
				
				page = new ReceiptHeaderFieldEditorPreferencePage("Belegkopf", 1); //$NON-NLS-1$
				page.setPreferenceStore(store);
				dialog.getPreferenceManager().addTo(
								Messages.getString("MainWindow.Belegstexte"), new PreferenceNode("Belegkopf", page)); //$NON-NLS-1$
				
				page = new ReceiptFooterFieldEditorPreferencePage("Belegfuss", 1); //$NON-NLS-1$
				page.setPreferenceStore(store);
				dialog.getPreferenceManager().addTo(
								Messages.getString("MainWindow.Belegstexte"), new PreferenceNode("Belegfuss", page)); //$NON-NLS-1$
				// 10157
				page = new ReceiptCustomerFieldEditorPreferencePage("Kundenkonto", 1); //$NON-NLS-1$
				page.setPreferenceStore(store);
				dialog.getPreferenceManager().addTo(
								Messages.getString("MainWindow.Belegstexte"), new PreferenceNode("Kundenkonto", page)); //$NON-NLS-1$
				// 10157
				/*
				 * Gutschein
				 */
				page = new VoucherFieldEditorPreferencePage("Kopf Rückgeldgutschein", 1); //$NON-NLS-1$
				page.setPreferenceStore(store);
				dialog.getPreferenceManager().addToRoot(new PreferenceNode("Kopf Rückgeldgutschein", page)); //$NON-NLS-1$
				/*
				 * Gutschein
				 */
				page = new SettlementFieldEditorPreferencePage("Tagesabschluss", 1); //$NON-NLS-1$
				page.setPreferenceStore(store);
				dialog.getPreferenceManager().addToRoot(new PreferenceNode("Tagesabschluss", page)); //$NON-NLS-1$
				
				/*
				 * Kundendisplaytexte
				 */
				page = new DisplayTextFieldEditorPreferencePage("Kundendisplaytexte", 1); //$NON-NLS-1$
				page.setPreferenceStore(store);
				dialog.getPreferenceManager().addToRoot(new PreferenceNode("Kundendisplaytexte", page)); //$NON-NLS-1$
				/*
				 * Protokollierung
				 */
				page = new LoggingFieldEditorPreferencePage(Messages.getString("MainWindow.Protokollierung_37"), 1); //$NON-NLS-1$
				page.setPreferenceStore(store);
				dialog.getPreferenceManager().addToRoot(
								new PreferenceNode(Messages.getString("MainWindow.Protokollierung_38"), page)); //$NON-NLS-1$
				/*
				 * Datenbankverbindungen
				 */
				page = new DatabaseFieldEditorPreferencePage(Messages.getString("MainWindow.Datenbankverbindung_39"), 1); //$NON-NLS-1$
				page.setPreferenceStore(store);
				dialog.getPreferenceManager().addToRoot(
								new PreferenceNode(Messages.getString("MainWindow.Datenbankverbindung_40"), page)); //$NON-NLS-1$
				
				page = new ConnectionFieldEditorPreferencePage(
								Messages.getString("MainWindow.Standarddatenbank_41"), 1, "standard"); //$NON-NLS-1$ //$NON-NLS-2$
				page.setPreferenceStore(store);
				dialog
								.getPreferenceManager()
								.addTo(
												Messages.getString("MainWindow.Datenbankverbindung_42"), new PreferenceNode(Messages.getString("MainWindow.Standarddatenbank_43"), page)); //$NON-NLS-1$ //$NON-NLS-2$
				page = new ConnectionFieldEditorPreferencePage(
								Messages.getString("MainWindow.Temporaere_Datenbank_44"), 1, "temporary"); //$NON-NLS-1$ //$NON-NLS-2$
				page.setPreferenceStore(store);
				dialog
								.getPreferenceManager()
								.addTo(
												Messages.getString("MainWindow.Datenbankverbindung_45"), new PreferenceNode(Messages.getString("MainWindow.Temporaere_Datenbank_46"), page)); //$NON-NLS-1$ //$NON-NLS-2$
				page = new ConnectionFieldEditorPreferencePage(
								Messages.getString("MainWindow.Schulungsdatenbank_47"), 1, "tutorial"); //$NON-NLS-1$ //$NON-NLS-2$
				page.setPreferenceStore(store);
				dialog
								.getPreferenceManager()
								.addTo(
												Messages.getString("MainWindow.Datenbankverbindung_48"), new PreferenceNode(Messages.getString("MainWindow.Schulungsdatenbank_49"), page)); //$NON-NLS-1$ //$NON-NLS-2$
				/*
				 * Auswahl Kassenstation
				 */
				page = new SalespointFieldEditorPreferencePage(Messages
								.getString("MainWindow.Auswahl_Kassenstation_50"), 1); //$NON-NLS-1$
				page.setPreferenceStore(store);
				dialog.getPreferenceManager().addToRoot(
								new PreferenceNode(Messages.getString("MainWindow.Auswahl_Kassenstation_51"), page)); //$NON-NLS-1$
				/*
				 * Eingabevorschläge
				 */
				page = new DefaultInputFieldEditorPreferencePage(
								Messages.getString("MainWindow.Eingabevorschlaege_52"), 1); //$NON-NLS-1$
				page.setPreferenceStore(store);
				dialog.getPreferenceManager().addToRoot(
								new PreferenceNode(Messages.getString("MainWindow.Eingabevorschlaege_53"), page)); //$NON-NLS-1$
				/*
				 * ComServer
				 * 
				 * 10126 Der Name der COM-Server PreferencePage scheint nicht
				 * überall korrekt eingesetzt gewesen zu sein, darum wurde die
				 * PreferencePage von Galileo nicht angezeigt.
				 */
				final String comServerName = "COM-Server";
				ComServerFieldEditorPreferencePage comServerPage = new ComServerFieldEditorPreferencePage(
								comServerName, 1);
				comServerPage.setPreferenceStore(store);
				dialog.getPreferenceManager().addToRoot(new PreferenceNode(comServerName, comServerPage));
				/*
				 * Galileo
				 */
				ComServerGalileoFieldEditorPreferencePage galileoPage = new ComServerGalileoFieldEditorPreferencePage(
								Messages.getString("MainWindow.Verbindung_Galileo_101"), 1); //$NON-NLS-1$
				galileoPage.setPreferenceStore(store);
				dialog.getPreferenceManager().addTo(comServerName, new PreferenceNode("Galileo", galileoPage)); //$NON-NLS-1$
				/*
				 * Code128
				 */
				Code128FieldEditorPreferencePage code128Page = new Code128FieldEditorPreferencePage(Messages
								.getString("MainWindow.Code_128"), 1); //$NON-NLS-1$
				code128Page.setPreferenceStore(store);
				dialog.getPreferenceManager().addToRoot(
								new PreferenceNode(Messages.getString("MainWindow.Code_128"), code128Page)); //$NON-NLS-1$
				File folder = new File(Path.getInstance().code128Dir);
				File[] files = folder.listFiles(new FileFilter()
				{
					public boolean accept(File file)
					{
						return file.isFile();
					}
				});
				for (int i = 0; i < files.length; i++)
				{
					Properties p = new Properties();
					try
					{
						p.load(new FileInputStream(files[i]));
						page = new Code128DataFieldEditorPreferencePage(p.getProperty("name"), 1, files[i]); //$NON-NLS-1$
						page.setPreferenceStore(store);
						dialog
										.getPreferenceManager()
										.addTo(
														Messages.getString("MainWindow.Code_128"), new PreferenceNode(Messages.getString("MainWindow.Positionen_60"), page)); //$NON-NLS-1$ //$NON-NLS-2$
						// galileoPage.addPropertyChangeListener(page);
					}
					catch (IOException io)
					{
					}
				}
				/*
				 * Benutzerschnittstelle
				 */
				page = new LayoutFieldEditorPreferencePage(Messages.getString("MainWindow.Benutzerschnittstelle_61"), 1); //$NON-NLS-1$
				page.setPreferenceStore(store);
				dialog.getPreferenceManager().addToRoot(
								new PreferenceNode(Messages.getString("MainWindow.Benutzerschnittstelle_62"), page)); //$NON-NLS-1$
				/*
				 * Lokalisierung
				 */
				page = new LocaleFieldEditorPreferencePage(Messages.getString("MainWindow.Lokalisierung_63"), 1); //$NON-NLS-1$
				page.setPreferenceStore(store);
				dialog.getPreferenceManager().addToRoot(
								new PreferenceNode(Messages.getString("MainWindow.Lokalisierung_64"), page)); //$NON-NLS-1$
				/*
				 * Währung
				 */
				page = new CurrencyFieldEditorPreferencePage(Messages.getString("MainWindow.Waehrung_65"), 1); //$NON-NLS-1$
				page.setPreferenceStore(store);
				dialog.getPreferenceManager().addToRoot(
								new PreferenceNode(Messages.getString("MainWindow.Waehrung_66"), page)); //$NON-NLS-1$
				/*
				 * Erscheinungsbild
				 */
				page = new PanelFieldEditorPreferencePage(Messages.getString("MainWindow.Erscheinungsbild_67"), 1); //$NON-NLS-1$
				page.setPreferenceStore(store);
				dialog.getPreferenceManager().addToRoot(
								new PreferenceNode(Messages.getString("MainWindow.Erscheinungsbild_68"), page)); //$NON-NLS-1$
				
				page = new SummaryFieldEditorPreferencePage(Messages.getString("MainWindow.Summenanzeige_3"), 1); //$NON-NLS-1$
				page.setPreferenceStore(store);
				dialog
								.getPreferenceManager()
								.addTo(
												Messages.getString("MainWindow.Erscheinungsbild_67"), new PreferenceNode(Messages.getString("MainWindow.Summenanzeige_4"), page)); //$NON-NLS-1$ //$NON-NLS-2$
				
				page = new TabFieldEditorPreferencePage(Messages.getString("MainWindow.Registeranzeige_5"), 1); //$NON-NLS-1$
				page.setPreferenceStore(store);
				dialog
								.getPreferenceManager()
								.addTo(
												Messages.getString("MainWindow.Erscheinungsbild_67"), new PreferenceNode(Messages.getString("MainWindow.Registeranzeige_6"), page)); //$NON-NLS-1$ //$NON-NLS-2$
				
				page = new DetailBlockFieldEditorPreferencePage(Messages.getString("MainWindow.Detailanzeige_7"), 1); //$NON-NLS-1$
				page.setPreferenceStore(store);
				dialog
								.getPreferenceManager()
								.addTo(
												Messages.getString("MainWindow.Erscheinungsbild_67"), new PreferenceNode(Messages.getString("MainWindow.Detailanzeige_8"), page)); //$NON-NLS-1$ //$NON-NLS-2$
				
				page = new DisplayFieldEditorPreferencePage(Messages.getString("MainWindow.Displayanzeige_9"), 1); //$NON-NLS-1$
				page.setPreferenceStore(store);
				dialog
								.getPreferenceManager()
								.addTo(
												Messages.getString("MainWindow.Erscheinungsbild_67"), new PreferenceNode(Messages.getString("MainWindow.Displayanzeige_10"), page)); //$NON-NLS-1$ //$NON-NLS-2$
				/*
				 * Peripherie
				 */
				page = new PeripheryFieldEditorPreferencePage("Peripherie", 1); //$NON-NLS-1$
				page.setPreferenceStore(store);
				dialog.getPreferenceManager().addToRoot(new PreferenceNode("Peripherie", page)); //$NON-NLS-1$
				
				dialog.create();
				dialog.setBlockOnOpen(false);
				dialog.open();
			}
		});
	}
	
	public void userChanged(UserChangeEvent e)
	{
		this.setModes(e.getOldUser(), e.getNewUser());
		this.productGroupTableSash.getSelectedPage().refresh();
	}
	
	private void setModes(User oldUser, User newUser)
	{
		if (newUser == null)
		{
			return;
		}
		
		if (oldUser == null || oldUser.status != newUser.status)
		{
			boolean enable = new Integer(newUser.status).compareTo(new Integer(0)) <= 0;
			// TabItem[] items = mainFolder.getItems();
			// for (int i = 0; i < items.length; i++) {
			// if (items[i].getText().equals(Messages.getString(
			// "MainWindow.Benutzer_9"))) {
			// items[i].getControl().setVisible(enable);
			// }
			// else if (items[i].getText().equals(Messages.getString(
			// "MainWindow.Tastenbelegung_10"))) {
			// items[i].getControl().setVisible(enable);
			// }
			// else if (items[i].getText().equals(Messages.getString(
			// "MainWindow.Fixtasten_11"))) {
			// items[i].getControl().setVisible(enable);
			// }
			// }
			this.prefsItem.setEnabled(enable);
		}
		this.updateGalileoItem.setEnabled(User.getCurrentUser().status == User.USER_STATE_ADMINISTRATOR);
		
	}
	
	private void startManageProductGroupWizard()
	{
		WizardDialog dialog = null;
		StructuredSelection sel = (StructuredSelection) this.productGroupTableSash.getViewer().getSelection();
		if (!sel.isEmpty() && sel.size() == 1)
		{
			dialog = new WizardDialog(this.getShell(), new ProductGroupChangeWizard((ProductGroup) sel
							.getFirstElement()));
		}
		else
		{
			dialog = new WizardDialog(this.getShell(), new ProductGroupChangeWizard());
		}
		if (dialog.open() == 0)
		{
			;
		}
	}
	
	private void updateProductGroups()
	{
		if (GalileoProductGroupServer.updateProductGroups())
		{
			this.productGroupTableSash.initializeContent();
			this.keyTreeSash.initializeContent();
			MessageDialog
							.openInformation(
											this.getShell(),
											Messages.getString("MainWindow.Best_u00E4tigung_11"), Messages.getString("MainWindow.Die_Warengruppen_wurden_erfolgreich__u00FCbertragen._12")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
		{
			MessageDialog
							.openError(
											this.getShell(),
											Messages.getString("MainWindow.Fehlermeldung_13"), Messages.getString("MainWindow.Die_Warengruppen_konnten_nicht_ordnungsgem_u00E4ss__u00FCbertragen_werden._14")); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	private void importProductGroups()
	{
		if (GalileoProductGroupServer.copyProductGroups())
		{
			this.productGroupTableSash.initializeContent();
			this.keyTreeSash.initializeContent();
			MessageDialog
							.openInformation(
											this.getShell(),
											Messages.getString("MainWindow.Best_u00E4tigung_11"), Messages.getString("MainWindow.Die_Warengruppen_wurden_erfolgreich__u00FCbertragen._12")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
		{
			MessageDialog
							.openError(
											this.getShell(),
											Messages.getString("MainWindow.Fehlermeldung_13"), Messages.getString("MainWindow.Die_Warengruppen_konnten_nicht_ordnungsgem_u00E4ss__u00FCbertragen_werden._14")); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	private void updateGalileo()
	{
		if (LogManager.getLogManager().getLogger("colibri") != null)
		{
			LogManager.getLogManager().getLogger("colibri")
							.info("Dialog für Galileo-Aktualisierung wird instantiiert.");
		}
		UpdateGalileoDialog updateGalileoDialog = new UpdateGalileoDialog(this.getShell());
		updateGalileoDialog.open();
	}
	
	private void loadProperties()
	{
		Properties defaultProperties = new Properties();
		defaultProperties.setProperty("window.width", "800");
		defaultProperties.setProperty("window.height", "600");
		defaultProperties.setProperty("productgroup.left", "2");
		defaultProperties.setProperty("productgroup.right", "3");
		defaultProperties.setProperty("paymenttype.left", "2");
		defaultProperties.setProperty("paymenttype.right", "3");
		defaultProperties.setProperty("tax.left", "2");
		defaultProperties.setProperty("tax.right", "3");
		defaultProperties.setProperty("option.left", "2");
		defaultProperties.setProperty("option.right", "3");
		defaultProperties.setProperty("salespoint.left", "2");
		defaultProperties.setProperty("salespoint.right", "3");
		defaultProperties.setProperty("coin.left", "2");
		defaultProperties.setProperty("coin.right", "3");
		defaultProperties.setProperty("currency.left", "2");
		defaultProperties.setProperty("currency.right", "3");
		defaultProperties.setProperty("user.left", "2");
		defaultProperties.setProperty("user.right", "3");
		defaultProperties.setProperty("key.left", "2");
		defaultProperties.setProperty("key.right", "3");
		defaultProperties.setProperty("key.detail.left", "1");
		defaultProperties.setProperty("key.detail.right", "1");
		defaultProperties.setProperty("fixkey.left", "2");
		defaultProperties.setProperty("fixkey.right", "3");
		
		this.properties = new Properties(defaultProperties);
		
		File file = new File(Path.getInstance().cfgDir + "admin.ini");
		if (file.exists())
		{
			try
			{
				InputStream in = new FileInputStream(file);
				this.properties.load(in);
			}
			catch (IOException e)
			{
			}
		}
		
	}
	
	private void saveProperties()
	{
		this.properties.setProperty("window.width", new Integer(this.getShell().getSize().x).toString());
		this.properties.setProperty("window.height", new Integer(this.getShell().getSize().y).toString());
		this.properties.setProperty("productgroup.left", new Integer(this.productGroupTableSash.getWeights()[0])
						.toString());
		this.properties.setProperty("productgroup.right", new Integer(this.productGroupTableSash.getWeights()[1])
						.toString());
		this.properties.setProperty("paymenttype.left", new Integer(this.paymentTypeTreeSash.getWeights()[0])
						.toString());
		this.properties.setProperty("paymenttype.right", new Integer(this.paymentTypeTreeSash.getWeights()[1])
						.toString());
		this.properties.setProperty("tax.left", new Integer(this.taxTreeSash.getWeights()[0]).toString());
		this.properties.setProperty("tax.right", new Integer(this.taxTreeSash.getWeights()[1]).toString());
		this.properties.setProperty("option.left", new Integer(this.optionTableSash.getWeights()[0]).toString());
		this.properties.setProperty("option.right", new Integer(this.optionTableSash.getWeights()[1]).toString());
		this.properties.setProperty("salespoint.left", new Integer(this.salespointTreeSash.getWeights()[0]).toString());
		this.properties
						.setProperty("salespoint.right", new Integer(this.salespointTreeSash.getWeights()[1])
										.toString());
		this.properties.setProperty("coin.left", new Integer(this.coinTreeSash.getWeights()[0]).toString());
		this.properties.setProperty("coin.right", new Integer(this.coinTreeSash.getWeights()[1]).toString());
		this.properties.setProperty("currency.left", new Integer(this.currencyTableSash.getWeights()[0]).toString());
		this.properties.setProperty("currency.right", new Integer(this.currencyTableSash.getWeights()[1]).toString());
		this.properties.setProperty("user.left", new Integer(this.userTableSash.getWeights()[0]).toString());
		this.properties.setProperty("user.right", new Integer(this.userTableSash.getWeights()[1]).toString());
		this.properties.setProperty("key.left", new Integer(this.keyTreeSash.getWeights()[0]).toString());
		this.properties.setProperty("key.right", new Integer(this.keyTreeSash.getWeights()[1]).toString());
		this.properties.setProperty("key.detail.left", new Integer(this.tabFieldEditorPage.getWeights()[0]).toString());
		this.properties
						.setProperty("key.detail.right", new Integer(this.tabFieldEditorPage.getWeights()[1])
										.toString());
		this.properties.setProperty("fixkey.left", new Integer(this.fixKeyTreeSash.getWeights()[0]).toString());
		this.properties.setProperty("fixkey.right", new Integer(this.fixKeyTreeSash.getWeights()[1]).toString());
		
		File file = new File(Path.getInstance().cfgDir + "admin.ini");
		try
		{
			OutputStream out = new FileOutputStream(file);
			this.properties.store(out, null);
		}
		catch (IOException e)
		{
		}
	}
	
	public String getProperty(String name)
	{
		return this.properties.getProperty(name);
	}
	
	/**
	 * Returns the initial size to use for the shell. The default implementation
	 * returns the preferred size of the shell, using
	 * <code>Shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true)</code>.
	 * 
	 * @return the initial size of the shell
	 */
	protected Point getInitialSize()
	{
		return this.getShell().computeSize(MainWindow.dimension.x, MainWindow.dimension.y, true);
	}
	
	/**
	 * Returns the number of pixels corresponding to the height of the given
	 * number of characters.
	 * <p>
	 * The required <code>FontMetrics</code> parameter may be created in the
	 * following way: <code>
	 * 	GC gc = new GC(control);
	 * 	gc.setFont(control.getFont());
	 * 	fontMetrics = gc.getFontMetrics();
	 * 	gc.dispose();
	 * </code>
	 * </p>
	 * 
	 * @param fontMetrics
	 *            used in performing the conversion
	 * @param chars
	 *            the number of characters
	 * @return the number of pixels
	 * @since 2.0
	 */
	public static int convertHeightInCharsToPixels(FontMetrics fontMetrics, int chars)
	{
		return fontMetrics.getHeight() * chars;
	}
	
	/**
	 * Returns the number of pixels corresponding to the height of the given
	 * number of characters.
	 * <p>
	 * This method may only be called after <code>initializeDialogUnits</code>
	 * has been called.
	 * </p>
	 * <p>
	 * Clients may call this framework method, but should not override it.
	 * </p>
	 * 
	 * @param chars
	 *            the number of characters
	 * @return the number of pixels
	 */
	protected int convertHeightInCharsToPixels(int chars)
	{
		// test for failure to initialize for backward compatibility
		if (this.fontMetrics == null) return 0;
		return MainWindow.convertHeightInCharsToPixels(this.fontMetrics, chars);
	}
	
	/**
	 * Returns the number of pixels corresponding to the given number of
	 * horizontal dialog units.
	 * <p>
	 * The required <code>FontMetrics</code> parameter may be created in the
	 * following way: <code>
	 * 	GC gc = new GC(control);
	 * 	gc.setFont(control.getFont());
	 * 	fontMetrics = gc.getFontMetrics();
	 * 	gc.dispose();
	 * </code>
	 * </p>
	 * 
	 * @param fontMetrics
	 *            used in performing the conversion
	 * @param dlus
	 *            the number of horizontal dialog units
	 * @return the number of pixels
	 * @since 2.0
	 */
	public static int convertHorizontalDLUsToPixels(FontMetrics fontMetrics, int dlus)
	{
		// round to the nearest pixel
		return (fontMetrics.getAverageCharWidth() * dlus + MainWindow.HORIZONTAL_DIALOG_UNIT_PER_CHAR / 2)
						/ MainWindow.HORIZONTAL_DIALOG_UNIT_PER_CHAR;
	}
	
	/**
	 * Returns the number of pixels corresponding to the given number of
	 * horizontal dialog units.
	 * <p>
	 * This method may only be called after <code>initializeDialogUnits</code>
	 * has been called.
	 * </p>
	 * <p>
	 * Clients may call this framework method, but should not override it.
	 * </p>
	 * 
	 * @param dlus
	 *            the number of horizontal dialog units
	 * @return the number of pixels
	 */
	protected int convertHorizontalDLUsToPixels(int dlus)
	{
		// test for failure to initialize for backward compatibility
		if (this.fontMetrics == null) return 0;
		return MainWindow.convertHorizontalDLUsToPixels(this.fontMetrics, dlus);
	}
	
	/**
	 * Returns the number of pixels corresponding to the given number of
	 * vertical dialog units.
	 * <p>
	 * The required <code>FontMetrics</code> parameter may be created in the
	 * following way: <code>
	 * 	GC gc = new GC(control);
	 * 	gc.setFont(control.getFont());
	 * 	fontMetrics = gc.getFontMetrics();
	 * 	gc.dispose();
	 * </code>
	 * </p>
	 * 
	 * @param fontMetrics
	 *            used in performing the conversion
	 * @param dlus
	 *            the number of vertical dialog units
	 * @return the number of pixels
	 * @since 2.0
	 */
	public static int convertVerticalDLUsToPixels(FontMetrics fontMetrics, int dlus)
	{
		// round to the nearest pixel
		return (fontMetrics.getHeight() * dlus + MainWindow.VERTICAL_DIALOG_UNITS_PER_CHAR / 2)
						/ MainWindow.VERTICAL_DIALOG_UNITS_PER_CHAR;
	}
	
	/**
	 * Returns the number of pixels corresponding to the given number of
	 * vertical dialog units.
	 * <p>
	 * This method may only be called after <code>initializeDialogUnits</code>
	 * has been called.
	 * </p>
	 * <p>
	 * Clients may call this framework method, but should not override it.
	 * </p>
	 * 
	 * @param dlus
	 *            the number of vertical dialog units
	 * @return the number of pixels
	 */
	protected int convertVerticalDLUsToPixels(int dlus)
	{
		// test for failure to initialize for backward compatibility
		if (this.fontMetrics == null) return 0;
		return MainWindow.convertVerticalDLUsToPixels(this.fontMetrics, dlus);
	}
	
	/**
	 * Returns the number of pixels corresponding to the width of the given
	 * number of characters.
	 * <p>
	 * The required <code>FontMetrics</code> parameter may be created in the
	 * following way: <code>
	 * 	GC gc = new GC(control);
	 * 	gc.setFont(control.getFont());
	 * 	fontMetrics = gc.getFontMetrics();
	 * 	gc.dispose();
	 * </code>
	 * </p>
	 * 
	 * @param fontMetrics
	 *            used in performing the conversion
	 * @param chars
	 *            the number of characters
	 * @return the number of pixels
	 * @since 2.0
	 */
	public static int convertWidthInCharsToPixels(FontMetrics fontMetrics, int chars)
	{
		return fontMetrics.getAverageCharWidth() * chars;
	}
	
	/**
	 * Returns the number of pixels corresponding to the width of the given
	 * number of characters.
	 * <p>
	 * This method may only be called after <code>initializeDialogUnits</code>
	 * has been called.
	 * </p>
	 * <p>
	 * Clients may call this framework method, but should not override it.
	 * </p>
	 * 
	 * @param chars
	 *            the number of characters
	 * @return the number of pixels
	 */
	protected int convertWidthInCharsToPixels(int chars)
	{
		// test for failure to initialize for backward compatibility
		if (this.fontMetrics == null) return 0;
		return MainWindow.convertWidthInCharsToPixels(this.fontMetrics, chars);
	}
	
	/**
	 * Creates and returns the contents of the upper part of this dialog (above
	 * the button bar).
	 * <p>
	 * The <code>Dialog</code> implementation of this framework method creates
	 * and returns a new <code>Composite</code> with standard margins and
	 * spacing.
	 * </p>
	 * <p>
	 * The returned control's layout data must be an instance of
	 * <code>GridData</code>.
	 * </p>
	 * <p>
	 * Subclasses must override this method but may call <code>super</code> as
	 * in the following example:
	 * </p>
	 * 
	 * <pre>
	 * Composite composite = (Composite) super.createDialogArea(parent);
	 * //add controls to composite as necessary
	 * return composite;
	 * </pre>
	 * 
	 * @param parent
	 *            the parent composite to contain the dialog area
	 * @return the dialog area control
	 */
	protected TabFolder createTabFolderArea(Composite parent)
	{
		// create a composite with standard margins and spacing
		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = this.convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = this.convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = this.convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = this.convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		tabFolder.setLayout(layout);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		tabFolder.setFont(parent.getFont());
		
		return tabFolder;
	}
	
	/**
	 * Creates a new button with the given id.
	 * <p>
	 * The <code>Dialog</code> implementation of this framework method creates a
	 * standard push button, registers it for selection events including button
	 * presses, and registers default buttons with its shell. The button id is
	 * stored as the button's client data. If the button id is
	 * <code>IDialogConstants.CANCEL_ID</code>, the new button will be
	 * accessible from <code>getCancelButton()</code>. If the button id is
	 * <code>IDialogConstants.OK_ID</code>, the new button will be accesible
	 * from <code>getOKButton()</code>. Note that the parent's layout is assumed
	 * to be a <code>GridLayout</code> and the number of columns in this layout
	 * is incremented. Subclasses may override.
	 * </p>
	 * 
	 * @param parent
	 *            the parent composite
	 * @param id
	 *            the id of the button (see <code>IDialogConstants.*_ID</code>
	 *            constants for standard dialog button ids)
	 * @param label
	 *            the label from the button
	 * @param defaultButton
	 *            <code>true</code> if the button is to be the default button,
	 *            and <code>false</code> otherwise
	 * 
	 * @return the new button
	 * 
	 * @see #getCancelButton
	 * @see #getOKButton()
	 */
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton)
	{
		// increment the number of columns in the button bar
		((GridLayout) parent.getLayout()).numColumns++;
		
		Button button = new Button(parent, SWT.PUSH);
		
		button.setText(label);
		
		button.setData(new Integer(id));
		button.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent event)
			{
				MainWindow.this.buttonPressed(((Integer) event.widget.getData()).intValue());
			}
		});
		if (defaultButton)
		{
			Shell shell = parent.getShell();
			if (shell != null)
			{
				shell.setDefaultButton(button);
			}
		}
		button.setFont(parent.getFont());
		this.buttons.put(new Integer(id), button);
		this.setButtonLayoutData(button);
		
		return button;
	}
	
	/**
	 * Creates and returns the contents of this dialog's button bar.
	 * <p>
	 * The <code>Dialog</code> implementation of this framework method lays out
	 * a button bar and calls the <code>createButtonsForButtonBar</code>
	 * framework method to populate it. Subclasses may override.
	 * </p>
	 * <p>
	 * The returned control's layout data must be an instance of
	 * <code>GridData</code>.
	 * </p>
	 * 
	 * @param parent
	 *            the parent composite to contain the button bar
	 * @return the button bar control
	 */
	protected Control createButtonBar(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		
		// create a layout with spacing and margins appropriate for the font
		// size.
		GridLayout layout = new GridLayout();
		layout.numColumns = 0; // this is incremented by createButton
		layout.makeColumnsEqualWidth = true;
		layout.marginWidth = this.convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.marginHeight = this.convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.horizontalSpacing = this.convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.verticalSpacing = this.convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		
		composite.setLayout(layout);
		
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER);
		composite.setLayoutData(data);
		
		composite.setFont(parent.getFont());
		
		// Add the buttons to the button bar.
		this.createButtonsForButtonBar(composite);
		
		return composite;
	}
	
	/**
	 * Adds buttons to this dialog's button bar.
	 * <p>
	 * The <code>Dialog</code> implementation of this framework method adds
	 * standard ok and cancel buttons using the <code>createButton</code>
	 * framework method. These standard buttons will be accessible from
	 * <code>getCancelButton</code>, and <code>getOKButton</code>. Subclasses
	 * may override.
	 * </p>
	 * 
	 * @param parent
	 *            the button bar composite
	 */
	protected void createButtonsForButtonBar(Composite parent)
	{
		// create OK and Cancel buttons by default
		this.createButton(parent, Constants.ADD_ID, Constants.ADD_LABEL, true);
		this.createButton(parent, Constants.SAVE_ID, Constants.SAVE_LABEL, false);
		this.createButton(parent, Constants.RESET_ID, Constants.RESET_LABEL, false);
		this.createButton(parent, Constants.REMOVE_ID, Constants.REMOVE_LABEL, false);
		this.createButton(parent, Constants.CLOSE_ID, Constants.CLOSE_LABEL, false);
	}
	
	/**
	 * Set the layout data of the button to a GridData with appropriate heights
	 * and widths.
	 * 
	 * @param button
	 */
	protected void setButtonLayoutData(Button button)
	{
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.heightHint = this.convertVerticalDLUsToPixels(IDialogConstants.BUTTON_HEIGHT);
		int widthHint = this.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		button.setLayoutData(data);
	}
	
	/**
	 * Set the layout data of the button to a FormData with appropriate heights
	 * and widths.
	 * 
	 * @param button
	 */
	protected void setButtonLayoutFormData(Button button)
	{
		FormData data = new FormData();
		data.height = this.convertVerticalDLUsToPixels(IDialogConstants.BUTTON_HEIGHT);
		int widthHint = this.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		data.width = Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		button.setLayoutData(data);
	}
	
	/**
	 * Notifies that this dialog's button with the given id has been pressed.
	 * <p>
	 * The <code>Dialog</code> implementation of this framework method calls
	 * <code>okPressed</code> if the ok button is the pressed, and
	 * <code>cancelPressed</code> if the cancel button is the pressed. All other
	 * button presses are ignored. Subclasses may override to handle other
	 * buttons, but should call <code>super.buttonPressed</code> if the default
	 * handling of the ok and cancel buttons is desired.
	 * </p>
	 * 
	 * @param buttonId
	 *            the id of the button that was pressed (see
	 *            <code>IDialogConstants.*_ID</code> constants)
	 */
	protected void buttonPressed(int buttonId)
	{
		switch (buttonId)
		{
			case Constants.ADD_ID:
			{
				this.currentControl.add();
				break;
			}
			case Constants.SAVE_ID:
			{
				this.currentControl.save();
				break;
			}
			case Constants.RESET_ID:
			{
				this.currentControl.reset();
				break;
			}
			case Constants.REMOVE_ID:
			{
				this.currentControl.delete();
				break;
			}
			case Constants.CLOSE_ID:
			{
				this.close();
				break;
			}
		}
	}
	
	public boolean close()
	{
		//		LogManager.getLogManager().getLogger("colibri").info(Messages.getString("MainWindow.Hauptfenster_schliessen..._16")); //$NON-NLS-1$ //$NON-NLS-2$
		TabItem[] items = this.mainFolder.getItems();
		for (int i = 0; i < items.length; i++)
		{
			SashForm sash = (SashForm) items[i].getControl();
			Page[] pages = sash.getPages();
			for (int j = 0; j < pages.length; j++)
			{
				if (pages[j].getStore().needsSaving())
				{
					if (MessageDialog
									.openQuestion(
													this.getShell(),
													Messages.getString("MainWindow.Nicht_gespeicherte_Elemente_69"), Messages.getString("MainWindow.Sie_haben_noch_nicht_alle_vorgenommenen__Aenderungen_gespeichert._Wollen_Sie_die__Aenderungen_speichern__70"))) { //$NON-NLS-1$ //$NON-NLS-2$
						this.mainFolder.setSelection(i);
						this.setCurrentTabItem(items[i]);
						return false;
					}
				}
			}
		}
		
		if (Config.getInstance().getProductServerClass().equals(GalileoServer.class.getName()))
		{
			StringBuffer messagePart = new StringBuffer("");
			File galileoDb = new File(Config.getInstance().getGalileoPath());
			if (!galileoDb.exists())
			{
				messagePart.append("-> Der Pfad zum Datenverzeichnis von Galileo ist ungültig.\n");
				if (!MessageDialog
								.openQuestion(
												this.getShell(),
												"Ungültiger Pfad", "Der Pfad zum Datenverzeichnis von Galileo ist ungültig. Wollen Sie den Administrator trotzdem verlassen?")) { //$NON-NLS-1$ //$NON-NLS-2$
					return false;
				}
			}
			File file = new File(Config.getInstance().getGalileoCdPath());
			if (!file.exists())
			{
				messagePart.append("-> Der Pfad zur Datei \"bibWin.ini\" ist ungültig.\n");
			}
			// 10190
			// if (new
			// Boolean(Config.getInstance().getGalileo().getAttributeValue
			// ("use")).booleanValue() == false) {
			if (!Config.getInstance().getProductServerUse())
			{
				// 10190
				if (Config.getInstance().getGalileoUpdate() == 2)
				{
					messagePart
									.append("-> Der COM-Server ist deaktiviert, die Warenbewirtschaftung ist aber aktiviert.\n");
				}
				Code128.initialize();
				Iterator iter = Code128.codes.values().iterator();
				boolean useGalileo = false;
				while (iter.hasNext())
				{
					Code128 code128 = (Code128) iter.next();
					if (new Boolean(code128.getProperties().getProperty("galileo")).booleanValue()) useGalileo = true;
				}
				if (useGalileo)
				{
					messagePart
									.append("-> Der COM-Server ist deaktiviert, der Code128 versucht aber auf die Titeldaten zuzugreifen.\n");
				}
			}
			if (messagePart.length() > 0)
			{
				String message = "Probleme in den Einstellungen:\n\n" + messagePart
								+ "\nWollen Sie den Administrator trotzdem verlassen?";
				if (!MessageDialog.openQuestion(this.getShell(), "Fehler in den Einstellungen", message)) { //$NON-NLS-1$
					return false;
				}
			}
		}
		
		this.finalize();
		
		return super.close();
	}
	
	public void updateTitle()
	{
		this
						.getShell()
						.setText(
										MainWindow.TITLE
														+ " - " + this.mainFolder.getSelection()[0].getText() + " - Benutzer: " + User.getCurrentUser().username); //$NON-NLS-1$
	}
	
	public void updateButtons()
	{
		if (this.currentControl == null || this.currentControl.getSelectedPage() == null)
		{
			Set keys = this.buttons.keySet();
			Iterator i = keys.iterator();
			while (i.hasNext())
			{
				((Button) this.buttons.get(i.next())).setEnabled(false);
			}
		}
		else
		{
			((Button) this.buttons.get(new Integer(Constants.ADD_ID)))
							.setEnabled(this.currentControl.getSelectedPage().isAddActive);
			((Button) this.buttons.get(new Integer(Constants.SAVE_ID))).setEnabled(this.currentControl
							.getSelectedPage().getStore().needsSaving()
							&& this.currentControl.getSelectedPage().isValid());
			((Button) this.buttons.get(new Integer(Constants.RESET_ID))).setEnabled(this.currentControl
							.getSelectedPage().getStore().needsSaving());
			((Button) this.buttons.get(new Integer(Constants.REMOVE_ID))).setEnabled(this.currentControl
							.getSelectedPage().isRemoveActive ? !this.currentControl.getViewer().getSelection()
							.isEmpty() : false);
			((Button) this.buttons.get(new Integer(Constants.CLOSE_ID))).setEnabled(true);
			/*
			 * Set the default button
			 */
			if (this.currentControl.getSelectedPage().getStore().needsSaving()
							&& this.currentControl.getSelectedPage().isValid())
			{
				this.getShell().setDefaultButton((Button) this.buttons.get(new Integer(Constants.SAVE_ID)));
			}
			else if (this.currentControl.getSelectedPage().getStore().needsSaving())
			{
				this.getShell().setDefaultButton((Button) this.buttons.get(new Integer(Constants.RESET_ID)));
			}
			else
			{
				this.getShell().setDefaultButton((Button) this.buttons.get(new Integer(Constants.ADD_ID)));
			}
		}
	}
	
	public void finalize()
	{
		this.saveProperties();
		ColorRegistry.disposeColors();
	}
	
	public void updateMessage()
	{
		if (this.currentControl != null) this.currentControl.updateMessage();
	}
	
	public PersistentDBStore getStore()
	{
		return this.currentStore;
	}
	
	public void populateMenu()
	{
	}
	
	public void widgetSelected(SelectionEvent e)
	{
		if (e.item instanceof TabItem)
		{
			this.setCurrentTabItem((TabItem) e.item);
		}
	}
	
	public void widgetDefaultSelected(SelectionEvent e)
	{
		if (e.item instanceof TabItem)
		{
			this.setCurrentTabItem((TabItem) e.item);
		}
	}
	
	protected void setCurrentTabItem(TabItem item)
	{
		TabItem ti = item;
		if (item.getControl() instanceof TabFolder)
		{
			TabFolder folder = (TabFolder) ti.getControl();
			if (folder.getSelectionIndex() >= 0)
			{
				ti = folder.getItem(folder.getSelectionIndex());
			}
		}
		if (ti.getControl() instanceof SashForm)
		{
			this.currentControl = (SashForm) ti.getControl();
			this.updateTitle();
			this.updateButtons();
		}
	}
	
	public static MainWindow getInstance()
	{
		if (MainWindow.me == null)
		{
			//			LogManager.getLogManager().getLogger("colibri").log(Level.INFO, Messages.getString("MainWindow.Hauptfenster_wird_instantiiert..._73")); //$NON-NLS-1$ //$NON-NLS-2$
			MainWindow.me = new MainWindow(null);
			MainWindow.me.create();
			MainWindow.me.setBlockOnOpen(true);
			//			LogManager.getLogManager().getLogger("colibri").log(Level.INFO, Messages.getString("MainWindow.Hauptfenster_wird_geoeffnet..._75")); //$NON-NLS-1$ //$NON-NLS-2$
			MainWindow.me.open();
		}
		return MainWindow.me;
	}
	
	public Hashtable getSelectionChangedSources()
	{
		return this.senders;
	}
	
	public Object getSelectionChangedSource(String key)
	{
		return this.senders.get(key);
	}
	
	public Properties getProperties()
	{
		return this.properties;
	}
	
	private static MainWindow me;
	
	private Hashtable senders = new Hashtable();
	
	private TableSashForm productGroupTableSash;
	private TreeSashForm paymentTypeTreeSash;
	private TreeSashForm taxTreeSash;
	private TableSashForm optionTableSash;
	// private TableSashForm salespointTableSash;
	private TreeSashForm salespointTreeSash;
	private TreeSashForm coinTreeSash;
	// private TableSashForm coinTableSash;
	private TableSashForm currencyTableSash;
	private TableSashForm userTableSash;
	private TreeSashForm keyTreeSash;
	private TreeSashForm fixKeyTreeSash;
	
	private MenuItem prefsItem;
	private MenuItem updateGalileoItem;
	private Properties properties;
	private TabFieldEditorPage tabFieldEditorPage;
}
