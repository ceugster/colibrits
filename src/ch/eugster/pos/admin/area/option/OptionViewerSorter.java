/*
 * Created on 17.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.option;

import org.eclipse.jface.viewers.Viewer;

import ch.eugster.pos.db.Option;
import ch.eugster.pos.swt.TableViewerSorter;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class OptionViewerSorter extends TableViewerSorter
{
	
	public OptionViewerSorter(int criteria)
	{
		super(criteria);
	}
	
	public int compare(Viewer viewer, Object element1, Object element2)
	{
		
		Option option1 = (Option) element1;
		Option option2 = (Option) element2;
		
		switch (this.criteria)
		{
			case 0:
				return this.compareCode(option1, option2);
			case 1:
				return this.compareName(option1, option2);
			default:
				return 0;
		}
	}
	
	private int compareCode(Option option1, Option option2)
	{
		return option1.code.compareTo(option2.code);
	}
	
	private int compareName(Option option1, Option option2)
	{
		return option1.name.compareTo(option2.name);
	}
}
