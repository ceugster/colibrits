/*
 * Created on 05.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.BorderUIResource;

import org.eclipse.swt.graphics.RGB;

import ch.eugster.pos.Messages;
import ch.eugster.pos.client.model.PositionModel;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.FixKey;
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.db.Table;
import ch.eugster.pos.events.ModeChangeEvent;
import ch.eugster.pos.events.ReceiptChildChangeEvent;
import ch.eugster.pos.events.ReceiptChildChangeListener;
import ch.eugster.pos.util.Config;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PositionBlock extends ABlock implements ReceiptChildChangeListener, ActionListener, TableModelListener,
				ListSelectionListener
{
	
	public static final long serialVersionUID = 0l;
	
	public PositionBlock(UserPanel context)
	{
		super(new GridLayout(2, 1), context);
		this.init();
	}
	
	private void init()
	{
		Config cfg = Config.getInstance();
		this.size = cfg.getFontSize(cfg.getDetailBlockFont());
		this.style = cfg.getFontStyle(cfg.getDetailBlockFont());
		this.highlightColor = cfg.getDetailBlockColorForeground();
		
		this.table = this.context.getReceiptModel().getPositionTableModel().getTable();
		this.table.setFocusable(false);
		this.add(new JScrollPane(this.table));
		
		this.labels[3] = this
						.makeTextLabel(Messages.getString("PositionBlock.Warengruppe_-_Artikel_1"), JLabel.LEADING); //$NON-NLS-1$
		this.labels[4] = this.makeTextLabel(Messages.getString("PositionBlock.Mwst_2"), JLabel.LEADING); //$NON-NLS-1$
		this.labels[5] = this.makeTextLabel(Messages.getString("PositionBlock.Opt_3"), JLabel.LEADING); //$NON-NLS-1$
		
		this.values[0] = this.makeValueLabel(JLabel.RIGHT);
		this.values[1] = this.makeValueLabel(JLabel.RIGHT);
		this.values[2] = this.makeValueLabel(JLabel.RIGHT);
		this.values[3] = this.makeValueLabel(JLabel.LEFT);
		this.values[4] = this.makeValueLabel(JLabel.LEFT);
		this.values[5] = this.makeValueLabel(JLabel.LEFT);
		
		JPanel southPanel = new JPanel(new BorderLayout());
		
		JPanel southNorthPanel = new JPanel(new BorderLayout());
		JPanel southNorthCenterPanel = new JPanel(new GridLayout(2, 1));
		JPanel southNorthEastPanel = new JPanel(new GridLayout(2, 2));
		
		southNorthCenterPanel.add(this.labels[3]);
		southNorthCenterPanel.add(this.values[3]);
		southNorthEastPanel.add(this.labels[4]);
		southNorthEastPanel.add(this.labels[5]);
		southNorthEastPanel.add(this.values[4]);
		southNorthEastPanel.add(this.values[5]);
		
		southNorthPanel.add(southNorthCenterPanel, BorderLayout.CENTER);
		southNorthPanel.add(southNorthEastPanel, BorderLayout.EAST);
		
		southPanel.add(southNorthPanel, BorderLayout.NORTH);
		
		FixKey[] keys = FixKey.getByBlock(this.getClass().getName());
		PosButton[] b = new PosButton[keys.length];
		for (int i = 0; i < keys.length; i++)
		{
			b[i] = keys[i].createButton(this.context);
			if (keys[i].command.equals("quantity")) { //$NON-NLS-1$
				this.buttons.put(keys[i].command, b[i]);
			}
			else if (keys[i].command.equals("price")) { //$NON-NLS-1$
				this.buttons.put(keys[i].command, b[i]);
			}
			else if (keys[i].command.equals("discount")) { //$NON-NLS-1$
				b[i].addActionListener(this);
				this.buttons.put(keys[i].command, b[i]);
			}
			else if (keys[i].command.equals("up")) { //$NON-NLS-1$
				b[i].addActionListener(this);
				this.buttons.put(keys[i].command, b[i]);
			}
			else if (keys[i].command.equals("down")) { //$NON-NLS-1$
				b[i].addActionListener(this);
				this.buttons.put(keys[i].command, b[i]);
			}
		}
		
		JPanel southCenterPanel = new JPanel(new GridLayout(1, b.length));
		
		JPanel southCenterPanel1 = new JPanel(new BorderLayout());
		JComponent quantity = (JComponent) this.buttons.get("quantity"); //$NON-NLS-1$
		if (quantity == null) quantity = new JPanel();
		this.addComponent(southCenterPanel1, quantity, BorderLayout.CENTER);
		this.addComponent(southCenterPanel1, this.values[0], BorderLayout.SOUTH);
		this.addComponent(southCenterPanel, southCenterPanel1);
		
		JPanel southCenterPanel2 = new JPanel(new BorderLayout());
		JComponent price = (JComponent) this.buttons.get("price"); //$NON-NLS-1$
		if (price == null) price = new JPanel();
		this.addComponent(southCenterPanel2, price, BorderLayout.CENTER);
		this.addComponent(southCenterPanel2, this.values[1], BorderLayout.SOUTH);
		this.addComponent(southCenterPanel, southCenterPanel2);
		
		JPanel southCenterPanel3 = new JPanel(new BorderLayout());
		JComponent discount = (JComponent) this.buttons.get("discount"); //$NON-NLS-1$
		if (discount == null) discount = new JPanel();
		this.addComponent(southCenterPanel3, discount, BorderLayout.CENTER);
		this.addComponent(southCenterPanel3, this.values[2], BorderLayout.SOUTH);
		this.addComponent(southCenterPanel, southCenterPanel3);
		
		JPanel southCenterPanel4 = new JPanel(new BorderLayout());
		JComponent up = (JComponent) this.buttons.get("up"); //$NON-NLS-1$
		if (up == null) up = new JPanel();
		this.addComponent(southCenterPanel4, up, BorderLayout.CENTER);
		this.addComponent(southCenterPanel, southCenterPanel4);
		
		JPanel southCenterPanel5 = new JPanel(new BorderLayout());
		JComponent down = (JComponent) this.buttons.get("down"); //$NON-NLS-1$
		if (down == null) down = new JPanel();
		this.addComponent(southCenterPanel5, down, BorderLayout.CENTER);
		this.addComponent(southCenterPanel, southCenterPanel5);
		
		southPanel.add(southCenterPanel, BorderLayout.CENTER);
		
		this.add(southPanel);
		
		this.table.getModel().addTableModelListener(this);
		this.table.getSelectionModel().addListSelectionListener(this);
	}
	
	private void addComponent(JComponent parent, JComponent child)
	{
		parent.add(child);
	}
	
	private void addComponent(JComponent parent, JComponent child, String constraint)
	{
		parent.add(child, constraint);
	}
	
	private JLabel makeValueLabel(int textAlign)
	{
		JLabel label = new JLabel();
		label.setBorder(BorderUIResource.getEtchedBorderUIResource());
		label.setOpaque(true);
		if (Database.getCurrent().equals(Database.getTutorial()))
		{
			label.setBackground(Color.red);
			label.setForeground(Color.black);
		}
		else
		{
			label.setBackground(Color.white);
			label.setForeground(Color.black);
		}
		// label.setBackground(Color.white);
		// label.setForeground(Color.black);
		label.setHorizontalAlignment(textAlign);
		label.setFont(label.getFont().deriveFont(this.style, this.size));
		return label;
	}
	
	private JLabel makeTextLabel(String text, int textAlign)
	{
		JLabel label = new JLabel(text);
		label.setBorder(BorderUIResource.getEtchedBorderUIResource());
		label.setHorizontalAlignment(textAlign);
		label.setFont(label.getFont().deriveFont(this.style, this.size));
		return label;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals("up")) { //$NON-NLS-1$
			if (this.table.getSelectedRow() > 0)
			{
				int selectedRow = this.table.getSelectedRow() - 1;
				this.table.setRowSelectionInterval(selectedRow, selectedRow);
			}
		}
		else if (e.getActionCommand().equals("down")) { //$NON-NLS-1$
			if (this.table.getSelectedRow() < this.table.getRowCount() - 1)
			{
				int selectedRow = this.table.getSelectedRow() + 1;
				this.table.setRowSelectionInterval(selectedRow, selectedRow);
			}
		}
		((PosButton) this.buttons.get("up")).setEnabled(this.table.getSelectedRow() > 0); //$NON-NLS-1$
		((PosButton) this.buttons.get("down")).setEnabled(this.table.getSelectedRow() < this.table.getRowCount() - 1); //$NON-NLS-1$
		this.scrollToVisible();
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
	
	public void tableChanged(TableModelEvent e)
	{
		if (this.buttons.get("up") != null) { //$NON-NLS-1$
			((PosButton) this.buttons.get("up")).setEnabled(this.table.getSelectedRow() > 0); //$NON-NLS-1$
		}
		if (this.buttons.get("down") != null) { //$NON-NLS-1$
			((PosButton) this.buttons.get("down")).setEnabled(this.table.getSelectedRow() < this.table.getRowCount() - 1); //$NON-NLS-1$
		}
	}
	
	public void valueChanged(ListSelectionEvent e)
	{
		if (this.buttons.get("up") != null) { //$NON-NLS-1$
			((PosButton) this.buttons.get("up")).setEnabled(this.table.getSelectedRow() > 0); //$NON-NLS-1$
		}
		if (this.buttons.get("down") != null) { //$NON-NLS-1$
			((PosButton) this.buttons.get("down")).setEnabled(this.table.getSelectedRow() < this.table.getRowCount() - 1); //$NON-NLS-1$
		}
	}
	
	public void receiptChildChangePerformed(ReceiptChildChangeEvent e)
	{
		if (e.getReceiptChildModel() instanceof PositionModel)
		{
			PositionModel pm = (PositionModel) e.getReceiptChildModel();
			this.display(pm);
			this.setValues(pm);
		}
	}
	
	@Override
	public void modeChangePerformed(ModeChangeEvent e)
	{
	}
	
	public void display(PositionModel pos)
	{
		if (!(this.currentHighlighted == null))
		{
			this.currentHighlighted.setForeground(this.originalColor);
		}
		if (!pos.isComplete())
		{
			Position p = (Position) pos.getReceiptChild();
			if (p.getQuantity() == 0)
			{
				this.currentHighlighted = (PosButton) this.buttons.get("quantity"); //$NON-NLS-1$
			}
			else if (p.getProductGroupId() == null || p.getProductGroupId().equals(Table.ZERO_VALUE))
			{
				this.currentHighlighted = this.labels[3];
			}
			else if (p.getPrice() == 0)
			{
				this.currentHighlighted = (PosButton) this.buttons.get("price"); //$NON-NLS-1$
			}
			else if (p.getDiscount() == 0)
			{
				this.currentHighlighted = (PosButton) this.buttons.get("discount"); //$NON-NLS-1$
			}
			else if (p.getCurrentTaxId() == null || p.getCurrentTaxId().equals(Table.ZERO_VALUE))
			{
				this.currentHighlighted = this.labels[4];
			}
			else if (p.optCode.equals("") && p.getProductGroup().type != ProductGroup.TYPE_INCOME) { //$NON-NLS-1$
				this.currentHighlighted = this.labels[5];
			}
			
			if (!(this.currentHighlighted == null))
			{
				this.originalColor = this.currentHighlighted.getForeground();
				this.currentHighlighted.setForeground(this.highlightColor);
			}
		}
	}
	
	public void setValues(PositionModel pos)
	{
		this.values[0].setText(PositionModel.getDisplayQuantity(pos.getPosition()));
		this.values[1].setText(PositionModel.getDisplayPrice(pos.getPosition()));
		this.values[2].setText(PositionModel.getDisplayDiscount(pos.getPosition()));
		this.values[3].setText(PositionModel.getDisplayText(pos.getPosition()));
		this.values[4].setText(PositionModel.getDisplayTaxPercentage(pos.getPosition()));
		this.values[5].setText(PositionModel.getDisplayOptCode(pos.getPosition()));
		
		this.setColors(pos.getPosition()); // 10223
	}
	
	// 10223
	private void setColors(Position position)
	{
		if (this.normal == null) this.normal = Config.getInstance().getDetailBlockListNormalColorForeground();
		if (this.back == null) this.back = Config.getInstance().getDetailBlockListBackColorForeground();
		if (this.expense == null) this.expense = Config.getInstance().getDetailBlockListExpenseColorForeground();
		if (this.ret == null) this.ret = Config.getInstance().getDetailBlockRGBBackground();
		
		if (position.getAmount() < 0d)
		{
			this.values[0].setBackground(Color.WHITE);
			this.values[1].setForeground(this.expense);
		}
		else if (position.getQuantity() < 0)
		{
			this.values[0].setBackground(new Color(this.ret.red, this.ret.green, this.ret.blue));
			this.values[1].setForeground(this.normal);
		}
		else
		{
			this.values[0].setBackground(Color.WHITE);
			this.values[1].setForeground(this.normal);
		}
	}
	
	// 10223
	
	public PosButton getButton(String key)
	{
		return (PosButton) this.buttons.get(key);
	}
	
	private float size = 16F;
	private int style = 1;
	private JTable table;
	
	private JLabel[] labels = new JLabel[6];
	private JLabel[] values = new JLabel[6];
	private JComponent currentHighlighted = null;
	private Hashtable buttons = new Hashtable();
	private Color highlightColor;
	private Color originalColor;
	
	private Color expense = null;
	private Color back = null;
	private Color normal = null;
	private RGB ret = null;
}
