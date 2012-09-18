/*
 * Created on 05.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.tax.wizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.TaxType;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class TaxRateWizardPage2 extends WizardPage implements Listener
{
	
	/**
	 * @param pageName
	 */
	public TaxRateWizardPage2(String pageName, TaxType type)
	{
		super(pageName);
		this.type = type;
		this.setTitle(Messages.getString("TaxRateWizardPage2.text_1")); //$NON-NLS-1$
		this.setDescription(Messages.getString("TaxRateWizardPage2.text_2")); //$NON-NLS-1$
	}
	
	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public TaxRateWizardPage2(String pageName, String title, ImageDescriptor titleImage)
	{
		super(pageName, title, titleImage);
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
		
		Composite comp1 = new Composite(composite, SWT.FLAT);
		comp1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comp1.setLayout(new GridLayout());
		
		Label[] noteLabels = new Label[4];
		noteLabels[0] = new Label(comp1, SWT.FLAT);
		noteLabels[0].setText(Messages.getString("TaxRateWizardPage2.text_3")); //$NON-NLS-1$
		noteLabels[1] = new Label(comp1, SWT.FLAT);
		noteLabels[1].setText(Messages.getString("TaxRateWizardPage2.text_4")); //$NON-NLS-1$
		noteLabels[2] = new Label(comp1, SWT.FLAT);
		noteLabels[2].setText(Messages.getString("TaxRateWizardPage2.text_5")); //$NON-NLS-1$
		noteLabels[3] = new Label(comp1, SWT.FLAT);
		
		this.taxName = new Label(comp1, SWT.FLAT);
		this.taxName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.taxName.setFont(JFaceResources.getBannerFont());
		this.taxName.setText(this.type.name);
		
		Composite comp2 = new Composite(composite, SWT.FLAT);
		comp2.setLayoutData(new GridData(GridData.FILL_BOTH));
		comp2.setLayout(new GridLayout(2, false));
		
		Label galileoIdLabel = new Label(comp2, SWT.FLAT);
		galileoIdLabel.setText(Messages.getString("TaxRateWizardPage2.text_6")); //$NON-NLS-1$
		galileoIdLabel.setLayoutData(new GridData(GridData.BEGINNING));
		
		this.galileoIdText = new Text(comp2, SWT.BORDER);
		this.galileoIdText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.galileoIdText.addListener(SWT.FocusIn, this);
		
		Label code128 = new Label(comp2, SWT.FLAT);
		code128.setText("Steuercode Code128"); //$NON-NLS-1$
		code128.setLayoutData(new GridData(GridData.BEGINNING));
		
		this.code128Text = new Text(comp2, SWT.BORDER);
		this.code128Text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.code128Text.addListener(SWT.FocusIn, this);
		
		Label fibuIdLabel = new Label(comp2, SWT.FLAT);
		fibuIdLabel.setText(Messages.getString("TaxRateWizardPage2.text_7")); //$NON-NLS-1$
		fibuIdLabel.setLayoutData(new GridData(GridData.BEGINNING));
		
		this.fibuIdText = new Text(comp2, SWT.BORDER);
		this.fibuIdText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.fibuIdText.addListener(SWT.FocusIn, this);
		
		Label accountLabel = new Label(comp2, SWT.FLAT);
		accountLabel.setText(Messages.getString("TaxRateWizardPage2.text_8")); //$NON-NLS-1$
		accountLabel.setLayoutData(new GridData(GridData.BEGINNING));
		
		this.accountText = new Text(comp2, SWT.BORDER);
		this.accountText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.accountText.addListener(SWT.FocusIn, this);
		
		this.setControl(composite);
		this.getControl().addListener(SWT.Paint, this);
		this.setPageComplete(true);
	}
	
	public void handleEvent(Event e)
	{
		if (e.type == SWT.FocusIn)
		{
			if (e.widget instanceof Text)
			{
				((Text) e.widget).selectAll();
			}
		}
		else if (e.type == SWT.Paint)
		{
			this.accountText.setFocus();
		}
	}
	
	public IWizardPage getNextPage()
	{
		return this.getWizard().getNextPage(this);
	}
	
	public void setTaxName(String name)
	{
		this.taxName.setText(this.type.name + ", " + name); //$NON-NLS-1$
	}
	
	public String getAccount()
	{
		return this.accountText.getText();
	}
	
	public String getGalileoId()
	{
		return this.galileoIdText.getText();
	}
	
	public String getCode128Id()
	{
		return this.code128Text.getText();
	}
	
	public String getFibuId()
	{
		return this.fibuIdText.getText();
	}
	
	private Label taxName;
	private Text accountText;
	private Text galileoIdText;
	private Text code128Text;
	private Text fibuIdText;
	private TaxType type;
}
