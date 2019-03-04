/*
 * Created on 29.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.salespoint;

import java.sql.Timestamp;
import java.util.GregorianCalendar;

import org.eclipse.jface.viewers.IStructuredSelection;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.db.Table;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class SalespointStore extends PersistentDBStore
{
	
	public void initialize()
	{
		this.setElement(new Salespoint());
	}
	
	public void initialize(IStructuredSelection selection)
	{
		this.setElement(new Salespoint());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.Store#load()
	 */
	protected void load()
	{
		Salespoint salespoint = (Salespoint) this.element;
		this.putDefaultId(SalespointFieldEditorPage.KEY_ID, salespoint.getId());
		this.putDefault(SalespointFieldEditorPage.KEY_NAME, salespoint.name);
		this.putDefault(SalespointFieldEditorPage.KEY_PLACE, salespoint.place);
		this.putDefault(SalespointFieldEditorPage.KEY_STOCK, new Double(salespoint.stock));
		// 10183
		this.putDefault(SalespointFieldEditorPage.KEY_VARIABLE_STOCK, new Boolean(salespoint.variableStock));
		// 10183
		this.putDefault(SalespointFieldEditorPage.KEY_ACTIVE, new Boolean(salespoint.active));
		this.putDefault(SalespointFieldEditorPage.KEY_EXPORT_ID, salespoint.exportId);
		this.putToDefaults();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.Store#load()
	 */
	protected void store()
	{
		Salespoint salespoint = (Salespoint) this.element;
		salespoint.setId(this.getDefaultId(SalespointFieldEditorPage.KEY_ID));
		salespoint.name = this.getString(SalespointFieldEditorPage.KEY_NAME);
		salespoint.place = this.getString(SalespointFieldEditorPage.KEY_PLACE);
		salespoint.stock = this.getDouble(SalespointFieldEditorPage.KEY_STOCK).doubleValue();
		// 10183
		salespoint.variableStock = this.getBoolean(SalespointFieldEditorPage.KEY_VARIABLE_STOCK).booleanValue();
		// 10183
		salespoint.active = this.getBoolean(SalespointFieldEditorPage.KEY_ACTIVE).booleanValue();
		salespoint.exportId = this.getString(SalespointFieldEditorPage.KEY_EXPORT_ID);
		if (salespoint.getId() == null)
		{
			salespoint.currentReceiptId = Table.ZERO_VALUE;
			salespoint.currentDate = new Timestamp(GregorianCalendar.getInstance().getTimeInMillis());
		}
		this.setDirty(false);
	}
	
	public String getErrorMessage(DBResult result)
	{
		String text = ""; //$NON-NLS-1$
		if (result.getExternalErrorCode().equals(Messages.getString("SalespointStore.S1009_2"))) { //$NON-NLS-1$
			text = Messages
							.getString("SalespointStore.Die_Kasse_kann_nicht_eingef_u00FCgt_werden,_da_bereits_eine_Kasse_mit_der_angegebenen_Bezeichnung_besteht._3"); //$NON-NLS-1$
		}
		else if (result.getExternalErrorCode().equals("23000")) { //$NON-NLS-1$
			text = "Eine Kasse mit der gewählten Bezeichnung existiert bereits. Bitte wählen Sie eine andere Bezeichnung.";
		}
		return text;
	}
	
	public String getErrorMessage()
	{
		return "Diese Kasse darf nicht gelöscht werden, da ihr noch gültige Belege zugeordnet sind."; //$NON-NLS-1$
	}
	
	public boolean isDeletable()
	{
		return ((Salespoint) this.element).isRemovable();
	}
	
}
