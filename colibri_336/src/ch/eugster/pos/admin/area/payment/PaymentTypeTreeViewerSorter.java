/*
 * Created on 03.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.payment;

import java.text.Collator;

import org.eclipse.jface.viewers.ViewerSorter;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class PaymentTypeTreeViewerSorter extends ViewerSorter
{
	
	/**
	 * 
	 */
	public PaymentTypeTreeViewerSorter()
	{
		super();
	}
	
	/**
	 * @param collator
	 */
	public PaymentTypeTreeViewerSorter(Collator collator)
	{
		super(collator);
	}
	
}
