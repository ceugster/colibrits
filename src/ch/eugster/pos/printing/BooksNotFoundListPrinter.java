/*
 * Created on 31.08.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.printing;

import ch.eugster.pos.Messages;
import ch.eugster.pos.client.gui.TabPanel;
import ch.eugster.pos.devices.printers.POSPrinter;

/**
 * @author ceugster
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class BooksNotFoundListPrinter {

	/**
	 * 
	 */
	public BooksNotFoundListPrinter(TabPanel tabPanel, String[] bookList) {
		if (bookList.length > 0) {
			this.tabPanel = tabPanel;
			printer = tabPanel.getReceiptPrinter().getPrinter();
			printBooksNotFound(bookList);
		}
	}
	
	private void printBooksNotFound(String[] details) {
		printer = tabPanel.getReceiptPrinter().getPrinter();
		printHeader();
		printDetails(details);
		printFooter();
		
		printer.partialCut(6);
	}
	
	private void printHeader() {
		printer.println(Messages.getString("BooksNotFoundListPrinter.header_1")); //$NON-NLS-1$
		printer.println(Messages.getString("BooksNotFoundListPrinter.header_2")); //$NON-NLS-1$
		printer.println(Messages.getString("BooksNotFoundListPrinter.header_3")); //$NON-NLS-1$
		printer.println(Messages.getString("BooksNotFoundListPrinter.header_4")); //$NON-NLS-1$
		printer.println(Messages.getString("BooksNotFoundListPrinter.header_5")); //$NON-NLS-1$
		printer.println(Messages.getString("BooksNotFoundListPrinter.header_6")); //$NON-NLS-1$
		printer.println(""); //$NON-NLS-1$
	}
	
	private void printDetails(String[] details) {
		for (int i = 0; i < details.length; i++) {
			printer.println(details[i]);
		}
	}
	
	private void printFooter() {
		printer.println(""); //$NON-NLS-1$
		printer.println(Messages.getString("BooksNotFoundListPrinter.header_6")); //$NON-NLS-1$
		printer.println(Messages.getString("BooksNotFoundListPrinter.N_I_C_H_T__V_E_R_B_U_C_H_T_E___B_E_L_E_G_E_7")); //$NON-NLS-1$
		printer.println(Messages.getString("BooksNotFoundListPrinter.***_G_A_L_I_L_E_O_******_G_A_L_I_L_E_O_***_8")); //$NON-NLS-1$
	}
	
	private TabPanel tabPanel;
	private POSPrinter printer;
}
