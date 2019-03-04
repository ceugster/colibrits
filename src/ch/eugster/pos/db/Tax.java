package ch.eugster.pos.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.util.collections.RemovalAwareCollection;
import org.jdom.Element;

import ch.eugster.pos.util.XMLLoader;

public class Tax extends Table
{
	
	public String code = ""; //$NON-NLS-1$
	public String galileoId = ""; //$NON-NLS-1$
	public String code128Id = ""; //$NON-NLS-1$
	public String account = ""; //$NON-NLS-1$
	
	private Long taxTypeId;
	private TaxType taxType;
	private Long taxRateId;
	private TaxRate taxRate;
	private Long currentTaxId = null;
	
	private RemovalAwareCollection currentTaxes = new RemovalAwareCollection();
	private RemovalAwareCollection productGroups = new RemovalAwareCollection();
	
	public Tax()
	{
		this(new TaxType(), new TaxRate());
	}
	
	public Tax(TaxType type, TaxRate rate)
	{
		this.setTaxType(type);
		this.setTaxRate(rate);
	}
	
	public void setCurrentTaxId(Long id)
	{
		this.currentTaxId = id;
	}
	
	public CurrentTax getCurrentTax()
	{
		if (this.currentTax == null)
		{
			Iterator iter = this.currentTaxes.iterator();
			while (iter.hasNext())
			{
				CurrentTax ct = (CurrentTax) iter.next();
				if (ct.getId().equals(this.currentTaxId))
				{
					this.currentTax = ct;
				}
			}
		}
		if (this.currentTax == null || this.currentTax.getId() == null
						|| this.currentTax.getId().equals(Table.ZERO_VALUE))
		{
			this.currentTax = CurrentTax.getById(this.currentTaxId);
		}
		return this.currentTax;
	}
	
	public CurrentTax getCurrentTaxByTimeValue(long time)
	{
		Iterator iter = this.currentTaxes.iterator();
		while (iter.hasNext())
		{
			CurrentTax ct = (CurrentTax) iter.next();
			Calendar calendar1 = GregorianCalendar.getInstance();
			calendar1.setTime(ct.getValidationDate());
			calendar1.set(Calendar.HOUR_OF_DAY, 0);
			calendar1.set(Calendar.MINUTE, 0);
			calendar1.set(Calendar.SECOND, 0);
			calendar1.set(Calendar.MILLISECOND, 0);
			
			Calendar calendar2 = GregorianCalendar.getInstance();
			calendar2.setTimeInMillis(time);
			if (calendar2.get(Calendar.DAY_OF_MONTH) == calendar1.get(Calendar.DAY_OF_MONTH)
							&& calendar2.get(Calendar.MONTH) == calendar1.get(Calendar.MONTH)
							&& calendar2.get(Calendar.YEAR) == calendar1.get(Calendar.YEAR))
			{
				this.currentTax = ct;
			}
		}
		if (this.currentTax == null || this.currentTax.getId() == null
						|| this.currentTax.getId().equals(Table.ZERO_VALUE))
		{
			this.currentTax = CurrentTax.getById(this.currentTaxId);
		}
		return this.currentTax;
	}
	
	public Long getCurrentTaxId()
	{
		return this.currentTaxId;
	}
	
	public void setTaxType(TaxType type)
	{
		this.taxType = type;
		this.taxTypeId = this.taxType.getId();
	}
	
	public Long getTaxTypeId()
	{
		return this.taxTypeId;
	}
	
	public TaxType getTaxType()
	{
		if (this.taxType == null)
		{
			if (this.taxTypeId.equals(new Long(0l)))
			{
				this.taxType = new TaxType();
			}
			else
			{
				this.taxType = TaxType.getById(this.taxTypeId);
			}
		}
		return this.taxType;
	}
	
	public void setTaxRate(TaxRate rate)
	{
		this.taxRate = rate;
		this.taxRateId = this.taxRate.getId();
	}
	
	public Long getTaxRateId()
	{
		return this.taxRateId;
	}
	
	public TaxRate getTaxRate()
	{
		if (this.taxRate == null)
		{
			if (this.taxRateId.equals(new Long(0l)))
			{
				this.taxRate = new TaxRate();
			}
			else
			{
				this.taxRate = TaxRate.getById(this.taxRateId);
			}
		}
		return this.taxRate;
	}
	
	public boolean isRemovable()
	{
		boolean result = true;
		CurrentTax[] currentTaxes = CurrentTax.selectByTax(this, false);
		for (int i = 0; i < currentTaxes.length; i++)
		{
			if (!currentTaxes[i].isRemovable())
			{
				return false;
			}
		}
		ProductGroup[] productGroups = ProductGroup.selectByTax(this, false);
		for (int i = 0; i < productGroups.length; i++)
		{
			if (!productGroups[i].isRemovable())
			{
				return false;
			}
		}
		return result;
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
			CurrentTax[] currentTaxes = CurrentTax.selectByTax(this, false);
			for (int i = 0; i < currentTaxes.length; i++)
			{
				if (result.getErrorCode() == 0)
				{
					result = currentTaxes[i].delete();
				}
			}
			if (result.getErrorCode() == 0)
			{
				ProductGroup[] productGroups = ProductGroup.selectByTax(this, false);
				for (int i = 0; i < productGroups.length; i++)
				{
					if (result.getErrorCode() == 0)
					{
						result = productGroups[i].delete();
					}
				}
				this.deleted = true;
				result = super.store();
			}
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
	
	public static void setCurrentTaxes()
	{
		Tax[] taxes = Tax.selectAll(false);
		for (int i = 0; i < taxes.length; i++)
		{
			CurrentTax[] currentTaxes = CurrentTax.selectNextAfter(taxes[i], false);
			if (currentTaxes.length > 0)
			{
				if (!currentTaxes[0].getId().equals(taxes[i].getCurrentTaxId()))
				{
					taxes[i].setCurrentTaxId(currentTaxes[0].getId());
					if (!Database.getCurrent().equals(Database.getTemporary()))
					{
						taxes[i].store();
					}
				}
			}
		}
	}
	
	public static Tax selectById(Long pk)
	{
		Tax tax = new Tax();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("id", pk); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Tax.class, criteria);
		Collection objects = Table.select(query);
		Iterator i = objects.iterator();
		if (i.hasNext())
		{
			tax = (Tax) i.next();
		}
		return tax;
	}
	
	public static Tax selectByGalileoId(String galileoId, boolean deletedToo)
	{
		Tax tax = new Tax();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("galileoId", galileoId); //$NON-NLS-1$
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Tax.class, criteria);
		Collection objects = Table.select(query);
		Iterator i = objects.iterator();
		if (i.hasNext())
		{
			tax = (Tax) i.next();
		}
		return tax;
	}
	
	public static Tax selectByCode128Id(String code128Id, boolean deletedToo)
	{
		Tax tax = new Tax();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("code128Id", code128Id); //$NON-NLS-1$
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Tax.class, criteria);
		Collection objects = Table.select(query);
		Iterator i = objects.iterator();
		if (i.hasNext())
		{
			tax = (Tax) i.next();
		}
		return tax;
	}
	
	public static Tax selectByCode(String code, boolean deletedToo)
	{
		Tax tax = new Tax();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("code", code); //$NON-NLS-1$
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Tax.class, criteria);
		Collection objects = Table.select(query);
		Iterator i = objects.iterator();
		if (i.hasNext())
		{
			tax = (Tax) i.next();
		}
		return tax;
	}
	
	public static Tax[] selectByTypeId(Long taxTypeId, boolean deletedToo)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("taxTypeId", taxTypeId); //$NON-NLS-1$
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Tax.class, criteria);
		Collection objects = Table.select(query);
		return (Tax[]) objects.toArray(new Tax[0]);
	}
	
	public static Tax[] selectByRateId(Long taxTypeId, boolean deletedToo)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("taxRateId", taxTypeId); //$NON-NLS-1$
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Tax.class, criteria);
		Collection objects = Table.select(query);
		return (Tax[]) objects.toArray(new Tax[0]);
	}
	
	public static Tax selectByTypeIdAndRateId(Long taxTypeId, Long taxRateId, boolean deletedToo)
	{
		Tax tax = new Tax();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("taxTypeId", taxTypeId); //$NON-NLS-1$
		criteria.addEqualTo("taxRateId", taxRateId); //$NON-NLS-1$
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Tax.class, criteria);
		Collection objects = Table.select(query);
		Iterator i = objects.iterator();
		if (i.hasNext())
		{
			tax = (Tax) i.next();
		}
		return tax;
	}
	
	public static Tax[] selectAll(boolean deletedToo)
	{
		if (Database.getCurrent().equals(Database.getTemporary()))
		{
			return Tax.getAll(deletedToo);
		}
		else
		{
			Criteria criteria = new Criteria();
			if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
			Query query = QueryFactory.newQuery(Tax.class, criteria);
			Collection taxes = Table.select(query);
			return (Tax[]) taxes.toArray(new Tax[0]);
		}
	}
	
	public static void readDBRecords()
	{
		Tax[] taxes = Tax.selectAll(false);
		for (int i = 0; i < taxes.length; i++)
		{
			Tax.put(taxes[i]);
		}
	}
	
	public static Tax getById(Long id)
	{
		return (Tax) Tax.records.get(id);
	}
	
	public static Tax getByGalileoId(String galileoId, boolean deletedToo)
	{
		Tax tax = (Tax) Tax.galileoIdIndex.get(galileoId);
		if (tax != null && (deletedToo || !tax.deleted))
		{
			return tax;
		}
		return new Tax();
	}
	
	public static Tax getByCode128Id(String code128Id, boolean deletedToo)
	{
		Tax tax = (Tax) Tax.code128IdIndex.get(code128Id);
		if (tax != null && (deletedToo || !tax.deleted))
		{
			return tax;
		}
		return new Tax();
	}
	
	public static Tax getByCode(String code)
	{
		return (Tax) Tax.codeIndex.get(code);
	}
	
	public static Tax getByTypeId(Long typeId)
	{
		Tax tax = null;
		Enumeration taxes = Tax.records.elements();
		while (taxes.hasMoreElements())
		{
			tax = (Tax) taxes.nextElement();
			if (tax.getTaxTypeId().equals(typeId))
			{
				return tax;
			}
		}
		return tax;
	}
	
	public static Tax getByRateId(Long rateId)
	{
		Tax tax = null;
		Enumeration taxes = Tax.records.elements();
		while (taxes.hasMoreElements())
		{
			tax = (Tax) taxes.nextElement();
			if (tax.getTaxRateId().equals(rateId))
			{
				return tax;
			}
		}
		return tax;
	}
	
	public static Tax getByTypeIdAndRateId(Long typeId, Long rateId, boolean deletedToo)
	{
		Tax tax = null;
		Enumeration taxes = Tax.records.elements();
		while (taxes.hasMoreElements())
		{
			tax = (Tax) taxes.nextElement();
			if (tax.getTaxTypeId().equals(typeId) && tax.getTaxRateId().equals(rateId))
			{
				if (deletedToo || !tax.deleted)
				{
					return tax;
				}
			}
		}
		return new Tax();
	}
	
	public static Tax[] getAll(boolean deletedToo)
	{
		if (Tax.records.isEmpty() && Database.getCurrent().equals(Database.getStandard()))
		{
			if (Database.getStandard().isActive() && Database.getStandard().isConnected())
			{
				return Tax.selectAll(true);
			}
		}
		else
		{
			Enumeration taxes = Tax.records.elements();
			ArrayList txs = new ArrayList();
			while (taxes.hasMoreElements())
			{
				Tax tax = (Tax) taxes.nextElement();
				if (!tax.deleted || deletedToo)
				{
					txs.add(tax);
				}
			}
			return (Tax[]) txs.toArray(new Tax[0]);
		}
		return null;
	}
	
	private static void clearData()
	{
		Tax.records.clear();
		Tax.galileoIdIndex.clear();
		Tax.code128IdIndex.clear();
		Tax.codeIndex.clear();
	}
	
	private static void put(Tax tax)
	{
		Tax.records.put(tax.getId(), tax);
		if (!tax.galileoId.equals("")) { //$NON-NLS-1$
			Tax.galileoIdIndex.put(tax.galileoId, tax);
		}
		if (!tax.code128Id.equals("")) { //$NON-NLS-1$
			Tax.code128IdIndex.put(tax.code128Id, tax);
		}
		Tax.codeIndex.put(tax.code, tax);
	}
	
	public static Element writeXMLRecords(Element root)
	{
		Element table = Database.getTemporary().getTable("tax"); //$NON-NLS-1$
		if (table == null)
		{
			table = new Element("table"); //$NON-NLS-1$
			table.setAttribute("name", "tax"); //$NON-NLS-1$ //$NON-NLS-2$
			root.addContent(table);
		}
		
		Enumeration entries = Tax.records.elements();
		while (entries.hasMoreElements())
		{
			Tax tax = (Tax) entries.nextElement();
			Element record = tax.getJDOMRecordAttributes();
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
		
		Element gi = new Element("field"); //$NON-NLS-1$
		gi.setAttribute("name", "galileo-id"); //$NON-NLS-1$ //$NON-NLS-2$
		gi.setAttribute("value", this.taxType.code.equals("U") ? this.galileoId : ""); //$NON-NLS-1$
		record.addContent(gi);
		
		Element fi = new Element("field"); //$NON-NLS-1$
		fi.setAttribute("name", "code128-id"); //$NON-NLS-1$ //$NON-NLS-2$
		fi.setAttribute("value", this.code128Id); //$NON-NLS-1$
		record.addContent(fi);
		
		Element ac = new Element("field"); //$NON-NLS-1$
		ac.setAttribute("name", "account"); //$NON-NLS-1$ //$NON-NLS-2$
		ac.setAttribute("value", this.account); //$NON-NLS-1$
		record.addContent(ac);
		
		Element tt = new Element("field"); //$NON-NLS-1$
		tt.setAttribute("name", "tax-type-id"); //$NON-NLS-1$ //$NON-NLS-2$
		tt.setAttribute("value", this.getTaxTypeId().toString()); //$NON-NLS-1$
		record.addContent(tt);
		
		Element tr = new Element("field"); //$NON-NLS-1$
		tr.setAttribute("name", "tax-rate-id"); //$NON-NLS-1$ //$NON-NLS-2$
		tr.setAttribute("value", this.getTaxRateId().toString()); //$NON-NLS-1$
		record.addContent(tr);
		
		Element ct = new Element("field"); //$NON-NLS-1$
		ct.setAttribute("name", "current-tax-id"); //$NON-NLS-1$ //$NON-NLS-2$
		ct.setAttribute("value", this.getCurrentTax().getId().toString()); //$NON-NLS-1$
		record.addContent(ct);
		
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
			else if (field.getAttributeValue("name").equals("galileo-id")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.galileoId = field.getAttributeValue("value"); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("code128-id")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.code128Id = field.getAttributeValue("value"); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("account")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.account = field.getAttributeValue("value"); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("tax-type-id")) { //$NON-NLS-1$ //$NON-NLS-2$
				TaxType taxType = TaxType.getById(new Long(XMLLoader.getLong(field.getAttributeValue("value")))); //$NON-NLS-1$
				if (taxType != null)
				{
					this.setTaxType(taxType);
				}
			}
			else if (field.getAttributeValue("name").equals("tax-rate-id")) { //$NON-NLS-1$ //$NON-NLS-2$
				TaxRate taxRate = TaxRate.getById(new Long(XMLLoader.getLong(field.getAttributeValue("value")))); //$NON-NLS-1$
				if (taxRate != null)
				{
					this.setTaxRate(taxRate);
				}
			}
			else if (field.getAttributeValue("name").equals("current-tax-id")) { //$NON-NLS-1$ //$NON-NLS-2$
				Long currentTaxId = new Long(XMLLoader.getLong(field.getAttributeValue("value"))); //$NON-NLS-1$
				this.setCurrentTaxId(currentTaxId);
			}
		}
	}
	
	public static void readXML()
	{
		Tax.clearData();
		Element[] elements = Database.getTemporary().getRecords("tax"); //$NON-NLS-1$
		for (int i = 0; i < elements.length; i++)
		{
			Tax tax = new Tax();
			tax.setData(elements[i]);
			Tax.put(tax);
		}
	}
	
	private CurrentTax currentTax;
	
	private static Hashtable records = new Hashtable();
	private static Hashtable galileoIdIndex = new Hashtable();
	private static Hashtable code128IdIndex = new Hashtable();
	private static Hashtable codeIndex = new Hashtable();
}