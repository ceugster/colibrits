/*

 * Created on 05.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.tax.wizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import ch.eugster.pos.Messages;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class TaxTypeWizardPage3 extends WizardPage implements Listener
{
	
	/**
	 * @param pageName
	 */
	public TaxTypeWizardPage3(String pageName)
	{
		super(pageName);
		this.init();
	}
	
	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public TaxTypeWizardPage3(String pageName, String title, ImageDescriptor titleImage)
	{
		super(pageName, title, titleImage);
		this.init();
	}
	
	private void init()
	{
		this.setTitle(Messages.getString("TaxTypeWizardPage3.Hinzuf_u00FCgen_der_neuen_Mehrwertsteuerart__1")); //$NON-NLS-1$
		this
						.setDescription(Messages
										.getString("TaxTypeWizardPage3.Die_f_u00FCr_die_Erstellung_der_neuen_Mehrwertsteuerart_notwendigen_Angaben_sind_jetzt_vollst_u00E4ndig._2")); //$NON-NLS-1$
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
		Composite composite = new Composite(parent, SWT.FLAT);
		composite.setLayout(new GridLayout());
		
		Label[] noteLabels = new Label[6];
		noteLabels[0] = new Label(composite, SWT.FLAT);
		noteLabels[0]
						.setText(Messages
										.getString("TaxTypeWizardPage3.Sie_haben_die_notwendigen_Angaben_f_u00FCr_die_neue_Mehrwertsteuerart_eingegeben._3")); //$NON-NLS-1$
		noteLabels[1] = new Label(composite, SWT.FLAT);
		noteLabels[1]
						.setText(Messages
										.getString("TaxTypeWizardPage3.Soll_die_neue_Mehrwertsteuerart_nun_hinzugef_u00FCgt_werden__4")); //$NON-NLS-1$
		
		noteLabels[2] = new Label(composite, SWT.FLAT);
		
		noteLabels[3] = new Label(composite, SWT.FLAT);
		noteLabels[3]
						.setText(Messages
										.getString("TaxTypeWizardPage3.W_u00E4hlen_Sie___Finish__,_wenn_Sie_die_neue_Mehrwertsteuerart_hinzuf_u00FCgen_wollen._5")); //$NON-NLS-1$
		noteLabels[4] = new Label(composite, SWT.WRAP);
		noteLabels[4]
						.setText(Messages
										.getString("TaxTypeWizardPage3.W_u00E4hlen_Sie___Abbrechen__,_um_den_Assistenten_zu_beenden._6")); //$NON-NLS-1$
		
		this.setControl(composite);
		this.setPageComplete(this.validate());
	}
	
	public void handleEvent(Event e)
	{
		this.setPageComplete(this.validate());
	}
	
	private boolean validate()
	{
		return true;
	}
}
