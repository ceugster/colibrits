/*
 * Created on 23.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.util;

import java.awt.Color;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.jdom.Attribute;
import org.jdom.Comment;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.JDOMException;
import org.jdom.ProcessingInstruction;

import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.Setting;
import ch.eugster.pos.db.Tax;
import ch.eugster.pos.db.Version;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class Config
{
	protected Config()
	{
		this(Path.getInstance().cfgDir.concat(Path.getInstance().FILE_CFG));
	}
	
	protected Config(String path)
	{
		this(new File(path));
	}
	
	protected Config(File file)
	{
		this.init(file);
	}
	
	protected void init(File file)
	{
		Config.config = this;
		try
		{
			Config.doc = XMLLoader.getColibriXml(file);
		}
		catch (JDOMException e)
		{
			e.printStackTrace();
			Toolkit.getDefaultToolkit().beep();
			e.printStackTrace();
			org.eclipse.jface.dialogs.MessageDialog
							.openError(new Display().getActiveShell(), "Fehler in colibri.xml",
											"Die Konfigurationsdatei \"colibri.xml\" enthält Fehler. Möglicherweise entspricht sie nicht der neuesten Version.");
			System.exit(-18);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Toolkit.getDefaultToolkit().beep();
			org.eclipse.jface.dialogs.MessageDialog.openError(new Display().getActiveShell(), "Fehler in colibri.xml",
							"Die Konfigurationsdatei \"colibri.xml\" konnte nicht gelesen werden.");
			System.exit(-18);
		}
	}
	
	public static Config getInstance()
	{
		if (Config.config == null)
		{
			Config.file = new File(new File("properties/colibri.xml").getAbsolutePath()); //$NON-NLS-1$
			Config.config = Config.loadConfiguration(Config.file);
		}
		return Config.config;
	}
	
	protected static Config loadConfiguration(String fileName)
	{
		Config c = null;
		File file = new File(fileName);
		if (file.exists())
		{
			c = Config.loadConfiguration(file);
		}
		return c;
	}
	
	protected static Config loadConfiguration(File file)
	{
		return new Config(file);
	}
	
	public static Config reload()
	{
		Config.config = null;
		return Config.getInstance();
	}
	
	public boolean updateConfiguration(Document doc)
	{
		boolean doSave = false; // 10180
		/*
		 * Erster Update (Version ist noch nicht im colibri.xml gespeichert.
		 */
		if (doc.getRootElement().getChild("version") == null)
		{
			Document newDoc = new Document();
			newDoc.setDocType(new DocType("colibri", "colibri.dtd"));
			newDoc.setRootElement(new Element("colibri"));
			
			Element version = new Element("version");
			version.setText("0");
			newDoc.getRootElement().addContent(version);
			
			List elements = doc.getRootElement().getContent();
			Iterator i = elements.iterator();
			while (i.hasNext())
			{
				Object content = i.next();
				if (content instanceof Element)
				{
					Element el = (Element) ((Element) content).clone();
					newDoc.getRootElement().addContent(el);
				}
				else if (content instanceof Comment)
				{
					Comment cm = (Comment) ((Comment) content).clone();
					cm.detach();
					newDoc.getRootElement().addContent((Comment) content);
				}
				else if (content instanceof EntityRef)
				{
					EntityRef er = (EntityRef) ((EntityRef) content).clone();
					er.detach();
					newDoc.getRootElement().addContent((EntityRef) content);
				}
				else if (content instanceof ProcessingInstruction)
				{
					ProcessingInstruction pi = (ProcessingInstruction) ((ProcessingInstruction) content).clone();
					pi.detach();
					newDoc.getRootElement().addContent((ProcessingInstruction) content);
				}
			}
			
			if (doc.getRootElement().getChild("receipt").getChild("header").getAttribute("printlogo") == null)
			{
				String logo = doc.getRootElement().getChild("receipt").getChild("header").getAttributeValue("logo");
				String logomode = doc.getRootElement().getChild("receipt").getChild("header").getAttributeValue(
								"logomode");
				String rows = doc.getRootElement().getChild("receipt").getChild("header").getAttributeValue("rows");
				String text = doc.getRootElement().getChild("receipt").getChild("header").getAttributeValue("text");
				
				doc.getRootElement().getChild("receipt").getChild("header").removeAttribute("logo");
				doc.getRootElement().getChild("receipt").getChild("header").removeAttribute("logomode");
				doc.getRootElement().getChild("receipt").getChild("header").removeAttribute("rows");
				doc.getRootElement().getChild("receipt").getChild("header").removeAttribute("text");
				
				doc.getRootElement().getChild("receipt").getChild("header").setAttribute("printlogo", "false");
				doc.getRootElement().getChild("receipt").getChild("header").setAttribute("logo", logo);
				doc.getRootElement().getChild("receipt").getChild("header").setAttribute("logomode", logomode);
				doc.getRootElement().getChild("receipt").getChild("header").setAttribute("rows", rows);
				doc.getRootElement().getChild("receipt").getChild("header").setAttribute("text", text);
			}
			
			if (doc.getRootElement().getChild("receipt").getChild("header").getAttributeValue("logo").equals("true"))
			{
				doc.getRootElement().getChild("receipt").getChild("header").setAttribute("logo", "0");
			}
			else if (doc.getRootElement().getChild("receipt").getChild("header").getAttributeValue("logo").equals(
							"false"))
			{
				doc.getRootElement().getChild("receipt").getChild("header").setAttribute("logo", "0");
			}
			doc = newDoc;
		}
		
		if (new Integer(doc.getRootElement().getChild("version").getText()).intValue() < Version.getBuild())
		{
			if (new Integer(doc.getRootElement().getChild("version").getText()).intValue() < 56)
			{
				Element versionElement = doc.getRootElement().getChild("version");
				versionElement.setText("56");
				Element settlementElement = new Element("settlement");
				settlementElement.setAttribute("print-payment-quantity", "false");
				doc.getRootElement().addContent(settlementElement);
			}
			// 10064
			if (new Integer(doc.getRootElement().getChild("version").getText()).intValue() < 96)
			{
				Element versionElement = doc.getRootElement().getChild("version");
				versionElement.setText("96");
				doc.getRootElement().getChild("galileo").setAttribute("show-add-customer-message",
								String.valueOf(false));
			}
			// 10064
			// 10077
			// Neu tracelogging mittels LoggingAspect
			if (new Integer(doc.getRootElement().getChild("version").getText()).intValue() < 105)
			{
				Element versionElement = doc.getRootElement().getChild("version");
				versionElement.setText("105");
				doc.getRootElement().getChild("logging").setAttribute("trace", "false");
			}
			// 10077
			// 10112
			// Reformatieren von colibri.xml
			if (new Integer(doc.getRootElement().getChild("version").getText()).intValue() < 127)
			{
				Element versionElement = doc.getRootElement().getChild("version");
				versionElement.setText("127");
				doc.getRootElement().getChild("logging").setAttribute("trace", "false");
			}
			// 10112
			// 10118
			// Reformatieren von colibri.xml
			if (new Integer(doc.getRootElement().getChild("version").getText()).intValue() < 131)
			{
				Element versionElement = doc.getRootElement().getChild("version");
				versionElement.setText("131");
				Element galileo = doc.getRootElement().getChild("galileo");
				Element newChild = new Element("galileo");
				
				Element[] comServers = (Element[]) galileo.getChildren("com-server").toArray(new Element[0]);
				for (int i = 0; i < comServers.length; i++)
				{
					if (comServers[i].getAttributeValue("id").equals("galserve"))
					{
						newChild.setAttribute("update", new Integer(galileo.getAttributeValue("update")).toString());
						newChild.setAttribute("path", galileo.getAttributeValue("path"));
						newChild.setAttribute("show-add-customer-message", new Boolean(galileo
										.getAttributeValue("show-add-customer-message")).toString());
						newChild.setAttribute("search-cd", new Boolean(comServers[i].getAttributeValue("search-cd"))
										.toString());
						newChild.setAttribute("cd-path", comServers[i].getAttributeValue("cd-path"));
						galileo.setAttribute("use", new Boolean(comServers[i].getAttributeValue("use")).toString());
					}
				}
				galileo.removeChildren("com-server");
				galileo.removeAttribute("class");
				galileo.removeAttribute("update");
				galileo.removeAttribute("path");
				galileo.removeAttribute("show-add-customer-message");
				
				galileo.setName("com-server");
				galileo.setAttribute("class", "ch.eugster.pos.product.GalileoServer");
				galileo.addContent(newChild);
			}
			// 10118
			// 10122
			if (new Integer(doc.getRootElement().getChild("version").getText()).intValue() < 132)
			{
				Element versionElement = doc.getRootElement().getChild("version");
				versionElement.setText("132");
				
				Element comServer = doc.getRootElement().getChild("com-server");
				comServer.setAttribute("hold", "false");
			}
			// 10122
			// 10142
			if (new Integer(doc.getRootElement().getChild("version").getText()).intValue() < 145)
			{
				Element versionElement = doc.getRootElement().getChild("version");
				versionElement.setText("145");
				
				Element inputDefault = doc.getRootElement().getChild("input-default");
				inputDefault.setAttribute("clear-price", "false");
			}
			// 10142
			// 10146
			if (new Integer(doc.getRootElement().getChild("version").getText()).intValue() < 146)
			{
				Element versionElement = doc.getRootElement().getChild("version");
				versionElement.setText("146");
				
				Element receipt = doc.getRootElement().getChild("receipt");
				receipt.setAttribute("automatic-print", "true");
			}
			// 10146
			/*
			 * Build 150
			 */
			// 10156
			if (new Integer(doc.getRootElement().getChild("version").getText()).intValue() < 150)
			{
				Element versionElement = doc.getRootElement().getChild("version");
				versionElement.setText("150");
				
				Element logging = doc.getRootElement().getChild("logging");
				logging.setAttribute("receipts", "true");
			}
			// 10156
			/*
			 * Build 151
			 */
			// 10157
			if (new Integer(doc.getRootElement().getChild("version").getText()).intValue() < 151)
			{
				Element versionElement = doc.getRootElement().getChild("version");
				versionElement.setText("151");
				
				Element customer = new Element("customer");
				customer.setAttribute("rows", "2");
				customer.setAttribute("text", "");
				
				Element col = new Element("col");
				col.setAttribute("id", "0");
				col.setAttribute("width", "0");
				col.setAttribute("align", "left");
				col.setText("receipt.customer.text");
				
				Element row = new Element("row");
				row.setAttribute("id", "0");
				row.setAttribute("cols", "1");
				row.addContent(col);
				customer.addContent(row);
				
				col = new Element("col");
				col.setAttribute("id", "0");
				col.setAttribute("width", "0");
				col.setAttribute("align", "left");
				col.setText("");
				
				row = new Element("row");
				row.setAttribute("id", "1");
				row.setAttribute("cols", "1");
				row.addContent(col);
				customer.addContent(row);
				
				Element receipt = doc.getRootElement().getChild("receipt");
				receipt.addContent(customer);
			}
			// 10157
			/*
			 * Build 152
			 */
			// 10158 * Wird noch nicht benutzt
			if (new Integer(doc.getRootElement().getChild("version").getText()).intValue() < 152)
			{
				Element versionElement = doc.getRootElement().getChild("version");
				versionElement.setText("152");
				
				Element logging = doc.getRootElement().getChild("logging");
				logging.setAttribute("compress", "false");
			}
			// 10158
			
			// 10182
			/*
			 * Build 177
			 */
			// 10182
			if (new Integer(doc.getRootElement().getChild("version").getText()).intValue() < 177)
			{
				Element versionElement = doc.getRootElement().getChild("version");
				versionElement.setText("177");
				
				Element settlement = doc.getRootElement().getChild("settlement");
				settlement.setAttribute("admit-test-settlement", "false");
				doSave = true;
			}
			// 10182
			
			// 10217
			/*
			 * Build 196
			 */
			if (new Integer(doc.getRootElement().getChild("version").getText()).intValue() < 196)
			{
				Element versionElement = doc.getRootElement().getChild("version");
				versionElement.setText("196");
				
				Element list = doc.getRootElement().getChild("detail-block-list");
				Element old = list.getChild("fgcolor");
				list.removeChild("fgcolor");
				
				Element fgcolor = new Element("fgcolor");
				fgcolor.setAttribute("red", old.getAttributeValue("red"));
				fgcolor.setAttribute("green", old.getAttributeValue("green"));
				fgcolor.setAttribute("blue", old.getAttributeValue("blue"));
				
				Element normal = new Element("normal-color");
				normal.addContent(fgcolor);
				list.addContent(normal);
				
				fgcolor = new Element("fgcolor");
				fgcolor.setAttribute("red", old.getAttributeValue("red"));
				fgcolor.setAttribute("green", old.getAttributeValue("green"));
				fgcolor.setAttribute("blue", old.getAttributeValue("blue"));
				
				Element back = new Element("back-color");
				back.addContent(fgcolor);
				list.addContent(back);
				
				fgcolor = new Element("fgcolor");
				fgcolor.setAttribute("red", old.getAttributeValue("red"));
				fgcolor.setAttribute("green", old.getAttributeValue("green"));
				fgcolor.setAttribute("blue", old.getAttributeValue("blue"));
				
				Element expense = new Element("expense-color");
				expense.addContent(fgcolor);
				list.addContent(expense);
				
				doSave = true;
			}
			// 10217
			// 10273
			if (new Integer(doc.getRootElement().getChild("version").getText()).intValue() < 230)
			{
				Element versionElement = doc.getRootElement().getChild("version");
				versionElement.setText("230");
				
				Element element = doc.getRootElement().getChild("input-default");
				element.setAttribute("max-quantity-range", "1000");
				element.setAttribute("max-quantity-amount", "1000");
				
				doSave = true;
			}
			// 10273
			// 10277
			if (new Integer(doc.getRootElement().getChild("version").getText()).intValue() < 233)
			{
				Element versionElement = doc.getRootElement().getChild("version");
				versionElement.setText("233");
				
				Element element = doc.getRootElement().getChild("detail-block");
				
				Element bgColor = new Element("bgcolor");
				bgColor.setAttribute("red", "255");
				bgColor.setAttribute("green", "255");
				bgColor.setAttribute("blue", "255");
				element.addContent(bgColor);
				
				doSave = true;
			}
			// 10277
			// Build 308
			if (new Integer(doc.getRootElement().getChild("version").getText()).intValue() < 308)
			{
				Element versionElement = doc.getRootElement().getChild("version");
				versionElement.setText("308");
				
				Element dataVersionElement = doc.getRootElement().getChild("data-version");
				if (dataVersionElement == null)
				{
					Document newDoc = new Document(new Element("colibri"));
					newDoc.setDocType((DocType) doc.getDocType().clone());
					List rootChildren = doc.getRootElement().getChildren();
					Iterator iterator = rootChildren.iterator();
					while (iterator.hasNext())
					{
						Object object = iterator.next();
						if (object instanceof Element)
						{
							Element next = (Element) object;
							if (next.getName().equals("version"))
							{
								newDoc.getRootElement().addContent((Element) next.clone());
								dataVersionElement = new Element("data-version");
								dataVersionElement.setText("31");
								newDoc.getRootElement().addContent(dataVersionElement);
							}
							else
								newDoc.getRootElement().addContent((Element) next.clone());
						}
					}
					doc = newDoc;
				}
				
				doc.getRootElement().getChild("receipt").getChild("header").setAttribute(
								new Attribute("number-length", "0"));
				
				doSave = true;
			}
			// Build 308
			
			// Build 309
			if (new Integer(doc.getRootElement().getChild("version").getText()).intValue() < 309)
			{
				Element versionElement = doc.getRootElement().getChild("version");
				versionElement.setText("309");
				
				doc.getRootElement().getChild("receipt").setAttribute("take-back-print-twice", "false");
				doc.getRootElement().getChild("receipt").setAttribute("take-back-print-signature", "false");
				
				doSave = true;
			}
			// Build 309
		}
		
		Config.doc = doc;
		return doSave;
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
	
	/*
	 * Version
	 */
	public String getVersion()
	{
		return Config.doc.getRootElement().getChildText("version"); //$NON-NLS-1$
	}
	
	public void setVersion(String version)
	{
		Config.doc.getRootElement().getChild("version").setText(version); //$NON-NLS-1$
	}
	
	/*
	 * DataVersion
	 */
	public String getDataVersion()
	{
		return Config.doc.getRootElement().getChildText("data-version"); //$NON-NLS-1$
	}
	
	/*
	 * DataVersion
	 */
	public void setDataVersion(String dataVersion)
	{
		Config.doc.getRootElement().getChild("data-version").setText(dataVersion); //$NON-NLS-1$
	}
	
	/*
	 * Protokollierungseigenschaften
	 */
	public Element getLogging()
	{
		return Config.doc.getRootElement().getChild("logging"); //$NON-NLS-1$
	}
	
	public String getLoggingLevel()
	{
		return this.getLogging().getAttributeValue("level"); //$NON-NLS-1$
	}
	
	public int getLoggingMax()
	{
		return NumberUtility.parseInt(0, this.getLogging().getAttributeValue("max")); //$NON-NLS-1$
	}
	
	public String getLoggingTrace()
	{
		return this.getLogging().getAttributeValue("trace"); //$NON-NLS-1$
	}
	
	public boolean isLoggingReceipts()
	{
		return new Boolean(this.getLogging().getAttributeValue("receipts")).booleanValue(); //$NON-NLS-1$
	}
	
	public boolean isLoggingCompress()
	{
		return new Boolean(this.getLogging().getAttributeValue("compress")).booleanValue(); //$NON-NLS-1$
	}
	
	/*
	 * DefaultDatenbank und Eigenschaften der einzelnen Datenbankverbindungen
	 */
	public Element getDatabase()
	{
		return Config.doc.getRootElement().getChild("database"); //$NON-NLS-1$
	}
	
	public String getDatabaseDefault()
	{
		return this.getDatabase().getAttributeValue("default"); //$NON-NLS-1$
	}
	
	public Element getDatabaseStandard()
	{
		return this.getDatabase().getChild("standard"); //$NON-NLS-1$
	}
	
	public String getDatabaseStandardName()
	{
		return this.getDatabaseStandard().getAttributeValue("name"); //$NON-NLS-1$
	}
	
	public boolean getDatabaseStandardActive()
	{
		return new Boolean(this.getDatabaseStandard().getAttributeValue("active")).booleanValue(); //$NON-NLS-1$
	}
	
	public Element getDatabaseStandardConnection()
	{
		return this.getDatabaseStandard().getChild("connection"); //$NON-NLS-1$
	}
	
	public Element getDatabaseTemporary()
	{
		return this.getDatabase().getChild("temporary"); //$NON-NLS-1$
	}
	
	public String getDatabaseTemporaryName()
	{
		return this.getDatabaseTemporary().getAttributeValue("name"); //$NON-NLS-1$
	}
	
	public boolean getDatabaseTemporaryActive()
	{
		return new Boolean(this.getDatabaseTemporary().getAttributeValue("active")).booleanValue(); //$NON-NLS-1$
	}
	
	public Element getDatabaseTemporaryConnection()
	{
		return this.getDatabaseTemporary().getChild("connection"); //$NON-NLS-1$
	}
	
	public Element getDatabaseTutorial()
	{
		return this.getDatabase().getChild("tutorial"); //$NON-NLS-1$
	}
	
	public String getDatabaseTutorialName()
	{
		return this.getDatabaseTutorial().getAttributeValue("name"); //$NON-NLS-1$
	}
	
	public boolean getDatabaseTutorialActive()
	{
		return new Boolean(this.getDatabaseTutorial().getAttributeValue("active")).booleanValue(); //$NON-NLS-1$
	}
	
	public Element getDatabaseTutorialConnection()
	{
		return this.getDatabaseTutorial().getChild("connection"); //$NON-NLS-1$
	}
	
	public Element getDatabase(String which)
	{
		Element db = this.getDatabase();
		return db.getChild(which);
	}
	
	/*
	 * Eigenschaften dieser Kasse (id)
	 */
	public Element getSalespoint()
	{
		return Config.doc.getRootElement().getChild("salespoint"); //$NON-NLS-1$
	}
	
	public int getSalespointId()
	{
		return new Integer(NumberUtility.parseInt(0, this.getSalespoint().getAttributeValue("id"))).intValue(); //$NON-NLS-1$
	}
	
	public boolean getSalespointExport()
	{
		return new Boolean(this.getSalespoint().getAttributeValue("export")).booleanValue(); //$NON-NLS-1$
	}
	
	public boolean getSalespointForceSettlement()
	{
		return new Boolean(this.getSalespoint().getAttributeValue("force-settlement")).booleanValue(); //$NON-NLS-1$
	}
	
	public String getSalespointExportPath()
	{
		return this.getSalespoint().getAttributeValue("path"); //$NON-NLS-1$
	}
	
	/*
	 * Eingabevorschlaege
	 */
	public Element getInputDefault()
	{
		return Config.doc.getRootElement().getChild("input-default"); //$NON-NLS-1$
	}
	
	public int getInputDefaultQuantity()
	{
		return new Integer(NumberUtility.parseInt(0, this.getInputDefault().getAttributeValue("quantity"))).intValue(); //$NON-NLS-1$
	}
	
	public long getInputDefaultTax()
	{
		return new Long(NumberUtility.parseLong(0, this.getInputDefault().getAttributeValue("tax"))).longValue(); //$NON-NLS-1$
	}
	
	public String getInputDefaultOption()
	{
		return this.getInputDefault().getAttributeValue("option"); //$NON-NLS-1$
	}
	
	public int getInputDefaultMaxQuantityRange()
	{
		return Integer.valueOf(this.getInputDefault().getAttributeValue("max-quantity-range")).intValue(); //$NON-NLS-1$
	}
	
	public int getInputDefaultMaxQuantityAmount()
	{
		return Integer.valueOf(this.getInputDefault().getAttributeValue("max-quantity-amount")).intValue(); //$NON-NLS-1$
	}
	
	public double getInputDefaultMaxPriceRange()
	{
		return Double.valueOf(this.getInputDefault().getAttributeValue("max-price-range")).doubleValue(); //$NON-NLS-1$
	}
	
	public double getInputDefaultMaxPriceAmount()
	{
		return Double.valueOf(this.getInputDefault().getAttributeValue("max-price-amount")).doubleValue(); //$NON-NLS-1$
	}
	
	public double getInputDefaultMaxPaymentRange()
	{
		return Double.valueOf(this.getInputDefault().getAttributeValue("max-payment-range")).doubleValue(); //$NON-NLS-1$
	}
	
	public double getInputDefaultMaxPaymentAmount()
	{
		return Double.valueOf(this.getInputDefault().getAttributeValue("max-payment-amount")).doubleValue(); //$NON-NLS-1$
	}
	
	public boolean getInputDefaultClearPrice()
	{
		return new Boolean(this.getInputDefault().getAttributeValue("clear-price")).booleanValue(); //$NON-NLS-1$
	}
	
	/*
	 * Eigenschaften und Elemente von Comserver
	 */
	private Element getProductServer()
	{
		return Config.doc.getRootElement().getChild("com-server"); //$NON-NLS-1$
	}
	
	public boolean getProductServerUse()
	{
		return Setting.getInstance().getComServer().isUse();
		//		return new Boolean(this.getProductServer().getAttributeValue("use")).booleanValue(); //$NON-NLS-1$
	}
	
	public boolean getProductServerUseXML()
	{
		return new Boolean(this.getProductServer().getAttributeValue("use")).booleanValue(); //$NON-NLS-1$
	}
	
	public boolean getProductServerHold()
	{
		return Setting.getInstance().getComServer().isHold();
		//		return new Boolean(this.getProductServer().getAttributeValue("hold")).booleanValue(); //$NON-NLS-1$
	}
	
	public boolean getProductServerHoldXML()
	{
		return new Boolean(this.getProductServer().getAttributeValue("hold")).booleanValue(); //$NON-NLS-1$
	}
	
	public String getProductServerClass()
	{
		return Setting.getInstance().getComServer().getClassname();
		//		return this.getProductServer().getAttributeValue("class"); //$NON-NLS-1$
	}
	
	public String getProductServerClassXML()
	{
		return this.getProductServer().getAttributeValue("class"); //$NON-NLS-1$
	}
	
	private Element getGalileo()
	{
		return this.getProductServer().getChild("galileo");
	}
	
	private Element getOcto()
	{
		return this.getProductServer().getChild("octo");
	}
	
	/*
	 * Eigenschaften und Element von Galileo
	 */
	public int getGalileoUpdate()
	{
		return Setting.getInstance().getComServer().getUpdate();
		//		return new Integer(this.getGalileo().getAttributeValue("update")).intValue(); //$NON-NLS-1$
	}
	
	public int getGalileoUpdateXML()
	{
		return new Integer(this.getGalileo().getAttributeValue("update")).intValue(); //$NON-NLS-1$
	}
	
	public String getGalileoPath()
	{
		return Setting.getInstance().getComServer().getPath();
		// return this.getGalileo().getAttributeValue("path");
	}
	
	public String getGalileoPathXML()
	{
		return this.getGalileo().getAttributeValue("path");
	}
	
	public boolean getGalileoShowAddCustomerMessage()
	{
		return Setting.getInstance().getComServer().isShowAddCustomerMessage();
		//		return new Boolean(this.getGalileo().getAttributeValue("show-add-customer-message")).booleanValue(); //$NON-NLS-1$
	}
	
	public boolean getGalileoShowAddCustomerMessageXML()
	{
		return new Boolean(this.getGalileo().getAttributeValue("show-add-customer-message")).booleanValue(); //$NON-NLS-1$
	}
	
	public boolean getGalileoSearchCd()
	{
		return Setting.getInstance().getComServer().isSearchCd();
		//		return new Boolean(this.getGalileo().getAttributeValue("search-cd")).booleanValue(); //$NON-NLS-1$
	}
	
	public boolean getGalileoSearchCdXML()
	{
		return new Boolean(this.getGalileo().getAttributeValue("search-cd")).booleanValue(); //$NON-NLS-1$
	}
	
	public String getGalileoCdPath()
	{
		return Setting.getInstance().getComServer().getCdPath();
		//		return this.getGalileo().getAttributeValue("cd-path"); //$NON-NLS-1$
	}
	
	public String getGalileoCdPathXML()
	{
		return this.getGalileo().getAttributeValue("cd-path"); //$NON-NLS-1$
	}
	
	public Element getLayout()
	{
		return Config.doc.getRootElement().getChild("layout"); //$NON-NLS-1$
	}
	
	public int getLayoutLeftWidth()
	{
		return new Integer(this.getLayout().getAttributeValue("left")).intValue();
	}
	
	public String getLayoutTotalBlock()
	{
		return this.getLayout().getAttributeValue("total-block"); //$NON-NLS-1$
	}
	
	public String getLayoutTopLeft()
	{
		return this.getLayout().getAttributeValue("top-left"); //$NON-NLS-1$
	}
	
	public String getLayoutTopRight()
	{
		return this.getLayout().getAttributeValue("top-right"); //$NON-NLS-1$
	}
	
	public String getLayoutBottomLeft()
	{
		return this.getLayout().getAttributeValue("bottom-left"); //$NON-NLS-1$
	}
	
	public String getLayoutBottomRight()
	{
		return this.getLayout().getAttributeValue("bottom-right"); //$NON-NLS-1$
	}
	
	/*
	 * Eigenschaften von Locale
	 */
	public Element getLocale()
	{
		return Config.doc.getRootElement().getChild("locale"); //$NON-NLS-1$
	}
	
	public String getLocaleLanguage()
	{
		return this.getLocale().getAttributeValue("language"); //$NON-NLS-1$
	}
	
	public String getLocaleCountry()
	{
		return this.getLocale().getAttributeValue("country"); //$NON-NLS-1$
	}
	
	/*
	 * Eigenschaften von Currency
	 */
	public Element getCurrency()
	{
		return Config.doc.getRootElement().getChild("currency"); //$NON-NLS-1$
	}
	
	public String getCurrencyDefault()
	{
		return this.getCurrency().getAttributeValue("default"); //$NON-NLS-1$
	}
	
	public Element getCurrencyRoundfactor()
	{
		return this.getCurrency().getChild("roundfactor"); //$NON-NLS-1$
	}
	
	public double getCurrencyRoundFactorAmount()
	{
		return new Double(NumberUtility.parseDouble(0.01d, this.getCurrencyRoundfactor().getAttributeValue("amount"))).doubleValue(); //$NON-NLS-1$
	}
	
	public float getCurrencyRoundFactorTax()
	{
		return new Float(NumberUtility.parseFloat(0.01f, this.getCurrencyRoundfactor().getAttributeValue("tax"))).floatValue(); //$NON-NLS-1$
	}
	
	/*
	 * Eigenschaften von Look and Feel
	 */
	public Element getLookAndFeel()
	{
		return Config.doc.getRootElement().getChild("look-and-feel"); //$NON-NLS-1$
	}
	
	public String getLookAndFeelClass()
	{
		return this.getLookAndFeel().getAttributeValue("class"); //$NON-NLS-1$
	}
	
	/*
	 * Eigenschaften der Panels
	 */
	public Element getTabPanel()
	{
		return Config.doc.getRootElement().getChild("tab-panel"); //$NON-NLS-1$
	}
	
	public Element getTabPanelFont()
	{
		return this.getTabPanel().getChild("font"); //$NON-NLS-1$
	}
	
	public Element getTotalBlock()
	{
		return Config.doc.getRootElement().getChild("total-block"); //$NON-NLS-1$
	}
	
	public boolean getTotalBlockHoldValues()
	{
		return new Boolean(this.getTotalBlock().getAttributeValue("hold-values")).booleanValue(); //$NON-NLS-1$
	}
	
	public Element getTotalBlockFont()
	{
		return this.getTotalBlock().getChild("font"); //$NON-NLS-1$
	}
	
	public Element getDetailBlock()
	{
		return Config.doc.getRootElement().getChild("detail-block"); //$NON-NLS-1$
	}
	
	public Element getDetailBlockFont()
	{
		return this.getDetailBlock().getChild("font"); //$NON-NLS-1$
	}
	
	public Element getDetailBlockList()
	{
		return Config.doc.getRootElement().getChild("detail-block-list"); //$NON-NLS-1$
	}
	
	public Element getDetailBlockListFont()
	{
		return this.getDetailBlockList().getChild("font"); //$NON-NLS-1$
	}
	
	public Element getDisplay()
	{
		return Config.doc.getRootElement().getChild("display"); //$NON-NLS-1$
	}
	
	public Element getDisplayFont()
	{
		return this.getDisplay().getChild("font"); //$NON-NLS-1$
	}
	
	/*
	 * Element Periphery
	 */
	public Element getPeriphery()
	{
		return Config.doc.getRootElement().getChild("periphery"); //$NON-NLS-1$
	}
	
	/*
	 * Eigenschaften von PosPrinter
	 */
	public Element getPosPrinter()
	{
		return this.getPeriphery().getChild("pos-printer"); //$NON-NLS-1$
	}
	
	// public Element getPosPrinter(String id) {
	// for (Iterator iterator = getPosPrinters().iterator();
	// iterator.hasNext();) {
	// Element printer = (Element) iterator.next();
	//			if (printer.getAttributeValue("id").equals(id)) { //$NON-NLS-1$
	// return printer;
	// }
	// }
	// return null;
	// }
	public String getPosPrinterId(Element posPrinter)
	{
		return posPrinter.getAttributeValue("id"); //$NON-NLS-1$
	}
	
	public boolean getPosPrinterUse(Element posPrinter)
	{
		return new Boolean(posPrinter.getAttributeValue("use")).booleanValue(); //$NON-NLS-1$
	}
	
	public String getPosPrinterName(Element posPrinter)
	{
		return posPrinter.getAttributeValue("name"); //$NON-NLS-1$
	}
	
	public String getPosPrinterClass(Element posPrinter)
	{
		return posPrinter.getAttributeValue("class"); //$NON-NLS-1$
	}
	
	public boolean getPosPrinterLogoprint(Element posPrinter)
	{
		return new Boolean(posPrinter.getAttributeValue("logoprint")).booleanValue(); //$NON-NLS-1$
	}
	
	public int getPosPrinterLogoprintMode(Element posPrinter)
	{
		return Integer.valueOf(posPrinter.getAttributeValue("logoprintmode")).intValue(); //$NON-NLS-1$
	}
	
	public int getPosPrinterEndlineFeed(Element posPrinter)
	{
		return Integer.valueOf(posPrinter.getAttributeValue("endlinefeed")).intValue(); //$NON-NLS-1$
	}
	
	public int getPosPrinterColumns(Element posPrinter)
	{
		return Integer.valueOf(posPrinter.getAttributeValue("columns")).intValue(); //$NON-NLS-1$
	}
	
	public Element getPosPrinterDevice(Element posPrinter)
	{
		return posPrinter.getChild("device"); //$NON-NLS-1$
	}
	
	public List getPosPrinterCashDrawers()
	{
		return this.getPosPrinter().getChildren("cashdrawer"); //$NON-NLS-1$
	}
	
	public Element getCashDrawer(List list, String id)
	{
		for (Iterator i = list.iterator(); i.hasNext();)
		{
			Element drawer = (Element) i.next();
			if (drawer.getAttributeValue("id").equals(id)) { //$NON-NLS-1$
				return drawer;
			}
		}
		return null;
	}
	
	/*
	 * Eigenschaften von CustomerDisplay
	 */
	public Element getCustomerDisplayTextElement(String id)
	{
		Element[] elements = (Element[]) this.getCustomerDisplay().getChildren("text").toArray(new Element[0]); //$NON-NLS-1$
		for (int i = 0; i < elements.length; i++)
		{
			if (elements[i].getAttributeValue("id").equals(id)) { //$NON-NLS-1$
				return elements[i];
			}
		}
		return null; //$NON-NLS-1$
	}
	
	public Element getCustomerDisplay()
	{
		return this.getPeriphery().getChild("customer-display"); //$NON-NLS-1$
	}
	
	public Element getCustomerDisplayDevice(Element customerDisplay)
	{
		return customerDisplay.getChild("device"); //$NON-NLS-1$
	}
	
	public String getCustomerDisplayId(Element customerDisplay)
	{
		return customerDisplay.getAttributeValue("id"); //$NON-NLS-1$
	}
	
	public boolean getCustomerDisplayUse(Element customerDisplay)
	{
		return new Boolean(customerDisplay.getAttributeValue("use")).booleanValue(); //$NON-NLS-1$
	}
	
	public String getCustomerDisplayName(Element customerDisplay)
	{
		return customerDisplay.getAttributeValue("name"); //$NON-NLS-1$
	}
	
	public String getCustomerDisplayClass(Element customerDisplay)
	{
		return customerDisplay.getAttributeValue("class"); //$NON-NLS-1$
	}
	
	public int getCustomerDisplayEmulation(Element customerDisplay)
	{
		return Integer.valueOf(customerDisplay.getAttributeValue("emulation")).intValue(); //$NON-NLS-1$
	}
	
	public int getCustomerDisplayLineCount(Element customerDisplay)
	{
		return Integer.valueOf(customerDisplay.getAttributeValue("line-count")).intValue(); //$NON-NLS-1$
	}
	
	public int getCustomerDisplayLineLength(Element customerDisplay)
	{
		return Integer.valueOf(customerDisplay.getAttributeValue("line-length")).intValue(); //$NON-NLS-1$
	}
	
	public boolean getCustomerDisplayTimer(Element customerDisplay)
	{
		return new Boolean(customerDisplay.getAttributeValue("timer")).booleanValue(); //$NON-NLS-1$
	}
	
	public int getCustomerDisplaySeconds(Element customerDisplay)
	{
		return Integer.valueOf(customerDisplay.getAttributeValue("seconds")).intValue(); //$NON-NLS-1$
	}
	
	public String getCustomerDisplayGetWelcomeText()
	{
		return this.getCustomerDisplayTextElement("welcome").getText(); //$NON-NLS-1$
	}
	
	public boolean getCustomerDisplayGetWelcomeTextScroll()
	{
		return new Boolean(this.getCustomerDisplayTextElement("welcome").getAttributeValue("scroll")).booleanValue(); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public String getCustomerDisplayGetClosedText()
	{
		return this.getCustomerDisplayTextElement("closed").getText(); //$NON-NLS-1$
	}
	
	public boolean getCustomerDisplayGetClosedTextScroll()
	{
		return new Boolean(this.getCustomerDisplayTextElement("closed").getAttributeValue("scroll")).booleanValue(); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/*
	 * Eigenschaften von Device
	 */
	public String getDevicePort(Element device)
	{
		return device.getAttributeValue("port"); //$NON-NLS-1$
	}
	
	public String getDeviceAlias(Element device)
	{
		return device.getAttributeValue("alias"); //$NON-NLS-1$
	}
	
	public String getDeviceCharset(Element device)
	{
		return device.getAttributeValue("charset"); //$NON-NLS-1$
	}
	
	public int getDeviceCharactertable(Element device)
	{
		return Integer.valueOf(device.getAttributeValue("charactertable")).intValue(); //$NON-NLS-1$
	}
	
	public Element getSerial(Element device)
	{
		return device.getChild("serial"); //$NON-NLS-1$
	}
	
	/*
	 * Eigenschaften von Serial
	 */
	public int getBaudrate(Element serial)
	{
		return new Integer(NumberUtility.parseInt(9600, serial.getAttributeValue("baudrate"))).intValue(); //$NON-NLS-1$
	}
	
	public int getFlowcontrolin(Element serial)
	{
		return new Integer(NumberUtility.parseInt(0, serial.getAttributeValue("flowcontrolin"))).intValue(); //$NON-NLS-1$
	}
	
	public int getFlowcontrolout(Element serial)
	{
		return new Integer(NumberUtility.parseInt(0, serial.getAttributeValue("flowcontrolout"))).intValue(); //$NON-NLS-1$
	}
	
	public int getDatabits(Element serial)
	{
		return new Integer(NumberUtility.parseInt(8, serial.getAttributeValue("databits"))).intValue(); //$NON-NLS-1$
	}
	
	public int getStopbits(Element serial)
	{
		return new Integer(NumberUtility.parseInt(1, serial.getAttributeValue("stopbits"))).intValue(); //$NON-NLS-1$
	}
	
	public int getParity(Element serial)
	{
		return new Integer(NumberUtility.parseInt(0, serial.getAttributeValue("parity"))).intValue(); //$NON-NLS-1$
	}
	
	/*
	 * Eigenschaften von Cashdrawer
	 */
	public String getCashDrawerId(Element cashdrawer)
	{
		return cashdrawer.getAttributeValue("id"); //$NON-NLS-1$
	}
	
	public boolean getCashDrawerUse(Element cashdrawer)
	{
		return new Boolean(cashdrawer.getAttributeValue("use")).booleanValue(); //$NON-NLS-1$
	}
	
	public int getCashDrawerPin(Element cashdrawer)
	{
		return new Integer(NumberUtility.parseInt(0, cashdrawer.getAttributeValue("pin"))).intValue(); //$NON-NLS-1$
	}
	
	public int getCashDrawerPulseon(Element cashdrawer)
	{
		return new Integer(NumberUtility.parseInt(0, cashdrawer.getAttributeValue("pulseon"))).intValue(); //$NON-NLS-1$
	}
	
	public int getCashDrawerPulseoff(Element cashdrawer)
	{
		return new Integer(NumberUtility.parseInt(0, cashdrawer.getAttributeValue("pulseoff"))).intValue(); //$NON-NLS-1$
	}
	
	public String getCashDrawerCurrency(Element cashdrawer)
	{
		return cashdrawer.getAttributeValue("currency"); //$NON-NLS-1$
	}
	
	/*
	 * Eigenschaften der Belege
	 */

	public Element getReceipt()
	{
		return Config.doc.getRootElement().getChild("receipt"); //$NON-NLS-1$
	}
	
	public Element getReceiptHeader()
	{
		return this.getReceipt().getChild("header"); //$NON-NLS-1$
	}
	
	public Element getReceiptPosition()
	{
		return this.getReceipt().getChild("position"); //$NON-NLS-1$
	}
	
	public Element getReceiptFooter()
	{
		return this.getReceipt().getChild("footer"); //$NON-NLS-1$
	}
	
	public Element getReceiptCustomer()
	{
		return this.getReceipt().getChild("customer"); //$NON-NLS-1$
	}
	
	public String getReceiptHeaderText()
	{
		return this.getReceiptHeader().getAttributeValue("text"); //$NON-NLS-1$
	}
	
	public String getReceiptHeaderNumberLength()
	{
		return this.getReceiptHeader().getAttributeValue("number-length"); //$NON-NLS-1$
	}
	
	public String getReceiptHeaderTextAlign()
	{
		Element myRow = this.getRow(this.getReceiptHeader(), "id", "0");
		Element myCol = this.getCol(myRow, "id", "0");
		return myCol == null ? "left" : myCol.getAttributeValue("align");
	}
	
	public String getReceiptFooterText()
	{
		return this.getReceiptFooter().getAttributeValue("text"); //$NON-NLS-1$
	}
	
	public String getReceiptFooterTextAlign()
	{
		Element myRow = this.getRow(this.getReceiptFooter(), "id", "0");
		Element myCol = this.getCol(myRow, "id", "0");
		return myCol == null ? "left" : myCol.getAttributeValue("align");
	}
	
	public String getReceiptFooterPrintUser()
	{
		Element myRow = this.getRow(this.getReceiptFooter(), "id", "1");
		Element myCol = this.getCol(myRow, "id", "0");
		return myCol.getTextTrim().equals("") ? "false" : "true";
	}
	
	public String getReceiptCustomerText()
	{
		return this.getReceiptCustomer().getAttributeValue("text"); //$NON-NLS-1$
	}
	
	public String getReceiptCustomerTextAlign()
	{
		Element myRow = this.getRow(this.getReceiptCustomer(), "id", "0");
		Element myCol = this.getCol(myRow, "id", "0");
		return myCol == null ? "left" : myCol.getAttributeValue("align");
	}
	
	public Element getRow(Element section, String attribute, String value)
	{
		List rows = section.getChildren("row");
		if (rows != null)
		{
			Iterator i = rows.iterator();
			while (i.hasNext())
			{
				Element row = (Element) i.next();
				if (row.getAttributeValue(attribute).equals(value))
				{
					return row;
				}
			}
		}
		return null;
	}
	
	public Element getCol(Element row, String attribute, String value)
	{
		if (row != null)
		{
			List cols = row.getChildren("col");
			Iterator i = cols.iterator();
			while (i.hasNext())
			{
				Element col = (Element) i.next();
				if (col.getAttributeValue(attribute).equals(value))
				{
					return col;
				}
			}
		}
		return null;
	}
	
	public Element getVoucher()
	{
		return Config.doc.getRootElement().getChild("voucher"); //$NON-NLS-1$
	}
	
	public Element getSettlement()
	{
		return Config.doc.getRootElement().getChild("settlement"); //$NON-NLS-1$
	}
	
	// 10182
	public boolean getSettlementAdmitTestSettlement()
	{
		String admitTestSettlement = this.getSettlement().getAttributeValue("admit-test-settlement"); //$NON-NLS-1$
		return new Boolean(admitTestSettlement).booleanValue();
	}
	
	// 10182
	public boolean getSettlementPrintPaymentQuantity()
	{
		String print = this.getSettlement().getAttributeValue("print-payment-quantity"); //$NON-NLS-1$
		return new Boolean(print).booleanValue();
	}
	
	/*****************************************************
	 * 
	 * Setters
	 * 
	 ***************************************************** 
	 */
	public void setLoggingLevel(String value)
	{
		this.getLogging().setAttribute("level", value); //$NON-NLS-1$
	}
	
	public void setLoggingMax(int value)
	{
		this.getLogging().setAttribute("max", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setLoggingTrace(boolean value)
	{
		this.getLogging().setAttribute("trace", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setLoggingReceipts(boolean value)
	{
		this.getLogging().setAttribute("receipts", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setLoggingCompress(boolean value)
	{
		this.getLogging().setAttribute("compress", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setDatabaseDefault(String value)
	{
		this.getDatabase().setAttribute("default", value); //$NON-NLS-1$
	}
	
	public void setDatabaseStandardName(String value)
	{
		this.getDatabaseStandard().setAttribute("name", value); //$NON-NLS-1$
	}
	
	public void setDatabaseStandardActive(boolean value)
	{
		this.getDatabaseStandard().setAttribute("active", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setDatabaseStandardConnection(Element element)
	{
		this.getDatabaseStandardConnection().setAttribute("jcd-alias", element.getAttributeValue("jcd-alias")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseStandardConnection().setAttribute(
						"default-connection", element.getAttributeValue("default-connection")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseStandardConnection().setAttribute("platform", element.getAttributeValue("platform")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseStandardConnection().setAttribute("jdbc-level", element.getAttributeValue("jdbc-level")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseStandardConnection().setAttribute("driver", element.getAttributeValue("driver")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseStandardConnection().setAttribute("protocol", element.getAttributeValue("protocol")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseStandardConnection().setAttribute("subprotocol", element.getAttributeValue("subprotocol")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseStandardConnection().setAttribute("host", element.getAttributeValue("host")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseStandardConnection().setAttribute("port", element.getAttributeValue("port")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseStandardConnection().setAttribute("database", element.getAttributeValue("database")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseStandardConnection().setAttribute("options", element.getAttributeValue("options")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseStandardConnection().setAttribute("username", element.getAttributeValue("username")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseStandardConnection().setAttribute("password", element.getAttributeValue("password")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseStandardConnection().setAttribute("batch-mode", element.getAttributeValue("batch-mode")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseStandardConnection().setAttribute(
						"use-auto-commit", element.getAttributeValue("use-auto-commit")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void setDatabaseTemporaryName(String value)
	{
		this.getDatabaseTemporary().setAttribute("name", value); //$NON-NLS-1$
	}
	
	public void setDatabaseTemporaryActive(boolean value)
	{
		this.getDatabaseTemporary().setAttribute("active", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setDatabaseTemporaryConnection(Element element)
	{
		this.getDatabaseTemporaryConnection().setAttribute("jcd-alias", element.getAttributeValue("jcd-alias")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseTemporaryConnection().setAttribute(
						"default-connection", element.getAttributeValue("default-connection")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseTemporaryConnection().setAttribute("platform", element.getAttributeValue("platform")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseTemporaryConnection().setAttribute("jdbc-level", element.getAttributeValue("jdbc-level")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseTemporaryConnection().setAttribute("driver", element.getAttributeValue("driver")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseTemporaryConnection().setAttribute("protocol", element.getAttributeValue("protocol")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseTemporaryConnection().setAttribute("subprotocol", element.getAttributeValue("subprotocol")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseTemporaryConnection().setAttribute("host", element.getAttributeValue("host")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseTemporaryConnection().setAttribute("port", element.getAttributeValue("port")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseTemporaryConnection().setAttribute("database", element.getAttributeValue("database")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseTemporaryConnection().setAttribute("options", element.getAttributeValue("options")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseTemporaryConnection().setAttribute("username", element.getAttributeValue("username")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseTemporaryConnection().setAttribute("password", element.getAttributeValue("password")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseTemporaryConnection().setAttribute("batch-mode", element.getAttributeValue("batch-mode")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseTemporaryConnection().setAttribute(
						"use-auto-commit", element.getAttributeValue("use-auto-commit")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void setDatabaseTutorialName(String value)
	{
		this.getDatabaseTutorial().setAttribute("name", value); //$NON-NLS-1$
	}
	
	public void setDatabaseTutorialActive(boolean value)
	{
		this.getDatabaseTutorial().setAttribute("active", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setDatabaseTutorialConnection(Element element)
	{
		this.getDatabaseTutorialConnection().setAttribute("jcd-alias", element.getAttributeValue("jcd-alias")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseTutorialConnection().setAttribute(
						"default-connection", element.getAttributeValue("default-connection")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseTutorialConnection().setAttribute("platform", element.getAttributeValue("platform")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseTutorialConnection().setAttribute("jdbc-level", element.getAttributeValue("jdbc-level")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseTutorialConnection().setAttribute("driver", element.getAttributeValue("driver")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseTutorialConnection().setAttribute("protocol", element.getAttributeValue("protocol")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseTutorialConnection().setAttribute("subprotocol", element.getAttributeValue("subprotocol")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseTutorialConnection().setAttribute("host", element.getAttributeValue("host")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseTutorialConnection().setAttribute("port", element.getAttributeValue("port")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseTutorialConnection().setAttribute("database", element.getAttributeValue("database")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseTutorialConnection().setAttribute("options", element.getAttributeValue("options")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseTutorialConnection().setAttribute("username", element.getAttributeValue("username")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseTutorialConnection().setAttribute("password", element.getAttributeValue("password")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseTutorialConnection().setAttribute("batch-mode", element.getAttributeValue("batch-mode")); //$NON-NLS-1$ //$NON-NLS-2$
		this.getDatabaseTutorialConnection().setAttribute(
						"use-auto-commit", element.getAttributeValue("use-auto-commit")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void setSalespointId(int value)
	{
		this.getSalespoint().setAttribute("id", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setSalespointExport(boolean value)
	{
		this.getSalespoint().setAttribute("export", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setSalespointExportPath(String value)
	{
		this.getSalespoint().setAttribute("path", value); //$NON-NLS-1$
	}
	
	public void setInputDefaultQuantity(int value)
	{
		this.getInputDefault().setAttribute("quantity", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setInputDefaultTax(long value)
	{
		this.getInputDefault().setAttribute("tax", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setInputDefaultOption(String value)
	{
		this.getInputDefault().setAttribute("option", value); //$NON-NLS-1$
	}
	
	public void setInputDefaultMaxQuantityRange(int value)
	{
		this.getInputDefault().setAttribute("max-quantity-range", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setInputDefaultMaxQuantityAmount(int value)
	{
		this.getInputDefault().setAttribute("max-quantity-amount", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setInputDefaultMaxPriceRange(double value)
	{
		this.getInputDefault().setAttribute("max-price-range", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setInputDefaultMaxPriceAmount(double value)
	{
		this.getInputDefault().setAttribute("max-price-amount", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setInputDefaultMaxPaymentRange(double value)
	{
		this.getInputDefault().setAttribute("max-payment-range", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setInputDefaultMaxPaymentAmount(double value)
	{
		this.getInputDefault().setAttribute("max-payment-amount", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setInputDefaultClearPrice(boolean value)
	{
		this.getInputDefault().setAttribute("clear-price", String.valueOf(value)); //$NON-NLS-1$
	}
	
	/*
	 * Elemente und Eigenschaften von ComServer
	 */
	public void setProductServerUse(boolean use)
	{
		Setting.getInstance().getComServer().setUse(use);
		// this.getProductServer().setAttribute("use", new
		// Boolean(use).toString());
	}
	
	public void setProductServerHold(boolean hold)
	{
		Setting.getInstance().getComServer().setHold(hold);
		// this.getProductServer().setAttribute("hold", new
		// Boolean(hold).toString());
	}
	
	public void setProductServerClass(String clazz)
	{
		Setting.getInstance().getComServer().setClassname(clazz);
		// this.getProductServer().setAttribute("class", clazz);
	}
	
	/*
	 * Eigenschaften und Element von Galileo
	 */
	public void setGalileoUpdate(int update)
	{
		Setting.getInstance().getComServer().setUpdate(update);
		//		this.getGalileo().setAttribute("update", new Integer(update).toString()); //$NON-NLS-1$
	}
	
	public void setGalileoPath(String path)
	{
		Setting.getInstance().getComServer().setPath(path);
		// this.getGalileo().setAttribute("path", path);
	}
	
	public void setGalileoShowAddCustomerMessage(boolean show)
	{
		Setting.getInstance().getComServer().setShowAddCustomerMessage(show);
		//		this.getGalileo().setAttribute("show-add-customer-message", new Boolean(show).toString()); //$NON-NLS-1$
	}
	
	public void setGalileoSearchCd(boolean search)
	{
		Setting.getInstance().getComServer().setSearchCd(search);
		//		this.getGalileo().setAttribute("search-cd", new Boolean(search).toString()); //$NON-NLS-1$
	}
	
	public void setGalileoCdPath(String path)
	{
		Setting.getInstance().getComServer().setCdPath(path);
		//		this.getGalileo().setAttribute("cd-path", path); //$NON-NLS-1$
	}
	
	public void setLayoutLeftWidth(int value)
	{
		this.getLayout().setAttribute("left", String.valueOf(value));
	}
	
	public void setLayoutTotalBlock(String value)
	{
		this.getLayout().setAttribute("total-block", value); //$NON-NLS-1$
	}
	
	public void setLayoutTopLeft(String value)
	{
		this.getLayout().setAttribute("top-left", value); //$NON-NLS-1$
	}
	
	public void setLayoutTopRight(String value)
	{
		this.getLayout().setAttribute("top-right", value); //$NON-NLS-1$
	}
	
	public void setLayoutBottomLeft(String value)
	{
		this.getLayout().setAttribute("bottom-left", value); //$NON-NLS-1$
	}
	
	public void setLayoutBottomRight(String value)
	{
		this.getLayout().setAttribute("bottom-right", value); //$NON-NLS-1$
	}
	
	public void setLocaleLanguage(String value)
	{
		this.getLocale().setAttribute("language", value); //$NON-NLS-1$
	}
	
	public void setLocaleCountry(String value)
	{
		this.getLocale().setAttribute("country", value); //$NON-NLS-1$
	}
	
	public void setCurrencyDefault(String value)
	{
		this.getCurrency().setAttribute("default", value); //$NON-NLS-1$
	}
	
	public void setCurrencyRoundfactorAmount(double value)
	{
		this.getCurrencyRoundfactor().setAttribute("amount", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setCurrencyRoundfactorTax(double value)
	{
		this.getCurrencyRoundfactor().setAttribute("tax", String.valueOf(value)); //$NON-NLS-1$
	}
	
	/*
	 * Eigenschaften von Look and Feel
	 */
	public void setLookAndFeelClass(String value)
	{
		this.getLookAndFeel().setAttribute("class", value); //$NON-NLS-1$
	}
	
	/*
	 * Eigenschaften von Peripherie
	 * 
	 * Eigenschaften PosPrinter
	 */
	public void setPosPrinterUse(Element printer, boolean value)
	{
		printer.setAttribute("use", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setPosPrinterName(Element printer, String value)
	{
		printer.setAttribute("name", value); //$NON-NLS-1$
	}
	
	public void setPosPrinterClass(Element printer, String value)
	{
		printer.setAttribute("class", value); //$NON-NLS-1$
	}
	
	public void setPosPrinterLogoprint(Element printer, boolean value)
	{
		printer.setAttribute("logoprint", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setPosPrinterLogoprintmode(Element printer, int value)
	{
		printer.setAttribute("logoprintmode", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setPosPrinterEndlineFeed(Element printer, int value)
	{
		printer.setAttribute("endlinefeed", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setPosPrinterColumns(Element printer, int value)
	{
		printer.setAttribute("columns", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setDevicePort(Element device, String value)
	{
		device.setAttribute("port", value); //$NON-NLS-1$
	}
	
	public void setDeviceAlias(Element device, String value)
	{
		device.setAttribute("alias", value); //$NON-NLS-1$
	}
	
	public void setDeviceCharset(Element device, String value)
	{
		device.setAttribute("charset", value); //$NON-NLS-1$
	}
	
	public void setDeviceCharactertable(Element device, int value)
	{
		device.setAttribute("charactertable", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setSerialBaudrate(Element serial, int value)
	{
		serial.setAttribute("baudrate", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setSerialFlowcontrolin(Element serial, int value)
	{
		serial.setAttribute("flowcontrolin", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setSerialFlowcontrolout(Element serial, int value)
	{
		serial.setAttribute("flowcontrolout", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setSerialDatabits(Element serial, int value)
	{
		serial.setAttribute("databits", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setSerialStopbits(Element serial, int value)
	{
		serial.setAttribute("stopbits", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setSerialParity(Element serial, int value)
	{
		serial.setAttribute("parity", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setCashDrawerUse(Element cashdrawer, boolean value)
	{
		cashdrawer.setAttribute("use", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setCashDrawerPin(Element cashdrawer, int value)
	{
		cashdrawer.setAttribute("pin", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setCashDrawerPulseon(Element cashdrawer, int value)
	{
		cashdrawer.setAttribute("pulseon", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setCashDrawerPulseoff(Element cashdrawer, int value)
	{
		cashdrawer.setAttribute("pulseoff", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setCashDrawerCurrency(Element cashdrawer, String value)
	{
		cashdrawer.setAttribute("currency", value); //$NON-NLS-1$
	}
	
	public void setCustomerDisplayUse(Element customerDisplay, boolean value)
	{
		customerDisplay.setAttribute("use", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setCustomerDisplayName(Element customerDisplay, String value)
	{
		customerDisplay.setAttribute("name", value); //$NON-NLS-1$
	}
	
	public void setCustomerDisplayClass(Element customerDisplay, String value)
	{
		customerDisplay.setAttribute("class", value); //$NON-NLS-1$
	}
	
	public void setCustomerDisplayEmulation(Element customerDisplay, int value)
	{
		customerDisplay.setAttribute("emulation", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setCustomerDisplayLineCount(Element customerDisplay, int value)
	{
		customerDisplay.setAttribute("line-count", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setCustomerDisplayLineLength(Element customerDisplay, int value)
	{
		customerDisplay.setAttribute("line-length", String.valueOf(value)); //$NON-NLS-1$
	}
	
	public void setCustomerDisplayTextElement(String id, Boolean scroll, String text)
	{
		Element element = this.getCustomerDisplayTextElement(id);
		if (element == null)
		{
			element = new Element("text"); //$NON-NLS-1$
			element.setAttribute("id", id); //$NON-NLS-1$
			element.setAttribute("scroll", scroll.toString()); //$NON-NLS-1$
			element.setText(text);
			this.getCustomerDisplay().addContent(element);
		}
		else
		{
			element.setAttribute("scroll", scroll.toString()); //$NON-NLS-1$
			element.setText(text);
		}
	}
	
	/*
	 * Eigenschaften der Belege
	 */
	public void setReceiptHeaderText(String text)
	{
		this.getReceiptHeader().setAttribute("text", text); //$NON-NLS-1$
	}
	
	public void setReceiptHeaderNumberLength(String text)
	{
		this.getReceiptHeader().setAttribute("number-length", text); //$NON-NLS-1$
	}
	
	public void setReceiptHeaderTextAlign(String value)
	{
		Element myRow = this.getRow(this.getReceiptHeader(), "id", "0");
		Element myCol = this.getCol(myRow, "id", "0");
		myCol.setAttribute("align", value);
	}
	
	public void setReceiptFooterText(String text)
	{
		this.getReceiptFooter().setAttribute("text", text); //$NON-NLS-1$
	}
	
	public void setReceiptFooterTextAlign(String value)
	{
		Element myRow = this.getRow(this.getReceiptFooter(), "id", "0");
		Element myCol = this.getCol(myRow, "id", "0");
		myCol.setAttribute("align", value);
	}
	
	public void setReceiptFooterPrintUser(String value)
	{
		Element myRow = this.getRow(this.getReceiptFooter(), "id", "1");
		Element myCol = this.getCol(myRow, "id", "0");
		myCol.setText(value == "true" ? "user.name" : "");
	}
	
	public void setReceiptFooterPrintUserAlign(String value)
	{
		Element myRow = this.getRow(this.getReceiptFooter(), "id", "1");
		Element myCol = this.getCol(myRow, "id", "0");
		myCol.setAttribute("align", value);
	}
	
	public void setReceiptCustomerText(String text)
	{
		this.getReceiptCustomer().setAttribute("text", text); //$NON-NLS-1$
	}
	
	public void setReceiptCustomerTextAlign(String value)
	{
		Element myRow = this.getRow(this.getReceiptCustomer(), "id", "0");
		Element myCol = this.getCol(myRow, "id", "0");
		myCol.setAttribute("align", value);
	}
	
	public void setReceiptHeaderPrintLogo(boolean print)
	{
		this.getReceiptHeader().setAttribute("printlogo", String.valueOf(print)); //$NON-NLS-1$
	}
	
	public void setReceiptHeaderLogo(int logo)
	{
		this.getReceiptHeader().setAttribute("logo", String.valueOf(logo)); //$NON-NLS-1$
	}
	
	public void setReceiptHeaderLogoMode(int mode)
	{
		this.getReceiptHeader().setAttribute("logomode", String.valueOf(mode)); //$NON-NLS-1$
	}
	
	public void setReceiptPositionPrintSecondLine(boolean print)
	{
		this.getReceiptPosition().setAttribute("print-second-line", String.valueOf(print)); //$NON-NLS-1$
	}
	
	public void setVoucherPrintLogo(boolean print)
	{
		this.getVoucher().setAttribute("printlogo", String.valueOf(print)); //$NON-NLS-1$
	}
	
	public void setVoucherLogo(int logo)
	{
		this.getVoucher().setAttribute("logo", String.valueOf(logo)); //$NON-NLS-1$
	}
	
	public void setVoucherLogoMode(int mode)
	{
		this.getVoucher().setAttribute("logomode", String.valueOf(mode)); //$NON-NLS-1$
	}
	
	// 10182
	public void setSettlementAdmitTestSettlement(boolean admitTestSettlement)
	{
		Config.doc
						.getRootElement()
						.getChild("settlement").setAttribute("admit-test-settlement", String.valueOf(admitTestSettlement)); //$NON-NLS-1$
	}
	
	// 10182
	public void setSettlementPrintPaymentQuantity(boolean print)
	{
		Config.doc.getRootElement()
						.getChild("settlement").setAttribute("print-payment-quantity", String.valueOf(print)); //$NON-NLS-1$
	}
	
	/*
	 * 
	 * 
	 * 
	 */

	public void setTabPanelRGBForeground(RGB rgb)
	{
		Element color = this.getTabPanel().getChild("fgcolor"); //$NON-NLS-1$
		color.setAttribute("red", String.valueOf(rgb.red)); //$NON-NLS-1$
		color.setAttribute("green", String.valueOf(rgb.green)); //$NON-NLS-1$
		color.setAttribute("blue", String.valueOf(rgb.blue)); //$NON-NLS-1$
	}
	
	public void setTabPanelRGBBackground(RGB rgb)
	{
		Element color = this.getTabPanel().getChild("bgcolor"); //$NON-NLS-1$
		color.setAttribute("red", String.valueOf(rgb.red)); //$NON-NLS-1$
		color.setAttribute("green", String.valueOf(rgb.green)); //$NON-NLS-1$
		color.setAttribute("blue", String.valueOf(rgb.blue)); //$NON-NLS-1$
	}
	
	public void setTotalBlockRGBForeground(RGB rgb)
	{
		Element color = this.getTotalBlock().getChild("fgcolor"); //$NON-NLS-1$
		color.setAttribute("red", String.valueOf(rgb.red)); //$NON-NLS-1$
		color.setAttribute("green", String.valueOf(rgb.green)); //$NON-NLS-1$
		color.setAttribute("blue", String.valueOf(rgb.blue)); //$NON-NLS-1$
	}
	
	public void setTotalBlockRGBBackground(RGB rgb)
	{
		Element color = this.getTotalBlock().getChild("bgcolor"); //$NON-NLS-1$
		color.setAttribute("red", String.valueOf(rgb.red)); //$NON-NLS-1$
		color.setAttribute("green", String.valueOf(rgb.green)); //$NON-NLS-1$
		color.setAttribute("blue", String.valueOf(rgb.blue)); //$NON-NLS-1$
	}
	
	public void setDetailBlockRGBForeground(RGB rgb)
	{
		Element color = this.getDetailBlock().getChild("fgcolor"); //$NON-NLS-1$
		color.setAttribute("red", String.valueOf(rgb.red)); //$NON-NLS-1$
		color.setAttribute("green", String.valueOf(rgb.green)); //$NON-NLS-1$
		color.setAttribute("blue", String.valueOf(rgb.blue)); //$NON-NLS-1$
	}
	
	public void setDetailBlockRGBBackground(RGB rgb)
	{
		Element color = this.getDetailBlock().getChild("bgcolor"); //$NON-NLS-1$
		color.setAttribute("red", String.valueOf(rgb.red)); //$NON-NLS-1$
		color.setAttribute("green", String.valueOf(rgb.green)); //$NON-NLS-1$
		color.setAttribute("blue", String.valueOf(rgb.blue)); //$NON-NLS-1$
	}
	
	public void setDetailBlockListNormalRGBForeground(RGB rgb)
	{
		Element color = this.getDetailBlockList().getChild("normal-color").getChild("fgcolor"); //$NON-NLS-1$
		color.setAttribute("red", String.valueOf(rgb.red)); //$NON-NLS-1$
		color.setAttribute("green", String.valueOf(rgb.green)); //$NON-NLS-1$
		color.setAttribute("blue", String.valueOf(rgb.blue)); //$NON-NLS-1$
	}
	
	public void setDetailBlockListBackRGBForeground(RGB rgb)
	{
		Element color = this.getDetailBlockList().getChild("back-color").getChild("fgcolor"); //$NON-NLS-1$
		color.setAttribute("red", String.valueOf(rgb.red)); //$NON-NLS-1$
		color.setAttribute("green", String.valueOf(rgb.green)); //$NON-NLS-1$
		color.setAttribute("blue", String.valueOf(rgb.blue)); //$NON-NLS-1$
	}
	
	public void setDetailBlockListExpenseRGBForeground(RGB rgb)
	{
		Element color = this.getDetailBlockList().getChild("expense-color").getChild("fgcolor"); //$NON-NLS-1$
		color.setAttribute("red", String.valueOf(rgb.red)); //$NON-NLS-1$
		color.setAttribute("green", String.valueOf(rgb.green)); //$NON-NLS-1$
		color.setAttribute("blue", String.valueOf(rgb.blue)); //$NON-NLS-1$
	}
	
	public void setDisplayRGBForeground(RGB rgb)
	{
		Element color = this.getDisplay().getChild("fgcolor"); //$NON-NLS-1$
		color.setAttribute("red", String.valueOf(rgb.red)); //$NON-NLS-1$
		color.setAttribute("green", String.valueOf(rgb.green)); //$NON-NLS-1$
		color.setAttribute("blue", String.valueOf(rgb.blue)); //$NON-NLS-1$
	}
	
	public void setDisplayRGBBackground(RGB rgb)
	{
		Element color = this.getDisplay().getChild("bgcolor"); //$NON-NLS-1$
		color.setAttribute("red", String.valueOf(rgb.red)); //$NON-NLS-1$
		color.setAttribute("green", String.valueOf(rgb.green)); //$NON-NLS-1$
		color.setAttribute("blue", String.valueOf(rgb.blue)); //$NON-NLS-1$
	}
	
	public Color getTabPanelColorForeground()
	{
		Element color = this.getTabPanel().getChild("fgcolor"); //$NON-NLS-1$
		int red = Integer.valueOf(color.getAttributeValue("red")).intValue(); //$NON-NLS-1$
		int green = Integer.valueOf(color.getAttributeValue("green")).intValue(); //$NON-NLS-1$
		int blue = Integer.valueOf(color.getAttributeValue("blue")).intValue(); //$NON-NLS-1$
		return new Color(red, green, blue);
	}
	
	public Color getTabPanelColorBackground()
	{
		Element color = this.getTabPanel().getChild("bgcolor"); //$NON-NLS-1$
		int red = Integer.valueOf(color.getAttributeValue("red")).intValue(); //$NON-NLS-1$
		int green = Integer.valueOf(color.getAttributeValue("green")).intValue(); //$NON-NLS-1$
		int blue = Integer.valueOf(color.getAttributeValue("blue")).intValue(); //$NON-NLS-1$
		return new Color(red, green, blue);
	}
	
	public Color getTotalBlockColorForeground()
	{
		Element color = this.getTotalBlock().getChild("fgcolor"); //$NON-NLS-1$
		int red = Integer.valueOf(color.getAttributeValue("red")).intValue(); //$NON-NLS-1$
		int green = Integer.valueOf(color.getAttributeValue("green")).intValue(); //$NON-NLS-1$
		int blue = Integer.valueOf(color.getAttributeValue("blue")).intValue(); //$NON-NLS-1$
		return new Color(red, green, blue);
	}
	
	public Color getTotalBlockColorBackground()
	{
		Element color = this.getTotalBlock().getChild("bgcolor"); //$NON-NLS-1$
		int red = Integer.valueOf(color.getAttributeValue("red")).intValue(); //$NON-NLS-1$
		int green = Integer.valueOf(color.getAttributeValue("green")).intValue(); //$NON-NLS-1$
		int blue = Integer.valueOf(color.getAttributeValue("blue")).intValue(); //$NON-NLS-1$
		return new Color(red, green, blue);
	}
	
	public Color getDetailBlockListNormalColorForeground()
	{
		Element color = this.getDetailBlockList().getChild("normal-color").getChild("fgcolor"); //$NON-NLS-1$
		int red = Integer.valueOf(color.getAttributeValue("red")).intValue(); //$NON-NLS-1$
		int green = Integer.valueOf(color.getAttributeValue("green")).intValue(); //$NON-NLS-1$
		int blue = Integer.valueOf(color.getAttributeValue("blue")).intValue(); //$NON-NLS-1$
		return new Color(red, green, blue);
	}
	
	public Color getDetailBlockListBackColorForeground()
	{
		Element color = this.getDetailBlockList().getChild("back-color").getChild("fgcolor"); //$NON-NLS-1$
		int red = Integer.valueOf(color.getAttributeValue("red")).intValue(); //$NON-NLS-1$
		int green = Integer.valueOf(color.getAttributeValue("green")).intValue(); //$NON-NLS-1$
		int blue = Integer.valueOf(color.getAttributeValue("blue")).intValue(); //$NON-NLS-1$
		return new Color(red, green, blue);
	}
	
	public Color getDetailBlockListExpenseColorForeground()
	{
		Element color = this.getDetailBlockList().getChild("expense-color").getChild("fgcolor"); //$NON-NLS-1$
		int red = Integer.valueOf(color.getAttributeValue("red")).intValue(); //$NON-NLS-1$
		int green = Integer.valueOf(color.getAttributeValue("green")).intValue(); //$NON-NLS-1$
		int blue = Integer.valueOf(color.getAttributeValue("blue")).intValue(); //$NON-NLS-1$
		return new Color(red, green, blue);
	}
	
	public Color getDetailBlockColorForeground()
	{
		Element color = this.getDetailBlock().getChild("fgcolor"); //$NON-NLS-1$
		int red = Integer.valueOf(color.getAttributeValue("red")).intValue(); //$NON-NLS-1$
		int green = Integer.valueOf(color.getAttributeValue("green")).intValue(); //$NON-NLS-1$
		int blue = Integer.valueOf(color.getAttributeValue("blue")).intValue(); //$NON-NLS-1$
		return new Color(red, green, blue);
	}
	
	public Color getDisplayColorForeground()
	{
		Element color = this.getDisplay().getChild("fgcolor"); //$NON-NLS-1$
		int red = Integer.valueOf(color.getAttributeValue("red")).intValue(); //$NON-NLS-1$
		int green = Integer.valueOf(color.getAttributeValue("green")).intValue(); //$NON-NLS-1$
		int blue = Integer.valueOf(color.getAttributeValue("blue")).intValue(); //$NON-NLS-1$
		return new Color(red, green, blue);
	}
	
	public Color getDisplayColorBackground()
	{
		Element color = this.getDisplay().getChild("bgcolor"); //$NON-NLS-1$
		int red = Integer.valueOf(color.getAttributeValue("red")).intValue(); //$NON-NLS-1$
		int green = Integer.valueOf(color.getAttributeValue("green")).intValue(); //$NON-NLS-1$
		int blue = Integer.valueOf(color.getAttributeValue("blue")).intValue(); //$NON-NLS-1$
		return new Color(red, green, blue);
	}
	
	public RGB getTabPanelRGBForeground()
	{
		Element rgb = this.getTabPanel().getChild("fgcolor"); //$NON-NLS-1$
		int red = Integer.valueOf(rgb.getAttributeValue("red")).intValue(); //$NON-NLS-1$
		int green = Integer.valueOf(rgb.getAttributeValue("green")).intValue(); //$NON-NLS-1$
		int blue = Integer.valueOf(rgb.getAttributeValue("blue")).intValue(); //$NON-NLS-1$
		return new RGB(red, green, blue);
	}
	
	public RGB getTabPanelRGBBackground()
	{
		Element rgb = this.getTabPanel().getChild("bgcolor"); //$NON-NLS-1$
		int red = Integer.valueOf(rgb.getAttributeValue("red")).intValue(); //$NON-NLS-1$
		int green = Integer.valueOf(rgb.getAttributeValue("green")).intValue(); //$NON-NLS-1$
		int blue = Integer.valueOf(rgb.getAttributeValue("blue")).intValue(); //$NON-NLS-1$
		return new RGB(red, green, blue);
	}
	
	public RGB getTotalBlockRGBForeground()
	{
		Element rgb = this.getTotalBlock().getChild("fgcolor"); //$NON-NLS-1$
		int red = Integer.valueOf(rgb.getAttributeValue("red")).intValue(); //$NON-NLS-1$
		int green = Integer.valueOf(rgb.getAttributeValue("green")).intValue(); //$NON-NLS-1$
		int blue = Integer.valueOf(rgb.getAttributeValue("blue")).intValue(); //$NON-NLS-1$
		return new RGB(red, green, blue);
	}
	
	public RGB getTotalBlockRGBBackground()
	{
		Element rgb = this.getTotalBlock().getChild("bgcolor"); //$NON-NLS-1$
		int red = Integer.valueOf(rgb.getAttributeValue("red")).intValue(); //$NON-NLS-1$
		int green = Integer.valueOf(rgb.getAttributeValue("green")).intValue(); //$NON-NLS-1$
		int blue = Integer.valueOf(rgb.getAttributeValue("blue")).intValue(); //$NON-NLS-1$
		return new RGB(red, green, blue);
	}
	
	public RGB getDetailBlockRGBForeground()
	{
		Element rgb = this.getDetailBlock().getChild("fgcolor"); //$NON-NLS-1$
		int red = Integer.valueOf(rgb.getAttributeValue("red")).intValue(); //$NON-NLS-1$
		int green = Integer.valueOf(rgb.getAttributeValue("green")).intValue(); //$NON-NLS-1$
		int blue = Integer.valueOf(rgb.getAttributeValue("blue")).intValue(); //$NON-NLS-1$
		return new RGB(red, green, blue);
	}
	
	public RGB getDetailBlockRGBBackground()
	{
		Element rgb = this.getDetailBlock().getChild("bgcolor"); //$NON-NLS-1$
		int red = Integer.valueOf(rgb.getAttributeValue("red")).intValue(); //$NON-NLS-1$
		int green = Integer.valueOf(rgb.getAttributeValue("green")).intValue(); //$NON-NLS-1$
		int blue = Integer.valueOf(rgb.getAttributeValue("blue")).intValue(); //$NON-NLS-1$
		return new RGB(red, green, blue);
	}
	
	public RGB getDetailBlockListNormalRGBForeground()
	{
		Element rgb = this.getDetailBlockList().getChild("normal-color").getChild("fgcolor"); //$NON-NLS-1$
		int red = Integer.valueOf(rgb.getAttributeValue("red")).intValue(); //$NON-NLS-1$
		int green = Integer.valueOf(rgb.getAttributeValue("green")).intValue(); //$NON-NLS-1$
		int blue = Integer.valueOf(rgb.getAttributeValue("blue")).intValue(); //$NON-NLS-1$
		return new RGB(red, green, blue);
	}
	
	public RGB getDetailBlockListBackRGBForeground()
	{
		Element rgb = this.getDetailBlockList().getChild("back-color").getChild("fgcolor"); //$NON-NLS-1$
		int red = Integer.valueOf(rgb.getAttributeValue("red")).intValue(); //$NON-NLS-1$
		int green = Integer.valueOf(rgb.getAttributeValue("green")).intValue(); //$NON-NLS-1$
		int blue = Integer.valueOf(rgb.getAttributeValue("blue")).intValue(); //$NON-NLS-1$
		return new RGB(red, green, blue);
	}
	
	public RGB getDetailBlockListExpenseRGBForeground()
	{
		Element rgb = this.getDetailBlockList().getChild("expense-color").getChild("fgcolor"); //$NON-NLS-1$
		int red = Integer.valueOf(rgb.getAttributeValue("red")).intValue(); //$NON-NLS-1$
		int green = Integer.valueOf(rgb.getAttributeValue("green")).intValue(); //$NON-NLS-1$
		int blue = Integer.valueOf(rgb.getAttributeValue("blue")).intValue(); //$NON-NLS-1$
		return new RGB(red, green, blue);
	}
	
	public RGB getDisplayRGBForeground()
	{
		Element rgb = this.getDisplay().getChild("fgcolor"); //$NON-NLS-1$
		int red = Integer.valueOf(rgb.getAttributeValue("red")).intValue(); //$NON-NLS-1$
		int green = Integer.valueOf(rgb.getAttributeValue("green")).intValue(); //$NON-NLS-1$
		int blue = Integer.valueOf(rgb.getAttributeValue("blue")).intValue(); //$NON-NLS-1$
		return new RGB(red, green, blue);
	}
	
	public RGB getDisplayRGBBackground()
	{
		Element rgb = this.getDisplay().getChild("bgcolor"); //$NON-NLS-1$
		int red = Integer.valueOf(rgb.getAttributeValue("red")).intValue(); //$NON-NLS-1$
		int green = Integer.valueOf(rgb.getAttributeValue("green")).intValue(); //$NON-NLS-1$
		int blue = Integer.valueOf(rgb.getAttributeValue("blue")).intValue(); //$NON-NLS-1$
		return new RGB(red, green, blue);
	}
	
	public void setDatabaseConfiguration(String key, String value)
	{
		Element e = Config.doc.getRootElement();
		Element source = e.getChild(key);
		source.setAttribute("name", value); //$NON-NLS-1$
	}
	
	public Element getSalespointData()
	{
		return Config.doc.getRootElement().getChild("salespoint"); //$NON-NLS-1$
	}
	
	public String getDefaultOption()
	{
		Element root = Config.doc.getRootElement();
		return root.getChild("input-default").getAttributeValue("option"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public Tax getDefaultTax()
	{
		Element root = Config.doc.getRootElement();
		long id = NumberUtility.parseLong(2, root.getChild("input-default").getAttributeValue("tax")); //$NON-NLS-1$ //$NON-NLS-2$
		return Tax.getById(new Long(id));
	}
	
	public int getDefaultQuantity()
	{
		Element root = Config.doc.getRootElement();
		String s = root.getChild("input-default").getAttributeValue("quantity"); //$NON-NLS-1$ //$NON-NLS-2$
		return Integer.parseInt(s);
	}
	
	/*
	 * Eigenschaften Button
	 */
	public Element getButtonFont(Element button)
	{
		return button.getChild("font"); //$NON-NLS-1$
	}
	
	public Element getButtonFgcolor(Element button)
	{
		return button.getChild("fgcolor"); //$NON-NLS-1$
	}
	
	public Element getButtonBgcolor(Element button)
	{
		return button.getChild("bgcolor"); //$NON-NLS-1$
	}
	
	public String getFontName(Element font)
	{
		return font.getAttributeValue("name"); //$NON-NLS-1$
	}
	
	public float getFontSize(Element font)
	{
		return new Double(font.getAttributeValue("size")).floatValue(); //$NON-NLS-1$
	}
	
	public int getFontStyle(Element font)
	{
		return new Integer(font.getAttributeValue("style")).intValue(); //$NON-NLS-1$
	}
	
	/*
	 * Eigenschaften layout
	 */
	public String getPosition(String block)
	{
		Element root = Config.doc.getRootElement();
		Element position = root.getChild("layout"); //$NON-NLS-1$
		return position.getAttributeValue(block);
	}
	
	/*
	 * Eigenschaften locale
	 */
	public Locale getDefaultLocale()
	{
		if (Config.locale == null)
		{
			Config.locale = new Locale(this.getLocaleLanguage(), this.getLocaleCountry());
		}
		return Config.locale;
	}
	
	/*
	 * Eigenschaften Currency
	 */
	public double getRoundFactorAmount()
	{
		Element e = Config.doc.getRootElement().getChild("currency").getChild("roundfactor"); //$NON-NLS-1$ //$NON-NLS-2$
		return this.getRoundFactor(e.getAttributeValue("amount")); //$NON-NLS-1$
	}
	
	public double getRoundFactorTax()
	{
		Element e = Config.doc.getRootElement().getChild("currency").getChild("roundfactor"); //$NON-NLS-1$ //$NON-NLS-2$
		return this.getRoundFactor(e.getAttributeValue("tax")); //$NON-NLS-1$
	}
	
	public double getRoundFactor(String factor)
	{
		double d = 0.01D;
		try
		{
			d = Double.parseDouble(factor);
		}
		catch (NumberFormatException e)
		{
		}
		return d;
	}
	
	public void close()
	{
		Config.doc = null;
	}
	
	public Document getDocument()
	{
		return Config.doc;
	}
	
	public void dispose()
	{
		Config.doc = null;
	}
	
	protected static File file;
	
	protected static Config config = null;
	protected static Document doc = null;
	protected static Locale locale = null;
	protected static ForeignCurrency defaultForeignCurrency = null;
}
