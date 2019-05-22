/*
 * Created on 05.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.product;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.PropertyChangeEvent;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.gui.widget.BooleanFieldEditor;
import ch.eugster.pos.admin.gui.widget.ComboFieldEditor;
import ch.eugster.pos.admin.gui.widget.DoubleFieldEditor;
import ch.eugster.pos.admin.gui.widget.FieldEditorPage;
import ch.eugster.pos.admin.gui.widget.IntegerFieldEditor;
import ch.eugster.pos.admin.gui.widget.LongFieldEditor;
import ch.eugster.pos.admin.gui.widget.RadioGroupFieldEditor;
import ch.eugster.pos.admin.gui.widget.SpacerFieldEditor;
import ch.eugster.pos.admin.gui.widget.StringFieldEditor;
import ch.eugster.pos.admin.gui.widget.UniquePrimaryKeyValidator;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.db.Tax;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ProductGroupFieldEditorPage extends FieldEditorPage
{
	
	/**
	 * @param style
	 */
	public ProductGroupFieldEditorPage(String name, int style, PersistentDBStore store)
	{
		super(name, style, store);
	}
	
	/**
	 * @param title
	 * @param style
	 */
	public ProductGroupFieldEditorPage(String name, String title, int style, PersistentDBStore store)
	{
		super(name, title, style, store);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public ProductGroupFieldEditorPage(String name, String title, ImageDescriptor image, int style,
					PersistentDBStore store)
	{
		super(name, title, image, style, store);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.FieldEditorPage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors()
	{
		if (!ProductGroup.isIdFieldAutoincrement(ProductGroup.class))
		{
			this.idEditor = new LongFieldEditor(ProductGroupFieldEditorPage.KEY_ID,
							Messages.getString("ProductGroupFieldEditorPage.Id_1"), this.getFieldEditorParent()); //$NON-NLS-1$
			this.idEditor.setContentValidator(new UniquePrimaryKeyValidator(this.idEditor, ProductGroup.class));
			this.addField(this.idEditor);
		}
		
		this.nameEditor = new StringFieldEditor(
						ProductGroupFieldEditorPage.KEY_NAME,
						Messages.getString("ProductGroupFieldEditorPage.Bezeichnung_1"), -1, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		this.nameEditor.setEmptyStringAllowed(false);
		this.nameEditor.setErrorMessage(Messages
						.getString("ProductGroupFieldEditorPage.Die_Angabe_einer_Bezeichnung_ist_notwendig._2")); //$NON-NLS-1$
		this.nameEditor.showErrorMessage();
		this.addField(this.nameEditor);
		
		this.shortNameEditor = new StringFieldEditor(
						ProductGroupFieldEditorPage.KEY_SHORT_NAME,
						Messages.getString("ProductGroupFieldEditorPage.Beschriftung_3"), -1, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		this.shortNameEditor.setEmptyStringAllowed(true);
		this.addField(this.shortNameEditor);
		
		this.galileoIdEditor = new StringFieldEditor(
						ProductGroupFieldEditorPage.KEY_GALILEO_ID,
						Messages.getString("ProductGroupFieldEditorPage.GalileoId_4"), 3, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		this.galileoIdEditor.setEmptyStringAllowed(true);
		this.galileoIdEditor.setContentValidator(new GalileoIdFieldEditorContentValidator());
		this.addField(this.galileoIdEditor);
		
		String[][] labelAndValues =
		{
		{ "Umsatzrelevante Warengruppe", "0" },
		{ "Sonstige Verkäufe", "1" },
		{ "Ausgaben", "2" },
		{ "Geldeinlage", "3" },
		{ "Geldentnahme", "4" } };
		this.typeEditor = new RadioGroupFieldEditor(ProductGroupFieldEditorPage.KEY_TYPE,
						"Typ", 1, labelAndValues, this.getFieldEditorParent()); //$NON-NLS-1$
		this.addField(this.typeEditor);
		
		this.currencyEditor = new ComboFieldEditor(ProductGroupFieldEditorPage.KEY_CURRENCY,
						Messages.getString("PaymentTypeFieldEditorPage.W_u00E4hrung_4"), this.getFieldEditorParent()); //$NON-NLS-1$
		this.currencyEditor.setContentProvider(new CurrencyComboFieldEditorContentProvider(this.currencyEditor));
		this.currencyEditor.setLabelProvider(new CurrencyComboFieldEditorLabelProvider());
		this.currencyEditor.setInput(null);
		this.addField(this.currencyEditor);
		
		this.defaultTaxEditor = new ComboFieldEditor(
						ProductGroupFieldEditorPage.KEY_DEFAULT_TAX,
						Messages.getString("ProductGroupFieldEditorPage.Vorschlag_Steuer_10"), this.getFieldEditorParent()); //$NON-NLS-1$
		this.defaultTaxEditor.setContentProvider(new TaxComboFieldEditorContentProvider(this.defaultTaxEditor));
		this.defaultTaxEditor.setLabelProvider(new TaxComboFieldEditorLabelProvider());
		this.typeEditor.addPropertyChangeListener(this.defaultTaxEditor.getContentProvider());
		this.getStore().addPropertyChangeListener(this.defaultTaxEditor.getContentProvider());
		this.defaultTaxEditor.setInput(new Integer(ProductGroup.TYPE_INCOME));
		this.addField(this.defaultTaxEditor);
		
		this.addField(new SpacerFieldEditor(this.getFieldEditorParent()));
		
		this.defaultGroupEditor = new BooleanFieldEditor(ProductGroupFieldEditorPage.KEY_DEFAULT_GROUP,
						"Defaultgruppe", this.getFieldEditorParent()); //$NON-NLS-1$
		this.defaultGroupEditor.setEnabled(true, this.getFieldEditorParent());
		this.addField(this.defaultGroupEditor);
		
		this.ebookEditor = new BooleanFieldEditor(ProductGroupFieldEditorPage.KEY_EBOOK,
						"eBook", this.getFieldEditorParent()); //$NON-NLS-1$
		this.ebookEditor.setEnabled(true, this.getFieldEditorParent());
		this.addField(this.ebookEditor);
		
		// 10215
		this.paidInvoiceEditor = new BooleanFieldEditor(ProductGroupFieldEditorPage.KEY_PAID_INVOICE,
						"Bezahlte Rechnungen", this.getFieldEditorParent()); //$NON-NLS-1$
		this.paidInvoiceEditor.setEnabled(false, this.getFieldEditorParent());
		this.addField(this.paidInvoiceEditor);
		// 10215
		
		this.addField(new SpacerFieldEditor(this.getFieldEditorParent()));
		
		this.quantityEditor = new IntegerFieldEditor(
						ProductGroupFieldEditorPage.KEY_QUANTITY,
						Messages.getString("ProductGroupFieldEditorPage.Vorschlag_Menge_5"), this.getFieldEditorParent()); //$NON-NLS-1$
		this.quantityEditor.setTextLimit(3);
		this.quantityEditor.setEmptyStringAllowed(true);
		this.quantityEditor.setValidRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
		this.quantityEditor.setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
		this.addField(this.quantityEditor);
		
		this.priceEditor = new DoubleFieldEditor(
						ProductGroupFieldEditorPage.KEY_PRICE,
						Messages.getString("ProductGroupFieldEditorPage.Vorschlag_Preis_6"), this.getFieldEditorParent()); //$NON-NLS-1$
		this.priceEditor.setTextLimit(20);
		this.priceEditor.setEmptyStringAllowed(true);
		this.priceEditor.setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
		this.addField(this.priceEditor);
		
		this.optCodeEditor = new StringFieldEditor(
						ProductGroupFieldEditorPage.KEY_OPT_CODE,
						Messages.getString("ProductGroupFieldEditorPage.Vorschlag_Optionscode_7"), 1, this.getFieldEditorParent()); //$NON-NLS-1$
		this.optCodeEditor.setEmptyStringAllowed(true);
		this.optCodeEditor.setCapitalizationOn(true);
		this.addField(this.optCodeEditor);
		
		this.accountEditor = new StringFieldEditor(
						ProductGroupFieldEditorPage.KEY_ACCOUNT,
						Messages.getString("ProductGroupFieldEditorPage.Fibukonto_8"), -1, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		this.accountEditor.setEmptyStringAllowed(true);
		this.addField(this.accountEditor);
		
		this.addField(new SpacerFieldEditor(this.getFieldEditorParent()));
		
		this.exportIdEditor = new StringFieldEditor(ProductGroupFieldEditorPage.KEY_EXPORT_ID,
						"Export/Import-ID", 10, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		this.exportIdEditor.setEmptyStringAllowed(true);
		this.addField(this.exportIdEditor);
		
	}
	
	@Override
	public boolean isInstance(Object element)
	{
		return element instanceof ProductGroup;
	}
	
	@Override
	public boolean isDirty()
	{
		ProductGroup productGroup = (ProductGroup) this.getStore().getElement();
		return productGroup.modified;
	}
	
	@Override
	public String getElementName()
	{
		ProductGroup productGroup = (ProductGroup) this.getStore().getElement();
		return productGroup.name;
	}
	
	/**
	 * The field editor preference page implementation of this
	 * <code>IPreferencePage</code> (and <code>IPropertyChangeListener</code>)
	 * method intercepts <code>IS_VALID</code> events but passes other events on
	 * to its superclass.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		/**
		 * Hier muss sichergestellt werden, dass Nicht-Admin-Benutzer an der
		 * Default-WG nichts ändern können, was den Programmlauf beeinflussen
		 * könnte. Das betrifft insbesondere: 1. das Ändern der Default-WG 2.
		 * das Ändern der Galileo-Id in einer Default-WG. 3. das Ändern des
		 * WG-Typs (Default-WG muss immer umsatzrelevant sein). Änderungen, die
		 * durchden Admin gemacht werden, sollten zugelassen werden, damit doch
		 * Notfall-Eingriffe möglich sind. D.h. dass Admin im Falle Comelivres
		 * nur die Comelivres-Mitarbeiter sein sollten, die die Kasse kennen.
		 */
		if (this.idEditor != null)
		{
			this.idEditor.setEnabled(((ProductGroup) this.getStore().getElement()).getId() == null
							|| ((ProductGroup) this.getStore().getElement()).getId().equals(ProductGroup.ZERO_VALUE),
							this.getFieldEditorParent());
		}
		this.setValid(this.defaultTaxEditor.getContentProvider().doCheckState());
		
		// if (event.getSource() instanceof ProductGroupStore)
		// {
		if (event.getProperty().equals("type"))
		{
			this.defaultGroupEditor.setDefaultValue(false);
			this.defaultGroupEditor.setEnabled(false, this.getFieldEditorParent());
			this.ebookEditor.setDefaultValue(false);
			this.ebookEditor.setEnabled(false, this.getFieldEditorParent());
			Integer type = (Integer) this.typeEditor.getRadioBoxControl(this.getFieldEditorParent()).getData();
			if (this.quantityEditor.getIntValue() == 0)
			{
				// this.quantityEditor.setStringValue("1");
				this.quantityEditor.setDefaultValue(new Integer(1));
			}
			
			if (type.equals(new Integer(ProductGroup.TYPE_EXPENSE)))
			{
				this.quantityEditor
								.setStringValue(new Integer(-Math.abs(this.quantityEditor.getIntValue())).toString());
				this.quantityEditor.setDefaultValue(new Integer(-Math.abs(1)));
				this.quantityEditor.setEnabled(false, this.getFieldEditorParent());
				this.defaultTaxEditor.setInput(new Integer(ProductGroup.TYPE_EXPENSE));
				this.defaultTaxEditor.setEnabled(true, this.getFieldEditorParent());
				this.defaultTaxEditor.getCombo().setEnabled(true);
			}
			else if (type.equals(new Integer(ProductGroup.TYPE_INPUT)))
			{
				this.paidInvoiceEditor.setDefaultValue(false);
				this.paidInvoiceEditor.setEnabled(false, this.getFieldEditorParent());
				// this.quantityEditor.setStringValue(new
				// Integer(Math.abs(this.quantityEditor.getIntValue())).toString());
				this.quantityEditor.setDefaultValue(new Integer(Math.abs(1)).toString());
				this.quantityEditor.setEnabled(false, this.getFieldEditorParent());
				this.defaultTaxEditor.setInput(new Integer(ProductGroup.TYPE_INPUT));
				this.defaultTaxEditor.setEnabled(false, this.getFieldEditorParent());
				this.defaultTaxEditor.getCombo().setEnabled(false);
				this.defaultTaxEditor.getStore().setValue(ProductGroupFieldEditorPage.KEY_DEFAULT_TAX,
								Tax.selectByCode("UF", true));
			}
			else if (type.equals(new Integer(ProductGroup.TYPE_WITHDRAW)))
			{
				this.paidInvoiceEditor.setDefaultValue(false);
				this.paidInvoiceEditor.setEnabled(false, this.getFieldEditorParent());
				this.quantityEditor
								.setStringValue(new Integer(-Math.abs(this.quantityEditor.getIntValue())).toString());
				this.quantityEditor.setDefaultValue(new Integer(-Math.abs(1)).toString());
				this.quantityEditor.setEnabled(false, this.getFieldEditorParent());
				this.defaultTaxEditor.setInput(new Integer(ProductGroup.TYPE_WITHDRAW));
				this.defaultTaxEditor.setEnabled(false, this.getFieldEditorParent());
				this.defaultTaxEditor.getCombo().setEnabled(false);
				this.defaultTaxEditor.getStore().setValue(ProductGroupFieldEditorPage.KEY_DEFAULT_TAX,
								Tax.selectByCode("UF", true));
			}
			else
			{
				this.quantityEditor
								.setDefaultValue(new Integer(Math.abs(this.quantityEditor.getIntValue())).toString());
				// this.quantityEditor.setStringValue(new
				// Integer(Math.abs(this.quantityEditor.getIntValue())).toString());
				this.quantityEditor.setEnabled(true, this.getFieldEditorParent());
				this.defaultTaxEditor.setInput(new Integer(ProductGroup.TYPE_INCOME));
				this.defaultTaxEditor.setEnabled(true, this.getFieldEditorParent());
				this.defaultTaxEditor.getCombo().setEnabled(true);
			}
		}
		// }
		super.propertyChange(event);
	}
	
	private void doRefresh()
	{
		ProductGroup group = (ProductGroup) this.getStore().getElement();
		Object type = this.typeEditor.getRadioBoxControl(this.getFieldEditorParent()).getData();
		if (type instanceof Integer)
		{
			if (group.isDefault)
			{
				this.defaultGroupEditor.setEnabled(false, this.getFieldEditorParent());
				this.typeEditor.setEnabled(false, this.getFieldEditorParent());
				this.paidInvoiceEditor.setDefaultValue(false);
				this.paidInvoiceEditor.setEnabled(false, this.getFieldEditorParent());
				this.ebookEditor.setDefaultValue(false);
				this.ebookEditor.setEnabled(!group.ebook, this.getFieldEditorParent());
			}
			else
			{
				this.defaultGroupEditor.setEnabled(type.equals(new Integer(ProductGroup.TYPE_INCOME)),
								this.getFieldEditorParent());
				this.paidInvoiceEditor.setEnabled(type.equals(new Integer(ProductGroup.TYPE_NOT_INCOME)),
								this.getFieldEditorParent());
				this.ebookEditor.setEnabled(type.equals(new Integer(ProductGroup.TYPE_INCOME)),
								this.getFieldEditorParent());
				this.typeEditor.setEnabled(!this.paidInvoiceEditor.getBooleanValue(), this.getFieldEditorParent());
				this.quantityEditor.setEnabled(
								!type.equals(new Integer(ProductGroup.TYPE_INPUT))
												&& !type.equals(new Integer(ProductGroup.TYPE_WITHDRAW)),
								this.getFieldEditorParent());
				if (type.equals(new Integer(ProductGroup.TYPE_INPUT))
								|| type.equals(new Integer(ProductGroup.TYPE_WITHDRAW)))
				{
					// this.paidInvoiceEditor.setDefaultValue(false);
					// this.paidInvoiceEditor.setEnabled(false,
					// this.getFieldEditorParent());
					this.defaultTaxEditor.setEnabled(false, this.getFieldEditorParent());
					this.defaultTaxEditor.getCombo().setEnabled(false);
				}
				else
				{
					// this.paidInvoiceEditor.setDefaultValue(true);
					// this.paidInvoiceEditor.setEnabled(true,
					// this.getFieldEditorParent());
					this.defaultTaxEditor.setEnabled(true, this.getFieldEditorParent());
					this.defaultTaxEditor.getCombo().setEnabled(true);
				}
			}
		}
	}
	
	@Override
	public void refresh()
	{
		// 10051
		// idEditor wird nicht instantiiert, wenn die Id manuell vergeben wird
		// dass muss abgefangen werden, indem darauf überprüft wird, ob idEditor
		// instantiiert wurde.
		if (this.idEditor != null)
		{
			this.idEditor.setEnabled(((ProductGroup) this.getStore().getElement()).getId() == null
							|| ((ProductGroup) this.getStore().getElement()).getId().equals(ProductGroup.ZERO_VALUE),
							this.getFieldEditorParent());
		}
		this.doRefresh();
	}
	
	public StringFieldEditor getGalileoIdField()
	{
		return this.galileoIdEditor;
	}
	
	private LongFieldEditor idEditor;
	private StringFieldEditor nameEditor;
	private StringFieldEditor shortNameEditor;
	private StringFieldEditor galileoIdEditor;
	private IntegerFieldEditor quantityEditor;
	private DoubleFieldEditor priceEditor;
	private StringFieldEditor optCodeEditor;
	private StringFieldEditor accountEditor;
	// 10215
	private BooleanFieldEditor paidInvoiceEditor;
	// 10215
	// 10236
	private ComboFieldEditor currencyEditor;
	// 10236
	private BooleanFieldEditor defaultGroupEditor;
	private BooleanFieldEditor ebookEditor;
	
	private RadioGroupFieldEditor typeEditor;
	private ComboFieldEditor defaultTaxEditor;
	private StringFieldEditor exportIdEditor;
	// private Group group;
	
	public static final String KEY_ID = "id"; //$NON-NLS-1$
	public static final String KEY_NAME = "name"; //$NON-NLS-1$
	public static final String KEY_SHORT_NAME = "shortName"; //$NON-NLS-1$
	public static final String KEY_CURRENCY = "currency";
	public static final String KEY_GALILEO_ID = "galileoId"; //$NON-NLS-1$
	public static final String KEY_QUANTITY = "quantity"; //$NON-NLS-1$
	public static final String KEY_PRICE = "price"; //$NON-NLS-1$
	public static final String KEY_ACCOUNT = "account"; //$NON-NLS-1$
	public static final String KEY_OPT_CODE = "optCode"; //$NON-NLS-1$
	public static final String KEY_TYPE = "type"; //$NON-NLS-1$
	public static final String KEY_DEFAULT_TAX = "defaultTax"; //$NON-NLS-1$
	// 10215
	public static final String KEY_PAID_INVOICE = "paidInvoice"; //$NON-NLS-1$
	// 10215
	public static final String KEY_DEFAULT_GROUP = "defaultGroup"; //$NON-NLS-1$
	public static final String KEY_EBOOK = "ebook"; //$NON-NLS-1$
	public static final String KEY_MODIFIED = "modified"; //$NON-NLS-1$
	public static final String KEY_EXPORT_ID = "export-id"; //$NON-NLS-1$
	
}
