/*
 * Created on 27.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import ch.eugster.pos.Messages;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ProductGroupComposite extends Composite implements ITabFolderChild
{
	
	/**
	 * @param parent
	 * @param style
	 */
	public ProductGroupComposite(Composite parent, int style)
	{
		super(parent, style);
		this.init();
	}
	
	private void init()
	{
		this.setLayout(new GridLayout());
		
		Group group = new Group(this, SWT.SHADOW_ETCHED_IN);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout());
		group.setText(Messages.getString("ProductGroupComposite.Optionen_1")); //$NON-NLS-1$
		
		RowLayout typeLayout = new RowLayout();
		typeLayout.wrap = false;
		typeLayout.pack = true;
		typeLayout.justify = false;
		typeLayout.type = SWT.HORIZONTAL;
		typeLayout.marginLeft = 0;
		typeLayout.marginTop = 0;
		typeLayout.marginRight = 0;
		typeLayout.marginBottom = 10;
		typeLayout.spacing = 5;
		
		Composite typeComposite = new Composite(group, SWT.NO_RADIO_GROUP);
		typeComposite.setLayout(typeLayout);
		
		Label typeLabel = new Label(typeComposite, SWT.LEFT);
		typeLabel.setText("Auswertung bezogen auf");
		
		this.typeCombo = new Combo(typeComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		for (int i = 0; i < ProductGroupComposite.TYPE[0].length; i++)
		{
			this.typeCombo.add(ProductGroupComposite.TYPE[0][i]);
		}
		this.typeCombo.select(0);
		this.typeCombo.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				if (ProductGroupComposite.this.getType().equals(ProductGroupComposite.TYPE[1][2]))
				{
					ProductGroupComposite.this.previousYearCompareCheckbox.setSelection(true);
					ProductGroupComposite.this.previousYearCompareCheckbox.setEnabled(false);
				}
				else
				{
					ProductGroupComposite.this.previousYearCompareCheckbox.setEnabled(true);
				}
			}
		});
		
		this.previousYearCompareCheckbox = new Button(group, SWT.CHECK);
		this.previousYearCompareCheckbox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.previousYearCompareCheckbox.setText(Messages.getString("ProductGroupComposite.Mit_&Vergleich_Vorjahr_2")); //$NON-NLS-1$
		
		this.onlyGalileoProductGroupsCheckbox = new Button(group, SWT.CHECK);
		this.onlyGalileoProductGroupsCheckbox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.onlyGalileoProductGroupsCheckbox.setText(Messages
						.getString("ProductGroupComposite.Nur_Warengruppen_aus_&Galileo_3")); //$NON-NLS-1$
		
		this.withOtherSalesCheckbox = new Button(group, SWT.CHECK);
		this.withOtherSalesCheckbox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.withOtherSalesCheckbox.setText("Umsatzneutrale Warengruppen berücksichtigen"); //$NON-NLS-1$
		
		this.withExpensesCheckbox = new Button(group, SWT.CHECK);
		this.withExpensesCheckbox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.withExpensesCheckbox.setText(Messages.getString("ProductGroupComposite.&Ausgaben_ber_u00FCcksichtigen_4")); //$NON-NLS-1$
		
		RowLayout rowLayout = new RowLayout();
		rowLayout.wrap = false;
		rowLayout.pack = true;
		rowLayout.justify = false;
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.marginLeft = 0;
		rowLayout.marginTop = 10;
		rowLayout.marginRight = 0;
		rowLayout.marginBottom = 0;
		rowLayout.spacing = 5;
		
		Composite comp = new Composite(group, SWT.NO_RADIO_GROUP);
		comp.setLayout(rowLayout);
		
		Label sortLabel = new Label(comp, SWT.LEFT);
		sortLabel.setText("Sortierung");
		
		this.sortCombo = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY);
		for (int i = 0; i < ProductGroupComposite.SORT.length; i++)
		{
			this.sortCombo.add(ProductGroupComposite.SORT[0][i]);
		}
		this.sortCombo.select(0);
		
		Group helpGroup = new Group(this, SWT.SHADOW_ETCHED_IN);
		helpGroup.setText("Warengruppenauswertung");
		helpGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		helpGroup.setLayout(layout);
		Label help = new Label(helpGroup, SWT.WRAP);
		help.setLayoutData(new GridData(GridData.FILL_BOTH));
		help.setText(Main.getInstance().getProperty("product.group.help"));
		
	}
	
	public String getPrintButtonDesignation()
	{
		return Messages.getString("ProductGroupComposite.Drucken_5"); //$NON-NLS-1$
	}
	
	public String getPrintFileName()
	{
		if (this.previousYearCompare())
		{
			return Messages.getString("ProductGroupComposite.Warengruppenstatistik_mit_Vergleich_Vorjahr_6"); //$NON-NLS-1$
		}
		else
		{
			return Messages.getString("ProductGroupComposite.Warengruppenstatistik_7"); //$NON-NLS-1$
		}
	}
	
	public boolean previousYearCompare()
	{
		return this.previousYearCompareCheckbox.getSelection();
	}
	
	public boolean onlyGalileoProductGroups()
	{
		return this.onlyGalileoProductGroupsCheckbox.getSelection();
	}
	
	public boolean withOtherSales()
	{
		return this.withOtherSalesCheckbox.getSelection();
	}
	
	public boolean withExpenses()
	{
		return this.withExpensesCheckbox.getSelection();
	}
	
	public String getSort()
	{
		if (this.sortCombo.getText().equals(ProductGroupComposite.SORT[0][0]))
		{
			return ProductGroupComposite.SORT[1][0];
		}
		else
		{
			return ProductGroupComposite.SORT[1][1];
		}
	}
	
	public String getType()
	{
		for (int i = 0; i < ProductGroupComposite.TYPE[0].length; i++)
		{
			if (this.typeCombo.getText().equals(ProductGroupComposite.TYPE[0][i]))
			{
				return ProductGroupComposite.TYPE[1][i];
			}
		}
		return "";
	}
	
	public boolean isValid()
	{
		return true;
	}
	
	private Combo typeCombo;
	private Button previousYearCompareCheckbox;
	private Button onlyGalileoProductGroupsCheckbox;
	private Button withOtherSalesCheckbox;
	private Button withExpensesCheckbox;
	private Combo sortCombo;
	
	public static final String[][] TYPE = new String[][]
	{
	{ "Gesamtumsatz", "Warengruppenumerationsatz", "Vergleich Lagerverkauf - Besorgung" },
	{ "all", "each", "stock-order" } };
	public static final String[][] SORT = new String[][]
	{
	{ "Galileo-Id", "Umsatz" },
	{ "galileoId", "sales" } };
}
