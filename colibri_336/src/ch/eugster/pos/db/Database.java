/*
 * Created on 16.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.db;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ProgressMonitor;

import org.jdom.Document;
import org.jdom.Element;

import ch.eugster.pos.Messages;
import ch.eugster.pos.events.MessageDialog;
import ch.eugster.pos.events.ShowMessageEvent;
import ch.eugster.pos.events.ShowMessageListener;
import ch.eugster.pos.events.StateChangeListener;
import ch.eugster.pos.util.Config;
import ch.eugster.pos.util.Serializer;

import com.ibm.bridge2java.ComException;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Database
{
	
	public static void load()
	{
		Database.load(Config.getInstance().getDocument());
	}
	
	public static void load(Document doc)
	{
		Element root = doc.getRootElement();
		Element edb = root.getChild("database"); //$NON-NLS-1$
		Element es = edb.getChild("standard"); //$NON-NLS-1$
		Element et = edb.getChild("temporary"); //$NON-NLS-1$
		Element ec = edb.getChild("tutorial"); //$NON-NLS-1$
		Database.standard = new Standard(es);
		Database.temporary = new Temporary(et);
		Database.tutorial = new Tutorial(ec);
		if (edb.getAttributeValue("default").equals("standard")) { //$NON-NLS-1$ //$NON-NLS-2$
			Database.current = Database.standard;
		}
		else if (edb.getAttributeValue("default").equals("temporary")) { //$NON-NLS-1$ //$NON-NLS-2$
			Database.current = Database.temporary;
		}
		else if (edb.getAttributeValue("default").equals("tutorial")) { //$NON-NLS-1$ //$NON-NLS-2$
			Database.current = Database.tutorial;
		}
	}
	
	public static void setStandard(Standard standard)
	{
		Database.standard = standard;
	}
	
	public static Standard getStandard()
	{
		return Database.standard;
	}
	
	public static void setTemporary(Temporary temporary)
	{
		Database.temporary = temporary;
	}
	
	public static Temporary getTemporary()
	{
		return Database.temporary;
	}
	
	public static void setTutorial(Tutorial tutorial)
	{
		Database.tutorial = tutorial;
	}
	
	public static Tutorial getTutorial()
	{
		return Database.tutorial;
	}
	
	public static void setCurrent(Connection con)
	{
		Database.current = con;
		Database.fireStateChangeEvent();
	}
	
	public static Connection getCurrent()
	{
		if (Database.current == null)
		{
			Table.switchDatabase();
		}
		return Database.current;
	}
	
	public static boolean isStandard()
	{
		return Database.current instanceof Standard ? true : false;
	}
	
	public static boolean isTemporary()
	{
		return Database.current instanceof Temporary ? true : false;
	}
	
	public static boolean isTutorial()
	{
		return Database.current instanceof Tutorial ? true : false;
	}
	
	public static boolean isStandard(Connection con)
	{
		return con instanceof Standard ? true : false;
	}
	
	public static boolean isTemporary(Connection con)
	{
		return con instanceof Temporary ? true : false;
	}
	
	public static boolean isTutorial(Connection con)
	{
		return con instanceof Tutorial ? true : false;
	}
	
	public static Connection[] getConnections()
	{
		ArrayList list = new ArrayList();
		if (Database.standard.isActive())
		{
			list.add(Database.standard);
		}
		if (Database.temporary.isActive())
		{
			list.add(Database.temporary);
		}
		if (Database.tutorial.isActive())
		{
			list.add(Database.tutorial);
		}
		return (Connection[]) list.toArray(new Connection[0]);
	}
	
	public static String[] transferTemporaryData()
	{
		int inserted = 0;
		ArrayList booksNotFound = new ArrayList();
		if (Database.standard != null && Database.standard.isActive() && Database.standard.isConnected()
						&& Database.getCurrent().equals(Database.standard))
		{
			if (Database.temporary != null && Database.temporary.isActive() && Database.temporary.isConnected())
			{
				Connection old = Database.current;
				Database.setCurrent(Database.temporary);
				// 10110
				Receipt[] receipts = Receipt.selectCurrent(Salespoint.getCurrent(), true);
				// 10110
				if (receipts.length > 0)
				{
					boolean error = false;
					ProgressMonitor monitor = new ProgressMonitor(null,
									Messages.getString("Database.Belege_werden_transferiert._Bitte_Warten._17"), //$NON-NLS-1$
									Messages.getString("Database.Die_Belege_werden_aus_der_tempor_u00E4ren_Datenbank_in_die_Standarddatenbank_ntransferiert._Bitte_haben_Sie_etwas_Geduld..._18"), //$NON-NLS-1$
									0, receipts.length);
					monitor.setProgress(0);
					
					Logger.getLogger("colibri").getLevel(); //$NON-NLS-1$
					Logger.getLogger("colibri").setLevel(Level.INFO); //$NON-NLS-1$
					Logger.getLogger("colibri").info(Messages.getString("Database.Start_transfer__22") + receipts.length + Messages.getString("Database._Receipts_from_temporary_database_to_standard_database..._23")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					DBResult[] saved = new DBResult[receipts.length];
					DBResult[] removed = new DBResult[receipts.length];
					
					try
					{
						for (int i = 0; i < receipts.length; i++)
						{
							monitor.setProgress(i + 1);
							
							// 10163
							Logger.getLogger("colibri").info("Beleg wird eingelesen."); //$NON-NLS-1$ //$NON-NLS-2$
							Receipt copy = receipts[i].clone(false);
							// 10163
							
							// 10164
							// if (receipts[i].status.equals(Receipt.
							// RECEIPT_STATE_SERIALIZED)) {
							if (receipts[i].status == Receipt.RECEIPT_STATE_SERIALIZED
											|| receipts[i].status == Receipt.RECEIPT_STATE_REVERSED)
							{
								// 10164
								Logger.getLogger("colibri").info(Messages.getString("Database.Galileodaten_werden_aktualisiert..._25")); //$NON-NLS-1$ //$NON-NLS-2$
								Database.setCurrent(Database.standard);
								
								// 10163
								//								Logger.getLogger("colibri").info("Beleg wird in Galileo verbucht."); //$NON-NLS-1$ //$NON-NLS-2$
								// bookGalileo(copy, booksNotFound);
								//								Logger.getLogger("colibri").info("Beleg wird in Standarddatenbank kopiert."); //$NON-NLS-1$ //$NON-NLS-2$
								saved[i] = Database.saveReceipt(copy);
								error = saved[i].getErrorCode() == 0 ? error : false;
								// 10163
								
								/*
								 * Der Beleg darf in der temporären Datenbank
								 * nur gelöscht werden, wenn er erfolgreich in
								 * die Standarddatenbank geschrieben worden ist.
								 */
								if (saved[i].getErrorCode() == 0)
								{
									Logger.getLogger("colibri").info("Beleg wird in temporärer Datenbank gelöscht."); //$NON-NLS-1$ //$NON-NLS-2$
									removed[i] = Database.removeReceipt(receipts[i]);
									error = removed[i].getErrorCode() == 0 ? error : false;
								}
							}
							else
							{
								saved[i] = new DBResult(DBResult.NO_STORING_NEEDED, "");
								// 10163
								Logger.getLogger("colibri").info("Beleg wird in temporärer Datenbank gelöscht."); //$NON-NLS-1$ //$NON-NLS-2$
								removed[i] = Database.removeReceipt(receipts[i]);
								// error = removed[i].getErrorCode() == 0 ?
								// error : false;
								// 10163
							}
							
						}
					}
					catch (ComException e)
					{
						Table.switchDatabase();
					}
					finally
					{
						monitor.close();
						if (error)
						{
							MessageDialog.showInformation(
											null,
											"Transfermeldung",
											Messages.getString("Database.Beim_Transfer_von__28") + " " + receipts.length + " " + Messages.getString(Messages.getString("Database.Database._Belegen_aus_der_lokalen_in_die_Standarddatenbank_n_29_2")) + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
															Messages.getString("Database.wurden_Fehler_generiert_und_es_konnten_nicht_alle_Belege_transferiert_nwerden._30") + " " + //$NON-NLS-1$ //$NON-NLS-2$
															Messages.getString("Database.Die_Fehler_wurden_in_die_Protokolldatei_geschrieben._Nicht_transferierte_nBelege_31") + //$NON-NLS-1$
															Messages.getString("Database.werden_beim_n_u00E4chsten_Kassenstart_transferiert._32"), //$NON-NLS-1$
											0);
						}
						else
						{
							// 10110
							for (int i = 0; i < saved.length; i++)
							{
								if (saved[i] != null && saved[i].getErrorCode() == 0)
								{
									inserted++;
								}
							}
							// 10110
							if (inserted > 0)
							{
								MessageDialog.showInformation(
												null,
												"Transfermeldung",
												Messages.getString("Database.Es_sind__35") + inserted + Messages.getString("Database._Belege_erfolgreich_aus_der_lokalen_in_die_nStandarddatenbank_36") + //$NON-NLS-1$ //$NON-NLS-2$
																Messages.getString("Database._transferiert_worden._37"), //$NON-NLS-1$
												0);
							}
						}
					}
				}
				Database.setCurrent(old);
			}
		}
		return (String[]) booksNotFound.toArray(new String[0]);
	}
	
	// private static void bookGalileo (Receipt receipt, ArrayList
	// booksNotFound) {
	// boolean isOpen = false;
	// if (ProductServer.isUsed() && ProductServer.getInstance() != null) {
	// if (ProductServer.getInstance() instanceof GalileoServer) {
	// if (ProductServer.getInstance().isActive()) {
	// if (ProductServer.getInstance().getUpdate() > 0) {
	// boolean galileoBooked = true;
	// Position[] positions = receipt.getPositionsAsArray();
	//
	// isOpen = ProductServer.getInstance().isOpen();
	// if (!isOpen) {
	// ProductServer.getInstance().open();
	// }
	// for (int i = 0; i < positions.length; i++) {
	// if (positions[i].galileoBook.booleanValue() && galileoBooked) {
	// /*
	// * Je nach Server (GalileoComServer oder GalileoComServerWithStock)
	// * wird die Position in Galileo verbucht (GalileoComServerWithStock)
	// * oder eben nicht (GalileoComServer).
	// * Im ersten Fall gibt die Funktion eine Boolean zurück, die angibt,
	// * ob die Verbuchung erfolgreich war, im zweiten true.
	// */
	// if (!positions[i].galileoBooked.booleanValue() ||
	// (positions[i].galileoBooked.booleanValue() &&
	// receipt.status.equals(Receipt.RECEIPT_STATE_REVERSED))) {
	// if (positions[i].ordered.booleanValue()) {
	// // 10078
	// if (ProductServer.getInstance().getItem(positions[i].orderId)) {
	// ProductServer.getInstance().setDataToTransferredPosition(positions[i]);
	// }
	// // 10078
	// }
	// galileoBooked = ProductServer.getInstance().update(receipt.status,
	// positions[i]).booleanValue();
	// positions[i].galileoBooked = new
	// Boolean(receipt.status.equals(Receipt.RECEIPT_STATE_SERIALIZED) ?
	// galileoBooked : false);
	// }
	// }
	// }
	// if (!isOpen) {
	// ProductServer.getInstance().close();
	// }
	// }
	// }
	// }
	// }
	// }
	
	private static DBResult saveReceipt(Receipt receipt)
	{
		File file = Serializer.getInstance().getPath(receipt, Database.temporary, false);
		
		Database.setCurrent(Database.standard);
		DBResult result = receipt.store(true, true);
		if (result.getErrorCode() == 0)
		{
			if (file.exists())
			{
				Serializer.getInstance().deleteReceipt(file);
			}
			Serializer.getInstance().writeReceipt(receipt);
			Logger.getLogger("colibri").info(Messages.getString("Database.Receipt_with_id______40") + receipt.getNumber() + Messages.getString("Database.___transferred._41")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return result;
	}
	
	private static DBResult removeReceipt(Receipt receipt)
	{
		Database.setCurrent(Database.temporary);
		DBResult result = receipt.remove();
		if (result.getErrorCode() == 0)
		{
			Logger.getLogger("colibri").info(Messages.getString("Database.Receipt_with_id______49") + receipt.getNumber() + Messages.getString("Database.___in_temporary_database_removed._50")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		else
		{
			Logger.getLogger("colibri").info(Messages.getString("Database.Problem_removing_Receipt_with_id______52") + receipt.getNumber() + Messages.getString("Database.___from_temporary_database..._53")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			Logger.getLogger("colibri").info(result.getErrorText()); //$NON-NLS-1$
			if (result.getExternalErrorCode() != null && result.getExternalErrorCode().length() > 0)
			{
				Logger.getLogger("colibri").info(result.getExternalErrorCode()); //$NON-NLS-1$
				Logger.getLogger("colibri").info(result.getExternalErrorText()); //$NON-NLS-1$
			}
		}
		return result;
	}
	
	public static void setSwitchable(boolean switchable)
	{
		Database.isSwitchable = switchable;
	}
	
	public static boolean isSwitchable()
	{
		return Database.isSwitchable;
	}
	
	public static boolean addStateChangeListener(StateChangeListener listener)
	{
		return Database.stateChangeListeners.add(listener);
	}
	
	public static boolean removeStateChangeListener(StateChangeListener listener)
	{
		return Database.stateChangeListeners.remove(listener);
	}
	
	protected static void fireStateChangeEvent()
	{
		StateChangeListener[] listeners = (StateChangeListener[]) Database.stateChangeListeners
						.toArray(new StateChangeListener[0]);
		for (int i = 0; i < listeners.length; i++)
		{
			listeners[i].updateStates();
		}
	}
	
	public static void addMessageListener(ShowMessageListener listener)
	{
		if (!Database.messageListeners.contains(listener))
		{
			Database.messageListeners.add(listener);
		}
	}
	
	public static void removeMessageListener(ShowMessageListener listener)
	{
		if (Database.messageListeners.contains(listener))
		{
			Database.messageListeners.remove(listener);
		}
	}
	
	public static void removeMessageListeners()
	{
		Database.messageListeners.clear();
	}
	
	public static void sendMessage(ShowMessageEvent event)
	{
		ShowMessageListener[] listeners = (ShowMessageListener[]) Database.messageListeners
						.toArray(new ShowMessageListener[0]);
		for (int i = 0; i < listeners.length; i++)
		{
			listeners[i].showMessage(event);
		}
	}
	
	private static Standard standard;
	private static Temporary temporary;
	private static Tutorial tutorial;
	private static Connection current;
	
	private static boolean isSwitchable = false;
	
	private static ArrayList messageListeners = new ArrayList();
	private static ArrayList stateChangeListeners = new ArrayList();
}
