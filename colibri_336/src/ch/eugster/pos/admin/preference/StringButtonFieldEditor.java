/*
 * Created on 13.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.admin.preference;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class StringButtonFieldEditor extends org.eclipse.jface.preference.StringButtonFieldEditor implements
				IPropertyChangeListener
{
	
	/**
	 * 
	 */
	public StringButtonFieldEditor()
	{
		super();
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public StringButtonFieldEditor(String arg0, String arg1, Composite arg2)
	{
		super(arg0, arg1, arg2);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.StringButtonFieldEditor#changePressed()
	 */
	protected String changePressed()
	{
		return null;
	}
	
	public void propertyChange(PropertyChangeEvent e)
	{
	}
}
