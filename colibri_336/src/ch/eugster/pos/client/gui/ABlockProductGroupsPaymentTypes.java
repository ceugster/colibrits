/*
 * Created on 01.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.client.gui;

import java.awt.CardLayout;

import ch.eugster.pos.events.ModeChangeEvent;

public class ABlockProductGroupsPaymentTypes extends ABlock
{
	
	private static final long serialVersionUID = 0l;
	
	/**
	 * 
	 */
	public ABlockProductGroupsPaymentTypes(UserPanel context)
	{
		super(new CardLayout(), context);
		this.init();
	}
	
	private void init()
	{
		
		this.layout = (CardLayout) this.getLayout();
		
		this.productGroupBlock = new ProductGroupBlock(this.context);
		this.paymentTypeBlock = new PaymentTypeBlock(this.context);
		
		this.add(this.productGroupBlock, ABlockProductGroupsPaymentTypes.PRODUCT_GROUP_BLOCK);
		this.add(this.paymentTypeBlock, ABlockProductGroupsPaymentTypes.PAYMENT_TYPE_BLOCK);
		
	}
	
	public void modeChangePerformed(ModeChangeEvent e)
	{
		if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_POS))
		{
			this.layout.show(this, ABlockProductGroupsPaymentTypes.PRODUCT_GROUP_BLOCK);
		}
		else if (e.getRequestedMode().equals(UserPanel.CONTEXT_MODE_PAY))
		{
			this.layout.show(this, ABlockProductGroupsPaymentTypes.PAYMENT_TYPE_BLOCK);
		}
	}
	
	public ProductGroupBlock getProductGroupBlock()
	{
		return this.productGroupBlock;
	}
	
	public PaymentTypeBlock getPaymentTypeBlock()
	{
		return this.paymentTypeBlock;
	}
	
	private CardLayout layout;
	private ProductGroupBlock productGroupBlock;
	private PaymentTypeBlock paymentTypeBlock;
	
	private static final String PRODUCT_GROUP_BLOCK = "keyProductGroupBlock"; //$NON-NLS-1$
	private static final String PAYMENT_TYPE_BLOCK = "keyPaymentTypeBlock"; //$NON-NLS-1$
	
}
