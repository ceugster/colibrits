/*
 * Created on 01.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.admin;

import java.util.ArrayList;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import ch.eugster.pos.Messages;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ConnectionDatabaseWizardPage extends WizardPage implements Listener
{
	
	/**
	 * @param pageName
	 */
	public ConnectionDatabaseWizardPage(String pageName)
	{
		super(pageName);
		this.init();
	}
	
	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public ConnectionDatabaseWizardPage(String pageName, String title, ImageDescriptor titleImage)
	{
		super(pageName, title, titleImage);
		this.init();
	}
	
	public void init()
	{
		this
						.setMessage(Messages
										.getString("ConnectionDatabaseWizardPage.Geben_Sie_den_Namen_der_Datenbank,_den_Benutzernamen_und_das_Passwort_f_u00FCr_die_Verbindungsaufnahme_an._1")); //$NON-NLS-1$
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createControl(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		
		GridLayout gl = new GridLayout(2, false);
		composite.setLayout(gl);
		
		new Label(composite, SWT.NONE).setText(Messages.getString("ConnectionDatabaseWizardPage.Host_2")); //$NON-NLS-1$
		this.hostName = new Text(composite, SWT.BORDER);
		this.hostName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.hostName.addListener(SWT.FocusOut, this);
		this.hostName.setText(Messages.getString("ConnectionDatabaseWizardPage.localhost_3")); //$NON-NLS-1$
		
		new Label(composite, SWT.NONE).setText(Messages.getString("ConnectionDatabaseWizardPage.Datenbankname_4")); //$NON-NLS-1$
		this.databaseName = new Text(composite, SWT.BORDER);
		this.databaseName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.databaseName.addListener(SWT.FocusOut, this);
		this.databaseName.setText(Messages.getString("ConnectionDatabaseWizardPage.Datenbankname_5")); //$NON-NLS-1$
		
		new Label(composite, SWT.NONE).setText(Messages.getString("ConnectionDatabaseWizardPage.Benutzername_6")); //$NON-NLS-1$
		this.username = new Text(composite, SWT.BORDER);
		this.username.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.username.setText(Messages.getString("ConnectionDatabaseWizardPage.Benutzername_7")); //$NON-NLS-1$
		
		new Label(composite, SWT.NONE).setText(Messages.getString("ConnectionDatabaseWizardPage.Passwort_8")); //$NON-NLS-1$
		this.password = new Text(composite, SWT.BORDER);
		this.password.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.username.setText(Messages.getString("ConnectionDatabaseWizardPage.Passwort_9")); //$NON-NLS-1$
		
		this.setControl(composite);
	}
	
	public void handleEvent(Event e)
	{
		this.fireEvent(e);
		
	}
	
	public String getHostName()
	{
		return this.hostName.getText();
	}
	
	public String getDatabaseName()
	{
		return this.databaseName.getText();
	}
	
	public String getUsername()
	{
		return this.username.getText();
	}
	
	public String getPassword()
	{
		return this.password.getText();
	}
	
	public boolean addListener(Listener l)
	{
		return this.listeners.add(l);
	}
	
	public boolean removeListener(Listener l)
	{
		return this.listeners.remove(l);
	}
	
	public void fireEvent(Event e)
	{
		Listener[] l = (Listener[]) this.listeners.toArray(new Listener[0]);
		for (int i = 0; i < l.length; i++)
		{
			l[i].handleEvent(e);
		}
	}
	
	private Text hostName;
	private Text databaseName;
	private Text username;
	private Text password;
	
	private ArrayList listeners = new ArrayList();
}
