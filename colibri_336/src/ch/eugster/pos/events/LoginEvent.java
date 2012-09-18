/*
 * Created on 11.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.events;

import java.util.EventObject;

import ch.eugster.pos.db.User;

/**
 * @author Administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LoginEvent extends EventObject
{
	private static final long serialVersionUID = 0l;
	
	/**
	 * @param source
	 */
	public LoginEvent(Object source, User user)
	{
		super(source);
		this.user = user;
	}
	
	public User getUser()
	{
		return this.user;
	}
	
	private User user;
}
