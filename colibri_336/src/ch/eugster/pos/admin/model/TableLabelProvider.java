/*
 * Created on 04.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.model;

import org.eclipse.swt.graphics.Image;

import ch.eugster.pos.swt.BaseLabelProvider;
import ch.eugster.pos.swt.ITableLabelProvider;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public abstract class TableLabelProvider extends BaseLabelProvider implements ITableLabelProvider
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
	public abstract String getColumnText(Object element, int columnIndex);
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.admin.gui.container.ITableLabelProvider#getColumnNames()
	 */
	public abstract String[] getColumnNames();
	
}
