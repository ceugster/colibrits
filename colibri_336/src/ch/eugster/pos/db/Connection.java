/*
 * Created on 16.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.db;

import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.JOptionPane;

import org.apache.ojb.broker.PBFactoryException;
import org.apache.ojb.broker.PBKey;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.apache.ojb.broker.PersistenceBrokerFactory;
import org.apache.ojb.broker.metadata.ConnectionRepository;
import org.apache.ojb.broker.metadata.JdbcConnectionDescriptor;
import org.apache.ojb.broker.metadata.MetadataManager;
import org.apache.ojb.broker.metadata.SequenceDescriptor;
import org.apache.ojb.broker.platforms.PlatformMySQLImpl;
import org.jdom.Element;

import ch.eugster.pos.events.InitializationListener;
import ch.eugster.pos.events.ShowCoinCounterAction;
import ch.eugster.pos.util.Config;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class Connection
{
	
	/**
	 * 
	 */
	public Connection()
	{
		super();
	}
	
	public Connection(Element element)
	{
		super();
		this.setId(element.getName());
		this.setName(element.getAttributeValue("name")); //$NON-NLS-1$
		this.setActive(new Boolean(element.getAttributeValue("active")).booleanValue()); //$NON-NLS-1$
	}
	
	public Connection(String id, String name)
	{
		super();
		this.setId(id);
		this.setName(name);
	}
	
	public boolean openConnection()
	{
		ConnectionRepository cr = MetadataManager.getInstance().connectionRepository();
		Element db = Config.getInstance().getDatabase(this.id).getChild("connection");
		PBKey key = new PBKey(this.id, db.getAttributeValue("username"), db.getAttributeValue("password"));
		JdbcConnectionDescriptor jcd = new JdbcConnectionDescriptor();
		this.setConnection(jcd, db);
		cr.addDescriptor(jcd);
		//		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(Messages.getString("Connection.Broker_f_u00FCr_4") + " " + getName() + " " + Messages.getString("Connection.initialisieren..._7")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		try
		{
			this.broker = PersistenceBrokerFactory.createPersistenceBroker(key);
			//
			// @@@2005-09-21:
			// updateDB erfolgte vor Test, ob Verbindung wirklich geklappt hat.
			// Mit beginTransactin wird nun zuerst eine Exception provoziert
			// und nur wenn die Verbindung erfolgreich ist, wird versucht, die
			// DB zu aktualisieren.
			this.broker.beginTransaction();
			this.broker.abortTransaction();
			// @@@2005-09-21:
			
			this.setConnected(true);
		}
		catch (PBFactoryException e)
		{
			//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(Messages.getString("Connection.Broker_f_u00FCr_38") + " " + getName() + " " + Messages.getString("Connection.konnte_nicht_initialisiert_werden__41")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			String[] msg = e.getMessages();
			for (int i = 0; i < msg.length; i++)
			{
				//				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(msg[i]); //$NON-NLS-1$
			}
			this.setConnected(false);
			// setActive(false);
		}
		catch (PersistenceBrokerException e)
		{
			if (this.broker.isInTransaction()) this.broker.abortTransaction();
			//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(Messages.getString("Connection.Broker_f_u00FCr_44") + " " + getName() + " " + Messages.getString("Connection.konnte_nicht_initialisiert_werden__47")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			String[] msg = e.getMessages();
			for (int i = 0; i < msg.length; i++)
			{
				//				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(msg[i]); //$NON-NLS-1$
			}
			this.setConnected(false);
			// setActive(false);
		}
		if (this.isConnected())
		{
			/*
			 * Gegebenenfalls Datenbank aktualisieren
			 */
			this.updateDB();
			
			//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(Messages.getString("Connection.Datenbankversion_von_28") + " " + getName() + " " + Messages.getString("Connection.abrufen..._31")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			try
			{
				this.broker.beginTransaction();
				this.version = Version.select(this);
				this.broker.commitTransaction();
			}
			catch (PersistenceBrokerException e)
			{
				if (this.broker.isInTransaction()) this.broker.abortTransaction();
			}
			finally
			{
			}
			//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(Messages.getString("Connection.Verbindung_zu_33") + " " + getName() + " " + Messages.getString("Connection.erfolgreich_hergestellt..._36")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		}
		
		/*
		 * keep connection open
		 */
		this.heartbeat();
		
		return this.isConnected();
	}
	
	public String getConnectionString()
	{
		return this.connectionString;
	}
	
	public void setConnection(JdbcConnectionDescriptor cd, Element el)
	{
		cd.setJcdAlias(el.getAttributeValue("jcd-alias")); //$NON-NLS-1$
		cd.setDefaultConnection(Boolean.getBoolean(el.getAttributeValue("default-connection"))); //$NON-NLS-1$
		cd.setDbms(el.getAttributeValue("platform")); //$NON-NLS-1$
		cd.setJdbcLevel(el.getAttributeValue("jdbc-level")); //$NON-NLS-1$
		cd.setDriver(el.getAttributeValue("driver")); //$NON-NLS-1$
		cd.setProtocol(el.getAttributeValue("protocol")); //$NON-NLS-1$
		cd.setSubProtocol(el.getAttributeValue("subprotocol")); //$NON-NLS-1$
		StringBuffer sb = new StringBuffer("//"); //$NON-NLS-1$
		sb.append(el.getAttributeValue("host")); //$NON-NLS-1$
		sb.append(":"); //$NON-NLS-1$
		sb.append(el.getAttributeValue("port")); //$NON-NLS-1$
		if (el.getAttributeValue("database").length() > 0) { //$NON-NLS-1$
			sb.append("/"); //$NON-NLS-1$
			sb.append(el.getAttributeValue("database")); //$NON-NLS-1$
		}
		sb.append(el.getAttributeValue("options")); //$NON-NLS-1$
		this.connectionString = sb.toString();
		cd.setDbAlias(this.connectionString);
		cd.setUserName(el.getAttributeValue("username")); //$NON-NLS-1$
		cd.setPassWord(el.getAttributeValue("password")); //$NON-NLS-1$
		cd.setBatchMode(Boolean.getBoolean(el.getAttributeValue("batch-mode"))); //$NON-NLS-1$
		cd.setUseAutoCommit(Integer.parseInt(el.getAttributeValue("use-auto-commit"))); //$NON-NLS-1$
		SequenceDescriptor sd = new SequenceDescriptor(cd);
		sd.setSequenceManagerClass(org.apache.ojb.broker.util.sequence.SequenceManagerHighLowImpl.class);
		sd.addAttribute("grabSize", "20");
	}
	
	public void setActive(boolean active)
	{
		this.active = active;
	}
	
	public boolean isActive()
	{
		return this.active;
	}
	
	public void setConnected(boolean connected)
	{
		this.connected = connected;
	}
	
	public boolean isConnected()
	{
		return this.connected;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	
	public String getId()
	{
		return this.id;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void setBroker(PersistenceBroker broker)
	{
		this.broker = broker;
	}
	
	public PersistenceBroker getBroker()
	{
		return this.broker;
	}
	
	public boolean isStandard()
	{
		return this instanceof Standard ? true : false;
	}
	
	public boolean isTemporary()
	{
		return this instanceof Temporary ? true : false;
	}
	
	public boolean isTutorial()
	{
		return this instanceof Tutorial ? true : false;
	}
	
	public boolean testTables()
	{
		return true;
	}
	
	protected abstract Element getConnectionData();
	
	private boolean updateDB()
	{
		if (this.getBroker().serviceSqlGenerator().getPlatform() instanceof PlatformMySQLImpl)
		{
			return this.updateMySql();
		}
		else
		{
			return false;
		}
	}
	
	private boolean updateMySql()
	{
		boolean done = true;
		JdbcConnectionDescriptor dr = this.getBroker().serviceConnectionManager().getConnectionDescriptor();
		String url = "jdbc:mysql:" + dr.getDbAlias();
		Properties props = new Properties();
		props.setProperty("user", dr.getUserName());
		props.setProperty("password", dr.getPassWord());
		
		this.logger = Logger.getLogger("updateDB");
		this.logger.setLevel(this.level);
		
		try
		{
			this.fh = new FileHandler("log/updateDB.log", true);
			this.fh.setFormatter(new SimpleFormatter());
			this.logger.addHandler(this.fh);
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(null,
							"Die Protokollierung für den Datenbankupdate konnte nicht initialisiert werden.");
			System.exit(-10);
		}
		
		java.sql.Connection con = null;
		PreparedStatement stm = null;
		ResultSet rst = null;
		
		int v = 0;
		int dataVersion = 0;
		String engine = "TYPE";
		
		try
		{
			Driver drv = new com.mysql.jdbc.Driver();
			con = drv.connect(url, props);
			
			int databaseVersion = con.getMetaData().getDatabaseMajorVersion();
			engine = "TYPE";
			if (databaseVersion > 4)
			{
				engine = "ENGINE";
			}
			
			stm = con.prepareStatement("SELECT data_version FROM pos_version ORDER BY data_version DESC;",
							ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			rst = stm.executeQuery();
			rst.first();
			dataVersion = rst.getInt("data_version");
			
			// 10079
			if (dataVersion < Version.getMyDataVersion())
			{
				this.logger.setLevel(Level.INFO);
			}
			// 10079
			this.logger.info("Testing Version of Database " + this.getName() + "...");
			if (dataVersion > Version.getMyDataVersion())
			{
				this.logger.info("Database Version higher (" + this.version + ") than Program Version ("
								+ Version.getMyDataVersion() + "). Program exits.");
				JOptionPane.showMessageDialog(null,
								"Die Programmversion ist nicht mit der Datenversion der Datenbank kompatibel. Sie müssen das Programm aktualisieren.");
				System.exit(-10);
			}
			
			this.logger.info("Database Version: " + dataVersion + "; Program Version: " + Version.getMyDataVersion()
							+ ".");
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			this.logger.severe(e.getLocalizedMessage());
			JOptionPane.showMessageDialog(null,
							"Die Datenbankverbindung für den Datenbankupdate konnte nicht initialisiert werden.");
			System.exit(-10);
		}
		
		v = 0;
		try
		{
			while (v < Version.getMyDataVersion())
			{
				v++;
				// 10107
				this.fireInitialized("Datenbank auf Version " + v + " aktualisieren...");
				// 10107
				
				if (v == 1 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					done = done
									&& this.executeSQLStatement(con,
													"ALTER TABLE pos_payment_type ADD COLUMN is_voucher tinyint(1) NOT NULL default '0';");
					done = done
									&& this.executeSQLStatement(con,
													"ALTER TABLE pos_payment_type ADD COLUMN is_payment_voucher tinyint(1) NOT NULL default '0';");
					done = done
									&& this.executeSQLStatement(con,
													"ALTER TABLE pos_product_group ADD COLUMN is_income tinyint(1) NOT NULL default '0' AFTER account;");
					done = done
									&& this.executeSQLStatement(con,
													"ALTER TABLE pos_product_group ADD COLUMN modified tinyint(1) NOT NULL default '0';");
					done = done
									&& this.executeSQLStatement(con,
													"ALTER TABLE pos_receipt ADD INDEX idx_number (number);");
					done = done
									&& this.executeSQLStatement(con,
													"ALTER TABLE pos_foreign_currency ADD COLUMN account VARCHAR(100) NOT NULL DEFAULT '' AFTER quotation;");
					done = done
									&& this.executeSQLStatement(
													con,
													"INSERT INTO pos_function VALUES (16,now(),'ch.eugster.pos.client.event.StoreReceiptVoucherAction',102,'Belegabschluss mit Gutscheinrckgabe','Gutscheinabschluss');");
					dataVersion = v;
					rst.updateInt("data_version", dataVersion);
					rst.updateRow();
					this.logger.info("Update to Version " + v + " successfully.");
				}
				
				if (v == 2 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					done = done
									&& this.executeSQLStatement(con,
													"ALTER TABLE pos_product_group ADD COLUMN type integer(4) NOT NULL default '0';");
					done = done && this.executeSQLStatement(con, "UPDATE pos_product_group SET type = 1;");
					done = done
									&& this.executeSQLStatement(con,
													"UPDATE pos_product_group SET type = 2 WHERE is_expense = 1;");
					done = done
									&& this.executeSQLStatement(con,
													"UPDATE pos_product_group SET type = 0 WHERE is_income = 1;");
					dataVersion = v;
					rst.updateInt("data_version", dataVersion);
					rst.updateRow();
					this.logger.info("Update to Version " + v + " successfully.");
				}
				
				if (v == 3 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					done = done
									&& this.executeSQLStatement(
													con,
													"INSERT INTO pos_function VALUES (7,now(),'ch.eugster.pos.client.event.SalesPerUserAction',901,'Zeigt den Umsatz des aktuellen Benutzers an','Umsatz Benutzer');");
					done = done
									&& this.executeSQLStatement(
													con,
													"INSERT INTO pos_function VALUES (17,now(),'ch.eugster.pos.client.event.SalesPerSalespointAction',901,'Zeigt den Umsatz der aktuellen Kasse an','Umsatz Kasse');");
					dataVersion = v;
					rst.updateInt("data_version", dataVersion);
					rst.updateRow();
					this.logger.info("Update to Version " + v + " successfully.");
				}
				
				if (v == 4 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					done = done
									&& this.executeSQLStatement(con,
													"ALTER TABLE pos_key ADD COLUMN set_default_tab tinyint(1) NOT NULL default '0';");
					dataVersion = v;
					rst.updateInt("data_version", dataVersion);
					rst.updateRow();
					this.logger.info("Update to Version " + v + " successfully.");
				}
				
				if (v == 5 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					done = done
									&& this.executeSQLStatement(con,
													"ALTER TABLE pos_version ADD COLUMN transaction_id bigint(20) NOT NULL default '0';");
					dataVersion = v;
					rst.updateInt("data_version", dataVersion);
					rst.updateRow();
					this.logger.info("Update to Version " + v + " successfully.");
				}
				
				// Start Build 76
				if (v == 6 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					done = done
									&& this.executeSQLStatement(con,
													"ALTER TABLE pos_product_group ADD COLUMN export_id varchar(10) NOT NULL default '';");
					done = done
									&& this.executeSQLStatement(con,
													"ALTER TABLE pos_payment_type ADD COLUMN export_id varchar(10) NOT NULL default '';");
					dataVersion = v;
					rst.updateInt("data_version", dataVersion);
					rst.updateRow();
					this.logger.info("Update to Version " + v + " successfully.");
				}
				// End Build 76
				
				// Start Build 78
				if (v == 7 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					done = done
									&& this.executeSQLStatement(con,
													"ALTER TABLE pos_salespoint ADD COLUMN export_id varchar(10) NOT NULL default '';");
					dataVersion = v;
					rst.updateInt("data_version", dataVersion);
					rst.updateRow();
					this.logger.info("Update to Version " + v + " successfully.");
				}
				// End Build 78
				
				// Start Build 90
				if (v == 8 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					if (!this.columnExists(con, "pos_receipt", "customer_id"))
					{
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_receipt ADD COLUMN customer_id varchar(13) NOT NULL default '';");
					}
					if (!this.columnExists(con, "pos_position", "stock"))
					{
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_position ADD COLUMN stock tinyint(1) NOT NULL default '0';");
					}
					dataVersion = v;
					rst.updateInt("data_version", dataVersion);
					rst.updateRow();
					this.logger.info("Update to Version " + v + " successfully.");
				}
				
				if (v == 9 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					if (!this.rowExists(con, "pos_function", "id", new Long(18)))
					{
						done = done
										&& this.executeSQLStatement(
														con,
														"INSERT INTO pos_function VALUES (18,NOW(),'ch.eugster.pos.client.event.ChooseCustomerAction',0,'Öffnet das Kundenauswahlfenster','Kunden');");
					}
					dataVersion = v;
					rst.updateInt("data_version", dataVersion);
					rst.updateRow();
					this.logger.info("Update to Version " + v + " successfully.");
				}
				
				if (v == 10 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					if (!this.columnExists(con, "pos_fixkey", "bg_red_failover"))
					{
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_fixkey ADD COLUMN bg_red_failover int(3) NOT NULL default '255';");
					}
					if (!this.columnExists(con, "pos_fixkey", "bg_green_failover"))
					{
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_fixkey ADD COLUMN bg_green_failover int(3) NOT NULL default '255';");
					}
					if (!this.columnExists(con, "pos_fixkey", "bg_blue_failover"))
					{
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_fixkey ADD COLUMN bg_blue_failover int(3) NOT NULL default '255';");
					}
					done = done
									&& this.executeSQLStatement(con,
													"UPDATE pos_fixkey SET bg_red_failover = 255, bg_green_failover = 222, bg_blue_failover = 222;");
					dataVersion = v;
					rst.updateInt("data_version", dataVersion);
					rst.updateRow();
					this.logger.info("Update to Version " + v + " successfully.");
				}
				
				if (v == 11 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					if (!this.columnExists(con, "pos_receipt", "transferred"))
					{
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_receipt ADD COLUMN transferred tinyint NULL default '0';");
					}
					done = done && this.executeSQLStatement(con, "UPDATE pos_receipt SET transferred = 0;");
					dataVersion = v;
					rst.updateInt("data_version", dataVersion);
					rst.updateRow();
					this.logger.info("Update to Version " + v + " successfully.");
				}
				
				// Build 123
				if (v == 12 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					if (!this.columnExists(con, "pos_user", "default_user"))
					{
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_user ADD COLUMN default_user tinyint NULL default '0';");
					}
					done = done && this.executeSQLStatement(con, "UPDATE pos_user SET default_user = 0;");
					done = done && this.executeSQLStatement(con, "UPDATE pos_user SET default_user = 1 WHERE id = 1;");
					dataVersion = v;
					rst.updateInt("data_version", dataVersion);
					rst.updateRow();
					this.logger.info("Update to Version " + v + " successfully.");
				}
				
				// Build 147
				if (v == 13 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					this.updateEventColumns(con, "pos_fixkey", "class_name");
					this.updateEventColumns(con, "pos_key", "class_name");
					this.updateEventColumns(con, "pos_function", "class");
					this.updateEventColumns(con, "pos_function", "class");
					this.updateEventColumns(con, "pos_key_group", "action_class");
					dataVersion = v;
					rst.updateInt("data_version", dataVersion);
					rst.updateRow();
					this.logger.info("Update to Version " + v + " successfully.");
				}
				
				// Build 148
				if (v == 14 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					if (con.getCatalog().equals(
									Config.getInstance().getDatabaseStandardConnection().getAttributeValue("database")))
					{
						PreparedStatement ps = con.prepareStatement("SELECT MAX(id) AS id FROM pos_function;",
										ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
						ResultSet rs = ps.executeQuery();
						if (rs.next())
						{
							long id = rs.getLong("id");
							ps = con.prepareStatement(
											"INSERT INTO pos_function (id, timestamp, class, action_type, name, shortname) VALUES ("
															+ ++id
															+ ", NOW(), 'ch.eugster.pos.events.PrintLastReceiptAction', 0, 'Letzten Beleg drucken', 'Drucken');",
											ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
							ps.execute();
						}
					}
					dataVersion = v;
					rst.updateInt("data_version", dataVersion);
					rst.updateRow();
					this.logger.info("Update to Version " + v + " successfully.");
				}
				
				// Build 149
				if (v == 15 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					if (con.getCatalog().equals(
									Config.getInstance().getDatabaseStandardConnection().getAttributeValue("database")))
					{
						PreparedStatement ps = con.prepareStatement("UPDATE pos_function SET class = '"
										+ ShowCoinCounterAction.class.getName()
										+ "' WHERE class = 'ch.eugster.pos.events.ShowCoinCounter';",
										ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
						ps.execute();
						ps = con.prepareStatement(
										"UPDATE pos_key SET class_name = '"
														+ ShowCoinCounterAction.class.getName()
														+ "' WHERE class_name = 'ch.eugster.pos.events.ShowCoinCounter';",
										ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
						ps.execute();
					}
					dataVersion = v;
					rst.updateInt("data_version", dataVersion);
					rst.updateRow();
					this.logger.info("Update to Version " + v + " successfully.");
				}
				
				// Build 177 10183
				if (v == 16 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					if (!this.columnExists(con, "pos_salespoint", "variable_stock"))
					{
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_salespoint ADD COLUMN variable_stock tinyint default '0';");
						done = done && this.executeSQLStatement(con, "UPDATE pos_salespoint SET variable_stock = 1");
					}
					if (done)
					{
						dataVersion = v;
						rst.updateInt("data_version", dataVersion);
						rst.updateRow();
						this.logger.info("Update to Version " + v + " successfully.");
					}
				}
				
				// Build 186 10203
				if (v == 17 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					if (!this.columnExists(con, "pos_position", "update_customer_account"))
					{
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_position ADD COLUMN update_customer_account tinyint default '0';");
						done = done
										&& this.executeSQLStatement(
														con,
														"UPDATE pos_position, pos_receipt SET update_customer_account = 1 WHERE pos_position.receipt_id = pos_receipt.id AND pos_receipt.customer_id <> ''");
						
						PreparedStatement ps = con.prepareStatement("SELECT MAX(id) AS id FROM pos_function;",
										ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
						ResultSet rs = ps.executeQuery();
						if (rs.next())
						{
							long id = rs.getLong("id");
							ps = con.prepareStatement(
											"INSERT INTO pos_function (id, timestamp, class, action_type, name, shortname) VALUES ("
															+ ++id
															+ ", NOW(), 'ch.eugster.pos.events.UpdateCustomerAccountAction', 206, 'Position auf Kundenkarte (nicht) verbuchen', 'Titel KK +-');",
											ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
							ps.execute();
						}
					}
					if (done)
					{
						dataVersion = v;
						rst.updateInt("data_version", dataVersion);
						rst.updateRow();
						this.logger.info("Update to Version " + v + " successfully.");
					}
				}
				// Build 195 10215
				if (v == 18 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					done = done && this.executeSQLStatement(con, "UPDATE pos_product_group SET is_income = 0;");
					if (done)
					{
						dataVersion = v;
						rst.updateInt("data_version", dataVersion);
						rst.updateRow();
						this.logger.info("Update to Version " + v + " successfully.");
					}
				}
				// Build 196 10217
				if (v == 19 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					done = done
									&& this.executeSQLStatement(con,
													"ALTER TABLE pos_payment_type ADD COLUMN open_cashdrawer tinyint default '1';");
					done = done && this.executeSQLStatement(con, "UPDATE pos_payment_type SET open_cashdrawer = 1");
					if (done)
					{
						dataVersion = v;
						rst.updateInt("data_version", dataVersion);
						rst.updateRow();
						this.logger.info("Update to Version " + v + " successfully.");
					}
				}
				// 10217
				
				// Build 198 10221
				if (v == 20 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					done = done
									&& this.executeSQLStatement(con,
													"ALTER TABLE pos_position ADD COLUMN payed_invoice tinyint default '0';");
					done = done
									&& this.executeSQLStatement(con,
													"ALTER TABLE pos_position ADD COLUMN invoice integer default '0';");
					done = done
									&& this.executeSQLStatement(con,
													"ALTER TABLE pos_position ADD COLUMN invoice_date datetime;");
					done = done
									&& this.executeSQLStatement(con,
													"UPDATE pos_position SET payed_invoice = 0, invoice = 0");
					if (done)
					{
						dataVersion = v;
						rst.updateInt("data_version", dataVersion);
						rst.updateRow();
						this.logger.info("Update to Version " + v + " successfully.");
					}
				}
				// 10221
				
				// Datenversion 21: Build 201: 10226
				if (v == 21 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					if (!this.tableExists(con, "pos_stock"))
					{
						done = done
										&& this.executeSQLStatement(
														con,
														"CREATE TABLE pos_stock (id bigint(20) unsigned NOT NULL default '0', salespoint_id bigint(20) unsigned NOT NULL, foreign_currency_id bigint(20) unsigned NOT NULL, stock double default '0', PRIMARY KEY  (id), KEY index_salespoint (salespoint_id), KEY index_foreign_currency (foreign_currency_id)) "
																		+ engine + " = InnoDB;");
					}
					if (!this.columnExists(con, "pos_coin", "foreign_currency_id"))
					{
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_coin ADD COLUMN foreign_currency_id bigint(20) default '0';");
						
						if (done)
						{
							/*
							 * Default Währung auf bereits erfassten Münzen
							 * setzen (CHF)
							 */
							PreparedStatement ps = con.prepareStatement(
											"SELECT id AS id FROM pos_foreign_currency WHERE code = 'CHF';",
											ResultSet.TYPE_FORWARD_ONLY);
							ResultSet rs = ps.executeQuery();
							if (rs.next())
							{
								long id = rs.getLong("id");
								this.executeSQLStatement(con, "UPDATE pos_coin SET foreign_currency_id = " + id + ";");
							}
						}
					}
					if (!this.columnExists(con, "pos_payment", "back"))
					{
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_payment ADD COLUMN back tinyint(1) default '0';");
					}
					if (!this.columnExists(con, "pos_payment_type", "back"))
					{
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_payment_type ADD COLUMN back tinyint(1) default '0';");
					}
					
					PreparedStatement ps = con.prepareStatement(
									"SELECT id AS id FROM pos_foreign_currency WHERE code = 'CHF';",
									ResultSet.TYPE_FORWARD_ONLY);
					ResultSet rs = ps.executeQuery();
					if (rs.next())
					{
						long id = rs.getLong("id");
						done = done
										&& this.executeSQLStatement(
														con,
														"UPDATE pos_payment SET back = 1, payment_type_id = 1, foreign_currency_id = "
																		+ id
																		+ ", amount_fc = amount, quotation = 1, round_factor = .05 WHERE payment_type_id = 2;");
						if (this.columnExists(con, "pos_payment_type", "is_payment_voucher"))
						{
							done = done
											&& this.executeSQLStatement(con,
															"UPDATE pos_payment_type SET back = 1 WHERE is_payment_voucher = 1;");
						}
						done = done
										&& this.executeSQLStatement(con,
														"UPDATE pos_payment_type SET back = 1 WHERE foreign_currency_id <> "
																		+ id + ";");
						done = done && this.executeSQLStatement(con, "DELETE FROM pos_payment_type WHERE id = 2;");
					}
					if (!this.columnExists(con, "pos_key", "payment_type_id"))
					{
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_key ADD COLUMN payment_type_id bigint(20) default '0';");
						done = done
										&& this.executeSQLStatement(con,
														"UPDATE pos_key SET payment_type_id = 1 WHERE payment_type_id = 2;");
					}
					if (this.columnExists(con, "pos_payment_type", "is_payment_voucher"))
					{
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_payment_type DROP COLUMN is_payment_voucher;");
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_payment_type ADD COLUMN cash tinyint(1) default '0';");
						done = done
										&& this.executeSQLStatement(con,
														"UPDATE pos_payment_type SET cash = 1 WHERE id = 1;");
					}
					if (!this.columnExists(con, "pos_position", "tax"))
					{
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_position ADD COLUMN tax double default '0';");
						
						if (done)
						{
							/*
							 * Tax auf bestehenden Positionen berechnen
							 */
							ps = con.prepareStatement(
											"SELECT round_factor AS round_factor FROM pos_foreign_currency WHERE code = 'CHF';",
											ResultSet.TYPE_FORWARD_ONLY);
							rs = ps.executeQuery();
							if (rs.next())
							{
								Collection currentTaxes = new ArrayList();
								
								ps = con.prepareStatement(
												"SELECT id AS id, percentage AS percentage FROM pos_current_tax;",
												ResultSet.TYPE_FORWARD_ONLY);
								rs = ps.executeQuery();
								
								while (rs.next())
								{
									Object[] object = new Object[2];
									object[0] = new Long(rs.getLong("id"));
									object[1] = new Double(rs.getDouble("percentage"));
									currentTaxes.add(object);
								}
								rs.close();
								ps.close();
								
								Iterator iter = currentTaxes.iterator();
								while (iter.hasNext())
								{
									Object[] object = (Object[]) iter.next();
									Long pid = (Long) object[0];
									
									Double p = (Double) object[1];
									done = done
													&& this.executeSQLStatement(con,
																	"UPDATE pos_position SET tax = -round(price * (1 - discount) * quantity / "
																					+ (100d + p.doubleValue()) + " * "
																					+ p
																					+ ", 2) WHERE current_tax_id = "
																					+ pid + ";");
								}
							}
						}
					}
					done = done
									&& this.executeSQLStatement(
													con,
													"INSERT INTO pos_fixkey (timestamp, fix_key_group_id, block, text_editable, row, col, bg_red, bg_green, bg_blue, fg_red, fg_green, fg_blue, font_size, font_style, align, valign, image_path, rel_horizontal_text_pos, rel_vertical_text_pos, name, command, class_name, action_type, bg_red_failover, bg_green_failover, bg_blue_failover) VALUES (now(), 6, 'ch.eugster.pos.client.gui.CoinCounter', 0, 0, 2, 255, 255, 255, 0, 0, 0, 12, 1, 0, 0, '', 0, 0, 'Leeren', 'clear','ch.eugster.pos.events.ClearAllCoinsAction', 0, 255, 222, 222);",
													true);
					
					if (done)
					{
						dataVersion = v;
						rst.updateInt("data_version", dataVersion);
						rst.updateRow();
						this.logger.info("Update to Version " + v + " successfully.");
					}
				}
				// Datenversion 21: Build 201: 10226
				
				// Datenversion 22: Build 204: 10227
				if (v == 22 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					if (!this.columnExists(con, "pos_payment", "settlement"))
					{
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_payment ADD COLUMN settlement bigint(20) NULL;");
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_payment ADD INDEX idx_settlement (settlement);");
					}
					if (!this.columnExists(con, "pos_payment", "salespoint_id"))
					{
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_payment ADD COLUMN salespoint_id bigint(20) NULL;");
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_payment ADD INDEX idx_salespoint_id (salespoint_id);");
					}
					done = done
									&& this.executeSQLStatement(con,
													"UPDATE pos_payment SET amount_fc = round(amount / quotation, 2) WHERE quotation <> 1;");
					if (done)
					{
						dataVersion = v;
						rst.updateInt("data_version", dataVersion);
						rst.updateRow();
						this.logger.info("Update to Version " + v + " successfully.");
					}
				}
				// Datenversion 22: Build 204: 10227
				
				// Datenversion 23: Build 208: 10231
				if (v == 23 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					if (!this.columnExists(con, "pos_product_group", "withdraw"))
					{
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_product_group ADD COLUMN withdraw tinyint(1) NULL DEFAULT '0';");
						done = done && this.executeSQLStatement(con, "UPDATE pos_product_group SET withdraw = 0;");
					}
					if (!this.columnExists(con, "pos_product_group", "paid_invoice"))
					{
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_product_group ADD COLUMN paid_invoice tinyint(1) NULL DEFAULT '0';");
						done = done
										&& this.executeSQLStatement(con,
														"UPDATE pos_product_group SET paid_invoice = is_income;");
					}
					if (!this.columnExists(con, "pos_product_group", "is_default"))
					{
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_product_group ADD COLUMN is_default tinyint(1) NULL DEFAULT '0';");
						done = done
										&& this.executeSQLStatement(con,
														"UPDATE pos_product_group SET is_default = default_group;");
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_product_group DROP COLUMN default_group;");
					}
					if (this.columnExists(con, "pos_product_group", "is_expense"))
					{
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_product_group DROP COLUMN is_expense;");
					}
					if (this.columnExists(con, "pos_product_group", "is_income"))
					{
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_product_group DROP COLUMN is_income;");
					}
					if (done)
					{
						dataVersion = v;
						rst.updateInt("data_version", dataVersion);
						rst.updateRow();
						this.logger.info("Update to Version " + v + " successfully.");
					}
				}
				// Datenversion 23: Build 208: 10231
				
				// Datenversion 24: Build 208: 10231
				if (v == 24 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					done = done
									&& this.executeSQLStatement(con,
													"ALTER TABLE pos_product_group MODIFY COLUMN quantity_proposal INTEGER NOT NULL DEFAULT 0;");
					if (!this.columnExists(con, "pos_position", "type"))
					{
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_position ADD COLUMN type INTEGER NULL DEFAULT 0;");
						done = done
										&& this.executeSQLStatement(con,
														"UPDATE pos_position SET type = 2 WHERE expense = 1;");
					}
					if (this.columnExists(con, "pos_position", "expense"))
					{
						done = done && this.executeSQLStatement(con, "ALTER TABLE pos_position DROP COLUMN expense;");
					}
					if (done)
					{
						dataVersion = v;
						rst.updateInt("data_version", dataVersion);
						rst.updateRow();
						this.logger.info("Update to Version " + v + " successfully.");
					}
				}
				// Datenversion 24: Build 208: 10231
				
				// Datenversion 25: Build 211: 10236
				if (v == 25 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					if (!this.columnExists(con, "pos_product_group", "foreign_currency_id"))
					{
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_product_group ADD COLUMN foreign_currency_id BIGINT(20) NULL;");
						done = done
										&& this.executeSQLStatement(con,
														"UPDATE pos_product_group SET foreign_currency_id = 22;");
					}
					if (done)
					{
						dataVersion = v;
						rst.updateInt("data_version", dataVersion);
						rst.updateRow();
						this.logger.info("Update to Version " + v + " successfully.");
					}
				}
				// Datenversion 25: Build 211: 10236
				
				// Datenversion 26: Build 212: 10236
				if (v == 26 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					if (!this.columnExists(con, "pos_position", "amount_fc"))
					{
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_position ADD COLUMN amount_fc DOUBLE NULL DEFAULT '0';");
						done = done && this.executeSQLStatement(con, "UPDATE pos_position SET amount_fc = 0;");
					}
					if (done)
					{
						dataVersion = v;
						rst.updateInt("data_version", dataVersion);
						rst.updateRow();
						this.logger.info("Update to Version " + v + " successfully.");
					}
				}
				// Datenversion 26: Build 212: 10236
				
				// Datenversion 27: Build 237: 10283
				if (v == 27 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					if (!this.tableExists(con, "pos_setting"))
					{
						done = done
										&& this.executeSQLStatement(
														con,
														"CREATE TABLE  pos_setting (id bigint(20) unsigned NOT NULL default '0',com_use tinyint(1) unsigned default '0',com_classname varchar(255) default NULL,com_hold tinyint(1) unsigned default '0',com_update int(10) unsigned default '0',com_path varchar(255) default NULL,com_show_add_customer_message tinyint(1) unsigned default '0',com_search_cd tinyint(1) unsigned default '0',com_cd_path varchar(255) default NULL,PRIMARY KEY  (id)) "
																		+ engine + " = InnoDB;");
					}
					if (!this.rowExists(con, "pos_setting", "id", new Long(1l)))
					{
						String sql = "INSERT INTO pos_setting VALUES (1,"
										+ (Config.getInstance().getProductServerUseXML() ? "1,'" : "0,'")
										+ Config.getInstance().getProductServerClassXML() + "',"
										+ (Config.getInstance().getProductServerHoldXML() ? "1" : "0") + ","
										+ Config.getInstance().getGalileoUpdateXML() + ",'"
										+ Config.getInstance().getGalileoPathXML() + "',"
										+ (Config.getInstance().getGalileoShowAddCustomerMessageXML() ? "1," : "0,")
										+ (Config.getInstance().getGalileoSearchCdXML() ? "1,'" : "0,'")
										+ Config.getInstance().getGalileoCdPathXML() + "');";
						
						sql = sql.replaceAll("\\\\", "/");
						done = done && this.executeSQLStatement(con, sql);
					}
					if (done)
					{
						dataVersion = v;
						rst.updateInt("data_version", dataVersion);
						rst.updateRow();
						this.logger.info("Update to Version " + v + " successfully.");
					}
				}
				// Datenversion 27: Build 237: 10283
				
				// Datenversion 28: Build 289: 10349
				if (v == 28 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					String sql = "UPDATE pos_block SET visible = 1, name = 'Münzzähler' WHERE class = 'ch.eugster.pos.client.gui.CoinCounter';";
					done = done && this.executeSQLStatement(con, sql);
					
					if (done)
					{
						dataVersion = v;
						rst.updateInt("data_version", dataVersion);
						rst.updateRow();
						this.logger.info("Update to Version " + v + " successfully.");
					}
				}
				// Datenversion 28: Build 289: 10349
				
				// Datenversion 29: Build 296: 10359
				if (v == 29 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					if (!this.columnExists(con, "pos_user", "reverse_receipts"))
					{
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_user ADD COLUMN reverse_receipts TINYINT(1) NULL;");
						done = done && this.executeSQLStatement(con, "UPDATE pos_user SET reverse_receipts = 1;");
					}
					if (done)
					{
						dataVersion = v;
						rst.updateInt("data_version", dataVersion);
						rst.updateRow();
						this.logger.info("Update to Version " + v + " successfully.");
					}
				}
				// Datenversion 29: Build 296: 10359
				
				// Datenversion 30: Build 304: 10371
				if (v == 30 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					
					Collection currentTaxes = new ArrayList();
					
					PreparedStatement ps = con.prepareStatement(
									"SELECT id AS id, percentage AS percentage FROM pos_current_tax;",
									ResultSet.TYPE_FORWARD_ONLY);
					ResultSet rs = ps.executeQuery();
					
					while (rs.next())
					{
						Object[] object = new Object[2];
						object[0] = new Long(rs.getLong("id"));
						object[1] = new Double(rs.getDouble("percentage"));
						currentTaxes.add(object);
					}
					rs.close();
					ps.close();
					
					Iterator iter = currentTaxes.iterator();
					while (iter.hasNext())
					{
						Object[] object = (Object[]) iter.next();
						Long pid = (Long) object[0];
						
						Double p = (Double) object[1];
						
						StringBuffer sb = new StringBuffer("UPDATE pos_position ");
						String amount = "round(quantity * price * (1 - discount) / .05) * .05";
						String tax = "(100 + " + p.toString() + ") * " + p.toString();
						sb = sb.append("SET tax = -round(" + amount + " / " + tax + ", 2) ");
						sb = sb.append("WHERE current_tax_id = " + pid + ";");
						String sql = sb.toString();
						
						done = done && this.executeSQLStatement(con, sql);
					}
					if (done)
					{
						dataVersion = v;
						rst.updateInt("data_version", dataVersion);
						rst.updateRow();
						this.logger.info("Update to Version " + v + " successfully.");
					}
				}
				// Datenversion 30: Build 304: 10371
				
				// Datenversion 31: Build 307: 10375
				if (v == 31 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					
					if (!this.columnExists(con, "pos_payment", "is_input_or_withdraw"))
					{
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_payment ADD COLUMN is_input_or_withdraw tinyint(1) default '0';");
						
						PreparedStatement ps = con
										.prepareStatement(
														"SELECT p.receipt_id FROM pos_position p, pos_product_group g WHERE p.product_group_id = g.id AND (g.type = 3 OR g.type = 4);",
														ResultSet.TYPE_FORWARD_ONLY);
						ResultSet rs = ps.executeQuery();
						
						while (rs.next())
						{
							String receiptId = new Long(rs.getLong("receipt_id")).toString();
							String sql = "UPDATE pos_payment SET is_input_or_withdraw = 1 WHERE receipt_id = "
											+ receiptId + ";";
							done = done && this.executeSQLStatement(con, sql);
						}
						rs.close();
						ps.close();
						
						if (done)
						{
							dataVersion = v;
							rst.updateInt("data_version", dataVersion);
							rst.updateRow();
							this.logger.info("Update to Version " + v + " successfully.");
						}
					}
					
				}
				// Datenversion 31: Build 307: 10375
				// Datenversion 32: Build 308: 10376
				if (v == 32 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					
					String sql = "ALTER TABLE pos_receipt MODIFY COLUMN transaction_id BIGINT(20) DEFAULT 0, MODIFY COLUMN booking_id BIGINT(20) DEFAULT 0, MODIFY COLUMN settlement BIGINT(20) DEFAULT 0;";
					done = done && this.executeSQLStatement(con, sql);
					sql = "UPDATE pos_receipt SET transaction_id = NULL WHERE transaction_id = 0;";
					done = done && this.executeSQLStatement(con, sql);
					sql = "UPDATE pos_receipt SET settlement = NULL WHERE settlement = 0;";
					done = done && this.executeSQLStatement(con, sql);
					sql = "UPDATE pos_receipt SET booking_id = NULL WHERE booking_id = 0;";
					done = done && this.executeSQLStatement(con, sql);
					sql = "ALTER TABLE pos_block MODIFY COLUMN  font_size FLOAT DEFAULT 14;";
					done = done && this.executeSQLStatement(con, sql);
					sql = "ALTER TABLE pos_position ADD COLUMN amount DOUBLE DEFAULT 0;";
					done = done && this.executeSQLStatement(con, sql);
					sql = "UPDATE pos_position SET amount = FLOOR(quantity * price * (1 - discount) * ROUND(1 / 0.05, 0) + 0.51) / ROUND(1 / 0.05, 0);";
					done = done && this.executeSQLStatement(con, sql);
					
					PreparedStatement ps = con
									.prepareStatement(
													"SELECT * FROM pos_position p WHERE p.id = 424 AND p.receipt_id = 267 AND p.product_group_id = 62 AND p.price = 52.40;",
													ResultSet.TYPE_FORWARD_ONLY);
					ResultSet rs = ps.executeQuery();
					
					if (rs.next())
					{
						sql = "DELETE FROM pos_position WHERE id = 424;";
						done = done && this.executeSQLStatement(con, sql);
					}
					rs.close();
					ps.close();
					
					if (done)
					{
						dataVersion = v;
						rst.updateInt("data_version", dataVersion);
						rst.updateRow();
						this.logger.info("Update to Version " + v + " successfully.");
					}
					
				}
				// Datenversion 32: Build 308: 10376
				
				// Datenversion 33: Build 309: 10376
				if (v == 33 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					
					if (!this.tableExists(con, "pos_settlement"))
					{
						done = done
										&& this.executeSQLStatement(
														con,
														"CREATE TABLE pos_settlement (id bigint(20) unsigned not null default '0', "
																		+ "salespoint_id bigint(20) unsigned not null, settlement bigint(20) unsigned null, line_number integer default '0', "
																		+ "type integer default '0', subtype integer default '0', cashtype integer default '0', reference_class_name varchar(255), reference_object_id bigint(20), "
																		+ "short_text varchar(255), long_text varchar(255), value double default '0', quantity integer default '0', "
																		+ "amount1 double default '0', amount2 double default '0', code VARCHAR(20), receipts integer, PRIMARY KEY  (id), "
																		+ "KEY index_salespoint (salespoint_id), KEY index_settlement (settlement)) "
																		+ engine + " = InnoDB;");
					}
					
					if (done)
					{
						dataVersion = v;
						rst.updateInt("data_version", dataVersion);
						rst.updateRow();
						this.logger.info("Update to Version " + v + " successfully.");
					}
					
				}
				// Datenversion 33: Build 309: 10376
				
				// Datenversion 34: Build 309: 10376
				if (v == 34 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					
					if (!this.columnExists(con, "pos_payment_type", "sort"))
						done = done
										&& this.executeSQLStatement(con,
														"ALTER TABLE pos_payment_type ADD COLUMN sort integer default '0';");
					
					if (done)
					{
						dataVersion = v;
						rst.updateInt("data_version", dataVersion);
						rst.updateRow();
						this.logger.info("Update to Version " + v + " successfully.");
					}
					
				}
				// Datenversion 34: Build 309: 10376
				// Datenversion 35: Build 340: 10429
				if (v == 35 && dataVersion < v)
				{
					this.logger.info("Updating to Version " + v);
					
					done = done
									&& this.executeSQLStatement(
													con,
													"UPDATE pos_position SET tax = ROUND(-amount / 102.4 * 2.4, 2) WHERE discount <> 0 AND current_tax_id > 3 AND current_tax_id < 7;");
					done = done
									&& this.executeSQLStatement(
													con,
													"UPDATE pos_position SET tax = ROUND(-amount / 107.6 * 7.6, 2) WHERE discount <> 0 AND current_tax_id > 6 AND current_tax_id < 10;");
					
					if (done)
					{
						dataVersion = v;
						rst.updateInt("data_version", dataVersion);
						rst.updateRow();
						this.logger.info("Update to Version " + v + " successfully.");
					}
					
				}
				// Datenversion 35: Build 340: 10429
			}
			// if (v == 36 && dataVersion < v)
			// {
			// // Datenversion 36: Build 368: 10442
			// this.logger.info("Updating to Version " + v);
			//
			// if (!this.columnExists(con, "pos_position", "ebook"))
			// {
			// done = done
			// && this.executeSQLStatement(con,
			// "ALTER TABLE pos_position ADD COLUMN ebook SMALLINT DEFAULT 0");
			// // done = done
			// // && this.executeSQLStatement(
			// // con,
			// //
			// "UPDATE pos_position SET ebook = ROUND(-amount / 107.6 * 7.6, 2) WHERE discount <> 0 AND current_tax_id > 6 AND current_tax_id < 10;");
			// }
			// if (done)
			// {
			// dataVersion = v;
			// rst.updateInt("data_version", dataVersion);
			// rst.updateRow();
			// this.logger.info("Update to Version " + v + " successfully.");
			// }
			//
			// }
			// // Datenversion 36: Build 368: 10442
			
			/*
			 * Immer testen: Ist eine Bargruppe und eine BAR Zahlungsart
			 * vorhanden UND deleted = false!!!
			 */
			if (con.getCatalog().equals(
							Config.getInstance().getDatabaseStandardConnection().getAttributeValue("database")))
			{
				PreparedStatement ps = con.prepareStatement("SELECT * FROM pos_payment_type_group WHERE id = 1;",
								ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
				ResultSet rs = ps.executeQuery();
				if (rs.next())
				{
					byte on = 1;
					byte off = 0;
					if (rs.getByte("deleted") == on)
					{
						rs.updateByte("deleted", off);
						rs.updateRow();
					}
				}
				else
				{
					String sql = "INSERT INTO pos_payment_type_group (id,timestamp,deleted,visible,code,name,default_account) VALUES (1,NOW(),0,1,'Bar (Landeswährung)','Bar (Landeswährung)','');";
					this.executeSQLStatement(con, sql);
				}
			}
			
			if (con.getCatalog().equals(
							Config.getInstance().getDatabaseStandardConnection().getAttributeValue("database")))
			{
				PreparedStatement ps = con.prepareStatement("SELECT * FROM pos_payment_type WHERE id = 1;",
								ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
				ResultSet rs = ps.executeQuery();
				if (rs.next())
				{
					byte b = 0;
					byte c = 1;
					rs.updateByte("deleted", b);
					rs.updateLong("payment_type_group_id", 1);
					rs.updateLong("foreign_currency_id", 22);
					rs.updateByte("removeable", b);
					rs.updateByte("back", c);
					rs.updateByte("cash", c);
					rs.updateRow();
				}
				else
				{
					String sql = "INSERT INTO pos_payment_type (id,timestamp,deleted,payment_type_group_id, foreign_currency_id, removeable, name, code, account, is_voucher, export_id, open_cashdrawer, back, cash) VALUES (1,NOW(),0,1,22,0,'Bar (Landeswährung)','Bar','',0,'',1,1,1);";
					this.executeSQLStatement(con, sql);
				}
			}
			
			dataVersion = v;
			rst.updateInt("data_version", dataVersion);
			rst.updateRow();
			
			rst.close();
			con.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			this.logger.severe(e.getLocalizedMessage());
			// JOptionPane.showMessageDialog(null,
			// "Die Datenbankverbindung für den Datenbankupdate konnte nicht initialisiert werden."
			// );
		}
		finally
		{
			this.fh.close();
			this.logger = null;
			if (!done)
			{
				JOptionPane.showMessageDialog(null,
								"Der Datenbankupdate konnte nicht erfolgreich durchgeführt werden (Details finden Sie im Protokoll \"updateDB.log\".");
				System.exit(-10);
			}
		}
		
		return done;
	}
	
	private boolean executeSQLStatement(java.sql.Connection con, String sql)
	{
		return this.executeSQLStatement(con, sql, false);
	}
	
	private int getHeartbeat()
	{
		String value = this.getConnectionData().getAttributeValue("heartbeat");
		if (value == null)
		{
			return 0;
		}
		try
		{
			return Integer.valueOf(value).intValue();
		}
		catch (NumberFormatException e)
		{
			return 0;
		}
	}
	
	public void heartbeat()
	{
		int heartbeat = this.getHeartbeat();
		if (heartbeat > 0)
		{
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask()
			{
				public void run()
				{
					Version.select(Connection.this);
				}
			}, GregorianCalendar.getInstance().getTime(), 1000 * heartbeat);
		}
	}
	
	private boolean executeSQLStatement(java.sql.Connection con, String sql, boolean ignoreError)
	{
		try
		{
			this.logger.info("Executing: " + sql);
			PreparedStatement stm = con.prepareStatement(sql);
			stm.execute();
			this.logger.info("Executing successfull.");
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			if (e.getSQLState().equals("S1009"))
			{
				return true;
			}
			if (ignoreError)
			{
				return true;
			}
			else
			{
				this.logger.severe("Executing failed.");
				return false;
			}
		}
	}
	
	private boolean tableExists(java.sql.Connection con, String tableName)
	{
		try
		{
			DatabaseMetaData dbmd = con.getMetaData();
			ResultSet rs = dbmd.getTables(null, null, tableName, new String[]
			{ "TABLE" });
			
			if (!rs.first())
			{
				return false;
			}
			while (!rs.isAfterLast())
			{
				if (rs.getString(rs.findColumn("TABLE_NAME")).equals(tableName))
				{
					return true;
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		return false;
	}
	
	private boolean columnExists(java.sql.Connection con, String tableName, String columnName)
	{
		try
		{
			DatabaseMetaData dbmd = con.getMetaData();
			ResultSet rs = dbmd.getColumns(null, null, tableName, columnName);
			
			if (!rs.first())
			{
				return false;
			}
			while (!rs.isAfterLast())
			{
				if (rs.getString(rs.findColumn("COLUMN_NAME")).equals(columnName))
				{
					return true;
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		return false;
	}
	
	private boolean rowExists(java.sql.Connection con, String table, String primaryKeyColumn, Long primaryKeyValue)
	{
		try
		{
			PreparedStatement stm = con.prepareStatement("SELECT * FROM " + table + " WHERE " + primaryKeyColumn
							+ " = " + primaryKeyValue);
			ResultSet rs = stm.executeQuery();
			if (rs.first())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		return false;
	}
	
	private void updateEventColumns(java.sql.Connection con, String tableName, String columnName) throws SQLException
	{
		if (con.getCatalog().equals(Config.getInstance().getDatabaseStandardConnection().getAttributeValue("database")))
		{
			PreparedStatement ps = con.prepareStatement("SELECT * FROM " + tableName + ";",
							ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = ps.executeQuery();
			while (rs.next())
			{
				StringBuffer newClassName = new StringBuffer();
				String oldClassName = rs.getString(columnName);
				String[] parts = oldClassName.split("[.]");
				for (int i = 0; i < parts.length; i++)
				{
					if (parts[i].equals("client"))
					{
					}
					else if (parts[i].equals("event"))
					{
						if (newClassName.toString().equals(""))
						{
							newClassName.append("events");
						}
						else
						{
							newClassName.append("." + "events");
						}
					}
					else
					{
						if (newClassName.toString().equals(""))
						{
							newClassName.append(parts[i]);
						}
						else
						{
							newClassName.append("." + parts[i]);
						}
					}
				}
				rs.updateString(columnName, newClassName.toString());
				rs.updateRow();
			}
		}
	}
	
	public abstract String getCode();
	
	public void addInitializationListener(InitializationListener l)
	{
		if (!Connection.initializationListeners.contains(l))
		{
			Connection.initializationListeners.add(l);
		}
	}
	
	public void removeInitializationListener(InitializationListener l)
	{
		Connection.initializationListeners.remove(l);
	}
	
	protected void fireInitialized(int value, String text)
	{
		InitializationListener[] l = (InitializationListener[]) Connection.initializationListeners
						.toArray(new InitializationListener[0]);
		for (int i = 0; i < l.length; i++)
		{
			l[i].initialized(value, text);
		}
	}
	
	private void fireInitialized(String text)
	{
		InitializationListener[] l = (InitializationListener[]) Connection.initializationListeners
						.toArray(new InitializationListener[0]);
		for (int i = 0; i < l.length; i++)
		{
			l[i].initialized(text);
		}
	}
	
	private String id;
	private String name;
	private boolean active;
	private boolean connected;
	private PersistenceBroker broker;
	private Version version;
	
	private Logger logger;
	private FileHandler fh;
	private Level level = Level.SEVERE; // 10079
	private String connectionString;
	
	private static final ArrayList initializationListeners = new ArrayList();
}
