/*
 * Created on 14.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.admin.preference;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import ch.eugster.pos.Messages;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PreferenceDialog extends org.eclipse.jface.preference.PreferenceDialog
{
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public PreferenceDialog(Shell arg0, PreferenceManager arg1)
	{
		super(arg0, arg1);
	}
	
	public void createButtonsForButtonBar(Composite composite)
	{
		super.createButtonsForButtonBar(composite);
		this.getButton(IDialogConstants.OK_ID).setText(Messages.getString("PreferenceDialog.OK_1")); //$NON-NLS-1$
		this.getButton(IDialogConstants.CANCEL_ID).setText(Messages.getString("PreferenceDialog.Abbrechen_2")); //$NON-NLS-1$
	}
	
	protected void handleSave()
	{
		if (this.getPreferenceStore() != null && this.getPreferenceStore() instanceof PreferenceStore)
		{
			if (this.getPreferenceStore().needsSaving())
			{
				PreferenceStore store = (PreferenceStore) this.getPreferenceStore();
				// try {
				store.save();
				// }
				// catch (IOException e) {
				// MessageDialog.openError(getShell(),
				// "Fehler",
				// "Beim Speichern der Einstellungen ist ein Fehler aufgetreten.");
				// }
			}
		}
	}
}
