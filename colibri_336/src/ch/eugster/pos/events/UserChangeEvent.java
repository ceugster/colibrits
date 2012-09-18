/*
 * Created on 05.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.events;

import ch.eugster.pos.db.User;

/**
 * @author ceugster
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class UserChangeEvent {

	/**
	 * 
	 */
	public UserChangeEvent(User oldUser, User newUser) {
		super();
		this.oldUser = oldUser;
		this.newUser = newUser;
	}
	
	public User getOldUser() {
		return oldUser;
	}
	
	public User getNewUser() {
		return newUser;
	}

	private User oldUser = null;
	private User newUser = null;
}
