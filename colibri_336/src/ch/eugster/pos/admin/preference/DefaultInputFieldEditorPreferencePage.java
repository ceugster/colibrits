/*
 * Created on 17.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.preference;

import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.Tax;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class DefaultInputFieldEditorPreferencePage extends FieldEditorPreferencePage
{
	
	/**
	 * @param style
	 */
	public DefaultInputFieldEditorPreferencePage(int style)
	{
		super(style);
	}
	
	/**
	 * @param title
	 * @param style
	 */
	public DefaultInputFieldEditorPreferencePage(String title, int style)
	{
		super(title, style);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public DefaultInputFieldEditorPreferencePage(String title, ImageDescriptor image, int style)
	{
		super(title, image, style);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors
	 * ()
	 */
	protected void createFieldEditors()
	{
		IntegerFieldEditor quantityEditor = new IntegerFieldEditor(
						"input-default.quantity", Messages.getString("InputDefaultFieldEditorPreferencePage.Vorschlag_Menge_2"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		quantityEditor.setEmptyStringAllowed(false);
		quantityEditor.setTextLimit(3);
		this.addField(quantityEditor);
		
		Tax[] taxes = Tax.selectAll(false);
		String[] keys = new String[taxes.length];
		long[] values = new long[taxes.length];
		for (int i = 0; i < taxes.length; i++)
		{
			keys[i] = taxes[i].getTaxType().name + ", " + taxes[i].getTaxRate().name; //$NON-NLS-1$
			values[i] = taxes[i].getId().longValue();
		}
		LongComboFieldEditor taxEditor = new LongComboFieldEditor(
						"input-default.tax", "Vorschlag Mehrwertsteuer", this.getFieldEditorParent(), keys, values); //$NON-NLS-1$ //$NON-NLS-2$
		this.addField(taxEditor);
		
		StringFieldEditor optionEditor = new StringFieldEditor(
						"input-default.option", Messages.getString("InputDefaultFieldEditorPreferencePage.Vorschlag_Optionscode_4"), -1, this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		optionEditor.setEmptyStringAllowed(false);
		optionEditor.setTextLimit(3);
		this.addField(optionEditor);
		
		IntegerFieldEditor maxQuantityRangeEditor = new IntegerFieldEditor(
						"input-default.max-quantity-range", "Maximale Eingabe Menge", this.getFieldEditorParent()); //$NON-NLS-1$
		maxQuantityRangeEditor.setEmptyStringAllowed(false);
		maxQuantityRangeEditor.setValidRange(0, 100000);
		maxQuantityRangeEditor.setTextLimit(10);
		this.addField(maxQuantityRangeEditor);
		
		IntegerFieldEditor maxQuantityAmountEditor = new IntegerFieldEditor(
						"input-default.max-quantity-amount", "Warngrenze Menge", this.getFieldEditorParent()); //$NON-NLS-1$
		maxQuantityAmountEditor.setEmptyStringAllowed(false);
		maxQuantityAmountEditor.setValidRange(0, 100000);
		maxQuantityAmountEditor.setTextLimit(10);
		this.addField(maxQuantityAmountEditor);
		
		DoubleFieldEditor maxPriceRangeEditor = new DoubleFieldEditor(
						"input-default.max-price-range", "Maximaler Eingabebetrag Preis", this.getFieldEditorParent()); //$NON-NLS-1$
		maxPriceRangeEditor.setEmptyStringAllowed(false);
		maxPriceRangeEditor.setValidRange(0d, 100000d);
		maxPriceRangeEditor.setTextLimit(10);
		this.addField(maxPriceRangeEditor);
		
		DoubleFieldEditor maxPriceAmountEditor = new DoubleFieldEditor(
						"input-default.max-price-amount", "Warngrenzbetrag Preis", this.getFieldEditorParent()); //$NON-NLS-1$
		maxPriceAmountEditor.setEmptyStringAllowed(false);
		maxPriceAmountEditor.setValidRange(0d, 100000d);
		maxPriceAmountEditor.setTextLimit(10);
		this.addField(maxPriceAmountEditor);
		
		DoubleFieldEditor maxPaymentRangeEditor = new DoubleFieldEditor(
						"input-default.max-payment-range", "Maximaler Eingabebetrag Zahlung", this.getFieldEditorParent()); //$NON-NLS-1$
		maxPaymentRangeEditor.setEmptyStringAllowed(false);
		maxPaymentRangeEditor.setValidRange(0d, 100000d);
		maxPaymentRangeEditor.setTextLimit(10);
		this.addField(maxPaymentRangeEditor);
		
		DoubleFieldEditor maxPaymentAmountEditor = new DoubleFieldEditor(
						"input-default.max-payment-amount", "Warngrenzbetrag Zahlung", this.getFieldEditorParent()); //$NON-NLS-1$
		maxPaymentAmountEditor.setEmptyStringAllowed(false);
		maxPaymentAmountEditor.setValidRange(0d, 100000d);
		maxPaymentAmountEditor.setTextLimit(10);
		this.addField(maxPaymentAmountEditor);
		
		// 10142
		BooleanFieldEditor clearPriceEditor = new BooleanFieldEditor("input-default.clear-price",
						"Preis beim Scannen nicht übernehmen", this.getFieldEditorParent());
		this.addField(clearPriceEditor);
		// 10142
	}
	
}
