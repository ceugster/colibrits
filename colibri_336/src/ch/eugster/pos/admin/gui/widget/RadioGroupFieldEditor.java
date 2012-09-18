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

import org.eclipse.jface.util.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import ch.eugster.pos.swt.Store;

/**
 * A field editor for an enumerationeration type data. The choices are presented
 * as a list of radio buttons.
 */
public class RadioGroupFieldEditor extends FieldEditor
{
	
	/**
	 * List of radio button entries of the form [label,value].
	 */
	private String[][] labelsAndValues;
	
	/**
	 * Number of columns into which to arrange the radio buttons.
	 */
	private int numColumns;
	
	/**
	 * Indent used for the first column of the radion button matrix.
	 */
	private int indent = FieldEditor.HORIZONTAL_GAP;
	
	/**
	 * The current value, or <code>null</code> if none.
	 */
	private Integer value;
	
	/**
	 * The box of radio buttons, or <code>null</code> if none (before creation
	 * and after disposal).
	 */
	private Composite radioBox;
	
	/**
	 * The radio buttons, or <code>null</code> if none (before creation and
	 * after disposal).
	 */
	private Button[] radioButtons;
	
	/**
	 * Whether to use a Group control.
	 */
	private boolean useGroup;
	
	/**
	 * Creates a new radio group field editor
	 */
	protected RadioGroupFieldEditor()
	{
	}
	
	/**
	 * Creates a radio group field editor. This constructor does not use a
	 * <code>Group</code> to contain the radio buttons. It is equivalent to
	 * using the following constructor with <code>false</code> for the
	 * <code>useGroup</code> argument.
	 * <p>
	 * Example usage:
	 * 
	 * <pre>
	 * RadioGroupFieldEditor editor = new RadioGroupFieldEditor(&quot;GeneralPage.DoubleClick&quot;, resName, 1, new String[][]
	 * {
	 * { &quot;Open Browser&quot;, &quot;open&quot; },
	 * { &quot;Expand Tree&quot;, &quot;expand&quot; } }, parent);
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param name
	 *            the name of the data this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param numColumns
	 *            the number of columns for the radio button presentation
	 * @param labelAndValues
	 *            list of radio button [label, value] entries; the value is
	 *            returned when the radio button is selected
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public RadioGroupFieldEditor(String name, String labelText, int numColumns, String[][] labelAndValues,
					Composite parent)
	{
		this(name, labelText, numColumns, labelAndValues, parent, false);
	}
	
	/**
	 * Creates a radio group field editor.
	 * <p>
	 * Example usage:
	 * 
	 * <pre>
	 * RadioGroupFieldEditor editor = new RadioGroupFieldEditor(&quot;GeneralPage.DoubleClick&quot;, resName, 1, new String[][]
	 * {
	 * { &quot;Open Browser&quot;, &quot;open&quot; },
	 * { &quot;Expand Tree&quot;, &quot;expand&quot; } }, parent, true);
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param name
	 *            the name of the data this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param numColumns
	 *            the number of columns for the radio button presentation
	 * @param labelAndValues
	 *            list of radio button [label, value] entries; the value is
	 *            returned when the radio button is selected
	 * @param parent
	 *            the parent of the field editor's control
	 * @param useGroup
	 *            whether to use a Group control to contain the radio buttons
	 */
	public RadioGroupFieldEditor(String name, String labelText, int numColumns, String[][] labelAndValues,
					Composite parent, boolean useGroup)
	{
		this.init(name, labelText);
		Assert.isTrue(this.checkArray(labelAndValues));
		this.labelsAndValues = labelAndValues;
		this.numColumns = numColumns;
		this.useGroup = useGroup;
		this.createControl(parent);
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void adjustForNumColumns(int numColumns)
	{
		Control control = this.getLabelControl();
		if (control != null)
		{
			((GridData) control.getLayoutData()).horizontalSpan = numColumns;
		}
		((GridData) this.radioBox.getLayoutData()).horizontalSpan = numColumns;
	}
	
	/**
	 * Checks whether given <code>String[][]</code> is of "type"
	 * <code>String[][2]</code>.
	 * 
	 * @return <code>true</code> if it is ok, and <code>false</code> otherwise
	 */
	private boolean checkArray(String[][] table)
	{
		if (table == null) return false;
		for (int i = 0; i < table.length; i++)
		{
			String[] array = table[i];
			if (array == null || array.length != 2) return false;
		}
		return true;
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doFillIntoGrid(Composite parent, int numColumns)
	{
		if (this.useGroup)
		{
			Control control = this.getRadioBoxControl(parent);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			control.setLayoutData(gd);
		}
		else
		{
			Control control = this.getLabelControl(parent);
			GridData gd = new GridData();
			gd.horizontalSpan = numColumns;
			control.setLayoutData(gd);
			control = this.getRadioBoxControl(parent);
			gd = new GridData();
			gd.horizontalSpan = numColumns;
			gd.horizontalIndent = this.indent;
			control.setLayoutData(gd);
		}
		
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoad()
	{
		if (this.radioBox != null && this.getStore() != null)
		{
			Integer val = this.getStore().getInt(this.getName());
			if (val == null)
			{
				val = Store.INT_DEFAULT;
			}
			this.radioBox.setData(val);
			this.updateValue(val);
		}
		this.valueChanged();
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoadDefault()
	{
		if (this.radioBox != null && this.getStore() != null)
		{
			Integer val = this.getStore().getDefaultInt(this.getName());
			if (val == null)
			{
				val = Store.INT_DEFAULT;
			}
			this.radioBox.setData(val);
			this.updateValue(val);
		}
		this.valueChanged();
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doStore()
	{
		if (this.value == null)
		{
			this.getStore().setToDefault(this.getName());
			return;
		}
		
		this.getStore().setValue(this.getName(), this.value);
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public int getNumberOfControls()
	{
		return 1;
	}
	
	/**
	 * Returns this field editor's radio group control.
	 * 
	 * @return the radio group control
	 */
	public Composite getRadioBoxControl(Composite parent)
	{
		if (this.radioBox == null)
		{
			
			Font font = parent.getFont();
			
			if (this.useGroup)
			{
				Group group = new Group(parent, SWT.NONE);
				group.setFont(font);
				String text = this.getLabelText();
				if (text != null) group.setText(text);
				this.radioBox = group;
				GridLayout layout = new GridLayout();
				layout.horizontalSpacing = FieldEditor.HORIZONTAL_GAP;
				layout.numColumns = this.numColumns;
				this.radioBox.setLayout(layout);
			}
			else
			{
				this.radioBox = new Composite(parent, SWT.NONE);
				GridLayout layout = new GridLayout();
				layout.marginWidth = 0;
				layout.marginHeight = 0;
				layout.horizontalSpacing = FieldEditor.HORIZONTAL_GAP;
				layout.numColumns = this.numColumns;
				this.radioBox.setLayout(layout);
				this.radioBox.setFont(font);
			}
			
			this.radioButtons = new Button[this.labelsAndValues.length];
			for (int i = 0; i < this.labelsAndValues.length; i++)
			{
				Button radio = new Button(this.radioBox, SWT.RADIO | SWT.LEFT);
				this.radioButtons[i] = radio;
				String[] labelAndValue = this.labelsAndValues[i];
				radio.setText(labelAndValue[0]);
				radio.setData(new Integer(labelAndValue[1]));
				radio.setFont(font);
				radio.addSelectionListener(new SelectionAdapter()
				{
					public void widgetSelected(SelectionEvent event)
					{
						RadioGroupFieldEditor.this.radioBox.setData(((Button) event.getSource()).getData());
						RadioGroupFieldEditor.this.valueChanged();
					}
				});
			}
			this.radioBox.addDisposeListener(new DisposeListener()
			{
				public void widgetDisposed(DisposeEvent event)
				{
					RadioGroupFieldEditor.this.radioBox = null;
					RadioGroupFieldEditor.this.radioButtons = null;
				}
			});
		}
		else
		{
			this.checkParent(this.radioBox, parent);
		}
		return this.radioBox;
	}
	
	protected void valueChanged()
	{
		this.setPresentsDefaultValue(false);
		
		boolean oldState = this.isValid();
		this.refreshValidState();
		
		if (this.isValid() != oldState) this.fireStateChanged(FieldEditor.IS_VALID, oldState, this.isValid());
		
		Integer oldValue = this.value;
		this.value = (Integer) this.radioBox.getData();
		
		if (!this.value.equals(oldValue))
		{
			this.doStore();
			oldValue = this.value;
			this.fireValueChanged(this.getName(), oldValue, this.value);
		}
	}
	
	/**
	 * Sets the indent used for the first column of the radion button matrix.
	 * 
	 * @param indent
	 *            the indent (in pixels)
	 */
	public void setIndent(int indent)
	{
		if (indent < 0)
			this.indent = 0;
		else
			this.indent = indent;
	}
	
	/**
	 * Select the radio button that conforms to the given value.
	 * 
	 * @param selectedValue
	 *            the selected value
	 */
	private void updateValue(Integer selectedValue)
	{
		this.value = selectedValue;
		if (this.radioButtons == null) return;
		
		if (this.value != null)
		{
			boolean found = false;
			Integer val = (Integer) this.radioBox.getData();
			for (int i = 0; i < this.radioButtons.length; i++)
			{
				this.radioButtons[i].setSelection(val.intValue() == i);
				if (val.intValue() == i)
				{
					found = true;
				}
			}
			if (found) return;
		}
		
		// We weren't able to find the value. So we select the first
		// radio button as a default.
		if (this.radioButtons.length > 0)
		{
			this.radioButtons[0].setSelection(true);
			this.value = new Integer((String) this.radioButtons[0].getData());
		}
		return;
	}
	
	/*
	 * @see FieldEditor.setEnabled(boolean,Composite).
	 */
	public void setEnabled(boolean enabled, Composite parent)
	{
		super.setEnabled(enabled, parent);
		for (int i = 0; i < this.radioButtons.length; i++)
		{
			this.radioButtons[i].setEnabled(enabled);
		}
	}
	
	/**
	 * Refreshes this field editor's valid state after a value change and fires
	 * an <code>IS_VALID</code> property change event if warranted.
	 * <p>
	 * 
	 * @see #isValid
	 */
	protected void refreshValidState()
	{
		this.setValid(true);
	}
	
	protected boolean doCheckState()
	{
		return this.isValid();
	}
	
}
