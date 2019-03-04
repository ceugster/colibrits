/*
 * Created on 13.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.admin.preference;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ReceiptFieldEditorPreferencePage extends FieldEditorPreferencePage
{
	
	/**
	 * @param arg0
	 */
	public ReceiptFieldEditorPreferencePage(int arg0)
	{
		super(arg0);
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public ReceiptFieldEditorPreferencePage(String arg0, int arg1)
	{
		super(arg0, arg1);
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public ReceiptFieldEditorPreferencePage(String arg0, ImageDescriptor arg1, int arg2)
	{
		super(arg0, arg1, arg2);
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
		BooleanFieldEditor automaticPrintEditor = new BooleanFieldEditor("receipt.automatic-print",
						"Coupon beim Belegabschluss automatisch drucken", this.getFieldEditorParent());
		this.addField(automaticPrintEditor);
		
		BooleanFieldEditor takeBackPrintSignatureEditor = new BooleanFieldEditor("receipt.take-back-print-signature",
						"Bei Rücknahmen Unterschriftbereich drucken", this.getFieldEditorParent());
		this.addField(takeBackPrintSignatureEditor);
		
		BooleanFieldEditor takeBackPrintTwiceEditor = new BooleanFieldEditor("receipt.take-back-print-twice",
						"Bei Rücknahmen Coupon zweimal drucken", this.getFieldEditorParent());
		this.addField(takeBackPrintTwiceEditor);
		
		BooleanFieldEditor titleDataEditor = new BooleanFieldEditor("receipt.position.print-second-line",
						"Zweite Positionenzeile mit Titeldaten drucken", this.getFieldEditorParent());
		this.addField(titleDataEditor);
		
		StringComboFieldEditor positionItemEditor = new StringComboFieldEditor("receipt.position.row.col.item-name",
						"Auswahl Positionsbezeichnung", this.getFieldEditorParent(), new String[]
						{ "Artikelnummer", "Warengruppe", "Galileo-Warengruppe", }, new String[]
						{ "position.productid", "position.productgroup", "position.galileoid" });
		this.addField(positionItemEditor);
	}
}
