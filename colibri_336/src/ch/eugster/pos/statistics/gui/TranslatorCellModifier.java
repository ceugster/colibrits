/*
 * Created on 18.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.gui;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;

import ch.eugster.pos.statistics.data.Translator;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TranslatorCellModifier implements ICellModifier
{
	
	private ReceiptImportTranslatorComposite composite;
	private String[] columnNames;
	
	/**
	 * 
	 */
	public TranslatorCellModifier(ReceiptImportTranslatorComposite composite)
	{
		super();
		this.composite = composite;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object,
	 * java.lang.String)
	 */
	public boolean canModify(Object element, String property)
	{
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object,
	 * java.lang.String)
	 */
	public Object getValue(Object element, String property)
	{
		int columnIndex = this.composite.getColumnNames().indexOf(property);
		
		Object result = null;
		Translator translator = (Translator) element;
		
		switch (columnIndex)
		{
			case 0: // Source Code Column
				result = translator.sourceCode;
				break;
			case 1: // Target Table Column
				String stringValue = translator.targetTable;
				String[] choices = this.composite.getTables();
				int i = choices.length - 1;
				if (stringValue == null)
				{
					result = new Integer(0);
				}
				else
				{
					while (!stringValue.trim().equals(choices[i].trim()) && i > 0)
						--i;
					result = new Integer(i);
				}
				break;
			case 2: // Target Id
				stringValue = translator.getValue();
				choices = this.composite.getTargetValues(translator.targetTable);
				i = choices.length - 1;
				if (stringValue == null)
				{
					result = new Integer(0);
				}
				else
				{
					while (!stringValue.trim().equals(choices[i].trim()) && i > 0)
						--i;
					result = new Integer(i);
				}
				break;
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object,
	 * java.lang.String, java.lang.Object)
	 */
	public void modify(Object element, String property, Object value)
	{
		int columnIndex = this.composite.getColumnNames().indexOf(property);
		
		TableItem item = (TableItem) element;
		Translator translator = (Translator) item.getData();
		String valueString;
		
		switch (columnIndex)
		{
			case 0: // SOURCE_CODE
				translator.sourceCode = value.toString();
				break;
			case 1: // TARGET_TABLE
				valueString = this.composite.getTables()[((Integer) value).intValue()].trim();
				if (!translator.targetTable.equals(valueString))
				{
					translator.targetTable = valueString;
				}
				break;
			case 2: // TARGET_ID
				int i = ((Integer) value).intValue();
				String s = this.composite.getItems()[i].trim().split(" ")[0];
				translator.setTargetId(new Long(s));
				break;
			default:
		}
		this.composite.getTranslatorList().translatorChanged(translator);
	}
	
	private TableViewer tableViewer;
}
