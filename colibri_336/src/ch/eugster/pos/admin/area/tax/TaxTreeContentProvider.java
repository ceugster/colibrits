/*
 * Created on 26.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.tax;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.model.SimpleLabelProvider;
import ch.eugster.pos.admin.model.TreeContentProvider;
import ch.eugster.pos.db.CurrentTax;
import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.Tax;
import ch.eugster.pos.db.TaxRate;
import ch.eugster.pos.db.TaxType;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class TaxTreeContentProvider extends TreeContentProvider
{
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.
	 * Object)
	 */
	public Object[] getChildren(Object parentElement)
	{
		Object[] result = new Object[0];
		if (parentElement instanceof TaxRate)
		{
			result = Tax.selectByRateId(((TaxRate) parentElement).getId(), false);
		}
		else if (parentElement instanceof TaxType)
		{
			result = Tax.selectByTypeId(((TaxType) parentElement).getId(), false);
		}
		else if (parentElement instanceof Tax)
		{
			result = CurrentTax.selectByTax((Tax) parentElement, false);
		}
		else if (parentElement instanceof CurrentTax)
		{
		}
		else
		{
			if (this.selection.equals(new Integer(0)))
			{
				result = TaxRate.selectAll(false);
			}
			else if (this.selection.equals(new Integer(1)))
			{
				result = TaxType.selectAll(false);
			}
		}
		return result != null ? result : new Object[0];
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object
	 * )
	 */
	public Object getParent(Object element)
	{
		Object result = null;
		if (element instanceof Tax)
		{
			if (this.selection.equals(new Integer(0)))
			{
				result = ((Tax) element).getTaxRate();
			}
			else if (this.selection.equals(new Integer(1)))
			{
				result = ((Tax) element).getTaxType();
			}
		}
		if (element instanceof CurrentTax)
		{
			result = ((CurrentTax) element).getTax();
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.
	 * Object)
	 */
	public boolean hasChildren(Object element)
	{
		return this.getChildren(element).length > 0;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java
	 * .lang.Object)
	 */
	public Object[] getElements(Object inputElement)
	{
		return this.getChildren(inputElement);
	}
	
	public Composite createSelector(Composite parent, final StructuredViewer viewer, int style)
	{
		Group group = new Group(parent, style);
		group.setLayout(new GridLayout());
		group.setText(Messages.getString("TaxTreeContentProvider.Auswahl_Sortierung_1")); //$NON-NLS-1$
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Button[] radioButtons = new Button[2];
		radioButtons[0] = new Button(group, SWT.RADIO);
		// radioButtons[0].setLayoutData(new
		// GridData(GridData.FILL_HORIZONTAL));
		radioButtons[0].setText(Messages.getString("TaxTreeContentProvider.nach_Steuergruppen_2")); //$NON-NLS-1$
		radioButtons[0].setData(new Integer(0));
		radioButtons[0].addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				TaxTreeContentProvider.this.setInput(e);
			}
		});
		radioButtons[1] = new Button(group, SWT.RADIO);
		radioButtons[1].setText(Messages.getString("TaxTreeContentProvider.nach_Steuerarten_3")); //$NON-NLS-1$
		radioButtons[1].setData(new Integer(1));
		radioButtons[1].addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				TaxTreeContentProvider.this.setInput(e);
			}
		});
		radioButtons[0].setSelection(true);
		return group;
	}
	
	private void setInput(SelectionEvent e)
	{
		this.selection = (Integer) e.widget.getData();
		this.getViewer().setInput(this.getElements(this.selection));
		if (this.viewer.getInput() != null && ((Object[]) this.viewer.getInput()).length > 0)
		{
			Object[] objects = (Object[]) this.viewer.getInput();
			this.viewer.setSelection(new StructuredSelection(objects[0]));
		}
	}
	
	public boolean deleteElement(Object element)
	{
		if (element instanceof TaxType)
		{
			TaxType taxType = (TaxType) element;
			DBResult result = taxType.delete();
			if (result.getErrorCode() != 0)
			{
				MessageDialog
								.openError(
												this.viewer.getControl().getShell(),
												Messages.getString("TaxTreeContentProvider.Fehler_4"), Messages.getString("TaxTreeContentProvider.Die_Mehrwertsteuerart__5") + taxType.name + Messages.getString("TaxTreeContentProvider._konnte_nicht_gel_u00F6scht_werden._6")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			return result.getErrorCode() == 0;
		}
		else if (element instanceof TaxRate)
		{
			TaxRate taxRate = (TaxRate) element;
			DBResult result = taxRate.delete();
			if (result.getErrorCode() != 0)
			{
				MessageDialog
								.openError(
												this.viewer.getControl().getShell(),
												Messages.getString("TaxTreeContentProvider.Fehler_7"), Messages.getString("TaxTreeContentProvider.Die_Mehrwertsteuergruppe__8") + taxRate.name + Messages.getString("TaxTreeContentProvider._konnte_nicht_gel_u00F6scht_werden._9")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			return result.getErrorCode() == 0;
		}
		else if (element instanceof Tax)
		{
			Tax tax = (Tax) element;
			DBResult result = tax.delete();
			if (result.getErrorCode() != 0)
			{
				MessageDialog
								.openError(
												this.viewer.getControl().getShell(),
												Messages.getString("TaxTreeContentProvider.Fehler_10"), Messages.getString("TaxTreeContentProvider.Die_Mehrwertsteuer__11") + ((SimpleLabelProvider) this.viewer.getLabelProvider()).getText(tax) + Messages.getString("TaxTreeContentProvider._konnte_nicht_gel_u00F6scht_werden._12")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			return result.getErrorCode() == 0;
		}
		else if (element instanceof CurrentTax)
		{
			CurrentTax currentTax = (CurrentTax) element;
			DBResult result = currentTax.delete();
			if (result.getErrorCode() != 0)
			{
				MessageDialog
								.openError(
												this.viewer.getControl().getShell(),
												Messages.getString("TaxTreeContentProvider.Fehler_13"), Messages.getString("TaxTreeContentProvider.Der_Mehrwertsteuersatz__14") + Double.toString(currentTax.percentage) + Messages.getString("TaxTreeContentProvider._konnte_nicht_gel_u00F6scht_werden._15")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			return result.getErrorCode() == 0;
		}
		return false;
	}
	
	private Integer selection = new Integer(0);
	
	protected static final int RATE_SELECTED = 0;
	protected static final int TYPE_SELECTED = 1;
}
