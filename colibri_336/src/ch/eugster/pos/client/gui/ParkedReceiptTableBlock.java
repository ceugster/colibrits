/*
 * Created on 07.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.FixKey;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.db.User;
import ch.eugster.pos.events.Action;
import ch.eugster.pos.events.ModeChangeEvent;
import ch.eugster.pos.events.ParkAction;
import ch.eugster.pos.events.PosEvent;
import ch.eugster.pos.events.PosEventListener;
import ch.eugster.pos.util.NumberUtility;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ParkedReceiptTableBlock extends ABlock implements DetailBlock, TableModel, ListSelectionListener,
				ActionListener, PosEventListener
{
	
	private static final long serialVersionUID = 0l;
	
	/**
	 * 
	 */
	public ParkedReceiptTableBlock(UserPanel context)
	{
		super(new BorderLayout(), context);
		this.init();
	}
	
	private void init()
	{
		this.table = new JTable();
		this.table.setFocusable(false);
		this.table.setFont(this.table.getFont().deriveFont(Font.PLAIN, 12.0f));
		this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		this.table.setRowSelectionAllowed(true);
		this.table.setModel(this);
		this.table.getSelectionModel().addListSelectionListener(this);
		this.table.setAutoscrolls(true);
		this.add(new JScrollPane(this.table), BorderLayout.CENTER);
		
		FixKey[] fixKeys = FixKey.getByBlock(this.getClass().getName());
		
		int rows = 0;
		int cols = 0;
		for (FixKey fixKey : fixKeys)
		{
			if (fixKey.row >= rows)
			{
				rows = fixKey.row + 1;
			}
			if (fixKey.column >= cols)
			{
				cols = fixKey.column + 1;
			}
		}
		
		FixKey[][] keys = new FixKey[rows][cols];
		for (int i = 0; i < fixKeys.length; i++)
		{
			keys[fixKeys[i].row][fixKeys[i].column] = fixKeys[i];
		}
		
		JPanel panel = new JPanel(new GridLayout(1, keys.length));
		this.buttons = new PosButton[rows][cols];
		for (int i = 0; i < keys.length; i++)
		{
			if (keys[i].length > 0)
			{
				for (int j = 0; j < keys[i].length; j++)
				{
					if (keys[i][j] != null)
					{
						this.buttons[i][j] = keys[i][j].createButton(this.context);
						this.buttons[i][j].setPreferredSize(new Dimension(20, 100));
						this.buttons[i][j].addActionListener(this);
						
						if (this.buttons[i][j].getActionCommand().equals("up")) { //$NON-NLS-1$
							this.up = this.buttons[i][j];
						}
						if (this.buttons[i][j].getActionCommand().equals("down")) { //$NON-NLS-1$
							this.down = this.buttons[i][j];
						}
						panel.add(this.buttons[i][j]);
					}
				}
			}
		}
		this.add(panel, BorderLayout.SOUTH);
		this.addModeChangeListener(this.context);
	}
	
	public JTable getTable()
	{
		return this.table;
	}
	
	@Override
	public void posEventPerformed(PosEvent e)
	{
		if (e.getPosAction() instanceof ParkAction)
		{
			ParkAction pa = (ParkAction) e.getPosAction();
			if (pa.getActionType().equals(Action.POS_ACTION_SHOW_PARK_LIST))
			{
				this.loadReceipts(this.context.getUser(), Receipt.RECEIPT_STATE_PARKED);
			}
		}
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals("back")) { //$NON-NLS-1$
			this.fireModeChangeEvent(new ModeChangeEvent(UserPanel.CONTEXT_MODE_PREVIOUS));
		}
		if (this.table.getRowCount() > 0)
		{
			int selectedRow = this.table.getSelectedRow();
			if (e.getActionCommand().equals("get")) { //$NON-NLS-1$
				if (selectedRow > -1)
				{
					Receipt receipt = (Receipt) this.receiptList.get(selectedRow);
					receipt.status = Receipt.RECEIPT_STATE_NEW;
					if (Database.getCurrent().equals(Database.getTemporary())) receipt.update();
					receipt.store(false);
					this.context.getReceiptModel().setReceipt(receipt);
				}
			}
			else if (e.getActionCommand().equals("delete")) { //$NON-NLS-1$
				if (selectedRow > -1)
				{
					DBResult result = ((Receipt) this.receiptList.get(selectedRow)).delete();
					if (result.getErrorCode() == 0)
					{
						this.receiptList.remove(selectedRow);
						this.table.updateUI();
					}
				}
			}
			else if (e.getActionCommand().equals("up")) { //$NON-NLS-1$
				if (selectedRow > 0)
				{
					selectedRow--;
					this.table.setRowSelectionInterval(selectedRow, selectedRow);
					this.scrollToVisible();
				}
				this.up.setEnabled(selectedRow > 0);
			}
			else if (e.getActionCommand().equals("down")) { //$NON-NLS-1$
				if (selectedRow < this.table.getRowCount() - 1)
				{
					selectedRow++;
					this.table.setRowSelectionInterval(selectedRow, selectedRow);
					this.scrollToVisible();
				}
				this.down.setEnabled(selectedRow < this.table.getRowCount() - 1);
			}
		}
	}
	
	public void loadReceipts(User user, int state)
	{
		this.receiptState = state;
		Database.getCurrent().getBroker().beginTransaction();
		this.receiptList = Receipt.selectByUserStateToList(user, state);
		Database.getCurrent().getBroker().commitTransaction();
		this.setButtonState();
		TableModelEvent e = new TableModelEvent(this);
		TableModelListener[] l = (TableModelListener[]) this.tableModelListeners.toArray(new TableModelListener[0]);
		for (TableModelListener element : l)
		{
			element.tableChanged(e);
		}
		if (this.table.getRowCount() > 0)
		{
			this.table.setRowSelectionInterval(0, 0);
		}
	}
	
	private void setButtonState()
	{
		for (PosButton[] button : this.buttons)
		{
			if (button.length > 0)
			{
				for (PosButton element : button)
				{
					if (element != null)
					{
						if (element.getActionCommand().equals("up")) { //$NON-NLS-1$
							element.setEnabled(this.table.getSelectedRow() > 0);
						}
						else if (element.getActionCommand().equals("down")) { //$NON-NLS-1$
							element.setEnabled(this.table.getSelectedRow() < this.table.getRowCount() - 1);
						}
						else if (element.getActionCommand().equals("get")) { //$NON-NLS-1$
							element.setEnabled(!(this.table.getSelectedRow() == -1));
						}
						else if (element.getActionCommand().equals("delete")) { //$NON-NLS-1$
							element.setEnabled(!(this.table.getSelectedRow() == -1));
						}
						else if (element.getActionCommand().equals("back")) { //$NON-NLS-1$
							element.setEnabled(true);
						}
					}
				}
			}
		}
	}
	
	public void scrollToVisible()
	{
		if (!(this.table.getParent() instanceof JViewport))
		{
			return;
		}
		JViewport viewport = (JViewport) this.table.getParent();
		Rectangle rect = this.table.getCellRect(this.table.getSelectedRow(), 0, true);
		Point pt = viewport.getViewPosition();
		rect.setLocation(rect.x - pt.x, rect.y - pt.y);
		viewport.scrollRectToVisible(rect);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount()
	{
		return this.receiptList.size();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount()
	{
		return this.columnNames.length;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int columnIndex)
	{
		return this.columnNames[columnIndex];
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	public Class getColumnClass(int columnIndex)
	{
		return String.class;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		Object o = null;
		Receipt receipt = (Receipt) this.receiptList.get(rowIndex);
		if (columnIndex == 0)
		{
			o = receipt.getNumber();
		}
		else if (columnIndex == 1)
		{
			o = DateFormat.getDateInstance().format(receipt.getDate());
		}
		else if (columnIndex == 2)
		{
			o = DateFormat.getTimeInstance().format(receipt.getTime());
		}
		else if (columnIndex == 3)
		{
			o = NumberUtility.formatDefaultCurrency(receipt.getAmount(), true, false);
		}
		return o;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.table.TableModel#addTableModelListener(javax.swing.event.
	 * TableModelListener)
	 */
	public void addTableModelListener(TableModelListener l)
	{
		this.tableModelListeners.add(l);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.table.TableModel#removeTableModelListener(javax.swing.event
	 * .TableModelListener)
	 */
	public void removeTableModelListener(TableModelListener l)
	{
		this.tableModelListeners.remove(l);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event
	 * .ListSelectionEvent)
	 */
	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting()) return;
		this.setButtonState();
	}
	
	public int state()
	{
		return this.receiptState;
	}
	
	private int receiptState;
	private JTable table;
	private PosButton[][] buttons;
	private PosButton up;
	private PosButton down;
	private String[] columnNames =
	{ Messages.getString("ParkedReceiptTableBlock.Nummer_11"), //$NON-NLS-1$
					Messages.getString("ParkedReceiptTableBlock.Datum_12"), //$NON-NLS-1$
					Messages.getString("ParkedReceiptTableBlock.Zeit_13"), //$NON-NLS-1$
					Messages.getString("ParkedReceiptTableBlock.Betrag_14") }; //$NON-NLS-1$
	// private Receipt[] receipts = new Receipt[0];
	private ArrayList tableModelListeners = new ArrayList();
	private Vector receiptList = new Vector();
	
	public static final int LIST_NO_ROW_SELECTED = 0;
	public static final int LIST_ROW_SELECTED = 1;
}
