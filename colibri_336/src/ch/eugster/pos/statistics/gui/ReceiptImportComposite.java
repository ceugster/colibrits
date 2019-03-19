/*
 * Created on 17.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.gui;

import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
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
import ch.eugster.pos.statistics.core.Translator;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ReceiptImportComposite extends Composite implements Listener
{
	
	/**
	 * @param parent
	 * @param style
	 */
	public ReceiptImportComposite(Composite parent, int style, Properties properties)
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
		group.setText(Messages.getString("ReceiptImportComposite.Import_1")); //$NON-NLS-1$
		
		Composite importComposite = new Composite(group, SWT.NONE);
		importComposite.setLayout(new GridLayout(2, false));
		importComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		this.importPathControl = new Text(importComposite, SWT.BORDER);
		this.importPathControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.importPathControl.setText(this.properties.getProperty("import-path", "")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.selectImportPath = new Button(importComposite, SWT.PUSH);
		this.selectImportPath.setText(Messages.getString("ReceiptImportComposite.Suchen..._4")); //$NON-NLS-1$
		this.selectImportPath.addListener(SWT.Selection, this);
		
		this.oldFashionedColibri = new Button(group, SWT.CHECK);
		this.oldFashionedColibri.setText(Messages
						.getString("ReceiptImportComposite.Importdateien_altes_Format_(alte_Colibri-Version)_5")); //$NON-NLS-1$
		
		this.stackLayout = new StackLayout();
		this.subComposite = new Composite(this, SWT.NONE);
		this.subComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.subComposite.setLayout(this.stackLayout);
		
		this.tableComposite = new ReceiptImportTranslatorComposite(this.subComposite, SWT.NONE);
		this.tableComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.tableComposite.setLayout(new GridLayout());
		
		this.helpGroup = new Group(this.subComposite, SWT.SHADOW_ETCHED_IN);
		this.helpGroup.setText("Belegtransfer");
		// helpGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		this.helpGroup.setLayout(layout);
		Label help = new Label(this.helpGroup, SWT.WRAP);
		help.setLayoutData(new GridData(GridData.FILL_BOTH));
		help.setText(Main.getInstance().getProperty("transfer.help"));
		
		this.subComposite.layout();
		
		this.oldFashionedColibri.addListener(SWT.Selection, this);
		this.oldFashionedColibri
						.setSelection(new Boolean(this.properties.getProperty("oldfashioned-import", "false")).booleanValue()); //$NON-NLS-1$ //$NON-NLS-2$
		Event e = new Event();
		e.button = 0;
		e.widget = this.oldFashionedColibri;
		e.display = this.getDisplay();
		e.doit = true;
		e.item = this.oldFashionedColibri;
		e.type = SWT.Selection;
		this.oldFashionedColibri.notifyListeners(SWT.Selection, new Event());
	}
	
	public void handleEvent(Event e)
	{
		if (e.widget.equals(this.selectImportPath))
		{
			DirectoryDialog dialog = new DirectoryDialog(this.getShell());
			dialog.setMessage(Messages
							.getString("ReceiptImportComposite.W_u00E4hlen_Sie_das_Quellverzeichnis_aus..._8")); //$NON-NLS-1$
			dialog.setText(Messages.getString("ReceiptImportComposite.Importverzeichnis_9")); //$NON-NLS-1$
			dialog.setFilterPath(this.getImportPath());
			String path = dialog.open();
			if (path != null)
			{
				this.importPathControl.setText(path);
			}
		}
		else if (e.widget.equals(this.oldFashionedColibri))
		{
			if (this.oldFashionedColibri.getSelection())
			{
				this.stackLayout.topControl = this.tableComposite;
			}
			else
			{
				this.stackLayout.topControl = this.helpGroup;
			}
			this.subComposite.layout();
		}
	}
	
	public void saveTranslators()
	{
		this.tableComposite.getTranslatorList().save();
	}
	
	public void setOldFashionedColibri(boolean value)
	{
		this.oldFashionedColibri.setSelection(value);
	}
	
	public boolean getOldFashionedColibri()
	{
		return this.oldFashionedColibri.getSelection();
	}
	
	public String getImportPath()
	{
		return this.importPathControl.getText();
	}
	
	public void initTranslatorTable()
	{
		this.tableComposite.initTranslatorTable();
	}
	
	public Translator getTranslator(String key)
	{
		return this.tableComposite.getTranslator(key);
	}
	
	private Text importPathControl;
	private Button selectImportPath;
	private Button oldFashionedColibri;
	private Properties properties;
	private StackLayout stackLayout;
	private Composite subComposite;
	private ReceiptImportTranslatorComposite tableComposite;
	private Group helpGroup;
}
