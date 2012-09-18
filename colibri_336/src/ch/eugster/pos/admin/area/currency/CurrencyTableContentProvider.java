/*
 * Created on 11.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.currency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.ojb.broker.util.collections.RemovalAwareCollection;
import org.eclipse.jface.dialogs.MessageDialog;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.event.IPaymentTypeChangeListener;
import ch.eugster.pos.admin.event.PaymentTypeChangeEvent;
import ch.eugster.pos.admin.model.TableContentProvider;
import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.PaymentType;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class CurrencyTableContentProvider extends TableContentProvider implements IPaymentTypeChangeListener
{
	
	private static CurrencyTableContentProvider contentProvider;
	
	private CurrencyTableContentProvider()
	{
	}
	
	public static CurrencyTableContentProvider getInstance()
	{
		if (CurrencyTableContentProvider.contentProvider == null)
			CurrencyTableContentProvider.contentProvider = new CurrencyTableContentProvider();
		
		return CurrencyTableContentProvider.contentProvider;
	}
	
	public Object[] getElements(Object element)
	{
		PaymentType[] pt = PaymentType.selectAll(false);
		Collection usedCurrencies = new ArrayList();
		for (int i = 0; i < pt.length; i++)
		{
			if (!usedCurrencies.contains(pt[i].getForeignCurrency().code))
			{
				usedCurrencies.add(pt[i].getForeignCurrency().code);
			}
		}
		
		RemovalAwareCollection fcs = ForeignCurrency.selectAllUsed(false);
		
		Iterator i = fcs.iterator();
		while (i.hasNext())
		{
			ForeignCurrency fc = (ForeignCurrency) i.next();
			if (usedCurrencies.contains(fc.code))
			{
				fc.isUsed = true;
			}
			else
			{
				fc.isUsed = false;
			}
		}
		return fcs.toArray(new ForeignCurrency[0]);
	}
	
	public boolean deleteElement(Object element)
	{
		if (element instanceof ForeignCurrency)
		{
			ForeignCurrency foreignCurrency = (ForeignCurrency) element;
			DBResult result = foreignCurrency.delete();
			if (result.getErrorCode() != 0)
			{
				MessageDialog
								.openError(
												this.viewer.getControl().getShell(),
												Messages.getString("CurrencyTableContentProvider.Fehler_1"), Messages.getString("CurrencyTableContentProvider.Die_W_u00E4hrung__2") + foreignCurrency.code + " (" + foreignCurrency.region + Messages.getString("CurrencyTableContentProvider.)_konnte_nicht_gel_u00F6scht_werden._1")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			}
			return result.getErrorCode() == 0;
		}
		return false;
	}
	
	public void paymentTypeChange(PaymentTypeChangeEvent event)
	{
		if (event.foreignCurrencyChanged())
		{
			this.viewer.setInput(new Object());
		}
	}
}
