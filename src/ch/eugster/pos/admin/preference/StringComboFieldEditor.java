/*
 * Created on 16.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.preference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import ch.eugster.pos.admin.gui.widget.FieldEditor;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class StringComboFieldEditor extends org.eclipse.jface.preference.FieldEditor
{
	
	// The top-level control for the field editor.
	private Composite top;
	// The checkbox to select the item.
	private Combo combo;
	
	private String[] values;
	
	private String newValue = ""; //$NON-NLS-1$
	
	private boolean isValid;
	
	/**
	 * @param name
	 * @param labelText
	 * @param parent
	 */
	public StringComboFieldEditor(String name, String labelText, Composite parent, String[] list)
	{
		super(name, labelText, parent);
		this.combo.setItems(list);
		this.values = list;
	}
	
	/**
	 * 
	 * @param name
	 * @param labelText
	 * @param parent
	 * @param list
	 *            shown items in the combobox
	 * @param values
	 */
	public StringComboFieldEditor(String name, String labelText, Composite parent, String[] list, String[] values)
	{
		super(name, labelText, parent);
		this.combo.setItems(list);
		this.values = values;
	}
	
	public Combo getCombo()
	{
		return this.combo;
	}
	
	public void setItems(String[] items)
	{
		this.setItems(items, items);
	}
	
	public void setItems(String[] items, String[] values)
	{
		this.values = values;
		String value = ""; //$NON-NLS-1$
		if (this.combo.getSelectionIndex() == -1)
		{
			value = this.getPreferenceStore().getString(this.getPreferenceName());
		}
		else
		{
			value = this.combo.getItem(this.combo.getSelectionIndex());
		}
		this.combo.removeAll();
		this.combo.setItems(items);
		for (int i = 0; i < values.length; i++)
		{
			if (values[i].equals(value))
			{
				this.combo.select(i);
				return;
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#adjustForNumColumns(int)
	 */
	protected void adjustForNumColumns(int numColumns)
	{
		((GridData) this.top.getLayoutData()).horizontalSpan = numColumns;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.FieldEditor#doFillIntoGrid(org.eclipse.swt
	 * .widgets.Composite, int)
	 */
	protected void doFillIntoGrid(Composite parent, int numColumns)
	{
		this.top = parent;
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = numColumns;
		this.top.setLayoutData(gd);
		
		Label label = this.getLabelControl(this.top);
		GridData labelData = new GridData();
		labelData.horizontalSpan = numColumns;
		label.setLayoutData(labelData);
		
		this.combo = new Combo(this.top, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData comboData = new GridData(GridData.FILL_HORIZONTAL);
		this.combo.setLayoutData(comboData);
		
		this.combo.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				StringComboFieldEditor.this.selectionChanged(e);
			}
		});
	}
	
	private void selectionChanged(SelectionEvent e)
	{
		if (e.getSource().equals(this.combo))
		{
			this.setPresentsDefaultValue(false);
			boolean oldState = this.isValid;
			
			this.refreshValidState();
			if (this.isValid != oldState) this.fireStateChanged(FieldEditor.IS_VALID, oldState, this.isValid);
			
			String oldValue = this.newValue;
			this.newValue = this.values[this.combo.getSelectionIndex()];
			
			if (!oldValue.equals(this.newValue))
			{
				this.doStore();
				this.fireValueChanged(this.getPreferenceName(), oldValue, this.newValue);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#doLoad()
	 */
	protected void doLoad()
	{
		String value = this.getPreferenceStore().getString(this.getPreferenceName());
		for (int i = 0; i < this.values.length; i++)
		{
			if (this.values[i].equals(value))
			{
				this.combo.select(i);
				return;
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
	 */
	protected void doLoadDefault()
	{
		String value = this.getPreferenceStore().getDefaultString(this.getPreferenceName());
		for (int i = 0; i < this.values.length; i++)
		{
			if (this.values[i].equals(value))
			{
				this.combo.select(i);
				return;
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#doStore()
	 */
	protected void doStore()
	{
		this.getPreferenceStore().setValue(this.getPreferenceName(),
						this.combo.getSelectionIndex() > -1 ? this.values[this.combo.getSelectionIndex()] : ""); //$NON-NLS-1$
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#getNumberOfControls()
	 */
	public int getNumberOfControls()
	{
		return 1;
	}
	
}
