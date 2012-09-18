/*
 * Created on 10.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.client.gui;

import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.apache.ojb.broker.PersistenceBrokerException;

import ch.eugster.pos.client.model.PositionModel;
import ch.eugster.pos.client.model.ReceiptModel;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.db.User;
import ch.eugster.pos.db.Version;
import ch.eugster.pos.devices.drawers.CashDrawer;
import ch.eugster.pos.events.CurrencyChangeEvent;
import ch.eugster.pos.events.CurrencyChangeListener;
import ch.eugster.pos.events.MessageDialog;
import ch.eugster.pos.events.ModeChangeEvent;
import ch.eugster.pos.events.ModeChangeListener;
import ch.eugster.pos.events.ModeChangeRequest;
import ch.eugster.pos.events.ModeDependendAction;
import ch.eugster.pos.events.ReceiptChangeEvent;
import ch.eugster.pos.events.ReceiptChangeListener;
import ch.eugster.pos.events.ShowMessageEvent;
import ch.eugster.pos.events.ShowMessageListener;
import ch.eugster.pos.util.Config;

/**
 * @author Administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class UserPanel extends Panel implements KeyListener, ModeChangeListener, ModeInformationProvider,
				ModeChangeRequest, ReceiptChangeListener, ComponentListener
{
	public static final long serialVersionUID = 0l;
	
	/**
	 * 
	 */
	public UserPanel(TabPanel parent, User user, /* 10170 */Frame frame /* 10170 */)
	{
		super(null, parent);
		// super(new GridBagLayout(), parent);
		// 10170
		this.setMessageListener(frame);
		// 10170
		this.user = user;
		this.init();
	}
	
	private void init()
	{
		this.cfg = Config.getInstance();
		this.setDefaultForeignCurrencies();
		
		UserPanel.maxPrice = this.cfg.getInputDefaultMaxPriceAmount();
		UserPanel.maxPayment = this.cfg.getInputDefaultMaxPaymentAmount();
		
		PositionModel.initialize();
		this.receiptModel = new ReceiptModel(this);
		
		this.leftPanel = new JPanel(new GridLayout(2, 1));
		this.leftside = Config.getInstance().getLayoutLeftWidth();
		this.add(this.leftPanel);
		
		this.coinCounter = new CoinCounter(this);
		this.rightPanel = new JPanel(new GridLayout(2, 1));
		this.inLayout = new CardLayout();
		this.mainRightPanel = new JPanel(this.inLayout);
		this.mainRightPanel.add(this.coinCounter, this.coinCounter.getClass().getName());
		this.mainRightPanel.add(this.rightPanel, this.rightPanel.getClass().getName());
		this.add(this.mainRightPanel);
		
		this.childrenBlock = new ChildrenBlock(this);
		this.productBlock = new ABlockProductGroupsPaymentTypes(this);
		this.numericBlock = new ABlockNumeric(this);
		this.functionBlock = new ABlockFunction(this);
		
		this.receiptModel.getPositionModel().addReceiptChildChangeListener(this.childrenBlock.getPositionBlock());
		this.receiptModel.getPaymentModel().addReceiptChildChangeListener(this.childrenBlock.getPaymentBlock());
		
		this.numericBlock.addModeChangeListener((ModeDependendAction) this.childrenBlock.getPositionBlock()
						.getButton("quantity").getAction()); //$NON-NLS-1$
		this.numericBlock.addPropertyChangeListener(this.coinCounter);
		
		this.addBlock(this.leftPanel, this.cfg.getPosition("top-left")); //$NON-NLS-1$
		this.addBlock(this.leftPanel, this.cfg.getPosition("bottom-left")); //$NON-NLS-1$
		this.addBlock(this.rightPanel, this.cfg.getPosition("top-right")); //$NON-NLS-1$
		this.addBlock(this.rightPanel, this.cfg.getPosition("bottom-right")); //$NON-NLS-1$
		
		this.addKeyListener(this.numericBlock);
		
		this.receiptModel.addModeChangeListener(this);
		this.receiptModel.prepareReceipt();
		
		this.addCurrencyChangeListener(this.childrenBlock.getTotalBlock());
		this.addCurrencyChangeListener(this.parent.getCustomerDisplay());
		this.addCurrencyChangeListener(this.childrenBlock.getReceivedBackBlock()); // *
		this.addCurrencyChangeListener(this.parent.getCustomerDisplay());
		
	}
	
	private void addBlock(JPanel panel, String name)
	{
		if (name.equals("children-block")) { //$NON-NLS-1$
			panel.add(this.childrenBlock);
		}
		if (name.equals("function-block")) { //$NON-NLS-1$
			panel.add(this.functionBlock);
		}
		if (name.equals("numeric-block")) { //$NON-NLS-1$
			panel.add(this.numericBlock);
		}
		else if (name.equals("receipt-block")) { //$NON-NLS-1$
			panel.add(this.productBlock);
		}
	}
	
	public void componentHidden(ComponentEvent e)
	{
		Rectangle rect = this.getBounds();
		int left = rect.width / 100 * this.leftside;
		this.leftPanel.setBounds(0, 0, left - 1, rect.height);
		this.mainRightPanel.setBounds(left, 0, rect.width - left, rect.height);
	}
	
	public void componentMoved(ComponentEvent e)
	{
		Rectangle rect = this.getBounds();
		int left = rect.width / 100 * this.leftside;
		this.leftPanel.setBounds(0, 0, left - 1, rect.height);
		this.mainRightPanel.setBounds(left, 0, rect.width - left, rect.height);
	}
	
	public void componentResized(ComponentEvent e)
	{
		Rectangle rect = this.getBounds();
		int left = rect.width / 100 * this.leftside;
		this.leftPanel.setBounds(0, 0, left - 1, rect.height);
		this.mainRightPanel.setBounds(left, 0, rect.width - left, rect.height);
	}
	
	public void componentShown(ComponentEvent e)
	{
		Rectangle rect = this.getBounds();
		int left = rect.width / 100 * this.leftside;
		this.leftPanel.setBounds(0, 0, left - 1, rect.height);
		this.mainRightPanel.setBounds(left, 0, rect.width - left, rect.height);
	}
	
	public void cleanUp()
	{
		if (Database.getCurrent().equals(Database.getStandard()) && Database.getStandard().isConnected())
		{
			try
			{
				Version.select(Database.getStandard());
			}
			catch (PersistenceBrokerException e)
			{
				return;
			}
			/*
			 * 10435 Vorher wurden alle Belege des Benutzers gelöscht Neu wird
			 * ein angefangener Beleg als parkiert gespeichert
			 */
			if (this.getReceiptModel().getPositionTableModel().getRowCount() > 0)
			{
				this.getReceiptModel().getReceipt().status = Receipt.RECEIPT_STATE_PARKED;
				this.getReceiptModel().getReceipt().removePayments();
				this.getReceiptModel().getReceipt().store(true);
			}
			/*
			 * 10435
			 */
		}
	}
	
	public ReceiptModel getReceiptModel()
	{
		return this.receiptModel;
	}
	
	public TotalBlock getTotalBlock()
	{
		return this.childrenBlock.getTotalBlock();
	}
	
	public ABlockNumeric getNumericBlock()
	{
		return this.numericBlock;
	}
	
	public ChildrenBlock getChildrenBlock()
	{
		return this.childrenBlock;
	}
	
	public PaymentTypeBlock getPaymentTypeBlock()
	{
		return this.productBlock.getPaymentTypeBlock();
	}
	
	public User getUser()
	{
		return this.user;
	}
	
	public void keyPressed(KeyEvent e)
	{
		KeyListener[] l = this.getKeyListeners();
		for (int i = 0; i < l.length; i++)
		{
			l[i].keyPressed(e);
		}
	}
	
	public void receiptChangePerformed(ReceiptChangeEvent e)
	{
		if (Config.getInstance().getSalespointForceSettlement())
		{
			if (Salespoint.getCurrent().currentDate.before(TabPanel.today))
			{
				if (TabPanel.unSettled != 0)
				{
					this.modeChangePerformed(new ModeChangeEvent(UserPanel.CONTEXT_MODE_SETTLEMENT));
					return;
				}
			}
		}
		
		if (e.getEventType().equals(ReceiptModel.RECEIPT_INITIALIZED))
		{
			this.modeChangePerformed(new ModeChangeEvent(UserPanel.CONTEXT_MODE_POS));
		}
	}
	
	public boolean testReceiptSettled()
	{
		/*
		 * 10437 CleanUp eingefügt, Rest auskommentiert
		 */
		this.cleanUp();
		// if (this.getReceiptModel().getPositionTableModel().getRowCount() > 0
		// || this.getReceiptModel().getPaymentTableModel().getRowCount() > 0)
		// {
		// ShowMessageEvent event = new ShowMessageEvent(
		// Messages.getString("UserPanel.Sie_haben_den_aktuellen_Kassiervorgang_noch_nicht_abgeschlossen._n_10")
		// +
		// Messages.getString("UserPanel.Wollen_Sie_ihn_jetzt_noch_beenden__11"),
		// Messages.getString("UserPanel.Kassiervorgang_nicht_abgeschlossen_12"),
		// MessageDialog.TYPE_QUESTION);
		// if (this.sendMessage(event) == MessageDialog.BUTTON_YES)
		// {
		// return false;
		// }
		// }
		/*
		 * 10437
		 */
		return true;
	}
	
	public void modeChangePerformed(ModeChangeEvent e)
	{
		if (Config.getInstance().getSalespointForceSettlement())
		{
			if (Database.getCurrent().equals(Database.getStandard()))
			{
				if (Salespoint.getCurrent().currentDate.before(TabPanel.today))
				{
					if (TabPanel.unSettled == -1)
					{
						TabPanel.unSettled = Receipt.selectCurrentOlderThanToday(Salespoint.getCurrent(),
										TabPanel.today);
					}
					if (TabPanel.unSettled > 0)
					{
						ShowMessageEvent event = new ShowMessageEvent(
										"<html><body>Der Vortag ist noch nicht abgeschlossen. Bitte schliessen Sie die Kasse ab,<br>bevor Sie weiterarbeiten</body></html>",
										"Kassenabschluss", MessageDialog.TYPE_INFORMATION);
						this.sendMessage(event);
						e.setRequestedMode(UserPanel.CONTEXT_MODE_SETTLEMENT);
					}
				}
			}
			else if (Database.getCurrent().equals(Database.getTemporary()))
			{
				if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_SETTLEMENT))
				{
					this.previousMode = this.currentMode;
					this.currentMode = UserPanel.CONTEXT_MODE_NO_CHANGE;
				}
			}
		}
		
		if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_NO_CHANGE))
		{
			return;
		}
		else if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_LOGOFF))
		{
			if (this.testReceiptSettled())
			{
				((TabPanel) this.getParent()).removePanel(this);
			}
			else
			{
				this.fireModeChangeEvent(e);
			}
			return;
		}
		else if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_EXIT))
		{
			this.fireModeChangeEvent(e);
		}
		else
		{
			if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_PREVIOUS))
			{
				Integer state = this.previousMode;
				this.previousMode = this.currentMode;
				this.currentMode = state;
			}
			else if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_CURRENT))
			{
				// DO NOTHING
			}
			else
			{
				this.previousMode = this.currentMode;
				this.currentMode = e.getRequestedMode();
				
			}
			e.setRequestedMode(this.currentMode);
			if (this.currentMode.equals(UserPanel.CONTEXT_MODE_SETTLEMENT))
			{
				if (this.testReceiptSettled())
				{
					this.inLayout.show(this.mainRightPanel, this.coinCounter.getClass().getName());
				}
				else
				{
					Integer tmp = this.previousMode;
					this.previousMode = this.currentMode;
					this.currentMode = tmp;
				}
			}
			else
			{
				this.inLayout.show(this.mainRightPanel, this.rightPanel.getClass().getName());
			}
			
			this.fireModeChangeEvent(e);
			this.getNumericBlock().getEnterButton().requestFocus();
		}
		this.requestFocus(true);
	}
	
	public void fireModeChangeEvent(ModeChangeEvent e)
	{
		ModeChangeListener[] l = (ModeChangeListener[]) this.modeChangeListeners.toArray(new ModeChangeListener[0]);
		for (int i = 0; i < l.length; i++)
		{
			l[i].modeChangePerformed(e);
		}
	}
	
	public boolean addModeChangeListener(ModeChangeListener listener)
	{
		return this.modeChangeListeners.add(listener);
	}
	
	public boolean removeModeChangeListener(ModeChangeListener listener)
	{
		return this.modeChangeListeners.remove(listener);
	}
	
	public Integer getPreviousState()
	{
		return this.previousMode;
	}
	
	public Integer getMode()
	{
		return this.currentMode;
	}
	
	public Config getCfg()
	{
		return this.cfg;
	}
	
	public CoinCounter getCoinCounter()
	{
		return this.coinCounter;
	}
	
	private void setDefaultForeignCurrency(ForeignCurrency foreignCurrency)
	{
		this.mainCurrency = foreignCurrency;
	}
	
	private void setSecondForeignCurrency(ForeignCurrency foreignCurrency)
	{
		this.secondCurrency = foreignCurrency;
	}
	
	private void setDefaultForeignCurrencies()
	{
		if (this.parent.getReceiptPrinter() != null && this.parent.getReceiptPrinter().getPrinter() != null)
		{
			CashDrawer[] cd = this.parent.getReceiptPrinter().getPrinter().getCashDrawers();
			if (cd != null)
			{
				if (cd.length > 0)
				{
					if (cd[0].currency.length() > 0)
					{
						this.setDefaultForeignCurrency(ForeignCurrency.getByCode(cd[0].currency));
					}
					else
					{
						this.setDefaultForeignCurrency(ForeignCurrency.getDefaultCurrency());
					}
					if (cd.length == 2) if (cd[1].currency.length() > 0)
					{
						this.setSecondForeignCurrency(ForeignCurrency.getByCode(cd[1].currency));
					}
				}
				else
				{
					this.setDefaultForeignCurrency(ForeignCurrency.getDefaultCurrency());
				}
			}
			else
			{
				this.setDefaultForeignCurrency(ForeignCurrency.getDefaultCurrency());
			}
		}
		else
		{
			this.setDefaultForeignCurrency(ForeignCurrency.getDefaultCurrency());
		}
		
		this.setCurrentForeignCurrencyToDefault();
	}
	
	public ForeignCurrency getDefaultForeignCurrency()
	{
		return this.mainCurrency;
	}
	
	public ForeignCurrency getSecondForeignCurrency()
	{
		return this.secondCurrency;
	}
	
	public void setCurrentForeignCurrency(ForeignCurrency foreignCurrency)
	{
		if (!foreignCurrency.getId().equals(this.mainCurrency.getId()))
		{
			this.currentCurrency = foreignCurrency;
			this.fireCurrencyChangePerformedEvent();
		}
	}
	
	public void fireCurrencyChangePerformedEvent()
	{
		CurrencyChangeListener[] l = (CurrencyChangeListener[]) this.currencyChangeListeners
						.toArray(new CurrencyChangeListener[0]);
		for (int i = 0; i < l.length; i++)
		{
			l[i].currencyChangePerformed(new CurrencyChangeEvent(this.receiptModel, this.receiptModel.getPaymentModel()
							.getReceiptChild()));
		}
	}
	
	public boolean addCurrencyChangeListener(CurrencyChangeListener listener)
	{
		return this.currencyChangeListeners.add(listener);
	}
	
	public boolean removeCurrencyChangeListener(CurrencyChangeListener listener)
	{
		return this.currencyChangeListeners.remove(listener);
	}
	
	public void setCurrentForeignCurrencyToDefault()
	{
		this.currentCurrency = ForeignCurrency.getDefaultCurrency();
		this.fireCurrencyChangePerformedEvent();
	}
	
	public ForeignCurrency getCurrentForeignCurrency()
	{
		return this.currentCurrency;
	}
	
	public int sendMessage(ShowMessageEvent event)
	{
		if (this.messageListener != null)
		{
			return this.messageListener.showMessage(event);
		}
		else
		{
			return -1;
		}
	}
	
	public void setMessageListener(ShowMessageListener listener)
	{
		this.messageListener = listener;
	}
	
	public static void setCurrent(UserPanel panel)
	{
		UserPanel.current = panel;
	}
	
	public static UserPanel getCurrent()
	{
		return UserPanel.current;
	}
	
	public double getMaxPrice()
	{
		return UserPanel.maxPrice;
	}
	
	public double getMaxPayment()
	{
		return UserPanel.maxPayment;
	}
	
	private int leftside = 50;
	
	private JPanel leftPanel;
	private ChildrenBlock childrenBlock;
	private ABlockProductGroupsPaymentTypes productBlock;
	private ABlockNumeric numericBlock;
	private ABlockFunction functionBlock;
	private CoinCounter coinCounter;
	private JPanel mainRightPanel;
	private CardLayout inLayout;
	private JPanel rightPanel;
	private ReceiptModel receiptModel;
	
	private Integer previousMode;
	private Integer currentMode = ModeChangeEvent.MODE_NO_CHANGE;
	
	private User user;
	private Config cfg;
	private ForeignCurrency currentCurrency;
	private ForeignCurrency mainCurrency;
	private ForeignCurrency secondCurrency;
	
	private ArrayList modeChangeListeners = new ArrayList();
	private ArrayList currencyChangeListeners = new ArrayList();
	private ShowMessageListener messageListener;
	
	private static UserPanel current;
	
	private static double maxPrice = 10000d;
	private static double maxPayment = 10000d;
	
	public static final Integer CONTEXT_MODE_PREVIOUS = new Integer(255);
	
	public static final Integer CONTEXT_MODE_NO_CHANGE = new Integer(0);
	public static final Integer CONTEXT_MODE_POS = new Integer(1);
	public static final Integer CONTEXT_MODE_PAY = new Integer(2);
	public static final Integer CONTEXT_MODE_PARKED_RECEIPT_LIST = new Integer(3);
	public static final Integer CONTEXT_MODE_CURRENT_RECEIPT_LIST = new Integer(4);
	public static final Integer CONTEXT_MODE_LOCK = new Integer(5);
	public static final Integer CONTEXT_MODE_SETTLEMENT = new Integer(6);
	public static final Integer CONTEXT_MODE_CURRENT = new Integer(7);
	
	public static final Integer CONTEXT_MODE_LOGOFF = new Integer(10);
	public static final Integer CONTEXT_MODE_EXIT = new Integer(11);
	
}
