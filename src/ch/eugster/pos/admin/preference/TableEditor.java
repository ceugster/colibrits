package ch.eugster.pos.admin.preference;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Widget;

/**
 * An abstract field editor that manages a list of input values. The editor
 * displays a list containing the values, buttons for adding and removing
 * values, and Up and Down buttons to adjust the order of elements in the list.
 * <p>
 * Subclasses must implement the <code>parseString</code>,
 * <code>createTable</code>, and <code>getNewInputObject</code> framework
 * methods.
 * </p>
 */
public abstract class TableEditor extends FieldEditor
{
	
	/**
	 * The table widget; <code>null</code> if none (before creation or after
	 * disposal).
	 */
	private Table table;
	
	/**
	 * The selection listener.
	 */
	private SelectionListener selectionListener;
	
	/**
	 * Creates a table field editor.
	 */
	protected TableEditor()
	{
	}
	
	/**
	 * Creates a table field editor.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public TableEditor(String name, String labelText, Composite parent)
	{
		this.init(name, labelText);
		this.table = this.getTableControl(parent);
		this.createControl(parent);
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void adjustForNumColumns(int numColumns)
	{
		// Control control = getLabelControl();
		// ((GridData)control.getLayoutData()).horizontalSpan = numColumns;
		((GridData) this.table.getLayoutData()).horizontalSpan = numColumns;
	}
	
	/**
	 * Combines the given table of items into a single string. This method is
	 * the converse of <code>parseString</code>.
	 * <p>
	 * Subclasses must implement this method.
	 * </p>
	 * 
	 * @param items
	 *            the list of items
	 * @return the combined string
	 * @see #parseString
	 */
	protected void createTable(String[] colNames)
	{
		for (int i = 0; i < colNames.length; i++)
		{
			TableColumn t = new TableColumn(this.table, 0);
			t.setText(colNames[i]);
		}
		this.getTableControl().setHeaderVisible(true);
	}
	
	/**
	 * Helper method to create a push button.
	 * 
	 * @param parent
	 *            the parent control
	 * @param key
	 *            the resource name used to supply the button's label text
	 */
	
	/**
	 * Creates a selection listener.
	 */
	public void createSelectionListener()
	{
		this.selectionListener = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent event)
			{
				Widget widget = event.widget;
				if (widget == TableEditor.this.table)
				{
					TableEditor.this.selectionChanged();
				}
			}
		};
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doFillIntoGrid(Composite parent, int numColumns)
	{
		Control control = this.getLabelControl(parent);
		GridData gd = new GridData();
		gd.horizontalSpan = numColumns;
		control.setLayoutData(gd);
		
		this.table = this.getTableControl(parent);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalSpan = numColumns - 1;
		gd.grabExcessHorizontalSpace = true;
		this.table.setLayoutData(gd);
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected abstract void doLoad();
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoadDefault()
	{
		this.doLoad();
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected abstract void doStore();
	
	/**
	 * Returns this field editor's table control.
	 * 
	 * @return the table control
	 */
	public Table getTableControl()
	{
		return this.table;
	}
	
	/**
	 * Returns this field editor's table control.
	 * 
	 * @param parent
	 *            the parent control
	 * @return the table control
	 */
	public Table getTableControl(Composite parent)
	{
		if (this.table == null)
		{
			this.table = new Table(parent, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
			this.table.setFont(parent.getFont());
			this.table.addSelectionListener(this.getSelectionListener());
			this.table.addDisposeListener(new DisposeListener()
			{
				public void widgetDisposed(DisposeEvent event)
				{
					TableEditor.this.table = null;
				}
			});
		}
		else
		{
			this.checkParent(this.table, parent);
		}
		return this.table;
	}
	
	public int getNumberOfControls()
	{
		return 1;
	}
	
	/**
	 * Returns this field editor's selection listener. The listener is created
	 * if nessessary.
	 * 
	 * @return the selection listener
	 */
	private SelectionListener getSelectionListener()
	{
		if (this.selectionListener == null) this.createSelectionListener();
		return this.selectionListener;
	}
	
	/**
	 * Returns this field editor's shell.
	 * <p>
	 * This method is internal to the framework; subclassers should not call
	 * this method.
	 * </p>
	 * 
	 * @return the shell
	 */
	protected Shell getShell()
	{
		if (this.table == null) return null;
		return this.table.getShell();
	}
	
	/**
	 * Notifies that the list selection has changed.
	 */
	private void selectionChanged()
	{
		
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public void setFocus()
	{
		if (this.table != null)
		{
			this.table.setFocus();
		}
	}
	
	/*
	 * @see FieldEditor.setEnabled(boolean,Composite).
	 */
	public void setEnabled(boolean enabled, Composite parent)
	{
		super.setEnabled(enabled, parent);
		this.getTableControl(parent).setEnabled(enabled);
	}
}
