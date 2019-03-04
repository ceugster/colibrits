/*
 * Created on 17.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.gui;

import java.io.File;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import ch.eugster.pos.Messages;
import ch.eugster.pos.util.Config;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ReceiptExportComposite extends Composite implements Listener
{
	
	/**
	 * @param parent
	 * @param style
	 */
	public ReceiptExportComposite(Composite parent, int style, Properties properties)
	{
		super(parent, style);
		this.properties = properties;
		this.init();
	}
	
	private void init()
	{
		this.setLayout(new GridLayout());
		
		Group group = new Group(this, SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setText(Messages.getString("ReceiptExportComposite.Export_1")); //$NON-NLS-1$
		
		Composite exportComposite = new Composite(group, SWT.NONE);
		exportComposite.setLayout(new GridLayout(2, false));
		exportComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		this.exportPathControl = new Text(exportComposite, SWT.BORDER);
		this.exportPathControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.exportPathControl.setText(this.properties.getProperty("export-path", "")); //$NON-NLS-1$ //$NON-NLS-2$
		if (!new File(this.exportPathControl.getText()).exists())
		{
			this.exportPathControl.setText(Config.getInstance().getSalespointExportPath());
		}
		this.selectExportPath = new Button(exportComposite, SWT.PUSH);
		this.selectExportPath.setText(Messages.getString("ReceiptExportComposite.Suchen..._4")); //$NON-NLS-1$
		this.selectExportPath.addListener(SWT.Selection, this);
		
		Group helpGroup = new Group(this, SWT.SHADOW_ETCHED_IN);
		helpGroup.setText("Belegtransfer");
		helpGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		helpGroup.setLayout(layout);
		Label help = new Label(helpGroup, SWT.WRAP);
		help.setLayoutData(new GridData(GridData.FILL_BOTH));
		help.setText(Main.getInstance().getProperty("transfer.help"));
	}
	
	public void handleEvent(Event e)
	{
		if (e.widget.equals(this.selectExportPath))
		{
			DirectoryDialog dialog = new DirectoryDialog(this.getShell());
			dialog
							.setMessage(Messages
											.getString("ReceiptExportComposite.W_u00E4hlen_Sie_das_Zielverzeichnis_aus..._5")); //$NON-NLS-1$
			dialog.setText(Messages.getString("ReceiptExportComposite.Exportverzeichnis_6")); //$NON-NLS-1$
			dialog.setFilterPath(this.getExportPath());
			String path = dialog.open();
			if (path != null)
			{
				this.exportPathControl.setText(path);
			}
		}
	}
	
	public String getExportPath()
	{
		return this.exportPathControl.getText();
	}
	
	private Text exportPathControl;
	private Button selectExportPath;
	// private Button oldFashionedColibri;
	// private TableViewer table;
	private Properties properties;
}
