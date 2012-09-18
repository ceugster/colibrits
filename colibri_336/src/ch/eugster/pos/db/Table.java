/*
 * Created on 07.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.db;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.apache.ojb.broker.PersistenceBrokerSQLException;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.ClassNotPersistenceCapableException;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.jdom.Element;

import ch.eugster.pos.Messages;
import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.events.DatabaseEvent;
import ch.eugster.pos.events.DatabaseListener;
import ch.eugster.pos.events.ModeChangeEvent;
import ch.eugster.pos.events.ModeChangeListener;
import ch.eugster.pos.util.XMLLoader;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class Table
{
	
	private Long id;
	
	public Timestamp timestamp = new Timestamp(new Date().getTime());
	
	public boolean deleted = false;
	
	public Table()
	{
		this.id = null;
		this.timestamp = new Timestamp(new Date(0l).getTime());
	}
	
	public void setId(Long id)
	{
		this.id = id;
	}
	
	public Long getId()
	{
		return this.id;
	}
	
	public abstract boolean isRemovable();
	
	protected DBResult describeError(Exception e)
	{
		//		LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
		DBResult result = new DBResult();
		if (e.getCause() instanceof SQLException)
		{
			result.setErrorCode(DBResult.SQL_ERROR);
			result.setExternalErrorCode(((SQLException) e.getCause()).getSQLState());
			result.setExternalErrorText(((SQLException) e.getCause()).getLocalizedMessage());
			// int vendorCode = ((SQLException) e.getCause()).getErrorCode();
			result.log();
		}
		else
		{
			result.setErrorCode(-1);
			e.printStackTrace();
			result.setErrorText(Messages.getString("Table.Fehler_2")); //$NON-NLS-1$
		}
		return result;
	}
	
	public static boolean switchDatabase()
	{
		DatabaseEvent e = null;
		// if (Database.isSwitchable()) {
		if (Database.getCurrent().equals(Database.getTemporary()))
		{
			if (Database.getTemporary().isConnected())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		if (Database.getCurrent().equals(Database.getStandard()))
		{
			if (Database.getTemporary().isConnected())
			{
				
				Database.setCurrent(Database.getTemporary());
				Database.getStandard().setConnected(false);
				e = new DatabaseEvent(Database.getCurrent(), 1,
								Messages.getString("Table.tit_3"), Messages.getString("Table.msg_4")); //$NON-NLS-1$ 
				e.newConnection = Database.getCurrent();
				e.oldConnection = Database.getStandard();
				Table.fireDatabaseEvent(e);
				
				if (UserPanel.getCurrent() != null)
				{
					Table.fireModeChangeEvent(new ModeChangeEvent(UserPanel.getCurrent().getMode()));
				}
				return Database.getCurrent().equals(Database.getTemporary());
			}
			else
			{
				return false;
			}
		}
		
		e = new DatabaseEvent(Database.getStandard(), 2,
						Messages.getString("Table.tit_5"), Messages.getString("Table.msg_6")); //$NON-NLS-1$ //$NON-NLS-2$
		e.newConnection = Database.getCurrent();
		e.oldConnection = Database.getStandard();
		Table.fireDatabaseEvent(e);
		
		return false;
		// }
		// else {
		// return false;
		// }
	}
	
	public DBResult store(Connection connection)
	{
		return this.store(connection, true);
	}
	
	public DBResult store(Connection connection, boolean updateTimestamp)
	{
		PersistenceBroker broker = connection.getBroker();
		DBResult result = new DBResult();
		boolean isMyTransaction = !broker.isInTransaction();
		try
		{
			if (isMyTransaction)
			{
				broker.beginTransaction();
			}
			if (updateTimestamp) this.timestamp = new Timestamp(GregorianCalendar.getInstance().getTimeInMillis());
			broker.store(this);
			
			if (isMyTransaction)
			{
				broker.commitTransaction();
			}
		}
		catch (PersistenceBrokerException e)
		{
			if (isMyTransaction)
			{
				broker.abortTransaction();
			}
			
			if (e instanceof ClassNotPersistenceCapableException)
			{
				this.testSwitchDatabase();
			}
			else if (isMyTransaction)
			{
				result = this.describeError(e);
				if (e instanceof PersistenceBrokerSQLException)
				{
					PersistenceBrokerSQLException psqle = (PersistenceBrokerSQLException) e;
					if (psqle.getCause() instanceof SQLException)
					{
						// SQLException sqle = (SQLException) psqle.getCause();
						// result = describeError(sqle);
						//							if (sqle.getSQLState().equals("08S01")) { //$NON-NLS-1$
						this.testSwitchDatabase();
						// }
					}
				}
			}
		}
		return result;
	}
	
	protected void testSwitchDatabase()
	{
		String title = null;
		String text = null;
		
		if (Database.isSwitchable() && Database.getCurrent().equals(Database.getStandard()))
		{
			if (Database.getTemporary().isActive() && Database.getTemporary().isConnected())
			{
				
				Connection oldConnection = Database.getStandard();
				Connection newConnection = Database.getTemporary();
				
				title = Messages.getString("Table.Verbindung_unterbrochen_10"); //$NON-NLS-1$
				text = Messages.getString("Table.Die_Verbindung_zur_standardm_u00E4ssig_eingerichteten_Datenbank_11") + " " + //$NON-NLS-1$ //$NON-NLS-2$
								Database.getStandard().getName()
								+ "" + //$NON-NLS-1$
								Messages
												.getString("Table.ist_nicht_verf_u00FCgbar._Ich_versuche,_die_Verbindung_auf_die_lokale_Ersatzdatenbank_umzuleiten._14"); //$NON-NLS-1$
				
				Table.fireDatabaseEvent(new DatabaseEvent(Database.getStandard(), 0, oldConnection, newConnection,
								title, text));
				if (Table.switchDatabase())
				{
					this.store();
				}
			}
			else
			{
				title = Messages.getString("Table.Verbindung_unterbrochen_22"); //$NON-NLS-1$
				text = Messages.getString("Table.Die_Verbindung_zur_Datenbank_23") + " " + //$NON-NLS-1$ //$NON-NLS-2$
								Database.getStandard().getName()
								+ " " + //$NON-NLS-1$
								Messages
												.getString("Table.ist_nicht_mehr_verf_u00FCgbar._Das_Programm_wird_verlassen._26"); //$NON-NLS-1$
				
				Table.fireDatabaseEvent(new DatabaseEvent(Database.getStandard(), 2, title, text));
			}
		}
		else
		{
			title = Messages.getString("Table.Verbindung_unterbrochen_29"); //$NON-NLS-1$
			text = Messages.getString("Table.Die_Verbindung_zur_Datenbank_30") + " " + //$NON-NLS-1$ //$NON-NLS-2$
							Database.getStandard().getName() + " " + //$NON-NLS-1$
							Messages.getString("Table.ist_nicht_mehr_verf_u00FCgbar._Das_Programm_wird_verlassen._33"); //$NON-NLS-1$
			
			Table.fireDatabaseEvent(new DatabaseEvent(Database.getStandard(), 2, title, text));
		}
	}
	
	public DBResult store()
	{
		return this.store(Database.getCurrent());
	}
	
	public DBResult store(boolean updateTimestamp)
	{
		/**
		 * 'update' wurde nur verwendet, um die Hierarchie mit receipt
		 * sicherzustellen (siehe testSwitchDatabase())
		 */
		return this.store(Database.getCurrent(), updateTimestamp);
	}
	
	/**
	 * Remove record
	 * 
	 * @return
	 */
	public DBResult delete()
	{
		PersistenceBroker broker = Database.getCurrent().getBroker();
		DBResult result = new DBResult();
		boolean isMyTransaction = !broker.isInTransaction();
		try
		{
			if (isMyTransaction)
			{
				broker.beginTransaction();
			}
			if (this.isRemovable())
			{
				broker.delete(this);
			}
			else
			{
				this.deleted = true;
				broker.store(this);
			}
			if (isMyTransaction)
			{
				broker.commitTransaction();
			}
		}
		catch (Exception e)
		{
			if (isMyTransaction)
			{
				broker.abortTransaction();
			}
			result = this.describeError(e);
			
		}
		return result;
	}
	
	public DBResult remove()
	{
		PersistenceBroker broker = Database.getCurrent().getBroker();
		DBResult result = new DBResult();
		boolean isMyTransaction = !broker.isInTransaction();
		try
		{
			if (isMyTransaction)
			{
				broker.beginTransaction();
			}
			broker.delete(this);
			
			if (isMyTransaction)
			{
				broker.commitTransaction();
			}
		}
		catch (Exception e)
		{
			if (isMyTransaction)
			{
				broker.abortTransaction();
			}
			result = this.describeError(e);
		}
		return result;
	}
	
	public FieldDescriptor[] getAttributeNames()
	{
		return Table.fieldDescriptors;
	}
	
	protected static Collection select(Query query)
	{
		Collection result = new ArrayList();
		try
		{
			result = Database.getCurrent().getBroker().getCollectionByQuery(query);
		}
		catch (Exception e)
		{
			if (e instanceof PersistenceBrokerSQLException)
			{
				if (e.getCause() instanceof SQLException)
				{
					SQLException sqle = (SQLException) e.getCause();
					if (sqle.getSQLState().equals("08S01") || sqle.getSQLState().equals("08003")) { //$NON-NLS-1$
						result = Table.tryToSwitch(query);
					}
				}
			}
			else if (e instanceof PersistenceBrokerException)
			{
				if (e.getCause() instanceof PersistenceBrokerSQLException)
				{
					if (e.getCause().getCause() instanceof SQLException)
					{
						SQLException sqle = (SQLException) e.getCause().getCause();
						if (sqle.getSQLState().equals("08S01") || sqle.getSQLState().equals("08003")) { //$NON-NLS-1$
							result = Table.tryToSwitch(query);
						}
					}
				}
				else if (e.getCause() instanceof LookupException)
				{
					result = Table.tryToSwitch(query);
				}
			}
		}
		return result;
	}
	
	private static Collection tryToSwitch(Query query)
	{
		Collection result = null;
		String title = "";
		String text = "";
		
		Database.getCurrent().setConnected(false);
		if (Database.getCurrent().equals(Database.getStandard()))
		{
			if (Database.isSwitchable() && Database.getTemporary().isConnected())
			{
				
				title = Messages.getString("Table.Verbindung_unterbrochen_42"); //$NON-NLS-1$
				text = Messages.getString("Table.Die_Verbindung_zur_standardm_u00E4ssig_eingerichteten_Datenbank_43") + " " + //$NON-NLS-1$ //$NON-NLS-2$
								Database.getStandard().getName()
								+ " " + //$NON-NLS-1$
								Messages
												.getString("Table.ist_nicht_verf_u00FCgbar._Ich_versuche,_mit_der_lokalen_Ersatzdatenbank_eine_Verbindung_aufzubauen._46"); //$NON-NLS-1$
				
				Table.fireDatabaseEvent(new DatabaseEvent(Database.getStandard(), 0, Database.getStandard(), Database
								.getTemporary(), title, text));
				if (Table.switchDatabase())
				{
					//											LogManager.getLogManager().getLogger("colibri").severe(Messages.getString("Table.Datenbank_wurde_erfolgreich_gewechselt._48")); //$NON-NLS-1$ //$NON-NLS-2$
					result = Table.select(query);
					
					title = Messages.getString("Table.Datenbank_gewechselt_49"); //$NON-NLS-1$
					text = Messages.getString("Table.Die_Verbindung_zur_tempor_u00E4ren_Datenbank_50") + " " + //$NON-NLS-1$ //$NON-NLS-2$
									Database.getStandard().getName()
									+ " " + //$NON-NLS-1$
									Messages
													.getString("Table.konnte_hergestellt_werden._Sie_k_u00F6nnen_jetzt_weiterarbeiten._53"); //$NON-NLS-1$
					
					Table.fireDatabaseEvent(new DatabaseEvent(Database.getStandard(), 1, Database.getStandard(),
									Database.getTemporary(), title, text));
				}
			}
			else
			{
				Table
								.fireDatabaseEvent(new DatabaseEvent(
												Database.getStandard(),
												2,
												Messages.getString("Table.Verbindung_unterbrochen_54"), //$NON-NLS-1$
												Messages
																.getString("Table.Die_Verbindung_zur_Serverdatenbank_ist_unterbrochen._Das_Programm_wird_beendet._55") //$NON-NLS-1$
								));
			}
		}
		else
		{
			title = Messages.getString("Table.Verbindung_unterbrochen_58"); //$NON-NLS-1$
			text = Messages.getString("Table.Die_Verbindung_zur_Datenbank_59") + " " + //$NON-NLS-1$ //$NON-NLS-2$
							Database.getStandard().getName() + " " + //$NON-NLS-1$
							Messages.getString("Table.ist_nicht_mehr_verf_u00FCgbar._Das_Programm_wird_verlassen._62"); //$NON-NLS-1$
			
			Table.fireDatabaseEvent(new DatabaseEvent(Database.getStandard(), 2, title, text));
		}
		return result;
	}
	
	protected static Iterator selectToIterator(Query query)
	{
		try
		{
			return Database.getCurrent().getBroker().getIteratorByQuery(query);
		}
		catch (PersistenceBrokerException e)
		{
			//			LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
			Iterator iterator = new ArrayList().iterator();
			String title = null;
			String text = null;
			
			if (e.getCause() instanceof PersistenceBrokerSQLException)
			{
				PersistenceBrokerSQLException psqle = (PersistenceBrokerSQLException) e.getCause();
				if (psqle.getCause() instanceof SQLException)
				{
					SQLException sqle = (SQLException) psqle.getCause();
					
					if (sqle.getSQLState().equals("08S01") || sqle.getSQLState().equals("08003")) { //$NON-NLS-1$
						Database.getCurrent().setConnected(false);
						if (Database.getCurrent().equals(Database.getStandard()))
						{
							if (Database.isSwitchable() && Database.getTemporary().isConnected())
							{
								
								title = Messages.getString("Table.Verbindung_unterbrochen_42"); //$NON-NLS-1$
								text = Messages
												.getString("Table.Die_Verbindung_zur_standardm_u00E4ssig_eingerichteten_Datenbank_43") + " " + //$NON-NLS-1$ //$NON-NLS-2$
												Database.getStandard().getName()
												+ " " + //$NON-NLS-1$
												Messages
																.getString("Table.ist_nicht_verf_u00FCgbar._Ich_versuche,_mit_der_lokalen_Ersatzdatenbank_eine_Verbindung_aufzubauen._46"); //$NON-NLS-1$
								
								Table.fireDatabaseEvent(new DatabaseEvent(Database.getStandard(), 0, Database
												.getStandard(), Database.getTemporary(), title, text));
								if (Table.switchDatabase())
								{
									//									LogManager.getLogManager().getLogger("colibri").severe(Messages.getString("Table.Datenbank_wurde_erfolgreich_gewechselt._48")); //$NON-NLS-1$ //$NON-NLS-2$
									iterator = Table.selectToIterator(query);
									
									title = Messages.getString("Table.Datenbank_gewechselt_49"); //$NON-NLS-1$
									text = Messages.getString("Table.Die_Verbindung_zur_tempor_u00E4ren_Datenbank_50") + " " + //$NON-NLS-1$ //$NON-NLS-2$
													Database.getStandard().getName()
													+ " " + //$NON-NLS-1$
													Messages
																	.getString("Table.konnte_hergestellt_werden._Sie_k_u00F6nnen_jetzt_weiterarbeiten._53"); //$NON-NLS-1$
									
									Table.fireDatabaseEvent(new DatabaseEvent(Database.getStandard(), 1, Database
													.getStandard(), Database.getTemporary(), title, text));
								}
							}
							else
							{
								Table
												.fireDatabaseEvent(new DatabaseEvent(Database.getStandard(),
																0,
																Messages.getString("Table.Verbindung_unterbrochen_54"), //$NON-NLS-1$
																Messages
																				.getString("Table.Die_Verbindung_zur_Serverdatenbank_ist_unterbrochen._Das_Programm_wird_beendet._55") //$NON-NLS-1$
												));
								Table.fireDatabaseEvent(new DatabaseEvent(Database.getStandard(), 2, "", "")); //$NON-NLS-1$ //$NON-NLS-2$
							}
						}
						else
						{
							title = Messages.getString("Table.Verbindung_unterbrochen_58"); //$NON-NLS-1$
							text = Messages.getString("Table.Die_Verbindung_zur_Datenbank_59") + " " + //$NON-NLS-1$ //$NON-NLS-2$
											Database.getStandard().getName()
											+ " " + //$NON-NLS-1$
											Messages
															.getString("Table.ist_nicht_mehr_verf_u00FCgbar._Das_Programm_wird_verlassen._62"); //$NON-NLS-1$
							
							Table.fireDatabaseEvent(new DatabaseEvent(Database.getStandard(), 0, title, text));
							Table.fireDatabaseEvent(new DatabaseEvent(Database.getStandard(), 2, "", "")); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
				}
			}
			return iterator;
		}
	}
	
	public static Table selectById(Class cls, Long pk)
	{
		return Table.selectById(Database.getCurrent(), cls, pk);
	}
	
	protected static Table selectById(Connection connection, Class cls, Long pk)
	{
		Class[] params = new Class[0];
		Constructor c = null;
		Object[] p = null;
		Table table = null;
		try
		{
			c = cls.getConstructor(params);
			p = new Object[0];
			table = (Table) c.newInstance(p);
		}
		catch (NoSuchMethodException e)
		{
			//			LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
			System.exit(-1);
		}
		catch (InstantiationException e)
		{
			//			LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
			System.exit(-1);
		}
		catch (IllegalAccessException e)
		{
			//			LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
			System.exit(-1);
		}
		catch (InvocationTargetException e)
		{
			//			LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
			System.exit(-1);
		}
		
		if (Database.getCurrent().equals(Database.getTemporary()))
		{
			table = Database.getTemporary().getRecord(table.getClass().getName(), pk);
		}
		else
		{
			Criteria criteria = new Criteria();
			criteria.addEqualTo("id", pk); //$NON-NLS-1$
			Query query = QueryFactory.newQuery(cls, criteria);
			Collection tables = connection.getBroker().getCollectionByQuery(query);
			Iterator i = tables.iterator();
			if (i.hasNext())
			{
				table = (Table) i.next();
			}
		}
		return table;
	}
	
	protected static Table getById(Connection connection, Class cls, Long pk)
	{
		Class[] params = new Class[0];
		Constructor c = null;
		Object[] p = null;
		Table table = null;
		try
		{
			c = cls.getConstructor(params);
			p = new Object[0];
			table = (Table) c.newInstance(p);
			System.out.println();
		}
		catch (NoSuchMethodException e)
		{
			//			LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
			System.exit(-1);
		}
		catch (InstantiationException e)
		{
			//			LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
			System.exit(-1);
		}
		catch (IllegalAccessException e)
		{
			//			LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
			System.exit(-1);
		}
		catch (InvocationTargetException e)
		{
			//			LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
			System.exit(-1);
		}
		
		return Database.getTemporary().getRecord(table.getClass().getName(), pk);
	}
	
	public static void equalize(Class clazz, Connection source, Connection target)
	{
		boolean isSwitchable = Database.isSwitchable();
		Database.setSwitchable(false);
		Table[] records = Table.selectAll(source, clazz);
		if (records.length > 0)
		{
			List autoincrementFields = new ArrayList();
			FieldDescriptor[] fields = Table.classDescriptor.getPkFields();
			for (int i = 0; i < fields.length; i++)
			{
				if (fields[i].isAutoIncrement())
				{
					autoincrementFields.add(fields[i]);
					fields[i].setAutoIncrement(false);
				}
			}
			// if (classDescriptor.getPrimaryKey().isAutoIncrement()) {
			// classDescriptor.getPrimaryKey().setAutoIncrement(false);
			// }
			for (int i = 0; i < records.length; i++)
			{
				if (records[i].timestamp == null)
				{
					records[i].timestamp = new Timestamp(new Date().getTime());
					records[i].store(source);
				}
				Table record = Table.selectById(target, clazz, records[i].getId());
				if (record == null || record.timestamp == null || record.timestamp.before(records[i].timestamp))
				{
					// records[i].setId(ZERO_ID);
					DBResult result = records[i].store(target);
					if (result.getErrorCode() != 0)
					{
						Table.log(result, clazz.getName());
					}
				}
			}
			Iterator itr = autoincrementFields.iterator();
			while (itr.hasNext())
			{
				FieldDescriptor field = (FieldDescriptor) itr.next();
				field.setAutoIncrement(true);
			}
			// if (classDescriptor.getPrimaryKey().isAutoIncrement()) {
			// classDescriptor.getPrimaryKey().setAutoIncrement(true);
			// }
		}
		Database.setSwitchable(isSwitchable);
	}
	
	private static Table[] selectAll(Connection connection, Class clazz)
	{
		Query query = QueryFactory.newQuery(clazz, new Criteria());
		Collection tables = connection.getBroker().getCollectionByQuery(query);
		return tables == null ? new Table[0] : (Table[]) tables.toArray(new Table[0]);
	}
	
	private static void log(DBResult result, String table)
	{
		if (result.getErrorCode() != 0)
		{
			//			LogManager.getLogManager().getLogger("colibri").info("Problem occured while updating table '" + table + "' in temporary database."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			//			LogManager.getLogManager().getLogger("colibri").info(result.getErrorText()); //$NON-NLS-1$
			if (result.getExternalErrorCode() != null && result.getExternalErrorCode().length() > 0)
			{
				//				LogManager.getLogManager().getLogger("colibri").info(result.getExternalErrorCode()); //$NON-NLS-1$
				//				LogManager.getLogManager().getLogger("colibri").info(result.getExternalErrorText()); //$NON-NLS-1$
			}
		}
	}
	
	public static boolean addDatabaseErrorListener(DatabaseListener l)
	{
		return Table.databaseErrorListeners.add(l);
	}
	
	public static boolean removeDatabaseErrorListener(DatabaseListener l)
	{
		return Table.databaseErrorListeners.remove(l);
	}
	
	private static void fireDatabaseEvent(DatabaseEvent e)
	{
		DatabaseListener[] l = (DatabaseListener[]) Table.databaseErrorListeners.toArray(new DatabaseListener[0]);
		for (int i = 0; i < l.length; i++)
		{
			l[i].databaseErrorOccured(e);
		}
	}
	
	public static boolean addModeChangeListener(ModeChangeListener l)
	{
		return Table.modeChangeListeners.add(l);
	}
	
	public static boolean removeModeChangeListener(ModeChangeListener l)
	{
		return Table.modeChangeListeners.remove(l);
	}
	
	private static void fireModeChangeEvent(ModeChangeEvent e)
	{
		ModeChangeListener[] l = (ModeChangeListener[]) Table.modeChangeListeners.toArray(new ModeChangeListener[0]);
		for (int i = 0; i < l.length; i++)
		{
			l[i].modeChangePerformed(e);
		}
	}
	
	protected Element getJDOMRecordAttributes()
	{
		Element record = new Element("record"); //$NON-NLS-1$
		record.setAttribute("id", this.getId().toString()); //$NON-NLS-1$
		// 10202
		//		record.setAttribute("timestamp", timestamp.toString()); //$NON-NLS-1$
		record.setAttribute("timestamp", new Long(this.timestamp.getTime()).toString()); //$NON-NLS-1$
		// 10202
		record.setAttribute("deleted", new Boolean(this.deleted).toString()); //$NON-NLS-1$
		return record;
	}
	
	protected void getData(boolean setId, Element record)
	{
		this.setId(setId ? Long.valueOf(record.getAttributeValue("id")) : null);
		this.timestamp = XMLLoader.getTimestampFromLong(record.getAttributeValue("timestamp")); //$NON-NLS-1$
		this.deleted = new Boolean(record.getAttributeValue("deleted")).booleanValue(); //$NON-NLS-1$
	}
	
	protected void getData(Element record)
	{
		this.getData(true, record);
	}
	
	public static boolean isIdFieldAutoincrement(Class clazz)
	{
		FieldDescriptor id = Table.getClassDescriptor(clazz).getFieldDescriptorByName("id"); //$NON-NLS-1$
		return id.isAutoIncrement();
	}
	
	public static void setPrimaryKey(Class clazz, String field, boolean isPrimaryKey)
	{
		FieldDescriptor id = Table.getClassDescriptor(clazz).getFieldDescriptorByName("id"); //$NON-NLS-1$
		id.setPrimaryKey(isPrimaryKey);
	}
	
	protected static ClassDescriptor getClassDescriptor(Class clazz)
	{
		if (Table.classDescriptor == null)
		{
			Table.setDescriptors(clazz);
		}
		return Table.classDescriptor;
	}
	
	protected static FieldDescriptor getFieldDescriptor(Class clazz, String name)
	{
		if (Table.classDescriptor == null)
		{
			Table.setDescriptors(clazz);
		}
		return Table.classDescriptor.getFieldDescriptorByName(name);
	}
	
	private static void setDescriptors(Class clazz)
	{
		Table.classDescriptor = Database.getCurrent().getBroker().getClassDescriptor(clazz);
		Table.fieldDescriptors = Table.classDescriptor.getFieldDescriptions();
	}
	
	public boolean equals(Object object)
	{
		if (object == null)
		{
			return false;
		}
		
		if (object.getClass().equals(this.getClass()))
		{
			Table table = (Table) object;
			if (this.getId() == null)
			{
				return table.getId() == null;
			}
			else
			{
				return this.getId().equals(table.getId());
			}
		}
		else
		{
			return false;
		}
	}
	
	private static ClassDescriptor classDescriptor;
	private static FieldDescriptor[] fieldDescriptors;
	
	public static final int OK = 0;
	public static final int IS_REFERENCED = -1;
	public static final int SERVER_ERROR = -2;
	public static final String IS_REFERENCED_TEXT = Messages
					.getString("Table.Der_Datensatz_wird_referenziert_und_darf_nicht_gel_u00F6scht_werden._87"); //$NON-NLS-1$
	
	public static final Long ZERO_VALUE = new Long(0L);
	// public static final Long NULL_ID = null;
	public static final int INTEGER_DEFAULT_ZERO = 0;
	public static final int INTEGER_DEFAULT_255 = 255;
	public static final double DOUBLE_DEFAULT_ZERO = 0d;
	public static final double DOUBLE_DEFAULT_ONE = 1d;
	public static final boolean BOOLEAN_DEFAULT_TRUE = true;
	public static final boolean BOOLEAN_DEFAULT_FALSE = false;
	
	private static ArrayList databaseErrorListeners = new ArrayList();
	private static ArrayList modeChangeListeners = new ArrayList();
	
}
