/*
 * Created on 18.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.gui;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.PaymentType;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.statistics.data.Translator;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ReceiptImportTranslatorComposite extends Composite implements Listener
{
	
	/**
	 * @param parent
	 * @param style
	 */
	public ReceiptImportTranslatorComposite(Composite parent, int style)
	{
		super(parent, style);
		this.init();
	}
	
	private void init()
	{
		this.tables = Messages.getString("Constants.tables").split(":");
		
		this.translatorList = new TranslatorList();
		
		this.tableGroup = new Group(this, SWT.SHADOW_ETCHED_IN);
		this.tableGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.tableGroup
						.setText(Messages
										.getString("ReceiptImportTranslatorComposite._u00C3_u0153bersetzungstabelle_1")); //$NON-NLS-1$
		this.tableGroup.setLayout(new GridLayout());
		
		Table table = new Table(this.tableGroup, SWT.BORDER | SWT.SINGLE);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setHeaderVisible(true);
		
		TableColumn column = new TableColumn(table, SWT.LEFT, 0);
		column.setText(Messages.getString("ReceiptImportTranslatorComposite.Id_->_2")); //$NON-NLS-1$
		column.setWidth(60);
		column.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				ReceiptImportTranslatorComposite.this.tableViewer.setSorter(new TranslatorSorter(
								TranslatorSorter.SOURCE_CODE));
			}
		});
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText(Messages.getString("ReceiptImportTranslatorComposite.Zieltabelle_3")); //$NON-NLS-1$
		column.setWidth(150);
		column.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				ReceiptImportTranslatorComposite.this.tableViewer.setSorter(new TranslatorSorter(
								TranslatorSorter.TARGET_TABLE));
			}
		});
		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText(Messages.getString("ReceiptImportTranslatorComposite.Id_4")); //$NON-NLS-1$
		column.setWidth(200);
		column.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				ReceiptImportTranslatorComposite.this.tableViewer.setSorter(new TranslatorSorter(
								TranslatorSorter.TARGET_ID));
			}
		});
		
		this.tableContentProvider = new ReceiptImportTranslatorComposite.TranslatorTableContentProvider();
		this.tableLabelProvider = new ReceiptImportTranslatorComposite.TranslatorTableLabelProvider();
		this.cellModifier = new TranslatorCellModifier(this);
		
		this.tableViewer = new TableViewer(table);
		this.tableViewer.setContentProvider(this.tableContentProvider);
		this.tableViewer.setLabelProvider(this.tableLabelProvider);
		this.tableViewer.setUseHashlookup(true);
		this.tableViewer.setColumnProperties(this.columnNames);
		
		this.editors = new CellEditor[this.columnNames.length];
		
		TextCellEditor textEditor = new TextCellEditor(this.tableViewer.getTable());
		Text text = (Text) textEditor.getControl();
		text.setTextLimit(4);
		text.addListener(SWT.FocusIn, new Listener()
		{
			public void handleEvent(Event e)
			{
				ComboBoxCellEditor editor1 = (ComboBoxCellEditor) ReceiptImportTranslatorComposite.this.editors[1];
				CCombo combo1 = (CCombo) editor1.getControl();
				ComboBoxCellEditor editor2 = (ComboBoxCellEditor) ReceiptImportTranslatorComposite.this.editors[2];
				CCombo combo2 = (CCombo) editor2.getControl();
				TableItem[] items = ReceiptImportTranslatorComposite.this.tableViewer.getTable().getSelection();
				TableItem item = items[0];
				Translator t = (Translator) item.getData();
				
				e.doit = false;
				String s = item.getText(1);
				if (s.equals(ReceiptImportTranslatorComposite.this.tables[0]))
				{
					combo1.select(0);
				}
				else if (s.equals(ReceiptImportTranslatorComposite.this.tables[1]))
				{
					combo1.select(1);
				}
				if (combo1.getSelectionIndex() >= 0
								&& combo1.getSelectionIndex() < ReceiptImportTranslatorComposite.this.editors.length)
				{
					if (ReceiptImportTranslatorComposite.this.tables[combo1.getSelectionIndex()]
									.equals(ReceiptImportTranslatorComposite.this.tables[0]))
					{
						combo2.setItems(ReceiptImportTranslatorComposite.this.productGroups);
						if (!t.targetTable.equals(ReceiptImportTranslatorComposite.this.tables[0]))
						{
							combo2.select(0);
						}
						e.doit = true;
					}
					else if (ReceiptImportTranslatorComposite.this.tables[combo1.getSelectionIndex()]
									.equals(ReceiptImportTranslatorComposite.this.tables[1]))
					{
						combo2.setItems(ReceiptImportTranslatorComposite.this.paymentTypes);
						if (!t.targetTable.equals(ReceiptImportTranslatorComposite.this.tables[1]))
						{
							combo2.select(0);
						}
						e.doit = true;
					}
				}
			}
		});
		this.editors[0] = textEditor;
		
		ComboBoxCellEditor ce = new ComboBoxCellEditor(table, this.tables, SWT.READ_ONLY);
		((CCombo) ce.getControl()).addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				ComboBoxCellEditor editor1 = (ComboBoxCellEditor) ReceiptImportTranslatorComposite.this.editors[1];
				CCombo combo1 = (CCombo) editor1.getControl();
				ComboBoxCellEditor editor2 = (ComboBoxCellEditor) ReceiptImportTranslatorComposite.this.editors[2];
				CCombo combo2 = (CCombo) editor2.getControl();
				e.doit = false;
				if (combo1.getSelectionIndex() >= 0
								&& combo1.getSelectionIndex() < ReceiptImportTranslatorComposite.this.editors.length)
				{
					if (ReceiptImportTranslatorComposite.this.tables[combo1.getSelectionIndex()]
									.equals(ReceiptImportTranslatorComposite.this.tables[0]))
					{
						combo2.setItems(ReceiptImportTranslatorComposite.this.productGroups);
						combo2.select(0);
						e.doit = true;
					}
					else if (ReceiptImportTranslatorComposite.this.tables[combo1.getSelectionIndex()]
									.equals(ReceiptImportTranslatorComposite.this.tables[1]))
					{
						combo2.setItems(ReceiptImportTranslatorComposite.this.paymentTypes);
						combo2.select(0);
						e.doit = true;
					}
				}
			}
		});
		this.editors[1] = ce;
		
		ProductGroup[] pgs = ProductGroup.selectAll(false);
		this.productGroups = new String[pgs.length];
		for (int i = 0; i < pgs.length; i++)
		{
			this.productGroups[i] = pgs[i].getId() + " " + pgs[i].name;
		}
		PaymentType[] pts = PaymentType.selectAll(false);
		this.paymentTypes = new String[pts.length];
		for (int i = 0; i < pts.length; i++)
		{
			this.paymentTypes[i] = pts[i].getId() + " " + pts[i].name;
		}
		
		ce = new ComboBoxCellEditor(table, new String[0], SWT.READ_ONLY);
		((CCombo) ce.getControl()).addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				// ComboBoxCellEditor editor1 = (ComboBoxCellEditor)
				// ReceiptImportTranslatorComposite.this.editors[1];
				// CCombo combo1 = (CCombo) editor1.getControl();
				// ComboBoxCellEditor editor2 = (ComboBoxCellEditor)
				// ReceiptImportTranslatorComposite.this.editors[2];
				// CCombo combo2 = (CCombo) editor2.getControl();
			}
		});
		this.editors[2] = ce;
		
		this.tableViewer.setCellEditors(this.editors);
		this.tableViewer.setCellModifier(this.cellModifier);
		this.tableViewer.setSorter(new TranslatorSorter(TranslatorSorter.SOURCE_CODE));
		this.tableViewer.setInput(this.translatorList);
		
		Composite buttonComposite = new Composite(this.tableGroup, SWT.NONE);
		buttonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttonComposite.setLayout(new GridLayout(3, false));
		this.createButtons(buttonComposite);
		
	}
	
	/**
	 * Add the "Add", "Delete" and "Close" buttons
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private void createButtons(Composite parent)
	{
		
		// Create and configure the "Add" button
		Button add = new Button(parent, SWT.PUSH | SWT.CENTER);
		add.setText(Messages.getString("ReceiptImportTranslatorComposite.Hinzuf_u00FCgen_6")); //$NON-NLS-1$
		
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80;
		add.setLayoutData(gridData);
		add.addSelectionListener(new SelectionAdapter()
		{
			
			// Add a task to the ExampleTaskList and refresh the view
			public void widgetSelected(SelectionEvent e)
			{
				Long id = new Long(ReceiptImportTranslatorComposite.this.productGroups[0].trim().split(" ")[0]);
				Translator translator = new Translator("000", ReceiptImportTranslatorComposite.this.tables[0], id);
				ReceiptImportTranslatorComposite.this.translatorList.addTranslator(translator);
			}
		});
		
		// Create and configure the "Delete" button
		Button delete = new Button(parent, SWT.PUSH | SWT.CENTER);
		delete.setText(Messages.getString("ReceiptImportTranslatorComposite.Entfernen_7")); //$NON-NLS-1$
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80;
		delete.setLayoutData(gridData);
		
		delete.addSelectionListener(new SelectionAdapter()
		{
			
			// Remove the selection and refresh the view
			public void widgetSelected(SelectionEvent e)
			{
				IStructuredSelection sel = (IStructuredSelection) ReceiptImportTranslatorComposite.this.tableViewer
								.getSelection();
				Translator translator = (Translator) sel.getFirstElement();
				if (translator != null)
				{
					ReceiptImportTranslatorComposite.this.tableViewer.remove(translator);
					ReceiptImportTranslatorComposite.this.translatorList.removeTranslator(translator);
				}
			}
		});
		
		// Create and configure the "Delete" button
		Button save = new Button(parent, SWT.PUSH | SWT.CENTER);
		save.setText("Speichern"); //$NON-NLS-1$
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80;
		save.setLayoutData(gridData);
		
		save.addSelectionListener(new SelectionAdapter()
		{
			
			// Remove the selection and refresh the view
			public void widgetSelected(SelectionEvent e)
			{
				ReceiptImportTranslatorComposite.this.translatorList.save();
			}
		});
	}
	
	/**
	 * @return currently selected item
	 */
	public ISelection getSelection()
	{
		return this.tableViewer.getSelection();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.
	 * Event)
	 */
	public void handleEvent(Event event)
	{
		if (event.widget instanceof Button)
		{
			Button b = (Button) event.widget;
			this.tableGroup.setVisible(b.getSelection());
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang
	 * .Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex)
	{
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang
	 * .Object, int)
	 */
	public String getColumnText(Object element, int columnIndex)
	{
		String text = ""; //$NON-NLS-1$
		if (element instanceof Translator)
		{
			Translator t = (Translator) element;
			switch (columnIndex)
			{
				case 0:
					text = t.sourceCode;
					break;
				case 1:
					text = t.targetTable;
					break;
				case 2:
					text = t.getTargetId().toString();
					break;
			}
		}
		return text;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang
	 * .Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property)
	{
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.
	 * jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener)
	{
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse
	 * .jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener)
	{
	}
	
	/**
	 * Return the column names in a collection
	 * 
	 * @return List containing column names
	 */
	public List getColumnNames()
	{
		return Arrays.asList(this.columnNames);
	}
	
	public String[] getTables()
	{
		return this.tables;
	}
	
	public String[] getTargetValues(String table)
	{
		if (table.equals(this.tables[0]))
		{
			return this.productGroups;
		}
		else if (table.equals(this.tables[1]))
		{
			return this.paymentTypes;
		}
		return new String[0];
	}
	
	public TranslatorList getTranslatorList()
	{
		return this.translatorList;
	}
	
	public void initTranslatorTable()
	{
		this.translatorTable.clear();
		Iterator i = this.translatorList.getTranslators().iterator();
		while (i.hasNext())
		{
			Translator t = (Translator) i.next();
			this.translatorTable.put(t.sourceCode, t);
		}
	}
	
	public Translator getTranslator(String key)
	{
		return (Translator) this.translatorTable.get(key);
	}
	
	public String[] getItems()
	{
		ComboBoxCellEditor editor = (ComboBoxCellEditor) this.editors[2];
		CCombo combo = (CCombo) editor.getControl();
		return combo.getItems();
	}
	
	private class TranslatorTableContentProvider implements IStructuredContentProvider, ITranslatorListViewer
	{
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(
		 * java.lang.Object)
		 */
		public Object[] getElements(Object inputElement)
		{
			return ReceiptImportTranslatorComposite.this.translatorList.getTranslators().toArray();
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose()
		{
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse
		 * .jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
			if (newInput != null)
			{
				((TranslatorList) newInput).addChangeListener(this);
			}
			if (oldInput != null)
			{
				((TranslatorList) oldInput).addChangeListener(this);
			}
		}
		
		public void addTranslator(Translator translator)
		{
			ReceiptImportTranslatorComposite.this.tableViewer.add(translator);
		}
		
		public void removeTranslator(Translator translator)
		{
			ReceiptImportTranslatorComposite.this.tableViewer.remove(translator);
		}
		
		public void updateTranslator(Translator translator)
		{
			ReceiptImportTranslatorComposite.this.tableViewer.update(translator, null);
		}
		
	}
	
	private class TranslatorTableLabelProvider implements org.eclipse.jface.viewers.ITableLabelProvider
	{
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java
		 * .lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.
		 * lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex)
		{
			String text = ""; //$NON-NLS-1$
			if (element instanceof Translator)
			{
				Translator t = (Translator) element;
				switch (columnIndex)
				{
					case 0:
						text = t.sourceCode != null ? t.sourceCode : "";
						break;
					case 1:
						text = t.targetTable != null ? t.targetTable : "";
						break;
					case 2:
						text = t.getValue() != null ? t.getValue() : "";
						break;
				}
			}
			return text;
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse
		 * .jface.viewers.ILabelProviderListener)
		 */
		public void addListener(ILabelProviderListener listener)
		{
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
		 */
		public void dispose()
		{
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java
		 * .lang.Object, java.lang.String)
		 */
		public boolean isLabelProperty(Object element, String property)
		{
			return true;
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse
		 * .jface.viewers.ILabelProviderListener)
		 */
		public void removeListener(ILabelProviderListener listener)
		{
		}
		
	}
	
	private TableViewer tableViewer;
	private CellEditor[] editors;
	private Group tableGroup;
	private TranslatorTableContentProvider tableContentProvider;
	private TranslatorTableLabelProvider tableLabelProvider;
	private TranslatorCellModifier cellModifier;
	
	private String[] columnNames = new String[]
	{
					Messages.getString("ReceiptImportTranslatorComposite.Quellcode_10"), Messages.getString("ReceiptImportTranslatorComposite.Zieltabelle_11"), Messages.getString("ReceiptImportTranslatorComposite.Zielcode_12") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	private String[] tables;
	// private String[] tableNames;
	private String[] productGroups;
	private String[] paymentTypes;
	private TranslatorList translatorList;
	private Hashtable translatorTable = new Hashtable();
	
	// private boolean dirty = false;
}
