/*
 * Created on 17.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.preference;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.ForeignCurrency;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class CurrencyFieldEditorPreferencePage extends FieldEditorPreferencePage
{
	
	/**
	 * @param style
	 */
	public CurrencyFieldEditorPreferencePage(int style)
	{
		super(style);
	}
	
	/**
	 * @param title
	 * @param style
	 */
	public CurrencyFieldEditorPreferencePage(String title, int style)
	{
		super(title, style);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public CurrencyFieldEditorPreferencePage(String title, ImageDescriptor image, int style)
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
		String[] values = ForeignCurrency.selectAllReturnCodes(true);
		StringComboFieldEditor currencyEditor = new StringComboFieldEditor(
						"currency.default", Messages.getString("CurrencyFieldEditorPreferencePage.Landesw_u00E4hrung_2"), this.getFieldEditorParent(), values); //$NON-NLS-1$ //$NON-NLS-2$
		this.addField(currencyEditor);
		
		DoubleFieldEditor amountEditor = new DoubleFieldEditor(
						"currency.roundfactor.amount", Messages.getString("CurrencyFieldEditorPreferencePage.Rundungsfaktor_Geldbetr_u00E4ge_2"), this.getFieldEditorParent(), 10); //$NON-NLS-1$ //$NON-NLS-2$
		amountEditor.setEmptyStringAllowed(false);
		this.addField(amountEditor);
		
		DoubleFieldEditor taxEditor = new DoubleFieldEditor(
						"currency.roundfactor.tax", Messages.getString("CurrencyFieldEditorPreferencePage.Rundungsfaktor_Steuerbetr_u00E4ge_4"), this.getFieldEditorParent(), 10); //$NON-NLS-1$ //$NON-NLS-2$
		taxEditor.setEmptyStringAllowed(false);
		this.addField(taxEditor);
	}
	
}
