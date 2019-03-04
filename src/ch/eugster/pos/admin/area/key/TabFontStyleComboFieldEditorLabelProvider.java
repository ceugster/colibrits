/*
 * Created on 09.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.key;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.model.SimpleLabelProvider;

/**
 * @author ceugster
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TabFontStyleComboFieldEditorLabelProvider
	extends SimpleLabelProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		if (element instanceof Integer) {
			int index = ((Integer) element).intValue();
			switch (index) {
				case 0: return Messages.getString("TabFontStyleComboFieldEditorLabelProvider.Normal_1"); //$NON-NLS-1$
				case 1: return Messages.getString("TabFontStyleComboFieldEditorLabelProvider.Fett_2"); //$NON-NLS-1$
				case 2: return Messages.getString("TabFontStyleComboFieldEditorLabelProvider.Kursiv_3"); //$NON-NLS-1$
				case 3: return Messages.getString("TabFontStyleComboFieldEditorLabelProvider.Fett,_kursiv_4"); //$NON-NLS-1$
			}
		}
		return ""; //$NON-NLS-1$
	}

}
