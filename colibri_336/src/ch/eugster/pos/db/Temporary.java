/*
 * Created on 16.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.XMLOutputter;

import ch.eugster.pos.Messages;
import ch.eugster.pos.events.InitializationListener;
import ch.eugster.pos.events.ShutdownListener;
import ch.eugster.pos.util.Config;
import ch.eugster.pos.util.Path;
import ch.eugster.pos.util.XMLLoader;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Temporary extends Connection implements ShutdownListener
{
	
	/**
	 * 
	 */
	public Temporary()
	{
		super();
	}
	
	/**
	 * @param Element
	 */
	public Temporary(Element element)
	{
		super(element);
	}
	
	/**
	 * @param priority
	 * @param active
	 * @param name
	 */
	public Temporary(String id, String name)
	{
		super(id, name);
	}
	
	public void initialize(InitializationListener l, int value)
	{
		this.addInitializationListener(l);
		
		if (Database.getCurrent().equals(Database.getStandard()))
		{
			//			LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.Die_Stammdaten_werden_aus_der_Datenbank_geladen..._2")); //$NON-NLS-1$ //$NON-NLS-2$
			this.loadDataFromDB(value); // 22 Schritte
		}
		else
		{
			//			LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.Die_lokal_gespeicherten_Stammdaten_werden_geladen..._4")); //$NON-NLS-1$ //$NON-NLS-2$
			this.loadDataFromXML(value); // 21 Schritte
		}
		//		LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.Stammdaten_erfolgreich_geladen._6")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.removeInitializationListener(l);
	}
	
	protected Element getConnectionData()
	{
		return Config.getInstance().getDatabaseTemporaryConnection();
	}
	
	private void dispose()
	{
		this.writeDataToXML();
	}
	
	public void shutdownPerformed()
	{
		this.dispose();
	}
	
	private void loadDataFromXML(int value)
	{
		this.loadDataFromXML(this.file, value);
	}
	
	private void loadDataFromDB(int value)
	{
		this.data = new Document();
		DocType docType = new DocType(Messages.getString("Temporary.data"), "data.dtd"); //$NON-NLS-1$ //$NON-NLS-2$
		this.data.setDocType(docType);
		Element root = new Element("data"); //$NON-NLS-1$
		this.data.setRootElement(root);
		
		this.fireInitialized(++value, "Einstellungen laden..."); //$NON-NLS-1$
		Setting.readDBRecords();
		root = Setting.writeXMLRecords(root);
		//		LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.Kassendaten_geladen..._12")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.fireInitialized(++value, Messages.getString("Temporary.W_u00E4hrungen_laden..._67")); //$NON-NLS-1$
		ForeignCurrency.readDBRecords();
		root = ForeignCurrency.writeXMLRecords(root);
		//		LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.W_u00E4hrungen_geladen..._69")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.fireInitialized(++value, Messages.getString("Temporary.Benutzerdaten_laden..._13")); //$NON-NLS-1$
		User.readDBRecords();
		root = User.writeXMLRecords(root);
		//		LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.Benutzer_geladen..._15")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.fireInitialized(++value, Messages.getString("Temporary.Kassendaten_laden..._10")); //$NON-NLS-1$
		Salespoint.readDBRecords();
		root = Salespoint.writeXMLRecords(root);
		//		LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.Kassendaten_geladen..._12")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.fireInitialized(++value, Messages.getString("Temporary.Optionen_laden..._22")); //$NON-NLS-1$
		Option.readDBRecords();
		root = Option.writeXMLRecords(root);
		//		LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.Optionen_geladen..._24")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.fireInitialized(++value, Messages.getString("Temporary.Mehrwertsteuerarten_laden..._25")); //$NON-NLS-1$
		TaxType.readDBRecords();
		root = TaxType.writeXMLRecords(root);
		//		LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.TaxTypes_geladen..._27")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.fireInitialized(++value, Messages.getString("Temporary.Mehrwertsteuergruppen_laden..._28")); //$NON-NLS-1$
		TaxRate.readDBRecords();
		root = TaxRate.writeXMLRecords(root);
		//		LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.TaxRates_geladen..._30")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.fireInitialized(++value, Messages.getString("Temporary.Mehrwertsteuern_laden..._31")); //$NON-NLS-1$
		Tax.readDBRecords();
		root = Tax.writeXMLRecords(root);
		//		LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.Taxes_geladen..._33")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.fireInitialized(++value, Messages.getString("Temporary.Mehrwertsteuers_u00E4tze_laden..._34")); //$NON-NLS-1$
		CurrentTax.readDBRecords();
		root = CurrentTax.writeXMLRecords(root);
		//		LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.CurrentTaxes_geladen..._36")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.fireInitialized(++value, Messages.getString("Temporary.Warengruppen_laden..._37")); //$NON-NLS-1$
		ProductGroup.readDBRecords();
		root = ProductGroup.writeXMLRecords(root);
		//		LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.Warengruppen_geladen..._39")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.fireInitialized(++value, "Zahlungsartengruppen laden..."); //$NON-NLS-1$
		PaymentTypeGroup.readDBRecords();
		root = PaymentTypeGroup.writeXMLRecords(root);
		//		LogManager.getLogManager().getLogger("colibri").info("Zahlungsartengruppen geladen..."); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.fireInitialized(++value, Messages.getString("Temporary.Zahlungsarten_laden..._40")); //$NON-NLS-1$
		PaymentType.readDBRecords();
		root = PaymentType.writeXMLRecords(root);
		//		LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.Zahlungsarten_geladen..._42")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.fireInitialized(++value, Messages.getString("Temporary.Bl_u00F6cke_laden..._43")); //$NON-NLS-1$
		Block.readDBRecords();
		root = Block.writeXMLRecords(root);
		//		LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.Blocks_geladen..._45")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.fireInitialized(++value, Messages.getString("Temporary.Tabulatoren_laden..._46")); //$NON-NLS-1$
		Tab.readDBRecords();
		root = Tab.writeXMLRecords(root);
		//		LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.Tabs_geladen..._48")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.fireInitialized(++value, Messages.getString("Temporary.Funktionen_laden..._49")); //$NON-NLS-1$
		Function.readDBRecords();
		root = Function.writeXMLRecords(root);
		//		LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.Functions_geladen..._51")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.fireInitialized(++value, Messages.getString("Temporary.Tastenzuordnungsgruppen_laden..._52")); //$NON-NLS-1$
		KeyGroup.readDBRecords();
		root = KeyGroup.writeXMLRecords(root);
		//		LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.KeyGroups_geladen..._54")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.fireInitialized(++value, Messages.getString("Temporary.Tastenzuordnungen_laden..._55")); //$NON-NLS-1$
		CustomKey.readDBRecords();
		root = CustomKey.writeXMLRecords(root);
		//		LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.CustomKeys_geladen..._57")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.fireInitialized(++value, Messages.getString("Temporary.Fixtastengruppen_laden..._58")); //$NON-NLS-1$
		FixKeyGroup.readDBRecords();
		root = FixKeyGroup.writeXMLRecords(root);
		//		LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.FixKeyGroups_geladen..._60")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.fireInitialized(++value, Messages.getString("Temporary.Fixtasten_laden..._61")); //$NON-NLS-1$
		FixKey.readDBRecords();
		root = FixKey.writeXMLRecords(root);
		//		LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.FixKeys_geladen..._63")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.fireInitialized(++value, Messages.getString("Temporary.Hart-/Papiergeld_laden..._64")); //$NON-NLS-1$
		Coin.readDBRecords();
		root = Coin.writeXMLRecords(root);
		//		LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.Coins_geladen..._66")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.fireInitialized(++value, Messages.getString("Temporary.Version_laden..._70")); //$NON-NLS-1$
		Version.readDBRecords();
		root = Version.writeXMLRecords(root);
		//		LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.Version_geladen..._72")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.fireInitialized(++value, "Kassastockdaten laden..."); //$NON-NLS-1$
		Stock.readDBRecords();
		root = Stock.writeXMLRecords(root);
		//		LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.Kassendaten_geladen..._12")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.fireInitialized(++value, Messages.getString("Temporary.Geladene_Daten_in_lokale_Datei_schreiben..._73")); //$NON-NLS-1$
		this.writeDataToXML();
		//		LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.Lokale_Kopie_in___data.xml___abspeichern..._75")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private void loadDataFromXML(File file, int value)
	{
		try
		{
			this.data = XMLLoader.getDocument(file, true);
			
			this.fireInitialized(++value, "Einstellungen laden..."); //$NON-NLS-1$
			Setting.readXML();
			
			this.fireInitialized(++value, Messages.getString("Temporary.W_u00E4hrungen_laden..._131")); //$NON-NLS-1$
			ForeignCurrency.readXML();
			//			LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.W_u00E4hrungen_geladen..._133")); //$NON-NLS-1$ //$NON-NLS-2$
			
			this.fireInitialized(++value, Messages.getString("Temporary.Benutzerdaten_laden..._77")); //$NON-NLS-1$
			User.readXML();
			//			LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.Benutzer_geladen..._79")); //$NON-NLS-1$ //$NON-NLS-2$
			
			this.fireInitialized(++value, Messages.getString("Temporary.Kassendaten_laden..._76")); //$NON-NLS-1$
			Salespoint.readXML();
			
			this.fireInitialized(++value, Messages.getString("Temporary.Optionen_laden..._86")); //$NON-NLS-1$
			Option.readXML();
			//			LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.Optionen_geladen..._88")); //$NON-NLS-1$ //$NON-NLS-2$
			
			this.fireInitialized(++value, Messages.getString("Temporary.Mehrwertsteuerarten_laden..._89")); //$NON-NLS-1$
			TaxType.readXML();
			//			LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.TaxTypes_geladen..._91")); //$NON-NLS-1$ //$NON-NLS-2$
			
			this.fireInitialized(++value, Messages.getString("Temporary.Mehrwertsteuergruppen_laden..._92")); //$NON-NLS-1$
			TaxRate.readXML();
			//			LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.TaxRates_geladen..._94")); //$NON-NLS-1$ //$NON-NLS-2$
			
			this.fireInitialized(++value, Messages.getString("Temporary.Mehrwertsteuern_laden..._95")); //$NON-NLS-1$
			Tax.readXML();
			//			LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.Taxes_geladen..._97")); //$NON-NLS-1$ //$NON-NLS-2$
			
			this.fireInitialized(++value, Messages.getString("Temporary.Mehrwertsteuers_u00E4tze_laden..._98")); //$NON-NLS-1$
			CurrentTax.readXML();
			//			LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.CurrentTaxes_geladen..._100")); //$NON-NLS-1$ //$NON-NLS-2$
			
			this.fireInitialized(++value, Messages.getString("Temporary.Warengruppen_laden..._101")); //$NON-NLS-1$
			ProductGroup.readXML();
			//			LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.Warengruppen_geladen..._103")); //$NON-NLS-1$ //$NON-NLS-2$
			
			this.fireInitialized(++value, Messages.getString("Temporary.Zahlungsartengruppen_laden..._1")); //$NON-NLS-1$
			PaymentTypeGroup.readXML();
			//			LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.Zahlungsartengruppen_geladen..._3")); //$NON-NLS-1$ //$NON-NLS-2$
			
			this.fireInitialized(++value, Messages.getString("Temporary.Zahlungsarten_laden..._104")); //$NON-NLS-1$
			PaymentType.readXML();
			//			LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.Zahlungsarten_geladen..._106")); //$NON-NLS-1$ //$NON-NLS-2$
			
			this.fireInitialized(++value, Messages.getString("Temporary.Bl_u00F6cke_laden..._107")); //$NON-NLS-1$
			Block.readXML();
			//			LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.Blocks_geladen..._109")); //$NON-NLS-1$ //$NON-NLS-2$
			
			this.fireInitialized(++value, Messages.getString("Temporary.Tabulatoren_laden..._110")); //$NON-NLS-1$
			Tab.readXML();
			//			LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.Tabs_geladen..._112")); //$NON-NLS-1$ //$NON-NLS-2$
			
			this.fireInitialized(++value, Messages.getString("Temporary.Funktionen_laden..._113")); //$NON-NLS-1$
			Function.readXML();
			//			LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.Functions_geladen..._115")); //$NON-NLS-1$ //$NON-NLS-2$
			
			this.fireInitialized(++value, Messages.getString("Temporary.Tastenzuordnungsgruppen_laden..._116")); //$NON-NLS-1$
			KeyGroup.readXML();
			//			LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.KeyGroups_geladen..._118")); //$NON-NLS-1$ //$NON-NLS-2$
			
			this.fireInitialized(++value, Messages.getString("Temporary.Tastenzuordnungen_laden..._119")); //$NON-NLS-1$
			CustomKey.readXML();
			//			LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.CustomKeys_geladen..._121")); //$NON-NLS-1$ //$NON-NLS-2$
			
			this.fireInitialized(++value, Messages.getString("Temporary.Fixtastenzuordnungsgruppen_laden..._122")); //$NON-NLS-1$
			FixKeyGroup.readXML();
			//			LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.FixKeyGroups_geladen..._124")); //$NON-NLS-1$ //$NON-NLS-2$
			
			this.fireInitialized(++value, Messages.getString("Temporary.Fixtastenzuordnungen_laden..._125")); //$NON-NLS-1$
			FixKey.readXML();
			//			LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.FixKeys_geladen..._127")); //$NON-NLS-1$ //$NON-NLS-2$
			
			this.fireInitialized(++value, Messages.getString("Temporary.Hart-/Papiergeld_laden..._128")); //$NON-NLS-1$
			Coin.readXML();
			//			LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.Coins_geladen..._130")); //$NON-NLS-1$ //$NON-NLS-2$
			
			this.fireInitialized(++value, Messages.getString("Temporary.Version_laden..._134")); //$NON-NLS-1$
			Version.readXML();
			//			LogManager.getLogManager().getLogger("colibri").info(Messages.getString("Temporary.Version_geladen..._136")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		catch (JDOMException e)
		{
			//			LogManager.getLogManager().getLogger("colibri").info("JDOMException: " + e.getLocalizedMessage()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		catch (IOException e)
		{
			//			LogManager.getLogManager().getLogger("colibri").info("IOException: " + e.getLocalizedMessage()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	private void writeDataToXML()
	{
		OutputStream out = null;
		try
		{
			out = new FileOutputStream(this.file);
			XMLOutputter xmlOut = new XMLOutputter();
			xmlOut.setEncoding("ISO-8859-1"); //$NON-NLS-1$
			xmlOut.setNewlines(true);
			xmlOut.setIndent("  "); //$NON-NLS-1$
			xmlOut.output(this.data, out);
			out.close();
		}
		catch (FileNotFoundException e)
		{
			//			LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
		}
		catch (IOException e)
		{
			//			LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
		}
	}
	
	public Element getRoot()
	{
		if (this.data == null)
		{
			this.data = new Document();
		}
		
		Element root = this.data.getRootElement();
		if (root == null)
		{
			root = new Element("data"); //$NON-NLS-1$
			this.data.setRootElement(root);
		}
		return root;
	}
	
	public Document getDocument()
	{
		return this.data;
	}
	
	public Element getTable(String table)
	{
		List tables = this.data.getRootElement().getChildren("table"); //$NON-NLS-1$
		Iterator iter = tables.iterator();
		while (iter.hasNext())
		{
			Element element = (Element) iter.next();
			if (element.getAttributeValue("name").equals(table)) { //$NON-NLS-1$
				return element;
			}
		}
		return null;
	}
	
	public Element[] getTables()
	{
		return (Element[]) this.data.getRootElement().getChildren("table").toArray(new Element[0]); //$NON-NLS-1$
	}
	
	public Element[] getRecords(String table)
	{
		return (Element[]) this.getTable(table).getChildren("record").toArray(new Element[0]); //$NON-NLS-1$
	}
	
	public Table getRecord(String table, Long pk)
	{
		if (table.equals(Salespoint.class.getName()))
		{
			return Salespoint.getById(pk);
		}
		else if (table.equals(User.class.getName()))
		{
			return User.getById(pk);
		}
		// else if (table.equals(Access.class.getName())) {
		// return Access.getById(pk);
		// }
		// else if (table.equals(UserAccess.class.getName())) {
		// return UserAccess.getById(pk);
		// }
		else if (table.equals(Option.class.getName()))
		{
			return Option.getById(pk);
		}
		else if (table.equals(TaxType.class.getName()))
		{
			return TaxType.getById(pk);
		}
		else if (table.equals(TaxRate.class.getName()))
		{
			return TaxRate.getById(pk);
		}
		else if (table.equals(Tax.class.getName()))
		{
			return Tax.getById(pk);
		}
		else if (table.equals(CurrentTax.class.getName()))
		{
			return CurrentTax.getById(pk);
		}
		else if (table.equals(ProductGroup.class.getName()))
		{
			return ProductGroup.getById(pk);
		}
		else if (table.equals(PaymentType.class.getName()))
		{
			return PaymentType.getById(pk);
		}
		else if (table.equals(Block.class.getName()))
		{
			return Block.getById(pk);
		}
		else if (table.equals(Tab.class.getName()))
		{
			return Tab.getById(pk);
		}
		else if (table.equals(Function.class.getName()))
		{
			return Function.getById(pk);
		}
		else if (table.equals(KeyGroup.class.getName()))
		{
			return KeyGroup.getById(pk);
		}
		else if (table.equals(CustomKey.class.getName()))
		{
			return CustomKey.getById(pk);
		}
		else if (table.equals(FixKeyGroup.class.getName()))
		{
			return FixKeyGroup.getById(pk);
		}
		// else if (table.equals(Coin.class.getName())) {
		// return Coin.getById(pk);
		// }
		else if (table.equals(ForeignCurrency.class.getName()))
		{
			return ForeignCurrency.getById(pk);
		}
		// else if (table.equals(Version.class.getName())) {
		// return Version.getById(pk);
		// }
		else
		{
			return null;
		}
	}
	
	public String getCode()
	{
		return "tmp";
	}
	
	private Document data;
	private File file = new File(Path.getInstance().cfgDir.concat(Messages.getString("Temporary.__data.xml_144"))); //$NON-NLS-1$
	
	// private int value = 0;
}
