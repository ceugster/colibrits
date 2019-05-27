/*
 * Created on 13.03.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package ch.eugster.pos.db;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
 */
public class ProductGroup extends Table
{
	
	/*
	 * Die Defaultgruppe wird eingesetzt, wenn keine Warengruppe gefunden wurde.
	 */
	public boolean isDefault = ProductGroup.DEFAULT_GROUP;
	/*
	 * GalileoId entspricht der Warengruppe in Galileo. Da ColibriTS neben den
	 * Galileo- auch eigene Warengruppen (auch Ausgaben und nicht
	 * umsatzrelevante Leistungen haben kann, wurde ProductGroup durch dieses
	 * Attribut zur Kenntzeichnung der Warengruppen, die aus Galileo kommen
	 * ergänzt
	 */
	public String galileoId = ProductGroup.GALILEO_ID_DEFAULT;
	/*
	 * Ein Kürzel, das automatisch bei der Verwendung in die Tastenbelegung
	 * übernommen wird
	 */
	public String shortname = ProductGroup.SHORT_NAME_DEFAULT;
	/*
	 * Die Bezeichnung der Warengruppe
	 */
	public String name = ProductGroup.NAME_DEFAULT;
	/*
	 * Mengenvorschlag
	 */
	public int quantityProposal = ProductGroup.QUANTITY_DEFAULT;
	/*
	 * Preisvorschlag
	 */
	public double priceProposal = ProductGroup.PRICE_DEFAULT;
	/*
	 * Optionscodevorschlag
	 */
	public String optCodeProposal = ProductGroup.OPT_CODE_DEFAULT;
	/*
	 * Das Fibukonto wird beim Fibutransfer benötigt. Wenn die Funktion
	 * Fibutransfer verwendet wird, müssen alle Warengruppen ein gültiges und
	 * der Fibu bekanntes Konto aufweisen
	 */
	public String account = ""; //$NON-NLS-1$
	/*
	 * WG: Bezahlte Rechnung
	 */
	public boolean paidInvoice = ProductGroup.PAID_INVOICE_DEFAULT;
	/*
	 * Ausgabe
	 */
	// public Boolean isExpense = IS_EXPENSE_DEFAULT;
	/*
	 * Wurde in Galileo verändert. Daher sollten die Warengruppeneigenschaften
	 * überprüft werden
	 */
	public boolean modified = ProductGroup.MODIFIED;
	
	public int type = ProductGroup.TYPE_INCOME;
	
	public String exportId = "";
	
	public boolean ebook = false;
	
	private Tax defaultTax;
	private Long defaultTaxId;
	
	private ForeignCurrency foreignCurrency;
	private Long foreignCurrencyId;
	/**
	 * Neuer Typ ab 10231: Geldentnahme aus Kasse zwecks Eigeneinzahlung auf
	 * Bank
	 */
	private boolean withdraw = false;
	
	public ProductGroup()
	{
		this.setForeignCurrency(ForeignCurrency.getDefaultCurrency());
	}
	
	public void setDefaultTax(Tax tax)
	{
		if (tax == null)
		{
			tax = Tax.getByCode("UR");
		}
		this.defaultTax = tax;
		this.defaultTaxId = tax.getId();
	}
	
	public Long getDefaultTaxId()
	{
		return this.defaultTaxId;
	}
	
	public Tax getDefaultTax()
	{
		return this.defaultTax;
	}
	
	public void setEbook(boolean ebook)
	{
		this.ebook = ebook;
	}
	
	public boolean isEbook()
	{
		return this.ebook;
	}
	
	public void setForeignCurrency(ForeignCurrency currency)
	{
		this.foreignCurrency = currency;
		this.foreignCurrencyId = currency == null ? null : currency.getId();
	}
	
	public Long getForeignCurrencyId()
	{
		return this.foreignCurrencyId;
	}
	
	public ForeignCurrency getForeignCurrency()
	{
		if (this.foreignCurrency == null || this.foreignCurrency.getId() == null)
			this.foreignCurrency = ForeignCurrency.getDefaultCurrency();
		
		return this.foreignCurrency;
	}
	
	public boolean hasPositions()
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("productGroupId", this.getId()); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(Position.class, criteria);
		query.setEndAtIndex(1);
		Collection positions = Table.select(query);
		return positions.size() > 0;
	}
	
	@Override
	public boolean isRemovable()
	{
		return !Position.exist("productGroupId", this.getId()); //$NON-NLS-1$
	}
	
	@Override
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
		Query query = QueryFactory.newQuery(ProductGroup.class, criteria);
		query.setEndAtIndex(1);
		Collection productGroups = Table.select(query);
		Iterator i = productGroups.iterator();
		return i.hasNext();
	}
	
	public static ProductGroup selectById(Long pk)
	{
		ProductGroup pg = new ProductGroup();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("id", pk); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(ProductGroup.class, criteria);
		Collection productGroups = Table.select(query);
		Iterator i = productGroups.iterator();
		if (i.hasNext())
		{
			pg = (ProductGroup) i.next();
		}
		return pg;
	}
	
	public static ProductGroup selectByGalileoId(String galileoId, boolean deletedToo)
	{
		ProductGroup pg = new ProductGroup();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("galileoId", galileoId); //$NON-NLS-1$
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(ProductGroup.class, criteria);
		Collection productGroups = Table.select(query);
		Iterator i = productGroups.iterator();
		if (i.hasNext())
		{
			pg = (ProductGroup) i.next();
		}
		return pg;
	}
	
	public static ProductGroup selectByExportId(String exportId)
	{
		ProductGroup pg = new ProductGroup();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("exportId", exportId); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(ProductGroup.class, criteria);
		Collection productGroups = Table.select(query);
		Iterator i = productGroups.iterator();
		if (i.hasNext())
		{
			pg = (ProductGroup) i.next();
		}
		return pg;
	}
	
	public static ProductGroup[] select(ProductGroup except, Integer productGroupType)
	{
		// ProductGroup pg = ProductGroup.getDefaultGroup();
		Criteria criteria = new Criteria();
		criteria.addNotEqualTo("id", except.getId()); //$NON-NLS-1$
		criteria.addEqualTo("type", productGroupType);
		QueryByCriteria query = QueryFactory.newQuery(ProductGroup.class, criteria);
		query.addOrderByAscending("name");
		Collection productGroups = Table.select(query);
		return (ProductGroup[]) productGroups.toArray(new ProductGroup[0]);
	}
	
	public static ProductGroup[] selectByTax(Tax tax, boolean deletedToo)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("defaultTaxId", tax.getId()); //$NON-NLS-1$
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(ProductGroup.class, criteria);
		return (ProductGroup[]) Table.select(query).toArray(new ProductGroup[0]);
	}
	
	public static ProductGroup selectDefaultGroup()
	{
		ProductGroup pg = new ProductGroup();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("isDefault", new Boolean(true)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(ProductGroup.class, criteria);
		Collection productGroups = Table.select(query);
		Iterator i = productGroups.iterator();
		if (i.hasNext())
		{
			pg = (ProductGroup) i.next();
			if (pg.type != ProductGroup.TYPE_INCOME)
			{
				pg.type = ProductGroup.TYPE_INCOME;
				pg.store();
			}
		}
		return pg;
	}
	
	public static ProductGroup selectEbookGroup()
	{
		ProductGroup pg = null;
		Criteria criteria = new Criteria();
		criteria.addEqualTo("ebook", new Boolean(true)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(ProductGroup.class, criteria);
		Collection productGroups = Table.select(query);
		Iterator i = productGroups.iterator();
		if (i.hasNext())
		{
			pg = (ProductGroup) i.next();
			if (pg.type != ProductGroup.TYPE_INCOME)
			{
				pg.type = ProductGroup.TYPE_INCOME;
				pg.store();
			}
		}
		return pg;
	}
	
	public static ProductGroup selectPaidInvoiceGroup()
	{
		ProductGroup pg = new ProductGroup();
		Criteria criteria = new Criteria();
		/*
		 * Vorsicht: isIncome wird NEU VERWENDET, nicht mehr als Indikator, ob
		 * die WG Umsatzrelevant ist, sondern, ob sie die WG für das bezahlen
		 * von Rechnungen ist.
		 */
		criteria.addEqualTo("paidInvoice", new Boolean(true)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(ProductGroup.class, criteria);
		Collection productGroups = Table.select(query);
		Iterator i = productGroups.iterator();
		if (i.hasNext())
		{
			pg = (ProductGroup) i.next();
		}
		return pg;
	}
	
	public static ProductGroup[] selectProductGroups(boolean deletedToo)
	{
		Criteria criteria = new Criteria();
		//		criteria.addEqualTo("isExpense", new Boolean(false)); //$NON-NLS-1$
		criteria.addEqualTo("type", new Integer(ProductGroup.TYPE_INCOME));
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		//		criteria.addOrderBy("name", true); //$NON-NLS-1$
		QueryByCriteria query = QueryFactory.newQuery(ProductGroup.class, criteria);
		query.addOrderBy("name", true); //$NON-NLS-1$
		Collection productGroups = Table.select(query);
		return (ProductGroup[]) productGroups.toArray(new ProductGroup[0]);
	}
	
	public static ProductGroup[] selectAll(boolean deletedToo)
	{
		Criteria criteria = new Criteria();
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		//		criteria.addOrderBy("name", true); //$NON-NLS-1$
		QueryByCriteria query = QueryFactory.newQuery(ProductGroup.class, criteria);
		query.addOrderBy("name", true); //$NON-NLS-1$
		Collection productGroups = Table.select(query);
		return (ProductGroup[]) productGroups.toArray(new ProductGroup[0]);
	}
	
	public static void readDBRecords()
	{
		ProductGroup[] groups = ProductGroup.selectAll(true);
		for (ProductGroup group : groups)
		{
			ProductGroup.put(group);
		}
	}
	
	public static ProductGroup getById(Long id)
	{
		return (ProductGroup) ProductGroup.records.get(id);
	}
	
	public static ProductGroup getPaidInvoiceGroup()
	{
		if (ProductGroup.paidInvoiceProductGroup == null)
		{
			Enumeration groups = ProductGroup.records.elements();
			while (groups.hasMoreElements())
			{
				ProductGroup productGroup = (ProductGroup) groups.nextElement();
				if (productGroup.paidInvoice == true)
				{
					ProductGroup.paidInvoiceProductGroup = productGroup;
				}
			}
		}
		if (ProductGroup.paidInvoiceProductGroup == null || ProductGroup.paidInvoiceProductGroup.getId() == null
						|| ProductGroup.paidInvoiceProductGroup.getId().equals(ProductGroup.ZERO_VALUE))
		{
			ProductGroup.paidInvoiceProductGroup = null;
			if (Database.getCurrent().equals(Database.getStandard()))
			{
				ProductGroup.paidInvoiceProductGroup = ProductGroup.selectPaidInvoiceGroup();
			}
		}
		if (ProductGroup.paidInvoiceProductGroup == null || ProductGroup.paidInvoiceProductGroup.getId() == null)
		{
			if (Database.getCurrent().isStandard())
			{
				ProductGroup.paidInvoiceProductGroup = new ProductGroup();
				ProductGroup.paidInvoiceProductGroup.isDefault = false;
				ProductGroup.paidInvoiceProductGroup.name = "Bezahlte Rechnung";
				ProductGroup.paidInvoiceProductGroup.setDefaultTax(Tax.getByCode("UF"));
				ProductGroup.paidInvoiceProductGroup.deleted = false;
				ProductGroup.paidInvoiceProductGroup.galileoId = "";
				// paidInvoiceProductGroup.isExpense = new Boolean(false);
				ProductGroup.paidInvoiceProductGroup.paidInvoice = true;
				ProductGroup.paidInvoiceProductGroup.modified = true;
				ProductGroup.paidInvoiceProductGroup.optCodeProposal = "L";
				ProductGroup.paidInvoiceProductGroup.priceProposal = 0d;
				ProductGroup.paidInvoiceProductGroup.quantityProposal = 1;
				ProductGroup.paidInvoiceProductGroup.shortname = "Bez.Rg.";
				ProductGroup.paidInvoiceProductGroup.timestamp = new Timestamp(new Date().getTime());
				ProductGroup.paidInvoiceProductGroup.type = ProductGroup.TYPE_NOT_INCOME;
				ProductGroup.paidInvoiceProductGroup.withdraw = false;
				ProductGroup.paidInvoiceProductGroup.foreignCurrency = ForeignCurrency.getDefaultCurrency();
				ProductGroup.paidInvoiceProductGroup.store();
				ProductGroup.put(ProductGroup.paidInvoiceProductGroup);
			}
		}
		return ProductGroup.paidInvoiceProductGroup;
	}
	
	public static ProductGroup getByGalileoId(String galileoId, boolean deletedToo)
	{
		ProductGroup group = (ProductGroup) ProductGroup.galileoIdIndex.get(galileoId);
		if (group != null && (deletedToo || !group.deleted))
		{
			return group;
		}
		/*
		 * 10001 Wenn die Gruppe unbekannt ist, soll nicht eine leere Gruppe
		 * zurückgegeben werden, sondern die Default-Gruppe. Wenn keine
		 * Default-Gruppe existiert...
		 */
		return ProductGroup.getDefaultGroup();
	}
	
	public static ProductGroup getEmptyInstance()
	{
		ProductGroup pg = new ProductGroup();
		return pg;
	}
	
	public static ProductGroup getDefaultGroup()
	{
		if (ProductGroup.defaultProductGroup == null || ProductGroup.defaultProductGroup.getId() == null
						|| ProductGroup.defaultProductGroup.getId().equals(ProductGroup.ZERO_VALUE))
		{
			ProductGroup.defaultProductGroup = null;
			if (Database.getCurrent().equals(Database.getStandard()))
			{
				ProductGroup.defaultProductGroup = ProductGroup.selectDefaultGroup();
			}
		}
		if (ProductGroup.defaultProductGroup == null)
		{
			Enumeration groups = ProductGroup.records.elements();
			while (groups.hasMoreElements())
			{
				ProductGroup productGroup = (ProductGroup) groups.nextElement();
				if (productGroup.isDefault == true)
				{
					ProductGroup.defaultProductGroup = productGroup;
				}
			}
			if (ProductGroup.defaultProductGroup == null)
			{
				if (Database.getCurrent().isStandard())
				{
					ProductGroup.defaultProductGroup = ProductGroup.selectByGalileoId("999", true);
					if (ProductGroup.defaultProductGroup == null || ProductGroup.defaultProductGroup.getId() == null
									|| ProductGroup.defaultProductGroup.getId().equals(ProductGroup.ZERO_VALUE))
					{
						ProductGroup.defaultProductGroup = new ProductGroup();
						ProductGroup.defaultProductGroup.isDefault = true;
						ProductGroup.defaultProductGroup.name = "Default";
						ProductGroup.defaultProductGroup.setDefaultTax(Tax.getByCode("UR"));
						ProductGroup.defaultProductGroup.deleted = false;
						ProductGroup.defaultProductGroup.galileoId = "999";
						// defaultProductGroup.isExpense = new Boolean(false);
						ProductGroup.defaultProductGroup.paidInvoice = true;
						ProductGroup.defaultProductGroup.modified = true;
						ProductGroup.defaultProductGroup.optCodeProposal = "L";
						ProductGroup.defaultProductGroup.priceProposal = 0d;
						ProductGroup.defaultProductGroup.quantityProposal = 1;
						ProductGroup.defaultProductGroup.shortname = "Default";
						ProductGroup.defaultProductGroup.timestamp = new Timestamp(new Date().getTime());
						ProductGroup.defaultProductGroup.type = ProductGroup.TYPE_INCOME;
						ProductGroup.defaultProductGroup.withdraw = false;
						ProductGroup.defaultProductGroup.setForeignCurrency(ForeignCurrency.getDefaultCurrency());
						ProductGroup.defaultProductGroup.store();
						ProductGroup.put(ProductGroup.defaultProductGroup);
					}
					else
					{
						ProductGroup.defaultProductGroup = (ProductGroup) ProductGroup.galileoIdIndex
										.get(ProductGroup.defaultProductGroup.galileoId);
						ProductGroup.defaultProductGroup.isDefault = true;
						ProductGroup.defaultProductGroup.store();
						ProductGroup.galileoIdIndex.put(ProductGroup.defaultProductGroup.getId(),
										ProductGroup.defaultProductGroup);
						ProductGroup.records.put(ProductGroup.defaultProductGroup.getId(),
										ProductGroup.defaultProductGroup);
					}
				}
			}
		}
		return ProductGroup.defaultProductGroup;
	}
	
	public static ProductGroup[] getByTax(Tax tax)
	{
		Enumeration groups = ProductGroup.records.elements();
		ArrayList pg = new ArrayList();
		while (groups.hasMoreElements())
		{
			ProductGroup group = (ProductGroup) groups.nextElement();
			if (group.getDefaultTax().equals(tax))
			{
				pg.add(group);
			}
		}
		return (ProductGroup[]) pg.toArray(new ProductGroup[0]);
	}
	
	public static ProductGroup[] getAll(boolean deletedToo)
	{
		Enumeration groups = ProductGroup.records.elements();
		ArrayList pg = new ArrayList();
		while (groups.hasMoreElements())
		{
			ProductGroup group = (ProductGroup) groups.nextElement();
			if (!group.deleted || deletedToo)
			{
				pg.add(group);
			}
		}
		return (ProductGroup[]) pg.toArray(new ProductGroup[0]);
	}
	
	private static void clearData()
	{
		ProductGroup.records.clear();
		ProductGroup.galileoIdIndex.clear();
	}
	
	private static void put(ProductGroup group)
	{
		ProductGroup.records.put(group.getId(), group);
		ProductGroup.galileoIdIndex.put(group.galileoId, group);
	}
	
	public static Element writeXMLRecords(Element root)
	{
		Element table = Database.getTemporary().getTable("product-group"); //$NON-NLS-1$
		if (table == null)
		{
			table = new Element("table"); //$NON-NLS-1$
			table.setAttribute("name", "product-group"); //$NON-NLS-1$ //$NON-NLS-2$
			root.addContent(table);
		}
		
		Enumeration entries = ProductGroup.records.elements();
		while (entries.hasMoreElements())
		{
			ProductGroup productGroup = (ProductGroup) entries.nextElement();
			Element record = productGroup.getJDOMRecordAttributes();
			table.addContent(record);
		}
		return root;
	}
	
	@Override
	protected Element getJDOMRecordAttributes()
	{
		Element record = super.getJDOMRecordAttributes();
		
		Element dg = new Element("field"); //$NON-NLS-1$
		dg.setAttribute("name", "is-default"); //$NON-NLS-1$ //$NON-NLS-2$
		dg.setAttribute("value", new Boolean(this.isDefault).toString()); //$NON-NLS-1$
		record.addContent(dg);
		
		Element gi = new Element("field"); //$NON-NLS-1$
		gi.setAttribute("name", "galileo-id"); //$NON-NLS-1$ //$NON-NLS-2$
		gi.setAttribute("value", this.galileoId); //$NON-NLS-1$
		record.addContent(gi);
		
		Element sn = new Element("field"); //$NON-NLS-1$
		sn.setAttribute("name", "shortname"); //$NON-NLS-1$ //$NON-NLS-2$
		sn.setAttribute("value", this.shortname); //$NON-NLS-1$
		record.addContent(sn);
		
		Element nm = new Element("field"); //$NON-NLS-1$
		nm.setAttribute("name", "name"); //$NON-NLS-1$ //$NON-NLS-2$
		nm.setAttribute("value", this.name); //$NON-NLS-1$
		record.addContent(nm);
		
		Element qt = new Element("field"); //$NON-NLS-1$
		qt.setAttribute("name", "quantity"); //$NON-NLS-1$ //$NON-NLS-2$
		qt.setAttribute("value", Double.toString(this.quantityProposal)); //$NON-NLS-1$
		record.addContent(qt);
		
		Element pc = new Element("field"); //$NON-NLS-1$
		pc.setAttribute("name", "price"); //$NON-NLS-1$ //$NON-NLS-2$
		pc.setAttribute("value", Double.toString(this.priceProposal)); //$NON-NLS-1$
		record.addContent(pc);
		
		Element oc = new Element("field"); //$NON-NLS-1$
		oc.setAttribute("name", "opt-code"); //$NON-NLS-1$ //$NON-NLS-2$
		oc.setAttribute("value", this.optCodeProposal); //$NON-NLS-1$
		record.addContent(oc);
		
		Element ac = new Element("field"); //$NON-NLS-1$
		ac.setAttribute("name", "account"); //$NON-NLS-1$ //$NON-NLS-2$
		ac.setAttribute("value", this.account); //$NON-NLS-1$
		record.addContent(ac);
		
		Element ii = new Element("field"); //$NON-NLS-1$
		ii.setAttribute("name", "paid-invoice"); //$NON-NLS-1$ //$NON-NLS-2$
		ii.setAttribute("value", new Boolean(this.paidInvoice).toString()); //$NON-NLS-1$
		record.addContent(ii);
		
		//		Element ie = new Element("field"); //$NON-NLS-1$
		//		ie.setAttribute("name", "is-expense"); //$NON-NLS-1$ //$NON-NLS-2$
		//		ie.setAttribute("value", isExpense.toString()); //$NON-NLS-1$
		// record.addContent(ie);
		//
		Element mo = new Element("field"); //$NON-NLS-1$
		mo.setAttribute("name", "modified"); //$NON-NLS-1$ //$NON-NLS-2$
		mo.setAttribute("value", new Boolean(this.modified).toString()); //$NON-NLS-1$
		record.addContent(mo);
		
		Element ty = new Element("field"); //$NON-NLS-1$
		ty.setAttribute("name", "type"); //$NON-NLS-1$ //$NON-NLS-2$
		ty.setAttribute("value", Integer.toString(this.type)); //$NON-NLS-1$
		record.addContent(ty);
		
		Element tx = new Element("field"); //$NON-NLS-1$
		tx.setAttribute("name", "tax"); //$NON-NLS-1$ //$NON-NLS-2$
		tx.setAttribute("value", this.getDefaultTaxId().toString()); //$NON-NLS-1$
		record.addContent(tx);
		// Build 76
		Element ei = new Element("field"); //$NON-NLS-1$
		ei.setAttribute("name", "export-id"); //$NON-NLS-1$ //$NON-NLS-2$
		ei.setAttribute("value", this.exportId); //$NON-NLS-1$
		record.addContent(ei);
		// Build 76
		// 10231
		Element wd = new Element("field"); //$NON-NLS-1$
		wd.setAttribute("name", "withdraw"); //$NON-NLS-1$ //$NON-NLS-2$
		wd.setAttribute("value", new Boolean(this.withdraw).toString()); //$NON-NLS-1$
		record.addContent(wd);
		// 10231
		Element fc = new Element("field"); //$NON-NLS-1$
		fc.setAttribute("name", "foreign-currency-id"); //$NON-NLS-1$ //$NON-NLS-2$
		if (this.foreignCurrency == null || this.foreignCurrency.getId() == null)
			fc.setAttribute("value", ForeignCurrency.getDefaultCurrency().getId().toString()); //$NON-NLS-1$
		else
			fc.setAttribute("value", this.getForeignCurrency().getId().toString()); //$NON-NLS-1$
			
		record.addContent(fc);
		Element eb = new Element("field"); //$NON-NLS-1$
		eb.setAttribute("name", "ebook"); //$NON-NLS-1$ //$NON-NLS-2$
		eb.setAttribute("value", new Boolean(this.ebook).toString()); //$NON-NLS-1$
		record.addContent(eb);
		
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
			if (field.getAttributeValue("name").equals("is-default")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.isDefault = new Boolean(field.getAttributeValue("value")).booleanValue(); //$NON-NLS-1$
			}
			if (field.getAttributeValue("name").equals("galileo-id")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.galileoId = field.getAttributeValue("value"); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("shortname")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.shortname = field.getAttributeValue("value"); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("name")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.name = field.getAttributeValue("value"); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("quantity")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.quantityProposal = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("price")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.priceProposal = XMLLoader.getDouble(field.getAttributeValue("value")); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("opt-code")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.optCodeProposal = field.getAttributeValue("value"); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("account")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.account = field.getAttributeValue("value"); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("paid-invoice")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.paidInvoice = new Boolean(field.getAttributeValue("value")).booleanValue(); //$NON-NLS-1$
			}
			//			else if (field.getAttributeValue("name").equals("is-expense")) { //$NON-NLS-1$ //$NON-NLS-2$
			//				isExpense = new Boolean(field.getAttributeValue("value")); //$NON-NLS-1$
			// }
			else if (field.getAttributeValue("name").equals("modified")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.modified = new Boolean(field.getAttributeValue("value")).booleanValue(); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("type")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.type = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("tax")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.setDefaultTax(Tax.getById(new Long(XMLLoader.getLong(field.getAttributeValue("value"))))); //$NON-NLS-1$
			}
			// Build 76
			else if (field.getAttributeValue("name").equals("export-id")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.exportId = field.getAttributeValue("value"); //$NON-NLS-1$
			}
			// Build 76
			// 10231
			else if (field.getAttributeValue("name").equals("withdraw")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.withdraw = new Boolean(field.getAttributeValue("value")).booleanValue(); //$NON-NLS-1$
			}
			// 10231
			else if (field.getAttributeValue("name").equals("foreign-currency-id")) //$NON-NLS-1$ //$NON-NLS-2$
			{
				this.setForeignCurrency(ForeignCurrency.getById(new Long(XMLLoader.getLong(field
								.getAttributeValue("value"))))); //$NON-NLS-1$
				if (this.foreignCurrency == null || this.foreignCurrency.getId() == null)
				{
					this.setForeignCurrency(ForeignCurrency.getDefaultCurrency());
				}
			}
			else if (field.getAttributeValue("name").equals("ebook")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.ebook = new Boolean(field.getAttributeValue("value")).booleanValue(); //$NON-NLS-1$
			}
		}
	}
	
	public static void readXML()
	{
		ProductGroup.clearData();
		Element[] elements = Database.getTemporary().getRecords("product-group"); //$NON-NLS-1$
		for (Element element : elements)
		{
			ProductGroup productGroup = new ProductGroup();
			productGroup.setData(element);
			ProductGroup.put(productGroup);
		}
	}
	
	private static ProductGroup defaultProductGroup;
	private static ProductGroup paidInvoiceProductGroup;
	private static Hashtable records = new Hashtable();
	private static Hashtable galileoIdIndex = new Hashtable();
	
	public static final String NAME_DEFAULT = ""; //$NON-NLS-1$
	public static final String SHORT_NAME_DEFAULT = ""; //$NON-NLS-1$
	public static final String GALILEO_ID_DEFAULT = ""; //$NON-NLS-1$
	public static final int QUANTITY_DEFAULT = 0;
	public static final double PRICE_DEFAULT = 0.0d;
	public static final String OPT_CODE_DEFAULT = ""; //$NON-NLS-1$
	public static final boolean PAID_INVOICE_DEFAULT = false;
	// public static final Boolean IS_EXPENSE_DEFAULT = new Boolean(false);
	public static final boolean MODIFIED = false;
	public static final boolean DEFAULT_GROUP = false;
	// ProductGroup Types
	public static final int TYPE_INCOME = 0;
	public static final int TYPE_NOT_INCOME = 1;
	public static final int TYPE_EXPENSE = 2;
	public static final int TYPE_INPUT = 3;
	public static final int TYPE_WITHDRAW = 4;
}
