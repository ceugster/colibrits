/*
 * Created on 29.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.option;

import org.eclipse.jface.viewers.IStructuredSelection;

import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.Option;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class OptionStore extends PersistentDBStore
{
	
	public void initialize()
	{
		this.setElement(new Option());
	}
	
	public void initialize(IStructuredSelection selection)
	{
		this.setElement(new Option());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.Store#load()
	 */
	protected void load()
	{
		Option option = (Option) this.element;
		this.putDefaultId(OptionFieldEditorPage.KEY_ID, option.getId());
		this.putDefault(OptionFieldEditorPage.KEY_CODE, option.code);
		this.putDefault(OptionFieldEditorPage.KEY_NAME, option.name);
		this.putToDefaults();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.Store#load()
	 */
	protected void store()
	{
		Option option = (Option) this.element;
		option.setId(this.getDefaultId(OptionFieldEditorPage.KEY_ID));
		option.code = this.getString(OptionFieldEditorPage.KEY_CODE);
		option.name = this.getString(OptionFieldEditorPage.KEY_NAME);
		this.setDirty(false);
	}
	
	public String getErrorMessage(DBResult result)
	{
		if (result.getExternalErrorCode().equals("23000"))
		{
			return "Der Optionscode ist nicht eindeutig. Bitte wählen Sie einen Optionscode, der noch nicht vorhanden ist.";
		}
		return ""; //$NON-NLS-1$
	}
	
}
