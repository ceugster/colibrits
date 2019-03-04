/*
 * Created on 17.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.currency;

import org.eclipse.jface.viewers.Viewer;

import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.swt.TableViewerSorter;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class CurrencyViewerSorter extends TableViewerSorter
{
	
	/**
	 * 
	 */
	public CurrencyViewerSorter(int criteria)
	{
		super(criteria);
	}
	
	public int compare(Viewer viewer, Object element1, Object element2)
	{
		
		ForeignCurrency currency1 = (ForeignCurrency) element1;
		ForeignCurrency currency2 = (ForeignCurrency) element2;
		
		switch (this.criteria)
		{
			case 0:
				return this.compareCode(currency1, currency2);
			case 1:
				return this.compareName(currency1, currency2);
			case 2:
				return this.compareRegion(currency1, currency2);
			default:
				return 0;
		}
	}
	
	private int compareCode(ForeignCurrency currency1, ForeignCurrency currency2)
	{
		if (currency1.isUsed == currency2.isUsed)
		{
			return currency1.code.compareTo(currency2.code);
		}
		else if (currency1.isUsed)
		{
			return -1;
		}
		else
		{
			return 1;
		}
	}
	
	private int compareName(ForeignCurrency currency1, ForeignCurrency currency2)
	{
		return currency1.name.compareTo(currency2.name);
	}
	
	private int compareRegion(ForeignCurrency currency1, ForeignCurrency currency2)
	{
		return currency1.region.compareTo(currency2.region);
	}
}
