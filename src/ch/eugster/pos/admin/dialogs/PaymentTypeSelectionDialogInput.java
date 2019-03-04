/*
 * Created on 21.05.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.admin.dialogs;

import ch.eugster.pos.db.PaymentType;

/**
 * @author ceugster
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PaymentTypeSelectionDialogInput {

	private PaymentType paymentType;
	
	public PaymentTypeSelectionDialogInput(PaymentType paymentType)
	{
		this.paymentType = paymentType;
	}
	
	public void setPaymentType(PaymentType paymentType)
	{
		this.paymentType = paymentType;
	}
	
	public PaymentType getPaymentType()
	{
		return paymentType;
	}
}
