/*
 * Created on 28.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.payment;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionEvent;

import ch.eugster.pos.Constants;
import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.gui.container.ActionProvider;
import ch.eugster.pos.admin.gui.container.SashForm;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.PaymentType;
import ch.eugster.pos.db.PaymentTypeGroup;
import ch.eugster.pos.db.Table;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class PaymentTypeActionProvider extends ActionProvider
{
	
	/**
	 * 
	 */
	public PaymentTypeActionProvider(SashForm parent)
	{
		super(parent);
	}
	
	public void addMenuItems(StructuredViewer viewer)
	{
		IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
		if (sel.size() == 1)
		{
			if (sel.getFirstElement() instanceof PaymentType)
			{
				PaymentType paymentType = (PaymentType) sel.getFirstElement();
				this
								.createMenuItem(
												Messages.getString("PaymentTypeActionProvider.Zahlungsart__2") + paymentType.name + Messages.getString("PaymentTypeActionProvider._l_u00F6schen_3"), Constants.REMOVE_ITEM, !paymentType.cash); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else if (sel.getFirstElement() instanceof PaymentTypeGroup)
			{
				PaymentTypeGroup group = (PaymentTypeGroup) sel.getFirstElement();
				this
								.createMenuItem(
												Messages
																.getString("PaymentTypeActionProvider.Neue_Zahlungsart_hinzuf_u00FCgen_4"), Constants.ADD_CHILD, true); //$NON-NLS-1$
				this
								.createMenuItem(
												Messages
																.getString("PaymentTypeActionProvider.Zugeh_u00F6rige_Zahlungsarten_l_u00F6schen_5"), Constants.REMOVE_CHILDREN, true); //$NON-NLS-1$
				this.createMenuSeparator();
				this
								.createMenuItem(
												Messages
																.getString("PaymentTypeActionProvider.Neue_Gruppe_hinzuf_u00FCgen_6"), Constants.ADD_ITEM, true); //$NON-NLS-1$
				if (!group.getId().equals(new Long(1l)))
					this
									.createMenuItem(
													Messages.getString("PaymentTypeActionProvider.Gruppe__7") + group.name + Messages.getString("PaymentTypeActionProvider._l_u00F6schen_8"), Constants.REMOVE_ITEM, true); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	public void performMenuSelection(SelectionEvent e)
	{
		IStructuredSelection sel = (IStructuredSelection) this.parent.getViewer().getSelection();
		if (sel.size() > 1)
		{
			this.parent.delete();
		}
		else if (sel.getFirstElement() instanceof Table)
		{
			Table table = (Table) sel.getFirstElement();
			if (table instanceof PaymentTypeGroup)
			{
				if (e.widget.getData().equals(Constants.ADD_CHILD))
				{
					PaymentType type = new PaymentType();
					type.setPaymentTypeGroup((PaymentTypeGroup) table);
					type.setForeignCurrency(ForeignCurrency.getDefaultCurrency());
					this.selectPage(type);
				}
				else if (e.widget.getData().equals(Constants.REMOVE_CHILDREN))
				{
					MessageDialog dialog = new MessageDialog(
									this.parent.getShell(),
									Messages
													.getString("PaymentTypeActionProvider.Zugeh_u00F6rige_Zahlungsarten_l_u00F6schen_9"), //$NON-NLS-1$
									MessageDialog.getImage(MessageDialog.DLG_IMG_QUESTION),
									Messages.getString("PaymentTypeActionProvider.Sollen_die_zur_Gruppe__10") + ((PaymentTypeGroup) table).name + Messages.getString("PaymentTypeActionProvider._zugeh_u00F6rigen_Zahlungsarten_gel_u00F6scht_werden__11"), //$NON-NLS-1$ //$NON-NLS-2$
									MessageDialog.QUESTION, ActionProvider.yesNoButtonLabels, 0);
					if (dialog.open() == 0)
					{
						PaymentTypeGroup group = (PaymentTypeGroup) table;
						PaymentType[] children = PaymentType.selectByGroup(group, false);
						for (int i = 0; i < children.length; i++)
						{
							/*
							 * Achtung: PaymentTypeBack und PaymentTypeCash
							 * dürfen auf keinen Fall gelöscht werden!
							 */
							// 10226
							// if
							// (!children[i].getId().equals(PaymentType.BACK_ID)
							// &&
							// !children[i].getId().equals(PaymentType.CASH_ID))
							// {
							// children[i].delete();
							// parent.removeElement(children[i]);
							// }
							if (!children[i].getId().equals(PaymentType.CASH_ID))
							{
								if (!children[i].cash)
								{
									children[i].delete();
									this.parent.removeElement(children[i]);
								}
							}
							// 10226
						}
					}
				}
				else if (e.widget.getData().equals(Constants.ADD_ITEM))
				{
					((TreeViewer) this.parent.getViewer()).setSelection(null);
					this.selectPage(table);
					this.parent.prepareForNewItem(new StructuredSelection(table));
				}
				else if (e.widget.getData().equals(Constants.REMOVE_ITEM))
				{
					this.parent.delete();
				}
			}
			else if (table instanceof PaymentType)
			{
				if (e.widget.getData().equals(Constants.ADD_ITEM))
				{
					this.selectPage(table);
				}
				else if (e.widget.getData().equals(Constants.REMOVE_ITEM))
				{
					this.parent.delete();
				}
			}
		}
	}
	
	protected String getName(Object element)
	{
		String result = ""; //$NON-NLS-1$
		if (element instanceof PaymentType)
		{
			result = ((PaymentType) element).name;
		}
		else if (element instanceof PaymentTypeGroup)
		{
			result = ((PaymentTypeGroup) element).name;
		}
		return result;
	}
	
	protected String getErrorMessage()
	{
		return Messages
						.getString("PaymentTypeActionProvider.Der_Datensatz_konnte_nicht_gespeichert_werden._Bitte__u00FCberpr_u00FCfen_Sie,_ob_der_eingegebene_Code_eindeutig_ist._13"); //$NON-NLS-1$
	}
	
	public boolean isChildOf(Object child, Object parent)
	{
		boolean result = false;
		if (child instanceof PaymentType)
		{
			if (parent instanceof PaymentTypeGroup)
			{
				result = ((PaymentType) child).getPaymentTypeGroup().equals(parent);
			}
		}
		return result;
	}
	
}
