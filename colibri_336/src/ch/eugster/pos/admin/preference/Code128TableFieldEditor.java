/*
 * Created on 20.12.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.admin.preference;

import java.util.Hashtable;
import java.util.Properties;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Code128TableFieldEditor extends PropertiesTableEditor
{
	
	private Hashtable items = new Hashtable();
	
	/**
	 * Creates a table field editor.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public Code128TableFieldEditor(String name, String labelText, Composite parent, Properties properties)
	{
		super(name, labelText, parent, properties);
		super.createTable(this.columnNames);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.admin.preference.TableEditor#createTable(java.util.Properties
	 * )
	 */
	protected Properties createTable(Properties items)
	{
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#doLoad()
	 */
	protected void doLoad()
	{
		this.updateItem("day", "Tag");
		this.updateItem("month", "Monat");
		this.updateItem("year", "Jahr");
		this.updateItem("supplier", "Lieferant");
		this.updateItem("tax", "Steuer");
		this.updateItem("qualifier", "Qualifier");
		this.updateItem("price", "Preis");
		this.updateItem("net", "Netto");
		this.updateItem("group", "Warengruppe");
		this.updateItem("article", "Artikel");
		
		this.setColumnsWidth();
		
		Menu menu = new Menu(this.getTableControl());
		MenuItem changeStartValueMenu = new MenuItem(menu, SWT.PUSH);
		changeStartValueMenu.setText("Beginn-Wert ändern");
		changeStartValueMenu.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				TableItem[] items = Code128TableFieldEditor.this.getTableControl().getSelection();
				IntegerValidator validator = new IntegerValidator();
				InputDialog dialog = new InputDialog(Code128TableFieldEditor.this.getTableControl().getShell(),
								"Beginn-Wert ändern", "Geben Sie einen ganzzahligen Wert ein:", items[0].getText(1),
								validator);
				dialog.setBlockOnOpen(true);
				if (dialog.open() == 0) items[0].setText(1, dialog.getValue());
			}
			
			public void widgetDefaultSelected(SelectionEvent e)
			{
				this.widgetSelected(e);
			}
		});
		MenuItem changeEndValueMenu = new MenuItem(menu, SWT.PUSH);
		changeEndValueMenu.setText("End-Wert ändern");
		changeEndValueMenu.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				TableItem[] items = Code128TableFieldEditor.this.getTableControl().getSelection();
				IntegerValidator validator = new IntegerValidator();
				InputDialog dialog = new InputDialog(Code128TableFieldEditor.this.getTableControl().getShell(),
								"Beginn-Wert ändern", "Geben Sie einen ganzzahligen Wert ein:", items[0].getText(2),
								validator);
				dialog.setBlockOnOpen(true);
				if (dialog.open() == 0) items[0].setText(2, dialog.getValue());
			}
			
			public void widgetDefaultSelected(SelectionEvent e)
			{
				this.widgetSelected(e);
			}
		});
		this.getTableControl().setMenu(menu);
	}
	
	private void updateItem(String key, String name)
	{
		TableItem item = (TableItem) this.items.get(key);
		if (item == null) item = new TableItem(this.getTableControl(), 0);
		
		String[] text = new String[3];
		text[0] = name;
		text[1] = this.properties.getProperty(key + ".start");
		text[2] = this.properties.getProperty(key + ".end");
		
		item.setText(text);
		this.items.put(key, item);
	}
	
	private class IntegerValidator implements IInputValidator
	{
		public String isValid(String input)
		{
			try
			{
				Integer.parseInt(input);
				return null;
			}
			catch (NumberFormatException e)
			{
				return "Ungültige Eingabe";
			}
		}
	}
	
	protected void doStore()
	{
		TableItem[] items = this.getTableControl().getItems();
		for (int i = 0; i < items.length; i++)
		{
			if (items[i].getText(0).equals("Tag"))
			{
				this.properties.setProperty("day.start", items[i].getText(1));
				this.properties.setProperty("day.end", items[i].getText(2));
			}
			else if (items[i].getText(0).equals("Monat"))
			{
				this.properties.setProperty("month.start", items[i].getText(1));
				this.properties.setProperty("month.end", items[i].getText(2));
			}
			else if (items[i].getText(0).equals("Jahr"))
			{
				this.properties.setProperty("year.start", items[i].getText(1));
				this.properties.setProperty("year.end", items[i].getText(2));
			}
			else if (items[i].getText(0).equals("Lieferant"))
			{
				this.properties.setProperty("supplier.start", items[i].getText(1));
				this.properties.setProperty("supplier.end", items[i].getText(2));
			}
			else if (items[i].getText(0).equals("Steuer"))
			{
				this.properties.setProperty("tax.start", items[i].getText(1));
				this.properties.setProperty("tax.end", items[i].getText(2));
			}
			else if (items[i].getText(0).equals("Qualifier"))
			{
				this.properties.setProperty("qualifier.start", items[i].getText(1));
				this.properties.setProperty("qualifier.end", items[i].getText(2));
			}
			else if (items[i].getText(0).equals("Preis"))
			{
				this.properties.setProperty("price.start", items[i].getText(1));
				this.properties.setProperty("price.end", items[i].getText(2));
			}
			else if (items[i].getText(0).equals("Netto"))
			{
				this.properties.setProperty("net.start", items[i].getText(1));
				this.properties.setProperty("net.end", items[i].getText(2));
			}
			else if (items[i].getText(0).equals("Warengruppe"))
			{
				this.properties.setProperty("group.start", items[i].getText(1));
				this.properties.setProperty("group.end", items[i].getText(2));
			}
			else if (items[i].getText(0).equals("Artikel"))
			{
				this.properties.setProperty("article.start", items[i].getText(1));
				this.properties.setProperty("article.end", items[i].getText(2));
			}
		}
	}
	
	private void setColumnsWidth()
	{
		for (int i = 0; i < this.getTableControl().getColumnCount(); i++)
		{
			this.getTableControl().getColumn(i).pack();
		}
	}
	
	private String[] columnNames =
	{ "Spalte", "Beginn", "Ende" };
}
