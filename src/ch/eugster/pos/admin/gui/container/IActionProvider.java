/*
 * Created on 28.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.gui.container;

import org.eclipse.swt.events.SelectionEvent;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public interface IActionProvider
{
	public void buildMenu();
	
	public void performMenuSelection(SelectionEvent e);
	
	public void refresh();
}
