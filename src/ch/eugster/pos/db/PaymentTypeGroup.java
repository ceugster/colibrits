/*
 * Created on 14.07.2003
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
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.util.collections.RemovalAwareCollection;
import org.jdom.Element;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PaymentTypeGroup extends Table
{
	
	public String code = ""; //$NON-NLS-1$
	public String name = ""; //$NON-NLS-1$
	public String defaultAccount = ""; //$NON-NLS-1$
	public boolean visible = true;
	
	protected RemovalAwareCollection paymentTypes = new RemovalAwareCollection();
	
	/**
	 * 
	 */
	public PaymentTypeGroup()
	{
		super();
	}
	
	public boolean isRemovable()
	{
		PaymentType[] paymentTypes = PaymentType.selectByGroup(this, true);
		for (int i = 0; i < paymentTypes.length; i++)
		{
			if (!paymentTypes[i].isRemovable())
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
			PaymentType[] types = PaymentType.selectByGroup(this, false);
			for (int i = 0; i < types.length; i++)
			{
				types[i].delete();
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
	
	public static PaymentTypeGroup selectById(Long pk)
	{
		PaymentTypeGroup paymentTypeGroup = new PaymentTypeGroup();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("id", pk); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(PaymentTypeGroup.class, criteria);
		Collection paymentTypeGroups = Table.select(query);
		Iterator i = paymentTypeGroups.iterator();
		if (i.hasNext())
		{
			paymentTypeGroup = (PaymentTypeGroup) i.next();
		}
		return paymentTypeGroup;
	}
	
	public static PaymentTypeGroup selectByCode(String code, boolean deletedToo)
	{
		PaymentTypeGroup paymentTypeGroup = new PaymentTypeGroup();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("code", code); //$NON-NLS-1$
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(PaymentTypeGroup.class, criteria);
		Collection paymentTypeGroups = Table.select(query);
		Iterator i = paymentTypeGroups.iterator();
		if (i.hasNext())
		{
			paymentTypeGroup = (PaymentTypeGroup) i.next();
		}
		return paymentTypeGroup;
	}
	
	public static PaymentTypeGroup[] selectAll()
	{
		return PaymentTypeGroup.selectAll(true, false);
	}
	
	public static PaymentTypeGroup[] selectAll(boolean deletedToo, boolean invisibleToo)
	{
		Criteria criteria = new Criteria();
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		if (!invisibleToo) criteria.addEqualTo("visible", new Boolean(true)); //$NON-NLS-1$
			//		criteria.addOrderBy("name", true); //$NON-NLS-1$
		QueryByCriteria query = QueryFactory.newQuery(PaymentTypeGroup.class, criteria);
		query.addOrderBy("name", true); //$NON-NLS-1$
		return (PaymentTypeGroup[]) Table.select(query).toArray(new PaymentTypeGroup[0]);
	}
	
	public static void readDBRecords()
	{
		PaymentTypeGroup[] ptg = PaymentTypeGroup.selectAll(false, true);
		for (int i = 0; i < ptg.length; i++)
		{
			PaymentTypeGroup.put(ptg[i]);
		}
	}
	
	public static PaymentTypeGroup getById(Long id)
	{
		return (PaymentTypeGroup) PaymentTypeGroup.records.get(id);
	}
	
	public static PaymentTypeGroup getByCode(String code)
	{
		return (PaymentTypeGroup) PaymentTypeGroup.codeIndex.get(code);
	}
	
	private static void clearData()
	{
		PaymentTypeGroup.records.clear();
		PaymentTypeGroup.codeIndex.clear();
	}
	
	private static void put(PaymentTypeGroup paymentTypeGroup)
	{
		PaymentTypeGroup.records.put(paymentTypeGroup.getId(), paymentTypeGroup);
		PaymentTypeGroup.codeIndex.put(paymentTypeGroup.code, paymentTypeGroup);
	}
	
	public static Element writeXMLRecords(Element root)
	{
		Element table = Database.getTemporary().getTable("payment-type-group"); //$NON-NLS-1$
		if (table == null)
		{
			table = new Element("table"); //$NON-NLS-1$
			table.setAttribute("name", "payment-type-group"); //$NON-NLS-1$ //$NON-NLS-2$
			root.addContent(table);
		}
		
		Enumeration entries = PaymentTypeGroup.records.elements();
		while (entries.hasMoreElements())
		{
			PaymentTypeGroup paymentTypeGroup = (PaymentTypeGroup) entries.nextElement();
			Element record = paymentTypeGroup.getJDOMRecordAttributes();
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
		
		Element vi = new Element("field"); //$NON-NLS-1$
		vi.setAttribute("name", "visible"); //$NON-NLS-1$ //$NON-NLS-2$
		vi.setAttribute("value", new Boolean(this.visible).toString()); //$NON-NLS-1$
		record.addContent(vi);
		
		Element nm = new Element("field"); //$NON-NLS-1$
		nm.setAttribute("name", "name"); //$NON-NLS-1$ //$NON-NLS-2$
		nm.setAttribute("value", this.name); //$NON-NLS-1$
		record.addContent(nm);
		
		Element da = new Element("field"); //$NON-NLS-1$
		da.setAttribute("name", "default-account"); //$NON-NLS-1$ //$NON-NLS-2$
		da.setAttribute("value", this.defaultAccount); //$NON-NLS-1$
		record.addContent(da);
		
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
			else if (field.getAttributeValue("name").equals("default-account")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.defaultAccount = field.getAttributeValue("value"); //$NON-NLS-1$
			}
		}
	}
	
	public static void readXML()
	{
		PaymentTypeGroup.clearData();
		Element[] elements = Database.getTemporary().getRecords("payment-type-group"); //$NON-NLS-1$
		for (int i = 0; i < elements.length; i++)
		{
			PaymentTypeGroup paymentTypeGroup = new PaymentTypeGroup();
			paymentTypeGroup.getData(elements[i]);
			PaymentTypeGroup.put(paymentTypeGroup);
		}
	}
	
	private static Hashtable records = new Hashtable();
	private static Hashtable codeIndex = new Hashtable();
}
