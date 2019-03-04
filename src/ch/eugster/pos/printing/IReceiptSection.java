/*
 * Created on 26.01.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.printing;

import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.db.ReceiptChild;
import ch.eugster.pos.devices.printers.POSPrinter;

/**
 * @author ceugster
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface IReceiptSection {
	public int getRowCount();
	public void print(POSPrinter printer, Receipt receipt);
	public void print(POSPrinter printer, ReceiptChild child);
}
