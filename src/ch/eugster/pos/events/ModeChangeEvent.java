/*
 * Created on 04.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.events;

import java.util.Hashtable;



/**
 * @author administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ModeChangeEvent extends AbstractEvent {

	public ModeChangeEvent() {
		super();
		init(new Integer(0), new Integer(0), new Object());
	}
	
	public ModeChangeEvent(Integer requestedMode) {
		super();
		init(new Integer(0), requestedMode, new Object());
	}
	
	public ModeChangeEvent(Integer applicantType, Integer requestedMode) {
		super();
		init(applicantType, requestedMode, new Object());
	}
	
	public ModeChangeEvent(Integer applicantType, Integer requestedMode, Object object) {
		super();
		init(applicantType, requestedMode, object);
	}
	
	private void init(Integer applicant, Integer mode, Object object) {
		for (int i = 0; i < KEY_COUNT; i++) {
			modes.put(new Integer(i), (i == applicant.intValue() ? mode : new Integer(0)));
			sources.put(new Integer(i), (i == applicant.intValue() ? object : new Object()));
		}
	}
	
	public void setRequestedMode(Integer mode) {
		modes.put(new Integer(0), mode);
	}
	
	public void setRequestedMode(Integer key, Integer mode) {
		modes.put(key, mode);
	}
	
	public void setRequestedMode(Integer key, Integer mode, Object object) {
		modes.put(key, mode);
		sources.put(key, object);
	}
	
	public Integer getRequestedMode() {
		return (Integer) modes.get(new Integer(0));
	}

	public Integer getRequestedMode(Integer key) {
		return (Integer) modes.get(key);
	}

	public void setSource(Integer key, Object source) {
		sources.put(key, source); 
	}
	
	public Object getSource(Integer key) {
		return sources.get(key); 
	}

	private Hashtable modes = new Hashtable();
	private Hashtable sources = new Hashtable();

	public static final Integer KEY_VIEW				= new Integer(0);
	public static final Integer KEY_RECEIPT 			= new Integer(1);
	public static final Integer KEY_CHILD		 		= new Integer(2);
	public static final Integer KEY_CHILD_LIST		 	= new Integer(3);
	public static final Integer KEY_NUMERIC_BLOCK		= new Integer(4);

	public static final int KEY_COUNT = 5;

	/**
	 * Values for all keys
	 */
	public static final Integer MODE_NO_CHANGE	= new Integer(0);

	/**
	 * Values for Context (can be found under UserPanel)
	 */
	
	
	/**
	 * Values for KEY_QUANTITY_FLAG
	 */
	public static final Integer VALUE_IS_INTEGER 		= new Integer(1);
	public static final Integer VALUE_IS_NOT_INTEGER 	= new Integer(2);
	

}
