/*
 * Created on 11.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.plaf.BorderUIResource;

import ch.eugster.pos.Messages;
import ch.eugster.pos.client.App;

/**
 * @author Administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LoginBlock extends JPanel implements ActionListener, KeyListener
{
	private static final long serialVersionUID = 0l;
	
	public LoginBlock()
	{
		super(new BorderLayout());
		this.init();
	}
	
	private void init()
	{
		JPanel northPanel = new JPanel(new BorderLayout());
		JLabel label = new JLabel(LoginBlock.LABEL_TEXT);
		label.setFont(label.getFont().deriveFont(16F));
		label.setBorder(BorderUIResource.getEtchedBorderUIResource());
		this.display = new JPasswordField();
		this.display.setEnabled(false);
		this.display.setFont(this.display.getFont().deriveFont(24F));
		this.display.setBorder(BorderUIResource.getEtchedBorderUIResource());
		northPanel.add(label, BorderLayout.NORTH);
		northPanel.add(this.display, BorderLayout.CENTER);
		
		JPanel centerPanel = new JPanel(new GridLayout(2, 1)); // Container fuer
		// panels
		JPanel centerNorthPanel = new JPanel(new GridLayout(2, 4)); // Container
		// fuer die
		// ersten 8
		// Buttons
		JPanel centerSouthPanel = new JPanel(new GridLayout(1, 2));
		JPanel centerSouthWestPanel = new JPanel(new GridLayout(2, 2));
		JPanel centerSouthEastPanel = new JPanel(new GridLayout(1, 2));
		JPanel centerSouthEastWestPanel = new JPanel(new GridLayout(2, 1));
		JPanel centerSouthEastEastPanel = new JPanel(new GridLayout(1, 1));
		
		centerSouthEastPanel.add(centerSouthEastWestPanel);
		centerSouthEastPanel.add(centerSouthEastEastPanel);
		centerSouthPanel.add(centerSouthWestPanel);
		centerSouthPanel.add(centerSouthEastPanel);
		centerPanel.add(centerNorthPanel);
		centerPanel.add(centerSouthPanel);
		
		centerNorthPanel.add(this.makeButton("7")); //$NON-NLS-1$
		centerNorthPanel.add(this.makeButton("8")); //$NON-NLS-1$
		centerNorthPanel.add(this.makeButton("9")); //$NON-NLS-1$
		this.exit = this.makeButton(LoginBlock.TEXT_EXIT, LoginBlock.ACTION_COMMAND_EXIT);
		centerNorthPanel.add(this.exit);
		
		centerNorthPanel.add(this.makeButton("4")); //$NON-NLS-1$
		centerNorthPanel.add(this.makeButton("5")); //$NON-NLS-1$
		centerNorthPanel.add(this.makeButton("6")); //$NON-NLS-1$
		this.clear = this.makeButton(LoginBlock.TEXT_CLEAR, LoginBlock.ACTION_COMMAND_CLEAR);
		centerNorthPanel.add(this.clear);
		
		centerSouthWestPanel.add(this.makeButton("1")); //$NON-NLS-1$
		centerSouthWestPanel.add(this.makeButton("2")); //$NON-NLS-1$
		centerSouthEastWestPanel.add(this.makeButton("3")); //$NON-NLS-1$
		this.enter = this.makeButton(LoginBlock.TEXT_ENTER, LoginBlock.ACTION_COMMAND_ENTER);
		centerSouthEastEastPanel.add(this.enter);
		
		centerSouthWestPanel.add(this.makeButton("0")); //$NON-NLS-1$
		centerSouthWestPanel.add(this.makeButton("00")); //$NON-NLS-1$
		centerSouthEastWestPanel.add(this.makeButton("000")); //$NON-NLS-1$
		
		this.add(northPanel, BorderLayout.NORTH);
		this.add(centerPanel, BorderLayout.CENTER);
		
	}
	
	private LoginButton makeButton(String text)
	{
		return this.makeButton(text, text);
	}
	
	private LoginButton makeButton(String text, String actionCommand)
	{
		LoginButton b = new LoginButton(text);
		b.setActionCommand(actionCommand);
		b.setForeground(new Color(28, 93, 56));
		b.setBackground(new Color(240, 235, 206));
		b.setHorizontalAlignment(JButton.LEADING);
		b.setVerticalAlignment(JButton.TOP);
		b.addActionListener(this);
		b.addKeyListener(this);
		return b;
	}
	
	public void keyTyped(KeyEvent e)
	{
	}
	
	public void keyPressed(KeyEvent e)
	{
		if ("0123456789.".indexOf(String.valueOf(e.getKeyChar())) > -1) { //$NON-NLS-1$
			this.value = this.value.concat(String.valueOf(e.getKeyChar()));
			this.display(e.getKeyChar());
			return;
		}
		else if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			this.enter.doClick();
		}
		else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
		{
			this.clear.doClick();
		}
	}
	
	public void keyReleased(KeyEvent e)
	{
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() instanceof LoginButton)
		{
			if (e.getActionCommand().equals(LoginBlock.ACTION_COMMAND_ENTER))
			{
				e.setSource(this);
				this.fireActionEvent(e);
				this.initValue();
			}
			else if (e.getActionCommand().equals(LoginBlock.ACTION_COMMAND_CLEAR))
			{
				this.initValue();
			}
			else if (e.getActionCommand().equals(LoginBlock.ACTION_COMMAND_EXIT))
			{
				App.getApp().getFrame().closeApplication();
			}
			else
			{
				this.value = this.value.concat(e.getActionCommand());
			}
			this.display(this.value);
		}
	}
	
	public void display(char c)
	{
		String password = String.valueOf(this.display.getPassword()).concat(String.valueOf(c));
		this.display(password);
	}
	
	public void display(String text)
	{
		this.display.setText(text);
	}
	
	public String moveValue()
	{
		String s = this.readValue();
		this.initValue();
		return s;
	}
	
	public String readValue()
	{
		return this.value;
	}
	
	public Long getPosLogin()
	{
		Long l = new Long(0L);
		try
		{
			l = new Long(this.readValue());
		}
		catch (NumberFormatException e)
		{
		}
		return l;
	}
	
	private void initValue()
	{
		this.value = ""; //$NON-NLS-1$
	}
	
	public void fireActionEvent(ActionEvent e)
	{
		Iterator i = this.actionListeners.iterator();
		while (i.hasNext())
		{
			((ActionListener) i.next()).actionPerformed(e);
		}
	}
	
	public boolean addActionListener(ActionListener listener)
	{
		return this.actionListeners.add(listener);
	}
	
	public boolean removeActionListener(ActionListener listener)
	{
		return this.actionListeners.remove(listener);
	}
	
	private class LoginButton extends JButton
	{
		private static final long serialVersionUID = 0l;
		
		LoginButton(String text)
		{
			super(text);
		}
	}
	
	private String value = ""; //$NON-NLS-1$
	private LoginButton exit;
	private LoginButton enter;
	private LoginButton clear;
	private JPasswordField display = new JPasswordField();
	private ArrayList actionListeners = new ArrayList();
	
	public static final String LABEL_TEXT = Messages.getString("LoginBlock.Passwort_16"); //$NON-NLS-1$
	public static final String TEXT_EXIT = Messages.getString("LoginBlock.Beenden_17"); //$NON-NLS-1$
	public static final String TEXT_CLEAR = Messages.getString("LoginBlock.L_u00F6schen_18"); //$NON-NLS-1$
	public static final String TEXT_ENTER = Messages.getString("LoginBlock.Eingabe_19"); //$NON-NLS-1$
	
	public static final String ACTION_COMMAND_EXIT = "exit"; //$NON-NLS-1$
	public static final String ACTION_COMMAND_CLEAR = "clear"; //$NON-NLS-1$
	public static final String ACTION_COMMAND_ENTER = "enter"; //$NON-NLS-1$
}
