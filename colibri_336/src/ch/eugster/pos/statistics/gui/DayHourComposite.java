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
public class DayHourComposite extends Composite implements ITabFolderChild, Listener
{
	private Combo combo;
	
	private static final String ZEITRAUM_14 = "Zeitraum von 08:00 Uhr bis 22:00 Uhr";
	private static final String ZEITRAUM_24 = "Zeitraum von 00:00 Uhr bis 24:00 Uhr";
	
	private static final String REPORT_14 = "Day14HourStatistics";
	private static final String REPORT_24 = "Day24HourStatistics";
	
	private static final String[] ZEITRAUM_TEXT = new String[]
	{ DayHourComposite.ZEITRAUM_14, DayHourComposite.ZEITRAUM_24 };
	
	private static final String[] REPORT_NAMES = new String[]
	{ DayHourComposite.REPORT_14, DayHourComposite.REPORT_24 };
	
	/**
	 * @param parent
	 * @param style
	 */
	public DayHourComposite(Composite parent, int style)
	{
		super(parent, style);
		this.init();
	}
	
	private void init()
	{
		this.setLayout(new GridLayout());
		
		Label label = new Label(this, SWT.NONE);
		label.setText("Auswahl");
		label.setLayoutData(new GridData());
		
		this.combo = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		for (int i = 0; i < DayHourComposite.ZEITRAUM_TEXT.length; i++)
			this.combo.add(DayHourComposite.ZEITRAUM_TEXT[i]);
		
		this.combo.select(0);
		
		this.combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Group helpGroup = new Group(this, SWT.SHADOW_ETCHED_IN);
		helpGroup.setText("Tagesstatistik");
		helpGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		helpGroup.setLayout(layout);
		Label help = new Label(helpGroup, SWT.WRAP);
		help.setLayoutData(new GridData(GridData.FILL_BOTH));
		help.setText(Main.getInstance().getProperty("day.hour.help"));
	}
	
	public String getReportName()
	{
		return DayHourComposite.REPORT_NAMES[this.combo.getSelectionIndex()];
	}
	
	public String getPrintButtonDesignation()
	{
		return Messages.getString("DayHourComposite.Drucken_1"); //$NON-NLS-1$
	}
	
	public String getPrintFileName()
	{
		return Messages.getString("DayHourComposite.Tagesumsatzstatistik_2"); //$NON-NLS-1$
	}
	
	public void handleEvent(Event e)
	{
		if (e.widget instanceof Button)
		{
			Button b = (Button) e.widget;
			Integer i = (Integer) b.getData();
			if (i != null)
			{
				this.index = i.intValue();
			}
		}
	}
	
	public boolean isValid()
	{
		return true;
	}
	
	public int getDayOfWeek()
	{
		return this.index - 1;
	}
	
	// private Button[] weekdays;
	private int index = 0;
}
