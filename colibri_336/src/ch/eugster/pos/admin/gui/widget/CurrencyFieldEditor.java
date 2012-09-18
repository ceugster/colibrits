/*
 * Created on 13.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.gui.widget;

import java.util.Locale;

import org.eclipse.swt.widgets.Composite;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class CurrencyFieldEditor extends DoubleFieldEditor
{
	
	/**
	 * 
	 */
	public CurrencyFieldEditor()
	{
		super();
	}
	
	/**
	 * @param name
	 * @param labelText
	 * @param parent
	 */
	public CurrencyFieldEditor(String name, String labelText, Composite parent, Locale locale)
	{
		super(name, labelText, parent);
		this.locale = locale;
	}
	
	/**
	 * @param name
	 * @param labelText
	 * @param parent
	 * @param textLimit
	 */
	public CurrencyFieldEditor(String name, String labelText, Composite parent, Locale locale, int textLimit)
	{
		super(name, labelText, parent, textLimit);
		this.locale = locale;
	}
	
	protected Locale locale;
	
}
