/*
 * Created on 17.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.preference;

import org.eclipse.swt.widgets.Composite;

import ch.eugster.pos.Messages;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class FileFieldEditor extends org.eclipse.jface.preference.FileFieldEditor
{
	
	/**
	 * 
	 */
	public FileFieldEditor()
	{
		super();
		this.init();
	}
	
	/**
	 * @param name
	 * @param labelText
	 * @param parent
	 */
	public FileFieldEditor(String name, String labelText, Composite parent)
	{
		super(name, labelText, parent);
		this.init();
	}
	
	/**
	 * @param name
	 * @param labelText
	 * @param enforceAbsolute
	 * @param parent
	 */
	public FileFieldEditor(String name, String labelText, boolean enforceAbsolute, Composite parent)
	{
		super(name, labelText, enforceAbsolute, parent);
		this.init();
	}
	
	private void init()
	{
		this.setErrorMessage(Messages
						.getString("FileFieldEditor.Der_Pfad_muss_auf_eine_g_u00FCltige_Datei_verweisen._1")); //$NON-NLS-1$
	}
}
