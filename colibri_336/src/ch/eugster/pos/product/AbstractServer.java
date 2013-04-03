/*
 * Created on 22.01.2013
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.product;

import org.eclipse.core.runtime.IStatus;

public abstract class AbstractServer implements Server
{
	private boolean open;
	
	private IStatus status;
	
	protected abstract void close();
	
	protected abstract boolean open();
	
	protected abstract IStatus start();
}
