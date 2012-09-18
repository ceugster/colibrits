/*
 * Created on 19.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
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
import org.jdom.Element;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Option extends Table
{
	
	public String code = ""; //$NON-NLS-1$
	public String name = ""; //$NON-NLS-1$
	
	/**
	 * 
	 */
	public Option()
	{
		super();
	}
	
	public Option(String code, String name)
	{
		super();
		this.code = code;
		this.name = name;
	}
	
	public boolean isRemovable()
	{
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
	
	public static Option selectById(Long pk)
	{
		Option option = new Option();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("id", pk); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Option.class, criteria);
		Collection options = Table.select(query);
		Iterator i = options.iterator();
		if (i.hasNext())
		{
			option = (Option) i.next();
		}
		return option;
	}
	
	public static Option selectByCode(String code, boolean deletedToo)
	{
		Option option = new Option();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("code", code); //$NON-NLS-1$
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Option.class, criteria);
		Collection options = Table.select(query);
		Iterator i = options.iterator();
		if (i.hasNext())
		{
			option = (Option) i.next();
		}
		return option;
	}
	
	public static Option[] selectAll(boolean deletedToo)
	{
		Criteria criteria = new Criteria();
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Option.class, criteria);
		Collection options = Table.select(query);
		return (Option[]) options.toArray(new Option[0]);
	}
	
	public static void readDBRecords()
	{
		Option[] opts = Option.selectAll(false);
		for (int i = 0; i < opts.length; i++)
		{
			Option.put(opts[i]);
		}
	}
	
	public static Option getById(Long id)
	{
		return (Option) Option.records.get(id);
	}
	
	public static Option getByCode(String code)
	{
		return (Option) Option.codeIndex.get(code);
	}
	
	private static void clearData()
	{
		Option.records.clear();
		Option.codeIndex.clear();
	}
	
	private static void put(Option option)
	{
		Option.records.put(option.getId(), option);
		Option.codeIndex.put(option.code, option);
	}
	
	public static Element writeXMLRecords(Element root)
	{
		Element table = Database.getTemporary().getTable("option"); //$NON-NLS-1$
		if (table == null)
		{
			table = new Element("table"); //$NON-NLS-1$
			table.setAttribute("name", "option"); //$NON-NLS-1$ //$NON-NLS-2$
			root.addContent(table);
		}
		
		Enumeration entries = Option.records.elements();
		while (entries.hasMoreElements())
		{
			Option option = (Option) entries.nextElement();
			Element record = option.getJDOMRecordAttributes();
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
		Option.clearData();
		Element[] elements = Database.getTemporary().getRecords("option"); //$NON-NLS-1$
		for (int i = 0; i < elements.length; i++)
		{
			Option option = new Option();
			option.setData(elements[i]);
			Option.put(option);
		}
	}
	
	private static Hashtable records = new Hashtable();
	private static Hashtable codeIndex = new Hashtable();
}
