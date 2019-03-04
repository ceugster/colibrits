/*
 * Created on 04.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.model;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public abstract class ContentProvider implements org.eclipse.jface.viewers.IContentProvider,
				ch.eugster.pos.admin.model.IContentProvider
{
	
	public void setViewer(StructuredViewer viewer)
	{
		this.viewer = viewer;
	}
	
	public StructuredViewer getViewer()
	{
		return this.viewer;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose()
	{
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface
	 * .viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.admin.model.IContentProvider#createSelector(org.eclipse
	 * .swt.widgets.Composite, org.eclipse.jface.viewers.StructuredViewer, int)
	 */
	public Composite createSelector(Composite parent, StructuredViewer viewer, int style)
	{
		return null;
	}
	
	protected StructuredViewer viewer;
}
