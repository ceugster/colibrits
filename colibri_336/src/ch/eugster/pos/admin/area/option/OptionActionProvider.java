/*
 * Created on 29.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.option;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.events.SelectionEvent;

import ch.eugster.pos.Constants;
import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.gui.container.ActionProvider;
import ch.eugster.pos.admin.gui.container.SashForm;
import ch.eugster.pos.db.Option;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class OptionActionProvider extends ActionProvider
{
	
	/**
	 * @param parent
	 */
	public OptionActionProvider(SashForm parent)
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
											Messages.getString("OptionActionProvider.Auswahl_l_u00F6schen_1"), Constants.REMOVE_SELECTION, true); //$NON-NLS-1$
		}
		else if (sel.getFirstElement() instanceof Option)
		{
			Option option = (Option) sel.getFirstElement();
			this
							.createMenuItem(
											Messages
															.getString("OptionActionProvider.Neuen_Optionscode_hinzuf_u00FCgen_2"), Constants.ADD_ITEM, true); //$NON-NLS-1$
			this
							.createMenuItem(
											Messages.getString("OptionActionProvider.Optionscode__3") + this.getName(option) + Messages.getString("OptionActionProvider._l_u00F6schen_4"), Constants.REMOVE_ITEM, true); //$NON-NLS-1$ //$NON-NLS-2$
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
	
}
