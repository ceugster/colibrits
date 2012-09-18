/*
 * Created on 17.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.gui;

import java.util.Properties;

import org.eclipse.swt.SWT;
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
public class SettlementReceiptComposite extends Composite implements Listener
{
	private Button fullReport;
	
	/**
	 * @param parent
	 * @param style
	 */
	public SettlementReceiptComposite(Composite parent, int style, Properties properties)
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
		
		this.fullReport = new Button(group, SWT.CHECK | SWT.WRAP);
		this.fullReport.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.fullReport.setText("Inklusive &Liste der stornierten Belege"); //$NON-NLS-1$
		this.fullReport.setSelection(Boolean.getBoolean(properties.getProperty(Messages
						.getString("SettlementComposite.printReversedReceipts_3"), "false"))); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.fullReport.setSelection(Boolean
						.getBoolean(Main.getInstance().getProperty("receipt.report.full-selection")));
	}
	
	public boolean getFullSelection()
	{
		return this.fullReport.getSelection();
	}
	
	public void handleEvent(Event e)
	{
	}
}
