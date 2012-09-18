/*
 * Created on 05.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.product;

import java.text.DecimalFormat;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ProductGroupChangeWizardTestPage extends WizardPage implements Listener
{
	
	/**
	 * @param pageName
	 */
	public ProductGroupChangeWizardTestPage(String pageName)
	{
		super(pageName);
		this.init();
	}
	
	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public ProductGroupChangeWizardTestPage(String pageName, String title, ImageDescriptor titleImage)
	{
		super(pageName, title, titleImage);
		this.init();
	}
	
	private void init()
	{
		this.setTitle("Statistik"); //$NON-NLS-1$
		this.setDescription("Anzahl der betroffenen Belegspositionen"); //$NON-NLS-1$
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
		composite.setLayout(new GridLayout(2, false));
		
		Label label = new Label(composite, SWT.FLAT);
		label.setText("Anzahl betroffene Belegspositionen");
		label.setLayoutData(new GridData(GridData.BEGINNING));
		
		this.countLabel = new Label(composite, SWT.FLAT);
		this.countLabel.setLayoutData(new GridData(GridData.END | GridData.FILL_HORIZONTAL));
		
		this.setControl(composite);
		
		if (this.getWizard() instanceof ProductGroupChangeWizard)
		{
			ProductGroupChangeWizard wizard = (ProductGroupChangeWizard) this.getWizard();
			wizard.getChoosePage().setPositionCount();
		}
		
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
	
	public void setPositionCount(long count)
	{
		if (this.countLabel != null)
		{
			this.countLabel.setText(DecimalFormat.getNumberInstance().format(count));
		}
	}
	
	private Label countLabel;
}
