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
public class TotalBlock extends ABlock implements ReceiptChangeListener, CurrencyChangeListener
{
	
	public static final long serialVersionUID = 0l;
	
	public TotalBlock(UserPanel context)
	{
		super(new GridLayout(1, 3), context);
		this.init();
	}
	
	private void init()
	{
		Config cfg = Config.getInstance();
		float size = cfg.getFontSize(cfg.getTotalBlockFont());
		int style = cfg.getFontStyle(cfg.getTotalBlockFont());
		Color fg = cfg.getTotalBlockColorForeground();
		Color bg = cfg.getTotalBlockColorBackground();
		
		this.currencyFC = this.makeValueLabel(size, style, fg, bg);
		this.totalValueFC = this.makeValueLabel(size, style, fg, bg);
		this.currency = this.makeValueLabel(size, style, fg, bg);
		this.totalValue = this.makeValueLabel(size, style, fg, bg);
		
		JPanel fcPanel = new JPanel(new BorderLayout());
		fcPanel.add(this.currencyFC, BorderLayout.WEST);
		fcPanel.add(this.totalValueFC, BorderLayout.CENTER);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(this.currency, BorderLayout.WEST);
		panel.add(this.totalValue, BorderLayout.CENTER);
		
		this.add(this.makeNameLabel(Messages.getString("TotalBlock.Total_1"), size, style, fg, bg)); //$NON-NLS-1$
		this.add(fcPanel);
		this.add(this.makeNameLabel("     ", size, style, fg, bg)); //$NON-NLS-1$
		this.add(panel);
		
		this.clearAll();
		
		this.context.getReceiptModel().getPositionTableModel().addReceiptChangeListener(this);
		this.context.getReceiptModel().getPaymentTableModel().addReceiptChangeListener(this);
	}
	
	private JLabel makeNameLabel(String text, float size, int style, Color fg, Color bg)
	{
		JLabel label = new JLabel(text);
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
		label.setOpaque(true);
		label.setFont(label.getFont().deriveFont(style, size));
		label.setForeground(fg);
		label.setBackground(bg);
		label.setHorizontalAlignment(JLabel.RIGHT);
		return label;
	}
	
	public void clearAll()
	{
		this.setTotal(Receipt.getEmptyReceipt());
		// this.totalValue.setText(NumberUtility.formatDefaultCurrency(new
		// Double(
		// 0.), true, true));
		//		this.totalValueFC.setText(""); //$NON-NLS-1$
	}
	
	public void receiptChangePerformed(ReceiptChangeEvent e)
	{
		// if (!e.getEventType().equals(ReceiptModel.RECEIPT_INITIALIZED)
		// && !e.getEventType().equals(new Integer(0)))
		this.setTotal(e.getReceiptModel().getReceipt());
	}
	
	public void currencyChangePerformed(CurrencyChangeEvent e)
	{
		this.setTotal(e.getReceiptModel().getReceipt());
	}
	
	public void setTotal(Receipt r)
	{
		ForeignCurrency defaultCurrency = ForeignCurrency.getDefaultCurrency();
		// ForeignCurrency defaultForeignCurrency =
		// context.getDefaultForeignCurrency();
		ForeignCurrency currentForeignCurrency = this.context.getCurrentForeignCurrency();
		
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMinimumFractionDigits(defaultCurrency.getCurrency().getDefaultFractionDigits());
		nf.setMaximumFractionDigits(defaultCurrency.getCurrency().getDefaultFractionDigits());
		this.currency.setText(defaultCurrency.getCurrency().getCurrencyCode());
		this.totalValue.setText(nf.format(r.getAmount()));
		
		if (!this.context.getCurrentForeignCurrency().getId().equals(defaultCurrency.getId()))
		{
			this.currencyFC.setText(currentForeignCurrency.getCurrency().getCurrencyCode());
			nf.setCurrency(currentForeignCurrency.getCurrency());
			nf.setMinimumFractionDigits(currentForeignCurrency.getCurrency().getDefaultFractionDigits());
			nf.setMaximumFractionDigits(currentForeignCurrency.getCurrency().getDefaultFractionDigits());
			String value = nf.format(r.getAmountFC(currentForeignCurrency));
			this.totalValueFC.setText(value);
		}
		else
		{
			this.currencyFC.setText(""); //$NON-NLS-1$
			this.totalValueFC.setText(""); //$NON-NLS-1$
		}
	}
	
	// private JLabel totalName;
	private JLabel currencyFC;
	private JLabel totalValueFC;
	private JLabel currency;
	private JLabel totalValue;
	
}
