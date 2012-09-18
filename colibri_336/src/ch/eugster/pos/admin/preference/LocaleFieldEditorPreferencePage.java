/*
 * Created on 17.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.preference;

import java.util.ArrayList;
import java.util.Locale;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;

import ch.eugster.pos.Messages;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class LocaleFieldEditorPreferencePage extends FieldEditorPreferencePage implements SelectionListener
{
	
	/**
	 * @param style
	 */
	public LocaleFieldEditorPreferencePage(int style)
	{
		super(style);
	}
	
	/**
	 * @param title
	 * @param style
	 */
	public LocaleFieldEditorPreferencePage(String title, int style)
	{
		super(title, style);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public LocaleFieldEditorPreferencePage(String title, ImageDescriptor image, int style)
	{
		super(title, image, style);
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
		this.locales = Locale.getAvailableLocales();
		ArrayList l = new ArrayList();
		for (int i = 0; i < this.locales.length; i++)
		{
			if (this.locales[i].getCountry().equals("")) { //$NON-NLS-1$
				l.add(this.locales[i].getLanguage());
			}
		}
		String[] languages = (String[]) l.toArray(new String[0]);
		this.languageEditor = new StringComboFieldEditor(
						"locale.language", Messages.getString("LocaleFieldEditorPreferencePage.Sprachcode_3"), this.getFieldEditorParent(), languages); //$NON-NLS-1$ //$NON-NLS-2$
		this.languageEditor.getCombo().addSelectionListener(this);
		this.addField(this.languageEditor);
		
		String[] countries = this.selectCountries(this.getPreferenceStore().getDefaultString("locale.language")); //$NON-NLS-1$
		this.countryEditor = new StringComboFieldEditor(
						"locale.country", Messages.getString("LocaleFieldEditorPreferencePage.L_u00E4ndercode_6"), this.getFieldEditorParent(), countries); //$NON-NLS-1$ //$NON-NLS-2$
		this.addField(this.countryEditor);
	}
	
	public void widgetSelected(SelectionEvent e)
	{
		if (e.getSource() == this.languageEditor.getCombo())
		{
			Combo combo = (Combo) e.getSource();
			String value = combo.getItem(combo.getSelectionIndex());
			this.countryEditor.setItems(this.selectCountries(value));
		}
	}
	
	private String[] selectCountries(String language)
	{
		String[] empty = new String[0];
		ArrayList l = new ArrayList();
		for (int i = 0; i < this.locales.length; i++)
		{
			if (this.locales[i].getLanguage().equals(language)
							&& !this.locales[i].getCountry().equals("") && this.locales[i].getVariant().equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
				l.add(this.locales[i].getCountry());
			}
		}
		return l.size() > 0 ? (String[]) l.toArray(empty) : empty;
	}
	
	public void widgetDefaultSelected(SelectionEvent e)
	{
		
	}
	
	private StringComboFieldEditor languageEditor;
	private StringComboFieldEditor countryEditor;
	private Locale[] locales;
}
