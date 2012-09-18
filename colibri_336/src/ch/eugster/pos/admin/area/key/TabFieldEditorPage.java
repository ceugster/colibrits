/*
 * Created on 09.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.key;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.gui.widget.BooleanFieldEditor;
import ch.eugster.pos.admin.gui.widget.FieldEditorPage;
import ch.eugster.pos.admin.gui.widget.IntegerFieldEditor;
import ch.eugster.pos.admin.gui.widget.StringFieldEditor;
import ch.eugster.pos.db.Block;
import ch.eugster.pos.db.KeyGroup;
import ch.eugster.pos.db.Tab;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class TabFieldEditorPage extends FieldEditorPage
{
	
	/**
	 * @param name
	 * @param style
	 * @param store
	 */
	public TabFieldEditorPage(String name, int style, PersistentDBStore store)
	{
		super(name, style, store);
	}
	
	/**
	 * @param name
	 * @param title
	 * @param style
	 * @param store
	 */
	public TabFieldEditorPage(String name, String title, int style, PersistentDBStore store)
	{
		super(name, title, style, store);
	}
	
	/**
	 * @param name
	 * @param title
	 * @param image
	 * @param style
	 * @param store
	 */
	public TabFieldEditorPage(String name, String title, ImageDescriptor image, int style, PersistentDBStore store)
	{
		super(name, title, image, style, store);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.gui.widget.FieldEditorPage#createFieldEditors()
	 */
	protected void createFieldEditors()
	{
		this.titleEditor = new StringFieldEditor(
						TabFieldEditorPage.KEY_TITLE,
						Messages.getString("TabFieldEditorPage.Titel_1"), -1, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		this.titleEditor.setEmptyStringAllowed(false);
		this.titleEditor.setCapitalizationOn(false);
		this.titleEditor.setErrorMessage(Messages
						.getString("TabFieldEditorPage.Die_Angabe_des_Titels_ist_notwendig._2")); //$NON-NLS-1$
		this.titleEditor.showErrorMessage();
		this.addField(this.titleEditor);
		
		IntegerFieldEditor positionEditor = new IntegerFieldEditor(TabFieldEditorPage.KEY_ORDER,
						"Position", this.getFieldEditorParent(), 3); //$NON-NLS-1$
		positionEditor.setValidRange(0, 50);
		this.addField(positionEditor);
		
		BooleanFieldEditor isPositionDefaultEditor = new BooleanFieldEditor(TabFieldEditorPage.KEY_POSITION_DEFAULT,
						Messages.getString("TabFieldEditorPage.Default_3"), this.getFieldEditorParent()); //$NON-NLS-1$
		this.addField(isPositionDefaultEditor);
		
		BooleanFieldEditor isPaymentDefaultEditor = new BooleanFieldEditor(TabFieldEditorPage.KEY_PAYMENT_DEFAULT,
						Messages.getString("TabFieldEditorPage.Default_4"), this.getFieldEditorParent()); //$NON-NLS-1$
		this.addField(isPaymentDefaultEditor);
		
		this.rowEditor = new IntegerFieldEditor(TabFieldEditorPage.KEY_ROWS, Messages
						.getString("TabFieldEditorPage.Anzahl_Zeilen_4"), this.getFieldEditorParent(), 3); //$NON-NLS-1$
		this.rowEditor.setErrorMessage(Messages
						.getString("TabFieldEditorPage.Die_Angabe_der_Zeilenanzahl_ist_notwendig._5")); //$NON-NLS-1$
		this.rowEditor.setValidRange(1, 100);
		this.addField(this.rowEditor);
		
		this.columnEditor = new IntegerFieldEditor(TabFieldEditorPage.KEY_COLUMNS, Messages
						.getString("TabFieldEditorPage.Anzahl_Spalten_6"), this.getFieldEditorParent(), 3); //$NON-NLS-1$
		this.columnEditor.setErrorMessage(Messages
						.getString("TabFieldEditorPage.Die_Angabe_der_Spaltenanzahl_ist_notwendig._7")); //$NON-NLS-1$
		this.columnEditor.setValidRange(1, 100);
		this.addField(this.columnEditor);
		
		this.layoutEditor = new ButtonLayoutFieldEditor(ButtonLayoutFieldEditor.KEY_KEYS, Messages
						.getString("TabFieldEditorPage.Stil_der_Schriftart_8"), this.getFieldEditorParent()); //$NON-NLS-1$
		this.layoutEditor.getTreeViewer().setContentProvider(new ButtonTreeContentProvider());
		this.layoutEditor.getTreeViewer().setLabelProvider(new ButtonTreeLabelProvider());
		this.layoutEditor.getTreeViewer().setInput(
						((ITreeContentProvider) this.layoutEditor.getTreeViewer().getContentProvider())
										.getElements(null));
		this.rowEditor.addPropertyChangeListener(this.layoutEditor);
		this.columnEditor.addPropertyChangeListener(this.layoutEditor);
		this.addField(this.layoutEditor);
	}
	
	public ButtonLayoutFieldEditor getButtonLayoutFieldEditor()
	{
		return this.layoutEditor;
	}
	
	public void selectionChanged(Object element)
	{
		super.selectionChanged(element);
		TreeViewer tree = this.layoutEditor.getTreeViewer();
		Object[] items = (Object[]) tree.getInput();
		String text = ""; //$NON-NLS-1$
		if (element instanceof Tab)
		{
			Tab tab = (Tab) element;
			tab = Tab.selectById(tab.getId());
			text = ((Tab) element).getBlock().name;
		}
		else if (element instanceof Block)
		{
			text = ((Block) element).name;
		}
		this.layoutEditor.getTreeViewer().collapseAll();
		for (int i = 0; i < items.length; i++)
		{
			if (items[i] instanceof KeyGroup)
			{
				if (text.equals(((KeyGroup) items[i]).name))
				{
					Object[] o = new Object[]
					{ items[i] };
					this.layoutEditor.getTreeViewer().setExpandedElements(o);
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.gui.widget.Page#isInstance(java.lang.Object)
	 */
	public boolean isInstance(Object object)
	{
		return object instanceof Tab;
	}
	
	public String getElementName()
	{
		Tab tab = (Tab) this.getStore().getElement();
		return tab.title;
	}
	
	public int[] getWeights()
	{
		return this.layoutEditor.getWeights();
	}
	
	private StringFieldEditor titleEditor;
	private IntegerFieldEditor rowEditor;
	private IntegerFieldEditor columnEditor;
	private ButtonLayoutFieldEditor layoutEditor;
	
	public static final String KEY_TITLE = "title"; //$NON-NLS-1$
	public static final String KEY_ORDER = "order"; //$NON-NLS-1$
	public static final String KEY_ROWS = "rowCount"; //$NON-NLS-1$
	public static final String KEY_COLUMNS = "columnCount"; //$NON-NLS-1$
	public static final String KEY_POSITION_DEFAULT = "defaultTabPosition"; //$NON-NLS-1$
	public static final String KEY_PAYMENT_DEFAULT = "defaultTabPayment"; //$NON-NLS-1$
}
