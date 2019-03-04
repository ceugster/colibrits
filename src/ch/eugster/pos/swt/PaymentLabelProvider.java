/*
 * Created on 19.07.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.swt;

import java.text.NumberFormat;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.Payment;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PaymentLabelProvider implements org.eclipse.jface.viewers.ITableLabelProvider
{
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang
	 * .Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex)
	{
		if (columnIndex == 0)
		{
			return Resources.getImageRegistry().get(Messages.getString("PaymentLabelProvider.money.gif_1")); //$NON-NLS-1$
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang
	 * .Object, int)
	 */
	public String getColumnText(Object element, int columnIndex)
	{
		if (element instanceof Payment)
		{
			Payment p = (Payment) element;
			switch (columnIndex)
			{
				case 0:
				{
					String backMoney = "Rückgeld ";
					String value = null;
					if (p.getForeignCurrencyId().equals(ForeignCurrency.getDefaultCurrency().getId()))
					{
						value = p.getPaymentType().name;
					}
					else
					{
						value = p.getForeignCurrency().getCurrency().getSymbol();
					}
					if (p.isBack()) return backMoney + value;
					
					return value;
				}
				case 1:
				{
					NumberFormat nf = NumberFormat.getInstance();
					return nf.format(p.getQuotation());
				}
				case 2:
				{
					NumberFormat nf = NumberFormat.getCurrencyInstance();
					return nf.format(p.getAmount());
					
				}
				case 3:
				{
					return ForeignCurrency.getFormattedAmount(p.getAmountFC(), p.getForeignCurrency());
				}
			}
		}
		return ""; //$NON-NLS-1$
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.
	 * jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener)
	{
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose()
	{
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang
	 * .Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property)
	{
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse
	 * .jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener)
	{
	}
	
	public static final String[] COLUMN_NAMES =
	{ Messages.getString("PaymentLabelProvider.Zahlungsart_4"), //$NON-NLS-1$
					Messages.getString("PaymentLabelProvider.Kurs_5"), //$NON-NLS-1$
					Messages.getString("PaymentLabelProvider.Betrag_6"), //$NON-NLS-1$
					Messages.getString("PaymentLabelProvider.Betrag_FW_7") }; //$NON-NLS-1$
}
