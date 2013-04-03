/*
 * Created on 21.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.client.model;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

import ch.eugster.pos.client.gui.Frame;
import ch.eugster.pos.client.gui.TabPanel;
import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.db.CustomKey;
import ch.eugster.pos.db.Customer;
import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.Payment;
import ch.eugster.pos.db.PaymentType;
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.events.CustomerChangeEvent;
import ch.eugster.pos.events.CustomerChangeListener;
import ch.eugster.pos.events.DeleteAction;
import ch.eugster.pos.events.ModeChangeEvent;
import ch.eugster.pos.events.ModeChangeListener;
import ch.eugster.pos.events.ParkAction;
import ch.eugster.pos.events.PosEvent;
import ch.eugster.pos.events.PosEventListener;
import ch.eugster.pos.events.ReceiptChangeEvent;
import ch.eugster.pos.events.ReceiptChangeListener;
import ch.eugster.pos.events.ReceiptSaveEvent;
import ch.eugster.pos.events.ReceiptSaveListener;
import ch.eugster.pos.events.ReverseAction;
import ch.eugster.pos.events.StoreReceiptAction;
import ch.eugster.pos.events.StoreReceiptVoucherAction;
import ch.eugster.pos.util.Config;
import ch.eugster.pos.util.NumberUtility;
import ch.eugster.pos.util.Serializer;

/**
 * @author administrator
 * 
 */
public class ReceiptModel implements PosEventListener
{
	
	private static int currentTemporaryReceiptNumber = 1;
	
	// private static final String PRE_POSITION_CONSTANT = "1"; // 10193
	// private static final String PRE_POSITION_CONSTANT_FAILOVER = "F"; //
	// 10193
	// private static final NumberFormat salespointNumberFormat = new
	// DecimalFormat("00000"); // 10193
	// private static final NumberFormat receiptNumberFormat = new
	// DecimalFormat("0000000000"); // 10193
	
	/**
	 * Konstruktion
	 */
	public ReceiptModel(UserPanel context)
	{
		super();
		this.init(context);
	}
	
	private void init(UserPanel context)
	{
		ReceiptModel.receiptModel = this;
		
		this.context = context;
		
		this.receipt = Receipt.getReceipt(Salespoint.getCurrent(), context.getUser(),
						ForeignCurrency.getDefaultCurrency());
		
		this.positionTableModel = new PositionTableModel(this);
		this.paymentTableModel = new PaymentTableModel(this);
		
		this.positionModel = new PositionModel(this, this.positionTableModel.getTable());
		this.paymentModel = new PaymentModel(this, this.paymentTableModel.getTable());
		
		this.addCustomerChangeListener(this.positionTableModel);
		this.addCustomerChangeListener(Frame.getMainFrame());
		
		this.addReceiptChangeListener(this.positionTableModel);
		this.addReceiptChangeListener(this.paymentTableModel);
		
		this.positionTableModel.addReceiptChangeListener(this.positionModel);
		this.paymentTableModel.addReceiptChangeListener(this.paymentModel);
		
		this.prepareReceipt();
		
	}
	
	/**
	 * Initialisierungen
	 * 
	 */
	
	public UserPanel getContext()
	{
		return this.context;
	}
	
	public static ReceiptModel getInstance()
	{
		return ReceiptModel.receiptModel;
	}
	
	public void prepareReceipt()
	{
		this.context.setCurrentForeignCurrencyToDefault();
		this.setReceipt(Receipt.getReceipt(Salespoint.getCurrent(), this.context.getUser(),
						ForeignCurrency.getDefaultCurrency()));
	}
	
	public void setReceipt(Receipt receipt)
	{
		this.receipt = receipt;
		
		this.positionTableModel.setReceipt(receipt);
		this.paymentTableModel.setReceipt(receipt);
		
		this.positionModel.setPosition();
		this.paymentModel.setPayment();
		
		this.fireReceiptChangeEvent(new ReceiptChangeEvent(this, ReceiptModel.RECEIPT_INITIALIZED));
		
		this.fireModeChangeEvent(new ModeChangeEvent(UserPanel.CONTEXT_MODE_POS));
		
		this.fireCustomerChangeEvent(receipt);
		
		// this.mode = ReceiptModel.RECEIPT_INITIALIZED;
	}
	
	public Receipt getReceipt()
	{
		return this.receipt;
	}
	
	/**
	 * Receiptbezogene Abfragen
	 * 
	 */
	
	public boolean isBalanced()
	{
		return this.receipt.getAmount() == this.receipt.getPayment();
	}
	
	public double getDifference()
	{
		double diff = this.receipt.getAmount() - this.receipt.getPayment();
		NumberFormat nf = NumberFormat.getCurrencyInstance();
		nf.setCurrency(ForeignCurrency.getDefaultCurrency().getCurrency());
		return NumberUtility.round(diff, nf.getMaximumFractionDigits());
	}
	
	public double getDifferenceFC(ForeignCurrency fc)
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance();
		nf.setCurrency(fc.getCurrency());
		
		double diff = this.receipt.getAmount() - this.receipt.getPayment();
		return NumberUtility.round(diff / this.getContext().getCurrentForeignCurrency().quotation,
						nf.getMaximumFractionDigits());
	}
	
	public boolean balance()
	{
		return this.receipt.getAmount() <= NumberUtility.round(this.receipt.getPayment(), 2);
	}
	
	public int getState()
	{
		return this.getReceipt().status;
	}
	
	/**
	 * Schnittstelle zur aktuellen Position
	 * 
	 * @return PositionModel
	 */
	public PositionModel getPositionModel()
	{
		return this.positionModel;
	}
	
	public int getCurrentPositionField()
	{
		return PositionModel.getCurrentField(this.getPositionModel().getPosition());
	}
	
	public int getPositionState()
	{
		return this.getPositionModel().getState();
	}
	
	/**
	 * Schnittstelle zur aktuellen Zahlung
	 * 
	 * @return PaymentModel
	 */
	public PaymentModel getPaymentModel()
	{
		return this.paymentModel;
	}
	
	public Integer getCurrentPaymentField()
	{
		return PaymentModel.getCurrentField(this.getPaymentModel().getPayment());
	}
	
	public int getPaymentState()
	{
		return this.getPaymentModel().getState();
	}
	
	/**
	 * Schnittstelle zur aktuellen Positionenliste
	 * 
	 * @return PositionTableModel
	 */
	public PositionTableModel getPositionTableModel()
	{
		return this.positionTableModel;
	}
	
	public int getPositionTableState()
	{
		return this.getPositionTableModel().getState();
	}
	
	/**
	 * Schnittstelle zur aktuellen Paymentliste
	 * 
	 * @return PaymentTableModel
	 */
	public PaymentTableModel getPaymentTableModel()
	{
		return this.paymentTableModel;
	}
	
	public int getPaymentTableState()
	{
		return this.getPaymentTableModel().getState();
	}
	
	public boolean receiptHasInputPosition()
	{
		boolean inputPosition = false;
		if (this.context.getReceiptModel().getPositionTableModel().getRowCount() > 0)
		{
			Receipt receipt = this.context.getReceiptModel().getReceipt();
			Position[] positions = receipt.getPositionsAsArray();
			for (Position position : positions)
			{
				int type = position.getProductGroup().type;
				if (type == ProductGroup.TYPE_INPUT)
				{
					inputPosition = true;
				}
			}
		}
		return inputPosition;
	}
	
	public boolean receiptHasWithdrawPosition()
	{
		boolean withdrawPosition = false;
		if (this.context.getReceiptModel().getPositionTableModel().getRowCount() > 0)
		{
			Receipt receipt = this.context.getReceiptModel().getReceipt();
			Position[] positions = receipt.getPositionsAsArray();
			for (Position position : positions)
			{
				int type = position.getProductGroup().type;
				if (type == ProductGroup.TYPE_WITHDRAW)
				{
					withdrawPosition = true;
				}
			}
		}
		return withdrawPosition;
	}
	
	/**
	 * Aktionen
	 */
	public void posEventPerformed(PosEvent e)
	{
		if (e.getPosAction() instanceof StoreReceiptAction)
		{
			this.storeReceipt(e);
		}
		else
		{
			if (e.getPosAction() instanceof ParkAction)
			{
				this.receipt.status = Receipt.RECEIPT_STATE_PARKED;
				this.receipt.removePayments();
				this.receipt.store(true);
				this.addReceiptChangeListener(this.context.getChildrenBlock().getTotalBlock());
				this.addReceiptChangeListener(this.context.getChildrenBlock().getReceivedBackBlock()); // *
				this.prepareReceipt();
				this.removeReceiptChangeListener(this.context.getChildrenBlock().getReceivedBackBlock()); // *
				this.removeReceiptChangeListener(this.context.getChildrenBlock().getTotalBlock());
			}
			else if (e.getPosAction() instanceof ReverseAction)
			{
				this.receipt.status = Receipt.RECEIPT_STATE_REVERSED;
				this.receipt.store(false, true);
				this.addReceiptChangeListener(this.context.getChildrenBlock().getTotalBlock());
				this.addReceiptChangeListener(this.context.getChildrenBlock().getReceivedBackBlock()); // *
				this.prepareReceipt();
				this.removeReceiptChangeListener(this.context.getChildrenBlock().getReceivedBackBlock()); // *
				this.removeReceiptChangeListener(this.context.getChildrenBlock().getTotalBlock());
			}
			else if (e.getPosAction() instanceof DeleteAction)
			{
				this.addReceiptChangeListener(this.context.getChildrenBlock().getTotalBlock());
				this.addReceiptChangeListener(this.context.getChildrenBlock().getReceivedBackBlock()); // *
				this.prepareReceipt();
				this.removeReceiptChangeListener(this.context.getChildrenBlock().getReceivedBackBlock()); // *
				this.removeReceiptChangeListener(this.context.getChildrenBlock().getTotalBlock());
			}
		}
	}
	
	public void updateCustomer(Customer customer)
	{
		this.getReceipt().setCustomer(customer);
		this.fireCustomerChangeEvent(this.getReceipt());
	}
	
	private void storeReceipt(PosEvent e)
	{
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Start: Beleg " + this.receipt.getNumber() + " speichern...");
		PaymentType paymentType = null;
		if (e.getPosAction() instanceof StoreReceiptAction)
		{
			StoreReceiptAction action = (StoreReceiptAction) e.getPosAction();
			CustomKey key = (CustomKey) action.getKey();
			paymentType = key.getPaymentType();
			if (paymentType == null) paymentType = PaymentType.getPaymentTypeCash();
		}
		else
		{
			return;
		}
		
		/*
		 * Prüfen des Belegsaldos
		 */
		if (this.balance())
		{
			if (!this.isBalanced())
			{
				
				double diff = NumberUtility.round(
								Math.abs(this.getReceipt().getAmount()) - Math.abs(this.getReceipt().getPayment()), 2);
				if (diff != 0)
				{
					this.receipt.addPayment(this.computeBackMoney(paymentType));
				}
			}
			this.receipt.amount = this.receipt.getAmount();
			
			/**
			 * Vor dem Abspeichern den status auf SERIALIZED setzen
			 */
			this.receipt.status = Receipt.RECEIPT_STATE_SERIALIZED;
			
			/**
			 * Beleg speichern Hier wird vor dem Speichern das effektive
			 * Speicherungsdatum und die Speicherungszeit gesetzt. Das muss hier
			 * geschehen. Der Versuch, es direkt in der Methode
			 * 'receipt.store()' zu setzen, ist falsch, da diese Methode auch
			 * aufgerufen wird, wenn z.B. der Beleg abgerechnet wird
			 * (Tagesabrechnung) oder beim Importieren etc.
			 */
			DBResult result = this.receipt.store(true, true);
			if (result.getErrorCode() == 0)
			{
				if (!Database.getCurrent().getCode().equals("tut"))
				{
					Serializer.getInstance().writeReceipt(this.receipt);
				}
			}
			
			/*
			 * Falls die Speicherung erfolgreich war, wird der Beleg jetzt
			 * ausgedruckt
			 */
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Beleg gespeichert. Beleg wird gedruckt...");
			if (result.getErrorCode() == 0)
			{
				this.fireReceiptSaveEvent(this.receipt);
				if (this.context.getParent() instanceof TabPanel)
				{
					Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Check Belegdrucker aktiv...");
					TabPanel parent = (TabPanel) this.context.getParent();
					if (parent.getReceiptPrinter().getPrinter() != null)
					{
						Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Belegdrucker gefunden.");
						/*
						 * Abhängig von der Währung, mit der bezahlt wurde, wird
						 * nun die Schublade geöffnet, nämlich: 1. Wenn die
						 * Währung mit der Landeswährung NICHT übereinstimmt,
						 * dann wird geprüft, ob es sich um die Währung handelt,
						 * die als Hauptwährung festgelegt ist. 1.a) Wenn ja,
						 * wird Schublade 2 geöffnet, 1.b) sonst Schublade 1
						 * 
						 * 2. Wenn mit Landeswährung bezahlt wurde, wird
						 * Schublade 1 geöffnet
						 */
						if (this.receipt.openCashdrawer())
						{
							Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Schublade öffnen...");
							// 10226
							boolean[] openCashDrawer = new boolean[]
							{ false, false };
							Payment[] payments = this.receipt.getPaymentsAsArray();
							for (Payment payment : payments)
							{
								if (payment.getForeignCurrency().getId()
												.equals(ForeignCurrency.getDefaultCurrency().getId()))
								{
									openCashDrawer[0] = true;
									System.out.println("Leitwährungskasse geöffnet.");
									Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Leitwährungskasse geöffnet.");
								}
								else
								{
									openCashDrawer[1] = true;
									Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Zweitwährungskasse geöffnet.");
									System.out.println("Zweitwährungskasse geöffnet.");
								}
							}
							for (int i = 0; i < openCashDrawer.length; i++)
								if (openCashDrawer[i]) parent.getReceiptPrinter().openCashDrawer(i);
							// if
							// (context.getDefaultForeignCurrency().getId().equals
							// (ForeignCurrency.getDefaultCurrency().getId())) {
							// parent.getReceiptPrinter().openCashDrawer(0);
							// }
							// else {
							// if
							// (context.getCurrentForeignCurrency().getId().equals
							// (context.getDefaultForeignCurrency().getId())) {
							// parent.getReceiptPrinter().openCashDrawer(1);
							// }
							// else {
							// parent.getReceiptPrinter().openCashDrawer(0);
							// }
							// }
							// 10226
						}
						
						if (Config.getInstance().getReceipt().getAttributeValue("automatic-print").equals("true"))
						{
							Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Beleg wird gedruckt...");
							parent.getReceiptPrinter().print(parent.getReceiptPrinter().getPrinter(), this.receipt);
							
							if (e.getPosAction() instanceof StoreReceiptVoucherAction)
							{
								Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Gutschein wird gedruckt...");
								parent.getReceiptPrinter().printVoucher(parent.getReceiptPrinter().getPrinter(),
												this.receipt);
							}
							
							Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Check automatischer Belegdruck: JA");
							if (Config.getInstance().getReceipt().getAttributeValue("take-back-print-twice")
											.equals("true"))
							{
								Collection positions = this.receipt.getPositions();
								Iterator iterator = positions.iterator();
								while (iterator.hasNext())
								{
									Position position = (Position) iterator.next();
									if (position.getProductGroup().type != ProductGroup.TYPE_INPUT
													&& position.getProductGroup().type != ProductGroup.TYPE_WITHDRAW)
									{
										if (position.getQuantity() < 0)
										{
											Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Beleg wird gedruckt.");
											parent.getReceiptPrinter().print(parent.getReceiptPrinter().getPrinter(),
															this.receipt);
											break;
										}
									}
								}
							}
							// }
							// parent.getReceiptPrinter().print(parent.getReceiptPrinter().getPrinter(),
							// this.receipt);
							// if (e.getPosAction() instanceof
							// StoreReceiptVoucherAction)
							// {
							// parent.getReceiptPrinter().printVoucher(parent.getReceiptPrinter().getPrinter(),
							// this.receipt);
							// }
						}
					}
				}
				if (Config.getInstance().getTotalBlockHoldValues())
				{
					this.getPositionTableModel().removeReceiptChangeListener(
									this.context.getChildrenBlock().getTotalBlock());
					this.getPositionTableModel().removeReceiptChangeListener(
									this.context.getChildrenBlock().getReceivedBackBlock());
					this.getPaymentTableModel().removeReceiptChangeListener(
									this.context.getChildrenBlock().getTotalBlock());
					this.getPaymentTableModel().removeReceiptChangeListener(
									this.context.getChildrenBlock().getReceivedBackBlock());
					this.context.removeCurrencyChangeListener(this.context.getChildrenBlock().getTotalBlock());
					this.context.removeCurrencyChangeListener(this.context.getChildrenBlock().getReceivedBackBlock());
					this.removeReceiptChangeListener(this.context.getChildrenBlock().getReceivedBackBlock()); // *
					this.removeReceiptChangeListener(this.context.getChildrenBlock().getTotalBlock());
				}
				this.prepareReceipt();
				if (Config.getInstance().getTotalBlockHoldValues())
				{
					this.addReceiptChangeListener(this.context.getChildrenBlock().getTotalBlock());
					this.addReceiptChangeListener(this.context.getChildrenBlock().getReceivedBackBlock()); // *
					this.context.addCurrencyChangeListener(this.context.getChildrenBlock().getTotalBlock());
					this.context.addCurrencyChangeListener(this.context.getChildrenBlock().getReceivedBackBlock());
					this.getPositionTableModel().addReceiptChangeListener(
									this.context.getChildrenBlock().getTotalBlock());
					this.getPositionTableModel().addReceiptChangeListener(
									this.context.getChildrenBlock().getReceivedBackBlock());
					this.getPaymentTableModel().addReceiptChangeListener(
									this.context.getChildrenBlock().getTotalBlock());
					this.getPaymentTableModel().addReceiptChangeListener(
									this.context.getChildrenBlock().getReceivedBackBlock());
				}
			}
		}
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Ende: Beleg " + this.receipt.getNumber() + " speichern...");
	}
	
	private Payment computeBackMoney(PaymentType paymentType)
	{
		Payment p = Payment.getInstance(this.receipt, paymentType);
		
		/**
		 * 
		 * Der Totalbetrag der Fremdwährung in Position.amountFC Achtung: Hier
		 * ist nur ein Betrag drin, wenn es sich um eine Geldentnahme oder
		 * -einlage in Fremdwährung handelt.
		 */
		double totalAmountFC = NumberUtility.round(this.getReceipt().getAmountFC(p.getForeignCurrency()),
						p.getForeignCurrency().roundFactor);
		
		/**
		 * Das ist die 'normale' Summe der Positionen
		 */
		double totalAmount = this.getReceipt().getAmount();
		
		/**
		 * Das ist die 'normale' Summe der Zahlungen.
		 */
		double totalPayment = this.getReceipt().getPayment();
		
		/**
		 * Diese Variable soll die Summe der Fremdwährungsbeträge in den
		 * Zahlungen, zurückgerechnet in die Landeswährung, enthalten. Damit
		 * soll die Rundungsproblematik entschärft werden.
		 */
		double allPaymentsFC = 0d;
		Payment[] payments = this.getReceipt().getPaymentsAsArray();
		for (Payment payment : payments)
		{
			allPaymentsFC += NumberUtility.round(payment.getAmountFC() * payment.getQuotation(),
							payment.getRoundFactorFC());
		}
		double paymentFC = NumberUtility.round(allPaymentsFC / p.getForeignCurrency().quotation,
						p.getForeignCurrency().roundFactor);
		/**
		 * back soll die Differenz der Positions- und der aus der Fremdwährung
		 * zurückgerechneten Zahlungssumme enthalten. Dies, damit auch die
		 * Fremdwährung stimmt.
		 */
		double back = 0d;
		if (p.getForeignCurrency().getId().equals(ForeignCurrency.getDefaultCurrency().getId()))
			back = totalAmount - totalPayment;
		else
			back = totalAmountFC - paymentFC;
		
		p.setBack(true);
		double amount = NumberUtility.round(totalAmount - totalPayment, p.getRoundFactor());
		double amountFC = NumberUtility.round(back, p.getRoundFactorFC());
		p.setAmount(amount, amountFC);
		
		return p;
	}
	
	public void fireReceiptChangeEvent(ReceiptChangeEvent e)
	{
		ReceiptChangeListener[] l = (ReceiptChangeListener[]) this.receiptChangeListeners
						.toArray(new ReceiptChangeListener[0]);
		for (ReceiptChangeListener element : l)
		{
			element.receiptChangePerformed(e);
		}
	}
	
	public boolean addReceiptChangeListener(ReceiptChangeListener l)
	{
		return this.receiptChangeListeners.add(l);
	}
	
	public boolean removeReceiptChangeListener(ReceiptChangeListener l)
	{
		return this.receiptChangeListeners.remove(l);
	}
	
	protected void fireReceiptSaveEvent(Receipt receipt)
	{
		ReceiptSaveListener[] l = (ReceiptSaveListener[]) this.receiptSaveListeners.toArray(new ReceiptSaveListener[0]);
		ReceiptSaveEvent event = new ReceiptSaveEvent(receipt);
		for (ReceiptSaveListener element : l)
		{
			element.receiptSaved(event);
		}
	}
	
	public boolean addReceiptSaveListener(ReceiptSaveListener l)
	{
		if (!this.receiptSaveListeners.contains(l))
		{
			return this.receiptSaveListeners.add(l);
		}
		else
		{
			return false;
		}
	}
	
	public boolean removeReceiptSaveListener(ReceiptSaveListener l)
	{
		if (this.receiptSaveListeners.contains(l))
		{
			return this.receiptSaveListeners.remove(l);
		}
		else
		{
			return true;
		}
	}
	
	protected void fireModeChangeEvent(ModeChangeEvent e)
	{
		ModeChangeListener[] l = (ModeChangeListener[]) this.modeChangeListeners.toArray(new ModeChangeListener[0]);
		for (ModeChangeListener element : l)
		{
			element.modeChangePerformed(e);
		}
	}
	
	public boolean addModeChangeListener(ModeChangeListener l)
	{
		return this.modeChangeListeners.add(l);
	}
	
	public boolean removeModeChangeListener(ModeChangeListener l)
	{
		return this.modeChangeListeners.remove(l);
	}
	
	public void addCustomerChangeListener(CustomerChangeListener l)
	{
		if (!this.customerChangeListeners.contains(l))
		{
			this.customerChangeListeners.add(l);
		}
	}
	
	public void removeCustomerChangeListener(CustomerChangeListener l)
	{
		if (this.customerChangeListeners.contains(l))
		{
			this.customerChangeListeners.remove(l);
		}
	}
	
	public void fireCustomerChangeEvent(Receipt receipt)
	{
		CustomerChangeEvent event = new CustomerChangeEvent(receipt.getCustomer());
		CustomerChangeListener[] listeners = (CustomerChangeListener[]) this.customerChangeListeners
						.toArray(new CustomerChangeListener[0]);
		for (CustomerChangeListener listener : listeners)
		{
			listener.customerChanged(event);
		}
	}
	
	public static int getCurrentTemporaryReceiptNumber()
	{
		return ReceiptModel.currentTemporaryReceiptNumber++;
	}
	
	private Receipt receipt;
	private PositionModel positionModel;
	private PaymentModel paymentModel;
	private PositionTableModel positionTableModel;
	private PaymentTableModel paymentTableModel;
	private UserPanel context;
	// private Integer mode;
	private ArrayList receiptChangeListeners = new ArrayList();
	private ArrayList modeChangeListeners = new ArrayList();
	private ArrayList receiptSaveListeners = new ArrayList();
	private ArrayList customerChangeListeners = new ArrayList();
	
	private static ReceiptModel receiptModel;
	
	public static final Integer RECEIPT_INITIALIZED = new Integer(101);
	public static final Integer RECEIPT_CHILD_ADDED = new Integer(102);
	public static final Integer RECEIPT_CHILD_CHANGED = new Integer(103);
	public static final Integer RECEIPT_CHILD_REMOVED = new Integer(104);
	public static final Integer RECEIPT_CUSTOMER_SET = new Integer(105);
}
