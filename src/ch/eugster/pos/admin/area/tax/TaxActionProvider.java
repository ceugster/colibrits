/*
 * Created on 28.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.tax;

import java.text.NumberFormat;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.events.SelectionEvent;

import ch.eugster.pos.Constants;
import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.area.tax.wizard.TaxRateNewCurrentTaxWizard;
import ch.eugster.pos.admin.area.tax.wizard.TaxRateWizard;
import ch.eugster.pos.admin.area.tax.wizard.TaxTypeWizard;
import ch.eugster.pos.admin.gui.container.ActionProvider;
import ch.eugster.pos.admin.gui.container.SashForm;
import ch.eugster.pos.admin.model.TreeContentProvider;
import ch.eugster.pos.db.CurrentTax;
import ch.eugster.pos.db.PaymentType;
import ch.eugster.pos.db.PaymentTypeGroup;
import ch.eugster.pos.db.Table;
import ch.eugster.pos.db.Tax;
import ch.eugster.pos.db.TaxRate;
import ch.eugster.pos.db.TaxType;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class TaxActionProvider extends ActionProvider
{
	
	/**
	 * 
	 */
	public TaxActionProvider(SashForm parent)
	{
		super(parent);
	}
	
	public void addMenuItems(StructuredViewer viewer)
	{
		IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
		if (sel.size() > 1)
		{
			this.createMenuItem(
							Messages.getString("TaxActionProvider.Auswahl_l_u00F6schen_1"), Constants.REMOVE_SELECTION); //$NON-NLS-1$
		}
		else if (sel.getFirstElement() instanceof TaxRate)
		{
			TaxRate rate = (TaxRate) sel.getFirstElement();
			if (TaxRate.isIdFieldAutoincrement(TaxRate.class))
				this
								.createMenuItem(
												Messages
																.getString("TaxActionProvider.Neue_Gruppe_hinzuf_u00FCgen..._2"), Constants.ADD_WIZARD); //$NON-NLS-1$
			if (TaxRate.isIdFieldAutoincrement(TaxRate.class))
				this
								.createMenuItem(
												Messages
																.getString("TaxActionProvider.Neuen_Mehrwertsteuersatz_hinzuf_u00FCgen..._3"), Constants.ADD_CURRENT_TAX_WIZARD); //$NON-NLS-1$
			if (TaxRate.isIdFieldAutoincrement(TaxRate.class)) this.createMenuSeparator();
			this
							.createMenuItem(
											Messages.getString("TaxActionProvider.Gruppe__4") + rate.name + Messages.getString("TaxActionProvider._l_u00F6schen_5"), Constants.REMOVE_ITEM); //$NON-NLS-1$ //$NON-NLS-2$
			this
							.createMenuItem(
											Messages
															.getString("TaxActionProvider.Nicht_aktive_S_u00E4tze_l_u00F6schen_6"), Constants.REMOVE_CHILDREN); //$NON-NLS-1$
		}
		else if (sel.getFirstElement() instanceof CurrentTax)
		{
			CurrentTax currentTax = (CurrentTax) sel.getFirstElement();
			if (!currentTax.getTax().getCurrentTaxId().equals(currentTax.getId()))
			{
				String percentage = NumberFormat.getPercentInstance().format(currentTax.percentage / 100);
				this
								.createMenuItem(
												Messages.getString("TaxActionProvider.Mehrwertsteuersatz__7") + percentage + Messages.getString("TaxActionProvider._l_u00F6schen_8"), Constants.REMOVE_ITEM); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		else if (sel.getFirstElement() instanceof Tax)
		{
			if (TaxRate.isIdFieldAutoincrement(TaxRate.class))
				this
								.createMenuItem(
												Messages
																.getString("TaxActionProvider.Neuen_Mehrwertsteuersatz_hinzuf_u00FCgen..._9"), Constants.ADD_CHILD); //$NON-NLS-1$
			this
							.createMenuItem(
											Messages
															.getString("TaxActionProvider.Nicht_aktive_Mehrwertsteuers_u00E4tze_l_u00F6schen_10"), Constants.REMOVE_CHILDREN); //$NON-NLS-1$
		}
		else if (sel.getFirstElement() instanceof TaxType)
		{
			TaxType type = (TaxType) sel.getFirstElement();
			if (TaxRate.isIdFieldAutoincrement(TaxRate.class))
				this
								.createMenuItem(
												Messages
																.getString("TaxActionProvider.Neue_Mehrwertsteuerart_hinzuf_u00FCgen..._11"), Constants.ADD_WIZARD); //$NON-NLS-1$
			if (TaxRate.isIdFieldAutoincrement(TaxRate.class)) this.createMenuSeparator();
			this
							.createMenuItem(
											Messages.getString("TaxActionProvider.Mehrwertsteuerart__12") + type.name + Messages.getString("TaxActionProvider._l_u00F6schen_13"), Constants.REMOVE_ITEM); //$NON-NLS-1$ //$NON-NLS-2$
			this
							.createMenuItem(
											Messages
															.getString("TaxActionProvider.Nicht_aktive_S_u00E4tze_l_u00F6schen_14"), Constants.REMOVE_CHILDREN); //$NON-NLS-1$
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
			if (table instanceof TaxRate)
			{
				if (e.widget.getData().equals(Constants.ADD_WIZARD))
				{
					TaxRate rate = new TaxRate();
					WizardDialog dialog = new WizardDialog(this.parent.getShell(), new TaxRateWizard(rate));
					if (dialog.open() == 0)
					{
						this.parent.getViewer().setInput(
										((TreeContentProvider) this.parent.getViewer().getContentProvider())
														.getElements(null));
						this.parent.getViewer().setSelection(new StructuredSelection(rate));
					}
				}
				else if (e.widget.getData().equals(Constants.ADD_CURRENT_TAX_WIZARD))
				{
					WizardDialog dialog = new WizardDialog(this.parent.getShell(), new TaxRateNewCurrentTaxWizard(
									(TaxRate) table));
					if (dialog.open() == 0)
					{
						((TreeViewer) this.parent.getViewer()).expandToLevel(table, 2);
					}
				}
				else if (e.widget.getData().equals(Constants.REMOVE_ITEM))
				{
					this.parent.delete();
				}
				else if (e.widget.getData().equals(Constants.REMOVE_CHILDREN))
				{
					MessageDialog dialog = new MessageDialog(
									this.parent.getShell(),
									Messages
													.getString("TaxActionProvider.Zugeh_u00F6rige_Mehrwertsteuern_l_u00F6schen_16"), //$NON-NLS-1$
									MessageDialog.getImage(MessageDialog.DLG_IMG_QUESTION),
									Messages.getString("TaxActionProvider.Sollen_die_zur_Gruppe__17") + ((TaxRate) table).name + Messages.getString("TaxActionProvider._zugeh_u00F6rigen_Mehrwertsteuern_gel_u00F6scht_werden__18"), //$NON-NLS-1$ //$NON-NLS-2$
									MessageDialog.QUESTION, ActionProvider.yesNoButtonLabels, 0);
					if (dialog.open() == 0)
					{
						TaxRate rate = (TaxRate) table;
						Tax[] children = Tax.selectByRateId(rate.getId(), false);
						for (int i = 0; i < children.length; i++)
						{
							CurrentTax[] taxChildren = CurrentTax.selectByTax(children[i], false);
							for (int j = 0; j < taxChildren.length; j++)
							{
								taxChildren[j].delete();
								this.parent.removeElement(taxChildren[j]);
							}
						}
					}
				}
			}
			else if (table instanceof TaxType)
			{
				if (e.widget.getData().equals(Constants.ADD_WIZARD))
				{
					TaxType type = new TaxType();
					WizardDialog dialog = new WizardDialog(this.parent.getShell(), new TaxTypeWizard(type));
					if (dialog.open() == 0)
					{
						this.parent.getViewer().setInput(
										((TreeContentProvider) this.parent.getViewer().getContentProvider())
														.getElements(null));
						this.parent.getViewer().setSelection(new StructuredSelection(type));
					}
				}
				else if (e.widget.getData().equals(Constants.REMOVE_ITEM))
				{
					this.parent.delete();
				}
				else if (e.widget.getData().equals(Constants.REMOVE_CHILDREN))
				{
					MessageDialog dialog = new MessageDialog(
									this.parent.getShell(),
									Messages
													.getString("TaxActionProvider.Zugeh_u00F6rige_Mehrwertsteuern_l_u00F6schen_21"), //$NON-NLS-1$
									MessageDialog.getImage(MessageDialog.DLG_IMG_QUESTION),
									Messages.getString("TaxActionProvider.Sollen_die_zur_Gruppe__22") + ((TaxType) table).name + Messages.getString("TaxActionProvider._zugeh_u00F6rigen_Mehrwertsteuern_gel_u00F6scht_werden__23"), //$NON-NLS-1$ //$NON-NLS-2$
									MessageDialog.QUESTION, ActionProvider.yesNoButtonLabels, 0);
					if (dialog.open() == 0)
					{
						TaxType type = (TaxType) table;
						Tax[] children = Tax.selectByTypeId(type.getId(), false);
						for (int i = 0; i < children.length; i++)
						{
							CurrentTax[] taxChildren = CurrentTax.selectByTax(children[i], false);
							for (int j = 0; j < taxChildren.length; j++)
							{
								taxChildren[j].delete();
								this.parent.removeElement(taxChildren[j]);
							}
						}
					}
				}
			}
			else if (table instanceof CurrentTax)
			{
				if (e.widget.getData().equals(Constants.REMOVE_ITEM))
				{
					this.parent.delete();
				}
			}
			else if (table instanceof Tax)
			{
				if (e.widget.getData().equals(Constants.ADD_CHILD))
				{
					CurrentTax currentTax = new CurrentTax();
					currentTax.setTax((Tax) table);
					this.selectPage(currentTax);
				}
				else if (e.widget.getData().equals(Constants.REMOVE_CHILDREN))
				{
					MessageDialog dialog = new MessageDialog(
									this.parent.getShell(),
									Messages
													.getString("TaxActionProvider.Zugeh_u00F6rige_Mehrwertsteuers_u00E4tze_l_u00F6schen_19"), //$NON-NLS-1$
									MessageDialog.getImage(MessageDialog.DLG_IMG_QUESTION),
									Messages
													.getString("TaxActionProvider.Sollen_die_der_gew_u00E4hlten_Mehrwertsteuer_zugeh_u00F6rigen,_nicht_aktuellen_Mehrwertsteuers_u00E4tze_gel_u00F6scht_werden__20"), //$NON-NLS-1$
									MessageDialog.QUESTION, ActionProvider.yesNoButtonLabels, 0);
					if (dialog.open() == 0)
					{
						Tax tax = (Tax) table;
						CurrentTax[] children = CurrentTax.selectByTax(tax, false);
						for (int i = 0; i < children.length; i++)
						{
							if (!children[i].getId().equals(tax.getCurrentTaxId()))
							{
								children[i].delete();
								this.parent.removeElement(children[i]);
							}
						}
					}
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
						.getString("TaxActionProvider.Der_Datensatz_konnte_nicht_gespeichert_werden._Bitte__u00FCberpr_u00FCfen_Sie,_ob_der_eingegebene_Code_eindeutig_ist._25"); //$NON-NLS-1$
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
