/*
 * Created on 05.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.user;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.PropertyChangeEvent;

import ch.eugster.pos.Messages;
import ch.eugster.pos.admin.gui.widget.BooleanFieldEditor;
import ch.eugster.pos.admin.gui.widget.FieldEditorPage;
import ch.eugster.pos.admin.gui.widget.IntegerComboFieldEditor;
import ch.eugster.pos.admin.gui.widget.LabelFieldEditor;
import ch.eugster.pos.admin.gui.widget.LongFieldEditor;
import ch.eugster.pos.admin.gui.widget.StringFieldEditor;
import ch.eugster.pos.admin.gui.widget.UniquePrimaryKeyValidator;
import ch.eugster.pos.db.User;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class UserFieldEditorPage extends FieldEditorPage
{
	
	/**
	 * @param style
	 */
	public UserFieldEditorPage(String name, int style, PersistentDBStore store)
	{
		super(name, style, store);
	}
	
	/**
	 * @param title
	 * @param style
	 */
	public UserFieldEditorPage(String name, String title, int style, PersistentDBStore store)
	{
		super(name, title, style, store);
	}
	
	/**
	 * @param title
	 * @param image
	 * @param style
	 */
	public UserFieldEditorPage(String name, String title, ImageDescriptor image, int style, PersistentDBStore store)
	{
		super(name, title, image, style, store);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.FieldEditorPage#createFieldEditors()
	 */
	protected void createFieldEditors()
	{
		if (!User.isIdFieldAutoincrement(User.class))
		{
			LongFieldEditor idEditor = new LongFieldEditor(UserFieldEditorPage.KEY_ID, Messages
							.getString("UserFieldEditorPage.Id*_1"), this.getFieldEditorParent()); //$NON-NLS-1$
			idEditor.setContentValidator(new UniquePrimaryKeyValidator(idEditor, User.class));
			this.addField(idEditor);
		}
		
		this.userNameEditor = new StringFieldEditor(
						UserFieldEditorPage.KEY_USERNAME,
						Messages.getString("UserFieldEditorPage.Benutzername_1"), -1, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		this.userNameEditor.setEmptyStringAllowed(false);
		this.userNameEditor.setCapitalizationOn(false);
		this.userNameEditor.setErrorMessage(Messages
						.getString("UserFieldEditorPage.Die_Angabe_des_Benutzernamens_ist_notwendig._2")); //$NON-NLS-1$
		this.userNameEditor.showErrorMessage();
		this.addField(this.userNameEditor);
		
		this.passwordEditor = new StringFieldEditor(
						UserFieldEditorPage.KEY_PASSWORD,
						Messages.getString("UserFieldEditorPage.Passwort_3"), -1, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.getFieldEditorParent()); //$NON-NLS-1$
		this.passwordEditor.setEmptyStringAllowed(true);
		this.passwordEditor.setCapitalizationOn(false);
		this.passwordEditor.setErrorMessage(""); //$NON-NLS-1$
		this.passwordEditor.showErrorMessage();
		this.addField(this.passwordEditor);
		
		this.posLoginEditor = new LongFieldEditor(UserFieldEditorPage.KEY_POS_LOGIN, Messages
						.getString("UserFieldEditorPage.Zugangscode_Kasse_5"), this.getFieldEditorParent()); //$NON-NLS-1$
		this.posLoginEditor.setTextLimit(10);
		this.addField(this.posLoginEditor);
		
		String[] text = new String[]
		{ User.USER_STATE_TEXT[User.USER_STATE_ADMINISTRATOR], User.USER_STATE_TEXT[User.USER_STATE_MANAGER],
						User.USER_STATE_TEXT[User.USER_STATE_EMPLOYEE] };
		this.statusEditor = new IntegerComboFieldEditor(
						UserFieldEditorPage.KEY_STATE,
						Messages.getString("UserFieldEditorPage.Status_5"), this.getFieldEditorParent(), text, User.USER_STATE_VALUE); //$NON-NLS-1$
		this.statusEditor.setContentValidator(new StatusValidator());
		this.statusEditor
						.setErrorMessage(Messages
										.getString("UserFieldEditorPage.Der_Status_des_Administrators_darf_nicht_ver_u00E4ndert_werden._6")); //$NON-NLS-1$
		this.addField(this.statusEditor);
		
		this.defaultUserEditor = new BooleanFieldEditor(UserFieldEditorPage.KEY_DEFAULT_USER,
						"Default Benutzer", this.getFieldEditorParent()); //$NON-NLS-1$
		this.defaultUserEditor.setEnabled(true, this.getFieldEditorParent());
		this.addField(this.defaultUserEditor);
		
		this.reverseReceiptsEditor = new BooleanFieldEditor(UserFieldEditorPage.KEY_REVERSE_RECEIPTS,
						"Darf Belege stornieren", this.getFieldEditorParent()); //$NON-NLS-1$
		this.reverseReceiptsEditor.setEnabled(true, this.getFieldEditorParent());
		this.addField(this.reverseReceiptsEditor);
		
		LabelFieldEditor comment = new LabelFieldEditor(Messages
						.getString("UserFieldEditorPage.*Eindeutiger_Wert_zwingend._7"), this.getFieldEditorParent()); //$NON-NLS-1$
		this.addField(comment);
	}
	
	public void selectionChanged(Object object)
	{
		super.selectionChanged(object);
		if (object instanceof User)
		{
			this.statusEditor.getCombo().setEnabled(!((User) object).getId().equals(new Long(1l)));
			this.defaultUserEditor.setEnabled(!((User) object).defaultUser.equals(new Boolean(true)), this
							.getFieldEditorParent());
		}
	}
	
	public boolean isInstance(Object element)
	{
		return element instanceof User;
	}
	
	public String getElementName()
	{
		User user = (User) this.getStore().getElement();
		return user.username;
	}
	
	/**
	 * The field editor preference page implementation of this
	 * <code>IPreferencePage</code> (and <code>IPropertyChangeListener</code>)
	 * method intercepts <code>IS_VALID</code> events but passes other events on
	 * to its superclass.
	 */
	public void propertyChange(PropertyChangeEvent event)
	{
		this.defaultUserEditor.setEnabled(!((User) this.getStore().getElement()).defaultUser.booleanValue(), this
						.getFieldEditorParent());
		super.propertyChange(event);
	}
	
	private StringFieldEditor userNameEditor;
	private StringFieldEditor passwordEditor;
	private LongFieldEditor posLoginEditor;
	private IntegerComboFieldEditor statusEditor;
	private BooleanFieldEditor defaultUserEditor;
	private BooleanFieldEditor reverseReceiptsEditor;
	
	public static final String KEY_ID = "id"; //$NON-NLS-1$
	public static final String KEY_USERNAME = "username"; //$NON-NLS-1$
	public static final String KEY_PASSWORD = "password"; //$NON-NLS-1$
	public static final String KEY_POS_LOGIN = "posLogin"; //$NON-NLS-1$
	public static final String KEY_STATE = "state"; //$NON-NLS-1$
	public static final String KEY_DEFAULT_USER = "defaultUser"; //$NON-NLS-1$
	public static final String KEY_REVERSE_RECEIPTS = "reverseReceipts"; //$NON-NLS-1$
}
