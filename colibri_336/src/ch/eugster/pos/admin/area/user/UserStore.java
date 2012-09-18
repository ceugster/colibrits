/*
 * Created on 29.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.user;

import org.eclipse.jface.viewers.IStructuredSelection;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.Table;
import ch.eugster.pos.db.User;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class UserStore extends PersistentDBStore
{
	
	public void initialize()
	{
		this.setElement(new User());
	}
	
	public void initialize(IStructuredSelection selection)
	{
		this.setElement(new User());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.Store#load()
	 */
	protected void load()
	{
		User user = (User) this.element;
		this.putDefaultId(UserFieldEditorPage.KEY_ID, user.getId());
		this.putDefault(UserFieldEditorPage.KEY_USERNAME, user.username);
		this.putDefault(UserFieldEditorPage.KEY_PASSWORD, user.password);
		this.putDefault(UserFieldEditorPage.KEY_POS_LOGIN, user.posLogin);
		this.putDefault(UserFieldEditorPage.KEY_STATE, new Integer(user.status));
		this.putDefault(UserFieldEditorPage.KEY_DEFAULT_USER, user.defaultUser);
		this.putDefault(UserFieldEditorPage.KEY_REVERSE_RECEIPTS, new Boolean(user.getReverseReceipts()));
		this.putToDefaults();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.Store#load()
	 */
	protected void store()
	{
		User user = (User) this.element;
		user.setId(this.getDefaultId(UserFieldEditorPage.KEY_ID));
		user.username = this.getString(UserFieldEditorPage.KEY_USERNAME);
		user.password = this.getString(UserFieldEditorPage.KEY_PASSWORD);
		user.posLogin = this.getLong(UserFieldEditorPage.KEY_POS_LOGIN);
		user.status = this.getInt(UserFieldEditorPage.KEY_STATE).intValue();
		user.defaultUser = this.getBoolean(UserFieldEditorPage.KEY_DEFAULT_USER);
		user.setReverseReceipts(this.getBoolean(UserFieldEditorPage.KEY_REVERSE_RECEIPTS).booleanValue());
		this.setDirty(false);
	}
	
	public String getErrorMessage(DBResult result)
	{
		if (result.getExternalErrorCode().equals("S1009")) { //$NON-NLS-1$
			this.setErrorMessage(Messages
							.getString("UserStore.Es_gibt_bereits_einen_Benutzer_mit_dem_angegebenen_Login._2")); //$NON-NLS-1$
		}
		return this.errorMessage;
	}
	
	public boolean isDeletable()
	{
		if (((User) this.element).equals(User.getCurrentUser()))
		{
			this.errorMessage = Messages
							.getString("UserStore.Der_gew_u00E4hlte_Benutzer_darf_nicht_gel_u00F6scht_werden,_da_er_zur_Zeit_eingeloggt_ist._1"); //$NON-NLS-1$
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public DBResult save()
	{
		DBResult result = new DBResult(0, "");
		User user = (User) this.element;
		Database.getStandard().getBroker().beginTransaction();
		if (this.getBoolean(UserFieldEditorPage.KEY_DEFAULT_USER).booleanValue())
		{
			User[] users = User.selectAll(true);
			for (int i = 0; i < users.length; i++)
			{
				users[i].defaultUser = new Boolean(false);
				result = users[i].store();
			}
		}
		if (result.getErrorCode() == 0)
		{
			this.store();
			result = ((Table) this.element).store();
			if (result.getErrorCode() != 0)
			{
				result.log();
				result.setErrorText(this.getErrorMessage(result));
				result.showMessage();
			}
		}
		if (result.getErrorCode() == 0)
		{
			if (user.defaultUser.equals(new Boolean(true)))
			{
				User.setDefaultUser(user);
			}
			Database.getStandard().getBroker().commitTransaction();
			
		}
		else
		{
			Database.getStandard().getBroker().abortTransaction();
		}
		return result;
	}
	
}
