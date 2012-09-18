/*
 * Created on 26.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.salespoint;

import org.eclipse.swt.graphics.Image;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.model.SimpleLabelProvider;
import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.db.Stock;
import ch.eugster.pos.swt.Resources;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class SalespointTreeLabelProvider extends SimpleLabelProvider
{
	
	/**
	 * 
	 */
	public SalespointTreeLabelProvider()
	{
		super();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element)
	{
		if (element instanceof Salespoint)
		{
			return Resources.getImageRegistry().get(Messages.getString("SalespointTableLabelProvider.lego1.gif_1")); //$NON-NLS-1$
		}
		else if (element instanceof Stock)
		{
			return Resources.getImageRegistry().get(Messages.getString("CoinTableLabelProvider.money.gif_1")); //$NON-NLS-1$
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
		if (element instanceof Salespoint)
		{
			Salespoint salespoint = (Salespoint) element;
			StringBuffer buffer = new StringBuffer().append(salespoint.name);
			return buffer.toString();
		}
		else if (element instanceof Stock)
		{
			Stock stock = (Stock) element;
			StringBuffer buffer = new StringBuffer("Kassenstock Währung: ");
			return buffer.append(stock.getForeignCurrency().code).toString();
		}
		return ""; //$NON-NLS-1$
	}
}
