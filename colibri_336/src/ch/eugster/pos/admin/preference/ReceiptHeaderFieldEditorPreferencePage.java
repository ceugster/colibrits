/*
 * Created on 13.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.admin.preference;

import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import ch.eugster.pos.Messages;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ReceiptHeaderFieldEditorPreferencePage extends FieldEditorPreferencePage
{
	
	/**
	 * @param arg0
	 */
	public ReceiptHeaderFieldEditorPreferencePage(int arg0)
	{
		super(arg0);
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public ReceiptHeaderFieldEditorPreferencePage(String arg0, int arg1)
	{
		super(arg0, arg1);
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public ReceiptHeaderFieldEditorPreferencePage(String arg0, ImageDescriptor arg1, int arg2)
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
		BooleanFieldEditor printLogoEditor = new BooleanFieldEditor("receipt.header.printlogo", "Logo drucken", this
						.getFieldEditorParent());
		this.addField(printLogoEditor);
		
		Composite composite = new Composite(this.getFieldEditorParent(), SWT.NO_FOCUS);
		
		String[] list = new String[255];
		int[] values = new int[255];
		for (int i = 0; i < list.length; i++)
		{
			list[i] = new Integer(i + 1).toString();
			values[i] = i + 1;
		}
		IntegerComboFieldEditor logoEditor = new IntegerComboFieldEditor("receipt.header.logo", "Auswahl Logo-Nummer",
						composite, list, values);
		this.addField(logoEditor);
		
		String[] modes = new String[]
		{ "Normal", "Doppelte Breite", "Doppelte Höhe", "Doppelte Breite, doppelte Höhe" };
		int[] modeValues = new int[]
		{ 0, 1, 2, 3 };
		IntegerComboFieldEditor logoModeEditor = new IntegerComboFieldEditor("receipt.header.logomode",
						"Zeigemodus des Logo", composite, modes, modeValues);
		this.addField(logoModeEditor);
		
		SpacerFieldEditor space = new SpacerFieldEditor(this.getFieldEditorParent());
		this.addField(space);
		
		ListFieldEditor headerListEditor = new ListFieldEditor(
						"receipt.header.text", Messages.getString("ReceiptTextFieldEditorPreferencePage.Benutzerdefinierte_Kopfzeilen_2"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.setButtonText(headerListEditor);
		this.addField(headerListEditor);
		
		RadioGroupFieldEditor alignTopEditor = new RadioGroupFieldEditor(
						"receipt.header.row.col.align", "Ausrichtung", 3, new String[][] { { "Links", "left" }, { "Zentriert", "center" }, { "Rechts", "right" } }, this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.addField(alignTopEditor);
		
		IntegerFieldEditor receiptNumberLengthEditor = new IntegerFieldEditor(
						"receipt.header.number.length", "Angezeigte Anzahl Ziffern der Belegnummer", this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.addField(receiptNumberLengthEditor);
		
	}
	
	private void setButtonText(ListFieldEditor editor)
	{
		Composite cp = editor.getButtonBoxControl(this.getFieldEditorParent());
		Control[] ctrl = cp.getChildren();
		for (int i = 0; i < ctrl.length; i++)
		{
			if (ctrl[i] instanceof Button)
			{
				Button b = (Button) ctrl[i];
				if (b.getText().equals("Ne&w...")) { //$NON-NLS-1$
					b.setText(Messages.getString("ReceiptTextFieldEditorPreferencePage.Neu..._6")); //$NON-NLS-1$
				}
				else if (b.getText().equals("&Remove")) { //$NON-NLS-1$
					b.setText(Messages.getString("ReceiptTextFieldEditorPreferencePage.Entfernen_8")); //$NON-NLS-1$
				}
				else if (b.getText().equals("&Up")) { //$NON-NLS-1$
					b.setText(Messages.getString("ReceiptTextFieldEditorPreferencePage.Auf_10")); //$NON-NLS-1$
				}
				else if (b.getText().equals("Dow&n")) { //$NON-NLS-1$
					b.setText(Messages.getString("ReceiptTextFieldEditorPreferencePage.Ab_12")); //$NON-NLS-1$
				}
			}
		}
	}
	
}
