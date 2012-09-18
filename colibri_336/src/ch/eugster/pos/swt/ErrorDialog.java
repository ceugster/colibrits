/*
 * Created on 19.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.swt;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ErrorDialog extends IconAndMessageDialog
{
	
	/**
	 * @param parentShell
	 */
	public ErrorDialog(Shell parentShell)
	{
		super(parentShell);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.IconAndMessageDialog#getImage()
	 */
	protected Image getImage()
	{
		return Dialog.getImage(org.eclipse.jface.dialogs.Dialog.DLG_IMG_MESSAGE_ERROR);
	}
	
}
