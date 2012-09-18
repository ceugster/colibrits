/*
 * Created on 17.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.preference;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import ch.eugster.pos.Messages;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ComServerGalileoFieldEditorPreferencePage extends FieldEditorPreferencePage
{
	
	/**
	 * @param style
	 */
	public ComServerGalileoFieldEditorPreferencePage(int style)
	{
		super(style);
	}
	
	/**
	 * @param title
	 * @param style
	 */
	public ComServerGalileoFieldEditorPreferencePage(String title, int style)
	{
		super(title, style);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public ComServerGalileoFieldEditorPreferencePage(String title, ImageDescriptor image, int style)
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
		this.updateEditor = new RadioGroupFieldEditor(
						"galileo.update", "Galileo aktualisieren", 1, new String[][] { { "keine Aktualisierung", "0" }, { "Scannerpanel aktualisieren", "1" }, { "mit Warenbewirtschaftung", "2" } }, this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.addField(this.updateEditor);
		
		SpacerFieldEditor space1 = new SpacerFieldEditor(this.getFieldEditorParent());
		this.addField(space1);
		
		this.pathEditor = new ComServerFileFieldEditor(
						"galileo.path", Messages.getString("ComserverFieldEditorPreferencePage.Pfad_zu_Galileo_2"), true, this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.pathEditor.setEmptyStringAllowed(true);
		this.pathEditor.setChangeButtonText(Messages.getString("ComserverFieldEditorPreferencePage.Suchen..._3")); //$NON-NLS-1$
		this.pathEditor.setFileExtensions(new String[]
		{ Messages.getString("ComserverFieldEditorPreferencePage.*.dbc_4") }); //$NON-NLS-1$
		this.addField(this.pathEditor);
		
		SpacerFieldEditor space2 = new SpacerFieldEditor(this.getFieldEditorParent());
		this.addField(space2);
		
		this.customerEditor = new BooleanFieldEditor(
						"galileo.show-add-customer-message", "Meldung \"Kunden erfassen\" bei Rücknahmen anzeigen", this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.customerEditor.setPropertyChangeListener(this);
		this.addField(this.customerEditor);
		
		SpacerFieldEditor space3 = new SpacerFieldEditor(this.getFieldEditorParent());
		this.addField(space3);
		
		this.useCdEditor = new BooleanFieldEditor(
						"galileo.search-cd", "Titelsuche auf CD aktivieren", this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.addField(this.useCdEditor);
		
		this.cdPathEditor = new ComServerFileFieldEditor(
						"galileo.cd-path", "Pfad zu \"BIBWIN.INI\"", true, this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.cdPathEditor.setEmptyStringAllowed(true);
		this.cdPathEditor.setChangeButtonText("Suchen..."); //$NON-NLS-1$
		this.cdPathEditor.setFileExtensions(new String[]
		{ "bibWIN.ini", "*.ini" }); //$NON-NLS-1$ //$NON-NLS-2$
		this.addField(this.cdPathEditor);
	}
	
	public void propertyChange(PropertyChangeEvent e)
	{
		if (e.getSource() instanceof FieldEditor)
		{
			if (e.getSource().equals(this.useCdEditor))
			{
				this.cdPathEditor.propertyChange(e);
			}
			else if (e.getSource().equals(this.updateEditor))
			{
				if (e.getNewValue() == "2" && e.getOldValue() != "2")
				{
					if (!this.updateEditor.getPreferenceStore().getBoolean("database.temporary.active"))
					{
						MessageDialog
										.openInformation(
														this.getFieldEditorParent().getShell(),
														"Datenbankeinstellung",
														"Es empfiehlt sich, bei der Verwendung von Galileo mit Warenbewirtschaftung, eine lokale Ersatzbank einzurichten. Sie können das unter \"Einstellungen\", \"Datenbankverbindung\", \"Temporäre Datenbank\" nachholen.");
					}
				}
			}
		}
	}
	
	public boolean addPropertyChangeListener(IPropertyChangeListener listener)
	{
		return this.listeners.add(listener);
	}
	
	public boolean removePropertyChangeListener(IPropertyChangeListener listener)
	{
		return this.listeners.remove(listener);
	}
	
	public void fireEvent(PropertyChangeEvent event)
	{
		Iterator i = this.listeners.iterator();
		while (i.hasNext())
		{
			((IPropertyChangeListener) i.next()).propertyChange(event);
		}
	}
	
	private ComServerFileFieldEditor pathEditor;
	private ComServerFileFieldEditor cdPathEditor;
	private BooleanFieldEditor customerEditor;
	private RadioGroupFieldEditor updateEditor;
	private BooleanFieldEditor useCdEditor;
	
	private ArrayList listeners = new ArrayList();
}
