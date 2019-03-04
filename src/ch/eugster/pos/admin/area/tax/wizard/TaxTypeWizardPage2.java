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
import ch.eugster.pos.db.TaxRate;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class TaxTypeWizardPage2 extends WizardPage implements Listener
{
	
	/**
	 * @param pageName
	 */
	public TaxTypeWizardPage2(String pageName, TaxRate rate)
	{
		super(pageName);
		this.rate = rate;
		this.setTitle(Messages.getString("TaxTypeWizardPage2.text_1")); //$NON-NLS-1$
		this.setDescription(Messages.getString("TaxTypeWizardPage2.text_2")); //$NON-NLS-1$
	}
	
	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public TaxTypeWizardPage2(String pageName, String title, ImageDescriptor titleImage)
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
		noteLabels[0].setText(Messages.getString("TaxTypeWizardPage2.text_3")); //$NON-NLS-1$
		noteLabels[1] = new Label(comp1, SWT.FLAT);
		noteLabels[1].setText(Messages.getString("TaxTypeWizardPage2.text_4")); //$NON-NLS-1$
		noteLabels[2] = new Label(comp1, SWT.FLAT);
		noteLabels[2].setText(Messages.getString("TaxTypeWizardPage2.text_5")); //$NON-NLS-1$
		noteLabels[3] = new Label(comp1, SWT.FLAT);
		
		this.taxName = new Label(comp1, SWT.FLAT);
		this.taxName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.taxName.setFont(JFaceResources.getBannerFont());
		this.taxName.setText(this.rate.name);
		
		Composite comp2 = new Composite(composite, SWT.FLAT);
		comp2.setLayoutData(new GridData(GridData.FILL_BOTH));
		comp2.setLayout(new GridLayout(2, false));
		
		Label percentLabel = new Label(comp2, SWT.FLAT);
		percentLabel.setText(Messages.getString("TaxTypeWizardPage2.text_6")); //$NON-NLS-1$
		percentLabel.setLayoutData(new GridData(GridData.BEGINNING));
		
		this.percentText = new Text(comp2, SWT.BORDER);
		this.percentText.setText(Double.toString(this.percent) + "%"); //$NON-NLS-1$
		this.percentText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.percentText.addListener(SWT.KeyUp, this);
		this.percentText.addListener(SWT.FocusIn, this);
		this.percentText.addListener(SWT.FocusOut, this);
		
		Label fibuIdLabel = new Label(comp2, SWT.FLAT);
		fibuIdLabel.setText("Steuercode Fibu"); //$NON-NLS-1$
		fibuIdLabel.setLayoutData(new GridData(GridData.BEGINNING));
		
		this.fibuIdText = new Text(comp2, SWT.BORDER);
		this.fibuIdText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.fibuIdText.addListener(SWT.FocusIn, this);
		
		Label accountLabel = new Label(comp2, SWT.FLAT);
		accountLabel.setText(Messages.getString("TaxTypeWizardPage2.text_8")); //$NON-NLS-1$
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
			if (e.widget == this.percentText)
			{
				this.validatePercent();
				this.percentText.setText(Double.toString(this.percent));
			}
			if (e.widget instanceof Text)
			{
				((Text) e.widget).selectAll();
			}
		}
		else if (e.type == SWT.KeyUp)
		{
			if (e.widget == this.percentText)
			{
				this.setErrorMessage(this.validatePercent() ? null : Messages.getString("TaxTypeWizardPage2.msg_9")); //$NON-NLS-1$
			}
		}
		else if (e.type == SWT.FocusOut)
		{
			if (e.widget == this.percentText)
			{
				this.setErrorMessage(this.validatePercent() ? null : Messages.getString("TaxTypeWizardPage2.msg_10")); //$NON-NLS-1$
				this.percentText.setText(Double.toString(this.percent) + "%"); //$NON-NLS-1$
			}
		}
		else if (e.type == SWT.Paint)
		{
			this.percentText.setFocus();
		}
		this.setPageComplete(this.validate());
	}
	
	public boolean validate()
	{
		return true;
	}
	
	private boolean validatePercent()
	{
		boolean valid = true;
		try
		{
			this.percent = Double.parseDouble(this.percentText.getText());
		}
		catch (NumberFormatException nfe)
		{
			valid = false;
		}
		catch (NullPointerException npe)
		{
			valid = false;
		}
		return valid;
	}
	
	public IWizardPage getNextPage()
	{
		return this.getWizard().getNextPage(this);
	}
	
	public void setTaxName(String name)
	{
		this.taxName.setText(name + ", " + this.rate.name); //$NON-NLS-1$
	}
	
	public double getPercent()
	{
		return this.percent;
	}
	
	public String getFibuId()
	{
		return this.fibuIdText.getText();
	}
	
	public String getAccount()
	{
		return this.accountText.getText();
	}
	
	private Label taxName;
	private Text percentText;
	private Text fibuIdText;
	private double percent = 0d;
	private Text accountText;
	private TaxRate rate;
}
