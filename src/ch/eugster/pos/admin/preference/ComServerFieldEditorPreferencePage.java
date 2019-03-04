/*
 * Created on 17.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.preference;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.gui.widget.FieldEditor;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ComServerFieldEditorPreferencePage extends FieldEditorPreferencePage
{
	
	/**
	 * @param style
	 */
	public ComServerFieldEditorPreferencePage(int style)
	{
		super(style);
	}
	
	/**
	 * @param title
	 * @param style
	 */
	public ComServerFieldEditorPreferencePage(String title, int style)
	{
		super(title, style);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public ComServerFieldEditorPreferencePage(String title, ImageDescriptor image, int style)
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
		LabelFieldEditor messageEditor = new LabelFieldEditor(
						"Bitte beachten Sie: Die Einstellungen zum ComServer gelten für alle Kassen,\ndie an derselben Hauptdatenbank angeschlossen sind, sobald sie nach einer\nÄnderung neu gestartet wurden.",
						this.getFieldEditorParent());
		this.addField(messageEditor);
		
		LabelFieldEditor spaceEditor = new LabelFieldEditor("", this.getFieldEditorParent());
		this.addField(spaceEditor);
		
		this.activeEditor = new BooleanFieldEditor(
						"com-server.use", Messages.getString("ComserverFieldEditorPreferencePage.COM-Server_aktiv"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.activeEditor.setPropertyChangeListener(this);
		this.addField(this.activeEditor);
		
		String[] comboList = new String[]
		{ "Galileo", "OctoServer" };
		String[] values = new String[]
		{ "ch.eugster.pos.product.GalileoServer", "ch.eugster.pos.product.OctoServer" };
		this.chooseComServerEditor = new StringComboFieldEditor("com-server.class", "Auswahl COM-Server", this
						.getFieldEditorParent(), comboList, values);
		this.addField(this.chooseComServerEditor);
		
		BooleanFieldEditor holdEditor = new BooleanFieldEditor(
						"com-server.hold", "Verbindung offen halten", this.getFieldEditorParent()); //$NON-NLS-1$
		this.addField(holdEditor);
	}
	
	public void propertyChange(PropertyChangeEvent e)
	{
		if (e.getSource() instanceof FieldEditor)
		{
			if (e.getSource().equals(this.chooseComServerEditor))
			{
				if (this.activeEditor.getBooleanValue())
				{
					if (this.chooseComServerEditor.getCombo().getText().equals("Galileo"))
					{
						String path = this.getPreferenceStore().getString("galileo.path");
						if (path == null || !new File(path).exists() || !new File(path).isFile())
						{
							org.eclipse.jface.dialogs.MessageDialog
											.openInformation(this.getShell(), "Galileo-Pfad setzen",
															"Der Pfad zur Galileo-Datenbank ist nicht definiert. Bitte aktualisieren Sie die Daten des Galileo-Eintrags.");
						}
					}
					else if (this.chooseComServerEditor.getCombo().getText().equals("OctoServer"))
					{
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
	
	private BooleanFieldEditor activeEditor;
	private StringComboFieldEditor chooseComServerEditor;
	
	private ArrayList listeners = new ArrayList();
}
