/*
 * Created on 17.08.2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.events;

/**
 * @author ceugster
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ShowMessageEvent {
	
	private String message = "";
	private String title = "";
	private int type = 0;
	
	public ShowMessageEvent(String message, String title, int type) {
		this.message = message;
		this.title = title;	
		this.type = type;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getTitle() {
		return title;
	}
	
	public int getType() {
		return type;
	}
}
