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
import ch.eugster.pos.admin.gui.widget.FieldEditorPage;
import ch.eugster.pos.admin.gui.widget.LongFieldEditor;
import ch.eugster.pos.admin.gui.widget.StringFieldEditor;
import ch.eugster.pos.admin.gui.widget.UniquePrimaryKeyValidator;
import ch.eugster.pos.db.PaymentTypeGroup;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class PaymentTypeGroupFieldEditorPage extends FieldEditorPage
{
	
	/**
	 * @param style
	 * @param store
	 */
	public PaymentTypeGroupFieldEditorPage(String name, int style, PersistentDBStore store)
	{
		super(name, style, store);
	}
	
	/**
	 * @param title
	 * @param style
	 * @param store
	 */
	public PaymentTypeGroupFieldEditorPage(String name, String title, int style, PersistentDBStore store)
	{
		super(name, title, style, store);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 * @param store
	 */
	public PaymentTypeGroupFieldEditorPage(String name, String title, ImageDescriptor image, int style,
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
		if (!PaymentTypeGroup.isIdFieldAutoincrement(PaymentTypeGroup.class))
		{
			LongFieldEditor idEditor = new LongFieldEditor(PaymentTypeGroupFieldEditorPage.KEY_ID, Messages
							.getString("PaymentTypeGroupFieldEditorPage.Id_1"), this.getFieldEditorParent()); //$NON-NLS-1$
			idEditor.setContentValidator(new UniquePrimaryKeyValidator(idEditor, PaymentTypeGroup.class));
			this.addField(idEditor);
		}
		
		this.nameEditor = new StringFieldEditor(
						PaymentTypeGroupFieldEditorPage.KEY_NAME,
						Messages.getString("PaymentTypeGroupFieldEditorPage.Bezeichnung_1"), -1, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		this.nameEditor.setEmptyStringAllowed(false);
		this.nameEditor.setErrorMessage(Messages
						.getString("PaymentTypeGroupFieldEditorPage.Die_Angabe_einer_Bezeichnung_ist_notwendig._2")); //$NON-NLS-1$
		this.nameEditor.showErrorMessage();
		this.addField(this.nameEditor);
	}
	
	public boolean isInstance(Object element)
	{
		return element instanceof PaymentTypeGroup;
	}
	
	public void selectionChanged(Object element)
	{
		super.selectionChanged(element);
		if (this.getStore().getElement() instanceof PaymentTypeGroup)
		{
			PaymentTypeGroup paymentTypeGroup = (PaymentTypeGroup) this.getStore().getElement();
			this.isRemoveActive = !paymentTypeGroup.getId().equals(new Long(1l));
			MainWindow.getInstance().updateButtons();
		}
	}
	
	public String getElementName()
	{
		PaymentTypeGroup paymentTypeGroup = (PaymentTypeGroup) this.getStore().getElement();
		return paymentTypeGroup.name;
	}
	
	private StringFieldEditor nameEditor;
	
	public static final String KEY_ID = "id"; //$NON-NLS-1$
	public static final String KEY_NAME = "name"; //$NON-NLS-1$
}
