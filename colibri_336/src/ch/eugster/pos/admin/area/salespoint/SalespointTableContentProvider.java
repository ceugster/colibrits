/*
 * Created on 11.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.salespoint;

import org.eclipse.jface.dialogs.MessageDialog;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.model.TableContentProvider;
import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.Salespoint;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class SalespointTableContentProvider extends TableContentProvider
{
	
	public Object[] getElements(Object element)
	{
		return Salespoint.selectAll(false);
	}
	
	public boolean deleteElement(Object element)
	{
		if (element instanceof Salespoint)
		{
			Salespoint salespoint = (Salespoint) element;
			DBResult result = salespoint.delete();
			if (result.getErrorCode() != 0)
			{
				MessageDialog
								.openError(
												this.viewer.getControl().getShell(),
												Messages.getString("SalespointTableContentProvider.Fehler_1"), Messages.getString("SalespointTableContentProvider.Die_Kasse__2") + salespoint.name + Messages.getString("SalespointTableContentProvider._kann_nicht_gel_u00F6scht_werden._3")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			return result.getErrorCode() == 0;
		}
		return false;
	}
}
