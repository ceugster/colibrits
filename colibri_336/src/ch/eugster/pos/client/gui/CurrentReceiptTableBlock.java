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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.events.Action;
import ch.eugster.pos.events.MessageDialog;
import ch.eugster.pos.events.ModeChangeEvent;
import ch.eugster.pos.events.PosEvent;
import ch.eugster.pos.events.PosEventListener;
import ch.eugster.pos.events.PrintReceiptAction;
import ch.eugster.pos.events.ReverseAction;
import ch.eugster.pos.events.ShowMessageEvent;
import ch.eugster.pos.events.ShowMessageListener;
import ch.eugster.pos.events.ShowReceiptListAction;
import ch.eugster.pos.util.NumberUtility;
import ch.eugster.pos.util.Serializer;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CurrentReceiptTableBlock extends ABlock implements DetailBlock, TableModel, ListSelectionListener, ActionListener, PosEventListener
{
	private static final long serialVersionUID = 0l;
	
	/**
	 * 
	 */
	public CurrentReceiptTableBlock(UserPanel context)
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
		this.table.setModel(this);
		this.table.getSelectionModel().addListSelectionListener(this);
		this.add(new JScrollPane(this.table), BorderLayout.CENTER);
		
		FixKey[] fixKeys = FixKey.getByBlock(this.getClass().getName());
		
		int rows = 0;
		int cols = 0;
		for (int i = 0; i < fixKeys.length; i++)
		{
			if (fixKeys[i].row >= rows)
			{
				rows = fixKeys[i].row + 1;
			}
			if (fixKeys[i].column >= cols)
			{
				cols = fixKeys[i].column + 1;
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
		this.add(panel, BorderLayout.SOUTH);
		this.addModeChangeListener(this.context);
		this.setShowMessageListener(Frame.getMainFrame());
	}
	
	public JTable getTable()
	{
		return this.table;
	}
	
	public void setShowMessageListener(ShowMessageListener listener)
	{
		this.messageListener = listener;
	}
	
	public void posEventPerformed(PosEvent e)
	{
		if (e.getPosAction() instanceof ShowReceiptListAction)
		{
			ShowReceiptListAction srla = (ShowReceiptListAction) e.getPosAction();
			if (srla.getActionType().equals(Action.POS_ACTION_SHOW_RECEIPT_LIST))
			{
				this.loadReceipts(true);
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
			Action a = null;
			if (e.getSource() instanceof PosButton)
			{
				a = (Action) ((PosButton) e.getSource()).getAction();
			}
			if (a instanceof PrintReceiptAction)
			{
				if (this.table.getSelectedRow() > -1)
				{
					if (this.context.getParent() instanceof TabPanel)
					{
						TabPanel parent = (TabPanel) this.context.getParent();
						parent.getReceiptPrinter().print(parent.getReceiptPrinter().getPrinter(), this.receipts[this.table.getSelectedRow()]);
					}
				}
			}
			else if (a instanceof ReverseAction)
			{
				if (this.table.getSelectedRow() > -1)
				{
					Receipt receipt = this.receipts[this.table.getSelectedRow()];
					Serializer.getInstance().deleteReceipt(Serializer.getInstance().getPath(receipt));
					if (receipt.status == Receipt.RECEIPT_STATE_REVERSED)
					{
						receipt.status = Receipt.RECEIPT_STATE_SERIALIZED;
					}
					else if (receipt.status == Receipt.RECEIPT_STATE_SERIALIZED)
					{
						Position p = this.hasPayedInvoice(receipt);
						if (p != null)
						{
							// System.out.println("");
							ShowMessageEvent event = new ShowMessageEvent(
											"<html><p>Der Beleg enthält eine Position zum Bezahlen einer Rechnung.</p><p>Diese Bezahlung kann nicht rückgängig gemacht werden.</p><p>Bitte stellen Sie sicher, dass die Rückbuchung in Galileo manuell vorgenommen wird:</p><p>Nummer: "
															+ p.getInvoiceNumber().toString()
															+ "</p><p>Datum:  "
															+ (p.getInvoiceDate() instanceof Date ? SimpleDateFormat.getDateInstance().format(p.getInvoiceDate()) : "</p></html>"),
											"Bezahlte Rechnung", MessageDialog.TYPE_INFORMATION);
							this.messageListener.showMessage(event);
						}
						receipt.status = Receipt.RECEIPT_STATE_REVERSED;
					}
					// 10163
					if (!Database.getCurrent().equals(Database.getStandard()))
					{
						receipt.update();
					}
					// 10163
					// 10213
					DBResult result = receipt.store(false, false);
					// DBResult result = receipt.store(false);
					// 10213
					if (result.getErrorCode() == 0)
					{
						if (!Database.getCurrent().getCode().equals("tut"))
						{
							Serializer.getInstance().writeReceipt(receipt);
						}
					}
					
					this.fireTableChangedEvent(new TableModelEvent(this));
				}
			}
			else if (e.getActionCommand().equals("up")) { //$NON-NLS-1$
				if (this.table.getSelectedRow() > 0)
				{
					this.table.getSelectionModel().setLeadSelectionIndex(this.table.getSelectedRow() - 1);
					this.scrollToVisible();
				}
				this.up.setEnabled(this.table.getSelectedRow() > 0);
			}
			else if (e.getActionCommand().equals("down")) { //$NON-NLS-1$
				if (this.table.getSelectedRow() < this.table.getRowCount() - 1)
				{
					this.table.getSelectionModel().setLeadSelectionIndex(this.table.getSelectedRow() + 1);
					this.scrollToVisible();
				}
				this.down.setEnabled(this.table.getSelectedRow() < this.table.getRowCount() - 1);
			}
		}
	}
	
	private Position hasPayedInvoice(Receipt receipt)
	{
		Position[] p = receipt.getPositionsAsArray();
		for (int i = 0; i < p.length; i++)
		{
			if (p[i].isPayedInvoice())
			{
				return p[i];
			}
		}
		return null;
	}
	
	// private void updateReceipt(Receipt receipt) {
	// receipt.setDefaultCurrency(ForeignCurrency.getById(receipt.getDefaultCurrencyId()));
	// receipt.setSalespoint(Salespoint.getById(receipt.getSalespointId()));
	// receipt.setUser(User.getById(receipt.getUserId()));
	// updatePositions(receipt);
	// updatePayments(receipt);
	// }
	//	
	// private void updatePositions(Receipt receipt) {
	// Iterator iterator = receipt.getPositions().iterator();
	// while (iterator.hasNext()) {
	// Position p = (Position)iterator.next();
	// p.setProductGroup(ProductGroup.getById(p.getProductGroupId()));
	// p.setCurrentTax(p.getProductGroup().getDefaultTax().getCurrentTax());
	// p.setReceipt(receipt);
	// }
	// }
	//
	// private void updatePayments(Receipt receipt) {
	// Iterator iterator = receipt.getPayments().iterator();
	// while (iterator.hasNext()) {
	// Payment p = (Payment)iterator.next();
	// p.setForeignCurrency(ForeignCurrency.getById(p.getForeignCurrencyId()));
	// p.setPaymentType(PaymentType.getById(p.getPaymentTypeId()));
	// p.setReceipt(receipt);
	// }
	// }
	//
	private Receipt[] getReceipts(boolean reversedToo)
	{
		// 10358
		// if (context.getUser().status.intValue() < 2) {
		// Der User hat Status Admin oder Manager und kann alle Belege sehen
		return Receipt.selectCurrent(Salespoint.getCurrent(), reversedToo);
		// }
		// else {
		// return Receipt.selectCurrent(context.getUser(), reversedToo);
		// }
		// 10358
	}
	
	public void loadReceipts(boolean reversedToo)
	{
		this.receipts = this.getReceipts(reversedToo);
		if (this.table.getRowCount() > 0)
		{
			this.table.getSelectionModel().setLeadSelectionIndex(0);
		}
		for (int i = 0; i < this.buttons.length; i++)
		{
			for (int j = 0; j < this.buttons[i].length; j++)
			{
				if (this.buttons[i][j] != null)
				{
					if (this.buttons[i][j].getActionCommand().equals("up")) { //$NON-NLS-1$
						this.buttons[i][j].setEnabled(this.table.getSelectedRow() > 0);
					}
					else if (this.buttons[i][j].getActionCommand().equals("down")) { //$NON-NLS-1$
						this.buttons[i][j].setEnabled(this.table.getSelectedRow() < this.table.getRowCount() - 1);
					}
					else if (this.buttons[i][j].getActionCommand().equals("print")) { //$NON-NLS-1$
						this.buttons[i][j].setEnabled(!(this.table.getSelectedRow() == -1));
					}
					else if (this.buttons[i][j].getActionCommand().equals("reverse")) { //$NON-NLS-1$
						this.buttons[i][j].setEnabled(!(this.table.getSelectedRow() == -1));
					}
					else if (this.buttons[i][j].getActionCommand().equals("back")) { //$NON-NLS-1$
						this.buttons[i][j].setEnabled(true);
					}
				}
			}
		}
		this.fireTableChangedEvent(new TableModelEvent(this));
	}
	
	private void fireTableChangedEvent(TableModelEvent e)
	{
		TableModelListener[] l = (TableModelListener[]) this.tableModelListeners.toArray(new TableModelListener[0]);
		for (int i = 0; i < l.length; i++)
		{
			l[i].tableChanged(e);
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
		return this.receipts.length;
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
		if (columnIndex == 0)
		{
			o = this.receipts[rowIndex].getFormattedNumber();
		}
		else if (columnIndex == 1)
		{
			o = DateFormat.getDateInstance().format(this.receipts[rowIndex].getDate());
		}
		else if (columnIndex == 2)
		{
			o = DateFormat.getTimeInstance().format(this.receipts[rowIndex].getTime());
		}
		else if (columnIndex == 3)
		{
			o = NumberUtility.formatDefaultCurrency(this.receipts[rowIndex].getAmount(), true, true);
		}
		else if (columnIndex == 4)
		{
			o = this.receipts[rowIndex].status == Receipt.RECEIPT_STATE_REVERSED ? "S" : ""; //$NON-NLS-1$ //$NON-NLS-2$
			o = o + (this.receipts[rowIndex].isTransferred() ? "T" : ""); //$NON-NLS-1$ //$NON-NLS-2$
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
		
		for (int i = 0; i < this.buttons.length; i++)
		{
			for (int j = 0; j < this.buttons[i].length; j++)
			{
				if (this.buttons[i][j] != null)
				{
					if (this.buttons[i][j].getAction() instanceof ReverseAction)
					{
						this.buttons[i][j].setEnabled(this.table.getSelectedRow() == -1 ? false : UserPanel.getCurrent().getUser().getReverseReceipts());
					}
					else if (this.buttons[i][j].getAction() instanceof PrintReceiptAction)
					{
						this.buttons[i][j].setEnabled(this.table.getSelectedRow() == -1 ? false : true);
					}
					else
					{
						if (this.buttons[i][j].getActionCommand().equals("up")) { //$NON-NLS-1$
							this.buttons[i][j].setEnabled(this.table.getSelectedRow() > 0);
						}
						else if (this.buttons[i][j].getActionCommand().equals("down")) { //$NON-NLS-1$
							this.buttons[i][j].setEnabled(this.table.getSelectedRow() < this.table.getRowCount() - 1);
						}
					}
				}
			}
		}
	}
	
	private JTable table;
	private PosButton[][] buttons;
	private PosButton up;
	private PosButton down;
	private String[] columnNames =
	{ Messages.getString("CurrentReceiptTableBlock.Nummer_11"), //$NON-NLS-1$
					Messages.getString("CurrentReceiptTableBlock.Datum_12"), //$NON-NLS-1$
					Messages.getString("CurrentReceiptTableBlock.Zeit_13"), //$NON-NLS-1$
					Messages.getString("CurrentReceiptTableBlock.Betrag_14"), //$NON-NLS-1$
					Messages.getString("CurrentReceiptTableBlock.S_15") }; //$NON-NLS-1$}; 
	private Receipt[] receipts = new Receipt[0];
	private ArrayList tableModelListeners = new ArrayList();
	private ShowMessageListener messageListener;
	
	public static final Integer LIST_NO_ROW_SELECTED = new Integer(0);
	public static final Integer LIST_ROW_SELECTED = new Integer(1);
}
