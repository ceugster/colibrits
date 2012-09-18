/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package ch.eugster.pos.swt;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.eugster.pos.Constants;
import ch.eugster.pos.Messages;
import ch.eugster.pos.db.User;

/**
 * A simple input dialog for soliciting an input string from the user.
 * <p>
 * This concete dialog class can be instantiated as is, or further subclassed as
 * required.
 * </p>
 */
public class DatabaseAndUserVerificationDialog extends Dialog
{
	
	/**
	 * The title of the dialog.
	 */
	private String title;
	
	/**
	 * The message to display, or <code>null</code> if none.
	 */
	private String message;
	
	/**
	 * The input value; the empty string by default.
	 */
	private String userName = "";//$NON-NLS-1$
	
	/**
	 * The input value; the empty string by default.
	 */
	private String password = "";//$NON-NLS-1$
	
	/**
	 * The input validator, or <code>null</code> if none.
	 */
	private IInputValidator validator;
	
	/**
	 * Ok button widget.
	 */
	private Button okButton;
	
	/**
	 * Input text widget.
	 */
	private Text userNameField;
	
	/**
	 * Input text widget.
	 */
	private Text passwordField;
	
	/**
	 * Error message label widget.
	 */
	private Label errorMessageLabel;
	
	/**
	 * Creates an input dialog with OK and Cancel buttons. Note that the dialog
	 * will have no visual representation (no widgets) until it is told to open.
	 * <p>
	 * Note that the <code>open</code> method blocks for input dialogs.
	 * </p>
	 * 
	 * @param parentShell
	 *            the parent shell
	 * @param dialogTitle
	 *            the dialog title, or <code>null</code> if none
	 * @param dialogMessage
	 *            the dialog message, or <code>null</code> if none
	 * @param initialValue
	 *            the initial input value, or <code>null</code> if none
	 *            (equivalent to the empty string)
	 * @param validator
	 *            an input validator, or <code>null</code> if none
	 */
	public DatabaseAndUserVerificationDialog(Shell parentShell, String dialogTitle, String dialogMessage,
					String initialValue, IInputValidator validator)
	{
		super(parentShell);
		this.title = dialogTitle;
		this.message = dialogMessage;
		if (initialValue == null)
			this.userName = "";//$NON-NLS-1$
		else
			this.userName = initialValue;
		
		this.password = ""; //$NON-NLS-1$
		
		this.validator = validator;
	}
	
	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
	protected void buttonPressed(int buttonId)
	{
		if (buttonId == IDialogConstants.OK_ID)
		{
			this.userName = this.userNameField.getText();
			this.password = this.passwordField.getText();
			
			if (this.userName == null || this.userName.equals("")) { //0.8.0-rc1 001 //$NON-NLS-1$
				this
								.getErrorMessageLabel()
								.setText(
												Messages
																.getString("UserVerificationDialog.Sie_muessen_einen_Benutzernamen_eingeben._3")); //$NON-NLS-1$
				this.passwordField.setText(""); //$NON-NLS-1$
				this.userNameField.setFocus();
				this.password = null;
				return;
			}
			
			User user = User.selectByUsername(this.userName, false);
			if (user.username.equals(this.userName) && this.password.equals(user.password))
			{
				User.setCurrentUser(user);
				
				// MainWindow.getInstance();
			}
			else
			{
				this.userNameField.setFocus();
				this.userNameField.selectAll();
				this.passwordField.setText(""); //$NON-NLS-1$
				this.passwordField.setFocus();
				this.password = ""; //$NON-NLS-1$
				this.getErrorMessageLabel().setText(
								Messages.getString("UserVerificationDialog.Benutzername_oder_Passwort_ungueltig._4")); //$NON-NLS-1$
				return;
			}
		}
		else
		{
			this.userName = null;
			this.password = null;
		}
		super.buttonPressed(buttonId);
	}
	
	/*
	 * (non-Javadoc) Method declared in Window.
	 */
	protected void configureShell(Shell shell)
	{
		super.configureShell(shell);
		if (this.title != null) shell.setText(this.title);
	}
	
	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
	protected void createButtonsForButtonBar(Composite parent)
	{
		// create OK and Cancel buttons by default
		this.okButton = this.createButton(parent, IDialogConstants.OK_ID, Constants.OK_LABEL, true);
		this.createButton(parent, IDialogConstants.CANCEL_ID, Constants.CANCEL_LABEL, false);
		
		// do this here because setting the text will set enablement on the ok
		// button
		this.userNameField.setFocus();
		if (this.userName != null)
		{
			this.userNameField.setText(this.userName);
			this.userNameField.selectAll();
		}
	}
	
	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
	protected Control createDialogArea(Composite parent)
	{
		// create composite
		Composite composite = (Composite) super.createDialogArea(parent);
		
		// create message
		if (this.message != null)
		{
			Label label = new Label(composite, SWT.WRAP);
			label.setText(this.message);
			GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL
							| GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
			data.widthHint = this.convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
			label.setLayoutData(data);
			label.setFont(parent.getFont());
		}
		
		Label userNameLabel = new Label(composite, SWT.FLAT);
		userNameLabel.setText(Messages.getString("UserVerificationDialog.Benutzername_5")); //$NON-NLS-1$
		userNameLabel.setLayoutData(new GridData(GridData.BEGINNING));
		this.userNameField = new Text(composite, SWT.SINGLE | SWT.BORDER);
		this.userNameField.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		this.userNameField.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				DatabaseAndUserVerificationDialog.this.validateInput(e);
			}
		});
		this.userNameField.setFont(parent.getFont());
		
		Label passwordLabel = new Label(composite, SWT.FLAT);
		passwordLabel.setText(Messages.getString("UserVerificationDialog.Passwort_6")); //$NON-NLS-1$
		passwordLabel.setLayoutData(new GridData(GridData.BEGINNING));
		this.passwordField = new Text(composite, SWT.SINGLE | SWT.BORDER);
		this.passwordField.setEchoChar('*');
		this.passwordField.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		this.passwordField.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				DatabaseAndUserVerificationDialog.this.validateInput(e);
			}
		});
		this.passwordField.setFont(parent.getFont());
		
		this.errorMessageLabel = new Label(composite, SWT.NONE);
		this.errorMessageLabel.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		this.errorMessageLabel.setFont(parent.getFont());
		
		return composite;
	}
	
	/**
	 * Returns the error message label.
	 * 
	 * @return the error message label
	 */
	protected Label getErrorMessageLabel()
	{
		return this.errorMessageLabel;
	}
	
	/**
	 * Returns the ok button.
	 * 
	 * @return the ok button
	 */
	protected Button getOkButton()
	{
		return this.okButton;
	}
	
	/**
	 * Returns the text area.
	 * 
	 * @return the text area
	 */
	protected Text getUserNameField()
	{
		return this.userNameField;
	}
	
	/**
	 * Returns the text area.
	 * 
	 * @return the text area
	 */
	protected Text getPasswordField()
	{
		return this.passwordField;
	}
	
	/**
	 * Returns the validator.
	 * 
	 * @return the validator
	 */
	protected IInputValidator getValidator()
	{
		return this.validator;
	}
	
	/**
	 * Returns the string typed into this input dialog.
	 * 
	 * @return the input string
	 */
	public String getUserName()
	{
		return this.userName;
	}
	
	/**
	 * Returns the string typed into this input dialog.
	 * 
	 * @return the input string
	 */
	public String getPassword()
	{
		return this.password;
	}
	
	/**
	 * Validates the input.
	 * <p>
	 * The default implementation of this framework method delegates the request
	 * to the supplied input validator object; if it finds the input invalid,
	 * the error message is displayed in the dialog's message line. This hook
	 * method is called whenever the text changes in the input field.
	 * </p>
	 */
	protected void validateInput(ModifyEvent e)
	{
	}
}
