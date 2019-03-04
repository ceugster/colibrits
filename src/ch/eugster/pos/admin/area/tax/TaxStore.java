/*
 * Created on 29.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.tax;

import org.eclipse.jface.viewers.IStructuredSelection;

import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.Tax;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class TaxStore extends PersistentDBStore
{
	
	public void initialize()
	{
		this.setElement(new Tax());
	}
	
	public void initialize(IStructuredSelection selection)
	{
		this.setElement(new Tax());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.Store#load()
	 */
	protected void load()
	{
		Tax tax = (Tax) this.element;
		this.putDefaultId(TaxFieldEditorPage.KEY_ID, tax.getId());
		this.putDefault(TaxFieldEditorPage.KEY_CODE, tax.code);
		this.putDefault(TaxFieldEditorPage.KEY_GALILEO_ID, tax.galileoId);
		this.putDefault(TaxFieldEditorPage.KEY_ACCOUNT, tax.account);
		this.putDefault(TaxFieldEditorPage.KEY_CURRENT_TAX, tax.getCurrentTax());
		this.putToDefault(TaxFieldEditorPage.KEY_GALILEO_ID);
		this.putDefault(TaxFieldEditorPage.KEY_CODE128_ID, tax.code128Id);
		this.putToDefault(TaxFieldEditorPage.KEY_ACCOUNT);
		this.setToDefault(TaxFieldEditorPage.KEY_CURRENT_TAX);
		this.setDirty(false);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.Store#load()
	 */
	protected void store()
	{
		Tax tax = (Tax) this.element;
		tax.setId(this.getDefaultId(TaxFieldEditorPage.KEY_ID));
		tax.code = this.getString(TaxFieldEditorPage.KEY_CODE);
		tax.galileoId = this.getString(TaxFieldEditorPage.KEY_GALILEO_ID);
		tax.code128Id = this.getString(TaxFieldEditorPage.KEY_CODE128_ID);
		tax.account = this.getString(TaxFieldEditorPage.KEY_ACCOUNT);
		tax.setCurrentTaxId(this.getCurrentTax(TaxFieldEditorPage.KEY_CURRENT_TAX).getId());
		this.setDirty(false);
	}
	
	public String getErrorMessage(DBResult result)
	{
		return ""; //$NON-NLS-1$
	}
	
}
