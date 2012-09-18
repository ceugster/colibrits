/*
 * Created on 18.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.statistics.gui;

import org.eclipse.jface.viewers.Viewer;

import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.swt.TableViewerSorter;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class SalespointViewerSorter extends TableViewerSorter
{
	
	/**
	 * @param criteria
	 */
	public SalespointViewerSorter(int criteria)
	{
		super(criteria);
	}
	
	public int compare(Viewer viewer, Object element1, Object element2)
	{
		
		Salespoint salespoint1 = (Salespoint) element1;
		Salespoint salespoint2 = (Salespoint) element2;
		
		switch (this.criteria)
		{
			case 0:
				return this.compareName(salespoint1, salespoint2);
			case 1:
				return this.comparePlace(salespoint1, salespoint2);
			case 2:
				return this.compareActive(salespoint1, salespoint2);
			default:
				return 0;
		}
	}
	
	private int compareName(Salespoint salespoint1, Salespoint salespoint2)
	{
		return salespoint1.name.compareTo(salespoint2.name);
	}
	
	private int comparePlace(Salespoint salespoint1, Salespoint salespoint2)
	{
		return salespoint1.name.compareTo(salespoint2.name);
	}
	
	private int compareActive(Salespoint salespoint1, Salespoint salespoint2)
	{
		return salespoint1.active ? salespoint2.active ? 0 : -1 : salespoint2.active ? 1 : 0;
	}
	
}