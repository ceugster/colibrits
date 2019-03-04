/*
 * Created on 31.08.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.events;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.Icon;

import ch.eugster.pos.Messages;
import ch.eugster.pos.client.gui.Frame;
import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.db.Coin;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.Key;
import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.printing.SettlementPrinter2;
import ch.eugster.pos.util.Config;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class SettleDayAction extends Action implements ModeChangeRequest, IFailOverState
{
	public static final long serialVersionUID = 0l;
	
	/**
	 * @param context
	 * @param key
	 */
	public SettleDayAction(UserPanel context, Key key)
	{
		super(context, key);
		this.init(key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 */
	public SettleDayAction(UserPanel context, Key key, String name)
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
	public SettleDayAction(UserPanel context, Key key, String name, Icon icon)
	{
		super(context, key, name, icon);
		this.init(key);
	}
	
	public void init(Key key)
	{
		this.putValue(Action.POS_KEY_DRAWER_NUMBER, key.actionType);
		this.printer = new SettlementPrinter2(this.context);
		this.addPosEventListener(this.printer);
		this.addModeChangeListener(this.context);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (Database.getCurrent().equals(Database.getTemporary()))
		{
			MessageDialog.showSimpleDialog(Frame.getMainFrame(), "Funktion nicht aktiv",
							"Diese Funktion ist im Failover Modus nicht aktiv.", MessageDialog.TYPE_INFORMATION);
		}
		else
		{
			boolean forceSettlement = Config.getInstance().getSalespointForceSettlement();
			boolean forceStockCount = Config.getInstance().getSalespointForceStockCount();
			Coin[] countedCurrencies = this.printer.getCountedCurrencies();
			if (this.printer.receiptsToSettle() && !Salespoint.getCurrent().variableStock
							&& countedCurrencies.length == 0 && forceStockCount)
			{
				MessageDialog.showSimpleDialog(
								Frame.getMainFrame(),
								"Kassensturz noch nicht vorgenommen",
								"Der Kassensturz wurde noch nicht vorgenommen (Bei fixem Kassenstock muss der Kassensturz immer vorgenommen werden.",
								MessageDialog.TYPE_INFORMATION);
			}
			else
			{
				String msgSettle = this.printer.receiptsToSettle() ? "<br>- Tagesabschluss" : "";
				String msgStock = "";
				for (Coin countedCurrencie : countedCurrencies)
				{
					msgStock = msgStock + "<br>- Kassensturz " + countedCurrencie.getForeignCurrency().code;
				}
				
				if (!this.printer.receiptsToSettle() && countedCurrencies.length == 0)
					MessageDialog.showInformation(Frame.getMainFrame(), "Keine Vorgänge",
									"Es stehen keine Vorgänge zur Durchführung an.", MessageDialog.TYPE_INFORMATION);
				else
				{
					int i = MessageDialog.showSimpleDialog(Frame.getMainFrame(),
									Messages.getString("SettleDayAction.Tagesabschluss_2"), "<html>"
													+ "Folgende Vorgänge werden durchgeführt:<br>" + msgSettle
													+ msgStock + "<br><br>Wollen Sie weiterfahren?</html>", 1);
					
					if (i == MessageDialog.BUTTON_YES)
					{
						int j = 0;
						if (Config.getInstance().getSettlementAdmitTestSettlement())
						{ // 10182
							j = MessageDialog.showSimpleDialog(Frame.getMainFrame(), "Probeabschluss",
											"Soll ein Probeabschluss vorgenommen werden?", 1);
						} // 10182
						if (j == MessageDialog.BUTTON_YES)
						{
							this.printer.setTestPrint(true);
							super.actionPerformed(e);
						}
						else
						{
							this.printer.setTestPrint(false);
							super.actionPerformed(e);
						}
					}
				}
			}
		}
	}
	
	public void fireModeChangeEvent(ModeChangeEvent e)
	{
		ModeChangeListener[] listeners = (ModeChangeListener[]) this.modeChangeListeners
						.toArray(new ModeChangeListener[0]);
		for (ModeChangeListener listener : listeners)
		{
			listener.modeChangePerformed(e);
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
	
	private SettlementPrinter2 printer;
	private ArrayList modeChangeListeners = new ArrayList();
}
