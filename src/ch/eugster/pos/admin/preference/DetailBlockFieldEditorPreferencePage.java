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
public class DetailBlockFieldEditorPreferencePage extends FieldEditorPreferencePage
{
	
	/**
	 * @param style
	 */
	public DetailBlockFieldEditorPreferencePage(int style)
	{
		super(style);
	}
	
	/**
	 * @param title
	 * @param style
	 */
	public DetailBlockFieldEditorPreferencePage(String title, int style)
	{
		super(title, style);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public DetailBlockFieldEditorPreferencePage(String title, ImageDescriptor image, int style)
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
		LabelFieldEditor infoEditor = new LabelFieldEditor("Hier können Sie die Schrift des Listenbereichs festlegen.",
						this.getFieldEditorParent());
		this.addField(infoEditor);
		
		FloatComboFieldEditor listFontSizeEditor = new FloatComboFieldEditor("detail-block-list.font.size", //$NON-NLS-1$
						Messages.getString("PanelFieldEditorPreferencePage.Schriftgr_u00F6sse_27"), //$NON-NLS-1$
						this.getFieldEditorParent(), new String[]
						{ "8", "10", "12", "14", "16", "18", "20", "22", "24" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
						new float[]
						{ 8f, 10f, 12f, 14f, 16f, 18f, 20f, 22f, 24f });
		this.addField(listFontSizeEditor);
		
		IntegerComboFieldEditor listFontStyleEditor = new IntegerComboFieldEditor(
						"detail-block-list.font.style", //$NON-NLS-1$
						Messages.getString("PanelFieldEditorPreferencePage.Schriftstil_38"), //$NON-NLS-1$
						this.getFieldEditorParent(),
						new String[]
						{
										Messages.getString("PanelFieldEditorPreferencePage.Standard_39"), Messages.getString("PanelFieldEditorPreferencePage.Fett_40"), Messages.getString("PanelFieldEditorPreferencePage.Kursiv_41"), Messages.getString("PanelFieldEditorPreferencePage.Fett,_kursiv_42") }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						new int[]
						{ Constants.FONT_STYLE_PLAIN, Constants.FONT_STYLE_BOLD, Constants.FONT_STYLE_ITALIC,
										Constants.FONT_STYLE_BOLD + Constants.FONT_STYLE_ITALIC });
		this.addField(listFontStyleEditor);
		
		ColorFieldEditor listFgEditor = new ColorFieldEditor(
						"detail-block-list.normal-color.fgcolor", "Schriftfarbe normal", this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.addField(listFgEditor);
		
		ColorFieldEditor listBackFgEditor = new ColorFieldEditor(
						"detail-block-list.back-color.fgcolor", "Schriftfarbe Rücknahme", this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.addField(listBackFgEditor);
		
		ColorFieldEditor listExpenseFgEditor = new ColorFieldEditor(
						"detail-block-list.expense-color.fgcolor", "Schriftfarbe Ausgaben", this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.addField(listExpenseFgEditor);
		
		SpacerFieldEditor space = new SpacerFieldEditor(this.getFieldEditorParent());
		this.addField(space);
		
		LabelFieldEditor labelEditor = new LabelFieldEditor(
						"Hier können Sie das Erscheinungsbild des Bereichs, der die Detailinformationen\nzu einer ausgewählten Position anzeigt, festlegen.",
						this.getFieldEditorParent());
		this.addField(labelEditor);
		
		FloatComboFieldEditor detailSizeEditor = new FloatComboFieldEditor("detail-block.font.size", //$NON-NLS-1$
						Messages.getString("PanelFieldEditorPreferencePage.Schriftgr_u00F6sse_27"), //$NON-NLS-1$
						this.getFieldEditorParent(), new String[]
						{ "8", "10", "12", "14", "16", "18", "20", "22", "24" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
						new float[]
						{ 8f, 10f, 12f, 14f, 16f, 18f, 20f, 22f, 24f });
		this.addField(detailSizeEditor);
		
		IntegerComboFieldEditor detailStyleEditor = new IntegerComboFieldEditor(
						"detail-block.font.style", //$NON-NLS-1$
						Messages.getString("PanelFieldEditorPreferencePage.Schriftstil_38"), //$NON-NLS-1$
						this.getFieldEditorParent(),
						new String[]
						{
										Messages.getString("PanelFieldEditorPreferencePage.Standard_39"), Messages.getString("PanelFieldEditorPreferencePage.Fett_40"), Messages.getString("PanelFieldEditorPreferencePage.Kursiv_41"), Messages.getString("PanelFieldEditorPreferencePage.Fett,_kursiv_42") }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						new int[]
						{ Constants.FONT_STYLE_PLAIN, Constants.FONT_STYLE_BOLD, Constants.FONT_STYLE_ITALIC,
										Constants.FONT_STYLE_BOLD + Constants.FONT_STYLE_ITALIC });
		this.addField(detailStyleEditor);
		
		ColorFieldEditor detailFgEditor = new ColorFieldEditor(
						"detail-block.fgcolor", Messages.getString("PanelFieldEditorPreferencePage.Vordergrundfarbe_44"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.addField(detailFgEditor);
		
		ColorFieldEditor detailBgEditor = new ColorFieldEditor("detail-block.bgcolor",
						"Hintergrundfarbe Rückname/Ausgaben", this.getFieldEditorParent());
		this.addField(detailBgEditor);
	}
	
}
