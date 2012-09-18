/*
 * Created on 29.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.swt;

import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IStructuredSelection;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.Table;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public abstract class PersistentDBStore extends Store
{
	
	public void setElement(Object element)
	{
		this.element = element;
		this.load();
	}
	
	public Object getElement()
	{
		return this.element;
	}
	
	public abstract void initialize(IStructuredSelection selection);
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.swt.Store#save()
	 */
	protected abstract void load();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.swt.Store#save()
	 */
	protected abstract void store();
	
	public DBResult save()
	{
		this.store();
		DBResult result = ((Table) this.element).store();
		if (result.getErrorCode() != 0)
		{
			result.log();
			result.setErrorText(this.getErrorMessage(result));
			result.showMessage();
		}
		return result;
	}
	
	protected abstract String getErrorMessage(DBResult result);
	
	public boolean delete()
	{
		this.store();
		DBResult result = ((Table) this.element).delete();
		this.errorMessage = result.getErrorCode() == 0 ? "" : Messages.getString("PersistentDBStore.Beim_L_u00F6schen_des_Datensatzes_ist_ein_Fehler_aufgetreten._Der_Datensatz_wurde_nicht_gel_u00F6scht._2"); //$NON-NLS-1$ //$NON-NLS-2$
		return result.getErrorCode() == 0;
	}
	
	public boolean isDeletable()
	{
		return true;
	}
	
	public void propertyChange(PropertyChangeEvent e)
	{
		if (e.getProperty().equals(this.element.getClass().getName()))
		{
			this.setElement(e.getNewValue());
		}
	}
	
	protected void setErrorMessage(String message)
	{
		this.errorMessage = message;
	}
	
	protected void putDefaultId(String key, Long value)
	{
		if (value == null)
		{
			value = Table.ZERO_VALUE;
		}
		this.putDefault(key, value);
	}
	
	protected void putId(String key, Long value)
	{
		if (value == null) value = Table.ZERO_VALUE;
		this.putValue(key, value);
	}
	
	protected Long getDefaultId(String key)
	{
		Long id = this.getDefaultLong(key);
		return id.equals(Table.ZERO_VALUE) ? null : id;
	}
	
	protected Long getId(String key)
	{
		Long id = this.getLong(key);
		return id.equals(Table.ZERO_VALUE) ? null : id;
	}
	
	public String getErrorMessage()
	{
		return this.errorMessage;
	}
	
	protected Object element;
	protected String errorMessage = ""; //$NON-NLS-1$
}
