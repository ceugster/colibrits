/*
 * Created on 07.03.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package ch.eugster.pos.db;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.jdom.Element;

import ch.eugster.pos.util.NumberUtility;
import ch.eugster.pos.util.XMLLoader;

/**
 * @author administrator
 */
public class CurrentTax extends Table
{
	
	public String fibuId = ""; //$NON-NLS-1$
	public double percentage = Table.DOUBLE_DEFAULT_ZERO;
	public Timestamp validationDate = new Timestamp(new Date(0l).getTime());
	
	private Long taxId;
	private Tax tax;
	
	public CurrentTax()
	{
		this.setTax(new Tax());
	}
	
	public void setTax(Tax tax)
	{
		this.tax = tax;
		this.taxId = tax.getId();
	}
	
	public Tax getTax()
	{
		Tax tax = this.tax;
		if (tax == null)
		{
			if (this.taxId.equals(new Long(0)))
			{
				tax = new Tax();
			}
			else
			{
				tax = Tax.selectById(this.taxId);
			}
		}
		return tax;
	}
	
	public Long getTaxId()
	{
		return this.taxId;
	}
	
	public Double calculateTax(Double amount)
	{
		return new Double(this.calculateTax(amount.doubleValue()));
	}
	
	public double calculateTax(double amount)
	{
		return NumberUtility.round(amount / 100 * this.percentage, ForeignCurrency.getDefaultCurrency().getCurrency()
						.getDefaultFractionDigits());
	}
	
	public void setValidationDate(Date date)
	{
		this.validationDate = new Timestamp(date.getTime());
	}
	
	public Date getValidationDate()
	{
		return new Date(this.validationDate.getTime());
	}
	
	public boolean isRemovable()
	{
		return !Position.exist("currentTaxId", this.getId()); //$NON-NLS-1$
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
	
	public static CurrentTax selectById(Long pk)
	{
		CurrentTax currentTax = new CurrentTax();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("id", pk); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(CurrentTax.class, criteria);
		Collection currentTaxes = Table.select(query);
		Iterator i = currentTaxes.iterator();
		if (i.hasNext())
		{
			currentTax = (CurrentTax) i.next();
		}
		return currentTax;
	}
	
	public static CurrentTax[] selectNextAfter(Tax tax, boolean deletedToo)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("taxId", tax.getId()); //$NON-NLS-1$
		criteria.addLessOrEqualThan("validationDate", new Timestamp(new java.util.Date().getTime())); //$NON-NLS-1$
		criteria.addGreaterOrEqualThan("validationDate", tax.getCurrentTax().validationDate); //$NON-NLS-1$
		//		criteria.addOrderBy("validationDate", false); //$NON-NLS-1$
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		QueryByCriteria query = QueryFactory.newQuery(CurrentTax.class, criteria);
		query.addOrderByDescending("validationDate"); //$NON-NLS-1$
		Collection currentTaxes = null;
		try
		{
			currentTaxes = Table.select(query);
		}
		catch (RuntimeException e)
		{
			e.printStackTrace();
		}
		return (CurrentTax[]) currentTaxes.toArray(new CurrentTax[0]);
	}
	
	public static CurrentTax selectFromDate(Tax tax, Date date)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("taxId", tax.getId()); //$NON-NLS-1$
		criteria.addLessOrEqualThan("validationDate", new Timestamp(date.getTime())); //$NON-NLS-1$
		//		criteria.addOrderBy("validationDate", false); //$NON-NLS-1$
		QueryByCriteria query = QueryFactory.newQuery(CurrentTax.class, criteria);
		query.addOrderBy("validationDate", false); //$NON-NLS-1$
		Collection currentTaxes = null;
		currentTaxes = Table.select(query);
		Iterator i = currentTaxes.iterator();
		if (i.hasNext())
		{
			return (CurrentTax) i.next();
		}
		else
		{
			return null;
		}
	}
	
	public static CurrentTax[] selectAll(boolean deletedToo)
	{
		Criteria criteria = new Criteria();
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(CurrentTax.class, criteria);
		Collection currentTaxes = Table.select(query);
		return (CurrentTax[]) currentTaxes.toArray(new CurrentTax[0]);
	}
	
	public static CurrentTax[] selectByTax(Tax tax, boolean deletedToo)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("taxId", tax.getId()); //$NON-NLS-1$
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(CurrentTax.class, criteria);
		Collection currentTaxes = Table.select(query);
		return (CurrentTax[]) currentTaxes.toArray(new CurrentTax[0]);
	}
	
	public static void readDBRecords()
	{
		CurrentTax[] currentTax = CurrentTax.selectAll(false);
		for (int i = 0; i < currentTax.length; i++)
		{
			CurrentTax.put(currentTax[i]);
		}
	}
	
	public static CurrentTax getById(Long id)
	{
		CurrentTax ct = null;
		if (id != null)
		{
			ct = (CurrentTax) CurrentTax.records.get(id);
		}
		if (ct == null)
		{
			ct = new CurrentTax();
		}
		return ct;
	}
	
	public static CurrentTax[] getNextAfter(Tax tax, boolean deletedToo)
	{
		Enumeration curt = CurrentTax.records.elements();
		ArrayList ct = new ArrayList();
		
		java.util.Date d = new java.util.Date();
		Calendar gc = new GregorianCalendar();
		gc.setTimeInMillis(d.getTime());
		gc.add(Calendar.DAY_OF_MONTH, 1);
		java.util.Date now = gc.getTime();
		
		d = tax.getCurrentTax().getValidationDate();
		gc = GregorianCalendar.getInstance();
		gc.setTime(d);
		gc.set(Calendar.HOUR_OF_DAY, 0);
		gc.set(Calendar.MINUTE, 0);
		gc.set(Calendar.SECOND, 0);
		gc.set(Calendar.MILLISECOND, 0);
		gc.add(Calendar.DAY_OF_MONTH, -1);
		java.util.Date cur = gc.getTime();
		
		while (curt.hasMoreElements())
		{
			CurrentTax currentTax = (CurrentTax) curt.nextElement();
			if (currentTax.getTaxId().equals(tax.getId()))
			{
				if (currentTax.getValidationDate().before(now) && currentTax.getValidationDate().after(cur))
				{
					ct.add(currentTax);
				}
			}
		}
		return (CurrentTax[]) ct.toArray(new CurrentTax[0]);
	}
	
	public static CurrentTax[] getByTax(Tax tax, boolean deletedTo)
	{
		Enumeration curt = CurrentTax.records.elements();
		ArrayList ct = new ArrayList();
		while (curt.hasMoreElements())
		{
			CurrentTax currentTax = (CurrentTax) curt.nextElement();
			if (currentTax.getTax().equals(tax))
			{
				ct.add(currentTax);
			}
		}
		return (CurrentTax[]) ct.toArray(new CurrentTax[0]);
	}
	
	public static CurrentTax[] getAll(boolean deletedToo)
	{
		Enumeration currentTaxes = CurrentTax.records.elements();
		ArrayList ct = new ArrayList();
		while (currentTaxes.hasMoreElements())
		{
			CurrentTax currentTax = (CurrentTax) currentTaxes.nextElement();
			ct.add(currentTax);
		}
		return (CurrentTax[]) ct.toArray(new CurrentTax[0]);
	}
	
	private static void clearData()
	{
		CurrentTax.records.clear();
	}
	
	private static void put(CurrentTax currentTax)
	{
		CurrentTax.records.put(currentTax.getId(), currentTax);
	}
	
	public static Element writeXMLRecords(Element root)
	{
		Element table = Database.getTemporary().getTable("current-tax"); //$NON-NLS-1$
		if (table == null)
		{
			table = new Element("table"); //$NON-NLS-1$
			table.setAttribute("name", "current-tax"); //$NON-NLS-1$ //$NON-NLS-2$
			root.addContent(table);
		}
		
		Enumeration elements = CurrentTax.records.elements();
		while (elements.hasMoreElements())
		{
			CurrentTax rec = (CurrentTax) elements.nextElement();
			
			Element record = new Element("record"); //$NON-NLS-1$
			record.setAttribute("id", rec.getId().toString()); //$NON-NLS-1$
			record.setAttribute("timestamp", new Long(rec.timestamp.getTime()).toString()); //$NON-NLS-1$
			record.setAttribute("deleted", new Boolean(rec.deleted).toString()); //$NON-NLS-1$
			
			Element nm = new Element("field"); //$NON-NLS-1$
			nm.setAttribute("name", "tax"); //$NON-NLS-1$ //$NON-NLS-2$
			nm.setAttribute("value", rec.getTaxId().toString()); //$NON-NLS-1$
			record.addContent(nm);
			
			Element fi = new Element("field"); //$NON-NLS-1$
			fi.setAttribute("name", "fibu-id"); //$NON-NLS-1$ //$NON-NLS-2$
			fi.setAttribute("value", rec.fibuId); //$NON-NLS-1$
			record.addContent(fi);
			
			Element pe = new Element("field"); //$NON-NLS-1$
			pe.setAttribute("name", "percentage"); //$NON-NLS-1$ //$NON-NLS-2$
			pe.setAttribute("value", Double.toString(rec.percentage)); //$NON-NLS-1$
			record.addContent(pe);
			
			Element vd = new Element("field"); //$NON-NLS-1$
			vd.setAttribute("name", "validation-date"); //$NON-NLS-1$ //$NON-NLS-2$
			vd.setAttribute("value", rec.getValidationDate().toString()); //$NON-NLS-1$
			record.addContent(vd);
			
			table.addContent(record);
		}
		return root;
	}
	
	public static void readXML()
	{
		CurrentTax.clearData();
		Element[] elements = Database.getTemporary().getRecords("current-tax"); //$NON-NLS-1$
		for (int i = 0; i < elements.length; i++)
		{
			CurrentTax record = new CurrentTax();
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
				if (field.getAttributeValue("name").equals("tax")) { //$NON-NLS-1$ //$NON-NLS-2$
					Tax tax = Tax.getById(Long.valueOf(field.getAttributeValue("value"))); //$NON-NLS-1$
					record.setTax(tax);
				}
				else if (field.getAttributeValue("name").equals("fibu-id")) { //$NON-NLS-1$ //$NON-NLS-2$
					record.fibuId = field.getAttributeValue("value"); //$NON-NLS-1$
				}
				else if (field.getAttributeValue("name").equals("percentage")) { //$NON-NLS-1$ //$NON-NLS-2$
					record.percentage = XMLLoader.getDouble(field.getAttributeValue("value")); //$NON-NLS-1$
				}
				else if (field.getAttributeValue("name").equals("validation-date")) { //$NON-NLS-1$ //$NON-NLS-2$
					record.setValidationDate(XMLLoader.getDate(field.getAttributeValue("value"))); //$NON-NLS-1$
				}
			}
			CurrentTax.put(record);
		}
	}
	
	protected static Hashtable records = new Hashtable();
	
}
