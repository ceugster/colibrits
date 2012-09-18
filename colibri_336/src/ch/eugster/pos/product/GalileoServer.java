/*
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.product;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogManager;

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
import ch.eugster.pos.galileo.galserve.gdserve;
import ch.eugster.pos.util.Config;

import com.ibm.bridge2java.ComException;
import com.ibm.bridge2java.OleEnvironment;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class GalileoServer extends ProductServer
{
	
	protected gdserve server = null;
	protected static final String version = "1.3.8";
	
	protected GalileoServer()
	{
		this.init();
	}
	
	protected void init()
	{
		Config cfx = Config.getInstance();
		this.path = cfx.getGalileoPath();
		this.update = cfx.getGalileoUpdate();
		this.readCd = cfx.getGalileoSearchCd();
		this.cdPath = cfx.getGalileoCdPath();
		
		this.active = this.connect(this.path);
	}
	
	public boolean connect(String path)
	{
		if (ProductServer.isUsed())
		{
			try
			{
				OleEnvironment.Initialize();
				this.server = new gdserve();
				this.open();
				if (!Config.getInstance().getProductServerHold())
				{
					this.close();
				}
				return true;
			}
			catch (ComException e)
			{
				OleEnvironment.UnInitialize();
				e.printStackTrace();
				return false;
			}
			catch (IOException e)
			{
				OleEnvironment.UnInitialize();
				e.printStackTrace();
				return false;
			}
		}
		else
		{
			return false;
		}
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
				ComException ex = new ComException(
								"Beim Schliessen der Verbindung zum COM-Server ist ein Fehler aufgetreten.");
				this.catchComException(ex);
			}
			finally
			{
				
			}
		}
	}
	
	protected void finalize()
	{
		try
		{
			this.server.release();
			OleEnvironment.UnInitialize();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
			ComException ex = new ComException("Beim Beenden des COM-Servers ist ein IO-Fehler aufgetreten.");
			this.catchComException(ex);
		}
		catch (ComException e)
		{
			ComException ex = new ComException("Beim Beenden des COM-Servers ist ein Fehler aufgetreten.");
			this.catchComException(ex);
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
				this.open = this.server.do_NOpen(this.path);
				if (this.open)
				{
					if (this.readCd) this.readCd = new File(this.cdPath).exists();
					this.setReadCd(this.readCd);
					this.setUpdate(this.update);
					this.setBibwinIniPath(this.cdPath);
				}
				else
				{
					this.active = false;
					ComException ex = new ComException(
									"Beim Öffnen der Verbindung zum COM-Server ist ein unbekannter Fehler aufgetreten.");
					this.catchComException(ex);
				}
			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();
				this.active = false;
				ComException ex = new ComException(
								"Beim Öffnen der Verbindung zum COM-Server ist ein IO-Fehler aufgetreten.");
				this.catchComException(ex);
			}
			catch (ComException e)
			{
				ComException ex = new ComException(
								"Beim Öffnen der Verbindung zum COM-Server ist ein Fehler aufgetreten.");
				this.catchComException(ex);
			}
			finally
			{
			}
		}
	}
	
	public void close()
	{
		if (this.open)
		{
			try
			{
				this.open = !((Boolean) this.server.do_NClose()).booleanValue();
			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();
				ComException ex = new ComException(
								"Beim Schliessen der Verbindung zum COM-Server ist ein IO-Fehler aufgetreten.");
				this.catchComException(ex);
			}
			catch (ComException e)
			{
				ComException ex = new ComException(
								"Beim Schliessen der Verbindung zum COM-Server ist ein Fehler aufgetreten.");
				this.catchComException(ex);
			}
			finally
			{
				
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
			}
			if (!isOpen)
			{
				this.close();
			}
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
			ComException ex = new ComException(
							"Beim Versuch, die Daten eines Kunden via COM-Server anzufordern, ist ein IO-Fehler aufgetreten.");
			this.catchComException(ex);
		}
		catch (ComException e)
		{
			ComException ex = new ComException(
							"Beim Versuch, die Daten eines Kunden via COM-Server anzufordern, ist ein Fehler aufgetreten.");
			this.catchComException(ex);
		}
		return found;
	}
	
	// 10157
	public Customer getCustomerObject()
	{
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
			customerId = (Integer) this.server.getKUNDENNR();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			ComException ex = new ComException(
							"Beim Versuch, die Kundennummer eines Kunden via COM-Server abzufragen, ist ein IO-Fehler aufgetreten.");
			this.catchComException(ex);
		}
		catch (ComException e)
		{
			ComException ex = new ComException(
							"Beim Versuch, die Kundennummer eines Kunden via COM-Server abzufragen, ist ein Fehler aufgetreten.");
			this.catchComException(ex);
		}
		finally
		{
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
			String lastname = (String) this.server.getCNAME1();
			String vorname = (String) this.server.getCVORNAME();
			name = lastname.trim() + (vorname.trim().length() > 0 ? ", " + vorname.trim() : "");
		}
		catch (IOException e)
		{
			e.printStackTrace();
			ComException ex = new ComException(
							"Beim Versuch, den Namen eines Kunden via COM-Server abzufragen, ist ein IO-Fehler aufgetreten.");
			this.catchComException(ex);
		}
		catch (ComException e)
		{
			ComException ex = new ComException(
							"Beim Versuch, den Namen eines Kunden via COM-Server abzufragen, ist ein IO-Fehler aufgetreten.");
			this.catchComException(ex);
		}
		finally
		{
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
			hasCard = false;
			hasCard = ((Boolean) this.server.getLKUNDKARTE()).booleanValue();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			ComException ex = new ComException(
							"Beim Versuch, via COM-Server abzufragen, ob der Kunde eine Kundenkarte hat, ist ein IO-Fehler aufgetreten.");
			this.catchComException(ex);
		}
		catch (ComException e)
		{
			ComException ex = new ComException(
							"Beim Versuch, via COM-Server abzufragen, ob der Kunde eine Kundenkarte hat, ist ein COM-Fehler aufgetreten.");
			this.catchComException(ex);
		}
		finally
		{
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
			account = new Double(0d);
			Integer iAccount = null;
			Long lAccount = null;
			Object oAccount = this.server.getNKUNDKONTO();
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
			else if (oAccount instanceof Double) account = (Double) this.server.getNKUNDKONTO();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			ComException ex = new ComException(
							"Beim Versuch, den Kontostand eines Kunden via COM-Server abzufragen, ist ein IO-Fehler aufgetreten.");
			this.catchComException(ex);
		}
		catch (ComException e)
		{
			ComException ex = new ComException(
							"Beim Versuch, den Kontostand eines Kunden via COM-Server abzufragen, ist ein COM-Fehler aufgetreten.");
			this.catchComException(ex);
		}
		finally
		{
		}
		return account;
	}
	
	// 10157
	
	public boolean isStockManagement()
	{
		return Config.getInstance().getGalileoUpdate() == 2;
		// boolean returnValue = false;
		// try
		// {
		// returnValue = ((Boolean) this.server.iswws()).booleanValue();
		// }
		// catch (IOException e)
		// {
		// e.printStackTrace();
		// ComException ex = new
		// ComException("Beim Versuch, den Warenbewirtschaftungsstatus von Galileo via COM-Server abzufragen, ist ein IO-Fehler aufgetreten.");
		// this.catchComException(ex);
		// }
		// catch (ComException e)
		// {
		// ComException ex = new
		// ComException("Beim Versuch, den Warenbewirtschaftungsstatus von Galileo via COM-Server abzufragen, ist ein Fehler aufgetreten.");
		// this.catchComException(ex);
		// }
		// finally
		// {
		// }
		// return returnValue;
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
			if (LogManager.getLogManager().getLogger("colibri") != null)
				LogManager.getLogManager().getLogger("colibri").info("Aufruf von do_NSearch mit " + code + ".");
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
		catch (ComException ce)
		{
			if (LogManager.getLogManager().getLogger("colibri") != null)
				LogManager.getLogManager().getLogger("colibri")
								.severe("do_NSearch hat ComException in getItem ausgelöst.");
			this.catchComException(ce);
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
			this.catchComException(new ComException());
		}
		finally
		{
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
			validOrder = this.server.do_teststorno(code);
		}
		catch (ComException ce)
		{
			// showError(ce);
			ce.printStackTrace();
			this.catchComException(ce);
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
			// showError(ioe);
			this.catchComException(new ComException());
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
			// Integer id = new Integer(0);
			// try
			// {
			// id = new Integer(p.getReceipt().getCustomerId());
			// }
			// catch (NumberFormatException e)
			// {
			// System.out.println("No Customer...");
			// }
			// finally
			// {
			// if (this.getCustomer(id))
			// {
			// p.getReceipt().setCustomer(this.getCustomerObject());
			// }
			// else
			// {
			// p.getReceipt().setCustomer();
			// }
			p.updateCustomerAccount = true;
			// }
		}
		p.setOrderedQuantity(0); // 10224
	}
	
	public void setOrderedData(Position p)
	{
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
		p.optCode = this.getOptCode(p); // 10178
		p.stock = true;
		p.setOrderedQuantity(0); // 10224
	}
	
	private boolean setProductData(Position p)
	{
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
	
	// private String getProductNumber()
	// {
	// String productNumber = "";
	// try
	// {
	// productNumber = (String) this.server.getBESTNUMMER();
	// }
	// catch (Exception e)
	// {
	// e.printStackTrace();
	// this.catchComException(new ComException());
	// }
	// finally
	// {
	// return productNumber;
	// }
	// }
	
	public boolean found()
	{
		boolean found = false;
		try
		{
			found = ((Boolean) this.server.getGEFUNDEN()).booleanValue();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			this.catchComException(new ComException());
		}
		catch (ComException e)
		{
			this.catchComException(e);
		}
		finally
		{
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
			s = (String) this.server.getAUTOR();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			this.catchComException(new ComException());
		}
		catch (ComException e)
		{
			this.catchComException(e);
		}
		finally
		{
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
			s = (String) this.server.getTITEL();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			this.catchComException(new ComException());
		}
		catch (ComException e)
		{
			this.catchComException(e);
		}
		finally
		{
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
			s = (String) this.server.getVERLAG();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			this.catchComException(new ComException());
		}
		catch (ComException e)
		{
			this.catchComException(e);
		}
		finally
		{
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
			s = (String) this.server.getWGRUPPE();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			this.catchComException(new ComException());
		}
		catch (ComException e)
		{
			this.catchComException(e);
		}
		finally
		{
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
			price = (Double) this.server.getPREIS();
		}
		catch (NumberFormatException e)
		{
			if (LogManager.getLogManager().getLogger("colibri") != null) LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
		}
		catch (IOException ioe)
		{
			if (LogManager.getLogManager().getLogger("colibri") != null) LogManager.getLogManager().getLogger("colibri").severe(ioe.getLocalizedMessage()); //$NON-NLS-1$
			this.catchComException(new ComException());
		}
		catch (ClassCastException e)
		{
			price = this.getPrice(price);
			if (LogManager.getLogManager().getLogger("colibri") != null) LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
		}
		catch (ComException e)
		{
			this.catchComException(e);
		}
		finally
		{
		}
		return price.doubleValue();
	}
	
	private Double getPrice(Double price)
	{
		Double d = new Double(0d);
		try
		{
			d = new Double(((Integer) this.server.getPREIS()).doubleValue());
		}
		catch (IOException ioe)
		{
			return price;
		}
		catch (ComException e)
		{
			this.catchComException(e);
		}
		finally
		{
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
			tax = (String) this.server.getMWST();
		}
		catch (IOException e)
		{
			this.catchComException(new ComException());
		}
		catch (ComException e)
		{
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
				b = (Boolean) this.server.getBESTELLT();
				if (b.booleanValue())
				{
					b = (Boolean) this.server.getLAGERABHOLFACH();
				}
				optCode = b.booleanValue() ? "B" : "L"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			catch (IOException e)
			{
				this.catchComException(new ComException());
			}
			catch (ComException e)
			{
				this.catchComException(e);
			}
			finally
			{
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
			s = (String) this.server.getISBN();
		}
		catch (IOException e)
		{
			this.catchComException(new ComException());
		}
		catch (ComException e)
		{
			this.catchComException(e);
		}
		finally
		{
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
			s = (String) this.server.getBZNR();
		}
		catch (IOException e)
		{
			this.catchComException(new ComException());
		}
		catch (ComException e)
		{
			this.catchComException(e);
		}
		finally
		{
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
			isOrdered = (Boolean) this.server.getBESTELLT();
		}
		catch (IOException ioe)
		{
			if (LogManager.getLogManager().getLogger("colibri") != null) LogManager.getLogManager().getLogger("colibri").severe(ioe.getLocalizedMessage()); //$NON-NLS-1$
			this.catchComException(new ComException());
		}
		catch (ComException e)
		{
			this.catchComException(e);
		}
		finally
		{
		}
		return isOrdered.booleanValue();
	}
	
	public String getOrderId()
	{
		Object bestellt = null;
		// Object lagerabholfach = null;
		try
		{
			bestellt = this.server.getBESTELLT();
			// lagerabholfach = this.server.getLagerabholfach();
		}
		catch (IOException ioe)
		{
			if (LogManager.getLogManager().getLogger("colibri") != null) LogManager.getLogManager().getLogger("colibri").severe(ioe.getLocalizedMessage()); //$NON-NLS-1$
			this.catchComException(new ComException());
		}
		catch (ComException e)
		{
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
				s = (String) this.server.getBESTNUMMER();
				// }
			}
			catch (IOException ioe)
			{
				this.catchComException(new ComException());
			}
			catch (ComException e)
			{
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
			if (this.server.getBESTELLT().equals(new Boolean(true)))
			{
				quantity = (Integer) this.server.getMENGE();
			}
		}
		catch (IOException ioe)
		{
			//			LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
			this.catchComException(new ComException());
		}
		catch (ComException e)
		{
			this.catchComException(e);
		}
		finally
		{
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
			if (this.server.getBESTELLT().equals(new Boolean(true)))
			{
				quantity = (Integer) this.server.getMENGE();
			}
		}
		catch (IOException ioe)
		{
			//			LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
			this.catchComException(new ComException());
		}
		catch (ComException e)
		{
			this.catchComException(e);
		}
		finally
		{
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
			Object object = this.server.getBESTAND();
			if (object != null && object instanceof String)
			{
				s = (String) object;
			}
		}
		catch (IOException ioe)
		{
			//			LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
			this.catchComException(new ComException());
		}
		catch (ComException e)
		{
			this.catchComException(e);
		}
		finally
		{
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
				object = this.server.getLASTVKDAT();
			}
			catch (IOException ioe)
			{
				//			LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
				this.catchComException(new ComException());
			}
			catch (ComException e)
			{
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
			Object object = this.server.getLAGERABHOLFACH();
			if (object != null && object instanceof Boolean)
			{
				b = (Boolean) object;
			}
		}
		catch (IOException ioe)
		{
			//			LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
			this.catchComException(new ComException());
		}
		catch (ComException e)
		{
			this.catchComException(e);
		}
		finally
		{
		}
		return b.booleanValue();
	}
	
	private void setReadCd(boolean value)
	{
		try
		{
			this.server.setLCDSUCHE(new Boolean(value));
		}
		catch (IOException ioe)
		{
			//			LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
			this.catchComException(new ComException());
		}
		catch (ComException e)
		{
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
			this.server.setNICHTBUCHEN(new Boolean(!doUpdate));
		}
		catch (IOException ioe)
		{
			//			LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
			this.catchComException(new ComException());
		}
		catch (ComException e)
		{
			this.catchComException(e);
		}
		finally
		{
		}
	}
	
	private void setBibwinIniPath(String path)
	{
		try
		{
			this.server.setCBIBINI(path);
		}
		catch (IOException ioe)
		{
			this.catchComException(new ComException());
		}
		catch (ComException e)
		{
			this.catchComException(e);
		}
		finally
		{
		}
	}
	
	public boolean isCdError()
	{
		boolean err = true;
		try
		{
			err = ((Boolean) this.server.getLCDERROR()).booleanValue();
		}
		catch (IOException ioe)
		{
			//			LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
			return err;
		}
		catch (ComException e)
		{
			this.catchComException(e);
		}
		finally
		{
		}
		return err;
	}
	
	public String getError()
	{
		String err = "";
		try
		{
			err = (String) this.server.getCCDERROR();
		}
		catch (IOException ioe)
		{
			//			LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
			this.catchComException(new ComException());
		}
		catch (ComException e)
		{
			this.catchComException(e);
		}
		finally
		{
		}
		return err;
	}
	
	public boolean update(int receiptState, Position position)
	{
		if (LogManager.getLogManager().getLogger("colibri") != null)
			LogManager.getLogManager().getLogger("colibri").info("Entered: GalileoServer.update()");
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
				catch (ComException ce)
				{
					result = false;
					this.catchComException(ce);
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
		if (LogManager.getLogManager().getLogger("colibri") != null)
			LogManager.getLogManager().getLogger("colibri").info("Entered: GalileoServer.doUpdate()");
		boolean booked = true;
		/*
		 * Nur buchen, wenn Buchenflag gesetzt
		 */
		if (LogManager.getLogManager().getLogger("colibri") != null)
			LogManager.getLogManager().getLogger("colibri")
							.info("         p.galileoBook == " + Boolean.toString(position.galileoBook));
		if (position.galileoBook)
		{
			if (LogManager.getLogManager().getLogger("colibri") != null)
				LogManager.getLogManager().getLogger("colibri").info("         p.productId == " + position.productId);
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
			if (LogManager.getLogManager().getLogger("colibri") != null)
				LogManager.getLogManager().getLogger("colibri")
								.info("         p.galileoBooked == " + Boolean.toString(position.galileoBooked));
			
			if (receiptState == Receipt.RECEIPT_STATE_SERIALIZED)
			{
				if (LogManager.getLogManager().getLogger("colibri") != null)
				{
					LogManager.getLogManager().getLogger("colibri")
									.info("         p.ordered == " + Boolean.toString(position.ordered));
					LogManager.getLogManager().getLogger("colibri").info("         p.type == " + position.type);
					LogManager.getLogManager().getLogger("colibri")
									.info("         productGroup.galileoId == " + position.getProductGroup().galileoId);
					LogManager.getLogManager().getLogger("colibri")
									.info("         productGroup.type == " + position.getProductGroup().type);
				}
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
						if (LogManager.getLogManager().getLogger("colibri") != null)
							LogManager.getLogManager().getLogger("colibri").info("         r.state == " + receiptState);
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
						if (LogManager.getLogManager().getLogger("colibri") != null)
							LogManager.getLogManager().getLogger("colibri").log(Level.INFO, "Rückbuchung"); //$NON-NLS-1$ //$NON-NLS-2$
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
		if (LogManager.getLogManager().getLogger("colibri") != null)
			LogManager.getLogManager().getLogger("colibri").info("Entered: GalileoServer.updateStock()");
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
		if (LogManager.getLogManager().getLogger("colibri") != null)
			LogManager.getLogManager().getLogger("colibri").info("Entered: GalileoServer.doUpdateStock()");
		boolean booked = false;
		boolean transactionWritten = false;
		boolean stockUpdated = false;
		try
		{
			int menge = Math.abs(position.getQuantity()); // 10224
			this.server.setNICHTBUCHEN(new Boolean(this.getUpdate() == 0));
			this.server.setVCOUPONNR(position.getReceipt().getNumber());
			this.server.setVNUMMER(position.productId);
			this.server.setVPREIS(new Double(Math.abs(position.getPrice())));
			this.server.setVMWST(position.getCurrentTax().getTax().galileoId);
			this.server.setVWGRUPPE(position.getProductGroup().galileoId);
			this.server.setVMENGE(new Integer(menge));
			this.server.setVRABATT(new Double(position.getDiscountAmount()));
			
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
				this.server.setKUNDENNR(customerId);
				this.server.setVKUNDENNR(customerId);
			}
			
			this.server.setVBESTELLT(new Boolean(position.ordered));
			this.server.setVLAGERABHOLFACH(new Boolean(position.stock));
			
			long productId = 0;
			try
			{
				// 10143
				// productId = new Long(position.productId).longValue();
				productId = new Long(position.productId.replace('X', '0')).longValue();
				// 10143
			}
			catch (NumberFormatException nfe)
			{
				productId = 0;
			}
			if (productId == 0)
			{
				this.server.setVWGRUPPE(position.getProductGroupId().toString());
				this.server.setVWGNAME(position.getProductGroup().name);
				booked = this.server.do_wgverkauf();
				if (LogManager.getLogManager().getLogger("colibri") != null) LogManager.getLogManager().getLogger("colibri").info("do_wgverkauf aufrufen..." + (booked ? " Ok!" : " Fehler!")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else
			{
				booked = this.server.do_verkauf(position.productId);
				if (LogManager.getLogManager().getLogger("colibri") != null) LogManager.getLogManager().getLogger("colibri").info("do_verkauf aufrufen..." + (booked ? " Ok!" : " Fehler!")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			transactionWritten = ((Boolean) this.server.getVTRANSWRITE()).booleanValue();
			if (this.getUpdate() > 0)
			{
				if (position.ordered)
				{
					// 10224
					// stockUpdated = server.do_delabholfach(position.orderId);
					stockUpdated = this.server.do_delabholfach(position.orderId, menge);
					// 10224
					if (LogManager.getLogManager().getLogger("colibri") != null)
						LogManager.getLogManager()
										.getLogger("colibri").info("do_delabholfach aufrufen..." + (stockUpdated ? " Ok!" : " Fehler!")); //$NON-NLS-1$ //$NON-NLS-2$
				}
				else if (productId == 0)
				{
					stockUpdated = booked;
				}
				else
				{
					stockUpdated = ((Boolean) this.server.getVLAGERUPDATE()).booleanValue();
				}
			}
			if (position.getReceipt().getCustomer().hasCard())
			{
				Object object = this.server.getNKUNDKONTO();
				Double account = new Double(object.toString());
				position.getReceipt().getCustomer().setAccount(account);
			}
			booked = transactionWritten;
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
			this.catchComException(new ComException());
		}
		catch (ComException e)
		{
			this.catchComException(e);
		}
		finally
		{
		}
		return booked;
	}
	
	// 10221
	
	private boolean doUpdatePayedInvoice(Position position)
	{
		if (LogManager.getLogManager().getLogger("colibri") != null)
			LogManager.getLogManager().getLogger("colibri").info("Entered: GalileoServer.doUpdatePayedInvoice()");
		boolean booked = false;
		// boolean transactionWritten = false;
		// boolean stockUpdated = false;
		try
		{
			this.server.setNICHTBUCHEN(new Boolean(this.getUpdate() == 0));
			this.server.setVCOUPONNR(position.getReceipt().getNumber());
			this.server.setVNUMMER(position.productId);
			this.server.setVPREIS(new Double(Math.abs(position.getPrice())));
			this.server.setVMWST(position.getCurrentTax().getTax().galileoId);
			this.server.setVWGRUPPE(position.getProductGroup().galileoId);
			this.server.setVMENGE(new Integer(Math.abs(position.getQuantity())));
			this.server.setVRABATT(new Double(position.getDiscountAmount()));
			
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
				this.server.setKUNDENNR(customerId);
				this.server.setVKUNDENNR(customerId);
			}
			
			this.server.setVBESTELLT(new Boolean(position.ordered));
			this.server.setVLAGERABHOLFACH(new Boolean(position.stock));
			
			booked = this.server.do_BucheRechnung(position.getInvoiceNumber().intValue());
			if (!booked)
			{
				this.message = (String) this.server.getCRGERROR();
			}
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
			this.catchComException(new ComException());
		}
		catch (ComException e)
		{
			this.catchComException(e);
		}
		finally
		{
		}
		return booked;
	}
	
	// 10221
	
	private boolean reverseStock(Position position)
	{
		if (LogManager.getLogManager().getLogger("colibri") != null)
			LogManager.getLogManager().getLogger("colibri").info("Entered: GalileoServer.reverseStock()");
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
				result = false;
				this.catchComException(new ComException());
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
		if (LogManager.getLogManager().getLogger("colibri") != null)
			LogManager.getLogManager().getLogger("colibri").info("Entered: GalileoServer.doReverse()");
		boolean booked = false;
		boolean transactionWritten = false;
		try
		{
			this.server.setNICHTBUCHEN(new Boolean(this.getUpdate() == 0));
			this.server.setVCOUPONNR(position.getReceipt().getNumber());
			this.server.setVNUMMER(position.productId);
			this.server.setVPREIS(new Double(Math.abs(position.getPrice())));
			this.server.setVMWST(position.getCurrentTax().getTax().galileoId);
			this.server.setVWGRUPPE(position.getProductGroup().galileoId);
			this.server.setVMENGE(new Integer(Math.abs(position.getQuantity())));
			this.server.setVRABATT(new Double(position.getDiscountAmount()));
			
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
				this.server.setKUNDENNR(customerId);
				this.server.setVKUNDENNR(customerId);
			}
			
			this.server.setVBESTELLT(new Boolean(position.ordered));
			this.server.setVLAGERABHOLFACH(new Boolean(position.stock));
			
			long productId = 0;
			try
			{
				// 10143
				// productId = new Long(position.productId).longValue();
				productId = new Long(position.productId.replace('X', '0')).longValue();
				// 10143
			}
			catch (NumberFormatException nfe)
			{
				productId = 0;
			}
			if (productId == 0)
			{
				this.server.setVWGRUPPE(position.getProductGroupId().toString());
				this.server.setVWGNAME(position.getProductGroup().name);
				booked = this.server.do_wgstorno();
				if (LogManager.getLogManager().getLogger("colibri") != null) LogManager.getLogManager().getLogger("colibri").info("do_wgstorno aufrufen..." + (booked ? " Ok!" : " Fehler!")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else
			{
				booked = this.server.do_storno(position.productId);
				if (LogManager.getLogManager().getLogger("colibri") != null) LogManager.getLogManager().getLogger("colibri").info("do_storno aufrufen..." + (booked ? " Ok!" : " Fehler!")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			transactionWritten = ((Boolean) this.server.getVTRANSWRITE()).booleanValue();
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
				//						LogManager.getLogManager().getLogger("colibri").info("do_delabholfach aufrufen..." + (stockUpdated ? " Ok!" : " Fehler!")); //$NON-NLS-1$ //$NON-NLS-2$
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
					Object object = this.server.getNKUNDKONTO();
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
				Object object = this.server.getNKUNDKONTO();
				Double account = new Double(object.toString());
				position.getReceipt().getCustomer().setAccount(account);
			}
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
			this.catchComException(new ComException());
		}
		catch (ComException e)
		{
			this.catchComException(e);
		}
		finally
		{
		}
		return transactionWritten;
	}
	
	private void catchComException(ComException e) throws ComException
	{
		e.printStackTrace();
		this.setActive(false);
		
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
		if (LogManager.getLogManager().getLogger("colibri") != null)
			LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage());
		throw e;
	}
	
}