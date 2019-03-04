/*
 * Created on 05.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.fixkey;

import javax.swing.SwingConstants;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.pos.Constants;
import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.gui.widget.ColorFieldEditor;
import ch.eugster.pos.admin.gui.widget.DoubleComboFieldEditor;
import ch.eugster.pos.admin.gui.widget.FieldEditorPage;
import ch.eugster.pos.admin.gui.widget.FileFieldEditor;
import ch.eugster.pos.admin.gui.widget.IntegerComboFieldEditor;
import ch.eugster.pos.admin.gui.widget.StringFieldEditor;
import ch.eugster.pos.db.FixKey;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class FixKeyFieldEditorPage extends FieldEditorPage
{
	
	/**
	 * @param style
	 */
	public FixKeyFieldEditorPage(String name, int style, PersistentDBStore store)
	{
		super(name, style, store);
	}
	
	/**
	 * @param title
	 * @param style
	 */
	public FixKeyFieldEditorPage(String name, String title, int style, PersistentDBStore store)
	{
		super(name, title, style, store);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public FixKeyFieldEditorPage(String name, String title, ImageDescriptor image, int style, PersistentDBStore store)
	{
		super(name, title, image, style, store);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.FieldEditorPage#createFieldEditors()
	 */
	protected void createFieldEditors()
	{
		this.nameEditor = new StringFieldEditor(
						FixKeyFieldEditorPage.KEY_NAME,
						Messages.getString("FixKeyFieldEditorPage.Beschriftung_1"), -1, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		this.nameEditor.setEmptyStringAllowed(false);
		this.nameEditor.setErrorMessage(Messages.getString("FixKeyFieldEditorPage.Die_Beschriftung_ist_notwendig._2")); //$NON-NLS-1$
		this.nameEditor.showErrorMessage();
		this.addField(this.nameEditor);
		
		DoubleComboFieldEditor sizeEditor = new DoubleComboFieldEditor(
						FixKeyFieldEditorPage.KEY_SIZE,
						Messages.getString("FixKeyFieldEditorPage.Schriftgr_u00F6sse_3"), //$NON-NLS-1$
						this.getFieldEditorParent(),
						new String[]
						{
										Messages.getString("FixKeyFieldEditorPage.8_4"), Messages.getString("FixKeyFieldEditorPage.10_5"), Messages.getString("FixKeyFieldEditorPage.12_6"), Messages.getString("FixKeyFieldEditorPage.14_7"), Messages.getString("FixKeyFieldEditorPage.16_8"), Messages.getString("FixKeyFieldEditorPage.18_9"), Messages.getString("FixKeyFieldEditorPage.20_10"), Messages.getString("FixKeyFieldEditorPage.22_11"), Messages.getString("FixKeyFieldEditorPage.24_12") }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
						new Double[]
						{ new Double(8d), new Double(10d), new Double(12d), new Double(14d), new Double(16d),
										new Double(18d), new Double(20d), new Double(22d), new Double(24d) });
		this.addField(sizeEditor);
		
		IntegerComboFieldEditor styleEditor = new IntegerComboFieldEditor(
						FixKeyFieldEditorPage.KEY_STYLE,
						Messages.getString("FixKeyFieldEditorPage.Schriftstil_13"), //$NON-NLS-1$
						this.getFieldEditorParent(),
						new String[]
						{
										Messages.getString("FixKeyFieldEditorPage.Standard_14"), Messages.getString("FixKeyFieldEditorPage.Fett_15"), Messages.getString("FixKeyFieldEditorPage.Kursiv_16"), Messages.getString("FixKeyFieldEditorPage.Fett,_kursiv_17") }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						new Integer[]
						{ new Integer(Constants.FONT_STYLE_PLAIN), new Integer(Constants.FONT_STYLE_BOLD),
										new Integer(Constants.FONT_STYLE_ITALIC),
										new Integer(Constants.FONT_STYLE_BOLD + Constants.FONT_STYLE_ITALIC) });
		this.addField(styleEditor);
		
		IntegerComboFieldEditor horizontalAlignEditor = new IntegerComboFieldEditor(
						FixKeyFieldEditorPage.KEY_HORZ_POS,
						Messages.getString("FixKeyFieldEditorPage.Horizontale_Ausrichtung_1"), //$NON-NLS-1$
						this.getFieldEditorParent(),
						new String[]
						{
										Messages.getString("FixKeyFieldEditorPage.Links_2"), Messages.getString("FixKeyFieldEditorPage.Zentriert_3"), Messages.getString("FixKeyFieldEditorPage.Rechts_4") }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						new Integer[]
						{ new Integer(SwingConstants.LEFT), new Integer(SwingConstants.CENTER),
										new Integer(SwingConstants.RIGHT) });
		this.addField(horizontalAlignEditor);
		
		IntegerComboFieldEditor verticalAlignEditor = new IntegerComboFieldEditor(
						FixKeyFieldEditorPage.KEY_VERT_POS,
						Messages.getString("FixKeyFieldEditorPage.Vertikale_Ausrichtung_5"), //$NON-NLS-1$
						this.getFieldEditorParent(),
						new String[]
						{
										Messages.getString("FixKeyFieldEditorPage.Oben_6"), Messages.getString("FixKeyFieldEditorPage.Mitte_7"), Messages.getString("FixKeyFieldEditorPage.Unten_8") }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						new Integer[]
						{ new Integer(SwingConstants.TOP), new Integer(SwingConstants.CENTER),
										new Integer(SwingConstants.BOTTOM) });
		this.addField(verticalAlignEditor);
		
		ColorFieldEditor fgEditor = new ColorFieldEditor(FixKeyFieldEditorPage.KEY_FG, Messages
						.getString("FixKeyFieldEditorPage.Schriftfarbe_18"), this.getFieldEditorParent()); //$NON-NLS-1$
		this.addField(fgEditor);
		
		ColorFieldEditor bgEditor = new ColorFieldEditor(FixKeyFieldEditorPage.KEY_BG, Messages
						.getString("FixKeyFieldEditorPage.Hintergrundfarbe_19"), this.getFieldEditorParent()); //$NON-NLS-1$
		this.addField(bgEditor);
		
		// 10052
		// Neu kann die Hintergrundfarbe der Tasten eingegeben werden, die
		// gezeigt werden soll, wenn ein Failover passiert.
		ColorFieldEditor bgFailoverEditor = new ColorFieldEditor(FixKeyFieldEditorPage.KEY_BG_FAILOVER,
						"Hintergrundfarbe Failover", this.getFieldEditorParent()); //$NON-NLS-1$
		this.addField(bgFailoverEditor);
		
		FileFieldEditor imageEditor = new FileFieldEditor(
						Messages.getString("FixKeyFieldEditorPage.image-path_20"), Messages.getString("FixKeyFieldEditorPage.Bild_21"), true, this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		this.addField(imageEditor);
		
		IntegerComboFieldEditor horzEditor = new IntegerComboFieldEditor(
						FixKeyFieldEditorPage.KEY_TEXT_HORZ_POS,
						Messages.getString("FixKeyFieldEditorPage.Horizontale_Textausrichtung_zum_Bild_22"), //$NON-NLS-1$
						this.getFieldEditorParent(),
						new String[]
						{
										Messages.getString("FixKeyFieldEditorPage.Rechts_23"), Messages.getString("FixKeyFieldEditorPage.Links_24"), Messages.getString("FixKeyFieldEditorPage.Zentriert_25"), Messages.getString("FixKeyFieldEditorPage.F_u00FChrend_26"), Messages.getString("FixKeyFieldEditorPage.Folgend_27") }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
						new Integer[]
						{ new Integer(SwingConstants.RIGHT), new Integer(SwingConstants.LEFT),
										new Integer(SwingConstants.CENTER), new Integer(SwingConstants.LEADING),
										new Integer(SwingConstants.TRAILING) });
		this.addField(horzEditor);
		
		IntegerComboFieldEditor vertEditor = new IntegerComboFieldEditor(
						FixKeyFieldEditorPage.KEY_TEXT_VERT_POS,
						Messages.getString("FixKeyFieldEditorPage.Vertikale_Textausrichtung_zum_Bild_28"), //$NON-NLS-1$
						this.getFieldEditorParent(),
						new String[]
						{
										Messages.getString("FixKeyFieldEditorPage.Zentriert_29"), Messages.getString("FixKeyFieldEditorPage.Oben_30"), Messages.getString("FixKeyFieldEditorPage.Unten_31") }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						new Integer[]
						{ new Integer(SwingConstants.CENTER), new Integer(SwingConstants.TOP),
										new Integer(SwingConstants.BOTTOM) });
		this.addField(vertEditor);
		
	}
	
	public boolean isInstance(Object element)
	{
		return element instanceof FixKey;
	}
	
	public boolean isDirty()
	{
		return false;
	}
	
	public String getElementName()
	{
		FixKey key = (FixKey) this.getStore().getElement();
		return key.text;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(
	 * org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(Object element)
	{
		super.selectionChanged(element);
		if (this.getStore().getElement() instanceof FixKey)
		{
			FixKey key = (FixKey) this.getStore().getElement();
			this.nameEditor.setEnabled(key.textEditable, this.getFieldEditorParent());
		}
	}
	
	private StringFieldEditor nameEditor;
	
	public static final String KEY_NAME = Messages.getString("FixKeyFieldEditorPage.name_32"); //$NON-NLS-1$
	public static final String KEY_FG = Messages.getString("FixKeyFieldEditorPage.fg_33"); //$NON-NLS-1$
	public static final String KEY_BG = Messages.getString("FixKeyFieldEditorPage.bg_34"); //$NON-NLS-1$
	public static final String KEY_BG_FAILOVER = "bg-failover"; //$NON-NLS-1$
	public static final String KEY_SIZE = Messages.getString("FixKeyFieldEditorPage.size_35"); //$NON-NLS-1$
	public static final String KEY_STYLE = Messages.getString("FixKeyFieldEditorPage.style_36"); //$NON-NLS-1$
	public static final String KEY_HORZ_POS = Messages.getString("FixKeyFieldEditorPage.rel-horizontal-text-pos_38"); //$NON-NLS-1$
	public static final String KEY_VERT_POS = Messages.getString("FixKeyFieldEditorPage.rel-vertical-text-pos_39"); //$NON-NLS-1$
	public static final String KEY_IMAGE_PATH = Messages.getString("FixKeyFieldEditorPage.image-path_37"); //$NON-NLS-1$
	public static final String KEY_TEXT_HORZ_POS = Messages
					.getString("FixKeyFieldEditorPage.rel-horizontal-text-pos_38"); //$NON-NLS-1$
	public static final String KEY_TEXT_VERT_POS = Messages.getString("FixKeyFieldEditorPage.rel-vertical-text-pos_39"); //$NON-NLS-1$
}
