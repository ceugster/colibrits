/*
 * Created on 06.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.gui.widget;

import ch.eugster.pos.swt.PersistentDBStore;






/**
 * @author ceugster
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface IPageContainer {
	public PersistentDBStore getStore();
	public void updateButtons();
	public void updateMessage();
	public void updateTitle();
}
