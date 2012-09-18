/*
 * Created on 31.07.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos;

/**
 * @author ceugster
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ExceptionThrownEvent {
	public ExceptionThrownEvent(Throwable throwable, String exceptionMessage) {
		this.message = exceptionMessage;
		this.throwable = throwable;
	}
	
	public String message;
	public Throwable throwable;
}
