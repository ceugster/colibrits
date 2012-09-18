/*
 * Created on 20.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.tax;

import org.eclipse.jface.util.PropertyChangeEvent;

import ch.eugster.pos.admin.gui.widget.ComboFieldEditor;
import ch.eugster.pos.admin.gui.widget.ComboFieldEditorContentProvider;
import ch.eugster.pos.db.CurrentTax;
import ch.eugster.pos.db.Tax;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class CurrentTaxComboFieldEditorContentProvider extends ComboFieldEditorContentProvider
{
	
	/**
	 * 
	 */
	public CurrentTaxComboFieldEditorContentProvider(ComboFieldEditor editor)
	{
		super(editor);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java
	 * .lang.Object)
	 */
	public Object[] getElements(Object inputElement)
	{
		Object[] currentTaxes = null;
		if (inputElement instanceof Tax)
		{
			currentTaxes = CurrentTax.selectByTax((Tax) inputElement, false);
		}
		return currentTaxes == null ? new CurrentTax[0] : currentTaxes;
	}
	
	public Object doLoad()
	{
		return this.editor.getStore().getValue(this.editor.getName());
	}
	
	public Object doLoadDefault()
	{
		return this.editor.getStore().getDefaultValue(this.editor.getName());
	}
	
	public void doStore()
	{
		if (this.editor.isValid())
		{
			this.editor.getStore().setValue(this.editor.getName(), this.editor.getSelectedItem());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.admin.widgets.IComboFieldEditorContentProvider#doCheckState
	 * ()
	 */
	public boolean doCheckState()
	{
		boolean result = false;
		if (this.editor.getSelectedItem() == null)
		{
			return result;
		}
		
		if (this.editor.getCombo().getSelectionIndex() < 0)
		{
			return result;
		}
		
		if (this.editor.getSelectedItem() instanceof CurrentTax)
		{
			result = true;
		}
		return result;
	}
	
	public void propertyChange(PropertyChangeEvent e)
	{
		if (e.getProperty().equals("currentTax")) { //$NON-NLS-1$
			if (e.getNewValue() instanceof CurrentTax)
			{
				if (this.editor != null)
				{
					this.editor.setInput(this.editor.getStore().getElement());
					this.editor.load();
				}
			}
		}
	}
	
	protected boolean isValid(Object o)
	{
		return o instanceof CurrentTax;
	}
}
