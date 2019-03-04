/*
 * Created on 16.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.events;

import ch.eugster.pos.client.model.ReceiptModel;
import ch.eugster.pos.db.ReceiptChild;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ReceiptChangeEvent extends AbstractEvent {

	/**
	 * 
	 */
	public ReceiptChangeEvent(ReceiptModel model) {
		super();
		init(model, new Integer(0));
	}
	
	/**
	 * 
	 */
	public ReceiptChangeEvent(ReceiptModel model, Integer eventType) {
		super();
		init(model, eventType);
	}
	
	private void init(ReceiptModel model, Integer eventType) {
		this.model = model;
		this.eventType = eventType;
	}
	
	public void setEventType(Integer eventType) {
		this.eventType = eventType;
	}
	
	public Integer getEventType() {
		return eventType;
	}

	public void setReceiptModel(ReceiptModel model) {
		this.model = model;
	}
		
	public ReceiptModel getReceiptModel() {
		return model;
	}
	
	public void setReceiptChild(ReceiptChild child) {
		this.child = child;
	}
		
	public ReceiptChild getReceiptChild() {
		return child;
	}
	
	private Integer eventType;
	private ReceiptModel model;
	private ReceiptChild child;
}
