/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 * 	   Sebastian Davids <sdavids@gmx.de> - Fix for bug 38729 - [Data]
 * 			 NPE DataPage isValid.
 *******************************************************************************/
package ch.eugster.pos.admin.gui.widget;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;

import ch.eugster.pos.swt.PersistentDBStore;

/**
 * Abstract base implementation for all data page implementations.
 * <p>
 * Subclasses must implement the <code>createControl</code> framework method to
 * supply the page's main control.
 * </p>
 * <p>
 * Subclasses should extend the <code>doComputeSize</code> framework method to
 * compute the size of the page's control.
 * </p>
 * <p>
 * Subclasses may override the <code>performOk</code>, <code>performApply</code>, <code>performDefaults</code>, <code>performCancel</code>, and
 * <code>performHelp</code> framework methods to react to the standard button
 * events.
 * </p>
 * <p>
 * Subclasses may call the <code>noDefaultAndApplyButton</code> framework method
 * before the page's control has been created to suppress the standard Apply and
 * Defaults buttons.
 * </p>
 */
public abstract class Page extends DialogPage implements IPage, IPropertyChangeListener
{
	
	private String name;
	/**
	 * Data store, or <code>null</code>.
	 */
	private PersistentDBStore store;
	
	/**
	 * Valid state for this page; <code>true</code> by default.
	 * 
	 * @see #isValid
	 */
	private boolean isValid = true;
	
	/**
	 * Body of page.
	 */
	private Control body;
	
	/**
	 * The container this data page belongs to; <code>null</code> if none.
	 */
	private IPageContainer container = null;
	
	/**
	 * Description label.
	 * 
	 * @see #createDescriptionLabel.
	 */
	private Label descriptionLabel;
	
	/**
	 * Caches size of page.
	 */
	private Point size = null;
	
	/**
	 * Creates a new data page with an empty title and no image.
	 */
	protected Page(String name, PersistentDBStore store)
	{
		super(""); //$NON-NLS-1$
		this.name = name;
		this.store = store;
		this.isAddActive = false;
		this.isRemoveActive = false;
	}
	
	/**
	 * Creates a new data page with the given title and no image.
	 * 
	 * @param title
	 *            the title of this data page
	 */
	protected Page(String name, String title, PersistentDBStore store)
	{
		super(title);
		this.name = name;
		this.store = store;
	}
	
	/**
	 * Creates a new abstract data page with the given title and image.
	 * 
	 * @param title
	 *            the title of this data page
	 * @param image
	 *            the image for this data page, or <code>null</code> if none
	 */
	protected Page(String name, String title, ImageDescriptor image, PersistentDBStore store)
	{
		super(title, image);
		this.name = name;
		this.store = store;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * Computes the size for this page's UI control.
	 * <p>
	 * The default implementation of this <code>IDataPage</code> method returns
	 * the size set by <code>setSize</code>; if no size has been set, but the
	 * page has a UI control, the framework method <code>doComputeSize</code> is
	 * called to compute the size.
	 * </p>
	 * 
	 * @return the size of the data page encoded as
	 *         <code>new Point(width,height)</code>, or <code>(0,0)</code> if
	 *         the page doesn't currently have any UI component
	 */
	public Point computeSize()
	{
		if (this.size != null) return this.size;
		Control control = this.getControl();
		if (control != null)
		{
			this.size = this.doComputeSize();
			return this.size;
		}
		return new Point(0, 0);
	}
	
	/**
	 * Creates and returns the SWT control for the customized body of this data
	 * page under the given parent composite.
	 * <p>
	 * This framework method must be implemented by concrete subclasses. Any
	 * subclass returning a <code>Composite</code> object whose
	 * <code>Layout</code> has default margins (for example, a
	 * <code>GridLayout</code>) are expected to set the margins of this
	 * <code>Layout</code> to 0 pixels.
	 * </p>
	 * 
	 * @param parent
	 *            the parent composite
	 * @return the new control
	 */
	protected abstract Control createContents(Composite parent);
	
	/**
	 * The <code>DataPage</code> implementation of this <code>IDialogPage</code>
	 * method creates a description label and button bar for the page. It calls
	 * <code>createContents</code> to create the custom contents of the page.
	 * <p>
	 * If a subclass that overrides this method creates a <code>Composite</code>
	 * that has a layout with default margins (for example, a
	 * <code>GridLayout</code>) it is expected to set the margins of this
	 * <code>Layout</code> to 0 pixels.
	 */
	public void createControl(Composite parent)
	{
		if (this.store != null) this.store.addPropertyChangeListener(this);
		
		Composite content = new Composite(parent, SWT.NULL);
		content.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		this.setControl(content);
		Font font = parent.getFont();
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		content.setLayout(layout);
		content.setFont(font);
		
		// initialize the dialog units
		this.initializeDialogUnits(content);
		
		this.descriptionLabel = this.createDescriptionLabel(content);
		if (this.descriptionLabel != null)
		{
			this.descriptionLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}
		
		this.body = this.createContents(content);
		this.getStore().setDirty(false);
		if (this.body != null)
		// null is not a valid return value but support graceful failure
			this.body.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	/**
	 * Creates and returns an SWT label under the given composite.
	 * 
	 * @param parent
	 *            the parent composite
	 * @return the new label
	 */
	protected Label createDescriptionLabel(Composite parent)
	{
		Label result = null;
		String description = this.getDescription();
		if (description != null)
		{
			result = new Label(parent, SWT.WRAP);
			result.setFont(parent.getFont());
			result.setText(description);
		}
		return result;
	}
	
	/**
	 * Computes the size needed by this page's UI control.
	 * <p>
	 * All pages should override this method and set the appropriate sizes of
	 * their widgets, and then call <code>super.doComputeSize</code>.
	 * </p>
	 * 
	 * @return the size of the data page encoded as
	 *         <code>new Point(width,height)</code>
	 */
	protected Point doComputeSize()
	{
		if (this.descriptionLabel != null && this.body != null)
		{
			Point size = this.body.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
			GridData gd = (GridData) this.descriptionLabel.getLayoutData();
			gd.widthHint = size.x;
			this.descriptionLabel.getParent().layout(true);
		}
		return this.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
	}
	
	/**
	 * Returns the data store of this data page.
	 * <p>
	 * This is a framework hook method for subclasses to return a page-specific
	 * data store. The default implementation returns <code>null</code>.
	 * </p>
	 * 
	 * @return the data store, or <code>null</code> if none
	 */
	protected PersistentDBStore doGetStore()
	{
		return this.store;
	}
	
	/**
	 * Returns the container of this page.
	 * 
	 * @return the data page container, or <code>null</code> if this page has
	 *         yet to be added to a container
	 */
	public IPageContainer getContainer()
	{
		return this.container;
	}
	
	/**
	 * Initializez the SWT controls for the customized body of this data page
	 * under the given parent composite.
	 */
	public abstract void initialize();
	
	/**
	 * Returns the data store of this data page.
	 * 
	 * @return the data store , or <code>null</code> if none
	 */
	public PersistentDBStore getStore()
	{
		if (this.store == null) this.store = this.doGetStore();
		if (this.store != null)
			return this.store;
		else if (this.container != null) return this.container.getStore();
		return null;
	}
	
	/**
	 * The data page implementation of an <code>IPage</code> method returns
	 * whether this data page is valid. Data pages are considered valid by
	 * default; call <code>setValid(false)</code> to make a page invalid.
	 */
	public boolean isValid()
	{
		return this.isValid;
	}
	
	/**
	 * The <code>DataPage</code> implementation of this <code>IDataPage</code>
	 * method returns <code>true</code> if the page is valid.
	 */
	public boolean okToLeave()
	{
		return this.isValid();
	}
	
	/**
	 * Method declared on IDataPage. Subclasses should override
	 */
	public boolean doStore()
	{
		return true;
	}
	
	/**
	 * (non-Javadoc) Method declared on IDataPage.
	 */
	public void setContainer(IPageContainer container)
	{
		this.container = container;
	}
	
	/**
	 * The <code>DataPage</code> implementation of this method declared on
	 * <code>DialogPage</code> updates the container.
	 */
	public void setErrorMessage(String newMessage)
	{
		super.setErrorMessage(newMessage);
		if (this.getContainer() != null)
		{
			this.getContainer().updateMessage();
		}
	}
	
	/**
	 * The <code>DataPage</code> implementation of this method declared on
	 * <code>DialogPage</code> updates the container.
	 */
	public void setMessage(String newMessage, int newType)
	{
		super.setMessage(newMessage, newType);
		if (this.getContainer() != null)
		{
			this.getContainer().updateMessage();
		}
	}
	
	/**
	 * Sets the data store for this data page.
	 * <p>
	 * If store is set to null, getStore will invoke doGetStore the next time it
	 * is called.
	 * </p>
	 * 
	 * @param store
	 *            the data store, or <code>null</code>
	 * @see #getStore
	 */
	public void setStore(PersistentDBStore store)
	{
		this.store = store;
	}
	
	/*
	 * (non-Javadoc) Method declared on IDataPage.
	 */
	public void setSize(Point uiSize)
	{
		Control control = this.getControl();
		if (control != null)
		{
			control.setSize(uiSize);
			this.size = uiSize;
		}
	}
	
	/**
	 * The <code>DataPage</code> implementation of this <code>IDialogPage</code>
	 * method extends the <code>DialogPage</code> implementation to update the
	 * data page container title. Subclasses may extend.
	 */
	public void setTitle(String title)
	{
		super.setTitle(title);
		if (this.getContainer() != null) this.getContainer().updateTitle();
	}
	
	/**
	 * Sets whether this page is valid. The enable state of the container
	 * buttons and the apply button is updated when a page's valid state
	 * changes.
	 * <p>
	 * 
	 * @param b
	 *            the new valid state
	 */
	public void setValid(boolean b)
	{
		this.isValid = b;
	}
	
	/**
	 * Returns a string suitable for debugging purpose only.
	 */
	public String toString()
	{
		return this.getTitle();
	}
	
	/**
	 * Creates a composite with a highlighted Note entry and a message text.
	 * This is designed to take up the full width of the page.
	 * 
	 * @param font
	 *            the font to use
	 * @param composite
	 *            the parent composite
	 * @param title
	 *            the title of the note
	 * @param message
	 *            the message for the note
	 * @return the composite for the note
	 */
	protected Composite createNoteComposite(Font font, Composite composite, String title, String message)
	{
		Composite messageComposite = new Composite(composite, SWT.NONE);
		GridLayout messageLayout = new GridLayout();
		messageLayout.numColumns = 2;
		messageLayout.marginWidth = 0;
		messageLayout.marginHeight = 0;
		messageComposite.setLayout(messageLayout);
		messageComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		messageComposite.setFont(font);
		
		final Label noteLabel = new Label(messageComposite, SWT.BOLD);
		noteLabel.setText(title);
		noteLabel.setFont(JFaceResources.getBannerFont());
		noteLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		
		final IPropertyChangeListener fontListener = new IPropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent event)
			{
				if (JFaceResources.BANNER_FONT.equals(event.getProperty()))
				{
					noteLabel.setFont(JFaceResources.getFont(JFaceResources.BANNER_FONT));
				}
			}
		};
		JFaceResources.getFontRegistry().addListener(fontListener);
		noteLabel.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent event)
			{
				JFaceResources.getFontRegistry().removeListener(fontListener);
			}
		});
		
		Label messageLabel = new Label(messageComposite, SWT.WRAP);
		messageLabel.setText(message);
		messageLabel.setFont(font);
		return messageComposite;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.IDialogPage#performHelp()
	 */
	public void performHelp()
	{
		this.getControl().notifyListeners(SWT.Help, new Event());
	}
	
	public abstract void performDefaults();
	
	protected abstract void setFocus(int index);
	
	public abstract void initFocus();
	
	public abstract boolean isInstance(Object object);
	
	/**
	 * 
	 */
	public Object getCurrent()
	{
		return this.getStore().getElement();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(
	 * org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(Object element)
	{
		if (element == null)
		{
			this.getStore().initialize();
		}
		else
		{
			if (this.isInstance(element))
			{
				this.getStore().setElement(element);
			}
			else
			{
				this.getStore().initialize();
			}
		}
		this.performDefaults();
		this.getStore().setDirty(this.isDirty());
		this.getContainer().updateButtons();
	}
	
	public boolean isDirty()
	{
		return false;
	}
	
	public abstract String getElementName();
	
	public boolean isAddActive = true;
	public boolean isRemoveActive = true;
	
}
