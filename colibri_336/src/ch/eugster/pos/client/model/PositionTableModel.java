/*
 * Created on 24.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.client.model;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.util.Enumeration;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumn;

import ch.eugster.pos.Messages;
import ch.eugster.pos.client.gui.Frame;
import ch.eugster.pos.client.gui.PosTableCellRenderer;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.db.ReceiptChild;
import ch.eugster.pos.events.Action;
import ch.eugster.pos.events.ClearAction;
import ch.eugster.pos.events.CustomerChangeEvent;
import ch.eugster.pos.events.CustomerChangeListener;
import ch.eugster.pos.events.DeleteAction;
import ch.eugster.pos.events.DiscountAction;
import ch.eugster.pos.events.EnterAction;
import ch.eugster.pos.events.MessageDialog;
import ch.eugster.pos.events.PosEvent;
import ch.eugster.pos.events.ReceiptChangeEvent;
import ch.eugster.pos.events.UpdateCustomerAccountAction;
import ch.eugster.pos.util.Config;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PositionTableModel extends ReceiptChildTableModel implements CustomerChangeListener
{
	public static final long serialVersionUID = 0l;
	
	public PositionTableModel(ReceiptModel receiptModel)
	{
		super(receiptModel, new JTable());
		this.init();
	}
	
	protected void init()
	{
		Config cfg = Config.getInstance();
		int style = Integer.valueOf(cfg.getDetailBlockListFont().getAttributeValue("style")).intValue();
		float size = Float.valueOf(cfg.getDetailBlockListFont().getAttributeValue("size")).floatValue();
		this.table.setFont(this.table.getFont().deriveFont(style, size));
		if (Database.getCurrent().equals(Database.getTutorial()))
		{
			this.table.setForeground(Color.black);
			this.table.setBackground(Color.red);
		}
		else
		{
			this.table.setForeground(cfg.getDetailBlockListNormalColorForeground());
		}
		this.table.setRowSelectionAllowed(true);
		this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		Enumeration columns = this.table.getColumnModel().getColumns();
		Color normal = Config.getInstance().getDetailBlockListNormalColorForeground();
		Color back = Config.getInstance().getDetailBlockListBackColorForeground();
		Color expense = Config.getInstance().getDetailBlockListExpenseColorForeground();
		while (columns.hasMoreElements())
		{
			TableColumn column = (TableColumn) columns.nextElement();
			column.setCellRenderer(new PosTableCellRenderer(this, normal, back, expense));
		}
		this.resizeColumns();
	}
	
	private void resizeColumns()
	{
		this.resizeColumns(-1);
	}
	
	private void resizeColumns(int currentRow)
	{
		Graphics g = this.table.getGraphics();
		FontMetrics fm = null;
		if (g != null)
		{
			fm = g.getFontMetrics();
			int sum = 0;
			for (int i = 1; i < this.table.getColumnCount(); i++)
			{
				String title = (String) this.table.getColumnModel().getColumn(i).getHeaderValue();
				int w = fm.stringWidth(title);
				for (int j = 0; j < this.table.getRowCount(); j++)
				{
					String val = (String) this.table.getValueAt(j, i);
					if (fm.stringWidth(val + 6) > w) w = fm.stringWidth(val + 6);
				}
				TableColumn col = this.table.getColumnModel().getColumn(i);
				col.setMinWidth(w);
				col.setMaxWidth(w);
				col.setPreferredWidth(w);
				// col.setCellRenderer(new PosTableCellRenderer(this));
				if (i > 0 && i < PositionTableModel.columnNames.length - 1)
				{
					((JLabel) col.getCellRenderer()).setHorizontalAlignment(JLabel.RIGHT);
				}
				else
				{
					((JLabel) col.getCellRenderer()).setHorizontalAlignment(JLabel.LEFT);
				}
				sum = sum + col.getPreferredWidth();
			}
			this.table.getColumnModel().getColumn(0).setMinWidth(30);
			this.table.getColumnModel().getColumn(0).setMaxWidth(600);
			this.table.doLayout();
			// table.getColumnModel().getColumn(0).setPreferredWidth(table.
			// getWidth()
			// - sum);
		}
	}
	
	public void componentResized(ComponentEvent event)
	{
		this.resizeColumns();
	}
	
	public boolean isThereAGivenBackPosition()
	{
		Position[] positions = this.receipt.getPositionsAsArray();
		for (int i = 0; i < positions.length; i++)
		{
			if (positions[i].isReturnPosition()) return true;
		}
		return false;
	}
	
	public void customerChanged(CustomerChangeEvent event)
	{
		for (int i = 0; i < this.getRowCount(); i++)
		{
			Position p = (Position) this.get(i, Position.class);
			p.updateCustomerAccount = p.getReceipt().getCustomerId().length() > 0;
			this.receipt.setChild(i, p);
		}
		if (this.getRowCount() > 0) this.fireReceiptChangeEvent(new ReceiptChangeEvent(this.receiptModel, ReceiptModel.RECEIPT_CUSTOMER_SET));
	}
	
	public void posEventPerformed(PosEvent e)
	{
		Action action = e.getPosAction();
		if (action instanceof EnterAction)
		{
			if (action.getActionType().equals(Action.POS_ACTION_STORE_ENTRY))
			{
				this.store(this.receiptModel.getPositionModel().getReceiptChild());
				this.receiptModel.getPaymentTableModel().removeNotCashPayments();
			}
		}
		else if (action instanceof DeleteAction)
		{
			if (action.getActionType().equals(Action.POS_ACTION_DELETE_ENTRY))
			{
				this.remove(Position.class);
			}
		}
		else if (action instanceof ClearAction)
		{
			if (action.getActionType().equals(Action.POS_ACTION_CLEAR))
			{
				this.table.clearSelection();
			}
		}
		else if (action instanceof UpdateCustomerAccountAction)
		{
			if (action.getActionType().equals(Action.POS_ACTION_SET_UPDATE_CUSTOMER_ACCOUNT))
			{
				this.receiptModel.getPositionModel().setUpdateCustomerAccount(!this.receiptModel.getPositionModel().getPosition().updateCustomerAccount);
			}
		}
		else if (action instanceof DiscountAction)
		{
			if (this.getTable().getRowCount() > 0)
			{
				if (action.getActionType().equals(Action.POS_ACTION_SET_DISCOUNT))
				{
					if (this.receiptModel.getPositionModel().isFresh() && this.getTable().getSelectedRowCount() == 0)
					{
						int answer = MessageDialog.BUTTON_YES;
						if (this.getTable().getRowCount() > 0)
						{
							answer = MessageDialog.showSimpleDialog(Frame.getMainFrame(), Messages.getString("PositionTableModel.Rabatteingabe_2"), //$NON-NLS-1$
											Messages.getString("PositionTableModel.Wollen_Sie_den_Rabatt_auf_alle_Positionen_anwenden__1"), //$NON-NLS-1$
											1);
						}
						if (answer == MessageDialog.BUTTON_YES)
						{
							ReceiptChangeEvent event = new ReceiptChangeEvent(this.receiptModel, ReceiptModel.RECEIPT_CHILD_CHANGED);
							for (int i = 0; i < this.getRowCount(); i++)
							{
								Position p = (Position) this.get(i, Position.class);
								if (p.getAmount() > 0d && p.getQuantity() > 0)
								{
									if (p.getProductGroup().type == ProductGroup.TYPE_INCOME)
									{
										p.setDiscount(((Double) action.getValue(Action.POS_KEY_DISCOUNT)).doubleValue());
										this.receipt.setChild(i, p);
										if (event.getReceiptChild() == null) event.setReceiptChild(p);
									}
								}
							}
							
							this.fireTableChanged(new TableModelEvent(this));
							this.fireReceiptChangeEvent(event);
						}
					}
				}
			}
		}
	}
	
	public void store(ReceiptChild child)
	{
		// Position p = (Position) child;
		// Integer type = p.getProductGroup().type;
		// if (this.table.getRowCount() == 0 &&
		// (type.equals(ProductGroup.TYPE_INPUT) ||
		// type.equals(ProductGroup.TYPE_WITHDRAW)))
		// {
		// String what = p.getProductGroup().quantityProposal.intValue() < 0 ?
		// "der Kasse Geld entnehmen" : "Geld in die Kasse legen";
		// String msg = "<html>Sie können nicht gleichzeitig " + what +
		// " und eine weitere Transaktion vornehmen!"
		// + "<br>Bitte beenden Sie die aktuelle Transaktion.</html>";
		// MessageDialog.showInformation(Frame.getMainFrame(),
		// "Geldeinlage/Geldentnahme", msg, MessageDialog.TYPE_INFORMATION);
		// }
		//		
		ReceiptChangeEvent e = new ReceiptChangeEvent(this.receiptModel);
		int row = this.table.getSelectedRow();
		if (row == -1)
		{
			if (this.receipt.addChild(child))
			{
				this.fireTableRowsInserted(0, 0);
				e.setEventType(ReceiptModel.RECEIPT_CHILD_ADDED);
				e.setReceiptChild(child);
				this.fireReceiptChangeEvent(e);
			}
		}
		else
		{
			this.receipt.setChild(row, child);
			e.setReceiptChild(child);
			this.table.clearSelection();
			this.fireTableRowsUpdated(row, row);
			e.setEventType(ReceiptModel.RECEIPT_CHILD_CHANGED);
			this.fireReceiptChangeEvent(e);
		}
		this.resizeColumns(row == -1 ? 0 : row);
	}
	
	public Class getColumnClass(int i)
	{
		return String.class;
	}
	
	public Object getValueAt(int row, int col)
	{
		return PositionModel.getValueAt(this.receipt.getPositionAt(row), col);
	}
	
	public int getRowCount()
	{
		return this.receipt.getPositionCount();
	}
	
	public void valueChanged(ListSelectionEvent e)
	{
		System.out.println();
	}
	
	public String getColumnName(int columnIndex)
	{
		return PositionTableModel.columnNames[columnIndex];
	}
	
	// public String[] getColumnNames() {
	// return columnNames;
	// }
	
	public int getColumnCount()
	{
		return PositionTableModel.columnNames.length;
	}
	
	private static String[] columnNames =
	{ Messages.getString("PositionTableModel.Artikel_1"), //$NON-NLS-1$
					Messages.getString("PositionTableModel.Menge_2"), //$NON-NLS-1$
					Messages.getString("PositionTableModel.Preis_3"), //$NON-NLS-1$
					Messages.getString("PositionTableModel.Rabatt_4"), //$NON-NLS-1$
					Messages.getString("PositionTableModel.Betrag"), //$NON-NLS-1$
					Messages.getString("PositionTableModel.Mwst_5"), //$NON-NLS-1$
					Messages.getString("PositionTableModel.Opt_6"), //$NON-NLS-1$
					"KK" }; //$NON-NLS-1$
	
}
