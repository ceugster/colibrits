/*
 * Created on 17.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.preference;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.pos.Messages;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class DatabaseFieldEditorPreferencePage extends FieldEditorPreferencePage
{
	
	/**
	 * @param style
	 */
	public DatabaseFieldEditorPreferencePage(int style)
	{
		super(style);
	}
	
	/**
	 * @param title
	 * @param style
	 */
	public DatabaseFieldEditorPreferencePage(String title, int style)
	{
		super(title, style);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public DatabaseFieldEditorPreferencePage(String title, ImageDescriptor image, int style)
	{
		super(title, image, style);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors
	 * ()
	 */
	protected void createFieldEditors()
	{
		StringComboFieldEditor defaultEditor = new StringComboFieldEditor(
						"database.default", //$NON-NLS-1$
						Messages.getString("DatabaseFieldEditorPreferencePage.Standardverbindung_2"), //$NON-NLS-1$
						this.getFieldEditorParent(),
						new String[]
						{
										Messages.getString("DatabaseFieldEditorPreferencePage.Standard_3"), Messages.getString("DatabaseFieldEditorPreferencePage.Tempor_u00E4r_4"), Messages.getString("DatabaseFieldEditorPreferencePage.Schulung_5") }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						new String[]
						{ "standard", "temporary", "tutorial" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.addField(defaultEditor);
	}
	
}
