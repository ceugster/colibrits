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

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/**
 * The ColorSelector is a wrapper for a button that displays a selected Color
 * and allows the user to change the selection.
 */
public class ColorSelector
{
	
	private Point fExtent;
	private Image fImage;
	private RGB fColorValue;
	private Color fColor;
	private Button fButton;
	
	/**
	 * Create a new instance of the reciever and the button that it wrappers in
	 * the supplied parent Composite
	 * 
	 * @param parent
	 *            . The parent of the button.
	 */
	public ColorSelector(Composite parent)
	{
		
		this.fButton = new Button(parent, SWT.PUSH);
		this.fExtent = this.computeImageSize(parent);
		this.fImage = new Image(parent.getDisplay(), this.fExtent.x, this.fExtent.y);
		
		GC gc = new GC(this.fImage);
		gc.setBackground(this.fButton.getBackground());
		gc.fillRectangle(0, 0, this.fExtent.x, this.fExtent.y);
		gc.dispose();
		
		this.fButton.setImage(this.fImage);
		this.fButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent event)
			{
				ColorDialog colorDialog = new ColorDialog(ColorSelector.this.fButton.getShell());
				colorDialog.setRGB(ColorSelector.this.fColorValue);
				RGB newColor = colorDialog.open();
				if (newColor != null)
				{
					ColorSelector.this.fColorValue = newColor;
					ColorSelector.this.updateColorImage();
				}
			}
		});
		
		this.fButton.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent event)
			{
				if (ColorSelector.this.fImage != null)
				{
					ColorSelector.this.fImage.dispose();
					ColorSelector.this.fImage = null;
				}
				if (ColorSelector.this.fColor != null)
				{
					ColorSelector.this.fColor.dispose();
					ColorSelector.this.fColor = null;
				}
			}
		});
	}
	
	/**
	 * Return the currently displayed color.
	 * 
	 * @return RGB
	 */
	public RGB getColorValue()
	{
		return this.fColorValue;
	}
	
	/**
	 * Set the current color value and update the control.
	 * 
	 * @param rgb
	 *            . The new color.
	 */
	public void setColorValue(RGB rgb)
	{
		this.fColorValue = rgb;
		this.updateColorImage();
	}
	
	/**
	 * Get the button control being wrappered by the selector.
	 * 
	 * @return Button
	 */
	public Button getButton()
	{
		return this.fButton;
	}
	
	/**
	 * Update the image being displayed on the button using the current color
	 * setting,
	 */
	
	protected void updateColorImage()
	{
		
		Display display = this.fButton.getDisplay();
		
		GC gc = new GC(this.fImage);
		gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		gc.drawRectangle(0, 2, this.fExtent.x - 1, this.fExtent.y - 4);
		
		if (this.fColor != null) this.fColor.dispose();
		
		this.fColor = new Color(display, this.fColorValue);
		gc.setBackground(this.fColor);
		gc.fillRectangle(1, 3, this.fExtent.x - 2, this.fExtent.y - 5);
		gc.dispose();
		
		this.fButton.setImage(this.fImage);
	}
	
	/**
	 * Compute the size of the image to be displayed.
	 * 
	 * @return Point
	 * @param window
	 *            - the window used to calculate
	 */
	
	private Point computeImageSize(Control window)
	{
		GC gc = new GC(window);
		Font f = JFaceResources.getFontRegistry().get(JFaceResources.DEFAULT_FONT);
		gc.setFont(f);
		int height = gc.getFontMetrics().getHeight();
		gc.dispose();
		Point p = new Point(height * 3 - 6, height);
		return p;
	}
	
	/**
	 * Set whether or not the button is enabled.
	 */
	
	public void setEnabled(boolean state)
	{
		this.getButton().setEnabled(state);
	}
}
