/*
 * Created on 11.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.client.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.plaf.BorderUIResource;

/**
 * @author Administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ClockBlock_old extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 0l;
	
	/**
	 * @param layout
	 * @param isDoubleBuffered
	 */
	public ClockBlock_old(LayoutManager layout, boolean isDoubleBuffered, float size)
	{
		super(layout, isDoubleBuffered);
		this.init(size);
	}
	
	/**
	 * @param layout
	 */
	public ClockBlock_old(LayoutManager layout, float size)
	{
		super(layout);
		this.init(size);
	}
	
	/**
	 * @param isDoubleBuffered
	 */
	public ClockBlock_old(boolean isDoubleBuffered, float size)
	{
		super(isDoubleBuffered);
		this.init(size);
	}
	
	/**
	 * 
	 */
	public ClockBlock_old(float size)
	{
		super();
		this.init(size);
	}
	
	public ClockBlock_old()
	{
		super();
		this.init();
	}
	
	private void init()
	{
		this.init(this.getFont().getSize2D());
	}
	
	private void init(float size)
	{
		this.setLayout(new BorderLayout());
		this.setBorder(BorderUIResource.getEtchedBorderUIResource());
		this.date = new JLabel(this.dateFormat.format(new Date()));
		this.date.setFont(this.date.getFont().deriveFont(Font.BOLD, size));
		this.add(this.date, BorderLayout.EAST);
		this.time = new JLabel(this.timeFormat.format(new Date()));
		this.time.setFont(this.time.getFont().deriveFont(Font.BOLD, size));
		this.add(this.time, BorderLayout.WEST);
		
		this.timer.addActionListener(this);
		this.timer.start();
	}
	
	public void finalize()
	{
		this.timer.stop();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		this.date.setText(this.dateFormat.format(new Date()));
		this.time.setText(this.timeFormat.format(new Date()));
	}
	
	private Timer timer = new Timer(1000, this);
	private JLabel date;
	private JLabel time;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy"); //$NON-NLS-1$
	private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss"); //$NON-NLS-1$
}
