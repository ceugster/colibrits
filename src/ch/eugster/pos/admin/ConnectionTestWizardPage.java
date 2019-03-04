/*
 * Created on 01.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.admin;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import ch.eugster.pos.Messages;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ConnectionTestWizardPage extends WizardPage implements Listener
{
	
	/**
	 * @param pageName
	 */
	public ConnectionTestWizardPage(String pageName)
	{
		super(pageName);
		this.init();
	}
	
	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public ConnectionTestWizardPage(String pageName, String title, ImageDescriptor titleImage)
	{
		super(pageName, title, titleImage);
		this.init();
	}
	
	public void init()
	{
		this
						.setMessage(Messages
										.getString("ConnectionTestWizardPage.Testen_Sie_die_Verbindung,_bevor_Sie_die_Einstellungen__u00FCbernehmen._1")); //$NON-NLS-1$
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
		
		new Label(composite, SWT.NONE).setText(Messages.getString("ConnectionTestWizardPage.Verbindungsparameter_2")); //$NON-NLS-1$
		this.params = new Label(composite, SWT.NONE);
		this.params.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.params.setText(this.getParameters());
		new Label(composite, SWT.NONE).setText(Messages.getString("ConnectionTestWizardPage.Verbindung_pr_u00FCfen_3")); //$NON-NLS-1$
		this.test = new Button(composite, SWT.PUSH);
		this.test.setText(Messages.getString("ConnectionTestWizardPage.Verbindung_pr_u00FCfen..._4")); //$NON-NLS-1$
		this.test.addListener(SWT.Selection, this);
		
		this.setControl(composite);
	}
	
	public void handleEvent(Event e)
	{
		if (e.widget.equals(this.test))
		{
			try
			{
				Class.forName(((ConnectionWizard) this.getWizard()).getPlatformPage().getDriver());
			}
			catch (ClassNotFoundException cnfe)
			{
				MessageDialog
								.openError(
												this.getShell(),
												Messages
																.getString("ConnectionTestWizardPage.Treiber_nicht_verf_u00FCgbar_5"), Messages.getString("ConnectionTestWizardPage.Der_gew_u00E4hlte_Treiber_ist_nicht_verf_u00FCgbar._6")); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
			
			Properties props = new Properties();
			props
							.put(
											Messages.getString("ConnectionTestWizardPage.user_7"), ((ConnectionWizard) this.getWizard()).getDatabasePage().getUsername()); //$NON-NLS-1$
			props
							.put(
											Messages.getString("ConnectionTestWizardPage.password_8"), ((ConnectionWizard) this.getWizard()).getDatabasePage().getPassword()); //$NON-NLS-1$
			
			Connection con = null;
			try
			{
				con = DriverManager.getConnection(this.params.getText(), props);
			}
			catch (SQLException se)
			{
				MessageDialog
								.openError(
												this.getShell(),
												Messages.getString("ConnectionTestWizardPage.Verbindungsfehler_9"), Messages.getString("ConnectionTestWizardPage.Die_Verbindung_zur_Datenbank_konnte_nicht_hergestellt_werden._n_10") + se.getSQLState() + System.getProperty("line.separator") + se.getLocalizedMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				return;
			}
			
			ResultSet rs = null;
			try
			{
				DatabaseMetaData dmd = con.getMetaData();
				rs = dmd.getTables(null, null, Messages.getString("ConnectionTestWizardPage.version_12"), null); //$NON-NLS-1$
				rs.close();
				con.close();
			}
			catch (SQLException sqle)
			{
				MessageDialog
								.openError(
												this.getShell(),
												Messages.getString("ConnectionTestWizardPage.Abfragefehler_13"), Messages.getString("ConnectionTestWizardPage.Der_Versuch,_eine_Abfrage_an_die_Datenbank_zu_schicken,_ist_fehlgeschlagen._n_14") + sqle.getSQLState() + System.getProperty("line.separator") + sqle.getLocalizedMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				return;
			}
			MessageDialog
							.openInformation(
											this.getShell(),
											Messages.getString("ConnectionTestWizardPage.Verbindung_OK_16"), Messages.getString("ConnectionTestWizardPage.Die_Verbindung_zur_Datenbank_konnte_erfolgreich_hergestellt_werden._17")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
		{
			this.params.setText(this.getParameters());
		}
	}
	
	public String getParameters()
	{
		StringBuffer sb = new StringBuffer(((ConnectionWizard) this.getWizard()).getPlatformPage().getProtocol());
		sb.append(":"); //$NON-NLS-1$
		sb.append(((ConnectionWizard) this.getWizard()).getPlatformPage().getSubprotocol());
		sb.append("://"); //$NON-NLS-1$
		sb.append(((ConnectionWizard) this.getWizard()).getDatabasePage().getHostName());
		sb.append(":"); //$NON-NLS-1$
		sb.append(((ConnectionWizard) this.getWizard()).getPlatformPage().getPort());
		if (!((ConnectionWizard) this.getWizard()).getDatabasePage().getDatabaseName().equals("")) { //$NON-NLS-1$
			sb.append("/"); //$NON-NLS-1$
			sb.append(((ConnectionWizard) this.getWizard()).getDatabasePage().getDatabaseName());
		}
		sb.append(((ConnectionWizard) this.getWizard()).getPlatformPage().getOptions());
		return sb.toString();
	}
	
	private Label params;
	private Button test;
}
