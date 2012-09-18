/*
 * Created on 17.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.preference;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.pos.Constants;
import ch.eugster.pos.Messages;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class DisplayFieldEditorPreferencePage extends FieldEditorPreferencePage
{
	
	/**
	 * @param style
	 */
	public DisplayFieldEditorPreferencePage(int style)
	{
		super(style);
	}
	
	/**
	 * @param title
	 * @param style
	 */
	public DisplayFieldEditorPreferencePage(String title, int style)
	{
		super(title, style);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public DisplayFieldEditorPreferencePage(String title, ImageDescriptor image, int style)
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
		LabelFieldEditor tabPanelEditor = new LabelFieldEditor(Messages
						.getString("PanelFieldEditorPreferencePage.Register_3"), this.getFieldEditorParent()); //$NON-NLS-1$
		tabPanelEditor.setBold(true);
		this.addField(tabPanelEditor);
		
		FloatComboFieldEditor tabSizeEditor = new FloatComboFieldEditor("display.font.size", //$NON-NLS-1$
						Messages.getString("PanelFieldEditorPreferencePage.Schriftgr_u00F6sse_5"), //$NON-NLS-1$
						this.getFieldEditorParent(), new String[]
						{ "8", "10", "12", "14", "16", "18", "20", "22", "24" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
						new float[]
						{ 8f, 10f, 12f, 14f, 16f, 18f, 20f, 22f, 24f });
		this.addField(tabSizeEditor);
		
		IntegerComboFieldEditor tabStyleEditor = new IntegerComboFieldEditor(
						"display.font.style", //$NON-NLS-1$
						Messages.getString("PanelFieldEditorPreferencePage.Schriftstil_16"), //$NON-NLS-1$
						this.getFieldEditorParent(),
						new String[]
						{
										Messages.getString("PanelFieldEditorPreferencePage.Standard_17"), Messages.getString("PanelFieldEditorPreferencePage.Fett_18"), Messages.getString("PanelFieldEditorPreferencePage.Kursiv_19"), Messages.getString("PanelFieldEditorPreferencePage.Fett,_kursiv_20") }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						new int[]
						{ Constants.FONT_STYLE_PLAIN, Constants.FONT_STYLE_BOLD, Constants.FONT_STYLE_ITALIC,
										Constants.FONT_STYLE_BOLD + Constants.FONT_STYLE_ITALIC });
		this.addField(tabStyleEditor);
		
		SpacerFieldEditor space = new SpacerFieldEditor(this.getFieldEditorParent());
		this.addField(space);
		
		LabelFieldEditor labelEditor = new LabelFieldEditor(
						"Hier können Sie das Erscheinungsbild der Infozeile, die sich unmittelbar\nüber dem numerischen Block befindet, festlegen.", this.getFieldEditorParent()); //$NON-NLS-1$
		this.addField(labelEditor);
		
		ColorFieldEditor tabFgEditor = new ColorFieldEditor(
						"display.fgcolor", Messages.getString("PanelFieldEditorPreferencePage.Vordergrundfarbe_22"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.addField(tabFgEditor);
		
		ColorFieldEditor tabBgEditor = new ColorFieldEditor(
						"display.bgcolor", Messages.getString("PanelFieldEditorPreferencePage.Hintergrundfarbe_24"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.addField(tabBgEditor);
	}
	
}
