/*
 * Created on 01.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.client.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import ch.eugster.pos.events.ModeChangeEvent;
import ch.eugster.pos.util.Config;

public class ChildrenBlock extends ABlock
{
	private static final long serialVersionUID = 0l;
	
	/**
	 * 
	 */
	public ChildrenBlock(UserPanel context)
	{
		super(new BorderLayout(), context);
		this.init();
	}
	
	private void init()
	{
		this.setFocusable(false);
		
		this.totalBlock = new TotalBlock(this.context);
		this.context.getReceiptModel().addReceiptChangeListener(this.totalBlock);
		this.receivedBackBlock = new ReceivedBackBlock(this.context); // *
		this.context.getReceiptModel().addReceiptChangeListener(this.receivedBackBlock); // *
		
		JPanel northPanel = new JPanel(new BorderLayout()); // *
		
		northPanel.add(this.totalBlock, BorderLayout.NORTH); // *
		
		if (Boolean.valueOf(Config.getInstance().getTotalBlock().getAttributeValue("show-always")).booleanValue())
		{
			northPanel.add(this.receivedBackBlock, BorderLayout.SOUTH); // *
		}
		
		this.layout = new CardLayout();
		this.panel = new JPanel(this.layout);
		
		this.currentReceiptTable = new CurrentReceiptTableBlock(this.context);
		this.parkedReceiptTable = new ParkedReceiptTableBlock(this.context);
		this.positionBlock = new PositionBlock(this.context);
		this.paymentBlock = new PaymentBlock(this.context, this.receivedBackBlock);
		
		this.panel.add(this.currentReceiptTable, ChildrenBlock.KEY_CURRENT_RECEIPT_TABLE_BLOCK);
		this.panel.add(this.parkedReceiptTable, ChildrenBlock.KEY_PARKED_RECEIPT_TABLE_BLOCK);
		this.panel.add(this.positionBlock, ChildrenBlock.KEY_POSITION_BLOCK);
		this.panel.add(this.paymentBlock, ChildrenBlock.KEY_PAYMENT_BLOCK);
		
		this.add(northPanel, BorderLayout.NORTH);
		this.add(this.panel, BorderLayout.CENTER);
		
	}
	
	// public void receiptChangePerformed(ReceiptChangeEvent e) {
	// }
	
	public void modeChangePerformed(ModeChangeEvent e)
	{
		if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_LOCK))
		{
			return;
		}
		
		if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_POS))
		{
			this.currentBlock = this.positionBlock;
			this.layout.show(this.panel, ChildrenBlock.KEY_POSITION_BLOCK);
		}
		else if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_PAY))
		{
			this.currentBlock = this.paymentBlock;
			this.layout.show(this.panel, ChildrenBlock.KEY_PAYMENT_BLOCK);
		}
		else if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_PARKED_RECEIPT_LIST))
		{
			this.currentBlock = this.parkedReceiptTable;
			this.layout.show(this.panel, ChildrenBlock.KEY_PARKED_RECEIPT_TABLE_BLOCK);
		}
		else if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_CURRENT_RECEIPT_LIST))
		{
			this.currentBlock = this.currentReceiptTable;
			this.layout.show(this.panel, ChildrenBlock.KEY_CURRENT_RECEIPT_TABLE_BLOCK);
		}
		else if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_SETTLEMENT))
		{
			this.currentBlock = this.positionBlock;
			this.layout.show(this.panel, ChildrenBlock.KEY_POSITION_BLOCK);
		}
	}
	
	public CurrentReceiptTableBlock getCurrentReceiptTableBlock()
	{
		return this.currentReceiptTable;
	}
	
	public ParkedReceiptTableBlock getParkedReceiptTableBlock()
	{
		return this.parkedReceiptTable;
	}
	
	public PositionBlock getPositionBlock()
	{
		return this.positionBlock;
	}
	
	public PaymentBlock getPaymentBlock()
	{
		return this.paymentBlock;
	}
	
	public JComponent getCurrentBlock()
	{
		return this.currentBlock;
	}
	
	public TotalBlock getTotalBlock()
	{
		return this.totalBlock;
	}
	
	public ReceivedBackBlock getReceivedBackBlock()
	{ // *
		return this.receivedBackBlock; // *
	} // *
	
	private JPanel panel;
	private CardLayout layout;
	private PositionBlock positionBlock;
	private PaymentBlock paymentBlock;
	private CurrentReceiptTableBlock currentReceiptTable;
	private ParkedReceiptTableBlock parkedReceiptTable;
	private JComponent currentBlock;
	private TotalBlock totalBlock;
	
	private ReceivedBackBlock receivedBackBlock; // *
	
	private static final String KEY_CURRENT_RECEIPT_TABLE_BLOCK = "currentReceiptTableBlock"; //$NON-NLS-1$
	private static final String KEY_PARKED_RECEIPT_TABLE_BLOCK = "parkedReceiptTableBlock"; //$NON-NLS-1$
	private static final String KEY_POSITION_BLOCK = "positionBlock"; //$NON-NLS-1$
	private static final String KEY_PAYMENT_BLOCK = "paymentBlock"; //$NON-NLS-1$
}
