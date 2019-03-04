/*
 * Created on 27.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.payment;

import org.eclipse.jface.viewers.IStructuredSelection;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.PaymentTypeGroup;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class PaymentTypeGroupStore extends PersistentDBStore
{
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.PersistentDBStore#load()
	 */
	protected void load()
	{
		PaymentTypeGroup paymentTypeGroup = (PaymentTypeGroup) this.element;
		this.putDefaultId(PaymentTypeGroupFieldEditorPage.KEY_ID, paymentTypeGroup.getId());
		this.putDefault(PaymentTypeGroupFieldEditorPage.KEY_NAME, paymentTypeGroup.name);
		this.putToDefaults();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.PersistentDBStore#store()
	 */
	protected void store()
	{
		PaymentTypeGroup paymentTypeGroup = (PaymentTypeGroup) this.element;
		paymentTypeGroup.setId(this.getDefaultId(PaymentTypeGroupFieldEditorPage.KEY_ID));
		paymentTypeGroup.name = this.getString(PaymentTypeGroupFieldEditorPage.KEY_NAME);
		this.setDirty(false);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.Store#initialize()
	 */
	public void initialize()
	{
		this.setElement(new PaymentTypeGroup());
	}
	
	public void initialize(IStructuredSelection selection)
	{
		this.setElement(new PaymentTypeGroup());
	}
	
	protected String getErrorMessage(DBResult dbResult)
	{
		String result = ""; //$NON-NLS-1$
		if (dbResult.getExternalErrorCode().equals(Messages.getString("PaymentTypeGroupStore.S1009_2"))) { //$NON-NLS-1$
		}
		return result;
	}
	
	public boolean isDeletable()
	{
		// 10226
		PaymentTypeGroup ptg = (PaymentTypeGroup) this.getElement();
		if (ptg.getId().equals(new Long(1l)))
		{
			this.setErrorMessage("Die Zahlungsartengruppe " + ptg.name + " darf nicht gelöscht werden"); //$NON-NLS-1$ //$NON-NLS-2$
			return false;
		}
		else
			return true;
		// 10226
	}
	
}
