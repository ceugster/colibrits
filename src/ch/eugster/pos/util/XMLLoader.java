/*
 * Created on 30.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.Version;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class XMLLoader
{
	
	public static Document getDocument(String name, boolean validate) throws MalformedURLException, JDOMException,
					IOException
	{
		File file = new File(name);
		return XMLLoader.getDocument(file, validate);
	}
	
	public static Document getDocument(File file, boolean validate) throws JDOMException, IOException
	{
		URL url = null;
		url = new URL("file", null, 0, file.getAbsolutePath()); //$NON-NLS-1$
		return XMLLoader.getDocument(url, validate);
	}
	
	public static Document getDocument(URL url) throws JDOMException, IOException
	{
		return XMLLoader.getDocument(url, false);
	}
	
	public static Document getDocument(URL url, boolean validate) throws JDOMException, IOException
	{
		return XMLLoader.loadXML(url, validate);
	}
	
	public static Document getDocument(InputStream in, String systemId) throws JDOMException, IOException
	{
		return XMLLoader.loadXML(in, systemId);
	}
	
	public static Document getColibriXml(File file) throws JDOMException, IOException
	{
		URL url = null;
		url = new URL("file", null, 0, file.getAbsolutePath()); //$NON-NLS-1$
		Document doc = null;
		try
		{
			doc = XMLLoader.loadColibriXml(url, true);
		}
		catch (JDOMException e)
		{
			doc = XMLLoader.getColibriXml(url);
		}
		return doc;
	}
	
	private static Document getColibriXml(URL url) throws JDOMException, IOException
	{
		// boolean expandEntities = true;
		String saxDriverClass = null;
		SAXBuilder builder = null;
		if (saxDriverClass == null)
		{
			builder = new SAXBuilder();
		}
		else
		{
			builder = new SAXBuilder(saxDriverClass);
		}
		builder.setValidation(false);
		
		Document doc = builder.build(url);
		if (Config.getInstance().updateConfiguration(doc))
		{
			Config.getInstance().save();
		}
		builder.setValidation(true);
		return builder.build(url);
	}
	
	// 10114
	public static Document loadColibriXml(URL url, boolean validate) throws JDOMException, IOException
	{
		Document doc = null;
		boolean expandEntities = true;
		String saxDriverClass = null;
		SAXBuilder builder = null;
		if (saxDriverClass == null)
		{
			builder = new SAXBuilder();
		}
		else
		{
			builder = new SAXBuilder(saxDriverClass);
		}
		builder.setExpandEntities(expandEntities);
		try
		{
			builder.setValidation(validate);
			doc = builder.build(url);
			String v = doc.getRootElement().getChildText("version");
			int version = 0;
			if (v != null)
			{
				version = new Integer(v).intValue();
			}
			if (version < Version.getBuild())
			{
				if (Config.getInstance().updateConfiguration(doc))
				{
					Config.getInstance().save();
				}
				builder.setValidation(validate);
				doc = builder.build(url);
			}
		}
		catch (JDOMException e)
		{
			e.printStackTrace();
			builder.setValidation(false);
			doc = builder.build(url);
			if (Config.getInstance().updateConfiguration(doc))
			{
				Config.getInstance().save();
			}
			builder.setValidation(true);
			doc = builder.build(url);
		}
		
		return doc;
	}
	
	// 10114
	
	public static Document loadXML(URL url, boolean validate) throws JDOMException, IOException
	{
		boolean expandEntities = true;
		String saxDriverClass = null;
		SAXBuilder builder = null;
		if (saxDriverClass == null)
		{
			builder = new SAXBuilder();
		}
		else
		{
			builder = new SAXBuilder(saxDriverClass);
		}
		builder.setExpandEntities(expandEntities);
		builder.setValidation(validate);
		return builder.build(url);
	}
	
	public static Document loadXML(URL url, String encoding, boolean validate) throws JDOMException, IOException
	{
		boolean expandEntities = true;
		String saxDriverClass = null;
		SAXBuilder builder = null;
		if (saxDriverClass == null)
		{
			builder = new SAXBuilder();
			
		}
		else
		{
			builder = new SAXBuilder(saxDriverClass);
		}
		builder.setExpandEntities(expandEntities);
		builder.setValidation(validate);
		return builder.build(url);
	}
	
	public static Document loadXML(File file, String systemId) throws JDOMException, IOException
	{
		if (file.exists() && file.isFile())
		{
			FileInputStream in = new FileInputStream(file);
			
			boolean expandEntities = true;
			String saxDriverClass = null;
			SAXBuilder builder = null;
			if (saxDriverClass == null)
			{
				builder = new SAXBuilder();
			}
			else
			{
				builder = new SAXBuilder(saxDriverClass);
			}
			builder.setValidation(true);
			builder.setExpandEntities(expandEntities);
			Document doc = builder.build(in, systemId);
			in.close();
			return doc;
		}
		else
		{
			return null;
		}
	}
	
	public static Document loadXML(InputStream in, String systemId) throws JDOMException, IOException
	{
		boolean expandEntities = true;
		String saxDriverClass = null;
		SAXBuilder builder = null;
		if (saxDriverClass == null)
		{
			builder = new SAXBuilder();
		}
		else
		{
			builder = new SAXBuilder(saxDriverClass);
		}
		builder.setValidation(true);
		builder.setExpandEntities(expandEntities);
		Document doc = builder.build(in, systemId);
		in.close();
		return doc;
	}
	
	public static void saveXML(File file, Document doc, String encoding)
	{
		if (file.exists())
		{
			file.delete();
		}
		try
		{
			file.createNewFile();
			OutputStream fout = new FileOutputStream(file);
			Writer writer = new OutputStreamWriter(fout);
			XMLOutputter xmlOutputter = new XMLOutputter();
			xmlOutputter.setTextNormalize(true);
			xmlOutputter.setTextTrim(true);
			xmlOutputter.setIndent("   ");
			xmlOutputter.setEncoding(encoding);
			xmlOutputter.setNewlines(true);
			xmlOutputter.output(doc, writer);
			writer.close();
		}
		catch (IOException e)
		{
		}
	}
	
	public static void saveXML(File file, Document doc)
	{
		XMLLoader.saveXML(file, doc, "ISO-8859-1");
	}
	
	public static void saveXML(File file, String encoding, Document doc)
	{
		if (file.exists())
		{
			file.delete();
		}
		try
		{
			file.createNewFile();
			OutputStream fout = new FileOutputStream(file);
			Writer writer = new OutputStreamWriter(fout);
			XMLOutputter xmlOutputter = new XMLOutputter();
			xmlOutputter.setTextNormalize(true);
			xmlOutputter.setTextTrim(true);
			xmlOutputter.setIndent("   ");
			xmlOutputter.setEncoding(encoding);
			xmlOutputter.setNewlines(true);
			xmlOutputter.output(doc, writer);
			writer.close();
		}
		catch (IOException e)
		{
		}
	}
	
	public static boolean getBoolean(String text)
	{
		if (text == null) return false;
		return text.equals(Messages.getString("XMLLoader.error")) ? true : false; //$NON-NLS-1$
		
	}
	
	public static Timestamp getTimestamp(String text)
	{
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date date;
		try
		{
			date = sdf.parse(text);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			date = new Date(0);
		}
		long time = date.getTime();
		return new Timestamp(time);
	}
	
	public static Timestamp getTimestampFromLong(String text)
	{
		long dateTime = new Date().getTime();
		try
		{
			dateTime = new Long(text).longValue();
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
		}
		return new Timestamp(dateTime);
	}
	
	public static Date getDate(String text)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date date;
		try
		{
			date = sdf.parse(text);
		}
		catch (ParseException e)
		{
			date = new Date(0);
		}
		return date;
	}
	
	public static long getLong(String text)
	{
		long l = 0l;
		try
		{
			l = Long.parseLong(text);
		}
		catch (NumberFormatException e)
		{
		}
		return l;
	}
	
	public static double getDouble(String text)
	{
		double d = 0d;
		try
		{
			d = Double.parseDouble(text);
		}
		catch (Exception e)
		{
		}
		return d;
	}
	
	public static float getFloat(String text)
	{
		float d = 0f;
		try
		{
			d = Float.parseFloat(text);
		}
		catch (NumberFormatException e)
		{
		}
		return d;
	}
	
	public static int getInt(String text)
	{
		int i = 0;
		try
		{
			i = Integer.parseInt(text);
		}
		catch (NumberFormatException e)
		{
		}
		return i;
	}
	
}
