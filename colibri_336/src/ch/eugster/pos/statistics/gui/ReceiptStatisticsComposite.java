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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ReceiptStatisticsComposite extends Composite implements ITabFolderChild, Listener
{
	
	private Group choice;
	
	private Button[] radio;
	
	/**
	 * @param parent
	 * @param style
	 */
	public ReceiptStatisticsComposite(Composite parent, int style)
	{
		super(parent, style);
		this.init();
	}
	
	private void init()
	{
		this.setLayout(new GridLayout());
		
		this.choice = new Group(this, SWT.NONE);
		this.choice.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.choice.setLayout(new GridLayout(2, false));
		this.radio = new Button[2];
		this.radio[0] = new Button(this.choice, SWT.RADIO);
		this.radio[0].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.radio[0].setText("gruppiert nach Kassen");
		this.radio[0].setSelection(true);
		this.radio[0].addListener(SWT.Selection, this);
		this.radio[1] = new Button(this.choice, SWT.RADIO);
		this.radio[1].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.radio[1].setText("gruppiert nach Jahren");
		this.radio[1].addListener(SWT.Selection, this);
		
		Group helpGroup = new Group(this, SWT.SHADOW_ETCHED_IN);
		helpGroup.setText("Belegstatistik");
		helpGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		helpGroup.setLayout(layout);
		Label help = new Label(helpGroup, SWT.WRAP);
		help.setLayoutData(new GridData(GridData.FILL_BOTH));
		help.setText(Main.getInstance().getProperty("receipt.statistics.help"));
	}
	
	public String getPrintButtonDesignation()
	{
		return "Drucken";
	}
	
	public int getGroupSelection()
	{
		return this.index;
	}
	
	public String getPrintFileName()
	{
		return "Belegstatistik";
	}
	
	public void handleEvent(Event e)
	{
		if (e.widget instanceof Button)
		{
			if (e.widget.equals(this.radio[0]))
			{
				this.index = 0;
			}
			else if (e.widget.equals(this.radio[1]))
			{
				this.index = 1;
			}
			else
			{
				Button b = (Button) e.widget;
				Integer i = (Integer) b.getData();
				if (i != null)
				{
					this.index = i.intValue();
				}
			}
		}
	}
	
	public int getDayOfWeek()
	{
		return this.index - 1;
	}
	
	public boolean isValid()
	{
		return true;
	}
	
	private Button[] weekdays;
	/*
	 * der gewählte Index der Gruppenbildung
	 */
	private int index = 0;
}
