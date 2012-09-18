/*
 * Created on 17.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.fixkey;

import org.eclipse.jface.viewers.Viewer;

import ch.eugster.pos.db.Key;
import ch.eugster.pos.swt.TableViewerSorter;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class FixKeyViewerSorter extends TableViewerSorter
{
	
	public FixKeyViewerSorter(int criteria)
	{
		super(criteria);
	}
	
	public int compare(Viewer viewer, Object element1, Object element2)
	{
		
		Key key1 = (Key) element1;
		Key key2 = (Key) element2;
		
		switch (this.criteria)
		{
			case 0:
				return this.compareCode(key1, key2);
			default:
				return 0;
		}
	}
	
	private int compareCode(Key key1, Key key2)
	{
		return key1.text.compareTo(key2.text);
	}
}
