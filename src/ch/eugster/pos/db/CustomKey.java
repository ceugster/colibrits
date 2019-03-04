/*
 * Created on 20.05.2003
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
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.jdom.Element;

import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.events.Action;
import ch.eugster.pos.util.XMLLoader;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CustomKey extends Key
{
	
	public Double value = new Double(Table.DOUBLE_DEFAULT_ZERO);
	public String parentClassName = ""; //$NON-NLS-1$
	public Long parentId = null;
	public boolean setDefaultTab = false;
	
	private Long tabId = null;
	private Tab tab;
	// 10226
	private Long paymentTypeId;
	private PaymentType paymentType;
	
	// 10226
	/**
	 * 
	 */
	public CustomKey()
	{
		super();
	}
	
	public void setTab(Tab tab)
	{
		this.tab = tab;
		this.tabId = tab.getId();
	}
	
	public Long getTabId()
	{
		return this.tabId;
	}
	
	public Tab getTab()
	{
		return this.tab;
	}
	
	public void copyId(CustomKey key)
	{
		this.setId(key.getId());
	}
	
	protected Action getPosAction(UserPanel context)
	{
		Action action = null;
		Table table = null;
		
		if (this.parentClassName == null || this.parentClassName.equals("")) //$NON-NLS-1$
		{
			action = this.createAction(context);
			action.putValue(Action.POS_KEY_CLASS_NAME, ""); //$NON-NLS-1$
		}
		else
		{
			table = this.getParentObject();
			if (table == null)
			{
				return null;
			}
			
			action = this.createAction(context);
			if (action != null)
			{
				action.putParent(Action.POS_KEY_CLASS_NAME, this.parentClassName, table);
			}
		}
		return action;
	}
	
	public void setPaymentType(PaymentType paymentType)
	{
		this.paymentType = paymentType;
		if (paymentType != null) this.paymentTypeId = this.paymentType.getId();
	}
	
	public PaymentType getPaymentType()
	{
		if (this.paymentType == null)
		{
			if (this.paymentTypeId != null) this.paymentType = PaymentType.getById(this.paymentTypeId);
			
			if (this.paymentType == null)
			{
				this.setPaymentType(PaymentType.getPaymentTypeCash());
			}
		}
		
		return this.paymentType;
	}
	
	private Table getParentObject()
	{
		Table table = null;
		
		try
		{
			Class a = Class.forName(this.parentClassName);
			table = Table.getById(Database.getCurrent(), a, this.parentId);
		}
		catch (ClassNotFoundException e)
		{
		}
		return table;
	}
	
	public boolean isRemovable()
	{
		return true;
	}
	
	public static void delete(String parentClass, Long id)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("parentClassName", parentClass); //$NON-NLS-1$
		criteria.addEqualTo("parentId", id); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(CustomKey.class, criteria);
		Database.getCurrent().getBroker().deleteByQuery(query);
	}
	
	public static void delete(Tab tab)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("tabId", tab.getId()); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(CustomKey.class, criteria);
		Database.getCurrent().getBroker().deleteByQuery(query);
	}
	
	public static CustomKey selectById(Long pk)
	{
		CustomKey key = new CustomKey();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("id", pk); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(CustomKey.class, criteria);
		Collection keys = Table.select(query);
		Iterator i = keys.iterator();
		if (i.hasNext())
		{
			key = (CustomKey) i.next();
		}
		return key;
	}
	
	public static CustomKey[] selectByTab(Long tabId)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("tabId", tabId); //$NON-NLS-1$
		//		criteria.addOrderBy("row", true); //$NON-NLS-1$
		//		criteria.addOrderBy("column", true); //$NON-NLS-1$
		QueryByCriteria query = QueryFactory.newQuery(CustomKey.class, criteria);
		query.addOrderBy("row", true); //$NON-NLS-1$
		query.addOrderBy("column", true); //$NON-NLS-1$
		Collection keys = Table.select(query);
		return (CustomKey[]) keys.toArray(new CustomKey[0]);
	}
	
	public static CustomKey[] selectByTabAndRow(Long tabId, Integer row)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("tabId", tabId); //$NON-NLS-1$
		criteria.addEqualTo("row", row); //$NON-NLS-1$
		//		criteria.addOrderBy("row", true); //$NON-NLS-1$
		//		criteria.addOrderBy("column", true); //$NON-NLS-1$
		QueryByCriteria query = QueryFactory.newQuery(CustomKey.class, criteria);
		query.addOrderBy("row", true); //$NON-NLS-1$
		query.addOrderBy("column", true); //$NON-NLS-1$
		Collection keys = Table.select(query);
		return (CustomKey[]) keys.toArray(new CustomKey[0]);
	}
	
	public static CustomKey[] selectAll()
	{
		Criteria criteria = new Criteria();
		Query query = QueryFactory.newQuery(CustomKey.class, criteria);
		Collection keys = Table.select(query);
		return (CustomKey[]) keys.toArray(new CustomKey[0]);
	}
	
	public static void removeByTab(Tab tab)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("tabId", tab.getId()); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(CustomKey.class, criteria);
		Database.getCurrent().getBroker().deleteByQuery(query);
	}
	
	public static void readDBRecords()
	{
		CustomKey[] customKeys = CustomKey.selectAll();
		for (int i = 0; i < customKeys.length; i++)
		{
			CustomKey.put(customKeys[i]);
		}
	}
	
	public static CustomKey getById(Long id)
	{
		return (CustomKey) CustomKey.records.get(id);
	}
	
	public static CustomKey[] getByTab(Long tabId)
	{
		ArrayList keys = new ArrayList();
		Enumeration enumerationeration = CustomKey.records.elements();
		while (enumerationeration.hasMoreElements())
		{
			CustomKey key = (CustomKey) enumerationeration.nextElement();
			if (key.getTabId().equals(tabId))
			{
				keys.add(key);
			}
		}
		return (CustomKey[]) keys.toArray(new CustomKey[0]);
	}
	
	public static CustomKey[] getByTabAndRow(Long tabId, int row)
	{
		ArrayList keys = new ArrayList();
		Enumeration enumerationeration = CustomKey.records.elements();
		while (enumerationeration.hasMoreElements())
		{
			CustomKey key = (CustomKey) enumerationeration.nextElement();
			if (key.getTabId().equals(tabId) && key.row == row)
			{
				keys.add(key);
			}
		}
		return (CustomKey[]) keys.toArray(new CustomKey[0]);
	}
	
	public static CustomKey[] getAll()
	{
		ArrayList keys = new ArrayList();
		Enumeration enumerationeration = CustomKey.records.elements();
		while (enumerationeration.hasMoreElements())
		{
			keys.add(enumerationeration.nextElement());
		}
		return (CustomKey[]) keys.toArray(new CustomKey[0]);
	}
	
	private static void clearData()
	{
		CustomKey.records.clear();
	}
	
	private static void put(CustomKey customKey)
	{
		CustomKey.records.put(customKey.getId(), customKey);
	}
	
	public static Element writeXMLRecords(Element root)
	{
		Element table = Database.getTemporary().getTable("custom-key"); //$NON-NLS-1$
		if (table == null)
		{
			table = new Element("table"); //$NON-NLS-1$
			table.setAttribute("name", "custom-key"); //$NON-NLS-1$ //$NON-NLS-2$
			root.addContent(table);
		}
		
		Enumeration entries = CustomKey.records.elements();
		while (entries.hasMoreElements())
		{
			CustomKey rec = (CustomKey) entries.nextElement();
			Element record = rec.getJDOMRecordAttributes();
			table.addContent(record);
		}
		return root;
	}
	
	protected Element getJDOMRecordAttributes()
	{
		Element record = super.getJDOMRecordAttributes();
		
		Element va = new Element("field"); //$NON-NLS-1$
		va.setAttribute("name", "value"); //$NON-NLS-1$ //$NON-NLS-2$
		va.setAttribute("value", this.value.toString()); //$NON-NLS-1$
		record.addContent(va);
		
		Element pc = new Element("field"); //$NON-NLS-1$
		pc.setAttribute("name", "parent-class-name"); //$NON-NLS-1$ //$NON-NLS-2$
		pc.setAttribute("value", this.parentClassName); //$NON-NLS-1$
		record.addContent(pc);
		
		Element pi = new Element("field"); //$NON-NLS-1$
		pi.setAttribute("name", "parent-id"); //$NON-NLS-1$ //$NON-NLS-2$
		pi.setAttribute("value", this.parentId.toString()); //$NON-NLS-1$
		record.addContent(pi);
		
		Element ti = new Element("field"); //$NON-NLS-1$
		ti.setAttribute("name", "tab-id"); //$NON-NLS-1$ //$NON-NLS-2$
		ti.setAttribute("value", this.tabId.toString()); //$NON-NLS-1$
		record.addContent(ti);
		
		Element dt = new Element("field"); //$NON-NLS-1$
		dt.setAttribute("name", "set-default-type"); //$NON-NLS-1$ //$NON-NLS-2$
		dt.setAttribute("value", new Boolean(this.setDefaultTab).toString()); //$NON-NLS-1$
		record.addContent(dt);
		
		// 10226
		Element pt = new Element("field"); //$NON-NLS-1$
		pt.setAttribute("name", "payment-type-id"); //$NON-NLS-1$ //$NON-NLS-2$
		pt.setAttribute("value", this.getPaymentType().getId().toString()); //$NON-NLS-1$
		record.addContent(pt);
		// 10226
		
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
			if (field.getAttributeValue("name").equals("value")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.value = new Double(XMLLoader.getDouble(field.getAttributeValue("value"))); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("parent-class-name")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.parentClassName = field.getAttributeValue("value"); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("parent-id")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.parentId = new Long(XMLLoader.getLong(field.getAttributeValue("value"))); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("tab-id")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.setTab(Tab.getById(new Long(XMLLoader.getLong(field.getAttributeValue("value"))))); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("set-default-tab")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.setDefaultTab = XMLLoader.getBoolean(field.getAttributeValue("value")); //$NON-NLS-1$
			}
			// 10226
			else if (field.getAttributeValue("name").equals("payment-type-id")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.paymentTypeId = new Long(XMLLoader.getLong(field.getAttributeValue("value"))); //$NON-NLS-1$
			}
		}
	}
	
	public static void readXML()
	{
		CustomKey.clearData();
		Element[] elements = Database.getTemporary().getRecords("custom-key"); //$NON-NLS-1$
		for (int i = 0; i < elements.length; i++)
		{
			CustomKey key = new CustomKey();
			key.setData(elements[i]);
			CustomKey.put(key);
		}
	}
	
	private static Hashtable records = new Hashtable();
}
