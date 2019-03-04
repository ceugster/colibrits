/*
 * Created on 27.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.gui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import ch.eugster.pos.Messages;
import ch.eugster.pos.statistics.events.IDateChangeListener;

import com.tiff.common.ui.datepicker.DatePickerCombo;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DateRangeGroup extends Composite implements SelectionListener, ModifyListener, IDateChangeListener
{
	
	/**
	 * @param parent
	 * @param style
	 * @param properties
	 */
	public DateRangeGroup(Composite parent, int style, Properties properties)
	{
		super(parent, style);
		// this.properties = properties;
		this.init();
	}
	
	private void init()
	{
		this.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.setLayout(new GridLayout());
		
		Group dateRangeGroup = new Group(this, SWT.SHADOW_ETCHED_IN);
		dateRangeGroup.setLayout(new GridLayout(2, false));
		dateRangeGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		dateRangeGroup.setText(Messages.getString("DateRangeGroup.Datumsbereich_1")); //$NON-NLS-1$
		
		// DateFormat dateFormat = new SimpleDateFormat();
		
		// @@@2005-09-21 Wunsch von FEG: Aktuelles Tagesdatum anzeigen beim
		// Start
		this.from = GregorianCalendar.getInstance().getTime();
		this.to = GregorianCalendar.getInstance().getTime();
		
		// try {
		//			from = dateFormat.parse(properties.getProperty("fromDate")); //$NON-NLS-1$
		//			to = dateFormat.parse(properties.getProperty("toDate")); //$NON-NLS-1$
		// }
		// catch (ParseException e) {}
		
		Label fromLabel = new Label(dateRangeGroup, SWT.NULL);
		fromLabel.setText(Messages.getString("DateRangeGroup.Startdatum_4")); //$NON-NLS-1$
		
		// int dpStyle = DatePickerStyle.HIDE_WHEN_NOT_IN_FOCUS|
		// // DatePickerStyle.DISABLE_MONTH_BUTTONS |
		// DatePickerStyle.NO_TODAY_BUTTON |
		// DatePickerStyle.SINGLE_CLICK_SELECTION |
		// // DatePickerStyle.TEN_YEARS_BUTTONS |
		// DatePickerStyle.WEEKS_STARTS_ON_MONDAY|
		// DatePickerStyle.YEAR_BUTTONS |
		// 0 ;
		//
		this.fromDate = new DatePickerCombo(dateRangeGroup, SWT.BORDER);
		this.fromDate.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.fromDate.setSize(new Point(82, this.fromDate.getSize().y));
		this.fromDate.setFormat(new SimpleDateFormat(Messages.getString("DateRangeGroup.dd.MM.yyyy_5"))); //$NON-NLS-1$
		this.fromDate.setDate(this.from);
		this.fromDate.addSelectionListener(this);
		this.fromDate.addModifyListener(this);
		
		Label toLabel = new Label(dateRangeGroup, SWT.NULL);
		toLabel.setText(Messages.getString("DateRangeGroup.Enddatum_6")); //$NON-NLS-1$
		
		this.toDate = new DatePickerCombo(dateRangeGroup, SWT.BORDER);
		this.toDate.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.toDate.setSize(new Point(82, this.toDate.getSize().y));
		this.toDate.setFormat(new SimpleDateFormat(Messages.getString("DateRangeGroup.dd.MM.yyyy_7"))); //$NON-NLS-1$
		this.toDate.setDate(this.to);
		this.toDate.addSelectionListener(this);
		this.toDate.addModifyListener(this);
	}
	
	public void setFromDate(Date date)
	{
		this.fromDate.setDate(date);
	}
	
	public Date getFromDate()
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime((Date) this.fromDate.getDate().clone());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	
	public void setToDate(Date date)
	{
		this.toDate.setDate(date);
	}
	
	public Date getToDate()
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime((Date) this.toDate.getDate().clone());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	
	// @2006-03-08 10033
	public Date readToDate()
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime((Date) this.toDate.getDate().clone());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	
	// @2006-03-08 10033
	
	public void widgetDefaultSelected(SelectionEvent e)
	{
		this.widgetSelected(e);
	}
	
	public void modifyText(ModifyEvent e)
	{
		if (e.widget instanceof DatePickerCombo)
		{
			DatePickerCombo combo = (DatePickerCombo) e.widget;
			Calendar calendar = new GregorianCalendar(1990, 1, 1);
			
			if (combo.getDate() != null && combo.getDate().after(calendar.getTime()))
			{
				Event event = new Event();
				event.type = IDateChangeListener.DATE_CHANGE_EVENT_TYPE;
				event.widget = this;
				event.detail = event.type;
				event.display = this.getDisplay();
				event.doit = true;
				this.notifyListeners(event.type, event);
			}
		}
	}
	
	public void widgetSelected(SelectionEvent e)
	{
		if (e.getSource().equals(this.fromDate))
		{
			if (this.fromDate.getDate().after(this.toDate.getDate()))
			{
				this.toDate.setDate(this.fromDate.getDate());
			}
		}
		else if (e.getSource().equals(this.toDate))
		{
			if (this.toDate.getDate().before(this.fromDate.getDate()))
			{
				this.fromDate.setDate(this.toDate.getDate());
			}
		}
		Event event = new Event();
		event.type = IDateChangeListener.DATE_CHANGE_EVENT_TYPE;
		event.widget = this;
		event.detail = event.type;
		event.display = this.getDisplay();
		event.doit = true;
		this.notifyListeners(event.type, event);
		
	}
	
	public void handleEvent(Event e)
	{
		if (e.type == IDateChangeListener.DATE_CHANGE_EVENT_TYPE)
		{
			Date[] dates = (Date[]) e.data;
			if (dates[0] == null)
			{
				this.fromDate.setDate(this.from);
			}
			else
			{
				this.fromDate.setDate(dates[0]);
			}
			if (dates[1] == null)
			{
				this.toDate.setDate(this.to);
			}
			else
			{
				this.toDate.setDate(dates[1]);
			}
			if (this.toDate.getDate().before(this.fromDate.getDate()))
			{
				this.fromDate.setDate(this.toDate.getDate());
			}
			this.notifyListeners(e.type, e);
		}
	}
	
	public void setToDateEnabled(boolean enable)
	{
		this.toDate.setEnabled(enable);
	}
	
	private Date from;
	private Date to;
	private DatePickerCombo fromDate;
	private DatePickerCombo toDate;
	// private Properties properties;
}
