/*
 * Created on 19.07.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.gui;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.Payment;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.swt.Resources;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ReceiptLabelProvider implements org.eclipse.jface.viewers.ITableLabelProvider
{
	
	private DateFormat df = null;
	
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
			return Resources.getImageRegistry().get("books.gif"); //$NON-NLS-1$
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
		if (this.df == null) this.df = new SimpleDateFormat();
		
		if (element instanceof Receipt)
		{
			Receipt r = (Receipt) element;
			switch (columnIndex)
			{
				case 0:
				{
					return this.df.format(r.timestamp);
				}
				case 1:
				{
					return r.getNumber();
				}
				case 2:
				{
					return r.getSalespoint().name;
				}
				case 3:
				{
					return r.getUser().username;
				}
				case 4:
				{
					return Receipt.STATE_TEXT[r.status];
				}
				case 5:
				{
					if (r.getSettlement() == null)
					{
						return "Nicht abgeschlossen"; //$NON-NLS-1$
					}
					else
					{
						return r.getSettlement().toString();
					}
				}
				case 6:
				{
					NumberFormat nf = NumberFormat.getCurrencyInstance();
					return nf.format(r.getAmount());
				}
				case 7:
				{
					Payment[] ps = r.getPaymentsAsArray();
					for (int i = 0; i < ps.length; i++)
					{
						if (ps[i].isBack())
						{
							String value = null;
							if (ps[i].getForeignCurrencyId().equals(ForeignCurrency.getDefaultCurrency().getId()))
							{
								value = ps[i].getPaymentType().name;
							}
							else
							{
								value = ps[i].getForeignCurrency().getCurrency().getSymbol();
							}
							return value;
						}
					}
				}
				case 8:
					return r.getTransactionId() == null ? "" : r.getTransactionId().toString();
				case 9:
				{
					return r.getBookingId() == null ? "" : r.getBookingId().toString();
				}
			}
		}
		return ""; //$NON-NLS-1$
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.swt.ITableLabelProvider#getColumnNames()
	 */
	public String[] getColumnNames()
	{
		return ReceiptLabelProvider.columnNames;
	}
	
	public int[] getColumnAlignments()
	{
		return ReceiptLabelProvider.columnAlignments;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.swt.ITableLabelProvider#getSorter(int)
	 */
	public ViewerSorter getSorter(int criteria)
	{
		return null;
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
	
	private static final String[] columnNames =
	{ Messages.getString("ReceiptLabelProvider.Datum_3"), //$NON-NLS-1$
					Messages.getString("ReceiptLabelProvider.Nummer_4"), //$NON-NLS-1$
					Messages.getString("ReceiptLabelProvider.Kasse_5"), //$NON-NLS-1$
					Messages.getString("ReceiptLabelProvider.Benutzer_6"), //$NON-NLS-1$
					Messages.getString("ReceiptLabelProvider.Status_7"), //$NON-NLS-1$
					Messages.getString("ReceiptLabelProvider.Buchung_8"), //$NON-NLS-1$
					Messages.getString("ReceiptLabelProvider.Betrag_9"), //$NON-NLS-1$
					"Rückgeld", Messages.getString("ReceiptLabelProvider.Transaktion_10"), //$NON-NLS-1$
					Messages.getString("ReceiptLabelProvider.Buchung_11") }; //$NON-NLS-1$
	
	private static final int[] columnAlignments =
	{ SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.RIGHT, SWT.LEFT, // $NON-NLS-1$
					SWT.LEFT, SWT.LEFT };
}
