/*
 * Created on 13.09.2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.events;

import ch.eugster.pos.db.Receipt;

/**
 * @author ceugster
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ReceiptSaveEvent extends AbstractEvent {

	private Receipt receipt = null;
	
	public ReceiptSaveEvent(Receipt receipt) {
		this.receipt = receipt;
	}
	
	public Receipt getReceipt() {
		return receipt;
	}
}
