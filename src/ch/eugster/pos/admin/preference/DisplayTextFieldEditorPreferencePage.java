/*
 * Created on 13.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.admin.preference;

import org.eclipse.jface.preference.IntegerFieldEditor;
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
public class DisplayTextFieldEditorPreferencePage extends FieldEditorPreferencePage
{
	
	/**
	 * @param arg0
	 */
	public DisplayTextFieldEditorPreferencePage(int arg0)
	{
		super(arg0);
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public DisplayTextFieldEditorPreferencePage(String arg0, int arg1)
	{
		super(arg0, arg1);
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public DisplayTextFieldEditorPreferencePage(String arg0, ImageDescriptor arg1, int arg2)
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
		ListFieldEditor welcomeListEditor = new ListFieldEditor(
						"customer-display.welcome-text", Messages.getString("DisplayTextFieldEditorPreferencePage.Willkommenstext_2"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.setButtonText(welcomeListEditor);
		this.addField(welcomeListEditor);
		
		BooleanFieldEditor scrollWelcomeEditor = new BooleanFieldEditor(
						"customer-display.welcome-text.scroll", Messages.getString("DisplayTextFieldEditorPreferencePage.als_Lauftext_4"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.addField(scrollWelcomeEditor);
		
		BooleanFieldEditor timerEditor = new BooleanFieldEditor(
						"customer-display.timer", "Mit Anzeigeverzögerung nach Belegabschluss", this.getFieldEditorParent()); //$NON-NLS-1$
		this.addField(timerEditor);
		
		IntegerFieldEditor secondsEditor = new IntegerFieldEditor("customer-display.seconds",
						"Verzögerung in Sekunden", this.getFieldEditorParent());
		secondsEditor.setEmptyStringAllowed(false);
		this.addField(secondsEditor);
		
		SpacerFieldEditor sfe1 = new SpacerFieldEditor(this.getFieldEditorParent());
		this.addField(sfe1);
		
		ListFieldEditor closedListEditor = new ListFieldEditor(
						"customer-display.closed-text", Messages.getString("DisplayTextFieldEditorPreferencePage.Text_bei_nicht_bedienter_Kasse_6"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.setButtonText(closedListEditor);
		this.addField(closedListEditor);
		
		BooleanFieldEditor scrollClosedEditor = new BooleanFieldEditor(
						"customer-display.closed-text.scroll", Messages.getString("DisplayTextFieldEditorPreferencePage.als_Lauftext_8"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.addField(scrollClosedEditor);
		
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
					b.setText(Messages.getString("DisplayTextFieldEditorPreferencePage.Neu..._10")); //$NON-NLS-1$
				}
				else if (b.getText().equals("&Remove")) { //$NON-NLS-1$
					b.setText(Messages.getString("DisplayTextFieldEditorPreferencePage.Entfernen_12")); //$NON-NLS-1$
				}
				else if (b.getText().equals("&Up")) { //$NON-NLS-1$
					b.setText(Messages.getString("DisplayTextFieldEditorPreferencePage.Auf_14")); //$NON-NLS-1$
				}
				else if (b.getText().equals("Dow&n")) { //$NON-NLS-1$
					b.setText(Messages.getString("DisplayTextFieldEditorPreferencePage.Ab_16")); //$NON-NLS-1$
				}
			}
		}
	}
	
}
