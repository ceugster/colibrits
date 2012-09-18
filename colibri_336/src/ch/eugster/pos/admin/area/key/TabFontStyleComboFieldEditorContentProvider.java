/*
 * Created on 09.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.key;

import ch.eugster.pos.admin.gui.widget.ComboFieldEditor;
import ch.eugster.pos.admin.gui.widget.ComboFieldEditorContentProvider;

/**
 * @author ceugster
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TabFontStyleComboFieldEditorContentProvider
	extends ComboFieldEditorContentProvider {

	/**
	 * 
	 */
	public TabFontStyleComboFieldEditorContentProvider() {
		super();
	}

	/**
	 * @param editor
	 */
	public TabFontStyleComboFieldEditorContentProvider(ComboFieldEditor editor) {
		super(editor);
	}

	/* (non-Javadoc)
	 * @see ch.eugster.pos.admin.gui.widget.IComboFieldEditorContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		styles = new Integer[4];
		for (int i = 0; i < styles.length; i++) {
			styles[i] = new Integer(i);
		}
		return styles;
	}

	/* (non-Javadoc)
	 * @see ch.eugster.pos.admin.gui.widget.IComboFieldEditorContentProvider#doLoad()
	 */
	public Object doLoad() {
		return editor.getStore().getInt(BlockFieldEditorPage.KEY_FONT_STYLE);
	}

	/* (non-Javadoc)
	 * @see ch.eugster.pos.admin.gui.widget.IComboFieldEditorContentProvider#doLoadDefault()
	 */
	public Object doLoadDefault() {
		return editor.getStore().getDefaultInt(BlockFieldEditorPage.KEY_FONT_STYLE);
	}

	/* (non-Javadoc)
	 * @see ch.eugster.pos.admin.gui.widget.IComboFieldEditorContentProvider#doStore()
	 */
	public void doStore() {
		if (editor.isValid()) {
			editor.getStore().setValue(BlockFieldEditorPage.KEY_FONT_STYLE, styles[editor.getCombo().getSelectionIndex()]);
		}
	}

	/* (non-Javadoc)
	 * @see ch.eugster.pos.admin.gui.widget.ComboFieldEditorContentProvider#isValid(java.lang.Object)
	 */
	protected boolean isValid(Object object) {
		return (object instanceof Integer);
	}

	/* (non-Javadoc)
	 * @see ch.eugster.pos.admin.gui.widget.IComboFieldEditorContentProvider#doCheckState()
	 */
	public boolean doCheckState() {
		boolean result = false;
		if (editor.getSelectedItem() == null) {
			return result;
		}
		
		if (editor.getCombo().getSelectionIndex() < 0) {
			return result;
		}
		
		if (editor.getSelectedItem() instanceof Integer) {
			result = true;
		}
		return result;
	}

	private Integer[] styles;
}
