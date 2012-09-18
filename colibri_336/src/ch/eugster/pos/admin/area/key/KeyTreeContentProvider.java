/*
 * Created on 09.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.key;

import org.eclipse.jface.dialogs.MessageDialog;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.model.TreeContentProvider;
import ch.eugster.pos.db.Block;
import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.Tab;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class KeyTreeContentProvider extends TreeContentProvider
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
		Object[] result = new Object[0];
		if (parentElement instanceof Block)
		{
			result = Tab.selectByBlock(((Block) parentElement));
		}
		else if (parentElement instanceof Tab)
		{
			result = new Object[0];
		}
		else
		{
			result = Block.selectAll(false);
		}
		return result;
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
		if (element instanceof Tab)
		{
			result = ((Tab) element).getBlock();
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
		if (element instanceof Block)
		{
			MessageDialog
							.openError(
											this.viewer.getControl().getShell(),
											Messages.getString("KeyTreeContentProvider.Fehler_1"), Messages.getString("KeyTreeContentProvider.Bereiche_d_u00FCrfen_nicht_gel_u00F6scht_werden._2")); //$NON-NLS-1$ //$NON-NLS-2$
			return false;
		}
		else if (element instanceof Tab)
		{
			Tab tab = (Tab) element;
			DBResult result = tab.delete();
			if (result.getErrorCode() != 0)
			{
				MessageDialog
								.openError(
												this.viewer.getControl().getShell(),
												Messages.getString("KeyTreeContentProvider.Fehler_3"), Messages.getString("KeyTreeContentProvider.Das_Register__4") + tab.title + Messages.getString("KeyTreeContentProvider._konnte_nicht_gel_u00F6scht_werden._5")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			return result.getErrorCode() == 0;
		}
		return false;
	}
	
}
