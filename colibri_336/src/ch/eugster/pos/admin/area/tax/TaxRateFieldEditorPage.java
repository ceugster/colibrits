/*
 * Created on 05.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.tax;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.gui.widget.FieldEditorPage;
import ch.eugster.pos.admin.gui.widget.LongFieldEditor;
import ch.eugster.pos.admin.gui.widget.StringFieldEditor;
import ch.eugster.pos.admin.gui.widget.UniquePrimaryKeyValidator;
import ch.eugster.pos.db.TaxRate;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class TaxRateFieldEditorPage extends FieldEditorPage
{
	
	/**
	 * @param style
	 */
	public TaxRateFieldEditorPage(String name, int style, PersistentDBStore store)
	{
		super(name, style, store);
	}
	
	/**
	 * @param title
	 * @param style
	 */
	public TaxRateFieldEditorPage(String name, String title, int style, PersistentDBStore store)
	{
		super(name, title, style, store);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public TaxRateFieldEditorPage(String name, String title, ImageDescriptor image, int style, PersistentDBStore store)
	{
		super(name, title, image, style, store);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.FieldEditorPage#createFieldEditors()
	 */
	protected void createFieldEditors()
	{
		if (!TaxRate.isIdFieldAutoincrement(TaxRate.class))
		{
			LongFieldEditor idEditor = new LongFieldEditor(TaxRateFieldEditorPage.KEY_ID, Messages
							.getString("TaxRateFieldEditorPage.Id*_1"), this.getFieldEditorParent()); //$NON-NLS-1$
			idEditor.setContentValidator(new UniquePrimaryKeyValidator(idEditor, TaxRate.class));
			this.addField(idEditor);
		}
		
		StringFieldEditor nameEditor = new StringFieldEditor(
						TaxRateFieldEditorPage.KEY_NAME,
						Messages.getString("TaxRateFieldEditorPage.Bezeichnung_1"), -1, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		nameEditor.setEmptyStringAllowed(false);
		nameEditor.setErrorMessage(Messages
						.getString("TaxRateFieldEditorPage.Die_Angabe_einer_Bezeichnung_ist_notwendig._2")); //$NON-NLS-1$
		nameEditor.showErrorMessage();
		this.addField(nameEditor);
	}
	
	public boolean isInstance(Object element)
	{
		return element instanceof TaxRate;
	}
	
	public String getElementName()
	{
		TaxRate taxRate = (TaxRate) this.getStore().getElement();
		return taxRate.name;
	}
	
	public void initFocus()
	{
		this.performDefaults();
		this.setFocus(0);
	}
	
	public static final String KEY_ID = "id"; //$NON-NLS-1$
	public static final String KEY_NAME = "name"; //$NON-NLS-1$
}
