/*
 * Created on 27.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.payment;

import org.eclipse.swt.graphics.Image;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.model.SimpleLabelProvider;
import ch.eugster.pos.db.PaymentType;
import ch.eugster.pos.db.PaymentTypeGroup;
import ch.eugster.pos.swt.Resources;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class PaymentTypeTreeLabelProvider extends SimpleLabelProvider
{
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element)
	{
		if (element instanceof PaymentTypeGroup)
		{
			return Resources.getImageRegistry().get(Messages.getString("PaymentTypeTreeLabelProvider.books.gif_1")); //$NON-NLS-1$
		}
		else if (element instanceof PaymentType)
		{
			return Resources.getImageRegistry()
							.get(Messages.getString("PaymentTypeTreeLabelProvider.closedbook.gif_2")); //$NON-NLS-1$
		}
		else
		{
			return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element)
	{
		if (element instanceof PaymentTypeGroup)
		{
			return ((PaymentTypeGroup) element).name;
		}
		else if (element instanceof PaymentType)
		{
			return ((PaymentType) element).name;
		}
		return null;
	}
	
}
