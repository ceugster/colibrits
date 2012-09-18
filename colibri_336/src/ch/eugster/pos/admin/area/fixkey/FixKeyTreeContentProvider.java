/*
 * Created on 23.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.fixkey;

import org.eclipse.jface.dialogs.MessageDialog;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.model.TreeContentProvider;
import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.FixKey;
import ch.eugster.pos.db.FixKeyGroup;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class FixKeyTreeContentProvider extends TreeContentProvider
{
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.
	 * Object)
	 */
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof FixKeyGroup)
		{
			return FixKey.selectByGroup((FixKeyGroup) parentElement);
		}
		else if (parentElement instanceof FixKey)
		{
			return new Object[0];
		}
		else
		{
			return FixKeyGroup.selectAll();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object
	 * )
	 */
	public Object getParent(Object element)
	{
		Object result = null;
		if (element instanceof FixKey)
		{
			result = ((FixKey) element).getFixKeyGroup();
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.
	 * Object)
	 */
	public boolean hasChildren(Object element)
	{
		return this.getChildren(element).length > 0;
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
		return this.getChildren(inputElement);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.admin.model.IContentProvider#deleteElement(java.lang.Object
	 * )
	 */
	public boolean deleteElement(Object element)
	{
		if (element instanceof FixKeyGroup)
		{
			FixKeyGroup group = (FixKeyGroup) element;
			DBResult result = group.delete();
			if (result.getErrorCode() != 0)
			{
				MessageDialog
								.openError(
												this.viewer.getControl().getShell(),
												Messages.getString("FixKeyTreeContentProvider.Fehler_1"), Messages.getString("FixKeyTreeContentProvider.Die_Gruppe__2") + group.name + Messages.getString("FixKeyTreeContentProvider._konnte_nicht_gel_u00F6scht_werden._3")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			return result.getErrorCode() == 0;
		}
		else if (element instanceof FixKey)
		{
			FixKey fixKey = (FixKey) element;
			DBResult result = fixKey.delete();
			if (result.getErrorCode() != 0)
			{
				MessageDialog
								.openError(
												this.viewer.getControl().getShell(),
												Messages.getString("FixKeyTreeContentProvider.Fehler_4"), Messages.getString("FixKeyTreeContentProvider.Die_Gruppe__5") + fixKey.text + Messages.getString("FixKeyTreeContentProvider._konnte_nicht_gel_u00F6scht_werden._6")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			return result.getErrorCode() == 0;
		}
		return false;
	}
	
}
