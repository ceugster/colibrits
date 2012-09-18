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

public class TaxType extends Table
{
	
	public String code = ""; //$NON-NLS-1$
	public String name = ""; //$NON-NLS-1$
	private RemovalAwareCollection taxes = new RemovalAwareCollection();
	
	public TaxType()
	{
		this(""); //$NON-NLS-1$ 
	}
	
	public TaxType(String code)
	{
		this(code, ""); //$NON-NLS-1$
	}
	
	public TaxType(String code, String name)
	{
		this.code = code;
		this.name = name;
	}
	
	public boolean isRemovable()
	{
		Tax[] taxes = Tax.selectByTypeId(this.getId(), false);
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
		
		if (this.isRemovable())
		{
			result = super.delete();
		}
		else
		{
			Tax[] taxes = Tax.selectByTypeId(this.getId(), false);
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
	
	public static TaxType selectById(Long pk)
	{
		TaxType taxType = new TaxType();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("id", pk); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(TaxType.class, criteria);
		Collection taxTypes = Table.select(query);
		Iterator i = taxTypes.iterator();
		if (i.hasNext())
		{
			taxType = (TaxType) i.next();
		}
		return taxType;
	}
	
	public static TaxType selectByCode(String code, boolean deletedToo)
	{
		TaxType taxType = new TaxType();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("code", code); //$NON-NLS-1$
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(TaxType.class, criteria);
		Collection taxTypes = Table.select(query);
		Iterator i = taxTypes.iterator();
		if (i.hasNext())
		{
			taxType = (TaxType) i.next();
		}
		return taxType;
	}
	
	public static TaxType selectByName(String name, boolean deletedToo)
	{
		TaxType taxType = new TaxType();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("name", name); //$NON-NLS-1$
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(TaxType.class, criteria);
		Collection taxTypes = Table.select(query);
		Iterator i = taxTypes.iterator();
		if (i.hasNext())
		{
			taxType = (TaxType) i.next();
		}
		return taxType;
	}
	
	public static TaxType[] selectAll(boolean deletedToo)
	{
		Criteria criteria = new Criteria();
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(TaxType.class, criteria);
		Collection taxTypes = Database.getCurrent().getBroker().getCollectionByQuery(query);
		return (TaxType[]) taxTypes.toArray(new TaxType[0]);
	}
	
	public static void readDBRecords()
	{
		TaxType[] types = TaxType.selectAll(false);
		for (int i = 0; i < types.length; i++)
		{
			TaxType.put(types[i]);
		}
	}
	
	public static TaxType getById(Long id)
	{
		return (TaxType) TaxType.records.get(id);
	}
	
	public static TaxType getByCode(String code)
	{
		return (TaxType) TaxType.codeIndex.get(code);
	}
	
	public static TaxType getByName(String name)
	{
		return (TaxType) TaxType.nameIndex.get(name);
	}
	
	private static void clearData()
	{
		TaxType.records.clear();
		TaxType.codeIndex.clear();
		TaxType.nameIndex.clear();
	}
	
	private static void put(TaxType type)
	{
		TaxType.records.put(type.getId(), type);
		TaxType.codeIndex.put(type.code, type);
		TaxType.nameIndex.put(type.name, type);
	}
	
	public static Element writeXMLRecords(Element root)
	{
		Element table = Database.getTemporary().getTable("tax-type"); //$NON-NLS-1$
		if (table == null)
		{
			table = new Element("table"); //$NON-NLS-1$
			table.setAttribute("name", "tax-type"); //$NON-NLS-1$ //$NON-NLS-2$
			root.addContent(table);
		}
		
		Enumeration entries = TaxType.records.elements();
		while (entries.hasMoreElements())
		{
			TaxType taxType = (TaxType) entries.nextElement();
			Element record = taxType.getJDOMRecordAttributes();
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
		TaxType.clearData();
		Element[] elements = Database.getTemporary().getRecords("tax-type"); //$NON-NLS-1$
		for (int i = 0; i < elements.length; i++)
		{
			TaxType taxType = new TaxType();
			taxType.setData(elements[i]);
			TaxType.put(taxType);
		}
	}
	
	private static Hashtable records = new Hashtable();
	private static Hashtable codeIndex = new Hashtable();
	private static Hashtable nameIndex = new Hashtable();
}
