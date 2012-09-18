/*
 * Created on 09.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.fixkey;

import org.eclipse.swt.graphics.Image;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.model.SimpleLabelProvider;
import ch.eugster.pos.db.FixKey;
import ch.eugster.pos.db.FixKeyGroup;
import ch.eugster.pos.swt.Resources;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class FixKeyLabelProvider extends SimpleLabelProvider
{
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element)
	{
		return Resources.getImageRegistry().get(Messages.getString("FixKeyLabelProvider.hand.gif_1")); //$NON-NLS-1$
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element)
	{
		String result = ""; //$NON-NLS-1$
		if (element instanceof FixKeyGroup)
		{
			result = ((FixKeyGroup) element).name;
		}
		else if (element instanceof FixKey)
		{
			result = ((FixKey) element).text;
		}
		return result;
	}
	
}
