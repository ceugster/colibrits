/*
 * Created on 11.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.salespoint;

import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.graphics.Image;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.model.TableLabelProvider;
import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.swt.Resources;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class SalespointTableLabelProvider extends TableLabelProvider
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
			return Resources.getImageRegistry().get(Messages.getString("SalespointTableLabelProvider.lego1.gif_1")); //$NON-NLS-1$
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
					return ((Salespoint) element).name;
				case 1:
					return ((Salespoint) element).place;
				case 2:
					return ((Salespoint) element).active ? Messages.getString("SalespointTableLabelProvider.Ja_1") : Messages.getString("SalespointTableLabelProvider.Nein_2"); //$NON-NLS-1$ //$NON-NLS-2$
				case 3: // 10183
					return Double.toString(((Salespoint) element).stock);
				case 4: // 10183
					return ((Salespoint) element).variableStock ? Messages
									.getString("SalespointTableLabelProvider.Ja_1") : Messages.getString("SalespointTableLabelProvider.Nein_2"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return ""; //$NON-NLS-1$
	}
	
	public ViewerSorter getSorter(int criteria)
	{
		return new SalespointViewerSorter(criteria);
	}
	
	public String[] getColumnNames()
	{
		return this.columnNames;
	}
	
	// 10183 durch Stock und Variabler Stock ergänzt
	private String[] columnNames =
	{
					Messages.getString("SalespointTableLabelProvider.Bezeichnung_4"), Messages.getString("SalespointTableLabelProvider.Standort_5"), Messages.getString("SalespointTableLabelProvider.Aktiv_6"), Messages.getString("SalespointTableLabelProvider.Stock"), Messages.getString("SalespointTableLabelProvider.Variable") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
}
