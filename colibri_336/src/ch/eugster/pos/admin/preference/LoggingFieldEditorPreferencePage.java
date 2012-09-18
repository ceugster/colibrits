/*
 * Created on 16.12.2003
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
public class LoggingFieldEditorPreferencePage extends FieldEditorPreferencePage
{
	
	/**
	 * @param style
	 */
	public LoggingFieldEditorPreferencePage(int style)
	{
		super(style);
	}
	
	/**
	 * @param title
	 * @param style
	 */
	public LoggingFieldEditorPreferencePage(String title, int style)
	{
		super(title, style);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public LoggingFieldEditorPreferencePage(String title, ImageDescriptor image, int style)
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
		StringComboFieldEditor levelEditor = new StringComboFieldEditor(
						"logging.level", Messages.getString("LoggingFieldEditorPreferencePage.Level_2"), this.getFieldEditorParent(), new String[] { "ALL", "INFO", "WARNING", "SEVERE", "OFF" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
		this.addField(levelEditor);
		
		IntegerFieldEditor maxEntryEditor = new IntegerFieldEditor(
						"logging.max", Messages.getString("LoggingFieldEditorPreferencePage.Maximale_Protokollgr_u00F6sse_9"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		maxEntryEditor.setTextLimit(10);
		this.addField(maxEntryEditor);
		
		BooleanFieldEditor traceEditor = new BooleanFieldEditor(
						"logging.trace", "Trace-Protokollierung", this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$ 
		this.addField(traceEditor);
		
		LabelFieldEditor editor = new LabelFieldEditor(
						"Achtung: Aktivierte Trace-Protokollierung verlangsamt die Programme erheblich.", this
										.getFieldEditorParent());
		this.addField(editor);
		
		LabelFieldEditor filler = new LabelFieldEditor("", this.getFieldEditorParent());
		this.addField(filler);
		// 10156
		BooleanFieldEditor saveReceiptsEditor = new BooleanFieldEditor("logging.receipts", "Belegkopien speichern",
						this.getFieldEditorParent());
		this.addField(saveReceiptsEditor);
		// 10156
		// 10158
		BooleanFieldEditor zipReceiptsEditor = new BooleanFieldEditor("logging.compress",
						"Sicherungskopien komprimieren", this.getFieldEditorParent());
		this.addField(zipReceiptsEditor);
		// 10158
	}
	
}
