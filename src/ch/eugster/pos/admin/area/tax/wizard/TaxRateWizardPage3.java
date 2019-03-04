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
public class TaxRateWizardPage3 extends WizardPage implements Listener
{
	
	/**
	 * @param pageName
	 */
	public TaxRateWizardPage3(String pageName)
	{
		super(pageName);
		this.init();
	}
	
	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public TaxRateWizardPage3(String pageName, String title, ImageDescriptor titleImage)
	{
		super(pageName, title, titleImage);
		this.init();
	}
	
	private void init()
	{
		this.setTitle(Messages.getString("TaxRateWizardPage3.text_1")); //$NON-NLS-1$
		this.setDescription(Messages.getString("TaxRateWizardPage3.text_2")); //$NON-NLS-1$
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
		noteLabels[0].setText(Messages.getString("TaxRateWizardPage3.text_3")); //$NON-NLS-1$
		noteLabels[1] = new Label(composite, SWT.FLAT);
		noteLabels[1].setText(Messages.getString("TaxRateWizardPage3.text_4")); //$NON-NLS-1$
		
		noteLabels[2] = new Label(composite, SWT.FLAT);
		
		noteLabels[3] = new Label(composite, SWT.FLAT);
		noteLabels[3].setText(Messages.getString("TaxRateWizardPage3.text_5")); //$NON-NLS-1$
		noteLabels[4] = new Label(composite, SWT.WRAP);
		noteLabels[4].setText(Messages.getString("TaxRateWizardPage3.text_6")); //$NON-NLS-1$
		
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
