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
public class PropertyBooleanFieldEditor extends BooleanFieldEditor
{
	
	Properties properties;
	String key;
	
	public PropertyBooleanFieldEditor(String name, String label, Composite parent, Properties properties, String key)
	{
		super(name, label, parent);
		this.properties = properties;
		this.key = key;
	}
	
	protected void doLoad()
	{
		this.setBooleanValue(new Boolean(this.properties.getProperty(this.key)).booleanValue());
	}
	
	protected void doStore()
	{
		this.properties.put(this.key, new Boolean(this.getBooleanValue()).toString());
	}
}
