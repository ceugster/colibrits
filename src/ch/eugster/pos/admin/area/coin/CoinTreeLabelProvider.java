/*
 * Created on 26.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.coin;

import org.eclipse.swt.graphics.Image;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.model.SimpleLabelProvider;
import ch.eugster.pos.db.Coin;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.swt.Resources;
import ch.eugster.pos.util.NumberUtility;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class CoinTreeLabelProvider extends SimpleLabelProvider
{
	
	/**
	 * 
	 */
	public CoinTreeLabelProvider()
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
		if (element instanceof ForeignCurrency)
		{
			return Resources.getImageRegistry().get("addtsk_tsk.gif"); //$NON-NLS-1$
		}
		else if (element instanceof Coin)
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
		if (element instanceof ForeignCurrency)
		{
			ForeignCurrency currency = (ForeignCurrency) element;
			return currency.code + " " + currency.name + " " + currency.region;
		}
		else if (element instanceof Coin)
		{
			return NumberUtility.formatDouble(((Coin) element).value, 2, 2, true);
		}
		return ""; //$NON-NLS-1$
	}
}
