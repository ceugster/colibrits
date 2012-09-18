/*
 * Created on 08.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
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
import org.apache.ojb.broker.util.collections.RemovalAwareCollection;
import org.jdom.Element;

import ch.eugster.pos.util.XMLLoader;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Block extends Table
{
	
	public boolean visible = Table.BOOLEAN_DEFAULT_FALSE;
	public String name = ""; //$NON-NLS-1$
	public String clazz = ""; //$NON-NLS-1$
	public double fontSize = 14f;
	public int fontStyle = 0;
	
	private RemovalAwareCollection tabs = new RemovalAwareCollection();
	
	/**
	 * 
	 */
	public Block()
	{
		super();
	}
	
	public boolean isRemovable()
	{
		return true;
	}
	
	public static Block selectByClass(String clazz)
	{
		Block b = null;
		Criteria criteria = new Criteria();
		criteria.addEqualTo("clazz", clazz); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Block.class, criteria);
		Collection blocks = Table.select(query);
		Iterator iter = blocks.iterator();
		if (iter.hasNext())
		{
			b = (Block) iter.next();
		}
		return b;
	}
	
	public static Block[] selectAll(boolean invisibleToo)
	{
		Criteria criteria = new Criteria();
		if (!invisibleToo) criteria.addEqualTo("visible", new Boolean(true)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Block.class, criteria);
		return (Block[]) Table.select(query).toArray(new Block[0]);
	}
	
	public static void readDBRecords()
	{
		Block[] blocks = Block.selectAll(true);
		for (int i = 0; i < blocks.length; i++)
		{
			Block.put(blocks[i]);
		}
	}
	
	public static Block getById(Long id)
	{
		return (Block) Block.records.get(id);
	}
	
	public static Block getByClass(String clazz)
	{
		return (Block) Block.classIndex.get(clazz);
	}
	
	public static Block[] getAll(boolean invisibleToo)
	{
		ArrayList blocks = new ArrayList();
		Enumeration enumerationeration = Block.records.elements();
		while (enumerationeration.hasMoreElements())
		{
			Block block = (Block) enumerationeration.nextElement();
			if (invisibleToo || block.visible)
			{
				blocks.add(block);
			}
		}
		return (Block[]) blocks.toArray(new Block[0]);
	}
	
	private static void clearData()
	{
		Block.records.clear();
		Block.classIndex.clear();
	}
	
	private static void put(Block block)
	{
		Block.records.put(block.getId(), block);
		Block.classIndex.put(block.clazz, block);
	}
	
	public static Element writeXMLRecords(Element root)
	{
		Element table = Database.getTemporary().getTable("block"); //$NON-NLS-1$
		if (table == null)
		{
			table = new Element("table"); //$NON-NLS-1$
			table.setAttribute("name", "block"); //$NON-NLS-1$ //$NON-NLS-2$
			root.addContent(table);
		}
		
		Enumeration entries = Block.records.elements();
		while (entries.hasMoreElements())
		{
			Block rec = (Block) entries.nextElement();
			
			Element record = new Element("record"); //$NON-NLS-1$
			record.setAttribute("id", rec.getId().toString()); //$NON-NLS-1$
			record.setAttribute("timestamp", new Long(rec.timestamp.getTime()).toString()); //$NON-NLS-1$
			record.setAttribute("deleted", new Boolean(rec.deleted).toString()); //$NON-NLS-1$
			
			Element vi = new Element("field"); //$NON-NLS-1$
			vi.setAttribute("name", "visible"); //$NON-NLS-1$ //$NON-NLS-2$
			vi.setAttribute("value", Boolean.toString(rec.visible)); //$NON-NLS-1$
			record.addContent(vi);
			
			Element na = new Element("field"); //$NON-NLS-1$
			na.setAttribute("name", "name"); //$NON-NLS-1$ //$NON-NLS-2$
			na.setAttribute("value", rec.name); //$NON-NLS-1$
			record.addContent(na);
			
			Element cl = new Element("field"); //$NON-NLS-1$
			cl.setAttribute("name", "class"); //$NON-NLS-1$ //$NON-NLS-2$
			cl.setAttribute("value", rec.clazz); //$NON-NLS-1$
			record.addContent(cl);
			
			Element fi = new Element("field"); //$NON-NLS-1$
			fi.setAttribute("name", "font-size"); //$NON-NLS-1$ //$NON-NLS-2$
			fi.setAttribute("value", Double.toString(rec.fontSize)); //$NON-NLS-1$
			record.addContent(fi);
			
			Element fy = new Element("field"); //$NON-NLS-1$
			fy.setAttribute("name", "font-style"); //$NON-NLS-1$ //$NON-NLS-2$
			fy.setAttribute("value", Integer.toString(rec.fontStyle)); //$NON-NLS-1$
			record.addContent(fy);
			
			table.addContent(record);
		}
		return root;
	}
	
	public static void readXML()
	{
		Block.clearData();
		Element[] elements = Database.getTemporary().getRecords("block"); //$NON-NLS-1$
		for (int i = 0; i < elements.length; i++)
		{
			Block record = new Block();
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
				if (field.getAttributeValue("name").equals("visible")) { //$NON-NLS-1$ //$NON-NLS-2$
					record.visible = Boolean.getBoolean(field.getAttributeValue("value")); //$NON-NLS-1$
				}
				else if (field.getAttributeValue("name").equals("name")) { //$NON-NLS-1$ //$NON-NLS-2$
					record.name = field.getAttributeValue("value"); //$NON-NLS-1$
				}
				else if (field.getAttributeValue("name").equals("class")) { //$NON-NLS-1$ //$NON-NLS-2$
					record.clazz = field.getAttributeValue("value"); //$NON-NLS-1$
				}
				else if (field.getAttributeValue("name").equals("font-size")) { //$NON-NLS-1$ //$NON-NLS-2$
					record.fontSize = XMLLoader.getDouble(field.getAttributeValue("value")); //$NON-NLS-1$
				}
				else if (field.getAttributeValue("name").equals("font-style")) { //$NON-NLS-1$ //$NON-NLS-2$
					record.fontStyle = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
				}
			}
			Block.put(record);
		}
	}
	
	private static Hashtable records = new Hashtable();
	private static Hashtable classIndex = new Hashtable();
}
