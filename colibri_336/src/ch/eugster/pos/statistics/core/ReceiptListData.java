/*
 * Created on 19.07.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.core;

import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;

import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.statistics.gui.DateRangeGroup;
import ch.eugster.pos.statistics.gui.ReceiptListComposite;
import ch.eugster.pos.statistics.gui.SalespointComposite;
import ch.eugster.pos.swt.PrintDestinationGroup;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ReceiptListData implements IRunnableWithProgress
{
	
	public ReceiptListData(SalespointComposite salespointComposite, DateRangeGroup dateRangeGroup,
					PrintDestinationGroup printDestinationGroup, Properties properties,
					ReceiptListComposite receiptListComposite)
	{
		// this.sc = salespointComposite;
		// this.drg = dateRangeGroup;
		// this.pdg = printDestinationGroup;
		// this.properties = properties;
		this.rlc = receiptListComposite;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.statistics.data.IStart#start()
	 */
	public void run(IProgressMonitor monitor)
	{
		monitor.beginTask("Belege werden ermittelt...", IProgressMonitor.UNKNOWN);
		TableViewer tv = this.rlc.getViewer();
		tv.setInput(tv);
		if (tv.getSelection() instanceof StructuredSelection)
		{
			
			Event event = new Event();
			event.type = SWT.Selection;
			event.widget = this.rlc;
			StructuredSelection sel = (StructuredSelection) tv.getSelection();
			event.data = sel.isEmpty() ? null : (Receipt) sel.getFirstElement();
			this.rlc.notifyListeners(SWT.Selection, event);
		}
		
		this.rlc.setReceiptTitle(tv.getTable().getItemCount());
		for (int i = 0; i < tv.getTable().getColumnCount(); i++)
		{
			tv.getTable().getColumn(i).pack();
		}
		monitor.done();
	}
	
	// private SalespointComposite sc;
	// private DateRangeGroup drg;
	// private PrintDestinationGroup pdg;
	// private Properties properties;
	private ReceiptListComposite rlc;
}
