/*
 * Created on 23.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.JDOMException;
import org.jdom.filter.Filter;
import org.jdom.output.XMLOutputter;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class OjbRepositoryHandler
{
	
	protected OjbRepositoryHandler()
	{
		this(Path.getInstance().ojbDir.concat(Path.getInstance().FILE_CFG));
	}
	
	protected OjbRepositoryHandler(String path)
	{
		this(new File(path));
	}
	
	protected OjbRepositoryHandler(File file)
	{
		this.init(file);
	}
	
	protected void init(File file)
	{
		OjbRepositoryHandler.ojbRepositoryHandler = this;
		try
		{
			OjbRepositoryHandler.doc = XMLLoader.getDocument(file, true);
		}
		catch (JDOMException e)
		{
			//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			System.exit(-18);
		}
		catch (IOException e)
		{
			//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			System.exit(-18);
		}
	}
	
	public static OjbRepositoryHandler getOjbRepositoryHandler()
	{
		if (OjbRepositoryHandler.ojbRepositoryHandler == null)
		{
			//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(Messages.getString("Config.Pfad_der_Konfigurationsdatei_2") + " " + Path.getInstance().cfgDir.concat(Path.getInstance().FILE_CFG) + " " + Messages.getString("Config.ermitteln..._5")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			OjbRepositoryHandler.ojbRepository = new File(Path.getInstance().ojbFile);
			//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(Messages.getString("Config.Konfigurationsdatei_7") + " " + Path.getInstance().cfgDir.concat(Path.getInstance().FILE_CFG) + " " + Messages.getString("Config.laden..._10")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			OjbRepositoryHandler.ojbRepositoryHandler = OjbRepositoryHandler
							.loadOjbRepositoryHandler(OjbRepositoryHandler.ojbRepository);
		}
		return OjbRepositoryHandler.ojbRepositoryHandler;
	}
	
	protected static OjbRepositoryHandler loadOjbRepositoryHandler(String fileName)
	{
		OjbRepositoryHandler c = null;
		File file = new File(fileName);
		//		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(Messages.getString("Config.test_config_path_6")); //$NON-NLS-1$ //$NON-NLS-2$
		if (file.exists())
		{
			//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(Messages.getString("Config.config_file_exists_8")); //$NON-NLS-1$ //$NON-NLS-2$
			c = OjbRepositoryHandler.loadOjbRepositoryHandler(file);
		}
		return c;
	}
	
	protected static OjbRepositoryHandler loadOjbRepositoryHandler(File file)
	{
		//		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(Messages.getString("Config.load_config_file_10") + file.getAbsolutePath() + "..."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return new OjbRepositoryHandler(file);
	}
	
	public Document getOjbRepository()
	{
		return OjbRepositoryHandler.doc;
	}
	
	public void copyFromConfiguration(Element jdbcConnectionDescriptor, Element connection)
	{
		jdbcConnectionDescriptor.setAttribute("platform", connection.getAttributeValue("platform")); //$NON-NLS-1$ //$NON-NLS-2$
		jdbcConnectionDescriptor.setAttribute("jdbc-level", connection.getAttributeValue("jdbc-level")); //$NON-NLS-1$ //$NON-NLS-2$
		jdbcConnectionDescriptor.setAttribute("driver", connection.getAttributeValue("driver")); //$NON-NLS-1$ //$NON-NLS-2$
		jdbcConnectionDescriptor.setAttribute("protocol", connection.getAttributeValue("protocol")); //$NON-NLS-1$ //$NON-NLS-2$
		jdbcConnectionDescriptor.setAttribute("subprotocol", connection.getAttributeValue("subprotocol")); //$NON-NLS-1$ //$NON-NLS-2$
		StringBuffer sb = new StringBuffer("//"); //$NON-NLS-1$
		sb.append(connection.getAttributeValue("host")); //$NON-NLS-1$
		sb.append(":"); //$NON-NLS-1$
		sb.append(connection.getAttributeValue("port")); //$NON-NLS-1$
		if (connection.getAttributeValue("database").length() > 0) { //$NON-NLS-1$
			sb.append("/"); //$NON-NLS-1$
			sb.append(connection.getAttributeValue("database")); //$NON-NLS-1$
		}
		sb.append(connection.getAttributeValue("options")); //$NON-NLS-1$
		jdbcConnectionDescriptor.setAttribute("dbalias", sb.toString()); //$NON-NLS-1$
		jdbcConnectionDescriptor.setAttribute("username", connection.getAttributeValue("username")); //$NON-NLS-1$ //$NON-NLS-2$
		jdbcConnectionDescriptor.setAttribute("password", connection.getAttributeValue("password")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void saveOjbRepository()
	{
		this.saveOjbRepository(OjbRepositoryHandler.doc);
	}
	
	public void saveOjbRepository(Document doc)
	{
		OutputStream out = null;
		try
		{
			out = new FileOutputStream(OjbRepositoryHandler.ojbRepository);
			XMLOutputter xmlOut = new XMLOutputter();
			xmlOut.output(doc, out);
		}
		catch (FileNotFoundException e)
		{
			
		}
		catch (IOException e)
		{
			
		}
	}
	
	public static void main(String[] args)
	{
		OjbRepositoryHandler.updateRepositoryUser();
	}
	
	public static void updateRepositoryUser()
	{
		String dataVersion = Config.getInstance().getDataVersion();
		int intDataVersion = new Integer(dataVersion).intValue();
		
		if (intDataVersion < 12)
		{
			OjbRepositoryHandler.updateToDataVersion12();
			intDataVersion = 12;
			dataVersion = "12";
			
			Config.getInstance().setDataVersion(dataVersion);
			Config.getInstance().save();
			Config.reload();
		}
		
		if (intDataVersion < 27)
		{
			OjbRepositoryHandler.updateToDataVersion27();
			intDataVersion = 27;
			dataVersion = "27";
			
			Config.getInstance().setDataVersion(dataVersion);
			Config.getInstance().save();
			Config.reload();
		}
		
		if (intDataVersion < 29)
		{
			OjbRepositoryHandler.updateToDataVersion29();
			intDataVersion = 29;
			dataVersion = "29";
			
			Config.getInstance().setDataVersion(dataVersion);
			Config.getInstance().save();
			Config.reload();
		}
		
		if (intDataVersion < 31)
		{
			OjbRepositoryHandler.updateToDataVersion31();
			intDataVersion = 31;
			dataVersion = "31";
			
			Config.getInstance().setDataVersion(dataVersion);
			Config.getInstance().save();
			Config.reload();
		}
		
		if (intDataVersion < 32)
		{
			OjbRepositoryHandler.updateToDataVersion32();
			intDataVersion = 32;
			dataVersion = "32";
			
			Config.getInstance().setDataVersion(dataVersion);
			Config.getInstance().save();
			Config.reload();
		}
		
		if (intDataVersion < 33)
		{
			OjbRepositoryHandler.updateToDataVersion33();
			intDataVersion = 33;
			dataVersion = "33";
			
			Config.getInstance().setDataVersion(dataVersion);
			Config.getInstance().save();
			Config.reload();
		}
		
		if (intDataVersion < 34)
		{
			OjbRepositoryHandler.updateToDataVersion34();
			intDataVersion = 34;
			dataVersion = "34";
			
			Config.getInstance().setDataVersion(dataVersion);
			Config.getInstance().save();
			Config.reload();
		}
	}
	
	public void save()
	{
		// 10179
		Element root = Config.doc.getRootElement();
		Element receiptElement = root.getChild("receipt");
		root.removeChild("receipt");
		EntityRef entityRef = new EntityRef("receipt", "receipt.xml");
		
		Config.doc.getDocType().setInternalSubset("<!ENTITY receipt SYSTEM \"receipt.xml\">");
		root.addContent(entityRef);
		XMLLoader.saveXML(Config.file, Config.doc);
		
		Document receiptDocument = new Document();
		receiptDocument.setRootElement(receiptElement);
		File file = new File(Path.getInstance().cfgDir + "receipt.xml");
		XMLLoader.saveXML(file, receiptDocument);
		
		Config.reload();
		// 10179
	}
	
	private static void updateToDataVersion12()
	{
		try
		{
			boolean doStore = false;
			String fileName = Path.getInstance().ojbDir.concat(Path.getInstance().FILE_OJB);
			File file = new File(fileName);
			Document doc = XMLLoader.getDocument(file, false);
			
			Element root = doc.getRootElement();
			List classes = root.getChildren("class-descriptor");
			Iterator cit = classes.iterator();
			while (cit.hasNext())
			{
				Element clazz = (Element) cit.next();
				if (clazz.getAttributeValue("class").equals("ch.eugster.pos.db.Receipt"))
				{
					List fields = clazz.getChildren("field-descriptor");
					Iterator fit = fields.iterator();
					boolean found = false;
					while (fit.hasNext())
					{
						Element field = (Element) fit.next();
						if (field.getAttributeValue("name").equals("customerId"))
						{
							found = true;
						}
					}
					if (!found)
					{
						Element field = new Element("field-descriptor");
						field.setAttribute("name", "customerId");
						field.setAttribute("column", "customer_id");
						field.setAttribute("jdbc-type", "VARCHAR");
						field.setAttribute("length", "13");
						field.setAttribute("primarykey", "false");
						field.setAttribute("nullable", "true");
						field.setAttribute("indexed", "false");
						field.setAttribute("autoincrement", "false");
						field.setAttribute("locking", "false");
						field.setAttribute("update-lock", "true");
						field.setAttribute("default-fetch", "false");
						field.setAttribute("access", "readwrite");
						clazz.addContent(field);
						doStore = true;
					}
				}
				if (clazz.getAttributeValue("class").equals("ch.eugster.pos.db.Position"))
				{
					List fields = clazz.getChildren("field-descriptor");
					Iterator fit = fields.iterator();
					boolean found = false;
					while (fit.hasNext())
					{
						Element field = (Element) fit.next();
						if (field.getAttributeValue("name").equals("stock"))
						{
							found = true;
						}
					}
					if (!found)
					{
						Element field = new Element("field-descriptor");
						field.setAttribute("name", "stock");
						field.setAttribute("column", "stock");
						field.setAttribute("jdbc-type", "BIT");
						field.setAttribute("primarykey", "false");
						field.setAttribute("nullable", "true");
						field.setAttribute("indexed", "false");
						field.setAttribute("autoincrement", "false");
						field.setAttribute("locking", "false");
						field.setAttribute("update-lock", "true");
						field.setAttribute("default-fetch", "false");
						field.setAttribute("access", "readwrite");
						clazz.addContent(field);
						doStore = true;
					}
				}
			}
			
			cit = classes.iterator();
			while (cit.hasNext())
			{
				Element clazz = (Element) cit.next();
				if (clazz.getAttributeValue("class").equals("ch.eugster.pos.db.FixKey"))
				{
					List fields = clazz.getChildren("field-descriptor");
					Iterator fit = fields.iterator();
					boolean found = false;
					while (fit.hasNext())
					{
						Element field = (Element) fit.next();
						if (field.getAttributeValue("name").equals("bgRedFailover"))
						{
							found = true;
						}
					}
					if (!found)
					{
						Element field = new Element("field-descriptor");
						field.setAttribute("name", "bgRedFailover");
						field.setAttribute("column", "bg_red_failover");
						field.setAttribute("jdbc-type", "INTEGER");
						field.setAttribute("primarykey", "false");
						field.setAttribute("nullable", "true");
						field.setAttribute("indexed", "false");
						field.setAttribute("autoincrement", "false");
						field.setAttribute("locking", "false");
						field.setAttribute("update-lock", "true");
						field.setAttribute("default-fetch", "false");
						field.setAttribute("access", "readwrite");
						clazz.addContent(field);
						doStore = true;
					}
					fit = fields.iterator();
					found = false;
					while (fit.hasNext())
					{
						Element field = (Element) fit.next();
						if (field.getAttributeValue("name").equals("bgGreenFailover"))
						{
							found = true;
						}
					}
					if (!found)
					{
						Element field = new Element("field-descriptor");
						field.setAttribute("name", "bgGreenFailover");
						field.setAttribute("column", "bg_green_failover");
						field.setAttribute("jdbc-type", "INTEGER");
						field.setAttribute("primarykey", "false");
						field.setAttribute("nullable", "true");
						field.setAttribute("indexed", "false");
						field.setAttribute("autoincrement", "false");
						field.setAttribute("locking", "false");
						field.setAttribute("update-lock", "true");
						field.setAttribute("default-fetch", "false");
						field.setAttribute("access", "readwrite");
						clazz.addContent(field);
						doStore = true;
					}
					fit = fields.iterator();
					found = false;
					while (fit.hasNext())
					{
						Element field = (Element) fit.next();
						if (field.getAttributeValue("name").equals("bgBlueFailover"))
						{
							found = true;
						}
					}
					if (!found)
					{
						Element field = new Element("field-descriptor");
						field.setAttribute("name", "bgBlueFailover");
						field.setAttribute("column", "bg_blue_failover");
						field.setAttribute("jdbc-type", "INTEGER");
						field.setAttribute("primarykey", "false");
						field.setAttribute("nullable", "true");
						field.setAttribute("indexed", "false");
						field.setAttribute("autoincrement", "false");
						field.setAttribute("locking", "false");
						field.setAttribute("update-lock", "true");
						field.setAttribute("default-fetch", "false");
						field.setAttribute("access", "readwrite");
						clazz.addContent(field);
						doStore = true;
					}
				}
			}
			
			cit = classes.iterator();
			while (cit.hasNext())
			{
				Element clazz = (Element) cit.next();
				if (clazz.getAttributeValue("class").equals("ch.eugster.pos.db.Receipt"))
				{
					List fields = clazz.getChildren("field-descriptor");
					Iterator fit = fields.iterator();
					boolean found = false;
					while (fit.hasNext())
					{
						Element field = (Element) fit.next();
						if (field.getAttributeValue("name").equals("transferred"))
						{
							found = true;
						}
					}
					if (!found)
					{
						Element field = new Element("field-descriptor");
						field.setAttribute("name", "transferred");
						field.setAttribute("column", "transferred");
						field.setAttribute("jdbc-type", "BIT");
						field.setAttribute("primarykey", "false");
						field.setAttribute("nullable", "true");
						field.setAttribute("indexed", "false");
						field.setAttribute("autoincrement", "false");
						field.setAttribute("locking", "false");
						field.setAttribute("update-lock", "true");
						field.setAttribute("default-fetch", "false");
						field.setAttribute("access", "readwrite");
						clazz.addContent(field);
						doStore = true;
					}
				}
			}
			
			cit = classes.iterator();
			while (cit.hasNext())
			{
				Element clazz = (Element) cit.next();
				if (clazz.getAttributeValue("class").equals("ch.eugster.pos.db.User"))
				{
					List fields = clazz.getChildren("field-descriptor");
					Iterator fit = fields.iterator();
					boolean found = false;
					while (fit.hasNext())
					{
						Element field = (Element) fit.next();
						if (field.getAttributeValue("name").equals("defaultUser"))
						{
							found = true;
						}
					}
					if (!found)
					{
						Element field = new Element("field-descriptor");
						field.setAttribute("name", "defaultUser");
						field.setAttribute("column", "default_user");
						field.setAttribute("jdbc-type", "BIT");
						field.setAttribute("primarykey", "false");
						field.setAttribute("nullable", "true");
						field.setAttribute("indexed", "false");
						field.setAttribute("autoincrement", "false");
						field.setAttribute("locking", "false");
						field.setAttribute("update-lock", "true");
						field.setAttribute("default-fetch", "false");
						field.setAttribute("access", "readwrite");
						clazz.addContent(field);
						doStore = true;
					}
				}
			}
			if (doStore)
			{
				XMLLoader.saveXML(file, doc);
			}
		}
		catch (JDOMException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public static void updateToDataVersion27()
	{
		try
		{
			boolean exists = false;
			String fileName = Path.getInstance().ojbDir.concat(Path.getInstance().FILE_OJB);
			File file = new File(fileName);
			Document doc = XMLLoader.getDocument(file, false);
			
			Element root = doc.getRootElement();
			
			List children = root.getChildren("class-descriptor");
			Iterator iterator = children.iterator();
			while (iterator.hasNext())
			{
				Element cd = (Element) iterator.next();
				if (cd.getAttributeValue("class").equals("ch.eugster.pos.db.Setting")) exists = true;
			}
			if (!exists)
			{
				Element setting = new Element("class-descriptor");
				setting.setAttribute("class", "ch.eugster.pos.db.Setting");
				setting.setAttribute("table", "pos_setting");
				setting.setAttribute("isolation-level", "read-uncommitted");
				setting.setAttribute("accept-locks", "true");
				setting.setAttribute("refresh", "false");
				root.addContent(setting);
				
				Element field = new Element("field-descriptor");
				field.setAttribute("name", "id");
				field.setAttribute("column", "id");
				field.setAttribute("jdbc-type", "BIGINT");
				field.setAttribute("primarykey", "true");
				field.setAttribute("nullable", "false");
				field.setAttribute("indexed", "true");
				field.setAttribute("autoincrement", "true");
				field.setAttribute("locking", "false");
				field.setAttribute("update-lock", "true");
				field.setAttribute("default-fetch", "false");
				field.setAttribute("access", "readwrite");
				setting.addContent(field);
				
				field = new Element("field-descriptor");
				field.setAttribute("name", "use");
				field.setAttribute("column", "com_use");
				field.setAttribute("jdbc-type", "BIT");
				field.setAttribute("primarykey", "false");
				field.setAttribute("nullable", "true");
				field.setAttribute("indexed", "false");
				field.setAttribute("autoincrement", "false");
				field.setAttribute("locking", "false");
				field.setAttribute("update-lock", "true");
				field.setAttribute("default-fetch", "false");
				field.setAttribute("access", "readwrite");
				setting.addContent(field);
				
				field = new Element("field-descriptor");
				field.setAttribute("name", "comClassname");
				field.setAttribute("column", "com_classname");
				field.setAttribute("jdbc-type", "VARCHAR");
				field.setAttribute("primarykey", "false");
				field.setAttribute("nullable", "true");
				field.setAttribute("indexed", "false");
				field.setAttribute("autoincrement", "false");
				field.setAttribute("locking", "false");
				field.setAttribute("update-lock", "true");
				field.setAttribute("default-fetch", "false");
				field.setAttribute("access", "readwrite");
				setting.addContent(field);
				
				field = new Element("field-descriptor");
				field.setAttribute("name", "hold");
				field.setAttribute("column", "com_hold");
				field.setAttribute("jdbc-type", "BIT");
				field.setAttribute("primarykey", "false");
				field.setAttribute("nullable", "true");
				field.setAttribute("indexed", "false");
				field.setAttribute("autoincrement", "false");
				field.setAttribute("locking", "false");
				field.setAttribute("update-lock", "true");
				field.setAttribute("default-fetch", "false");
				field.setAttribute("access", "readwrite");
				setting.addContent(field);
				
				field = new Element("field-descriptor");
				field.setAttribute("name", "update");
				field.setAttribute("column", "com_update");
				field.setAttribute("jdbc-type", "INTEGER");
				field.setAttribute("primarykey", "false");
				field.setAttribute("nullable", "true");
				field.setAttribute("indexed", "false");
				field.setAttribute("autoincrement", "false");
				field.setAttribute("locking", "false");
				field.setAttribute("update-lock", "true");
				field.setAttribute("default-fetch", "false");
				field.setAttribute("access", "readwrite");
				setting.addContent(field);
				
				field = new Element("field-descriptor");
				field.setAttribute("name", "path");
				field.setAttribute("column", "com_path");
				field.setAttribute("jdbc-type", "VARCHAR");
				field.setAttribute("primarykey", "false");
				field.setAttribute("nullable", "true");
				field.setAttribute("indexed", "false");
				field.setAttribute("autoincrement", "false");
				field.setAttribute("locking", "false");
				field.setAttribute("update-lock", "true");
				field.setAttribute("default-fetch", "false");
				field.setAttribute("access", "readwrite");
				setting.addContent(field);
				
				field = new Element("field-descriptor");
				field.setAttribute("name", "showAddCustomerMessage");
				field.setAttribute("column", "com_show_add_customer_message");
				field.setAttribute("jdbc-type", "BIT");
				field.setAttribute("primarykey", "false");
				field.setAttribute("nullable", "true");
				field.setAttribute("indexed", "false");
				field.setAttribute("autoincrement", "false");
				field.setAttribute("locking", "false");
				field.setAttribute("update-lock", "true");
				field.setAttribute("default-fetch", "false");
				field.setAttribute("access", "readwrite");
				setting.addContent(field);
				
				field = new Element("field-descriptor");
				field.setAttribute("name", "searchCd");
				field.setAttribute("column", "com_search_cd");
				field.setAttribute("jdbc-type", "BIT");
				field.setAttribute("primarykey", "false");
				field.setAttribute("nullable", "true");
				field.setAttribute("indexed", "false");
				field.setAttribute("autoincrement", "false");
				field.setAttribute("locking", "false");
				field.setAttribute("update-lock", "true");
				field.setAttribute("default-fetch", "false");
				field.setAttribute("access", "readwrite");
				setting.addContent(field);
				
				field = new Element("field-descriptor");
				field.setAttribute("name", "cdPath");
				field.setAttribute("column", "com_cd_path");
				field.setAttribute("jdbc-type", "VARCHAR");
				field.setAttribute("primarykey", "false");
				field.setAttribute("nullable", "true");
				field.setAttribute("indexed", "false");
				field.setAttribute("autoincrement", "false");
				field.setAttribute("locking", "false");
				field.setAttribute("update-lock", "true");
				field.setAttribute("default-fetch", "false");
				field.setAttribute("access", "readwrite");
				setting.addContent(field);
				
				XMLLoader.saveXML(file, doc, "UTF-8");
			}
		}
		catch (JDOMException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public static void updateToDataVersion29()
	{
		try
		{
			boolean exists = false;
			String fileName = Path.getInstance().ojbDir.concat(Path.getInstance().FILE_OJB);
			File file = new File(fileName);
			Document doc = XMLLoader.getDocument(file, false);
			
			Element root = doc.getRootElement();
			
			List children = root.getChildren("class-descriptor");
			Iterator iterator = children.iterator();
			while (iterator.hasNext())
			{
				Element cd = (Element) iterator.next();
				if (cd.getAttributeValue("class").equals("ch.eugster.pos.db.User"))
				{
					Element field = new Element("field-descriptor");
					field.setAttribute("name", "reverseReceipts");
					field.setAttribute("column", "reverse_receipts");
					field.setAttribute("jdbc-type", "BIT");
					field.setAttribute("primarykey", "false");
					field.setAttribute("nullable", "true");
					field.setAttribute("indexed", "false");
					field.setAttribute("autoincrement", "false");
					field.setAttribute("locking", "false");
					field.setAttribute("update-lock", "true");
					field.setAttribute("default-fetch", "false");
					field.setAttribute("access", "readwrite");
					cd.addContent(field);
				}
			}
			XMLLoader.saveXML(file, doc, "UTF-8");
		}
		catch (JDOMException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public static void updateToDataVersion31()
	{
		try
		{
			boolean exists = false;
			String fileName = Path.getInstance().ojbDir.concat(Path.getInstance().FILE_OJB);
			File file = new File(fileName);
			Document doc = XMLLoader.getDocument(file, false);
			
			Element root = doc.getRootElement();
			
			List children = root.getChildren("class-descriptor");
			Iterator iterator = children.iterator();
			while (iterator.hasNext())
			{
				Element cd = (Element) iterator.next();
				if (cd.getAttributeValue("class").equals("ch.eugster.pos.db.Payment"))
				{
					Element clone = (Element) cd.clone();
					cd.removeChildren("field-descriptor");
					cd.removeChildren("reference-descriptor");
					
					Element[] content = (Element[]) clone.getContent(new Filter()
					{
						public boolean matches(Object object)
						{
							if (object instanceof Element)
							{
								return ((Element) object).getName().equals("field-descriptor");
							}
							return false;
						}
					}).toArray(new Element[0]);
					for (int i = 0; i < content.length; i++)
					{
						cd.addContent((Element) content[i].clone());
					}
					
					Element field = new Element("field-descriptor");
					field.setAttribute("name", "isInputOrWithdraw");
					field.setAttribute("column", "is_input_or_withdraw");
					field.setAttribute("jdbc-type", "BIT");
					field.setAttribute("primarykey", "false");
					field.setAttribute("nullable", "true");
					field.setAttribute("indexed", "false");
					field.setAttribute("autoincrement", "false");
					field.setAttribute("locking", "false");
					field.setAttribute("update-lock", "true");
					field.setAttribute("default-fetch", "false");
					field.setAttribute("access", "readwrite");
					cd.addContent(field);
					
					content = (Element[]) clone.getContent(new Filter()
					{
						public boolean matches(Object object)
						{
							if (object instanceof Element)
							{
								return !((Element) object).getName().equals("field-descriptor");
							}
							return false;
						}
					}).toArray(new Element[0]);
					for (int i = 0; i < content.length; i++)
					{
						cd.addContent((Element) content[i].clone());
					}
				}
			}
			XMLLoader.saveXML(file, doc, "UTF-8");
		}
		catch (JDOMException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public static void updateToDataVersion32()
	{
		try
		{
			String fileName = Path.getInstance().ojbDir.concat(Path.getInstance().FILE_OJB);
			File file = new File(fileName);
			Document doc = XMLLoader.getDocument(file, false);
			
			Element root = doc.getRootElement();
			
			List children = root.getChildren("class-descriptor");
			Iterator iterator = children.iterator();
			while (iterator.hasNext())
			{
				Element cd = (Element) iterator.next();
				if (cd.getAttributeValue("class").equals("ch.eugster.pos.db.Position"))
				{
					boolean exists = false;
					
					Element clone = (Element) cd.clone();
					cd.removeChildren("field-descriptor");
					cd.removeChildren("reference-descriptor");
					
					Element[] content = (Element[]) clone.getContent(new Filter()
					{
						public boolean matches(Object object)
						{
							if (object instanceof Element)
							{
								return ((Element) object).getName().equals("field-descriptor");
							}
							return false;
						}
					}).toArray(new Element[0]);
					for (int i = 0; i < content.length; i++)
					{
						Element element = (Element) content[i].clone();
						if (element.getName().equals("field-descriptor"))
							if (element.getAttributeValue("name").equals("amount")) exists = true;
						cd.addContent((Element) content[i].clone());
					}
					
					if (!exists)
					{
						Element field = new Element("field-descriptor");
						field.setAttribute("name", "amount");
						field.setAttribute("column", "amount");
						field.setAttribute("jdbc-type", "DOUBLE");
						field.setAttribute("primarykey", "false");
						field.setAttribute("nullable", "true");
						field.setAttribute("indexed", "false");
						field.setAttribute("autoincrement", "false");
						field.setAttribute("locking", "false");
						field.setAttribute("update-lock", "true");
						field.setAttribute("default-fetch", "false");
						field.setAttribute("access", "readwrite");
						cd.addContent(field);
					}
					
					content = (Element[]) clone.getContent(new Filter()
					{
						public boolean matches(Object object)
						{
							if (object instanceof Element)
							{
								return !((Element) object).getName().equals("field-descriptor");
							}
							return false;
						}
					}).toArray(new Element[0]);
					for (int i = 0; i < content.length; i++)
					{
						cd.addContent((Element) content[i].clone());
					}
				}
				else if (cd.getAttributeValue("class").equals("ch.eugster.pos.db.Block"))
				{
					List fields = cd.getChildren("field-descriptor");
					Iterator fieldsIterator = fields.iterator();
					while (fieldsIterator.hasNext())
					{
						Object object = fieldsIterator.next();
						if (object instanceof Element)
						{
							Element field = (Element) object;
							if (field.getAttributeValue("name").equals("fontSize"))
							{
								field.setAttribute(new Attribute("jdbc-type", "FLOAT"));
							}
						}
					}
				}
				else if (cd.getAttributeValue("class").equals("ch.eugster.pos.db.FixKey"))
				{
					List fields = cd.getChildren("field-descriptor");
					Iterator fieldsIterator = fields.iterator();
					while (fieldsIterator.hasNext())
					{
						Object object = fieldsIterator.next();
						if (object instanceof Element)
						{
							Element field = (Element) object;
							if (field.getAttributeValue("name").equals("fontSize"))
							{
								field.setAttribute(new Attribute("jdbc-type", "FLOAT"));
							}
						}
					}
				}
				else if (cd.getAttributeValue("class").equals("ch.eugster.pos.db.CustomKey"))
				{
					List fields = cd.getChildren("field-descriptor");
					Iterator fieldsIterator = fields.iterator();
					while (fieldsIterator.hasNext())
					{
						Object object = fieldsIterator.next();
						if (object instanceof Element)
						{
							Element field = (Element) object;
							if (field.getAttributeValue("name").equals("fontSize"))
							{
								field.setAttribute(new Attribute("jdbc-type", "FLOAT"));
							}
						}
					}
				}
				else if (cd.getAttributeValue("class").equals("ch.eugster.pos.db.Tab"))
				{
					List fields = cd.getChildren("field-descriptor");
					Iterator fieldsIterator = fields.iterator();
					while (fieldsIterator.hasNext())
					{
						Object object = fieldsIterator.next();
						if (object instanceof Element)
						{
							Element field = (Element) object;
							if (field.getAttributeValue("name").equals("fontSize"))
							{
								field.setAttribute(new Attribute("jdbc-type", "FLOAT"));
							}
						}
					}
				}
			}
			XMLLoader.saveXML(file, doc, "UTF-8");
		}
		catch (JDOMException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public static void updateToDataVersion33()
	{
		try
		{
			boolean exists = false;
			
			String fileName = Path.getInstance().ojbDir.concat(Path.getInstance().FILE_OJB);
			File file = new File(fileName);
			Document doc = XMLLoader.getDocument(file, false);
			
			Element root = doc.getRootElement();
			
			List classDescriptors = root.getChildren("class-descriptor");
			Iterator iter = classDescriptors.iterator();
			while (iter.hasNext())
			{
				Element classDescriptor = (Element) iter.next();
				if (classDescriptor.getAttributeValue("class").equals("ch.eugster.pos.db.Settlement")) exists = true;
			}
			
			if (!exists)
			{
				Element settlement = new Element("class-descriptor");
				settlement.setAttribute(new Attribute("class", "ch.eugster.pos.db.Settlement"));
				settlement.setAttribute(new Attribute("table", "pos_settlement"));
				settlement.setAttribute(new Attribute("isolation-level", "read-uncommitted"));
				settlement.setAttribute(new Attribute("accept-locks", "true"));
				settlement.setAttribute(new Attribute("refresh", "false"));
				
				Element field = new Element("field-descriptor");
				field.setAttribute(new Attribute("name", "id"));
				field.setAttribute(new Attribute("column", "id"));
				field.setAttribute(new Attribute("jdbc-type", "BIGINT"));
				field.setAttribute(new Attribute("primarykey", "true"));
				field.setAttribute(new Attribute("nullable", "false"));
				field.setAttribute(new Attribute("indexed", "true"));
				field.setAttribute(new Attribute("autoincrement", "true"));
				field.setAttribute(new Attribute("locking", "false"));
				field.setAttribute(new Attribute("update-lock", "true"));
				field.setAttribute(new Attribute("default-fetch", "false"));
				field.setAttribute(new Attribute("access", "readwrite"));
				settlement.addContent(field);
				
				field = new Element("field-descriptor");
				field.setAttribute(new Attribute("name", "salespointId"));
				field.setAttribute(new Attribute("column", "salespoint_id"));
				field.setAttribute(new Attribute("jdbc-type", "BIGINT"));
				field.setAttribute(new Attribute("primarykey", "false"));
				field.setAttribute(new Attribute("nullable", "false"));
				field.setAttribute(new Attribute("indexed", "true"));
				field.setAttribute(new Attribute("autoincrement", "false"));
				field.setAttribute(new Attribute("locking", "false"));
				field.setAttribute(new Attribute("update-lock", "true"));
				field.setAttribute(new Attribute("default-fetch", "false"));
				field.setAttribute(new Attribute("access", "readwrite"));
				settlement.addContent(field);
				
				field = new Element("field-descriptor");
				field.setAttribute(new Attribute("name", "settlement"));
				field.setAttribute(new Attribute("column", "settlement"));
				field.setAttribute(new Attribute("jdbc-type", "BIGINT"));
				field.setAttribute(new Attribute("primarykey", "false"));
				field.setAttribute(new Attribute("nullable", "true"));
				field.setAttribute(new Attribute("indexed", "true"));
				field.setAttribute(new Attribute("autoincrement", "false"));
				field.setAttribute(new Attribute("locking", "false"));
				field.setAttribute(new Attribute("update-lock", "true"));
				field.setAttribute(new Attribute("default-fetch", "false"));
				field.setAttribute(new Attribute("access", "readwrite"));
				settlement.addContent(field);
				
				field = new Element("field-descriptor");
				field.setAttribute(new Attribute("name", "lineNumber"));
				field.setAttribute(new Attribute("column", "line_number"));
				field.setAttribute(new Attribute("jdbc-type", "INTEGER"));
				field.setAttribute(new Attribute("primarykey", "false"));
				field.setAttribute(new Attribute("nullable", "true"));
				field.setAttribute(new Attribute("indexed", "false"));
				field.setAttribute(new Attribute("autoincrement", "false"));
				field.setAttribute(new Attribute("locking", "false"));
				field.setAttribute(new Attribute("update-lock", "true"));
				field.setAttribute(new Attribute("default-fetch", "false"));
				field.setAttribute(new Attribute("access", "readwrite"));
				settlement.addContent(field);
				
				field = new Element("field-descriptor");
				field.setAttribute(new Attribute("name", "type"));
				field.setAttribute(new Attribute("column", "type"));
				field.setAttribute(new Attribute("jdbc-type", "INTEGER"));
				field.setAttribute(new Attribute("primarykey", "false"));
				field.setAttribute(new Attribute("nullable", "true"));
				field.setAttribute(new Attribute("indexed", "false"));
				field.setAttribute(new Attribute("autoincrement", "false"));
				field.setAttribute(new Attribute("locking", "false"));
				field.setAttribute(new Attribute("update-lock", "true"));
				field.setAttribute(new Attribute("default-fetch", "false"));
				field.setAttribute(new Attribute("access", "readwrite"));
				settlement.addContent(field);
				
				field = new Element("field-descriptor");
				field.setAttribute(new Attribute("name", "subtype"));
				field.setAttribute(new Attribute("column", "subtype"));
				field.setAttribute(new Attribute("jdbc-type", "INTEGER"));
				field.setAttribute(new Attribute("primarykey", "false"));
				field.setAttribute(new Attribute("nullable", "true"));
				field.setAttribute(new Attribute("indexed", "false"));
				field.setAttribute(new Attribute("autoincrement", "false"));
				field.setAttribute(new Attribute("locking", "false"));
				field.setAttribute(new Attribute("update-lock", "true"));
				field.setAttribute(new Attribute("default-fetch", "false"));
				field.setAttribute(new Attribute("access", "readwrite"));
				settlement.addContent(field);
				
				field = new Element("field-descriptor");
				field.setAttribute(new Attribute("name", "cashtype"));
				field.setAttribute(new Attribute("column", "cashtype"));
				field.setAttribute(new Attribute("jdbc-type", "INTEGER"));
				field.setAttribute(new Attribute("primarykey", "false"));
				field.setAttribute(new Attribute("nullable", "true"));
				field.setAttribute(new Attribute("indexed", "false"));
				field.setAttribute(new Attribute("autoincrement", "false"));
				field.setAttribute(new Attribute("locking", "false"));
				field.setAttribute(new Attribute("update-lock", "true"));
				field.setAttribute(new Attribute("default-fetch", "false"));
				field.setAttribute(new Attribute("access", "readwrite"));
				settlement.addContent(field);
				
				field = new Element("field-descriptor");
				field.setAttribute(new Attribute("name", "referenceClassName"));
				field.setAttribute(new Attribute("column", "reference_class_name"));
				field.setAttribute(new Attribute("jdbc-type", "VARCHAR"));
				field.setAttribute(new Attribute("primarykey", "false"));
				field.setAttribute(new Attribute("nullable", "true"));
				field.setAttribute(new Attribute("indexed", "false"));
				field.setAttribute(new Attribute("autoincrement", "false"));
				field.setAttribute(new Attribute("locking", "false"));
				field.setAttribute(new Attribute("update-lock", "true"));
				field.setAttribute(new Attribute("default-fetch", "false"));
				field.setAttribute(new Attribute("access", "readwrite"));
				settlement.addContent(field);
				
				field = new Element("field-descriptor");
				field.setAttribute(new Attribute("name", "referenceObjectId"));
				field.setAttribute(new Attribute("column", "reference_object_id"));
				field.setAttribute(new Attribute("jdbc-type", "BIGINT"));
				field.setAttribute(new Attribute("primarykey", "false"));
				field.setAttribute(new Attribute("nullable", "true"));
				field.setAttribute(new Attribute("indexed", "false"));
				field.setAttribute(new Attribute("autoincrement", "false"));
				field.setAttribute(new Attribute("locking", "false"));
				field.setAttribute(new Attribute("update-lock", "true"));
				field.setAttribute(new Attribute("default-fetch", "false"));
				field.setAttribute(new Attribute("access", "readwrite"));
				settlement.addContent(field);
				
				field = new Element("field-descriptor");
				field.setAttribute(new Attribute("name", "shortText"));
				field.setAttribute(new Attribute("column", "short_text"));
				field.setAttribute(new Attribute("jdbc-type", "VARCHAR"));
				field.setAttribute(new Attribute("primarykey", "false"));
				field.setAttribute(new Attribute("nullable", "true"));
				field.setAttribute(new Attribute("indexed", "false"));
				field.setAttribute(new Attribute("autoincrement", "false"));
				field.setAttribute(new Attribute("locking", "false"));
				field.setAttribute(new Attribute("update-lock", "true"));
				field.setAttribute(new Attribute("default-fetch", "false"));
				field.setAttribute(new Attribute("access", "readwrite"));
				settlement.addContent(field);
				
				field = new Element("field-descriptor");
				field.setAttribute(new Attribute("name", "longText"));
				field.setAttribute(new Attribute("column", "long_text"));
				field.setAttribute(new Attribute("jdbc-type", "VARCHAR"));
				field.setAttribute(new Attribute("primarykey", "false"));
				field.setAttribute(new Attribute("nullable", "true"));
				field.setAttribute(new Attribute("indexed", "false"));
				field.setAttribute(new Attribute("autoincrement", "false"));
				field.setAttribute(new Attribute("locking", "false"));
				field.setAttribute(new Attribute("update-lock", "true"));
				field.setAttribute(new Attribute("default-fetch", "false"));
				field.setAttribute(new Attribute("access", "readwrite"));
				settlement.addContent(field);
				
				field = new Element("field-descriptor");
				field.setAttribute(new Attribute("name", "value"));
				field.setAttribute(new Attribute("column", "value"));
				field.setAttribute(new Attribute("jdbc-type", "DOUBLE"));
				field.setAttribute(new Attribute("primarykey", "false"));
				field.setAttribute(new Attribute("nullable", "true"));
				field.setAttribute(new Attribute("indexed", "false"));
				field.setAttribute(new Attribute("autoincrement", "false"));
				field.setAttribute(new Attribute("locking", "false"));
				field.setAttribute(new Attribute("update-lock", "true"));
				field.setAttribute(new Attribute("default-fetch", "false"));
				field.setAttribute(new Attribute("access", "readwrite"));
				settlement.addContent(field);
				
				field = new Element("field-descriptor");
				field.setAttribute(new Attribute("name", "quantity"));
				field.setAttribute(new Attribute("column", "quantity"));
				field.setAttribute(new Attribute("jdbc-type", "INTEGER"));
				field.setAttribute(new Attribute("primarykey", "false"));
				field.setAttribute(new Attribute("nullable", "true"));
				field.setAttribute(new Attribute("indexed", "false"));
				field.setAttribute(new Attribute("autoincrement", "false"));
				field.setAttribute(new Attribute("locking", "false"));
				field.setAttribute(new Attribute("update-lock", "true"));
				field.setAttribute(new Attribute("default-fetch", "false"));
				field.setAttribute(new Attribute("access", "readwrite"));
				settlement.addContent(field);
				
				field = new Element("field-descriptor");
				field.setAttribute(new Attribute("name", "amount1"));
				field.setAttribute(new Attribute("column", "amount1"));
				field.setAttribute(new Attribute("jdbc-type", "DOUBLE"));
				field.setAttribute(new Attribute("primarykey", "false"));
				field.setAttribute(new Attribute("nullable", "true"));
				field.setAttribute(new Attribute("indexed", "false"));
				field.setAttribute(new Attribute("autoincrement", "false"));
				field.setAttribute(new Attribute("locking", "false"));
				field.setAttribute(new Attribute("update-lock", "true"));
				field.setAttribute(new Attribute("default-fetch", "false"));
				field.setAttribute(new Attribute("access", "readwrite"));
				settlement.addContent(field);
				
				field = new Element("field-descriptor");
				field.setAttribute(new Attribute("name", "amount2"));
				field.setAttribute(new Attribute("column", "amount2"));
				field.setAttribute(new Attribute("jdbc-type", "DOUBLE"));
				field.setAttribute(new Attribute("primarykey", "false"));
				field.setAttribute(new Attribute("nullable", "true"));
				field.setAttribute(new Attribute("indexed", "false"));
				field.setAttribute(new Attribute("autoincrement", "false"));
				field.setAttribute(new Attribute("locking", "false"));
				field.setAttribute(new Attribute("update-lock", "true"));
				field.setAttribute(new Attribute("default-fetch", "false"));
				field.setAttribute(new Attribute("access", "readwrite"));
				settlement.addContent(field);
				
				field = new Element("field-descriptor");
				field.setAttribute(new Attribute("name", "code"));
				field.setAttribute(new Attribute("column", "code"));
				field.setAttribute(new Attribute("jdbc-type", "VARCHAR"));
				field.setAttribute(new Attribute("primarykey", "false"));
				field.setAttribute(new Attribute("nullable", "true"));
				field.setAttribute(new Attribute("indexed", "false"));
				field.setAttribute(new Attribute("autoincrement", "false"));
				field.setAttribute(new Attribute("locking", "false"));
				field.setAttribute(new Attribute("update-lock", "true"));
				field.setAttribute(new Attribute("default-fetch", "false"));
				field.setAttribute(new Attribute("access", "readwrite"));
				settlement.addContent(field);
				
				field = new Element("field-descriptor");
				field.setAttribute(new Attribute("name", "receipts"));
				field.setAttribute(new Attribute("column", "receipts"));
				field.setAttribute(new Attribute("jdbc-type", "INTEGER"));
				field.setAttribute(new Attribute("primarykey", "false"));
				field.setAttribute(new Attribute("nullable", "true"));
				field.setAttribute(new Attribute("indexed", "false"));
				field.setAttribute(new Attribute("autoincrement", "false"));
				field.setAttribute(new Attribute("locking", "false"));
				field.setAttribute(new Attribute("update-lock", "true"));
				field.setAttribute(new Attribute("default-fetch", "false"));
				field.setAttribute(new Attribute("access", "readwrite"));
				settlement.addContent(field);
				
				root.addContent(settlement);
				
				XMLLoader.saveXML(file, doc, "UTF-8");
			}
		}
		catch (JDOMException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public static void updateToDataVersion34()
	{
		try
		{
			String fileName = Path.getInstance().ojbDir.concat(Path.getInstance().FILE_OJB);
			File file = new File(fileName);
			Document doc = XMLLoader.getDocument(file, false);
			
			Element root = doc.getRootElement();
			
			List children = root.getChildren("class-descriptor");
			Iterator iterator = children.iterator();
			while (iterator.hasNext())
			{
				Element cd = (Element) iterator.next();
				if (cd.getAttributeValue("class").equals("ch.eugster.pos.db.PaymentType"))
				{
					boolean exists = false;
					
					Element clone = (Element) cd.clone();
					cd.removeChildren("field-descriptor");
					cd.removeChildren("reference-descriptor");
					
					Element[] content = (Element[]) clone.getContent(new Filter()
					{
						public boolean matches(Object object)
						{
							if (object instanceof Element)
							{
								return ((Element) object).getName().equals("field-descriptor");
							}
							return false;
						}
					}).toArray(new Element[0]);
					for (int i = 0; i < content.length; i++)
					{
						Element element = (Element) content[i].clone();
						if (element.getName().equals("field-descriptor"))
							if (element.getAttributeValue("name").equals("sort")) exists = true;
						cd.addContent((Element) content[i].clone());
					}
					
					if (!exists)
					{
						Element field = new Element("field-descriptor");
						field.setAttribute("name", "sort");
						field.setAttribute("column", "sort");
						field.setAttribute("jdbc-type", "INTEGER");
						field.setAttribute("primarykey", "false");
						field.setAttribute("nullable", "true");
						field.setAttribute("indexed", "false");
						field.setAttribute("autoincrement", "false");
						field.setAttribute("locking", "false");
						field.setAttribute("update-lock", "true");
						field.setAttribute("default-fetch", "false");
						field.setAttribute("access", "readwrite");
						cd.addContent(field);
					}
					
					content = (Element[]) clone.getContent(new Filter()
					{
						public boolean matches(Object object)
						{
							if (object instanceof Element)
							{
								return !((Element) object).getName().equals("field-descriptor");
							}
							return false;
						}
					}).toArray(new Element[0]);
					for (int i = 0; i < content.length; i++)
					{
						cd.addContent((Element) content[i].clone());
					}
				}
			}
			
			XMLLoader.saveXML(file, doc, "UTF-8");
		}
		catch (JDOMException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void close()
	{
		OjbRepositoryHandler.doc = null;
	}
	
	public void dispose()
	{
		OjbRepositoryHandler.doc = null;
	}
	
	protected static File ojbRepository;
	protected static OjbRepositoryHandler ojbRepositoryHandler = null;
	protected static Document doc = null;
}
