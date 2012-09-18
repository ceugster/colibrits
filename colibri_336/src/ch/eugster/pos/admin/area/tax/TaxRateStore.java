/*
 * Created on 29.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.tax;

import org.eclipse.jface.viewers.IStructuredSelection;

import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.TaxRate;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class TaxRateStore extends PersistentDBStore
{
	
	public void initialize()
	{
		this.setElement(new TaxRate());
	}
	
	public void initialize(IStructuredSelection selection)
	{
		this.setElement(new TaxRate());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.Store#load()
	 */
	protected void load()
	{
		TaxRate taxRate = (TaxRate) this.element;
		this.putDefaultId(TaxRateFieldEditorPage.KEY_ID, taxRate.getId());
		this.putDefault(TaxRateFieldEditorPage.KEY_NAME, taxRate.name);
		this.putToDefaults();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.Store#load()
	 */
	protected void store()
	{
		TaxRate taxRate = (TaxRate) this.element;
		taxRate.setId(this.getDefaultId(TaxRateFieldEditorPage.KEY_ID));
		taxRate.name = this.getString(TaxRateFieldEditorPage.KEY_NAME);
		this.setDirty(false);
	}
	
	public String getErrorMessage(DBResult result)
	{
		return ""; //$NON-NLS-1$
	}
	
}
