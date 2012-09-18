/*
 * Created on 02.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.client.model;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogManager;

import javax.swing.JTable;

import ch.eugster.pos.Messages;
import ch.eugster.pos.client.gui.Frame;
import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.db.CurrentTax;
import ch.eugster.pos.db.Customer;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.Option;
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.db.ReceiptChild;
import ch.eugster.pos.db.Table;
import ch.eugster.pos.db.Tax;
import ch.eugster.pos.db.TaxRate;
import ch.eugster.pos.db.TaxType;
import ch.eugster.pos.events.Action;
import ch.eugster.pos.events.ClearAction;
import ch.eugster.pos.events.DeleteAction;
import ch.eugster.pos.events.MessageDialog;
import ch.eugster.pos.events.PosEvent;
import ch.eugster.pos.events.PositionChangeAction;
import ch.eugster.pos.events.ReceiptChangeEvent;
import ch.eugster.pos.events.ReceiptChildChangeEvent;
import ch.eugster.pos.product.Code128;
import ch.eugster.pos.product.EAN13;
import ch.eugster.pos.product.ISBN;
import ch.eugster.pos.product.ProductServer;
import ch.eugster.pos.util.Config;
import ch.eugster.pos.util.NumberUtility;

import com.ibm.bridge2java.ComException;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PositionModel extends ReceiptChildModel
{
	
	public PositionModel(ReceiptModel receiptModel, JTable table)
	{
		super(receiptModel, table);
		this.init(Position.getInstance(receiptModel.getReceipt(), PositionModel.defaultTax,
						PositionModel.defaultQuantity, PositionModel.defaultOption));
	}
	
	public PositionModel(ReceiptModel receiptModel, JTable table, Position position)
	{
		super(receiptModel, table);
		this.init(position);
	}
	
	private void init(Position position)
	{
		this.maxQuantityRange = Math.abs(Config.getInstance().getInputDefaultMaxQuantityRange());
		this.maxQuantityAmount = Math.abs(Config.getInstance().getInputDefaultMaxQuantityAmount());
		this.maxRange = Math.abs(Config.getInstance().getInputDefaultMaxPriceRange());
		this.maxAmount = Math.abs(Config.getInstance().getInputDefaultMaxPriceAmount());
		this.setPosition(position);
	}
	
	protected void showPutCustomerMessage(ReceiptChangeEvent e)
	{
		if (this.getProductGroup().type != ProductGroup.TYPE_INPUT
						&& this.getProductGroup().type != ProductGroup.TYPE_WITHDRAW)
		{
			if (e.getEventType() == ReceiptModel.RECEIPT_CHILD_ADDED
							|| e.getEventType() == ReceiptModel.RECEIPT_CHILD_CHANGED)
			{
				if (ProductServer.isUsed())
				{
					if (this.getPositionState() == Position.STATE_TAKE_BACK)
					{
						if (!this.getPosition().getReceipt().hasCustomer())
						{
							if (Config.getInstance().getGalileoShowAddCustomerMessage())
							{
								MessageDialog.showInformation(Frame.getMainFrame(), "Kunden erfassen", //$NON-NLS-1$
												"Denken Sie bitte daran, gegebenenfalls den Kunden mitzuerfassen.", 0);
							}
						}
					}
				}
			}
		}
	}
	
	public void setReceiptChild()
	{
		this.setPosition();
	}
	
	public void setReceiptChild(ReceiptChild child)
	{
		this.setPosition((Position) child);
	}
	
	public void setPosition()
	{
		this.child = Position.getInstance(this.receiptModel.getReceipt(), PositionModel.defaultTax,
						PositionModel.defaultQuantity, PositionModel.defaultOption);
		this.fireReceiptChildChangeEvent(new ReceiptChildChangeEvent(this));
	}
	
	public void setPosition(Position position)
	{
		// child = position;
		// child = (ReceiptChild)position.clone(); // 10195
		this.child = position.clone(position.getReceipt(), true);
		this.fireReceiptChildChangeEvent(new ReceiptChildChangeEvent(this));
	}
	
	public Position getPosition()
	{
		return (Position) this.child;
	}
	
	public void posEventPerformed(PosEvent p)
	{
		ReceiptChildChangeEvent evt = new ReceiptChildChangeEvent(this);
		Action a = p.getPosAction();
		if (a instanceof PositionChangeAction)
		{
			if (a.getValue(Action.POS_KEY_ACTION_TYPE).equals(Action.POS_ACTION_SET_PRODUCT_GROUP))
			{
				if (a.getKey().command.length() > 0)
				{
					/*
					 * Der Taste ist eine Artikelnummer hinterlegt. Daher soll
					 * sie sich wie ein Scan verhalten und die Artikeldaten
					 * holen.
					 */
					this.setData(a.getKey().command);
					if (!ProductServer.getInstance().isActive())
					{
						ProductGroup pg = (ProductGroup) a.getValue(Action.POS_KEY_PRODUCT_GROUP);
						this.setProductGroup(pg);
						PositionModel.setText(this.getPosition());
					}
				}
				else
				{
					/*
					 * Es wurde eine Warengruppentaste gedrückt. Hier müssen wir
					 * unterscheiden zwischen ProductGroup.TYPE_WITHDRAW und
					 * 'Normal', weil bei ersterem der Preis/Betrag anders
					 * gerechnet wird als beim letzteren.
					 */
					ProductGroup pg = (ProductGroup) a.getValue(Action.POS_KEY_PRODUCT_GROUP);
					this.setProductGroup(pg);
					
					double amount = 0d;
					if (a.getValue(Action.POS_KEY_PRICE).equals(new Double(0d)))
					{
						amount = this.getPosition().getPrice();
					}
					else
					{
						amount = ((Double) a.getValue(Action.POS_KEY_PRICE)).doubleValue();
						if (this.verifyAmount(amount))
						{
							this.setPrice(amount, amount);
						}
					}
					// if (pg.type.equals(ProductGroup.TYPE_WITHDRAW))
					// {
					// < Double amnt = new Double(amount);
					// if (this.verifyAmount(amnt))
					// {
					// UserPanel.getCurrent().setCurrentForeignCurrency(pg.
					// getForeignCurrency());
					// Double priceFC = new Double(NumberUtility.round(amount,
					// pg.getForeignCurrency().roundFactor.doubleValue()));
					// Double price = new Double(NumberUtility.round(amount *
					// pg.getForeignCurrency().quotation.doubleValue(),
					// ForeignCurrency
					// .getDefaultCurrency().roundFactor.doubleValue()));
					// this.setPrice(price, priceFC);
					// }
					// }
					// else
					// {
					this.setUpdateCustomerAccount(this.getPosition().getReceipt().getCustomerId().length() > 0);
					// }
				}
			}
			else if (a.getValue(Action.POS_KEY_ACTION_TYPE).equals(Action.POS_ACTION_SET_QUANTITY))
			{
				int qty = ((Integer) a.getValue(Action.POS_KEY_QUANTITY)).intValue();
				if (this.verifyQuantity(qty))
				{
					this.setQuantity(qty);
				}
			}
			else if (a.getValue(Action.POS_KEY_ACTION_TYPE).equals(Action.POS_ACTION_SET_PRICE))
			{
				double amount = 0d;
				if (a.getValue(Action.POS_KEY_PRICE).equals(new Double(0d)))
				{
					amount = this.getPosition().getPrice();
				}
				else
				{
					amount = ((Double) a.getValue(Action.POS_KEY_PRICE)).doubleValue();
				}
				int type = this.getPosition().getProductGroup().type;
				if (type == ProductGroup.TYPE_INPUT || type == ProductGroup.TYPE_WITHDRAW)
				{
					ForeignCurrency currency = this.getPosition().getProductGroup().getForeignCurrency();
					double priceFC = NumberUtility.round(amount, currency.roundFactor);
					double price = NumberUtility.round(amount * currency.quotation, ForeignCurrency
									.getDefaultCurrency().roundFactor);
					this.setPrice(price, priceFC);
				}
				else
				{
					double price = amount;
					if (this.verifyAmount(price))
					{
						this.setPrice(price, price);
					}
				}
			}
			else if (a.getValue(Action.POS_KEY_ACTION_TYPE).equals(Action.POS_ACTION_SET_UPDATE_CUSTOMER_ACCOUNT))
			{
				this.setUpdateCustomerAccount(!((Position) this.child).updateCustomerAccount);
			}
			else if (a.getValue(Action.POS_KEY_ACTION_TYPE).equals(Action.POS_ACTION_SET_DISCOUNT))
			{
				// if (!isFresh()) {
				this.setDiscount(((Double) a.getValue(Action.POS_KEY_DISCOUNT)).doubleValue());
				// }
			}
			else if (a.getValue(Action.POS_KEY_ACTION_TYPE).equals(Action.POS_ACTION_SET_TAX))
			{
				if (a.getValue(Action.POS_KEY_TAX_RATE) instanceof TaxRate)
				{
					TaxRate rate = (TaxRate) a.getValue(Action.POS_KEY_TAX_RATE);
					TaxType type = null;
					if (this.getProductGroup().getDefaultTax() == null)
					{
						type = ProductGroup.getDefaultGroup().getDefaultTax().getTaxType();
					}
					else
					{
						type = this.getProductGroup().getDefaultTax().getTaxType();
					}
					if (type.getId() != null && !type.getId().equals(Table.ZERO_VALUE))
					{
						Tax tax = Tax.getByTypeIdAndRateId(type.getId(), rate.getId(), false);
						if (tax.getCurrentTax().getId() != null
										&& !tax.getCurrentTax().getId().equals(Table.ZERO_VALUE))
							this.setCurrentTax(tax.getCurrentTax());
					}
				}
			}
			else if (a.getValue(Action.POS_KEY_ACTION_TYPE).equals(Action.POS_ACTION_SET_OPTION))
			{
				this.setOptCode(((Option) a.getValue(Action.POS_KEY_OPTION)).code);
			}
			/**
			 * Rückgabe eines Titels
			 */
			else if (a.getValue(Action.POS_KEY_ACTION_TYPE).equals(Action.POS_ACTION_RETURN))
			{
				Position pos = (Position) this.child;
				// 10061
				int qty = -pos.getQuantity();
				this.setQuantity(qty);
				// togglePositionState();
				evt.initInputValue(false);
				
				if (pos.getPositionState() == Position.STATE_TAKE_BACK)
				{
					if (pos.productId != null && pos.productId.length() != 0)
					{
						// if (ProductServer.getInstance().getUpdate() > 0)
						// {
						if (pos.ordered && pos.orderId != null && pos.orderId.startsWith(EAN13.PRE_ORDERED))
						{
							MessageDialog
											.showInformation(
															Frame.getMainFrame(),
															"Kein_Lagertitel",
															"<HTML>Der gewählte Titel stammt aus einer Kundenbestellung. <br>Eine allfällige Rückbuchung in Galileo muss manuell vorgenommen werden.", //$NON-NLS-1$
															0);
							pos.galileoBook = true;
						}
						else
						{
							// if (MessageDialog
							// .showSimpleDialog(
							// Frame.getMainFrame(),
							// "Lagertitel",
							//																"<HTML>Der gewählte Titel kann ans Lager gegeben werden. <br>Sollen die Lagerdaten entsprechend aktualisiert werden?", //$NON-NLS-1$
							// 1) == MessageDialog.BUTTON_YES)
							// {
							pos.galileoBook = true;
							// }
							// else
							// {
							// pos.galileoBook = new Boolean(false);
							// }
						}
						// }
						// else
						// {
						// pos.galileoBook = new Boolean(true);
						// }
					}
				}
				else if (pos.getProductGroup().type == ProductGroup.TYPE_INCOME)
				{
					pos.galileoBook = true;
				}
			}
		}
		else if (a instanceof ClearAction)
		{
			if (a.getValue(Action.POS_KEY_ACTION_TYPE).equals(Action.POS_ACTION_CLEAR))
			{
				this.setPosition();
			}
		}
		else if (a instanceof DeleteAction)
		{
			if (a.getValue(Action.POS_KEY_ACTION_TYPE).equals(Action.POS_ACTION_DELETE_ENTRY))
			{
				this.setPosition();
			}
		}
		this.fireReceiptChildChangeEvent(evt);
	}
	
	private boolean verifyAmount(double amount)
	{
		double testAmount = Math.abs(amount);
		NumberFormat nf = NumberFormat.getCurrencyInstance();
		String value = nf.format(testAmount);
		if (testAmount > this.maxRange)
		{
			MessageDialog.showSimpleDialog(Frame.getMainFrame(), "Betrag zu Hoch", "Der Betrag " + value
							+ " ist zu hoch.", MessageDialog.TYPE_INFORMATION);
			return false;
		}
		int answer = MessageDialog.BUTTON_YES;
		if (testAmount > this.maxAmount)
		{
			answer = MessageDialog.showSimpleDialog(Frame.getMainFrame(), "Betrag zu hoch", "Der Betrag " + value
							+ " ist sehr hoch. Wollen Sie ihn trotzdem verwenden?", MessageDialog.TYPE_QUESTION);
		}
		return answer == MessageDialog.BUTTON_YES ? true : false;
	}
	
	private boolean verifyQuantity(int qty)
	{
		int quantity = Math.abs(qty);
		if (quantity > this.maxQuantityRange)
		{
			MessageDialog.showSimpleDialog(Frame.getMainFrame(), "Ungültige Menge", "Die Menge " + qty
							+ " ist ausserhalb des gültigen Bereichs.", MessageDialog.TYPE_INFORMATION);
			return false;
		}
		int answer = MessageDialog.BUTTON_YES;
		if (quantity > this.maxQuantityAmount)
		{
			answer = MessageDialog.showSimpleDialog(Frame.getMainFrame(), "Ausserordentliche Menge", "Die Menge " + qty
							+ " ist sehr hoch. Wollen Sie sie trotzdem verwenden?", MessageDialog.TYPE_QUESTION);
		}
		return answer == MessageDialog.BUTTON_YES ? true : false;
	}
	
	public void setProductGroup(ProductGroup pg)
	{
		Position p = (Position) this.child;
		p.setProductGroup(pg);
		p.galileoBook = pg.type == ProductGroup.TYPE_INCOME;
		p.galileoBooked = false;
		
	}
	
	public ProductGroup getProductGroup()
	{
		return ((Position) this.child).getProductGroup();
	}
	
	public void setQuantity(int qty)
	{
		((Position) this.child).setQuantity(qty);
	}
	
	public void setAmountFC(double amount)
	{
		((Position) this.child).amountFC = amount;
	}
	
	// public void setPrice(Double price) {
	// (( Position) child).setPrice(NumberUtility.round(price,
	// roundFactorAmount));
	// }
	
	public void setPrice(double price, double priceFC)
	{
		((Position) this.child).setPrice(price, priceFC);
	}
	
	public void setDiscount(double discount)
	{
		((Position) this.child).setDiscount(discount);
	}
	
	public void setCurrentTax(CurrentTax currentTax)
	{
		((Position) this.child).setCurrentTax(currentTax);
	}
	
	// public void setExpense(Boolean expense) {
	// (( Position) child).expense = expense;
	// }
	
	public void setType(int type)
	{
		((Position) this.child).type = type;
	}
	
	public void setOptCode(String optCode)
	{
		((Position) this.child).optCode = optCode;
	}
	
	public void setUpdateCustomerAccount(boolean updateCustomerAccount)
	{
		((Position) this.child).updateCustomerAccount = updateCustomerAccount;
	}
	
	public static void setText(Position position)
	{
		String book = ""; //$NON-NLS-1$
		if (!position.author.equals("")) { //$NON-NLS-1$
			book = position.author;
		}
		if (!position.title.equals("")) { //$NON-NLS-1$
			if (book.length() > 0)
			{
				book = book + ": "; //$NON-NLS-1$
			}
			book = book + position.title;
		}
		position.text = book;
		
		if (book.equals("")) { //$NON-NLS-1$
			position.text = position.getProductGroup().name;
		}
	}
	
	public int getPositionState()
	{
		return this.getPosition().getPositionState();
	}
	
	public boolean setData(String value)
	{
		if (LogManager.getLogManager().getLogger("colibri") != null)
		{
			LogManager.getLogManager().getLogger("colibri").log(Level.INFO, "Eingabewert: " + value); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (value.length() >= 7)
		{
			if (this.receiptModel.receiptHasInputPosition() || this.receiptModel.receiptHasWithdrawPosition())
			{
				if (ReceiptModel.getInstance().receiptHasInputPosition())
				{
					String msg = "<html>Sie haben eben eine Geldeinlage in die Kasse getippt. Bitte beenden Sie diese Transaktion, bevor Sie eine neue beginnen.";
					MessageDialog.showInformation(Frame.getMainFrame(), "Geldeinlage", msg,
									MessageDialog.TYPE_INFORMATION);
				}
				if (ReceiptModel.getInstance().receiptHasWithdrawPosition())
				{
					String msg = "<html>Sie haben eben eine Geldentnahme in die Kasse getippt. Bitte beenden Sie diese Transaktion, bevor Sie eine neue beginnen.";
					MessageDialog.showInformation(Frame.getMainFrame(), "Geldeinlage", msg,
									MessageDialog.TYPE_INFORMATION);
				}
			}
			else
			{
				Code128 code128 = Code128.getCode128(value);
				if (code128 != null && code128.isUsed())
				{
					if (PositionModel.setCode128Data(code128, this.getPosition(), value))
						return true;
					else
					{
						if (ProductServer.getInstance().isStockManagement()
										&& Database.getCurrent().equals(Database.getStandard()))
						{
							MessageDialog.showInformation(Frame.getMainFrame(), PositionModel.MESSAGE_TITLE_PRODUCT,
											PositionModel.MESSAGE_BODY_PRODUCT, 0);
							return false;
						}
					}
				}
				
				// if (ProductServer.isUsed() && ProductServer.accept(value))
				if (ProductServer.accept(value))
				{
					boolean[] ret = PositionModel.setGalileoData(this.getPosition(), value);
					if (!ret[0])
					{
						if (!ret[1])
						{
							// 10055
							switch (PositionModel.chooseMessage)
							{
								case 0:
									MessageDialog.showInformation(Frame.getMainFrame(),
													PositionModel.MESSAGE_TITLE_PRODUCT,
													PositionModel.MESSAGE_BODY_PRODUCT, 0);
									break;
								case 1:
									MessageDialog.showInformation(Frame.getMainFrame(),
													PositionModel.MESSAGE_TITLE_CUSTOMER,
													PositionModel.MESSAGE_BODY_CUSTOMER, 0);
									break;
							}
							// 10055
						}
					}
				}
			}
		}
		else if (PositionModel.getCurrentField(this.getPosition()) == PositionModel.FIELD_QUANTITY)
		{
			int quantity = Integer.parseInt(value);
			if (this.verifyQuantity(quantity))
			{
				this.setQuantity(quantity);
			}
		}
		// else if
		// (getCurrentField(getPosition()).equals(PositionModel.FIELD_PRICE)) {
		else
		{
			double amount = 0d;
			try
			{
				amount = Double.parseDouble(value);
				if (this.verifyAmount(amount))
				{
					this.setPrice(amount, amount);
				}
				else
				{
					UserPanel.getCurrent().getNumericBlock().moveValue();
				}
			}
			catch (NumberFormatException e)
			{
			}
		}
		return this.isComplete();
	}
	
	/**
	 * Das Objekt GalileoComServer sucht in der Galileodatenbank nach dem
	 * Artikel, dessen Schluessel mit dem Suchwert (value) uebereinstimmt. Er
	 * gibt <true> zurueck, wenn er einen Datensatz gefunden hat, sonst <false>.
	 * Die gefundenen Daten macht er durch getter-Methoden verfuegbar.
	 * 
	 * @param value
	 *            String, aus dem im Konstruktor des Code128-Objekts die
	 *            Detaildaten ermittelt werden
	 * @return true, wenn Position vollstaendig bestueckt worden ist
	 */
	public static boolean[] setGalileoData(Position position, String value)
	{
		boolean isOpen = false;
		boolean[] result = new boolean[]
		{ true, false };
		// Vorhandene Bindestriche entfernen und String ggf. konvertieren
		String v = PositionModel.convert(ISBN.removeHyphen(value));
		
		if (ProductServer.isUsed())
		{
			if (ProductServer.getInstance().isActive())
			{
				ProductServer galileo = ProductServer.getInstance();
				try
				{
					if (value.startsWith(EAN13.PRE_CUSTOMER_ID) && value.length() == 13)
					{
						return PositionModel.addCustomer(galileo, value, isOpen, result);
					}
					else
					{
						isOpen = galileo.isOpen();
						if (!isOpen)
						{
							galileo.open();
						}
						
						result[0] = false;
						PositionModel.chooseMessage = 0;
						
						if (v.startsWith(EAN13.PRE_ORDERED))
						{
							if (LogManager.getLogManager().getLogger("colibri") != null)
							{
								LogManager.getLogManager()
												.getLogger("colibri").log(Level.INFO, "Bestellten Titel eingegeben"); //$NON-NLS-1$ //$NON-NLS-2$
							} /*
							 * Es handelt sich um einen bestellten Artikel
							 */
							if (position.getPositionState() == Position.STATE_TAKE_BACK)
							{
								/*
								 * Prüfen, ob es sich wirklich um einen
								 * bestellten Artikel handelt (Galileo sucht im
								 * Abholfach und in der Abholfachablage, wo die
								 * bereits abgeholten Bestellungen abgelegt
								 * werden und gibt <code>true<code> zurück, wenn
								 * eine entsprechende Bestellung gefunden wurde.
								 */
								result[0] = galileo.isOrderValid(v);
								// MessageDialog
								// .showInformation(
								// Frame.getMainFrame(),
								// "Kein_Lagertitel",
								//																"<HTML>Der gewählte Titel stammt aus einer Kundenbestellung. <br>Eine allfällige Rückbuchung in Galileo muss manuell vorgenommen werden.", //$NON-NLS-1$
								// 0);
								// position.galileoBook = new Boolean(false);
							}
						}
						
						/*
						 * Falls es sich nicht um einen bereits abgeholten
						 * Artikel handelt, soll der Artikel normal gesucht
						 * werden. Falls es sich um einen abgeholten Artikel
						 * handelt, dann wird folgender Aufruf übersprungen.
						 */
						if (!result[0])
						{
							if (LogManager.getLogManager().getLogger("colibri") != null)
							{
								LogManager.getLogManager()
												.getLogger("colibri").log(Level.INFO, "Titel in Galileo suchen"); //$NON-NLS-1$ //$NON-NLS-2$
							}
							result[0] = galileo.getItem(v);
						}
						
						/*
						 * Falls ein Artikel gefunden wurde, werden nun die
						 * Daten abgefüllt.
						 */
						if (result[0])
						{
							if (LogManager.getLogManager().getLogger("colibri") != null)
							{
								LogManager.getLogManager()
												.getLogger("colibri").log(Level.INFO, "Titeldaten aus Galileo füllen"); //$NON-NLS-1$ //$NON-NLS-2$
							}
							position.productId = v;
							position.productNumber = value;
							result[1] = galileo.setData(position);
							PositionModel.setText(position);
							// if (v.startsWith(EAN13.PRE_ORDERED))
							// {
							// UserPanel.getCurrent().getReceiptModel().updateCustomer(galileo.getCustomerObject());
							// }
						}
						if (!Config.getInstance().getProductServerHold())
						{
							if (!isOpen)
							{
								galileo.close();
							}
						}
						// if (position.getPositionState() ==
						// Position.STATE_TAKE_BACK)
						// {
						// if (position.ordered) return result;
						// }
					}
				}
				catch (ComException e)
				{
					e.printStackTrace();
					// if (ProductServer.getInstance().getUpdate() > 0 &&
					// Database.isSwitchable())
					if (Database.isSwitchable())
					{
						Table.switchDatabase();
						
						/*
						 * 10063 Kunden akzeptieren, bestellte Artikel bei
						 * gestörter Verbindung nicht akzeptieren...
						 */
						if (value.startsWith(EAN13.PRE_CUSTOMER_ID) && value.length() == 13)
						{
							Integer cuId = Integer.valueOf(value.substring(3, 12));
							Customer customer = new Customer(cuId.toString(), "Kunde konnte nicht verifiziert werden",
											false, new Double(Table.DOUBLE_DEFAULT_ZERO));
							// 10206
							UserPanel.getCurrent().getReceiptModel().updateCustomer(customer);
							// position.getReceipt().setCustomer(customer);
							// 10206
							// 10074
							result[0] = true;
							return result;
							// 10074
						}
						// if (value.startsWith(EAN13.PRE_ORDERED)
						// && value.length() == 13)
						// {
						// MessageDialog
						// .showInformation(
						// Frame.getMainFrame(),
						//											"Ungültige Eingabe", //$NON-NLS-1$
						//											"Wegen eines Verbindungsfehlers können zur Zeit \nkeine bestellten Artikel erfasst werden.", //$NON-NLS-1$
						// 0);
						// // 10074
						// result[0] = true;
						// return result;
						// // 10074
						// }
						// 10074
						result[0] = false;
						// 10074
					}
					else
					{
						MessageDialog.showInformation(Frame.getMainFrame(), Messages
										.getString("PositionModel.Verbindungsfehler"), //$NON-NLS-1$
										Messages.getString("PositionModel.Die_Verbindung_zu_Galileo_ist_unterbrochen"), //$NON-NLS-1$
										0);
						result[0] = false;
					}
				}
			}
			else
			{
				// if (ProductServer.getInstance().getUpdate() > 0 &&
				// Database.isSwitchable())
				if (Database.isSwitchable())
				{
					Table.switchDatabase();
				}
				/*
				 * 10063 Kunden akzeptieren, bestellte Artikel bei gestörter
				 * Verbindung nicht akzeptieren...
				 */
				if (value.startsWith(EAN13.PRE_CUSTOMER_ID) && value.length() == 13)
				{
					Integer cuId = Integer.valueOf(value.substring(3, 12));
					Customer customer = new Customer(cuId.toString(), "Kunde konnte nicht verifiziert werden", false,
									new Double(Table.DOUBLE_DEFAULT_ZERO));
					// 10206
					UserPanel.getCurrent().getReceiptModel().updateCustomer(customer);
					position.getReceipt().setCustomer(customer);
					// 10206
					// 10074
					result[0] = true;
					return result;
					// 10074
				}
				// 10078 Alter Zustand wiederhergestellt: Bestellte Bücher
				// können erfasst werden, auch wenn Verbindung zu Galileo nicht
				// besteht.
				// if (value.startsWith(EAN13.PRE_ORDERED) && value.length() ==
				// 13) {
				// MessageDialog.showInformation(Frame.getMainFrame(),
				//												  "Ungültige Eingabe", //$NON-NLS-1$
				//												  "Wegen eines Verbindungsfehlers können zur Zeit \nweder Kunden noch bestellte Artikel erfasst werden.", //$NON-NLS-1$
				// 0);
				// //10074
				// result[0] = true;
				// return result;
				// //10074
				// }
				// 10074
				result[0] = false;
				// 10074
				// 10078
			}
		}
		else
		{
			result[0] = false;
		}
		
		if (!result[0])
		{
			// if (Database.getCurrent().equals(Database.getTemporary()) &&
			// ProductServer.getInstance().getUpdate() > 0)
			if (Database.getCurrent().equals(Database.getTemporary()))
			{
				// if (value.startsWith(EAN13.PRE_ORDERED))
				// {
				// PositionModel.chooseMessage = -1;
				// MessageDialog.showInformation(Frame.getMainFrame(),
				// "Bestellter Titel",
				//									"<HTML>Der gewählte Titel stammt aus einer Kundenbestellung. <br>Im Failover Modus kann dieser nicht erfasst werden.", //$NON-NLS-1$
				// 0);
				// position.galileoBook = new Boolean(false);
				// result[1] = false;
				// return result;
				// }
				if (value.startsWith(EAN13.PRE_CUSTOMER_ID) && value.length() == 13)
				{
					Integer cuId = Integer.valueOf(value.substring(3, 12));
					Customer customer = new Customer(cuId.toString(), "Kunde konnte nicht verifiziert werden", false,
									new Double(Table.DOUBLE_DEFAULT_ZERO));
					// 10206
					UserPanel.getCurrent().getReceiptModel().updateCustomer(customer);
					position.getReceipt().setCustomer(customer);
					// 10206
					// 10074
					result[0] = true;
					return result;
					// 10074
				}
				else
				{
					position.setProductGroup(ProductGroup.getDefaultGroup());
					result[1] = position.getProductGroupId() != null
									&& !position.getProductGroupId().equals(Table.ZERO_VALUE);
					position.productId = v;
					/*
					 * Build 45 Wenn keine Verbindung zu Galileo bestand, dann
					 * wurden die Bestellangaben nicht auf die Position
					 * übertragen.
					 */
					if (position.productId.startsWith(EAN13.PRE_ORDERED))
					{
						position.ordered = true;
						position.orderId = position.productId;
						position.galileoBook = position.getQuantity() != 0;
						result[0] = true;
						return result;
					}
					else
					{
						position.productNumber = value;
						position.galileoBook = true;
					}
					// position.productNumber = value;
					PositionModel.setText(position);
					result[0] = true;
				}
			}
		}
		// 10043 Start
		// position.galileoBook = new Boolean(position.productId.length() > 0);
		position.galileoBook = position.getProductGroup().type == ProductGroup.TYPE_INCOME;
		
		// 10043 End
		return result;
	}
	
	private static boolean[] addCustomer(ProductServer galileo, String value, boolean isOpen, boolean[] result)
	{
		isOpen = galileo.isOpen();
		if (!isOpen)
		{
			galileo.open();
		}
		if (LogManager.getLogManager().getLogger("colibri") != null)
		{
			LogManager.getLogManager().getLogger("colibri").log(Level.INFO, "Kunde mit Nummer " + value + " erfasst"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		PositionModel.chooseMessage = 1;
		Integer tmp = new Integer(0);
		try
		{
			tmp = new Integer(value.substring(3, 12));
		}
		catch (NumberFormatException e)
		{
		}
		if (tmp != new Integer(0))
		{
			if (LogManager.getLogManager().getLogger("colibri") != null)
			{
				LogManager
								.getLogManager()
								.getLogger("colibri").log(Level.INFO, "Kunde mit Nummer " + value + " in Galileo suchen"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			if (galileo.getCustomer(tmp))
			{
				Customer customer = galileo.getCustomerObject();
				customer.setId(tmp.toString());
				// 10206
				UserPanel.getCurrent().getReceiptModel().updateCustomer(customer);
				// position.getReceipt().setCustomer(customer);
				// 10206
				result[0] = true; // 10055
				result[1] = true; // 10055
			}
			else
			{
				// 10206
				UserPanel.getCurrent().getReceiptModel().updateCustomer(new Customer());
				// position.getReceipt().setCustomer(new
				// Customer());
				// 10206
				result[0] = false; // 10055
				result[1] = false; // 10055
			}
		}
		else
		{
			// 10206
			UserPanel.getCurrent().getReceiptModel().updateCustomer(new Customer());
			// position.getReceipt().setCustomer(new
			// Customer());
			// 10206
			result[0] = false; // 10055
			result[1] = false; // 10055
		}
		if (!Config.getInstance().getProductServerHold())
		{
			if (!isOpen)
			{
				galileo.close();
			}
		}
		return result;
	}
	
	public void addInvoicePosition(Boolean paid, double amount, Integer invoice, Date date)
	{
		this.setPosition(Position.getInstance(this.receiptModel.getReceipt(), Tax.getByCode("UF").getCurrentTax(), 1,
						"L"));
		this.getPosition().setPrice(amount, amount);
		this.getPosition().setDiscount(0d);
		this.getPosition().setProductGroup(ProductGroup.getPaidInvoiceGroup());
		this.getPosition().setPayedInvoice(true);
		this.getPosition().setInvoiceNumber(invoice);
		this.getPosition().setInvoiceDate(date);
		this.getPosition().galileoBook = true;
		this.getPosition().galileoBooked = false;
		this.getPosition().text = "Rechnung " + invoice.toString() + " vom "
						+ SimpleDateFormat.getDateInstance().format(date) + " bezahlt: "
						+ DecimalFormat.getCurrencyInstance().format(amount);
	}
	
	/**
	 * Erstellt ein Code128 Objekt. Das Objekt gibt die notwendigen Detaildaten
	 * im aufbereiteten Format zurueck. Die Position wird mit den Daten
	 * bestueckt.
	 * 
	 * @param value
	 *            String, aus dem im KOnstruktor des Code128-Objekts die
	 *            Detaildaten ermittelt werden
	 * @return true, wenn Position vollstaendig bestueckt worden ist
	 */
	public static boolean setCode128Data(Code128 code128, Position position, String value)
	{
		boolean found = false;
		if (!code128.accept(value))
		{
			return false;
		}
		
		if (ProductServer.isUsed())
		{
			if (code128.searchGalileo())
			{
				if (ProductServer.getInstance().isActive())
				{
					found = PositionModel.setGalileoData(position, code128.getArticleCode(value))[0];
					if (found)
					{
						if (!new Boolean(Config.getInstance().getInputDefault().getAttributeValue("clear-price"))
										.booleanValue())
						{
							// 10142 Bedingung eingebaut
							double p = code128.getOrdinalPrice(value);
							position.setPrice(p, p);
						}
						if (position.getProductGroupId().equals(ProductGroup.getDefaultGroup().getId())
										|| position.getProductGroupId() == null
										|| position.getProductGroupId().equals(Table.ZERO_VALUE))
						{
							String galileoId = code128.getProductGroup(value);
							ProductGroup group = ProductGroup.getByGalileoId(galileoId, true);
							if (group.galileoId.equals(galileoId)
											&& !position.getProductGroup().galileoId.equals(galileoId))
							{
								position.setProductGroup(group);
								PositionModel.setText(position);
								position.galileoBook = position.getProductGroup().type == ProductGroup.TYPE_INCOME;
							}
						}
						position.setCurrentTax(Tax.getByCode128Id(code128.getTax(value), false).getCurrentTax());
						return position.isComplete();
					}
				}
				else
				{
					if (!new Boolean(Config.getInstance().getInputDefault().getAttributeValue("clear-price"))
									.booleanValue())
					{
						// 10142 Bedingung eingebaut
						double p = code128.getOrdinalPrice(value);
						position.setPrice(p, p);
					}
					position.productId = code128.getArticleCode(value);
					position.productNumber = value;
					position.galileoBook = true; // 10434
					position.setProductGroup(ProductGroup.getByGalileoId(code128.getProductGroup(value), true));
					position.setCurrentTax(Tax.getByCode128Id(code128.getTax(value), false).getCurrentTax());
					position.updateCustomerAccount = position.getReceipt().getCustomerId().length() > 0;
				}
			}
		}
		if (!ProductServer.getInstance().isStockManagement())
		{
			PositionModel.itemFound = ProductServer.getInstance().isStockManagement() ? found : true;
			position.productId = PositionModel.convert(code128.getArticleCode(value));
			position.setProductGroup(ProductGroup.getByGalileoId(code128.getProductGroup(value), false));
			
			if (ProductServer.isUsed())
			{
				if (position.getProductGroupId() == null || position.getProductGroupId().equals(Table.ZERO_VALUE)
								|| position.getProductGroup().type != ProductGroup.TYPE_INCOME)
				{
					// 10091 if
					// (position.getProductGroupId().equals(Table.ZERO_ID))
					// {
					position.setProductGroup(ProductGroup.getDefaultGroup());
				}
				// 10043 Start
				// position.galileoBook = new
				// Boolean(position.productId.length() >
				// 0);
				position.galileoBook = position.getProductGroup().type == ProductGroup.TYPE_INCOME;
				// 10043 End
			}
			if (!new Boolean(Config.getInstance().getInputDefault().getAttributeValue("clear-price")).booleanValue())
			{
				// 10142 Bedingung eingebaut
				double p = code128.getOrdinalPrice(value);
				position.setPrice(p, p);
			}
			position.setCurrentTax(Tax.getByCode128Id(code128.getTax(value), false).getCurrentTax());
			position.author = ""; //$NON-NLS-1$
			position.title = ""; //$NON-NLS-1$
			position.publisher = ""; //$NON-NLS-1$
			position.updateCustomerAccount = position.getReceipt().getCustomerId().length() > 0;
			
		}
		return position.isComplete();
	}
	
	private static String convert(String value)
	{
		switch (value.length())
		{
			case 7:
				/*
				 * Es handelt sich entweder um eine BZ- oder KNOE-Nummer oder
				 * gar um eine sonstige Lieferantennummer. Einfach so
				 * weitergeben.
				 */
				break;
			case 9:
				/*
				 * Es handelt sich um eine ISBN-Nummer ohne Pruefziffer Die
				 * Pruefziffer muss angehaengt werden.
				 */
				value = ISBN.computeISBN(value);
				break;
			case 10:
				/*
				 * Es handelt sich anscheinend um eine ISBN-Nummer mit
				 * Pruefziffer Der Code kann einfach so weitergegeben werden.
				 */
				break;
			case 13:
				/*
				 * Da keine Bindestriche mehr vorhanden sind, kann es sich
				 * eigentlich nur um einen EAN13-Code handeln...
				 */
				value = EAN13.getGalileoSearchValue(value);
				break;
			default:
				/*
				 * Dieser Code ist zwar unbekannt, aber warum nicht mal
				 * versuchen einen Datensatz zu fischen?
				 */
				break;
		}
		return value;
	}
	
	public boolean onlyPriceIsMissing()
	{
		return ((Position) this.child).onlyPriceMissing();
	}
	
	public boolean isFresh()
	{
		return ((Position) this.child).isFresh();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.client.gui.ChildModel#isCompleted()
	 */
	/**
	 * In diesem Bereich werden die Daten fuer Bildschirm und Display etc
	 * formatiert
	 */
	
	/**
	 * @param int index
	 * @return
	 */
	public static String getValueAt(Position p, int index)
	{
		String value = ""; //$NON-NLS-1$
		switch (index)
		{
			case 0:
				value = PositionModel.getListText(p);
				break;
			case 1:
				value = PositionModel.getDisplayQuantity(p);
				break;
			case 2:
				value = PositionModel.getDisplayPrice(p);
				break;
			case 3:
				value = PositionModel.getDisplayDiscount(p);
				break;
			case 4:
				value = PositionModel.getDisplayAmount(p);
				break;
			case 5:
				value = PositionModel.getDisplayTaxCode(p);
				break;
			case 6:
				value = PositionModel.getDisplayOptCode(p);
				break;
			case 7:
				value = PositionModel.getDisplayUpdateCustomerAccount(p);
				break;
		}
		return value;
	}
	
	public static String getDisplayText(Position position)
	{
		String text = ""; //$NON-NLS-1$
		String book = ""; //$NON-NLS-1$
		String productGroupName = ""; //$NON-NLS-1$
		if (!position.author.equals("")) { //$NON-NLS-1$
			book = position.author;
		}
		if (!position.title.equals("")) { //$NON-NLS-1$
			if (book.length() > 0)
			{
				book = book + ": "; //$NON-NLS-1$
			}
			book = book + position.title;
		}
		if (book.length() == 0) book = position.text;
		
		productGroupName = position.getProductGroup().name;
		if (!productGroupName.equals("")) { //$NON-NLS-1$
			if (!book.equals("")) { //$NON-NLS-1$
				text = productGroupName + " - " + book; //$NON-NLS-1$
			}
			else
			{
				text = productGroupName;
				if (!position.productId.equals("")) { //$NON-NLS-1$
					text += " - " + position.productId; //$NON-NLS-1$
				}
			}
		}
		else
		{
			text = book;
		}
		if (text.equals("") && !position.productId.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
			text = position.productId;
		}
		return text;
	}
	
	public static String getListText(Position position)
	{
		String text = "";
		if (position.productId.equals(""))
		{
			if (position.getProductGroup().paidInvoice)
				text = position.text;
			else
				text = position.getProductGroup().name;
		}
		else
		{
			if (position.author.length() > 0)
			{
				if (position.title.length() > 0)
				{
					text = position.author + ", " + position.title;
				}
				else
				{
					text = position.author;
				}
			}
			else
			{
				if (position.title.length() > 0)
				{
					text = position.title;
				}
				else if (position.text.length() > 0)
				{
					text = position.text;
				}
				else
				{
					text = position.productId;
				}
			}
		}
		return text;
	}
	
	public static String getDisplayQuantity(Position position)
	{
		return NumberUtility.formatInteger(position.getQuantity(), true);
	}
	
	public static String getDisplayPrice(Position position)
	{
		return NumberUtility.formatDouble(position.getPrice(), 2, 2, true);
	}
	
	public static String getDisplayDiscount(Position position)
	{
		return NumberUtility.formatPercent(position.getDiscount(), 0, 6);
	}
	
	public static String getDisplayTaxCode(Position position)
	{
		return PositionModel.getDisplayTaxPercentage(position);
	}
	
	public static String getDisplayTaxPercentage(Position position)
	{
		double percentage = position.getCurrentTax().percentage / 100;
		return NumberUtility.formatPercent(percentage, 0, 3);
	}
	
	public static String getDisplayOptCode(Position position)
	{
		return position.optCode;
	}
	
	public static String getDisplayUpdateCustomerAccount(Position position)
	{
		return position.updateCustomerAccount ? "J" : "";
	}
	
	public static String getDisplayAmount(Position position)
	{
		return NumberUtility.formatDouble(position.getAmount(), 2, 2, true);
	}
	
	public static String getDisplayGrossAmount(Position position)
	{
		return NumberUtility.formatDouble(position.getGrossAmount(), 2, 2, true);
	}
	
	public static String getDisplayDiscountAmount(Position position)
	{
		return NumberUtility.formatDouble(position.getDiscountAmount(), 2, 2, true);
	}
	
	public static String getDisplayProduct(Position position)
	{
		StringBuffer b = new StringBuffer();
		if (position.author != null && position.author.length() > 0)
		{
			b.append(position.author);
		}
		if (position.title != null && position.title.length() > 0)
		{
			if (b.length() > 0)
			{
				b.append(", "); //$NON-NLS-1$
			}
			b.append(position.title);
		}
		if (position.publisher != null && position.publisher.length() > 0)
		{
			if (b.length() > 0)
			{
				b.append(", "); //$NON-NLS-1$
			}
			b.append(position.publisher);
		}
		return b.toString();
	}
	
	public static String getMessage(Position position)
	{
		String s = ""; //$NON-NLS-1$
		if (!PositionModel.itemFound)
		{
			s = Messages.getString("PositionModel.text_32"); //$NON-NLS-1$
			if (ProductServer.isUsed()) s = s + " / " + Messages.getString("PositionModel.text_34"); //$NON-NLS-1$ //$NON-NLS-2$
			if (Code128.isAnyUsed()) s = s + " / " + Messages.getString("PositionModel.text_31"); //$NON-NLS-1$ //$NON-NLS-2$
			s = s + ": ";//$NON-NLS-1$
		}
		else if (position.getQuantity() == 0)
		{
			s = Messages.getString("PositionModel.text_33"); //$NON-NLS-1$
		}
		else if (position.getProductGroupId() == null || position.getProductGroupId().equals(Table.ZERO_VALUE))
		{
			s = Messages.getString("PositionModel.text_32"); //$NON-NLS-1$
			
			if (position.productId.equals(""))
			{
				if (ProductServer.isUsed()) s = s + " / " + Messages.getString("PositionModel.text_34"); //$NON-NLS-1$ //$NON-NLS-2$
				if (Code128.isAnyUsed()) s = s + " / " + Messages.getString("PositionModel.text_31"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else
			{
				s = Messages.getString("PositionModel.Warengruppe_nicht_bekannt._Warengruppe___1"); //$NON-NLS-1$
			}
			s = s + ": ";//$NON-NLS-1$
		}
		else if (position.getPrice() == 0)
		{
			s = Messages.getString("PositionModel.text_35"); //$NON-NLS-1$
		}
		else if (Database.getCurrent().equals(Database.getTemporary()) && ProductServer.getInstance().getUpdate() > 0
						&& position.getProductGroup().galileoId != null
						&& !position.getProductGroup().galileoId.equals("") && position.productId.equals("")) { //$NON-NLS-1$
			s = Messages.getString("PositionModel.text_30"); //$NON-NLS-1$
		}
		else if (position.getCurrentTaxId() == null || position.getCurrentTaxId().equals(Table.ZERO_VALUE))
		{
			s = Messages.getString("PositionModel.text_36"); //$NON-NLS-1$
		}
		else if (position.optCode.equals("") && position.getProductGroup().type != ProductGroup.TYPE_INCOME) { //$NON-NLS-1$
			s = Messages.getString("PositionModel.text_38"); //$NON-NLS-1$
		}
		else if (position.isComplete())
		{
			s = Messages.getString("PositionModel.text_39"); //$NON-NLS-1$
		}
		PositionModel.itemFound = true;
		return s;
	}
	
	public static Color getColor(Position position)
	{
		if (!PositionModel.itemFound)
		{
		}
		else if (position.getQuantity() == 0)
		{
		}
		else if (position.getProductGroupId() == null || position.getProductGroupId().equals(Table.ZERO_VALUE))
		{
			if (position.productId.equals(""))
			{
			}
			else
			{
				return Color.red;
			}
		}
		else if (Database.getCurrent().equals(Database.getTemporary()) && ProductServer.getInstance().getUpdate() > 0
						&& position.productId.equals("")) { //$NON-NLS-1$
		}
		else if (position.getPrice() == 0d)
		{
		}
		else if (position.getCurrentTaxId() == null || position.getCurrentTaxId().equals(Table.ZERO_VALUE))
		{
		}
		else if (position.optCode.equals("") && position.getProductGroup().type != ProductGroup.TYPE_INCOME) { //$NON-NLS-1$
		}
		else if (position.isComplete())
		{
		}
		return Color.black;
	}
	
	public static int getCurrentField(Position position)
	{
		int i = PositionModel.FIELD_EMPTY;
		if (position.getQuantity() == 0)
		{
			i = PositionModel.FIELD_QUANTITY;
		}
		else if (position.getPrice() == 0d)
		{
			i = PositionModel.FIELD_PRICE;
		}
		return i;
	}
	
	// public static String[] getValues(Position position) {
	// String[] columns = new String[columnNames.length];
	// columns[0] = getDisplayQuantity(position);
	// columns[1] = getDisplayPrice(position);
	// columns[2] = getDisplayDiscount(position);
	// columns[3] = getDisplayText(position);
	// columns[4] = getDisplayTaxCode(position);
	// columns[5] = getDisplayOptCode(position);
	// columns[6] = getDisplayUpdateCustomerAccount(position);
	// return columns;
	// }
	
	// public static String[] getColumnNames() {
	// return columnNames;
	// }
	
	public static void initialize()
	{
		PositionModel.defaultOption = ReceiptChildModel.cfg.getDefaultOption();
		PositionModel.defaultTax = ReceiptChildModel.cfg.getDefaultTax().getCurrentTax();
		PositionModel.defaultQuantity = ReceiptChildModel.cfg.getDefaultQuantity();
	}
	
	public static String defaultOption = ""; //$NON-NLS-1$
	public static CurrentTax defaultTax;
	public static int defaultQuantity = 1;
	private static boolean itemFound = true;
	//	private static String[] columnNames = {Messages.getString("PositionModel.Artikel_41"), //$NON-NLS-1$
	//										   	Messages.getString("PositionModel.Menge_42"), //$NON-NLS-1$
	//											Messages.getString("PositionModel.Preis_43"), //$NON-NLS-1$
	//											Messages.getString("PositionModel.Rabatt_44"), //$NON-NLS-1$
	//											Messages.getString("PositionModel.Mwst_45"), //$NON-NLS-1$
	//											Messages.getString("PositionModel.Opt_46")}; //$NON-NLS-1$
	//											"KK"}; //$NON-NLS-1$
	
	public static final int FIELD_EMPTY = 0;
	public static final int FIELD_QUANTITY = 1;
	public static final int FIELD_PRICE = 2;
	
	public static final String MESSAGE_TITLE_CUSTOMER = "Kunde nicht gefunden";
	public static final String MESSAGE_BODY_CUSTOMER = "Der gesuchte Kunde konnte nicht gefunden werden.";
	public static final String MESSAGE_TITLE_PRODUCT = "Artikel nicht gefunden";
	public static final String MESSAGE_BODY_PRODUCT = "Der gesuchte Artikel konnte nicht gefunden werden.";
	
	private static int chooseMessage = 0;
}
