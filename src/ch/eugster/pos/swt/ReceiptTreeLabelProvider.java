/*
 * Created on 11.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.swt;

import java.io.File;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.graphics.Image;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ReceiptTreeLabelProvider extends BaseLabelProvider implements ILabelProvider
{
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang
	 * .Object, int)
	 */
	public Image getImage(Object element)
	{
		if (element instanceof File)
		{
			File file = (File) element;
			if (file.isDirectory())
			{
				return Resources.getImageRegistry().get("fldr_obj.gif");
			}
			else
			{
				return Resources.getImageRegistry().get("file_obj.gif");
			}
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
	public String getText(Object element)
	{
		File entry = (File) element;
		return entry.getName();
	}
	
	public ViewerSorter getSorter(int criteria)
	{
		return new ReceiptTreeViewerSorter(criteria);
	}
}
