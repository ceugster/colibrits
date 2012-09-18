/*
 * Created on 26.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import ch.eugster.pos.Messages;
import ch.eugster.pos.statistics.gui.ITabFolderChild;
import ch.eugster.pos.statistics.gui.Main;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PrintDestinationGroup extends Composite implements ControlListener, Listener
{
	
	/**
	 * @param parent
	 * @param style
	 */
	public PrintDestinationGroup(Composite parent, int style)
	{
		super(parent, style);
		this.init(0);
	}
	
	/**
	 * @param parent
	 * @param style
	 * @param defaultValue
	 */
	public PrintDestinationGroup(Composite parent, int style, int defaultValue)
	{
		super(parent, style);
		this.init(defaultValue);
	}
	
	private void init(int value)
	{
		this.setLayout(new GridLayout());
		
		this.addControlListener(this);
		
		this.outputGroup = new Group(this, SWT.SHADOW_ETCHED_IN);
		this.outputGroup.setLayout(new GridLayout());
		this.outputGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.outputGroup.setText(Messages.getString("PrintDestinationGroup.Ausgabe_1")); //$NON-NLS-1$
		
		this.outputRadio = new Button[3];
		this.outputRadio[0] = new Button(this.outputGroup, SWT.RADIO);
		this.outputRadio[0].addListener(SWT.Selection, this);
		this.outputRadio[0].setText(Messages.getString("PrintDestinationGroup.Bildschirm_2")); //$NON-NLS-1$
		this.outputRadio[1] = new Button(this.outputGroup, SWT.RADIO);
		this.outputRadio[1].addListener(SWT.Selection, this);
		this.outputRadio[1].setText(Messages.getString("PrintDestinationGroup.Drucker_3")); //$NON-NLS-1$
		this.outputRadio[2] = new Button(this.outputGroup, SWT.RADIO);
		this.outputRadio[2].addListener(SWT.Selection, this);
		this.outputRadio[2].setText(Messages.getString("PrintDestinationGroup.Datei_4")); //$NON-NLS-1$
		
		String[] fileTypes = new String[]
		{
						Messages.getString("PrintDestinationGroup.Portable_Document_Format_(*.pdf)_5"), Messages.getString("PrintDestinationGroup.Excel-Arbeitsmappe_(*.xls)_6"), Messages.getString("PrintDestinationGroup.Webseite_(*.html)_7"), Messages.getString("PrintDestinationGroup.XML-Dokument_(*.xml)_8"), Messages.getString("PrintDestinationGroup.Durch_Kommas_getrennte_Werte_(*.csv)_9") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		this.fileTypeCombo = new Combo(this.outputGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.fileTypeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.fileTypeCombo.setItems(fileTypes);
		this.fileTypeCombo.select(0);
		this.fileTypeCombo.addListener(SWT.Selection, this);
		
		this.destination = 0;
		this.outputRadio[this.destination].setSelection(true);
	}
	
	public void handleEvent(Event e)
	{
		if (e.widget.equals(this.fileTypeCombo))
		{
			this.outputRadio[0].setSelection(false);
			this.outputRadio[1].setSelection(false);
			this.outputRadio[2].setSelection(true);
			this.destination = 2;
		}
		else
		{
			if (e.type == SWT.Selection)
			{
				if (e.widget.equals(this.outputRadio[0]))
				{
					this.destination = 0;
				}
				else if (e.widget.equals(this.outputRadio[1]))
				{
					this.destination = 1;
				}
				else if (e.widget.equals(this.outputRadio[2]))
				{
					this.destination = 2;
				}
			}
		}
	}
	
	public int getDestination()
	{
		return this.destination;
	}
	
	public int getFileType()
	{
		return this.fileTypeCombo.getSelectionIndex();
	}
	
	public void controlResized(ControlEvent e)
	{
		this.outputGroup.pack();
	}
	
	public String getFileName()
	{
		// 10057
		// FileDialog SAVE statt wie bis anhin OPEN
		// FileDialog fd = new FileDialog(getShell());
		FileDialog fd = new FileDialog(this.getShell(), SWT.SAVE);
		fd.setText(Messages.getString("PrintDestinationGroup.Speichern_unter__10")); //$NON-NLS-1$
		int type = this.getFileType();
		switch (type)
		{
			case PrintDestinationGroup.FILE_PDF:
				fd.setFilterExtensions(new String[]
				{ ".pdf" }); //$NON-NLS-1$
				break;
			case PrintDestinationGroup.FILE_EXCEL:
				fd.setFilterExtensions(new String[]
				{ ".xls" }); //$NON-NLS-1$
				break;
			case PrintDestinationGroup.FILE_HTML:
				fd.setFilterExtensions(new String[]
				{ ".html", ".htm", ".php" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				break;
			case PrintDestinationGroup.FILE_XML:
				fd.setFilterExtensions(new String[]
				{ ".xml" }); //$NON-NLS-1$
				break;
			case PrintDestinationGroup.FILE_CSV:
				fd.setFilterExtensions(new String[]
				{ ".csv" }); //$NON-NLS-1$
				break;
		}
		TabFolder tf = Main.getInstance().getTabFolder();
		TabItem item = tf.getItem(tf.getSelectionIndex());
		if (item.getControl() instanceof ITabFolderChild)
		{
			fd.setFileName(((ITabFolderChild) item.getControl()).getPrintFileName());
		}
		if (fd.getFileName() == null || fd.getFileName().equals("")) { //$NON-NLS-1$
			fd.setFileName(tf.getItem(tf.getSelectionIndex()).getText());
		}
		String fileName = fd.open();
		if (fileName != null)
		{
			boolean hasExtension = false;
			for (int i = 0; i < fd.getFilterExtensions().length; i++)
			{
				if (fileName.endsWith(fd.getFilterExtensions()[i]))
				{
					hasExtension = true;
				}
			}
			if (!hasExtension)
			{
				fileName = fileName.concat(fd.getFilterExtensions()[0]);
			}
		}
		return fileName;
	}
	
	public void controlMoved(ControlEvent e)
	{
	}
	
	private Group outputGroup;
	private Button[] outputRadio;
	private Combo fileTypeCombo;
	// private Button printButton;
	private int destination;
	
	public static final int SCREEN = 0;
	public static final int PRINTER = 1;
	public static final int FILE = 2;
	
	public static final int FILE_PDF = 0;
	public static final int FILE_EXCEL = 1;
	public static final int FILE_HTML = 2;
	public static final int FILE_XML = 3;
	public static final int FILE_CSV = 4;
	
}
