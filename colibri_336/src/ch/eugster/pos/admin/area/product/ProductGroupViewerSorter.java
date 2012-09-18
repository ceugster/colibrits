/*
 * Created on 18.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.product;

import org.eclipse.jface.viewers.Viewer;

import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.swt.TableViewerSorter;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ProductGroupViewerSorter extends TableViewerSorter
{
	
	/**
	 * @param criteria
	 */
	public ProductGroupViewerSorter(int criteria)
	{
		super(criteria);
	}
	
	public int compare(Viewer viewer, Object element1, Object element2)
	{
		
		ProductGroup productGroup1 = (ProductGroup) element1;
		ProductGroup productGroup2 = (ProductGroup) element2;
		
		switch (this.criteria)
		{
			case 0:
				return this.compareName(productGroup1, productGroup2);
			case 1:
				return this.compareShortname(productGroup1, productGroup2);
			case 2:
				return this.compareGalileoId(productGroup1, productGroup2);
			case 3:
				return this.compareId(productGroup1, productGroup2);
			case 5:
				return this.compareExportId(productGroup1, productGroup2);
			default:
				return 0;
		}
	}
	
	private int compareId(ProductGroup productGroup1, ProductGroup productGroup2)
	{
		return productGroup1.getId().compareTo(productGroup2.getId());
	}
	
	private int compareName(ProductGroup productGroup1, ProductGroup productGroup2)
	{
		return productGroup1.name.compareTo(productGroup2.name);
	}
	
	private int compareShortname(ProductGroup productGroup1, ProductGroup productGroup2)
	{
		return productGroup1.shortname.compareTo(productGroup2.shortname);
	}
	
	private int compareGalileoId(ProductGroup productGroup1, ProductGroup productGroup2)
	{
		return productGroup1.galileoId.compareTo(productGroup2.galileoId);
	}
	
	private int compareExportId(ProductGroup productGroup1, ProductGroup productGroup2)
	{
		return productGroup1.exportId.compareTo(productGroup2.exportId);
	}
	
}
