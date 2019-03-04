/*
 * Created on 11.12.2003
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
import org.jdom.Element;

import ch.eugster.pos.util.XMLLoader;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class KeyGroup extends Table
{
	
	public String name = ""; //$NON-NLS-1$
	public String clazz = ""; //$NON-NLS-1$
	public String actionClass = ""; //$NON-NLS-1$
	public int actionType = 0;
	
	/**
	 * 
	 */
	public KeyGroup()
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
	
	public static KeyGroup selectByClass(String className)
	{
		KeyGroup group = null;
		Criteria criteria = new Criteria();
		criteria.addEqualTo("clazz", className); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(KeyGroup.class, criteria);
		Collection keyGroups = Table.select(query);
		Iterator iterator = keyGroups.iterator();
		if (iterator.hasNext())
		{
			group = (KeyGroup) iterator.next();
		}
		return group;
	}
	
	public static KeyGroup[] selectAll()
	{
		Criteria criteria = new Criteria();
		Query query = QueryFactory.newQuery(KeyGroup.class, criteria);
		Collection keyGroups = Table.select(query);
		return (KeyGroup[]) keyGroups.toArray(new KeyGroup[0]);
	}
	
	public static void readDBRecords()
	{
		KeyGroup[] opts = KeyGroup.selectAll();
		for (int i = 0; i < opts.length; i++)
		{
			KeyGroup.put(opts[i]);
		}
	}
	
	public static KeyGroup getById(Long id)
	{
		return (KeyGroup) KeyGroup.records.get(id);
	}
	
	public static KeyGroup getByClass(String clazz)
	{
		return (KeyGroup) KeyGroup.classIndex.get(clazz);
	}
	
	private static void clearData()
	{
		KeyGroup.records.clear();
		KeyGroup.classIndex.clear();
	}
	
	private static void put(KeyGroup keyGroup)
	{
		KeyGroup.records.put(keyGroup.getId(), keyGroup);
		KeyGroup.classIndex.put(keyGroup.clazz, keyGroup);
	}
	
	public static Element writeXMLRecords(Element root)
	{
		Element table = Database.getTemporary().getTable("key-group"); //$NON-NLS-1$
		if (table == null)
		{
			table = new Element("table"); //$NON-NLS-1$
			table.setAttribute("name", "key-group"); //$NON-NLS-1$ //$NON-NLS-2$
			root.addContent(table);
		}
		
		Enumeration entries = KeyGroup.records.elements();
		while (entries.hasMoreElements())
		{
			KeyGroup rec = (KeyGroup) entries.nextElement();
			Element record = rec.getJDOMRecordAttributes();
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
		
		Element cl = new Element("field"); //$NON-NLS-1$
		cl.setAttribute("name", "class"); //$NON-NLS-1$ //$NON-NLS-2$
		cl.setAttribute("value", this.clazz); //$NON-NLS-1$
		record.addContent(cl);
		
		Element ac = new Element("field"); //$NON-NLS-1$
		ac.setAttribute("name", "actin-class"); //$NON-NLS-1$ //$NON-NLS-2$
		ac.setAttribute("value", this.actionClass); //$NON-NLS-1$
		record.addContent(ac);
		
		Element at = new Element("field"); //$NON-NLS-1$
		at.setAttribute("name", "action-type"); //$NON-NLS-1$ //$NON-NLS-2$
		at.setAttribute("value", Integer.toString(this.actionType)); //$NON-NLS-1$
		record.addContent(at);
		
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
			else if (field.getAttributeValue("name").equals("class")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.clazz = field.getAttributeValue("value"); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("action-class")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.actionClass = field.getAttributeValue("value"); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("action-type")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.actionType = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
			}
		}
	}
	
	public static void readXML()
	{
		KeyGroup.clearData();
		Element[] elements = Database.getTemporary().getRecords("key-group"); //$NON-NLS-1$
		for (int i = 0; i < elements.length; i++)
		{
			KeyGroup keyGroup = new KeyGroup();
			keyGroup.setData(elements[i]);
			KeyGroup.put(keyGroup);
		}
	}
	
	private static Hashtable records = new Hashtable();
	private static Hashtable classIndex = new Hashtable();
}
