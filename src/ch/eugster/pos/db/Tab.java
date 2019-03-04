/*
 * Created on 20.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
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
public class Tab extends Table implements Comparable
{
	
	public boolean defaultTabPosition = false;
	public boolean defaultTabPayment = false;
	public int order = Table.INTEGER_DEFAULT_ZERO;
	public int rows = Table.INTEGER_DEFAULT_ZERO;
	public int columns = Table.INTEGER_DEFAULT_ZERO;
	public boolean visible = false;
	public double fontSize = 0f;
	public int fontStyle = Table.INTEGER_DEFAULT_ZERO;
	public String title = ""; //$NON-NLS-1$
	
	private Long blockId = null;
	private Block block;
	private RemovalAwareCollection keys = new RemovalAwareCollection();
	
	/**
	 * 
	 */
	public Tab()
	{
	}
	
	public void setBlock(Block block)
	{
		this.block = block;
		this.blockId = block.getId();
	}
	
	public Long getBlockId()
	{
		return this.blockId;
	}
	
	public Block getBlock()
	{
		return this.block;
	}
	
	public boolean isRemovable()
	{
		return true;
	}
	
	public void addKey(CustomKey key)
	{
		this.keys.add(key);
	}
	
	public void removeKey(CustomKey key)
	{
		this.keys.remove(key);
	}
	
	public void setKeys(CustomKey[] k)
	{
		this.keys.clear();
		for (int i = 0; i < k.length; i++)
		{
			this.keys.add(k[i]);
		}
	}
	
	public CustomKey[] getKeys()
	{
		return (CustomKey[]) this.keys.toArray(new CustomKey[0]);
	}
	
	public int compareTo(Object other)
	{
		int result = 0;
		if (other instanceof Tab)
		{
			result = new Integer(this.order).compareTo(new Integer(((Tab) other).order));
		}
		return result;
	}
	
	public static Tab selectById(Long id)
	{
		Tab tab = new Tab();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("id", id); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Tab.class, criteria);
		Collection tabs = Table.select(query);
		Iterator i = tabs.iterator();
		if (i.hasNext())
		{
			return (Tab) i.next();
		}
		return tab;
	}
	
	public static Tab[] selectByBlock(Block block)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("blockId", block.getId()); //$NON-NLS-1$
		//		criteria.addOrderBy("order", true); //$NON-NLS-1$
		QueryByCriteria query = QueryFactory.newQuery(Tab.class, criteria);
		query.addOrderBy("order", true); //$NON-NLS-1$
		Collection tabs = Table.select(query);
		return (Tab[]) tabs.toArray(new Tab[0]);
	}
	
	private static Tab[] selectDefaultPositionTabs(Block block)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("blockId", block.getId()); //$NON-NLS-1$
		criteria.addEqualTo("defaultTabPosition", Boolean.TRUE); //$NON-NLS-1$
		QueryByCriteria query = QueryFactory.newQuery(Tab.class, criteria);
		Collection tabs = Table.select(query);
		return (Tab[]) tabs.toArray(new Tab[0]);
	}
	
	private static Tab[] selectDefaultPaymentTabs(Block block)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("blockId", block.getId()); //$NON-NLS-1$
		criteria.addEqualTo("defaultTabPayment", Boolean.TRUE); //$NON-NLS-1$
		QueryByCriteria query = QueryFactory.newQuery(Tab.class, criteria);
		Collection tabs = Table.select(query);
		return (Tab[]) tabs.toArray(new Tab[0]);
	}
	
	public static void clearDefaultPositionTabs(Tab tab)
	{
		Tab[] tabs = Tab.selectDefaultPositionTabs(tab.getBlock());
		for (int i = 0; i < tabs.length; i++)
		{
			if (!tabs[i].getId().equals(tab.getId()))
			{
				tabs[i].defaultTabPosition = false;
				tabs[i].store();
			}
		}
	}
	
	public static void clearDefaultPaymentTabs(Tab tab)
	{
		Tab[] tabs = Tab.selectDefaultPaymentTabs(tab.getBlock());
		for (int i = 0; i < tabs.length; i++)
		{
			if (!tabs[i].getId().equals(tab.getId()))
			{
				tabs[i].defaultTabPayment = false;
				tabs[i].store();
			}
		}
	}
	
	public static Tab[] selectAll()
	{
		Criteria criteria = new Criteria();
		Query query = QueryFactory.newQuery(Tab.class, criteria);
		Collection tabs = Table.select(query);
		return (Tab[]) tabs.toArray(new Tab[0]);
	}
	
	public static void readDBRecords()
	{
		Tab[] tabs = Tab.selectAll();
		for (int i = 0; i < tabs.length; i++)
		{
			Tab.put(tabs[i]);
		}
	}
	
	public static Tab getById(Long id)
	{
		return (Tab) Tab.records.get(id);
	}
	
	public static Tab[] getByBlock(Block block)
	{
		ArrayList tabs = new ArrayList();
		Enumeration enumeration = Tab.records.elements();
		while (enumeration.hasMoreElements())
		{
			Tab tab = (Tab) enumeration.nextElement();
			if (tab.getBlockId().equals(block.getId()))
			{
				tabs.add(tab);
			}
		}
		
		Tab[] tabArray = (Tab[]) tabs.toArray(new Tab[0]);
		Arrays.sort(tabArray);
		return tabArray;
	}
	
	public static Tab[] getAll()
	{
		ArrayList tabs = new ArrayList();
		Enumeration enumeration = Tab.records.elements();
		while (enumeration.hasMoreElements())
		{
			tabs.add(enumeration.nextElement());
		}
		return (Tab[]) tabs.toArray(new Tab[0]);
	}
	
	private static void clearData()
	{
		Tab.records.clear();
	}
	
	private static void put(Tab tab)
	{
		Tab.records.put(tab.getId(), tab);
	}
	
	public static Element writeXMLRecords(Element root)
	{
		Element table = Database.getTemporary().getTable("tab"); //$NON-NLS-1$
		if (table == null)
		{
			table = new Element("table"); //$NON-NLS-1$
			table.setAttribute("name", "tab"); //$NON-NLS-1$ //$NON-NLS-2$
			root.addContent(table);
		}
		
		Enumeration entries = Tab.records.elements();
		while (entries.hasMoreElements())
		{
			Tab tab = (Tab) entries.nextElement();
			Element record = tab.getJDOMRecordAttributes();
			table.addContent(record);
		}
		return root;
	}
	
	protected Element getJDOMRecordAttributes()
	{
		Element record = super.getJDOMRecordAttributes();
		
		Element dto = new Element("field"); //$NON-NLS-1$
		dto.setAttribute("name", "default-tab-position"); //$NON-NLS-1$ //$NON-NLS-2$
		dto.setAttribute("value", new Boolean(this.defaultTabPosition).toString()); //$NON-NLS-1$
		record.addContent(dto);
		
		Element dta = new Element("field"); //$NON-NLS-1$
		dta.setAttribute("name", "default-tab-payment"); //$NON-NLS-1$ //$NON-NLS-2$
		dta.setAttribute("value", new Boolean(this.defaultTabPayment).toString()); //$NON-NLS-1$
		record.addContent(dta);
		
		Element or = new Element("field"); //$NON-NLS-1$
		or.setAttribute("name", "order"); //$NON-NLS-1$ //$NON-NLS-2$
		or.setAttribute("value", Integer.toString(this.order)); //$NON-NLS-1$
		record.addContent(or);
		
		Element ro = new Element("field"); //$NON-NLS-1$
		ro.setAttribute("name", "rows"); //$NON-NLS-1$ //$NON-NLS-2$
		ro.setAttribute("value", Integer.toString(this.rows)); //$NON-NLS-1$
		record.addContent(ro);
		
		Element cl = new Element("field"); //$NON-NLS-1$
		cl.setAttribute("name", "columns"); //$NON-NLS-1$ //$NON-NLS-2$
		cl.setAttribute("value", Integer.toString(this.columns)); //$NON-NLS-1$
		record.addContent(cl);
		
		Element vi = new Element("field"); //$NON-NLS-1$
		vi.setAttribute("name", "visible"); //$NON-NLS-1$ //$NON-NLS-2$
		vi.setAttribute("value", new Boolean(this.visible).toString()); //$NON-NLS-1$
		record.addContent(vi);
		
		Element fi = new Element("field"); //$NON-NLS-1$
		fi.setAttribute("name", "font-size"); //$NON-NLS-1$ //$NON-NLS-2$
		fi.setAttribute("value", Double.toString(this.fontSize)); //$NON-NLS-1$
		record.addContent(fi);
		
		Element fy = new Element("field"); //$NON-NLS-1$
		fy.setAttribute("name", "font-style"); //$NON-NLS-1$ //$NON-NLS-2$
		fy.setAttribute("value", Integer.toString(this.fontStyle)); //$NON-NLS-1$
		record.addContent(fy);
		
		Element ti = new Element("field"); //$NON-NLS-1$
		ti.setAttribute("name", "title"); //$NON-NLS-1$ //$NON-NLS-2$
		ti.setAttribute("value", this.title); //$NON-NLS-1$
		record.addContent(ti);
		
		Element bl = new Element("field"); //$NON-NLS-1$
		bl.setAttribute("name", "block"); //$NON-NLS-1$ //$NON-NLS-2$
		bl.setAttribute("value", this.getBlockId().toString()); //$NON-NLS-1$
		record.addContent(bl);
		
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
			if (field.getAttributeValue("name").equals("default-tab-position")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.defaultTabPosition = new Boolean(field.getAttributeValue("value")).booleanValue(); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("default-tab-payment")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.defaultTabPayment = new Boolean(field.getAttributeValue("value")).booleanValue(); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("order")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.order = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("rows")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.rows = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("columns")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.columns = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("visible")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.visible = new Boolean(field.getAttributeValue("value")).booleanValue(); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("font-size")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.fontSize = XMLLoader.getDouble(field.getAttributeValue("value")); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("font-style")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.fontStyle = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("title")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.title = field.getAttributeValue("value"); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("block")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.setBlock(Block.getById(new Long(XMLLoader.getLong(field.getAttributeValue("value"))))); //$NON-NLS-1$
			}
		}
	}
	
	public static void readXML()
	{
		Tab.clearData();
		Element[] elements = Database.getTemporary().getRecords("tab"); //$NON-NLS-1$
		for (int i = 0; i < elements.length; i++)
		{
			Tab tab = new Tab();
			tab.setData(elements[i]);
			Tab.put(tab);
		}
	}
	
	private static Hashtable records = new Hashtable();
}
