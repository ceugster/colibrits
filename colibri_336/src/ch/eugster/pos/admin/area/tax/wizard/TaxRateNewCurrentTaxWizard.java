/*
 * Created on 05.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.tax.wizard;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.CurrentTax;
import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.Tax;
import ch.eugster.pos.db.TaxRate;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class TaxRateNewCurrentTaxWizard extends Wizard
{
	
	/**
	 * 
	 */
	public TaxRateNewCurrentTaxWizard()
	{
		super();
		this.init(new TaxRate());
	}
	
	/**
	 * 
	 */
	public TaxRateNewCurrentTaxWizard(TaxRate rate)
	{
		super();
		this.init(rate);
	}
	
	public void init(TaxRate rate)
	{
		this.rate = rate;
		this.setWindowTitle(Messages.getString("TaxRateNewCurrentTaxWizard.text_1")); //$NON-NLS-1$
		this.namePage = new TaxRateNewCurrentTaxWizardPage1("name"); //$NON-NLS-1$
		this.addPage(this.namePage);
		this.addPage(new TaxRateNewCurrentTaxWizardPage2("ende")); //$NON-NLS-1$
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish()
	{
		DBResult result = new DBResult();
		Tax[] taxes = Tax.selectByRateId(this.rate.getId(), false);
		Database.getCurrent().getBroker().beginTransaction();
		for (int i = 0; i < taxes.length; i++)
		{
			if (result.getErrorCode() == 0)
			{
				CurrentTax currentTax = new CurrentTax();
				currentTax.percentage = this.namePage.getPercent();
				currentTax.setValidationDate(this.namePage.getDate());
				currentTax.setTax(taxes[i]);
				result = currentTax.store();
				if (result.getErrorCode() == 0)
				{
					taxes[i].setCurrentTaxId(currentTax.getId());
					result = taxes[i].store();
				}
			}
		}
		if (result.getErrorCode() != 0)
		{
			Database.getCurrent().getBroker().abortTransaction();
			String message = result.getErrorText() + ""; //$NON-NLS-1$
			message = message.concat(result.getExternalErrorCode() + ": "); //$NON-NLS-1$
			MessageDialog.openError(this.getShell(), Messages.getString("TaxRateNewCurrentTaxWizard.msg_6"), message); //$NON-NLS-1$
			return false;
		}
		Database.getCurrent().getBroker().commitTransaction();
		return true;
	}
	
	private TaxRate rate;
	private TaxRateNewCurrentTaxWizardPage1 namePage;
	
}
