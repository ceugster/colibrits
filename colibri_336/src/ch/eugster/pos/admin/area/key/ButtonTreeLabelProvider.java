/*
 * Created on 10.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.key;

import org.eclipse.swt.graphics.Image;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.model.SimpleLabelProvider;
import ch.eugster.pos.db.Function;
import ch.eugster.pos.db.KeyGroup;
import ch.eugster.pos.db.Option;
import ch.eugster.pos.db.PaymentType;
import ch.eugster.pos.db.PaymentTypeGroup;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.db.TaxRate;
import ch.eugster.pos.swt.Resources;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ButtonTreeLabelProvider extends SimpleLabelProvider
{
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element)
	{
		if (element instanceof KeyGroup)
		{
			return Resources.getImageRegistry().get(Messages.getString("ButtonTreeLabelProvider.folder.gif_1")); //$NON-NLS-1$
		}
		if (element instanceof PaymentTypeGroup)
		{
			return Resources.getImageRegistry().get(Messages.getString("ButtonTreeLabelProvider.folder.gif_2")); //$NON-NLS-1$
		}
		else if (element instanceof ProductGroup)
		{
			return Resources.getImageRegistry().get(Messages.getString("ButtonTreeLabelProvider.books.gif_3")); //$NON-NLS-1$
		}
		else if (element instanceof PaymentType)
		{
			return Resources.getImageRegistry().get(Messages.getString("ButtonTreeLabelProvider.money.gif_4")); //$NON-NLS-1$
		}
		else if (element instanceof TaxRate)
		{
			return Resources.getImageRegistry().get(Messages.getString("ButtonTreeLabelProvider.money.gif_5")); //$NON-NLS-1$
		}
		else if (element instanceof Option)
		{
			return Resources.getImageRegistry().get(Messages.getString("ButtonTreeLabelProvider.dot.gif_6")); //$NON-NLS-1$
		}
		else if (element instanceof Function)
		{
			return Resources.getImageRegistry().get(Messages.getString("ButtonTreeLabelProvider.compass.gif_7")); //$NON-NLS-1$
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
		if (element instanceof KeyGroup)
		{
			return ((KeyGroup) element).name;
		}
		else if (element instanceof ProductGroup)
		{
			ProductGroup gp = (ProductGroup) element;
			StringBuffer name = new StringBuffer(gp.name);
			if (gp.galileoId != null && gp.galileoId.length() > 0)
			{
				name.append(": " + gp.galileoId);
			}
			switch (gp.type)
			{
				case 0:
					name.append("; U");
					break;
				case 1:
					name.append("; N");
					break;
				case 2:
					name.append("; A");
					break;
			}
			return name.toString();
		}
		else if (element instanceof PaymentTypeGroup)
		{
			return ((PaymentTypeGroup) element).name;
		}
		else if (element instanceof PaymentType)
		{
			return ((PaymentType) element).name;
		}
		else if (element instanceof TaxRate)
		{
			return ((TaxRate) element).name;
		}
		else if (element instanceof Option)
		{
			return ((Option) element).name;
		}
		else if (element instanceof Function)
		{
			return ((Function) element).shortname;
		}
		return ""; //$NON-NLS-1$
	}
	
}
