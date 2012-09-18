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
import org.eclipse.swt.widgets.Combo;
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
public class TaxRateWizardPage1 extends WizardPage implements Listener
{
	
	/**
	 * @param pageName
	 */
	public TaxRateWizardPage1(String pageName)
	{
		super(pageName);
		this.init();
	}
	
	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public TaxRateWizardPage1(String pageName, String title, ImageDescriptor titleImage)
	{
		super(pageName, title, titleImage);
		this.init();
	}
	
	private void init()
	{
		this.setTitle(Messages.getString("TaxRateWizardPage1.text_1")); //$NON-NLS-1$
		this.setDescription(Messages.getString("TaxRateWizardPage1.text_2")); //$NON-NLS-1$
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
		
		Label[] noteLabels = new Label[2];
		noteLabels[0] = new Label(comp1, SWT.FLAT);
		noteLabels[0].setText(Messages.getString("TaxRateWizardPage1.text_3")); //$NON-NLS-1$
		noteLabels[1] = new Label(comp1, SWT.FLAT);
		noteLabels[1].setText(Messages.getString("TaxRateWizardPage1.text_4")); //$NON-NLS-1$
		noteLabels[1] = new Label(comp1, SWT.FLAT);
		noteLabels[1].setText(Messages.getString("TaxRateWizardPage1.text_5")); //$NON-NLS-1$
		
		Composite comp2 = new Composite(composite, SWT.FLAT);
		comp2.setLayoutData(new GridData(GridData.FILL_BOTH));
		comp2.setLayout(new GridLayout(2, false));
		
		Label codeLabel = new Label(comp2, SWT.FLAT);
		codeLabel.setText(Messages.getString("TaxRateWizardPage1.text_6")); //$NON-NLS-1$
		codeLabel.setLayoutData(new GridData(GridData.BEGINNING));
		
		String codes = Messages.getString("TaxRateWizardPage1.text_7"); //$NON-NLS-1$
		TaxRate[] rate = TaxRate.selectAll(true);
		for (int i = 0; i < rate.length; i++)
		{
			codes = codes.replaceFirst(rate[i].code, Messages.getString("TaxRateWizardPage1.text_8")); //$NON-NLS-1$
		}
		this.codeCombo = new Combo(comp2, SWT.READ_ONLY);
		this.codeCombo.setLayoutData(new GridData(GridData.BEGINNING));
		for (int i = 0; i < codes.length(); i++)
		{
			this.codeCombo.add(codes.substring(i, i + 1));
		}
		if (this.codeCombo.getItemCount() > 0)
		{
			this.codeCombo.select(0);
		}
		this.codeCombo.addListener(SWT.Selection, this);
		
		Label nameLabel = new Label(comp2, SWT.FLAT);
		nameLabel.setText(Messages.getString("TaxRateWizardPage1.text_9")); //$NON-NLS-1$
		nameLabel.setLayoutData(new GridData(GridData.BEGINNING));
		
		this.nameText = new Text(comp2, SWT.BORDER);
		this.nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.nameText.addListener(SWT.FocusIn, this);
		this.nameText.addListener(SWT.KeyUp, this);
		this.nameText.addListener(SWT.FocusOut, this);
		
		Label percentLabel = new Label(comp2, SWT.FLAT);
		percentLabel.setText(Messages.getString("TaxRateWizardPage1.text_10")); //$NON-NLS-1$
		percentLabel.setLayoutData(new GridData(GridData.BEGINNING));
		
		this.percentText = new Text(comp2, SWT.BORDER);
		this.percentText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.percentText.addListener(SWT.FocusIn, this);
		this.percentText.addListener(SWT.KeyUp, this);
		this.percentText.addListener(SWT.FocusOut, this);
		
		Label dateLabel = new Label(comp2, SWT.FLAT);
		dateLabel.setText(Messages.getString("TaxRateWizardPage1.text_11")); //$NON-NLS-1$
		dateLabel.setLayoutData(new GridData(GridData.BEGINNING));
		
		this.dateText = new Text(comp2, SWT.BORDER);
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
		if (e.widget == this.codeCombo)
		{
			this.setErrorMessage(this.validateCode() ? null : Messages.getString("TaxRateWizardPage1.msg_12")); //$NON-NLS-1$
		}
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
			if (e.widget == this.nameText)
			{
				this.setErrorMessage(this.validateName() ? null : Messages.getString("TaxRateWizardPage1.msg_13")); //$NON-NLS-1$
			}
			else if (e.widget == this.percentText)
			{
				this.setErrorMessage(this.validatePercent() ? null : Messages.getString("TaxRateWizardPage1.msg_14")); //$NON-NLS-1$
			}
			else if (e.widget == this.dateText)
			{
				this.setErrorMessage(this.validateDate() ? null : Messages.getString("TaxRateWizardPage1.msg_15")); //$NON-NLS-1$
			}
		}
		else if (e.type == SWT.FocusOut)
		{
			if (e.widget == this.nameText)
			{
				IWizardPage[] pages = this.getWizard().getPages();
				for (int i = 0; i < pages.length; i++)
				{
					if (pages[i] instanceof TaxRateWizardPage2)
					{
						((TaxRateWizardPage2) pages[i]).setTaxName(this.nameText.getText());
					}
				}
			}
			else if (e.widget == this.percentText)
			{
				this.setErrorMessage(this.validatePercent() ? null : Messages.getString("TaxRateWizardPage1.msg_16")); //$NON-NLS-1$
				this.percentText.setText(Double.toString(this.percent) + "%"); //$NON-NLS-1$
			}
			else if (e.widget == this.dateText)
			{
				this.dateText.setText(SimpleDateFormat.getDateInstance(DateFormat.MEDIUM).format(this.date));
			}
		}
		else if (e.type == SWT.Paint)
		{
			this.codeCombo.setFocus();
			this.percentText.setText(Double.toString(this.percent) + "%"); //$NON-NLS-1$
			this.dateText.setText(SimpleDateFormat.getDateInstance(DateFormat.MEDIUM).format(this.date));
		}
		this.setPageComplete(this.validate());
	}
	
	public boolean validate()
	{
		boolean result = true;
		result = this.validateCode() ? result : false;
		result = this.validateName() ? result : false;
		result = this.validateDate() ? result : false;
		return result;
	}
	
	private boolean validateCode()
	{
		return !this.codeCombo.getItem(this.codeCombo.getSelectionIndex()).equals(""); //$NON-NLS-1$
	}
	
	private boolean validateName()
	{
		return !this.nameText.getText().equals(""); //$NON-NLS-1$
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
	
	public String getCode()
	{
		return this.codeCombo.getItem(this.codeCombo.getSelectionIndex());
	}
	
	public String getRateName()
	{
		return this.nameText.getText();
	}
	
	public double getPercent()
	{
		return this.percent;
	}
	
	public Date getDate()
	{
		return this.date;
	}
	
	private Combo codeCombo;
	private Text nameText;
	private Text percentText;
	private Text dateText;
	private double percent = 0d;
	private Date date = new Date();
}
