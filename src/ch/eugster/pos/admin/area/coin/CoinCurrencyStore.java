/*
 * Created on 29.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.coin;

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
public class CoinCurrencyStore extends PersistentDBStore
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
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.Store#load()
	 */
	protected void store()
	{
		this.setDirty(false);
	}
	
	public String getErrorMessage(DBResult result)
	{
		return "";
	}
	
}
