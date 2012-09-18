/*
 * Created on 15.02.2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.admin;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.LogManager;

import org.apache.ojb.broker.PersistenceBrokerException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.product.ProductServer;

/**
 * @author Christian
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class UpdateGalileoRunnableWithProgress implements IRunnableWithProgress, Runnable
{
	
	private Date from;
	private Date to;
	private ProductServer server;
	private UpdateGalileoProgressMonitor monitor;
	
	/**
	 * 
	 */
	public UpdateGalileoRunnableWithProgress(Date from, Date to, UpdateGalileoProgressMonitor monitor)
	{
		super();
		this.from = from;
		this.to = to;
		this.monitor = monitor;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core
	 * .runtime.IProgressMonitor)
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
	{
		
		assert monitor instanceof UpdateGalileoProgressMonitor;
		this.monitor = (UpdateGalileoProgressMonitor) monitor;
		
		LogManager.getLogManager().getLogger("colibri").info("Verbindung zu Galileo herstellen...");
		if (this.connect())
		{
			if (LogManager.getLogManager().getLogger("colibri") != null)
			{
				LogManager.getLogManager().getLogger("colibri").info("Verbindung hergestellt.");
				LogManager.getLogManager().getLogger("colibri").info("Belege zählen...");
			}
			int receiptCount = new Long(Receipt.count(null, this.from, this.to, false)).intValue();
			if (LogManager.getLogManager().getLogger("colibri") != null)
			{
				LogManager.getLogManager().getLogger("colibri").info(
								"...Es werden " + receiptCount + " Belege verarbeitet.");
			}
			this.initMonitor(this.monitor, "Anzahl gefundene Belege: " + receiptCount);
			if (receiptCount > 0)
			{
				this.monitor.beginTask("Verarbeitungsprozess gestartet...", receiptCount);
				if (LogManager.getLogManager().getLogger("colibri") != null)
				{
					LogManager.getLogManager().getLogger("colibri").info("Galserve öffnen.");
				}
				this.server.open();
				try
				{
					if (LogManager.getLogManager().getLogger("colibri") != null)
					{
						LogManager
										.getLogManager()
										.getLogger("colibri")
										.info(
														"Belege laden (es werden immer 100 Belege geladen und verarbeitet, dann die nächsten 100 Belege...");
					}
					Receipt[] receipts = Receipt.select(this.from, this.to, false, false, 100);
					int value = 0;
					
					if (LogManager.getLogManager().getLogger("colibri") != null)
					{
						LogManager.getLogManager().getLogger("colibri").info("Start Verarbeitungsloop...");
					}
					mainLoop: while (receipts.length > 0)
					{
						this.from = this.getNextFrom(receipts[receipts.length - 1].getDate());
						
						for (int i = 0; i < receipts.length; i++)
						{
							if (LogManager.getLogManager().getLogger("colibri") != null)
							{
								LogManager.getLogManager().getLogger("colibri").info("Beleg " + i);
							} // 10109
							// if
							// (receipts[i].status.equals(Receipt.RECEIPT_STATE_SERIALIZED)
							// ||
							// receipts[i].status.equals(Receipt.RECEIPT_STATE_REVERSED))
							// {
							// 10109
							if (receipts[i].status == Receipt.RECEIPT_STATE_SERIALIZED)
							{
								if (LogManager.getLogManager().getLogger("colibri") != null)
								{
									LogManager.getLogManager().getLogger("colibri").info(
													"Positionen von Beleg " + i + " laden.");
								}
								Position[] positions = receipts[i].getPositionsAsArray();
								if (LogManager.getLogManager().getLogger("colibri") != null)
								{
									LogManager.getLogManager().getLogger("colibri").info(
													"" + positions.length + " Positionen geladen.");
								}
								boolean updateReceipt = false;
								for (int j = 0; j < positions.length; j++)
								{
									try
									{
										if (LogManager.getLogManager().getLogger("colibri") != null)
										{
											LogManager.getLogManager().getLogger("colibri").info(
															"Position " + j + " von Beleg " + i + " verarbeiten.");
										}
										boolean booked = this.update(positions[j], this.server);
										if (LogManager.getLogManager().getLogger("colibri") != null)
										{
											LogManager.getLogManager().getLogger("colibri").info(
															"Position " + j + " von Beleg " + i + " verarbeitet.");
										}
										if (booked) updateReceipt = true;
									}
									catch (UpdateGalileoException e)
									{
										if (LogManager.getLogManager().getLogger("colibri") != null)
										{
											LogManager.getLogManager().getLogger("colibri").severe(e.getMessage());
										}
									}
								}
								if (updateReceipt)
								{
									if (LogManager.getLogManager().getLogger("colibri") != null)
									{
										LogManager.getLogManager().getLogger("colibri").info(
														"Positionen in Datenbank aktualisieren.");
									}
									try
									{
										Database.getStandard().getBroker().store(receipts[i]);
										Database.getStandard().getBroker().commitTransaction();
										if (LogManager.getLogManager().getLogger("colibri") != null)
										{
											LogManager.getLogManager().getLogger("colibri").info(
															"Positionen in Datenbank aktualisiert.");
										}
									}
									catch (PersistenceBrokerException e)
									{
										Database.getStandard().getBroker().abortTransaction();
										if (LogManager.getLogManager().getLogger("colibri") != null)
										{
											LogManager.getLogManager().getLogger("colibri").info(
															"Aktualisierung fehlgeschlagen.");
										}
									}
								}
							}
							else
							{
								this.monitor.updateCountOther(receipts[i].getPositionCount());
							}
							if (LogManager.getLogManager().getLogger("colibri") != null)
							{
								LogManager.getLogManager().getLogger("colibri").info("Verlaufsmonitor aktualisieren.");
							}
							this.updateMonitor(this.monitor, ++value, receiptCount);
							if (this.monitor.isCanceled())
							{
								this.monitor.ask();
								if (this.monitor.isCanceled())
								{
									break mainLoop;
								}
							}
						}
						if (LogManager.getLogManager().getLogger("colibri") != null)
						{
							LogManager.getLogManager().getLogger("colibri").info(
											"Die nächsten maximal 100 Belege laden...");
						}
						receipts = Receipt.select(this.from, this.to, false, 100);
						monitor.setCanceled(false);
					}
				}
				finally
				{
					monitor.done();
					if (LogManager.getLogManager().getLogger("colibri") != null)
					{
						LogManager.getLogManager().getLogger("colibri").info("Verbindung zu Galserve beenden.");
					}
					this.disconnect();
				}
			}
		}
		else
		{
			if (LogManager.getLogManager().getLogger("colibri") != null)
			{
				LogManager.getLogManager().getLogger("colibri").info("Verbindung konnte nicht hergestellt werden.");
			}
		}
		if (LogManager.getLogManager().getLogger("colibri") != null)
		{
			LogManager.getLogManager().getLogger("colibri").info("Aktualisierung abgeschlossen.");
		}
	}
	
	private void initMonitor(IProgressMonitor monitor, String text)
	{
		this.monitor.setProgressLabelText(text);
	}
	
	private void updateMonitor(IProgressMonitor monitor, int currentValue, int maxValue)
	{
		this.monitor.worked(1);
	}
	
	private Date getNextFrom(Date from)
	{
		Calendar gc = GregorianCalendar.getInstance();
		gc.setTime(from);
		gc.add(Calendar.SECOND, 1);
		return gc.getTime();
	}
	
	private boolean update(Position position, ProductServer galileo) throws UpdateGalileoException
	{
		boolean done = false;
		if (LogManager.getLogManager().getLogger("colibri") != null)
		{
			LogManager
							.getLogManager()
							.getLogger("colibri")
							.info(
											"...Warengruppentyp prüfen (es werden nur Warengruppen, welche umsatzrelevant sind, aktualisiert.");
		}
		if (position.getProductGroup().type == ProductGroup.TYPE_INCOME)
		{
			if (LogManager.getLogManager().getLogger("colibri") != null)
			{
				LogManager
								.getLogManager()
								.getLogger("colibri")
								.info(
												"Falls der Verbuchungsflag auf false gesetzt ist, auf true setzen, damit Verbuchung erfolgt.");
			}
			if (!position.galileoBook) position.galileoBook = true;
			if (LogManager.getLogManager().getLogger("colibri") != null)
			{
				LogManager
								.getLogManager()
								.getLogger("colibri")
								.info(
												"Falls der Verbuchtflag auf true gesetzt ist, auf false setzen, damit Verbuchung erfolgt.");
			}
			if (position.galileoBooked) position.galileoBooked = false;
			done = this.doUpdate(position, galileo);
		}
		else
		{
			this.monitor.updateCountOther(1);
		}
		return done;
	}
	
	private boolean doUpdate(Position position, ProductServer galileo) throws UpdateGalileoException
	{
		if (LogManager.getLogManager().getLogger("colibri") != null)
		{
			LogManager.getLogManager().getLogger("colibri").info("JETZT erfolgt die Buchung in Galileo!");
		}
		position.galileoBooked = galileo.update(position.getReceipt().status, position);
		if (!position.galileoBooked)
		{
			if (LogManager.getLogManager().getLogger("colibri") != null)
			{
				LogManager.getLogManager().getLogger("colibri").info("...Die Verbuchung ist fehlgeschlagen.");
			}
			this.monitor.updateCountFailed(1);
			String posId = position.productId != null && position.productId.trim().length() > 0 ? position.productId
							: position.getProductGroup().galileoId;
			throw new UpdateGalileoException("Eine Position (Position " + posId + " von Beleg "
							+ position.getReceipt().getNumber() + " vom " + position.getReceipt().timestamp
							+ ") konnte nicht ordnungsgemäss verarbeitet werden.");
		}
		else
		{
			if (LogManager.getLogManager().getLogger("colibri") != null)
			{
				LogManager.getLogManager().getLogger("colibri").info("...Die Verbuchung war erfolgreich.");
			}
			this.countReceipt(position.productId);
		}
		return position.galileoBooked;
	}
	
	private void countReceipt(String productId)
	{
		if (productId == null || productId.trim().length() == 0 || productId.equals("0"))
		{
			this.monitor.updateCountColibri(1);
		}
		else
		{
			long number = 0;
			try
			{
				number = new Long(productId).longValue();
			}
			catch (NumberFormatException e)
			{
			}
			finally
			{
				if (number == 0)
				{
					this.monitor.updateCountColibri(1);
				}
				else
				{
					this.monitor.updateCountGalileo(1);
				}
			}
		}
	}
	
	private boolean connect()
	{
		if (LogManager.getLogManager().getLogger("colibri") != null)
		{
			LogManager.getLogManager().getLogger("colibri").info("...Galserve initialisieren...");
		}
		this.server = ProductServer.getInstance();
		if (LogManager.getLogManager().getLogger("colibri") != null)
		{
			LogManager.getLogManager().getLogger("colibri").info("...Benutzungsstatus von Galserve prüfen...");
		}
		if (ProductServer.isUsed() && this.server != null)
		{
			if (LogManager.getLogManager().getLogger("colibri") != null)
			{
				LogManager.getLogManager().getLogger("colibri").info("...Galserve-Modus (WWS?) prüfen...");
			}
			if (this.server.isActive())
			{
				if (this.server.getUpdate() == 1 || this.server.getUpdate() > 0)
				{
					if (LogManager.getLogManager().getLogger("colibri") != null)
					{
						LogManager.getLogManager().getLogger("colibri").info("...Initialisieren Ok!");
					}
					return true;
				}
			}
		}
		if (LogManager.getLogManager().getLogger("colibri") != null)
		{
			LogManager.getLogManager().getLogger("colibri").info("...Initialisieren fehlgeschlagen!");
		}
		return false;
	}
	
	private void disconnect()
	{
		if (LogManager.getLogManager().getLogger("colibri") != null)
		{
			LogManager.getLogManager().getLogger("colibri").info("...Verbindung zu Galserve schliessen...");
		}
		this.server.disconnect();
		if (LogManager.getLogManager().getLogger("colibri") != null)
		{
			LogManager.getLogManager().getLogger("colibri").info("...Verbindung geschlossen...");
		}
	}
	
	public void run()
	{
		try
		{
			this.run(this.monitor);
		}
		catch (InvocationTargetException e)
		{
		}
		catch (InterruptedException e)
		{
		}
		finally
		{
		}
	}
	
	public UpdateGalileoProgressMonitor getMonitor()
	{
		return this.monitor;
	}
}
