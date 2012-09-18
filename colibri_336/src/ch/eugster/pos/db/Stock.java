/*
 * Created on 22.05.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.db;

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
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Stock extends Table
{
	private Salespoint salespoint;
	private Long salespointId;
	
	private ForeignCurrency foreignCurrency;
	private Long foreignCurrencyId;
	
	private double stock;
	
	public Stock()
	{
		
	}
	
	public Stock(Salespoint salespoint, ForeignCurrency currency)
	{
		this.setSalespoint(salespoint);
		this.setForeignCurrency(currency);
		this.setStock(0d);
	}
	
	public void setSalespoint(Salespoint salespoint)
	{
		this.salespoint = salespoint;
		this.salespointId = salespoint == null ? null : salespoint.getId();
	}
	
	public Salespoint getSalespoint()
	{
		return this.salespoint;
	}
	
	public Long getSalespointId()
	{
		return this.salespointId;
	}
	
	public void setForeignCurrency(ForeignCurrency foreignCurrency)
	{
		this.foreignCurrency = foreignCurrency;
		this.foreignCurrencyId = foreignCurrency == null ? null : foreignCurrency.getId();
	}
	
	public ForeignCurrency getForeignCurrency()
	{
		return this.foreignCurrency;
	}
	
	public Long getForeignCurrencyId()
	{
		return this.foreignCurrencyId;
	}
	
	public void setStock(double stock)
	{
		this.stock = stock;
	}
	
	public double getStock()
	{
		return this.stock;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.db.Table#isRemovable()
	 */
	public boolean isRemovable()
	{
		return PaymentType.selectByCurrency(this.getForeignCurrency()) == null;
	}
	
	public static Stock insert(Stock stock)
	{
		stock.store();
		return stock;
	}
	
	public static Stock[] selectAll()
	{
		Criteria criteria = new Criteria();
		Query query = QueryFactory.newQuery(Stock.class, criteria);
		Collection stocks = Table.select(query);
		return (Stock[]) stocks.toArray(new Stock[0]);
	}
	
	public static Stock[] selectBySalespoint(Salespoint salespoint)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("salespointId", salespoint.getId());
		Query query = QueryFactory.newQuery(Stock.class, criteria);
		Collection stocks = Table.select(query);
		return (Stock[]) stocks.toArray(new Stock[0]);
	}
	
	public static Stock select(Salespoint salespoint, ForeignCurrency currency)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("salespointId", salespoint.getId());
		criteria.addEqualTo("foreignCurrencyId", currency.getId());
		Query query = QueryFactory.newQuery(Stock.class, criteria);
		Collection stocks = Table.select(query);
		if (stocks.isEmpty())
			return Stock.insert(new Stock(salespoint, currency));
		else
			return ((Stock[]) stocks.toArray(new Stock[0]))[0];
	}
	
	public static void readDBRecords()
	{
		Stock[] stocks = Stock.selectAll();
		for (int i = 0; i < stocks.length; i++)
		{
			Stock.put(stocks[i]);
		}
	}
	
	public static void readXML()
	{
		Stock.clearData();
		Element[] elements = Database.getTemporary().getRecords("stock"); //$NON-NLS-1$
		for (int i = 0; i < elements.length; i++)
		{
			Stock stock = new Stock();
			stock.setData(elements[i]);
			Stock.put(stock);
		}
	}
	
	private static void clearData()
	{
		Stock.records.clear();
	}
	
	private static void put(Stock stock)
	{
		Stock.records.put(stock.getId(), stock);
	}
	
	public static Element writeXMLRecords(Element root)
	{
		Element table = Database.getTemporary().getTable("stock"); //$NON-NLS-1$
		if (table == null)
		{
			table = new Element("table"); //$NON-NLS-1$
			table.setAttribute("name", "stock"); //$NON-NLS-1$ //$NON-NLS-2$
			root.addContent(table);
		}
		
		Enumeration entries = Stock.records.elements();
		while (entries.hasMoreElements())
		{
			Stock stock = (Stock) entries.nextElement();
			Element record = stock.getJDOMRecordAttributes();
			table.addContent(record);
		}
		return root;
	}
	
	protected Element getJDOMRecordAttributes()
	{
		Element record = super.getJDOMRecordAttributes();
		
		Element sp = new Element("field"); //$NON-NLS-1$
		sp.setAttribute("name", "salespoint-id"); //$NON-NLS-1$ //$NON-NLS-2$
		sp.setAttribute("value", this.salespointId.toString()); //$NON-NLS-1$
		record.addContent(sp);
		
		Element fc = new Element("field"); //$NON-NLS-1$
		fc.setAttribute("name", "foreign-currency-id"); //$NON-NLS-1$ //$NON-NLS-2$
		fc.setAttribute("value", this.foreignCurrencyId.toString()); //$NON-NLS-1$
		record.addContent(fc);
		
		Element st = new Element("field"); //$NON-NLS-1$
		st.setAttribute("name", "stock"); //$NON-NLS-1$ //$NON-NLS-2$
		st.setAttribute("value", new Double(this.stock).toString()); //$NON-NLS-1$
		record.addContent(st);
		
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
			if (field.getAttributeValue("name").equals("salespoint-id")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.setSalespoint(Salespoint.getById(new Long(XMLLoader.getLong(field.getAttributeValue("value")))));
			}
			else if (field.getAttributeValue("name").equals("foreign-currency-id")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.setForeignCurrency(ForeignCurrency.getById(new Long(XMLLoader.getLong(field
								.getAttributeValue("value"))))); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("stock")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.stock = XMLLoader.getDouble(field.getAttributeValue("value")); //$NON-NLS-1$
			}
		}
	}
	
	private static Hashtable records = new Hashtable();
}
