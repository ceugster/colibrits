/*
 * Created on 20.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.product;

import org.eclipse.jface.util.PropertyChangeEvent;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.gui.widget.ComboFieldEditor;
import ch.eugster.pos.admin.gui.widget.ComboFieldEditorContentProvider;
import ch.eugster.pos.admin.gui.widget.RadioGroupFieldEditor;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.db.Tax;
import ch.eugster.pos.db.TaxType;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class TaxComboFieldEditorContentProvider extends ComboFieldEditorContentProvider
{
	
	/**
	 * 
	 */
	public TaxComboFieldEditorContentProvider(ComboFieldEditor editor)
	{
		super(editor);
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
		Tax[] taxes = null;
		if (((Integer) inputElement).equals(new Integer(ProductGroup.TYPE_EXPENSE)))
		{
			Tax[] tb = Tax.selectByTypeId(
							TaxType.selectByCode(Messages.getString("TaxComboFieldEditorContentProvider.B_1"), false).getId(), false); //$NON-NLS-1$
			Tax[] tm = Tax.selectByTypeId(
							TaxType.selectByCode(Messages.getString("TaxComboFieldEditorContentProvider.M_2"), false).getId(), false); //$NON-NLS-1$
			taxes = this.merge(tb, tm);
		}
		else
		{
			taxes = Tax.selectByTypeId(
							TaxType.selectByCode(Messages.getString("TaxComboFieldEditorContentProvider.U_3"), false).getId(), false); //$NON-NLS-1$
		}
		return taxes == null ? new Tax[0] : taxes;
	}
	
	private Tax[] merge(Tax[] tax1, Tax[] tax2)
	{
		Tax[] taxes = new Tax[tax1.length + tax2.length];
		for (int i = 0; i < tax1.length; i++)
		{
			taxes[i] = tax1[i];
		}
		for (int i = tax1.length; i < tax1.length + tax2.length; i++)
		{
			taxes[i] = tax2[i - tax1.length];
		}
		return taxes;
	}
	
	public Object doLoad()
	{
		return this.editor.getStore().getTax(this.editor.getName());
	}
	
	public Object doLoadDefault()
	{
		return this.editor.getStore().getDefaultTax(this.editor.getName());
	}
	
	public void doStore()
	{
		if (this.editor.isValid())
		{
			this.editor.getStore().setValue(this.editor.getName(), (Tax) this.editor.getSelectedItem());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.admin.widgets.IComboFieldEditorContentProvider#doCheckState
	 * ()
	 */
	public boolean doCheckState()
	{
		boolean result = false;
		if (this.editor.getSelectedItem() == null)
		{
			return result;
		}
		
		if (this.editor.getCombo().getSelectionIndex() < 0)
		{
			return result;
		}
		
		if (this.editor.getSelectedItem() instanceof Tax)
		{
			result = true;
		}
		return result;
	}
	
	public void propertyChange(PropertyChangeEvent e)
	{
		if (e.getSource() instanceof RadioGroupFieldEditor || e.getSource() instanceof ProductGroupStore)
		{
			if (e.getNewValue() instanceof Integer)
			{
				int type = ((Integer) e.getNewValue()).intValue();
				if (type >= ProductGroup.TYPE_INCOME && type <= ProductGroup.TYPE_WITHDRAW)
				{
					if (this.editor != null)
					{
						this.editor.setInput(e.getNewValue());
						this.editor.load();
					}
				}
			}
		}
	}
	
	protected boolean isValid(Object o)
	{
		return o instanceof Tax;
	}
}
