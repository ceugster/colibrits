/*
 * Created on 13.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.admin.preference;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class VoucherFieldEditorPreferencePage extends FieldEditorPreferencePage
{
	
	/**
	 * @param arg0
	 */
	public VoucherFieldEditorPreferencePage(int arg0)
	{
		super(arg0);
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public VoucherFieldEditorPreferencePage(String arg0, int arg1)
	{
		super(arg0, arg1);
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public VoucherFieldEditorPreferencePage(String arg0, ImageDescriptor arg1, int arg2)
	{
		super(arg0, arg1, arg2);
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
		BooleanFieldEditor printLogoEditor = new BooleanFieldEditor("voucher.printlogo", "Logo drucken", this
						.getFieldEditorParent());
		this.addField(printLogoEditor);
		
		String[] list = new String[255];
		int[] values = new int[255];
		for (int i = 0; i < list.length; i++)
		{
			list[i] = new Integer(i + 1).toString();
			values[i] = i + 1;
		}
		IntegerComboFieldEditor chooseLogoEditor = new IntegerComboFieldEditor("voucher.logo", "Auswahl Logo-Nummer",
						this.getFieldEditorParent(), list, values);
		this.addField(chooseLogoEditor);
		
		String[] modes = new String[]
		{ "Normal", "Doppelte Breite", "Doppelte Höhe", "Doppelte Breite, doppelte Höhe" };
		int[] modeValues = new int[]
		{ 0, 1, 2, 3 };
		IntegerComboFieldEditor logoModeEditor = new IntegerComboFieldEditor("voucher.logomode", "Zeigemodus des Logo",
						this.getFieldEditorParent(), modes, modeValues);
		this.addField(logoModeEditor);
	}
}
