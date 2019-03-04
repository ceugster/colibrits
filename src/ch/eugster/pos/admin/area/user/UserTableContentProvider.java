/*
 * Created on 11.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.user;

import org.eclipse.jface.dialogs.MessageDialog;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.model.TableContentProvider;
import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.User;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class UserTableContentProvider extends TableContentProvider
{
	
	public Object[] getElements(Object element)
	{
		return User.selectAll(false);
	}
	
	public boolean deleteElement(Object element)
	{
		if (element instanceof User)
		{
			User user = (User) element;
			if (user.equals(User.getCurrentUser()))
			{
				MessageDialog
								.openInformation(
												this.viewer.getControl().getShell(),
												Messages.getString("UserTableContentProvider.Fehler_1"), Messages.getString("UserTableContentProvider.Der_Benutzer__2") + user.username + Messages.getString("UserTableContentProvider._ist_zur_Zeit_angemeldet_und_kann_daher_nicht_gel_u00F6scht_werden._3")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			DBResult result = user.delete();
			if (result.getErrorCode() != 0)
			{
				MessageDialog
								.openError(
												this.viewer.getControl().getShell(),
												Messages.getString("UserTableContentProvider.Fehler_4"), Messages.getString("UserTableContentProvider.Der_Benutzer__5") + user.username + Messages.getString("UserTableContentProvider._konnte_nicht_gel_u00F6scht_werden._6")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			return result.getErrorCode() == 0;
		}
		return false;
	}
}
