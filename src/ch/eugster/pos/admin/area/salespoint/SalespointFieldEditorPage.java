/*
 * Created on 05.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.salespoint;

import java.sql.Timestamp;
import java.util.GregorianCalendar;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.gui.widget.BooleanFieldEditor;
import ch.eugster.pos.admin.gui.widget.FieldEditorPage;
import ch.eugster.pos.admin.gui.widget.LabelFieldEditor;
import ch.eugster.pos.admin.gui.widget.LongFieldEditor;
import ch.eugster.pos.admin.gui.widget.StringFieldEditor;
import ch.eugster.pos.admin.gui.widget.UniquePrimaryKeyValidator;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.db.Stock;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class SalespointFieldEditorPage extends FieldEditorPage
{
	
	/**
	 * @param style
	 */
	public SalespointFieldEditorPage(String name, int style, PersistentDBStore store)
	{
		super(name, style, store);
	}
	
	/**
	 * @param title
	 * @param style
	 */
	public SalespointFieldEditorPage(String name, String title, int style, PersistentDBStore store)
	{
		super(name, title, style, store);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public SalespointFieldEditorPage(String name, String title, ImageDescriptor image, int style,
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
		if (!Salespoint.isIdFieldAutoincrement(Salespoint.class))
		{
			LongFieldEditor idEditor = new LongFieldEditor(SalespointFieldEditorPage.KEY_ID, Messages
							.getString("SalespointFieldEditorPage.Id_1"), this.getFieldEditorParent()); //$NON-NLS-1$
			idEditor.setContentValidator(new UniquePrimaryKeyValidator(idEditor, Salespoint.class));
			this.addField(idEditor);
		}
		
		this.nameEditor = new StringFieldEditor(
						SalespointFieldEditorPage.KEY_NAME,
						Messages.getString("SalespointFieldEditorPage.Bezeichnung_1"), -1, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		this.nameEditor.setEmptyStringAllowed(false);
		this.nameEditor.setErrorMessage(Messages
						.getString("SalespointFieldEditorPage.Die_Angabe_einer_Bezeichnung_ist_notwendig._2")); //$NON-NLS-1$
		this.nameEditor.showErrorMessage();
		this.addField(this.nameEditor);
		
		this.placeEditor = new StringFieldEditor(
						SalespointFieldEditorPage.KEY_PLACE,
						Messages.getString("SalespointFieldEditorPage.Standort_3"), -1, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		this.placeEditor.setEmptyStringAllowed(true);
		this.placeEditor.showErrorMessage();
		this.addField(this.placeEditor);
		
		/*
		 * Der StockEditor befindet sich jetzt auf einer eigenen Seite
		 * (SalespointStockFieldEditorPage)
		 */
		//		stockEditor = new DoubleFieldEditor(KEY_STOCK, Messages.getString("SalespointFieldEditorPage.Kassastock_1"), getFieldEditorParent()); //$NON-NLS-1$
		// stockEditor.setTextLimit(10);
		// stockEditor.setEmptyStringAllowed(true);
		// stockEditor.setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
		// addField(stockEditor);
		// 10183
		this.variableStockEditor = new BooleanFieldEditor(SalespointFieldEditorPage.KEY_VARIABLE_STOCK,
						"Variabler Kassastock", this.getFieldEditorParent());
		this.addField(this.variableStockEditor);
		// 10183
		this.activeEditor = new BooleanFieldEditor(SalespointFieldEditorPage.KEY_ACTIVE, Messages
						.getString("SalespointFieldEditorPage.Aktiv_4"), this.getFieldEditorParent()); //$NON-NLS-1$
		this.addField(this.activeEditor);
		
		this.exportIdEditor = new StringFieldEditor(SalespointFieldEditorPage.KEY_EXPORT_ID,
						"Export/Import-ID", 10, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		this.exportIdEditor.setEmptyStringAllowed(true);
		this.exportIdEditor.showErrorMessage();
		this.addField(this.exportIdEditor);
		
		LabelFieldEditor commentEditor = new LabelFieldEditor("*Eindeutiger Wert zwingend", this.getFieldEditorParent()); //$NON-NLS-1$
		this.addField(commentEditor);
	}
	
	public boolean isInstance(Object element)
	{
		return element instanceof Salespoint;
	}
	
	/**
	 * Method declared on IDataPage. Subclasses should override
	 */
	public boolean doStore()
	{
		Salespoint salespoint = (Salespoint) this.getStore().getElement();
		if (salespoint.getId() == null)
		{
			ForeignCurrency[] currencies = ForeignCurrency.selectUsedForeingCurrencies();
			for (int i = 0; i < currencies.length; i++)
			{
				if (currencies[i].isUsed)
				{
					Stock stock = new Stock(salespoint, currencies[i]);
					stock.timestamp = new Timestamp(GregorianCalendar.getInstance().getTimeInMillis());
					stock.setStock(0d);
					salespoint.addStock(stock);
				}
			}
		}
		return true;
	}
	
	public String getElementName()
	{
		Salespoint salespoint = (Salespoint) this.getStore().getElement();
		return salespoint.name;
	}
	
	private StringFieldEditor nameEditor;
	private StringFieldEditor placeEditor;
	private BooleanFieldEditor variableStockEditor; // 10183
	private BooleanFieldEditor activeEditor;
	private StringFieldEditor exportIdEditor;
	
	public static final String KEY_ID = "id"; //$NON-NLS-1$
	public static final String KEY_NAME = "code"; //$NON-NLS-1$
	public static final String KEY_PLACE = "place"; //$NON-NLS-1$
	public static final String KEY_STOCK = "stock"; //$NON-NLS-1$
	public static final String KEY_VARIABLE_STOCK = "variable-stock"; // 10183
	// $NON-NLS-1$
	public static final String KEY_ACTIVE = "active"; //$NON-NLS-1$
	public static final String KEY_EXPORT_ID = "export-id"; //$NON-NLS-1$
	
}
