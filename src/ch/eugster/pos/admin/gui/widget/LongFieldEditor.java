/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package ch.eugster.pos.admin.gui.widget;

import java.text.NumberFormat;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import ch.eugster.pos.swt.IStore;

/**
 * A field editor for an integer type data.
 */
public class LongFieldEditor extends StringFieldEditor
{
	private long minValidValue = 0l;
	private long maxValidValue = Long.MAX_VALUE;
	protected Long defaultValue = IStore.LONG_DEFAULT;
	private static final int DEFAULT_TEXT_LIMIT = 10;
	
	protected Long oldValue;
	private Long newValue;
	
	/**
	 * Creates an integer field editor.
	 * 
	 * @param name
	 *            the name of the data this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public LongFieldEditor(String name, String labelText, Composite parent)
	{
		this(name, labelText, LongFieldEditor.DEFAULT_TEXT_LIMIT, parent);
	}
	
	/**
	 * Creates an integer field editor.
	 * 
	 * @param name
	 *            the name of the data this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 * @param textLimit
	 *            the maximum number of characters in the text.
	 */
	public LongFieldEditor(String name, String labelText, int textLimit, Composite parent)
	{
		this.init(name, labelText);
		this.setTextLimit(textLimit);
		this.setEmptyStringAllowed(false);
		this.setErrorMessage("Ungültiges Zahlenformat: Ganzzahl erforderlich.");//$NON-NLS-1$
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
	public void setValidRange(int min, int max)
	{
		this.minValidValue = min;
		this.maxValidValue = max;
	}
	
	protected boolean doCheckState()
	{
		if (super.doCheckState() == false)
		{
			return false;
		}
		
		String numberString = this.getTextControl().getText();
		try
		{
			this.newValue = Long.valueOf(numberString);
			if (this.newValue.intValue() < this.minValidValue || this.newValue.intValue() > this.maxValidValue)
			{
				this.showErrorMessage();
				return false;
			}
		}
		catch (NumberFormatException e1)
		{
			this.showErrorMessage();
			return false;
		}
		
		this.clearErrorMessage();
		return true;
	}
	
	/**
	 * Sets the defaultValue for this Editor Object
	 * 
	 * @param value
	 *            the default value
	 */
	public void setDefaultValue(Long value)
	{
		this.defaultValue = value;
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoad()
	{
		this.oldValue = this.newValue;
		this.newValue = this.getStore().getLong(this.getName());
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
			this.newValue = this.getStore().getDefaultLong(this.getName());
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
			Long i = null;
			try
			{
				i = new Long(text.getText());
			}
			catch (NumberFormatException e)
			{
				i = new Long(0);
			}
			this.getStore().setValue(this.getName(), i);
		}
	}
	
	public void formatOutput()
	{
		if (this.textField != null)
		{
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setMaximumFractionDigits(0);
			nf.setMinimumFractionDigits(0);
			nf.setGroupingUsed(false);
			this.textField.setText(nf.format(this.newValue));
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
	public long getLongValue() throws NumberFormatException
	{
		return new Long(this.getStringValue()).longValue();
	}
}
