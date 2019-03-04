/*
 * Created on 05.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.coin;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.pos.admin.gui.widget.FieldEditorPage;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class CoinCurrencyFieldEditorPage extends FieldEditorPage
{
	
	/**
	 * @param style
	 */
	public CoinCurrencyFieldEditorPage(String name, int style, PersistentDBStore store)
	{
		super(name, style, store);
	}
	
	/**
	 * @param title
	 * @param style
	 */
	public CoinCurrencyFieldEditorPage(String name, String title, int style, PersistentDBStore store)
	{
		super(name, title, style, store);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public CoinCurrencyFieldEditorPage(String name, String title, ImageDescriptor image, int style,
					PersistentDBStore store)
	{
		super(name, title, image, style, store);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.FieldEditorPage#createFieldEditors()
	 */
	protected void createFieldEditors()
	{
	}
	
	public boolean isInstance(Object element)
	{
		return element instanceof ForeignCurrency;
	}
	
	public String getElementName()
	{
		ForeignCurrency currency = (ForeignCurrency) this.getStore().getElement();
		return currency.name;
	}
}
