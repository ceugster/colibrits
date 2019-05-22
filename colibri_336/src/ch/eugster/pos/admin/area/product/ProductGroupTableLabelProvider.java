/*
 * Created on 11.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.product;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.graphics.Image;

import ch.eugster.pos.admin.model.TableLabelProvider;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.swt.Resources;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ProductGroupTableLabelProvider extends TableLabelProvider
{
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang
	 * .Object, int)
	 */
	@Override
	public Image getColumnImage(Object element, int columnIndex)
	{
		if (columnIndex == 0 && element instanceof ProductGroup)
		{
			ProductGroup group = (ProductGroup) element;
			return Resources.getImageRegistry().get(
							group.modified ? "newBook.gif" : group.deleted ? "exitFull.gif" : "books.gif"); //$NON-NLS-1$
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
	@Override
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
					return ((ProductGroup) element).name;
				case 1:
					return ((ProductGroup) element).shortname;
				case 2:
					return ((ProductGroup) element).galileoId;
				case 3:
					return ((ProductGroup) element).getId().toString();
				case 4:
					return ((ProductGroup) element).isDefault ? "Ja" : "";
				case 5:
					return ((ProductGroup) element).ebook ? "Ja" : "";
				case 6:
					return ((ProductGroup) element).paidInvoice ? "Ja" : "";
				case 7:
					return ((ProductGroup) element).exportId;
			}
		}
		return ""; //$NON-NLS-1$
	}
	
	@Override
	public String[] getColumnNames()
	{
		return this.columnNames;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.
	 * jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void addListener(ILabelProviderListener listener)
	{
		this.productGroupTableLabelProviderListeners.add(listener);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	@Override
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
	@Override
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
	@Override
	public void removeListener(ILabelProviderListener listener)
	{
		this.productGroupTableLabelProviderListeners.remove(listener);
	}
	
	public ViewerSorter getSorter(int criteria)
	{
		return new ProductGroupViewerSorter(criteria);
	}
	
	private ArrayList productGroupTableLabelProviderListeners = new ArrayList();
	private String[] columnNames =
	{ "Bezeichnung", "Kurzbezeichnung", "Galileo", "Id", "Default", "eBook", "bez.Rg", "Export" };
}
