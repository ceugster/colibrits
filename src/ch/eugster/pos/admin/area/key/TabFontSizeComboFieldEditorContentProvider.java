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
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class TabFontSizeComboFieldEditorContentProvider extends ComboFieldEditorContentProvider
{
	
	/**
	 * 
	 */
	public TabFontSizeComboFieldEditorContentProvider()
	{
		super();
	}
	
	/**
	 * @param editor
	 */
	public TabFontSizeComboFieldEditorContentProvider(ComboFieldEditor editor)
	{
		super(editor);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.admin.gui.widget.IComboFieldEditorContentProvider#getElements
	 * (java.lang.Object)
	 */
	public Object[] getElements(Object inputElement)
	{
		this.sizes = new Double[64];
		for (int i = 0; i < this.sizes.length; i++)
		{
			this.sizes[i] = new Double(i + 8);
		}
		return this.sizes;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.admin.gui.widget.IComboFieldEditorContentProvider#doLoad()
	 */
	public Object doLoad()
	{
		return this.editor.getStore().getDouble(BlockFieldEditorPage.KEY_FONT_SIZE);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @seech.eugster.pos.admin.gui.widget.IComboFieldEditorContentProvider#
	 * doLoadDefault()
	 */
	public Object doLoadDefault()
	{
		return this.editor.getStore().getDefaultDouble(BlockFieldEditorPage.KEY_FONT_SIZE);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.admin.gui.widget.IComboFieldEditorContentProvider#doStore
	 * ()
	 */
	public void doStore()
	{
		if (this.editor.isValid())
		{
			this.editor.getStore().setValue(BlockFieldEditorPage.KEY_FONT_SIZE,
							this.sizes[this.editor.getCombo().getSelectionIndex()]);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.admin.gui.widget.ComboFieldEditorContentProvider#isValid
	 * (java.lang.Object)
	 */
	protected boolean isValid(Object object)
	{
		return object instanceof Double;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.admin.gui.widget.IComboFieldEditorContentProvider#doCheckState
	 * ()
	 */
	public boolean doCheckState()
	{
		boolean result = false;
		if (this.editor.getSelectedItem() == null)
		{
			return result;
		}
		
		if (this.editor.getCombo().getSelectionIndex() < 0)
		{
			return result;
		}
		
		if (this.editor.getSelectedItem() instanceof Double)
		{
			result = true;
		}
		return result;
	}
	
	private Double[] sizes;
}
