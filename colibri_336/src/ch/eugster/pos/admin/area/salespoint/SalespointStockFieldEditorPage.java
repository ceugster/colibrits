/*
 * Created on 05.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.salespoint;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.gui.widget.DoubleFieldEditor;
import ch.eugster.pos.admin.gui.widget.FieldEditorPage;
import ch.eugster.pos.admin.gui.widget.StringFieldEditor;
import ch.eugster.pos.db.Stock;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class SalespointStockFieldEditorPage extends FieldEditorPage
{
	
	/**
	 * @param style
	 */
	public SalespointStockFieldEditorPage(String name, int style, PersistentDBStore store)
	{
		super(name, style, store);
	}
	
	/**
	 * @param title
	 * @param style
	 */
	public SalespointStockFieldEditorPage(String name, String title, int style, PersistentDBStore store)
	{
		super(name, title, style, store);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public SalespointStockFieldEditorPage(String name, String title, ImageDescriptor image, int style,
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
		this.stockEditor = new DoubleFieldEditor(SalespointStockFieldEditorPage.KEY_STOCK, Messages
						.getString("SalespointFieldEditorPage.Kassastock_1"), this.getFieldEditorParent()); //$NON-NLS-1$
		this.stockEditor.setTextLimit(10);
		this.stockEditor.setEmptyStringAllowed(true);
		this.stockEditor.setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
		this.addField(this.stockEditor);
	}
	
	public boolean isInstance(Object element)
	{
		return element instanceof Stock;
	}
	
	public String getElementName()
	{
		Stock stock = (Stock) this.getStore().getElement();
		return stock.getForeignCurrency().code;
	}
	
	private DoubleFieldEditor stockEditor;
	
	public static final String KEY_STOCK = "stock"; //$NON-NLS-1$
}
