/*
 * Created on 29.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.tax;

import org.eclipse.jface.viewers.IStructuredSelection;

import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.TaxType;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class TaxTypeStore extends PersistentDBStore
{
	
	public void initialize()
	{
		this.setElement(new TaxType());
	}
	
	public void initialize(IStructuredSelection selection)
	{
		this.setElement(new TaxType());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.Store#load()
	 */
	protected void load()
	{
		TaxType taxType = (TaxType) this.element;
		this.putDefaultId(TaxTypeFieldEditorPage.KEY_ID, taxType.getId());
		this.putDefault(TaxTypeFieldEditorPage.KEY_NAME, taxType.name);
		this.putToDefaults();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.Store#load()
	 */
	protected void store()
	{
		TaxType taxType = (TaxType) this.element;
		taxType.setId(this.getDefaultId(TaxTypeFieldEditorPage.KEY_ID));
		taxType.name = this.getString(TaxTypeFieldEditorPage.KEY_NAME);
		this.setDirty(false);
	}
	
	public String getErrorMessage(DBResult result)
	{
		return ""; //$NON-NLS-1$
	}
	
}
