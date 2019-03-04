/*
 * Created on 30.06.2003
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

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.jdom.Element;

import ch.eugster.pos.util.XMLLoader;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PaymentType extends Table
{
	
	public boolean removeable = true;
	public String name = ""; //$NON-NLS-1$
	public String code = ""; //$NON-NLS-1$
	public String account = ""; //$NON-NLS-1$
	public boolean voucher = false;
	public boolean cash = false;
	public boolean back = false; // Data Version 21 @2008-05-21 Build 201
	public String exportId = ""; // Data Version 6 @2005-11-15 Build 76
	public boolean openCashdrawer = true; // Data Version 19 @2008-03-14 Build
	// 196
	public int sort;
	
	private Long foreignCurrencyId = null;
	private ForeignCurrency foreignCurrency = new ForeignCurrency();
	
	protected Long paymentTypeGroupId = null;
	protected PaymentTypeGroup paymentTypeGroup = new PaymentTypeGroup();
	
	public PaymentType()
	{
		super();
	}
	
	public void setPaymentTypeGroup(PaymentTypeGroup group)
	{
		if (group == null)
		{
			group = new PaymentTypeGroup();
		}
		this.paymentTypeGroup = group;
		this.paymentTypeGroupId = group.getId();
	}
	
	public Long getPaymentTypeGroupId()
	{
		return this.paymentTypeGroupId;
	}
	
	public PaymentTypeGroup getPaymentTypeGroup()
	{
		return this.paymentTypeGroup;
	}
	
	public void setForeignCurrency(ForeignCurrency currency)
	{
		this.foreignCurrency = currency;
		// this.foreignCurrencyId = currency.getId();
	}
	
	public void setForeignCurrency(Long id)
	{
		this.setForeignCurrency(ForeignCurrency.getById(id));
	}
	
	public ForeignCurrency getForeignCurrency()
	{
		if (this.foreignCurrency == null)
		{
			this.foreignCurrency = ForeignCurrency.getById(this.foreignCurrencyId);
		}
		return this.foreignCurrency;
	}
	
	public Long getForeignCurrencyId()
	{
		return this.foreignCurrencyId;
	}
	
	// private String getDefaultCurrency() {
	// return
	// NumberFormat.getCurrencyInstance(Config.getInstance().getDefaultLocale()).getCurrency().getCurrencyCode();
	// }
	//	
	public String getCurrencySymbol()
	{
		return this.foreignCurrency.getCurrency().getSymbol();
	}
	
	public String getCurrencyCode()
	{
		return this.foreignCurrency.getCurrency().getCurrencyCode();
	}
	
	public int getCurrencyDefaultFractionDigits()
	{
		return this.foreignCurrency.getCurrency().getDefaultFractionDigits();
	}
	
	public boolean isRemovable()
	{
		return !Payment.exist("paymentTypeId", this.getId()); //$NON-NLS-1$
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
	
	public static boolean exist(String key, Object value)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo(key, value);
		Query query = QueryFactory.newQuery(PaymentType.class, criteria);
		query.setEndAtIndex(1);
		Collection positions = Table.select(query);
		Iterator i = positions.iterator();
		return i.hasNext();
	}
	
	public static PaymentType selectById(Long pk)
	{
		PaymentType paymentType = new PaymentType();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("id", pk); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(PaymentType.class, criteria);
		Collection paymentTypes = Table.select(query);
		Iterator i = paymentTypes.iterator();
		if (i.hasNext())
		{
			paymentType = (PaymentType) i.next();
		}
		return paymentType;
	}
	
	public static PaymentType selectByCurrency(ForeignCurrency currency)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("foreignCurrencyId", currency.getId()); //$NON-NLS-1$
		criteria.addEqualTo("deleted", new Boolean(false));
		Query query = QueryFactory.newQuery(PaymentType.class, criteria);
		Collection paymentTypes = Table.select(query);
		Iterator i = paymentTypes.iterator();
		if (i.hasNext())
			return (PaymentType) i.next();
		else
			return null;
	}
	
	public static PaymentType selectByExportId(String exportId)
	{
		PaymentType paymentType = new PaymentType();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("exportId", exportId); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(PaymentType.class, criteria);
		Collection paymentTypes = Table.select(query);
		Iterator i = paymentTypes.iterator();
		if (i.hasNext())
		{
			paymentType = (PaymentType) i.next();
		}
		return paymentType;
	}
	
	public static PaymentType selectCash()
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("cash", new Boolean(true)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(PaymentType.class, criteria);
		Collection paymentTypes = Table.select(query);
		Iterator i = paymentTypes.iterator();
		if (i.hasNext())
			return (PaymentType) i.next();
		
		else
			return PaymentType.storeCash();
		
	}
	
	public static PaymentType storeCash()
	{
		PaymentType paymentType = new PaymentType();
		paymentType.setId(PaymentType.CASH_ID);
		paymentType.cash = true;
		paymentType.name = "Barzahlung"; //$NON-NLS-1$
		paymentType.code = "BAR"; //$NON-NLS-1$
		paymentType.setForeignCurrency(ForeignCurrency.getDefaultCurrency());
		paymentType.paymentTypeGroupId = new Long(1L);
		paymentType.store();
		
		Element table = Database.getTemporary().getTable("payment-type"); //$NON-NLS-1$
		if (table == null)
		{
			table = new Element("table"); //$NON-NLS-1$
			table.setAttribute("name", "payment-type"); //$NON-NLS-1$ //$NON-NLS-2$
			Database.getTemporary().getRoot().addContent(table);
		}
		
		Element record = paymentType.getJDOMRecordAttributes();
		List records = table.getChildren("record"); //$NON-NLS-1$
		if (!records.contains(record))
		{
			table.addContent(record);
		}
		
		return paymentType;
	}
	
	// public static PaymentType selectBack() {
	// PaymentType paymentType = new PaymentType();
	// Criteria criteria = new Criteria();
	//		criteria.addEqualTo("id", BACK_ID); //$NON-NLS-1$
	// Query query = QueryFactory.newQuery(PaymentType.class, criteria);
	// Collection paymentTypes = select(query);
	// Iterator i = paymentTypes.iterator();
	// if (i.hasNext()) {
	// paymentType = (PaymentType) i.next();
	// }
	// else {
	// paymentType = storeBack();
	// }
	// return paymentType;
	// }
	
	// public static PaymentType storeBack() {
	// PaymentType paymentType = new PaymentType();
	// paymentType.setId(BACK_ID);
	//		paymentType.name = "Rückgeld"; //$NON-NLS-1$
	//		paymentType.code = "Rückgeld"; //$NON-NLS-1$
	// paymentType.paymentTypeGroupId = new Long(2L);
	// paymentType.setForeignCurrency(ForeignCurrency.getDefaultCurrency());
	// paymentType.store();
	//
	//		Element table = Database.getTemporary().getTable("payment-type"); //$NON-NLS-1$
	// if (table == null) {
	//			table = new Element("table"); //$NON-NLS-1$
	//			table.setAttribute("name", "payment-type"); //$NON-NLS-1$ //$NON-NLS-2$
	// Database.getTemporary().getRoot().addContent(table);
	// }
	//
	// Element record = paymentType.getJDOMRecordAttributes();
	//		List records = table.getChildren("record"); //$NON-NLS-1$
	// if (!records.contains(record)) {
	// table.addContent(record);
	// }
	//		
	// return paymentType;
	// }
	
	public static PaymentType selectByCode(String code, boolean deletedToo)
	{
		PaymentType paymentType = new PaymentType();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("code", code); //$NON-NLS-1$
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(PaymentType.class, criteria);
		Collection paymentTypes = Table.select(query);
		Iterator i = paymentTypes.iterator();
		if (i.hasNext())
		{
			paymentType = (PaymentType) i.next();
		}
		return paymentType;
	}
	
	// public static boolean doesBackVoucherExist() {
	// PaymentType paymentType = new PaymentType();
	// Criteria criteria = new Criteria();
	//		criteria.addEqualTo("paymentVoucher", new Boolean(true)); //$NON-NLS-1$
	//		criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
	// Query query = QueryFactory.newQuery(PaymentType.class, criteria);
	// Collection paymentTypes = select(query);
	// Iterator i = paymentTypes.iterator();
	// return i.hasNext();
	// }
	
	public static PaymentType[] selectByGroup(PaymentTypeGroup group, boolean deletedToo)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("paymentTypeGroupId", group.getId()); //$NON-NLS-1$
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(PaymentType.class, criteria);
		return (PaymentType[]) Table.select(query).toArray(new PaymentType[0]);
	}
	
	public static PaymentType[] selectBacks(boolean deletedToo)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("back", new Boolean(true));
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(PaymentType.class, criteria);
		return (PaymentType[]) Table.select(query).toArray(new PaymentType[0]);
	}
	
	public static PaymentType[] selectAll(boolean deletedToo)
	{
		Criteria criteria = new Criteria();
		//		criteria.addOrderBy("id", true); //$NON-NLS-1$
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		QueryByCriteria query = QueryFactory.newQuery(PaymentType.class, criteria);
		query.addOrderBy("id", true); //$NON-NLS-1$
		return (PaymentType[]) Table.select(query).toArray(new PaymentType[0]);
	}
	
	public static void readDBRecords()
	{
		PaymentType[] paymentTypes = PaymentType.selectAll(false);
		for (int i = 0; i < paymentTypes.length; i++)
		{
			PaymentType.put(paymentTypes[i]);
		}
		// if (myPaymentVoucher == null) myPaymentVoucher =
		// getPaymentTypeBack();
	}
	
	public static PaymentType getPaymentTypeCash()
	{
		PaymentType paymentTypeCash = null;
		if (Database.getCurrent().equals(Database.getStandard()) && Database.getStandard().isConnected())
		{
			try
			{
				paymentTypeCash = PaymentType.selectCash();
			}
			catch (Exception e)
			{
				paymentTypeCash = PaymentType.getById(PaymentType.CASH_ID);
			}
			finally
			{
			}
		}
		else
		{
			paymentTypeCash = PaymentType.getById(PaymentType.CASH_ID);
		}
		return paymentTypeCash;
	}
	
	public static PaymentType[] getBacks()
	{
		Collection backs = new ArrayList();
		Iterator iterator = PaymentType.records.values().iterator();
		while (iterator.hasNext())
		{
			PaymentType type = (PaymentType) iterator.next();
			if (type.back) backs.add(type);
		}
		return (PaymentType[]) backs.toArray(new PaymentType[0]);
	}
	
	public static PaymentType getById(Long id)
	{
		return (PaymentType) PaymentType.records.get(id);
	}
	
	// public static PaymentType getPaymentVoucher() {
	// return PaymentType.myPaymentVoucher;
	// }
	
	public static PaymentType getByShortname(String shortname)
	{
		return (PaymentType) PaymentType.shortnameIndex.get(shortname);
	}
	
	public static PaymentType[] getAll(boolean deletedToo)
	{
		Enumeration types = PaymentType.records.elements();
		ArrayList pts = new ArrayList();
		while (types.hasMoreElements())
		{
			PaymentType pt = (PaymentType) types.nextElement();
			if (!pt.deleted || deletedToo)
			{
				pts.add(pt);
			}
		}
		return (PaymentType[]) pts.toArray(new PaymentType[0]);
	}
	
	private static void clearData()
	{
		PaymentType.records.clear();
		PaymentType.shortnameIndex.clear();
	}
	
	private static void put(PaymentType paymentType)
	{
		PaymentType.records.put(paymentType.getId(), paymentType);
		PaymentType.shortnameIndex.put(paymentType.code, paymentType);
		// if (paymentType.paymentVoucher) myPaymentVoucher = paymentType;
	}
	
	public static Element writeXMLRecords(Element root)
	{
		Element table = Database.getTemporary().getTable("payment-type"); //$NON-NLS-1$
		if (table == null)
		{
			table = new Element("table"); //$NON-NLS-1$
			table.setAttribute("name", "payment-type"); //$NON-NLS-1$ //$NON-NLS-2$
			root.addContent(table);
		}
		
		Enumeration entries = PaymentType.records.elements();
		while (entries.hasMoreElements())
		{
			PaymentType paymentType = (PaymentType) entries.nextElement();
			Element record = paymentType.getJDOMRecordAttributes();
			table.addContent(record);
		}
		return root;
	}
	
	protected Element getJDOMRecordAttributes()
	{
		Element record = super.getJDOMRecordAttributes();
		
		Element rm = new Element("field"); //$NON-NLS-1$
		rm.setAttribute("name", "removeable"); //$NON-NLS-1$ //$NON-NLS-2$
		rm.setAttribute("value", new Boolean(this.removeable).toString()); //$NON-NLS-1$
		record.addContent(rm);
		
		Element na = new Element("field"); //$NON-NLS-1$
		na.setAttribute("name", "name"); //$NON-NLS-1$ //$NON-NLS-2$
		na.setAttribute("value", this.name); //$NON-NLS-1$
		record.addContent(na);
		
		Element sn = new Element("field"); //$NON-NLS-1$
		sn.setAttribute("name", "code"); //$NON-NLS-1$ //$NON-NLS-2$
		sn.setAttribute("value", this.code); //$NON-NLS-1$
		record.addContent(sn);
		
		Element ac = new Element("field"); //$NON-NLS-1$
		ac.setAttribute("name", "account"); //$NON-NLS-1$ //$NON-NLS-2$
		ac.setAttribute("value", this.account); //$NON-NLS-1$
		record.addContent(ac);
		
		Element vo = new Element("field"); //$NON-NLS-1$
		vo.setAttribute("name", "voucher"); //$NON-NLS-1$ //$NON-NLS-2$
		vo.setAttribute("value", new Boolean(this.voucher).toString()); //$NON-NLS-1$
		record.addContent(vo);
		
		// 10226
		Element pv = new Element("field"); //$NON-NLS-1$
		pv.setAttribute("name", "cash"); //$NON-NLS-1$ //$NON-NLS-2$
		pv.setAttribute("value", new Boolean(this.cash).toString()); //$NON-NLS-1$
		record.addContent(pv);
		// 10226
		
		Element cu = new Element("field"); //$NON-NLS-1$
		cu.setAttribute("name", "foreign-currency"); //$NON-NLS-1$ //$NON-NLS-2$
		cu.setAttribute("value", this.foreignCurrency.getId().toString()); //$NON-NLS-1$
		record.addContent(cu);
		
		Element pg = new Element("field"); //$NON-NLS-1$
		pg.setAttribute("name", "payment-type-group-id"); //$NON-NLS-1$ //$NON-NLS-2$
		pg.setAttribute("value", this.paymentTypeGroupId.toString()); //$NON-NLS-1$
		record.addContent(pg);
		
		// Start Build 76
		Element ei = new Element("field"); //$NON-NLS-1$
		ei.setAttribute("name", "export-id"); //$NON-NLS-1$ //$NON-NLS-2$
		ei.setAttribute("value", this.exportId); //$NON-NLS-1$
		record.addContent(ei);
		// End Build 76
		
		// Start Build 196
		Element oc = new Element("field"); //$NON-NLS-1$
		oc.setAttribute("name", "open-cashdrawer"); //$NON-NLS-1$ //$NON-NLS-2$
		oc.setAttribute("value", new Boolean(this.openCashdrawer).toString()); //$NON-NLS-1$
		record.addContent(oc);
		// End Build 196
		
		// 10226
		Element ba = new Element("field"); //$NON-NLS-1$
		ba.setAttribute("name", "back"); //$NON-NLS-1$ //$NON-NLS-2$
		ba.setAttribute("value", new Boolean(this.back).toString()); //$NON-NLS-1$
		record.addContent(ba);
		// 10226
		
		ba = new Element("field"); //$NON-NLS-1$
		ba.setAttribute("name", "sort"); //$NON-NLS-1$ //$NON-NLS-2$
		ba.setAttribute("value", Integer.toString(this.sort)); //$NON-NLS-1$
		record.addContent(ba);
		
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
			if (field.getAttributeValue("name").equals("removeable")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.removeable = new Boolean(field.getAttributeValue("value")).booleanValue(); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("name")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.name = field.getAttributeValue("value"); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("code")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.code = field.getAttributeValue("value"); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("account")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.account = field.getAttributeValue("value"); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("voucher")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.voucher = new Boolean(field.getAttributeValue("value")).booleanValue(); //$NON-NLS-1$
			}
			// 10226
			else if (field.getAttributeValue("name").equals("cash")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.cash = new Boolean(field.getAttributeValue("value")).booleanValue(); //$NON-NLS-1$
			}
			// 10226
			else if (field.getAttributeValue("name").equals("foreign-currency")) { //$NON-NLS-1$ //$NON-NLS-2$
				ForeignCurrency fc = ForeignCurrency.getById(new Long(XMLLoader.getLong(field
								.getAttributeValue("value")))); //$NON-NLS-1$
				this.setForeignCurrency(fc);
			}
			else if (field.getAttributeValue("name").equals("payment-type-group-id")) { //$NON-NLS-1$ //$NON-NLS-2$
				PaymentTypeGroup ptg = PaymentTypeGroup.getById(new Long(XMLLoader.getLong(field
								.getAttributeValue("value")))); //$NON-NLS-1$
				this.setPaymentTypeGroup(ptg);
			}
			// Start Build 76
			else if (field.getAttributeValue("name").equals("export-id")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.exportId = field.getAttributeValue("value"); //$NON-NLS-1$
			}
			// End Build 76
			// Start Build 196
			else if (field.getAttributeValue("name").equals("open-cashdrawer")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.openCashdrawer = new Boolean(field.getAttributeValue("value")).booleanValue(); //$NON-NLS-1$
			}
			// End Build 196
			// 10226
			else if (field.getAttributeValue("name").equals("back")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.back = new Boolean(field.getAttributeValue("value")).booleanValue(); //$NON-NLS-1$
			}
			// 10226
			else if (field.getAttributeValue("name").equals("sort")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.sort = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
			}
		}
	}
	
	public static void readXML()
	{
		PaymentType.clearData();
		Element[] elements = Database.getTemporary().getRecords("payment-type"); //$NON-NLS-1$
		for (int i = 0; i < elements.length; i++)
		{
			PaymentType paymentType = new PaymentType();
			paymentType.setData(elements[i]);
			PaymentType.put(paymentType);
		}
		// if (myPaymentVoucher == null) myPaymentVoucher =
		// getPaymentTypeBack();
	}
	
	protected static Hashtable records = new Hashtable();
	protected static Hashtable shortnameIndex = new Hashtable();
	// protected static PaymentType myPaymentVoucher = null;
	
	public final static Long CASH_ID = new Long(1L);
	// public final static Long BACK_ID = new Long(2L);
}
