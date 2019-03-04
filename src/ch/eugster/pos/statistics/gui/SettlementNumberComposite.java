/*
 * Created on 17.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.gui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;

import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.db.Settlement;
import ch.eugster.pos.statistics.events.IDateChangeListener;
import ch.eugster.pos.statistics.events.ISalespointSelectionListener;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SettlementNumberComposite extends Composite implements Listener
{
	// 10378 Build 311
	private Long[] settlements = new Long[0];
	
	private Calendar calendar = GregorianCalendar.getInstance();
	
	private DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM);
	
	private DateFormat timeFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT);
	
	// 10378 Build 311
	
	/**
	 * @param parent
	 * @param style
	 */
	public SettlementNumberComposite(Composite parent, int style, Properties properties, SalespointComposite sc,
					DateRangeGroup drg)
	{
		super(parent, style);
		this.properties = properties;
		this.drg = drg;
		this.sc = sc;
		this.init();
	}
	
	private void init()
	{
		SettlementNumberComposite.instance = this;
		
		this.setLayout(new GridLayout());
		
		Group group = new Group(this, SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setText("Auswahl Abschlussnummer"); //$NON-NLS-1$
		
		Composite chooseSettlement = new Composite(group, SWT.NONE);
		chooseSettlement.setLayout(new GridLayout(2, false));
		chooseSettlement.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		this.settlementList = new Combo(chooseSettlement, SWT.READ_ONLY);
		this.settlementList.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.settlementList.addListener(SWT.Selection, Main.getInstance());
		
		this.drg.addListener(IDateChangeListener.DATE_CHANGE_EVENT_TYPE, this);
		this.sc.addListener(ISalespointSelectionListener.SALESPOINT_SELECTION, this);
		
		this.handleEvent(new Event());
	}
	
	public void handleEvent(Event e)
	{
		if (this.drg.getFromDate() != null && this.drg.getToDate() != null)
		{
			// 10378 Build 311
			this.settlementList.setEnabled(false);
			this.settlements = Settlement.selectSettlementsNumbers(this.sc.getSelectedSalespoints(), this.drg
							.getFromDate(), this.drg.getToDate());
			if (this.settlements.length == 0)
				this.settlements = Receipt.selectSettlements(this.sc.getSelectedSalespoints(), this.drg.getFromDate(),
								this.drg.getToDate());
			
			String[] items = new String[this.settlements.length];
			for (int i = 0; i < items.length; i++)
			{
				this.calendar.setTimeInMillis(this.settlements[i].longValue());
				String date = this.dateFormat.format(this.calendar.getTime());
				String time = this.timeFormat.format(this.calendar.getTime());
				items[i] = this.settlements[i].toString() + " " + date + " " + time;
			}
			// 10378 Build 311
			SettlementNumberComposite.getInstance().settlementList.setItems(items);
			if (items.length > 0) SettlementNumberComposite.getInstance().settlementList.select(0);
			SettlementNumberComposite.getInstance().settlementList.setEnabled(true);
		}
		else
		{
			this.settlementList.removeAll();
		}
		if (this.settlementList.getItemCount() > 0) this.settlementList.select(0);
		Main.getInstance().handleEvent(e);
	}
	
	public Long getSelectedSettlement()
	{
		// 10378 Build 311
		if (this.settlementList.getSelectionIndex() > -1)
		{
			return this.settlements[this.settlementList.getSelectionIndex()];
		}
		else
		{
			return null;
		}
		// 10378 Build 311
	}
	
	public boolean settlementSelected()
	{
		return this.settlementList.getSelectionIndex() > -1;
	}
	
	public static SettlementNumberComposite getInstance()
	{
		return SettlementNumberComposite.instance;
	}
	
	private Combo settlementList;
	private Properties properties;
	private DateRangeGroup drg;
	private SalespointComposite sc;
	
	private static SettlementNumberComposite instance;
}
