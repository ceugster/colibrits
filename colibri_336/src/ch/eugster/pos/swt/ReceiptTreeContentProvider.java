/*
 * Created on 29.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.swt;

import java.io.File;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ReceiptTreeContentProvider implements ITreeContentProvider
{
	
	private File root = null;
	
	/**
	 * 
	 */
	public ReceiptTreeContentProvider(File root)
	{
		super();
		this.root = root;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java
	 * .lang.Object)
	 */
	public Object[] getElements(Object inputElement)
	{
		if (inputElement instanceof File)
		{
			File root = (File) inputElement;
			return new File[]
			{ root };
		}
		else if (inputElement instanceof File[])
		{
			File[] dates = (File[]) inputElement;
			return dates;
		}
		else
		{
			return new Object[]
			{};
		}
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
	 * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.
	 * Object)
	 */
	public Object[] getChildren(Object parentElement)
	{
		Object[] children = new Object[]
		{};
		if (parentElement instanceof File)
		{
			File dir = (File) parentElement;
			if (dir.isDirectory())
			{
				children = dir.listFiles();
			}
		}
		return children;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object
	 * )
	 */
	public Object getParent(Object element)
	{
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.
	 * Object)
	 */
	public boolean hasChildren(Object element)
	{
		boolean hasChildren = false;
		if (element instanceof File)
		{
			File dir = (File) element;
			if (dir.isDirectory())
			{
				hasChildren = dir.listFiles().length > 0;
			}
		}
		return hasChildren;
	}
	
}
