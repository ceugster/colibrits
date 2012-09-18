/*
 * Created on 16.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.gui;

import java.text.NumberFormat;
import java.util.Properties;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableColumn;
import org.jdom.Element;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.printing.ReceiptPrinter;
import ch.eugster.pos.swt.PaymentContentProvider;
import ch.eugster.pos.swt.PaymentLabelProvider;
import ch.eugster.pos.swt.PositionContentProvider;
import ch.eugster.pos.swt.PositionLabelProvider;
import ch.eugster.pos.swt.PrintDestinationGroup;
import ch.eugster.pos.util.Config;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ReceiptListComposite extends Composite implements ITabFolderChild, ISelectionChangedListener
{
	
	/**
	 * 
	 */
	public ReceiptListComposite(Composite parent, int style, SalespointComposite salespointComposite,
					DateRangeGroup dateRangeGroup, PrintDestinationGroup printDestinationGroup, Properties properties)
	{
		super(parent, style);
		this.salespointComposite = salespointComposite;
		this.dateRangeGroup = dateRangeGroup;
		this.init();
	}
	
	private void init()
	{
		this.setLayout(new FillLayout());
		org.eclipse.swt.custom.SashForm sash = new org.eclipse.swt.custom.SashForm(this, SWT.VERTICAL);
		sash.setLayout(new FillLayout());
		
		this.receiptGroup = new Group(sash, SWT.SHADOW_ETCHED_IN);
		this.receiptGroup.setLayout(new FillLayout());
		
		this.contentProvider = new ReceiptContentProvider(this.salespointComposite, this.dateRangeGroup);
		this.labelProvider = new ReceiptLabelProvider();
		this.receiptTable = new TableViewer(this.receiptGroup, SWT.BORDER);
		// receiptTable.getTable().setLayoutData(new
		// GridData(GridData.FILL_BOTH));
		this.receiptTable.setContentProvider(this.contentProvider);
		this.receiptTable.setLabelProvider(this.labelProvider);
		this.receiptTable.getTable().setHeaderVisible(true);
		TableColumn[] tc = new TableColumn[this.labelProvider.getColumnNames().length];
		for (int i = 0; i < tc.length; i++)
		{
			tc[i] = new TableColumn(this.receiptTable.getTable(), SWT.NONE);
			tc[i].setAlignment(this.labelProvider.getColumnAlignments()[i]);
			tc[i].setText(this.labelProvider.getColumnNames()[i]);
		}
		this.receiptTable.addPostSelectionChangedListener(this);
		
		TabFolder tf = new TabFolder(sash, SWT.NONE);
		
		this.positionTable = new TableViewer(tf, SWT.BORDER);
		this.positionTable.setContentProvider(new PositionContentProvider());
		this.positionTable.setLabelProvider(new PositionLabelProvider());
		this.positionTable.getTable().setHeaderVisible(true);
		TableColumn[] poc = new TableColumn[PositionLabelProvider.COLUMN_NAMES.length];
		for (int i = 0; i < poc.length; i++)
		{
			poc[i] = new TableColumn(this.positionTable.getTable(), SWT.NONE);
			poc[i].setText(PositionLabelProvider.COLUMN_NAMES[i]);
		}
		
		TabItem positionItem = new TabItem(tf, SWT.NONE);
		positionItem.setText(Messages.getString("ReceiptListComposite.Positionen_1")); //$NON-NLS-1$
		positionItem.setControl(this.positionTable.getTable());
		
		this.paymentTable = new TableViewer(tf, SWT.BORDER);
		this.paymentTable.setContentProvider(new PaymentContentProvider());
		this.paymentTable.setLabelProvider(new PaymentLabelProvider());
		this.paymentTable.getTable().setHeaderVisible(true);
		TableColumn[] pac = new TableColumn[PaymentLabelProvider.COLUMN_NAMES.length];
		for (int i = 0; i < pac.length; i++)
		{
			pac[i] = new TableColumn(this.paymentTable.getTable(), SWT.NONE);
			pac[i].setText(PaymentLabelProvider.COLUMN_NAMES[i]);
		}
		
		TabItem paymentItem = new TabItem(tf, SWT.NONE);
		paymentItem.setText(Messages.getString("ReceiptListComposite.Zahlungen_2")); //$NON-NLS-1$
		paymentItem.setControl(this.paymentTable.getTable());
		
		this.loadPosPrinter();
		
		sash.setWeights(new int[]
		{ 3, 2 });
	}
	
	protected void loadPosPrinter()
	{
		Element printer = Config.getInstance().getPosPrinter();
		if (Config.getInstance().getPosPrinterUse(printer))
		{
			this.receiptPrinter = ReceiptPrinter.getInstance(printer);
		}
	}
	
	public void printReceipt()
	{
		if (this.receiptPrinter != null)
		{
			if (this.receiptTable.getSelection() instanceof StructuredSelection)
			{
				StructuredSelection sel = (StructuredSelection) this.receiptTable.getSelection();
				if (sel.getFirstElement() instanceof Receipt)
				{
					this.receiptPrinter.print(this.receiptPrinter.getPrinter(), (Receipt) sel.getFirstElement());
				}
			}
		}
	}
	
	public void setReceiptTitle(int records)
	{
		NumberFormat nf = NumberFormat.getInstance();
		this.receiptGroup.setText("Anzahl Datensätze: " + nf.format(records));
	}
	
	public TableViewer getViewer()
	{
		return this.receiptTable;
	}
	
	public ReceiptContentProvider getProvider()
	{
		return this.contentProvider;
	}
	
	public String getPrintButtonDesignation()
	{
		return Messages.getString("ReceiptListComposite.Finden_3"); //$NON-NLS-1$
	}
	
	public String getPrintFileName()
	{
		return Messages.getString("ReceiptListComposite.Belegliste_4"); //$NON-NLS-1$
	}
	
	public void selectionChanged(SelectionChangedEvent e)
	{
		if (e.getSelection() instanceof StructuredSelection)
		{
			StructuredSelection sel = (StructuredSelection) e.getSelection();
			if (sel.getFirstElement() instanceof Receipt)
			{
				Receipt receipt = (Receipt) sel.getFirstElement();
				
				Event event = new Event();
				event.type = SWT.Selection;
				event.widget = this;
				event.data = receipt;
				this.notifyListeners(SWT.Selection, event);
				
				this.positionTable.getTable().removeAll();
				this.positionTable.setInput(receipt);
				for (int i = 0; i < this.positionTable.getTable().getColumnCount(); i++)
				{
					this.positionTable.getTable().getColumn(i).pack();
				}
				
				this.paymentTable.getTable().removeAll();
				this.paymentTable.setInput(receipt);
				for (int i = 0; i < this.paymentTable.getTable().getColumnCount(); i++)
				{
					this.paymentTable.getTable().getColumn(i).pack();
				}
			}
			else
			{
				this.positionTable.getTable().removeAll();
				this.paymentTable.getTable().removeAll();
			}
		}
	}
	
	public void postSelectionChanged(SelectionChangedEvent e)
	{
	}
	
	public boolean receiptSelected()
	{
		return !this.receiptTable.getSelection().isEmpty();
	}
	
	public TableViewer getReceiptTable()
	{
		return this.receiptTable;
	}
	
	public boolean isValid()
	{
		return true;
	}
	
	private TableViewer receiptTable;
	private Group receiptGroup;
	private ReceiptContentProvider contentProvider;
	private ReceiptLabelProvider labelProvider;
	private TableViewer positionTable;
	private TableViewer paymentTable;
	private SalespointComposite salespointComposite;
	private DateRangeGroup dateRangeGroup;
	private PrintDestinationGroup printDestinationGroup;
	private Properties properties;
	private ReceiptPrinter receiptPrinter = null;
}
