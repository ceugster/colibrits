package ch.eugster.pos.db;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.util.collections.RemovalAwareCollection;
import org.jdom.Element;

public class TaxRate extends Table
{
	
	public String code = ""; //$NON-NLS-1$
	public String name = ""; //$NON-NLS-1$
	private RemovalAwareCollection taxes = new RemovalAwareCollection();
	
	public TaxRate()
	{
		this(""); //$NON-NLS-1$
	}
	
	public TaxRate(String code)
	{
		this(code, ""); //$NON-NLS-1$
	}
	
	public TaxRate(String code, String name)
	{
		this.code = code;
		this.name = name;
	}
	
	public boolean isRemovable()
	{
		Tax[] taxes = Tax.selectByRateId(this.getId(), false);
		for (int i = 0; i < taxes.length; i++)
		{
			if (!taxes[i].isRemovable())
			{
				return false;
			}
		}
		return true;
	}
	
	public DBResult delete()
	{
		PersistenceBroker broker = Database.getCurrent().getBroker();
		DBResult result = new DBResult();
		boolean isMyTransaction = !broker.isInTransaction();
		if (isMyTransaction)
		{
			broker.beginTransaction();
		}
		
		CustomKey.delete(this.getClass().getName(), this.getId());
		if (this.isRemovable())
		{
			result = super.delete();
		}
		else
		{
			Tax[] taxes = Tax.selectByRateId(this.getId(), false);
			for (int i = 0; i < taxes.length; i++)
			{
				result = taxes[i].delete();
			}
			this.deleted = true;
			result = super.store();
		}
		
		if (isMyTransaction)
		{
			if (result.getErrorCode() == 0)
			{
				broker.commitTransaction();
			}
			else
			{
				broker.abortTransaction();
			}
		}
		return result;
	}
	
	public static TaxRate selectById(Long pk)
	{
		TaxRate taxRate = new TaxRate();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("id", pk); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(TaxRate.class, criteria);
		Collection taxRates = Table.select(query);
		Iterator i = taxRates.iterator();
		if (i.hasNext())
		{
			taxRate = (TaxRate) i.next();
		}
		return taxRate;
	}
	
	public static TaxRate selectByCode(String code, boolean deletedToo)
	{
		TaxRate taxRate = new TaxRate();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("code", code); //$NON-NLS-1$
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(TaxRate.class, criteria);
		Collection taxRates = Table.select(query);
		Iterator i = taxRates.iterator();
		if (i.hasNext())
		{
			taxRate = (TaxRate) i.next();
		}
		return taxRate;
	}
	
	public static TaxRate selectByName(String name, boolean deletedToo)
	{
		TaxRate taxRate = new TaxRate();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("name", name); //$NON-NLS-1$
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(TaxRate.class, criteria);
		Collection taxRates = Table.select(query);
		Iterator i = taxRates.iterator();
		if (i.hasNext())
		{
			taxRate = (TaxRate) i.next();
		}
		return taxRate;
	}
	
	public static TaxRate[] selectAll(boolean deletedToo)
	{
		Criteria criteria = new Criteria();
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(TaxRate.class, criteria);
		Collection taxRates = Database.getCurrent().getBroker().getCollectionByQuery(query);
		return (TaxRate[]) taxRates.toArray(new TaxRate[0]);
	}
	
	public static void readDBRecords()
	{
		TaxRate[] rates = TaxRate.selectAll(false);
		for (int i = 0; i < rates.length; i++)
		{
			TaxRate.put(rates[i]);
		}
	}
	
	public static TaxRate getById(Long id)
	{
		return (TaxRate) TaxRate.records.get(id);
	}
	
	public static TaxRate getByCode(String code)
	{
		return (TaxRate) TaxRate.codeIndex.get(code);
	}
	
	public static TaxRate getByName(String name)
	{
		return (TaxRate) TaxRate.nameIndex.get(name);
	}
	
	private static void clearData()
	{
		TaxRate.records.clear();
		TaxRate.codeIndex.clear();
		TaxRate.nameIndex.clear();
	}
	
	private static void put(TaxRate rate)
	{
		TaxRate.records.put(rate.getId(), rate);
		TaxRate.codeIndex.put(rate.code, rate);
		TaxRate.nameIndex.put(rate.name, rate);
	}
	
	public static Element writeXMLRecords(Element root)
	{
		Element table = Database.getTemporary().getTable("tax-rate"); //$NON-NLS-1$
		if (table == null)
		{
			table = new Element("table"); //$NON-NLS-1$
			table.setAttribute("name", "tax-rate"); //$NON-NLS-1$ //$NON-NLS-2$
			root.addContent(table);
		}
		
		Enumeration entries = TaxRate.records.elements();
		while (entries.hasMoreElements())
		{
			TaxRate taxRate = (TaxRate) entries.nextElement();
			Element record = taxRate.getJDOMRecordAttributes();
			table.addContent(record);
		}
		return root;
	}
	
	protected Element getJDOMRecordAttributes()
	{
		Element record = super.getJDOMRecordAttributes();
		
		Element cd = new Element("field"); //$NON-NLS-1$
		cd.setAttribute("name", "code"); //$NON-NLS-1$ //$NON-NLS-2$
		cd.setAttribute("value", this.code); //$NON-NLS-1$
		record.addContent(cd);
		
		Element nm = new Element("field"); //$NON-NLS-1$
		nm.setAttribute("name", "name"); //$NON-NLS-1$ //$NON-NLS-2$
		nm.setAttribute("value", this.name); //$NON-NLS-1$
		record.addContent(nm);
		
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
			if (field.getAttributeValue("name").equals("code")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.code = field.getAttributeValue("value"); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("name")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.name = field.getAttributeValue("value"); //$NON-NLS-1$
			}
		}
	}
	
	public static void readXML()
	{
		TaxRate.clearData();
		Element[] elements = Database.getTemporary().getRecords("tax-rate"); //$NON-NLS-1$
		for (int i = 0; i < elements.length; i++)
		{
			TaxRate taxRate = new TaxRate();
			taxRate.setData(elements[i]);
			TaxRate.put(taxRate);
		}
	}
	
	private static Hashtable records = new Hashtable();
	private static Hashtable codeIndex = new Hashtable();
	private static Hashtable nameIndex = new Hashtable();
}
