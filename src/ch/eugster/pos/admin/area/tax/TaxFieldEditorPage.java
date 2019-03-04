/*
 * Created on 05.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.tax;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.gui.widget.ComboFieldEditor;
import ch.eugster.pos.admin.gui.widget.FieldEditorPage;
import ch.eugster.pos.admin.gui.widget.LongFieldEditor;
import ch.eugster.pos.admin.gui.widget.StringFieldEditor;
import ch.eugster.pos.admin.gui.widget.UniquePrimaryKeyValidator;
import ch.eugster.pos.db.Tax;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class TaxFieldEditorPage extends FieldEditorPage
{
	
	/**
	 * @param style
	 */
	public TaxFieldEditorPage(String name, int style, PersistentDBStore store)
	{
		super(name, style, store);
	}
	
	/**
	 * @param title
	 * @param style
	 */
	public TaxFieldEditorPage(String name, String title, int style, PersistentDBStore store)
	{
		super(name, title, style, store);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public TaxFieldEditorPage(String name, String title, ImageDescriptor image, int style, PersistentDBStore store)
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
		if (!Tax.isIdFieldAutoincrement(Tax.class))
		{
			LongFieldEditor idEditor = new LongFieldEditor(TaxFieldEditorPage.KEY_ID, Messages
							.getString("TaxFieldEditorPage.Id*_1"), this.getFieldEditorParent()); //$NON-NLS-1$
			idEditor.setContentValidator(new UniquePrimaryKeyValidator(idEditor, Tax.class));
			this.addField(idEditor);
		}
		
		this.codeEditor = new StringFieldEditor(
						TaxFieldEditorPage.KEY_CODE,
						Messages.getString("TaxFieldEditorPage.Code_1"), -1, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		this.codeEditor.setEmptyStringAllowed(true);
		this.addField(this.codeEditor);
		
		this.galileoIdEditor = new StringFieldEditor(
						TaxFieldEditorPage.KEY_GALILEO_ID,
						Messages.getString("TaxFieldEditorPage.K_u00FCrzel_Galileo_2"), 3, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		this.galileoIdEditor.setEmptyStringAllowed(true);
		this.galileoIdEditor.setTextLimit(3);
		this.addField(this.galileoIdEditor);
		
		this.fibuIdEditor = new StringFieldEditor(
						TaxFieldEditorPage.KEY_CODE128_ID,
						Messages.getString("TaxFieldEditorPage.K_u00FCrzel_code128_3"), 3, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		this.fibuIdEditor.setEmptyStringAllowed(true);
		this.fibuIdEditor.setTextLimit(3);
		this.addField(this.fibuIdEditor);
		
		this.accountEditor = new StringFieldEditor(
						TaxFieldEditorPage.KEY_ACCOUNT,
						Messages.getString("TaxFieldEditorPage.Konto_4"), -1, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		this.accountEditor.setTextLimit(10);
		this.addField(this.accountEditor);
		
		this.currentTaxEditor = new ComboFieldEditor(TaxFieldEditorPage.KEY_CURRENT_TAX, Messages
						.getString("TaxFieldEditorPage.Aktueller_Steuersatz_5"), this.getFieldEditorParent()); //$NON-NLS-1$
		this.currentTaxEditor.setContentProvider(new CurrentTaxComboFieldEditorContentProvider(this.currentTaxEditor));
		this.currentTaxEditor.setLabelProvider(new CurrentTaxComboFieldEditorLabelProvider());
		this.getStore().addPropertyChangeListener(this.currentTaxEditor.getContentProvider());
		this.currentTaxEditor.setInput(null);
		this.addField(this.currentTaxEditor);
	}
	
	public boolean isInstance(Object element)
	{
		return element instanceof Tax;
	}
	
	public String getElementName()
	{
		Tax tax = (Tax) this.getStore().getElement();
		return tax.getTaxType().name + " " + tax.getTaxRate().name; //$NON-NLS-1$
	}
	
	public void initFocus()
	{
		this.performDefaults();
		// WizardDialog wd = new WizardDialog(getShell(), new TaxWizard());
		// wd.setBlockOnOpen(true);
		// wd.open();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(
	 * org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(Object element)
	{
		super.selectionChanged(element);
		if (this.getStore().getElement() instanceof Tax)
		{
			Tax tax = (Tax) this.getStore().getElement();
			this.galileoIdEditor.setEnabled(tax.getTaxType().code.equals("U"), this.getFieldEditorParent());
			this.fibuIdEditor.setEnabled(tax.getTaxType().code.equals("U"), this.getFieldEditorParent());
		}
	}
	
	private StringFieldEditor codeEditor;
	private StringFieldEditor galileoIdEditor;
	private StringFieldEditor fibuIdEditor;
	private StringFieldEditor accountEditor;
	private ComboFieldEditor currentTaxEditor;
	
	public static final String KEY_ID = "id"; //$NON-NLS-1$
	public static final String KEY_CODE = "code"; //$NON-NLS-1$
	public static final String KEY_GALILEO_ID = "galileoId"; //$NON-NLS-1$
	public static final String KEY_CODE128_ID = "code128Id"; //$NON-NLS-1$
	public static final String KEY_ACCOUNT = "account"; //$NON-NLS-1$
	public static final String KEY_CURRENT_TAX = "currentTax"; //$NON-NLS-1$
	
}
