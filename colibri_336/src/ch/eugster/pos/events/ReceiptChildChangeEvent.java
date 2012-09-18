/*
 * Created on 16.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.events;

import ch.eugster.pos.client.model.ReceiptChildModel;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ReceiptChildChangeEvent extends AbstractEvent {

	/**
	 * 
	 */
	public ReceiptChildChangeEvent(ReceiptChildModel model) {
		super();
		init(model);
	}
	
	private void init(ReceiptChildModel model) {
		this.model = model;
	}
	
	public ReceiptChildModel getReceiptChildModel() {
		return model;
	}
	
	public void initInputValue(boolean init) {
		initInputValue = init;
	}
	
	public boolean getInitInputValue() {
		return initInputValue;
	}
	
	private ReceiptChildModel model;
	private boolean initInputValue = true;

}
