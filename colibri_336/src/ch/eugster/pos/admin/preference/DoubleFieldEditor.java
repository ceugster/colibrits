/*
 * Created on 17.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.preference;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class DoubleFieldEditor extends StringFieldEditor
{
	private double minValidValue = 0;
	private double maxValidValue = Double.MAX_VALUE;
	private static final int DEFAULT_TEXT_LIMIT = 10;
	
	/**
	 * 
	 */
	protected DoubleFieldEditor()
	{
		super();
	}
	
	/**
	 * Creates an integer field editor.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public DoubleFieldEditor(String name, String labelText, Composite parent)
	{
		this(name, labelText, parent, DoubleFieldEditor.DEFAULT_TEXT_LIMIT);
	}
	
	/**
	 * Creates an integer field editor.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 * @param textLimit
	 *            the maximum number of characters in the text.
	 */
	public DoubleFieldEditor(String name, String labelText, Composite parent, int textLimit)
	{
		this.init(name, labelText);
		this.setTextLimit(textLimit);
		this.setEmptyStringAllowed(false);
		this.setErrorMessage("Die Eingabe ist ungültig");//$NON-NLS-1$
		this.createControl(parent);
	}
	
	/**
	 * Sets the range of valid values for this field.
	 * 
	 * @param min
	 *            the minimum allowed value (inclusive)
	 * @param max
	 *            the maximum allowed value (inclusive)
	 */
	public void setValidRange(double min, double max)
	{
		this.minValidValue = min;
		this.maxValidValue = max;
	}
	
	/*
	 * (non-Javadoc) Method declared on StringFieldEditor. Checks whether the
	 * entered String is a valid integer or not.
	 */
	protected boolean checkState()
	{
		
		Text text = this.getTextControl();
		
		if (text == null) return false;
		
		String numberString = text.getText();
		try
		{
			double number = Double.valueOf(numberString).doubleValue();
			if (number >= this.minValidValue && number <= this.maxValidValue)
			{
				this.clearErrorMessage();
				return true;
			}
			else
			{
				this.showErrorMessage();
				return false;
			}
		}
		catch (NumberFormatException e1)
		{
			this.showErrorMessage();
		}
		
		return false;
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoad()
	{
		Text text = this.getTextControl();
		if (text != null)
		{
			double value = this.getPreferenceStore().getDouble(this.getPreferenceName());
			text.setText("" + value);//$NON-NLS-1$
		}
		
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoadDefault()
	{
		Text text = this.getTextControl();
		if (text != null)
		{
			double value = this.getPreferenceStore().getDefaultDouble(this.getPreferenceName());
			text.setText("" + value);//$NON-NLS-1$
		}
		this.valueChanged();
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doStore()
	{
		Text text = this.getTextControl();
		if (text != null)
		{
			Double d = new Double(text.getText());
			this.getPreferenceStore().setValue(this.getPreferenceName(), d.doubleValue());
		}
	}
	
	/**
	 * Returns this field editor's current value as an integer.
	 * 
	 * @return the value
	 * @exception NumberFormatException
	 *                if the <code>String</code> does not contain a parsable
	 *                integer
	 */
	public double getDoubleValue() throws NumberFormatException
	{
		return new Double(this.getStringValue()).doubleValue();
	}
}
