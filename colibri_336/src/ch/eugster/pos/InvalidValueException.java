/*
 * Created on 16.11.2005
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
public class InvalidValueException extends Exception {

	public static final long serialVersionUID = 0l;
	/**
	 * 
	 */
	public InvalidValueException() {
		super();
	}

	/**
	 * @param arg0
	 */
	public InvalidValueException(String msg) {
		super(msg);
	}

	/**
	 * @param arg0
	 */
	public InvalidValueException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public InvalidValueException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

}
