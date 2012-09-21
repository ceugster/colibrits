/*
 * Created on 17.08.2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.events;

import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.Icon;

import ch.eugster.pos.client.gui.Frame;
import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.client.model.ReceiptModel;
import ch.eugster.pos.db.Customer;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.Key;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.galileo.kundenserver.kundenserver;
import ch.eugster.pos.util.Config;

/**
 * @author Arbeiten
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ChooseCustomerAction extends ModeDependendAction implements IFailOverState
{
	public static final long serialVersionUID = 0l;
	
	/**
	 * @param context
	 * @param key
	 */
	public ChooseCustomerAction(UserPanel context, Key key)
	{
		super(context, key);
		this.init(key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 */
	public ChooseCustomerAction(UserPanel context, Key key, String name)
	{
		super(context, key, name);
		this.init(key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 * @param icon
	 */
	public ChooseCustomerAction(UserPanel context, Key key, String name, Icon icon)
	{
		super(context, key, name, icon);
		this.init(key);
	}
	
	/**
	 * @param key
	 */
	private void init(Key key)
	{
		this.putActionType(Action.POS_ACTION_CHOOSE_CUSTOMER);
	}
	
	/**
	 * @param actionEvent
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (Database.getCurrent().equals(Database.getTemporary()))
		{
			MessageDialog.showSimpleDialog(Frame.getMainFrame(), "Funktion nicht aktiv",
							"Diese Funktion ist im Failover Modus nicht aktiv.", MessageDialog.TYPE_INFORMATION);
		}
		else
		{
			if (!this.getActionType().equals(Action.POS_ACTION_NO_ACTION))
			{
				if (ReceiptModel.getInstance().receiptHasInputPosition())
				{
					String msg = "<html>Sie haben eben eine Geldeinlage in die Kasse getippt. Bitte beenden Sie diese Transaktion, bevor Sie eine neue beginnen.";
					MessageDialog.showInformation(Frame.getMainFrame(), "Geldeinlage", msg,
									MessageDialog.TYPE_INFORMATION);
				}
				else if (ReceiptModel.getInstance().receiptHasWithdrawPosition())
				{
					String msg = "<html>Sie haben eben eine Geldentnahme in die Kasse getippt. Bitte beenden Sie diese Transaktion, bevor Sie eine neue beginnen.";
					MessageDialog.showInformation(Frame.getMainFrame(), "Geldeinlage", msg,
									MessageDialog.TYPE_INFORMATION);
				}
				else
				{
					try
					{
						Logger.getLogger("colibri").info("Kundenserver wird instantiiert.");
						// OleEnvironment.Initialize(); //10086
						kundenserver ks = new kundenserver();
						Logger.getLogger("colibri").info(
										"Kundenserver versucht, Verbindung zur Datenbank herzustellen...");
						if (ks.db_open(Config.getInstance().getGalileoPath()))
						{
							Logger.getLogger("colibri").info("Verbindung zur Datenbank hergestellt.");
							Customer customer = new Customer();
							Logger.getLogger("colibri").info("Kundenfenster wird geöffnet.");
							int i = ks.getkundennr();
							System.out.println(i);
							if (i == 0)
							{
								Logger.getLogger("colibri").info("Es wurde keine Auswahl getroffen.");
							}
							else
							{
								Logger.getLogger("colibri").info(
												"Kunde wurde ausgewählt, die Kundendaten werden gesetzt.");
								customer.setId(new Integer(i).toString());
								String vname = (String) ks.getCVORNAME();
								String nname = (String) ks.getCNAME1();
								customer.setName(vname.trim() + " " + nname.trim());
								customer.setHasCard(((Boolean) ks.getLKUNDKARTE()).booleanValue());
								customer.setAccount(Customer.getAccount(ks.getNKUNDKONTO()));
								
								// 10215
								Boolean paid = (Boolean) ks.getLRGGEWAEHLT();
								if (paid.booleanValue())
								{
									Logger.getLogger("colibri").info("Offene Rechnung für Bezahlung ausgewählt.");
									if (ProductGroup.getPaidInvoiceGroup() == null)
									{
										MessageDialog.showInformation(
														Frame.getMainFrame(),
														"Fehler",
														"Es ist keine Warengruppe für bezahlte Rechnungen festgelegt worden.\nBevor Rechnungen bar bezahlt werden können, muss eine Warengruppe definiert sein.",
														MessageDialog.TYPE_ERROR);
										ks.db_close();
										Logger.getLogger("colibri").info("Verbindung zur Datenbank wird geschlossen.");
										ks.release();
										Logger.getLogger("colibri").info("Kundenserverinstanz wird zerstört.");
										return;
									}
									Double amount = (Double) ks.getNRGBETRAG();
									Integer invoice = (Integer) ks.getNRGNUMMER();
									Date date = (Date) ks.getCRGDATUM();
									if (paid.booleanValue())
									{
										this.context.getReceiptModel().getPositionModel()
														.addInvoicePosition(paid, amount.doubleValue(), invoice, date);
										if (this.context.getReceiptModel().getPositionModel().isComplete())
										{
											this.context.getReceiptModel()
															.getPositionTableModel()
															.store(this.context.getReceiptModel().getPositionModel()
																			.getPosition());
										}
									}
								}
								// 10215
							}
							UserPanel.getCurrent().getReceiptModel().updateCustomer(customer);
							Logger.getLogger("colibri").info("Verbindung zur Datenbank wird geschlossen.");
							ks.db_close();
						}
						Logger.getLogger("colibri").info("Kundenserverinstanz wird zerstört.");
						ks.release();
						// OleEnvironment.UnInitialize(); //10086
						
						PosEvent p = new PosEvent(this);
						PosEventListener[] l = (PosEventListener[]) this.posEventListeners
										.toArray(new PosEventListener[0]);
						for (int i = 0; i < l.length; i++)
						{
							l[i].posEventPerformed(p);
						}
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
						String msg = "<html>Beim Aufrufen des Kundenfensters ist ein unerwarteter Fehler aufgetreten. Das Fenster konnte nicht geöffnet werden.";
						MessageDialog.showInformation(Frame.getMainFrame(), "Kundenfenster", msg,
										MessageDialog.TYPE_ERROR);
					}
				}
			}
		}
	}
	
	public void modeChangePerformed(ModeChangeEvent e)
	{
		super.modeChangePerformed(e);
		
		if (this.getValue("enabled").equals(new Boolean(true)))
		{
			if (Database.getCurrent().equals(Database.getTemporary()))
			{
				this.putValue("enabled", new Boolean(false)); //$NON-NLS-1$
			}
			else if (this.context.getReceiptModel().receiptHasInputPosition()
							|| this.context.getReceiptModel().receiptHasWithdrawPosition())
			{
				this.putValue("enabled", new Boolean(false)); //$NON-NLS-1$
			}
		}
	}
	
}
