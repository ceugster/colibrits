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

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.swt.graphics.Point;

import ch.eugster.pos.swt.PersistentDBStore;

/**
 * An interface for a preference page. This interface is used primarily by the
 * page's container
 */
public interface IPage extends IDialogPage
{
	
	/**
	 * Computes a size for this page's UI component.
	 * 
	 * @return the size of the preference page encoded as
	 *         <code>new Point(width,height)</code>, or <code>(0,0)</code> if
	 *         the page doesn't currently have any UI component
	 */
	public Point computeSize();
	
	/**
	 * Returns whether this dialog page is in a valid state.
	 * 
	 * @return <code>true</code> if the page is in a valid state, and
	 *         <code>false</code> if invalid
	 */
	public boolean isValid();
	
	/**
	 * Checks whether it is alright to leave this page.
	 * 
	 * @return <code>false</code> to abort page flipping and the have the
	 *         current page remain visible, and <code>true</code> to allow the
	 *         page flip
	 */
	public boolean okToLeave();
	
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
	public void setStore(PersistentDBStore store);
	
	/**
	 * Sets or clears the container of this page.
	 * 
	 * @param preferencePageContainer
	 *            the preference page container, or <code>null</code>
	 */
	public void setContainer(IPageContainer pageContainer);
	
	/**
	 * Sets the size of this page's UI component.
	 * 
	 * @param size
	 *            the size of the preference page encoded as
	 *            <code>new Point(width,height)</code>
	 */
	public void setSize(Point size);
	
	/**
	 * Returns the data store of this data page.
	 * 
	 * @return the data store , or <code>null</code> if none
	 */
	public PersistentDBStore getStore();
	
	/**
	 * Refreshes the FieldEditors.
	 */
	public void refresh();
}
