/*
 * Created on 11.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.option;

import org.eclipse.jface.dialogs.MessageDialog;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.model.TableContentProvider;
import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.Option;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class OptionTableContentProvider extends TableContentProvider
{
	
	public Object[] getElements(Object element)
	{
		return Option.selectAll(false);
	}
	
	public boolean deleteElement(Object element)
	{
		if (element instanceof Option)
		{
			Option option = (Option) element;
			DBResult result = option.delete();
			if (result.getErrorCode() != 0)
			{
				MessageDialog
								.openError(
												this.viewer.getControl().getShell(),
												Messages.getString("OptionTableContentProvider.Fehler_1"), Messages.getString("OptionTableContentProvider.Die_Option__2") + option.name + Messages.getString("OptionTableContentProvider._konnte_nicht_gel_u00F6scht_werden._3")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			return result.getErrorCode() == 0;
		}
		return false;
	}
}
