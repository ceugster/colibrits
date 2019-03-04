/*
 * Created on 13.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.gui.container;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;

import ch.eugster.pos.admin.model.ContentProvider;
import ch.eugster.pos.admin.model.TreeContentProvider;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class TreeSashForm extends SashForm
{
	
	/**
	 * @param parent
	 * @param style
	 */
	public TreeSashForm(Composite parent, int style, String identity)
	{
		super(parent, style, identity);
	}
	
	protected StructuredViewer createViewer(Composite parent)
	{
		TreeViewer tv = new TreeViewer(parent);
		tv.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		tv.addPostSelectionChangedListener(this);
		return tv;
	}
	
	/**
	 * Die Selektion in der Liste auf das aktuell im Buffer liegende Element
	 * setzen.
	 */
	protected void setSelection()
	{
		Object element = this.currentPage.getStore().getElement();
		((TreeViewer) this.viewer).expandToLevel(element, 0);
		this.viewer.setSelection(new StructuredSelection(element));
		if (this.viewer.getSelection().isEmpty())
		{
			TreeContentProvider tcp = (TreeContentProvider) ((TreeViewer) this.viewer).getContentProvider();
			Object parent = tcp.getParent(element);
			this.addElement(parent, element);
			this.viewer.setSelection(new StructuredSelection(element));
		}
		else
		{
			this.updateElement(((IStructuredSelection) this.viewer.getSelection()).getFirstElement());
		}
	}
	
	public void addElement(Object element)
	{
	}
	
	public void addElement(Object parentElement, Object childElement)
	{
		if (parentElement == null)
		{
			this.initializeContent();
			// viewer.refresh();
		}
		else
		{
			((TreeViewer) this.viewer).add(parentElement, childElement);
		}
	}
	
	public void removeElement(Object element)
	{
		if (((ContentProvider) this.viewer.getContentProvider()).deleteElement(element))
		{
			((TreeViewer) this.viewer).remove(element);
			this.viewer.refresh();
			if (this.viewer.getSelection().isEmpty())
			{
				Object input = this.viewer.getInput();
				if (input instanceof Object[])
				{
					Object[] inputs = (Object[]) input;
					if (inputs.length > 0)
					{
						TreeItem[] items = ((TreeViewer) this.viewer).getTree().getItems();
						TreeItem[] sel = new TreeItem[1];
						sel[0] = items[0];
						((TreeViewer) this.viewer).getTree().setSelection(sel);
						// StructuredSelection sel = new
						// StructuredSelection(inputs[0]);
						// viewer.setSelection(sel);
						// StructuredSelection sel1 =
						// (StructuredSelection)viewer.getSelection();
						// sel1.getFirstElement();
					}
				}
			}
		}
	}
	
	protected void expand(Object node)
	{
		((TreeViewer) this.viewer).expandToLevel(node, 1);
	}
	
	public void prepareForNewItem(IStructuredSelection selection)
	{
		this.currentPage.getStore().initialize(selection);
		this.currentPage.getContainer().updateButtons();
		this.currentPage.initFocus();
	}
	
	protected int[] weights =
	{ 1, 2 };
}
