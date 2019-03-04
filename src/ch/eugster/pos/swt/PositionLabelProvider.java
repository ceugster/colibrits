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
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.ProductGroup;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PositionLabelProvider implements org.eclipse.jface.viewers.ITableLabelProvider
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
			return Resources.getImageRegistry().get(Messages.getString("PositionLabelProvider.openbook.gif_1")); //$NON-NLS-1$
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
		if (element instanceof Position)
		{
			Position p = (Position) element;
			switch (columnIndex)
			{
				case 0:
				{
					return p.productId;
				}
				case 1:
				{
					return p.getProductGroup().name;
				}
				case 2:
				{
					NumberFormat nf = NumberFormat.getInstance();
					return nf.format(p.getCurrentTax().percentage) + "%"; //$NON-NLS-1$
				}
				case 3:
				{
					NumberFormat nf = NumberFormat.getInstance();
					return nf.format(p.getQuantity());
				}
				case 4:
				{
					NumberFormat nf = NumberFormat.getCurrencyInstance();
					return nf.format(p.getPrice());
				}
				case 5:
				{
					NumberFormat nf = NumberFormat.getPercentInstance();
					return nf.format(p.getDiscount());
				}
				case 6:
				{
					NumberFormat nf = NumberFormat.getCurrencyInstance();
					return nf.format(p.getAmount());
				}
				case 7:
				{
					return p.getProductGroup().type == ProductGroup.TYPE_EXPENSE ? Messages
									.getString("PositionLabelProvider.Einkauf_2") : Messages.getString("PositionLabelProvider.Verkauf_3"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				case 8:
				{
					return p.optCode;
				}
				case 9:
				{
					return p.author;
				}
				case 10:
				{
					return p.title;
				}
				case 11:
				{
					return p.publisher;
				}
				case 12:
				{
					return p.orderId;
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
	{ Messages.getString("PositionLabelProvider.Art.-Nr._5"), //$NON-NLS-1$
					Messages.getString("PositionLabelProvider.Warengruppe_6"), //$NON-NLS-1$
					Messages.getString("PositionLabelProvider.Mwst_7"), //$NON-NLS-1$
					Messages.getString("PositionLabelProvider.Menge_8"), //$NON-NLS-1$
					Messages.getString("PositionLabelProvider.Preis_9"), //$NON-NLS-1$
					Messages.getString("PositionLabelProvider.Rabatt_10"), //$NON-NLS-1$
					Messages.getString("PositionLabelProvider.Betrag_11"), //$NON-NLS-1$
					Messages.getString("PositionLabelProvider.Ausgabe_11"), //$NON-NLS-1$
					Messages.getString("PositionLabelProvider.Option_12"), //$NON-NLS-1$
					Messages.getString("PositionLabelProvider.Autor_13"), //$NON-NLS-1$
					Messages.getString("PositionLabelProvider.Titel_14"), //$NON-NLS-1$
					Messages.getString("PositionLabelProvider.Verlag_15"), //$NON-NLS-1$
					Messages.getString("PositionLabelProvider.Bestellnummer_16") }; //$NON-NLS-1$
}
