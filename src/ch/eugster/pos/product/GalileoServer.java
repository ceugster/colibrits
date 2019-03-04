/*
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.product;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.eugster.pos.Messages;
import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.client.model.PositionModel;
import ch.eugster.pos.db.CurrentTax;
import ch.eugster.pos.db.Customer;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.db.Table;
import ch.eugster.pos.db.Tax;
import ch.eugster.pos.db.Version;
import ch.eugster.pos.events.MessageDialog;
import ch.eugster.pos.events.ShowMessageEvent;
import ch.eugster.pos.util.Config;

import com4j.ComException;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class GalileoServer extends ProductServer
{
	// private static final Logger log =
	// Logger.getLogger("ch.eugster.pos.product");
	
	protected Igdserve server = null;
	protected static final String version = "1.3.8";
	
	protected GalileoServer()
	{
		this.init();
	}
	
	@Override
	protected void init()
	{
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Konfiguration für Verbindung zu Galileo wird geladen.");
		Config cfx = Config.getInstance();
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Konfiguriere Pfad zur Galileo Datenbank.");
		this.path = cfx.getGalileoPath();
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Konfiguriere Aktualisierung von Daten in Galileo.");
		this.update = cfx.getGalileoUpdate();
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Konfiguriere Zugriff auf VLB DVD.");
		this.readCd = cfx.getGalileoSearchCd();
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Konfiguriere Zugriffspfad auf VLB DVD.");
		this.cdPath = cfx.getGalileoCdPath();
		
		this.active = this.connect(this.path);
	}
	
	public boolean connect(String path)
	{
		boolean connected = false;
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Prüfen, ob Galileo verwendet werden soll.");
		if (ProductServer.isUsed())
		{
			try
			{
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Initialisiere OLE Umgebung.");
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Instantiiere COM-Server galserve.");
				this.server = GalserveFactory.creategdserve();
				this.open();
				if (!Config.getInstance().getProductServerHold())
				{
					this.close();
				}
				connected = true;
			}
			catch (ComException e)
			{
				this.catchComException(e);
			}
		}
		return connected;
	}
	
	public void disconnect()
	{
		if (this.active)
		{
			try
			{
				this.close();
				this.finalize();
			}
			catch (ComException e)
			{
				this.catchComException(e);
			}
		}
	}
	
	@Override
	protected void finalize()
	{
		try
		{
			this.server.dispose();
		}
		catch (ComException e)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: finalize: " + e.getClass().getName() + ": " + e.getMessage());
			this.catchComException(e);
		}
		finally
		{
		}
	}
	
	public void open()
	{
		if (!this.open)
		{
			try
			{
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Verbindung zu Galileo wird geöffnet.");
				this.open = this.server.do_NOpen(this.path);
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Verbindung zu Galileo wurde geöffnet.");
				if (this.open)
				{
					Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Verbindung zu Galileo wird konfiguriert.");
					if (this.readCd) this.readCd = new File(this.cdPath).exists();
					this.setReadCd(this.readCd);
					this.setUpdate(this.update);
					this.setBibwinIniPath(this.cdPath);
				}
				else
				{
					this.active = false;
					Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
									"Verbindung zu Galileo konnte nicht geöffnet werden (Keine Fehlerangabe).");
					ComException ex = new ComException(
									"Beim Öffnen der Verbindung zum COM-Server ist ein unbekannter Fehler aufgetreten.",
									-1);
					this.catchComException(ex);
				}
			}
			catch (ComException e)
			{
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
								"Method: open: " + e.getClass().getName() + ": " + e.getMessage());
				this.catchComException(e);
			}
		}
	}
	
	public void close()
	{
		if (this.open)
		{
			try
			{
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Verbindung zu Galileo wird geschlossen.");
				this.open = !((Boolean) this.server.do_NClose()).booleanValue();
			}
			catch (ComException e)
			{
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
								"Method: close: " + e.getClass().getName() + ": " + e.getMessage());
				this.catchComException(e);
			}
		}
	}
	
	public boolean getCustomer(Integer customerId)
	{
		boolean isOpen = false;
		boolean found = false;
		try
		{
			isOpen = this.isOpen();
			if (!isOpen)
			{
				this.open();
			}
			if (this.open)
			{
				found = this.server.do_getkunde(customerId.intValue());
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
								"Kunde mit der Nummer " + customerId + " wird gesucht: "
												+ (found ? "gefunden" : "nicht gefunden"));
			}
			if (!isOpen)
			{
				this.close();
			}
			
		}
		catch (ComException e)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: getCustomer: " + e.getClass().getName() + ": " + e.getMessage());
			ComException ex = new ComException(
							"Beim Versuch, die Daten eines Kunden via COM-Server anzufordern, ist ein Fehler aufgetreten: "
											+ e.getMessage(), -1);
			this.catchComException(ex);
		}
		return found;
	}
	
	// 10157
	public Customer getCustomerObject()
	{
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Kundendaten werden gelesen.");
		Customer customer = new Customer();
		customer.setId(this.getCustomerId().toString());
		customer.setName(this.getCustomerName());
		customer.setHasCard(this.customerHasCard());
		customer.setAccount(Customer.getAccount(this.getCustomerAccount()));
		return customer;
	}
	
	// 10157
	
	// 10157
	public Integer getCustomerId()
	{
		Integer customerId = new Integer(0);
		try
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Kundennummer wird gelesen.");
			customerId = (Integer) this.server.kundennr();
		}
		catch (ComException e)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: getCustomerId: " + e.getClass().getName() + ": " + e.getMessage());
			ComException ex = new ComException(
							"Beim Versuch, die Kundennummer eines Kunden via COM-Server abzufragen, ist ein Fehler aufgetreten: "
											+ e.getMessage(), -1);
			this.catchComException(ex);
		}
		return customerId;
	}
	
	// 10157
	
	// 10157
	public String getCustomerName()
	{
		String name = "";
		try
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Kundenname wird gelesen.");
			String lastname = (String) this.server.cnamE1();
			String vorname = (String) this.server.cvorname();
			name = lastname.trim() + (vorname.trim().length() > 0 ? ", " + vorname.trim() : "");
		}
		catch (ComException e)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: getCustomerName: " + e.getClass().getName() + ": " + e.getMessage());
			ComException ex = new ComException(
							"Beim Versuch, den Namen eines Kunden via COM-Server abzufragen, ist ein IO-Fehler aufgetreten: "
											+ e.getMessage(), -1);
			this.catchComException(ex);
		}
		return name;
	}
	
	// 10157
	
	// 10157
	public boolean customerHasCard()
	{
		boolean hasCard = false;
		try
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Kundenkarte wird gelesen.");
			hasCard = false;
			hasCard = ((Boolean) this.server.lkundkarte()).booleanValue();
		}
		catch (ComException e)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: customerHasCard: " + e.getClass().getName() + ": " + e.getMessage());
			ComException ex = new ComException(
							"Beim Versuch, via COM-Server abzufragen, ob der Kunde eine Kundenkarte hat, ist ein COM-Fehler aufgetreten: "
											+ e.getMessage(), -1);
			this.catchComException(ex);
		}
		return hasCard;
	}
	
	// 10157
	
	// 10157
	public Double getCustomerAccount()
	{
		Double account = new Double(0d);
		try
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Kundenkonto wird gelesen.");
			account = new Double(0d);
			Integer iAccount = null;
			Long lAccount = null;
			Object oAccount = this.server.nkundkonto();
			if (oAccount instanceof Integer)
			{
				iAccount = (Integer) oAccount;
				account = new Double(iAccount.doubleValue());
			}
			else if (oAccount instanceof Long)
			{
				lAccount = (Long) oAccount;
				account = new Double(lAccount.doubleValue());
			}
			else if (oAccount instanceof Double) account = (Double) this.server.nkundkonto();
		}
		catch (ComException e)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: getCustomerAccount: " + e.getClass().getName() + ": " + e.getMessage());
			ComException ex = new ComException(
							"Beim Versuch, den Kontostand eines Kunden via COM-Server abzufragen, ist ein COM-Fehler aufgetreten: "
											+ e.getMessage(), -1);
			this.catchComException(ex);
		}
		finally
		{
		}
		return account;
	}
	
	// 10157
	
	@Override
	public boolean isStockManagement()
	{
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Prüfe Status Warenbewirtschaftung.");
		return Config.getInstance().getGalileoUpdate() == 2;
	}
	
	public boolean getItem(String code)
	{
		boolean isOpen = false;
		boolean returnValue = false;
		if (!this.active)
		{
			return false;
		}
		try
		{
			isOpen = this.isOpen();
			if (!isOpen)
			{
				this.open();
			}
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Aufruf von do_NSearch mit " + code + ".");
			// boolean o = this.open;
			this.server.do_NSearch(code);
			returnValue = this.found();
			if (returnValue)
			{
				if (code.startsWith(EAN13.PRE_ORDERED))
				{
					int qty = this.getOrderedQuantity().intValue();
					qty -= Position.countOrderedItemsUsed(code);
					if (qty <= 0) returnValue = false;
				}
			}
			if (!isOpen)
			{
				this.close();
			}
		}
		catch (ComException e)
		{
			e.printStackTrace();
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: getItem: " + e.getClass().getName() + ": " + e.getMessage());
			this.catchComException(e);
		}
		return returnValue;
	}
	
	/*
	 * Prüft, ob der Artikel mit dem Code <code>code<code> im Abholfach ist.
	 * Galileo sucht im Abholfach und in der Abholfachablage, wo abgeholte
	 * Artikel abgelegt werden.
	 */
	public boolean isOrderValid(String code)
	{
		boolean validOrder = false;
		boolean isOpen = false;
		if (!this.active)
		{
			return false;
		}
		try
		{
			isOpen = this.isOpen();
			if (!isOpen)
			{
				this.open();
			}
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Prüfe Bestellung.");
			validOrder = this.server.do_teststorno(code);
		}
		catch (ComException e)
		{
			e.printStackTrace();
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: isOrderValid: " + e.getClass().getName() + ": " + e.getMessage());
			this.catchComException(e);
		}
		finally
		{
			if (!isOpen)
			{
				this.close();
			}
		}
		return validOrder;
	}
	
	public boolean setDataToTransferredPosition(Position p)
	{
		double price = p.getPrice();
		CurrentTax tx = p.getCurrentTax();
		if (this.setProductData(p))
		{
			p.setCurrentTax(tx);
			p.setPrice(price, price);
			if (p.getPositionState() == Position.STATE_TAKE_BACK)
			{
				int qty = p.getQuantity();
				this.setReversedData(p);
				p.setQuantity(qty);
			}
			else if (p.orderId.startsWith(EAN13.PRE_ORDERED))
			{
				int qty = p.getQuantity();
				this.setOrderedData(p);
				p.setQuantity(qty);
			}
			if (p.getReceipt().getCustomerId() != null && p.getReceipt().getCustomerId().length() > 0
							&& !p.getReceipt().getCustomerId().equals("0")) p.updateCustomerAccount = true;
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean setData(Position p)
	{
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Galileo Daten werden in Position übernommen.");
		p.ordered = this.isOrdered();
		// String oId = this.getOrderId().trim();
		if (this.setProductData(p))
		{
			if (p.getPositionState() == Position.STATE_TAKE_BACK)
			{
				this.setReversedData(p);
			}
			else if (p.ordered)
			{
				this.setOrderedData(p);
			}
			else
			{
				this.setStockData(p);
			}
			p.updateCustomerAccount = p.getReceipt().getCustomerId().length() > 0;
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void setReversedData(Position p)
	{
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Rückname daten werden in Position übernommen.");
		// p.setPositionState(Position.STATE_TAKE_BACK);
		if (p.productId.startsWith(EAN13.PRE_ORDERED))
		{
			p.orderId = p.productId;
			p.productId = this.getOrderId().trim();
			p.productNumber = p.productId;
			p.stock = this.isFromStock();
			p.optCode = p.stock ? "L" : "B";
			// p.orderId = new Integer(p.productId.substring(3, 12)).toString();
			// p.stock = this.isFromStock();
			// p.optCode = p.stock.booleanValue() ? "L" : "B";
			Integer id = this.getCustomerId();
			if (id != null)
			{
				p.getReceipt().setCustomer(Customer.getCustomer(id.toString()));
				if (UserPanel.getCurrent() != null)
					UserPanel.getCurrent().getReceiptModel().updateCustomer(p.getReceipt().getCustomer());
			}
		}
		else
		{
		}
		if (p.getReceipt().hasCustomer())
		{
			p.updateCustomerAccount = true;
		}
		p.setOrderedQuantity(0); // 10224
	}
	
	public void setOrderedData(Position p)
	{
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Bestelldaten werden in Position übernommen.");
		/*
		 * Bestelldaten
		 */
		p.orderId = p.productId;
		p.productId = this.getOrderId().trim();
		p.stock = this.isFromStock();
		p.optCode = p.stock ? "L" : "B";
		Integer id = this.getCustomerId();
		if (id != null)
		{
			p.getReceipt().setCustomer(Customer.getCustomer(id.toString()));
			if (UserPanel.getCurrent() != null)
				UserPanel.getCurrent().getReceiptModel().updateCustomer(p.getReceipt().getCustomer());
		}
		int usedItems = Position.countOrderedItemsUsed(p.orderId);
		p.setQuantity(this.getQuantity() - usedItems);
		p.setOrderedQuantity(p.getQuantity()); // 10224
	}
	
	private void setStockData(Position p)
	{
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Lagerdaten werden in Position übernommen.");
		p.optCode = this.getOptCode(p); // 10178
		p.stock = true;
		p.setOrderedQuantity(0); // 10224
	}
	
	private boolean setProductData(Position p)
	{
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Artikeldaten werden in Position übernommen.");
		p.author = this.getAuthor().trim();
		p.title = this.getTitle().trim();
		p.publisher = this.getPublisher().trim();
		p.setCurrentTax(Tax.getByGalileoId(this.getTax(), false).getCurrentTax());
		String galileoId = this.getProductGroup();
		if (galileoId == null || galileoId.trim().equals(""))
		{
			if (this.getUpdate() > 0)
			{
				p.setProductGroup(ProductGroup.getDefaultGroup());
			}
		}
		else
		{
			p.setProductGroup(ProductGroup.getByGalileoId(this.getProductGroup(), false));
			if ((p.getProductGroup() == null || p.getProductGroupId() == null || p.getProductGroupId().equals(
							Table.ZERO_VALUE))
							&& this.getUpdate() > 0)
			{
				p.setProductGroup(ProductGroup.getDefaultGroup());
			}
		}
		boolean result = p.getProductGroupId() != null && !p.getProductGroupId().equals(Table.ZERO_VALUE);
		
		if (!new Boolean(Config.getInstance().getInputDefault().getAttributeValue("clear-price")).booleanValue())
		{
			p.setPrice(this.getPrice(), this.getPrice());
		}
		
		p.isbn = this.getISBN();
		p.bznr = this.getBZNumber();
		
		p.productNumber = p.isbn.length() == 0 ? "" : p.isbn;
		if (p.productNumber.length() == 0)
		{
			p.productNumber = p.bznr.length() == 0 ? "" : p.bznr;
		}
		
		return result;
	}
	
	public boolean found()
	{
		boolean found = false;
		try
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Gefunden.");
			found = ((Boolean) this.server.gefunden()).booleanValue();
		}
		catch (ComException e)
		{
			e.printStackTrace();
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: found: " + e.getClass().getName() + ": " + e.getMessage());
			this.catchComException(e);
		}
		return found;
	}
	
	public String getAuthor()
	{
		String s = ""; //$NON-NLS-1$
		if (!this.active)
		{
			return s;
		}
		try
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Autor wird übernommen.");
			s = (String) this.server.autor();
		}
		catch (ComException e)
		{
			e.printStackTrace();
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: getAuthor: " + e.getClass().getName() + ": " + e.getMessage());
			this.catchComException(e);
		}
		return s;
	}
	
	public String getTitle()
	{
		String s = ""; //$NON-NLS-1$
		if (!this.active)
		{
			return s;
		}
		try
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Titel wird übernommen.");
			s = (String) this.server.titel();
		}
		catch (ComException e)
		{
			e.printStackTrace();
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: getTitle: " + e.getClass().getName() + ": " + e.getMessage());
			this.catchComException(e);
		}
		return s;
	}
	
	public String getPublisher()
	{
		String s = ""; //$NON-NLS-1$
		if (!this.active)
		{
			return s;
		}
		try
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Verlag wird übernommen.");
			s = (String) this.server.verlag();
		}
		catch (ComException e)
		{
			e.printStackTrace();
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: getPublisher: " + e.getClass().getName() + ": " + e.getMessage());
			this.catchComException(e);
		}
		return s;
	}
	
	public String getProductGroup()
	{
		String s = ""; //$NON-NLS-1$
		if (!this.active)
		{
			return s;
		}
		try
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Warengruppe wird übernommen.");
			s = (String) this.server.wgruppe();
		}
		catch (ComException e)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: getProductGroup: " + e.getClass().getName() + ": " + e.getMessage());
			this.catchComException(e);
		}
		return s;
	}
	
	public double getPrice()
	{
		Double price = new Double(0d);
		if (!this.active)
		{
			return price.doubleValue();
		}
		try
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Preis wird übernommen.");
			price = (Double) this.server.preis();
		}
		catch (NumberFormatException e)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage());
		}
		catch (ClassCastException e)
		{
			price = this.getPrice(price);
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage());
		}
		catch (ComException e)
		{
			e.printStackTrace();
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: getPrice: " + e.getClass().getName() + ": " + e.getMessage());
			this.catchComException(e);
		}
		return price.doubleValue();
	}
	
	private Double getPrice(Double price)
	{
		Double d = new Double(0d);
		try
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Preis wird übernommen.");
			d = new Double(((Integer) this.server.preis()).doubleValue());
		}
		catch (ComException e)
		{
			e.printStackTrace();
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: getPrice: " + e.getClass().getName() + ": " + e.getMessage());
			this.catchComException(e);
		}
		return d;
	}
	
	public String getTax()
	{
		String tax = new String(""); //$NON-NLS-1$
		if (!this.active)
		{
			return tax;
		}
		try
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Mehrwertsteuercode wird übernommen.");
			tax = (String) this.server.mwst();
		}
		catch (ComException e)
		{
			e.printStackTrace();
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: getTax: " + e.getClass().getName() + ": " + e.getMessage());
			this.catchComException(e);
		}
		return tax;
	}
	
	// 10178
	public String getOptCode(Position position)
	{
		String optCode = position.optCode;
		
		if (this.active && this.getUpdate() == 2)
		{
			try
			{
				Boolean b = new Boolean(false);
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Optionscode wird übernommen.");
				b = (Boolean) this.server.bestellt();
				if (b.booleanValue())
				{
					b = (Boolean) this.server.lagerabholfach();
				}
				optCode = b.booleanValue() ? "B" : "L"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			catch (ComException e)
			{
				e.printStackTrace();
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
								"Method: getOptCode: " + e.getClass().getName() + ": " + e.getMessage());
				this.catchComException(e);
			}
		}
		else
		{
			if (optCode.equals(PositionModel.defaultOption))
			{
				if (!position.getProductGroup().optCodeProposal.equals("")) { //$NON-NLS-1$
					position.optCode = position.getProductGroup().optCodeProposal;
				}
			}
		}
		return optCode;
	}
	
	// 10178
	
	public String getISBN()
	{
		String s = ""; //$NON-NLS-1$
		if (!this.active)
		{
			return s;
		}
		try
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("ISBN wird übernommen.");
			s = (String) this.server.isbn();
		}
		catch (ComException e)
		{
			e.printStackTrace();
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: getISBN: " + e.getClass().getName() + ": " + e.getMessage());
			this.catchComException(e);
		}
		return s;
	}
	
	public String getBZNumber()
	{
		String s = ""; //$NON-NLS-1$
		if (!this.active)
		{
			return s;
		}
		try
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("BZ-Nummer wird übernommen.");
			s = (String) this.server.bznr();
		}
		catch (ComException e)
		{
			e.printStackTrace();
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: getBZNumber: " + e.getClass().getName() + ": " + e.getMessage());
			this.catchComException(e);
		}
		return s;
	}
	
	public boolean isOrdered()
	{
		Boolean isOrdered = new Boolean(false);
		if (!this.active)
		{
			return isOrdered.booleanValue();
		}
		
		// Object bestellt = null;
		try
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Bestellstatus wird übernommen.");
			isOrdered = (Boolean) this.server.bestellt();
		}
		catch (ComException e)
		{
			e.printStackTrace();
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: isOrdered: " + e.getClass().getName() + ": " + e.getMessage());
			this.catchComException(e);
		}
		return isOrdered.booleanValue();
	}
	
	public String getOrderId()
	{
		Object bestellt = null;
		// Object lagerabholfach = null;
		try
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Bestellstatus wird übernommen.");
			bestellt = this.server.bestellt();
			// lagerabholfach = this.server.getLagerabholfach();
		}
		catch (ComException e)
		{
			e.printStackTrace();
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: getOrderId: " + e.getClass().getName() + ": " + e.getMessage());
			this.catchComException(e);
		}
		
		String s = ""; //$NON-NLS-1$
		if (bestellt != null)
		{
			try
			{
				// Boolean isOrdered = new Boolean(false);
				// if (this.server.getBESTELLT().equals(new Boolean(true)))
				// {
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Bestellnummerwird übernommen.");
				s = (String) this.server.bestnummer();
				// }
			}
			catch (ComException e)
			{
				e.printStackTrace();
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
								"Method: getOrderId: " + e.getClass().getName() + ": " + e.getMessage());
				this.catchComException(e);
			}
		}
		return s;
	}
	
	public int getQuantity()
	{
		Integer quantity = new Integer(1);
		if (!this.active)
		{
			return quantity.intValue();
		}
		
		try
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Menge wird übernommen.");
			if (this.server.bestellt().equals(new Boolean(true)))
			{
				quantity = (Integer) this.server.menge();
			}
		}
		catch (ComException e)
		{
			e.printStackTrace();
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: getQuantity: " + e.getClass().getName() + ": " + e.getMessage());
			this.catchComException(e);
		}
		return quantity.intValue();
	}
	
	public Integer getOrderedQuantity()
	{
		Integer quantity = new Integer(0);
		if (!this.active)
		{
			return quantity;
		}
		
		try
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Bestellte Menge wird übernommen.");
			if (this.server.bestellt().equals(new Boolean(true)))
			{
				quantity = (Integer) this.server.menge();
			}
		}
		catch (ComException e)
		{
			e.printStackTrace();
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: getOrderedQuantity: " + e.getClass().getName() + ": " + e.getMessage());
			this.catchComException(e);
		}
		return quantity;
	}
	
	public String getStock()
	{
		String s = ""; //$NON-NLS-1$
		if (!this.active)
		{
			return s;
		}
		try
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Lagerbestand wird übernommen.");
			Object object = this.server.bestand();
			if (object != null && object instanceof String)
			{
				s = (String) object;
			}
		}
		catch (ComException e)
		{
			e.printStackTrace();
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: getStock: " + e.getClass().getName() + ": " + e.getMessage());
			this.catchComException(e);
		}
		return s;
	}
	
	public Date getLastCashSaleDate()
	{
		Object object = null;
		Date d = new Date();
		if (this.active)
		{
			try
			{
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Letztes Verkaufsdatum wird übernommen.");
				object = this.server.lastvkdat();
			}
			catch (ComException e)
			{
				e.printStackTrace();
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
								"Method: getLastCashSaleDate: " + e.getClass().getName() + ": " + e.getMessage());
				this.catchComException(e);
			}
		}
		if (object != null && object instanceof String)
		{
			SimpleDateFormat sdf = new SimpleDateFormat();
			try
			{
				d = sdf.parse((String) object);
			}
			catch (ParseException e)
			{
			}
		}
		return d;
	}
	
	public boolean isCustomer(String customerId)
	{
		boolean returnValue = false;
		// Integer i = new Integer(0);
		if (!this.active)
		{
			return returnValue;
		}
		return this.getCustomerId().intValue() > 0;
	}
	
	public boolean isFromStock()
	{
		Boolean b = new Boolean(false);
		if (!this.active)
		{
			return b.booleanValue();
		}
		try
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Lagerabholfach wird übernommen.");
			Object object = this.server.lagerabholfach();
			if (object != null && object instanceof Boolean)
			{
				b = (Boolean) object;
			}
		}
		catch (ComException e)
		{
			e.printStackTrace();
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: isFromStock: " + e.getClass().getName() + ": " + e.getMessage());
			this.catchComException(e);
		}
		return b.booleanValue();
	}
	
	private void setReadCd(boolean value)
	{
		try
		{
			this.server.lcdsuche(new Boolean(value));
		}
		catch (ComException e)
		{
			e.printStackTrace();
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: setReadCd: " + e.getClass().getName() + ": " + e.getMessage());
			this.catchComException(e);
		}
		finally
		{
		}
	}
	
	private void setUpdate(int value)
	{
		boolean doUpdate = value > 0;
		try
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Buchungsstatus wird übernommen.");
			this.server.nichtbuchen(new Boolean(!doUpdate));
		}
		catch (ComException e)
		{
			e.printStackTrace();
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: setUpdate: " + e.getClass().getName() + ": " + e.getMessage());
			this.catchComException(e);
		}
	}
	
	private void setBibwinIniPath(String path)
	{
		try
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Bibwin.ini-Pfad wird übernommen.");
			this.server.cbibini(path);
		}
		catch (ComException e)
		{
			this.catchComException(e);
		}
	}
	
	public boolean isCdError()
	{
		boolean err = true;
		try
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Ob DVD Fehler vorhanden wird übernommen.");
			err = ((Boolean) this.server.lcderror()).booleanValue();
		}
		catch (ComException e)
		{
			e.printStackTrace();
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: isCdError: " + e.getClass().getName() + ": " + e.getMessage());
			this.catchComException(e);
		}
		return err;
	}
	
	public String getError()
	{
		String err = "";
		try
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("DVD Fehlertext lesen.");
			err = (String) this.server.ccderror();
		}
		catch (ComException e)
		{
			e.printStackTrace();
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: getError: " + e.getClass().getName() + ": " + e.getMessage());
			this.catchComException(e);
		}
		return err;
	}
	
	public boolean update(int receiptState, Position position)
	{
		if (Database.getCurrent().equals(Database.getTutorial()))
		{
			return true;
		}
		else if (Database.getCurrent().equals(Database.getTemporary()))
		{
			return false;
		}
		else if (!this.active)
		{
			return false;
		}
		else
		{
			boolean result = true;
			if (!this.open)
			{
				try
				{
					this.open();
					result = this.doUpdate(receiptState, position);
					if (!Config.getInstance().getProductServerHold())
					{
						this.close();
					}
				}
				catch (ComException e)
				{
					e.printStackTrace();
					Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
									"Method: update: " + e.getClass().getName() + ": " + e.getMessage());
					this.catchComException(e);
				}
				finally
				{
				}
			}
			else
			{
				result = this.doUpdate(receiptState, position);
			}
			return result;
		}
	}
	
	private boolean doUpdate(int receiptState, Position position)
	{
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Galileo Daten werden aktualisiert.");
		boolean booked = true;
		/*
		 * Nur buchen, wenn Buchenflag gesetzt
		 */
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
						"         p.galileoBook == " + Boolean.toString(position.galileoBook));
		if (position.galileoBook)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("         p.productId == " + position.productId);
			if (position.productId.length() > 0 && !position.productId.equals("0"))
			{
				if (position.getProductGroup() == null || position.getProductGroup().getId().equals(Table.ZERO_VALUE))
				{
					this.setProductGroup(position);
				}
				else if (!position.getProductGroupId().equals(position.getProductGroup().getId()))
				{
					this.setProductGroup(position);
				}
			}
			/*
			 * Nur buchen, wenn entweder der Beleg bereits verbucht und aber
			 * storniert ist oder wenn der Beleg noch nicht verbucht ist.
			 */
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
							"         p.galileoBooked == " + Boolean.toString(position.galileoBooked));
			
			if (receiptState == Receipt.RECEIPT_STATE_SERIALIZED)
			{
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
								"         p.ordered == " + Boolean.toString(position.ordered));
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("         p.type == " + position.type);
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
								"         productGroup.galileoId == " + position.getProductGroup().galileoId);
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
								"         productGroup.type == " + position.getProductGroup().type);
				if (!position.galileoBooked)
				{
					// 10221
					/*
					 * Prüfen ob die Position eine bezahlte Rechnung
					 * repräsentiert. Falls ja, muss die Rechnung abgebucht
					 * werden. Weitere Schritte müssen unterbunden werden.
					 * 
					 * Bezahlte Rechnungen werden immer abgebucht (auch wenn
					 * Galileo nicht als ScannerPanel oder WWS funktioniert!
					 */
					if (position.isPayedInvoice())
					{
						if (receiptState == Receipt.RECEIPT_STATE_SERIALIZED)
						{
							booked = this.doUpdatePayedInvoice(position);
						}
					}
					// 10221
					else if (position.ordered)
					{
						/*
						 * Die Position wurde bestellt, also muss die Position
						 * aus der Bestellablage gelöscht werden (der umgekehrte
						 * Weg beim Stornieren darf hier nicht gemacht werden,
						 * da die Gestellung in Galileo beim verbuchen ja
						 * gelöscht worden ist.
						 */
						if (Version.isFrameVisible)
						{
							if (!position.getReceipt().hasCustomer())
							{
								if (this.getItem(position.orderId))
								{
									Customer c = Customer.getCustomer(this.getCustomerId().toString());
									position.getReceipt().setCustomer(c);
								}
							}
						}
						booked = position.getQuantity() > 0 ? this.updateStock(position) : this.reverseStock(position);
					}
					// 10044
					// else if (position.productId != null &&
					// position.productId.length() > 0) {
					// else if
					// (position.getProductGroup().type.equals(ProductGroup.TYPE_INCOME))
					else if (position.type == ProductGroup.TYPE_INCOME)
					{
						Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("         r.state == " + receiptState);
						if (receiptState == Receipt.RECEIPT_STATE_SERIALIZED)
						{
							// 10427
							booked = position.getQuantity() > 0 ? this.updateStock(position) : this
											.reverseStock(position);
							// booked = this.updateStock(position);
							// 10427
						}
					}
				}
			}
			else if (receiptState == Receipt.RECEIPT_STATE_REVERSED)
			{
				if (position.galileoBooked)
				{
					if (!position.isPayedInvoice())
					{
						Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "Rückbuchung"); //$NON-NLS-1$ 
						// 10427
						booked = position.getQuantity() < 0 ? this.updateStock(position) : this.reverseStock(position);
						// booked = this.reverseStock(position);
						// 10427
					}
				}
			}
		}
		return booked;
	}
	
	private void setProductGroup(Position position)
	{
		if (this.getItem(position.productId))
		{
			String wgruppe = this.getProductGroup();
			if (!position.getProductGroup().galileoId.equals(wgruppe))
			{
				ProductGroup pg = ProductGroup.getByGalileoId(wgruppe, true);
				if (pg != null)
				{
					position.setProductGroup(pg);
				}
			}
		}
	}
	
	private boolean updateStock(Position position)
	{
		boolean result = true;
		if (!this.open)
		{
			this.open();
			result = this.doUpdateStock(position);
			if (!Config.getInstance().getProductServerHold())
			{
				this.close();
			}
		}
		else
		{
			result = this.doUpdateStock(position);
		}
		return result;
	}
	
	private boolean doUpdateStock(Position position)
	{
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Galileo Lagerbestand wird mit verkauften Daten aktualisiert");
		boolean booked = false;
		boolean transactionWritten = false;
		boolean stockUpdated = false;
		try
		{
			int menge = Math.abs(position.getQuantity()); // 10224
			this.server.nichtbuchen(new Boolean(this.getUpdate() == 0));
			this.server.vcouponnr(position.getReceipt().getNumber());
			this.server.vnummer(position.productNumber);
			this.server.vpreis(new Double(Math.abs(position.getPrice())));
			this.server.vmwst(position.getCurrentTax().getTax().galileoId);
			this.server.vwgruppe(position.getProductGroup().galileoId);
			this.server.vmenge(new Integer(menge));
			this.server.vrabatt(new Double(position.getDiscountAmount()));
			// 10442
			this.server.vebook(position.productNumber.startsWith(EAN13.PRE_EBOOK));
			
			Integer customerId = new Integer(0);
			try
			{
				customerId = new Integer(position.getReceipt().getCustomerId());
				if (customerId.intValue() > 0)
				{
					position.updateCustomerAccount = true;
				}
			}
			catch (NumberFormatException e)
			{
			}
			finally
			{
				this.server.kundennr(customerId);
				this.server.vkundennr(customerId);
			}
			
			this.server.vbestellt(new Boolean(position.ordered));
			this.server.vlagerabholfach(new Boolean(position.stock));
			
			long productId = 0;
			try
			{
				// 10143
				// productId = new Long(position.productId).longValue();
				String value = position.productId.replace('X', '0');
				productId = new Long(value).longValue();
				// 10143
			}
			catch (NumberFormatException nfe)
			{
				productId = 0;
			}
			if (productId == 0)
			{
				this.server.vwgruppe(position.getProductGroupId().toString());
				this.server.vwgname(position.getProductGroup().name);
				booked = this.server.do_wgverkauf();
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
								"do_wgverkauf aufrufen..." + (booked ? " Ok!" : " Fehler!")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else
			{
				booked = this.server.do_verkauf(position.productId);
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
								"do_verkauf aufrufen..." + (booked ? " Ok!" : " Fehler!")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			transactionWritten = ((Boolean) this.server.vtranswrite()).booleanValue();
			if (this.getUpdate() > 0)
			{
				if (position.ordered)
				{
					// 10224
					// stockUpdated = server.do_delabholfach(position.orderId);
					stockUpdated = this.server.do_delabholfach(position.orderId, menge);
					// 10224
					Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
									"do_delabholfach aufrufen..." + (stockUpdated ? " Ok!" : " Fehler!")); //$NON-NLS-1$ //$NON-NLS-2$
				}
				else if (productId == 0)
				{
					stockUpdated = booked;
				}
				else
				{
					stockUpdated = ((Boolean) this.server.vlagerupdate()).booleanValue();
				}
			}
			if (position.getReceipt().getCustomer().hasCard())
			{
				Object object = this.server.nkundkonto();
				Double account = new Double(object.toString());
				position.getReceipt().getCustomer().setAccount(account);
			}
			booked = transactionWritten;
		}
		catch (ComException e)
		{
			e.printStackTrace();
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: doUpdateStock: " + e.getClass().getName() + ": " + e.getMessage());
			this.catchComException(e);
		}
		return booked;
	}
	
	// 10221
	
	private boolean doUpdatePayedInvoice(Position position)
	{
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Galileo bezahlte Rechnung wird aktualisiert");
		boolean booked = false;
		// boolean transactionWritten = false;
		// boolean stockUpdated = false;
		try
		{
			this.server.nichtbuchen(new Boolean(this.getUpdate() == 0));
			this.server.vcouponnr(position.getReceipt().getNumber());
			this.server.vnummer(position.productId);
			this.server.vpreis(new Double(Math.abs(position.getPrice())));
			this.server.vmwst(position.getCurrentTax().getTax().galileoId);
			this.server.vwgruppe(position.getProductGroup().galileoId);
			this.server.vmenge(new Integer(Math.abs(position.getQuantity())));
			this.server.vrabatt(new Double(position.getDiscountAmount()));
			
			Integer customerId = new Integer(0);
			try
			{
				customerId = new Integer(position.getReceipt().getCustomerId());
				if (customerId.intValue() > 0)
				{
					position.updateCustomerAccount = true;
				}
			}
			catch (NumberFormatException e)
			{
			}
			finally
			{
				this.server.kundennr(customerId);
				this.server.vkundennr(customerId);
			}
			
			this.server.vbestellt(new Boolean(position.ordered));
			this.server.vlagerabholfach(new Boolean(position.stock));
			
			booked = this.server.do_BucheRechnung(position.getInvoiceNumber().intValue());
			if (!booked)
			{
				this.message = (String) this.server.crgerror();
			}
		}
		catch (ComException e)
		{
			e.printStackTrace();
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: doUpdatePayedInvoice: " + e.getClass().getName() + ": " + e.getMessage());
			this.catchComException(e);
		}
		return booked;
	}
	
	// 10221
	
	private boolean reverseStock(Position position)
	{
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
						"Galileo Lagerbestand wird mit zurückgenommenen Daten aktualisiert");
		boolean result = true;
		if (!this.open)
		{
			try
			{
				this.open();
				result = this.doReverse(position);
				if (!Config.getInstance().getProductServerHold())
				{
					this.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
								"Method: reverseStock: " + e.getClass().getName() + ": " + e.getMessage());
				this.catchComException(new ComException(e.getLocalizedMessage(), -1));
			}
			finally
			{
			}
		}
		else
		{
			return this.doReverse(position);
		}
		return result;
	}
	
	private boolean doReverse(Position position)
	{
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Konfiguriere Rücknahme");
		boolean booked = false;
		boolean transactionWritten = false;
		try
		{
			this.server.nichtbuchen(new Boolean(this.getUpdate() == 0));
			this.server.vcouponnr(position.getReceipt().getNumber());
			this.server.vnummer(position.productId);
			this.server.vpreis(new Double(Math.abs(position.getPrice())));
			this.server.vmwst(position.getCurrentTax().getTax().galileoId);
			this.server.vwgruppe(position.getProductGroup().galileoId);
			this.server.vmenge(new Integer(Math.abs(position.getQuantity())));
			this.server.vrabatt(new Double(position.getDiscountAmount()));
			this.server.vebook(position.productNumber.startsWith(EAN13.PRE_EBOOK));
			
			Integer customerId = new Integer(0);
			try
			{
				customerId = new Integer(position.getReceipt().getCustomerId());
				if (customerId.intValue() > 0)
				{
					position.updateCustomerAccount = true;
				}
			}
			catch (NumberFormatException e)
			{
			}
			finally
			{
				this.server.kundennr(customerId);
				this.server.vkundennr(customerId);
			}
			
			this.server.vbestellt(new Boolean(position.ordered));
			this.server.vlagerabholfach(new Boolean(position.stock));
			
			long productId = 0;
			try
			{
				// 10143
				// productId = new Long(position.productId).longValue();
				String value = position.productId.replace('X', '0');
				productId = new Long(value).longValue();
				// 10143
			}
			catch (NumberFormatException nfe)
			{
				productId = 0;
			}
			if (productId == 0)
			{
				this.server.vwgruppe(position.getProductGroupId().toString());
				this.server.vwgname(position.getProductGroup().name);
				booked = this.server.do_wgstorno();
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
								"do_wgstorno aufrufen..." + (booked ? " Ok!" : " Fehler!")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else
			{
				booked = this.server.do_storno(position.productId);
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
								"do_storno aufrufen..." + (booked ? " Ok!" : " Fehler!")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			transactionWritten = ((Boolean) this.server.vtranswrite()).booleanValue();
			if (this.getUpdate() > 0)
			{
				// 10060
				// Bei Aktualisierung einer Position ohne Galileo-Titel
				// darf getVLAGERUPDATE nicht abgefragt werden, stockUpdated
				// muss aber true zurückgeben.
				// stockUpdated = ((Boolean)
				// server.getVLAGERUPDATE()).booleanValue();
				// }
				// if (this.getUpdate() > 0)
				// {
				// if (position.ordered.booleanValue())
				// {
				// // 10224
				// // stockUpdated =
				// // server.do_delabholfach(position.orderId);
				// stockUpdated = this.server.do_delabholfach(position.orderId,
				// position.getQuantity().intValue());
				// // 10224
				//						Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("do_delabholfach aufrufen..." + (stockUpdated ? " Ok!" : " Fehler!")); //$NON-NLS-1$ //$NON-NLS-2$
				// }
				// else if (productId == 0)
				// {
				// stockUpdated = booked;
				// }
				// else
				// {
				// stockUpdated = ((Boolean)
				// this.server.getVLAGERUPDATE()).booleanValue();
				// }
				// }
				if (position.getReceipt().getCustomer().hasCard())
				{
					Object object = this.server.nkundkonto();
					Double account = new Double(object.toString());
					position.getReceipt().getCustomer().setAccount(account);
				}
				// if (productId == 0)
				// {
				// // stockUpdated = booked;
				// }
				// else
				// {
				// // stockUpdated = ((Boolean) this.server.getVLAGERUPDATE())
				// // .booleanValue();
				// }
			}
			if (position.getReceipt().getCustomer().hasCard())
			{
				Object object = this.server.nkundkonto();
				Double account = new Double(object.toString());
				position.getReceipt().getCustomer().setAccount(account);
			}
		}
		catch (ComException e)
		{
			e.printStackTrace();
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
							"Method: doReverse: " + e.getClass().getName() + ": " + e.getMessage());
			this.catchComException(e);
		}
		return transactionWritten;
	}
	
	public void catchComException(Exception e) throws ComException
	{
		this.setActive(false);
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).throwing(this.getClass().getName(), "catchComException", e);
		// if (this.update > 0 &&
		// Database.getCurrent().equals(Database.getStandard()))
		if (Database.getCurrent().equals(Database.getStandard()))
		{
			if (Database.isSwitchable())
			{
				String msg = Messages.getString("GalileoComServerWithStock_12"); //$NON-NLS-1$
				ProductServer.sendMessage(new ShowMessageEvent(msg, Messages.getString("GalileoComServerWithStock_11"),
								MessageDialog.TYPE_INFORMATION));
				Table.switchDatabase();
			}
			else
			{
				String msg = Messages.getString("GalileoComServerWithStock_10"); //$NON-NLS-1$
				ProductServer.sendMessage(new ShowMessageEvent(msg, Messages.getString("GalileoComServerWithStock_11"),
								MessageDialog.TYPE_INFORMATION));
			}
		}
	}
}