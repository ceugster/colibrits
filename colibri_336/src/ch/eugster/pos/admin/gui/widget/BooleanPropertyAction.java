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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import ch.eugster.pos.swt.IStore;

public class BooleanPropertyAction extends Action
{
	
	private IStore preferenceStore;
	private String property;
	
	public BooleanPropertyAction(String title, IStore preferenceStore, String property) throws IllegalArgumentException
	{
		super(title, IAction.AS_CHECK_BOX);
		
		if (preferenceStore == null || property == null) throw new IllegalArgumentException();
		
		this.preferenceStore = preferenceStore;
		this.property = property;
		final String finalProprety = property;
		
		preferenceStore.addPropertyChangeListener(new IPropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent event)
			{
				if (finalProprety.equals(event.getProperty()))
					BooleanPropertyAction.this.setChecked(Boolean.TRUE.equals(event.getNewValue()));
			}
		});
		
		this.setChecked(preferenceStore.getBoolean(property).booleanValue());
	}
	
	public void run()
	{
		this.preferenceStore.setValue(this.property, new Boolean(this.isChecked()));
	}
}
