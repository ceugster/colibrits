/*
 * Created on 21.02.2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.admin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class UpdateGalileoProgressMonitor implements IProgressMonitor
{
	
	private boolean canceled = false;
	private int receiptCount = 0;
	private int receiptWorked = 0;
	private int countGalileo = 0;
	private int countColibri = 0;
	private int countFailed = 0;
	private int countOther = 0;
	private String taskMessage;
	private UpdateGalileoDialog dialog;
	
	/**
	 * @param arg0
	 */
	public UpdateGalileoProgressMonitor(UpdateGalileoDialog dialog)
	{
		this.dialog = dialog;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.IProgressMonitor#beginTask(java.lang.String,
	 * int)
	 */
	public void beginTask(String name, int totalWork)
	{
		this.taskMessage = "Verarbeitungsprozess gestartet...";
		this.receiptCount = totalWork;
		this.receiptWorked = 0;
		this.countGalileo = 0;
		this.countColibri = 0;
		this.countFailed = 0;
		this.countOther = 0;
		
		this.dialog.getShell().getDisplay().asyncExec(new Runnable()
		{
			public void run()
			{
				UpdateGalileoProgressMonitor.this.dialog.getOkButton().setEnabled(false);
				UpdateGalileoProgressMonitor.this.dialog.getCancelButton().setText("Abbrechen");
				UpdateGalileoProgressMonitor.this.dialog.getProgressBar().setMinimum(0);
				UpdateGalileoProgressMonitor.this.dialog.getProgressBar().setSelection(0);
				UpdateGalileoProgressMonitor.this.dialog.getProgressBar().setMaximum(
								UpdateGalileoProgressMonitor.this.receiptCount);
				UpdateGalileoProgressMonitor.this.dialog.getProgressLabel().setText(
								UpdateGalileoProgressMonitor.this.taskMessage);
				UpdateGalileoProgressMonitor.this.dialog.getGalileoResultLabel().setText("0");
				UpdateGalileoProgressMonitor.this.dialog.getColibriResultLabel().setText("0");
				UpdateGalileoProgressMonitor.this.dialog.getFailedResultLabel().setText("0");
			}
		});
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IProgressMonitor#done()
	 */
	public void done()
	{
		this.taskMessage = "Verarbeitungsprozess beendet (verarbeitet: " + this.receiptWorked + " Belege).";
		
		this.dialog.getShell().getDisplay().asyncExec(new Runnable()
		{
			public void run()
			{
				UpdateGalileoProgressMonitor.this.dialog.getOkButton().setEnabled(true);
				UpdateGalileoProgressMonitor.this.dialog.getCancelButton().setText("Schliessen");
				UpdateGalileoProgressMonitor.this.dialog.getProgressLabel().setText(
								UpdateGalileoProgressMonitor.this.taskMessage);
				UpdateGalileoProgressMonitor.this.dialog.getGalileoResultLabel().setText(
								"" + UpdateGalileoProgressMonitor.this.countGalileo);
				UpdateGalileoProgressMonitor.this.dialog.getColibriResultLabel().setText(
								"" + UpdateGalileoProgressMonitor.this.countColibri);
				UpdateGalileoProgressMonitor.this.dialog.getFailedResultLabel().setText(
								"" + UpdateGalileoProgressMonitor.this.countFailed);
				UpdateGalileoProgressMonitor.this.dialog.getOtherResultLabel().setText(
								"" + UpdateGalileoProgressMonitor.this.countOther);
			}
		});
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IProgressMonitor#internalWorked(double)
	 */
	public void internalWorked(double worked)
	{
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IProgressMonitor#isCanceled()
	 */
	public boolean isCanceled()
	{
		return this.canceled;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IProgressMonitor#setCanceled(boolean)
	 */
	public void setCanceled(boolean canceled)
	{
		this.canceled = canceled;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.IProgressMonitor#setTaskName(java.lang.String)
	 */
	public void setTaskName(String name)
	{
		this.taskMessage = name;
		this.dialog.getShell().getDisplay().asyncExec(new Runnable()
		{
			public void run()
			{
				UpdateGalileoProgressMonitor.this.dialog.getProgressLabel().setText(
								UpdateGalileoProgressMonitor.this.taskMessage);
			}
		});
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IProgressMonitor#subTask(java.lang.String)
	 */
	public void subTask(String name)
	{
	}
	
	public void setProgressLabelText(final String text)
	{
		this.dialog.getShell().getDisplay().asyncExec(new Runnable()
		{
			public void run()
			{
				UpdateGalileoProgressMonitor.this.dialog.getProgressLabel().setText(text);
			}
		});
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IProgressMonitor#worked(int)
	 */
	public void worked(int worked)
	{
		this.receiptWorked++;
		double val = new Double(this.receiptWorked).doubleValue();
		double max = new Double(this.receiptCount).doubleValue();
		int percent = new Double(val / max * 100).intValue();
		this.taskMessage = "" + this.receiptWorked + " von " + this.receiptCount + " Belegen (" + percent
						+ "%) verarbeitet...";
		
		this.dialog.getShell().getDisplay().asyncExec(new Runnable()
		{
			public void run()
			{
				UpdateGalileoProgressMonitor.this.dialog.getProgressBar().setSelection(
								UpdateGalileoProgressMonitor.this.receiptWorked);
				UpdateGalileoProgressMonitor.this.dialog.getProgressLabel().setText(
								UpdateGalileoProgressMonitor.this.taskMessage);
				UpdateGalileoProgressMonitor.this.dialog.getGalileoResultLabel().setText(
								"" + UpdateGalileoProgressMonitor.this.countGalileo);
				UpdateGalileoProgressMonitor.this.dialog.getColibriResultLabel().setText(
								"" + UpdateGalileoProgressMonitor.this.countColibri);
				UpdateGalileoProgressMonitor.this.dialog.getFailedResultLabel().setText(
								"" + UpdateGalileoProgressMonitor.this.countFailed);
				UpdateGalileoProgressMonitor.this.dialog.getOtherResultLabel().setText(
								"" + UpdateGalileoProgressMonitor.this.countOther);
			}
		});
	}
	
	public boolean ask()
	{
		this.dialog.getShell().getDisplay().syncExec(new Runnable()
		{
			public void run()
			{
				UpdateGalileoProgressMonitor.this.canceled = MessageDialog.openQuestion(
								UpdateGalileoProgressMonitor.this.dialog.getShell(), "Abbrechen",
								"Wollen Sie den Verarbeitungsprozess abbrechen?");
			}
		});
		return this.canceled;
	}
	
	public void updateCountGalileo(int add)
	{
		this.countGalileo = this.countGalileo + add;
	}
	
	public void updateCountColibri(int add)
	{
		this.countColibri = this.countColibri + add;
	}
	
	public void updateCountFailed(int add)
	{
		this.countFailed = this.countFailed + add;
	}
	
	public void updateCountOther(int add)
	{
		this.countOther = this.countOther + add;
	}
}
