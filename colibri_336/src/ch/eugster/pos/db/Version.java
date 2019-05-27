/*
 * Created on 08.01.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.db;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.apache.ojb.broker.PersistenceBrokerSQLException;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.jdom.Element;

import ch.eugster.pos.Messages;
import ch.eugster.pos.util.XMLLoader;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class Version
{
	
	/*
	 * Die folgenden Variablen sind nicht in der Datenbank!
	 */
	private static int major = 1;
	private static int minor = 6;
	private static int service = 0;
	private static int build = 387;
	private static String date = "27.05.2019";
	
	private String connectionId = ""; //$NON-NLS-1$
	private static int runningProgram = -1;
	public static boolean isFrameVisible = false;
	
	// Version 16 ab Build 177
	private static int dataVersion = 36; // Datenversion Programm
	
	/*
	 * Die folgenden Variablen sind in der Datenbank
	 */
	private int data = 0; // Datenversion in Datenbank
	
	private Long transactionId = Table.ZERO_VALUE;
	
	public Version()
	{
		super();
	}
	
	public void setDataVersion(int newVersion)
	{
		this.data = newVersion;
		this.connection.getBroker().store(this);
	}
	
	public int getDataVersion()
	{
		return Version.select(this.connection).data;
	}
	
	/**
	 * 
	 * @return version difference (databBaseVersionEqualsMyVersion == 0: equal;
	 *         databBaseVersionEqualsMyVersion < 0: databaseVersion newer;
	 *         databBaseVersionEqualsMyVersion > 0: program newer)
	 */
	public int dataBaseVersionEqualsMyVersion()
	{
		return Version.dataVersion - this.getDataVersion();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.db.Table#isRemovable()
	 */
	public boolean isRemovable()
	{
		return false;
	}
	
	public static String version()
	{
		return String.valueOf(Version.major) + "." //$NON-NLS-1$
						+ String.valueOf(Version.minor) + "." //$NON-NLS-1$
						+ String.valueOf(Version.service) + "-" //$NON-NLS-1$
						+ String.valueOf(Version.build);
	}
	
	public static int getBuild()
	{
		return Version.build;
	}
	
	public static int getMyDataVersion()
	{
		return Version.dataVersion;
	}
	
	public static String getVersionDate()
	{
		return Version.date;
	}
	
	public static void setRunningProgram(int program)
	{
		Version.runningProgram = program;
	}
	
	public static int getRunningProgram()
	{
		return Version.runningProgram;
	}
	
	public static Version select(Connection con) throws PersistenceBrokerException
	{
		Version v = null;
		try
		{
			v = new Version();
		}
		catch (Error e)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage());
		}
		catch (Exception e)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage());
		}
		// v = new Version();
		Query query = QueryFactory.newQuery(Version.class, new Criteria());
		Collection versions = con.getBroker().getCollectionByQuery(query);
		Iterator i = versions.iterator();
		if (i.hasNext())
		{
			v = (Version) i.next();
			v.connection = con;
		}
		return v;
	}
	
	// Start 10441
	public DBResult store()
	{
		PersistenceBroker broker = this.connection.getBroker();
		DBResult result = new DBResult();
		boolean isMyTransaction = !broker.isInTransaction();
		try
		{
			if (isMyTransaction)
			{
				broker.beginTransaction();
			}
			
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
			
			if (isMyTransaction)
			{
				result = this.describeError(e);
				if (e instanceof PersistenceBrokerSQLException)
				{
					PersistenceBrokerSQLException psqle = (PersistenceBrokerSQLException) e;
				}
			}
		}
		return result;
	}
	
	protected DBResult describeError(Exception e)
	{
		//		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
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
	
	// End 10441
	
	public Long getTransactionId()
	{
		return this.transactionId;
	}
	
	public void setTransactionId(Long id)
	{
		this.transactionId = id;
	}
	
	public static void readDBRecords()
	{
		Version version = Version.select(Database.getCurrent());
		Version.put(version);
	}
	
	public static Version get(Connection con)
	{
		return (Version) Version.records.get(con.getId());
	}
	
	private static void clearData()
	{
		Version.records.clear();
	}
	
	private static void put(Version version)
	{
		Version.records.put(Database.getCurrent().getId(), version);
	}
	
	public static Element writeXMLRecords(Element root)
	{
		Element table = Database.getTemporary().getTable("version"); //$NON-NLS-1$
		if (table == null)
		{
			table = new Element("table"); //$NON-NLS-1$
			table.setAttribute("name", "version"); //$NON-NLS-1$ //$NON-NLS-2$
			root.addContent(table);
		}
		
		Version rec = (Version) Version.records.get(Database.getCurrent().getId());
		
		Element record = new Element("record"); //$NON-NLS-1$
		record.setAttribute("id", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		record.setAttribute("timestamp", ""); //$NON-NLS-1$ //$NON-NLS-2$
		record.setAttribute("deleted", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		
		Element dt = new Element("field"); //$NON-NLS-1$
		dt.setAttribute("name", "data"); //$NON-NLS-1$ //$NON-NLS-2$
		dt.setAttribute("value", new Integer(rec.data).toString()); //$NON-NLS-1$
		record.addContent(dt);
		
		Element ci = new Element("field"); //$NON-NLS-1$
		ci.setAttribute("name", "connection-id"); //$NON-NLS-1$ //$NON-NLS-2$
		ci.setAttribute("value", rec.connectionId); //$NON-NLS-1$
		record.addContent(ci);
		
		Element ti = new Element("field"); //$NON-NLS-1$
		ti.setAttribute("name", "transaction-id"); //$NON-NLS-1$ //$NON-NLS-2$
		ti.setAttribute("value", rec.transactionId.toString()); //$NON-NLS-1$
		record.addContent(ti);
		
		table.addContent(record);
		
		return root;
	}
	
	public static void readXML()
	{
		Version.clearData();
		Element[] elements = Database.getTemporary().getRecords("version"); //$NON-NLS-1$
		for (Element element : elements)
		{
			Version record = new Version();
			List fields = element.getChildren("field"); //$NON-NLS-1$
			Iterator iter = fields.iterator();
			while (iter.hasNext())
			{
				Element field = (Element) iter.next();
				if (field.getAttributeValue("name").equals("data")) { //$NON-NLS-1$ //$NON-NLS-2$
					record.data = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
				}
				else if (field.getAttributeValue("name").equals("connection-id")) { //$NON-NLS-1$ //$NON-NLS-2$
					record.connectionId = field.getAttributeValue("value"); //$NON-NLS-1$
				}
				else if (field.getAttributeValue("name").equals("transaction-id")) { //$NON-NLS-1$ //$NON-NLS-2$
					record.transactionId = new Long(XMLLoader.getLong(field.getAttributeValue("value"))); //$NON-NLS-1$
				}
			}
			Version.put(record);
		}
	}
	
	private static Hashtable records = new Hashtable();
	
	private Connection connection;
	
	public static final int COLIBRI = 0;
	public static final int ADMINISTRATOR = 1;
	public static final int STATISTICS = 2;
	public static final int RECEIPT_BROWSER = 3;
}
