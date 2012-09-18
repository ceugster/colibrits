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
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import ch.eugster.pos.Messages;
import ch.eugster.pos.swt.IStore;
import ch.eugster.pos.swt.Store;

/**
 * A field editor for a string type data.
 * <p>
 * This class may be used as is, or subclassed as required.
 * </p>
 */
public class StringFieldEditor extends FieldEditor implements FocusListener
{
	
	/**
	 * Validation strategy constant (value <code>0</code>) indicating that the
	 * editor should perform validation after every key stroke.
	 * 
	 * @see #setValidateStrategy
	 */
	public static final int VALIDATE_ON_KEY_STROKE = 0;
	
	/**
	 * Validation strategy constant (value <code>1</code>) indicating that the
	 * editor should perform validation only when the text widget loses focus.
	 * 
	 * @see #setValidateStrategy
	 */
	public static final int VALIDATE_ON_FOCUS_LOST = 1;
	
	/**
	 * Text limit constant (value <code>-1</code>) indicating unlimited text
	 * limit and width.
	 */
	public static int UNLIMITED = -1;
	
	/**
	 * How to set the Cursor when the textControl gaines the Focus
	 */
	protected static final int SET_CURSOR_LEFT = 0;
	protected static final int SET_CURSOR_RIGHT = 1;
	protected static final int SELECT_ALL_CURSOR = 2;
	
	protected int setCursorAfterGainingFocus = StringFieldEditor.SELECT_ALL_CURSOR;
	
	/**
	 * Old text value.
	 */
	private String oldValue;
	
	/**
	 * default text value.
	 */
	protected String defaultValue = IStore.STRING_DEFAULT;
	
	/**
	 * The text field, or <code>null</code> if none.
	 */
	protected Text textField;
	
	/**
	 * Width of text field in characters; initially unlimited.
	 */
	protected int widthInChars = StringFieldEditor.UNLIMITED;
	
	/**
	 * Text limit of text field in characters; initially unlimited.
	 */
	protected int textLimit = StringFieldEditor.UNLIMITED;
	
	/**
	 * Indicates whether the empty string is legal; <code>true</code> by
	 * default.
	 */
	private boolean emptyStringAllowed = true;
	
	/**
	 * Convert to Capitals; <code>false</code> by default.
	 */
	private boolean capitalizationOn = false;
	
	/**
	 * The validation strategy; <code>VALIDATE_ON_KEY_STROKE</code> by default.
	 */
	protected int validateStrategy = StringFieldEditor.VALIDATE_ON_KEY_STROKE;
	
	/**
	 * Creates a new string field editor
	 */
	protected StringFieldEditor()
	{
	}
	
	/**
	 * Creates a string field editor. Use the method <code>setTextLimit</code>
	 * to limit the text.
	 * 
	 * @param name
	 *            the name of the data this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param width
	 *            the width of the text input field in characters, or
	 *            <code>UNLIMITED</code> for no limit
	 * @param strategy
	 *            either <code>VALIDATE_ON_KEY_STROKE</code> to perform on the
	 *            fly checking (the default), or
	 *            <code>VALIDATE_ON_FOCUS_LOST</code> to perform validation only
	 *            after the text has been typed in
	 * @param parent
	 *            the parent of the field editor's control
	 * @since 2.0
	 */
	public StringFieldEditor(String name, String labelText, int width, int strategy, Composite parent)
	{
		this.init(name, labelText);
		this.widthInChars = width;
		this.setValidateStrategy(strategy);
		this.setValid(false);
		this.setErrorMessage("");//$NON-NLS-1$
		this.createControl(parent);
	}
	
	/**
	 * Creates a string field editor. Use the method <code>setTextLimit</code>
	 * to limit the text.
	 * 
	 * @param name
	 *            the name of the data this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param width
	 *            the width of the text input field in characters, or
	 *            <code>UNLIMITED</code> for no limit
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public StringFieldEditor(String name, String labelText, int width, Composite parent)
	{
		this(name, labelText, width, StringFieldEditor.VALIDATE_ON_KEY_STROKE, parent);
	}
	
	/**
	 * Creates a string field editor of unlimited width. Use the method
	 * <code>setTextLimit</code> to limit the text.
	 * 
	 * @param name
	 *            the name of the data this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public StringFieldEditor(String name, String labelText, Composite parent)
	{
		this(name, labelText, StringFieldEditor.UNLIMITED, parent);
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void adjustForNumColumns(int numColumns)
	{
		GridData gd = (GridData) this.textField.getLayoutData();
		gd.horizontalSpan = numColumns - 1;
		// We only grab excess space if we have to
		// If another field editor has more columns then
		// we assume it is setting the width.
		gd.grabExcessHorizontalSpace = gd.horizontalSpan == 1;
	}
	
	/**
	 * Checks whether the text input field contains a valid value or not.
	 * 
	 * @return <code>true</code> if the field value is valid, and
	 *         <code>false</code> if invalid
	 */
	protected boolean checkState()
	{
		boolean result = true;
		if (this.textField == null) return false;
		
		String txt = this.textField.getText();
		
		if (txt.trim().length() == 0 && !this.emptyStringAllowed)
		{
			this.setErrorMessage(Messages.getString("StringFieldEditor.Das_Feld_darf_nicht_leer_sein._1")); //$NON-NLS-1$
			this.showErrorMessage();
			return false;
		}
		
		// call hook for subclasses
		result = result && this.validate();
		
		if (result)
			this.clearErrorMessage();
		else
			this.showErrorMessage();
		
		return result;
	}
	
	/**
	 * Hook for subclasses to do specific state checks.
	 * <p>
	 * The default implementation of this framework method does nothing and
	 * returns <code>true</code>. Subclasses should override this method to
	 * specific state checks.
	 * </p>
	 * 
	 * @return <code>true</code> if the field value is valid, and
	 *         <code>false</code> if invalid
	 */
	protected boolean doCheckState()
	{
		return true;
	}
	
	/**
	 * Fills this field editor's basic controls into the given parent.
	 * <p>
	 * The string field implementation of this <code>FieldEditor</code>
	 * framework method contributes the text field. Subclasses may override but
	 * must call <code>super.doFillIntoGrid</code>.
	 * </p>
	 */
	protected void doFillIntoGrid(Composite parent, int numColumns)
	{
		this.getLabelControl(parent);
		
		this.textField = this.getTextControl(parent);
		GridData gd = new GridData();
		gd.horizontalSpan = numColumns - 1;
		if (this.widthInChars != StringFieldEditor.UNLIMITED)
		{
			GC gc = new GC(this.textField);
			try
			{
				Point extent = gc.textExtent("X");//$NON-NLS-1$
				gd.widthHint = this.widthInChars * extent.x;
			}
			finally
			{
				gc.dispose();
			}
		}
		else
		{
			gd.horizontalAlignment = GridData.FILL;
			gd.grabExcessHorizontalSpace = true;
		}
		this.textField.setLayoutData(gd);
		this.textField.addFocusListener(this);
	}
	
	public void setCursorAfterControlGainedFocus(int value)
	{
		this.setCursorAfterGainingFocus = value;
	}
	
	public void focusGained(FocusEvent e)
	{
		switch (this.setCursorAfterGainingFocus)
		{
			case SET_CURSOR_LEFT:
			{
				this.getTextControl().setSelection(0, 0);
				return;
			}
			case SET_CURSOR_RIGHT:
			{
				this.getTextControl().setSelection(this.getTextControl().getText().length(),
								this.getTextControl().getText().length());
				return;
			}
			case SELECT_ALL_CURSOR:
			{
				this.getTextControl().setSelection(0, this.getTextControl().getText().length());
				return;
			}
		}
		this.getTextControl().setSelection(0, this.getTextControl().getText().length());
	}
	
	public void focusLost(FocusEvent e)
	{
		
	}
	
	/**
	 * Sets the defaultValue for this Editor Object
	 * 
	 * @param value
	 *            the default value
	 */
	public void setDefaultValue(String value)
	{
		this.defaultValue = value;
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoad()
	{
		if (this.textField != null)
		{
			String value = this.getStore().getString(this.getName());
			this.textField.setText(value);
			this.oldValue = value;
		}
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoadDefault()
	{
		if (this.textField != null && this.getStore() != null)
		{
			this.textField.setText(this.getStore().getDefaultString(this.getName()) != null ? this.getStore()
							.getDefaultString(this.getName()) : Store.STRING_DEFAULT);
		}
		this.valueChanged();
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doStore()
	{
		this.getStore().setValue(this.getName(), this.textField.getText());
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public int getNumberOfControls()
	{
		return 2;
	}
	
	/**
	 * Returns the field editor's value.
	 * 
	 * @return the current value
	 */
	public String getStringValue()
	{
		if (this.textField != null)
			return this.textField.getText();
		else
			return this.getStore().getString(this.getName());
	}
	
	/**
	 * Returns this field editor's text control.
	 * 
	 * @param parent
	 *            the parent
	 * @return the text control, or <code>null</code> if no text field is
	 *         created yet
	 */
	public Text getTextControl()
	{
		return this.textField;
	}
	
	/**
	 * Returns this field editor's text control.
	 * <p>
	 * The control is created if it does not yet exist
	 * </p>
	 * 
	 * @param parent
	 *            the parent
	 * @return the text control
	 */
	public Text getTextControl(Composite parent)
	{
		if (this.textField == null)
		{
			this.textField = new Text(parent, SWT.SINGLE | SWT.BORDER);
			this.textField.setFont(parent.getFont());
			switch (this.validateStrategy)
			{
				case VALIDATE_ON_KEY_STROKE:
					this.textField.addKeyListener(new KeyAdapter()
					{
						public void keyReleased(KeyEvent e)
						{
							StringFieldEditor.this.valueChanged();
						}
					});
					
					this.textField.addFocusListener(new FocusAdapter()
					{
						public void focusGained(FocusEvent e)
						{
							StringFieldEditor.this.refreshValidState();
						}
						
						public void focusLost(FocusEvent e)
						{
							if (!StringFieldEditor.this.isValid())
							{
								StringFieldEditor.this.setFocus();
							}
							else
							{
								StringFieldEditor.this.formatOutput();
								StringFieldEditor.this.clearErrorMessage();
							}
						}
					});
					break;
				case VALIDATE_ON_FOCUS_LOST:
					this.textField.addKeyListener(new KeyAdapter()
					{
						public void keyPressed(KeyEvent e)
						{
							StringFieldEditor.this.clearErrorMessage();
						}
					});
					this.textField.addFocusListener(new FocusAdapter()
					{
						public void focusGained(FocusEvent e)
						{
							StringFieldEditor.this.refreshValidState();
						}
						
						public void focusLost(FocusEvent e)
						{
							StringFieldEditor.this.valueChanged();
							if (!StringFieldEditor.this.isValid())
							{
								StringFieldEditor.this.setFocus();
							}
							else
							{
								StringFieldEditor.this.formatOutput();
								StringFieldEditor.this.clearErrorMessage();
							}
						}
					});
					break;
				default:
					Assert.isTrue(false, "Unknown validate strategy");//$NON-NLS-1$
			}
			this.textField.addDisposeListener(new DisposeListener()
			{
				public void widgetDisposed(DisposeEvent event)
				{
					StringFieldEditor.this.textField = null;
				}
			});
			if (this.textLimit > 0)
			{// Only set limits above 0 - see SWT spec
				this.textField.setTextLimit(this.textLimit);
			}
		}
		else
		{
			this.checkParent(this.textField, parent);
		}
		return this.textField;
	}
	
	/**
	 * Returns whether an empty string is a valid value.
	 * 
	 * @return <code>true</code> if an empty string is a valid value, and
	 *         <code>false</code> if an empty string is invalid
	 * @see #setEmptyStringAllowed
	 */
	public boolean isEmptyStringAllowed()
	{
		return this.emptyStringAllowed;
	}
	
	/**
	 * Returns whether letters are capitalized.
	 * 
	 * @return <code>true</code> if letters are capitalized, and
	 *         <code>false</code> if letters are leaved as the are typed in
	 * @see #setToCapitales
	 */
	public boolean isCapitalizationOn()
	{
		return this.capitalizationOn;
	}
	
	/**
	 * Formats the value in the textField
	 * 
	 */
	public void formatOutput()
	{
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void refreshValidState()
	{
		this.setValid(this.checkState());
	}
	
	/**
	 * Sets whether the typed letters are to be capitalized.
	 * 
	 * @param b
	 *            <code>true</code> if typed letters are to be capitalized, and
	 *            <code>false</code> if not.
	 */
	public void setEmptyStringAllowed(boolean b)
	{
		this.emptyStringAllowed = b;
	}
	
	/**
	 * Sets whether the typed letters are capitalized.
	 * 
	 * @param b
	 *            <code>true</code> if the empty string is allowed, and
	 *            <code>false</code> if it is considered invalid
	 */
	public void setCapitalizationOn(boolean b)
	{
		this.capitalizationOn = b;
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public void setFocus()
	{
		if (this.textField != null)
		{
			this.textField.setFocus();
		}
	}
	
	/**
	 * Sets this field editor's value.
	 * 
	 * @param value
	 *            the new value, or <code>null</code> meaning the empty string
	 */
	public void setStringValue(String value)
	{
		if (this.textField != null)
		{
			if (value == null) value = "";//$NON-NLS-1$
			this.oldValue = this.textField.getText();
			if (!this.oldValue.equals(value))
			{
				this.textField.setText(value);
				this.valueChanged();
			}
		}
	}
	
	/**
	 * Sets this text field's text limit.
	 * 
	 * @param limit
	 *            the limit on the number of character in the text input field,
	 *            or <code>UNLIMITED</code> for no limit
	 */
	public void setTextLimit(int limit)
	{
		this.textLimit = limit;
		this.widthInChars = limit;
		if (this.textField != null) this.textField.setTextLimit(limit);
	}
	
	/**
	 * Sets the strategy for validating the text.
	 * <p>
	 * Calling this method has no effect after <code>createPartControl</code> is
	 * called. Thus this method is really only useful for subclasses to call in
	 * their constructor. However, it has public visibility for backward
	 * compatibility.
	 * </p>
	 * 
	 * @param value
	 *            either <code>VALIDATE_ON_KEY_STROKE</code> to perform on the
	 *            fly checking (the default), or
	 *            <code>VALIDATE_ON_FOCUS_LOST</code> to perform validation only
	 *            after the text has been typed in
	 */
	public void setValidateStrategy(int value)
	{
		Assert.isTrue(value == StringFieldEditor.VALIDATE_ON_FOCUS_LOST
						|| value == StringFieldEditor.VALIDATE_ON_KEY_STROKE);
		this.validateStrategy = value;
	}
	
	/**
	 * Shows the error message set via <code>setErrorMessage</code>.
	 */
	public void showErrorMessage()
	{
		this.showErrorMessage(this.getErrorMessage());
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
		
		if (this.capitalizationOn)
		{
			this.textField.setText(this.textField.getText().toUpperCase());
		}
		
		String newValue = this.textField.getText();
		if (!newValue.equals(this.oldValue))
		{
			this.doStore();
			this.oldValue = newValue;
			this.fireValueChanged(this.getName(), this.oldValue, newValue);
		}
	}
	
	/*
	 * @see FieldEditor.setEnabled(boolean,Composite).
	 */
	public void setEnabled(boolean enabled, Composite parent)
	{
		super.setEnabled(enabled, parent);
		this.getTextControl(parent).setEnabled(enabled);
	}
}
