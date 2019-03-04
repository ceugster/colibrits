/*
 * Created on 08.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.gui.widget;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Composite;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class PercentFieldEditor extends DoubleFieldEditor
{
	
	protected Double currentValue = new Double(0d);
	
	/**
	 * 
	 */
	public PercentFieldEditor()
	{
		super();
	}
	
	/**
	 * @param name
	 * @param labelText
	 * @param parent
	 */
	public PercentFieldEditor(String name, String labelText, Composite parent)
	{
		super(name, labelText, parent);
	}
	
	/**
	 * @param name
	 * @param labelText
	 * @param parent
	 * @param textLimit
	 */
	public PercentFieldEditor(String name, String labelText, Composite parent, int textLimit)
	{
		super(name, labelText, parent, textLimit);
	}
	
	public void formatOutput()
	{
		this.textField.setText(this.numberFormat.format(this.newValue) + "%"); //$NON-NLS-1$
	}
	
	public void focusGained(FocusEvent e)
	{
		if (this.getStore() != null)
		{
			if (this.getStore().getDouble(this.getName()) != null)
			{
				this.getStore().getDouble(this.getName());
			}
			else
			{
				
			}
		}
		if (e.widget == this.getTextControl())
		{
			
		}
		
	}
	
}
