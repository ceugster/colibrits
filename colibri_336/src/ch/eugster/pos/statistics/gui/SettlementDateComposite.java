/*
 * Created on 17.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.gui;

import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import ch.eugster.pos.Messages;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SettlementDateComposite extends Composite implements Listener
{
	private Button[] radioButtons;
	private Button fullReport;
	private Button onlyWithDiscounts;
	
	/**
	 * @param parent
	 * @param style
	 */
	public SettlementDateComposite(Composite parent, int style, Properties properties)
	{
		super(parent, style);
		this.init(properties);
	}
	
	private void init(Properties properties)
	{
		this.setLayout(new GridLayout(2, true));
		this.setLayout(new GridLayout());
		
		Group group = new Group(this, SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setText("Optionen"); //$NON-NLS-1$
		
		this.radioButtons = new Button[3];
		
		Composite composite = new Composite(group, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		this.radioButtons[0] = new Button(composite, SWT.RADIO);
		this.radioButtons[0].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.radioButtons[0].setText("Kassenabschluss gewählte Periode");
		this.radioButtons[0].addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent event)
			{
				SettlementDateComposite.this.radioButtons[1].setSelection(false);
				SettlementDateComposite.this.radioButtons[2].setSelection(false);
			}
			
			public void widgetDefaultSelected(SelectionEvent event)
			{
				this.widgetSelected(event);
			}
		});
		
		composite = new Composite(group, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label filler = new Label(composite, SWT.NONE);
		filler.setText("     ");
		
		this.fullReport = new Button(composite, SWT.CHECK | SWT.WRAP);
		this.fullReport.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.fullReport.setText(Messages.getString("SettlementComposite.inklusive_&Liste_der_stornierten_Belege_2")); //$NON-NLS-1$
		this.fullReport.setSelection(Boolean.getBoolean(properties.getProperty(Messages
						.getString("SettlementComposite.printReversedReceipts_3"), "false"))); //$NON-NLS-1$ //$NON-NLS-2$
		this.fullReport.setSelection(Boolean.getBoolean(Main.getInstance().getProperty("date.report.full-selection")));
		
		filler = new Label(composite, SWT.NONE);
		filler.setText("     ");
		
		filler = new Label(composite, SWT.WRAP);
		filler.setText("      " + Messages.getString("SettlementComposite.inklusive_&Liste_der_stornierten_Belege_3"));
		
		composite = new Composite(group, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		this.radioButtons[1] = new Button(composite, SWT.RADIO);
		this.radioButtons[1].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.radioButtons[1].setText("Differenzliste (nur für Datumsbereiche mit gespeicherten Abschlüssen)");
		this.radioButtons[1].addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent event)
			{
				SettlementDateComposite.this.radioButtons[0].setSelection(false);
				SettlementDateComposite.this.radioButtons[2].setSelection(false);
			}
			
			public void widgetDefaultSelected(SelectionEvent event)
			{
				this.widgetSelected(event);
			}
		});
		
		composite = new Composite(group, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		this.radioButtons[2] = new Button(composite, SWT.RADIO);
		this.radioButtons[2].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.radioButtons[2].setText("Rabattliste");
		this.radioButtons[2].addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent event)
			{
				SettlementDateComposite.this.radioButtons[0].setSelection(false);
				SettlementDateComposite.this.radioButtons[1].setSelection(false);
			}
			
			public void widgetDefaultSelected(SelectionEvent event)
			{
				this.widgetSelected(event);
			}
		});
		
		composite = new Composite(group, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		filler = new Label(composite, SWT.NONE);
		filler.setText("     ");
		
		this.onlyWithDiscounts = new Button(composite, SWT.CHECK | SWT.WRAP);
		this.onlyWithDiscounts.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.onlyWithDiscounts.setText("Nur Positionen mit Rabatt berücksichtigen"); //$NON-NLS-1$
		this.onlyWithDiscounts.setSelection(Boolean.getBoolean(properties.getProperty(
						"discount-list.only-with-discounts", "false"))); //$NON-NLS-1$ //$NON-NLS-2$
		
		int selection = Integer.parseInt(properties.getProperty("date.report.selection", "0"));
		for (int i = 0; i < this.radioButtons.length; i++)
		{
			this.radioButtons[i].setSelection(i == selection);
		}
		
	}
	
	public boolean getFullSelection()
	{
		return this.fullReport.getSelection();
	}
	
	public int getSelectedDateReport()
	{
		for (int i = 0; i < this.radioButtons.length; i++)
		{
			if (this.radioButtons[i].getSelection()) return i;
		}
		return 0;
	}
	
	public boolean getOnlyWithDiscounts()
	{
		return this.onlyWithDiscounts.getSelection();
	}
	
	public void handleEvent(Event e)
	{
	}
}
