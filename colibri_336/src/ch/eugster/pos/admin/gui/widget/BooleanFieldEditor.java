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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * A field editor for a boolean type data.
 */
public class BooleanFieldEditor extends FieldEditor
{
	
	/**
	 * Style constant (value <code>0</code>) indicating the default layout where
	 * the field editor's check box appears to the left of the label.
	 */
	public static final int DEFAULT = 0;
	
	/**
	 * Style constant (value <code>1</code>) indicating a layout where the field
	 * editor's label appears on the left with a check box on the right.
	 */
	public static final int SEPARATE_LABEL = 1;
	
	/**
	 * Style bits. Either <code>DEFAULT</code> or <code>SEPARATE_LABEL</code>.
	 */
	private int style;
	
	/**
	 * The previously selected, or "before", value.
	 */
	private boolean wasSelected;
	
	/**
	 * The default value for this Editor.
	 */
	protected boolean defaultValue = false;
	
	/**
	 * The checkbox control, or <code>null</code> if none.
	 */
	private Button checkBox = null;
	
	/**
	 * Creates a new boolean field editor
	 */
	protected BooleanFieldEditor()
	{
	}
	
	/**
	 * Creates a boolean field editor in the given style.
	 * 
	 * @param name
	 *            the name of the data this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param style
	 *            the style, either <code>DEFAULT</code> or
	 *            <code>SEPARATE_LABEL</code>
	 * @param parent
	 *            the parent of the field editor's control
	 * @see #DEFAULT
	 * @see #SEPARATE_LABEL
	 */
	public BooleanFieldEditor(String name, String labelText, int style, Composite parent)
	{
		this.init(name, labelText);
		this.style = style;
		this.createControl(parent);
	}
	
	/**
	 * Creates a boolean field editor in the default style.
	 * 
	 * @param name
	 *            the name of the data this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public BooleanFieldEditor(String name, String label, Composite parent)
	{
		this(name, label, BooleanFieldEditor.DEFAULT, parent);
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void adjustForNumColumns(int numColumns)
	{
		if (this.style == BooleanFieldEditor.SEPARATE_LABEL) numColumns--;
		((GridData) this.checkBox.getLayoutData()).horizontalSpan = numColumns;
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doFillIntoGrid(Composite parent, int numColumns)
	{
		String text = this.getLabelText();
		switch (this.style)
		{
			case SEPARATE_LABEL:
				this.getLabelControl(parent);
				numColumns--;
				text = null;
			default:
				this.checkBox = this.getChangeControl(parent);
				GridData gd = new GridData();
				gd.horizontalSpan = numColumns;
				this.checkBox.setLayoutData(gd);
				if (text != null) this.checkBox.setText(text);
		}
	}
	
	/**
	 * Sets the defaultValue for this Editor Object
	 * 
	 * @param value
	 *            the default value
	 */
	public void setDefaultValue(boolean value)
	{
		this.defaultValue = value;
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor. Loads the value from the
	 * data store and sets it to the check box.
	 */
	protected void doLoad()
	{
		if (this.checkBox != null)
		{
			boolean value = this.getStore().getBoolean(this.getName()).booleanValue();
			this.checkBox.setSelection(value);
			this.wasSelected = value;
		}
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor. Loads the default value
	 * from the data store and sets it to the check box.
	 */
	protected void doLoadDefault()
	{
		if (this.checkBox != null)
		{
			boolean value = this.getStore().getDefaultBoolean(this.getName()).booleanValue();
			this.checkBox.setSelection(value);
			this.wasSelected = value;
		}
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doStore()
	{
		this.getStore().setValue(this.getName(), new Boolean(this.checkBox.getSelection()));
	}
	
	/**
	 * Returns this field editor's current value.
	 * 
	 * @return the value
	 */
	public boolean getBooleanValue()
	{
		return this.checkBox.getSelection();
	}
	
	/**
	 * Returns the change button for this field editor.
	 * 
	 * @return the change button
	 */
	protected Button getChangeControl(Composite parent)
	{
		if (this.checkBox == null)
		{
			this.checkBox = new Button(parent, SWT.CHECK | SWT.LEFT);
			this.checkBox.setFont(parent.getFont());
			this.checkBox.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					boolean isSelected = BooleanFieldEditor.this.checkBox.getSelection();
					BooleanFieldEditor.this.valueChanged(BooleanFieldEditor.this.wasSelected, isSelected);
					BooleanFieldEditor.this.wasSelected = isSelected;
				}
			});
			this.checkBox.addDisposeListener(new DisposeListener()
			{
				public void widgetDisposed(DisposeEvent event)
				{
					BooleanFieldEditor.this.checkBox = null;
				}
			});
		}
		else
		{
			this.checkParent(this.checkBox, parent);
		}
		return this.checkBox;
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public int getNumberOfControls()
	{
		switch (this.style)
		{
			case SEPARATE_LABEL:
				return 2;
			default:
				return 1;
		}
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public void setFocus()
	{
		if (this.checkBox != null)
		{
			this.checkBox.setFocus();
		}
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public void setLabelText(String text)
	{
		super.setLabelText(text);
		Label label = this.getLabelControl();
		if (label == null && this.checkBox != null)
		{
			this.checkBox.setText(text);
		}
	}
	
	/**
	 * Informs this field editor's listener, if it has one, about a change to
	 * the value (<code>VALUE</code> property) provided that the old and new
	 * values are different.
	 * 
	 * @param oldValue
	 *            the old value
	 * @param newValue
	 *            the new value
	 */
	protected void valueChanged(boolean oldValue, boolean newValue)
	{
		this.setPresentsDefaultValue(false);
		if (oldValue != newValue) this.doStore();
		this.fireStateChanged(FieldEditor.VALUE, oldValue, newValue);
	}
	
	/*
	 * @see FieldEditor.setEnabled
	 */
	public void setEnabled(boolean enabled, Composite parent)
	{
		// Only call super if there is a label already
		if (this.style == BooleanFieldEditor.SEPARATE_LABEL) super.setEnabled(enabled, parent);
		this.getChangeControl(parent).setEnabled(enabled);
	}
	
	protected boolean doCheckState()
	{
		return true;
	}
}