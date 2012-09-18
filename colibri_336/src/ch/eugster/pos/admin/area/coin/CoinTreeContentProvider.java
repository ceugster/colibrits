/*
 * Created on 26.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.coin;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.dialogs.MessageDialog;

import ch.eugster.pos.admin.event.IPaymentTypeChangeListener;
import ch.eugster.pos.admin.event.PaymentTypeChangeEvent;
import ch.eugster.pos.admin.model.TreeContentProvider;
import ch.eugster.pos.db.Coin;
import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.PaymentType;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class CoinTreeContentProvider extends TreeContentProvider implements IPaymentTypeChangeListener
{
	
	private static CoinTreeContentProvider contentProvider;
	
	private CoinTreeContentProvider()
	{
	}
	
	public static CoinTreeContentProvider getInstance()
	{
		if (CoinTreeContentProvider.contentProvider == null)
			CoinTreeContentProvider.contentProvider = new CoinTreeContentProvider();
		
		return CoinTreeContentProvider.contentProvider;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.
	 * Object)
	 */
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof ForeignCurrency)
		{
			ForeignCurrency currency = (ForeignCurrency) parentElement;
			return Coin.selectByForeignCurrency(currency);
		}
		else if (parentElement instanceof Coin)
		{
			return new Object[0];
		}
		else
		{
			PaymentType[] pt = PaymentType.selectAll(false);
			Collection currencies = new ArrayList();
			for (int i = 0; i < pt.length; i++)
			{
				if (!currencies.contains(pt[i].getForeignCurrency()))
				{
					currencies.add(pt[i].getForeignCurrency());
				}
			}
			return currencies.toArray(new ForeignCurrency[0]);
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
		if (element instanceof Coin)
		{
			return ((Coin) element).getForeignCurrency();
		}
		else
			return null;
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
	
	public boolean deleteElement(Object element)
	{
		if (element instanceof ForeignCurrency)
		{
			ForeignCurrency currency = (ForeignCurrency) element;
			DBResult result = currency.delete();
			if (result.getErrorCode() != 0)
			{
				MessageDialog.openError(this.viewer.getControl().getShell(),
								"Fehler", "Die Währung " + currency.name + " kann nicht gelöscht werden"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			return result.getErrorCode() == 0;
		}
		else if (element instanceof Coin)
		{
			Coin coin = (Coin) element;
			DBResult result = coin.delete();
			if (result.getErrorCode() != 0)
			{
				MessageDialog.openError(this.viewer.getControl().getShell(),
								"Fehler", "Die Einheit" + coin.value + " konnte nicht gelöscht werden."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			return result.getErrorCode() == 0;
		}
		return false;
	}
	
	public void paymentTypeChange(PaymentTypeChangeEvent event)
	{
		this.viewer.setInput(new Object());
	}
}
