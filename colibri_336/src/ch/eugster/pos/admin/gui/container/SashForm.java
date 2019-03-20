/*
 * Created on 13.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.gui.container;

import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.ColorRegistry;
import ch.eugster.pos.admin.area.product.ProductGroupFieldEditorPage;
import ch.eugster.pos.admin.gui.widget.Page;
import ch.eugster.pos.admin.model.ContentProvider;
import ch.eugster.pos.admin.preference.PreferenceDialog;
import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.swt.BaseLabelProvider;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public abstract class SashForm extends org.eclipse.swt.custom.SashForm implements ISelectionChangedListener
{
	
	public static final String DLG_TITLE_IMG = "admin_dialog_title_image"; //$NON-NLS-1$
	public static final String DLG_IMG_TITLE_ERROR = Dialog.DLG_IMG_MESSAGE_ERROR;
	
	static
	{
		ImageRegistry reg = JFaceResources.getImageRegistry();
		reg.put(SashForm.DLG_TITLE_IMG,
						ImageDescriptor.createFromFile(PreferenceDialog.class, "images/pref_dialog_title.gif")); //$NON-NLS-1$
	}
	
	/**
	 * @param parent
	 * @param style
	 */
	public SashForm(Composite parent, int style, String identity)
	{
		super(parent, style);
		
		/*
		 * Jede SashForm soll über das Property 'identity' eindeutig
		 * identifiziert werden können
		 */
		this.setData("identity", identity); //$NON-NLS-1$
		
		// Linkes Panel, das den Viewer enthalten soll
		GridLayout gl = new GridLayout();
		gl.marginWidth = 5;
		gl.marginHeight = 5;
		this.left = new Composite(this, SWT.FLAT);
		this.left.setLayout(gl);
		
		this.viewer = this.createViewer(this.left);
		
		// Rechtes Panel, das die Detailseite enthalten wird
		GridLayout sl = new GridLayout();
		sl.marginWidth = 5;
		sl.marginHeight = 5;
		this.right = new Composite(this, SWT.FLAT);
		this.right.setLayout(sl);
		
		// Title area
		this.titleArea = this.createTitleArea(this.right);
		
		this.content = this.setPageContainer(this.right);
		this.content.setLayoutData(new GridData(GridData.FILL_BOTH));
		
	}
	
	protected abstract StructuredViewer createViewer(Composite parent);
	
	public Composite getSelectorParent()
	{
		return this.left;
	}
	
	private Composite createTitleArea(Composite parent)
	{
		// Create the title area which will contain
		// a title, message, and image.
		this.titleArea = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 2;
		layout.marginWidth = 2;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.numColumns = 2;
		
		// Get the background color for the title area
		Display display = parent.getDisplay();
		Color background = JFaceColors.getBannerBackground(display);
		final Color foreground = JFaceColors.getBannerForeground(display);
		
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		this.titleArea.setLayout(layout);
		this.titleArea.setLayoutData(layoutData);
		this.titleArea.setBackground(background);
		
		final Color borderColor = new Color(this.titleArea.getDisplay(), ViewForm.borderOutsideRGB);
		
		this.titleArea.addPaintListener(new PaintListener()
		{
			public void paintControl(PaintEvent e)
			{
				e.gc.setForeground(borderColor);
				Rectangle bounds = SashForm.this.titleArea.getClientArea();
				bounds.height = bounds.height - 2;
				bounds.width = bounds.width - 1;
				e.gc.drawRectangle(bounds);
			}
		});
		
		// Add a dispose listener
		this.titleArea.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e)
			{
				if (SashForm.this.titleAreaColor != null) SashForm.this.titleAreaColor.dispose();
				if (SashForm.this.errorMsgAreaBackground != null) SashForm.this.errorMsgAreaBackground.dispose();
				borderColor.dispose();
			}
		});
		
		// Message label
		this.messageLabel = new CLabel(this.titleArea, SWT.LEFT);
		JFaceColors.setColors(this.messageLabel, foreground, background);
		this.messageLabel.setText(" "); //$NON-NLS-1$
		this.messageLabel.setFont(JFaceResources.getBannerFont());
		
		final IPropertyChangeListener fontListener = new IPropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent event)
			{
				if (JFaceResources.BANNER_FONT.equals(event.getProperty())) SashForm.this.updateMessage();
				if (JFaceResources.DIALOG_FONT.equals(event.getProperty()))
				{
					SashForm.this.updateMessage();
				}
			}
		};
		
		this.messageLabel.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent event)
			{
				JFaceResources.getFontRegistry().removeListener(fontListener);
			}
		});
		
		JFaceResources.getFontRegistry().addListener(fontListener);
		
		GridData gd = new GridData(GridData.FILL_BOTH);
		this.messageLabel.setLayoutData(gd);
		
		// Title image
		this.titleImage = new Label(this.titleArea, SWT.LEFT);
		this.titleImage.setBackground(background);
		this.titleImage.setImage(JFaceResources.getImage(SashForm.DLG_TITLE_IMG));
		gd = new GridData();
		gd.horizontalAlignment = GridData.END;
		this.titleImage.setLayoutData(gd);
		
		return this.titleArea;
	}
	
	/**
	 * Creates the inner page container.
	 */
	private Composite setPageContainer(Composite parent)
	{
		StackLayout sl = new StackLayout();
		sl.marginWidth = 5;
		sl.marginHeight = 5;
		
		Composite result = new Composite(parent, SWT.NONE);
		result.setLayout(sl);
		
		return result;
	}
	
	public Composite getPageContainer()
	{
		return this.content;
	}
	
	/*
	 * (non-Javadoc) Method declared on IPageContainer.
	 */
	public void updateMessage()
	{
		String pageMessage = this.currentPage.getMessage();
		int pageMessageType = IMessageProvider.NONE;
		pageMessageType = this.currentPage.getMessageType();
		
		String pageErrorMessage = this.currentPage.getErrorMessage();
		
		// Adjust the font
		if (pageMessage == null && pageErrorMessage == null)
			this.messageLabel.setFont(JFaceResources.getBannerFont());
		else
			this.messageLabel.setFont(JFaceResources.getDialogFont());
		
		// Set the message and error message
		if (pageMessage == null)
		{
			this.setMessage(this.currentPage.getTitle());
		}
		else
		{
			this.setMessage(pageMessage, pageMessageType);
		}
		this.setErrorMessage(pageErrorMessage);
	}
	
	/**
	 * Set the message text. If the message line currently displays an error,
	 * the message is stored and will be shown after a call to clearErrorMessage
	 * <p>
	 * Shortcut for <code>setMessage(newMessage, NONE)</code>
	 * </p>
	 * 
	 * @param newMessage
	 *            the message, or <code>null</code> to clear the message
	 */
	public void setMessage(String newMessage)
	{
		this.setMessage(newMessage, IMessageProvider.NONE);
	}
	
	/**
	 * Sets the message for this dialog with an indication of what type of
	 * message it is.
	 * <p>
	 * The valid message types are one of <code>NONE</code>,
	 * <code>INFORMATION</code>, <code>WARNING</code>, or <code>ERROR</code>.
	 * </p>
	 * <p>
	 * Note that for backward compatibility, a message of type
	 * <code>ERROR</code> is different than an error message (set using
	 * <code>setErrorMessage</code>). An error message overrides the current
	 * message until the error message is cleared. This method replaces the
	 * current message and does not affect the error message.
	 * </p>
	 * 
	 * @param newMessage
	 *            the message, or <code>null</code> to clear the message
	 * @param newType
	 *            the message type
	 * @since 2.0
	 */
	public void setMessage(String newMessage, int newType)
	{
		Image newImage = null;
		
		if (newMessage != null)
		{
			switch (newType)
			{
				case IMessageProvider.NONE:
					break;
				case IMessageProvider.INFORMATION:
					newImage = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_INFO);
					break;
				case IMessageProvider.WARNING:
					newImage = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_WARNING);
					break;
				case IMessageProvider.ERROR:
					newImage = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_ERROR);
					break;
			}
		}
		
		this.showMessage(newMessage, newImage);
	}
	
	/**
	 * Display the given error message. The currently displayed message is saved
	 * and will be redisplayed when the error message is set to
	 * <code>null</code>.
	 * 
	 * @param errorMessage
	 *            the errorMessage to display or <code>null</code>
	 */
	public void setErrorMessage(String newErrorMessage)
	{
		// Any change?
		if (this.errorMessage == null ? newErrorMessage == null : this.errorMessage.equals(newErrorMessage)) return;
		
		this.errorMessage = newErrorMessage;
		if (this.errorMessage == null)
		{
			if (this.showingError)
			{
				// we were previously showing an error
				this.showingError = false;
				this.messageLabel.setBackground(this.normalMsgAreaBackground);
				this.messageLabel.setForeground(this.normalMsgAreaForeground);
				this.messageLabel.setImage(null);
			}
			
			// avoid calling setMessage in case it is overridden to call
			// setErrorMessage,
			// which would result in a recursive infinite loop
			if (this.message == null)
			// this should probably never happen since setMessage does this
			// conversion....
				this.message = ""; //$NON-NLS-1$
			this.messageLabel.setText(this.message);
			this.messageLabel.setImage(this.messageImage);
			this.messageLabel.setToolTipText(this.message);
		}
		else
		{
			this.messageLabel.setText(this.errorMessage);
			this.messageLabel.setToolTipText(this.errorMessage);
			if (!this.showingError)
			{
				// we were not previously showing an error
				this.showingError = true;
				
				// lazy initialize the error background color and image
				if (this.errorMsgAreaBackground == null)
				{
					this.errorMsgAreaBackground = JFaceColors.getErrorBackground(this.messageLabel.getDisplay());
					this.errorMsgImage = JFaceResources.getImage(PreferenceDialog.PREF_DLG_IMG_TITLE_ERROR);
				}
				
				// show the error
				this.normalMsgAreaBackground = this.messageLabel.getBackground();
				this.normalMsgAreaForeground = this.messageLabel.getForeground();
				// messageLabel.setBackground(errorMsgAreaBackground);
				this.messageLabel.setForeground(ColorRegistry.getErrorText(this.getDisplay()));
				this.messageLabel.setImage(this.errorMsgImage);
			}
		}
	}
	
	/**
	 * Show the new message
	 */
	private void showMessage(String newMessage, Image newImage)
	{
		// Any change?
		if (this.message.equals(newMessage) && this.messageImage == newImage) return;
		
		this.message = newMessage;
		if (this.message == null) this.message = ""; //$NON-NLS-1$
		this.messageImage = newImage;
		
		if (!this.showingError)
		{
			// we are not showing an error
			this.messageLabel.setText(this.message);
			this.messageLabel.setImage(this.messageImage);
			this.messageLabel.setToolTipText(this.message);
		}
	}
	
	public void initialize(ContentProvider contentProvider, IBaseLabelProvider labelProvider)
	{
		this.viewer.setContentProvider(contentProvider);
		((ContentProvider) this.viewer.getContentProvider()).setViewer(this.viewer);
		this.viewer.setLabelProvider(labelProvider);
		((BaseLabelProvider) this.viewer.getLabelProvider()).setViewer(this.viewer);
		
		this.initializeViewer();
		
		this.selector = ((ContentProvider) this.viewer.getContentProvider()).createSelector(this.left, this.viewer,
						SWT.FLAT);
		this.initializeContent();
	}
	
	public void initialize(ContentProvider contentProvider, IBaseLabelProvider labelProvider, IElementComparer comparer)
	{
		this.viewer.setContentProvider(contentProvider);
		((ContentProvider) this.viewer.getContentProvider()).setViewer(this.viewer);
		this.viewer.setLabelProvider(labelProvider);
		((BaseLabelProvider) this.viewer.getLabelProvider()).setViewer(this.viewer);
		
		this.initializeViewer(comparer);
		
		this.selector = ((ContentProvider) this.viewer.getContentProvider()).createSelector(this.left, this.viewer,
						SWT.FLAT);
		this.initializeContent();
	}
	
	public void initialize(ContentProvider contentProvider, IBaseLabelProvider labelProvider, ViewerSorter sorter)
	{
		this.viewer.setContentProvider(contentProvider);
		((ContentProvider) this.viewer.getContentProvider()).setViewer(this.viewer);
		this.viewer.setLabelProvider(labelProvider);
		((BaseLabelProvider) this.viewer.getLabelProvider()).setViewer(this.viewer);
		this.viewer.setSorter(sorter);
		
		this.initializeViewer();
		
		this.selector = ((ContentProvider) this.viewer.getContentProvider()).createSelector(this.left, this.viewer,
						SWT.FLAT);
		this.initializeContent();
	}
	
	protected void initializeViewer()
	{
		this.initializeViewer(null);
	}
	
	protected void initializeViewer(IElementComparer comparer)
	{
		if (comparer != null) this.viewer.setComparer(comparer);
	}
	
	public void initializeContent()
	{
		this.viewer.setInput(((IStructuredContentProvider) this.viewer.getContentProvider())
						.getElements(new RootItem()));
		Object[] objects = (Object[]) this.viewer.getInput();
		if (objects != null && objects.length > 0)
		{
			StructuredSelection selection = new StructuredSelection(objects[0]);
			this.viewer.setSelection(selection);
		}
		System.out.println();
	}
	
	public abstract void addElement(Object element);
	
	public abstract void addElement(Object parentElement, Object childElement);
	
	public void updateElement(Object element)
	{
		this.viewer.refresh(element);
		// viewer.update(element, null);
	}
	
	public abstract void removeElement(Object element);
	
	public StructuredViewer getViewer()
	{
		return this.viewer;
	}
	
	public void setContentProvider(ContentProvider contentProvider)
	{
		this.viewer.setContentProvider(contentProvider);
	}
	
	public void setLabelProvider(IBaseLabelProvider labelProvider)
	{
		this.viewer.setLabelProvider(labelProvider);
	}
	
	public void setSorter(ViewerSorter sorter)
	{
		this.viewer.setSorter(sorter);
	}
	
	public IContentProvider getContentProvider()
	{
		return this.viewer.getContentProvider();
	}
	
	public IBaseLabelProvider getLabelProvider()
	{
		return this.viewer.getLabelProvider();
	}
	
	public ViewerSorter getSorter()
	{
		return this.viewer.getSorter();
	}
	
	public void addPage(String key, Page page)
	{
		page.createControl(this.content);
		this.currentPage = page;
		this.pages.put(key, page);
	}
	
	public Page[] getPages()
	{
		return (Page[]) this.pages.values().toArray(new Page[0]);
	}
	
	public int getPageCount()
	{
		return this.pages.size();
	}
	
	public Page getSelectedPage()
	{
		return this.currentPage;
	}
	
	public Page getCurrentPage()
	{
		return this.currentPage;
	}
	
	public void selectPage(String key)
	{
		this.currentPage = (Page) this.pages.get(key);
		if (this.currentPage != null)
		{
			if (((StackLayout) this.getPageContainer().getLayout()).topControl == null
							|| !((StackLayout) this.getPageContainer().getLayout()).topControl.equals(this.currentPage
											.getControl()))
			{
				((StackLayout) this.getPageContainer().getLayout()).topControl = this.currentPage.getControl();
				this.getPageContainer().layout();
			}
		}
	}
	
	public abstract void prepareForNewItem(IStructuredSelection selection);
	
	/**
	 * 
	 */
	public void selectionChanged(SelectionChangedEvent e)
	{
		this.change = true;
		if (this.currentPage.getStore().needsSaving())
		{
			if (this.askForSave(this.oldSelection) == 0)
			{
				this.currentPage.doStore();
				DBResult dbResult = this.currentPage.getStore().save();
				if (dbResult.getErrorCode() == 0)
				{
					this.setSelection();
					this.viewer.getControl().setFocus();
					this.currentPage.getContainer().updateButtons();
					Event evt = new Event();
					evt.widget = this;
					evt.detail = 0;
					evt.data = this.currentPage.getStore().getElement();
					this.notifyListeners(SWT.Selection, evt);
					this.viewer.setSelection(e.getSelection());
				}
				else
					this.change = false;
			}
		}
		if (this.change)
		{
			if (e.getSelectionProvider() instanceof StructuredViewer)
			{
				StructuredSelection selection = (StructuredSelection) e.getSelection();
				this.oldSelection = selection;
				if (!selection.isEmpty())
				{
					Object element = selection.getFirstElement();
					this.selectPage(element.getClass().getName());
					this.currentPage.selectionChanged(element);
				}
			}
		}
	}
	
	/**
	 * prepares table and store to add a new item
	 */
	public void add()
	{
		IStructuredSelection selection = (IStructuredSelection) this.viewer.getSelection();
		int result = 0;
		if (this.currentPage.getStore().needsSaving())
		{
			MessageDialog dialog = new MessageDialog(
							this.getShell(),
							Messages.getString("SashForm._u00C4nderungen_speichern_1"), //$NON-NLS-1$
							MessageDialog.getImage(MessageDialog.DLG_IMG_QUESTION),
							Messages.getString("SashForm.Sollen_die__u00C4nderungen_am_aktuellen_Datensatz_gespeichert_werden__2"), //$NON-NLS-1$
							MessageDialog.QUESTION, SashForm.yesNoCancelButtonLabels, 0);
			result = dialog.open();
			if (result == 0)
			{
				this.currentPage.getStore().save();
			}
		}
		if (result == 0 || result == 1)
		{
			this.prepareForNewItem(selection);
		}
	}
	
	/**
	 * saves changes made to the selected item
	 */
	public void save()
	{
		if (this.currentPage.getStore().needsSaving())
		{
			this.currentPage.doStore();
			DBResult dbResult = this.currentPage.getStore().save();
			if (dbResult.getErrorCode() == 0)
			{
				this.setSelection();
				this.viewer.getControl().setFocus();
				this.currentPage.refresh();
				this.currentPage.getContainer().updateButtons();
				
				Event e = new Event();
				e.widget = this;
				e.detail = 0;
				e.data = this.currentPage.getStore().getElement();
				this.notifyListeners(SWT.Selection, e);
				this.getActionProvider().refresh();
			}
			else if (dbResult.getErrorCode() == -1)
			{
				if (this.currentPage instanceof ProductGroupFieldEditorPage)
				{
					((ProductGroupFieldEditorPage) this.currentPage).getGalileoIdField().setFocus();
					return;
				}
			}
			else
			{
				this.currentPage.performDefaults();
				this.currentPage.getStore().setDirty(false);
				this.currentPage.getContainer().updateButtons();
			}
		}
	}
	
	protected abstract void setSelection();
	
	/**
	 * resets the default values
	 */
	public void reset()
	{
		if (this.currentPage.getStore().needsSaving())
		{
			this.currentPage.performDefaults();
			this.currentPage.getStore().setDirty(false);
			this.currentPage.getContainer().updateButtons();
		}
	}
	
	/**
	 * deletes the selected item in the list
	 */
	public void delete()
	{
		IStructuredSelection selection = (IStructuredSelection) this.viewer.getSelection();
		if (!selection.isEmpty())
		{
			if (this.currentPage.getStore().isDeletable())
			{
				if (this.askForDelete(selection) == 0)
				{
					Iterator i = selection.iterator();
					while (i.hasNext())
					{
						this.removeElement(i.next());
					}
					Event e = new Event();
					e.widget = this;
					e.detail = 1;
					e.data = selection;
					this.notifyListeners(SWT.Selection, e);
				}
			}
			else
			{
				MessageDialog.openInformation(this.viewer.getControl().getShell(),
								"Fehler", this.currentPage.getStore().getErrorMessage()); //$NON-NLS-1$
			}
		}
	}
	
	public int askForDelete(IStructuredSelection selection)
	{
		int size = selection.size();
		String title = ""; //$NON-NLS-1$
		String text = ""; //$NON-NLS-1$
		if (size == 1)
		{
			title = Messages.getString("SashForm.Eintrag_l_u00F6schen_6"); //$NON-NLS-1$
			text = Messages.getString("SashForm.Soll_der_gew_u00E4hlte_Eintrag__7") + (this.actionProvider == null ? "" : this.actionProvider.getName(selection.getFirstElement())) + Messages.getString("SashForm._gel_u00F6scht_werden__9"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		else
		{
			title = Messages.getString("SashForm.Eintr_u00E4ge_l_u00F6schen_10"); //$NON-NLS-1$
			text = Messages.getString("SashForm.Sollen_die_gew_u00E4hlten_Eintr_u00E4ge_gel_u00F6scht_werden__11"); //$NON-NLS-1$
		}
		
		MessageDialog dialog = new MessageDialog(this.getShell(), title,
						MessageDialog.getImage(MessageDialog.DLG_IMG_QUESTION), text, MessageDialog.QUESTION,
						SashForm.yesNoButtonLabels, 0);
		return dialog.open();
	}
	
	public int askForSave(StructuredSelection selection)
	{
		if (selection == null)
		{
			return -1;
		}
		if (selection.size() == 1)
		{
			String title = Messages.getString("SashForm._u00C4nderungen_speichern_12"); //$NON-NLS-1$
			String text = Messages.getString("SashForm.Sie_haben__u00C4nderungen_am_Eintrag__13") + this.currentPage.getElementName() + Messages.getString("SashForm._vorgenommen._Sollen_die__u00C4nderungen_gespeichert_werden__14"); //$NON-NLS-1$ //$NON-NLS-2$
			MessageDialog dialog = new MessageDialog(this.getShell(), title,
							MessageDialog.getImage(MessageDialog.DLG_IMG_QUESTION), text, MessageDialog.QUESTION,
							SashForm.yesNoButtonLabels, 0);
			return dialog.open();
		}
		return -1;
	}
	
	public void setActionProvider(ActionProvider provider)
	{
		this.actionProvider = provider;
	}
	
	public ActionProvider getActionProvider()
	{
		return this.actionProvider;
	}
	
	protected Composite left = null;
	protected Composite right = null;
	protected Hashtable pages = new Hashtable();
	protected Page currentPage;
	protected int[] weights = new int[]
	{ 1, 1 };
	protected Point minimumPageSize = new Point(400, 400);
	
	protected Composite titleArea;
	protected Composite content;
	protected CLabel messageLabel;
	protected String message = ""; //$NON-NLS-1$
	protected Color titleAreaColor;
	protected Label titleImage;
	protected Color normalMsgAreaBackground;
	protected Color errorMsgAreaBackground;
	protected Color normalMsgAreaForeground;
	protected Color errorMsgAreaForeground;
	protected String errorMessage;
	protected Image messageImage;
	protected Image errorMsgImage;
	protected boolean showingError = false;
	
	protected StructuredViewer viewer;
	protected Composite selector;
	protected ActionProvider actionProvider;
	protected StructuredSelection oldSelection = null;
	
	protected static final String[] yesNoButtonLabels =
	{ Messages.getString("SashForm.Ja_15"), Messages.getString("SashForm.Nein_16") }; //$NON-NLS-1$ //$NON-NLS-2$
	protected static final String[] yesNoCancelButtonLabels =
	{
					Messages.getString("SashForm.Ja_17"), Messages.getString("SashForm.Nein_18"), Messages.getString("SashForm.Abbrechen_19") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	
	protected int state = 0;
	private boolean change = true;
	
	protected static final int INSTANTIATED = 0;
	protected static final int VIEWER_INITIALIZED = 1;
	protected static final int VIEWER_CONTENT_INITIALIZED = 2;
	
}
