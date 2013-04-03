/*
 * Created on 11.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.currency;

import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.graphics.Image;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.model.TableLabelProvider;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.swt.Resources;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class CurrencyTableLabelProvider extends TableLabelProvider
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
			if (((ForeignCurrency) element).isUsed)
			{
				return Resources.getImageRegistry().get(Messages.getString("CurrencyTableLabelProvider.money.gif_1")); //$NON-NLS-1$
			}
			else
			{
				return Resources.getImageRegistry().get(Messages.getString("CurrencyTableLabelProvider.money2.gif_2")); //$NON-NLS-1$
			}
		}
		else
		{
			return null;
		}
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
		if (element == null)
		{
			return this.columnNames[columnIndex];
		}
		else
		{
			switch (columnIndex)
			{
				case 0:
					return ((ForeignCurrency) element).code;
				case 1:
					return ((ForeignCurrency) element).name;
				case 2:
					return ((ForeignCurrency) element).region;
			}
		}
		return ""; //$NON-NLS-1$
	}
	
	public ViewerSorter getSorter(int criteria)
	{
		return new CurrencyViewerSorter(criteria);
	}
	
	public String[] getColumnNames()
	{
		return this.columnNames;
	}
	
	private String[] columnNames =
	{
					Messages.getString("CurrencyTableLabelProvider.Code_2"), Messages.getString("CurrencyTableLabelProvider.Bezeichnung_3"), Messages.getString("CurrencyTableLabelProvider.Gebiet_4") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
}
