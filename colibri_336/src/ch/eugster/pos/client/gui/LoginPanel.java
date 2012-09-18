/*
 * Created on 10.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.client.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JPanel;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.User;
import ch.eugster.pos.events.LoginEvent;
import ch.eugster.pos.events.LoginListener;
import ch.eugster.pos.events.MessageDialog;

public class LoginPanel extends Panel
{
	
	private static final long serialVersionUID = 0l;
	
	/**
	 * 
	 */
	public LoginPanel(TabPanel parent)
	{
		super(parent);
		this.init();
	}
	
	private void init()
	{
		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weightx = 25;
		gbc.weighty = 20;
		
		this.add(new JPanel(), gbc, 0, 0, 1, 6);
		this.add(new JPanel(), gbc, 6, 0, 1, 6);
		this.add(new JPanel(), gbc, 1, 0, 5, 1);
		this.add(new JPanel(), gbc, 1, 5, 5, 1);
		
		gbc.weightx = 100;
		gbc.weighty = 100;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		this.loginBlock = new LoginBlock();
		this.loginBlock.addActionListener(this);
		this.parent.addKeyListener(this.loginBlock);
		this.add(this.loginBlock, gbc, 1, 3, 5, 2);
	}
	
	private void add(Component child, GridBagConstraints gbc, int x, int y, int w, int h)
	{
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = w;
		gbc.gridheight = h;
		this.add(child, gbc);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() instanceof LoginBlock)
		{
			LoginBlock b = (LoginBlock) e.getSource();
			if (e.getActionCommand().equals(LoginBlock.ACTION_COMMAND_ENTER))
			{
				User user = User.getByPosLogin(b.getPosLogin(), false);
				if (user.posLogin.equals(new Long(0L)))
				{
					MessageDialog.showInformation(Frame.getMainFrame(), Messages.getString("LoginPanel.Benutzeranmeldung_2"), //$NON-NLS-1$
									Messages.getString("LoginPanel.Keinen_Benutzer_mit_dem_eingegebenen_Anmeldecode_gefunden._1"), //$NON-NLS-1$
									0);
				}
				else
				{
					this.fireLoginEvent(new LoginEvent(this, user));
				}
			}
		}
	}
	
	public void fireLoginEvent(LoginEvent e)
	{
		Iterator i = this.loginListeners.iterator();
		while (i.hasNext())
		{
			((LoginListener) i.next()).userLoggedIn(e);
		}
	}
	
	public boolean addLoginListener(LoginListener listener)
	{
		return this.loginListeners.add(listener);
	}
	
	public boolean removeLoginListener(LoginListener listener)
	{
		return this.loginListeners.remove(listener);
	}
	
	private LoginBlock loginBlock;
	private ArrayList loginListeners = new ArrayList();
}
