/*
 * Created on 27.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.events;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.client.model.ReceiptModel;
import ch.eugster.pos.db.CustomKey;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.Key;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.product.ProductServer;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ProductGroupAction extends PositionChangeAction implements ReceiptChangeListener
{
	
	public static final long serialVersionUID = 0l;
	
	private boolean isInput;
	private boolean isWithdraw;
	private ForeignCurrency foreignCurrency;
	
	/**
	 * @param context
	 * @param key
	 */
	public ProductGroupAction(UserPanel context, Key key)
	{
		super(context, key);
		this.init(key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 */
	public ProductGroupAction(UserPanel context, Key key, String name)
	{
		super(context, key, name);
		this.init(key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 * @param icon
	 */
	public ProductGroupAction(UserPanel context, Key key, String name, Icon icon)
	{
		super(context, key, name, icon);
		this.init(key);
	}
	
	/**
	 * @param key
	 */
	private void init(Key key)
	{
		ReceiptModel.getInstance().getPositionTableModel().addReceiptChangeListener(this);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		ProductGroup pg = (ProductGroup) this.getValue(Action.POS_KEY_PRODUCT_GROUP);
		if (this.foreignCurrency == null)
		{
			this.isInput = pg.type == ProductGroup.TYPE_INPUT;
			this.isWithdraw = pg.type == ProductGroup.TYPE_WITHDRAW;
			this.foreignCurrency = pg.getForeignCurrency();
		}
		
		CustomKey ck = (CustomKey) this.key;
		if (ck.value.doubleValue() == 0d)
		{
			this.putValue(Action.POS_KEY_PRICE, new Double(this.context.getNumericBlock().getValueAsDouble()));
		}
		else
		{
			this.putValue(Action.POS_KEY_PRICE, ck.value);
		}
		// 10124
		super.actionPerformed(e);
		if (this.context.getReceiptModel().getPositionModel().isComplete())
		{
			this.context.getReceiptModel().getPositionTableModel().store(this.context.getReceiptModel().getPositionModel().getPosition());
		}
		
		if (pg.getForeignCurrency() != null && !pg.getForeignCurrency().getId().equals(ForeignCurrency.getDefaultCurrency().getId()))
		{
			if (this.context.getCurrentForeignCurrency() == null || this.context.getCurrentForeignCurrency().getId().equals(ForeignCurrency.getDefaultCurrency().getId()))
			{
				this.context.setCurrentForeignCurrency(pg.getForeignCurrency());
			}
		}
	}
	
	public void receiptChangePerformed(ReceiptChangeEvent e)
	{
		if (this.foreignCurrency == null)
		{
			ProductGroup pg = (ProductGroup) this.getValue(Action.POS_KEY_PRODUCT_GROUP);
			this.isInput = pg.type == ProductGroup.TYPE_INPUT;
			this.isWithdraw = pg.type == ProductGroup.TYPE_WITHDRAW;
			this.foreignCurrency = pg.getForeignCurrency();
		}
		
		this.putValue("enabled", new Boolean(true));
	}
	
	public void modeChangePerformed(ModeChangeEvent e)
	{
		if (this.foreignCurrency == null)
		{
			ProductGroup pg = (ProductGroup) this.getValue(Action.POS_KEY_PRODUCT_GROUP);
			this.isInput = pg.type == ProductGroup.TYPE_INPUT;
			this.isWithdraw = pg.type == ProductGroup.TYPE_WITHDRAW;
			this.foreignCurrency = pg.getForeignCurrency();
		}
		
		if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_LOCK))
		{
			this.putValue("enabled", new Boolean(false)); //$NON-NLS-1$
		}
		else if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_PARKED_RECEIPT_LIST))
		{
			this.putValue("enabled", new Boolean(false)); //$NON-NLS-1$
		}
		else if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_CURRENT_RECEIPT_LIST))
		{
			this.putValue("enabled", new Boolean(false)); //$NON-NLS-1$
		}
		else
		{
			ProductGroup pg = (ProductGroup) this.getValue(Action.POS_KEY_PRODUCT_GROUP);
			this.putValue("enabled", new Boolean(true));
			
			if (this.getValue("enabled") != null && this.getValue("enabled").equals(new Boolean(true)))
			{
				if (ProductServer.isUsed() && ProductServer.getInstance().getUpdate() == 2)
				{
					//
					if (pg == null || pg.type == ProductGroup.TYPE_INCOME && !pg.galileoId.equals(""))
					{
						this.putValue("enabled", new Boolean(this.getKey().command.length() > 0)); //$NON-NLS-1$
					}
					else
					{
						this.putValue("enabled", new Boolean(true)); //$NON-NLS-1$
					}
				}
				else
				{
					this.putValue("enabled", new Boolean(true)); //$NON-NLS-1$
				}
			}
		}
	}
	
	protected boolean isInput()
	{
		return this.isInput;
	}
	
	protected boolean isWithdraw()
	{
		return this.isWithdraw;
	}
}
