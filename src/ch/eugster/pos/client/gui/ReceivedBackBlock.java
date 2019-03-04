/*
 * Created on 05.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.text.NumberFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.BorderUIResource;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.events.CurrencyChangeEvent;
import ch.eugster.pos.events.CurrencyChangeListener;
import ch.eugster.pos.events.ReceiptChangeEvent;
import ch.eugster.pos.events.ReceiptChangeListener;
import ch.eugster.pos.util.Config;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ReceivedBackBlock extends ABlock implements ReceiptChangeListener, CurrencyChangeListener
{
	
	public static final long serialVersionUID = 0l;
	
	private String[] backNames;
	
	public ReceivedBackBlock(UserPanel context)
	{
		super(new GridLayout(2, 3), context);
		this.init();
	}
	
	private void init()
	{
		Config cfg = Config.getInstance();
		float size = cfg.getFontSize(cfg.getTotalBlockFont());
		int style = cfg.getFontStyle(cfg.getTotalBlockFont());
		Color fg = cfg.getTotalBlockColorForeground();
		Color bg = cfg.getTotalBlockColorBackground();
		
		this.receivedName = this.makeNameLabel(Messages.getString("ReceivedBackBlock.Erhalten_2"), size, style, fg, bg); //$NON-NLS-1$
		this.receivedCodeFC = this.makeValueLabel(size, style, fg, bg);
		this.receivedValueFC = this.makeValueLabel(size, style, fg, bg);
		JLabel filler = this.makeNameLabel("     ", size, style, fg, bg); //$NON-NLS-1$
		this.receivedCode = this.makeValueLabel(size, style, fg, bg);
		this.receivedValue = this.makeValueLabel(size, style, fg, bg);
		
		JPanel receivedFCPanel = new JPanel(new BorderLayout());
		receivedFCPanel.add(this.receivedCodeFC, BorderLayout.WEST);
		receivedFCPanel.add(this.receivedValueFC, BorderLayout.CENTER);
		
		JPanel receivedPanel = new JPanel(new BorderLayout());
		receivedPanel.add(this.receivedCode, BorderLayout.WEST);
		receivedPanel.add(this.receivedValue, BorderLayout.CENTER);
		
		this.add(this.receivedName);
		this.add(receivedFCPanel);
		this.add(filler);
		this.add(receivedPanel);
		
		this.backNames = new String[2];
		this.backNames[0] = Messages.getString("ReceivedBackBlock.Backmoney_3");
		this.backNames[1] = "Offen";
		
		this.backName = this.makeNameLabel(Messages.getString("ReceivedBackBlock.Backmoney_3"), size, style, fg, bg); //$NON-NLS-1$
		this.backCode = this.makeValueLabel(size, style, fg, bg);
		this.backCodeFC = this.makeValueLabel(size, style, fg, bg);
		filler = this.makeNameLabel("     ", size, style, fg, bg); //$NON-NLS-1$
		this.backValue = this.makeValueLabel(size, style, fg, bg);
		this.backValueFC = this.makeValueLabel(size, style, fg, bg);
		
		JPanel backFCPanel = new JPanel(new BorderLayout());
		backFCPanel.add(this.backCodeFC, BorderLayout.WEST);
		backFCPanel.add(this.backValueFC, BorderLayout.CENTER);
		
		JPanel backPanel = new JPanel(new BorderLayout());
		backPanel.add(this.backCode, BorderLayout.WEST);
		backPanel.add(this.backValue, BorderLayout.CENTER);
		
		this.add(this.backName);
		this.add(backFCPanel);
		this.add(filler);
		this.add(backPanel);
		
		this.clearAll();
		
		this.context.getReceiptModel().getPositionTableModel().addReceiptChangeListener(this);
		this.context.getReceiptModel().getPaymentTableModel().addReceiptChangeListener(this);
	}
	
	private JLabel makeNameLabel(String text, float size, int style, Color fg, Color bg)
	{
		JLabel label = new JLabel(text);
		label.setBorder(BorderUIResource.getEtchedBorderUIResource());
		label.setOpaque(true);
		label.setFont(label.getFont().deriveFont(style, size));
		label.setForeground(fg);
		label.setBackground(bg);
		label.setHorizontalAlignment(JLabel.LEFT);
		return label;
	}
	
	private JLabel makeValueLabel(float size, int style, Color fg, Color bg)
	{
		JLabel label = new JLabel();
		label.setBorder(BorderUIResource.getEtchedBorderUIResource());
		label.setOpaque(true);
		label.setFont(label.getFont().deriveFont(style, size));
		label.setForeground(fg);
		label.setBackground(bg);
		label.setHorizontalAlignment(JLabel.RIGHT);
		return label;
	}
	
	public void clearAll()
	{
		this.setReceived(Receipt.getEmptyReceipt());
		this.setBack(Receipt.getEmptyReceipt());
	}
	
	public void receiptChangePerformed(ReceiptChangeEvent e)
	{
		Receipt r = e.getReceiptModel().getReceipt();
		// if (!e.getEventType().equals(ReceiptModel.RECEIPT_INITIALIZED) &&
		// !e.getEventType().equals(new Integer(0)))
		if (!e.getEventType().equals(new Integer(0)))
		{
			if (r.getPositionCount() == 0 || r.getPayment() != 0d)
			{
				this.setReceived(e.getReceiptModel().getReceipt());
				this.setBack(e.getReceiptModel().getReceipt());
				this.setVisible(true);
			}
			else
			{
				this.setVisible(false);
			}
		}
	}
	
	public void currencyChangePerformed(CurrencyChangeEvent e)
	{
		if (e.getReceiptModel().getReceipt().status != Receipt.RECEIPT_STATE_SERIALIZED)
		{
			this.setReceived(e.getReceiptModel().getReceipt());
			this.setBack(e.getReceiptModel().getReceipt());
		}
	}
	
	public void setReceived(Receipt r)
	{
		ForeignCurrency defaultCurrency = ForeignCurrency.getDefaultCurrency();
		// ForeignCurrency defaultForeignCurrency =
		// context.getDefaultForeignCurrency();
		ForeignCurrency currentForeignCurrency = this.context.getCurrentForeignCurrency();
		
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMinimumFractionDigits(defaultCurrency.getCurrency().getDefaultFractionDigits());
		nf.setMaximumFractionDigits(defaultCurrency.getCurrency().getDefaultFractionDigits());
		this.receivedCode.setText(defaultCurrency.getCurrency().getCurrencyCode());
		this.receivedValue.setText(nf.format(r.getPayment()));
		
		if (!this.context.getCurrentForeignCurrency().getId().equals(defaultCurrency.getId()))
		{
			this.receivedCodeFC.setText(currentForeignCurrency.getCurrency().getCurrencyCode());
			nf.setCurrency(currentForeignCurrency.getCurrency());
			this.receivedValueFC.setText(nf.format(r.getPaymentAmountFC(currentForeignCurrency)));
		}
		else
		{
			this.receivedCodeFC.setText(""); //$NON-NLS-1$
			this.receivedValueFC.setText(""); //$NON-NLS-1$
		}
	}
	
	public void setBack(Receipt r)
	{
		ForeignCurrency defaultCurrency = ForeignCurrency.getDefaultCurrency();
		// ForeignCurrency defaultForeignCurrency =
		// context.getDefaultForeignCurrency();
		ForeignCurrency currentForeignCurrency = this.context.getCurrentForeignCurrency();
		
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMinimumFractionDigits(defaultCurrency.getCurrency().getDefaultFractionDigits());
		nf.setMaximumFractionDigits(defaultCurrency.getCurrency().getDefaultFractionDigits());
		double amount = r.getPayment() - r.getAmount();
		if (amount < 0d)
			this.backName.setText(this.backNames[1]);
		else
			this.backName.setText(this.backNames[0]);
		this.backCode.setText(defaultCurrency.getCurrency().getCurrencyCode());
		this.backValue.setText(nf.format(Math.abs(amount)));
		
		if (!this.context.getCurrentForeignCurrency().getId().equals(defaultCurrency.getId()))
		{
			this.backCodeFC.setText(currentForeignCurrency.getCurrency().getCurrencyCode());
			nf.setCurrency(currentForeignCurrency.getCurrency());
			// double amount = r.getAmount().doubleValue()
			// / currentForeignCurrency.quotation.doubleValue();
			// double receivedAmount = r
			// .getPaymentAmountFC(currentForeignCurrency).doubleValue();
			double amountFC = r.getAmountFC(currentForeignCurrency);
			double receivedAmountFC = r.getPaymentAmountFC(currentForeignCurrency);
			this.backValueFC.setText(nf.format(Math.abs(receivedAmountFC - amountFC)));
		}
		else
		{
			this.backCodeFC.setText(""); //$NON-NLS-1$
			this.backValueFC.setText(""); //$NON-NLS-1$
		}
	}
	
	private JLabel receivedName;
	private JLabel receivedCode;
	private JLabel receivedCodeFC;
	private JLabel receivedValue;
	private JLabel receivedValueFC;
	private JLabel backName;
	private JLabel backCode;
	private JLabel backCodeFC;
	private JLabel backValue;
	private JLabel backValueFC;
}
