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
public class IntegerFieldEditor extends StringFieldEditor
{
	private int minValidValue = 0;
	private int maxValidValue = Integer.MAX_VALUE;
	protected Integer defaultValue = IStore.INT_DEFAULT;
	private static final int DEFAULT_TEXT_LIMIT = 10;
	
	protected Integer oldValue;
	private Integer newValue;
	
	/**
	 * Creates a new integer field editor
	 */
	protected IntegerFieldEditor()
	{
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
	 */
	public IntegerFieldEditor(String name, String labelText, Composite parent)
	{
		this(name, labelText, parent, IntegerFieldEditor.DEFAULT_TEXT_LIMIT);
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
	public IntegerFieldEditor(String name, String labelText, Composite parent, int textLimit)
	{
		this.init(name, labelText);
		this.setTextLimit(textLimit);
		this.setEmptyStringAllowed(false);
		this.setErrorMessage("Unültiges Zahlenformat: Ganzzahl erforderlich.");//$NON-NLS-1$
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
			this.newValue = Integer.valueOf(numberString);
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
	public void setDefaultValue(Integer value)
	{
		this.defaultValue = value;
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoad()
	{
		this.oldValue = this.newValue;
		this.newValue = this.getStore().getInt(this.getName());
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
			this.newValue = this.getStore().getDefaultInt(this.getName());
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
			Integer i = null;
			try
			{
				i = new Integer(text.getText());
			}
			catch (NumberFormatException e)
			{
				i = new Integer(0);
			}
			this.getStore().setValue(this.getName(), i);
		}
	}
	
	public void formatOutput()
	{
		if (this.textField != null)
		{
			this.textField.setText(NumberFormat.getIntegerInstance().format(this.newValue));
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
	public int getIntValue() throws NumberFormatException
	{
		return new Integer(this.getStringValue()).intValue();
	}
}
