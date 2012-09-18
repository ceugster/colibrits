/*
 * Created on 30.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.admin;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jface.wizard.Wizard;
import org.jdom.Document;
import org.jdom.Element;

import ch.eugster.pos.Messages;
import ch.eugster.pos.util.Config;
import ch.eugster.pos.util.JdbcDefaultHandler;
import ch.eugster.pos.util.OjbRepositoryHandler;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ConnectionWizard extends Wizard
{
	
	/**
	 * 
	 */
	public ConnectionWizard()
	{
		super();
		this.init();
	}
	
	private void init()
	{
		JdbcDefaultHandler jdh = JdbcDefaultHandler.getJdbcDefaultHandler();
		this.jdbcDefaults = jdh.getJdbcDefaults();
		Element doc = this.jdbcDefaults.getRootElement();
		
		List pl = doc.getChildren("platform"); //$NON-NLS-1$
		this.sources = new String[pl.size()];
		this.platforms = new Element[pl.size()];
		Iterator iter = pl.iterator();
		for (int i = 0; i < this.sources.length; i++)
		{
			Element e = (Element) iter.next();
			this.sources[i] = e.getAttributeValue("id"); //$NON-NLS-1$
			this.platforms[i] = e;
		}
		
		StringTokenizer st = new StringTokenizer(doc.getChild("jdbc-levels").getAttributeValue("levels"), ","); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.levels = new String[st.countTokens()];
		int i = 0;
		while (st.hasMoreTokens())
		{
			String value = st.nextToken();
			this.levels[i++] = value;
		}
		
		this.cfg = Config.getInstance();
		this.std = this.cfg.getDatabaseStandardConnection();
		this.std.setAttribute("default-connection", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		Element tmp = this.cfg.getDatabaseStandardConnection();
		tmp.setAttribute("default-connection", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		Element tut = this.cfg.getDatabaseStandardConnection();
		tut.setAttribute("default-connection", "false"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish()
	{
		this.std.setAttribute("platform", this.platformPage.getSelectedPlatform()); //$NON-NLS-1$
		this.std.setAttribute("jdbc-level", this.platformPage.getLevel()); //$NON-NLS-1$
		this.std.setAttribute("driver", this.platformPage.getDriver()); //$NON-NLS-1$
		this.std.setAttribute("protocol", this.platformPage.getProtocol()); //$NON-NLS-1$
		this.std.setAttribute("subprotocol", this.platformPage.getSubprotocol()); //$NON-NLS-1$
		this.std.setAttribute("host", this.databasePage.getHostName()); //$NON-NLS-1$
		this.std.setAttribute("port", this.platformPage.getPort()); //$NON-NLS-1$
		this.std.setAttribute("database", this.databasePage.getDatabaseName()); //$NON-NLS-1$
		this.std.setAttribute("options", this.platformPage.getOptions()); //$NON-NLS-1$
		this.std.setAttribute("username", this.databasePage.getUsername()); //$NON-NLS-1$
		this.std.setAttribute("password", this.databasePage.getPassword()); //$NON-NLS-1$
		
		this.cfg.save();
		
		OjbRepositoryHandler ojbHandler = OjbRepositoryHandler.getOjbRepositoryHandler();
		Document ojbRepository = ojbHandler.getOjbRepository();
		Element root = ojbRepository.getRootElement();
		List elements = root.getChildren("jdbc-connection-descriptor"); //$NON-NLS-1$
		Iterator it = elements.iterator();
		while (it.hasNext())
		{
			Element next = (Element) it.next();
			if (next.getAttributeValue("jcd-alias").equals("standard")) { //$NON-NLS-1$ //$NON-NLS-2$
				ojbHandler.copyFromConfiguration(next, this.std);
			}
			else
			{
				next.setAttribute("default-connection", "false"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		ojbHandler.saveOjbRepository(ojbRepository);
		
		return true;
	}
	
	public void addPages()
	{
		this.platformPage = new ConnectionPlatformWizardPage(
						"platform", Messages.getString("ConnectionWizard.Auswahl_Datenbank_2"), null); //$NON-NLS-1$ //$NON-NLS-2$
		this.addPage(this.platformPage);
		this.databasePage = new ConnectionDatabaseWizardPage(
						"database", Messages.getString("ConnectionWizard.Datenbank_4"), null); //$NON-NLS-1$ //$NON-NLS-2$
		this.addPage(this.databasePage);
		this.testPage = new ConnectionTestWizardPage(
						"test", Messages.getString("ConnectionWizard.Testen_der_Verbindung_6"), null); //$NON-NLS-1$ //$NON-NLS-2$
		this.addPage(this.testPage);
		
		this.platformPage.addListener(this.testPage);
		this.databasePage.addListener(this.testPage);
	}
	
	public String[] getPlatforms()
	{
		return this.sources;
	}
	
	public String[] getLevels()
	{
		return this.levels;
	}
	
	private Element getPlatform(String platform)
	{
		for (int i = 0; i < this.platforms.length; i++)
		{
			if (this.platforms[i].getAttributeValue("id").equals(platform)) { //$NON-NLS-1$
				return this.platforms[i];
			}
		}
		return null;
	}
	
	public String getDriver(String platform)
	{
		Element element = this.getPlatform(platform);
		if (element != null)
		{
			return element.getAttributeValue("driver"); //$NON-NLS-1$
		}
		return ""; //$NON-NLS-1$
	}
	
	public String getLevel(String platform)
	{
		Element element = this.getPlatform(platform);
		if (element != null)
		{
			return element.getAttributeValue("level"); //$NON-NLS-1$
		}
		return ""; //$NON-NLS-1$
	}
	
	public String getProtocol(String platform)
	{
		Element element = this.getPlatform(platform);
		if (element != null)
		{
			return element.getAttributeValue("protocol"); //$NON-NLS-1$
		}
		return ""; //$NON-NLS-1$
	}
	
	public String getSubprotocol(String platform)
	{
		Element element = this.getPlatform(platform);
		if (element != null)
		{
			return element.getAttributeValue("subprotocol"); //$NON-NLS-1$
		}
		return ""; //$NON-NLS-1$
	}
	
	public String getPort(String platform)
	{
		Element element = this.getPlatform(platform);
		if (element != null)
		{
			return element.getAttributeValue("port"); //$NON-NLS-1$
		}
		return ""; //$NON-NLS-1$
	}
	
	public ConnectionPlatformWizardPage getPlatformPage()
	{
		return this.platformPage;
	}
	
	public ConnectionDatabaseWizardPage getDatabasePage()
	{
		return this.databasePage;
	}
	
	public ConnectionTestWizardPage getTestPage()
	{
		return this.testPage;
	}
	
	private Config cfg = null;
	private Element std = null;
	private ConnectionPlatformWizardPage platformPage;
	private ConnectionDatabaseWizardPage databasePage;
	private ConnectionTestWizardPage testPage;
	
	private Document jdbcDefaults;
	private String[] sources;
	private String[] levels;
	private Element[] platforms;
}
