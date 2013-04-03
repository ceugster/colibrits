/*
 * Created on 30.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.devices.printers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;

import org.jdom.Element;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.Version;
import ch.eugster.pos.devices.drawers.CashDrawer;
import ch.eugster.pos.devices.drawers.DefaultCashDrawer;
import ch.eugster.pos.events.MessageDialog;
import ch.eugster.pos.util.Path;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class POSPrinter implements IPOSPrinter
{
	
	/**
	 * 
	 */
	protected POSPrinter(Element settings)
	{
		super();
		this.init(settings);
	}
	
	private void init(Element p)
	{
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Belegdrucker wird initialisiert.");
		this.usePOSPrinter = new Boolean(p.getAttributeValue("use")).booleanValue(); //$NON-NLS-1$
		
		this.id = p.getAttributeValue("id"); //$NON-NLS-1$
		this.name = p.getAttributeValue("name"); //$NON-NLS-1$
		
		Element device = p.getChild("device"); //$NON-NLS-1$
		this.alias = device.getAttributeValue("alias"); //$NON-NLS-1$
		this.port = device.getAttributeValue("port"); //$NON-NLS-1$
		this.charsetTable = device.getAttributeValue("charset"); //$NON-NLS-1$
		this.characterTable = Integer.valueOf(device.getAttributeValue("charactertable")).intValue(); //$NON-NLS-1$
		if (this.charsetTable.length() > 0)
		{
			this.loadConverter(this.charsetTable);
		}
		
		Element serial = device.getChild("serial"); //$NON-NLS-1$
		this.baudrate = Integer.valueOf(serial.getAttributeValue("baudrate")).intValue(); //$NON-NLS-1$
		this.flowcontrolin = Integer.valueOf(serial.getAttributeValue("flowcontrolin")).intValue(); //$NON-NLS-1$
		this.flowcontrolout = Integer.valueOf(serial.getAttributeValue("flowcontrolout")).intValue(); //$NON-NLS-1$
		this.databits = Integer.valueOf(serial.getAttributeValue("databits")).intValue(); //$NON-NLS-1$
		this.stopbits = Integer.valueOf(serial.getAttributeValue("stopbits")).intValue(); //$NON-NLS-1$
		this.parity = Integer.valueOf(serial.getAttributeValue("parity")).intValue(); //$NON-NLS-1$
		
		this.outputStream = this.openPort(this.port, this.alias);
		if (this.usePOSPrinter)
		{
			Element[] drawers = (Element[]) p.getChildren("cashdrawer").toArray(new Element[0]); //$NON-NLS-1$
			List d = new ArrayList();
			for (Element drawer : drawers)
			{
				if (new Boolean(drawer.getAttributeValue("use")).booleanValue()) { //$NON-NLS-1$
					d.add(new DefaultCashDrawer(drawer, this.outputStream));
				}
			}
			this.cashDrawers = new CashDrawer[d.size()];
			Iterator iter = d.iterator();
			for (int i = 0; i < this.cashDrawers.length; i++)
			{
				this.cashDrawers[i] = (CashDrawer) iter.next();
			}
		}
	}
	
	private OutputStream openPort(String port, String alias)
	{
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Port wird geöffnet.");
		CommPortIdentifier portId = null;
		this.serialPort = null;
		this.outputStream = null;
		
		try
		{
			portId = CommPortIdentifier.getPortIdentifier(port);
		}
		catch (NoSuchPortException e)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Port existiert nicht.");
			if (Version.getRunningProgram() == Version.COLIBRI)
			{
				MessageDialog.showInformation(
								null,
								Messages.getString("POSPrinter.Falscher_Port_31"),
								Messages.getString("POSPrinter.Der_serielle_Port__29").concat(port)
												.concat(Messages.getString("POSPrinter._existiert_nicht._30")), 0);
			}
			this.usePOSPrinter = false;
		}
		
		if (this.usePOSPrinter)
		{
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL)
			{
				try
				{
					this.serialPort = (SerialPort) portId.open(alias, 2000);
					this.outputStream = this.serialPort.getOutputStream();
					this.serialPort.setSerialPortParams(this.baudrate, this.databits, this.stopbits, this.parity);
				}
				catch (PortInUseException e)
				{
					if (Version.getRunningProgram() == Version.COLIBRI)
					{
						MessageDialog.showInformation(
										null,
										"Fehler beim Initialisieren des Coupondruckers",
										"Der serielle Port "
														+ port
														+ " wird von einem anderen Gerät genutzt. Das Programm wird verlassen.",
										0);
					}
					this.usePOSPrinter = false;
				}
				catch (IOException e)
				{
					System.exit(-23);
				}
				catch (UnsupportedCommOperationException e)
				{
					if (Version.getRunningProgram() == Version.COLIBRI)
					{
						MessageDialog.showInformation(
										null,
										"Fehler beim Initialisieren des Coupondruckers",
										"Der serielle Port "
														+ port
														+ " unterstützt einen Befehl nicht. Das Programm wird verlassen.",
										0);
					}
					System.exit(-23);
				}
			}
		}
		else
		{
			this.outputStream = System.out;
		}
		return this.outputStream;
	}
	
	public void closePort()
	{
		try
		{
			if (this.outputStream != null)
			{
				this.outputStream.close();
				this.serialPort.close();
			}
		}
		catch (IOException e)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
							.warning(Messages.getString("POSPrinter.Der_Bondrucker__47") + this.name + Messages.getString("POSPrinter._konnte_nicht_ordnungsgem_u00E4ss_geschlossen_werden___48") + e.getLocalizedMessage()); //$NON-NLS-1$ //$NON-NLS-2$ 
		}
	}
	
	public void send(int code)
	{
		// if (usePOSPrinter) {
		try
		{
			this.outputStream.write(code);
		}
		catch (IOException e)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage());
		}
		// }
	}
	
	public void send(int[] codes)
	{
		// if (usePOSPrinter) {
		try
		{
			for (int code : codes)
			{
				this.outputStream.write(code);
			}
		}
		catch (IOException e)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage());
		}
		// }
	}
	
	public void println(String s)
	{
		// if (usePOSPrinter) {
		this.print(s);
		this.print(System.getProperty("line.separator")); //$NON-NLS-1$
		// }
	}
	
	public void print(String s)
	{
		// Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("...");
		int[] intArray = this.convert(s);
		try
		{
			for (int element : intArray)
			{
				this.outputStream.write(element);
				if (!System.out.equals(this.outputStream))
				{
					System.out.write(element);
				}
			}
		}
		catch (IOException e)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage());
		}
	}
	
	public void testPrint()
	{
		// if (usePOSPrinter) {
		try
		{
			for (int i = 32; i < 300; i++)
			{
				this.outputStream.write(i);
				byte[] b = String.valueOf(i).getBytes();
				this.outputStream.write(b);
			}
		}
		catch (IOException e)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage());
		}
		// }
	}
	
	public int getCashDrawerCount()
	{
		return this.cashDrawers.length;
	}
	
	public CashDrawer getCashDrawer(int which)
	{
		if (this.cashDrawers.length > which)
		{
			return this.cashDrawers[which];
		}
		return null;
	}
	
	public CashDrawer[] getCashDrawers()
	{
		return this.cashDrawers;
	}
	
	public boolean isUsed()
	{
		return this.usePOSPrinter;
	}
	
	protected byte[] convert(byte[] array)
	{
		Byte key = null;
		Byte value = null;
		for (int i = 0; i < array.length; i++)
		{
			key = new Byte(array[i]);
			value = (Byte) this.converter.get(key);
			if (value != null)
			{
				array[i] = value.byteValue();
			}
		}
		return array;
	}
	
	protected int[] convert(String text)
	{
		int[] intArray = new int[text.length()];
		for (int i = 0; i < text.length(); i++)
		{
			Integer val = (Integer) this.converter.get(new Integer(text.charAt(i)));
			if (val != null)
			{
				intArray[i] = val.intValue();
			}
			else
			{
				intArray[i] = new Integer(text.charAt(i)).intValue();
			}
		}
		return intArray;
	}
	
	protected void loadConverter(String filename)
	{
		File cfg = new File(Path.getInstance().cfgDir.concat(filename));
		if (cfg.exists())
		{
			FileInputStream in = null;
			Properties p = new Properties();
			this.converter = new Hashtable();
			try
			{
				in = new FileInputStream(cfg);
				p.load(in);
				in.close();
				
				if (!p.isEmpty())
				{
					String skey = null;
					String sval = null;
					Integer key = null;
					Integer val = null;
					Set keys = p.keySet();
					Iterator i = keys.iterator();
					while (i.hasNext())
					{
						try
						{
							skey = (String) i.next();
							sval = (String) p.get(skey);
							key = new Integer(skey);
							val = new Integer(sval);
							this.converter.put(key, val);
						}
						catch (NumberFormatException e)
						{
							//							Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning(Messages.getString("POSPrinter.Ung_u00FCltiger_Wert_in_der_Convertertabelle___55") + e.getLocalizedMessage()); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
				}
			}
			catch (FileNotFoundException e)
			{
				//				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning(Messages.getString("POSPrinter.Converterdatei__57") + filename + Messages.getString("POSPrinter._nicht_gefunden___58") + e.getLocalizedMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			catch (IOException e)
			{
				//				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning(Messages.getString("POSPrinter.Converterdatei__60") + filename + Messages.getString("POSPrinter._konnte_nicht_geladen_werden___61") + e.getLocalizedMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			finally
			{
			}
		}
	}
	
	/**
	 * @deprecated in 1.2.0 Build 80 use isUsed() instead.
	 * 
	 * @return <code>true</code> if printer is used for printing out on serial
	 *         port, <code>false</code> if no serial port is available. The
	 *         printer prints in the second case to System.out.
	 * 
	 */
	@Deprecated
	public boolean isPOSPrinterUsed()
	{
		return this.usePOSPrinter;
	}
	
	protected abstract void kickOutDrawerOne(int t1, int t2);
	
	protected abstract void kickOutDrawerTwo(int t1, int t2);
	
	public abstract void kickOutDrawer(int drawer);
	
	public abstract void kickOutDrawer(int[] drawers);
	
	protected boolean usePOSPrinter = false;
	
	protected int baudrate = 9600;
	protected int flowcontrolin = 0;
	protected int flowcontrolout = 0;
	protected int databits = 8;
	protected int stopbits = 1;
	protected int parity = 0;
	
	protected String id;
	protected String name;
	protected String alias;
	protected String port;
	protected String charsetTable;
	protected int characterSet = 2;
	protected int characterTable = 0;
	protected Hashtable converter;
	
	protected CashDrawer[] cashDrawers;
	protected OutputStream outputStream = null;
	protected SerialPort serialPort = null;
}
