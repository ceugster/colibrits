/*
 * Created on 10.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.key;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ch.eugster.pos.db.Function;
import ch.eugster.pos.db.KeyGroup;
import ch.eugster.pos.db.Option;
import ch.eugster.pos.db.PaymentType;
import ch.eugster.pos.db.PaymentTypeGroup;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.db.TaxRate;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ButtonTreeContentProvider implements ITreeContentProvider
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
		Object[] result = new Object[0];
		if (parentElement instanceof ProductGroup)
		{
			result = new Object[0];
		}
		else if (parentElement instanceof PaymentType)
		{
			result = new Object[0];
		}
		else if (parentElement instanceof TaxRate)
		{
			result = new Object[0];
		}
		else if (parentElement instanceof Option)
		{
			result = new Object[0];
		}
		else if (parentElement instanceof Function)
		{
			result = new Object[0];
		}
		else if (parentElement instanceof PaymentTypeGroup)
		{
			result = PaymentType.selectByGroup((PaymentTypeGroup) parentElement, false);
		}
		else if (parentElement instanceof KeyGroup)
		{
			KeyGroup element = (KeyGroup) parentElement;
			if (element.clazz.equals(ProductGroup.class.getName()))
			{
				return ProductGroup.selectAll(false);
			}
			else if (element.clazz.equals(PaymentTypeGroup.class.getName()))
			{
				return PaymentTypeGroup.selectAll(false, false);
			}
			else if (element.clazz.equals(TaxRate.class.getName()))
			{
				return TaxRate.selectAll(false);
			}
			else if (element.clazz.equals(Option.class.getName()))
			{
				return Option.selectAll(false);
			}
			else if (element.clazz.equals(Function.class.getName()))
			{
				return Function.selectAll();
			}
		}
		else
		{
			result = KeyGroup.selectAll();
		}
		return result;
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
		Object result = null;
		if (element instanceof ProductGroup)
		{
			result = KeyGroup.selectByClass(ProductGroup.class.getName());
		}
		else if (element instanceof PaymentType)
		{
			result = ((PaymentType) element).getPaymentTypeGroup();
		}
		else if (element instanceof PaymentTypeGroup)
		{
			result = KeyGroup.selectByClass(PaymentTypeGroup.class.getName());
		}
		else if (element instanceof TaxRate)
		{
			result = KeyGroup.selectByClass(TaxRate.class.getName());
		}
		else if (element instanceof Option)
		{
			result = KeyGroup.selectByClass(Option.class.getName());
		}
		else if (element instanceof Function)
		{
			result = KeyGroup.selectByClass(Function.class.getName());
		}
		return result;
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
	
	public void dispose()
	{
	}
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
	}
	
}
