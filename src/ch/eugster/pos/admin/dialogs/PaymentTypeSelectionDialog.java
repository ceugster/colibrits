/*
 * Created on 21.05.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.admin.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import ch.eugster.pos.db.PaymentType;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PaymentTypeSelectionDialog extends TitleAreaDialog
{
	private Combo combo;
	private PaymentTypeSelectionDialogInput input;
	
	public PaymentTypeSelectionDialog(Shell shell, PaymentTypeSelectionDialogInput input)
	{
		super(shell);
		this.input = input;
	}
	
	protected Control createDialogArea(Composite parent)
	{
		this.setTitle("Auswahl Rückgeld");
		this.setMessage("Wählen Sie aus der Liste die gewünschte Zahlungsart.");
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label label = new Label(composite, SWT.NONE);
		label.setText("Zahlungsart");
		
		PaymentType[] paymentTypes = PaymentType.selectBacks(false);
		this.combo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		this.combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		PaymentType type = this.input.getPaymentType();
		String selection = "";
		if (type != null) selection = type.code + " " + this.input.getPaymentType().name;
		
		for (int i = 0; i < paymentTypes.length; i++)
		{
			String data = paymentTypes[i].code + " " + paymentTypes[i].name;
			this.combo.add(data);
			this.combo.setData(data, paymentTypes[i]);
			if (data.equals(selection)) this.combo.select(this.combo.getItemCount() - 1);
		}
		if (this.combo.getItemCount() > 0 && this.combo.getSelectionIndex() == -1) this.combo.select(0);
		
		return null;
	}
	
	protected void createButtonsForButtonBar(Composite parent)
	{
		this.createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		this.createButton(parent, IDialogConstants.CANCEL_ID, "Abbrechen", false);
	}
	
	protected void okPressed()
	{
		Object object = this.combo.getData(this.combo.getItem(this.combo.getSelectionIndex()));
		this.input.setPaymentType((PaymentType) object);
		super.okPressed();
	}
}
