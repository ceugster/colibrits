/*
 * Created on 22.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.statistics.gui.DateRangeGroup;
import ch.eugster.pos.statistics.gui.Main;
import ch.eugster.pos.statistics.gui.SalespointComposite;
import ch.eugster.pos.util.Path;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class Runner
{
	/*
	 * Select salespoints
	 */
	protected Salespoint[] getSalespoints()
	{
		Salespoint[] salespoints = null;
		if (!this.sc.areAllSalespointsSelected())
		{
			salespoints = this.sc.getSelectedSalespoints();
		}
		return salespoints;
	}
	
	protected Salespoint[] getSelectedSalespoint()
	{
		return this.sc.getSelectedSalespoints();
	}
	
	protected void defineDateRange()
	{
		this.from = this.drg.getFromDate();
		this.to = this.drg.getToDate();
	}
	
	// private IProgressMonitor createMonitor() {
	// if (monitor == null) {
	// return Main.getInstance().getProgressMonitor();
	// }
	// else {
	// return null;
	// }
	// }
	//
	// protected void startMonitorUnknownLength(String text) {
	// monitor = createMonitor();
	// if (monitor != null) {
	// monitor.beginTask(text, IProgressMonitor.UNKNOWN);
	// }
	// }
	//
	// protected void startMonitor(String text, int length) {
	// monitor = createMonitor();
	// if (monitor != null) {
	// monitor.beginTask(text, length);
	// }
	// }
	//
	// protected void updateMonitor(int i) {
	// if (monitor != null) {
	// monitor.worked(i);
	// }
	// }
	//
	// protected void monitorSubTask(String text) {
	// if (monitor != null) {
	// monitor.subTask(text);
	// }
	// }
	//
	// protected void endMonitor() {
	// if (monitor != null) {
	// monitor.done();
	// monitor = null;
	// }
	// }
	//
	protected void setupLogWriter(String fileName)
	{
		String path = Path.getInstance().logDir + fileName + ".log"; //$NON-NLS-1$
		File file = new File(path);
		if (file.exists())
		{
			file.delete();
		}
		try
		{
			if (file.createNewFile())
			{
				this.writer = new FileWriter(file);
				this.writeLog(fileName
								+ Messages.getString("Runner._vom__2") + new Date().toString() + System.getProperty("line.separator")); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		catch (IOException e)
		{
			org.eclipse.jface.dialogs.MessageDialog
							.openError(Main.getInstance().getShell(),
											Messages.getString("Runner.Protokoll_4"), Messages.getString("Runner.Das_Protokoll_f_u00FCr_die_Verarbeitung_der_Belege_konnte_nicht_initialisiert_werden._Der_Lauf_wird_beendet._5")); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	protected void writeLog(String text)
	{
		if (this.writer == null)
		{
			return;
		}
		
		try
		{
			this.writer.write(text + System.getProperty("line.separator")); //$NON-NLS-1$
		}
		catch (IOException e)
		{
		}
	}
	
	protected void closeLog()
	{
		if (this.writer == null)
		{
			return;
		}
		
		try
		{
			this.writer.close();
		}
		catch (IOException e)
		{
		}
	}
	
	private FileWriter writer;
	
	protected Date from;
	protected Date to;
	
	protected SalespointComposite sc;
	protected DateRangeGroup drg;
}
