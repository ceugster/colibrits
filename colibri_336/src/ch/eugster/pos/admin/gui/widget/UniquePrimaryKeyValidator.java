/*
 * Created on 16.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.admin.gui.widget;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.Table;

/**
 * @author ceugster
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class UniquePrimaryKeyValidator
	implements IFieldEditorContentValidator {

	/**
	 * 
	 */
	public UniquePrimaryKeyValidator(LongFieldEditor editor, Class clazz) {
		super();
		this.longFieldEditor = editor;
		this.clazz = clazz;
	}

	/* (non-Javadoc)
	 * @see ch.eugster.pos.admin.gui.widget.IFieldEditorContentValidator#isValid(ch.eugster.pos.admin.gui.widget.FieldEditor)
	 */
	public boolean isValid(FieldEditor editor) {
		Object object = this.longFieldEditor.getStore().getDefaultValue(Messages.getString("UniquePrimaryKeyValidator.id_1")); //$NON-NLS-1$
		if (object == null) return false;
		Table t = Table.selectById(this.clazz, new Long(this.longFieldEditor.getLongValue()));
		boolean isValid = t.getId() == null || t.getId().equals(Table.ZERO_VALUE) || object.equals(t.getId());
		if (!isValid) this.longFieldEditor.setErrorMessage(Messages.getString("UniquePrimaryKeyValidator.Die_Id_darf_nicht_verwendet_werden._2")); //$NON-NLS-1$
		return isValid;
	}

	private LongFieldEditor longFieldEditor;
	private Class clazz;
}
