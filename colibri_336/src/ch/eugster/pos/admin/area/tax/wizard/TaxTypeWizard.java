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
public class TaxTypeWizard extends Wizard
{
	
	/**
	 * 
	 */
	public TaxTypeWizard(TaxType type)
	{
		super();
		this.type = type;
		this.setWindowTitle(Messages.getString("TaxTypeWizard.text_1")); //$NON-NLS-1$
		this.namePage = new TaxTypeWizardPage1("name"); //$NON-NLS-1$
		this.addPage(this.namePage);
		this.rates = TaxRate.selectAll(false);
		this.kontoPages = new TaxTypeWizardPage2[this.rates.length];
		for (int i = 0; i < this.rates.length; i++)
		{
			this.kontoPages[i] = new TaxTypeWizardPage2("konto" + i, this.rates[i]); //$NON-NLS-1$
			this.addPage(this.kontoPages[i]);
		}
		this.addPage(new TaxTypeWizardPage3("ende")); //$NON-NLS-1$
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish()
	{
		this.type.code = ((TaxTypeWizardPage1) this.getPage("name")).getCode(); //$NON-NLS-1$
		this.type.name = ((TaxTypeWizardPage1) this.getPage("name")).getRateName(); //$NON-NLS-1$
		
		Database.getCurrent().getBroker().beginTransaction();
		DBResult result = this.type.store();
		if (result.getErrorCode() == 0)
		{
			// Jetzt werden fuer jeden aktiven TaxType zusaetzlich die
			// Tax-Element erstellt.
			this.rates = TaxRate.selectAll(false);
			for (int i = 0; i < this.rates.length; i++)
			{
				if (result.getErrorCode() == 0)
				{
					Tax tax = new Tax(this.type, this.rates[i]);
					tax.code = this.type.code + this.rates[i].code;
					tax.account = this.kontoPages[i].getAccount();
					tax.galileoId = this.namePage.getGalileoId();
					tax.code128Id = this.namePage.getCode128Id();
					tax.setCurrentTaxId(new Long(0));
					result = tax.store();
					if (result.getErrorCode() == 0)
					{
						CurrentTax currentTax = new CurrentTax();
						currentTax.percentage = this.kontoPages[i].getPercent();
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
			MessageDialog.openError(this.getShell(), Messages.getString("TaxTypeWizard.msg_9"), message); //$NON-NLS-1$
			return false;
		}
		Database.getCurrent().getBroker().commitTransaction();
		return true;
	}
	
	private TaxType type;
	private TaxRate[] rates;
	private TaxTypeWizardPage1 namePage;
	private TaxTypeWizardPage2[] kontoPages;
}
