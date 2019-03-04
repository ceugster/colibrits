/*
 * Created on 11.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.coin;

import ch.eugster.pos.admin.gui.widget.ComboFieldEditor;
import ch.eugster.pos.admin.gui.widget.ComboFieldEditorContentProvider;
import ch.eugster.pos.db.ForeignCurrency;

/**
 * @author administrator
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class ForeignCurrencyComboEditorFieldContentProvider extends ComboFieldEditorContentProvider {

	public ForeignCurrencyComboEditorFieldContentProvider(ComboFieldEditor editor)
	{
		super(editor);
	}
	
	public Object[] getElements(Object element) {
		ForeignCurrency[] currencies = ForeignCurrency.selectAll(true);
		return currencies;
	}

	public Object doLoad() {
		return editor.getStore().getForeignCurrency(editor.getName());
	}
	
	public Object doLoadDefault() {
		return editor.getStore().getDefaultForeignCurrency(editor.getName());
	}
	
	public void doStore() {
		if (editor.isValid()) {
			editor.getStore().setValue(editor.getName(), (ForeignCurrency) editor.getSelectedItem());
		}
	}
	
	/* (non-Javadoc)
	 * @see ch.eugster.pos.admin.widgets.IComboFieldEditorContentProvider#doCheckState()
	 */
	 public boolean doCheckState() {
		 boolean result = false;
		 if (editor.getSelectedItem() == null) {
			 return result;
		 }
		
		 if (editor.getCombo().getSelectionIndex() < 0) {
			 return result;
		 }
		
		 if (editor.getSelectedItem() instanceof ForeignCurrency) {
			 result = true;
		 }
		 return result;
	 }

	protected boolean isValid(Object o) {
		return (o instanceof ForeignCurrency);
	}
}
