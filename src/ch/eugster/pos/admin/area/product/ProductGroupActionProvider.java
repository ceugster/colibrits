/*
 * Created on 29.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.product;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.events.SelectionEvent;

import ch.eugster.pos.Constants;
import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.gui.container.ActionProvider;
import ch.eugster.pos.admin.gui.container.SashForm;
import ch.eugster.pos.admin.gui.container.TableSashForm;
import ch.eugster.pos.db.ProductGroup;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ProductGroupActionProvider extends ActionProvider
{
	
	/**
	 * @param parent
	 */
	public ProductGroupActionProvider(SashForm parent)
	{
		super(parent);
	}
	
	public void addMenuItems(StructuredViewer viewer)
	{
		this
						.createMenuItem(
										Messages
														.getString("ProductGroupActionProvider.Neue_Warengruppen_hinzuf_u00FCgen_1"), Constants.ADD_ITEM, true); //$NON-NLS-1$
		this
						.createMenuItem(
										Messages.getString("ProductGroupActionProvider.Warengruppe__2") + this.getName(((IStructuredSelection) viewer.getSelection()).getFirstElement()) + Messages.getString("ProductGroupActionProvider._l_u00F6schen_3"), Constants.REMOVE_ITEM, true); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.gui.ActionProvider#getName(java.lang.Object)
	 */
	protected String getName(Object element)
	{
		String result = ""; //$NON-NLS-1$
		if (element instanceof ProductGroup)
		{
			result = ((ProductGroup) element).name;
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
			ProductGroup[] items = (ProductGroup[]) sash.getViewer().getInput();
			for (int i = 0; i < items.length; i++)
			{
				if (!items[i].getId().equals(((ProductGroup) sel.getFirstElement()).getId()))
				{
					if (items[i].isDefault)
					{
						items[i].isDefault = false;
					}
				}
			}
			sash.getViewer().refresh();
		}
	}
	
}
