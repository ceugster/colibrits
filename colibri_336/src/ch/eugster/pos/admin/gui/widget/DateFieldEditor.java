/*
 * Created on 03.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.gui.widget;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import ch.eugster.pos.Messages;
import ch.eugster.pos.swt.IStore;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class DateFieldEditor extends StringFieldEditor
{
	protected Date minValidValue;
	protected Date maxValidValue;
	private Date defaultValue = new Date();
	private Date oldValue;
	private Date newValue = this.defaultValue;
	
	/**
	 * 
	 */
	public DateFieldEditor()
	{
		super();
		this.init();
	}
	
	/**
	 * @param name
	 * @param labelText
	 * @param width
	 * @param strategy
	 * @param parent
	 */
	public DateFieldEditor(String name, String labelText, int width, int strategy, Composite parent)
	{
		super(name, labelText, width, strategy, parent);
	}
	
	/**
	 * @param name
	 * @param labelText
	 * @param width
	 * @param parent
	 */
	public DateFieldEditor(String name, String labelText, int width, Composite parent)
	{
		super(name, labelText, width, parent);
	}
	
	/**
	 * @param name
	 * @param labelText
	 * @param parent
	 */
	public DateFieldEditor(String name, String labelText, Composite parent)
	{
		super(name, labelText, parent);
	}
	
	private void init()
	{
		Calendar c1 = new GregorianCalendar(1900, 1, 1);
		this.minValidValue = c1.getTime();
		
		c1.set(Calendar.YEAR, 2100);
		c1.set(Calendar.MONTH, 12);
		c1.set(Calendar.DAY_OF_MONTH, 31);
		this.maxValidValue = c1.getTime();
	}
	
	/**
	 * Sets the range of valid values for this field.
	 * 
	 * @param min
	 *            the minimum allowed value (inclusive)
	 * @param max
	 *            the maximum allowed value (inclusive)
	 */
	public void setValidRange(Date min, Date max)
	{
		this.minValidValue = min;
		this.maxValidValue = max;
	}
	
	protected boolean doCheckState()
	{
		boolean result = true;
		try
		{
			DateFormat.getDateInstance(DateFormat.MEDIUM).parse(this.textField.getText());
		}
		catch (ParseException e)
		{
			this.setErrorMessage(Messages.getString("DateFieldEditor.Das_eingegebene_Datum_ist_ung_u00FCltig._1")); //$NON-NLS-1$
			result = false;
		}
		return result;
	}
	
	/**
	 * Sets the defaultValue for this Editor Object
	 * 
	 * @param value
	 *            the default value
	 */
	public void setDefaultValue(Date value)
	{
		this.defaultValue = value;
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoad()
	{
		this.oldValue = this.newValue;
		if (this.getStore().getDate(this.getName()) != null)
		{
			this.newValue = this.getStore().getDate(this.getName());
		}
		else
		{
			this.newValue = IStore.DATE_DEFAULT;
		}
		this.formatOutput();
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoadDefault()
	{
		if (this.textField != null && this.getStore() != null)
		{
			this.oldValue = this.newValue;
			if (this.getStore().getDefaultDate(this.getName()) != null)
			{
				this.newValue = this.getStore().getDefaultDate(this.getName());
			}
			else
			{
				this.newValue = IStore.DATE_DEFAULT;
			}
			this.formatOutput();
			this.valueChanged();
		}
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doStore()
	{
		Text text = this.getTextControl();
		if (text != null)
		{
			this.getStore().setValue(this.getName(), this.toDate(this.textField.getText()));
		}
	}
	
	protected Date toDate(String s)
	{
		Date result = null;
		try
		{
			result = DateFormat.getDateInstance().parse(this.textField.getText());
		}
		catch (ParseException e)
		{
			result = new Date();
		}
		return result;
	}
	
	/**
	 * Formats the value in the textField
	 * 
	 */
	public void formatOutput()
	{
		this.textField.setText(SimpleDateFormat.getDateInstance(DateFormat.MEDIUM).format(this.newValue));
	}
	
	/**
	 * Informs this field editor's listener, if it has one, about a change to
	 * the value (<code>VALUE</code> property) provided that the old and new
	 * values are different.
	 * <p>
	 * This hook is <em>not</em> called when the text is initialized (or reset
	 * to the default value) from the data store.
	 * </p>
	 */
	protected void valueChanged()
	{
		this.setPresentsDefaultValue(false);
		boolean oldState = this.isValid();
		this.refreshValidState();
		
		if (this.isValid() != oldState) this.fireStateChanged(FieldEditor.IS_VALID, oldState, this.isValid());
		
		this.oldValue = this.newValue;
		this.newValue = this.toDate(this.textField.getText());
		if (!this.newValue.equals(this.oldValue))
		{
			this.doStore();
			this.oldValue = this.newValue;
		}
	}
	
}
