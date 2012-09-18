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

import java.io.File;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

/**
 * A field editor for a file path type preference. A standard file dialog
 * appears when the user presses the change button.
 */
public class FileFieldEditor extends StringButtonFieldEditor
{
	
	/**
	 * List of legal file extension suffixes, or <code>null</code> for system
	 * defaults.
	 */
	private String[] extensions = null;
	
	/**
	 * Indicates whether the path must be absolute; <code>false</code> by
	 * default.
	 */
	private boolean enforceAbsolute = false;
	
	/**
	 * Creates a new file field editor
	 */
	protected FileFieldEditor()
	{
	}
	
	/**
	 * Creates a file field editor.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public FileFieldEditor(String name, String labelText, Composite parent)
	{
		this(name, labelText, false, parent);
	}
	
	/**
	 * Creates a file field editor.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param enforceAbsolute
	 *            <code>true</code> if the file path must be absolute, and
	 *            <code>false</code> otherwise
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public FileFieldEditor(String name, String labelText, boolean enforceAbsolute, Composite parent)
	{
		this.init(name, labelText);
		this.enforceAbsolute = enforceAbsolute;
		this.setErrorMessage(JFaceResources.getString("FileFieldEditor.errorMessage"));//$NON-NLS-1$
		this.setChangeButtonText(JFaceResources.getString("openBrowse"));//$NON-NLS-1$
		this.setValidateStrategy(StringFieldEditor.VALIDATE_ON_FOCUS_LOST);
		this.createControl(parent);
	}
	
	/*
	 * (non-Javadoc) Method declared on StringButtonFieldEditor. Opens the file
	 * chooser dialog and returns the selected file.
	 */
	protected String changePressed()
	{
		File f = new File(this.getTextControl().getText());
		if (!f.exists()) f = null;
		File d = this.getFile(f);
		if (d == null) return null;
		
		return d.getAbsolutePath();
	}
	
	/*
	 * (non-Javadoc) Method declared on StringFieldEditor. Checks whether the
	 * text input field specifies an existing file.
	 */
	protected boolean checkState()
	{
		
		String msg = null;
		
		String path = this.getTextControl().getText();
		if (path != null)
			path = path.trim();
		else
			path = "";//$NON-NLS-1$
		if (path.length() == 0)
		{
			if (!this.isEmptyStringAllowed()) msg = this.getErrorMessage();
		}
		else
		{
			File file = new File(path);
			if (file.isFile())
			{
				if (this.enforceAbsolute && !file.isAbsolute())
					msg = JFaceResources.getString("FileFieldEditor.errorMessage2");//$NON-NLS-1$
			}
			else
			{
				msg = this.getErrorMessage();
			}
		}
		
		if (msg != null)
		{ // error
			this.showErrorMessage(msg);
			return false;
		}
		
		// OK!
		this.clearErrorMessage();
		return true;
	}
	
	/**
	 * Helper to open the file chooser dialog.
	 */
	private File getFile(File startingDirectory)
	{
		
		FileDialog dialog = new FileDialog(this.getShell(), SWT.OPEN);
		if (startingDirectory != null) dialog.setFileName(startingDirectory.getPath());
		if (this.extensions != null) dialog.setFilterExtensions(this.extensions);
		String file = dialog.open();
		if (file != null)
		{
			file = file.trim();
			if (file.length() > 0) return new File(file);
		}
		
		return null;
	}
	
	/**
	 * Sets this file field editor's file extension filter.
	 * 
	 * @param extension
	 *            a list of file extension, or <code>null</code> to set the
	 *            filter to the system's default value
	 */
	public void setFileExtensions(String[] extensions)
	{
		this.extensions = extensions;
	}
}
