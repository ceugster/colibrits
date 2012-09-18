/*
 * Created on 09.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.key;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.events.SelectionEvent;

import ch.eugster.pos.Constants;
import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.gui.container.ActionProvider;
import ch.eugster.pos.admin.gui.container.SashForm;
import ch.eugster.pos.db.Block;
import ch.eugster.pos.db.CustomKey;
import ch.eugster.pos.db.Key;
import ch.eugster.pos.db.Tab;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class KeyActionProvider extends ActionProvider
{
	
	/**
	 * @param parent
	 */
	public KeyActionProvider(SashForm parent)
	{
		super(parent);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.admin.gui.container.ActionProvider#getName(java.lang.Object
	 * )
	 */
	protected String getName(Object element)
	{
		String result = null;
		if (element instanceof Block)
		{
			result = ((Block) element).name;
		}
		else if (element instanceof Tab)
		{
			result = ((Tab) element).title;
		}
		else if (element instanceof Key)
		{
			result = ((Key) element).text;
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.gui.container.ActionProvider#getErrorMessage()
	 */
	protected String getErrorMessage()
	{
		return ""; //$NON-NLS-1$
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.admin.gui.container.ActionProvider#isChildOf(java.lang
	 * .Object, java.lang.Object)
	 */
	public boolean isChildOf(Object child, Object parent)
	{
		boolean result = false;
		if (child instanceof Tab)
		{
			if (parent instanceof Block)
			{
				result = ((Tab) child).getBlock().equals(parent);
			}
		}
		else if (child instanceof CustomKey)
		{
			if (parent instanceof Tab)
			{
				result = ((CustomKey) child).getTab().equals(parent);
			}
		}
		return result;
	}
	
	public void addMenuItems(StructuredViewer viewer)
	{
		IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
		if (sel.getFirstElement() instanceof Block)
		{
			Block block = (Block) sel.getFirstElement();
			if (!block.clazz.equals("ch.eugster.pos.client.gui.CoinCounter"))
			{
				this
								.createMenuItem(
												Messages
																.getString("KeyActionProvider.Neues_Register_hinzuf_u00FCgen_2"), Constants.ADD_CHILD, true); //$NON-NLS-1$
				this
								.createMenuItem(
												Messages
																.getString("KeyActionProvider.Zugeh_u00F6rige_Register_l_u00F6schen_3"), Constants.REMOVE_CHILDREN, true); //$NON-NLS-1$
			}
		}
		else if (sel.getFirstElement() instanceof Tab)
		{
			Tab tab = (Tab) sel.getFirstElement();
			this
							.createMenuItem(
											Messages.getString("KeyActionProvider.Register_hinzuf_u00FCgen_4"), Constants.ADD_ITEM, true); //$NON-NLS-1$
			this
							.createMenuItem(
											Messages.getString("KeyActionProvider.Register__5") + tab.title + Messages.getString("KeyActionProvider._entfernen_6"), Constants.REMOVE_ITEM, true); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	public void performMenuSelection(SelectionEvent e)
	{
		IStructuredSelection sel = (IStructuredSelection) this.parent.getViewer().getSelection();
		if (sel.getFirstElement() instanceof Block)
		{
			Block block = (Block) sel.getFirstElement();
			if (e.widget.getData().equals(Constants.ADD_CHILD))
			{
				Tab tab = new Tab();
				tab.setBlock(block);
				this.selectPage(tab);
			}
			else if (e.widget.getData().equals(Constants.REMOVE_CHILDREN))
			{
				Tab[] tabs = Tab.selectByBlock(block);
				for (int i = 0; i < tabs.length; i++)
				{
					this.parent.removeElement(tabs[i]);
				}
			}
		}
		else if (sel.getFirstElement() instanceof Tab)
		{
			Tab tab = (Tab) sel.getFirstElement();
			if (e.widget.getData().equals(Constants.ADD_ITEM))
			{
				Tab newTab = new Tab();
				newTab.setBlock(tab.getBlock());
				this.selectPage(newTab);
			}
			else if (e.widget.getData().equals(Constants.REMOVE_ITEM))
			{
				this.parent.delete();
			}
		}
	}
}
