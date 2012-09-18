/*
 * Created on 10.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.events;

import java.util.EventObject;

import ch.eugster.pos.db.Connection;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DatabaseEvent extends EventObject
{
	private static final long serialVersionUID = 0l;
	
	/**
	 * @param arg0
	 */
	public DatabaseEvent(int type, Connection con1, Connection con2)
	{
		super(null);
		this.type = type;
		this.oldConnection = con1;
		this.newConnection = con2;
	}
	
	/**
	 * @param arg0
	 */
	public DatabaseEvent(Object source, int type, String title, String text)
	{
		super(source);
		this.type = type;
		this.title = title;
		this.text = text;
	}
	
	/**
	 * @param arg0
	 */
	public DatabaseEvent(Object source, int type, Connection oldCon, Connection newCon, String title, String text)
	{
		super(source);
		this.type = type;
		this.oldConnection = oldCon;
		this.newConnection = newCon;
		this.title = title;
		this.text = text;
	}
	
	public int type;
	public Connection oldConnection;
	public Connection newConnection;
	public String text;
	public String title;
	
	public static final int MESSAGE = 0;
	public static final int TITLE = 1;
}
