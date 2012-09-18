/*
 * Created on 26.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.salespoint;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.model.TreeContentProvider;
import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.PaymentType;
import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.db.Stock;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class SalespointTreeContentProvider extends TreeContentProvider implements Listener
{
	
	private static SalespointTreeContentProvider contentProvider;
	
	private SalespointTreeContentProvider()
	{
		
	}
	
	public static SalespointTreeContentProvider getInstance()
	{
		if (SalespointTreeContentProvider.contentProvider == null)
			SalespointTreeContentProvider.contentProvider = new SalespointTreeContentProvider();
		
		return SalespointTreeContentProvider.contentProvider;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.
	 * Object)
	 */
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof Salespoint)
		{
			Salespoint salespoint = (Salespoint) parentElement;
			return Stock.selectBySalespoint(salespoint);
		}
		else if (parentElement instanceof Stock)
		{
			return new Object[0];
		}
		else
			return Salespoint.selectAll(false);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object
	 * )
	 */
	public Object getParent(Object element)
	{
		if (element instanceof Stock)
		{
			return ((Stock) element).getSalespoint();
		}
		else
			return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.
	 * Object)
	 */
	public boolean hasChildren(Object element)
	{
		return this.getChildren(element).length > 0;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java
	 * .lang.Object)
	 */
	public Object[] getElements(Object inputElement)
	{
		return this.getChildren(inputElement);
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
		else if (element instanceof Stock)
		{
			Stock stock = (Stock) element;
			DBResult result = stock.delete();
			if (result.getErrorCode() != 0)
			{
				MessageDialog
								.openError(
												this.viewer.getControl().getShell(),
												"Fehler", "Der Kassenstock der Währung " + stock.getForeignCurrency().code + " konnte nicht gelöscht werden."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			return result.getErrorCode() == 0;
		}
		return false;
	}
	
	public void handleEvent(Event event)
	{
		if (event.data instanceof PaymentType)
		{
			PaymentType type = (PaymentType) event.data;
			if (!type.getForeignCurrency().getId().equals(ForeignCurrency.getDefaultCurrency().getId()))
			{
				Database.getStandard().testForStock(type.getForeignCurrency());
			}
		}
		
	}
}
