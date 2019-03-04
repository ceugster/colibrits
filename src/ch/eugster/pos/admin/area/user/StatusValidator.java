/*
 * Created on 19.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.admin.area.user;

import ch.eugster.pos.admin.gui.widget.FieldEditor;
import ch.eugster.pos.admin.gui.widget.IFieldEditorContentValidator;
import ch.eugster.pos.admin.gui.widget.IntegerComboFieldEditor;
import ch.eugster.pos.db.User;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class StatusValidator implements IFieldEditorContentValidator
{
	
	/**
	 * 
	 */
	public StatusValidator()
	{
		super();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.admin.gui.widget.IFieldEditorContentValidator#isValid(
	 * ch.eugster.pos.admin.gui.widget.FieldEditor)
	 */
	public boolean isValid(FieldEditor editor)
	{
		if (editor instanceof IntegerComboFieldEditor)
		{
			IntegerComboFieldEditor ed = (IntegerComboFieldEditor) editor;
			if (editor.getName().equals(UserFieldEditorPage.KEY_STATE))
			{
				if (new Long(1l).equals(((User) ed.getStore().getElement()).getId()))
				{
					return ed.getCurrentValue().equals(new Integer(0));
				}
			}
		}
		return true;
	}
	
}
