/*
 * Created on Jun 6, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.admin.preference;

import java.util.Properties;

import org.eclipse.swt.widgets.Composite;

/**
 * @author U1923
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class PropertiesTableEditor extends TableEditor
{
	
	protected Properties properties;
	
	/**
	 * @param name
	 * @param labelText
	 * @param parent
	 * @param properties
	 */
	public PropertiesTableEditor(String name, String labelText, Composite parent, Properties properties)
	{
		super(name, labelText, parent);
		this.properties = properties;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#doLoad()
	 */
	protected abstract void doLoad();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#doStore()
	 */
	protected abstract void doStore();
	
}
