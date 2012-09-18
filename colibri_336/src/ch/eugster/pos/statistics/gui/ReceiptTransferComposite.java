/*
 * Created on 16.05.2004
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;

import ch.eugster.pos.Messages;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ReceiptTransferComposite extends Composite implements ITabFolderChild, Listener
{
	
	/**
	 * @param parent
	 * @param style
	 */
	public ReceiptTransferComposite(Composite parent, int style, Properties properties)
	{
		super(parent, style);
		this.properties = properties;
		this.init();
	}
	
	private void init()
	{
		this.setLayout(new GridLayout());
		
		Group group = new Group(this, SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout(2, true));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setText(Messages.getString("ReceiptComposite.Belege_1")); //$NON-NLS-1$
		
		this.transferButton = new Button[2];
		this.transferButton[0] = new Button(group, SWT.RADIO);
		this.transferButton[0].setText(Messages.getString("ReceiptComposite.importieren..._2")); //$NON-NLS-1$
		this.transferButton[0].addListener(SWT.Selection, this);
		this.transferButton[1] = new Button(group, SWT.RADIO);
		this.transferButton[1].setText(Messages.getString("ReceiptComposite.exportieren..._3")); //$NON-NLS-1$
		this.transferButton[1].addListener(SWT.Selection, this);
		
		String transfer = this.properties.getProperty(Messages.getString("ReceiptComposite.transfer-type_4"), "0"); //$NON-NLS-1$ //$NON-NLS-2$
		int i = 0;
		try
		{
			i = Integer.parseInt(transfer);
		}
		catch (NumberFormatException e)
		{
		}
		this.transferButton[i].setSelection(true);
		
		this.stackLayout = new StackLayout();
		this.subParent = new Composite(this, SWT.NONE);
		this.subParent.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.subParent.setLayout(this.stackLayout);
		
		this.importComposite = new ReceiptImportComposite(this.subParent, SWT.NONE, this.properties);
		this.exportComposite = new ReceiptExportComposite(this.subParent, SWT.NONE, this.properties);
		
		switch (i)
		{
			case 0:
				this.stackLayout.topControl = this.importComposite;
				break;
			case 1:
				this.stackLayout.topControl = this.exportComposite;
				break;
		}
		this.subParent.layout();
		
	}
	
	public String getPrintButtonDesignation()
	{
		if (this.getTransferIndex() == 0)
		{
			return Messages.getString("ReceiptComposite.Importieren_6"); //$NON-NLS-1$
		}
		else if (this.getTransferIndex() == 1)
		{
			return Messages.getString("ReceiptComposite.Exportieren_7"); //$NON-NLS-1$
		}
		else
		{
			return ""; //$NON-NLS-1$
		}
	}
	
	public String getPrintFileName()
	{
		if (this.getTransferIndex() == 0)
		{
			return Messages.getString("ReceiptComposite.Import.xml_9"); //$NON-NLS-1$
		}
		else if (this.getTransferIndex() == 1)
		{
			return Messages.getString("ReceiptComposite.Export.xml_10"); //$NON-NLS-1$
		}
		else
		{
			return ""; //$NON-NLS-1$
		}
	}
	
	public void handleEvent(Event e)
	{
		if (e.type == SWT.Selection)
		{
			if (e.widget.equals(this.transferButton[0]))
			{
				this.stackLayout.topControl = this.importComposite;
				this.notifyListeners(SWT.Selection, e);
			}
			else if (e.widget.equals(this.transferButton[1]))
			{
				this.stackLayout.topControl = this.exportComposite;
				this.notifyListeners(SWT.Selection, e);
			}
			this.subParent.layout();
		}
	}
	
	public String getType()
	{
		return String.valueOf(this.getTransferIndex());
	}
	
	public int getTransferIndex()
	{
		for (int i = 0; i < this.transferButton.length; i++)
		{
			if (this.transferButton[i].getSelection())
			{
				return i;
			}
		}
		return -1;
	}
	
	public void setOldFashionedColibri(boolean value)
	{
		this.importComposite.setOldFashionedColibri(value);
	}
	
	public boolean getOldFashionedColibri()
	{
		return this.importComposite.getOldFashionedColibri();
	}
	
	public String getImportPath()
	{
		return this.importComposite.getImportPath();
	}
	
	public String getExportPath()
	{
		return this.exportComposite.getExportPath();
	}
	
	public Button getTransferButton()
	{
		return this.transferButton[this.getTransferIndex()];
	}
	
	public ReceiptImportComposite getReceiptImportComposite()
	{
		return this.importComposite;
	}
	
	public boolean isValid()
	{
		return true;
	}
	
	private Properties properties;
	
	private Button[] transferButton;
	private Composite subParent;
	private StackLayout stackLayout;
	private ReceiptImportComposite importComposite;
	private ReceiptExportComposite exportComposite;
}
