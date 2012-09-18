/*
 * Created on 05.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.currency;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.MainWindow;
import ch.eugster.pos.admin.gui.container.TableSashForm;
import ch.eugster.pos.admin.gui.widget.DoubleFieldEditor;
import ch.eugster.pos.admin.gui.widget.FieldEditorPage;
import ch.eugster.pos.admin.gui.widget.LabelFieldEditor;
import ch.eugster.pos.admin.gui.widget.LongFieldEditor;
import ch.eugster.pos.admin.gui.widget.StringFieldEditor;
import ch.eugster.pos.admin.gui.widget.UniquePrimaryKeyValidator;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class CurrencyFieldEditorPage extends FieldEditorPage implements Listener
{
	
	/**
	 * @param style
	 */
	public CurrencyFieldEditorPage(String name, int style, PersistentDBStore store)
	{
		super(name, style, store);
	}
	
	/**
	 * @param title
	 * @param style
	 */
	public CurrencyFieldEditorPage(String name, String title, int style, PersistentDBStore store)
	{
		super(name, title, style, store);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public CurrencyFieldEditorPage(String name, String title, ImageDescriptor image, int style, PersistentDBStore store)
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
		if (!ForeignCurrency.isIdFieldAutoincrement(ForeignCurrency.class))
		{
			LongFieldEditor idEditor = new LongFieldEditor(CurrencyFieldEditorPage.KEY_ID, Messages
							.getString("CurrencyFieldEditorPage.Id*_1"), this.getFieldEditorParent()); //$NON-NLS-1$
			idEditor.setContentValidator(new UniquePrimaryKeyValidator(idEditor, ForeignCurrency.class));
			this.addField(idEditor);
		}
		
		this.codeEditor = new StringFieldEditor(
						CurrencyFieldEditorPage.KEY_CODE,
						Messages.getString("CurrencyFieldEditorPage.Code_1"), 3, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		this.codeEditor.setEmptyStringAllowed(false);
		this.codeEditor.setCapitalizationOn(true);
		this.codeEditor.setErrorMessage(Messages
						.getString("CurrencyFieldEditorPage.Die_Angabe_des_Codes_ist_notwendig._2")); //$NON-NLS-1$
		this.codeEditor.showErrorMessage();
		this.addField(this.codeEditor);
		
		this.nameEditor = new StringFieldEditor(
						CurrencyFieldEditorPage.KEY_NAME,
						Messages.getString("CurrencyFieldEditorPage.Bezeichnung_3"), -1, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		this.nameEditor.setEmptyStringAllowed(true);
		this.addField(this.nameEditor);
		
		this.regionEditor = new StringFieldEditor(
						CurrencyFieldEditorPage.KEY_REGION,
						Messages.getString("CurrencyFieldEditorPage.Bereich_4"), -1, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		this.regionEditor.setEmptyStringAllowed(true);
		this.addField(this.regionEditor);
		
		this.quotationEditor = new DoubleFieldEditor(CurrencyFieldEditorPage.KEY_QUOTATION, Messages
						.getString("PaymentTypeFieldEditorPage.Kurs_5"), this.getFieldEditorParent()); //$NON-NLS-1$
		this.quotationEditor.setTextLimit(10);
		this.quotationEditor.setEmptyStringAllowed(false);
		this.quotationEditor.setFractionDigits(0, 6);
		this.quotationEditor.setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
		this.addField(this.quotationEditor);
		
		this.roundEditor = new DoubleFieldEditor(CurrencyFieldEditorPage.KEY_ROUND_FACTOR,
						"Rundungsfaktor", this.getFieldEditorParent()); //$NON-NLS-1$
		this.roundEditor.setTextLimit(10);
		this.roundEditor.setEmptyStringAllowed(false);
		this.roundEditor.setFractionDigits(0, 6);
		this.roundEditor.setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
		this.addField(this.roundEditor);
		
		LabelFieldEditor commentEditor = new LabelFieldEditor(Messages
						.getString("CurrencyFieldEditorPage.*Eindeutiger_Wert_zwingend_2"), this.getFieldEditorParent()); //$NON-NLS-1$
		this.addField(commentEditor);
	}
	
	public boolean isInstance(Object element)
	{
		return element instanceof ForeignCurrency;
	}
	
	public String getElementName()
	{
		ForeignCurrency c = (ForeignCurrency) this.getStore().getElement();
		return c.name;
	}
	
	public void handleEvent(Event e)
	{
		boolean dirty = this.getStore().needsSaving();
		if (e.widget instanceof TableSashForm)
		{
			MainWindow mw = (MainWindow) this.getContainer();
			TableSashForm tsf = (TableSashForm) mw.getSelectionChangedSource("foreignCurrency"); //$NON-NLS-1$
			if (tsf != null)
			{
				tsf.initializeContent();
				this.getStore().setDirty(dirty ? true : false);
			}
		}
	}
	
	private StringFieldEditor codeEditor;
	private StringFieldEditor nameEditor;
	private StringFieldEditor regionEditor;
	private DoubleFieldEditor quotationEditor;
	private DoubleFieldEditor roundEditor;
	
	public static final String KEY_ID = "id"; //$NON-NLS-1$
	public static final String KEY_CODE = "code"; //$NON-NLS-1$
	public static final String KEY_NAME = "name"; //$NON-NLS-1$
	public static final String KEY_REGION = "region"; //$NON-NLS-1$
	public static final String KEY_QUOTATION = "quotation"; //$NON-NLS-1$
	public static final String KEY_ROUND_FACTOR = "round-factor"; //$NON-NLS-1$
}
