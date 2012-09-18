/*
 * Created on 05.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.tax.wizard;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.resource.ImageDescriptor;
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

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class TaxRateNewCurrentTaxWizardPage1 extends WizardPage implements Listener
{
	
	/**
	 * @param pageName
	 */
	public TaxRateNewCurrentTaxWizardPage1(String pageName)
	{
		super(pageName);
		this.init();
	}
	
	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public TaxRateNewCurrentTaxWizardPage1(String pageName, String title, ImageDescriptor titleImage)
	{
		super(pageName, title, titleImage);
		this.init();
	}
	
	private void init()
	{
		this.setTitle(Messages.getString("TaxRateNewCurrentTaxWizardPage1.text_1")); //$NON-NLS-1$
		this.setDescription(Messages.getString("TaxRateNewCurrentTaxWizardPage1.text_2")); //$NON-NLS-1$
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
		
		Label percentLabel = new Label(composite, SWT.FLAT);
		percentLabel.setText(Messages.getString("TaxRateNewCurrentTaxWizardPage1.text_3")); //$NON-NLS-1$
		percentLabel.setLayoutData(new GridData(GridData.BEGINNING));
		
		this.percentText = new Text(composite, SWT.BORDER);
		this.percentText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.percentText.addListener(SWT.FocusIn, this);
		this.percentText.addListener(SWT.KeyUp, this);
		this.percentText.addListener(SWT.FocusOut, this);
		
		Label dateLabel = new Label(composite, SWT.FLAT);
		dateLabel.setText(Messages.getString("TaxRateNewCurrentTaxWizardPage1.text_4")); //$NON-NLS-1$
		dateLabel.setLayoutData(new GridData(GridData.BEGINNING));
		
		this.dateText = new Text(composite, SWT.BORDER);
		this.dateText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.dateText.addListener(SWT.FocusIn, this);
		this.dateText.addListener(SWT.KeyUp, this);
		this.dateText.addListener(SWT.FocusOut, this);
		
		this.setControl(composite);
		this.getControl().addListener(SWT.Paint, this);
		this.setPageComplete(this.validate());
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
				this.setErrorMessage(this.validatePercent() ? null : Messages
								.getString("TaxRateNewCurrentTaxWizardPage1.msg_5")); //$NON-NLS-1$
			}
			else if (e.widget == this.dateText)
			{
				this.setErrorMessage(this.validateDate() ? null : Messages
								.getString("TaxRateNewCurrentTaxWizardPage1.msg_6")); //$NON-NLS-1$
			}
		}
		else if (e.type == SWT.FocusOut)
		{
			if (e.widget == this.percentText)
			{
				this.setErrorMessage(this.validatePercent() ? null : Messages
								.getString("TaxRateNewCurrentTaxWizardPage1.msg_7")); //$NON-NLS-1$
				this.percentText.setText(Double.toString(this.percent) + "%"); //$NON-NLS-1$
			}
			else if (e.widget == this.dateText)
			{
				this.dateText.setText(SimpleDateFormat.getDateInstance(DateFormat.MEDIUM).format(this.date));
			}
		}
		else if (e.type == SWT.Paint)
		{
			this.percentText.setText(Double.toString(this.percent) + "%"); //$NON-NLS-1$
			this.dateText.setText(SimpleDateFormat.getDateInstance(DateFormat.MEDIUM).format(this.date));
		}
		this.setPageComplete(this.validate());
	}
	
	public boolean validate()
	{
		boolean result = true;
		result = this.validateDate() ? result : false;
		return result;
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
	
	private boolean validateDate()
	{
		boolean valid = true;
		try
		{
			this.date = SimpleDateFormat.getDateInstance().parse(this.dateText.getText());
		}
		catch (ParseException pe)
		{
			valid = false;
		}
		return valid;
	}
	
	public IWizardPage getNextPage()
	{
		return this.getWizard().getNextPage(this);
	}
	
	public double getPercent()
	{
		return this.percent;
	}
	
	public Date getDate()
	{
		return this.date;
	}
	
	private Text percentText;
	private Text dateText;
	private double percent = 0d;
	private Date date = new Date();
}
