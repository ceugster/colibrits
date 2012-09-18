/*
 * Created on 28.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.admin.preference;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PreferencePage extends org.eclipse.jface.preference.PreferencePage
{
	
	/**
	 * 
	 */
	public PreferencePage()
	{
		super();
	}
	
	/**
	 * @param title
	 */
	public PreferencePage(String title)
	{
		super(title);
	}
	
	/**
	 * @param title
	 * @param image
	 */
	public PreferencePage(String title, ImageDescriptor image)
	{
		super(title, image);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse
	 * .swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent)
	{
		return null;
	}
	
}
