/*
 * Created on 13.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.gui.container;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ListSashForm extends SashForm
{
	
	/**
	 * @param parent
	 * @param style
	 */
	public ListSashForm(Composite parent, int style, String identity)
	{
		super(parent, style, identity);
	}
	
	protected StructuredViewer createViewer(Composite parent)
	{
		ListViewer lv = new ListViewer(parent);
		lv.getList().setLayoutData(new GridData(GridData.FILL_BOTH));
		lv.addPostSelectionChangedListener(this);
		return lv;
	}
	
	protected void initializeViewer()
	{
	}
	
	public void initializeContent()
	{
		this.viewer.setInput(((IStructuredContentProvider) this.viewer.getContentProvider()).getElements(new Object()));
		if (this.viewer.getInput() != null)
		{
			this.viewer.setSelection(new StructuredSelection((Object[]) this.viewer.getInput()));
		}
	}
	
	/**
	 * Die Selektion in der Liste auf das aktuell im Buffer liegende Element
	 * setzen.
	 */
	protected void setSelection()
	{
		this.viewer.setSelection(new StructuredSelection(this.currentPage.getStore().getElement()));
		if (this.viewer.getSelection().isEmpty())
		{
			this.addElement(this.currentPage.getStore().getElement());
		}
	}
	
	public void addElement(Object element)
	{
		((ListViewer) this.viewer).add(element);
	}
	
	public void addElement(Object parentElement, Object childElement)
	{
		this.addElement(childElement);
	}
	
	public void removeElement(Object element)
	{
		((ListViewer) this.viewer).remove(element);
	}
	
	public void prepareForNewItem(IStructuredSelection selection)
	{
		((ListViewer) this.getViewer()).getList().deselectAll();
		this.getSelectedPage().getStore().initialize();
		this.getSelectedPage().initFocus();
	}
	
	protected int[] weights =
	{ 6, 8 };
	
}
