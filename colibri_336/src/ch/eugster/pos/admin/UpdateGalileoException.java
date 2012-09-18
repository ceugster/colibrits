/*
 * Created on 22.02.2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.admin;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class UpdateGalileoException extends Exception
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	public UpdateGalileoException()
	{
		super();
	}
	
	/**
	 * @param message
	 */
	public UpdateGalileoException(String message)
	{
		super(message);
	}
	
	/**
	 * @param message
	 */
	public UpdateGalileoException(int errNumber, String message)
	{
		super(message);
	}
	
	/**
	 * @param throwable
	 */
	public UpdateGalileoException(Throwable throwable)
	{
		super(throwable);
	}
	
	/**
	 * @param message
	 * @param throwable
	 */
	public UpdateGalileoException(String message, Throwable throwable)
	{
		super(message, throwable);
	}
	
	private int errorNumber = UpdateGalileoException.NO_ERROR;
	
	public static final int NO_ERROR = 0;
	public static final int ERROR_DEL_ABHOLFACH = 1;
}
