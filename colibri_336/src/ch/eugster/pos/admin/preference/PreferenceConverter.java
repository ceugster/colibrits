/*
 * Created on 17.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.preference;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.swt.graphics.RGB;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class PreferenceConverter
{
	
	/**
	 * 
	 */
	public PreferenceConverter()
	{
		super();
	}
	
	public static void setColor(IPreferenceStore store, String name, RGB value)
	{
		RGB oldValue = PreferenceConverter.getColor(store, name);
		if (oldValue == null || !oldValue.equals(value))
		{
			store.putValue(name + ".red", String.valueOf(value.red)); //$NON-NLS-1$
			store.putValue(name + ".green", String.valueOf(value.green)); //$NON-NLS-1$
			store.putValue(name + ".blue", String.valueOf(value.blue)); //$NON-NLS-1$
			// Clear the cache before updating
			JFaceColors.clearColor(name);
			store.firePropertyChangeEvent(name, oldValue, value);
		}
	}
	
	public static RGB getColor(IPreferenceStore store, String name)
	{
		RGB color = new RGB(0, 0, 0);
		color.red = store.getInt(name + ".red"); //$NON-NLS-1$
		color.green = store.getInt(name + ".green"); //$NON-NLS-1$
		color.blue = store.getInt(name + ".blue"); //$NON-NLS-1$
		return color;
	}
	
	public static RGB getDefaultColor(IPreferenceStore store, String name)
	{
		RGB color = new RGB(0, 0, 0);
		color.red = store.getDefaultInt(name + ".red"); //$NON-NLS-1$
		color.green = store.getDefaultInt(name + ".green"); //$NON-NLS-1$
		color.blue = store.getDefaultInt(name + ".blue"); //$NON-NLS-1$
		return color;
	}
}
