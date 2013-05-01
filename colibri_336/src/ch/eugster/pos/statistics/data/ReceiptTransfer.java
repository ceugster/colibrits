/*
 * Created on 14.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.data;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.ojb.broker.PersistenceBroker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.XMLOutputter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import ch.eugster.pos.InvalidValueException;
import ch.eugster.pos.Messages;
import ch.eugster.pos.db.CurrentTax;
import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.Payment;
import ch.eugster.pos.db.PaymentType;
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.db.Tax;
import ch.eugster.pos.statistics.gui.ColibriNewExportFileFilter;
import ch.eugster.pos.statistics.gui.ColibriOldExportFileFilter;
import ch.eugster.pos.statistics.gui.DateRangeGroup;
import ch.eugster.pos.statistics.gui.ReceiptTransferComposite;
import ch.eugster.pos.statistics.gui.SalespointComposite;
import ch.eugster.pos.statistics.gui.TranslatorList;
import ch.eugster.pos.swt.PrintDestinationGroup;
import ch.eugster.pos.util.Config;
import ch.eugster.pos.util.Path;
import ch.eugster.pos.util.XMLLoader;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ReceiptTransfer extends Transfer
{
	
	/**
	 * @param salespointComposite
	 * @param dateRangeGroup
	 * @param printDestinationGroup
	 * @param properties
	 */
	public ReceiptTransfer(SalespointComposite salespointComposite, DateRangeGroup dateRangeGroup,
					PrintDestinationGroup printDestinationGroup, Properties properties,
					ReceiptTransferComposite receiptComposite)
	{
		super(salespointComposite, dateRangeGroup, printDestinationGroup, properties);
		this.receiptComposite = receiptComposite;
	}
	
	public void setPath(String path)
	{
		
	}
	
	public void run(IProgressMonitor monitor)
	{
		if (this.receiptComposite.getTransferIndex() == 0)
		{
			this.filesRead = 0;
			this.recordsRead = 0;
			this.filesSkipped = 0;
			this.recordsSkipped = 0;
			this.setupLogWriter(Messages.getString("ReceiptTransfer.Import_1")); //$NON-NLS-1$
			this.doImport(monitor);
			this.closeLog();
			Database.getCurrent().getBroker().clearCache();
			String text = Messages.getString("ReceiptTransfer.Der_Import_ist_beendet._Es_wurden__2") + System.getProperty("line.separator") + //$NON-NLS-1$ //$NON-NLS-2$
							this.filesRead
							+ (this.filesRead == 1 ? Messages.getString("ReceiptTransfer._Datei__4") : Messages.getString("ReceiptTransfer._Dateien__5")) + Messages.getString("ReceiptTransfer.eingelesen._6") + System.getProperty("line.separator") + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							this.recordsRead
							+ (this.recordsRead == 1 ? Messages.getString("ReceiptTransfer._Datensatz__8") : Messages.getString("ReceiptTransfer._Datens_u00E4tze__9")) + Messages.getString("ReceiptTransfer._u00FCbernommen._10") + System.getProperty("line.separator") + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							this.recordsSkipped
							+ (this.recordsSkipped == 1 ? Messages.getString("ReceiptTransfer._Datensatz__12") : Messages.getString("ReceiptTransfer._Datens_u00E4tze__13")) + Messages.getString("ReceiptTransfer.verworfen._14") + System.getProperty("line.separator") + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							this.filesSkipped
							+ (this.filesSkipped == 1 ? Messages.getString("ReceiptTransfer._Datei__16") : Messages.getString("ReceiptTransfer._Dateien__17")) + Messages.getString("ReceiptTransfer._u00FCbersprungen._18"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if (this.filesSkipped > 0)
				text = text
								+ System.getProperty("line.separator") + System.getProperty("line.separator") + Messages.getString("ReceiptTransfer.Das_Protokoll_befindet_sich_im_Verzeichnis___21") + Path.getInstance().DIR_LOG + "."; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			org.eclipse.jface.dialogs.MessageDialog.openInformation(this.receiptComposite.getShell(),
							Messages.getString("ReceiptTransfer.Import_beendet_23"), text); //$NON-NLS-1$
		}
		else if (this.receiptComposite.getTransferIndex() == 1)
		{
			File path = new File(this.receiptComposite.getExportPath());
			if (path.exists() && path.isDirectory())
			{
				this.setupLogWriter(Messages.getString("ReceiptTransfer.Export_24")); //$NON-NLS-1$
				long done = this.doExport(monitor);
				this.closeLog();
				org.eclipse.jface.dialogs.MessageDialog.openInformation(this.receiptComposite.getShell(),
								"Export beendet", "Es wurden " + done + " Belege exportiert.");
			}
			else
			{
				org.eclipse.jface.dialogs.MessageDialog.openInformation(this.receiptComposite.getShell(),
								"Ungültiger Exportpfad", "Der Exportpfad ist ungültig. Der Vorgang wird abgebrochen.");
			}
		}
		return;
	}
	
	public boolean doImport(IProgressMonitor monitor)
	{
		File[] files = this.getFiles();
		if (files == null) return false;
		
		this.writeLog(files.length
						+ Messages.getString("ReceiptTransfer._Importdateien_f_u00FCr_Datenimport_gefunden._25")); //$NON-NLS-1$
		
		FileFilter oldFileFilter = new ColibriOldExportFileFilter();
		FileFilter newFileFilter = new ColibriNewExportFileFilter();
		
		monitor.beginTask("Datenimport wird durchgeführt...", files.length);
		monitor.worked(0);
		for (int i = 0; i < files.length; i++)
		{
			if (oldFileFilter.accept(files[i]))
			{
				this.writeLog(Messages.getString("ReceiptTransfer.Start_Import_Datei__26") + files[i].getName() + Messages.getString("ReceiptTransfer._(altes_Colibriformat)_27")); //$NON-NLS-1$ //$NON-NLS-2$
				if (this.importOld(files[i]))
				{
					this.saveFile(files[i]);
				}
			}
			else if (newFileFilter.accept(files[i]))
			{
				this.writeLog(Messages.getString("ReceiptTransfer.Start_Import_Datei__26") + files[i].getName()); //$NON-NLS-1$ 
				if (this.importNew(files[i]))
				{
					this.saveFile(files[i]);
				}
			}
			monitor.worked(i);
			this.writeLog(Messages.getString("ReceiptTransfer.Ende_Import_Datei__28") + files[i].getName()); //$NON-NLS-1$
		}
		monitor.done();
		return true;
	}
	
	private File[] getFiles()
	{
		File[] files = null;
		if (this.receiptComposite.getImportPath() != null)
		{
			File dir = new File(this.receiptComposite.getImportPath());
			if (dir.exists() && dir.isDirectory())
			{
				FileFilter filter = null;
				if (this.receiptComposite.getOldFashionedColibri())
				{
					filter = new ColibriOldExportFileFilter();
				}
				else
				{
					filter = new ColibriNewExportFileFilter();
				}
				files = dir.listFiles(filter);
			}
		}
		return files;
	}
	
	private void saveFile(File file)
	{
		String fileName = file.getName();
		File saveDir = new File(this.receiptComposite.getImportPath().concat("/".concat("save")));
		if (!saveDir.exists())
		{
			saveDir.mkdir();
		}
		String extension;
		String name;
		int startExtension = file.getName().lastIndexOf(".");
		
		if (startExtension > 0)
		{
			extension = file.getName().substring(startExtension);
			name = file.getName().substring(0, startExtension);
		}
		else
		{
			extension = "";
			name = file.getName();
		}
		
		File out = new File(saveDir.getAbsolutePath() + "/" + name + extension);
		int i = 1;
		while (out.exists())
		{
			out = new File(saveDir.getAbsolutePath() + "/" + name + " (" + i++ + ")" + extension);
		}
		if (file.canRead())
		{
			file.renameTo(out);
			if (file.getName().startsWith("KB"))
			{
				int s = file.getAbsolutePath().lastIndexOf(file.getName());
				String path = file.getAbsolutePath().substring(0, s);
				name = "KP" + name.substring(2);
				file = new File(path + "KP" + file.getName().substring(2));
				if (file.exists())
				{
					out = new File(saveDir.getAbsolutePath() + "/" + name + extension);
					i = 1;
					while (out.exists())
					{
						out = new File(saveDir.getAbsolutePath() + "/" + name + "(" + i++ + ")" + extension);
					}
					if (file.canRead())
					{
						file.renameTo(out);
					}
				}
			}
			this.writeLog("Datei " + fileName + " im Verzeichnis " + saveDir + " gesichert.");
		}
	}
	
	private boolean importOld(File parent)
	{
		this.receiptComposite.getReceiptImportComposite().initTranslatorTable();
		
		String kp = parent.getName();
		kp = kp.replaceFirst(Messages.getString("ReceiptTransfer.KB_29"), Messages.getString("ReceiptTransfer.KP_30")); //$NON-NLS-1$ //$NON-NLS-2$
		kp = parent.getAbsolutePath().replaceFirst(parent.getName(), kp);
		
		if (!parent.exists())
		{
			this.writeLog(Messages.getString("ReceiptTransfer.Dateifehler__Die_Belegsdatei___31") + parent.getName() + Messages.getString("ReceiptTransfer.__existiert_nicht._Datenimport_wird__u00FCbersprungen._32")); //$NON-NLS-1$ //$NON-NLS-2$
			this.filesSkipped++;
			return false;
		}
		
		File child = new File(kp);
		if (!child.exists())
		{
			this.writeLog(Messages.getString("ReceiptTransfer.Dateifehler__Die_Positionendatei___33") + child.getName() + Messages.getString("ReceiptTransfer.__existiert_nicht._Datenimport_wird__u00FCbersprungen._34")); //$NON-NLS-1$ //$NON-NLS-2$
			this.filesSkipped++;
			return false;
		}
		
		char[] r = this.getContent(parent);
		char[] p = this.getContent(child);
		
		String[] receiptHeaders = String.valueOf(r).split(System.getProperty("line.separator")); //$NON-NLS-1$
		this.writeLog(Messages.getString("ReceiptTransfer.Belegdatei___36") + parent.getName() + Messages.getString("ReceiptTransfer.__eingelesen___37") + receiptHeaders.length + Messages.getString("ReceiptTransfer._Datens_u00E4tze._38")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		Hashtable headers = new Hashtable();
		for (String receiptHeader : receiptHeaders)
		{
			String header = receiptHeader.replaceAll("\"", ""); //$NON-NLS-1$ //$NON-NLS-2$
			String[] fields = header.split("\t"); //$NON-NLS-1$
			if (fields.length != 21)
			{
				this.writeLog(Messages.getString("ReceiptTransfer.Formatfehler__Belegdatei___42") + parent.getName() + Messages.getString("ReceiptTransfer.___Ung_u00FCltige_Felderzahl_(Anzahl_Felder_<>_21)._Datenimport_wird__u00FCbersprungen._43")); //$NON-NLS-1$ //$NON-NLS-2$
				this.filesSkipped++;
				return false;
			}
			
			headers.put(fields[15], this.getReceipt(fields));
		}
		
		String[] receiptDetails = String.valueOf(p).split(System.getProperty("line.separator")); //$NON-NLS-1$
		Element[] details = new Element[receiptDetails.length];
		
		this.writeLog(Messages.getString("ReceiptTransfer.Positionsdatei___45") + child.getName() + Messages.getString("ReceiptTransfer.__eingelesen___46") + receiptDetails.length + Messages.getString("ReceiptTransfer._Datens_u00E4tze._47")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		for (int i = 0; i < receiptDetails.length; i++)
		{
			String detail = receiptDetails[i].replaceAll("\"", ""); //$NON-NLS-1$ //$NON-NLS-2$
			String[] fields = detail.split("\t"); //$NON-NLS-1$
			if (fields.length != 33)
			{
				this.writeLog(Messages.getString("ReceiptTransfer.Formatfehler__Positionsdatei___51") + child.getName() + Messages.getString("ReceiptTransfer.___Ung_u00FCltige_Felderzahl_(Anzahl_Felder_<>_33)._Datenimport_wird__u00FCbersprungen._52")); //$NON-NLS-1$ //$NON-NLS-2$
				this.filesSkipped++;
				return false;
			}
			details[i] = this.getDetails(fields);
			if (details[i] == null)
			{
				this.writeLog(Messages.getString("ReceiptTransfer.Datenfehler__Positionsdatei___53") + child.getName() + Messages.getString("ReceiptTransfer.___Ung_u00FCltige_Daten_in_Datensatz._Datenimport_wird__u00FCbersprungen._54")); //$NON-NLS-1$ //$NON-NLS-2$
				this.filesSkipped++;
				return false;
			}
		}
		
		for (Element detail : details)
		{
			Element header = (Element) headers.get(detail.getAttributeValue("receipt-id")); //$NON-NLS-1$
			if (header == null)
			{
				this.writeLog(Messages.getString("ReceiptTransfer.Datenfehler__Positionsdatei___56") + child.getName() + Messages.getString("ReceiptTransfer.___Datensatz_ohne_g_u00FCltige_Referenz_zu_Belegdatensatz._Datenimport_wird__u00FCbersprungen._57")); //$NON-NLS-1$ //$NON-NLS-2$
				this.filesSkipped++;
				return false;
			}
			if (detail.getName().equals("position") || detail.getName().equals("payment")) { //$NON-NLS-1$ //$NON-NLS-2$
				header.addContent(detail);
			}
			else
			{
				this.writeLog(Messages.getString("ReceiptTransfer.Formatfehler__Positionsdatei___60") + child.getName() + Messages.getString("ReceiptTransfer.___Ung_u00FCltige_Datensatzart_in_Datei._Datenimport_wird__u00FCbersprungen._61")); //$NON-NLS-1$ //$NON-NLS-2$
				this.filesSkipped++;
				return false;
			}
		}
		
		List list = new ArrayList();
		Enumeration enumerationeration = headers.elements();
		while (enumerationeration.hasMoreElements())
		{
			list.add(enumerationeration.nextElement());
		}
		
		if (this.createReceipts(list))
		{
			this.filesRead++;
			return true;
		}
		else
		{
			this.filesSkipped++;
			return false;
		}
	}
	
	private char[] getContent(File file)
	{
		try
		{
			FileReader reader = new FileReader(file);
			long length = file.length();
			int size = new Long(length).intValue();
			char[] r = new char[size];
			reader.read(r, 0, size);
			reader.close();
			return r;
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
	
	private Element getReceipt(String[] fields)
	{
		String timestamp = this.getDate(fields[2], fields[3]);
		return Receipt.getReceipt(timestamp, fields);
	}
	
	private Element getDetails(String[] fields)
	{
		Element detail = null;
		
		Translator translator = this.receiptComposite.getReceiptImportComposite().getTranslator(fields[2]);
		if (translator == null) return detail;
		
		if (translator.targetTable.equals(TranslatorList.PRODUCT_GROUP))
		{
			detail = this.getPositionData(fields, translator);
		}
		else if (translator.targetTable.equals(TranslatorList.PAYMENT_TYPE))
		{
			detail = this.getPaymentData(fields, translator);
		}
		return detail;
	}
	
	private Element getPositionData(String[] fields, Translator translator)
	{
		StringBuffer code = new StringBuffer(this.getTaxTypeCode(Integer.parseInt(fields[13])));
		code.append(this.getTaxRateCode(Integer.parseInt(fields[12])));
		Tax tax = Tax.selectByCode(code.toString(), true);
		String date = this.getDate(fields[19], "00:00"); //$NON-NLS-1$
		Date d = null;
		try
		{
			d = new SimpleDateFormat().parse(date);
		}
		catch (ParseException e)
		{
			this.writeLog(Messages
							.getString("ReceiptTransfer.Datenfehler_in_Positionsdatei__Ung_u00FCltiges_Datumsformat_f_u00FCr_Position_mit_der_Id___79") + fields[0] + Messages.getString("ReceiptTransfer._._Datenimport_wird__u00FCbersprungen._80")); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}
		CurrentTax currentTax = CurrentTax.selectFromDate(tax, d);
		if (currentTax == null)
		{
			this.writeLog(Messages
							.getString("ReceiptTransfer.Datenfehler_in_Positionsdatei__Mehrwertsteuersatz_f_u00FCr_Position_mit_der_Id___81") + fields[0] + Messages.getString("ReceiptTransfer.__in_Zieldatenbank_nicht_vorhanden._Datenimport_wird__u00FCbersprungen._82")); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}
		
		ProductGroup productGroup = ProductGroup.selectById(translator.getTargetId());
		if (productGroup == null)
		{
			this.writeLog("Datenfehler in Positionsdatei: Warengruppe für Position mit der Id" + fields[0] + " in Zieldatenbank nicht vorhanden. Datenimport wird übersprungen."); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}
		
		return Position.getPositionData(code.toString(), currentTax, productGroup, fields, translator);
	}
	
	private Element getPaymentData(String[] fields, Translator translator)
	{
		PaymentType paymentType = PaymentType.selectById(translator.getTargetId());
		if (paymentType == null || paymentType.getId().equals(new Long(0l)))
		{
			this.writeLog(Messages
							.getString("ReceiptTransfer.Datenfehler_in_Positionsdatei__Zahlungsart_f_u00FCr_Zahlung_mit_der_Id___114") + fields[0] + Messages.getString("ReceiptTransfer.__in_Zieldatenbank_nicht_vorhanden._Datenimport_wird__u00FCbersprungen._115")); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}
		Long foreignCurrencyId = paymentType.getForeignCurrencyId();
		ForeignCurrency foreignCurrency = ForeignCurrency.selectById(foreignCurrencyId);
		if (foreignCurrency == null || foreignCurrency.getId().equals(new Long(0l)))
		{
			this.writeLog(Messages
							.getString("ReceiptTransfer.Datenfehler_in_Positionsdatei__Fremdw_u00E4hrung_f_u00FCr_Zahlung_mit_der_Id___116") + fields[0] + Messages.getString("ReceiptTransfer.__in_Zieldatenbank_nicht_vorhanden._Datenimport_wird__u00FCbersprungen._117")); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}
		
		return Payment.getPaymentData(paymentType, foreignCurrency, fields, translator);
	}
	
	private String getDate(String dateString, String timeString)
	{
		GregorianCalendar gc = new GregorianCalendar();
		dateString = dateString.replace('.', ':');
		String[] date = dateString.split(":"); //$NON-NLS-1$
		int year = Integer.parseInt(date[2]);
		int month = Integer.parseInt(date[1]) - 1;
		int day = Integer.parseInt(date[0]);
		String[] time = timeString.split(":"); //$NON-NLS-1$
		int hour = Integer.parseInt(time[0]);
		int minute = Integer.parseInt(time[1]);
		gc.set(GregorianCalendar.YEAR, year);
		gc.set(GregorianCalendar.MONTH, month);
		gc.set(GregorianCalendar.DAY_OF_MONTH, day);
		gc.set(GregorianCalendar.HOUR_OF_DAY, hour);
		gc.set(GregorianCalendar.MINUTE, minute);
		SimpleDateFormat sdf = new SimpleDateFormat();
		return sdf.format(gc.getTime());
	}
	
	private String getTaxTypeCode(int id)
	{
		switch (id)
		{
			case 0:
				return Messages.getString("ReceiptTransfer.U_133"); //$NON-NLS-1$
			case 1:
				return Messages.getString("ReceiptTransfer.M_134"); //$NON-NLS-1$
			case 2:
				return Messages.getString("ReceiptTransfer.I_135"); //$NON-NLS-1$
			default:
				return ""; //$NON-NLS-1$
		}
	}
	
	private String getTaxRateCode(int id)
	{
		switch (id)
		{
			case 0:
				return Messages.getString("ReceiptTransfer.F_137"); //$NON-NLS-1$
			case 1:
				return Messages.getString("ReceiptTransfer.R_138"); //$NON-NLS-1$
			case 2:
				return Messages.getString("ReceiptTransfer.N_139"); //$NON-NLS-1$
			default:
				return ""; //$NON-NLS-1$
		}
	}
	
	private boolean importNew(File file)
	{
		boolean done = false;
		try
		{
			Document doc = XMLLoader.loadXML(file, Path.getInstance().cfgDir.concat("transfer.dtd"));
			Element root = doc.getRootElement();
			done = this.createReceipts(root.getChildren("receipt")); //$NON-NLS-1$
			if (!done)
			{
				this.filesSkipped++;
			}
		}
		catch (JDOMException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return done;
	}
	
	private boolean createReceipts(List list)
	{
		int skipped = 0;
		
		Receipt[] receipts = new Receipt[list.size()];
		Iterator k = list.iterator();
		for (int i = 0; k.hasNext(); i++)
		{
			Element re = (Element) k.next();
			receipts[i] = Receipt.getEmptyReceipt();
			
			try
			{
				receipts[i].setRecordAttributes(re, false, true);
			}
			catch (InvalidValueException e)
			{
				this.writeLog(Messages.getString("ReceiptTransfer.Datenfehler_in_Beleg__149") + receipts[i].getNumber() + Messages.getString("ReceiptTransfer.__Fehlerhafte_Belegattribute._150") + ": " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
				return false;
			}
			
			List po = re.getChildren("position"); //$NON-NLS-1$
			Iterator ip = po.iterator();
			while (ip.hasNext())
			{
				Element p = (Element) ip.next();
				try
				{
					Position position = this.createPosition(receipts[i], p);
					receipts[i].addPosition(position);
				}
				catch (InvalidValueException e)
				{
					this.writeLog(Messages.getString("ReceiptTransfer.Datenfehler_in_Beleg__144") + receipts[i].getNumber() + Messages.getString("ReceiptTransfer.__Fehlerhafte_Positionsattribute._145") + ": " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
					return false;
				}
			}
			
			po = re.getChildren("payment"); //$NON-NLS-1$
			ip = po.iterator();
			while (ip.hasNext())
			{
				Element p = (Element) ip.next();
				try
				{
					Payment payment = this.createPayment(receipts[i], p);
					receipts[i].addPayment(payment);
				}
				catch (InvalidValueException e)
				{
					this.writeLog(Messages.getString("ReceiptTransfer.Datenfehler_in_Beleg__147") + receipts[i].getNumber() + Messages.getString("ReceiptTransfer.__Fehlerhafte_Zahlungsattribute._148") + ": " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
					return false;
				}
			}
		}
		
		PersistenceBroker broker = Database.getCurrent().getBroker();
		broker.beginTransaction();
		for (Receipt receipt : receipts)
		{
			/*
			 * count wurde ergänzt mit der SalespointId, weil number die id der
			 * Salespoint enthält, die auf verschiedenen Systemen aber
			 * unterschiedlich sein kann.
			 * 
			 * 10439 weitere Ergänzung mit der Timestamp
			 */
			if (Receipt.count(receipt.timestamp, receipt.getSalespointId(), receipt.getNumber()) == 0)
			{
				DBResult result = receipt.store(false);
				if (result.getErrorCode() == 0)
				{
					this.recordsRead++;
				}
				else
				{
					this.recordsSkipped++;
					skipped++;
					this.writeLog(result.getErrorText());
					result.log();
					broker.abortTransaction();
					return false;
				}
			}
			else
			{
				this.writeLog("Beleg mit der Nummer " + receipt.getNumber() + " ist bereits vorhanden."); //$NON-NLS-1$ //$NON-NLS-2$
				this.recordsSkipped++;
				skipped++;
			}
		}
		broker.commitTransaction();
		this.filesRead++;
		return this.recordsSkipped == 0;
	}
	
	private Position createPosition(Receipt r, Element e) throws InvalidValueException
	{
		Position p = Position.getEmptyInstance();
		p.setRecordAttributes(r, e, true);
		return p;
	}
	
	private Payment createPayment(Receipt r, Element e) throws InvalidValueException
	{
		Payment p = Payment.getEmptyInstance();
		p.setRecordAttributes(r, e, true);
		return p;
	}
	
	public long doExport(IProgressMonitor monitor)
	{
		long count = Receipt.countSettled(this.sc.getSelectedSalespoints(), this.drg.getFromDate(),
						this.drg.getToDate(), true);
		if (count > 0)
		{
			int i = 0;
			int max = Long.valueOf(count).intValue();
			Element root = this.prepareExport(count);
			
			Iterator iter = Receipt.selectSettled(this.sc.getSelectedSalespoints(), this.drg.getFromDate(),
							this.drg.getToDate(), true);
			monitor.beginTask("Datenexport wird durchgeführt...", max);
			monitor.worked(0);
			while (iter.hasNext())
			{
				this.updateReceipt((Receipt) iter.next(), root);
				monitor.worked(++i / max);
			}
			monitor.done();
			monitor.beginTask("Datei wird gespeichert...", IProgressMonitor.UNKNOWN);
			this.saveExport(root);
			monitor.done();
		}
		return count;
	}
	
	private Element prepareExport(long count)
	{
		Element root = new Element("transfer"); //$NON-NLS-1$
		Salespoint salespoint = this.getSelectedSalespoint()[0];
		root.setAttribute("salespoint", salespoint.getId().toString()); //$NON-NLS-1$
		root.setAttribute("date", new Long(new Date().getTime()).toString());
		root.setAttribute("count", new Long(count).toString());
		return root;
	}
	
	private void updateReceipt(Receipt receipt, Element root)
	{
		root.addContent(receipt.buildJDOMElement(true));
		this.writeLog("Beleg " + receipt.getNumber() + " exportiert.");
	}
	
	private void saveExport(Element root)
	{
		if (root instanceof Element)
		{
			String path = this.receiptComposite.getExportPath();
			if (path.isEmpty())
			{
				path = Config.getInstance().getSalespointExportPath();
			}
			File dir = new File(path);
			if (dir.exists() && dir.isDirectory())
			{
				String name = dir.getAbsolutePath() + File.separator + root.getAttributeValue("salespoint") + //$NON-NLS-1$
								root.getAttributeValue("date") + //$NON-NLS-1$
								".xml"; //$NON-NLS-1$
				try
				{
					File file = new File(name);
					if (!file.exists())
					{
						file.createNewFile();
					}
					OutputStream out = new FileOutputStream(file);
					Document doc = new Document(root);
					doc.setDocType(new DocType("transfer", "transfer.dtd"));
					XMLOutputter xmlOut = new XMLOutputter();
					xmlOut.output(doc, out);
					out.close();
				}
				catch (Exception e)
				{
					org.eclipse.jface.dialogs.MessageDialog
									.openInformation(
													this.receiptComposite.getShell(),
													"Fehler beim Schreiben der Datei",
													"Die Datei mit dem Namen "
																	+ name
																	+ " konnte nicht gespeichert werden. Stellen Sie sicher, dass Sie über die notwendigen Berechtigungen zum Schreiben in das Dateisystem verfügen und dass eine Datei gleichen Namens nicht bereits vorhanden ist.");
				}
			}
			else
			{
				org.eclipse.jface.dialogs.MessageDialog
								.openInformation(this.receiptComposite.getShell(), "Fehler beim Schreiben der Datei",
												"Die Exportdatei konnte nicht gespeichert werden. Das angegebene Zielverzeichnis ist ungültig.");
			}
		}
	}
	
	@Override
	protected String getLogfileName()
	{
		if (this.receiptComposite.getTransferIndex() == 0)
		{
			return Messages.getString("ReceiptTransfer.Importprotokoll_153");
		}
		else if (this.receiptComposite.getTransferIndex() == 1)
		{
			return Messages.getString("ReceiptTransfer.Exportprotokoll_154");
		}
		return "";
	}
	
	public class RootFilterImpl extends XMLFilterImpl
	{
		@Override
		public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
		{
			if (localName.equals("transfer"))
			{
				super.startElement(uri, localName, qName, atts);
				
			}
		}
		
		public void endElement(String uri, String localName, String qName, Attributes atts) throws SAXException
		{
			if (localName.equals("transfer"))
			{
				super.endElement(uri, localName, qName);
			}
		}
	}
	
	private ReceiptTransferComposite receiptComposite;
	private int filesRead = 0;
	private int recordsRead = 0;
	private int filesSkipped = 0;
	private int recordsSkipped = 0;
}
