/*
 * Created on 05.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.option;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.gui.widget.FieldEditorPage;
import ch.eugster.pos.admin.gui.widget.LabelFieldEditor;
import ch.eugster.pos.admin.gui.widget.LongFieldEditor;
import ch.eugster.pos.admin.gui.widget.StringFieldEditor;
import ch.eugster.pos.admin.gui.widget.UniquePrimaryKeyValidator;
import ch.eugster.pos.db.Option;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class OptionFieldEditorPage extends FieldEditorPage
{
	
	/**
	 * @param style
	 */
	public OptionFieldEditorPage(String name, int style, PersistentDBStore store)
	{
		super(name, style, store);
	}
	
	/**
	 * @param title
	 * @param style
	 */
	public OptionFieldEditorPage(String name, String title, int style, PersistentDBStore store)
	{
		super(name, title, style, store);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public OptionFieldEditorPage(String name, String title, ImageDescriptor image, int style, PersistentDBStore store)
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
		if (!Option.isIdFieldAutoincrement(Option.class))
		{
			LongFieldEditor idEditor = new LongFieldEditor(OptionFieldEditorPage.KEY_ID, Messages
							.getString("OptionFieldEditorPage.Id*_1"), this.getFieldEditorParent()); //$NON-NLS-1$
			idEditor.setContentValidator(new UniquePrimaryKeyValidator(idEditor, Option.class));
			this.addField(idEditor);
		}
		
		this.codeEditor = new StringFieldEditor(
						OptionFieldEditorPage.KEY_CODE,
						Messages.getString("OptionFieldEditorPage.Code_1"), 3, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		this.codeEditor.setEmptyStringAllowed(false);
		this.codeEditor.setCapitalizationOn(true);
		this.codeEditor.setErrorMessage(Messages
						.getString("OptionFieldEditorPage.Die_Angabe_des_Codes_ist_notwendig._2")); //$NON-NLS-1$
		this.codeEditor.showErrorMessage();
		this.addField(this.codeEditor);
		
		this.nameEditor = new StringFieldEditor(
						OptionFieldEditorPage.KEY_NAME,
						Messages.getString("OptionFieldEditorPage.Bezeichnung_3"), -1, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		this.nameEditor.setEmptyStringAllowed(true);
		this.addField(this.nameEditor);
		
		LabelFieldEditor commentEditor = new LabelFieldEditor(Messages
						.getString("OptionFieldEditorPage.*Eindeutiger_Wert_zwingend_2"), this.getFieldEditorParent()); //$NON-NLS-1$
		this.addField(commentEditor);
	}
	
	public boolean isInstance(Object element)
	{
		return element instanceof Option;
	}
	
	public String getElementName()
	{
		Option option = (Option) this.getStore().getElement();
		return option.name;
	}
	
	private StringFieldEditor codeEditor;
	private StringFieldEditor nameEditor;
	
	public static final String KEY_ID = "id"; //$NON-NLS-1$
	public static final String KEY_CODE = "code"; //$NON-NLS-1$
	public static final String KEY_NAME = "name"; //$NON-NLS-1$
	
}
