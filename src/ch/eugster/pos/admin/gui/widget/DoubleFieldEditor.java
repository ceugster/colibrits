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

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.eclipse.swt.widgets.Composite;

import ch.eugster.pos.Messages;
import ch.eugster.pos.swt.IStore;

/**
 * A field editor for an integer type data.
 */
public class DoubleFieldEditor extends StringFieldEditor
{
	protected double minValidValue = Integer.MIN_VALUE;
	protected double maxValidValue = Integer.MAX_VALUE;
	protected Double defaultValue = IStore.DOUBLE_DEFAULT;
	protected Double currentValue;
	protected static final int DEFAULT_TEXT_LIMIT = 10;
	protected NumberFormat numberFormat = NumberFormat.getNumberInstance();
	
	protected Double oldValue;
	protected Double newValue;
	
	/**
	 * Creates a new integer field editor
	 */
	protected DoubleFieldEditor()
	{
	}
	
	/**
	 * Creates an double field editor.
	 * 
	 * @param name
	 *            the name of the data this field editor works on
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
	 *            the name of the data this field editor works on
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
		this.textField = this.getTextControl();
		this.setTextLimit(textLimit);
		this.setEmptyStringAllowed(false);
		this.setErrorMessage(Messages.getString("DoubleFieldEditor.Ung_u00FCltiges_Zahlenformat._1")); //$NON-NLS-1$
		this.createControl(parent);
		this.numberFormat.setMinimumFractionDigits(NumberFormat.getCurrencyInstance().getMinimumFractionDigits());
		this.numberFormat.setMaximumFractionDigits(NumberFormat.getCurrencyInstance().getMaximumFractionDigits());
		this.numberFormat.setGroupingUsed(false);
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
	
	public void setFractionDigits(int min, int max)
	{
		this.numberFormat.setMinimumFractionDigits(min);
		this.numberFormat.setMaximumFractionDigits(max);
	}
	
	public void setNumberFormat(String format)
	{
		this.numberFormat = new DecimalFormat(format);
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
			this.newValue = Double.valueOf(numberString);
			if (this.newValue.doubleValue() < this.minValidValue || this.newValue.doubleValue() > this.maxValidValue)
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
	
	protected Double parse(String text)
	{
		try
		{
			return new Double(Double.parseDouble(text));
		}
		catch (NumberFormatException e)
		{
			return this.newValue;
		}
	}
	
	/**
	 * Formats the value in the textField
	 * 
	 */
	public void formatOutput()
	{
		if (this.textField != null)
		{
			this.textField.setText(this.numberFormat.format(this.newValue.doubleValue()));
		}
	}
	
	/**
	 * Sets the defaultValue for this Editor Object
	 * 
	 * @param value
	 *            the default value
	 */
	public void setDefaultValue(Double value)
	{
		this.defaultValue = value;
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoad()
	{
		this.oldValue = this.newValue;
		this.newValue = this.getStore().getDouble(this.getName());
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
			this.newValue = this.getStore().getDefaultDouble(this.getName());
			this.formatOutput();
			this.valueChanged();
		}
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doStore()
	{
		this.getStore().setValue(this.getName(), this.parse(this.textField.getText()));
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
