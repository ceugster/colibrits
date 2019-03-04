/*
 * Created on 29.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.currency;

import org.eclipse.jface.viewers.IStructuredSelection;

import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class CurrencyStore extends PersistentDBStore
{
	
	public void initialize()
	{
		this.setElement(new ForeignCurrency());
	}
	
	public void initialize(IStructuredSelection selection)
	{
		this.setElement(new ForeignCurrency());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.Store#load()
	 */
	protected void load()
	{
		ForeignCurrency foreignCurrency = (ForeignCurrency) this.element;
		this.putDefaultId(CurrencyFieldEditorPage.KEY_ID, foreignCurrency.getId());
		this.putDefault(CurrencyFieldEditorPage.KEY_CODE, foreignCurrency.code);
		this.putDefault(CurrencyFieldEditorPage.KEY_NAME, foreignCurrency.name);
		this.putDefault(CurrencyFieldEditorPage.KEY_REGION, foreignCurrency.region);
		this.putDefault(CurrencyFieldEditorPage.KEY_QUOTATION, new Double(foreignCurrency.quotation));
		this.putDefault(CurrencyFieldEditorPage.KEY_ROUND_FACTOR, new Double(foreignCurrency.roundFactor));
		this.putToDefaults();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.Store#load()
	 */
	protected void store()
	{
		ForeignCurrency foreignCurrency = (ForeignCurrency) this.element;
		foreignCurrency.setId(this.getDefaultId(CurrencyFieldEditorPage.KEY_ID));
		foreignCurrency.code = this.getString(CurrencyFieldEditorPage.KEY_CODE);
		foreignCurrency.name = this.getString(CurrencyFieldEditorPage.KEY_NAME);
		foreignCurrency.region = this.getString(CurrencyFieldEditorPage.KEY_REGION);
		foreignCurrency.quotation = this.getDouble(CurrencyFieldEditorPage.KEY_QUOTATION).doubleValue();
		foreignCurrency.roundFactor = this.getDouble(CurrencyFieldEditorPage.KEY_ROUND_FACTOR).doubleValue();
		this.setDirty(false);
	}
	
	public String getErrorMessage(DBResult result)
	{
		return ""; //$NON-NLS-1$
	}
}
