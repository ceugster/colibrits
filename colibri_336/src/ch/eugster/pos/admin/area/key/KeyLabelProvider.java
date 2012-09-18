/*
 * Created on 09.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.key;

import org.eclipse.swt.graphics.Image;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.model.SimpleLabelProvider;
import ch.eugster.pos.db.Block;
import ch.eugster.pos.db.Tab;
import ch.eugster.pos.swt.Resources;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class KeyLabelProvider extends SimpleLabelProvider
{
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element)
	{
		return Resources.getImageRegistry().get(Messages.getString("KeyLabelProvider.folder.gif_1")); //$NON-NLS-1$
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element)
	{
		String result = ""; //$NON-NLS-1$
		if (element instanceof Block)
		{
			result = ((Block) element).name;
		}
		else if (element instanceof Tab)
		{
			result = ((Tab) element).title;
		}
		return result;
	}
	
}
