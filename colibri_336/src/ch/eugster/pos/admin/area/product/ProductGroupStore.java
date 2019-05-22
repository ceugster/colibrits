/*
 * Created on 29.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.product;

import org.eclipse.jface.viewers.IStructuredSelection;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.db.Table;
import ch.eugster.pos.db.Tax;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ProductGroupStore extends PersistentDBStore
{
	
	@Override
	public void initialize()
	{
		this.setElement(ProductGroup.getEmptyInstance());
	}
	
	@Override
	public void initialize(IStructuredSelection selection)
	{
		this.setElement(ProductGroup.getEmptyInstance());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.Store#load()
	 */
	@Override
	protected void load()
	{
		ProductGroup productGroup = (ProductGroup) this.element;
		if (productGroup.isDefault)
		{
			if (productGroup.type != ProductGroup.TYPE_INCOME)
			{
				productGroup.type = ProductGroup.TYPE_INCOME;
				productGroup.store();
			}
		}
		
		this.putDefaultId(ProductGroupFieldEditorPage.KEY_ID, productGroup.getId());
		this.putDefault(ProductGroupFieldEditorPage.KEY_NAME, productGroup.name);
		this.putDefault(ProductGroupFieldEditorPage.KEY_SHORT_NAME, productGroup.shortname);
		this.putDefault(ProductGroupFieldEditorPage.KEY_DEFAULT_GROUP, new Boolean(productGroup.isDefault));
		this.putDefault(ProductGroupFieldEditorPage.KEY_CURRENCY,
						productGroup.getForeignCurrency() != null ? productGroup.getForeignCurrency() : ForeignCurrency
										.getDefaultCurrency());
		// 10215
		this.putDefault(ProductGroupFieldEditorPage.KEY_PAID_INVOICE, new Boolean(productGroup.paidInvoice));
		// 10215
		this.putDefault(ProductGroupFieldEditorPage.KEY_GALILEO_ID, productGroup.galileoId);
		this.putDefault(ProductGroupFieldEditorPage.KEY_QUANTITY, new Integer(productGroup.quantityProposal));
		this.putDefault(ProductGroupFieldEditorPage.KEY_PRICE, new Double(productGroup.priceProposal));
		this.putDefault(ProductGroupFieldEditorPage.KEY_OPT_CODE, productGroup.optCodeProposal);
		this.putDefault(ProductGroupFieldEditorPage.KEY_ACCOUNT, productGroup.account);
		this.putDefault(ProductGroupFieldEditorPage.KEY_TYPE, new Integer(productGroup.type));
		this.putDefault(ProductGroupFieldEditorPage.KEY_MODIFIED, new Boolean(productGroup.modified));
		this.putDefault(ProductGroupFieldEditorPage.KEY_DEFAULT_TAX, productGroup.getDefaultTax() == null ? new Tax()
						: productGroup.getDefaultTax());
		this.putDefault(ProductGroupFieldEditorPage.KEY_EXPORT_ID, productGroup.exportId);
		this.putToDefault(ProductGroupFieldEditorPage.KEY_NAME);
		this.putToDefault(ProductGroupFieldEditorPage.KEY_SHORT_NAME);
		this.putToDefault(ProductGroupFieldEditorPage.KEY_DEFAULT_GROUP);
		this.putToDefault(ProductGroupFieldEditorPage.KEY_GALILEO_ID);
		this.putToDefault(ProductGroupFieldEditorPage.KEY_QUANTITY);
		this.putToDefault(ProductGroupFieldEditorPage.KEY_PRICE);
		this.putToDefault(ProductGroupFieldEditorPage.KEY_OPT_CODE);
		this.putToDefault(ProductGroupFieldEditorPage.KEY_ACCOUNT);
		// 10215
		this.putToDefault(ProductGroupFieldEditorPage.KEY_CURRENCY);
		this.putToDefault(ProductGroupFieldEditorPage.KEY_PAID_INVOICE);
		// 10215
		// setToDefault(ProductGroupFieldEditorPage.KEY_IS_EXPENSE);
		this.putToDefault(ProductGroupFieldEditorPage.KEY_TYPE);
		this.putToDefault(ProductGroupFieldEditorPage.KEY_MODIFIED);
		this.putToDefault(ProductGroupFieldEditorPage.KEY_DEFAULT_TAX);
		this.putToDefault(ProductGroupFieldEditorPage.KEY_EXPORT_ID);
		this.putDefault(ProductGroupFieldEditorPage.KEY_EBOOK, productGroup.isEbook());
		this.setDirty(false);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.Store#load()
	 */
	@Override
	protected void store()
	{
		System.out.println();
		ProductGroup productGroup = (ProductGroup) this.element;
		if (!ProductGroup.isIdFieldAutoincrement(ProductGroup.class))
		{
			productGroup.setId(this.getId(ProductGroupFieldEditorPage.KEY_ID));
		}
		productGroup.name = this.getString(ProductGroupFieldEditorPage.KEY_NAME);
		productGroup.shortname = this.getString(ProductGroupFieldEditorPage.KEY_SHORT_NAME);
		productGroup.isDefault = this.getBoolean(ProductGroupFieldEditorPage.KEY_DEFAULT_GROUP).booleanValue();
		productGroup.galileoId = this.getString(ProductGroupFieldEditorPage.KEY_GALILEO_ID);
		productGroup.quantityProposal = this.getInt(ProductGroupFieldEditorPage.KEY_QUANTITY).intValue();
		productGroup.priceProposal = this.getDouble(ProductGroupFieldEditorPage.KEY_PRICE).doubleValue();
		productGroup.optCodeProposal = this.getString(ProductGroupFieldEditorPage.KEY_OPT_CODE);
		productGroup.account = this.getString(ProductGroupFieldEditorPage.KEY_ACCOUNT);
		productGroup.setForeignCurrency(this.getForeignCurrency(ProductGroupFieldEditorPage.KEY_CURRENCY));
		// 10215
		productGroup.paidInvoice = this.getBoolean(ProductGroupFieldEditorPage.KEY_PAID_INVOICE).booleanValue();
		// 10215
		productGroup.type = this.getInt(ProductGroupFieldEditorPage.KEY_TYPE).intValue();
		productGroup.modified = false;
		productGroup.setDefaultTax(this.getTax(ProductGroupFieldEditorPage.KEY_DEFAULT_TAX));
		productGroup.exportId = this.getString(ProductGroupFieldEditorPage.KEY_EXPORT_ID);
		productGroup.ebook = this.getBoolean(ProductGroupFieldEditorPage.KEY_EBOOK);
		this.setDirty(false);
	}
	
	@Override
	protected String getErrorMessage(DBResult dbResult)
	{
		String result = ""; //$NON-NLS-1$
		if (dbResult.getExternalErrorCode().equals(Messages.getString("ProductGroupStore.S1009_2"))) { //$NON-NLS-1$
		}
		return result;
	}
	
	@Override
	public DBResult save()
	{
		DBResult result = new DBResult(0, "");
		ProductGroup group = (ProductGroup) this.element;
		Long oldId = group.getId();
		
		if (this.getString(ProductGroupFieldEditorPage.KEY_NAME).equals(""))
		{
			result.setErrorCode(-1);
			result.log();
			result.setErrorText("Die Warengruppe hat keine gültige Bezeichnung.");
			result.showMessage();
		}
		else
		{
			boolean valid = false;
			Object tax = this.getValue(ProductGroupFieldEditorPage.KEY_DEFAULT_TAX);
			if (tax != null)
			{
				if (tax instanceof Tax)
				{
					Tax t = (Tax) tax;
					if (t.getId() != null && t.getId().longValue() > 0l) valid = true;
				}
			}
			
			if (!valid)
			{
				result.setErrorCode(-1);
				result.log();
				result.setErrorText("Sie haben keine Mehrwertsteuer ausgewählt.");
				result.showMessage();
			}
			
			if (this.getBoolean(ProductGroupFieldEditorPage.KEY_DEFAULT_GROUP).booleanValue())
			{
				// 10089
				if (this.getString(ProductGroupFieldEditorPage.KEY_GALILEO_ID).equals(""))
				{
					result.setErrorCode(-1);
					result.log();
					result.setErrorText("Die Default-Warengruppe muss eine gültige, dreistellige Galileo-Id haben.");
					result.showMessage();
				}
				// 10089
				// 10138
				if (!this.getInt(ProductGroupFieldEditorPage.KEY_TYPE).equals(new Integer(0)))
				{
					this.setValue(ProductGroupFieldEditorPage.KEY_TYPE, new Integer(0));
					
				}
				// 10138
				if (result.getErrorCode() == 0)
				{
					ProductGroup isDefault = ProductGroup.selectDefaultGroup();
					if (isDefault != null && isDefault.getId() != null
									&& !isDefault.getId().equals(ProductGroup.ZERO_VALUE))
					{
						isDefault.isDefault = false;
						result = isDefault.store();
					}
				}
				// 10215
				if (result.getErrorCode() == 0)
				{
					ProductGroup paidInvoiceGroup = ProductGroup.selectPaidInvoiceGroup();
					if (paidInvoiceGroup != null && paidInvoiceGroup.getId() != null
									&& !paidInvoiceGroup.getId().equals(ProductGroup.ZERO_VALUE))
					{
						paidInvoiceGroup.paidInvoice = false;
						result = paidInvoiceGroup.store();
					}
				}
				// 10215
			}
			
			if (this.getBoolean(ProductGroupFieldEditorPage.KEY_EBOOK).booleanValue())
			{
				if (this.getString(ProductGroupFieldEditorPage.KEY_GALILEO_ID).equals(""))
				{
					result.setErrorCode(-1);
					result.log();
					result.setErrorText("Die eBook-Warengruppe muss eine gültige, dreistellige Galileo-Id haben.");
					result.showMessage();
				}
				if (!this.getInt(ProductGroupFieldEditorPage.KEY_TYPE).equals(new Integer(0)))
				{
					this.setValue(ProductGroupFieldEditorPage.KEY_TYPE, new Integer(0));
					
				}
				if (result.getErrorCode() == 0)
				{
					ProductGroup isEbook = ProductGroup.selectEbookGroup();
					if (isEbook != null && isEbook.getId() != null && !isEbook.getId().equals(ProductGroup.ZERO_VALUE))
					{
						isEbook.ebook = false;
						result = isEbook.store();
					}
				}
			}
		}
		
		if (result.getErrorCode() == 0)
		{
			Database.getStandard().getBroker().beginTransaction();
			this.store();
			result = ((Table) this.element).store();
			if (result.getErrorCode() != 0)
			{
				result.log();
				result.setErrorText(this.getErrorMessage(result));
				result.showMessage();
			}
		}
		if (result.getErrorCode() == 0)
		{
			if (oldId != null)
			{
				if (!oldId.equals(group.getId()))
				{
					ProductGroup.selectById(oldId).delete();
				}
			}
			Database.getStandard().getBroker().commitTransaction();
			
		}
		else
		{
			Database.getStandard().getBroker().abortTransaction();
		}
		return result;
	}
	
	@Override
	public boolean isDeletable()
	{
		return !((ProductGroup) this.getElement()).isDefault;
	}
	
	@Override
	public String getErrorMessage()
	{
		return "Die gewählte Warengruppe darf nicht gelöscht werden.";
	}
	
}
