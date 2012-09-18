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
import org.eclipse.swt.widgets.Combo;
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
public class ConnectionPlatformWizardPage extends WizardPage implements Listener
{
	
	/**
	 * @param arg0
	 */
	public ConnectionPlatformWizardPage(String pageName)
	{
		super(pageName);
		this.init();
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public ConnectionPlatformWizardPage(String pageName, String title, ImageDescriptor image)
	{
		super(pageName, title, image);
		this.init();
	}
	
	public void init()
	{
		this
						.setMessage(Messages
										.getString("ConnectionPlatformWizardPage.W_u00E4hlen_Sie_den_gew_u00FCnschten_Datenbankserver,_mit_dem_standardm_u00E4ssig_nVerbindung_aufgenommen_werden_soll._1")); //$NON-NLS-1$
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
		
		new Label(composite, SWT.NONE).setText(Messages.getString("ConnectionPlatformWizardPage.Datenbankserver_2")); //$NON-NLS-1$
		this.platforms = new Combo(composite, SWT.SINGLE + SWT.READ_ONLY);
		this.platforms.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.platforms.setItems(((ConnectionWizard) this.getWizard()).getPlatforms());
		this.platforms.addListener(SWT.Selection, this);
		this.platforms.select(0);
		
		new Label(composite, SWT.NONE).setText(Messages.getString("ConnectionPlatformWizardPage.Treiber_3")); //$NON-NLS-1$
		this.driver = new Text(composite, SWT.BORDER);
		this.driver.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.driver.addListener(SWT.FocusOut, this);
		this.driver.setText(((ConnectionWizard) this.getWizard()).getDriver(this.platforms.getText()));
		
		new Label(composite, SWT.NONE).setText(Messages.getString("ConnectionPlatformWizardPage.Jdbc-Level_4")); //$NON-NLS-1$
		this.level = new Combo(composite, SWT.SINGLE + SWT.READ_ONLY);
		this.level.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.level
						.setItems(new String[]
						{
										Messages.getString("ConnectionPlatformWizardPage.1.0_5"), Messages.getString("ConnectionPlatformWizardPage.1.2_6"), Messages.getString("ConnectionPlatformWizardPage.2.0_7"), Messages.getString("ConnectionPlatformWizardPage.3.0_8"), Messages.getString("ConnectionPlatformWizardPage.4.0_9") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		this.level.addListener(SWT.Selection, this);
		this.level.select(3);
		
		new Label(composite, SWT.NONE).setText(Messages.getString("ConnectionPlatformWizardPage.Protokoll_10")); //$NON-NLS-1$
		this.protocol = new Text(composite, SWT.BORDER);
		this.protocol.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.protocol.addListener(SWT.FocusOut, this);
		this.protocol.setText(((ConnectionWizard) this.getWizard()).getProtocol(this.platforms.getText()));
		
		new Label(composite, SWT.NONE).setText(Messages.getString("ConnectionPlatformWizardPage.Subprotokoll_11")); //$NON-NLS-1$
		this.subprotocol = new Text(composite, SWT.BORDER);
		this.subprotocol.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.subprotocol.addListener(SWT.FocusOut, this);
		this.subprotocol.setText(((ConnectionWizard) this.getWizard()).getSubprotocol(this.platforms.getText()));
		
		new Label(composite, SWT.NONE).setText(Messages.getString("ConnectionPlatformWizardPage.Port_12")); //$NON-NLS-1$
		this.port = new Text(composite, SWT.BORDER);
		this.port.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.port.addListener(SWT.FocusOut, this);
		this.port.setText(((ConnectionWizard) this.getWizard()).getPort(this.platforms.getText()));
		
		new Label(composite, SWT.NONE).setText(Messages.getString("ConnectionPlatformWizardPage.Optionen_13")); //$NON-NLS-1$
		this.options = new Text(composite, SWT.BORDER);
		this.options.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.options.addListener(SWT.FocusOut, this);
		
		this.setControl(composite);
	}
	
	public void handleEvent(Event e)
	{
		if (e.widget.equals(this.platforms))
		{
			this.driver.setText(((ConnectionWizard) this.getWizard()).getDriver(this.platforms.getText()));
			this.protocol.setText(((ConnectionWizard) this.getWizard()).getProtocol(this.platforms.getText()));
			this.subprotocol.setText(((ConnectionWizard) this.getWizard()).getSubprotocol(this.platforms.getText()));
			this.port.setText(((ConnectionWizard) this.getWizard()).getPort(this.platforms.getText()));
		}
		this.fireEvent(e);
		
	}
	
	public Combo getPlatformCombo()
	{
		return this.platforms;
	}
	
	public String getSelectedPlatform()
	{
		return this.platforms.getText();
	}
	
	public String getDriver()
	{
		return this.driver.getText();
	}
	
	public String getLevel()
	{
		return this.level.getText();
	}
	
	public String getProtocol()
	{
		return this.protocol.getText();
	}
	
	public String getSubprotocol()
	{
		return this.subprotocol.getText();
	}
	
	public String getPort()
	{
		return this.port.getText();
	}
	
	public String getOptions()
	{
		return this.options.getText();
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
	
	private Combo platforms;
	private Text driver;
	private Combo level;
	private Text protocol;
	private Text subprotocol;
	private Text port;
	private Text options;
	
	private ArrayList listeners = new ArrayList();
}
