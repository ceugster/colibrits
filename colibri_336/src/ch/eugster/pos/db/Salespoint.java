/*
 * Created on 04.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.db;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.jdom.Element;

import ch.eugster.pos.util.XMLLoader;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Salespoint extends Table
{
	
	public String name = ""; //$NON-NLS-1$
	public String place = ""; //$NON-NLS-1$
	public double stock = 0d;
	public boolean variableStock = false; // 10183
	public Long currentReceiptId = null;
	public Timestamp currentDate = new Timestamp(Calendar.getInstance().getTimeInMillis());
	public boolean active = false;
	public String exportId = "";
	
	private Collection stocks = new ArrayList();
	
	private Long currentReceiptNumber;
	
	/**
	 * Beleglaufnummer im Failover-Modus
	 * 
	 */
	private static long currentTemporaryReceiptNumber = 0l;
	
	/**
	 * 
	 */
	public Salespoint()
	{
		super();
	}
	
	// 10193
	public Long getNextReceiptNumber()
	{
		if (Database.getCurrent().equals(Database.getTemporary()))
		{
			return this.getNextTemporaryReceiptNumber();
		}
		else
		{
			if (this.currentReceiptId == null || this.currentReceiptId.longValue() < 1l)
			{
				this.currentReceiptNumber = new Long(1l);
			}
			else
			{
				this.currentReceiptNumber = new Long(this.currentReceiptId.longValue() + 1);
			}
			return this.currentReceiptNumber;
		}
	}
	
	// 10193
	
	public Long getNextTemporaryReceiptNumber()
	{
		Salespoint.currentTemporaryReceiptNumber++;
		return new Long(Salespoint.currentTemporaryReceiptNumber);
	}
	
	// 10193
	public void setNextReceiptNumber()
	{
		if (Database.getCurrent().equals(Database.getStandard()))
		{
			this.currentReceiptId = this.currentReceiptNumber;
			this.store();
		}
	}
	
	// 10193
	
	// 10193
	public void setNextReceiptNumber(Long number)
	{
		this.currentReceiptId = number;
		this.store();
	}
	
	// 10193
	
	// 10226
	public Collection getStocks()
	{
		return this.stocks;
	}
	
	public Stock getStock(ForeignCurrency foreignCurrency)
	{
		Iterator iterator = this.stocks.iterator();
		while (iterator.hasNext())
		{
			Stock stock = (Stock) iterator.next();
			if (stock.getForeignCurrency().getId().equals(foreignCurrency.getId())) return stock;
		}
		Stock stock = Stock.insert(new Stock(this, foreignCurrency));
		this.stocks.add(stock);
		return stock;
	}
	
	public Stock getStock(String code)
	{
		Iterator iterator = this.stocks.iterator();
		while (iterator.hasNext())
		{
			Stock stock = (Stock) iterator.next();
			if (stock.getForeignCurrency().code.equals(code)) return stock;
		}
		ForeignCurrency foreignCurrency = ForeignCurrency.getByCode(code);
		Stock stock = Stock.insert(new Stock(this, foreignCurrency));
		stock.store();
		this.stocks.add(stock);
		return stock;
	}
	
	// public void setStock(Stock stock)
	// {
	// Stock myStock = null;
	// Iterator iterator = stocks.iterator();
	// while (iterator.hasNext())
	// {
	// myStock = (Stock) iterator.next();
	// if (myStock.getId().equals(stock.getId()))
	// break;
	// }
	// if (myStock != null)
	// {
	// stocks.remove(myStock);
	// stocks.add(stock);
	// }
	// }
	
	public void addStock(Stock stock)
	{
		this.stocks.add(stock);
	}
	
	public void setStocks(Collection stocks)
	{
		this.stocks = stocks;
	}
	
	// 10226
	
	public boolean isRemovable()
	{
		return !Receipt.exist("salespointId", this.getId()); //$NON-NLS-1$
	}
	
	public static Salespoint selectById(Long pk)
	{
		Salespoint salespoint = new Salespoint();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("id", pk); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Salespoint.class, criteria);
		Collection salespoints = Table.select(query);
		Iterator i = salespoints.iterator();
		if (i.hasNext())
		{
			salespoint = (Salespoint) i.next();
		}
		return salespoint;
	}
	
	public static Salespoint selectByName(String name)
	{
		Salespoint salespoint = new Salespoint();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("name", name); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Salespoint.class, criteria);
		Collection salespoints = Table.select(query);
		Iterator i = salespoints.iterator();
		if (i.hasNext())
		{
			salespoint = (Salespoint) i.next();
		}
		return salespoint;
	}
	
	public static Salespoint selectByExportId(String exportId)
	{
		Salespoint salespoint = new Salespoint();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("exportId", exportId); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Salespoint.class, criteria);
		Collection salespoints = Table.select(query);
		Iterator i = salespoints.iterator();
		if (i.hasNext())
		{
			salespoint = (Salespoint) i.next();
		}
		return salespoint;
	}
	
	public static Salespoint[] selectAll(boolean deletedToo)
	{
		Criteria criteria = new Criteria();
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Salespoint.class, criteria);
		Collection salespoints = Table.select(query);
		System.out.println();
		return (Salespoint[]) salespoints.toArray(new Salespoint[0]);
	}
	
	public static void setCurrent(Salespoint salespoint)
	{
		Salespoint.current = salespoint;
	}
	
	public static Salespoint getCurrent()
	{
		return Salespoint.current;
	}
	
	public static Criteria getSalespointCriteria(Salespoint[] salespoints)
	{
		Criteria criteria = new Criteria();
		if (salespoints != null)
		{
			if (salespoints.length != 0)
			{
				criteria.addEqualTo("salespointId", salespoints[0].getId()); //$NON-NLS-1$
				for (int i = 1; i < salespoints.length; i++)
				{
					Criteria orCriteria = new Criteria();
					orCriteria.addEqualTo("salespointId", salespoints[i].getId()); //$NON-NLS-1$
					criteria.addOrCriteria(orCriteria);
				}
			}
		}
		return criteria;
	}
	
	public static void readDBRecords()
	{
		Salespoint[] salespoints = Salespoint.selectAll(false);
		for (int i = 0; i < salespoints.length; i++)
		{
			Salespoint.put(salespoints[i]);
		}
	}
	
	public static Salespoint getById(Long id)
	{
		return (Salespoint) Salespoint.records.get(id);
	}
	
	private static void clearData()
	{
		Salespoint.records.clear();
	}
	
	private static void put(Salespoint salespoint)
	{
		Salespoint.records.put(salespoint.getId(), salespoint);
	}
	
	public static Element writeXMLRecords(Element root)
	{
		Element table = Database.getTemporary().getTable("salespoint"); //$NON-NLS-1$
		if (table == null)
		{
			table = new Element("table"); //$NON-NLS-1$
			table.setAttribute("name", "salespoint"); //$NON-NLS-1$ //$NON-NLS-2$
			root.addContent(table);
		}
		
		Enumeration entries = Salespoint.records.elements();
		while (entries.hasMoreElements())
		{
			Salespoint salespoint = (Salespoint) entries.nextElement();
			Element record = salespoint.getJDOMRecordAttributes();
			table.addContent(record);
		}
		return root;
	}
	
	protected Element getJDOMRecordAttributes()
	{
		Element record = super.getJDOMRecordAttributes();
		
		Element na = new Element("field"); //$NON-NLS-1$
		na.setAttribute("name", "name"); //$NON-NLS-1$ //$NON-NLS-2$
		na.setAttribute("value", this.name); //$NON-NLS-1$
		record.addContent(na);
		
		Element pl = new Element("field"); //$NON-NLS-1$
		pl.setAttribute("name", "place"); //$NON-NLS-1$ //$NON-NLS-2$
		pl.setAttribute("value", this.place); //$NON-NLS-1$
		record.addContent(pl);
		
		Element st = new Element("field"); //$NON-NLS-1$
		st.setAttribute("name", "stock"); //$NON-NLS-1$ //$NON-NLS-2$
		st.setAttribute("value", Double.toString(this.stock)); //$NON-NLS-1$
		record.addContent(st);
		
		Element ci = new Element("field"); //$NON-NLS-1$
		ci.setAttribute("name", "current-receipt-id"); //$NON-NLS-1$ //$NON-NLS-2$
		ci.setAttribute("value", this.currentReceiptId.toString()); //$NON-NLS-1$
		record.addContent(ci);
		
		Element cd = new Element("field"); //$NON-NLS-1$
		cd.setAttribute("name", "current-settle-date"); //$NON-NLS-1$ //$NON-NLS-2$
		cd.setAttribute("value", this.currentDate.toString()); //$NON-NLS-1$
		record.addContent(cd);
		
		Element ac = new Element("field"); //$NON-NLS-1$
		ac.setAttribute("name", "active"); //$NON-NLS-1$ //$NON-NLS-2$
		ac.setAttribute("value", new Boolean(this.active).toString()); //$NON-NLS-1$
		record.addContent(ac);
		
		Element ei = new Element("field"); //$NON-NLS-1$
		ei.setAttribute("name", "export-id"); //$NON-NLS-1$ //$NON-NLS-2$
		ei.setAttribute("value", this.exportId); //$NON-NLS-1$
		record.addContent(ei);
		
		return record;
	}
	
	protected void setData(Element record)
	{
		super.getData(record);
		
		List fields = record.getChildren("field"); //$NON-NLS-1$
		Iterator iter = fields.iterator();
		while (iter.hasNext())
		{
			Element field = (Element) iter.next();
			if (field.getAttributeValue("name").equals("name")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.name = field.getAttributeValue("value"); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("place")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.place = field.getAttributeValue("value"); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("stock")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.stock = XMLLoader.getDouble(field.getAttributeValue("value")); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("current-receipt-id")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.currentReceiptId = new Long(XMLLoader.getLong(field.getAttributeValue("value"))); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("current-settle-date")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.currentDate = XMLLoader.getTimestamp(field.getAttributeValue("value")); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("active")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.active = new Boolean(field.getAttributeValue("value")).booleanValue(); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("export-id")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.exportId = field.getAttributeValue("value"); //$NON-NLS-1$
			}
		}
	}
	
	public static void readXML()
	{
		Salespoint.clearData();
		Element[] elements = Database.getTemporary().getRecords("salespoint"); //$NON-NLS-1$
		for (int i = 0; i < elements.length; i++)
		{
			Salespoint salespoint = new Salespoint();
			salespoint.setData(elements[i]);
			Salespoint.put(salespoint);
		}
	}
	
	private static Hashtable records = new Hashtable();
	
	private static Salespoint current = null;
}
