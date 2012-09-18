/*
 * Created on 23.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.events;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import ch.eugster.pos.Messages;
import ch.eugster.pos.util.Path;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MessageDialog extends JDialog
{
	private static final long serialVersionUID = 0l;
	
	/**
	 * @throws java.awt.HeadlessException
	 */
	public MessageDialog() throws HeadlessException
	{
		super();
	}
	
	/**
	 * @param owner
	 * @throws java.awt.HeadlessException
	 */
	public MessageDialog(Dialog owner) throws HeadlessException
	{
		super(owner);
	}
	
	/**
	 * @param owner
	 * @param modal
	 * @throws java.awt.HeadlessException
	 */
	public MessageDialog(Dialog owner, boolean modal) throws HeadlessException
	{
		super(owner, modal);
	}
	
	/**
	 * @param owner
	 * @throws java.awt.HeadlessException
	 */
	public MessageDialog(Frame owner) throws HeadlessException
	{
		super(owner);
	}
	
	/**
	 * @param owner
	 * @param modal
	 * @throws java.awt.HeadlessException
	 */
	public MessageDialog(Frame owner, boolean modal) throws HeadlessException
	{
		super(owner, modal);
	}
	
	/**
	 * @param owner
	 * @param title
	 * @throws java.awt.HeadlessException
	 */
	public MessageDialog(Dialog owner, String title) throws HeadlessException
	{
		super(owner, title);
	}
	
	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @throws java.awt.HeadlessException
	 */
	public MessageDialog(Dialog owner, String title, boolean modal) throws HeadlessException
	{
		super(owner, title, modal);
	}
	
	/**
	 * @param owner
	 * @param title
	 * @throws java.awt.HeadlessException
	 */
	public MessageDialog(Frame owner, String title, int[] buttons, int defaultButton) throws HeadlessException
	{
		super(owner, title, true);
		this.init(buttons, defaultButton);
	}
	
	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @throws java.awt.HeadlessException
	 */
	public MessageDialog(Frame owner, String title, boolean modal) throws HeadlessException
	{
		super(owner, title, modal);
	}
	
	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 * @throws java.awt.HeadlessException
	 */
	public MessageDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) throws HeadlessException
	{
		super(owner, title, modal, gc);
	}
	
	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 */
	public MessageDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc)
	{
		super(owner, title, modal, gc);
	}
	
	private void init(int[] b, int defaultButton)
	{
		this.buttons = new JButton[b.length];
		for (int i = 0; i < b.length; i++)
		{
			this.buttons[i] = this.createButton(b[i]);
			this.buttons[i].setDefaultCapable(b[i] == defaultButton);
		}
		
		Container main = new Container();
		BorderLayout layout = new BorderLayout();
		layout.setHgap(50);
		layout.setVgap(50);
		main.setLayout(layout);
		
		main.add(new JLabel(" "), BorderLayout.NORTH); //$NON-NLS-1$
		
		this.imageLabel = new JLabel();
		main.add(this.imageLabel, BorderLayout.WEST);
		main.add(new JLabel(), BorderLayout.EAST);
		
		this.messageLabel = new JLabel();
		main.add(this.messageLabel, BorderLayout.CENTER);
		
		Container ctrl = new Container();
		ctrl.setLayout(new BorderLayout());
		
		Container btnctrl = new Container();
		btnctrl.setLayout(new GridLayout(1, this.buttons.length));
		
		for (int i = 0; i < this.buttons.length; i++)
		{
			btnctrl.add(this.buttons[i]);
		}
		
		ctrl.add(btnctrl, BorderLayout.CENTER);
		
		main.add(ctrl, BorderLayout.SOUTH);
		
		this.getContentPane().add(main);
		
		this.setResizable(false);
	}
	
	private JButton createButton(int type)
	{
		String text = ""; //$NON-NLS-1$
		String cmd = ""; //$NON-NLS-1$
		switch (type)
		{
			case BUTTON_OK:
			{
				text = Messages.getString("MessageDialog.OK_4"); //$NON-NLS-1$
				cmd = MessageDialog.BUTTON_CMD_OK;
				break;
			}
			case BUTTON_CANCEL:
			{
				text = Messages.getString("MessageDialog.Abbrechen_5"); //$NON-NLS-1$
				cmd = MessageDialog.BUTTON_CMD_CANCEL;
				break;
			}
			case BUTTON_YES:
			{
				text = Messages.getString("MessageDialog.Ja_6"); //$NON-NLS-1$
				cmd = MessageDialog.BUTTON_CMD_YES;
				break;
			}
			case BUTTON_NO:
			{
				text = Messages.getString("MessageDialog.Nein_7"); //$NON-NLS-1$
				cmd = MessageDialog.BUTTON_CMD_NO;
				break;
			}
		}
		JButton b = new JButton(text);
		b.setActionCommand(cmd);
		b.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (e.getActionCommand().equals(MessageDialog.BUTTON_CMD_OK))
				{
					MessageDialog.this.returnValue = MessageDialog.BUTTON_OK;
				}
				else if (e.getActionCommand().equals(MessageDialog.BUTTON_CMD_CANCEL))
				{
					MessageDialog.this.returnValue = MessageDialog.BUTTON_CANCEL;
				}
				if (e.getActionCommand().equals(MessageDialog.BUTTON_CMD_YES))
				{
					MessageDialog.this.returnValue = MessageDialog.BUTTON_YES;
				}
				if (e.getActionCommand().equals(MessageDialog.BUTTON_CMD_NO))
				{
					MessageDialog.this.returnValue = MessageDialog.BUTTON_NO;
				}
				MessageDialog.this.closeWindow();
			}
		});
		b.setMinimumSize(new Dimension(100, 100));
		b.setMaximumSize(new Dimension(100, 100));
		b.setPreferredSize(new Dimension(100, 100));
		return b;
	}
	
	private void closeWindow()
	{
		this.dispose();
	}
	
	public void setImage(String path)
	{
		Icon icon = new ImageIcon(path);
		this.imageLabel.setIcon(icon);
	}
	
	public void setMessage(String message)
	{
		this.messageLabel.setText(message);
	}
	
	public static void showInformation(Frame owner, String title, String message, int type)
	{
		Toolkit.getDefaultToolkit().beep();
		
		MessageDialog dialog = new MessageDialog(owner, title, new int[]
		{ MessageDialog.BUTTON_OK }, -1);
		String path = Path.getInstance().iconDir.concat("msg\\"); //$NON-NLS-1$
		switch (type)
		{
			case 0:
			{
				path = path.concat(MessageDialog.ICON_INFORMATION);
				break;
			}
			case 1:
			{
				path = path.concat(MessageDialog.ICON_QUESTION);
				break;
			}
			case 2:
			{
				path = path.concat(MessageDialog.ICON_WARN);
				break;
			}
			case 3:
			{
				path = path.concat(MessageDialog.ICON_ERROR);
				break;
			}
		}
		dialog.setImage(path);
		dialog.setModal(true);
		dialog.setMessage(message);
		dialog.pack();
		dialog.setResizable(false);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension c = dialog.getSize();
		Point p = new Point();
		p.x = (d.width - c.width) / 2;
		p.y = (d.height - c.height) / 2;
		dialog.setLocation(p);
		dialog.setDefaultCloseOperation(MessageDialog.DISPOSE_ON_CLOSE);
		dialog.show();
	}
	
	public static int showQuestion(Frame owner, String title, String message, int type, int[] buttons, int defaultButton)
	{
		Toolkit.getDefaultToolkit().beep();
		
		MessageDialog dialog = new MessageDialog(owner, title, buttons, defaultButton);
		String path = Path.getInstance().iconDir.concat("msg".concat(File.separator)); //$NON-NLS-1$
		switch (type)
		{
			case 0:
			{
				path = path.concat(MessageDialog.ICON_INFORMATION);
				break;
			}
			case 1:
			{
				path = path.concat(MessageDialog.ICON_QUESTION);
				break;
			}
			case 2:
			{
				path = path.concat(MessageDialog.ICON_WARN);
				break;
			}
			case 3:
			{
				path = path.concat(MessageDialog.ICON_ERROR);
				break;
			}
		}
		dialog.setImage(path);
		dialog.setModal(true);
		dialog.setMessage(message);
		dialog.pack();
		dialog.setResizable(false);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension c = dialog.getSize();
		Point p = new Point();
		p.x = (d.width - c.width) / 2;
		p.y = (d.height - c.height) / 2;
		dialog.setLocation(p);
		dialog.setDefaultCloseOperation(MessageDialog.DISPOSE_ON_CLOSE);
		dialog.show();
		return dialog.returnValue;
		
	}
	
	public static int showSimpleDialog(Frame owner, String title, String message, int type)
	{
		switch (type)
		{
			case 0:
			{
				MessageDialog.showInformation(owner, title, message, type);
				return 0;
			}
			case 1:
			{
				return MessageDialog.showQuestion(owner, title, message, type, new int[]
				{ MessageDialog.BUTTON_YES, MessageDialog.BUTTON_NO }, 0);
			}
			default:
				return 0;
		}
	}
	
	private int returnValue = -1;
	private JLabel imageLabel;
	private JLabel messageLabel;
	private JButton[] buttons;
	public static final int BUTTON_OK = 0;
	public static final int BUTTON_CANCEL = 1;
	public static final int BUTTON_YES = 2;
	public static final int BUTTON_NO = 3;
	
	private static final String BUTTON_CMD_OK = "ok"; //$NON-NLS-1$
	private static final String BUTTON_CMD_CANCEL = "cancel"; //$NON-NLS-1$
	private static final String BUTTON_CMD_YES = "yes"; //$NON-NLS-1$
	private static final String BUTTON_CMD_NO = "no"; //$NON-NLS-1$
	
	public static final int TYPE_INFORMATION = 0;
	public static final int TYPE_QUESTION = 1;
	public static final int TYPE_WARN = 2;
	public static final int TYPE_ERROR = 3;
	
	private static final String ICON_INFORMATION = "metal-information.gif"; //$NON-NLS-1$
	private static final String ICON_QUESTION = "metal-question.gif"; //$NON-NLS-1$
	private static final String ICON_WARN = "metal-warn.gif"; //$NON-NLS-1$
	private static final String ICON_ERROR = "metal-error.gif"; //$NON-NLS-1$
}
