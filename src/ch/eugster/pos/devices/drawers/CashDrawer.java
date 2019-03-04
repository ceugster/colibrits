/*
 * Created on 13.08.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.devices.drawers;

import java.io.OutputStream;

import org.jdom.Element;

/**
 * @author Administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class CashDrawer implements ICashDrawer
{
	
	/**
	 * 
	 */
	public CashDrawer(Element element, OutputStream out)
	{
		super();
		this.init(element, out);
	}
	
	private void init(Element element, OutputStream out)
	{
		this.id = element.getAttributeValue("id"); //$NON-NLS-1$
		this.use = new Boolean(element.getAttributeValue("use")).booleanValue(); //$NON-NLS-1$
		this.pin = Integer.parseInt(element.getAttributeValue("pin")); //$NON-NLS-1$
		this.pulseON = Integer.parseInt(element.getAttributeValue("pulseon")); //$NON-NLS-1$
		this.pulseOFF = Integer.parseInt(element.getAttributeValue("pulseoff")); //$NON-NLS-1$
		this.currency = element.getAttributeValue("currency"); //$NON-NLS-1$
		this.out = out;
	}
	
	public abstract void open();
	
	public boolean use;
	public String id;
	public int pin;
	public int pulseON;
	public int pulseOFF;
	public String currency;
	
	protected OutputStream out;
}
