/*
 * Created on 27.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.gui;

import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import ch.eugster.pos.Messages;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SettlementComposite extends Composite implements ITabFolderChild, Listener
{
	
	private TabFolder tabFolder;
	
	private SettlementNumberComposite numberComposite;
	
	private SettlementReceiptComposite receiptComposite;
	
	private SettlementDateComposite dateComposite;
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public SettlementComposite(Composite parent, int style, Properties properties, SalespointComposite sc,
					DateRangeGroup drg)
	{
		super(parent, style);
		this.properties = properties;
		this.sc = sc;
		this.drg = drg;
		this.init();
	}
	
	private void init()
	{
		this.setLayout(new GridLayout());
		
		this.tabFolder = new TabFolder(this, SWT.TOP);
		this.tabFolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.tabFolder.addListener(SWT.Selection, this);
		
		TabItem item = new TabItem(this.tabFolder, SWT.NONE);
		item.setText("Einzelne Abschlüsse");
		this.numberComposite = new SettlementNumberComposite(this.tabFolder, SWT.NONE, this.properties, this.sc,
						this.drg);
		item.setControl(this.numberComposite);
		
		item = new TabItem(this.tabFolder, SWT.NONE);
		item.setText("Datumsbereich");
		this.receiptComposite = new SettlementReceiptComposite(this.tabFolder, SWT.NONE, this.properties);
		item.setControl(this.receiptComposite);
		
		item = new TabItem(this.tabFolder, SWT.NONE);
		item.setText("Abschlüsse über Periode");
		this.dateComposite = new SettlementDateComposite(this.tabFolder, SWT.NONE, this.properties);
		item.setControl(this.dateComposite);
		
		String transfer = this.properties.getProperty("settlement-type", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		int i = 0;
		try
		{
			i = Integer.parseInt(transfer);
		}
		catch (NumberFormatException e)
		{
		}
		
		this.tabFolder.setSelection(i);
		Event event = new Event();
		event.type = SWT.Selection;
		event.widget = this;
		
		Group helpGroup = new Group(this, SWT.SHADOW_ETCHED_IN);
		helpGroup.setText("Durchführen eines Abschlusses");
		helpGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		helpGroup.setLayout(layout);
		Label help = new Label(helpGroup, SWT.WRAP);
		help.setLayoutData(new GridData(GridData.FILL_BOTH));
		help.setText(Main.getInstance().getProperty("settlement.help"));
	}
	
	public void handleEvent(Event e)
	{
		if (e.type == SWT.Selection)
		{
			this.notifyListeners(SWT.Selection, e);
		}
	}
	
	public boolean isValid()
	{
		if (this.getCurrentControl() instanceof SettlementDateComposite)
		{
			return true;
		}
		else if (this.getCurrentControl() instanceof SettlementNumberComposite)
		{
			return ((SettlementNumberComposite) this.getCurrentControl()).settlementSelected();
		}
		else if (this.getCurrentControl() instanceof SettlementReceiptComposite)
		{
			return true;
		}
		return false;
	}
	
	public Control getCurrentControl()
	{
		TabItem item = this.tabFolder.getItem(this.tabFolder.getSelectionIndex());
		return item.getControl();
	}
	
	public SettlementNumberComposite getNumberComposite()
	{
		return this.numberComposite;
	}
	
	public SettlementDateComposite getDateComposite()
	{
		return this.dateComposite;
	}
	
	public SettlementReceiptComposite getReceiptComposite()
	{
		return this.receiptComposite;
	}
	
	public String getPrintButtonDesignation()
	{
		return Messages.getString("SettlementComposite.Drucken_5"); //$NON-NLS-1$
	}
	
	public String getPrintFileName()
	{
		return Messages.getString("SettlementComposite.Kassenabschl_u00FCsse_6"); //$NON-NLS-1$
	}
	
	public Long getSelectedSettlement()
	{
		if (this.getCurrentControl() instanceof SettlementNumberComposite)
		{
			return ((SettlementNumberComposite) this.getCurrentControl()).getSelectedSettlement();
		}
		return null;
	}
	
	public int getSettlementType()
	{
		return this.tabFolder.getSelectionIndex();
	}
	
	public boolean getFullSelection()
	{
		if (this.getCurrentControl() instanceof SettlementNumberComposite)
		{
			return false;
		}
		else if (this.getCurrentControl() instanceof SettlementDateComposite)
		{
			return ((SettlementDateComposite) this.getCurrentControl()).getFullSelection();
		}
		else if (this.getCurrentControl() instanceof SettlementReceiptComposite)
		{
			return ((SettlementReceiptComposite) this.getCurrentControl()).getFullSelection();
		}
		else
			return false;
	}
	
	public int getSelectedDateReport()
	{
		TabItem[] items = this.tabFolder.getItems();
		for (int i = 0; i < items.length; i++)
		{
			if (items[i].getControl() instanceof SettlementDateComposite)
			{
				return ((SettlementDateComposite) items[i].getControl()).getSelectedDateReport();
			}
		}
		return 0;
	}
	
	public boolean getOnlyWithDiscounts()
	{
		TabItem[] items = this.tabFolder.getItems();
		for (int i = 0; i < items.length; i++)
		{
			if (items[i].getControl() instanceof SettlementDateComposite)
			{
				return ((SettlementDateComposite) items[i].getControl()).getOnlyWithDiscounts();
			}
		}
		return false;
	}
	
	private Properties properties;
	private DateRangeGroup drg;
	private SalespointComposite sc;
}
