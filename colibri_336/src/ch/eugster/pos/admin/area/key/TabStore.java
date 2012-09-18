/*
 * Created on 09.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.key;

import org.eclipse.jface.viewers.IStructuredSelection;

import ch.eugster.pos.db.CustomKey;
import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.Tab;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class TabStore extends PersistentDBStore
{
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.model.PersistentDBStore#load()
	 */
	protected void load()
	{
		Tab tab = (Tab) this.element;
		int rows = tab.rows == 0 ? 1 : tab.rows;
		int cols = tab.columns == 0 ? 1 : tab.columns;
		this.putDefault(TabFieldEditorPage.KEY_TITLE, tab.title);
		this.putDefault(TabFieldEditorPage.KEY_ORDER, new Integer(tab.order));
		this.putDefault(TabFieldEditorPage.KEY_POSITION_DEFAULT, new Boolean(tab.defaultTabPosition));
		this.putDefault(TabFieldEditorPage.KEY_PAYMENT_DEFAULT, new Boolean(tab.defaultTabPayment));
		this.putDefault(TabFieldEditorPage.KEY_ROWS, new Integer(rows));
		this.putDefault(TabFieldEditorPage.KEY_COLUMNS, new Integer(cols));
		this.putDefault(ButtonLayoutFieldEditor.KEY_KEYS, tab.getKeys());
		this.putToDefaults();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.model.PersistentDBStore#store()
	 */
	protected void store()
	{
		Tab tab = (Tab) this.element;
		tab.title = this.getString(TabFieldEditorPage.KEY_TITLE);
		tab.order = this.getInt(TabFieldEditorPage.KEY_ORDER).intValue();
		tab.defaultTabPosition = this.getBoolean(TabFieldEditorPage.KEY_POSITION_DEFAULT).booleanValue();
		tab.defaultTabPayment = this.getBoolean(TabFieldEditorPage.KEY_PAYMENT_DEFAULT).booleanValue();
		tab.rows = this.getInt(TabFieldEditorPage.KEY_ROWS).intValue();
		tab.columns = this.getInt(TabFieldEditorPage.KEY_COLUMNS).intValue();
		tab.setKeys((CustomKey[]) this.getValue(ButtonLayoutFieldEditor.KEY_KEYS));
		this.setDirty(false);
		
		if (tab.defaultTabPosition) Tab.clearDefaultPositionTabs(tab);
		if (tab.defaultTabPayment) Tab.clearDefaultPaymentTabs(tab);
	}
	
	public DBResult save()
	{
		this.store();
		DBResult result = ((Tab) this.element).store();
		if (result.getErrorCode() != 0)
		{
			result.log();
			result.setErrorText(this.getErrorMessage(result));
			result.showMessage();
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.admin.model.PersistentDBStore#getErrorMessage(ch.eugster
	 * .pos.db.DBResult)
	 */
	protected String getErrorMessage(DBResult result)
	{
		return result.getErrorText();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.model.Store#initialize()
	 */
	public void initialize()
	{
		this.setElement(new Tab());
	}
	
	public void initialize(IStructuredSelection selection)
	{
		this.setElement(new Tab());
	}
}
