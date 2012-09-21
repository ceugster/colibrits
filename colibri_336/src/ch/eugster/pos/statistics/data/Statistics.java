/*
 * Created on 27.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.data;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.statistics.gui.DateRangeGroup;
import ch.eugster.pos.statistics.gui.SalespointComposite;
import ch.eugster.pos.swt.PrintDestinationGroup;
import ch.eugster.pos.util.Config;
import ch.eugster.pos.util.Path;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class Statistics extends Runner implements IRunnableWithProgress
{
	
	/**
	 * 
	 */
	public Statistics(SalespointComposite salespointComposite, DateRangeGroup dateRangeGroup,
					PrintDestinationGroup printDestinationGroup, Properties properties)
	{
		super();
		this.sc = salespointComposite;
		this.drg = dateRangeGroup;
		this.pdg = printDestinationGroup;
		this.properties = properties;
		
		System.setProperty("jasper.reports.compile.temp", Path.getInstance().repDir); //$NON-NLS-1$
	}
	
	protected String getPrintTime()
	{
		SimpleDateFormat sdf = new SimpleDateFormat(Messages.getString("Statistics.dd.MM.yyyy_HH_mm_ss_2")); //$NON-NLS-1$
		return Messages.getString("Statistics.Druckdatum___3").concat(sdf.format(new Date())); //$NON-NLS-1$
	}
	
	protected String getSelectionTextSalespoint()
	{
		StringBuffer sb = new StringBuffer(Messages.getString("Statistics.Kasse___4")); //$NON-NLS-1$
		
		if (this.sc.getSelectedSalespoints().length == 0 || this.sc.areAllSalespointsSelected())
		{
			sb.append(Messages.getString("Statistics.Alle_5")); //$NON-NLS-1$
		}
		else
		{
			Salespoint[] salespoints = this.sc.getSelectedSalespoints();
			for (int i = 0; i < salespoints.length; i++)
			{
				sb.append(salespoints[i].name);
				if (i < salespoints.length - 1) sb.append("; "); //$NON-NLS-1$
			}
		}
		return sb.toString();
	}
	
	protected String getSelectionTextDateRange()
	{
		StringBuffer sb = new StringBuffer(Messages.getString("Statistics.Datumsbereich___7")); //$NON-NLS-1$
		SimpleDateFormat sdf = new SimpleDateFormat(Messages.getString("Statistics.dd.MM.yyyy_8")); //$NON-NLS-1$
		sb.append(sdf.format(this.from));
		sb.append(Messages.getString("Statistics._bis__9")); //$NON-NLS-1$
		sb.append(sdf.format(this.drg.readToDate())); // @2006-03-08 10033
		return sb.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.statistics.data.Statistics#getParameters()
	 */
	protected Hashtable getParameters()
	{
		Hashtable ht = new Hashtable();
		String header = Config.getInstance().getReceiptHeaderText().replaceAll("[|]", "\n");
		ht.put("header", header);
		ht.put("printTime", this.getPrintTime()); //$NON-NLS-1$
		ht.put("salespoints", this.getSelectionTextSalespoint()); //$NON-NLS-1$
		ht.put("dateRange", this.getSelectionTextDateRange()); //$NON-NLS-1$
		ht = this.getMoreParameters(ht);
		return ht;
	}
	
	protected void defineDateRange()
	{
		this.from = this.drg.getFromDate();
		this.to = this.drg.getToDate();
	}
	
	protected boolean prepare(IProgressMonitor monitor, long steps)
	{
		if (this.pdg.getDestination() == PrintDestinationGroup.FILE)
		{
			String path = this.pdg.getFileName();
			if (path == null)
			{
				return false;
			}
		}
		
		int progress = new Long(steps).intValue();
		this.cursor = new Cursor(this.pdg.getDisplay(), SWT.CURSOR_WAIT);
		this.pdg.getShell().setCursor(this.cursor);
		
		monitor.beginTask("Auswertung wird generiert", progress);
		
		this.defineDateRange();
		monitor.worked(1);
		
		this.setOptions();
		monitor.worked(1);
		
		this.setReport();
		monitor.worked(1);
		
		return true;
	}
	
	public void run(IProgressMonitor monitor)
	{
		String path = "";
		int progress = 10;
		
		if (this.prepare(monitor, progress))
		{
			Iterator iterator = this.selectData();
			monitor.worked(1);
			if (iterator == null)
				return;
			else if (!iterator.hasNext())
			{
				org.eclipse.jface.dialogs.MessageDialog
								.openInformation(
												this.pdg.getShell(),
												Messages.getString("Statistics.Keine_Daten_gefunden_14"), Messages.getString("Statistics.Es_wurden_keine_Daten_innerhalb_des_Selektionsbereiches_gefunden._15")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else
			{
				JRDataSource source = this.computeOutput(iterator);
				
				if (this.pdg.getDestination() == PrintDestinationGroup.FILE) path = this.pdg.getFileName();
				
				this.preparePrint(monitor, source, path);
			}
			
			this.complete(monitor);
		}
		
	}
	
	protected void complete(IProgressMonitor monitor)
	{
		monitor.worked(1);
		this.pdg.getShell().setCursor(null);
		this.cursor.dispose();
		monitor.done();
	}
	
	protected void preparePrint(IProgressMonitor monitor, JRDataSource source, String path)
	{
		monitor.worked(1);
		
		this.report = this.getReport();
		monitor.worked(1);
		if (this.report == null)
		{
			//			Logger.getLogger("colibri").info("Report == null"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			org.eclipse.jface.dialogs.MessageDialog
							.openWarning(
											this.pdg.getShell(),
											Messages.getString("Statistics.Bericht_ung_u00FCltig_16"), Messages.getString("Statistics.Der_Bericht_kann_nicht_verarbeitet_werden._Entweder_fehlt_die_Vorlage_oder_sie_hat_ein_ung_u00FCltiges_Format._17")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
		{
			this.printOutput(monitor, source, path);
		}
		source = null;
	}
	
	protected boolean printOutput(IProgressMonitor monitor, JRDataSource source, String path)
	{
		boolean result = false;
		Hashtable ht = this.getParameters();
		monitor.worked(1);
		
		JasperPrint jp = null;
		try
		{
			// Logger.getLogger("colibri").info("Report wird gefüllt...");
			jp = JasperFillManager.fillReport(this.report, ht, source);
			monitor.worked(1);
			
			if (this.pdg.getDestination() == PrintDestinationGroup.SCREEN)
			{
				// Logger.getLogger("colibri").info("Report wird angezeigt...");
				JasperViewer.viewReport(jp, false);
			}
			else if (this.pdg.getDestination() == PrintDestinationGroup.PRINTER)
			{
				// Logger.getLogger("colibri").info("Report wird gedruckt...");
				result = JasperPrintManager.printReport(jp, true);
			}
			else if (this.pdg.getDestination() == PrintDestinationGroup.FILE)
			{
				if (path != null)
				{
					int type = this.pdg.getFileType();
					switch (type)
					{
						case PrintDestinationGroup.FILE_PDF:
							// Logger.getLogger("colibri").info("Report wird als PDF exportiert...");
							JasperExportManager.exportReportToPdfFile(jp, path);
							// Logger.getLogger("colibri").info("Report wurde als PDF exportiert.");
							break;
						case PrintDestinationGroup.FILE_EXCEL:
							result = this.printFile(jp, path);
							break;
						case PrintDestinationGroup.FILE_HTML:
							result = this.printFile(jp, path);
							break;
						case PrintDestinationGroup.FILE_XML:
							JasperExportManager.exportReportToXmlFile(jp, path, false);
							result = true;
							break;
						case PrintDestinationGroup.FILE_CSV:
							result = this.printFile(jp, path);
							break;
					}
				}
			}
			monitor.worked(1);
		}
		catch (JRException e)
		{
			// Logger.getLogger("colibri").info("Exception ausgelöst. "
			// + e.getLocalizedMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	private boolean printFile(JasperPrint jp, String path)
	{
		JRAbstractExporter exporter = this.getExporter(this.pdg.getFileType());
		
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, bOut);
		
		try
		{
			exporter.exportReport();
		}
		catch (JRException e)
		{
			org.eclipse.jface.dialogs.MessageDialog
							.openWarning(
											this.pdg.getShell(),
											Messages.getString("Statistics.Berichtfehler_18"), Messages.getString("Statistics.Der_Bericht_konnte_nicht_erstellt_werden._19")); //$NON-NLS-1$ //$NON-NLS-2$
			return false;
		}
		
		byte[] bytes = bOut.toByteArray();
		
		File file = new File(path);
		if (file.exists())
		{
			file.delete();
		}
		
		try
		{
			if (!file.createNewFile())
			{
				org.eclipse.jface.dialogs.MessageDialog
								.openWarning(
												this.pdg.getShell(),
												Messages.getString("Statistics.Dateifehler_20"), Messages.getString("Statistics.Die_Datei_konnte_nicht_gespeichert_werden._21")); //$NON-NLS-1$ //$NON-NLS-2$
				return false;
			}
			
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(bytes);
			fos.close();
		}
		catch (FileNotFoundException e)
		{
			org.eclipse.jface.dialogs.MessageDialog
							.openWarning(
											this.pdg.getShell(),
											Messages.getString("Statistics.Dateifehler_22"), Messages.getString("Statistics.Die_Datei_konnte_nicht_gespeichert_werden._23")); //$NON-NLS-1$ //$NON-NLS-2$
			return false;
		}
		catch (IOException e)
		{
			org.eclipse.jface.dialogs.MessageDialog
							.openWarning(
											this.pdg.getShell(),
											Messages.getString("Statistics.Dateifehler_24"), Messages.getString("Statistics.Die_Datei_konnte_nicht_gespeichert_werden._25")); //$NON-NLS-1$ //$NON-NLS-2$
			return false;
		}
		return true;
	}
	
	private JRAbstractExporter getExporter(int type)
	{
		switch (type)
		{
			case PrintDestinationGroup.FILE_EXCEL:
				return new JRXlsExporter();
			case PrintDestinationGroup.FILE_HTML:
				return new JRHtmlExporter();
			case PrintDestinationGroup.FILE_CSV:
				return new JRCsvExporter();
		}
		;
		return null;
	}
	
	protected JasperReport getReport()
	{
		JasperReport report = null;
		
		String templateFileName = Path.getInstance().repDir + File.separator + this.reportTemplate;
		String designFileName = Path.getInstance().repDir + File.separator + this.reportDesignName;
		String templatePath = templateFileName + ".xml"; //$NON-NLS-1$
		String designPath = designFileName + ".xml"; //$NON-NLS-1$
		String reportPath = designFileName + ".jasper"; //$NON-NLS-1$
		File templateFile = new File(templatePath);
		File designFile = new File(designPath);
		File reportFile = new File(reportPath);
		
		//			Logger.getLogger("colibri").info(Messages.getString("Statistics.Die_Vorlage_wird_im_Pfad__30") + designPath + Messages.getString("Statistics._gesucht..._31")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		//			Logger.getLogger(Messages.getString("Statistics.colibri_32")).info(Messages.getString("Statistics.Der_kompilierte_Bericht_wird_im_Pfad__33") + reportPath + Messages.getString("Statistics._gesucht..._34")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		if (!designFile.exists())
		{
			this.configureReport();
		}
		
		if (designFile.exists())
		{
			//				Logger.getLogger("colibri").info(Messages.getString("Statistics.Die_Vorlage_existiert._36")); //$NON-NLS-1$ //$NON-NLS-2$
			if (reportFile.exists())
			{
				if (designFile.lastModified() > reportFile.lastModified())
				{
					if (reportFile.exists()) reportFile.delete();
				}
			}
			
			if (!reportFile.exists())
			{
				try
				{
					JasperDesign design = JRXmlLoader.load(designFile.getAbsolutePath());
					JasperCompileManager.compileReportToFile(design, reportPath);
				}
				catch (JRException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		if (reportFile.exists())
		{
			//				Logger.getLogger("colibri").info(Messages.getString("Statistics.Der_Bericht_existiert._46")); //$NON-NLS-1$ //$NON-NLS-2$
			try
			{
				report = (JasperReport) JRLoader.loadObject(reportFile.getAbsolutePath());
			}
			catch (JRException e)
			{
				e.printStackTrace();
			}
		}
		return report;
	}
	
	protected abstract boolean configureReport();
	
	protected abstract void setOptions();
	
	protected abstract void setReport();
	
	protected abstract Iterator selectData();
	
	protected abstract JRDataSource computeOutput(Iterator iterator);
	
	protected abstract Hashtable getMoreParameters(Hashtable hashtable);
	
	protected String reportTemplate;
	protected String reportDesignName;
	// protected JRDataSource source;
	protected JasperReport report;
	
	protected SalespointComposite sc;
	protected DateRangeGroup drg;
	protected PrintDestinationGroup pdg;
	protected Properties properties;
	
	protected Date from = new Date();
	protected Date to = new Date();
	protected Cursor cursor;
}
