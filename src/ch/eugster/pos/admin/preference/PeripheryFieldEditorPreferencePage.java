/*
 * Created on 23.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.admin.preference;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.ForeignCurrency;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PeripheryFieldEditorPreferencePage extends FieldEditorPreferencePage
{
	
	/**
	 * @param arg0
	 */
	public PeripheryFieldEditorPreferencePage(int arg0)
	{
		super(arg0);
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public PeripheryFieldEditorPreferencePage(String arg0, int arg1)
	{
		super(arg0, arg1);
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public PeripheryFieldEditorPreferencePage(String arg0, ImageDescriptor arg1, int arg2)
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
		
		BooleanFieldEditor usePrinterEditor = new BooleanFieldEditor(
						"periphery.pos-printer.1.use", Messages.getString("PeripheryFieldEditorPreferencePage.Belegdrucker_aktiv_2"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.addField(usePrinterEditor);
		
		String[] ports = new String[]
		{ "COM1", "COM2", "COM3", "COM4", "COM5", "COM6" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		StringComboFieldEditor printerPortEditor = new StringComboFieldEditor("periphery.pos-printer.1.device.port", //$NON-NLS-1$
						"Port Belegdrucker", //$NON-NLS-1$
						this.getFieldEditorParent(), ports);
		this.addField(printerPortEditor);
		
		SpacerFieldEditor spacerEditor = new SpacerFieldEditor(this.getFieldEditorParent());
		this.addField(spacerEditor);
		
		BooleanFieldEditor casher1Editor = new BooleanFieldEditor(
						"periphery.pos-printer.1.cashdrawer.1.use", Messages.getString("PeripheryFieldEditorPreferencePage.Schublade_1_aktiv_10"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.addField(casher1Editor);
		
		spacerEditor = new SpacerFieldEditor(this.getFieldEditorParent());
		this.addField(spacerEditor);
		
		BooleanFieldEditor casher2Editor = new BooleanFieldEditor(
						"periphery.pos-printer.1.cashdrawer.2.use", Messages.getString("PeripheryFieldEditorPreferencePage.Schublade_2_aktiv_12"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.addField(casher2Editor);
		
		String[] currencies = ForeignCurrency.selectAllReturnCodes(false);
		StringComboFieldEditor currenciesEditor = new StringComboFieldEditor(
						"periphery.pos-printer.1.cashdrawer.2.currency", //$NON-NLS-1$
						"Hauptwährung 2. Schublade", //$NON-NLS-1$
						this.getFieldEditorParent(), currencies);
		this.addField(currenciesEditor);
		
		spacerEditor = new SpacerFieldEditor(this.getFieldEditorParent());
		this.addField(spacerEditor);
		
		BooleanFieldEditor useDisplayEditor = new BooleanFieldEditor(
						"periphery.customer-display.ch.eugster.pos.devices.displays.Cd7220Display.use", Messages.getString("PeripheryFieldEditorPreferencePage.Kundendisplay_aktiv_14"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.addField(useDisplayEditor);
		
		StringComboFieldEditor displayPortEditor = new StringComboFieldEditor(
						"periphery.customer-display.ch.eugster.pos.devices.displays.Cd7220Display.device.port", //$NON-NLS-1$
						"Port Kundendisplay", //$NON-NLS-1$
						this.getFieldEditorParent(), ports);
		this.addField(displayPortEditor);
		
	}
}
