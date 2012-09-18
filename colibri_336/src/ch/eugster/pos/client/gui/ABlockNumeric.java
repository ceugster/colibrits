/*
 * Created on 11.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.BorderUIResource;

import org.jdom.Element;

import ch.eugster.pos.Messages;
import ch.eugster.pos.client.model.PaymentModel;
import ch.eugster.pos.client.model.PositionModel;
import ch.eugster.pos.client.model.ReceiptChildModel;
import ch.eugster.pos.db.FixKey;
import ch.eugster.pos.db.Table;
import ch.eugster.pos.events.Action;
import ch.eugster.pos.events.ClearAction;
import ch.eugster.pos.events.DeleteAction;
import ch.eugster.pos.events.DigitAction;
import ch.eugster.pos.events.EnterAction;
import ch.eugster.pos.events.MessageDialog;
import ch.eugster.pos.events.ModeChangeEvent;
import ch.eugster.pos.events.ModeChangeListener;
import ch.eugster.pos.events.ReceiptChildChangeEvent;
import ch.eugster.pos.events.ReceiptChildChangeListener;
import ch.eugster.pos.util.Config;
import ch.eugster.pos.util.NumberUtility;

/**
 * @author Administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ABlockNumeric extends ABlock implements ActionListener, ReceiptChildChangeListener
{
	private static final long serialVersionUID = 0l;
	
	public ABlockNumeric(UserPanel context)
	{
		super(new BorderLayout(), context);
		this.init();
	}
	
	private void init()
	{
		FixKey[] keys = FixKey.getByBlock(this.getClass().getName());
		
		int rows = 4;
		int cols = 4;
		
		FixKey[][] fixKeys = new FixKey[rows][cols];
		for (int i = 0; i < keys.length; i++)
		{
			if (keys[i].row < rows && keys[i].column < cols)
			{
				fixKeys[keys[i].row][keys[i].column] = keys[i];
			}
		}
		
		Element disp = Config.getInstance().getDisplay();
		Element font = disp.getChild("font"); //$NON-NLS-1$
		float fontSize = Float.parseFloat(font.getAttributeValue("size")); //$NON-NLS-1$
		int fontStyle = Integer.parseInt(font.getAttributeValue("style")); //$NON-NLS-1$
		
		Element color = disp.getChild("fgcolor"); //$NON-NLS-1$
		int red = Integer.parseInt(color.getAttributeValue("red")); //$NON-NLS-1$
		int green = Integer.parseInt(color.getAttributeValue("green")); //$NON-NLS-1$
		int blue = Integer.parseInt(color.getAttributeValue("blue")); //$NON-NLS-1$
		Color fg = new Color(red, green, blue);
		
		color = disp.getChild("bgcolor"); //$NON-NLS-1$
		red = Integer.parseInt(color.getAttributeValue("red")); //$NON-NLS-1$
		green = Integer.parseInt(color.getAttributeValue("green")); //$NON-NLS-1$
		blue = Integer.parseInt(color.getAttributeValue("blue")); //$NON-NLS-1$
		Color bg = new Color(red, green, blue);
		
		JPanel northPanel = new JPanel(new BorderLayout());
		this.display = new JLabel();
		this.display.setBorder(BorderUIResource.getEtchedBorderUIResource());
		this.display.setFont(this.display.getFont().deriveFont(fontStyle, fontSize));
		this.display.setForeground(fg);
		this.display.setBackground(bg);
		this.display.setOpaque(true);
		northPanel.add(this.display, BorderLayout.CENTER);
		// northPanel.add(new ClockBlock(), BorderLayout.EAST);
		
		JPanel centerPanel = new JPanel(new GridLayout(2, 1)); // Container for
		// panels
		JPanel centerNorthPanel = new JPanel(new GridLayout(2, 4)); // Container
		// fuer die
		// ersten 8
		// Buttons
		JPanel centerSouthPanel = new JPanel(new GridLayout(1, 2));
		JPanel centerSouthWestPanel = new JPanel(new GridLayout(2, 2));
		JPanel centerSouthEastPanel = new JPanel(new GridLayout(1, 2));
		JPanel centerSouthEastWestPanel = new JPanel(new GridLayout(2, 1));
		JPanel centerSouthEastEastPanel = new JPanel(new GridLayout(1, 1));
		
		centerSouthEastPanel.add(centerSouthEastWestPanel);
		centerSouthEastPanel.add(centerSouthEastEastPanel);
		centerSouthPanel.add(centerSouthWestPanel);
		centerSouthPanel.add(centerSouthEastPanel);
		centerPanel.add(centerNorthPanel);
		centerPanel.add(centerSouthPanel);
		
		centerNorthPanel.add(this.makeButton(fixKeys[0][0]));
		centerNorthPanel.add(this.makeButton(fixKeys[0][1]));
		centerNorthPanel.add(this.makeButton(fixKeys[0][2]));
		centerNorthPanel.add(this.makeButton(fixKeys[0][3]));
		
		centerNorthPanel.add(this.makeButton(fixKeys[1][0]));
		centerNorthPanel.add(this.makeButton(fixKeys[1][1]));
		centerNorthPanel.add(this.makeButton(fixKeys[1][2]));
		centerNorthPanel.add(this.makeButton(fixKeys[1][3]));
		
		centerSouthWestPanel.add(this.makeButton(fixKeys[2][0]));
		centerSouthWestPanel.add(this.makeButton(fixKeys[2][1]));
		centerSouthEastWestPanel.add(this.makeButton(fixKeys[2][2]));
		centerSouthEastEastPanel.add(this.makeButton(fixKeys[2][3]));
		
		centerSouthWestPanel.add(this.makeButton(fixKeys[3][0]));
		centerSouthWestPanel.add(this.makeButton(fixKeys[3][1]));
		centerSouthEastWestPanel.add(this.makeButton(fixKeys[3][2]));
		
		this.add(northPanel, BorderLayout.NORTH);
		this.add(centerPanel, BorderLayout.CENTER);
		
		this.removeKeyListener(this.context);
		this.context.getReceiptModel().getPositionModel().addReceiptChildChangeListener(this);
		this.context.getReceiptModel().getPaymentModel().addReceiptChildChangeListener(this);
	}
	
	private Component makeButton(FixKey key)
	{
		if (key == null)
		{
			return new JPanel();
		}
		else
		{
			PosButton button = key.createButton(this.context);
			Table.addDatabaseErrorListener(button);
			if (key.className.equals(EnterAction.class.getName()))
			{
				this.enter = button;
			}
			else if (key.className.equals(ClearAction.class.getName()))
			{
				this.clear = button;
			}
			else if (key.className.equals(DeleteAction.class.getName()))
			{
				this.clearAll = button;
			}
			button.addActionListener(this);
			return button;
		}
	}
	
	public void keyTyped(KeyEvent e)
	{
	}
	
	public void keyPressed(KeyEvent e)
	{
		if ("0123456789.".indexOf(String.valueOf(e.getKeyChar())) > -1) { //$NON-NLS-1$
			this.calculateValue(String.valueOf(e.getKeyChar()), true);
			return;
		}
		else if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			if (this.display.getText().startsWith(
							Messages.getString("PositionModel.Warengruppe_nicht_bekannt._Warengruppe___1")))
			{
				Toolkit.getDefaultToolkit().beep();
				this.initValue();
				return;
			}
			else
			{
				this.enter.doClick();
			}
		}
		else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
		{
			this.clear.doClick();
		}
		else if (e.getKeyCode() == KeyEvent.VK_DELETE)
		{
			this.clearAll.doClick();
		}
		this
						.fireModeChangeEvent(new ModeChangeEvent(ModeChangeEvent.KEY_NUMERIC_BLOCK, this
										.valueIsInteger() ? ModeChangeEvent.VALUE_IS_INTEGER
										: ModeChangeEvent.VALUE_IS_NOT_INTEGER));
		
		this.initValue();
	}
	
	public void keyReleased(KeyEvent e)
	{
	}
	
	public void actionPerformed(ActionEvent e)
	{
		Action action = (Action) ((PosButton) e.getSource()).getAction();
		if (action instanceof ClearAction)
		{
			this.initValue();
			this.fireModeChangeEvent(new ModeChangeEvent(ModeChangeEvent.KEY_NUMERIC_BLOCK,
							this.valueIsInteger() ? ModeChangeEvent.VALUE_IS_INTEGER
											: ModeChangeEvent.VALUE_IS_NOT_INTEGER));
			
			if (this.context.getMode().equals(UserPanel.CONTEXT_MODE_SETTLEMENT))
			{
				if (MessageDialog.showSimpleDialog(Frame.getMainFrame(), "Kassensturzeingaben löschen",
								"Wollen Sie alle Kassensturzeingaben löschen?", MessageDialog.TYPE_QUESTION) == MessageDialog.BUTTON_YES)
				{
					this.firePropertyChange("clearAll", null, null);
				}
			}
		}
		else if (action instanceof DigitAction)
		{
			this.calculateValue(e.getActionCommand(), false);
		}
	}
	
	public void receiptChildChangePerformed(ReceiptChildChangeEvent e)
	{
		this.display(e.getReceiptChildModel());
		((DeleteAction) this.clearAll.getAction()).enable(this.context.getMode());
		this.initValue(e.getInitInputValue());
	}
	
	public void modeChangePerformed(ModeChangeEvent e)
	{
		if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_POS))
		{
			this.initValue();
			this.display(this.context.getReceiptModel().getPositionModel());
		}
		else if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_PAY))
		{
			this.initValue();
			this.display(this.context.getReceiptModel().getPaymentModel());
		}
		else if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_SETTLEMENT))
		{
			this.initValue();
			this.display(null);
		}
		else if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_LOCK))
		{
			this.initValue();
			this.display(null);
		}
	}
	
	public void display(ReceiptChildModel rm)
	{
		if (this.context.getMode().equals(UserPanel.CONTEXT_MODE_LOCK))
		{
			this.comment = Messages.getString("ABlockNumeric.Zugangscode___12"); //$NON-NLS-1$
			this.display.setText(this.comment);
			this.display.setForeground(Color.black);
		}
		else if (this.context.getMode().equals(UserPanel.CONTEXT_MODE_SETTLEMENT))
		{
			this.comment = "Menge: "; //$NON-NLS-1$
			this.display.setText(this.comment);
			this.display.setForeground(Color.black);
		}
		else if (rm instanceof PositionModel)
		{
			Color color = PositionModel.getColor(((PositionModel) rm).getPosition());
			this.comment = PositionModel.getMessage(((PositionModel) rm).getPosition()).concat(" "); //$NON-NLS-1$
			this.display.setText(this.comment + this.readValue());
			this.display.setForeground(color);
		}
		else if (rm instanceof PaymentModel)
		{
			this.comment = PaymentModel.getMessage(((PaymentModel) rm).getPayment()).concat(" "); //$NON-NLS-1$
			this.display.setText(this.comment);
			this.display.setForeground(Color.black);
		}
	}
	
	private void calculateValue(String s, boolean keyBoard)
	{
		this.value = this.value.concat(s);
		
		if (this.context.getMode().equals(UserPanel.CONTEXT_MODE_LOCK))
		{
			if (this.value.length() == 1)
			{
				//				comment += "*"; //$NON-NLS-1$
				this.display.setText(this.comment + " *"); //$NON-NLS-1$
			}
			else
			{
				this.display.setText(this.display.getText() + "*"); //$NON-NLS-1$
			}
			return;
		}
		else if (this.context.getMode().equals(UserPanel.CONTEXT_MODE_POS))
		{
			if (keyBoard)
			{
				this.display.setText(this.comment + this.value);
				return;
			}
			else
			{
				this.display.setText(this.comment + this.value);
			}
		}
		else if (this.context.getMode().equals(UserPanel.CONTEXT_MODE_PAY))
		{
			this.display.setText(this.comment + this.value);
			((EnterAction) this.enter.getAction()).setState(this.context.getReceiptModel().getPaymentModel());
		}
		else if (this.context.getMode().equals(UserPanel.CONTEXT_MODE_SETTLEMENT))
		{
			this.display.setText(this.comment + this.value);
			((EnterAction) this.enter.getAction()).setState(this.context.getReceiptModel().getPaymentModel());
		}
		this
						.fireModeChangeEvent(new ModeChangeEvent(ModeChangeEvent.KEY_NUMERIC_BLOCK, this
										.valueIsInteger() ? ModeChangeEvent.VALUE_IS_INTEGER
										: ModeChangeEvent.VALUE_IS_NOT_INTEGER));
	}
	
	public String moveValue()
	{
		String s = this.readValue();
		this.initValue();
		return s;
	}
	
	public String readValue()
	{
		return this.value;
	}
	
	public int moveQuantity()
	{
		int i = this.readQuantity();
		this.initValue();
		return i;
	}
	
	public double moveAmount()
	{
		double d = this.readAmount();
		this.initValue();
		return d;
	}
	
	public double moveDiscount()
	{
		double d = this.readDiscount();
		this.initValue();
		return d;
	}
	
	public double moveQuotation()
	{
		double d = this.readQuotation();
		this.initValue();
		return d;
	}
	
	public int readQuantity()
	{
		return this.getValueAsInteger();
	}
	
	public double readAmount()
	{
		// return new
		// Double(NumberUtility.round(getValueAsDouble().doubleValue(),
		// ReceiptChildModel.roundFactorAmount.doubleValue()));
		return this.getValueAsDouble();
	}
	
	public double readDiscount()
	{
		return NumberUtility.round(this.getValueAsDouble() / 100, 0.000001D);
	}
	
	public double readQuotation()
	{
		return this.getValueAsDouble();
	}
	
	public double getValueAsDouble()
	{
		double d = 0D;
		try
		{
			d = Double.parseDouble(this.readValue());
		}
		catch (NumberFormatException e)
		{
		}
		return d;
	}
	
	public int getValueAsInteger()
	{
		int i = 0;
		try
		{
			i = Integer.parseInt(this.readValue());
		}
		catch (NumberFormatException e)
		{
		}
		return i;
	}
	
	private boolean valueIsInteger()
	{
		boolean isInteger = false;
		String s = "0"; //$NON-NLS-1$
		if (this.value.length() > 0)
		{
			s = this.value;
		}
		try
		{
			Integer.parseInt(s);
			isInteger = true;
		}
		catch (NumberFormatException e)
		{
			isInteger = false;
		}
		return isInteger;
	}
	
	public Long getPosLogin()
	{
		Long l = new Long(0L);
		try
		{
			l = new Long(this.readValue());
		}
		catch (NumberFormatException e)
		{
		}
		this.initValue();
		return l;
	}
	
	private void initValue()
	{
		this.initValue(true);
	}
	
	private void initValue(boolean init)
	{
		if (init)
		{
			this.value = ""; //$NON-NLS-1$
			this.display.setText(this.comment);
			this.fireModeChangeEvent(new ModeChangeEvent(ModeChangeEvent.KEY_NUMERIC_BLOCK,
							this.valueIsInteger() ? ModeChangeEvent.VALUE_IS_INTEGER
											: ModeChangeEvent.VALUE_IS_NOT_INTEGER));
		}
	}
	
	public void fireModeChangeEvent(ModeChangeEvent e)
	{
		ModeChangeListener[] l = (ModeChangeListener[]) this.modeChangeListeners.toArray(new ModeChangeListener[0]);
		for (int i = 0; i < l.length; i++)
		{
			l[i].modeChangePerformed(e);
		}
	}
	
	public PosButton getEnterButton()
	{
		return this.enter;
	}
	
	public PosButton getClearButton()
	{
		return this.clear;
	}
	
	// private static float size = 16F;
	
	private String comment = ""; //$NON-NLS-1$
	private String value = ""; //$NON-NLS-1$
	private PosButton enter;
	private PosButton clear;
	private PosButton clearAll;
	private JLabel display = new JLabel();
	
}
