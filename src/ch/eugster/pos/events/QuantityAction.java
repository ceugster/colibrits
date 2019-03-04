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
import ch.eugster.pos.db.Key;
import ch.eugster.pos.db.Position;
import ch.eugster.pos.product.ProductServer;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class QuantityAction extends PositionChangeAction
{
	
	public static final long serialVersionUID = 0l;
	
	/**
	 * @param context
	 * @param key
	 */
	public QuantityAction(UserPanel context, Key key)
	{
		super(context, key);
		this.init(key);
	}
	
	/**
	 * @param context
	 * @param key
	 * @param name
	 */
	public QuantityAction(UserPanel context, Key key, String name)
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
	public QuantityAction(UserPanel context, Key key, String name, Icon icon)
	{
		super(context, key, name, icon);
		this.init(key);
	}
	
	/**
	 * @param key
	 */
	private void init(Key key)
	{
	}
	
	/**
	 * @param actionEvent
	 */
	public void actionPerformed(ActionEvent actionEvent)
	{
		int i = this.context.getNumericBlock().moveQuantity();
		if (i != 0)
		{
			Position p = UserPanel.getCurrent().getReceiptModel().getPositionModel().getPosition();
			if (ProductServer.getInstance().isActive())
			{
				if (p.ordered)
				{
					int usedItems = Position.countOrderedItemsUsed(p.orderId);
					if (i + usedItems > p.getOrderedQuantity())
					{
						MessageDialog.showInformation(Frame.getMainFrame(), "Ungültige Mengeneingabe", "Die eingegebene Menge ist grösser als der Restbestand des abzuholenden Titels.",
										MessageDialog.TYPE_INFORMATION);
						this.putValue(Action.POS_KEY_QUANTITY, new Integer(0));
						
					}
					else
						this.putValue(Action.POS_KEY_QUANTITY, new Integer(i));
				}
				else
					this.putValue(Action.POS_KEY_QUANTITY, new Integer(i));
			}
			else
				this.putValue(Action.POS_KEY_QUANTITY, new Integer(i));
			
			super.actionPerformed(actionEvent);
			
		}
	}
	
	public void modeChangePerformed(ModeChangeEvent e)
	{
		super.modeChangePerformed(e);
		// if (e.getRequestedState(StateChangeEvent.KEY_NUMERIC_BLOCK).equals(
		// StateChangeEvent.VALUE_IS_INTEGER)) {
		//			putValue("enabled", new Boolean(true));			 //$NON-NLS-1$
		// }
		// else {
		//			putValue("enabled", new Boolean(false));			 //$NON-NLS-1$
		// }
	}
	
}
