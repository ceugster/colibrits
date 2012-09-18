/*
 * Created on 27.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.gui.widget;

import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public abstract class ComboFieldEditorContentProvider implements IComboFieldEditorContentProvider,
				IFieldEditorContentValidator
{
	
	/**
	 * 
	 */
	public ComboFieldEditorContentProvider()
	{
		super();
	}
	
	/**
	 * 
	 */
	public ComboFieldEditorContentProvider(ComboFieldEditor editor)
	{
		this.editor = editor;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.admin.widgets.IComboFieldEditorContentProvider#setEditor
	 * (ch.eugster.pos.admin.widgets.ComboFieldEditor)
	 */
	public void setEditor(ComboFieldEditor editor)
	{
		this.editor = editor;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.admin.widgets.IComboFieldEditorContentProvider#getElements
	 * (java.lang.Object)
	 */
	public abstract Object[] getElements(Object inputElement);
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.admin.widgets.IComboFieldEditorContentProvider#dispose()
	 */
	public void dispose()
	{
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.admin.widgets.IComboFieldEditorContentProvider#inputChanged
	 * (ch.eugster.pos.admin.widgets.FieldEditor, java.lang.Object,
	 * java.lang.Object)
	 */
	public void inputChanged(FieldEditor editor, Object oldInput, Object newInput)
	{
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.admin.widgets.IComboFieldEditorContentProvider#doLoad()
	 */
	public abstract Object doLoad();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.admin.widgets.IComboFieldEditorContentProvider#doLoadDefault
	 * ()
	 */
	public abstract Object doLoadDefault();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.admin.widgets.IComboFieldEditorContentProvider#doStore()
	 */
	public abstract void doStore();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse
	 * .jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event)
	{
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.admin.widgets.IFieldEditorContentValidator#isValid(ch.
	 * eugster.pos.admin.widgets.FieldEditor)
	 */
	public boolean isValid(FieldEditor editor)
	{
		boolean result = true;
		if (editor instanceof ComboFieldEditor)
		{
			Object o = ((ComboFieldEditor) editor).getSelectedItem();
			result = this.isValid(o);
		}
		return result;
	}
	
	protected abstract boolean isValid(Object object);
	
	protected ComboFieldEditor editor;
}
