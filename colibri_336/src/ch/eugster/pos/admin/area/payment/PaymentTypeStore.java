/*
 * Created on 27.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.payment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.event.IPaymentTypeChangeListener;
import ch.eugster.pos.admin.event.PaymentTypeChangeEvent;
import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.PaymentType;
import ch.eugster.pos.db.PaymentTypeGroup;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class PaymentTypeStore extends PersistentDBStore
{
	
	private Collection paymentTypeChangeListeners = new ArrayList();
	
	private static PaymentTypeStore store = null;
	
	private PaymentTypeStore()
	{
		
	}
	
	public static PaymentTypeStore getInstance()
	{
		if (PaymentTypeStore.store == null) PaymentTypeStore.store = new PaymentTypeStore();
		
		return PaymentTypeStore.store;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.PersistentDBStore#load()
	 */
	protected void load()
	{
		PaymentType paymentType = (PaymentType) this.element;
		this.putDefaultId(PaymentTypeFieldEditorPage.KEY_ID, paymentType.getId());
		this.putDefault(PaymentTypeFieldEditorPage.KEY_GROUP, paymentType.getPaymentTypeGroup());
		this.putDefault(PaymentTypeFieldEditorPage.KEY_REMOVEABLE, new Boolean(paymentType.removeable));
		this.putDefault(PaymentTypeFieldEditorPage.KEY_NAME, paymentType.name);
		this.putDefault(PaymentTypeFieldEditorPage.KEY_CODE, paymentType.code);
		this.putDefault(PaymentTypeFieldEditorPage.KEY_CURRENCY, paymentType.getForeignCurrency());
		this.putDefault(PaymentTypeFieldEditorPage.KEY_ACCOUNT, paymentType.account);
		this.putDefault(PaymentTypeFieldEditorPage.KEY_IS_VOUCHER, new Boolean(paymentType.voucher));
		// 10226
		// putDefault(PaymentTypeFieldEditorPage.KEY_IS_PAYMENT_VOUCHER, new
		// Boolean(paymentType.paymentVoucher));
		this.putDefault(PaymentTypeFieldEditorPage.KEY_CASH, new Boolean(paymentType.cash));
		// 10226
		this.putDefault(PaymentTypeFieldEditorPage.KEY_OPEN_CASHDRAWER, new Boolean(paymentType.openCashdrawer));
		this.putDefault(PaymentTypeFieldEditorPage.KEY_IS_PAYMENT_BACK, new Boolean(paymentType.back));
		this.putDefault(PaymentTypeFieldEditorPage.KEY_EXPORT_ID, paymentType.exportId);
		this.putDefault(PaymentTypeFieldEditorPage.KEY_SORT, new Integer(paymentType.sort));
		this.putToDefaults();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.PersistentDBStore#store()
	 */
	protected void store()
	{
		PaymentType paymentType = (PaymentType) this.element;
		paymentType.setId(this.getDefaultId(PaymentTypeFieldEditorPage.KEY_ID));
		paymentType.setPaymentTypeGroup(this.getPaymentTypeGroup(PaymentTypeFieldEditorPage.KEY_GROUP));
		paymentType.name = this.getString(PaymentTypeFieldEditorPage.KEY_NAME);
		paymentType.removeable = this.getBoolean(PaymentTypeFieldEditorPage.KEY_REMOVEABLE).booleanValue();
		paymentType.code = this.getString(PaymentTypeFieldEditorPage.KEY_CODE);
		paymentType.setForeignCurrency(this.getForeignCurrency(PaymentTypeFieldEditorPage.KEY_CURRENCY));
		paymentType.account = this.getString(PaymentTypeFieldEditorPage.KEY_ACCOUNT);
		paymentType.voucher = this.getBoolean(PaymentTypeFieldEditorPage.KEY_IS_VOUCHER).booleanValue();
		// 10226
		// paymentType.paymentVoucher =
		// getBoolean(PaymentTypeFieldEditorPage.KEY_IS_PAYMENT_VOUCHER).booleanValue();
		paymentType.cash = this.getBoolean(PaymentTypeFieldEditorPage.KEY_CASH).booleanValue();
		// 10226
		paymentType.openCashdrawer = this.getBoolean(PaymentTypeFieldEditorPage.KEY_OPEN_CASHDRAWER).booleanValue();
		paymentType.back = this.getBoolean(PaymentTypeFieldEditorPage.KEY_IS_PAYMENT_BACK).booleanValue();
		paymentType.exportId = this.getString(PaymentTypeFieldEditorPage.KEY_EXPORT_ID);
		paymentType.sort = this.getInt(PaymentTypeFieldEditorPage.KEY_SORT).intValue();
		this.setDirty(false);
	}
	
	public DBResult save()
	{
		// 10226
		// if
		// (getBoolean(PaymentTypeFieldEditorPage.KEY_IS_PAYMENT_VOUCHER).booleanValue()
		// && PaymentType.doesBackVoucherExist()) {
		// MessageDialog dialog = new
		// MessageDialog(MainWindow.getInstance().getShell(),
		//						"Rückgeldgutschein existiert bereits",  //$NON-NLS-1$
		// MessageDialog.getImage(MessageDialog.DLG_IMG_ERROR),
		// "Ein Rückgeldgutschein wurde bereits definiert. Deaktivieren Sie die entsprechende Checkbox, bevor Sie eine andere Zahlungsart als Rückgeldgutschein auswählen.",
		// MessageDialog.ERROR,
		//						new String[] {Messages.getString("DBResult.Ok_14")}, //$NON-NLS-1$
		// 0);
		// dialog.open();
		// return new DBResult(1,"");
		//			
		// }
		// 10226
		PaymentType oldElement = (PaymentType) this.element;
		this.store();
		DBResult result = ((PaymentType) this.element).store();
		if (result.getErrorCode() == 0)
		{
			this.firePaymentTypeChangeEvent(oldElement, (PaymentType) this.element);
		}
		else
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
	 * @see ch.eugster.pos.admin.Store#initialize()
	 */
	public void initialize()
	{
		PaymentType type = new PaymentType();
		type.setForeignCurrency(ForeignCurrency.getDefaultCurrency());
		this.setElement(type);
	}
	
	public void initialize(IStructuredSelection selection)
	{
		Object object = selection.getFirstElement();
		PaymentType type = new PaymentType();
		if (object instanceof PaymentType)
		{
			type.setPaymentTypeGroup(((PaymentType) object).getPaymentTypeGroup());
		}
		else if (object instanceof PaymentTypeGroup)
		{
			type.setPaymentTypeGroup((PaymentTypeGroup) object);
		}
		this.setElement(type);
	}
	
	protected String getErrorMessage(DBResult dbResult)
	{
		String result = ""; //$NON-NLS-1$
		if (dbResult.getExternalErrorCode().equals(Messages.getString("PaymentTypeStore.S1009_2"))) { //$NON-NLS-1$
		}
		return result;
	}
	
	public boolean isDeletable()
	{
		// 10226
		PaymentType pt = (PaymentType) this.getElement();
		if (pt.getId().equals(PaymentType.CASH_ID) || pt.cash)
		{
			this
							.setErrorMessage(Messages.getString("PaymentTypeStore.Die_Zahlungsart_1") + " " + pt.name + " " + Messages.getString("PaymentTypeStore._darf_nicht_gel_u00F6scht_werden._4")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			return false;
		}
		else
			return true;
		// 10226
	}
	
	public void addPaymentTypeChangeListener(IPaymentTypeChangeListener listener)
	{
		if (!this.paymentTypeChangeListeners.contains(listener)) this.paymentTypeChangeListeners.add(listener);
	}
	
	public void removePaymentTypeChangeListener(IPaymentTypeChangeListener listener)
	{
		if (this.paymentTypeChangeListeners.contains(listener)) this.paymentTypeChangeListeners.remove(listener);
	}
	
	public void firePaymentTypeChangeEvent(PaymentType oldType, PaymentType newType)
	{
		PaymentTypeChangeEvent event = new PaymentTypeChangeEvent(oldType, newType);
		Iterator iterator = this.paymentTypeChangeListeners.iterator();
		while (iterator.hasNext())
		{
			IPaymentTypeChangeListener listener = (IPaymentTypeChangeListener) iterator.next();
			listener.paymentTypeChange(event);
		}
	}
}
