/*
 * Created on 10.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdom.Element;

import ch.eugster.pos.ExceptionThrownDelegate;
import ch.eugster.pos.Messages;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.Table;
import ch.eugster.pos.db.User;
import ch.eugster.pos.devices.displays.CustomerDisplay;
import ch.eugster.pos.events.LoginEvent;
import ch.eugster.pos.events.LoginListener;
import ch.eugster.pos.events.ModeChangeEvent;
import ch.eugster.pos.events.ModeChangeListener;
import ch.eugster.pos.printing.ReceiptPrinter;
import ch.eugster.pos.product.ProductServer;
import ch.eugster.pos.util.Config;

/**
 * @author Administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TabPanel extends JTabbedPane implements LoginListener, ChangeListener, KeyListener, ActionListener,
				ModeChangeListener
{
	private static final long serialVersionUID = 0l;
	
	/**
	 * 
	 */
	public TabPanel()
	{
		super();
		this.init();
	}
	
	/**
	 * @param tabPlacement
	 */
	public TabPanel(int tabPlacement)
	{
		super(tabPlacement);
		this.init();
	}
	
	/**
	 * @param tabPlacement
	 * @param tabLayoutPolicy
	 */
	public TabPanel(int tabPlacement, int tabLayoutPolicy)
	{
		super(tabPlacement, tabLayoutPolicy);
		this.init();
	}
	
	private void init()
	{
		this.loadPrinter();
		this.loadDisplay();
		
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		TabPanel.today = new Timestamp(calendar.getTimeInMillis());
		
		this.fg = Config.getInstance().getTabPanelColorForeground();
		this.bg = Config.getInstance().getTabPanelColorBackground();
		//		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(Messages.getString("TabPanel.Initialisierung_TabPanel._2")); //$NON-NLS-1$ //$NON-NLS-2$
		
		//		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(Messages.getString("TabPanel.Konfiguration_wird_gelesen._4")); //$NON-NLS-1$ //$NON-NLS-2$
		Config config = Config.getInstance();
		this.setFocusable(false);
		this.setFont(this.getFont().deriveFont(config.getFontStyle(config.getTabPanelFont()),
						config.getFontSize(config.getTabPanelFont())));
		
		//		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(Messages.getString("TabPanel.ClockPanel_wird_initialisiert._6")); //$NON-NLS-1$ //$NON-NLS-2$
		this.clockPanel = new ClockPanel(this);
		this.clockPanel.setEnabled(false);
		String tabText = Database.getCurrent().equals(Database.getTutorial()) ? Messages
						.getString("TabPanel.Schulungsmodus_7") : //$NON-NLS-1$
						this.dateFormat.format(new Date()) + " " + this.timeFormat.format(new Date()); //$NON-NLS-1$
		this.addTab(tabText, this.clockPanel);
		this.dateFormat.setCalendar(Calendar.getInstance());
		this.timeFormat.setCalendar(Calendar.getInstance());
		this.clockPanel.getTimer().addActionListener(this);
		ProductServer.addStateChangeListener(this.clockPanel);
		Database.addStateChangeListener(this.clockPanel);
		ExceptionThrownDelegate.addExceptionThrownListener(this.clockPanel);
		this.clockPanel.updateStates();
		
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
						Messages.getString("TabPanel.LoginPanel_wird_initialisiert._10")); //$NON-NLS-1$ 
		this.loginPanel = new LoginPanel(this);
		this.addChangeListener(this);
		this.addTab(this.loginTitle, this.loginPanel);
		this.setSelectedComponent(this.loginPanel);
		this.loginPanel.addLoginListener(this);
	}
	
	protected void loadPrinter()
	{
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
						Messages.getString("TabPanel.Bondrucker_wird_initialisiert._12")); //$NON-NLS-1$ 
		Element printer = Config.getInstance().getPosPrinter();
		this.receiptPrinter = ReceiptPrinter.getInstance(printer);
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
						Messages.getString("TabPanel.Bondrucker_sind_initialisiert._14")); //$NON-NLS-1$ 
	}
	
	protected void loadDisplay()
	{
		//		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(Messages.getString("TabPanel.Kundendisplays_werden_initialisiert._16")); //$NON-NLS-1$ //$NON-NLS-2$
		Element display = Config.getInstance().getCustomerDisplay();
		this.customerDisplay = new CustomerDisplay(display);
		this.showClosedText();
	}
	
	public void showWelcomeText()
	{
		this.customerDisplay.clear(this.customerDisplay.getDisplay());
		String text = Config.getInstance().getCustomerDisplayGetWelcomeText();
		boolean scroll = Config.getInstance().getCustomerDisplayGetWelcomeTextScroll();
		if (scroll)
		{
			this.customerDisplay.scrollUpperLine(this.customerDisplay.getDisplay(), text);
		}
		else
		{
			this.customerDisplay.write(this.customerDisplay.getDisplay(), text);
		}
	}
	
	public void showClosedText()
	{
		this.customerDisplay.clear(this.customerDisplay.getDisplay());
		String text = Config.getInstance().getCustomerDisplayGetClosedText();
		boolean scroll = Config.getInstance().getCustomerDisplayGetClosedTextScroll();
		if (scroll)
		{
			this.customerDisplay.scrollUpperLine(this.customerDisplay.getDisplay(), text);
		}
		else
		{
			this.customerDisplay.write(this.customerDisplay.getDisplay(), text);
		}
	}
	
	public void addUser(User user)
	{
		UserPanel panel = new UserPanel(this, user, /* 10170 */Frame.getMainFrame() /* 10170 */);
		this.addComponentListener(panel);
		// 10170
		// panel.setMessageListener(Frame.getMainFrame());
		// 10170
		this.setVisible(false);
		this.setVisible(true);
		this.addTab(user.username, panel);
		this.setForegroundAt(this.getSelectedIndex(), this.fg);
	}
	
	public void removePanel(Panel panel)
	{
		panel.cleanUp();
		if (panel instanceof UserPanel)
		{
			UserPanel p = (UserPanel) panel;
			this.removeComponentListener(p);
			p.setMessageListener(null);
		}
		this.unsetCurrentPanel();
		this.remove(panel);
		this.setCurrentPanel();
	}
	
	public void stateChanged(ChangeEvent e)
	{
		this.unsetCurrentPanel();
		this.setCurrentPanel();
	}
	
	private void setCurrentPanel()
	{
		this.currentPanel = (Panel) this.getSelectedComponent();
		this.addKeyEventSenders(this.currentPanel);
		if (this.currentPanel instanceof UserPanel)
		{
			User.setCurrentUser(((UserPanel) this.currentPanel).getUser());
			UserPanel.setCurrent((UserPanel) this.currentPanel);
			((UserPanel) this.currentPanel).getReceiptModel().addReceiptChangeListener(this.customerDisplay);
			((UserPanel) this.currentPanel).getReceiptModel().getPositionTableModel()
							.addReceiptChangeListener(this.customerDisplay);
			((UserPanel) this.currentPanel).getReceiptModel().getPaymentTableModel()
							.addReceiptChangeListener(this.customerDisplay);
			((UserPanel) this.currentPanel).addModeChangeListener(this);
			Table.addModeChangeListener((UserPanel) this.currentPanel);
			UserPanel.getCurrent().getReceiptModel()
							.fireCustomerChangeEvent(UserPanel.getCurrent().getReceiptModel().getReceipt());
			this.showWelcomeText();
		}
		else
		{
			if (UserPanel.getCurrent() != null)
			{
				UserPanel.getCurrent().getReceiptModel()
								.fireCustomerChangeEvent(UserPanel.getCurrent().getReceiptModel().getReceipt());
			}
			this.showClosedText();
		}
		this.setForegroundAt(this.getSelectedIndex(), this.fg);
	}
	
	public void unsetCurrentPanel()
	{
		if (!(this.currentPanel == null))
		{
			Component[] comps = this.getComponents();
			for (int i = 0; i < comps.length; i++)
			{
				if (comps[i].equals(this.currentPanel)) this.setForegroundAt(i, this.bg);
			}
			this.removeKeyEventSenders(this.currentPanel);
			if (this.currentPanel instanceof UserPanel)
			{
				Table.removeModeChangeListener((UserPanel) this.currentPanel);
				((UserPanel) this.currentPanel).removeModeChangeListener(this);
				((UserPanel) this.currentPanel).getReceiptModel().removeReceiptChangeListener(this.customerDisplay);
				((UserPanel) this.currentPanel).getReceiptModel().getPositionTableModel()
								.removeReceiptChangeListener(this.customerDisplay);
				((UserPanel) this.currentPanel).getReceiptModel().getPaymentTableModel()
								.removeReceiptChangeListener(this.customerDisplay);
				UserPanel.setCurrent(null);
			}
			/*
			 * 10437
			 */
			this.currentPanel = null;
			/*
			 * 10437
			 */
		}
	}
	
	public void addKeyEventSenders(Component c)
	{
		if (!(c == null))
		{
			if (!(c instanceof KeyListener))
			{
				c.addKeyListener(this);
				if (c instanceof Container)
				{
					Component[] children = ((Container) c).getComponents();
					for (Component element : children)
					{
						this.addKeyEventSenders(element);
					}
				}
			}
		}
	}
	
	public void removeKeyEventSenders(Component c)
	{
		c.removeKeyListener(this);
		if (c instanceof Container)
		{
			Component[] children = ((Container) c).getComponents();
			for (Component element : children)
			{
				this.removeKeyEventSenders(element);
			}
		}
	}
	
	public void keyTyped(KeyEvent e)
	{
	}
	
	public void keyPressed(KeyEvent e)
	{
		this.currentPanel.keyPressed(e);
	}
	
	public void keyReleased(KeyEvent e)
	{
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(this.clockPanel.getTimer()))
		{
			if (Database.getCurrent().equals(Database.getTutorial()) || this.clockPanel.isError())
			{
				if (this.getForegroundAt(0).equals(this.fg))
				{
					this.setForegroundAt(0, this.bg);
				}
				else
				{
					this.setForegroundAt(0, this.fg);
				}
			}
			else
			{
				this.setTitleAt(0, this.dateFormat.format(new Date()) + " " + this.timeFormat.format(new Date())); //$NON-NLS-1$
			}
		}
	}
	
	public void modeChangePerformed(ModeChangeEvent e)
	{
		this.fireModeChangeEvent(e);
	}
	
	public void userLoggedIn(LoginEvent e)
	{
		int index = this.getSelectedIndex();
		boolean found = false;
		Component[] tabs = this.getComponents();
		for (int i = 0; i < tabs.length; i++)
		{
			if (tabs[i] instanceof UserPanel)
			{
				if (((UserPanel) tabs[i]).getUser().equals(e.getUser()))
				{
					index = i;
					found = true;
				}
			}
		}
		if (!found)
		{
			this.addUser(e.getUser());
			index = this.getTabCount() - 1;
		}
		
		this.setSelectedIndex(index);
	}
	
	public void cleanUpPanels()
	{
		Component[] tabs = this.getComponents();
		for (Component tab : tabs)
		{
			if (tab instanceof UserPanel)
			{
				((UserPanel) tab).cleanUp();
			}
		}
	}
	
	public ReceiptPrinter getReceiptPrinter()
	{
		return this.receiptPrinter;
	}
	
	public CustomerDisplay getCustomerDisplay()
	{
		return this.customerDisplay;
	}
	
	public boolean addModeChangeListener(ModeChangeListener l)
	{
		return this.modeChangeListeners.add(l);
	}
	
	public boolean removeModeChangeListener(ModeChangeListener l)
	{
		return this.modeChangeListeners.remove(l);
	}
	
	private void fireModeChangeEvent(ModeChangeEvent e)
	{
		ModeChangeListener[] l = (ModeChangeListener[]) this.modeChangeListeners.toArray(new ModeChangeListener[0]);
		for (ModeChangeListener element : l)
		{
			element.modeChangePerformed(e);
		}
	}
	
	private String loginTitle = Messages.getString("TabPanel.Anmelden_21"); //$NON-NLS-1$
	private LoginPanel loginPanel;
	private ClockPanel clockPanel;
	private Panel currentPanel;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy"); //$NON-NLS-1$
	private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss"); //$NON-NLS-1$
	private ReceiptPrinter receiptPrinter;
	private CustomerDisplay customerDisplay;
	private Color fg;
	private Color bg;
	
	private ArrayList modeChangeListeners = new ArrayList();
	
	public static Timestamp today;
	public static int unSettled = -1;
	
}
