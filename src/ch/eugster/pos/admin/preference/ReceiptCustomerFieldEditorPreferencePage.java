/*
 * Created on 13.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.admin.preference;

import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
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
public class ReceiptCustomerFieldEditorPreferencePage extends FieldEditorPreferencePage
{
	
	/**
	 * @param arg0
	 */
	public ReceiptCustomerFieldEditorPreferencePage(int arg0)
	{
		super(arg0);
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public ReceiptCustomerFieldEditorPreferencePage(String arg0, int arg1)
	{
		super(arg0, arg1);
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public ReceiptCustomerFieldEditorPreferencePage(String arg0, ImageDescriptor arg1, int arg2)
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
		ListFieldEditor footerListEditor = new ListFieldEditor(
						"receipt.customer.text", "Text", this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.setButtonText(footerListEditor);
		this.addField(footerListEditor);
		
		LabelFieldEditor labelEditor = new LabelFieldEditor(
						"Legen Sie die Stelle der Kundennummer im Text fest, indem Sie sie mit <Nummer> markieren.",
						this.getFieldEditorParent());
		this.addField(labelEditor);
		
		labelEditor = new LabelFieldEditor(
						"Legen Sie die Stelle des Kontostandes im Text fest, indem Sie sie mit <Konto> markieren.",
						this.getFieldEditorParent());
		this.addField(labelEditor);
		
		RadioGroupFieldEditor alignBottomEditor = new RadioGroupFieldEditor(
						"receipt.customer.row.col.align", "Ausrichtung", 1, new String[][] { { "Links", "left" }, { "Zentriert", "center" }, { "Rechts", "right" } }, this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.addField(alignBottomEditor);
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
