/*
 * Created on 03.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.swt;

import org.eclipse.jface.viewers.ViewerSorter;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public abstract class TableViewerSorter extends ViewerSorter
{
	
	/**
	 * 
	 */
	public TableViewerSorter(int criteria)
	{
		super();
		this.criteria = criteria;
	}
	
	protected int criteria;
}
