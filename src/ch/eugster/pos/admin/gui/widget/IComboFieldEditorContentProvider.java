/*
 * Created on 20.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.gui.widget;

import org.eclipse.jface.util.IPropertyChangeListener;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public interface IComboFieldEditorContentProvider extends IPropertyChangeListener, IFieldEditorContentValidator
{
	
	public abstract void setEditor(ComboFieldEditor editor);
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java
	 * .lang.Object)
	 */
	public Object[] getElements(Object inputElement);
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface
	 * .viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(FieldEditor editor, Object oldInput, Object newInput);
	
	public Object doLoad();
	
	public Object doLoadDefault();
	
	public void doStore();
	
	public boolean doCheckState();
}
