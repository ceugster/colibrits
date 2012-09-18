/*
 * Created on 03.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.tax;

import org.eclipse.jface.viewers.IStructuredSelection;

import ch.eugster.pos.db.CurrentTax;
import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class CurrentTaxStore extends PersistentDBStore
{
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.PersistentDBStore#load()
	 */
	protected void load()
	{
		CurrentTax currentTax = (CurrentTax) this.element;
		this.putDefaultId(CurrentTaxFieldEditorPage.KEY_ID, currentTax.getId());
		this.putDefault(CurrentTaxFieldEditorPage.KEY_FIBU_ID, currentTax.fibuId);
		this.putDefault(CurrentTaxFieldEditorPage.KEY_PERCENTAGE, new Double(currentTax.percentage));
		this.putDefault(CurrentTaxFieldEditorPage.KEY_DATE, currentTax.getValidationDate());
		this.putToDefaults();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.PersistentDBStore#store()
	 */
	protected void store()
	{
		CurrentTax currentTax = (CurrentTax) this.element;
		currentTax.setId(this.getDefaultId(CurrentTaxFieldEditorPage.KEY_ID));
		currentTax.fibuId = this.getString(CurrentTaxFieldEditorPage.KEY_FIBU_ID);
		currentTax.percentage = this.getDouble(CurrentTaxFieldEditorPage.KEY_PERCENTAGE).doubleValue();
		currentTax.setValidationDate(this.getDate(CurrentTaxFieldEditorPage.KEY_DATE));
		this.setDirty(false);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.Store#initialize()
	 */
	public void initialize()
	{
		this.setElement(new CurrentTax());
	}
	
	public void initialize(IStructuredSelection selection)
	{
		this.setElement(new CurrentTax());
	}
	
	public String getErrorMessage(DBResult result)
	{
		return ""; //$NON-NLS-1$
	}
	
}
