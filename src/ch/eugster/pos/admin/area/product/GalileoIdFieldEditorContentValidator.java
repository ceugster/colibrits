/*
 * Created on 05.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.product;

import ch.eugster.pos.admin.gui.widget.FieldEditor;
import ch.eugster.pos.admin.gui.widget.IFieldEditorContentValidator;
import ch.eugster.pos.admin.gui.widget.StringFieldEditor;
import ch.eugster.pos.product.GalileoProductGroupServer;

/**
 * @author ceugster
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class GalileoIdFieldEditorContentValidator
	implements IFieldEditorContentValidator {

	/**
	 * 
	 */
	public GalileoIdFieldEditorContentValidator() {
		super();
	}

	/* (non-Javadoc)
	 * @see ch.eugster.pos.admin.gui.widget.IFieldEditorContentValidator#isValid(ch.eugster.pos.admin.gui.widget.FieldEditor)
	 */
	public boolean isValid(FieldEditor editor) {
		boolean result = true;
		String value = ((StringFieldEditor) editor).getTextControl().getText();
		if (value != null && value.length() == 3) {
			result = GalileoProductGroupServer.galileoIdExists(value);
		}
		return result;
	}

}
