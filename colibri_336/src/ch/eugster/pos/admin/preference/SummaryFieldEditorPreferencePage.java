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
public class SummaryFieldEditorPreferencePage extends FieldEditorPreferencePage
{
	
	/**
	 * @param style
	 */
	public SummaryFieldEditorPreferencePage(int style)
	{
		super(style);
	}
	
	/**
	 * @param title
	 * @param style
	 */
	public SummaryFieldEditorPreferencePage(String title, int style)
	{
		super(title, style);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public SummaryFieldEditorPreferencePage(String title, ImageDescriptor image, int style)
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
		LabelFieldEditor labelEditor = new LabelFieldEditor(
						"Hier k�nnen Sie das Erscheinungsbild des Bereichs, der die\nBelegssumme und Zahlungsbetrag anzeigt, festlegen.", this.getFieldEditorParent()); //$NON-NLS-1$
		this.addField(labelEditor);
		
		FloatComboFieldEditor totalSizeEditor = new FloatComboFieldEditor("total-block.font.size", //$NON-NLS-1$
						Messages.getString("PanelFieldEditorPreferencePage.Schriftgr_u00F6sse_47"), //$NON-NLS-1$
						this.getFieldEditorParent(), new String[]
						{ "8", "10", "12", "14", "16", "18", "20", "22", "24" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
						new float[]
						{ 8f, 10f, 12f, 14f, 16f, 18f, 20f, 22f, 24f });
		this.addField(totalSizeEditor);
		
		IntegerComboFieldEditor totalStyleEditor = new IntegerComboFieldEditor(
						"total-block.font.style", //$NON-NLS-1$
						Messages.getString("PanelFieldEditorPreferencePage.Schriftstil_58"), //$NON-NLS-1$
						this.getFieldEditorParent(),
						new String[]
						{
										Messages.getString("PanelFieldEditorPreferencePage.Standard_59"), Messages.getString("PanelFieldEditorPreferencePage.Fett_60"), Messages.getString("PanelFieldEditorPreferencePage.Kursiv_61"), Messages.getString("PanelFieldEditorPreferencePage.Fett,_kursiv_62") }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						new int[]
						{ Constants.FONT_STYLE_PLAIN, Constants.FONT_STYLE_BOLD, Constants.FONT_STYLE_ITALIC,
										Constants.FONT_STYLE_BOLD + Constants.FONT_STYLE_ITALIC });
		this.addField(totalStyleEditor);
		
		ColorFieldEditor totalFgEditor = new ColorFieldEditor(
						"total-block.fgcolor", Messages.getString("PanelFieldEditorPreferencePage.Vordergrundfarbe_64"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.addField(totalFgEditor);
		
		ColorFieldEditor totalBgEditor = new ColorFieldEditor(
						"total-block.bgcolor", Messages.getString("PanelFieldEditorPreferencePage.Hintergrundfarbe_66"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.addField(totalBgEditor);
		
		BooleanFieldEditor showAlwaysEditor = new BooleanFieldEditor("total-block.show-always",
						"Zeilen \"Bezahlt\" und \"R�ckgeld\" immer zeigen", this.getFieldEditorParent());
		this.addField(showAlwaysEditor);
		
		BooleanFieldEditor holdValuesEditor = new BooleanFieldEditor("total-block.hold-values",
						"Zahlungssumme und R�ckgeld, resp. Offenbetr�ge belassen", this.getFieldEditorParent());
		this.addField(holdValuesEditor);
	}
	
}
