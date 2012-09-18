/*
 * Created on 21.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.swt;

import java.util.Hashtable;

import ch.eugster.pos.db.User;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class UserVerificationInputValidator implements IUserVerificationInputValidator
{
	
	/**
	 * 
	 */
	public UserVerificationInputValidator()
	{
		super();
	}
	
	/**
	 * 
	 */
	public UserVerificationInputValidator(Integer state)
	{
		super();
		if (state != null)
		{
			this.setValidStates(new Integer[]
			{ state });
		}
	}
	
	/**
	 * @param valid
	 *            states
	 */
	public UserVerificationInputValidator(Integer[] states)
	{
		super();
		if (states != null)
		{
			this.setValidStates(states);
		}
	}
	
	public boolean isValid(User user)
	{
		return this.validStates.get(new Integer(user.status)) == null ? false : true;
	}
	
	public void setValidStates(Integer[] states)
	{
		for (int i = 0; i < states.length; i++)
		{
			this.validStates.put(states[i], states[i]);
		}
	}
	
	Hashtable validStates = new Hashtable();
}
