/*
 * Created on 18.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.serial;

import java.util.Hashtable;

/**
 * @author ceugster
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Port {

	/**
	 * 
	 */
	public Port() {
		super();
		
	}
	
	public static void addDevice(String key, Object device) {
		ports.put(key, device);
	}
	
	public static void removeDevice(String key) {
		ports.remove(key);
	}
	
	public static Object getDevice(String key) {
		return ports.get(key);
	}

	protected static Hashtable ports = new Hashtable();
}
