/*
 * Created on 05.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.product;

import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;

import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.ProductGroup;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ProductGroupChangeWizard extends Wizard implements IRunnableWithProgress
{
	
	/**
	 * 
	 */
	public ProductGroupChangeWizard()
	{
		super();
		this.init(ProductGroup.getEmptyInstance());
	}
	
	/**
	 * 
	 */
	public ProductGroupChangeWizard(ProductGroup source)
	{
		super();
		this.init(source);
	}
	
	public void init(ProductGroup group)
	{
		this.setWindowTitle("Warengruppen"); //$NON-NLS-1$
		this.choosePage = new ProductGroupChangeWizardChoosePage("source", group); //$NON-NLS-1$
		this.addPage(this.choosePage);
		this.testPage = new ProductGroupChangeWizardTestPage("target"); //$NON-NLS-1$
		this.addPage(this.testPage);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish()
	{
		if (MessageDialog.openQuestion(this.getShell(), "Belegpositionen übertragen",
						"Wollen Sie die Belegpositionen, die bislang der Warengruppe "
										+ this.choosePage.getSource().name
										+ " zugeordnet waren, der neu gewählten Warengruppe "
										+ this.choosePage.getTarget().name
										+ " zuordnen? Diese Funktion ist kann nicht rückgängig gemacht werden!"))
		{
			try
			{
				ProgressMonitorDialog monitor = new ProgressMonitorDialog(this.getShell());
				monitor.run(true, true, this);
				MessageDialog.openInformation(this.getShell(), "Positionen übertragen",
								"Die Positionen wurden erfolgreich übertragen.");
			}
			catch (InvocationTargetException ite)
			{
				ite.printStackTrace();
			}
			catch (InterruptedException ie)
			{
				ie.printStackTrace();
			}
		}
		return true;
	}
	
	public void run(IProgressMonitor monitor)
	{
		ProductGroup pgSource = this.choosePage.getSource();
		ProductGroup pgTarget = this.choosePage.getTarget();
		long count = Position.countProductGroupPositions(pgSource);
		if (count == 0)
		{
			Toolkit.getDefaultToolkit().beep();
			MessageDialog
							.openInformation(this.getShell(), "Keine Positionen gefunden",
											"Es konnten keine Positionen gefunden werden, die der bisherigen Warengruppe zugeordnet sind.");
			return;
		}
		long mainLoops = count / 10 + 1;
		long current = 0;
		
		monitor.beginTask("Die Positionen werde aktualisiert...(" + current + "/" + count + ")", new Long(mainLoops)
						.intValue());
		for (long l = 0; l < mainLoops; l++)
		{
			Position[] pos = Position.selectProductGroupPositions(pgSource);
			Database.getCurrent().getBroker().beginTransaction();
			for (int i = 0; i < pos.length; i++)
			{
				pos[i].setProductGroup(pgTarget);
				pos[i].store();
				current++;
				monitor.setTaskName("Die Positionen werde aktualisiert...(" + current + "/" + count + ")");
			}
			Database.getCurrent().getBroker().commitTransaction();
			monitor.worked(1);
		}
		monitor.done();
	}
	
	public ProductGroupChangeWizardChoosePage getChoosePage()
	{
		return this.choosePage;
	}
	
	public ProductGroupChangeWizardTestPage getTestPage()
	{
		return this.testPage;
	}
	
	private ProductGroupChangeWizardChoosePage choosePage;
	private ProductGroupChangeWizardTestPage testPage;
}
