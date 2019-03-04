/*
 * Created on 11.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.product;

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
import ch.eugster.pos.admin.model.TableContentProvider;
import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.ProductGroup;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ProductGroupTableContentProvider extends TableContentProvider
{
	
	public Object[] getElements(Object element)
	{
		return ProductGroup.selectAll(this.checkboxButton.getSelection());
	}
	
	public boolean deleteElement(Object element)
	{
		if (element instanceof ProductGroup)
		{
			ProductGroup productGroup = (ProductGroup) element;
			DBResult result = productGroup.delete();
			if (result.getErrorCode() != 0)
			{
				MessageDialog
								.openError(
												this.viewer.getControl().getShell(),
												Messages.getString("ProductGroupTableContentProvider.Fehler_1"), Messages.getString("ProductGroupTableContentProvider.Die_Warengruppe__2") + productGroup.name + Messages.getString("ProductGroupTableContentProvider._konnte_nicht_gel_u00F6scht_werden._3")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			return result.getErrorCode() == 0;
		}
		return false;
	}
	
	public Composite createSelector(Composite parent, final StructuredViewer viewer, int style)
	{
		Group group = new Group(parent, style);
		group.setLayout(new GridLayout());
		group.setText("Warengruppenfilter"); //$NON-NLS-1$
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.checkboxButton = new Button(group, SWT.CHECK);
		// radioButtons[0].setLayoutData(new
		// GridData(GridData.FILL_HORIZONTAL));
		this.checkboxButton.setText("gelöschte Warengruppen anzeigen"); //$NON-NLS-1$
		this.checkboxButton.setSelection(false);
		this.checkboxButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				ProductGroupTableContentProvider.this.setInput(e);
			}
		});
		return group;
	}
	
	private void setInput(SelectionEvent e)
	{
		if (e.widget.equals(this.checkboxButton))
		{
			Button b = (Button) e.widget;
			this.getViewer().setInput(this.getElements(new Boolean(b.getSelection())));
			if (this.viewer.getInput() != null && ((Object[]) this.viewer.getInput()).length > 0)
			{
				Object[] objects = (Object[]) this.viewer.getInput();
				this.viewer.setSelection(new StructuredSelection(objects[0]));
			}
		}
	}
	
	private Button checkboxButton;
}
