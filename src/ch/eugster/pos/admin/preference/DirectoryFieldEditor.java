/*
 * Created on 14.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.admin.preference;

import java.io.File;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;

import ch.eugster.pos.Messages;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DirectoryFieldEditor extends org.eclipse.jface.preference.StringButtonFieldEditor
{
	
	/**
	 * 
	 */
	public DirectoryFieldEditor()
	{
		super();
	}
	
	/**
	 * @param name
	 * @param labelText
	 * @param parent
	 */
	public DirectoryFieldEditor(String name, String labelText, Composite parent)
	{
		super(name, labelText, parent);
		this.setChangeButtonText(Messages.getString("DirectoryFieldEditor_old.Durchsuchen..._1")); //$NON-NLS-1$
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.admin.gui.widget.StringButtonFieldEditor#changePressed()
	 */
	protected String changePressed()
	{
		File file = new File(this.getStringValue());
		DirectoryDialog fileDialog = new DirectoryDialog(this.getShell());
		fileDialog.setText("Exportpfad");
		if (file.exists() && file.isDirectory())
		{
			fileDialog.setFilterPath(file.getAbsolutePath());
		}
		String dir = fileDialog.open();
		if (dir != null)
		{
			dir = dir.trim();
			if (dir.length() > 0) return dir;
		}
		
		return null;
	}
	
}
