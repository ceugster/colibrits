/*
 * Created on 13.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.admin.preference;

import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.widgets.Composite;

import ch.eugster.pos.Messages;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ListFieldEditor extends ListEditor
{
	
	/**
	 * 
	 */
	public ListFieldEditor()
	{
		super();
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public ListFieldEditor(String arg0, String arg1, Composite arg2)
	{
		super(arg0, arg1, arg2);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.ListEditor#createList(java.lang.String[])
	 */
	protected String createList(String[] list)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < list.length; i++)
		{
			sb.append(list[i]);
			if (i < list.length - 1)
			{
				sb.append("|"); //$NON-NLS-1$
			}
		}
		return sb.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.ListEditor#getNewInputObject()
	 */
	protected String getNewInputObject()
	{
		InputDialog dialog = new InputDialog(
						this.getShell(),
						Messages.getString("ListFieldEditor.Text_2"), Messages.getString("ListFieldEditor.Gew_u00C3_u00BCnschter_Text_3"), "", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if (dialog.open() == 0)
		{
			return dialog.getValue();
		}
		else
		{
			return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.ListEditor#parseString(java.lang.String)
	 */
	protected String[] parseString(String text)
	{
		StringTokenizer t = new StringTokenizer(text, "|"); //$NON-NLS-1$
		String[] result = new String[t.countTokens()];
		int i = 0;
		while (t.hasMoreElements())
		{
			result[i] = t.nextToken();
			i++;
		}
		return result;
	}
}
