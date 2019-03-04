/*
 * Created on 27.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.fixkey;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.gui.widget.FieldEditorPage;
import ch.eugster.pos.admin.gui.widget.StringFieldEditor;
import ch.eugster.pos.db.FixKeyGroup;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class FixKeyGroupFieldEditorPage extends FieldEditorPage
{
	
	/**
	 * @param style
	 * @param store
	 */
	public FixKeyGroupFieldEditorPage(String name, int style, PersistentDBStore store)
	{
		super(name, style, store);
	}
	
	/**
	 * @param title
	 * @param style
	 * @param store
	 */
	public FixKeyGroupFieldEditorPage(String name, String title, int style, PersistentDBStore store)
	{
		super(name, title, style, store);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 * @param store
	 */
	public FixKeyGroupFieldEditorPage(String name, String title, ImageDescriptor image, int style,
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
		this.nameEditor = new StringFieldEditor(
						FixKeyGroupFieldEditorPage.KEY_NAME,
						Messages.getString("FixKeyGroupFieldEditorPage.Bezeichnung_1"), -1, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		this.nameEditor.setEmptyStringAllowed(false);
		this.nameEditor.setErrorMessage(Messages
						.getString("FixKeyGroupFieldEditorPage.Die_Angabe_einer_Bezeichnung_ist_notwendig._2")); //$NON-NLS-1$
		this.nameEditor.showErrorMessage();
		this.addField(this.nameEditor);
	}
	
	public boolean isInstance(Object element)
	{
		return element instanceof FixKeyGroup;
	}
	
	public String getElementName()
	{
		FixKeyGroup keyGroup = (FixKeyGroup) this.getStore().getElement();
		return keyGroup.name;
	}
	
	private StringFieldEditor nameEditor;
	
	public static final String KEY_NAME = "name"; //$NON-NLS-1$
}
