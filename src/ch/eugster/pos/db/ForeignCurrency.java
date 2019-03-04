/*
 * Created on 19.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.db;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
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

import ch.eugster.pos.util.Config;
import ch.eugster.pos.util.NumberUtility;
import ch.eugster.pos.util.XMLLoader;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ForeignCurrency extends Table
{
	
	public String code = "CHF"; //$NON-NLS-1$
	public String name = ""; //$NON-NLS-1$
	public String region = ""; //$NON-NLS-1$
	public double quotation = 1d;
	public String account = ""; //$NON-NLS-1$
	public double roundFactor = .01d;
	
	// private Collection coins = new ArrayList();
	/*
	 * Only for internal use (set image in CurrencyViewer of Administrator
	 */
	public boolean isUsed = false;
	
	/**
	 * 
	 */
	public ForeignCurrency()
	{
		super();
	}
	
	public ForeignCurrency(String code, String name, String region)
	{
		this(Currency.getInstance(code), name, region);
	}
	
	public ForeignCurrency(Currency currency, String name, String region)
	{
		super();
		this.name = name;
		this.region = region;
	}
	
	// public Collection getCoins()
	// {
	// return coins;
	// }
	
	public Currency getCurrency()
	{
		return Currency.getInstance(this.code);
	}
	
	public boolean isRemovable()
	{
		return !PaymentType.exist("foreignCurrencyId", this.getId()); //$NON-NLS-1$
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
	
	public static ForeignCurrency selectById(Long pk)
	{
		ForeignCurrency currency = new ForeignCurrency();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("id", pk); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(ForeignCurrency.class, criteria);
		Collection currencies = Table.select(query);
		Iterator i = currencies.iterator();
		if (i.hasNext())
		{
			currency = (ForeignCurrency) i.next();
		}
		return currency;
	}
	
	public static ForeignCurrency selectDefaultCurrency()
	{
		return ForeignCurrency.selectByCode(Config.getInstance().getCurrencyDefault());
	}
	
	public static ForeignCurrency selectByCode(String code)
	{
		ForeignCurrency currency = new ForeignCurrency();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("code", code); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(ForeignCurrency.class, criteria);
		Collection currencies = Table.select(query);
		Iterator i = currencies.iterator();
		if (i.hasNext())
		{
			currency = (ForeignCurrency) i.next();
		}
		return currency;
	}
	
	public static ForeignCurrency[] selectAll(boolean deletedToo)
	{
		Criteria criteria = new Criteria();
		//		criteria.addOrderBy("code", true); //$NON-NLS-1$
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		QueryByCriteria query = QueryFactory.newQuery(ForeignCurrency.class, criteria);
		query.addOrderBy("code", true); //$NON-NLS-1$
		Collection currencies = Table.select(query);
		return (ForeignCurrency[]) currencies.toArray(new ForeignCurrency[0]);
	}
	
	public static RemovalAwareCollection selectAllUsed(boolean deletedToo)
	{
		Criteria criteria = new Criteria();
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(ForeignCurrency.class, criteria);
		return (RemovalAwareCollection) Table.select(query);
	}
	
	public static String[] selectAllReturnCodes(boolean deletedToo)
	{
		Criteria criteria = new Criteria();
		//		criteria.addOrderBy("code", true); //$NON-NLS-1$
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		QueryByCriteria query = QueryFactory.newQuery(ForeignCurrency.class, criteria);
		query.addOrderBy("code", true); //$NON-NLS-1$
		Collection currencies = Table.select(query);
		String[] currencyCodes = new String[currencies.size()];
		Iterator it = currencies.iterator();
		for (int i = 0; i < currencyCodes.length; i++)
		{
			currencyCodes[i] = ((ForeignCurrency) it.next()).code;
		}
		return currencyCodes;
	}
	
	public static String getFormattedAmount(double amount, ForeignCurrency fc)
	{
		return NumberUtility.formatCurrency(NumberFormat.getCurrencyInstance(), fc.getCurrency(), amount, true);
	}
	
	public static String getFormattedCalculatedForeignCurrencyAmount(double amount, ForeignCurrency fc)
	{
		return NumberUtility.formatCurrency(NumberFormat.getCurrencyInstance(), fc.getCurrency(), fc.calculate(amount),
						true);
	}
	
	public double calculate(double amount)
	{
		return NumberUtility.round(amount / this.quotation, this.roundFactor);
	}
	
	public static void readDBRecords()
	{
		ForeignCurrency[] foreignCurrencies = ForeignCurrency.selectAll(false);
		for (int i = 0; i < foreignCurrencies.length; i++)
		{
			ForeignCurrency.put(foreignCurrencies[i]);
		}
	}
	
	public static ForeignCurrency getDefaultCurrency()
	{
		if (ForeignCurrency.defaultCurrency == null)
		{
			ForeignCurrency.defaultCurrency = ForeignCurrency.getByCode(Config.getInstance().getCurrency()
							.getAttributeValue("default")); //$NON-NLS-1$
		}
		if (ForeignCurrency.defaultCurrency == null)
		{
			ForeignCurrency.defaultCurrency = ForeignCurrency.selectDefaultCurrency();
		}
		return ForeignCurrency.defaultCurrency;
	}
	
	public static ForeignCurrency[] selectUsedForeingCurrencies()
	{
		PaymentType[] pt = PaymentType.selectAll(false);
		Collection usedCurrencies = new ArrayList();
		for (int i = 0; i < pt.length; i++)
		{
			if (!usedCurrencies.contains(pt[i].getForeignCurrency().code))
			{
				usedCurrencies.add(pt[i].getForeignCurrency().code);
			}
		}
		
		RemovalAwareCollection fcs = ForeignCurrency.selectAllUsed(false);
		
		Iterator i = fcs.iterator();
		while (i.hasNext())
		{
			ForeignCurrency fc = (ForeignCurrency) i.next();
			if (usedCurrencies.contains(fc.code))
			{
				fc.isUsed = true;
			}
			else
			{
				fc.isUsed = false;
			}
		}
		return (ForeignCurrency[]) fcs.toArray(new ForeignCurrency[0]);
	}
	
	public static ForeignCurrency getById(Long id)
	{
		return (ForeignCurrency) ForeignCurrency.records.get(id);
	}
	
	public static ForeignCurrency getByCode(String code)
	{
		return (ForeignCurrency) ForeignCurrency.codeIndex.get(code);
	}
	
	private static void clearData()
	{
		ForeignCurrency.records.clear();
		ForeignCurrency.codeIndex.clear();
	}
	
	private static void put(ForeignCurrency foreignCurrency)
	{
		ForeignCurrency.records.put(foreignCurrency.getId(), foreignCurrency);
		ForeignCurrency.codeIndex.put(foreignCurrency.code, foreignCurrency);
	}
	
	public static Element writeXMLRecords(Element root)
	{
		Element table = Database.getTemporary().getTable("foreign-currency"); //$NON-NLS-1$
		if (table == null)
		{
			table = new Element("table"); //$NON-NLS-1$
			table.setAttribute("name", "foreign-currency"); //$NON-NLS-1$ //$NON-NLS-2$
			root.addContent(table);
		}
		
		Enumeration entries = ForeignCurrency.records.elements();
		while (entries.hasMoreElements())
		{
			ForeignCurrency rec = (ForeignCurrency) entries.nextElement();
			Element record = rec.getJDOMRecordAttributes();
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
		
		Element rg = new Element("field"); //$NON-NLS-1$
		rg.setAttribute("name", "region"); //$NON-NLS-1$ //$NON-NLS-2$
		rg.setAttribute("value", this.region); //$NON-NLS-1$
		record.addContent(rg);
		
		Element qu = new Element("field"); //$NON-NLS-1$
		qu.setAttribute("name", "quotation"); //$NON-NLS-1$ //$NON-NLS-2$
		qu.setAttribute("value", Double.toString(this.quotation)); //$NON-NLS-1$
		record.addContent(qu);
		
		Element ac = new Element("field"); //$NON-NLS-1$
		ac.setAttribute("name", "account"); //$NON-NLS-1$ //$NON-NLS-2$
		ac.setAttribute("value", this.account); //$NON-NLS-1$
		record.addContent(ac);
		
		Element rf = new Element("field"); //$NON-NLS-1$
		rf.setAttribute("name", "round-factor"); //$NON-NLS-1$ //$NON-NLS-2$
		rf.setAttribute("value", Double.toString(this.roundFactor)); //$NON-NLS-1$
		record.addContent(rf);
		
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
			else if (field.getAttributeValue("name").equals("region")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.region = field.getAttributeValue("value"); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("quotation")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.quotation = XMLLoader.getDouble(field.getAttributeValue("value")); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("account")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.account = field.getAttributeValue("value"); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("round-factor")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.roundFactor = XMLLoader.getDouble(field.getAttributeValue("value")); //$NON-NLS-1$
			}
		}
	}
	
	public static void readXML()
	{
		ForeignCurrency.clearData();
		Element[] elements = Database.getTemporary().getRecords("foreign-currency"); //$NON-NLS-1$
		for (int i = 0; i < elements.length; i++)
		{
			ForeignCurrency fc = new ForeignCurrency();
			fc.setData(elements[i]);
			ForeignCurrency.put(fc);
		}
	}
	
	private static ForeignCurrency defaultCurrency;
	private static Hashtable records = new Hashtable();
	private static Hashtable codeIndex = new Hashtable();
}
