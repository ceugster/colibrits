package ch.eugster.pos.devices.displays;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.Timer;

import org.jdom.Element;

import ch.eugster.pos.Messages;
import ch.eugster.pos.client.model.ReceiptModel;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.Payment;
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.events.CurrencyChangeEvent;
import ch.eugster.pos.events.CurrencyChangeListener;
import ch.eugster.pos.events.ReceiptChangeEvent;
import ch.eugster.pos.events.ReceiptChangeListener;
import ch.eugster.pos.util.Config;
import ch.eugster.pos.util.NumberUtility;
import ch.eugster.pos.util.Path;

/*
 * Created on 25.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class CustomerDisplay implements ICustomerDisplay, ReceiptChangeListener, CurrencyChangeListener
{
	
	public CustomerDisplay(Element display)
	{
		this();
		this.loadDisplay(display);
	}
	
	/**
	 * 
	 */
	public CustomerDisplay()
	{
		super();
		
		// timer = new Timer();
		Properties properties = new Properties();
		try
		{
			FileInputStream fis = new FileInputStream(Path.getInstance().cfgDir.concat(File.separator
							.concat("display.ini"))); //$NON-NLS-1$
			properties.load(fis);
			fis.close();
		}
		catch (FileNotFoundException e)
		{
			System.exit(-1);
		}
		catch (IOException e)
		{
			System.exit(-1);
		}
		
		this.defaultText = new String[properties.size()];
		for (int i = 0; i < this.defaultText.length; i++)
		{
			String key = "line".concat(String.valueOf(i).concat(".default")); //$NON-NLS-1$ //$NON-NLS-2$
			this.defaultText[i] = properties.getProperty(key);
		}
		
		String welcomeText = Config.getInstance().getCustomerDisplayGetWelcomeText().trim();
		StringTokenizer t = new StringTokenizer(welcomeText, "|");
		this.welcomeText = new String[t.countTokens()];
		for (int i = 0; i < t.countTokens();)
		{
			this.welcomeText[i] = t.nextToken();
		}
		this.scrollWelcomeText = Config.getInstance().getCustomerDisplayGetWelcomeTextScroll();
		
		String closedText = Config.getInstance().getCustomerDisplayGetClosedText();
		t = new StringTokenizer(closedText);
		this.closedText = new String[t.countTokens()];
		for (int i = 0; i < t.countTokens();)
		{
			this.closedText[i] = t.nextToken();
		}
		// this.scrollClosedText =
		// Config.getInstance().getCustomerDisplayGetClosedTextScroll();
	}
	
	private void loadDisplay(Element element)
	{
		this.display = this.createCustomerDisplay(element);
	}
	
	private AbstractCustomerDisplay createCustomerDisplay(Element el)
	{
		AbstractCustomerDisplay pos = null;
		try
		{
			Class cls = Class.forName(el.getAttributeValue("class")); //$NON-NLS-1$
			Class[] params = new Class[1];
			params[0] = Element.class;
			Constructor c = cls.getConstructor(params);
			Object[] ps = new Object[params.length];
			ps[0] = el;
			pos = (AbstractCustomerDisplay) c.newInstance(ps);
			
		}
		catch (ClassNotFoundException e)
		{
			System.exit(-22);
		}
		catch (NoSuchMethodException e)
		{
			System.exit(-22);
		}
		catch (IllegalAccessException e)
		{
			System.exit(-22);
		}
		catch (InvocationTargetException e)
		{
			System.exit(-22);
		}
		catch (InstantiationException e)
		{
			System.exit(-22);
		}
		return pos;
		
	}
	
	// public void receiptChildChangePerformed(ReceiptChildChangeEvent e) {
	// if (UserPanel.getCurrent() != null) {
	// refresh(UserPanel.getCurrent().getReceiptModel(),
	// e.getReceiptChildModel().getReceiptChild());
	// }
	// }
	//	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.client.event.ReceiptChangeListener#receiptChangePerformed
	 * (ch.eugster.pos.client.event.ReceiptChangeEvent)
	 */
	public void receiptChangePerformed(final ReceiptChangeEvent e)
	{
		if (e.getReceiptModel().getPositionTableModel().getRowCount() > 0)
		// if (e.getEventType().equals(ReceiptModel.RECEIPT_CHILD_ADDED) ||
		// e.getEventType().equals(ReceiptModel.RECEIPT_CHILD_CHANGED)
		// || e.getEventType().equals(ReceiptModel.RECEIPT_CHILD_REMOVED) ||
		// e.getEventType().equals(ReceiptModel.RECEIPT_CUSTOMER_SET))
		{
			// timer.cancel();
			if (this.timer != null && this.timer.isRunning()) this.timer.stop();
			this.refresh(e.getReceiptModel(), e);
		}
		else
		{
			Element customerDisplay = Config.getInstance().getCustomerDisplay();
			if (Config.getInstance().getCustomerDisplayTimer(customerDisplay))
			{
				int delay = 1000 * Config.getInstance().getCustomerDisplaySeconds(customerDisplay);
				// TimerTask task = new TimerTask() {
				// public void run() {
				// refresh(e.getReceiptModel(), e.getReceiptChild());
				// }
				// };
				// timer.schedule(task, delay);
				ActionListener taskPerformer = new ActionListener()
				{
					public void actionPerformed(ActionEvent evt)
					{
						CustomerDisplay.this.refresh(e.getReceiptModel(), e);
					}
				};
				if (this.timer == null)
				{
					this.timer = new Timer(delay, taskPerformer);
					this.timer.setRepeats(false);
				}
				this.timer.start();
				
			}
			else
			{
				this.refresh(e.getReceiptModel(), e);
			}
		}
	}
	
	public void currencyChangePerformed(CurrencyChangeEvent e)
	{
		if (e.getReceiptModel().getReceipt().status != Receipt.RECEIPT_STATE_SERIALIZED)
		{
			if (e.getReceiptChild() instanceof Position)
				this.refreshPosition(e.getReceiptModel(), (Position) e.getReceiptChild());
			else if (e.getReceiptChild() instanceof Payment)
				this.refreshPayment(e.getReceiptModel(), (Payment) e.getReceiptChild());
			else
				this.refresh(e.getReceiptModel(), null);
		}
	}
	
	private void refreshPosition(ReceiptModel model, Position position)
	{
		String[] text = new String[this.display.getLineCount()];
		if (text.length > 0)
		{
			text[0] = this.getPositionLine(this.display, position);
		}
		if (text.length == 2)
		{
			text[1] = this.getTotalLine(this.display, model);
		}
		this.refreshDisplay(text);
	}
	
	private void refreshPayment(ReceiptModel model, Payment payment)
	{
		String[] text = new String[this.display.getLineCount()];
		if (new Double(payment.getAmount()).compareTo(new Double(model.getReceipt().getAmount())) > 0)
		{
			if (text.length > 0)
			{
				text[0] = this.getPaidLine(this.display, model);
			}
			if (text.length == 2)
			{
				text[1] = this.getBackLine(this.display, model);
			}
		}
		else
		{
			if (text.length > 0)
			{
				text[0] = this.getTotalLine(this.display, model);
			}
			if (text.length == 2)
			{
				text[1] = this.getPaidLine(this.display, model);
			}
		}
		this.refreshDisplay(text);
	}
	
	private void refreshDisplay(String[] text)
	{
		this.clear(this.display);
		this.display.moveCursorHome();
		this.display.print(text);
	}
	
	private void refresh(ReceiptModel model, ReceiptChangeEvent event)
	{
		if (event == null)
		{
			this.welcome(this.display);
		}
		else if (event.getEventType().equals(ReceiptModel.RECEIPT_CUSTOMER_SET))
			return;
		else if (event.getReceiptChild() instanceof Position)
		{
			this.refreshPosition(model, (Position) event.getReceiptChild());
		}
		else if (event.getReceiptChild() instanceof Payment)
		{
			this.refreshPayment(model, (Payment) event.getReceiptChild());
		}
		else if (event.getReceiptChild() == null)
		{
			this.welcome(this.display);
		}
	}
	
	private String getPositionLine(AbstractCustomerDisplay display, Position p)
	{
		String amount = NumberUtility.formatDefaultCurrency(p.getAmount(), false, false);
		String t = new String();
		if (p.text == null | p.text.equals("")) { //$NON-NLS-1$
			t = p.getProductGroup().name;
		}
		else
		{
			t = p.text;
		}
		if (amount.length() + t.length() >= display.getLineLength())
		{
			t = t.substring(0, display.getLineLength() - amount.length() - 4);
			t = t.concat("... "); //$NON-NLS-1$
		}
		StringBuffer text = new StringBuffer(t);
		StringBuffer fill = new StringBuffer(display.getLineLength() - amount.length() - t.length());
		for (int i = 0; i < fill.capacity(); i++)
		{
			fill.append(" "); //$NON-NLS-1$
		}
		return text.append(fill.append(amount)).toString();
	}
	
	private String getPaidLine(AbstractCustomerDisplay display, ReceiptModel model)
	{
		String t = Messages.getString("CustomerDisplay.Paid"); //$NON-NLS-1$
		String text = t;
		String amount = ""; //$NON-NLS-1$
		
		if (model.getContext().getCurrentForeignCurrency().getId().equals(ForeignCurrency.getDefaultCurrency().getId()))
		{
			amount = NumberUtility.formatDefaultCurrency(model.getReceipt().getPayment(), false, false);
		}
		else
		{
			NumberFormat nf = NumberFormat.getCurrencyInstance();
			ForeignCurrency fc = model.getContext().getCurrentForeignCurrency();
			nf.setCurrency(fc.getCurrency());
			amount = NumberUtility.formatCurrency(nf, fc.getCurrency(), model.getReceipt().getPaymentAmountFC(), false);
		}
		
		if (amount.length() + t.length() >= display.getLineLength())
		{
			if (amount.length() + 4 > display.getLineLength())
			{
				t = "";
			}
			else
			{
				t = t.substring(0, display.getLineLength() - amount.length() - 4);
				t = t.concat("..."); //$NON-NLS-1$
			}
		}
		
		char[] filler = new char[display.getLineLength() - t.length() - amount.length()];
		for (int i = 0; i < filler.length; i++)
		{
			filler[i] = ' ';
		}
		
		return text + String.valueOf(filler) + amount;
	}
	
	private String getBackLine(AbstractCustomerDisplay display, ReceiptModel model)
	{
		String t = Messages.getString("CustomerDisplay.Back"); //$NON-NLS-1$
		StringBuffer text = new StringBuffer(t);
		
		double amnt = model.getReceipt().getAmount();
		String amount = ""; //$NON-NLS-1$
		if (model.getContext().getCurrentForeignCurrency().getId().equals(
						model.getContext().getDefaultForeignCurrency().getId()))
		{
			NumberFormat nf = NumberFormat.getCurrencyInstance();
			ForeignCurrency fc = model.getContext().getCurrentForeignCurrency();
			nf.setCurrency(fc.getCurrency());
			double diff = amnt >= 0d ? Math.abs(model.getDifferenceFC(fc)) : -Math.abs(model.getDifferenceFC(fc));
			amount = NumberUtility.formatCurrency(nf, fc.getCurrency(), diff, false);
		}
		else
		{
			double diff = amnt >= 0d ? Math.abs(model.getDifference()) : -Math.abs(model.getDifference());
			amount = NumberUtility.formatDefaultCurrency(diff, false, false);
		}
		
		if (amount.length() + t.length() >= display.getLineLength())
		{
			if (amount.length() + 4 > display.getLineLength())
			{
				t = "";
			}
			else
			{
				t = t.substring(0, display.getLineLength() - amount.length() - 4);
				t = t.concat("..."); //$NON-NLS-1$
			}
		}
		
		StringBuffer fill = new StringBuffer(display.getLineLength() - t.length() - amount.length());
		for (int i = 0; i < fill.capacity(); i++)
		{
			fill.append(" "); //$NON-NLS-1$
		}
		return text.append(fill.append(amount)).toString();
	}
	
	private String getTotalLine(AbstractCustomerDisplay display, ReceiptModel model)
	{
		String t = Messages.getString("CustomerDisplay.Total"); //$NON-NLS-1$
		StringBuffer text = new StringBuffer(t);
		String amount = ""; //$NON-NLS-1$
		
		if (model.getContext().getCurrentForeignCurrency().getId().equals(ForeignCurrency.getDefaultCurrency().getId()))
		{
			amount = NumberUtility.formatDefaultCurrency(model.getReceipt().getAmount(), false, false);
		}
		else
		{
			NumberFormat nf = NumberFormat.getCurrencyInstance();
			ForeignCurrency fc = model.getContext().getCurrentForeignCurrency();
			nf.setCurrency(fc.getCurrency());
			amount = NumberUtility.formatCurrency(nf, fc.getCurrency(), model.getReceipt().getAmountFC(fc), false);
		}
		
		if (amount.length() + t.length() >= display.getLineLength())
		{
			t = t.substring(0, display.getLineLength() - amount.length() - 4);
			t = t.concat("..."); //$NON-NLS-1$
		}
		
		StringBuffer fill = new StringBuffer(display.getLineLength() - t.length() - amount.length());
		for (int i = 0; i < fill.capacity(); i++)
		{
			fill.append(" "); //$NON-NLS-1$
		}
		return text.append(fill.append(amount)).toString();
	}
	
	// private String getPaidLine(AbstractCustomerDisplay display, ReceiptModel
	// model) {
	//		String t = Messages.getString("CustomerDisplay.Paid"); //$NON-NLS-1$
	// StringBuffer text = new StringBuffer(t);
	// String amount = "";
	//		
	// if
	// (model.getContext().getCurrentForeignCurrency().getId().equals(ForeignCurrency.getDefaultCurrency().getId()))
	// {
	// amount =
	// NumberUtility.formatDefaultCurrency(model.getReceipt().getPaymentAmount(),
	// false, false) ;
	// }
	// else {
	// NumberFormat nf = NumberFormat.getCurrencyInstance();
	// ForeignCurrency fc = model.getContext().getCurrentForeignCurrency();
	// nf.setCurrency(fc.getCurrency());
	// amount = NumberUtility.formatCurrency(nf, fc.getCurrency(),
	// model.getReceipt().getPaymentAmountFC(fc), false) ;
	// }
	//		
	// if (amount.length() + t.length() >= display.getLineLength()) {
	// t = t.substring(0, display.getLineLength() - amount.length() - 4);
	//			t = t.concat("..."); //$NON-NLS-1$
	// }
	//		
	// StringBuffer fill = new StringBuffer(display.getLineLength() - t.length()
	// - amount.length());
	// for (int i = 0; i < fill.capacity(); i++) {
	//			fill.append(" "); //$NON-NLS-1$
	// }
	// return text.append(fill.append(amount)).toString();
	// }
	//
	public void clear(AbstractCustomerDisplay display)
	{
		display.clear();
	}
	
	public void welcome(AbstractCustomerDisplay display)
	{
		display.clear();
		if (this.welcomeText.length > 0)
		{
			if (this.scrollWelcomeText)
			{
				this.scrollUpperLine(display, this.welcomeText[0]);
			}
			else
			{
				display.welcome(this.welcomeText);
			}
		}
		else
		{
			display.welcome(this.defaultText);
		}
	}
	
	public void scrollUpperLine(AbstractCustomerDisplay display, String text)
	{
		text = text + " ";
		display.setUpperLineMessageScrollContinuously(text.getBytes());
	}
	
	public void write(AbstractCustomerDisplay display, String[] text)
	{
		display.write(text);
	}
	
	public void write(AbstractCustomerDisplay display, String text)
	{
		display.write(text.getBytes());
	}
	
	public void write(AbstractCustomerDisplay display, byte[] text)
	{
		display.write(text);
	}
	
	public void selectInternationalCharacterSet(AbstractCustomerDisplay display, int characterSet)
	{
		display.selectInternationalCharacterSet(characterSet);
	}
	
	public AbstractCustomerDisplay getDisplay()
	{
		return this.display;
	}
	
	public void closeAll()
	{
		this.display.clear();
		this.display.close();
	}
	
	protected static int parse(String value, int defaultValue)
	{
		int result = defaultValue;
		try
		{
			result = new Integer(value).intValue();
		}
		catch (NumberFormatException e)
		{
			result = defaultValue;
		}
		return result;
	}
	
	private AbstractCustomerDisplay display;
	private String[] defaultText;
	private String[] welcomeText;
	private boolean scrollWelcomeText;
	private String[] closedText;
	// private boolean scrollClosedText;
	private Timer timer;
	
}
