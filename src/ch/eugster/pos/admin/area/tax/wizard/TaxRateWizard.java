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
import ch.eugster.pos.db.TaxType;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class TaxRateWizard extends Wizard
{
	
	/**
	 * 
	 */
	public TaxRateWizard()
	{
		super();
		this.init(new TaxRate());
	}
	
	/**
	 * 
	 */
	public TaxRateWizard(TaxRate rate)
	{
		super();
		this.init(rate);
	}
	
	public void init(TaxRate rate)
	{
		this.rate = rate;
		this.setWindowTitle(Messages.getString("TaxRateWizard.text_1")); //$NON-NLS-1$
		this.namePage = new TaxRateWizardPage1("name"); //$NON-NLS-1$
		this.addPage(this.namePage);
		this.types = TaxType.selectAll(false);
		this.kontoPages = new TaxRateWizardPage2[this.types.length];
		for (int i = 0; i < this.types.length; i++)
		{
			this.kontoPages[i] = new TaxRateWizardPage2("konto" + i, this.types[i]); //$NON-NLS-1$
			this.addPage(this.kontoPages[i]);
		}
		this.addPage(new TaxRateWizardPage3("ende")); //$NON-NLS-1$
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish()
	{
		this.rate.code = ((TaxRateWizardPage1) this.getPage("name")).getCode(); //$NON-NLS-1$
		this.rate.name = ((TaxRateWizardPage1) this.getPage("name")).getRateName(); //$NON-NLS-1$
		
		Database.getCurrent().getBroker().beginTransaction();
		DBResult result = this.rate.store();
		if (result.getErrorCode() == 0)
		{
			for (int i = 0; i < this.types.length; i++)
			{
				if (result.getErrorCode() == 0)
				{
					Tax tax = new Tax(this.types[i], this.rate);
					tax.code = this.types[i].code + this.rate.code;
					tax.account = this.kontoPages[i].getAccount();
					tax.galileoId = this.kontoPages[i].getGalileoId();
					tax.code128Id = this.kontoPages[i].getCode128Id();
					tax.setCurrentTaxId(new Long(0));
					result = tax.store();
					if (result.getErrorCode() == 0)
					{
						CurrentTax currentTax = new CurrentTax();
						currentTax.percentage = this.namePage.getPercent();
						currentTax.fibuId = this.kontoPages[i].getFibuId();
						currentTax.setValidationDate(this.namePage.getDate());
						currentTax.setTax(tax);
						result = currentTax.store();
						if (result.getErrorCode() == 0)
						{
							tax.setCurrentTaxId(currentTax.getId());
							result = tax.store();
						}
					}
				}
			}
		}
		if (result.getErrorCode() != 0)
		{
			Database.getCurrent().getBroker().abortTransaction();
			String message = result.getErrorText() + ""; //$NON-NLS-1$
			message = message.concat(result.getExternalErrorCode() + ": "); //$NON-NLS-1$
			MessageDialog.openError(this.getShell(), Messages.getString("TaxRateWizard.msg_9"), message); //$NON-NLS-1$
			return false;
		}
		Database.getCurrent().getBroker().commitTransaction();
		return true;
	}
	
	private TaxRate rate;
	private TaxType[] types;
	private TaxRateWizardPage1 namePage;
	private TaxRateWizardPage2[] kontoPages;
	
}
