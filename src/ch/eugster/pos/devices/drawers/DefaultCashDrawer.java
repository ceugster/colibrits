/*
 * Created on 13.08.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.devices.drawers;

import java.io.IOException;
import java.io.OutputStream;

import org.jdom.Element;

import ch.eugster.pos.devices.AsciiConstants;

/**
 * @author Administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DefaultCashDrawer extends CashDrawer
{
	
	/**
	 * @param out
	 * @param pulseOn
	 * @param pulseOff
	 */
	public DefaultCashDrawer(Element element, OutputStream out)
	{
		super(element, out);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.device.ICashDrawer#open()
	 */
	public void open()
	{
		if (this.use)
		{
			try
			{
				this.out.write(AsciiConstants.ESC);
				this.out.write(AsciiConstants.p);
				this.out.write(this.pin);
				this.out.write(this.pulseON);
				this.out.write(this.pulseOFF);
				this.out.flush();
			}
			catch (IOException e)
			{
			}
		}
	}
	
}
