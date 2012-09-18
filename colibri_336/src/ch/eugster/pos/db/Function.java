/*
 * Created on 11.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
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
import org.apache.ojb.broker.query.QueryFactory;
import org.jdom.Element;

import ch.eugster.pos.util.XMLLoader;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class Function extends Table
{
	
	public String clazz = ""; //$NON-NLS-1$
	public int actionType = Table.INTEGER_DEFAULT_ZERO;
	public String name = ""; //$NON-NLS-1$
	public String shortname = ""; //$NON-NLS-1$
	
	/**
	 * 
	 */
	public Function()
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
	
	public static Function selectById(Long id)
	{
		Function function = null;
		Criteria criteria = new Criteria();
		criteria.addEqualTo("id", id);
		Query query = QueryFactory.newQuery(Function.class, criteria);
		Collection functions = Table.select(query);
		if (!functions.isEmpty())
		{
			function = (Function) functions.iterator().next();
		}
		return function != null ? function : new Function();
	}
	
	public static Function[] selectAll()
	{
		Criteria criteria = new Criteria();
		Query query = QueryFactory.newQuery(Function.class, criteria);
		Collection functions = Table.select(query);
		return (Function[]) functions.toArray(new Function[0]);
	}
	
	public static void readDBRecords()
	{
		Function[] functions = Function.selectAll();
		for (int i = 0; i < functions.length; i++)
		{
			Function.put(functions[i]);
		}
	}
	
	public static Function[] getAll()
	{
		ArrayList functions = new ArrayList();
		Enumeration enumeration = Function.records.elements();
		while (enumeration.hasMoreElements())
		{
			functions.add(enumeration.nextElement());
		}
		return (Function[]) functions.toArray(new Function[0]);
	}
	
	public static Function getById(Long id)
	{
		return (Function) Function.records.get(id);
	}
	
	private static void clearData()
	{
		Function.records.clear();
	}
	
	private static void put(Function function)
	{
		Function.records.put(function.getId(), function);
	}
	
	public static Element writeXMLRecords(Element root)
	{
		Element table = Database.getTemporary().getTable("function"); //$NON-NLS-1$
		if (table == null)
		{
			table = new Element("table"); //$NON-NLS-1$
			table.setAttribute("name", "function"); //$NON-NLS-1$ //$NON-NLS-2$
			root.addContent(table);
		}
		
		Enumeration entries = Function.records.elements();
		while (entries.hasMoreElements())
		{
			Function rec = (Function) entries.nextElement();
			Element record = rec.getJDOMRecordAttributes();
			table.addContent(record);
		}
		return root;
	}
	
	protected Element getJDOMRecordAttributes()
	{
		Element record = super.getJDOMRecordAttributes();
		
		Element cl = new Element("field"); //$NON-NLS-1$
		cl.setAttribute("name", "class"); //$NON-NLS-1$ //$NON-NLS-2$
		cl.setAttribute("value", this.clazz); //$NON-NLS-1$
		record.addContent(cl);
		
		Element at = new Element("field"); //$NON-NLS-1$
		at.setAttribute("name", "action-type"); //$NON-NLS-1$ //$NON-NLS-2$
		at.setAttribute("value", Integer.toString(this.actionType)); //$NON-NLS-1$
		record.addContent(at);
		
		Element na = new Element("field"); //$NON-NLS-1$
		na.setAttribute("name", "name"); //$NON-NLS-1$ //$NON-NLS-2$
		na.setAttribute("value", this.name); //$NON-NLS-1$
		record.addContent(na);
		
		Element sn = new Element("field"); //$NON-NLS-1$
		sn.setAttribute("name", "shortname"); //$NON-NLS-1$ //$NON-NLS-2$
		sn.setAttribute("value", this.shortname); //$NON-NLS-1$
		record.addContent(sn);
		
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
			if (field.getAttributeValue("name").equals("class")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.clazz = field.getAttributeValue("value"); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("action-type")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.actionType = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("name")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.name = field.getAttributeValue("value"); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("shortname")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.shortname = field.getAttributeValue("value"); //$NON-NLS-1$
			}
		}
	}
	
	public static void readXML()
	{
		Function.clearData();
		Element[] elements = Database.getTemporary().getRecords("function"); //$NON-NLS-1$
		for (int i = 0; i < elements.length; i++)
		{
			Function function = new Function();
			function.setData(elements[i]);
			Function.put(function);
		}
	}
	
	private static Hashtable records = new Hashtable();
	
}
