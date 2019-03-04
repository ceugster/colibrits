/*
 * Created on 05.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.swt;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import ch.eugster.pos.Messages;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TextButton extends Composite implements Listener
{
	
	/**
	 * @param parent
	 * @param style
	 */
	public TextButton(Composite parent, int style, String path)
	{
		super(parent, style);
		this.setLayout(new GridLayout(3, false));
		
		Label label = new Label(this, SWT.NONE);
		label.setText(Messages.getString("TextButton.Exportverzeichnis_1")); //$NON-NLS-1$
		
		this.text = new Text(this, SWT.BORDER);
		this.text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.text.setText(path);
		
		this.button = new Button(this, SWT.PUSH);
		this.button.setText(Messages.getString("TextButton.Suchen..._2")); //$NON-NLS-1$
		this.button.addListener(SWT.Selection, this);
	}
	
	public void handleEvent(Event e)
	{
		if (e.type == SWT.Selection)
		{
			if (e.widget.equals(this.button))
			{
				DirectoryDialog dialog = new DirectoryDialog(this.getShell());
				dialog.setText(Messages.getString("TextButton.Zieverzeichnis_3")); //$NON-NLS-1$
				dialog
								.setMessage(Messages
												.getString("TextButton.W_u00E4hlen_Sie_das_Zielverzeichnis_f_u00FCr_die_Fibu-Transaktionsdatei_aus._4")); //$NON-NLS-1$
				dialog.setFilterPath(this.getDirectory());
				String path = dialog.open();
				if (path != null)
				{
					File file = new File(path);
					if (file.isDirectory())
					{
						this.text.setText(file.getAbsolutePath());
					}
				}
			}
		}
	}
	
	public void setDirectory(String path)
	{
		this.text.setText(path);
	}
	
	public String getDirectory()
	{
		return this.text.getText();
	}
	
	private Text text;
	private Button button;
}
