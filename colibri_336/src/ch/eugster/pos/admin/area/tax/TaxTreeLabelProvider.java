/*
 * Created on 26.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.tax;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import org.eclipse.swt.graphics.Image;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.model.SimpleLabelProvider;
import ch.eugster.pos.db.CurrentTax;
import ch.eugster.pos.db.Tax;
import ch.eugster.pos.db.TaxRate;
import ch.eugster.pos.db.TaxType;
import ch.eugster.pos.swt.Resources;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class TaxTreeLabelProvider extends SimpleLabelProvider
{
	
	/**
	 * 
	 */
	public TaxTreeLabelProvider()
	{
		super();
		this.nf = NumberFormat.getPercentInstance();
		this.nf.setMaximumFractionDigits(3);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element)
	{
		if (element instanceof TaxRate)
		{
			return Resources.getImageRegistry().get(Messages.getString("TaxTreeLabelProvider.books.gif_1")); //$NON-NLS-1$
		}
		else if (element instanceof TaxType)
		{
			return Resources.getImageRegistry().get(Messages.getString("TaxTreeLabelProvider.books.gif_2")); //$NON-NLS-1$
		}
		else if (element instanceof Tax)
		{
			return Resources.getImageRegistry().get(Messages.getString("TaxTreeLabelProvider.closedbook.gif_3")); //$NON-NLS-1$
		}
		else if (element instanceof CurrentTax)
		{
			CurrentTax ct = (CurrentTax) element;
			if (ct.equals(ct.getTax().getCurrentTax()))
			{
				return Resources.getImageRegistry().get(Messages.getString("TaxTreeLabelProvider.openbook.gif_4")); //$NON-NLS-1$
			}
			else
			{
				return Resources.getImageRegistry().get(Messages.getString("TaxTreeLabelProvider.closedbook.gif_5")); //$NON-NLS-1$
			}
		}
		else
		{
			return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element)
	{
		if (element instanceof TaxRate)
		{
			return ((TaxRate) element).name;
		}
		if (element instanceof TaxType)
		{
			return ((TaxType) element).name;
		}
		else if (element instanceof Tax)
		{
			Object[] input = (Object[]) this.getViewer().getInput();
			if (input[0] instanceof TaxType)
			{
				return ((Tax) element).getTaxRate().name;
			}
			else if (input[0] instanceof TaxRate)
			{
				return ((Tax) element).getTaxType().name;
			}
		}
		else if (element instanceof CurrentTax)
		{
			CurrentTax tax = (CurrentTax) element;
			String percents = this.nf.format(tax.percentage / 100);
			String date = SimpleDateFormat.getDateInstance().format(tax.getValidationDate());
			return percents + " - " + date; //$NON-NLS-1$
		}
		return ""; //$NON-NLS-1$
	}
	
	private NumberFormat nf;
}
