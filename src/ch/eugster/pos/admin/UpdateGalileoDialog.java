/*
 * Created on 21.02.2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.admin;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.eugster.pos.swt.Resources;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class UpdateGalileoDialog extends TitleAreaDialog implements IRunnableContext
{
	
	private Text fromDate;
	private Text toDate;
	private ProgressBar progressBar;
	private Label progressLabel;
	private Label galileoResultLabel;
	private Label colibriResultLabel;
	private Label failedResultLabel;
	private Label otherResultLabel;
	
	private UpdateGalileoRunnableWithProgress runnable;
	
	/**
	 * @param arg0
	 */
	public UpdateGalileoDialog(Shell shell)
	{
		super(shell);
	}
	
	protected Control createDialogArea(Composite parent)
	{
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Dialog für Galileo-Aktualisierung wird initialisiert.");
		this.setTitle("Galileo aktualisieren");
		
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(1, true));
		
		Label textLabel = new Label(composite, SWT.NONE);
		textLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		textLabel.setText("Diese Funktion erlaubt die nachträgliche Aktualisierung von \nBelegpositionen in Galileo.");
		
		Composite dateComposite = new Composite(composite, SWT.NO_FOCUS);
		dateComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		dateComposite.setLayout(new GridLayout(2, false));
		
		Label fromLabel = new Label(dateComposite, SWT.SINGLE);
		fromLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fromLabel.setText("Beginn Datum");
		
		Calendar gc = GregorianCalendar.getInstance();
		gc.set(Calendar.HOUR_OF_DAY, 1);
		gc.set(Calendar.MINUTE, 0);
		gc.set(Calendar.SECOND, 0);
		gc.set(Calendar.MILLISECOND, 0);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy kk:mm:ss");
		this.fromDate = new Text(dateComposite, SWT.BORDER + SWT.SINGLE);
		this.fromDate.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.fromDate.setText(sdf.format(gc.getTime()));
		
		Label toLabel = new Label(dateComposite, SWT.SINGLE);
		toLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		toLabel.setText("Ende Datum");
		
		gc = GregorianCalendar.getInstance();
		this.toDate = new Text(dateComposite, SWT.BORDER + SWT.SINGLE);
		this.toDate.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.toDate.setText(sdf.format(gc.getTime()));
		
		this.progressBar = new ProgressBar(composite, SWT.SMOOTH);
		this.progressBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		this.progressLabel = new Label(composite, SWT.SINGLE);
		this.progressLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.progressLabel.setText("Bereit...");
		
		Composite child = new Composite(composite, SWT.NONE);
		child.setLayoutData(new GridData(GridData.FILL_BOTH));
		child.setLayout(new GridLayout(2, false));
		
		Label galileoResultTextLabel = new Label(child, SWT.SINGLE);
		galileoResultTextLabel.setText("Verarbeitete Positionen Galileotitel:");
		
		this.galileoResultLabel = new Label(child, SWT.SINGLE);
		this.galileoResultLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.galileoResultLabel.setAlignment(SWT.RIGHT);
		this.galileoResultLabel.setText("0");
		
		Label colibriResultTextLabel = new Label(child, SWT.SINGLE);
		colibriResultTextLabel.setText("Verarbeitete Positionen ColibriTS-Warengruppen:");
		
		this.colibriResultLabel = new Label(child, SWT.SINGLE);
		this.colibriResultLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.colibriResultLabel.setAlignment(SWT.RIGHT);
		this.colibriResultLabel.setText("0");
		
		Label failedResultTextLabel = new Label(child, SWT.SINGLE);
		failedResultTextLabel.setText("Fehlerhaft verarbeitete Positionen:");
		
		this.failedResultLabel = new Label(child, SWT.SINGLE);
		this.failedResultLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.failedResultLabel.setAlignment(SWT.RIGHT);
		this.failedResultLabel.setText("0");
		
		Label otherResultTextLabel = new Label(child, SWT.SINGLE);
		otherResultTextLabel.setText("Positionen ohne Verarbeitung:");
		
		this.otherResultLabel = new Label(child, SWT.SINGLE);
		this.otherResultLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.otherResultLabel.setAlignment(SWT.RIGHT);
		this.otherResultLabel.setText("0");
		
		return composite;
	}
	
	protected void createButtonsForButtonBar(Composite composite)
	{
		this.createButton(composite, IDialogConstants.OK_ID, "Starten", true);
		this.createButton(composite, IDialogConstants.CANCEL_ID, "Schliessen", false);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.IconAndMessageDialog#getImage()
	 */
	protected Image getImage()
	{
		return Resources.getImageRegistry().get("activity_diagram_wiz.gif");
	}
	
	protected void buttonPressed(int button_id)
	{
		if (button_id == IDialogConstants.OK_ID)
		{
			Date from = null;
			Date to = null;
			
			try
			{
				from = this.getDate(this.fromDate);
			}
			catch (ParseException e)
			{
				MessageDialog.openError(this.getShell(), "Ungültiges Datum", "Das eingegebene Datum ist ungültig.");
				this.fromDate.setFocus();
				return;
			}
			
			try
			{
				to = this.getDate(this.toDate);
			}
			catch (ParseException e)
			{
				MessageDialog.openError(this.getShell(), "Ungültiges Datum", "Das eingegebene Datum ist ungültig.");
				this.toDate.setFocus();
				return;
			}
			
			try
			{
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Taste 'Starten' gedrückt...");
				this.runnable = new UpdateGalileoRunnableWithProgress(from, to, new UpdateGalileoProgressMonitor(this));
				this.run(true, true, this.runnable);
			}
			catch (InvocationTargetException e)
			{
			}
			catch (InterruptedException e)
			{
			}
			finally
			{
			}
		}
		else if (button_id == IDialogConstants.CANCEL_ID)
		{
			if (this.getButton(IDialogConstants.CANCEL_ID).getText().equals("Abbrechen"))
			{
				this.runnable.getMonitor().setCanceled(true);
			}
			else
			{
				this.close();
			}
			
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.operation.IRunnableContext#run(boolean, boolean,
	 * org.eclipse.jface.operation.IRunnableWithProgress)
	 */
	public void run(boolean fork, boolean cancelable, IRunnableWithProgress runnable) throws InvocationTargetException,
					InterruptedException
	{
		
		Thread thread = new Thread(this.runnable);
		thread.start();
	}
	
	private Date getDate(Text dateText) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy kk:mm:ss");
		Date date = sdf.parse(dateText.getText());
		Calendar gc = GregorianCalendar.getInstance();
		gc.setTime(date);
		return gc.getTime();
	}
	
	public Button getOkButton()
	{
		return this.getButton(IDialogConstants.OK_ID);
	}
	
	public Button getCancelButton()
	{
		return this.getButton(IDialogConstants.CANCEL_ID);
	}
	
	public ProgressBar getProgressBar()
	{
		return this.progressBar;
	}
	
	public Label getProgressLabel()
	{
		return this.progressLabel;
	}
	
	public Label getGalileoResultLabel()
	{
		return this.galileoResultLabel;
	}
	
	public Label getColibriResultLabel()
	{
		return this.colibriResultLabel;
	}
	
	public Label getFailedResultLabel()
	{
		return this.failedResultLabel;
	}
	
	public Label getOtherResultLabel()
	{
		return this.otherResultLabel;
	}
}
