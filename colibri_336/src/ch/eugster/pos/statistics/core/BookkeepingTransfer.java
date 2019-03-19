/*
 * Created on 15.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.core;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.statistics.gui.BookkeepingTransferComposite;
import ch.eugster.pos.statistics.gui.DateRangeGroup;
import ch.eugster.pos.statistics.gui.SalespointComposite;
import ch.eugster.pos.swt.PrintDestinationGroup;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BookkeepingTransfer extends Transfer
{
	
	/**
	 * 
	 */
	public BookkeepingTransfer(SalespointComposite salespointComposite, DateRangeGroup dateRangeGroup,
					PrintDestinationGroup printDestinationGroup, Properties properties,
					BookkeepingTransferComposite bookkeepingTransferComposite)
	{
		super(salespointComposite, dateRangeGroup, printDestinationGroup, properties);
		this.btc = bookkeepingTransferComposite;
		this.properties = properties;
		this.init();
	}
	
	private void init()
	{
		
	}
	
	public void prepare()
	{
		this.pattern = this.properties.getProperty("pattern"); //$NON-NLS-1$
		this.sdf = new SimpleDateFormat(this.pattern);
		this.group = this.properties.getProperty("group"); //$NON-NLS-1$
		this.type = Integer.getInteger(this.properties.getProperty("type")); //$NON-NLS-1$
		this.origin = Integer.getInteger(this.properties.getProperty("origin")); //$NON-NLS-1$
		this.defineDateRange();
	}
	
	public void run(IProgressMonitor monitor)
	{
		monitor.beginTask("Datentransfer wird durchgeführt...", IProgressMonitor.UNKNOWN);
		// receiptsCount = 0;
		this.receiptsProcessed = 0;
		this.receiptsWritten = 0;
		// receiptsSkipped = 0;
		this.setupLogWriter(this.getLogfileName());
		this.prepare();
		this.createTransfer();
		this.closeLog();
		monitor.done();
		return;
	}
	
	protected boolean createTransfer()
	{
		Long newTransactionId = this.btc.getNewTransactionId();
		Long selectedTransactionId = this.btc.getSelectedTransactionId();
		Long currentId = newTransactionId.compareTo(selectedTransactionId) >= 0 ? newTransactionId
						: selectedTransactionId;
		
		Receipt[] receipts = Receipt.selectBookkeeppingTransferReceipts(this.getSalespoints(), this.from, this.to);
		this.writeLog(receipts.length + Messages.getString("BookkeepingTransfer._Belege_selektiert._9")); //$NON-NLS-1$
		BookkeepingTransferReceipt[] transfers = this.createBookkeepingTransferReceipts(receipts, newTransactionId);
		
		if (transfers.length > 0)
		{
			File file = this.createTransferFile(currentId);
			if (file == null)
			{
				org.eclipse.jface.dialogs.MessageDialog
								.openInformation(
												this.btc.getShell(),
												Messages.getString("BookkeepingTransfer.Dateifehler_10"), Messages.getString("BookkeepingTransfer.Die_Transferdatei_konnte_nicht_erstellt_werden._Der_Transfer_wird_abgebrochen._11")); //$NON-NLS-1$ //$NON-NLS-2$
				return false;
			}
			
			if (this.printFile(file, transfers, newTransactionId))
			{
				this.btc.updateTransactionId(currentId);
				
				MessageDialog.openInformation(
								this.btc.getShell(),
								Messages.getString("BookkeepingTransfer.Transfer_beendet_12"), Messages.getString("BookkeepingTransfer.Der_Belegtransfer_in_die_Datei_ist_abgeschlossen._Es_wurden__13") + transfers.length + Messages.getString("BookkeepingTransfer._Belege_transferiert._14")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			else
			{
				MessageDialog.openInformation(
								this.btc.getShell(),
								Messages.getString("BookkeepingTransfer.Dateifehler_15"), Messages.getString("BookkeepingTransfer.Beim_Schreiben_in_die_Transferdatei_ist_ein_Fehler_aufgetreten._Der_Transfer_wird_abgebrochen._16")); //$NON-NLS-1$ //$NON-NLS-2$
				return false;
			}
			
		}
		else
		{
			Toolkit.getDefaultToolkit().beep();
			MessageDialog.openInformation(
							this.btc.getShell(),
							Messages.getString("BookkeepingTransfer.Keine_Belege_gefunden_17"), Messages.getString("BookkeepingTransfer.F_u00FCr_die_festgelegte_Selektion_konnten_keine_Belege_gefunden_werden._18")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		return true;
	}
	
	private BookkeepingTransferReceipt[] createBookkeepingTransferReceipts(Receipt[] receipts, Long transactionId)
	{
		BookkeepingTransferReceipt[] transfers = new BookkeepingTransferReceipt[receipts.length];
		for (int i = 0; i < receipts.length; i++)
		{
			receipts[i].setTransactionId(transactionId);
			receipts[i].setBookingId(this.btc.incrementBookingId());
			receipts[i].store(false);
			transfers[i] = this.createBookkeepingTransferReceipt(receipts[i], this.group, this.type, this.origin);
		}
		return transfers;
	}
	
	private BookkeepingTransferReceipt createBookkeepingTransferReceipt(Receipt receipt, String group, Integer type,
					Integer origin)
	{
		BookkeepingTransferReceipt transfer = new BookkeepingTransferReceipt(this.sdf);
		transfer.setDate(receipt.timestamp);
		transfer.setGroup(group);
		transfer.setType(type);
		transfer.setOrigin(origin);
		transfer.createBookkeepingTransferReceiptDetails(receipt);
		
		String text = Messages.getString("BookkeepingTransfer.Beleg__19") + receipt.getNumber() + Messages.getString("BookkeepingTransfer._verarbeitet..._20"); //$NON-NLS-1$ //$NON-NLS-2$
		this.writeLog(text);
		this.receiptsProcessed++;
		return transfer;
	}
	
	private boolean printFile(File file, BookkeepingTransferReceipt[] transfers, Long newTransactionId)
	{
		boolean done = true;
		try
		{
			String text = Messages.getString("BookkeepingTransfer.Transferdatei_wird_geschrieben..._21"); //$NON-NLS-1$
			this.writeLog(text);
			
			FileOutputStream fos = new FileOutputStream(file);
			for (int i = 0; i < transfers.length; i++)
			{
				fos.write(transfers[i].read().getBytes());
				this.receiptsWritten++;
			}
			text = Messages.getString("BookkeepingTransfer.Transferdatei_wird_geschlossen..._22"); //$NON-NLS-1$
			this.writeLog(text);
			fos.close();
		}
		catch (IOException e)
		{
			this.writeLog(Messages
							.getString("BookkeepingTransfer.Fehler_in_der_Ausgabedatei._Belege_konnten_nicht_geschrieben_werden._23")); //$NON-NLS-1$
			done = false;
		}
		return done;
	}
	
	private File createTransferFile(Long transactionId)
	{
		String path = this.btc.getExportDirectory();
		File dir = new File(path);
		
		try
		{
			if (!dir.exists())
			{
				// dir.createNewFile(); // 10441
				dir.mkdirs();
			}
			
			if (!dir.isDirectory())
			{
				return null;
			}
			else
			{
				path = dir.getAbsolutePath() + "\\FBT_" + transactionId.toString() + ".TAF"; //$NON-NLS-1$ //$NON-NLS-2$
				File file = new File(path);
				if (file.exists())
				{
					file.delete();
				}
				file.createNewFile();
				return file;
			}
			
		}
		catch (FileNotFoundException e)
		{
			return null;
		}
		catch (IOException e)
		{
			return null;
		}
	}
	
	protected String getLogfileName()
	{
		return Messages.getString("BookkeepingTransfer.Fibutransferprotokoll_26"); //$NON-NLS-1$
	}
	
	private String pattern;
	private SimpleDateFormat sdf;
	private String group;
	private Integer type;
	private Integer origin;
	
	private BookkeepingTransferComposite btc;
	private Properties properties;
	// private int receiptsCount = 0;
	private int receiptsProcessed = 0;
	private int receiptsWritten = 0;
	// private int receiptsSkipped = 0;
}
