/*
 * Created on 14.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.admin.preference;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;

import ch.eugster.pos.Messages;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FieldEditorPreferencePage extends org.eclipse.jface.preference.FieldEditorPreferencePage
{
	
	/**
	 * @param arg0
	 */
	public FieldEditorPreferencePage(int arg0)
	{
		super(arg0);
		this.init();
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public FieldEditorPreferencePage(String arg0, int arg1)
	{
		super(arg0, arg1);
		this.init();
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public FieldEditorPreferencePage(String arg0, ImageDescriptor arg1, int arg2)
	{
		super(arg0, arg1, arg2);
		this.init();
	}
	
	private void init()
	{
		
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
	}
	
	public void createControl(Composite control)
	{
		super.createControl(control);
		this.getApplyButton().setText(Messages.getString("FieldEditorPreferencePage.Anwenden_1")); //$NON-NLS-1$
		this.getDefaultsButton().setText(Messages.getString("FieldEditorPreferencePage.Voreinstellung_2")); //$NON-NLS-1$
	}
	
}
