/*
 * Created on 17.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.preference;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class LabelFieldEditor extends org.eclipse.jface.preference.FieldEditor
{
	
	private Label label;
	
	/**
	 * 
	 */
	public LabelFieldEditor()
	{
		super();
	}
	
	/**
	 * @param name
	 * @param labelText
	 * @param parent
	 */
	public LabelFieldEditor(String labelText, Composite parent)
	{
		super("", labelText, parent); //$NON-NLS-1$
	}
	
	// Adjusts the field editor to be displayed correctly
	// for the given number of columns.
	protected void adjustForNumColumns(int numColumns)
	{
		((GridData) this.label.getLayoutData()).horizontalSpan = numColumns;
	}
	
	// Fills the field editor's controls into the given parent.
	protected void doFillIntoGrid(Composite parent, int numColumns)
	{
		this.label = this.getLabelControl(parent);
		
		GridData gridData = new GridData();
		gridData.horizontalSpan = numColumns;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessVerticalSpace = false;
		
		this.label.setLayoutData(gridData);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#doLoad()
	 */
	protected void doLoad()
	{
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
	 */
	protected void doLoadDefault()
	{
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#doStore()
	 */
	protected void doStore()
	{
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#getNumberOfControls()
	 */
	public int getNumberOfControls()
	{
		return 1;
	}
	
	public void setBold(boolean bold)
	{
		this.getLabelControl().setFont(bold ? JFaceResources.getBannerFont() : JFaceResources.getDefaultFont());
	}
	
}
