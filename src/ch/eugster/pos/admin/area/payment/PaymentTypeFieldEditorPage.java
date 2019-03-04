/*
 * Created on 27.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.payment;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.MainWindow;
import ch.eugster.pos.admin.gui.widget.BooleanFieldEditor;
import ch.eugster.pos.admin.gui.widget.ComboFieldEditor;
import ch.eugster.pos.admin.gui.widget.FieldEditorPage;
import ch.eugster.pos.admin.gui.widget.IntegerFieldEditor;
import ch.eugster.pos.admin.gui.widget.LongFieldEditor;
import ch.eugster.pos.admin.gui.widget.StringFieldEditor;
import ch.eugster.pos.admin.gui.widget.UniquePrimaryKeyValidator;
import ch.eugster.pos.db.PaymentType;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class PaymentTypeFieldEditorPage extends FieldEditorPage
{
	
	/**
	 * @param style
	 * @param store
	 */
	public PaymentTypeFieldEditorPage(String name, int style, PersistentDBStore store)
	{
		super(name, style, store);
	}
	
	/**
	 * @param title
	 * @param style
	 * @param store
	 */
	public PaymentTypeFieldEditorPage(String name, String title, int style, PersistentDBStore store)
	{
		super(name, title, style, store);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 * @param store
	 */
	public PaymentTypeFieldEditorPage(String name, String title, ImageDescriptor image, int style,
					PersistentDBStore store)
	{
		super(name, title, image, style, store);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.widgets.FieldEditorPage#createFieldEditors()
	 */
	protected void createFieldEditors()
	{
		if (!PaymentType.isIdFieldAutoincrement(PaymentType.class))
		{
			LongFieldEditor idEditor = new LongFieldEditor(PaymentTypeFieldEditorPage.KEY_ID, Messages
							.getString("PaymentTypeFieldEditorPage.Id*_1"), this.getFieldEditorParent()); //$NON-NLS-1$
			idEditor.setContentValidator(new UniquePrimaryKeyValidator(idEditor, PaymentType.class));
			this.addField(idEditor);
		}
		
		this.nameEditor = new StringFieldEditor(
						PaymentTypeFieldEditorPage.KEY_NAME,
						Messages.getString("PaymentTypeFieldEditorPage.Bezeichnung_1"), -1, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		this.nameEditor.setEmptyStringAllowed(false);
		this.nameEditor.setErrorMessage(Messages
						.getString("PaymentTypeFieldEditorPage.Die_Angabe_einer_Bezeichnung_ist_notwendig._2")); //$NON-NLS-1$
		this.nameEditor.showErrorMessage();
		this.addField(this.nameEditor);
		
		this.shortNameEditor = new StringFieldEditor(
						PaymentTypeFieldEditorPage.KEY_CODE,
						Messages.getString("PaymentTypeFieldEditorPage.Beschriftung_3"), -1, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		this.shortNameEditor.setEmptyStringAllowed(true);
		this.addField(this.shortNameEditor);
		
		BooleanFieldEditor cashEditor = new BooleanFieldEditor(PaymentTypeFieldEditorPage.KEY_CASH,
						"Barzahlung", this.getFieldEditorParent()); //$NON-NLS-1$
		cashEditor.setEnabled(false, this.getFieldEditorParent());
		this.addField(cashEditor);
		
		this.currencyEditor = new ComboFieldEditor(PaymentTypeFieldEditorPage.KEY_CURRENCY, Messages
						.getString("PaymentTypeFieldEditorPage.W_u00E4hrung_4"), this.getFieldEditorParent()); //$NON-NLS-1$
		this.currencyEditor.setContentProvider(new CurrencyComboFieldEditorContentProvider(this.currencyEditor));
		this.currencyEditor.setLabelProvider(new CurrencyComboFieldEditorLabelProvider());
		this.currencyEditor.setInput(null);
		this.addField(this.currencyEditor);
		
		this.accountEditor = new StringFieldEditor(
						PaymentTypeFieldEditorPage.KEY_ACCOUNT,
						Messages.getString("PaymentTypeFieldEditorPage.Konto_8"), -1, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		this.accountEditor.setEmptyStringAllowed(true);
		this.addField(this.accountEditor);
		
		BooleanFieldEditor isVoucherEditor = new BooleanFieldEditor(PaymentTypeFieldEditorPage.KEY_IS_VOUCHER,
						"Gilt als Gutschein oder Bücherbon", this.getFieldEditorParent()); //$NON-NLS-1$
		this.addField(isVoucherEditor);
		
		BooleanFieldEditor isBackEditor = new BooleanFieldEditor(PaymentTypeFieldEditorPage.KEY_IS_PAYMENT_BACK,
						"Als Rückgeld verwendbar", this.getFieldEditorParent()); //$NON-NLS-1$
		this.addField(isBackEditor);
		
		BooleanFieldEditor openCashdrawerEditor = new BooleanFieldEditor(
						PaymentTypeFieldEditorPage.KEY_OPEN_CASHDRAWER,
						"Bei Verwendung Schublade öffnen", this.getFieldEditorParent()); //$NON-NLS-1$
		this.addField(openCashdrawerEditor);
		
		IntegerFieldEditor sortEditor = new IntegerFieldEditor(PaymentTypeFieldEditorPage.KEY_SORT,
						"Sortierung", this.getFieldEditorParent()); //$NON-NLS-1$
		sortEditor.setEmptyStringAllowed(false);
		this.addField(sortEditor);
		
		this.exportIdEditor = new StringFieldEditor(PaymentTypeFieldEditorPage.KEY_EXPORT_ID,
						"Export/Import-ID", 10, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		this.exportIdEditor.setEmptyStringAllowed(true);
		this.addField(this.exportIdEditor);
		
	}
	
	public void selectionChanged(Object element)
	{
		super.selectionChanged(element);
		if (this.getStore().getElement() instanceof PaymentType)
		{
			PaymentType paymentType = (PaymentType) this.getStore().getElement();
			this.isRemoveActive = !paymentType.cash;
			MainWindow.getInstance().updateButtons();
		}
	}
	
	public boolean isInstance(Object element)
	{
		return element instanceof PaymentType;
	}
	
	public String getElementName()
	{
		PaymentType paymentType = (PaymentType) this.getStore().getElement();
		return paymentType.name;
	}
	
	private StringFieldEditor nameEditor;
	private StringFieldEditor shortNameEditor;
	private ComboFieldEditor currencyEditor;
	private StringFieldEditor accountEditor;
	private StringFieldEditor exportIdEditor;
	
	public static final String KEY_ID = "id"; //$NON-NLS-1$
	public static final String KEY_GROUP = "group"; //$NON-NLS-1$
	public static final String KEY_REMOVEABLE = "removeable"; //$NON-NLS-1$
	public static final String KEY_NAME = "name"; //$NON-NLS-1$
	public static final String KEY_CODE = "code"; //$NON-NLS-1$
	public static final String KEY_CURRENCY = "currency"; //$NON-NLS-1$
	public static final String KEY_ACCOUNT = "account"; //$NON-NLS-1$
	public static final String KEY_IS_VOUCHER = "voucher"; //$NON-NLS-1$
	// 10226
	public static final String KEY_CASH = "cash"; //$NON-NLS-1$
	// 10226
	public static final String KEY_OPEN_CASHDRAWER = "open-cashdrawer"; //$NON-NLS-1$
	public static final String KEY_IS_PAYMENT_BACK = "payment.back";
	public static final String KEY_EXPORT_ID = "export-id"; //$NON-NLS-1$
	
	public static final String KEY_SORT = "sort"; //$NON-NLS-1$
}
