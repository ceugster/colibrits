/*
 * Created on 17.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.preference;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import ch.eugster.pos.Messages;
import ch.eugster.pos.util.Path;
import ch.eugster.pos.util.XMLLoader;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ConnectionFieldEditorPreferencePage extends FieldEditorPreferencePage implements SelectionListener
{
	
	/**
	 * @param style
	 */
	public ConnectionFieldEditorPreferencePage(int style, String cfg)
	{
		super(style);
		this.init(cfg);
	}
	
	/**
	 * @param title
	 * @param style
	 */
	public ConnectionFieldEditorPreferencePage(String title, int style, String cfg)
	{
		super(title, style);
		this.init(cfg);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public ConnectionFieldEditorPreferencePage(String title, ImageDescriptor image, int style, String cfg)
	{
		super(title, image, style);
		this.init(cfg);
	}
	
	private void init(String cfg)
	{
		this.whichConnection = cfg;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors
	 * ()
	 */
	protected void createFieldEditors()
	{
		File defaults = new File(Path.getInstance().cfgDir.concat(Messages
						.getString("ConnectionFieldEditorPreferencePage.text_1"))); //$NON-NLS-1$
		String[] sources = new String[0];
		double[] levelValues = new double[0];
		String[] levelNames = new String[0];
		
		if (defaults.exists())
		{
			try
			{
				Document doc = XMLLoader.getDocument(defaults, true);
				Element root = doc.getRootElement();
				List pl = root.getChildren("platform"); //$NON-NLS-1$
				sources = new String[pl.size()];
				this.platforms = new Element[pl.size()];
				Iterator iter = pl.iterator();
				for (int i = 0; i < sources.length; i++)
				{
					this.platforms[i] = (Element) iter.next();
					sources[i] = this.platforms[i].getAttributeValue("id"); //$NON-NLS-1$
				}
				StringTokenizer st = new StringTokenizer(root.getChild("jdbc-levels").getAttributeValue("levels"), ","); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				levelValues = new double[st.countTokens()];
				levelNames = new String[st.countTokens()];
				int i = 0;
				while (st.hasMoreTokens())
				{
					String value = st.nextToken();
					levelValues[i] = Double.parseDouble(value);
					levelNames[i++] = value;
				}
			}
			catch (IOException e)
			{
			}
			catch (JDOMException e)
			{
			}
		}
		
		this.nameEditor = new StringFieldEditor(
						"database." + this.whichConnection + ".name", Messages.getString("ConnectionFieldEditorPreferencePage.text_4"), -1, this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.nameEditor.setEmptyStringAllowed(false);
		// this.nameEditor.setEnabled(false, this.getFieldEditorParent());
		this.addField(this.nameEditor);
		
		// jcdAliasEditor = new StringFieldEditor("database." + whichConnection + ".connection.jcd-alias", "Verbindungsalias", -1, getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		// jcdAliasEditor.setEmptyStringAllowed(false);
		// jcdAliasEditor.setEnabled(false, getFieldEditorParent());
		// addField(jcdAliasEditor);
		
		String[] platforms = new String[]
		{
						Messages.getString("ConnectionFieldEditorPreferencePage.text_16"), Messages.getString("ConnectionFieldEditorPreferencePage.text_17"), Messages.getString("ConnectionFieldEditorPreferencePage.text_18") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.platformEditor = new StringComboFieldEditor(
						"database." + this.whichConnection + ".connection.platform", Messages.getString("ConnectionFieldEditorPreferencePage.text_21"), this.getFieldEditorParent(), platforms); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.addField(this.platformEditor);
		
		this.levelEditor = new DoubleComboFieldEditor(
						"database." + this.whichConnection + ".connection.jdbc-level", Messages.getString("ConnectionFieldEditorPreferencePage.text_24"), this.getFieldEditorParent(), levelNames, levelValues); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.addField(this.levelEditor);
		
		this.driverEditor = new StringFieldEditor(
						"database." + this.whichConnection + ".connection.driver", Messages.getString("ConnectionFieldEditorPreferencePage.text_27"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.driverEditor.setEmptyStringAllowed(false);
		this.addField(this.driverEditor);
		
		this.protocolEditor = new StringFieldEditor(
						"database." + this.whichConnection + ".connection.protocol", Messages.getString("ConnectionFieldEditorPreferencePage.text_30"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.protocolEditor.setEmptyStringAllowed(false);
		this.addField(this.protocolEditor);
		
		this.subprotocolEditor = new StringFieldEditor(
						"database." + this.whichConnection + ".connection.subprotocol", Messages.getString("ConnectionFieldEditorPreferencePage.text_33"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.subprotocolEditor.setEmptyStringAllowed(false);
		this.addField(this.subprotocolEditor);
		
		this.hostEditor = new StringFieldEditor(
						"database." + this.whichConnection + ".connection.host", Messages.getString("ConnectionFieldEditorPreferencePage.text_36"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.hostEditor.setEmptyStringAllowed(false);
		this.addField(this.hostEditor);
		
		this.portEditor = new IntegerFieldEditor(
						"database." + this.whichConnection + ".connection.port", "Port", this.getFieldEditorParent(), 6); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.portEditor.setEmptyStringAllowed(false);
		this.addField(this.portEditor);
		
		this.databaseEditor = new StringFieldEditor(
						"database." + this.whichConnection + ".connection.database", Messages.getString("ConnectionFieldEditorPreferencePage.text_42"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.addField(this.databaseEditor);
		
		this.optionEditor = new StringFieldEditor(
						"database." + this.whichConnection + ".connection.options", Messages.getString("ConnectionFieldEditorPreferencePage.text_45"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.addField(this.optionEditor);
		
		this.usernameEditor = new StringFieldEditor(
						"database." + this.whichConnection + ".connection.username", Messages.getString("ConnectionFieldEditorPreferencePage.text_48"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.addField(this.usernameEditor);
		
		this.passwordEditor = new StringFieldEditor(
						"database." + this.whichConnection + ".connection.password", Messages.getString("ConnectionFieldEditorPreferencePage.text_51"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.addField(this.passwordEditor);
		
		String[] names = new String[]
		{
						Messages.getString("ConnectionFieldEditorPreferencePage.text_52"), Messages.getString("ConnectionFieldEditorPreferencePage.text_53"), Messages.getString("ConnectionFieldEditorPreferencePage.text_54") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		int[] values = new int[]
		{ 0, 1, 2 };
		IntegerComboFieldEditor useAutoCommitEditor = new IntegerComboFieldEditor(
						"database." + this.whichConnection + ".connection.use-auto-commit", Messages.getString("ConnectionFieldEditorPreferencePage.text_57"), this.getFieldEditorParent(), names, values); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.addField(useAutoCommitEditor);
		
		this.heartbeatEditor = new IntegerFieldEditor("database." + this.whichConnection + ".connection.heartbeat",
						"Heartbeat", this.getFieldEditorParent());
		this.addField(this.heartbeatEditor);
		
		BooleanFieldEditor activeEditor = new BooleanFieldEditor(
						"database." + this.whichConnection + ".active", Messages.getString("ConnectionFieldEditorPreferencePage.text_58"), this.getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.addField(activeEditor);
		
		//		BooleanFieldEditor defaultConnectionEditor = new BooleanFieldEditor("database." + whichConnection + ".connection.default-connection", Messages.getString("ConnectionFieldEditorPreferencePage.text_61"), getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		// addField(defaultConnectionEditor);
		//
		//		BooleanFieldEditor batchModeEditor = new BooleanFieldEditor("database." + whichConnection + ".connection.batch-mode", Messages.getString("ConnectionFieldEditorPreferencePage.text_64"), getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		// addField(batchModeEditor);
		//
		this.testConnection = new ButtonFieldEditor(
						Messages.getString("ConnectionFieldEditorPreferencePage.text_65"), this.getFieldEditorParent()); //$NON-NLS-1$
		this.testConnection.getButton().addSelectionListener(this);
		this.addField(this.testConnection);
		
	}
	
	public void createControl(Composite control)
	{
		super.createControl(control);
		this.getDefaultsButton().setVisible(false);
	}
	
	public void dispose()
	{
		if (this.testConnection != null)
		{
			this.testConnection.getButton().removeSelectionListener(this);
		}
	}
	
	public void propertyChange(PropertyChangeEvent e)
	{
		if (e.getProperty().equals("database." + this.whichConnection + ".connection.platform")) { //$NON-NLS-1$ //$NON-NLS-2$
			for (int i = 0; i < this.platforms.length; i++)
			{
				if (this.platforms[i].getAttributeValue("id").equals(this.platformEditor.getCombo().getText())) { //$NON-NLS-1$
					this.getPreferenceStore()
									.setValue("database." + this.whichConnection + ".connection.jdbc-level", Double.parseDouble(this.platforms[i].getAttributeValue("jdbc-level"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					this.levelEditor.doLoad();
					this.getPreferenceStore()
									.setValue("database." + this.whichConnection + ".connection.driver", this.platforms[i].getAttributeValue("driver")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					this.driverEditor.load();
					this.getPreferenceStore()
									.setValue("database." + this.whichConnection + ".connection.protocol", this.platforms[i].getAttributeValue("protocol")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					this.protocolEditor.load();
					this.getPreferenceStore()
									.setValue("database." + this.whichConnection + ".connection.subprotocol", this.platforms[i].getAttributeValue("subprotocol")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					this.subprotocolEditor.load();
					this.getPreferenceStore()
									.setValue("database." + this.whichConnection + ".connection.port", Integer.parseInt(this.platforms[i].getAttributeValue("port"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					this.portEditor.load();
					this.getPreferenceStore()
									.setValue("database." + this.whichConnection + ".connection.options", this.platforms[i].getAttributeValue("options")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					this.optionEditor.load();
					this.getPreferenceStore()
									.setValue("database." + this.whichConnection + ".connection.heartbeat", this.platforms[i].getAttributeValue("heartbeat")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					this.heartbeatEditor.load();
				}
			}
			if (e.getNewValue().equals(new Boolean(true)))
			{
				
			}
		}
	}
	
	public void widgetSelected(SelectionEvent e)
	{
		if (e.widget.equals(this.testConnection.getButton()))
		{
			this.testConnectionNow();
		}
		else if (e.widget.equals(this.nameEditor))
		{
		}
	}
	
	public void widgetDefaultSelected(SelectionEvent e)
	{
		this.widgetSelected(e);
	}
	
	private void testConnectionNow()
	{
		try
		{
			Class.forName(this.driverEditor.getStringValue());
		}
		catch (ClassNotFoundException cnfe)
		{
			MessageDialog.openError(
							this.getShell(),
							Messages.getString("ConnectionFieldEditorPreferencePage.text_87"), Messages.getString("ConnectionFieldEditorPreferencePage.text_88")); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		
		StringBuffer b = new StringBuffer(this.protocolEditor.getStringValue());
		b.append(":"); //$NON-NLS-1$
		b.append(this.subprotocolEditor.getStringValue());
		b.append("://"); //$NON-NLS-1$
		b.append(this.hostEditor.getStringValue());
		b.append(":"); //$NON-NLS-1$
		b.append(this.portEditor.getStringValue());
		if (!this.databaseEditor.getStringValue().equals("")) { //$NON-NLS-1$
			b.append("/"); //$NON-NLS-1$
			b.append(this.databaseEditor.getStringValue());
		}
		b.append(this.optionEditor.getStringValue());
		
		Properties props = new Properties();
		props.put("user", this.usernameEditor.getStringValue()); //$NON-NLS-1$
		props.put("password", this.passwordEditor.getStringValue()); //$NON-NLS-1$
		
		Connection con = null;
		try
		{
			con = DriverManager.getConnection(b.toString(), props);
		}
		catch (SQLException se)
		{
			MessageDialog.openError(
							this.getShell(),
							Messages.getString("ConnectionFieldEditorPreferencePage.text_96"), Messages.getString("ConnectionFieldEditorPreferencePage.text_97") + se.getSQLState() + System.getProperty("line.separator") + se.getLocalizedMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return;
		}
		
		ResultSet rs = null;
		try
		{
			DatabaseMetaData dmd = con.getMetaData();
			rs = dmd.getTables(null, null, "version", null); //$NON-NLS-1$
			rs.close();
			con.close();
		}
		catch (SQLException sqle)
		{
			MessageDialog.openError(
							this.getShell(),
							Messages.getString("ConnectionFieldEditorPreferencePage.text_100"), Messages.getString("ConnectionFieldEditorPreferencePage.text_101") + sqle.getSQLState() + System.getProperty("line.separator") + sqle.getLocalizedMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return;
		}
		MessageDialog.openInformation(
						this.getShell(),
						Messages.getString("ConnectionFieldEditorPreferencePage.text_103"), Messages.getString("ConnectionFieldEditorPreferencePage.text_104")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private StringFieldEditor nameEditor;
	private StringComboFieldEditor platformEditor;
	private DoubleComboFieldEditor levelEditor;
	private StringFieldEditor driverEditor;
	private StringFieldEditor protocolEditor;
	private StringFieldEditor subprotocolEditor;
	private StringFieldEditor hostEditor;
	private IntegerFieldEditor portEditor;
	private StringFieldEditor databaseEditor;
	private StringFieldEditor optionEditor;
	private StringFieldEditor usernameEditor;
	private StringFieldEditor passwordEditor;
	private ButtonFieldEditor testConnection;
	private IntegerFieldEditor heartbeatEditor;
	private Element[] platforms;
	private String whichConnection;
	
}
