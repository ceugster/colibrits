/*
 * Created on 17.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.preference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.PropertyChangeEvent;

import ch.eugster.pos.Messages;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class Code128DataFieldEditorPreferencePage extends org.eclipse.jface.preference.FieldEditorPreferencePage
{
	
	/**
	 * @param style
	 * @param file
	 */
	public Code128DataFieldEditorPreferencePage(int style, File file)
	{
		super(style);
		this.file = file;
	}
	
	/**
	 * @param title
	 * @param style
	 * @param file
	 */
	public Code128DataFieldEditorPreferencePage(String title, int style, File file)
	{
		super(title, style);
		this.file = file;
		
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public Code128DataFieldEditorPreferencePage(String title, ImageDescriptor image, int style, File file)
	{
		super(title, image, style);
		this.file = file;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors
	 * ()
	 */
	protected void createFieldEditors()
	{
		this.fields = new ArrayList();
		// dirty = false;
		this.loadProperties(this.file);
		this.setTitle(this.properties.getProperty("name"));
		
		this.useEditor = new PropertyBooleanFieldEditor(
						"code128.use", Messages.getString("Code128FieldEditorPreferencePage.Code128-Modus_aktiviert_2"), this.getFieldEditorParent(), this.properties, "use"); //$NON-NLS-1$ //$NON-NLS-2$
		this.useEditor.setBooleanValue(new Boolean(this.properties.getProperty("use")).booleanValue());
		this.addField(this.useEditor);
		this.fields.add(this.useEditor);
		
		this.galileoEditor = new PropertyBooleanFieldEditor(
						"code128.search-galileo", Messages.getString("Code128FieldEditorPreferencePage.Titeldaten_aus_Galileo_importieren_6"), this.getFieldEditorParent(), this.properties, "galileo"); //$NON-NLS-1$ //$NON-NLS-2$
		this.galileoEditor.setBooleanValue(new Boolean(this.properties.getProperty("galileo")).booleanValue());
		this.addField(this.galileoEditor);
		this.fields.add(this.galileoEditor);
		
		this.code128Editor = new Code128TableFieldEditor("", "", this.getFieldEditorParent(), this.properties);
		this.addField(this.code128Editor);
		this.fields.add(this.code128Editor);
	}
	
	private void loadProperties(File file)
	{
		try
		{
			FileInputStream fis = new FileInputStream(file);
			this.properties = new Properties();
			this.properties.load(fis);
			try
			{
				Integer.parseInt(this.properties.getProperty("day.start"));
				Integer.parseInt(this.properties.getProperty("day.end"));
				Integer.parseInt(this.properties.getProperty("month.start"));
				Integer.parseInt(this.properties.getProperty("month.end"));
				Integer.parseInt(this.properties.getProperty("year.start"));
				Integer.parseInt(this.properties.getProperty("year.end"));
				Integer.parseInt(this.properties.getProperty("supplier.start"));
				Integer.parseInt(this.properties.getProperty("supplier.end"));
				Integer.parseInt(this.properties.getProperty("tax.start"));
				Integer.parseInt(this.properties.getProperty("tax.end"));
				Integer.parseInt(this.properties.getProperty("qualifier.start"));
				Integer.parseInt(this.properties.getProperty("qualifier.end"));
				Integer.parseInt(this.properties.getProperty("price.start"));
				Integer.parseInt(this.properties.getProperty("price.end"));
				Integer.parseInt(this.properties.getProperty("net.start"));
				Integer.parseInt(this.properties.getProperty("net.end"));
				Integer.parseInt(this.properties.getProperty("group.start"));
				Integer.parseInt(this.properties.getProperty("group.end"));
				Integer.parseInt(this.properties.getProperty("article.start"));
				Integer.parseInt(this.properties.getProperty("article.end"));
			}
			catch (NumberFormatException e)
			{
			}
		}
		catch (IOException e)
		{
		}
	}
	
	public void propertyChange(PropertyChangeEvent e)
	{
		if (e.getSource() instanceof BooleanFieldEditor)
		{
			BooleanFieldEditor editor = (BooleanFieldEditor) e.getSource();
			if (editor.getPreferenceName().equals("code128.use"))
			{
				// dirty = true;
				this.properties.put("use", new Boolean(editor.getBooleanValue()).toString());
			}
			else if (editor.getPreferenceName().equals("code128.search-galileo"))
			{
				this.properties.put("galileo", new Boolean(editor.getBooleanValue()).toString());
				// dirty = true;
			}
		}
	}
	
	public void performApply()
	{
		this.useEditor.doStore();
		this.galileoEditor.doStore();
		this.code128Editor.doStore();
		try
		{
			FileOutputStream fos = new FileOutputStream(this.file);
			this.properties.store(fos, this.file.getAbsolutePath());
		}
		catch (FileNotFoundException e)
		{
		}
		catch (IOException e)
		{
		}
	}
	
	public void performDefaults()
	{
		this.loadProperties(this.file);
		this.useEditor.doLoadDefault();
		this.galileoEditor.doLoadDefault();
		this.code128Editor.doLoadDefault();
	}
	
	public boolean performOk()
	{
		if (this.fields != null)
		{
			Iterator i = this.fields.iterator();
			while (i.hasNext())
			{
				FieldEditor pe = (FieldEditor) i.next();
				pe.store();
			}
			
			try
			{
				FileOutputStream fos = new FileOutputStream(this.file);
				this.properties.store(fos, this.file.getAbsolutePath());
			}
			catch (FileNotFoundException e)
			{
				return false;
			}
			catch (IOException e)
			{
				return false;
			}
		}
		return true;
	}
	
	private File file;
	// private boolean dirty;
	private Properties properties;
	private PropertyBooleanFieldEditor useEditor;
	private PropertyBooleanFieldEditor galileoEditor;
	private Code128TableFieldEditor code128Editor;
	private ArrayList fields;
}
