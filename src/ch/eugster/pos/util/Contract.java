/*
 * Created on 09.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.util;

/**
 * @author administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Contract {

	/**
	 * @param boolean precondition
	 * @param String description
	 * @throws AssertionError(int)
	 */
	public static void pre(boolean precondition, String description) throws AssertionError {
		if (enabled) {
			if (precondition == false) {
				throw new AssertionError(1);
			}
		}
	}

	/**
	 * @param boolean postcondition
	 * @param String description
	 * @throws AssertionError(int)
	 */
	public static void post(boolean postcondition, String description) throws AssertionError {
		if (enabled) {
			if (postcondition == false) {
				throw new AssertionError(2);
			}
		}
	}
	
	
	/**
	 * @param boolean postcondition
	 * @param String description
	 * @throws AssertionError(int)
	 */
	public static void invariant(boolean invariant, String description) throws AssertionError {
		if (enabled) {
			if (invariant == false) {
				throw new AssertionError(3);
			}
		}
	}
	
	public static boolean enabled = true;
}
