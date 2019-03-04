/*
 * Created on 16.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.preference;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class DoubleComboFieldEditor extends FieldEditor
{
	
	// The top-level control for the field editor.
	private Composite top;
	// The checkbox to select the item.
	private Combo combo;
	
	private double[] values;
	
	private double newValue = 0;
	
	private boolean isValid;
	
	/**
	 * 
	 * @param name
	 * @param labelText
	 * @param parent
	 * @param list
	 *            shown items in the combobox
	 * @param values
	 */
	public DoubleComboFieldEditor(String name, String labelText, Composite parent, String[] list, double[] values)
	{
		super(name, labelText, parent);
		this.combo.setItems(list);
		this.values = values;
	}
	
	public Combo getCombo()
	{
		return this.combo;
	}
	
	public void setItems(String[] items, double[] values)
	{
		this.values = values;
		double value = 0d;
		if (this.combo.getSelectionIndex() == -1)
		{
			value = this.getPreferenceStore().getDouble(this.getPreferenceName());
		}
		else
		{
			value = values[this.combo.getSelectionIndex()];
		}
		this.combo.removeAll();
		this.combo.setItems(items);
		for (int i = 0; i < values.length; i++)
		{
			if (values[i] == value)
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
				DoubleComboFieldEditor.this.selectionChanged(e);
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
			
			double oldValue = this.newValue;
			this.newValue = this.values[this.combo.getSelectionIndex()];
			
			if (oldValue != this.newValue)
			{
				this.doStore();
				this.fireValueChanged(this.getPreferenceName(), new Double(oldValue), new Double(this.newValue));
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
		double value = this.getPreferenceStore().getDouble(this.getPreferenceName());
		for (int i = 0; i < this.values.length; i++)
		{
			if (this.values[i] == value)
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
		double value = this.getPreferenceStore().getDefaultDouble(this.getPreferenceName());
		for (int i = 0; i < this.values.length; i++)
		{
			if (this.values[i] == value)
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
		this.getPreferenceStore().setValue(this.getPreferenceName(), this.values[this.combo.getSelectionIndex()]);
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
