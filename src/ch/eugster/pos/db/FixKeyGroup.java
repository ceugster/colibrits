/*
 * Created on 23.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
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
import org.apache.ojb.broker.util.collections.RemovalAwareCollection;
import org.jdom.Element;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class FixKeyGroup extends Table
{
	
	public String name = ""; //$NON-NLS-1$
	private RemovalAwareCollection fixKeys = new RemovalAwareCollection();
	
	/**
	 * 
	 */
	public FixKeyGroup()
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
	
	public static FixKeyGroup[] selectAll()
	{
		Criteria criteria = new Criteria();
		Query query = QueryFactory.newQuery(FixKeyGroup.class, criteria);
		Collection groups = Table.select(query);
		return (FixKeyGroup[]) groups.toArray(new FixKeyGroup[0]);
	}
	
	public static void readDBRecords()
	{
		FixKeyGroup[] fixKeyGroups = FixKeyGroup.selectAll();
		for (int i = 0; i < fixKeyGroups.length; i++)
		{
			FixKeyGroup.put(fixKeyGroups[i]);
		}
	}
	
	public static FixKeyGroup getById(Long id)
	{
		return (FixKeyGroup) FixKeyGroup.records.get(id);
	}
	
	private static void clearData()
	{
		FixKeyGroup.records.clear();
	}
	
	private static void put(FixKeyGroup fixKeyGroup)
	{
		FixKeyGroup.records.put(fixKeyGroup.getId(), fixKeyGroup);
	}
	
	public static Element writeXMLRecords(Element root)
	{
		Element table = Database.getTemporary().getTable("fix-key-group"); //$NON-NLS-1$
		if (table == null)
		{
			table = new Element("table"); //$NON-NLS-1$
			table.setAttribute("name", "fix-key-group"); //$NON-NLS-1$ //$NON-NLS-2$
			root.addContent(table);
		}
		
		Enumeration entries = FixKeyGroup.records.elements();
		while (entries.hasMoreElements())
		{
			FixKeyGroup rec = (FixKeyGroup) entries.nextElement();
			Element record = rec.getJDOMRecordAttributes();
			table.addContent(record);
		}
		return root;
	}
	
	protected Element getJDOMRecordAttributes()
	{
		Element record = super.getJDOMRecordAttributes();
		
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
			if (field.getAttributeValue("name").equals("name")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.name = field.getAttributeValue("value"); //$NON-NLS-1$
			}
		}
	}
	
	public static void readXML()
	{
		FixKeyGroup.clearData();
		Element[] elements = Database.getTemporary().getRecords("fix-key-group"); //$NON-NLS-1$
		for (int i = 0; i < elements.length; i++)
		{
			FixKeyGroup key = new FixKeyGroup();
			key.setData(elements[i]);
			FixKeyGroup.put(key);
		}
	}
	
	private static Hashtable records = new Hashtable();
	
}
