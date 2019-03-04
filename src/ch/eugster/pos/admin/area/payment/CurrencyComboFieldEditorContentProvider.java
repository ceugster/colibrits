/*
 * Created on 27.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.payment;

import ch.eugster.pos.admin.gui.widget.ComboFieldEditor;
import ch.eugster.pos.admin.gui.widget.ComboFieldEditorContentProvider;
import ch.eugster.pos.db.ForeignCurrency;

/**
 * @author administrator
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class CurrencyComboFieldEditorContentProvider
	extends ComboFieldEditorContentProvider {

	/**
	 * 
	 */
	public CurrencyComboFieldEditorContentProvider() {
		super();
	}

	/**
	 * @param editor
	 */
	public CurrencyComboFieldEditorContentProvider(ComboFieldEditor editor) {
		super(editor);
	}

	/* (non-Javadoc)
	 * @see ch.eugster.pos.admin.widgets.IComboFieldEditorContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		currencies = ForeignCurrency.selectAll(true);
		return currencies;
	}
	
	/* (non-Javadoc)
	 * @see ch.eugster.pos.admin.widgets.IComboFieldEditorContentProvider#doLoad()
	 */
	public Object doLoad() {
		return editor.getStore().getForeignCurrency(PaymentTypeFieldEditorPage.KEY_CURRENCY);
	}

	/* (non-Javadoc)
	 * @see ch.eugster.pos.admin.widgets.IComboFieldEditorContentProvider#doLoadDefault()
	 */
	public Object doLoadDefault() {
		return editor.getStore().getDefaultForeignCurrency(PaymentTypeFieldEditorPage.KEY_CURRENCY);
	}

	/* (non-Javadoc)
	 * @see ch.eugster.pos.admin.widgets.IComboFieldEditorContentProvider#doStore()
	 */
	public void doStore() {
		if (editor.isValid()) {
			editor.getStore().setValue(editor.getName(), currencies[editor.getCombo().getSelectionIndex()]);
		}
	}

	/* (non-Javadoc)
	 * @see ch.eugster.pos.admin.widgets.ComboFieldEditorContentProvider#isValid(java.lang.Object)
	 */
	protected boolean isValid(Object object) {
		return (object instanceof ForeignCurrency);
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
	

	private ForeignCurrency[] currencies;
}
