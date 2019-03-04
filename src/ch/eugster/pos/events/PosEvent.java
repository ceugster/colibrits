/*
 * Created on 07.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.events;



/**
 * @author administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PosEvent {

	/**
	 * 
	 */
	public PosEvent(Action action) {
		super();
		this.action = action;
	}

	public void setPosAction(Action action) {
		this.action = action;
	}
	
	public Action getPosAction() {
		return action;
	}

	protected Action action;

}
