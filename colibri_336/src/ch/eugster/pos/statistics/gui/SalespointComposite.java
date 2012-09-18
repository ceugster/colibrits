/*
 * Created on 26.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.gui;

import java.util.ArrayList;
import java.util.Properties;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.statistics.events.ISalespointSelectionListener;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SalespointComposite extends Composite implements Listener, ControlListener, ISalespointSelectionListener
{
	
	/**
	 * @param parent
	 * @param style
	 * @param salespoints
	 */
	public SalespointComposite(Composite parent, int style, Properties properties)
	{
		super(parent, style);
		this.properties = properties;
		this.init();
	}
	
	private void init()
	{
		this.setLayout(new GridLayout());
		this.addControlListener(this);
		
		Group salespointGroup = new Group(this, SWT.SHADOW_ETCHED_IN);
		salespointGroup.setLayout(new GridLayout(1, false));
		salespointGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		salespointGroup.setText(Messages.getString("SalespointComposite.Auswahl_Kassen_1")); //$NON-NLS-1$
		
		this.salespointViewer = new TableViewer(salespointGroup, SWT.BORDER | SWT.MULTI);
		this.salespointViewer.setContentProvider(new SalespointTableContentProvider());
		this.salespointViewer.setLabelProvider(new SalespointTableLabelProvider());
		this.salespointViewer.setInput(((IStructuredContentProvider) this.salespointViewer.getContentProvider())
						.getElements(null));
		
		this.salespointViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		this.salespointViewer.getTable().setHeaderVisible(true);
		this.salespointViewer.getTable().setLinesVisible(false);
		String[] columnNames = ((SalespointTableLabelProvider) this.salespointViewer.getLabelProvider())
						.getColumnNames();
		
		TableColumn tc = new TableColumn(this.salespointViewer.getTable(), SWT.NULL);
		tc.setText(columnNames[0]);
		
		if (null != this.salespointViewer.getInput())
		{
			Salespoint[] salespoints = this.getSalespointListFromProperties();
			this.setSelectedSalespoints(salespoints);
		}
		this.salespointViewer.getTable().addListener(SWT.Selection, this);
		
		Composite buttonParent = new Composite(salespointGroup, SWT.NONE);
		buttonParent.setLayout(new GridLayout(2, true));
		buttonParent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		this.allButton = new Button(buttonParent, SWT.PUSH);
		this.allButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.allButton.setText(Messages.getString("SalespointComposite.&Alle_ausw_u00E4hlen_2")); //$NON-NLS-1$
		this.allButton.setToolTipText(Messages.getString("SalespointComposite.Alle_Kassen_ausw_u00E4hlen_3")); //$NON-NLS-1$
		this.allButton.addListener(SWT.Selection, this);
		
		this.inverseButton = new Button(buttonParent, SWT.PUSH);
		this.inverseButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.inverseButton.setText(Messages.getString("SalespointComposite.Auswahl_umkehren_4")); //$NON-NLS-1$
		this.inverseButton.setToolTipText(Messages.getString("SalespointComposite.Auswahl_der_Kassen_umkehren_5")); //$NON-NLS-1$
		this.inverseButton.addListener(SWT.Selection, this);
	}
	
	public void controlResized(ControlEvent e)
	{
		for (int i = 0; i < this.salespointViewer.getTable().getColumnCount(); i++)
		{
			this.salespointViewer.getTable().getColumn(i).pack();
		}
	}
	
	public String getSalespointPropertyFromList(Salespoint[] salespoints)
	{
		String sps = ""; //$NON-NLS-1$
		for (int i = 0; i < salespoints.length; i++)
		{
			if (sps.length() == 0)
			{
				sps += String.valueOf(salespoints[i].getId());
			}
			else
			{
				sps += "|" + String.valueOf(salespoints[i].getId()); //$NON-NLS-1$
			}
		}
		return sps;
	}
	
	public Salespoint[] getSalespointListFromProperties()
	{
		String salespoints = this.properties.getProperty("salespoints"); //$NON-NLS-1$
		ArrayList list = new ArrayList();
		String[] split = salespoints.split("|"); //$NON-NLS-1$
		for (int i = 0; i < split.length; i++)
		{
			Long id = new Long(0l);
			if (split[i] != null && split[i].length() > 0)
			{
				try
				{
					id = Long.valueOf(split[i]);
				}
				catch (NumberFormatException e)
				{
				}
				if (!id.equals(new Long(0l)))
				{
					Salespoint sp = Salespoint.selectById(id);
					if (sp != null && sp.getId() != null && !sp.getId().equals(Salespoint.ZERO_VALUE))
					{
						list.add(sp);
					}
				}
			}
		}
		
		Salespoint[] salespoint = new Salespoint[list.size()];
		for (int i = 0; i < list.size(); i++)
		{
			salespoint[i] = (Salespoint) list.get(i);
		}
		return salespoint;
	}
	
	public void setSalespoints(Salespoint[] salespoints)
	{
		this.salespointViewer.setInput(salespoints);
	}
	
	public void setSelectedSalespoints(Salespoint[] salespoints)
	{
		ArrayList list = new ArrayList();
		Salespoint[] input = (Salespoint[]) this.salespointViewer.getInput();
		for (int i = 0; i < input.length; i++)
		{
			if (this.find(input[i], salespoints) >= 0)
			{
				list.add(input[i]);
			}
		}
		
		Salespoint[] selected = new Salespoint[list.size()];
		for (int i = 0; i < list.size(); i++)
		{
			selected[i] = (Salespoint) list.get(i);
		}
		ISelection selection = new StructuredSelection(selected);
		this.salespointViewer.setSelection(selection);
	}
	
	private int find(Salespoint salespoint, Salespoint[] salespoints)
	{
		for (int i = 0; i < salespoints.length; i++)
		{
			if (salespoint.getId().equals(salespoints[i].getId()))
			{
				return i;
			}
		}
		return -1;
	}
	
	public Salespoint[] getSelectedSalespoints()
	{
		StructuredSelection sel = (StructuredSelection) this.salespointViewer.getSelection();
		if (sel.isEmpty())
		{
			return new Salespoint[0];
		}
		else
		{
			Object[] object = sel.toArray();
			Salespoint[] salespoints = new Salespoint[object.length];
			for (int i = 0; i < salespoints.length; i++)
			{
				salespoints[i] = (Salespoint) object[i];
			}
			return salespoints;
		}
	}
	
	public void handleEvent(Event e)
	{
		if (e.type == ISalespointSelectionListener.SALESPOINT_SELECTION)
		{
			Salespoint[] salespoints = (Salespoint[]) e.data;
			this.setSelectedSalespoints(salespoints);
		}
		else if (e.type == SWT.Selection)
		{
			if (e.widget.equals(this.allButton))
			{
				Salespoint[] salespoints = (Salespoint[]) this.salespointViewer.getInput();
				ISelection sel = new StructuredSelection(salespoints);
				this.salespointViewer.setSelection(sel, true);
			}
			else if (e.widget.equals(this.inverseButton))
			{
				Salespoint[] spList = (Salespoint[]) this.salespointViewer.getInput();
				
				IStructuredSelection ss = (IStructuredSelection) this.salespointViewer.getSelection();
				Object[] spSel = ss.toArray();
				
				ArrayList newList = new ArrayList();
				for (int i = 0; i < spList.length; i++)
				{
					boolean found = false;
					for (int j = 0; j < spSel.length; j++)
					{
						if (spSel[j].equals(spList[i]))
						{
							found = true;
						}
					}
					if (!found)
					{
						newList.add(spList[i]);
					}
				}
				
				Salespoint[] salespoints = (Salespoint[]) newList.toArray(new Salespoint[0]);
				ISelection sel = new StructuredSelection(salespoints);
				this.salespointViewer.setSelection(sel, true);
			}
			else if (e.widget.equals(this.salespointViewer.getTable()))
			{
				StructuredSelection sel = (StructuredSelection) this.salespointViewer.getSelection();
				e.data = sel.toArray();
			}
		}
		Event event = new Event();
		event.type = ISalespointSelectionListener.SALESPOINT_SELECTION;
		event.widget = this;
		event.detail = event.type;
		event.display = this.getDisplay();
		event.doit = true;
		this.notifyListeners(event.type, event);
	}
	
	public int getSalespointCount()
	{
		return this.salespointViewer.getTable().getItemCount();
	}
	
	public boolean areAllSalespointsSelected()
	{
		IStructuredSelection sel = (IStructuredSelection) this.salespointViewer.getSelection();
		Object[] input = (Object[]) this.salespointViewer.getInput();
		return input.length == sel.size();
	}
	
	public TableViewer getSalespointViewer()
	{
		return this.salespointViewer;
	}
	
	public void controlMoved(ControlEvent e)
	{
	}
	
	private Properties properties;
	private TableViewer salespointViewer;
	private TableColumn nameCol;
	private Button allButton;
	private Button inverseButton;
}
