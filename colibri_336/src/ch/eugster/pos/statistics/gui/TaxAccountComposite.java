/*
 * Created on 07.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import ch.eugster.pos.Messages;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TaxAccountComposite extends Composite implements ITabFolderChild
{
	private Button withoutZeroPercentPositions;
	
	/**
	 * @param parent
	 * @param style
	 */
	public TaxAccountComposite(Composite parent, int style)
	{
		super(parent, style);
		this.init();
	}
	
	private void init()
	{
		this.setLayout(new GridLayout());
		
		Group helpGroup = new Group(this, SWT.SHADOW_ETCHED_IN);
		helpGroup.setText("Mehrwertsteuerliste");
		helpGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		helpGroup.setLayout(layout);
		
		this.withoutZeroPercentPositions = new Button(helpGroup, SWT.CHECK);
		this.withoutZeroPercentPositions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.withoutZeroPercentPositions.setText("Positionen ohne MWST oder mit 0% MWST nicht berücksichtigen");
		
		Label help = new Label(helpGroup, SWT.WRAP);
		help.setLayoutData(new GridData(GridData.FILL_BOTH));
		help.setText(Main.getInstance().getProperty("tax.account.help"));
		
	}
	
	public String getPrintButtonDesignation()
	{
		return Messages.getString("TaxAccountComposite.Drucken_1"); //$NON-NLS-1$
	}
	
	public String getPrintFileName()
	{
		return Messages.getString("TaxAccountComposite.Mwst-Liste_2"); //$NON-NLS-1$
	}
	
	public boolean isValid()
	{
		return true;
	}
	
	public boolean isWithoutZeroPercentPositions()
	{
		return this.withoutZeroPercentPositions.getSelection();
	}
	
}
