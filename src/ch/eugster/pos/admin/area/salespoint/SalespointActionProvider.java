/*
 * Created on 29.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.salespoint;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.events.SelectionEvent;

import ch.eugster.pos.Constants;
import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.gui.container.ActionProvider;
import ch.eugster.pos.admin.gui.container.SashForm;
import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.db.Stock;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class SalespointActionProvider extends ActionProvider
{
	
	/**
	 * @param parent
	 */
	public SalespointActionProvider(SashForm parent)
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
											Messages.getString("SalespointActionProvider.Auswahl_l_u00F6schen_1"), Constants.REMOVE_SELECTION, true); //$NON-NLS-1$
		}
		else if (sel.getFirstElement() instanceof Salespoint)
		{
			Salespoint salespoint = (Salespoint) sel.getFirstElement();
			this
							.createMenuItem(
											Messages.getString("SalespointActionProvider.Neue_Kasse_hinzuf_u00FCgen_2"), Constants.ADD_ITEM, true); //$NON-NLS-1$
			this
							.createMenuItem(
											Messages.getString("SalespointActionProvider.Kasse__3") + this.getName(salespoint) + Messages.getString("SalespointActionProvider._l_u00F6schen_4"), Constants.REMOVE_ITEM, true); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else if (sel.getFirstElement() instanceof Stock)
		{
			this.createMenuItem("Kassenstock entfernen", Constants.REMOVE_ITEM, true); //$NON-NLS-1$
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
		if (element instanceof Salespoint)
		{
			result = ((Salespoint) element).name;
		}
		else if (element instanceof Stock)
		{
			result = "Kassenstock Währung: " + ((Stock) element).getForeignCurrency().code;
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
		if (child instanceof Stock) return ((Stock) child).getSalespoint().equals(parent);
		
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
		IStructuredSelection sel = (IStructuredSelection) this.parent.getViewer().getSelection();
		if (sel.size() > 1)
		{
			this.parent.delete();
		}
		if (sel.getFirstElement() instanceof Salespoint)
		{
			if (e.widget.getData().equals(Constants.ADD_ITEM))
			{
				Salespoint salespoint = new Salespoint();
				this.selectPage(salespoint);
			}
			else if (e.widget.getData().equals(Constants.REMOVE_ITEM))
			{
				this.parent.delete();
			}
		}
		else if (sel.getFirstElement() instanceof Stock)
		{
			if (e.widget.getData().equals(Constants.REMOVE_ITEM))
			{
				this.parent.delete();
			}
		}
	}
}
