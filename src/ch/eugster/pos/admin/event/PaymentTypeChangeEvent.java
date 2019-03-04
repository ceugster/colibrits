/*
 * Created on 29.05.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.admin.event;

import ch.eugster.pos.db.PaymentType;

/**
 * @author ceugster
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PaymentTypeChangeEvent {

	public PaymentType oldType;
	public PaymentType newType;
	/**
	 * 
	 */
	public PaymentTypeChangeEvent(PaymentType oldType, PaymentType newType) {
		super();
		this.oldType = oldType;
		this.newType = newType;
	}
	
	public boolean foreignCurrencyChanged()
	{
		return (!oldType.getForeignCurrency().getId().equals(newType.getForeignCurrency().getId()));
	}

}
