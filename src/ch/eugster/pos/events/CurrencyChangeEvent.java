/*
 * Created on 24.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.events;

import ch.eugster.pos.client.model.ReceiptModel;
import ch.eugster.pos.db.ReceiptChild;

/**
 * @author ceugster
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CurrencyChangeEvent {

	/**
	 * 
	 */
	public CurrencyChangeEvent(ReceiptModel model, ReceiptChild child) {
		super();
		this.receiptModel = model;
		this.receiptChild = child;
	}
	
	public ReceiptModel getReceiptModel() {
		return this.receiptModel;
	}

	public ReceiptChild getReceiptChild() {
		return this.receiptChild;
	}

	private ReceiptModel receiptModel;
	private ReceiptChild receiptChild;
}
