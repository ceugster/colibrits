/*
 * Created on 28.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.gui.container;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import ch.eugster.pos.Messages;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public abstract class ActionProvider implements IActionProvider
{
	
	/**
	 * 
	 */
	public ActionProvider(SashForm parent)
	{
		super();
		this.parent = parent;
		this.buildMenu();
	}
	
	public void buildMenu()
	{
		final StructuredViewer view = this.parent.getViewer();
		final Control ctrl = view.getControl();
		if (ctrl.getMenu() == null)
		{
			ctrl.setMenu(new Menu(ctrl));
			ctrl.getMenu().addListener(SWT.Show, new Listener()
			{
				public void handleEvent(Event event)
				{
					MenuItem[] items = ctrl.getMenu().getItems();
					for (int i = 0; i < items.length; i++)
					{
						items[i].dispose();
					}
					ActionProvider.this.addMenuItems(view);
				}
			});
		}
	}
	
	public void addMenuItems(StructuredViewer viewer)
	{
	}
	
	protected void createMenuItem(String text, Integer id, boolean enabled)
	{
		MenuItem item = new MenuItem(this.parent.getViewer().getControl().getMenu(), SWT.PUSH);
		item.setText(text);
		item.setData(id);
		item.setEnabled(enabled);
		item.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				ActionProvider.this.performMenuSelection(e);
			}
		});
	}
	
	protected void createMenuItem(String text, Integer id)
	{
		this.createMenuItem(text, id, true);
	}
	
	protected void createMenuSeparator()
	{
		new MenuItem(this.parent.getViewer().getControl().getMenu(), SWT.SEPARATOR);
	}
	
	protected void selectPage(Object element)
	{
		this.parent.selectPage(element.getClass().getName());
		if (this.parent.getSelectedPage() != null)
		{
			this.parent.getSelectedPage().selectionChanged(element);
			this.parent.getSelectedPage().getContainer().updateButtons();
		}
	}
	
	protected abstract String getName(Object element);
	
	protected abstract String getErrorMessage();
	
	public abstract boolean isChildOf(Object child, Object parent);
	
	public void refresh()
	{
	}
	
	protected SashForm parent;
	protected String errorMessage;
	
	protected static final String[] yesNoButtonLabels =
	{ Messages.getString("ActionProvider.Ja_1"), Messages.getString("ActionProvider.Nein_2") }; //$NON-NLS-1$ //$NON-NLS-2$
	protected static final String[] yesNoCancelButtonLabels =
	{
					Messages.getString("ActionProvider.Ja_3"), Messages.getString("ActionProvider.Nein_4"), Messages.getString("ActionProvider.Abbrechen_5") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
}
