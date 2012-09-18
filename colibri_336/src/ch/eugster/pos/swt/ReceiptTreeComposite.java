/*
 * Created on 26.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.swt;

import java.io.File;
import java.util.Properties;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import ch.eugster.pos.statistics.events.ISalespointSelectionListener;
import ch.eugster.pos.util.Path;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ReceiptTreeComposite extends Composite implements Listener, ISalespointSelectionListener
{
	
	/**
	 * @param parent
	 * @param style
	 * @param salespoints
	 */
	public ReceiptTreeComposite(Composite parent, int style, Properties properties)
	{
		super(parent, style);
		this.properties = properties;
		this.init();
	}
	
	private void init()
	{
		this.setLayout(new GridLayout());
		// addControlListener(this);
		
		File root = new File(Path.getInstance().rootDir + File.separator + this.properties.getProperty("root"));
		if (root.exists())
		{
			this.receiptTreeViewer = new TreeViewer(this, SWT.BORDER | SWT.MULTI);
			this.receiptTreeViewer.setContentProvider(new ReceiptTreeContentProvider(root));
			this.receiptTreeViewer.setLabelProvider(new ReceiptTreeLabelProvider());
			this.receiptTreeViewer.setInput(((IStructuredContentProvider) this.receiptTreeViewer.getContentProvider())
							.getElements(root));
			this.receiptTreeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
			this.receiptTreeViewer.getTree().addListener(SWT.Selection, this);
			this.receiptTreeViewer.getTree().addListener(SWT.MouseDoubleClick, this);
		}
		
	}
	
	// public void controlResized(ControlEvent e) {
	//		
	// }
	
	public void handleEvent(Event e)
	{
		if (e.type == SWT.MouseDoubleClick)
		{
			System.out.println("SWT.MouseDoubleClick");
			e.item = this.selectedItem;
			if (e.item instanceof TreeItem)
			{
				TreeItem item = (TreeItem) e.item;
				if (item.getData() instanceof File)
				{
					File file = (File) item.getData();
					if (file.isFile())
					{
						this.notifyListeners(e.type, e);
					}
				}
			}
		}
		else if (e.type == SWT.Selection)
		{
			System.out.println("SWT.Selection");
			if (e.item instanceof TreeItem)
			{
				this.selectedItem = e.item;
			}
		}
	}
	
	public TreeViewer getReceiptTreeViewer()
	{
		return this.receiptTreeViewer;
	}
	
	// public void controlMoved(ControlEvent e) {}
	
	private Properties properties;
	private TreeViewer receiptTreeViewer;
	private Widget selectedItem = null;
}
