/*
 * Created on 11.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.user;

import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.graphics.Image;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.model.TableLabelProvider;
import ch.eugster.pos.db.User;
import ch.eugster.pos.swt.Resources;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class UserTableLabelProvider extends TableLabelProvider
{
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang
	 * .Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex)
	{
		if (columnIndex == 0)
		{
			return Resources.getImageRegistry().get(Messages.getString("UserTableLabelProvider.user.gif_1")); //$NON-NLS-1$
		}
		else
		{
			return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang
	 * .Object, int)
	 */
	public String getColumnText(Object element, int columnIndex)
	{
		if (element == null)
		{
			return this.columnNames[columnIndex];
		}
		else
		{
			switch (columnIndex)
			{
				case 0:
					return ((User) element).username;
				case 1:
					return ((User) element).password;
				case 2:
					return ((User) element).posLogin.toString();
				case 3:
					return User.USER_STATE_TEXT[((User) element).status];
				case 4:
					if (((User) element).defaultUser.booleanValue())
					{
						return "Ja";
					}
					else
					{
						return "";
					}
			}
		}
		return ""; //$NON-NLS-1$
	}
	
	public ViewerSorter getSorter(int criteria)
	{
		return new UserViewerSorter(criteria);
	}
	
	public String[] getColumnNames()
	{
		return this.columnNames;
	}
	
	private String[] columnNames =
	{
					Messages.getString("UserTableLabelProvider.Benutzername_3"), Messages.getString("UserTableLabelProvider.Passwort_4"), Messages.getString("UserTableLabelProvider.POS_Login_5"), Messages.getString("UserTableLabelProvider.Status_6"), "Default Benutzer" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
}
