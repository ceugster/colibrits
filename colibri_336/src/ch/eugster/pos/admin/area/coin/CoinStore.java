/*
 * Created on 29.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.coin;

import org.eclipse.jface.viewers.IStructuredSelection;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.Coin;
import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class CoinStore extends PersistentDBStore
{
	
	public void initialize()
	{
		this.setElement(new Coin());
	}
	
	public void initialize(IStructuredSelection selection)
	{
		this.setElement(new Coin());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.Store#load()
	 */
	protected void load()
	{
		Coin coin = (Coin) this.element;
		this.putDefaultId(CoinFieldEditorPage.KEY_ID, coin.getId());
		this.putDefault(CoinFieldEditorPage.KEY_VALUE, new Double(coin.value));
		this.putToDefaults();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.Store#load()
	 */
	protected void store()
	{
		Coin coin = (Coin) this.element;
		coin.setId(this.getDefaultId(CoinFieldEditorPage.KEY_ID));
		coin.value = this.getDouble(CoinFieldEditorPage.KEY_VALUE).doubleValue();
		this.setDirty(false);
	}
	
	public String getErrorMessage(DBResult result)
	{
		String text = Messages.getString("CoinStore._1"); //$NON-NLS-1$
		if (result.getExternalErrorCode().equals(Messages.getString("CoinStore.S1009_2"))) { //$NON-NLS-1$
			text = Messages
							.getString("CoinStore.Der_Wert_kann_nicht_eingef_u00FCgt_werden,_da_er_bereits_vorhanden_ist._3"); //$NON-NLS-1$
		}
		return text;
	}
	
}
