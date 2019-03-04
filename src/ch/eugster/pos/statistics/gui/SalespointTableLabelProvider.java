/*
 * Created on 11.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.statistics.gui;

import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.graphics.Image;

import ch.eugster.pos.db.Salespoint;

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
		return ((Salespoint) element).name;
	}
	
	public ViewerSorter getSorter(int criteria)
	{
		return new SalespointViewerSorter(criteria);
	}
	
	public String[] getColumnNames()
	{
		return this.columnNames;
	}
	
	private String[] columnNames =
	{ "Kassenauswahl" }; //$NON-NLS-1$
}
