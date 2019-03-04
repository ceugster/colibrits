/*
 * Created on 26.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.events;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import ch.eugster.pos.client.gui.Frame;
import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.client.model.ReceiptModel;
import ch.eugster.pos.db.Key;
import ch.eugster.pos.db.ProductGroup;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PositionChangeAction extends ModeDependendAction
{
	
	public static final long serialVersionUID = 0l;
	
	/**
	 * @param context
	 * @param key
	 */
	public PositionChangeAction(UserPanel context, Key key)
	{
		super(context, key);
		this.init(key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 */
	public PositionChangeAction(UserPanel context, Key key, String name)
	{
		super(context, key, name);
		this.init(key);
	}
	
	/**
	 * 
	 * @param context
	 * @param key
	 * @param name
	 * @param icon
	 */
	public PositionChangeAction(UserPanel context, Key key, String name, Icon icon)
	{
		super(context, key, name, icon);
		this.init(key);
	}
	
	/**
	 * @param key
	 */
	private void init(Key key)
	{
		this.addPosEventListener(this.context.getReceiptModel().getPositionModel());
	}
	
	public void actionPerformed(ActionEvent event)
	{
		if (this.context.getReceiptModel().getPositionTableModel().getRowCount() > 0)
		{
			if (ReceiptModel.getInstance().receiptHasInputPosition())
			{
				String msg = "<html>Sie haben eben eine Geldeinlage in die Kasse getippt. Bitte beenden Sie diese Transaktion, bevor Sie eine neue beginnen.";
				MessageDialog.showInformation(Frame.getMainFrame(), "Geldeinlage", msg, MessageDialog.TYPE_INFORMATION);
				this.context.getReceiptModel().getPositionModel().setProductGroup(new ProductGroup());
				this.context.getNumericBlock().moveValue();
				return;
			}
			if (ReceiptModel.getInstance().receiptHasWithdrawPosition())
			{
				String msg = "<html>Sie haben eben eine Geldentnahme in die Kasse getippt. Bitte beenden Sie diese Transaktion, bevor Sie eine neue beginnen.";
				MessageDialog.showInformation(Frame.getMainFrame(), "Geldeinlage", msg, MessageDialog.TYPE_INFORMATION);
				this.context.getReceiptModel().getPositionModel().setProductGroup(new ProductGroup());
				this.context.getNumericBlock().moveValue();
				return;
			}
			else
			{
				if (this.isInput() || this.isWithdraw())
				{
					String what = this.isWithdraw() ? "der Kasse kein Geld entnehmen" : "kein Geld in die Kasse legen";
					String msg = "<html>Sie können " + what + ", <br>solange die laufende Transaktion nicht abgeschlossen ist.</html>";
					MessageDialog.showInformation(Frame.getMainFrame(), "Geldeinlage/Geldentnahme", msg, MessageDialog.TYPE_INFORMATION);
					this.context.getReceiptModel().getPositionModel().setProductGroup(new ProductGroup());
					this.context.getNumericBlock().moveValue();
					return;
				}
			}
		}
		super.actionPerformed(event);
	}
	
	protected boolean isInput()
	{
		return false;
	}
	
	protected boolean isWithdraw()
	{
		return false;
	}
}
