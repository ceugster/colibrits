/*
 * Created on 10.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.key;

import java.io.File;

import javax.swing.SwingConstants;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.MainWindow;
import ch.eugster.pos.admin.dialogs.PaymentTypeSelectionDialog;
import ch.eugster.pos.admin.dialogs.PaymentTypeSelectionDialogInput;
import ch.eugster.pos.admin.gui.widget.FieldEditor;
import ch.eugster.pos.db.CurrentTax;
import ch.eugster.pos.db.CustomKey;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.Function;
import ch.eugster.pos.db.Option;
import ch.eugster.pos.db.PaymentType;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.db.Tab;
import ch.eugster.pos.db.Tax;
import ch.eugster.pos.db.TaxRate;
import ch.eugster.pos.db.TaxType;
import ch.eugster.pos.events.Action;
import ch.eugster.pos.events.ExpressPaymentAction;
import ch.eugster.pos.events.ExpressStoreReceiptAction;
import ch.eugster.pos.events.OptionAction;
import ch.eugster.pos.events.PreDefinedDiscountAction;
import ch.eugster.pos.events.ProductGroupAction;
import ch.eugster.pos.events.StoreReceiptAction;
import ch.eugster.pos.events.StoreReceiptVoucherAction;
import ch.eugster.pos.events.TaxAction;
import ch.eugster.pos.events.ToggleAction;
import ch.eugster.pos.swt.DoubleInputDialog;
import ch.eugster.pos.swt.DoubleInputValidator;
import ch.eugster.pos.util.NumberUtility;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ButtonLayoutFieldEditor extends FieldEditor implements IPropertyChangeListener, Listener
{
	
	private TreeViewer tree;
	
	private Composite layoutContainer;
	
	private SashForm controlContainer;
	
	//
	// private MenuItem item;
	
	/**
	 * 
	 */
	public ButtonLayoutFieldEditor()
	{
		super();
	}
	
	/**
	 * @param name
	 * @param labelText
	 * @param parent
	 */
	public ButtonLayoutFieldEditor(String name, String labelText, Composite parent)
	{
		super(name, labelText, parent);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.gui.widget.FieldEditor#adjustForNumColumns(int)
	 */
	protected void adjustForNumColumns(int numColumns)
	{
		GridData gd = (GridData) this.controlContainer.getLayoutData();
		gd.horizontalSpan = numColumns;
		// We only grab excess space if we have to
		// If another field editor has more columns then
		// we assume it is setting the width.
		gd.grabExcessHorizontalSpace = gd.horizontalSpan == 1;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.admin.gui.widget.FieldEditor#doFillIntoGrid(org.eclipse
	 * .swt.widgets.Composite, int)
	 */
	protected void doFillIntoGrid(Composite parent, int numColumns)
	{
		this.controlContainer = new SashForm(parent, SWT.HORIZONTAL);
		this.controlContainer.setLayout(new GridLayout(2, false));
		GridData gd1 = new GridData(GridData.FILL_BOTH);
		gd1.horizontalSpan = numColumns;
		gd1.grabExcessHorizontalSpace = true;
		this.controlContainer.setLayoutData(gd1);
		
		this.tree = this.getTreeViewer(this.controlContainer);
		this.tree.getTree().setLayoutData(new GridData(GridData.FILL_VERTICAL));
		this.layoutContainer = this.getButtonContainer(this.controlContainer);
		int left = new Integer(MainWindow.getInstance().getProperty("key.detail.left")).intValue();
		int right = new Integer(MainWindow.getInstance().getProperty("key.detail.right")).intValue();
		this.controlContainer.setWeights(new int[]
		{ left, right });
		// controlContainer = new Composite(parent, SWT.FLAT);
		// controlContainer.setLayout(new GridLayout(2, false));
		// GridData gd1 = new GridData(GridData.FILL_BOTH);
		// gd1.horizontalSpan = numColumns;
		// gd1.grabExcessHorizontalSpace = true;
		// controlContainer.setLayoutData(gd1);
		//
		// tree = getTreeViewer(controlContainer);
		// tree.getTree().setLayoutData(new GridData(GridData.FILL_VERTICAL));
		// layoutContainer = getButtonContainer(controlContainer);
	}
	
	/**
	 * Returns this field editor's tree control.
	 * <p>
	 * The control is created if it does not yet exist
	 * </p>
	 * 
	 * @param parent
	 *            the parent
	 * @return the text control
	 */
	private TreeViewer getTreeViewer(Composite parent)
	{
		if (this.tree == null)
		{
			this.tree = new TreeViewer(parent, SWT.BORDER);
			this.tree.getTree().setFont(parent.getFont());
			/*
			 * DragSource Start
			 */
			/*
			 * treeSource = new DragSource(tree.getTree(), operations);
			 * treeSource.setTransfer(types); treeSource.addDragListener(new
			 * DragSourceListener() { public void dragStart(DragSourceEvent e) {
			 * IStructuredSelection selection = (IStructuredSelection)
			 * tree.getSelection(); dragSource = (DragSource) e.getSource(); if
			 * (selection.isEmpty()) { e.doit = false; } else { e.detail =
			 * DND.DROP_COPY; Object element = selection.getFirstElement(); if
			 * (element instanceof ProductGroup) { e.doit = true; } else if
			 * (element instanceof PaymentType) { e.doit = true; } else if
			 * (element instanceof TaxRate) { e.doit = true; } else if (element
			 * instanceof Function) { e.doit = true; } else { e.doit = false; }
			 * } } public void dragSetData(DragSourceEvent e) { if
			 * (TextTransfer.getInstance().isSupportedType(e.dataType)) {
			 * e.detail = DND.DROP_COPY; IStructuredSelection selection =
			 * (IStructuredSelection) tree.getSelection(); Object element =
			 * selection.getFirstElement(); if (element instanceof ProductGroup)
			 * { dragSourceItem[0] = element; } else if (element instanceof
			 * PaymentType) { dragSourceItem[0] = element; } else if (element
			 * instanceof TaxRate) { dragSourceItem[0] = element; } else if
			 * (element instanceof Function) { dragSourceItem[0] = element; }
			 * else { e.doit = false; } } } public void
			 * dragFinished(DragSourceEvent e) { } }); / DragSource End
			 */
		}
		else
		{
			this.checkParent(this.tree.getTree(), parent);
		}
		return this.tree;
	}
	
	public TreeViewer getTreeViewer()
	{
		return this.tree;
	}
	
	/**
	 * Returns this field editor's composite control.
	 * <p>
	 * The control is created if it does not yet exist
	 * </p>
	 * 
	 * @param parent
	 *            the parent
	 * @return the text control
	 */
	private Composite getButtonContainer(Composite parent)
	{
		if (this.layoutContainer == null)
		{
			this.layoutContainer = new Composite(parent, SWT.BORDER);
			this.layoutContainer.setFont(parent.getFont());
			this.layoutContainer.addDisposeListener(new DisposeListener()
			{
				public void widgetDisposed(DisposeEvent event)
				{
					ButtonLayoutFieldEditor.this.layoutContainer = null;
				}
			});
			this.layoutContainer.setLayoutData(new GridData(GridData.FILL_BOTH));
		}
		else
		{
			this.checkParent(this.layoutContainer, parent);
		}
		return this.layoutContainer;
	}
	
	public void propertyChange(PropertyChangeEvent e)
	{
		if (e.getProperty().equals(TabFieldEditorPage.KEY_ROWS)
						|| e.getProperty().equals(TabFieldEditorPage.KEY_COLUMNS))
		{
			this.doLoad();
		}
	}
	
	public void handleEvent(Event e)
	{
		boolean dirty = this.getStore().needsSaving();
		if (e.widget instanceof SashForm)
		{
			if (e.data instanceof ProductGroup)
			{
				this.tree.setInput(((ITreeContentProvider) this.tree.getContentProvider()).getElements(null));
			}
			else if (e.data instanceof PaymentType)
			{
				this.tree.setInput(((ITreeContentProvider) this.tree.getContentProvider()).getElements(null));
			}
			else if (e.data instanceof Option)
			{
				this.tree.setInput(((ITreeContentProvider) this.tree.getContentProvider()).getElements(null));
			}
			else if (e.data instanceof TaxRate | e.data instanceof TaxType | e.data instanceof Tax
							| e.data instanceof CurrentTax)
			{
				this.tree.setInput(((ITreeContentProvider) this.tree.getContentProvider()).getElements(null));
			}
			if (e.detail == 1)
			{
				// Es wurden Datensaetze geloescht
				// daher Neuaufbau des Buttonfeldes
				Database.getCurrent().getBroker().clearCache();
				this.getStore().setElement(Tab.selectById(((Tab) this.getStore().getElement()).getId()));
				this.doLoadDefault();
			}
			this.getStore().setDirty(dirty ? true : false);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.gui.widget.FieldEditor#doLoad()
	 */
	protected void doLoad()
	{
		if (this.getStore() != null) this.createLabels();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.gui.widget.FieldEditor#doLoadDefault()
	 */
	protected void doLoadDefault()
	{
		if (this.getStore() != null) this.createLabels();
	}
	
	// private void populateLabels()
	// {
	// if (this.labels == null)
	// {
	// this.labels = new Label[this.maxRows][this.maxCols];
	// for (int i = 0; i < this.labels.length; i++)
	// {
	// for (int j = 0; j < this.labels[i].length; j++)
	// {
	// this.addLabel(i, j);
	// this.addDropTarget(this.labels[i][j]);
	// }
	// }
	// }
	//
	// int rows = this.getStore().getInt(TabFieldEditorPage.KEY_ROWS)
	// .intValue();
	// int cols = this.getStore().getInt(TabFieldEditorPage.KEY_COLUMNS)
	// .intValue();
	// if (rows > this.maxRows) rows = this.maxRows;
	// if (cols > this.maxCols) cols = this.maxCols;
	// this.oldKeys = new CustomKey[rows][cols];
	// this.newKeys = new CustomKey[rows][cols];
	// this.layoutContainer.setLayout(new GridLayout(cols, true));
	// this.tabKeys = (CustomKey[]) this.getStore().getValue(
	// ButtonLayoutFieldEditor.KEY_KEYS);
	// for (int i = 0; i < this.tabKeys.length; i++)
	// {
	// int row = this.tabKeys[i].row.intValue();
	// int col = this.tabKeys[i].column.intValue();
	// if (row < rows && col < cols)
	// {
	// this.oldKeys[row][col] = this.tabKeys[i];
	// this.newKeys[row][col] = this.tabKeys[i];
	// }
	// }
	// for (int i = 0; i < this.oldKeys.length; i++)
	// {
	// for (int j = 0; j < this.oldKeys[i].length; j++)
	// {
	// if (this.oldKeys[i][j] == null)
	// {
	// this.clearLabel(this.labels[i][j]);
	// /*
	// * if (sources[i][j] != null) { if
	// * (sources[i][j].isDisposed()) { sources[i][j].dispose();
	// * sources[i][j] = null; } }
	// */}
	// else
	// {
	// this.fillLabel(this.labels[i][j], this.oldKeys[i][j]);
	// /*
	// * DragSource Start
	// *
	// * sources[i][j] = new DragSource(labels[i][j], operations);
	// * sources[i][j].setTransfer(types);
	// * sources[i][j].addDragListener(new DragSourceAdapter() {
	// * public void dragStart(DragSourceEvent e) { dragSource =
	// * (DragSource) e.getSource(); } public void
	// * dragSetData(DragSourceEvent e) { if
	// * (TextTransfer.getInstance().isSupportedType(e.dataType))
	// * { dragSourceItem[0] = ((Control)
	// * e.getSource()).getData("key"); } } public void
	// * dragFinished(DragSourceEvent e) { if (e.detail ==
	// * DND.DROP_MOVE) { clearLabel((Label) e.getSource()); } }
	// * }); DragSource End
	// */
	// }
	//
	// }
	// }
	// }
	
	// private void addLabel(int row, int col)
	// {
	// this.labels[row][col] = new Label(this.layoutContainer, SWT.BORDER
	// | SWT.SHADOW_OUT);
	//		this.labels[row][col].setData("row", new Integer(row)); //$NON-NLS-1$
	//		this.labels[row][col].setData("column", new Integer(col)); //$NON-NLS-1$
	// this.labels[row][col].setLayoutData(new GridData(GridData.FILL_BOTH));
	// this.labels[row][col].addMouseListener(new MouseAdapter()
	// {
	// public void mouseDoubleClick(MouseEvent e)
	// {
	// Label lbl = (Label) e.getSource();
	//				if (lbl.getData("key") instanceof CustomKey) { //$NON-NLS-1$
	// Event event = new Event();
	// event.widget = lbl.getMenu().getDefaultItem();
	// lbl.getMenu().getDefaultItem().notifyListeners(
	// SWT.Selection, event);
	// }
	// else
	// {
	// IStructuredSelection sel = (IStructuredSelection)
	// ButtonLayoutFieldEditor.this.tree
	// .getSelection();
	// if (!sel.isEmpty())
	// {
	// Object element = sel.getFirstElement();
	// ButtonLayoutFieldEditor.this.fillLabel(lbl,
	// ButtonLayoutFieldEditor.this.createKey(lbl,
	// element));
	// }
	// }
	// }
	// });
	// }
	
	// private void addDropTarget(Label label)
	// {
	// try
	// {
	// DropTarget target = new DropTarget(label, this.operations);
	// target.setTransfer(this.types);
	// target.addDropListener(new DropTargetAdapter()
	// {
	// public void drop(DropTargetEvent e)
	// {
	// DropTarget dt = (DropTarget) e.getSource();
	// Label label = (Label) dt.getControl();
	// if (ButtonLayoutFieldEditor.this.transfer
	// .isSupportedType(e.currentDataType))
	// {
	// e.detail = DND.DROP_MOVE;
	//						if (label.getData("key") instanceof CustomKey) { //$NON-NLS-1$
	// boolean ask = true;
	// if (ButtonLayoutFieldEditor.this.dragSourceItem[0] instanceof CustomKey)
	// {
	//								if (((CustomKey) label.getData("key")).parentClassName.equals(((CustomKey) ButtonLayoutFieldEditor.this.dragSourceItem[0]).parentClassName) && //$NON-NLS-1$
	//										((CustomKey) label.getData("key")).parentId.equals(((CustomKey) ButtonLayoutFieldEditor.this.dragSourceItem[0]).parentId)) { //$NON-NLS-1$
	// e.detail = DND.DROP_COPY;
	// return;
	// }
	// }
	// if (ask)
	// {
	// MessageDialog dlg = new MessageDialog(
	// ButtonLayoutFieldEditor.this.layoutContainer
	// .getShell(),
	// Messages
	//												.getString("ButtonLayoutFieldEditor.Taste_belegt_7"), null, Messages.getString("ButtonLayoutFieldEditor.Die_gew_u00FCnschte_Taste_ist_bereits_belegt._Soll_die_bestehende_Belegung__u00FCberschrieben_werden__8"), MessageDialog.QUESTION, new String[] { Messages.getString("ButtonLayoutFieldEditor.Ja_9"), Messages.getString("ButtonLayoutFieldEditor.Nein_10") }, 0); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	// dlg.setBlockOnOpen(true);
	// if (dlg.open() != 0)
	// {
	// e.detail = DND.DROP_COPY;
	// return;
	// }
	// }
	// }
	// ButtonLayoutFieldEditor.this
	// .fillLabel(
	// label,
	// ButtonLayoutFieldEditor.this
	// .createKey(
	// label,
	// ButtonLayoutFieldEditor.this.dragSourceItem[0]));
	// }
	// }
	//
	// public void dragEnter(DropTargetEvent e)
	// {
	// if (ButtonLayoutFieldEditor.this.dragSource.getControl() instanceof
	// Label)
	// {
	// e.detail = DND.DROP_MOVE;
	// }
	// else if (ButtonLayoutFieldEditor.this.dragSource
	// .getControl() instanceof Tree)
	// {
	// e.detail = DND.DROP_COPY;
	// }
	// }
	// });
	// }
	// catch (SWTError e)
	// {
	//
	// }
	// }
	
	private void createLabels()
	{
		// disposeDragSources();
		// disposeDropTargets();
		this.disposeLabels();
		// Control[] c = layoutContainer.getChildren();
		int rows = this.getStore().getInt(TabFieldEditorPage.KEY_ROWS).intValue();
		int cols = this.getStore().getInt(TabFieldEditorPage.KEY_COLUMNS).intValue();
		this.oldKeys = new CustomKey[rows][cols];
		this.newKeys = new CustomKey[rows][cols];
		this.labels = new Label[rows][cols];
		this.sources = new DragSource[rows][cols];
		this.targets = new DropTarget[rows][cols];
		this.layoutContainer.setLayout(new GridLayout(cols, true));
		this.tabKeys = (CustomKey[]) this.getStore().getValue(ButtonLayoutFieldEditor.KEY_KEYS);
		for (int i = 0; i < this.tabKeys.length; i++)
		{
			int row = this.tabKeys[i].row;
			int col = this.tabKeys[i].column;
			if (row < rows && col < cols)
			{
				this.oldKeys[row][col] = this.tabKeys[i];
				this.newKeys[row][col] = this.tabKeys[i];
			}
		}
		for (int i = 0; i < this.oldKeys.length; i++)
		{
			for (int j = 0; j < this.oldKeys[i].length; j++)
			{
				this.labels[i][j] = new Label(this.layoutContainer, SWT.BORDER | SWT.SHADOW_OUT);
				this.labels[i][j].setData("row", new Integer(i)); //$NON-NLS-1$
				this.labels[i][j].setData("column", new Integer(j)); //$NON-NLS-1$
				this.labels[i][j].setLayoutData(new GridData(GridData.FILL_BOTH));
				/*
				 * DropTarget Start
				 */
				/*
				 * try { targets[i][j] = new DropTarget(label, operations);
				 * targets[i][j].setTransfer(types);
				 * targets[i][j].addDropListener(new DropTargetAdapter() {
				 * public void drop(DropTargetEvent e) { DropTarget dt =
				 * (DropTarget) e.getSource(); Label label = (Label)
				 * dt.getControl(); if
				 * (transfer.isSupportedType(e.currentDataType)) { e.detail =
				 * DND.DROP_MOVE; if (label.getData("key") instanceof CustomKey)
				 * { boolean ask = true; if (dragSourceItem[0] instanceof
				 * CustomKey) { if (((CustomKey)
				 * label.getData("key")).parentClassName.equals(((CustomKey)
				 * dragSourceItem[0]).parentClassName) && ((CustomKey)
				 * label.getData("key")).parentId.equals(((CustomKey)
				 * dragSourceItem[0]).parentId)) { e.detail = DND.DROP_COPY;
				 * return; } } if (ask) { MessageDialog dlg = new
				 * MessageDialog(layoutContainer.getShell(), "Taste belegt",
				 * null,
				 * "Die gewünschte Taste ist bereits belegt. Soll die bestehende Belegung überschrieben werden?"
				 * , MessageDialog.QUESTION, new String[] {"Ja", "Nein"}, 0);
				 * dlg.setBlockOnOpen(true); if (dlg.open() != 0) { e.detail =
				 * DND.DROP_COPY; return; } } } fillLabel(label,
				 * createKey(label, dragSourceItem[0])); } } public void
				 * dragEnter(DropTargetEvent e) { if (dragSource.getControl()
				 * instanceof Label) { e.detail = DND.DROP_MOVE; } else if
				 * (dragSource.getControl() instanceof Tree) { e.detail =
				 * DND.DROP_COPY; } } }); } catch (SWTError e) { } / DropTarget
				 * End
				 */
				
				if (this.oldKeys[i][j] != null)
				{
					this.fillLabel(this.labels[i][j], this.oldKeys[i][j]);
					/*
					 * DragSource Start
					 */
					/*
					 * sources[i][j] = new DragSource(labels[i][j], operations);
					 * sources[i][j].setTransfer(types);
					 * sources[i][j].addDragListener(new DragSourceAdapter() {
					 * public void dragStart(DragSourceEvent e) { dragSource =
					 * (DragSource) e.getSource(); } public void
					 * dragSetData(DragSourceEvent e) { if
					 * (TextTransfer.getInstance().isSupportedType(e.dataType))
					 * { dragSourceItem[0] = ((Control)
					 * e.getSource()).getData("key"); } } public void
					 * dragFinished(DragSourceEvent e) { if (e.detail ==
					 * DND.DROP_MOVE) { clearLabel((Label) e.getSource()); } }
					 * }); / DragSource End
					 */
				}
				
				this.labels[i][j].addMouseListener(new MouseAdapter()
				{
					public void mouseDoubleClick(MouseEvent e)
					{
						Label lbl = (Label) e.getSource();
						if (lbl.getData("key") instanceof CustomKey) { //$NON-NLS-1$
							Event event = new Event();
							event.widget = lbl.getMenu().getDefaultItem();
							lbl.getMenu().getDefaultItem().notifyListeners(SWT.Selection, event);
						}
						else
						{
							IStructuredSelection sel = (IStructuredSelection) ButtonLayoutFieldEditor.this.tree
											.getSelection();
							if (!sel.isEmpty())
							{
								Object element = sel.getFirstElement();
								if (element instanceof ProductGroup || element instanceof PaymentType
												|| element instanceof TaxRate || element instanceof Option
												|| element instanceof Function)
								{
									ButtonLayoutFieldEditor.this.fillLabel(lbl,
													ButtonLayoutFieldEditor.this.createKey(lbl, element));
								}
							}
						}
					}
				});
			}
		}
		this.layoutContainer.layout();
		// configDragAndDrop();
	}
	
	private CustomKey createKey(Label label, Object element)
	{
		CustomKey key = null;
		if (element instanceof CustomKey)
		{
			key = (CustomKey) element;
			key.row = ((Integer) label.getData("row")).intValue(); //$NON-NLS-1$
			key.column = ((Integer) label.getData("column")).intValue(); //$NON-NLS-1$
		}
		else
		{
			key = new CustomKey();
			key.setTab((Tab) this.getStore().getElement());
			key.deleted = false;
			key.row = ((Integer) label.getData("row")).intValue(); //$NON-NLS-1$
			key.column = ((Integer) label.getData("column")).intValue(); //$NON-NLS-1$
			key.bgRed = 255;
			key.bgGreen = 255;
			key.bgBlue = 255;
			key.fgRed = 0;
			key.fgGreen = 0;
			key.fgBlue = 0;
			key.fontSize = 12f;
			key.fontStyle = 1;
			key.align = 0;
			key.valign = 0;
			key.relHorizontalTextPos = SwingConstants.TRAILING;
			key.relVerticalTextPos = SwingConstants.CENTER;
			if (element instanceof ProductGroup)
			{
				ProductGroup item = (ProductGroup) element;
				key.actionType = Action.POS_ACTION_SET_PRODUCT_GROUP;
				key.className = ProductGroupAction.class.getName();
				key.parentClassName = element.getClass().getName();
				key.parentId = item.getId();
				if (item.shortname != null && item.shortname.length() > 0)
					key.text = item.shortname;
				else
					key.text = item.name;
			}
			else if (element instanceof PaymentType)
			{
				PaymentType item = (PaymentType) element;
				System.out.println("Schritt 1");
				key.actionType = Action.POS_ACTION_SET_PAYMENT_TYPE;
				System.out.println("Schritt 2");
				key.className = ExpressPaymentAction.class.getName();
				System.out.println("Schritt 3");
				key.parentClassName = element.getClass().getName();
				System.out.println("Schritt 4");
				key.parentId = item.getId();
				System.out.println("Schritt 5");
				key.text = item.code;
				System.out.println("Schritt 6");
				label.setToolTipText("Zahlungsart: " + item.name);
				System.out.println("Schritt 7");
			}
			else if (element instanceof TaxRate)
			{
				TaxRate item = (TaxRate) element;
				key.actionType = Action.POS_ACTION_SET_TAX;
				key.className = TaxAction.class.getName();
				key.parentClassName = element.getClass().getName();
				key.parentId = item.getId();
				key.text = item.code;
				label.setToolTipText("Steuerart: " + item.name);
			}
			else if (element instanceof Option)
			{
				Option item = (Option) element;
				key.actionType = Action.POS_ACTION_SET_OPTION;
				key.className = OptionAction.class.getName();
				key.parentClassName = element.getClass().getName();
				key.parentId = item.getId();
				key.text = item.code;
				label.setToolTipText("Option: " + item.name);
			}
			else if (element instanceof Function)
			{
				Function item = (Function) element;
				key.actionType = new Integer(item.actionType);
				key.className = item.clazz;
				key.parentClassName = element.getClass().getName();
				key.parentId = item.getId();
				key.text = item.shortname;
				if (key.className.equals(StoreReceiptAction.class.getName())
								|| key.className.equals(StoreReceiptVoucherAction.class.getName())
								|| key.className.equals(ExpressStoreReceiptAction.class.getName()))
				{
					if (key.getPaymentType() == null) key.setPaymentType(PaymentType.getPaymentTypeCash());
				}
				label.setToolTipText("Funktion: " + item.name);
			}
		}
		this.newKeys[key.row][key.column] = key;
		return key;
	}
	
	private void fillLabel(Label label, CustomKey key)
	{
		label.setData("key", key); //$NON-NLS-1$
		label.setText(key.text);
		if (key.parentClassName.equals(ProductGroup.class.getName()))
		{
			label.setToolTipText("Warengruppe: " + ProductGroup.selectById(key.parentId).name);
		}
		else if (key.parentClassName.equals(PaymentType.class.getName()))
		{
			label.setToolTipText("Zahlungsart: " + PaymentType.selectById(key.parentId).name);
		}
		else if (key.parentClassName.equals(TaxRate.class.getName()))
		{
			label.setToolTipText("Steuerart: " + TaxRate.selectById(key.parentId).name);
		}
		else if (key.parentClassName.equals(Option.class.getName()))
		{
			label.setToolTipText("Option: " + Option.selectById(key.parentId).name);
		}
		else if (key.parentClassName.equals(Function.class.getName()))
		{
			label.setToolTipText("Funktion: " + Function.selectById(key.parentId).name);
		}
		label.setForeground(new Color(Display.getCurrent(), new RGB(key.fgRed, key.fgGreen, key.fgBlue)));
		label.setBackground(new Color(Display.getCurrent(), new RGB(key.bgRed, key.bgGreen, key.bgBlue)));
		label.setAlignment(key.align);
		label.setMenu(this.getMenu(label));
		if (this.pathIsValid(key.imagepath))
		{
			label.setImage(new Image(Display.getCurrent(), key.imagepath));
		}
		this.updateStore();
	}
	
	private void clearLabel(Label label)
	{
		int row = ((Integer) label.getData("row")).intValue(); //$NON-NLS-1$
		int col = ((Integer) label.getData("column")).intValue(); //$NON-NLS-1$
		this.newKeys[row][col] = null;
		
		label.setData("key", new Object()); //$NON-NLS-1$
		label.setText(""); //$NON-NLS-1$
		label.setToolTipText(""); //$NON-NLS-1$
		label.setForeground(this.layoutContainer.getForeground());
		label.setBackground(this.layoutContainer.getBackground());
		label.setMenu(null);
		this.updateStore();
	}
	
	private boolean pathIsValid(String path)
	{
		boolean result = false;
		File file = new File(path);
		if (file.exists())
		{
			result = true;
		}
		return result;
	}
	
	private void updateStore()
	{
		this.getStore().setDirty(true);
		this.getStore().firePropertyChangeEvent("keys", null, this.tabKeys); //$NON-NLS-1$
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.gui.widget.FieldEditor#doStore()
	 */
	protected void doStore()
	{
		for (int i = 0; i < this.oldKeys.length; i++)
		{
			for (int j = 0; j < this.oldKeys[i].length; j++)
			{
				if (this.newKeys[i][j] == null)
				{
					if (this.oldKeys[i][j] != null)
					{
						((Tab) this.getStore().getElement()).removeKey(this.oldKeys[i][j]);
						this.oldKeys[i][j].delete();
					}
				}
				else if (this.oldKeys[i][j] == null)
				{
					((Tab) this.getStore().getElement()).addKey(this.newKeys[i][j]);
				}
				else if (this.newKeys[i][j].getId() == null)
				{
					((Tab) this.getStore().getElement()).removeKey(this.oldKeys[i][j]);
					this.oldKeys[i][j].delete();
					this.newKeys[i][j].store();
					((Tab) this.getStore().getElement()).addKey(this.newKeys[i][j]);
				}
				else if (!this.newKeys[i][j].getId().equals(this.oldKeys[i][j].getId()))
				{
					((Tab) this.getStore().getElement()).removeKey(this.oldKeys[i][j]);
					this.oldKeys[i][j].delete();
					((Tab) this.getStore().getElement()).addKey(this.newKeys[i][j]);
				}
			}
		}
		this.getStore().putValue(ButtonLayoutFieldEditor.KEY_KEYS, ((Tab) this.getStore().getElement()).getKeys());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.gui.widget.FieldEditor#getNumberOfControls()
	 */
	public int getNumberOfControls()
	{
		return 1;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.gui.widget.FieldEditor#doCheckState()
	 */
	protected boolean doCheckState()
	{
		return true;
	}
	
	public void dispose()
	{
		this.disposeTreeDragSource();
		this.disposeDragSources();
		this.disposeDropTargets();
	}
	
	private void disposeTreeDragSource()
	{
		if (this.treeSource != null)
		{
			if (!this.treeSource.isDisposed())
			{
				this.treeSource.dispose();
				this.treeSource = null;
			}
		}
	}
	
	private void disposeDragSources()
	{
		if (this.sources != null)
		{
			for (int i = 0; i < this.sources.length; i++)
			{
				for (int j = 0; j < this.sources[i].length; j++)
				{
					if (this.sources[i][j] != null)
					{
						if (!this.sources[i][j].isDisposed())
						{
							this.sources[i][j].dispose();
							this.sources[i][j] = null;
						}
					}
				}
			}
			this.sources = null;
		}
	}
	
	private void disposeDropTargets()
	{
		if (this.targets != null)
		{
			for (int i = 0; i < this.targets.length; i++)
			{
				for (int j = 0; j < this.targets[i].length; j++)
				{
					if (this.targets[i][j] != null)
					{
						if (!this.targets[i][j].isDisposed())
						{
							this.targets[i][j].dispose();
							this.targets[i][j] = null;
						}
					}
				}
			}
			this.targets = null;
		}
	}
	
	private void disposeLabels()
	{
		if (this.labels != null)
		{
			for (int i = 0; i < this.labels.length; i++)
			{
				for (int j = 0; j < this.labels[i].length; j++)
				{
					if (this.labels[i][j] != null)
					{
						if (!this.labels[i][j].isDisposed())
						{
							this.labels[i][j].dispose();
							this.labels[i][j] = null;
						}
					}
				}
			}
			this.labels = null;
		}
	}
	
	private Menu getMenu(final Label label)
	{
		CustomKey key = (CustomKey) label.getData("key"); //$NON-NLS-1$
		
		Menu menu = new Menu(label);
		MenuItem item = null;
		
		String name = null;
		if (key.parentClassName.equals(ProductGroup.class.getName()))
		{
			ProductGroup pg = ProductGroup.selectById(key.parentId);
			if (pg != null) name = pg.name;
		}
		else if (key.parentClassName.equals(PaymentType.class.getName()))
		{
			PaymentType pt = PaymentType.selectById(key.parentId);
			if (pt != null) name = pt.name;
		}
		else if (key.parentClassName.equals(TaxRate.class.getName()))
		{
			TaxRate tr = TaxRate.selectById(key.parentId);
			if (tr != null) name = tr.name;
		}
		else if (key.parentClassName.equals(Option.class.getName()))
		{
			Option op = Option.selectById(key.parentId);
			if (op != null) name = op.name;
		}
		else if (key.parentClassName.equals(Function.class.getName()))
		{
			Function fn = (Function) Function.selectById(Function.class, key.parentId);
			if (fn != null) name = fn.name;
			
			if (key.className.equals(StoreReceiptAction.class.getName())
							|| key.className.equals(StoreReceiptVoucherAction.class.getName())
							|| key.className.equals(ExpressStoreReceiptAction.class.getName()))
			{
				if (key.getPaymentType() == null) key.setPaymentType(PaymentType.getPaymentTypeCash());
			}
		}
		if (name != null)
		{
			item = new MenuItem(menu, SWT.PUSH);
			item.setData(label);
			item.setText(name);
			
			item = new MenuItem(menu, SWT.SEPARATOR);
		}
		
		if (key.className.equals(StoreReceiptAction.class.getName())
						|| key.className.equals(StoreReceiptVoucherAction.class.getName())
						|| key.className.equals(ExpressStoreReceiptAction.class.getName()))
		{
			item = new MenuItem(menu, SWT.PUSH);
			item.setData(label);
			item.setText("Rückgeld: " + key.getPaymentType().code); //$NON-NLS-1$
			item.addListener(SWT.Selection, new Listener()
			{
				public void handleEvent(Event e)
				{
					CustomKey key = (CustomKey) label.getData("key"); //$NON-NLS-1$
					PaymentTypeSelectionDialogInput input = new PaymentTypeSelectionDialogInput(key.getPaymentType());
					PaymentTypeSelectionDialog dialog = new PaymentTypeSelectionDialog(MainWindow.getInstance()
									.getShell(), input);
					if (dialog.open() == IDialogConstants.OK_ID)
					{
						key = (CustomKey) label.getData("key"); //$NON-NLS-1$
						key.setPaymentType(input.getPaymentType());
						((MenuItem) e.widget).setText("Rückgeld: " + key.getPaymentType().code);
						ButtonLayoutFieldEditor.this.updateStore();
					}
				}
			});
			
			item = new MenuItem(menu, SWT.SEPARATOR);
		}
		
		item = new MenuItem(menu, SWT.PUSH);
		item.setData(label);
		item.setText(Messages.getString("ButtonLayoutFieldEditor.Hintergrundfarbe..._62")); //$NON-NLS-1$
		item.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				ColorDialog color = new ColorDialog(ButtonLayoutFieldEditor.this.layoutContainer.getShell());
				color.getRGB();
				color.setText(Messages.getString("ButtonLayoutFieldEditor.Auswahl_Hintergrundfarbe_63")); //$NON-NLS-1$
				RGB rgb = color.open();
				Label label = (Label) ((MenuItem) e.widget).getData();
				label.setBackground(new Color(Display.getCurrent(), rgb));
				CustomKey key = (CustomKey) label.getData("key"); //$NON-NLS-1$
				key.bgRed = rgb.red;
				key.bgGreen = rgb.green;
				key.bgBlue = rgb.blue;
				ButtonLayoutFieldEditor.this.updateStore();
			}
		});
		
		if (key.className.equals(ToggleAction.class.getName()))
		{
			item = new MenuItem(menu, SWT.PUSH);
			item.setData(label);
			item.setText(Messages.getString("ButtonLayoutFieldEditor.2._Hintergrundfarbe..._65")); //$NON-NLS-1$
			item.addListener(SWT.Selection, new Listener()
			{
				public void handleEvent(Event e)
				{
					ColorDialog color = new ColorDialog(ButtonLayoutFieldEditor.this.layoutContainer.getShell());
					color.getRGB();
					color.setText(Messages.getString("ButtonLayoutFieldEditor.Auswahl_Hintergrundfarbe_2_66")); //$NON-NLS-1$
					RGB rgb = color.open();
					Label label = (Label) ((MenuItem) e.widget).getData();
					label.setBackground(new Color(Display.getCurrent(), rgb));
					CustomKey key = (CustomKey) label.getData("key"); //$NON-NLS-1$
					key.bgRed2 = rgb.red;
					key.bgGreen2 = rgb.green;
					key.bgBlue2 = rgb.blue;
					ButtonLayoutFieldEditor.this.updateStore();
				}
			});
			
			item = new MenuItem(menu, SWT.PUSH);
			item.setData(label);
			item.setText(Messages.getString("ButtonLayoutFieldEditor.Wechsle_Hintergrundfarbenanzeige_68")); //$NON-NLS-1$
			item.addListener(SWT.Selection, new Listener()
			{
				public void handleEvent(Event e)
				{
					Label label = (Label) ((MenuItem) e.widget).getData();
					CustomKey key = (CustomKey) label.getData("key"); //$NON-NLS-1$
					if (label.getBackground().equals(
									new Color(Display.getCurrent(), key.bgRed, key.bgGreen, key.bgBlue)))
					{
						label.setBackground(new Color(Display.getCurrent(), key.bgRed2, key.bgGreen2, key.bgBlue2));
					}
					else
					{
						label.setBackground(new Color(Display.getCurrent(), key.bgRed, key.bgGreen, key.bgBlue));
					}
				}
			});
		}
		
		// 10124
		// Es können jetzt auch den Warengruppen ein Wert zugeordnet werden
		
		if (key.parentClassName.equals(PaymentType.class.getName())
						|| key.className.equals(PreDefinedDiscountAction.class.getName())
						|| key.parentClassName.equals(ProductGroup.class.getName()))
		{
			item = new MenuItem(menu, SWT.SEPARATOR);
			
			item = new MenuItem(menu, SWT.PUSH);
			item.setData(label);
			item.setText(Messages.getString("ButtonLayoutFieldEditor.Wert..._1")); //$NON-NLS-1$
			item.addListener(SWT.Selection, new Listener()
			{
				public void handleEvent(Event e)
				{
					Label label = (Label) ((MenuItem) e.widget).getData();
					if (label.getData("key") instanceof CustomKey) { //$NON-NLS-1$
						CustomKey key = (CustomKey) label.getData("key"); //$NON-NLS-1$
						double val = key.value.doubleValue();
						DoubleInputValidator div = new DoubleInputValidator();
						String msg = ""; //$NON-NLS-1$
						if (key.className.equals(PreDefinedDiscountAction.class.getName()))
						{
							val = NumberUtility.round(val * 100, .01d);
							msg = Messages.getString("ButtonLayoutFieldEditor.Geben_Sie_den_gew_u00FCnschten_Wert_in_Prozenten_(ohne_Prozentzeichen)_an._4"); //$NON-NLS-1$
							div.setRange(0d, 100d);
						}
						else
						{
							msg = Messages.getString("ButtonLayoutFieldEditor.Geben_Sie_den_gew_u00FCnschten_Wert_an._5"); //$NON-NLS-1$
							div.setRange(0d, 10000d);
						}
						DoubleInputDialog dlg = new DoubleInputDialog(
										ButtonLayoutFieldEditor.this.layoutContainer.getShell(),
										Messages.getString("ButtonLayoutFieldEditor.Wert_3"), msg, String.valueOf(val), div); //$NON-NLS-1$ 
						dlg.create();
						dlg.setBlockOnOpen(true);
						if (0 == dlg.open())
						{
							try
							{
								Double result;
								if (key.className.equals(PreDefinedDiscountAction.class.getName()))
								{
									result = new Double(NumberUtility.round(Double.parseDouble(dlg.getValue()) / 100,
													.01d));
								}
								else
								{
									result = new Double(Double.parseDouble(dlg.getValue()));
								}
								key.value = result;
							}
							catch (NumberFormatException nfe)
							{
							}
							ButtonLayoutFieldEditor.this.updateStore();
						}
					}
				}
			});
			// 10124
		}
		// 10124
		
		if (key.parentClassName.equals(ProductGroup.class.getName()))
		{
			item = new MenuItem(menu, SWT.SEPARATOR);
			
			item = new MenuItem(menu, SWT.PUSH);
			item.setData(label);
			item.setText("Artikelcode...");
			item.addListener(SWT.Selection, new Listener()
			{
				public void handleEvent(Event e)
				{
					Label label = (Label) ((MenuItem) e.widget).getData();
					if (label.getData("key") instanceof CustomKey) { //$NON-NLS-1$
						CustomKey key = (CustomKey) label.getData("key"); //$NON-NLS-1$
						String code = key.command;
						String msg = "Geben Sie den Artikelcode ein.";
						InputDialog dialog = new InputDialog(ButtonLayoutFieldEditor.this.layoutContainer.getShell(),
										"Artikelcode", msg, code, null); //$NON-NLS-1$ 
						dialog.create();
						dialog.setBlockOnOpen(true);
						if (0 == dialog.open())
						{
							key.command = dialog.getValue();
							ButtonLayoutFieldEditor.this.updateStore();
						}
					}
				}
			});
		}
		
		item = new MenuItem(menu, SWT.SEPARATOR);
		
		item = new MenuItem(menu, SWT.PUSH);
		item.setData(label);
		item.setText(Messages.getString("ButtonLayoutFieldEditor.Beschriftung..._70")); //$NON-NLS-1$
		item.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				Label label = (Label) ((MenuItem) e.widget).getData();
				InputDialog dlg = new InputDialog(
								ButtonLayoutFieldEditor.this.layoutContainer.getShell(),
								Messages.getString("ButtonLayoutFieldEditor.Beschriftung_71"), Messages.getString("ButtonLayoutFieldEditor.Legen_Sie_die_Beschriftung_f_u00FCr_die_Taste_fest_72"), label.getText(), null); //$NON-NLS-1$ //$NON-NLS-2$
				dlg.create();
				dlg.setBlockOnOpen(true);
				if (0 == dlg.open())
				{
					label.setText(dlg.getValue());
					((CustomKey) label.getData("key")).text = dlg.getValue(); //$NON-NLS-1$
					ButtonLayoutFieldEditor.this.updateStore();
				}
			}
		});
		
		/*
		 * Horizontale Ausrichtung
		 */
		item = new MenuItem(menu, SWT.CASCADE);
		item.setData(label);
		item.setText("Horizontale Ausrichtung"); //$NON-NLS-1$
		Menu menuHorizontalAlign = new Menu(item);
		this.addHorizontalAlignMenuItem(menuHorizontalAlign, label, 2,
						Messages.getString("ButtonLayoutFieldEditor.Links_6")); //$NON-NLS-1$
		this.addHorizontalAlignMenuItem(menuHorizontalAlign, label, 0,
						Messages.getString("ButtonLayoutFieldEditor.Zentriert_7")); //$NON-NLS-1$
		this.addHorizontalAlignMenuItem(menuHorizontalAlign, label, 4,
						Messages.getString("ButtonLayoutFieldEditor.Rechts_8")); //$NON-NLS-1$
		item.setMenu(menuHorizontalAlign);
		/*
		 * Vertikale Ausrichtung
		 */
		item = new MenuItem(menu, SWT.CASCADE);
		item.setData(label);
		item.setText("Vertikale Ausrichtung"); //$NON-NLS-1$
		Menu menuVerticalAlign = new Menu(item);
		this.addVerticalAlignMenuItem(menuVerticalAlign, label, 1, Messages.getString("ButtonLayoutFieldEditor.Oben_9")); //$NON-NLS-1$
		this.addVerticalAlignMenuItem(menuVerticalAlign, label, 0,
						Messages.getString("ButtonLayoutFieldEditor.Mitte_10")); //$NON-NLS-1$
		this.addVerticalAlignMenuItem(menuVerticalAlign, label, 3,
						Messages.getString("ButtonLayoutFieldEditor.Unten_11")); //$NON-NLS-1$
		item.setMenu(menuVerticalAlign);
		
		item = new MenuItem(menu, SWT.CASCADE);
		item.setData(label);
		item.setText(Messages.getString("ButtonLayoutFieldEditor.Schriftgr_u00F6sse_74")); //$NON-NLS-1$
		Menu menuFontSize = new Menu(item);
		for (int i = 8; i < 25; i++)
		{
			this.addSizeMenuItem(menuFontSize, label, i);
		}
		item.setMenu(menuFontSize);
		
		item = new MenuItem(menu, SWT.CASCADE);
		item.setData(label);
		item.setText(Messages.getString("ButtonLayoutFieldEditor.Schriftstil_75")); //$NON-NLS-1$
		Menu fontStyleMenu = new Menu(item);
		this.addStyleMenuItems(fontStyleMenu, label);
		item.setMenu(fontStyleMenu);
		
		item = new MenuItem(menu, SWT.PUSH);
		item.setData(label);
		item.setText(Messages.getString("ButtonLayoutFieldEditor.Scriftfarbe..._76")); //$NON-NLS-1$
		item.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				ColorDialog color = new ColorDialog(ButtonLayoutFieldEditor.this.layoutContainer.getShell());
				color.getRGB();
				color.setText(Messages.getString("ButtonLayoutFieldEditor.Auswahl_Textfarbe_77")); //$NON-NLS-1$
				RGB rgb = color.open();
				Label label = (Label) ((MenuItem) e.widget).getData();
				label.setForeground(new Color(Display.getCurrent(), rgb));
				CustomKey key = (CustomKey) label.getData("key"); //$NON-NLS-1$
				key.fgRed = rgb.red;
				key.fgGreen = rgb.green;
				key.fgBlue = rgb.blue;
				ButtonLayoutFieldEditor.this.updateStore();
			}
		});
		
		item = new MenuItem(menu, SWT.CHECK);
		item.setData(label);
		item.setText("Defaultregister setzen");
		item.setSelection(key.setDefaultTab);
		item.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				CustomKey key = (CustomKey) label.getData("key"); //$NON-NLS-1$
				if (e.widget instanceof MenuItem)
				{
					MenuItem mi = (MenuItem) e.widget;
					key.setDefaultTab = mi.getSelection();
					ButtonLayoutFieldEditor.this.updateStore();
				}
			}
		});
		
		item = new MenuItem(menu, SWT.SEPARATOR);
		
		item = new MenuItem(menu, SWT.PUSH);
		item.setData(label);
		item.setText(Messages.getString("ButtonLayoutFieldEditor.Bild..._79")); //$NON-NLS-1$
		item.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				Label label = (Label) ((MenuItem) e.widget).getData();
				FileDialog dlg = new FileDialog(ButtonLayoutFieldEditor.this.layoutContainer.getShell(), SWT.OPEN);
				dlg.setFilterExtensions(new String[]
				{
								Messages.getString("ButtonLayoutFieldEditor.*.gif_80"), Messages.getString("ButtonLayoutFieldEditor.*.jpg_81"), Messages.getString("ButtonLayoutFieldEditor.*.jpeg_82"), Messages.getString("ButtonLayoutFieldEditor.*.png_83") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				dlg.setFilterPath("icons/"); //$NON-NLS-1$
				dlg.setText(Messages.getString("ButtonLayoutFieldEditor.Auswahl_Bilddateien_85")); //$NON-NLS-1$
				String path = dlg.open();
				File file = new File(path);
				if (file.exists())
				{
					label.setImage(new Image(Display.getCurrent(), path));
					((CustomKey) label.getData("key")).imagepath = path; //$NON-NLS-1$
					ButtonLayoutFieldEditor.this.updateStore();
				}
			}
		});
		
		if (key.imagepath.length() > 0)
		{
			item = new MenuItem(menu, SWT.PUSH);
			item.setData(label);
			item.setText(Messages.getString("ButtonLayoutFieldEditor.Bild_entfernen_87")); //$NON-NLS-1$
			item.addListener(SWT.Selection, new Listener()
			{
				public void handleEvent(Event e)
				{
					Label label = (Label) ((MenuItem) e.widget).getData();
					label.setImage(null);
					label.setText(label.getText());
					((CustomKey) label.getData("key")).imagepath = ""; //$NON-NLS-1$ //$NON-NLS-2$
					ButtonLayoutFieldEditor.this.updateStore();
				}
			});
			
			item = new MenuItem(menu, SWT.CASCADE);
			item.setData(label);
			item.setText(Messages.getString("ButtonLayoutFieldEditor.Horizontale_Textposition_zum_Bild_90")); //$NON-NLS-1$
			Menu horizontalTextPos = new Menu(item);
			this.addHorzPosMenuItems(horizontalTextPos, label);
			item.setMenu(horizontalTextPos);
			
			item = new MenuItem(menu, SWT.CASCADE);
			item.setData(label);
			item.setText(Messages.getString("ButtonLayoutFieldEditor.Vertikale_Textposition_zum_Bild_91")); //$NON-NLS-1$
			Menu verticalTextPos = new Menu(item);
			this.addVertPosMenuItems(verticalTextPos, label);
			item.setMenu(verticalTextPos);
		}
		
		item = new MenuItem(menu, SWT.SEPARATOR);
		
		item = new MenuItem(menu, SWT.PUSH);
		item.setData(label);
		item.setText(Messages.getString("ButtonLayoutFieldEditor.Entfernen_92")); //$NON-NLS-1$
		item.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				Label label = (Label) ((MenuItem) e.widget).getData();
				ButtonLayoutFieldEditor.this.clearLabel(label);
			}
		});
		menu.setDefaultItem(item);
		return menu;
	}
	
	private void addHorizontalAlignMenuItem(Menu menu, Control control, int align, String name)
	{
		int a = align;
		CustomKey key = (CustomKey) control.getData("key"); //$NON-NLS-1$
		MenuItem item = new MenuItem(menu, SWT.RADIO);
		item.setData("control", control); //$NON-NLS-1$
		item.setData("align", new Integer(a)); //$NON-NLS-1$
		item.setText(name);
		item.setSelection(key.align == a);
		item.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				Control ctrl = (Control) e.widget.getData("control"); //$NON-NLS-1$
				CustomKey key = (CustomKey) ctrl.getData("key"); //$NON-NLS-1$
				key.align = ((Integer) e.widget.getData("align")).intValue(); //$NON-NLS-1$
				ButtonLayoutFieldEditor.this.updateStore();
			}
		});
	}
	
	private void addVerticalAlignMenuItem(Menu menu, Control control, int align, String name)
	{
		int a = align;
		CustomKey key = (CustomKey) control.getData("key"); //$NON-NLS-1$
		MenuItem item = new MenuItem(menu, SWT.RADIO);
		item.setData("control", control); //$NON-NLS-1$
		item.setData("valign", new Integer(a)); //$NON-NLS-1$
		item.setText(name);
		item.setSelection(key.align == a);
		item.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				Control ctrl = (Control) e.widget.getData("control"); //$NON-NLS-1$
				CustomKey key = (CustomKey) ctrl.getData("key"); //$NON-NLS-1$
				key.valign = ((Integer) e.widget.getData("valign")).intValue(); //$NON-NLS-1$
				ButtonLayoutFieldEditor.this.updateStore();
			}
		});
	}
	
	private void addSizeMenuItem(Menu menu, Control control, int size)
	{
		float s = size;
		CustomKey key = (CustomKey) control.getData("key"); //$NON-NLS-1$
		MenuItem item = new MenuItem(menu, SWT.RADIO);
		item.setData("control", control); //$NON-NLS-1$
		item.setData("size", new Float(s)); //$NON-NLS-1$
		item.setText(String.valueOf(size));
		item.setSelection(key.fontSize == s);
		item.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				Control ctrl = (Control) e.widget.getData("control"); //$NON-NLS-1$
				CustomKey key = (CustomKey) ctrl.getData("key"); //$NON-NLS-1$
				key.fontSize = ((Float) e.widget.getData("size")).floatValue(); //$NON-NLS-1$
				ButtonLayoutFieldEditor.this.updateStore();
			}
		});
	}
	
	private void addStyleMenuItems(Menu menu, Control control)
	{
		this.addStyleMenuItem(menu, control, 0, Messages.getString("ButtonLayoutFieldEditor.Standard_99")); //$NON-NLS-1$
		this.addStyleMenuItem(menu, control, 1, Messages.getString("ButtonLayoutFieldEditor.Fett_100")); //$NON-NLS-1$
		this.addStyleMenuItem(menu, control, 2, Messages.getString("ButtonLayoutFieldEditor.Kursiv_101")); //$NON-NLS-1$
		this.addStyleMenuItem(menu, control, 3, Messages.getString("ButtonLayoutFieldEditor.Fett,_kursiv_102")); //$NON-NLS-1$
	}
	
	private void addStyleMenuItem(Menu menu, Control control, int style, String text)
	{
		int s = style;
		CustomKey key = (CustomKey) control.getData("key"); //$NON-NLS-1$
		MenuItem item = new MenuItem(menu, SWT.RADIO);
		item.setData("control", control); //$NON-NLS-1$
		item.setData("style", new Integer(s)); //$NON-NLS-1$
		item.setText(text);
		item.setSelection(key.fontStyle == s);
		item.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				Control ctrl = (Control) e.widget.getData("control"); //$NON-NLS-1$
				CustomKey key = (CustomKey) ctrl.getData("key"); //$NON-NLS-1$
				key.fontStyle = ((Integer) e.widget.getData("style")).intValue(); //$NON-NLS-1$
				ButtonLayoutFieldEditor.this.updateStore();
			}
		});
	}
	
	private void addHorzPosMenuItems(Menu menu, Control control)
	{
		this.addHorzPosMenuItem(menu, control, 4, Messages.getString("ButtonLayoutFieldEditor.Rechts_109")); //$NON-NLS-1$
		this.addHorzPosMenuItem(menu, control, 2, Messages.getString("ButtonLayoutFieldEditor.Links_110")); //$NON-NLS-1$
		this.addHorzPosMenuItem(menu, control, 0, Messages.getString("ButtonLayoutFieldEditor.Zentriert_111")); //$NON-NLS-1$
		this.addHorzPosMenuItem(menu, control, 10, Messages.getString("ButtonLayoutFieldEditor.F_u00FChrend_112")); //$NON-NLS-1$
		this.addHorzPosMenuItem(menu, control, 11, Messages.getString("ButtonLayoutFieldEditor.Folgend_113")); //$NON-NLS-1$
	}
	
	private void addHorzPosMenuItem(Menu menu, Control control, int pos, String text)
	{
		int s = pos;
		CustomKey key = (CustomKey) control.getData("key"); //$NON-NLS-1$
		MenuItem item = new MenuItem(menu, SWT.RADIO);
		item.setData("control", control); //$NON-NLS-1$
		item.setData("horzPos", new Integer(s)); //$NON-NLS-1$
		item.setText(text);
		item.setSelection(key.relHorizontalTextPos == s);
		item.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				Control ctrl = (Control) e.widget.getData("control"); //$NON-NLS-1$
				CustomKey key = (CustomKey) ctrl.getData("key"); //$NON-NLS-1$
				key.relHorizontalTextPos = ((Integer) e.widget.getData("horzPos")).intValue(); //$NON-NLS-1$
				ButtonLayoutFieldEditor.this.updateStore();
			}
		});
	}
	
	private void addVertPosMenuItems(Menu menu, Control control)
	{
		this.addVertPosMenuItem(menu, control, 0, Messages.getString("ButtonLayoutFieldEditor.Zentriert_120")); //$NON-NLS-1$
		this.addVertPosMenuItem(menu, control, 1, Messages.getString("ButtonLayoutFieldEditor.Oben_121")); //$NON-NLS-1$
		this.addVertPosMenuItem(menu, control, 3, Messages.getString("ButtonLayoutFieldEditor.Unten_122")); //$NON-NLS-1$
	}
	
	private void addVertPosMenuItem(Menu menu, Control control, int pos, String text)
	{
		int s = pos;
		CustomKey key = (CustomKey) control.getData("key"); //$NON-NLS-1$
		MenuItem item = new MenuItem(menu, SWT.RADIO);
		item.setData(Messages.getString("ButtonLayoutFieldEditor.control_124"), control); //$NON-NLS-1$
		item.setData(Messages.getString("ButtonLayoutFieldEditor.vertPos_125"), new Integer(s)); //$NON-NLS-1$
		item.setText(text);
		item.setSelection(key.relVerticalTextPos == s);
		item.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				Control ctrl = (Control) e.widget.getData("control"); //$NON-NLS-1$
				CustomKey key = (CustomKey) ctrl.getData("key"); //$NON-NLS-1$
				key.relVerticalTextPos = ((Integer) e.widget.getData("vertPos")).intValue(); //$NON-NLS-1$
				ButtonLayoutFieldEditor.this.updateStore();
			}
		});
	}
	
	public int[] getWeights()
	{
		return this.controlContainer.getWeights();
	}
	
	private int maxRows = 5;
	private int maxCols = 5;
	
	private CustomKey[] tabKeys;
	private CustomKey[][] oldKeys;
	private CustomKey[][] newKeys;
	// private DragSource dragSource;
	// private TextTransfer transfer = TextTransfer.getInstance();
	// private Transfer[] types = new Transfer[]
	// { this.transfer };
	// private int operations = DND.DROP_DEFAULT | DND.DROP_MOVE |
	// DND.DROP_COPY;
	private Label[][] labels = null;
	private DragSource treeSource = null;
	private DragSource[][] sources = new DragSource[this.maxRows][this.maxCols];
	private DropTarget[][] targets = new DropTarget[this.maxRows][this.maxCols];
	// private Object[] dragSourceItem = new Object[1];
	// private TabFieldEditorPage parentPage;
	public static final String KEY_KEYS = "keys"; //$NON-NLS-1$
}
