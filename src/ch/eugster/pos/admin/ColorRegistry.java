/*
 * Created on 19.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ColorRegistry
{
	
	private static Hashtable colorTable = new Hashtable();
	// Keep a list of the Colors we have allocated seperately
	// as system colors do not need to be disposed.
	private static ArrayList allocatedColors = new ArrayList();
	
	/**
	 * Get the Color used for banner backgrounds
	 */
	
	public static Color getBannerBackground(Display display)
	{
		return display.getSystemColor(SWT.COLOR_LIST_BACKGROUND);
	}
	
	/**
	 * Get the Color used for banner foregrounds
	 */
	
	public static Color getBannerForeground(Display display)
	{
		return display.getSystemColor(SWT.COLOR_LIST_FOREGROUND);
	}
	
	/**
	 * Get the background Color for widgets that display errors.
	 */
	
	public static Color getErrorBackground(Display display)
	{
		return display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
	}
	
	/**
	 * Get the border Color for widgets that display errors.
	 */
	
	public static Color getErrorBorder(Display display)
	{
		return display.getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW);
	}
	
	/**
	 * Get the defualt color to use for displaying errors.
	 */
	public static Color getErrorText(Display display)
	{
		
		return ColorRegistry.getColorSetting(display, JFacePreferences.ERROR_COLOR);
	}
	
	/**
	 * Get the default color to use for displaying hyperlinks.
	 */
	public static Color getHyperlinkText(Display display)
	{
		
		return ColorRegistry.getColorSetting(display, JFacePreferences.HYPERLINK_COLOR);
	}
	
	/**
	 * Get the default color to use for displaying active hyperlinks.
	 */
	public static Color getActiveHyperlinkText(Display display)
	{
		
		return ColorRegistry.getColorSetting(display, JFacePreferences.ACTIVE_HYPERLINK_COLOR);
	}
	
	/**
	 * Clear out the cached color for name. This is generally done when the
	 * color preferences changed and any cached colors may be disposed. Users of
	 * the colors in this class should add a IPropertyChangeListener to detect
	 * when any of these colors change.
	 */
	public static void clearColor(String colorName)
	{
		ColorRegistry.colorTable.remove(colorName);
		// We do not dispose here for backwards compatibility
	}
	
	/**
	 * Get the color setting for the name.
	 */
	private static Color getColorSetting(Display display, String name)
	{
		
		if (ColorRegistry.colorTable.containsKey(name)) return (Color) ColorRegistry.colorTable.get(name);
		
		Color color = ColorRegistry.getDefaultColor(display, name);
		ColorRegistry.colorTable.put(name, color);
		return color;
	}
	
	/**
	 * Return the default color for the preferenceName. If there is no setting
	 * return the system black.
	 */
	private static Color getDefaultColor(Display display, String name)
	{
		
		if (name.equals(JFacePreferences.ERROR_COLOR)) return display.getSystemColor(SWT.COLOR_RED);
		if (name.equals(JFacePreferences.HYPERLINK_COLOR))
		{
			Color color = new Color(display, 0, 0, 153);
			ColorRegistry.allocatedColors.add(color);
			return color;
		}
		if (name.equals(JFacePreferences.ACTIVE_HYPERLINK_COLOR)) return display.getSystemColor(SWT.COLOR_BLUE);
		return display.getSystemColor(SWT.COLOR_BLACK);
	}
	
	/**
	 * Dispose of all allocated colors. Called on workbench shutdown.
	 */
	public static void disposeColors()
	{
		Iterator colors = ColorRegistry.allocatedColors.iterator();
		while (colors.hasNext())
		{
			((Color) colors.next()).dispose();
		}
	}
	
	/**
	 * Set the foreground and background colors of the control to the specified
	 * values. If the values are null than ignore them.
	 * 
	 * @param foreground
	 *            Color
	 * @param background
	 *            Color
	 */
	public static void setColors(Control control, Color foreground, Color background)
	{
		if (foreground != null) control.setForeground(foreground);
		if (background != null) control.setBackground(background);
	}
	
}
