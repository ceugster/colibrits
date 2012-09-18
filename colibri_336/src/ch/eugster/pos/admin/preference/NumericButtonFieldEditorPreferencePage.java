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
public class NumericButtonFieldEditorPreferencePage extends FieldEditorPreferencePage
{
	
	/**
	 * @param style
	 */
	public NumericButtonFieldEditorPreferencePage(int style)
	{
		super(style);
	}
	
	/**
	 * @param title
	 * @param style
	 */
	public NumericButtonFieldEditorPreferencePage(String title, int style)
	{
		super(title, style);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public NumericButtonFieldEditorPreferencePage(String title, ImageDescriptor image, int style)
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
		LabelFieldEditor zeroLabelEditor = new LabelFieldEditor(Messages
						.getString("NumericButtonFieldEditorPreferencePage.Taste___0___1"), this.getFieldEditorParent()); //$NON-NLS-1$
		zeroLabelEditor.setBold(true);
		this.addField(zeroLabelEditor);
		
		FloatComboFieldEditor zeroSizeEditor = new FloatComboFieldEditor(
						"fix-buttons.numeric-block.zero-button.font.size", //$NON-NLS-1$
						Messages.getString("NumericButtonFieldEditorPreferencePage.Schriftgr_u00F6sse_3"), //$NON-NLS-1$
						this.getFieldEditorParent(), new String[]
						{ "8", "10", "12", "14", "16", "18", "20", "22", "24" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
						new float[]
						{ 8f, 10f, 12f, 14f, 16f, 18f, 20f, 22f, 24f });
		this.addField(zeroSizeEditor);
		
		IntegerComboFieldEditor zeroStyleEditor = new IntegerComboFieldEditor(
						"fix-buttons.numeric-block.zero-button.font.style", //$NON-NLS-1$
						Messages.getString("NumericButtonFieldEditorPreferencePage.Schriftstil_14"), //$NON-NLS-1$
						this.getFieldEditorParent(),
						new String[]
						{
										Messages.getString("NumericButtonFieldEditorPreferencePage.Standard_15"), Messages.getString("NumericButtonFieldEditorPreferencePage.Fett_16"), Messages.getString("NumericButtonFieldEditorPreferencePage.Kursiv_17"), Messages.getString("NumericButtonFieldEditorPreferencePage.Fett,_kursiv_18") }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						new int[]
						{ Constants.FONT_STYLE_PLAIN, Constants.FONT_STYLE_BOLD, Constants.FONT_STYLE_ITALIC,
										Constants.FONT_STYLE_BOLD + Constants.FONT_STYLE_ITALIC });
		this.addField(zeroStyleEditor);
		
		ColorFieldEditor zeroFgEditor = new ColorFieldEditor(
						"fix-buttons.numeric-block.zero-button.fgcolor", Messages.getString("NumericButtonFieldEditorPreferencePage.Vordergrundfarbe_20"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.addField(zeroFgEditor);
		
		ColorFieldEditor zeroBgEditor = new ColorFieldEditor(
						"fix-buttons.numeric-block.zero-button.bgcolor", Messages.getString("NumericButtonFieldEditorPreferencePage.Hintergrundfarbe_22"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.addField(zeroBgEditor);
		
		LabelFieldEditor zerozeroLabelEditor = new LabelFieldEditor(
						Messages.getString("NumericButtonFieldEditorPreferencePage.Taste___00___23"), this.getFieldEditorParent()); //$NON-NLS-1$
		zerozeroLabelEditor.setBold(true);
		this.addField(zerozeroLabelEditor);
		
		FloatComboFieldEditor zerozeroSizeEditor = new FloatComboFieldEditor(
						"fix-buttons.numeric-block.zerozero-button.font.size", //$NON-NLS-1$
						Messages.getString("NumericButtonFieldEditorPreferencePage.Schriftgr_u00F6sse_25"), //$NON-NLS-1$
						this.getFieldEditorParent(), new String[]
						{ "8", "10", "12", "14", "16", "18", "20", "22", "24" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
						new float[]
						{ 8f, 10f, 12f, 14f, 16f, 18f, 20f, 22f, 24f });
		this.addField(zerozeroSizeEditor);
		
		IntegerComboFieldEditor zerozeroStyleEditor = new IntegerComboFieldEditor(
						"fix-buttons.numeric-block.zerozero-button.font.style", //$NON-NLS-1$
						Messages.getString("NumericButtonFieldEditorPreferencePage.Schriftstil_36"), //$NON-NLS-1$
						this.getFieldEditorParent(),
						new String[]
						{
										Messages.getString("NumericButtonFieldEditorPreferencePage.Standard_37"), Messages.getString("NumericButtonFieldEditorPreferencePage.Fett_38"), Messages.getString("NumericButtonFieldEditorPreferencePage.Kursiv_39"), Messages.getString("NumericButtonFieldEditorPreferencePage.Fett,_kursiv_40") }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						new int[]
						{ Constants.FONT_STYLE_PLAIN, Constants.FONT_STYLE_BOLD, Constants.FONT_STYLE_ITALIC,
										Constants.FONT_STYLE_BOLD + Constants.FONT_STYLE_ITALIC });
		this.addField(zerozeroStyleEditor);
		
		ColorFieldEditor zerozeroFgEditor = new ColorFieldEditor(
						"fix-buttons.numeric-block.zerozero-button.fgcolor", Messages.getString("NumericButtonFieldEditorPreferencePage.Vordergrundfarbe_42"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.addField(zerozeroFgEditor);
		
		ColorFieldEditor zerozeroBgEditor = new ColorFieldEditor(
						"fix-buttons.numeric-block.zerozero-button.bgcolor", Messages.getString("NumericButtonFieldEditorPreferencePage.Hintergrundfarbe_44"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.addField(zerozeroBgEditor);
	}
	
}
