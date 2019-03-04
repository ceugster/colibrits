/*
 * Created on 17.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.preference;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class PanelFieldEditorPreferencePage extends FieldEditorPreferencePage
{
	
	/**
	 * @param style
	 */
	public PanelFieldEditorPreferencePage(int style)
	{
		super(style);
	}
	
	/**
	 * @param title
	 * @param style
	 */
	public PanelFieldEditorPreferencePage(String title, int style)
	{
		super(title, style);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public PanelFieldEditorPreferencePage(String title, ImageDescriptor image, int style)
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
		ArrayList ns = new ArrayList();
		ArrayList is = new ArrayList();
		LookAndFeel[] auxiliaryLaf = UIManager.getAuxiliaryLookAndFeels();
		UIManager.LookAndFeelInfo[] installedLaf = UIManager.getInstalledLookAndFeels();
		if (auxiliaryLaf != null)
		{
			for (int i = 0; i < auxiliaryLaf.length; i++)
			{
				ns.add(auxiliaryLaf[i].getName());
				is.add(auxiliaryLaf[i].getClass().getName());
			}
		}
		if (installedLaf != null)
		{
			for (int i = 0; i < installedLaf.length; i++)
			{
				ns.add(installedLaf[i].getName());
				is.add(installedLaf[i].getClassName());
			}
		}
		String[] s1 = new String[ns.size()];
		String[] s2 = new String[ns.size()];
		Iterator ins = ns.iterator();
		Iterator iis = is.iterator();
		int index = 0;
		while (ins.hasNext())
		{
			s1[index] = (String) ins.next();
			s2[index] = (String) iis.next();
			index++;
		}
		StringComboFieldEditor lafEditor = new StringComboFieldEditor("look-and-feel", //$NON-NLS-1$
						"Look And Feel", //$NON-NLS-1$
						this.getFieldEditorParent(), s1, s2);
		this.addField(lafEditor);
	}
	
}
