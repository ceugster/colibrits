/*
 * Created on 29.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.coin;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.events.SelectionEvent;

import ch.eugster.pos.Constants;
import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.gui.container.ActionProvider;
import ch.eugster.pos.admin.gui.container.SashForm;
import ch.eugster.pos.db.Coin;
import ch.eugster.pos.db.ForeignCurrency;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class CoinActionProvider extends ActionProvider
{
	
	/**
	 * @param parent
	 */
	public CoinActionProvider(SashForm parent)
	{
		super(parent);
	}
	
	public void addMenuItems(StructuredViewer viewer)
	{
		IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
		if (sel.size() > 1)
		{
			this
							.createMenuItem(
											Messages.getString("CoinActionProvider.Auswahl_l_u00F6schen_1"), Constants.REMOVE_SELECTION, true); //$NON-NLS-1$
		}
		else if (sel.getFirstElement() instanceof ForeignCurrency)
		{
			this
							.createMenuItem(
											Messages.getString("CoinActionProvider.Neuen_Wert_hinzuf_u00FCgen_2"), Constants.ADD_CHILD, true); //$NON-NLS-1$
		}
		else if (sel.getFirstElement() instanceof Coin)
		{
			Coin coin = (Coin) sel.getFirstElement();
			this
							.createMenuItem(
											Messages.getString("CoinActionProvider.Wert__3") + this.getName(coin) + Messages.getString("CoinActionProvider._l_u00F6schen_4"), Constants.REMOVE_ITEM, true); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.gui.ActionProvider#getName(java.lang.Object)
	 */
	protected String getName(Object element)
	{
		String result = Messages.getString("CoinActionProvider._5"); //$NON-NLS-1$
		if (element instanceof ForeignCurrency)
		{
			result = ((ForeignCurrency) element).name;
		}
		else if (element instanceof Coin)
		{
			result = Double.toString(((Coin) element).value);
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.gui.ActionProvider#getErrorMessage()
	 */
	protected String getErrorMessage()
	{
		return this.errorMessage;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.gui.ActionProvider#isChildOf(java.lang.Object,
	 * java.lang.Object)
	 */
	public boolean isChildOf(Object child, Object parent)
	{
		if (child instanceof Coin)
		{
			if (parent instanceof ForeignCurrency)
			{
				ForeignCurrency currency = (ForeignCurrency) parent;
				if (((Coin) child).getForeignCurrency().getId().equals(currency.getId()))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.admin.gui.IActionProvider#performMenuSelection(org.eclipse
	 * .swt.events.SelectionEvent)
	 */
	public void performMenuSelection(SelectionEvent e)
	{
		IStructuredSelection sel = (IStructuredSelection) this.parent.getViewer().getSelection();
		if (sel.size() > 1)
		{
			this.parent.delete();
		}
		else if (sel.getFirstElement() instanceof ForeignCurrency)
		{
			ForeignCurrency currency = (ForeignCurrency) sel.getFirstElement();
			if (e.widget.getData().equals(Constants.ADD_CHILD))
			{
				Coin coin = new Coin();
				coin.setForeignCurrency(currency);
				this.selectPage(coin);
			}
			else if (e.widget.getData().equals(Constants.REMOVE_CHILDREN))
			{
				MessageDialog dialog = new MessageDialog(
								this.parent.getShell(),
								"Münzen löschen", //$NON-NLS-1$
								MessageDialog.getImage(MessageDialog.DLG_IMG_QUESTION),
								"Sollen die ausgewählten zur Währung " + currency.code + " " + currency.name + "Münzen gelöscht werden?", //$NON-NLS-1$ //$NON-NLS-2$
								MessageDialog.QUESTION, ActionProvider.yesNoButtonLabels, 0);
				if (dialog.open() == 0)
				{
					Coin[] children = Coin.selectByForeignCurrency(currency);
					for (int i = 0; i < children.length; i++)
					{
						children[i].delete();
						this.parent.removeElement(children[i]);
					}
				}
			}
		}
		else if (sel.getFirstElement() instanceof Coin)
		{
			if (e.widget.getData().equals(Constants.REMOVE_ITEM))
			{
				this.parent.delete();
			}
		}
	}
	
}
