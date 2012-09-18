/*
 * Created on 23.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.product;

/**
 * @author ceugster
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class ISBN {

	public static String computeISBN(String isbn) {
		String i = null;
		i = isbn.replaceAll("-", ""); //$NON-NLS-1$ //$NON-NLS-2$

		if (i.length() == 10) {
			// Pruefziffer ist vorhanden; Ausgabe == Eingabe
			return i;
		}
		else if (i.length() == 9) {
			// Pruefziffer muss berechnet werden
			i = i.concat(computeChecksum(i));
		}
		return i;
	}
	
	public static String computeChecksum(String isbn) {
		if (!(isbn.length() == 9)) {
			return isbn;
		}

		int checksum = 0;
		for (int i = 0; i < isbn.length(); i++) {
			checksum = checksum + new Integer(isbn.substring(i, i + 1)).intValue() * (11 - (i + 1));
		}
		
		checksum = 11 - checksum%11;
		String checksumcode = null;
		switch (checksum) {
			case 10: 
				checksumcode = "X"; //$NON-NLS-1$
				break;
			case 11: 
				checksumcode = "0"; //$NON-NLS-1$
				break;
			default:
				checksumcode = new Integer(checksum).toString();
				break;
		}
		
		return checksumcode;
	}
	
	public static String removeHyphen(String isbn) {
		return isbn.replaceAll("-", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
