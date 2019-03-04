/*
 * Created on 03.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.tax;

import java.text.SimpleDateFormat;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.gui.widget.DateFieldEditor;
import ch.eugster.pos.admin.gui.widget.DoubleFieldEditor;
import ch.eugster.pos.admin.gui.widget.FieldEditorPage;
import ch.eugster.pos.admin.gui.widget.LongFieldEditor;
import ch.eugster.pos.admin.gui.widget.StringFieldEditor;
import ch.eugster.pos.admin.gui.widget.UniquePrimaryKeyValidator;
import ch.eugster.pos.db.CurrentTax;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class CurrentTaxFieldEditorPage extends FieldEditorPage
{
	
	/**
	 * @param style
	 * @param store
	 */
	public CurrentTaxFieldEditorPage(String name, int style, PersistentDBStore store)
	{
		super(name, style, store);
	}
	
	/**
	 * @param title
	 * @param style
	 * @param store
	 */
	public CurrentTaxFieldEditorPage(String name, String title, int style, PersistentDBStore store)
	{
		super(name, title, style, store);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 * @param store
	 */
	public CurrentTaxFieldEditorPage(String name, String title, ImageDescriptor image, int style,
					PersistentDBStore store)
	{
		super(name, title, image, style, store);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.gui.widget.FieldEditorPage#createFieldEditors()
	 */
	protected void createFieldEditors()
	{
		if (!CurrentTax.isIdFieldAutoincrement(CurrentTax.class))
		{
			LongFieldEditor idEditor = new LongFieldEditor(CurrentTaxFieldEditorPage.KEY_ID, Messages
							.getString("CurrentTaxFieldEditorPage.Id*_1"), this.getFieldEditorParent()); //$NON-NLS-1$
			idEditor.setContentValidator(new UniquePrimaryKeyValidator(idEditor, CurrentTax.class));
			this.addField(idEditor);
		}
		
		this.percentageEditor = new DoubleFieldEditor(CurrentTaxFieldEditorPage.KEY_PERCENTAGE, Messages
						.getString("CurrentTaxFieldEditorPage.Prozentsatz_(in_%)_1"), this.getFieldEditorParent()); //$NON-NLS-1$
		this.percentageEditor.setTextLimit(10);
		this.percentageEditor.setFractionDigits(0, 6);
		this.percentageEditor.setEmptyStringAllowed(false);
		this.percentageEditor.setValidateStrategy(DoubleFieldEditor.VALIDATE_ON_KEY_STROKE);
		this.addField(this.percentageEditor);
		
		this.dateEditor = new DateFieldEditor(
						CurrentTaxFieldEditorPage.KEY_DATE,
						Messages.getString("CurrentTaxFieldEditorPage.G_u00FCltig_ab_2"), 10, DateFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		this.dateEditor.setEmptyStringAllowed(false);
		this.addField(this.dateEditor);
		
		StringFieldEditor fibuEditor = new StringFieldEditor(CurrentTaxFieldEditorPage.KEY_FIBU_ID, Messages
						.getString("CurrentTaxFieldEditorPage.Steuercode_Fibu_1"), 10, this.getFieldEditorParent()); //$NON-NLS-1$
		this.addField(fibuEditor);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.gui.widget.Page#isInstance(java.lang.Object)
	 */
	public boolean isInstance(Object object)
	{
		return object instanceof CurrentTax;
	}
	
	public String getElementName()
	{
		SimpleDateFormat df = new SimpleDateFormat();
		CurrentTax currentTax = (CurrentTax) this.getStore().getElement();
		return currentTax.percentage + " " + df.format(currentTax.getValidationDate()); //$NON-NLS-1$
	}
	
	private DoubleFieldEditor percentageEditor;
	private DateFieldEditor dateEditor;
	
	public static final String KEY_ID = "id"; //$NON-NLS-1$
	public static final String KEY_PERCENTAGE = "percentage"; //$NON-NLS-1$
	public static final String KEY_DATE = "date"; //$NON-NLS-1$
	public static final String KEY_FIBU_ID = "fibuId"; //$NON-NLS-1$
	
}
