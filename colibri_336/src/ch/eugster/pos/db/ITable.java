/*
 * Created on 27.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.db;

import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author ceugster
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface ITable {
	public void set(PersistentDBStore store);
	public Table get(PersistentDBStore store);
}
