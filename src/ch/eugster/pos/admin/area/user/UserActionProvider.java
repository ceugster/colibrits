/*
 * Created on 29.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.user;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.events.SelectionEvent;

import ch.eugster.pos.Constants;
import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.gui.container.ActionProvider;
import ch.eugster.pos.admin.gui.container.SashForm;
import ch.eugster.pos.admin.gui.container.TableSashForm;
import ch.eugster.pos.db.Option;
import ch.eugster.pos.db.User;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class UserActionProvider extends ActionProvider
{
	
	/**
	 * @param parent
	 */
	public UserActionProvider(SashForm parent)
	{
		super(parent);
	}
	
	public void addMenuItems(StructuredViewer viewer)
	{
		IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
		if (sel.size() > 1)
		{
			this
							.createMenuItem(
											Messages.getString("UserActionProvider.Auswahl_l_u00F6schen_1"), Constants.REMOVE_SELECTION); //$NON-NLS-1$
		}
		else if (sel.getFirstElement() instanceof Option)
		{
			Option option = (Option) sel.getFirstElement();
			this
							.createMenuItem(
											Messages
															.getString("UserActionProvider.Neuen_Optionscode_hinzuf_u00FCgen_2"), Constants.ADD_ITEM); //$NON-NLS-1$
			this
							.createMenuItem(
											Messages.getString("UserActionProvider.Optionscode__3") + this.getName(option) + Messages.getString("UserActionProvider._l_u00F6schen_4"), Constants.REMOVE_ITEM); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.gui.ActionProvider#getName(java.lang.Object)
	 */
	protected String getName(Object element)
	{
		String result = ""; //$NON-NLS-1$
		if (element instanceof Option)
		{
			result = ((Option) element).name;
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.gui.ActionProvider#getErrorMessage()
	 */
	protected String getErrorMessage()
	{
		return this.errorMessage;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.gui.ActionProvider#isChildOf(java.lang.Object,
	 * java.lang.Object)
	 */
	public boolean isChildOf(Object child, Object parent)
	{
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.admin.gui.IActionProvider#performMenuSelection(org.eclipse
	 * .swt.events.SelectionEvent)
	 */
	public void performMenuSelection(SelectionEvent e)
	{
		if (e.widget.getData().equals(Constants.ADD_ITEM))
		{
			this.parent.add();
		}
		else if (e.widget.getData().equals(Constants.REMOVE_ITEM))
		{
			this.parent.delete();
		}
	}
	
	public void refresh()
	{
		if (this.parent instanceof TableSashForm)
		{
			TableSashForm sash = (TableSashForm) this.parent;
			StructuredSelection sel = (StructuredSelection) sash.getViewer().getSelection();
			User[] items = (User[]) sash.getViewer().getInput();
			for (int i = 0; i < items.length; i++)
			{
				if (!items[i].getId().equals(((User) sel.getFirstElement()).getId()))
				{
					if (items[i].defaultUser.booleanValue())
					{
						items[i].defaultUser = new Boolean(false);
					}
				}
			}
			sash.getViewer().refresh();
		}
	}
}
