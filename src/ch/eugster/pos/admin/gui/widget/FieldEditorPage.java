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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import ch.eugster.pos.swt.PersistentDBStore;

/**
 * A special abstract preference page to host field editors.
 * <p>
 * Subclasses must implement the <code>createFieldEditors</code> method and
 * should override <code>createLayout</code> if a special layout of the field
 * editors is needed.
 * </p>
 */
public abstract class FieldEditorPage extends Page
{
	
	/**
	 * Layout constant (value <code>0</code>) indicating that each field editor
	 * is handled as a single component.
	 */
	public static final int FLAT = 0;
	
	/**
	 * Layout constant (value <code>1</code>) indicating that the field editors'
	 * basic controls are put into a grid layout.
	 */
	public static final int GRID = 1;
	
	/**
	 * The vertical spacing used by layout styles <code>FLAT</code> and
	 * <code>GRID</code>.
	 */
	protected static final int VERTICAL_SPACING = 10;
	
	/**
	 * The margin width used by layout styles <code>FLAT</code> and
	 * <code>GRID</code>.
	 */
	protected static final int MARGIN_WIDTH = 0;
	
	/**
	 * The margin height used by layout styles <code>FLAT</code> and
	 * <code>GRID</code>.
	 */
	protected static final int MARGIN_HEIGHT = 0;
	
	/**
	 * The field editors, or <code>null</code> if not created yet.
	 */
	private List fields = null;
	
	/**
	 * The layout style; either <code>FLAT</code> or <code>GRID</code>.
	 */
	private int style;
	
	/**
	 * The first invalid field editor, or <code>null</code> if all field editors
	 * are valid.
	 */
	protected FieldEditor invalidFieldEditor = null;
	
	/**
	 * The parent composite for field editors
	 */
	private Composite fieldEditorParent;
	
	/**
	 * Creates a new field editor preference page with the given style, an empty
	 * title, and no image.
	 * 
	 * @param style
	 *            either <code>GRID</code> or <code>FLAT</code>
	 */
	protected FieldEditorPage(String name, int style, PersistentDBStore store)
	{
		super(name, store);
		this.style = style;
	}
	
	/**
	 * Creates a new field editor preference page with the given title and
	 * style, but no image.
	 * 
	 * @param title
	 *            the title of this preference page
	 * @param style
	 *            either <code>GRID</code> or <code>FLAT</code>
	 */
	protected FieldEditorPage(String name, String title, int style, PersistentDBStore store)
	{
		super(name, title, store);
		this.style = style;
	}
	
	/**
	 * Creates a new field editor preference page with the given title, image,
	 * and style.
	 * 
	 * @param title
	 *            the title of this preference page
	 * @param image
	 *            the image for this preference page, or <code>null</code> if
	 *            none
	 * @param style
	 *            either <code>GRID</code> or <code>FLAT</code>
	 */
	protected FieldEditorPage(String name, String title, ImageDescriptor image, int style, PersistentDBStore store)
	{
		super(name, title, image, store);
		this.style = style;
	}
	
	/**
	 * Adds the given field editor to this page.
	 * 
	 * @param editor
	 *            the field editor
	 */
	protected void addField(FieldEditor editor)
	{
		if (this.fields == null) this.fields = new ArrayList();
		this.fields.add(editor);
	}
	
	/**
	 * Adjust the layout of the field editors so that they are properly aligned.
	 */
	protected void adjustGridLayout()
	{
		int numColumns = this.calcNumberOfColumns();
		((GridLayout) this.fieldEditorParent.getLayout()).numColumns = numColumns;
		if (this.fields != null)
		{
			for (int i = 0; i < this.fields.size(); i++)
			{
				FieldEditor fieldEditor = (FieldEditor) this.fields.get(i);
				fieldEditor.adjustForNumColumns(numColumns);
			}
		}
	}
	
	/*
	 * (non-Javadoc) Method declared on DialogPage. Forwards the call to the
	 * field editors managed by this page.
	 */
	protected void applyFont()
	{
		if (this.fields != null)
		{
			Iterator e = this.fields.iterator();
			while (e.hasNext())
			{
				FieldEditor pe = (FieldEditor) e.next();
				pe.applyFont();
			}
		}
	}
	
	/**
	 * Calculates the number of columns needed to host all field editors.
	 * 
	 * @return the number of columns
	 */
	private int calcNumberOfColumns()
	{
		int result = 0;
		if (this.fields != null)
		{
			Iterator e = this.fields.iterator();
			while (e.hasNext())
			{
				FieldEditor pe = (FieldEditor) e.next();
				result = Math.max(result, pe.getNumberOfControls());
			}
		}
		return result;
	}
	
	/**
	 * Recomputes the page's error state by calling <code>isValid</code> for
	 * every field editor.
	 */
	protected void checkState()
	{
		boolean valid = true;
		this.invalidFieldEditor = null;
		// The state can only be set to true if all
		// field editors contain a valid value. So we must check them all
		if (this.fields != null)
		{
			int size = this.fields.size();
			for (int i = 0; i < size; i++)
			{
				FieldEditor editor = (FieldEditor) this.fields.get(i);
				valid = valid && editor.isValid();
				if (!valid)
				{
					this.invalidFieldEditor = editor;
					break;
				}
			}
		}
		this.setValid(valid);
		this.refresh();
	}
	
	/*
	 * (non-Javadoc) Method declared on PreferencePage.
	 */
	protected Control createContents(Composite parent)
	{
		this.fieldEditorParent = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		this.fieldEditorParent.setLayout(layout);
		this.fieldEditorParent.setFont(parent.getFont());
		
		this.createFieldEditors();
		
		if (this.style == FieldEditorPage.GRID) this.adjustGridLayout();
		
		this.initialize();
		this.checkState();
		return this.fieldEditorParent;
	}
	
	/**
	 * Creates the page's field editors.
	 * <p>
	 * The default implementation of this framework method does nothing.
	 * Subclass must implement this method to create the field editors.
	 * </p>
	 * <p>
	 * Subclasses should call <code>getFieldEditorParent</code> to obtain the
	 * parent control for each field editor. This same parent should not be used
	 * for more than one editor as the parent may change for each field editor
	 * depending on the layout style of the page
	 * </p>
	 */
	protected abstract void createFieldEditors();
	
	/**
	 * The field editor preference page implementation of an
	 * <code>IDialogPage</code> method disposes of this page's controls and
	 * images. Subclasses may override to release their own allocated SWT
	 * resources, but must call <code>super.dispose</code>.
	 */
	public void dispose()
	{
		super.dispose();
		if (this.fields != null)
		{
			Iterator e = this.fields.iterator();
			while (e.hasNext())
			{
				FieldEditor pe = (FieldEditor) e.next();
				pe.setPage(null);
				pe.addPropertyChangeListener(null);
				pe.setStore(null);
			}
		}
	}
	
	/**
	 * Returns a parent composite for a field editor.
	 * <p>
	 * This value must not be cached since a new parent may be created each time
	 * this method called. Thus this method must be called each time a field
	 * editor is constructed.
	 * </p>
	 * 
	 * @return a parent
	 */
	protected Composite getFieldEditorParent()
	{
		if (this.style == FieldEditorPage.FLAT)
		{
			// Create a new parent for each field editor
			Composite parent = new Composite(this.fieldEditorParent, SWT.NULL);
			parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			return parent;
		}
		else
		{
			// Just return the parent
			return this.fieldEditorParent;
		}
	}
	
	/**
	 * Initializes all field editors.
	 */
	public void initialize()
	{
		if (this.fields != null)
		{
			Iterator e = this.fields.iterator();
			while (e.hasNext())
			{
				FieldEditor pe = (FieldEditor) e.next();
				pe.setPage(this);
				pe.removePropertyChangeListener(this);
				pe.addPropertyChangeListener(this);
				pe.setStore(this.getStore());
				pe.loadDefault();
			}
		}
	}
	
	/**
	 * The field editor preference page implementation of a
	 * <code>PreferencePage</code> method loads all the field editors with their
	 * default values.
	 */
	public void performDefaults()
	{
		if (this.fields != null)
		{
			Iterator e = this.fields.iterator();
			while (e.hasNext())
			{
				FieldEditor pe = (FieldEditor) e.next();
				pe.loadDefault();
			}
		}
		this.checkState();
	}
	
	/**
	 * The field editor preference page implementation of this
	 * <code>PreferencePage</code> method saves all field editors by calling
	 * <code>FieldEditor.store</code>. Note that this method does not save the
	 * preference store itself; it just stores the values back into the
	 * preference store.
	 * 
	 * @see FieldEditor#store()
	 */
	public boolean doStore()
	{
		if (this.fields != null)
		{
			Iterator e = this.fields.iterator();
			while (e.hasNext())
			{
				FieldEditor pe = (FieldEditor) e.next();
				pe.store();
			}
		}
		return true;
	}
	
	/**
	 * The field editor preference page implementation of this
	 * <code>IPreferencePage</code> (and <code>IPropertyChangeListener</code>)
	 * method intercepts <code>IS_VALID</code> events but passes other events on
	 * to its superclass.
	 */
	public void propertyChange(PropertyChangeEvent event)
	{
		
		if (event.getProperty().equals(FieldEditor.IS_VALID))
		{
			boolean newValue = ((Boolean) event.getNewValue()).booleanValue();
			// If the new value is true then we must check all field editors.
			// If it is false, then the page is invalid in any case.
			if (newValue)
			{
				this.checkState();
			}
			else
			{
				this.invalidFieldEditor = (FieldEditor) event.getSource();
				this.setValid(newValue);
			}
		}
		
		this.getContainer().updateButtons();
	}
	
	/*
	 * (non-Javadoc) Method declared on IDialog.
	 */
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		if (visible && this.invalidFieldEditor != null)
		{
			this.invalidFieldEditor.setFocus();
		}
	}
	
	protected void setFocus(int index)
	{
		if (this.fields != null) if (this.fields.size() > index) ((FieldEditor) this.fields.get(index)).setFocus();
	}
	
	public void initFocus()
	{
		this.performDefaults();
		this.setFocus(0);
	}
	
	public void refresh()
	{
	}
	
}
