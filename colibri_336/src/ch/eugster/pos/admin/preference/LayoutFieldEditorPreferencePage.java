/*
 * Created on 17.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.preference;

import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.pos.Messages;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class LayoutFieldEditorPreferencePage extends FieldEditorPreferencePage
{
	
	/**
	 * @param style
	 */
	public LayoutFieldEditorPreferencePage(int style)
	{
		super(style);
	}
	
	/**
	 * @param title
	 * @param style
	 */
	public LayoutFieldEditorPreferencePage(String title, int style)
	{
		super(title, style);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public LayoutFieldEditorPreferencePage(String title, ImageDescriptor image, int style)
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
		IntegerFieldEditor leftWidthEditor = new IntegerFieldEditor("layout.left", "Verhältniszahl linke Seite (in %)",
						this.getFieldEditorParent(), 2);
		leftWidthEditor.setEmptyStringAllowed(false);
		this.addField(leftWidthEditor);
		
		StringComboFieldEditor totalEditor = new StringComboFieldEditor(
						"layout.total-block", //$NON-NLS-1$
						Messages.getString("LayoutFieldEditorPreferencePage.Anordnung_Totalblock_2"), //$NON-NLS-1$
						this.getFieldEditorParent(),
						new String[]
						{
										Messages.getString("LayoutFieldEditorPreferencePage.Oben_3"), Messages.getString("LayoutFieldEditorPreferencePage.Unten_4") }, //$NON-NLS-1$ //$NON-NLS-2$
						new String[]
						{ "top", "bottom" }); //$NON-NLS-1$ //$NON-NLS-2$
		this.addField(totalEditor);
		
		StringComboFieldEditor topLeftEditor = new StringComboFieldEditor(
						"layout.top-left", //$NON-NLS-1$
						Messages.getString("LayoutFieldEditorPreferencePage.Linker_oberer_Quadrant_8"), //$NON-NLS-1$
						this.getFieldEditorParent(),
						new String[]
						{
										Messages.getString("LayoutFieldEditorPreferencePage.Belegdetailangaben_9"), Messages.getString("LayoutFieldEditorPreferencePage.Eingabeauswahl_10"), Messages.getString("LayoutFieldEditorPreferencePage.Numerischer_Eingabeblock_11"), Messages.getString("LayoutFieldEditorPreferencePage.Funktionsblock_12") }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						new String[]
						{ "children-block", "receipt-block", "numeric-block", "function-block" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		this.addField(topLeftEditor);
		
		StringComboFieldEditor topRightEditor = new StringComboFieldEditor(
						"layout.top-right", //$NON-NLS-1$
						Messages.getString("LayoutFieldEditorPreferencePage.Rechter_oberer_Quadrant_18"), //$NON-NLS-1$
						this.getFieldEditorParent(),
						new String[]
						{
										Messages.getString("LayoutFieldEditorPreferencePage.Belegdetailangaben_19"), Messages.getString("LayoutFieldEditorPreferencePage.Eingabeauswahl_20"), Messages.getString("LayoutFieldEditorPreferencePage.Numerischer_Eingabeblock_21"), Messages.getString("LayoutFieldEditorPreferencePage.Funktionsblock_22") }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						new String[]
						{ "children-block", "receipt-block", "numeric-block", "function-block" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		this.addField(topRightEditor);
		
		StringComboFieldEditor bottomLeftEditor = new StringComboFieldEditor(
						"layout.bottom-left", //$NON-NLS-1$
						Messages.getString("LayoutFieldEditorPreferencePage.Linker_unterer_Quadrant_28"), //$NON-NLS-1$
						this.getFieldEditorParent(),
						new String[]
						{
										Messages.getString("LayoutFieldEditorPreferencePage.Belegdetailangaben_29"), Messages.getString("LayoutFieldEditorPreferencePage.Eingabeauswahl_30"), Messages.getString("LayoutFieldEditorPreferencePage.Numerischer_Eingabeblock_31"), Messages.getString("LayoutFieldEditorPreferencePage.Funktionsblock_32") }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						new String[]
						{ "children-block", "receipt-block", "numeric-block", "function-block" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		this.addField(bottomLeftEditor);
		
		StringComboFieldEditor bottomRightEditor = new StringComboFieldEditor(
						"layout.bottom-right", //$NON-NLS-1$
						Messages.getString("LayoutFieldEditorPreferencePage.Rechter_unterer_Quadrant_38"), //$NON-NLS-1$
						this.getFieldEditorParent(),
						new String[]
						{
										Messages.getString("LayoutFieldEditorPreferencePage.Belegdetailangaben_39"), Messages.getString("LayoutFieldEditorPreferencePage.Eingabeauswahl_40"), Messages.getString("LayoutFieldEditorPreferencePage.Numerischer_Eingabeblock_41"), Messages.getString("LayoutFieldEditorPreferencePage.Funktionsblock_42") }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						new String[]
						{ "children-block", "receipt-block", "numeric-block", "function-block" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		this.addField(bottomRightEditor);
	}
	
}
