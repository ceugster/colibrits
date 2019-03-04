/*
 * Created on 20.01.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.preference;

import java.io.File;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;

import ch.eugster.pos.util.Path;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ComServerFileFieldEditor extends org.eclipse.jface.preference.FileFieldEditor implements
				IPropertyChangeListener
{
	
	/**
	 * 
	 */
	public ComServerFileFieldEditor()
	{
		super();
	}
	
	/**
	 * @param name
	 * @param labelText
	 * @param parent
	 */
	public ComServerFileFieldEditor(String name, String labelText, Composite parent)
	{
		super(name, labelText, parent);
	}
	
	/**
	 * @param name
	 * @param labelText
	 * @param enforceAbsolute
	 * @param parent
	 */
	public ComServerFileFieldEditor(String name, String labelText, boolean enforceAbsolute, Composite parent)
	{
		super(name, labelText, enforceAbsolute, parent);
	}
	
	public void propertyChange(PropertyChangeEvent e)
	{
		if (e.getNewValue() instanceof Boolean)
		{
			this.setEmptyStringAllowed(!((Boolean) e.getNewValue()).booleanValue());
			this.refreshValidState();
		}
	}
	
	/*
	 * (non-Javadoc) Method declared on StringFieldEditor. Checks whether the
	 * text input field specifies an existing file.
	 */
	protected boolean checkState()
	{
		if (this.isEmptyStringAllowed())
		{
			this.clearErrorMessage();
			return true;
		}
		else
		{
			return super.checkState();
		}
	}
	
	public String changePressed()
	{
		if (this.getTextControl().getText() == null)
		{
			this.getTextControl().setText(Path.getInstance().rootDir);
		}
		else
		{
			File file = new File(this.getTextControl().getText());
			if (!file.exists())
			{
				this.getTextControl().setText(Path.getInstance().rootDir);
			}
		}
		return super.changePressed();
	}
	
}
