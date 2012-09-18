/*
 * Created on 05.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.gui.widget;

import org.eclipse.jface.viewers.ILabelProvider;
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
public class ComboFieldEditor extends FieldEditor
{
	
	// The top-level control for the field editor.
	private Composite top;
	// The checkbox to select the item.
	private Combo combo;
	// The items.
	private Object[] array;
	// The currently selected item.
	private Object newValue;
	// The old Object
	private Object oldValue;
	// The Content Provider
	private IComboFieldEditorContentProvider contentProvider;
	// The Label Provider
	private ILabelProvider labelProvider;
	
	/**
	 * The error message, or <code>null</code> if none.
	 */
	private String errorMessage;
	
	/**
	 * 
	 */
	public ComboFieldEditor()
	{
		super();
	}
	
	/**
	 * @param name
	 * @param labelText
	 * @param parent
	 */
	public ComboFieldEditor(String name, String labelText, Composite parent)
	{
		super(name, labelText, parent);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.FieldEditor#adjustForNumColumns(int)
	 */
	protected void adjustForNumColumns(int numColumns)
	{
		((GridData) this.top.getLayoutData()).horizontalSpan = numColumns;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.admin.framework.FieldEditor#doFillIntoGrid(org.eclipse
	 * .swt.widgets.Composite, int)
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
				ComboFieldEditor.this.selectionChanged(e);
			}
		});
	}
	
	public Combo getCombo()
	{
		return this.combo;
	}
	
	/**
	 * sets the content provider or null if parameter equals null
	 * 
	 * @param provider
	 */
	public void setContentProvider(IComboFieldEditorContentProvider provider)
	{
		this.contentProvider = provider;
	}
	
	/**
	 * returns the content provider or null
	 */
	public IComboFieldEditorContentProvider getContentProvider()
	{
		return this.contentProvider;
	}
	
	/**
	 * sets the content provider or null if parameter equals null
	 * 
	 * @param provider
	 */
	public void setLabelProvider(ILabelProvider provider)
	{
		this.labelProvider = provider;
	}
	
	/**
	 * returns the content provider or null
	 */
	public ILabelProvider getLabelProvider()
	{
		return this.labelProvider;
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoad()
	{
		if (this.combo != null && this.contentProvider != null)
		{
			this.oldValue = this.newValue;
			this.newValue = this.contentProvider.doLoad();
			String text = this.labelProvider.getText(this.newValue);
			for (int i = 0; i < this.combo.getItemCount(); i++)
			{
				if (this.combo.getItem(i).equals(text))
				{
					this.combo.select(i);
					return;
				}
			}
			this.refreshValidState();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.FieldEditor#doLoadDefault()
	 */
	protected void doLoadDefault()
	{
		if (this.combo != null && this.contentProvider != null)
		{
			this.oldValue = this.newValue;
			this.newValue = this.contentProvider.doLoadDefault();
			// if (!newValue.equals(oldValue)) {
			String text = this.labelProvider.getText(this.newValue);
			this.combo.deselectAll();
			for (int i = 0; i < this.combo.getItemCount(); i++)
			{
				if (this.combo.getItem(i).equals(text))
				{
					this.combo.select(i);
					return;
				}
			}
			// }
		}
	}
	
	protected void selectItem(Object object)
	{
		
	}
	
	public void setInput(Object o)
	{
		if (this.contentProvider != null)
		{
			this.array = this.contentProvider.getElements(o);
			this.combo.removeAll();
			for (int i = 0; i < this.array.length; i++)
			{
				if (this.labelProvider != null)
				{
					this.combo.add(this.labelProvider.getText(this.array[i]));
				}
				else
				{
					this.combo.add(this.array[i].toString());
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.FieldEditor#doStore()
	 */
	protected void doStore()
	{
		if (this.contentProvider != null)
		{
			this.contentProvider.doStore();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.FieldEditor#getNumberOfControls()
	 */
	public int getNumberOfControls()
	{
		return 1;
	}
	
	/**
	 * Refreshes this field editor's valid state after a value change and fires
	 * an <code>IS_VALID</code> property change event if warranted.
	 * <p>
	 * The default implementation of this framework method does nothing.
	 * Subclasses wishing to perform validation should override both this method
	 * and <code>isValid</code>.
	 * </p>
	 * 
	 * @see #isValid
	 */
	protected void refreshValidState()
	{
		this.setValid(this.checkState());
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
		{
			this.clearErrorMessage();
		}
		else
		{
			this.getPage().setValid(false);
			this.errorMessage = "Die Mehrwertsteuerart ist nicht ausgewählt.";
			this.showErrorMessage(this.errorMessage);
		}
		
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
		this.setValid(this.contentProvider.doCheckState());
		if (!this.isValid())
		{
			
		}
		return this.isValid();
	}
	
	public void widgetSelected(SelectionEvent e)
	{
	}
	
	public void widgetDefaultSelected(SelectionEvent e)
	{
	}
	
	private void selectionChanged(SelectionEvent e)
	{
		if (e.getSource().equals(this.combo))
		{
			this.setPresentsDefaultValue(false);
			boolean oldState = this.isValid();
			
			this.refreshValidState();
			if (this.isValid() != oldState) this.fireStateChanged(FieldEditor.IS_VALID, oldState, this.isValid());
			
			this.oldValue = this.newValue;
			this.newValue = this.array[this.combo.getSelectionIndex()];
			if (!this.oldValue.equals(this.newValue))
			{
				this.doStore();
				this.fireValueChanged(this.getName(), this.oldValue, this.newValue);
			}
		}
	}
	
	public Object getSelectedItem()
	{
		return this.newValue;
	}
	
	public Object[] getItems()
	{
		return this.array;
	}
	
}
