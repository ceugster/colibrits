/*
 * Created on 16.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.gui.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class DoubleComboFieldEditor extends FieldEditor
{
	
	// The checkbox to select the item.
	private Combo combo;
	
	private Double[] values;
	
	private Double newValue = new Double(0);
	
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
	public DoubleComboFieldEditor(String name, String labelText, Composite parent, String[] list, Double[] values)
	{
		super(name, labelText, parent);
		this.combo.setItems(list);
		this.values = values;
	}
	
	public Combo getCombo()
	{
		return this.combo;
	}
	
	public void setItems(String[] items, Double[] values)
	{
		this.values = values;
		Double value = new Double(0d);
		if (this.combo.getSelectionIndex() == -1)
		{
			value = this.getStore().getDouble(this.getName());
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
		GridData gd = (GridData) this.combo.getLayoutData();
		gd.horizontalSpan = numColumns - 1;
		// We only grab excess space if we have to
		// If another field editor has more columns then
		// we assume it is setting the width.
		gd.grabExcessHorizontalSpace = gd.horizontalSpan == 1;
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
		this.getLabelControl(parent);
		
		this.combo = this.getComboControl(parent);
		GridData gd = new GridData();
		gd.horizontalSpan = numColumns - 1;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		this.combo.setLayoutData(gd);
		this.combo.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				DoubleComboFieldEditor.this.selectionChanged(e);
			}
		});
	}
	
	public Combo getComboControl(Composite parent)
	{
		if (this.combo == null)
		{
			this.combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
			this.combo.setFont(parent.getFont());
			this.combo.addDisposeListener(new DisposeListener()
			{
				public void widgetDisposed(DisposeEvent event)
				{
					DoubleComboFieldEditor.this.combo = null;
				}
			});
		}
		else
		{
			this.checkParent(this.combo, parent);
		}
		return this.combo;
	}
	
	private void selectionChanged(SelectionEvent e)
	{
		if (e.getSource().equals(this.combo))
		{
			this.setPresentsDefaultValue(false);
			boolean oldState = this.isValid;
			
			this.refreshValidState();
			if (this.isValid != oldState) this.fireStateChanged(FieldEditor.IS_VALID, oldState, this.isValid);
			
			Double oldValue = this.newValue;
			this.newValue = this.values[this.combo.getSelectionIndex()];
			
			if (!oldValue.equals(this.newValue))
			{
				this.doStore();
				this.fireValueChanged(this.getName(), oldValue, this.newValue);
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
		Double value = this.getStore().getDouble(this.getName());
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
		Double value = this.getStore().getDefaultDouble(this.getName());
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
		this.getStore().setValue(this.getName(), this.values[this.combo.getSelectionIndex()]);
	}
	
	/**
	 * Checks whether the text input field contains a valid value or not.
	 * 
	 * @return <code>true</code> if the field value is valid, and
	 *         <code>false</code> if invalid
	 */
	protected boolean checkState()
	{
		boolean result = true;
		if (this.newValue == null) result = false;
		
		// call hook for subclasses
		result = result && this.doCheckState();
		
		if (result)
			this.clearErrorMessage();
		else
			this.showErrorMessage(""); //$NON-NLS-1$
			
		return result;
	}
	
	/**
	 * Hook for subclasses to do specific state checks.
	 * <p>
	 * The default implementation of this framework method does nothing and
	 * returns <code>true</code>. Subclasses should override this method to
	 * specific state checks.
	 * </p>
	 * 
	 * @return <code>true</code> if the field value is valid, and
	 *         <code>false</code> if invalid
	 */
	protected boolean doCheckState()
	{
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#getNumberOfControls()
	 */
	public int getNumberOfControls()
	{
		return 2;
	}
	
}
