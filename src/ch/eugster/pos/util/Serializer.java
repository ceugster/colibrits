/*
 * Created on 14.09.2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import ch.eugster.pos.InvalidValueException;
import ch.eugster.pos.db.Connection;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.Payment;
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.Receipt;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Serializer
{
	
	private static Serializer writer = null;
	private static SAXBuilder reader = null;
	private static final DateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
	private static final DateFormat st = new SimpleDateFormat("HH-mm-ss");
	private static final DateFormat ts = SimpleDateFormat.getDateTimeInstance();
	
	protected Serializer()
	{
		Serializer.writer = this;
	}
	
	public void writeReceipt(Receipt receipt)
	{
		if (Config.getInstance().isLoggingReceipts())
		{
			File file = null;
			OutputStream out = null;
			
			Document doc = this.buildXmlFromReceipt(receipt);
			try
			{
				if (new Boolean(Config.getInstance().isLoggingCompress()).booleanValue())
				{
					file = this.getPath(receipt, true);
					out = new GZIPOutputStream(new FileOutputStream(file));
				}
				else
				{
					file = this.getPath(receipt, false);
					out = new FileOutputStream(file);
				}
				XMLOutputter xmlOutputter = new XMLOutputter();
				xmlOutputter.setTextNormalize(true);
				xmlOutputter.setTextTrim(true);
				xmlOutputter.setIndent("   ");
				xmlOutputter.setEncoding("UTF-8");
				xmlOutputter.setNewlines(true);
				xmlOutputter.output(doc, out);
				System.out.println(xmlOutputter.outputString(doc));
				out.close();
				
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public Document readReceipt(File file) throws Exception
	{
		Document document = null;
		InputStream in;
		try
		{
			if (file.getAbsolutePath().endsWith(Serializer.EXT_COMPRESSED))
			{
				InputStream fin = new FileInputStream(file);
				in = new GZIPInputStream(fin);
			}
			else
			{
				in = new FileInputStream(file);
			}
			Serializer.getReader().setValidation(true);
			document = Serializer.getReader().build(in, Path.getInstance().cfgDir.concat("receipt.dtd"));
			in.close();
		}
		
		catch (FileNotFoundException fex)
		{
			throw new Exception("Datei nicht gefunden.");
		}
		catch (IOException iex)
		{
			throw new Exception("Die Datei lässt sich nicht öffnen.");
		}
		catch (JDOMException jex)
		{
			jex.printStackTrace();
			throw new Exception("Das Format der Datei ist ungültig.");
		}
		return document;
	}
	
	public void deleteReceipt(File file)
	{
		if (file.exists())
		{
			file.delete();
		}
	}
	
	public File getPath(Receipt receipt)
	{
		return this.getPath(receipt, false);
	}
	
	public File getPath(Receipt receipt, boolean compressed)
	{
		return this.getPath(receipt, Database.getCurrent(), compressed);
	}
	
	public File getPath(Receipt receipt, Connection connection, boolean compressed)
	{
		/**
		 * Pfad wählen, Aufbau: [COLIBRI_HOME]/save/Datum ODER FALLS TEMPORÄR:
		 * [COLIBRI_HOME[/save/failover
		 */
		StringBuffer sbPath = new StringBuffer(Path.getInstance().savDir);
		if (connection.isTemporary())
		{
			sbPath.append(File.separator.concat("failover"));
		}
		else
		{
			sbPath.append(File.separator.concat(Serializer.sd.format(receipt.getDate())));
		}
		File dir = new File(sbPath.toString());
		if (!dir.exists() || !dir.isDirectory())
		{
			dir.mkdirs();
		}
		return new File(dir.getAbsolutePath().concat(
						File.separator.concat(this.getFilename(receipt, connection, compressed))));
	}
	
	// private String getFilename(Receipt receipt, boolean compressed)
	// {
	// return this.getFilename(receipt, Database.getCurrent(), compressed);
	// }
	//	
	/**
	 * Dateiname zusammensetzen, Form: receipt.getNumber() + "_" + ["std" |
	 * "tmp"] + [".xml" | ".xml.gz"], wo receipt.getNumber() = 16 Stellen Stelle
	 * 0-1 : Konstante (1) Stelle 1-6 : Kassennummer Stelle 6-16 : Zähler Stelle
	 * 17-19: entweder 'std' für Standard oder 'tmp' für Temporary ab Stelle 19:
	 * Extension entweder '.xml' oder '.xml.gz' (gezippte Datei)
	 */
	private String getFilename(Receipt receipt, Connection connection, boolean compressed)
	{
		return receipt.getNumber() + connection.getCode()
						+ (compressed ? Serializer.EXT_COMPRESSED : Serializer.EXT_XML);
	}
	
	private Document buildXmlFromReceipt(Receipt receipt)
	{
		Document doc = new Document();
		DocType type = new DocType("receipt");
		type.setSystemID("receipt.dtd");
		doc.setDocType(type);
		doc.setRootElement(this.getRootElement(receipt));
		return doc;
	}
	
	private Element getRootElement(Receipt receipt)
	{
		Element root = receipt.getJDOMRecordAttributes(true, false);
		/*
		 * Positions
		 */
		Element positionList = new Element("positions");
		Position[] positions = receipt.getPositionsAsArray();
		for (int i = 0; i < positions.length; i++)
		{
			positionList.addContent(this.getPositionElement(positions[i]));
		}
		root.addContent(positionList);
		/*
		 * Payments
		 */
		Element paymentList = new Element("payments");
		Payment[] payments = receipt.getPaymentsAsArray();
		for (int i = 0; i < payments.length; i++)
		{
			paymentList.addContent(this.getPaymentElement(payments[i]));
		}
		root.addContent(paymentList);
		return root;
	}
	
	private Element getPositionElement(Position position)
	{
		return position.getJDOMRecordAttributes(true, false);
	}
	
	private Element getPaymentElement(Payment payment)
	{
		return payment.getJDOMRecordAttributes(true, false);
	}
	
	public Receipt getReceipt(Element root, boolean useExportIds) throws InvalidValueException
	{
		Receipt receipt = Receipt.getReceipt(root, true, false);
		
		/*
		 * Positions
		 */
		Element po = root.getChild("positions");
		List positions = po.getChildren("position");
		Iterator iterator = positions.iterator();
		while (iterator.hasNext())
		{
			receipt.addPosition(this.getPosition(receipt, (Element) iterator.next()));
		}
		/*
		 * Payments
		 */
		Element pa = root.getChild("positions");
		List payments = pa.getChildren("position");
		iterator = positions.iterator();
		while (iterator.hasNext())
		{
			receipt.addPayment(this.getPayment(receipt, (Element) iterator.next()));
		}
		
		return receipt;
	}
	
	private Position getPosition(Receipt receipt, Element child) throws InvalidValueException
	{
		Position position = Position.getEmptyInstance();
		position.setRecordAttributes(receipt, child, true, false);
		return position;
	}
	
	private Payment getPayment(Receipt receipt, Element child) throws InvalidValueException
	{
		Payment payment = Payment.getEmptyInstance();
		payment.setRecordAttributes(receipt, child, true, false);
		return payment;
	}
	
	public static Serializer getInstance()
	{
		if (Serializer.writer == null)
		{
			Serializer.writer = new Serializer();
		}
		return Serializer.writer;
	}
	
	public static SAXBuilder getReader()
	{
		if (Serializer.reader == null)
		{
			Serializer.reader = new SAXBuilder(true);
		}
		return Serializer.reader;
	}
	
	private static final String EXT_COMPRESSED = ".xml.gz";
	private static final String EXT_XML = ".xml";
}
