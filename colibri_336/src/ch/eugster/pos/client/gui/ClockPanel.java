/*
 * Created on 19.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.client.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.plaf.BorderUIResource;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import ch.eugster.pos.ExceptionThrownEvent;
import ch.eugster.pos.ExceptionThrownListener;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.Version;
import ch.eugster.pos.events.StateChangeEvent;
import ch.eugster.pos.events.StateChangeListener;
import ch.eugster.pos.util.Config;
import ch.eugster.pos.util.Path;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ClockPanel extends Panel implements StateChangeListener, ExceptionThrownListener
{
	public static final long serialVersionUID = 0l;
	
	private JTable table;
	private TableModel stateModel;
	
	/**
	 * @param layout
	 * @param isDoubleBuffered
	 * @param parent
	 */
	public ClockPanel(LayoutManager layout, boolean isDoubleBuffered, TabPanel parent)
	{
		super(layout, isDoubleBuffered, parent);
		this.init();
	}
	
	/**
	 * @param layout
	 * @param parent
	 */
	public ClockPanel(LayoutManager layout, TabPanel parent)
	{
		super(layout, parent);
		this.init();
	}
	
	/**
	 * @param isDoubleBuffered
	 * @param parent
	 */
	public ClockPanel(boolean isDoubleBuffered, TabPanel parent)
	{
		super(isDoubleBuffered, parent);
		this.init();
	}
	
	/**
	 * @param parent
	 */
	public ClockPanel(TabPanel parent)
	{
		super(parent);
		this.init();
	}
	
	private void init()
	{
		this.setLayout(new GridLayout(2, 1));
		
		JPanel versionPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JPanel errorIconPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		
		Config cfg = Config.getInstance();
		
		JLabel versionLabel = new JLabel("Version: " + Version.version());
		versionPanel.add(versionLabel);
		
		this.icons[0] = new ImageIcon(Path.getInstance().iconDir + "states" + File.separator + "grey.gif");
		this.icons[1] = new ImageIcon(Path.getInstance().iconDir + "states" + File.separator + "yellow.gif");
		this.icons[2] = new ImageIcon(Path.getInstance().iconDir + "states" + File.separator + "green.gif");
		
		JLabel nameLabel = new JLabel("Spalte 'Status':");
		iconPanel.add(nameLabel);
		JLabel activeLabel = new JLabel("Aktiv", this.icons[2], JLabel.LEADING);
		iconPanel.add(activeLabel);
		JLabel standbyLabel = new JLabel("StandBy", this.icons[1], JLabel.LEADING);
		iconPanel.add(standbyLabel);
		JLabel inactiveLabel = new JLabel("Inaktiv", this.icons[0], JLabel.LEADING);
		iconPanel.add(inactiveLabel);
		
		this.errorIcons[0] = new ImageIcon(Path.getInstance().iconDir + "states" + File.separator + "grey.gif");
		this.errorIcons[1] = new ImageIcon(Path.getInstance().iconDir + "states" + File.separator + "red.gif");
		
		nameLabel = new JLabel("Spalte 'Fehler':");
		errorIconPanel.add(nameLabel);
		JLabel noErrorLabel = new JLabel("OK", this.errorIcons[0], JLabel.LEADING);
		errorIconPanel.add(noErrorLabel);
		JLabel errorLabel = new JLabel("Fehler", this.errorIcons[1], JLabel.LEADING);
		errorIconPanel.add(errorLabel);
		
		final String[] items = new String[]
		{ "Hauptdatenbank", "Ersatzdatenbank", "Schulungsdatenbank", "Code128", "   mit Titeldatensuche", "Galileo", "   mit CD-Suche", "   mit Scannerpanel",
						"   mit Warenbewirtschaftung" };
		
		this.states = new ImageIcon[items.length];
		this.errorStates = new ImageIcon[items.length];
		
		String standard = Database.getStandard().getConnectionString();
		String temporary = Database.getTemporary().getConnectionString();
		String tutorial = Database.getTutorial().getConnectionString();
		String galileo = cfg.getGalileoPath();
		String cdrom = cfg.getGalileoCdPath();
		
		final String[] notices = new String[]
		{ standard, temporary, tutorial, "", "", galileo, cdrom, "", "" };
		
		TableColumnModel tableColumns = new DefaultTableColumnModel();
		
		TableColumn column = new TableColumn(0);
		column.setHeaderValue("Funktion");
		tableColumns.addColumn(column);
		
		column = new TableColumn(1);
		column.setHeaderValue("Status");
		
		tableColumns.addColumn(column);
		
		column = new TableColumn(2);
		column.setHeaderValue("Fehler");
		tableColumns.addColumn(column);
		
		column = new TableColumn(3);
		column.setHeaderValue("Pfad");
		tableColumns.addColumn(column);
		
		this.stateModel = new AbstractTableModel()
		{
			public static final long serialVersionUID = 0l;
			
			public int getColumnCount()
			{
				return 4;
			}
			
			public int getRowCount()
			{
				return items.length;
			}
			
			public Object getValueAt(int row, int col)
			{
				switch (col)
				{
					case 0:
						return items[row];
					case 1:
						return ClockPanel.this.states[row];
					case 2:
						return ClockPanel.this.errorStates[row];
					case 3:
						return notices[row];
						
					default:
						return null;
				}
			}
			
			public Class getColumnClass(int columnIndex)
			{
				switch (columnIndex)
				{
					case 1:
						return ImageIcon.class;
					case 2:
						return ImageIcon.class;
						
					default:
						return Object.class;
				}
			}
			
			public void setValueAt(Object object, int row, int col)
			{
				if (col == 1)
				{
					ClockPanel.this.states[row] = (ImageIcon) object;
				}
				else if (col == 2) ClockPanel.this.errorStates[row] = (ImageIcon) object;
				
				this.fireTableRowsUpdated(row, row);
			}
		};
		
		this.table = new JTable(this.stateModel, tableColumns);
		
		FontMetrics metrics = this.table.getFontMetrics(this.table.getFont());
		int width = 0;
		int plus = 10;
		for (int i = 0; i < items.length; i++)
			if (metrics.stringWidth(items[i]) + plus > width) width = metrics.stringWidth(items[i] + plus);
		this.table.getColumnModel().getColumn(0).setPreferredWidth(width);
		this.table.getColumnModel().getColumn(0).setMaxWidth(width);
		
		width = metrics.stringWidth("Status") + plus;
		this.table.getColumnModel().getColumn(1).setPreferredWidth(width);
		this.table.getColumnModel().getColumn(1).setMaxWidth(width);
		
		width = metrics.stringWidth("Fehler") + plus;
		this.table.getColumnModel().getColumn(2).setPreferredWidth(width);
		this.table.getColumnModel().getColumn(2).setMaxWidth(width);
		
		this.stateModel.addTableModelListener(this.table);
		JScrollPane scrollpane = new JScrollPane(this.table);
		
		// GridBagLayout gridbag = new GridBagLayout();
		// GridBagConstraints gbc = new GridBagConstraints();
		// gbc.fill = GridBagConstraints.
		//		
		// JPanel statePanel = new JPanel(gridbag);
		// this.states = new JLabel[items.length];
		// this.errorStates = new JLabel[items.length];
		//		
		// for (int i = 0; i < items.length; i++)
		// {
		// JLabel name = new JLabel(items[i]);
		// statePanel.add(name);
		//			
		// if (i == 0)
		// {
		// JLabel label = new JLabel("Status");
		// label.setHorizontalAlignment(JLabel.CENTER);
		// this.states[i] = label;
		// }
		// else
		// {
		// JLabel label = new JLabel(this.icons[0]);
		// label.setHorizontalAlignment(JLabel.CENTER);
		// this.states[i] = label;
		// }
		// statePanel.add(this.states[i]);
		//			
		// if (i == 0)
		// {
		// JLabel label = new JLabel("Fehler");
		// label.setHorizontalAlignment(JLabel.CENTER);
		// this.errorStates[i] = label;
		// }
		// else
		// {
		// JLabel label = new JLabel(this.icons[0]);
		// label.setHorizontalAlignment(JLabel.CENTER);
		// this.errorStates[i] = label;
		// }
		// statePanel.add(this.errorStates[i]);
		//			
		// name = new JLabel(notices[i]);
		// statePanel.add(name);
		// }
		//		
		// this.add(new JPanel(), BorderLayout.WEST);
		// this.add(new JPanel(), BorderLayout.EAST);
		
		// JPanel statePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		// statePanel.add(scrollpane);
		
		JPanel explanationPanel = new JPanel(new GridLayout(3, 1));
		explanationPanel.add(versionPanel);
		explanationPanel.add(iconPanel);
		explanationPanel.add(errorIconPanel);
		
		JPanel statePanel = new JPanel(new BorderLayout());
		statePanel.add(scrollpane, BorderLayout.CENTER);
		statePanel.add(explanationPanel, BorderLayout.SOUTH);
		
		this.add(statePanel);
		
		// this.updateStates();
		
		// JLabel version = new JLabel("Version: " + Version.version() + " D" +
		// Version.getMyDataVersion());
		// version.setHorizontalAlignment(SwingConstants.CENTER);
		// JPanel versionPanel = new JPanel();
		// versionPanel.setLayout(new BorderLayout());
		// versionPanel.add(version);
		// versionPanel.setAlignmentX(0.5f);
		// this.add(versionPanel);
		
		this.errors = new JTextArea();
		this.errors.setEditable(false);
		this.errors.setLineWrap(false);
		this.errors.setOpaque(false);
		this.errors.setRows(20);
		JScrollPane sp = new JScrollPane(this.errors);
		sp.setBorder(BorderUIResource.getLoweredBevelBorderUIResource());
		this.add(sp);
		DateFormat sdf = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT);
		this.errors.setText(sdf.format(new Date()) + ": Programm gestartet...\n");
		
		this.timer.start();
	}
	
	public void finalize()
	{
		this.timer.stop();
	}
	
	public Timer getTimer()
	{
		return this.timer;
	}
	
	public void stateChanged(StateChangeEvent e)
	{
		this.stateModel.setValueAt(this.icons[e.states[0]], e.type, 1);
		this.stateModel.setValueAt(this.errorIcons[e.states[1]], e.type, 2);
		if (e.states[1] == 1) this.err = true;
		// this.states[e.type].setIcon(this.icons[e.states[0]]);
		// this.errorStates[e.type].setIcon(this.errorIcons[e.states[1]]);
		// if (e.states[1] == 1) this.err = true;
	}
	
	public void updateStates()
	{
		this.stateChanged(new StateChangeEvent(StateChangeEvent.STANDARD_DATABASE));
		this.stateChanged(new StateChangeEvent(StateChangeEvent.TEMPORARY_DATABASE));
		this.stateChanged(new StateChangeEvent(StateChangeEvent.TUTORIAL_DATABASE));
		this.stateChanged(new StateChangeEvent(StateChangeEvent.CODE128));
		this.stateChanged(new StateChangeEvent(StateChangeEvent.TITLE));
		this.stateChanged(new StateChangeEvent(StateChangeEvent.GALILEO));
		this.stateChanged(new StateChangeEvent(StateChangeEvent.CD));
		this.stateChanged(new StateChangeEvent(StateChangeEvent.SCANPANEL));
		this.stateChanged(new StateChangeEvent(StateChangeEvent.STOCK));
		this.table.repaint();
	}
	
	public void exceptionThrown(ExceptionThrownEvent e)
	{
		this.updateStates();
		this.errors.setText(this.errors.getText() + e.message);
	}
	
	public boolean isError()
	{
		return this.err;
	}
	
	private ImageIcon[] states;
	private ImageIcon[] errorStates;
	private ImageIcon[] icons = new ImageIcon[3];
	private ImageIcon[] errorIcons = new ImageIcon[2];
	private JTextArea errors;
	private Timer timer = new Timer(1000, this);
	private boolean err = false;
	
}
