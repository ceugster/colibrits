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
package ch.eugster.pos.admin.preference;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import ch.eugster.pos.admin.gui.widget.ColorSelector;

/**
 * A field editor for a color type preference.
 */
public class ColorFieldEditor extends FieldEditor
{
	
	/**
	 * The color selector, or <code>null</code> if none.
	 */
	private ColorSelector colorSelector;
	
	/**
	 * Creates a new color field editor
	 */
	protected ColorFieldEditor()
	{
	}
	
	/**
	 * Creates a color field editor.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public ColorFieldEditor(String name, String labelText, Composite parent)
	{
		super(name, labelText, parent);
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void adjustForNumColumns(int numColumns)
	{
		((GridData) this.colorSelector.getButton().getLayoutData()).horizontalSpan = numColumns - 1;
	}
	
	/**
	 * Computes the size of the color image displayed on the button.
	 * <p>
	 * This is an internal method and should not be called by clients.
	 * </p>
	 */
	protected Point computeImageSize(Control window)
	{
		// Make the image height as high as a corresponding character. This
		// makes sure that the button has the same size as a "normal" text
		// button.
		GC gc = new GC(window);
		Font f = JFaceResources.getFontRegistry().get(JFaceResources.DEFAULT_FONT);
		gc.setFont(f);
		int height = gc.getFontMetrics().getHeight();
		gc.dispose();
		Point p = new Point(height * 3 - 6, height);
		return p;
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doFillIntoGrid(Composite parent, int numColumns)
	{
		Control control = this.getLabelControl(parent);
		GridData gd = new GridData();
		gd.horizontalSpan = numColumns - 1;
		control.setLayoutData(gd);
		
		Button colorButton = this.getChangeControl(parent);
		gd = new GridData();
		gd.heightHint = this.convertVerticalDLUsToPixels(colorButton, IDialogConstants.BUTTON_HEIGHT);
		int widthHint = this.convertHorizontalDLUsToPixels(colorButton, IDialogConstants.BUTTON_WIDTH);
		gd.widthHint = Math.max(widthHint, colorButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		colorButton.setLayoutData(gd);
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoad()
	{
		if (this.colorSelector == null) return;
		this.colorSelector.setColorValue(PreferenceConverter.getColor(this.getPreferenceStore(), this
						.getPreferenceName()));
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoadDefault()
	{
		if (this.colorSelector == null) return;
		this.colorSelector.setColorValue(PreferenceConverter.getDefaultColor(this.getPreferenceStore(), this
						.getPreferenceName()));
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doStore()
	{
		PreferenceConverter.setColor(this.getPreferenceStore(), this.getPreferenceName(), this.colorSelector
						.getColorValue());
	}
	
	/**
	 * Returns the change button for this field editor.
	 * 
	 * @return the change button
	 */
	protected Button getChangeControl(Composite parent)
	{
		if (this.colorSelector == null)
		{
			this.colorSelector = new ColorSelector(parent);
		}
		else
		{
			this.checkParent(this.colorSelector.getButton(), parent);
		}
		return this.colorSelector.getButton();
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public int getNumberOfControls()
	{
		return 2;
	}
	
	/*
	 * @see FieldEditor.setEnabled
	 */
	public void setEnabled(boolean enabled, Composite parent)
	{
		super.setEnabled(enabled, parent);
		this.getChangeControl(parent).setEnabled(enabled);
	}
	
}
