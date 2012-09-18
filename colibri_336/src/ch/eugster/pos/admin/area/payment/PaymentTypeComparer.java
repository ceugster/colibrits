/*
 * Created on 21.02.2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.admin.area.payment;

import org.eclipse.jface.viewers.IElementComparer;

import ch.eugster.pos.db.PaymentType;
import ch.eugster.pos.db.PaymentTypeGroup;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PaymentTypeComparer implements IElementComparer
{
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IElementComparer#equals(java.lang.Object,
	 * java.lang.Object)
	 */
	public boolean equals(Object a, Object b)
	{
		if (a == null && b == null) return false;
		
		if (a instanceof PaymentTypeGroup && b instanceof PaymentTypeGroup)
		{
			return ((PaymentTypeGroup) a).getId().equals(((PaymentTypeGroup) b).getId());
		}
		else if (a instanceof PaymentType && b instanceof PaymentType)
		{
			return ((PaymentType) a).getId().equals(((PaymentType) b).getId());
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IElementComparer#hashCode(java.lang.Object)
	 */
	public int hashCode(Object element)
	{
		return 0;
	}
	
}
