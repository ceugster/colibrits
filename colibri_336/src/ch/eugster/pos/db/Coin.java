/*
 * Created on 05.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.jdom.Element;

import ch.eugster.pos.util.XMLLoader;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Coin extends Table
{
	
	public double value = 0d;
	public int quantity = 0;
	private Long foreignCurrencyId;
	private ForeignCurrency foreignCurrency;
	public double amount = 0d;
	
	/**
	 * 
	 */
	public Coin()
	{
		super();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.db.Table#isRemovable()
	 */
	public boolean isRemovable()
	{
		return true;
	}
	
	public void setForeignCurrency(ForeignCurrency currency)
	{
		this.foreignCurrency = currency;
		this.foreignCurrencyId = currency.getId();
	}
	
	public ForeignCurrency getForeignCurrency()
	{
		if (this.foreignCurrency == null) this.foreignCurrency = ForeignCurrency.getDefaultCurrency();
		
		return this.foreignCurrency;
	}
	
	public static Coin selectById(Long pk)
	{
		Coin coin = new Coin();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("id", pk); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Coin.class, criteria);
		Collection coins = Table.select(query);
		Iterator i = coins.iterator();
		if (i.hasNext())
		{
			coin = (Coin) i.next();
		}
		return coin;
	}
	
	public static Coin[] selectAll()
	{
		Criteria criteria = new Criteria();
		//		criteria.addOrderBy("value", true); //$NON-NLS-1$
		QueryByCriteria query = QueryFactory.newQuery(Coin.class, criteria);
		query.addOrderByAscending("value");
		return (Coin[]) Table.select(query).toArray(new Coin[0]);
	}
	
	public static Coin[] selectByForeignCurrency(ForeignCurrency currency)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("foreignCurrencyId", currency.getId());
		QueryByCriteria query = QueryFactory.newQuery(Coin.class, criteria);
		return (Coin[]) Table.select(query).toArray(new Coin[0]);
	}
	
	public static void readDBRecords()
	{
		Coin[] coins = Coin.selectAll();
		for (int i = 0; i < coins.length; i++)
		{
			Coin.put(coins[i]);
		}
	}
	
	public static Coin[] getAll()
	{
		Enumeration coins = Coin.records.elements();
		ArrayList cns = new ArrayList();
		while (coins.hasMoreElements())
		{
			cns.add(coins.nextElement());
		}
		return (Coin[]) cns.toArray(new Coin[0]);
	}
	
	private static void clearData()
	{
		Coin.records.clear();
	}
	
	private static void put(Coin coin)
	{
		Coin.records.put(coin.getId(), coin);
	}
	
	public static Element writeXMLRecords(Element root)
	{
		Element table = Database.getTemporary().getTable("coin"); //$NON-NLS-1$
		if (table == null)
		{
			table = new Element("table"); //$NON-NLS-1$
			table.setAttribute("name", "coin"); //$NON-NLS-1$ //$NON-NLS-2$
			root.addContent(table);
		}
		
		Enumeration entries = Coin.records.elements();
		while (entries.hasMoreElements())
		{
			Coin rec = (Coin) entries.nextElement();
			
			Element record = new Element("record"); //$NON-NLS-1$
			record.setAttribute("id", rec.getId().toString()); //$NON-NLS-1$
			record.setAttribute("timestamp", new Long(rec.timestamp.getTime()).toString()); //$NON-NLS-1$
			record.setAttribute("deleted", new Boolean(rec.deleted).toString()); //$NON-NLS-1$
			
			Element vl = new Element("field"); //$NON-NLS-1$
			vl.setAttribute("name", "value"); //$NON-NLS-1$ //$NON-NLS-2$
			vl.setAttribute("value", Double.toString(rec.value)); //$NON-NLS-1$
			record.addContent(vl);
			
			Element qt = new Element("field"); //$NON-NLS-1$
			qt.setAttribute("name", "quantity"); //$NON-NLS-1$ //$NON-NLS-2$
			qt.setAttribute("value", String.valueOf(rec.quantity)); //$NON-NLS-1$
			record.addContent(qt);
			
			Element am = new Element("field"); //$NON-NLS-1$
			am.setAttribute("name", "amount"); //$NON-NLS-1$ //$NON-NLS-2$
			am.setAttribute("value", String.valueOf(rec.amount)); //$NON-NLS-1$
			record.addContent(am);
			
			Element fc = new Element("field"); //$NON-NLS-1$
			fc.setAttribute("name", "foreign-currency-id"); //$NON-NLS-1$ //$NON-NLS-2$
			fc.setAttribute("value", rec.getForeignCurrency().getId().toString()); //$NON-NLS-1$
			record.addContent(fc);
			
			table.addContent(record);
		}
		return root;
	}
	
	public static void readXML()
	{
		Coin.clearData();
		Element[] elements = Database.getTemporary().getRecords("coin"); //$NON-NLS-1$
		for (int i = 0; i < elements.length; i++)
		{
			Coin record = new Coin();
			record.setId(new Long(XMLLoader.getLong(elements[i].getAttributeValue("id")))); //$NON-NLS-1$
			// 10202
			//			record.timestamp 	= XMLLoader.getTimestamp(elements[i].getAttributeValue("timestamp")); //$NON-NLS-1$
			record.timestamp = XMLLoader.getTimestampFromLong(elements[i].getAttributeValue("timestamp")); //$NON-NLS-1$
			// 10202
			record.deleted = new Boolean(elements[i].getAttributeValue("deleted")).booleanValue(); //$NON-NLS-1$
			
			List fields = elements[i].getChildren("field"); //$NON-NLS-1$
			Iterator iter = fields.iterator();
			while (iter.hasNext())
			{
				Element field = (Element) iter.next();
				if (field.getAttributeValue("name").equals("value")) { //$NON-NLS-1$ //$NON-NLS-2$
					record.value = XMLLoader.getDouble(field.getAttributeValue("value")); //$NON-NLS-1$
				}
				else if (field.getAttributeValue("name").equals("quantity")) { //$NON-NLS-1$ //$NON-NLS-2$
					record.quantity = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
				}
				else if (field.getAttributeValue("name").equals("amount")) { //$NON-NLS-1$ //$NON-NLS-2$
					record.amount = XMLLoader.getDouble(field.getAttributeValue("value")); //$NON-NLS-1$
				}
				else if (field.getAttributeValue("name").equals("foreign-currency-id")) { //$NON-NLS-1$ //$NON-NLS-2$
					ForeignCurrency foreignCurrency = ForeignCurrency.getById(new Long(XMLLoader.getLong(field
									.getAttributeValue("value")))); //$NON-NLS-1$
					if (foreignCurrency != null)
					{
						record.setForeignCurrency(foreignCurrency);
					}
				}
			}
			Coin.put(record);
		}
	}
	
	private static Hashtable records = new Hashtable();
}
