/*
 * Created on 14.03.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.client.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import ch.eugster.pos.client.model.PositionTableModel;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PosTableCellRenderer extends DefaultTableCellRenderer
{
	private static final long serialVersionUID = 0l;
	
	private PositionTableModel model;
	private Color normal;
	private Color back;
	private Color expense;
	
	public PosTableCellRenderer(PositionTableModel model, Color normal, Color back, Color expense)
	{
		super();
		this.model = model;
		this.normal = normal;
		this.back = back;
		this.expense = expense;
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		Component result = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		// if (isNegativePriceValue(row))
		// if (!isSelected)
		// result.setBackground(Color.PINK);
		// else
		// if (!isSelected)
		// result.setBackground(Color.WHITE );
		
		if (this.isNegativePriceValue(row))
			result.setForeground(isSelected ? Color.GREEN : this.expense);
		else if (this.isNegativeQuantityValue(row))
			result.setForeground(isSelected ? Color.MAGENTA : this.back);
		else
			result.setForeground(isSelected ? Color.DARK_GRAY : this.normal);
		
		return result;
	}
	
	private boolean isNegativePriceValue(int row)
	{
		return this.model.getReceipt().getPositionAt(row).getPrice() < 0d;
	}
	
	private boolean isNegativeQuantityValue(int row)
	{
		return this.model.getReceipt().getPositionAt(row).getQuantity() < 0;
	}
}
