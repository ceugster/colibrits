/*
 * Created on 15.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.gui;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.db.Version;
import ch.eugster.pos.statistics.events.IDateChangeListener;
import ch.eugster.pos.statistics.events.ISalespointSelectionListener;
import ch.eugster.pos.swt.TextButton;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BookkeepingTransferComposite extends Composite implements KeyListener, FocusListener, ITabFolderChild
{
	private Version version = null;
	
	/**
	 * @param parent
	 * @param style
	 */
	public BookkeepingTransferComposite(Composite parent, int style, Properties properties)
	{
		super(parent, style);
		this.properties = properties;
		this.init();
	}
	
	private void init()
	{
		this.setLayout(new GridLayout());
		
		this.oldTransactionId = this.loadTransactionId();
		this.newTransactionId = new Long(this.oldTransactionId.longValue() + 1);
		
		this.oldBookingId = this.loadBookingId();
		this.newBookingId = new Long(this.oldBookingId.longValue() + 1);
		
		Composite comp = new Composite(this, SWT.NONE);
		comp.setLayout(new GridLayout(2, false));
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label transactionIdLabel = new Label(comp, SWT.NONE);
		transactionIdLabel.setText(Messages.getString("BookkeepingTransferComposite.Transaktions-Nummer_1")); //$NON-NLS-1$
		
		this.transactionIdText = new Text(comp, SWT.BORDER);
		this.transactionIdText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.transactionIdText.setText(this.newTransactionId.toString());
		this.transactionIdText.addKeyListener(this);
		this.transactionIdText.addFocusListener(this);
		
		Label bookingIdLabel = new Label(comp, SWT.NONE);
		bookingIdLabel.setText(Messages.getString("BookkeepingTransferComposite.Buchungs-Nummer_2")); //$NON-NLS-1$
		
		this.bookingIdText = new Text(comp, SWT.BORDER);
		this.bookingIdText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.bookingIdText.setText(this.newBookingId.toString());
		this.bookingIdText.setEnabled(false);
		
		this.message = new Label(this, SWT.NONE);
		this.message.setText(Messages.getString("BookkeepingTransferComposite.Die_Transaktionsnummer_ist_neu._3")); //$NON-NLS-1$
		
		Group group = new Group(this, SWT.SHADOW_ETCHED_IN);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout());
		group.setText(Messages.getString("BookkeepingTransferComposite.Optionen_4")); //$NON-NLS-1$
		
		this.exportPath = new TextButton(group, SWT.NONE, this.loadExportDirectory());
		this.exportPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Group helpGroup = new Group(this, SWT.SHADOW_ETCHED_IN);
		helpGroup.setText("Belegtransfer in die Finanzbuchhaltung");
		helpGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		helpGroup.setLayout(layout);
		Label help = new Label(helpGroup, SWT.WRAP);
		help.setLayoutData(new GridData(GridData.FILL_BOTH));
		help.setText(Main.getInstance().getProperty("bookkeeping.transfer.help"));
		
	}
	
	public void setMessage(String text)
	{
		this.message.setText(text);
	}
	
	public String getPrintButtonDesignation()
	{
		return Messages.getString("BookkeepingTransferComposite.Exportieren_5"); //$NON-NLS-1$
	}
	
	public String getPrintFileName()
	{
		return ""; //$NON-NLS-1$
	}
	
	private Long loadTransactionId()
	{
		return this.getVersion().getTransactionId();
	}
	
	private Version getVersion()
	{
		if (this.version == null)
		{
			this.version = Version.select(Database.getCurrent());
		}
		return this.version;
	}
	
	private void saveTransactionId()
	{
		if (!this.newTransactionId.equals(this.oldTransactionId))
		{
			this.getVersion().setTransactionId(this.newTransactionId);
			this.getVersion().store();
			// 10441
			this.oldTransactionId = this.newTransactionId;
			this.newTransactionId = new Long(this.oldTransactionId.longValue() + 1);
			this.transactionIdText.setText(this.newTransactionId.toString());
			// 10441
		}
	}
	
	public Long getSelectedTransactionId()
	{
		return this.parse(this.transactionIdText.getText());
	}
	
	public Long getNewTransactionId()
	{
		return this.newTransactionId;
	}
	
	public void updateTransactionId(Long newTransactionId)
	{
		this.newTransactionId = newTransactionId;
		this.transactionIdText.setText(newTransactionId.toString());
		this.saveTransactionId();
	}
	
	private Long loadBookingId()
	{
		return this.parse(this.properties.getProperty(
						Messages.getString("BookkeepingTransferComposite.bookingId_10"), "0")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public Long getBookingId()
	{
		return this.newBookingId;
	}
	
	public Long incrementBookingId()
	{
		Long id = this.newBookingId;
		this.newBookingId = new Long(this.newBookingId.longValue() + 1);
		return id;
	}
	
	public void setExportDirectory(String path)
	{
		this.exportPath.setDirectory(path);
	}
	
	public String getExportDirectory()
	{
		return this.exportPath.getDirectory();
	}
	
	private String loadExportDirectory()
	{
		return this.properties.getProperty(Messages.getString("BookkeepingTransferComposite.fibu-export-path_12"), ""); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private Long parse(String text)
	{
		Long num = null;
		try
		{
			num = new Long(Long.parseLong(text));
		}
		catch (NumberFormatException e)
		{
		}
		return num;
	}
	
	public void keyPressed(KeyEvent e)
	{
		if (e.getSource().equals(this.transactionIdText))
		{
			// if (e.character < 48 || e.character > 57) {
			// e.
			// }
			int chars = this.transactionIdText.getCharCount();
			int pos = this.transactionIdText.getCaretPosition();
			StringBuffer sb = new StringBuffer(""); //$NON-NLS-1$
			for (int i = 0; i < this.transactionIdText.getText().length(); i++)
			{
				if ("0123456789".indexOf(this.transactionIdText.getText().charAt(i)) == -1) { //$NON-NLS-1$
					sb.append(this.transactionIdText.getText().charAt(i));
				}
			}
			this.transactionIdText.setText(sb.toString());
			if (this.transactionIdText.getCharCount() < chars)
			{
				this.transactionIdText.setSelection(pos - 1);
			}
			else
			{
				this.transactionIdText.setSelection(pos);
			}
		}
	}
	
	public void keyReleased(KeyEvent e)
	{
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.FocusListener#focusGained(org.eclipse.swt.events
	 * .FocusEvent)
	 */
	public void focusGained(FocusEvent e)
	{
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt.events
	 * .FocusEvent)
	 */
	public void focusLost(FocusEvent e)
	{
		// 10441
		Long transactionId = new Long(1L);
		try
		{
			transactionId = Long.valueOf(this.transactionIdText.getText());
		}
		catch (NumberFormatException nfe)
		{
		}
		Iterator itr = Receipt.selectByTransactionId(transactionId);
		// 10441
		ArrayList salespoints = new ArrayList();
		Date start = null;
		Date end = null;
		if (itr.hasNext())
		{
			this.message.setText(Messages
							.getString("BookkeepingTransferComposite.Die_Transaktionsnummer_wird_bereits_verwendet._Wenn_Sie_diese_Transaktion_starten,_wird_die_alte_Transaktionsnummer__u00FCberschrieben._16")); //$NON-NLS-1$
			while (itr.hasNext())
			{
				Object[] record = (Object[]) itr.next();
				salespoints.add(Salespoint.selectById((Long) record[1]));
				Date transStart = (Timestamp) record[2];
				Date transEnd = (Timestamp) record[3];
				if (start == null)
				{
					start = transStart;
				}
				else
				{
					if (transStart.before(start))
					{
						start = transStart;
					}
				}
				if (end == null)
				{
					end = transEnd;
				}
				else
				{
					if (transEnd.before(end))
					{
						end = transEnd;
					}
				}
			}
			this.fireSalespointsSelectionEvent((Salespoint[]) salespoints.toArray(new Salespoint[0]));
			this.fireDateChangeEvent(start, end);
		}
		else
		{
			this.fireDateChangeEvent(null, null);
			this.message.setText(Messages.getString("BookkeepingTransferComposite.Die_Transaktionsnummer_ist_neu._17")); //$NON-NLS-1$
		}
	}
	
	public void fireDateChangeEvent(Date from, Date to)
	{
		Event event = new Event();
		Date[] dates = new Date[]
		{ from, to };
		event.data = dates;
		event.widget = this;
		event.display = this.getDisplay();
		event.item = this.transactionIdText;
		event.type = IDateChangeListener.DATE_CHANGE_EVENT_TYPE;
		
		if (this.isListening(IDateChangeListener.DATE_CHANGE_EVENT_TYPE))
		{
			this.notifyListeners(IDateChangeListener.DATE_CHANGE_EVENT_TYPE, event);
		}
	}
	
	public void fireSalespointsSelectionEvent(Salespoint[] salespoints)
	{
		Event event = new Event();
		event.data = salespoints;
		event.widget = this;
		event.display = this.getDisplay();
		event.type = ISalespointSelectionListener.SALESPOINT_SELECTION;
		
		if (this.isListening(ISalespointSelectionListener.SALESPOINT_SELECTION))
		{
			this.notifyListeners(ISalespointSelectionListener.SALESPOINT_SELECTION, event);
		}
	}
	
	public boolean isValid()
	{
		return true;
	}
	
	private Text transactionIdText;
	private Text bookingIdText;
	private Label message;
	private TextButton exportPath;
	private Properties properties;
	private Long oldTransactionId;
	private Long newTransactionId;
	private Long oldBookingId;
	private Long newBookingId;
}
