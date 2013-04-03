/*
 * Created on 10.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.client.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.swing.JFrame;

import ch.eugster.pos.Messages;
import ch.eugster.pos.client.App;
import ch.eugster.pos.db.Customer;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.db.Table;
import ch.eugster.pos.db.Version;
import ch.eugster.pos.events.CustomerChangeEvent;
import ch.eugster.pos.events.CustomerChangeListener;
import ch.eugster.pos.events.DatabaseEvent;
import ch.eugster.pos.events.DatabaseListener;
import ch.eugster.pos.events.MessageDialog;
import ch.eugster.pos.events.ModeChangeEvent;
import ch.eugster.pos.events.ModeChangeListener;
import ch.eugster.pos.events.ShowMessageEvent;
import ch.eugster.pos.events.ShowMessageListener;
import ch.eugster.pos.events.ShutdownListener;
import ch.eugster.pos.product.ProductServer;
import ch.eugster.pos.util.Path;

public class Frame extends JFrame implements ModeChangeListener, CustomerChangeListener, DatabaseListener,
				ShowMessageListener
{
	public static final long serialVersionUID = 0l;
	
	/**
	 * @throws java.awt.HeadlessException
	 */
	public Frame() throws HeadlessException
	{
		super();
		this.init();
	}
	
	/**
	 * @param gc
	 */
	public Frame(GraphicsConfiguration gc)
	{
		super(gc);
		this.init();
	}
	
	/**
	 * @param title
	 * @throws java.awt.HeadlessException
	 */
	public Frame(String title) throws HeadlessException
	{
		super(title);
		this.init();
	}
	
	/**
	 * @param title
	 * @param gc
	 */
	public Frame(String title, GraphicsConfiguration gc)
	{
		super(title, gc);
		this.init();
	}
	
	private void init()
	{
		Frame.main = this;
		
		//		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(Messages.getString("Frame.TabPanel_wird_initialisiert._2")); //$NON-NLS-1$ //$NON-NLS-2$
		this.tabPanel = new TabPanel();
		this.tabPanel.setFocusable(false);
		this.tabPanel.addModeChangeListener(this);
		
		//		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(Messages.getString("Frame.TabPanel_ist_initialisiert._4")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(this.tabPanel, BorderLayout.CENTER);
		//		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(Messages.getString("Frame.TabPanel_eingefuegt._6")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.lockFile = new File(
						Path.getInstance().lockDir.concat(File.separator.concat(Path.getInstance().FILE_COLIBRI_LOCK)));
		try
		{
			this.lockFile.createNewFile();
			this.bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.lockFile.getAbsolutePath(),
							true), "UTF-8"));//$NON-NLS-1$
			//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(Messages.getString("Frame.lockfile_erstellt._8")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		catch (IOException e)
		{
			//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
		}
		
		// Table.addDatabaseErrorListener(this) ;
		//		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(Messages.getString("Frame.Frame_wird_DatabaseErrorListener_von_Table._11")); //$NON-NLS-1$ //$NON-NLS-2$
		this.addShutdownListener(Database.getTemporary());
		//		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(Messages.getString("Frame.Tempor_u00E4re_Datenbank_wird_ShutdownListener_von_Frame._13")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent event)
			{
				System.out.println("Width: " + event.getComponent().getSize().width + "; Height: "
								+ event.getComponent().getSize().height);
			}
		});
		
		Table.addDatabaseErrorListener(this);
		Version.isFrameVisible = true;
	}
	
	public void modeChangePerformed(ModeChangeEvent e)
	{
		if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_EXIT))
		{
			this.closeApplication();
		}
	}
	
	public void dispose()
	{
		Table.addDatabaseErrorListener(this);
		
		//		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(Messages.getString("Frame.Kundendisplay_wird_geschlossen..._15")); //$NON-NLS-1$ //$NON-NLS-2$
		this.tabPanel.getCustomerDisplay().closeAll();
		//		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(Messages.getString("Frame.Bondrucker_wird_geschlossen..._17")); //$NON-NLS-1$ //$NON-NLS-2$
		this.tabPanel.getReceiptPrinter().cleanup();
		
		try
		{
			this.bw.close();
		}
		catch (IOException e)
		{
		}
		
		this.fireShutdown();
		
		super.dispose();
	}
	
	public TabPanel getTabPanel()
	{
		return this.tabPanel;
	}
	
	public void setTitle()
	{
		this.setTitle(App.getApp().getTitle());
	}
	
	public void setTitle(Customer customer)
	{
		String title = App.getApp().getTitle();
		if (customer.getId().length() > 0)
		{
			title = title + " ::  Kunde " + customer.getId() + ": " + customer.getName();
			if (customer.hasCard())
			{
				title = title + " MIT KUNDENKARTE, Kontostand: " + customer.getFormattedAccount();
			}
			else
				title = title + " (OHNE KUNDENKARTE)";
		}
		super.setTitle(title);
	}
	
	public void processWindowEvent(WindowEvent e)
	{
		if (e.getSource().equals(this))
		{
			if (e.getID() == WindowEvent.WINDOW_CLOSING)
			{
				this.closeApplication();
			}
		}
	}
	
	public static Frame getMainFrame()
	{
		return Frame.main;
	}
	
	public void databaseErrorOccured(DatabaseEvent e)
	{
		switch (e.type)
		{
			case 0:
				/*
				 * Diese Meldung erscheint, bevor auf die temporäre Datenbank
				 * umgeschaltet wird.
				 */
				MessageDialog.showInformation(this, e.title, e.text, 0);
				break;
			case 1:
				/*
				 * Diese Meldung erscheint, nachdem auf die temporäre Datenbank
				 * umgeschaltet worden ist.
				 */
				this.setTitle(Salespoint.getCurrent().name + " [" + Database.getCurrent().getName() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
				MessageDialog.showInformation(this, e.title, e.text, 0);
				break;
			case 2:
				/*
				 * Nach der Meldung wird die Applikation heruntergefahren.
				 */
				if (e.text != null && !e.text.equals(""))
				{
					MessageDialog.showInformation(this, e.title, e.text, 0);
				}
				this.closeApplication(true);
				break;
		}
	}
	
	public void closeApplication(boolean force)
	{
		int k = 0;
		
		Database.removeMessageListeners();
		ProductServer.setMessageListener(null);
		
		Component[] comp = this.tabPanel.getComponents();
		for (int i = 0; i < comp.length; i++)
		{
			if (comp[i] instanceof UserPanel)
			{
				k++;
			}
		}
		
		if (!force)
		{
			if (k > 1)
			{
				MessageDialog.showInformation(
								this,
								Messages.getString("Frame.Programm_beenden_21"),
								Messages.getString("Frame.Sie_koennen_das_Programm_erst_beenden,_nwenn_alle_Benutzer_sich_abgemeldet_haben._20"),
								0);
				return;
			}
			// 10435
			//
			// if (UserPanel.getCurrent() != null)
			// {
			// if
			// (UserPanel.getCurrent().getReceiptModel().getPositionTableModel().getRowCount()
			// > 0
			// ||
			// UserPanel.getCurrent().getReceiptModel().getPaymentTableModel().getRowCount()
			// > 0)
			// {
			// if (MessageDialog
			// .showSimpleDialog(
			// Frame.getMainFrame(),
			// Messages
			// .getString("UserPanel.Kassiervorgang_nicht_abgeschlossen_12"),
			// Messages
			//																	.getString("UserPanel.Sie_haben_den_aktuellen_Kassiervorgang_noch_nicht_abgeschlossen._n_10") + //$NON-NLS-1$
			// Messages
			//																					.getString("UserPanel.Wollen_Sie_ihn_jetzt_noch_beenden__11"), //$NON-NLS-1$ 
			// 1) == MessageDialog.BUTTON_YES)
			// {
			// if (this.tabPanel.getSelectedComponent() instanceof
			// UserPanel)
			// {
			// UserPanel.getCurrent().fireModeChangeEvent(
			// new ModeChangeEvent(UserPanel.getCurrent().getMode()));
			// return;
			// }
			// else
			// {
			// 10437 Nun habe ich auch noch force = true auskommentiert, damit
			// die Frage Programm beenden gezeigt wird
			// force = true;
			// 10437
			// }
			// }
			// }
			// }
			//
			// 10435
		}
		
		if (force || MessageDialog.showSimpleDialog(this, Messages.getString("Frame.Programm_beenden_23"), //$NON-NLS-1$
						Messages.getString("Frame.Wollen_Sie_das_Programm_beenden__22"), //$NON-NLS-1$
						1) == MessageDialog.BUTTON_YES)
		{
			this.tabPanel.cleanUpPanels();
			App.getApp().dispose();
		}
		else
		{
			if (this.tabPanel.getSelectedComponent() instanceof UserPanel)
			{
				UserPanel.getCurrent().fireModeChangeEvent(new ModeChangeEvent(UserPanel.getCurrent().getMode()));
			}
		}
	}
	
	public void customerChanged(CustomerChangeEvent ev)
	{
		this.setTitle(ev.getCustomer());
	}
	
	public void closeApplication()
	{
		this.closeApplication(false);
	}
	
	public int showMessage(ShowMessageEvent event)
	{
		return MessageDialog.showSimpleDialog(this, event.getTitle(), event.getMessage(), event.getType());
	}
	
	private boolean addShutdownListener(ShutdownListener l)
	{
		return this.shutdownListeners.add(l);
	}
	
	// private boolean removeShutdownListener(ShutdownListener l)
	// {
	// return this.shutdownListeners.remove(l);
	// }
	
	private void fireShutdown()
	{
		ShutdownListener[] ls = (ShutdownListener[]) this.shutdownListeners.toArray(new ShutdownListener[0]);
		for (int i = 0; i < ls.length; i++)
		{
			ls[i].shutdownPerformed();
		}
	}
	
	private TabPanel tabPanel;
	private static Frame main;
	private File lockFile;
	private BufferedWriter bw;
	
	private ArrayList shutdownListeners = new ArrayList();
	// private ArrayList databaseListeners = new ArrayList();
}
