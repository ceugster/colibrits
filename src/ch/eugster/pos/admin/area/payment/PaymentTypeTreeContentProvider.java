/*
 * Created on 27.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.payment;

import org.eclipse.jface.dialogs.MessageDialog;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.model.TreeContentProvider;
import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.PaymentType;
import ch.eugster.pos.db.PaymentTypeGroup;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class PaymentTypeTreeContentProvider extends TreeContentProvider
{
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.
	 * Object)
	 */
	public Object[] getChildren(Object parentElement)
	{
		Object[] result = null;
		if (parentElement instanceof PaymentType)
		{
			result = new Object[0];
		}
		else if (parentElement instanceof PaymentTypeGroup)
		{
			result = PaymentType.selectByGroup((PaymentTypeGroup) parentElement, false);
		}
		else
		{
			result = PaymentTypeGroup.selectAll(false, false);
		}
		return result != null ? result : new Object[0];
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
		Object parent = null;
		if (element instanceof PaymentType)
		{
			parent = ((PaymentType) element).getPaymentTypeGroup();
		}
		if (element instanceof PaymentTypeGroup)
		{
			parent = null;
		}
		return parent;
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
		if (element instanceof PaymentType)
		{
			PaymentType paymentType = (PaymentType) element;
			DBResult result = paymentType.delete();
			if (result.getErrorCode() != 0)
			{
				MessageDialog
								.openError(
												this.viewer.getControl().getShell(),
												Messages.getString("PaymentTypeTreeContentProvider.Fehler_1"), Messages.getString("PaymentTypeTreeContentProvider.Die_Zahlungsart__2") + paymentType.name + Messages.getString("PaymentTypeTreeContentProvider._konnte_nicht_gel_u00F6scht_werden._3")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			return result.getErrorCode() == 0;
		}
		else if (element instanceof PaymentTypeGroup)
		{
			PaymentTypeGroup paymentTypeGroup = (PaymentTypeGroup) element;
			DBResult result = paymentTypeGroup.delete();
			if (result.getErrorCode() != 0)
			{
				MessageDialog
								.openError(
												this.viewer.getControl().getShell(),
												Messages.getString("PaymentTypeTreeContentProvider.Fehler_4"), Messages.getString("PaymentTypeTreeContentProvider.Die_ZahlungsartenGruppe__5") + paymentTypeGroup.name + Messages.getString("PaymentTypeTreeContentProvider._konnte_nicht_gel_u00F6scht_werden._6")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			return result.getErrorCode() == 0;
		}
		return false;
	}
	
}
