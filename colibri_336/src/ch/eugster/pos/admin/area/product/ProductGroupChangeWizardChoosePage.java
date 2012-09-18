/*
 * Created on 05.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.product;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.db.Table;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ProductGroupChangeWizardChoosePage extends WizardPage implements Listener
{
	
	/**
	 * @param pageName
	 */
	public ProductGroupChangeWizardChoosePage(String pageName, ProductGroup source)
	{
		super(pageName);
		this.init(source);
	}
	
	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public ProductGroupChangeWizardChoosePage(String pageName, String title, ImageDescriptor titleImage,
					ProductGroup source)
	{
		super(pageName, title, titleImage);
		this.init(source);
	}
	
	private void init(ProductGroup source)
	{
		this.setSourceGroup(source);
		this.setTitle("Auswahl Warengruppen"); //$NON-NLS-1$
		this
						.setDescription("Wählen Sie alte und neue Warengruppe aus. \nAchtung: die Steuersätze auf den Belegspositionen werden nicht geändert."); //$NON-NLS-1$
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
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 20;
		Composite composite = new Composite(parent, SWT.FLAT);
		composite.setLayout(layout);
		
		Label typeLabel = new Label(composite, SWT.FLAT);
		typeLabel.setText("&Warengruppenart"); //$NON-NLS-1$
		typeLabel.setLayoutData(new GridData(GridData.BEGINNING));
		
		this.typeCombo = new Combo(composite, SWT.SIMPLE | SWT.DROP_DOWN | SWT.READ_ONLY);
		this.typeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.typeCombo.addListener(SWT.Selection, this);
		this.typeCombo.setItems(this.typeNames);
		this.typeCombo.select(this.sourceGroup.type);
		this.typeCombo.addListener(SWT.Selection, this);
		
		Label sourceLabel = new Label(composite, SWT.FLAT);
		sourceLabel.setText("&Bisherige Warengruppe"); //$NON-NLS-1$
		sourceLabel.setLayoutData(new GridData(GridData.BEGINNING));
		
		this.sourceCombo = new Combo(composite, SWT.SIMPLE | SWT.DROP_DOWN | SWT.READ_ONLY);
		this.sourceCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.sourceCombo.addListener(SWT.Selection, this);
		ProductGroup group = ProductGroup.getEmptyInstance();
		group.setId(Table.ZERO_VALUE);
		this.sourceCombo.setItems(this.getProductGroups(group));
		if (this.sourceGroup.getId() != null && !this.sourceGroup.getId().equals(ProductGroup.ZERO_VALUE))
		{
			if (this.sourceCombo.getItemCount() > 0) this.sourceCombo.select(this.selectSource(this.sourceGroup));
		}
		else
		{
			if (this.sourceCombo.getItemCount() > 0) this.sourceCombo.select(0);
		}
		this.setSourceGroup(this.sourceGroups[this.sourceCombo.getSelectionIndex()]);
		
		Label targetLabel = new Label(composite, SWT.FLAT);
		targetLabel.setText("&Neue Warengruppe"); //$NON-NLS-1$
		targetLabel.setLayoutData(new GridData(GridData.BEGINNING));
		
		this.targetCombo = new Combo(composite, SWT.SIMPLE | SWT.DROP_DOWN | SWT.READ_ONLY);
		this.targetCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.targetCombo.addListener(SWT.Selection, this);
		this.targetCombo.setItems(this.getProductGroups(this.sourceGroups[this.sourceCombo.getSelectionIndex()]));
		if (this.targetCombo.getItemCount() > 0)
		{
			this.targetCombo.select(0);
			this.setTargetGroup(this.targetGroups[this.targetCombo.getSelectionIndex()]);
		}
		
		this.targetCombo.setFocus();
		
		this.setControl(composite);
		this.getControl().addListener(SWT.Paint, this);
		
		this.setPageComplete(this.validate());
	}
	
	private int selectSource(ProductGroup group)
	{
		if (group.getId() == null || group.getId().equals(Table.ZERO_VALUE)) return 0;
		for (int i = 0; i < this.sourceGroups.length; i++)
		{
			if (this.sourceGroups[i].getId().equals(this.sourceGroup.getId())) return i;
		}
		return 0;
	}
	
	public String[] getProductGroups(ProductGroup except)
	{
		ProductGroup[] groups = ProductGroup.select(except, this.getProductGroupType());
		
		if (except.getId() == null || except.getId().equals(ProductGroup.ZERO_VALUE))
		{
			this.sourceGroups = groups.length == 0 ? null : groups;
		}
		else
		{
			this.targetGroups = groups.length == 0 ? null : groups;
		}
		String[] names = new String[groups.length];
		for (int i = 0; i < groups.length; i++)
		{
			names[i] = groups[i].name + " (" + groups[i].galileoId + ")";
		}
		return names;
	}
	
	public void setPositionCount()
	{
		ProductGroupChangeWizard wizard = (ProductGroupChangeWizard) this.getWizard();
		if (this.sourceCombo.getSelectionIndex() > -1)
		{
			wizard.getTestPage().setPositionCount(this.positionCount);
		}
		else
		{
			wizard.getTestPage().setPositionCount(0);
		}
	}
	
	public void handleEvent(Event e)
	{
		if (e.widget.equals(this.typeCombo) && e.type == SWT.Selection)
		{
			this.sourceCombo.setItems(this.getProductGroups(ProductGroup.getEmptyInstance()));
			this.sourceCombo.deselectAll();
			if (this.sourceCombo.getItemCount() > 0)
			{
				this.sourceCombo.select(0);
				this.setSourceGroup(this.sourceGroups[this.sourceCombo.getSelectionIndex()]);
			}
			this.targetCombo.setItems(this.getProductGroups(this.sourceGroups[this.sourceCombo.getSelectionIndex()]));
			this.targetCombo.deselectAll();
			if (this.targetCombo.getItemCount() > 0)
			{
				this.targetCombo.select(0);
			}
		}
		else if (e.widget.equals(this.sourceCombo) && e.type == SWT.Selection)
		{
			this.setSourceGroup(this.sourceGroups[this.sourceCombo.getSelectionIndex()]);
			ProductGroup currentTargetGroup = this.targetGroups[this.targetCombo.getSelectionIndex()];
			this.targetCombo.setItems(this.getProductGroups(this.sourceGroups[this.sourceCombo.getSelectionIndex()]));
			this.targetCombo.deselectAll();
			for (int i = 0; i < this.targetGroups.length; i++)
			{
				if (this.targetGroups[i].getId().equals(currentTargetGroup.getId()))
				{
					this.targetCombo.select(i);
				}
			}
			if (this.targetCombo.getSelectionIndex() == -1 && this.targetCombo.getItemCount() > 0)
			{
				this.targetCombo.select(0);
			}
		}
		else if (e.widget.equals(this.targetCombo) && e.type == SWT.Selection)
		{
			this.setTargetGroup(this.targetGroups[this.targetCombo.getSelectionIndex()]);
			ProductGroup currentSourceGroup = this.sourceGroups[this.sourceCombo.getSelectionIndex()];
			this.sourceCombo.setItems(this.getProductGroups(this.targetGroups[this.targetCombo.getSelectionIndex()]));
			this.sourceCombo.deselectAll();
			for (int i = 0; i < this.sourceGroups.length; i++)
			{
				if (this.sourceGroups[i].getId().equals(currentSourceGroup.getId()))
				{
					this.sourceCombo.select(i);
				}
			}
			if (this.sourceCombo.getSelectionIndex() == -1 && this.sourceCombo.getItemCount() > 0)
			{
				this.sourceCombo.select(0);
			}
		}
		this.setPositionCount();
		this.setPageComplete(this.validate());
	}
	
	private void setSourceGroup(ProductGroup group)
	{
		this.sourceGroup = group;
		this.positionCount = Position.count(this.sourceGroup);
	}
	
	private void setTargetGroup(ProductGroup group)
	{
		this.targetGroup = group;
	}
	
	public boolean validate()
	{
		return this.sourceGroups != null && this.targetGroups != null;
	}
	
	public ProductGroup getSource()
	{
		return this.sourceGroup;
	}
	
	public ProductGroup getTarget()
	{
		return this.targetGroup;
	}
	
	public int getPositionCount()
	{
		return this.positionCount;
	}
	
	public Integer getProductGroupType()
	{
		return new Integer(this.typeIds[this.typeCombo.getSelectionIndex()]);
	}
	
	private Combo typeCombo;
	private Combo sourceCombo;
	private Combo targetCombo;
	private int positionCount;
	
	private String[] typeNames = new String[]
	{ "Umsatzrelevante Warengruppen", "Umsatzneutrale Warengruppen", "Ausgaben" };
	private int[] typeIds = new int[]
	{ 0, 1, 2 };
	private ProductGroup sourceGroup;
	private ProductGroup targetGroup;
	private ProductGroup[] sourceGroups = new ProductGroup[]
	{};
	private ProductGroup[] targetGroups = new ProductGroup[]
	{};
}
