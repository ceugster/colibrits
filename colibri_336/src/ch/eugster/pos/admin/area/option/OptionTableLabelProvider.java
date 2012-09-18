/*
 * Created on 11.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.option;

import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.graphics.Image;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.model.TableLabelProvider;
import ch.eugster.pos.db.Option;
import ch.eugster.pos.swt.Resources;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class OptionTableLabelProvider extends TableLabelProvider
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
			return Resources.getImageRegistry().get(Messages.getString("OptionTableLabelProvider.dot.gif_1")); //$NON-NLS-1$
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
		assert columnIndex >= 0 && columnIndex < this.columnNames.length;
		
		if (element == null)
		{
			return this.columnNames[columnIndex];
		}
		else
		{
			switch (columnIndex)
			{
				case 0:
					return ((Option) element).code;
				case 1:
					return ((Option) element).name;
			}
		}
		return ""; //$NON-NLS-1$
	}
	
	public String[] getColumnNames()
	{
		return this.columnNames;
	}
	
	public ViewerSorter getSorter(int column)
	{
		return new OptionViewerSorter(column);
	}
	
	private String[] columnNames =
	{
					Messages.getString("OptionTableLabelProvider.Code_2"), Messages.getString("OptionTableLabelProvider.Bezeichnung_3") }; //$NON-NLS-1$ //$NON-NLS-2$
}
