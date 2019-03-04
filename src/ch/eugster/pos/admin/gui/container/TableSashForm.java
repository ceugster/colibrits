/*
 * Created on 13.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.gui.container;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

import ch.eugster.pos.admin.model.ContentProvider;
import ch.eugster.pos.swt.ITableLabelProvider;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class TableSashForm extends SashForm
{
	
	/**
	 * @param parent
	 * @param style
	 */
	public TableSashForm(Composite parent, int style, String identity)
	{
		super(parent, style, identity);
	}
	
	protected StructuredViewer createViewer(Composite parent)
	{
		TableViewer tv = new TableViewer(parent);
		tv.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		tv.addPostSelectionChangedListener(this);
		return tv;
	}
	
	protected void initializeViewer()
	{
		((TableViewer) this.viewer).getTable().setLinesVisible(false);
		((TableViewer) this.viewer).getTable().setHeaderVisible(true);
		String[] columnNames = ((ch.eugster.pos.swt.ITableLabelProvider) this.viewer.getLabelProvider())
						.getColumnNames();
		for (int i = 0; i < columnNames.length; i++)
		{
			TableColumn tc = new TableColumn(((TableViewer) this.viewer).getTable(), SWT.NULL);
			tc.setData(new Integer(i));
			tc.setText(columnNames[i]);
			tc.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					Integer col = (Integer) ((TableColumn) e.getSource()).getData();
					TableSashForm.this.viewer.setSorter(((ITableLabelProvider) TableSashForm.this.viewer
									.getLabelProvider()).getSorter(col.intValue()));
				}
			});
		}
	}
	
	public void initializeContent()
	{
		this.viewer.setInput(((IStructuredContentProvider) this.viewer.getContentProvider())
						.getElements(new RootItem()));
		this.setColumnsWidth();
		Object[] objects = (Object[]) this.viewer.getInput();
		// if (objects != null && objects.length > 0)
		// {
		// StructuredSelection selection = new StructuredSelection(objects[0]);
		// this.viewer.setSelection(selection);
		// }
		System.out.println();
	}
	
	private void setColumnsWidth()
	{
		for (int i = 0; i < ((TableViewer) this.viewer).getTable().getColumnCount(); i++)
		{
			((TableViewer) this.viewer).getTable().getColumn(i).pack();
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
		else
		{
			this.updateElement(((IStructuredSelection) this.viewer.getSelection()).getFirstElement());
			this.setColumnsWidth();
		}
	}
	
	public void addElement(Object element)
	{
		((TableViewer) this.viewer).add(element);
		this.viewer.setSelection(new StructuredSelection(element));
		this.setColumnsWidth();
	}
	
	public void addElement(Object parentElement, Object childElement)
	{
		this.addElement(childElement);
	}
	
	public void removeElement(Object element)
	{
		if (((ContentProvider) this.viewer.getContentProvider()).deleteElement(element))
		{
			int index = ((TableViewer) this.viewer).getTable().getSelectionIndex();
			((TableViewer) this.viewer).remove(element);
			int max = ((TableViewer) this.viewer).getTable().getItemCount();
			if (max > 0)
			{
				if (max > index)
				{
					this.viewer.setSelection(new StructuredSelection(((TableViewer) this.viewer).getElementAt(index)),
									true);
				}
				else
				{
					this.viewer.setSelection(
									new StructuredSelection(((TableViewer) this.viewer).getElementAt(max - 1)), true);
				}
			}
		}
	}
	
	public void prepareForNewItem(IStructuredSelection selection)
	{
		((TableViewer) this.getViewer()).getTable().deselectAll();
		this.selectPage(this.currentPage.getName());
		this.currentPage.getStore().initialize();
		this.currentPage.initialize();
		this.currentPage.initFocus();
	}
	
}
