/*
 * Created on 05.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.coin;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.gui.widget.DoubleFieldEditor;
import ch.eugster.pos.admin.gui.widget.FieldEditorPage;
import ch.eugster.pos.admin.gui.widget.LabelFieldEditor;
import ch.eugster.pos.admin.gui.widget.LongFieldEditor;
import ch.eugster.pos.admin.gui.widget.StringFieldEditor;
import ch.eugster.pos.admin.gui.widget.UniquePrimaryKeyValidator;
import ch.eugster.pos.db.Coin;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class CoinFieldEditorPage extends FieldEditorPage
{
	
	/**
	 * @param style
	 */
	public CoinFieldEditorPage(String name, int style, PersistentDBStore store)
	{
		super(name, style, store);
	}
	
	/**
	 * @param title
	 * @param style
	 */
	public CoinFieldEditorPage(String name, String title, int style, PersistentDBStore store)
	{
		super(name, title, style, store);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public CoinFieldEditorPage(String name, String title, ImageDescriptor image, int style, PersistentDBStore store)
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
		if (!Coin.isIdFieldAutoincrement(Coin.class))
		{
			LongFieldEditor idEditor = new LongFieldEditor(CoinFieldEditorPage.KEY_ID, Messages
							.getString("CoinFieldEditorPage.Id*_1"), this.getFieldEditorParent()); //$NON-NLS-1$
			idEditor.setContentValidator(new UniquePrimaryKeyValidator(idEditor, Coin.class));
			this.addField(idEditor);
		}
		
		this.valueEditor = new DoubleFieldEditor(CoinFieldEditorPage.KEY_VALUE, Messages
						.getString("CoinFieldEditorPage.Wert_1"), this.getFieldEditorParent()); //$NON-NLS-1$
		this.valueEditor.setTextLimit(10);
		this.valueEditor.setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
		this.addField(this.valueEditor);
		
		LabelFieldEditor commentEditor = new LabelFieldEditor(Messages
						.getString("CoinFieldEditorPage.*Eindeutiger_Wert_zwingend_2"), this.getFieldEditorParent()); //$NON-NLS-1$
		this.addField(commentEditor);
	}
	
	public boolean isInstance(Object element)
	{
		return element instanceof Coin;
	}
	
	public String getElementName()
	{
		Coin coin = (Coin) this.getStore().getElement();
		return Double.toString(coin.value);
	}
	
	private DoubleFieldEditor valueEditor;
	
	public static final String KEY_ID = "id"; //$NON-NLS-1$
	public static final String KEY_VALUE = "value"; //$NON-NLS-1$
}
