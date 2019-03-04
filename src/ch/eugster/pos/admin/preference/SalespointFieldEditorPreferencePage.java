/*
 * Created on 17.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.preference;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.PropertyChangeEvent;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.Salespoint;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class SalespointFieldEditorPreferencePage extends FieldEditorPreferencePage
{
	
	/**
	 * @param style
	 */
	public SalespointFieldEditorPreferencePage(int style)
	{
		super(style);
	}
	
	/**
	 * @param title
	 * @param style
	 */
	public SalespointFieldEditorPreferencePage(String title, int style)
	{
		super(title, style);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public SalespointFieldEditorPreferencePage(String title, ImageDescriptor image, int style)
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
	@Override
	protected void createFieldEditors()
	{
		Salespoint[] sp = Salespoint.selectAll(false);
		String[] names = new String[sp.length];
		long[] ids = new long[sp.length];
		for (int i = 0; i < sp.length; i++)
		{
			names[i] = sp[i].name;
			ids[i] = sp[i].getId().longValue();
		}
		LongComboFieldEditor idEditor = new LongComboFieldEditor("salespoint.id", //$NON-NLS-1$
						Messages.getString("SalespointFieldEditorPreferencePage.Kassenidentit_u00E4t_2"), //$NON-NLS-1$
						this.getFieldEditorParent(), names, ids);
		this.addField(idEditor);
		
		SpacerFieldEditor space = new SpacerFieldEditor(this.getFieldEditorParent());
		this.addField(space);
		
		BooleanFieldEditor forceSettlementEditor = new BooleanFieldEditor(
						"salespoint.force-settlement", "Tagesabschluss erzwingen", this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		// forceEditor.setPropertyChangeListener(this);
		this.addField(forceSettlementEditor);
		
		BooleanFieldEditor forceStockCountEditor = new BooleanFieldEditor(
						"salespoint.force-stock-count", "Kassensturz erzwingen", this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		// forceEditor.setPropertyChangeListener(this);
		this.addField(forceStockCountEditor);
		
		this.exportEditor = new BooleanFieldEditor(
						"salespoint.export", Messages.getString("SalespointFieldEditorPreferencePage.Belege_beim_Kassenabschluss_zus_u00E4tzlich_exportieren_2"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.exportEditor.setPropertyChangeListener(this);
		this.addField(this.exportEditor);
		
		this.pathEditor = new DirectoryFieldEditor(
						"salespoint.export.path", Messages.getString("SalespointFieldEditorPreferencePage.Exportverzeichnis_4"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.pathEditor.setEmptyStringAllowed(this.exportEditor.getBooleanValue());
		// pathEditor.setEnabled(true, getFieldEditorParent());
		this.addField(this.pathEditor);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
		if (e.getSource().equals(this.exportEditor))
		{
			this.pathEditor.setEnabled(this.exportEditor.getBooleanValue(), this.getFieldEditorParent());
		}
	}
	
	private BooleanFieldEditor exportEditor;
	private DirectoryFieldEditor pathEditor;
}
