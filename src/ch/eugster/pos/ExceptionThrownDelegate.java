/*
 * Created on 31.07.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos;

import java.util.ArrayList;

/**
 * @author ceugster
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ExceptionThrownDelegate {
	public static boolean addExceptionThrownListener(ExceptionThrownListener listener) {
		return exceptionThrownListeners.add(listener);
	}

	public static boolean removeExceptionThrownListener(ExceptionThrownListener listener) {
		return exceptionThrownListeners.remove(listener);
	}

	public static void fireExceptionThrownEvent(ExceptionThrownEvent e) {
		ExceptionThrownListener[] l = (ExceptionThrownListener[])exceptionThrownListeners.toArray(new ExceptionThrownListener[0]);
		for (int i = 0; i < l.length; i++) {
			l[i].exceptionThrown(e);
		}
	}
	
	private static ArrayList exceptionThrownListeners = new ArrayList();

}
