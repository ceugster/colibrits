/*
 * Created on 09.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.key;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.gui.widget.ComboFieldEditor;
import ch.eugster.pos.admin.gui.widget.FieldEditorPage;
import ch.eugster.pos.admin.gui.widget.StringFieldEditor;
import ch.eugster.pos.db.Block;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class BlockFieldEditorPage extends FieldEditorPage
{
	
	/**
	 * @param name
	 * @param style
	 * @param store
	 */
	public BlockFieldEditorPage(String name, int style, PersistentDBStore store)
	{
		super(name, style, store);
	}
	
	/**
	 * @param name
	 * @param title
	 * @param style
	 * @param store
	 */
	public BlockFieldEditorPage(String name, String title, int style, PersistentDBStore store)
	{
		super(name, title, style, store);
	}
	
	/**
	 * @param name
	 * @param title
	 * @param image
	 * @param style
	 * @param store
	 */
	public BlockFieldEditorPage(String name, String title, ImageDescriptor image, int style, PersistentDBStore store)
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
		this.nameEditor = new StringFieldEditor(
						BlockFieldEditorPage.KEY_NAME,
						Messages.getString("BlockFieldEditorPage.Bezeichnung_1"), -1, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		this.nameEditor.setEmptyStringAllowed(false);
		this.nameEditor.setCapitalizationOn(false);
		this.nameEditor.setErrorMessage(Messages
						.getString("BlockFieldEditorPage.Die_Angabe_der_Bezeichnung_ist_notwendig._2")); //$NON-NLS-1$
		this.nameEditor.showErrorMessage();
		this.addField(this.nameEditor);
		
		this.fontSizeEditor = new ComboFieldEditor(
						BlockFieldEditorPage.KEY_FONT_SIZE,
						Messages.getString("BlockFieldEditorPage.Schriftgr_u00F6sse_der_Registertitel_3"), this.getFieldEditorParent()); //$NON-NLS-1$
		this.fontSizeEditor.setErrorMessage(Messages
						.getString("BlockFieldEditorPage.Die_Angabe_der_Fontgr_u00F6sse_ist_notwendig._4")); //$NON-NLS-1$
		this.fontSizeEditor.setContentProvider(new TabFontSizeComboFieldEditorContentProvider(this.fontSizeEditor));
		this.fontSizeEditor.setLabelProvider(new TabFontSizeComboFieldEditorLabelProvider());
		this.fontSizeEditor.setInput(new Object());
		this.addField(this.fontSizeEditor);
		
		this.fontStyleEditor = new ComboFieldEditor(BlockFieldEditorPage.KEY_FONT_STYLE, Messages
						.getString("BlockFieldEditorPage.Schriftstil_der_Registertitel_5"), this.getFieldEditorParent()); //$NON-NLS-1$
		this.fontStyleEditor.setErrorMessage(Messages
						.getString("BlockFieldEditorPage.Die_Angabe_des_Fontstils_ist_notwendig._6")); //$NON-NLS-1$
		this.fontStyleEditor.setContentProvider(new TabFontStyleComboFieldEditorContentProvider(this.fontStyleEditor));
		this.fontStyleEditor.setLabelProvider(new TabFontStyleComboFieldEditorLabelProvider());
		this.fontStyleEditor.setInput(new Object());
		this.addField(this.fontStyleEditor);
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.gui.widget.Page#isInstance(java.lang.Object)
	 */
	public boolean isInstance(Object object)
	{
		return object instanceof Block;
	}
	
	public String getElementName()
	{
		Block block = (Block) this.getStore().getElement();
		return block.name;
	}
	
	private StringFieldEditor nameEditor;
	private ComboFieldEditor fontSizeEditor;
	private ComboFieldEditor fontStyleEditor;
	public static final String KEY_NAME = "name"; //$NON-NLS-1$
	public static final String KEY_FONT_SIZE = "size"; //$NON-NLS-1$
	public static final String KEY_FONT_STYLE = "style"; //$NON-NLS-1$
}
