/*
 * Created on 27.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.fixkey;

import org.eclipse.jface.viewers.IStructuredSelection;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.FixKeyGroup;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class FixKeyGroupStore extends PersistentDBStore
{
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.PersistentDBStore#load()
	 */
	protected void load()
	{
		FixKeyGroup fixKeyGroup = (FixKeyGroup) this.element;
		this.putDefault(FixKeyGroupFieldEditorPage.KEY_NAME, fixKeyGroup.name);
		this.putToDefaults();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.PersistentDBStore#store()
	 */
	protected void store()
	{
		FixKeyGroup fixKeyGroup = (FixKeyGroup) this.element;
		fixKeyGroup.name = this.getString(FixKeyGroupFieldEditorPage.KEY_NAME);
		this.setDirty(false);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.Store#initialize()
	 */
	public void initialize()
	{
		this.setElement(new FixKeyGroup());
	}
	
	public void initialize(IStructuredSelection selection)
	{
		this.setElement(new FixKeyGroup());
	}
	
	protected String getErrorMessage(DBResult dbResult)
	{
		String result = ""; //$NON-NLS-1$
		if (dbResult.getExternalErrorCode().equals(Messages.getString("FixKeyGroupStore.S1009_2"))) { //$NON-NLS-1$
		}
		return result;
	}
	
}
