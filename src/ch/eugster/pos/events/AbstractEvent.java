/*
 * Created on 09.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.events;

import java.util.Hashtable;

/**
 * @author administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class AbstractEvent {

	/**
	 * 
	 */
	public AbstractEvent() {
		super();
	}
	
	public void put(Integer key, Integer value) {
		table.put(key, value);
	}
	
	public Integer get(Integer key) {
		return (Integer) table.get(key);
	}

	protected Hashtable table = new Hashtable();
}
