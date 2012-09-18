/*
 * Created on 18.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.user;

import org.eclipse.jface.viewers.Viewer;

import ch.eugster.pos.db.User;
import ch.eugster.pos.swt.TableViewerSorter;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class UserViewerSorter extends TableViewerSorter
{
	
	/**
	 * @param criteria
	 */
	public UserViewerSorter(int criteria)
	{
		super(criteria);
	}
	
	public int compare(Viewer viewer, Object element1, Object element2)
	{
		
		User user1 = (User) element1;
		User user2 = (User) element2;
		
		switch (this.criteria)
		{
			case 0:
				return this.compareUsername(user1, user2);
			case 1:
				return this.comparePassword(user1, user2);
			case 2:
				return this.comparePosLogin(user1, user2);
			case 3:
				return this.compareStatus(user1, user2);
			default:
				return 0;
		}
	}
	
	private int compareUsername(User user1, User user2)
	{
		return user1.username.compareTo(user2.username);
	}
	
	private int comparePassword(User user1, User user2)
	{
		return user1.password.compareTo(user2.password);
	}
	
	private int comparePosLogin(User user1, User user2)
	{
		return user1.posLogin.compareTo(user2.posLogin);
	}
	
	private int compareStatus(User user1, User user2)
	{
		return new Integer(user1.status).compareTo(new Integer(user2.status));
	}
	
}
