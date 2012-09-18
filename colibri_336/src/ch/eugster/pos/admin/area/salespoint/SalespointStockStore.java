/*
 * Created on 29.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.salespoint;

import org.eclipse.jface.viewers.IStructuredSelection;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.Stock;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class SalespointStockStore extends PersistentDBStore
{
	
	public void initialize()
	{
		this.setElement(new Stock());
	}
	
	public void initialize(IStructuredSelection selection)
	{
		this.setElement(new Stock());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.Store#load()
	 */
	protected void load()
	{
		Stock stock = (Stock) this.element;
		this.putDefault(SalespointStockFieldEditorPage.KEY_STOCK, new Double(stock.getStock()));
		this.putToDefaults();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.Store#load()
	 */
	protected void store()
	{
		Stock stock = (Stock) this.element;
		stock.setStock(this.getDouble(SalespointStockFieldEditorPage.KEY_STOCK).doubleValue());
		this.setDirty(false);
	}
	
	public String getErrorMessage(DBResult result)
	{
		String text = ""; //$NON-NLS-1$
		if (result.getExternalErrorCode().equals(Messages.getString("SalespointStore.S1009_2"))) { //$NON-NLS-1$
			text = "Der Kassenstock kann nicht eingefügt werden, da bereits ein Kassenstock besteht."; //$NON-NLS-1$
		}
		else if (result.getExternalErrorCode().equals("23000")) { //$NON-NLS-1$
			text = "Ein solcher Kassenstock existiert bereits. Bitte wählen Sie eine andere Bezeichnung.";
		}
		return text;
	}
	
	public String getErrorMessage()
	{
		return "Dieser Stock kann nicht gelöscht werden."; //$NON-NLS-1$
	}
	
	public boolean isDeletable()
	{
		return ((Stock) this.element).isRemovable();
	}
	
}
