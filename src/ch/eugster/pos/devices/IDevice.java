/*
 * Created on 18.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.devices;

import ch.eugster.pos.serial.Port;

/**
 * @author ceugster
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface IDevice {
	abstract void setPort(Port port);
	
}
