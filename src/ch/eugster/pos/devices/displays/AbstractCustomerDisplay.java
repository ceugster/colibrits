/*
 * Created on 24.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.devices.displays;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;

import org.jdom.Element;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.Version;
import ch.eugster.pos.events.MessageDialog;
import ch.eugster.pos.util.Path;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public abstract class AbstractCustomerDisplay
{
	
	/**
	 * @param properties
	 */
	public AbstractCustomerDisplay(Element element)
	{
		this.init(element);
	}
	
	protected void init(Element element)
	{
		this.useCustomerDisplay = new Boolean(element.getAttributeValue("use")).booleanValue(); //$NON-NLS-1$
		
		this.id = element.getAttributeValue("id"); //$NON-NLS-1$
		this.name = element.getAttributeValue("name"); //$NON-NLS-1$
		this.emulation = this.parse(element.getAttributeValue("emulation"), this.codetable); //$NON-NLS-1$
		this.lineLength = this.parse(element.getAttributeValue("line-length"), this.lineLength); //$NON-NLS-1$
		this.lineCount = this.parse(element.getAttributeValue("line-count"), this.lineCount); //$NON-NLS-1$
		
		Element device = element.getChild("device"); //$NON-NLS-1$
		this.alias = device.getAttributeValue("alias"); //$NON-NLS-1$
		this.port = device.getAttributeValue("port"); //$NON-NLS-1$
		this.charsetTable = device.getAttributeValue("charset", ""); //$NON-NLS-1$ //$NON-NLS-2$
		this.codetable = Integer.valueOf(device.getAttributeValue("charactertable")).intValue(); //$NON-NLS-1$
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
		
		if (this.useCustomerDisplay)
		{
			if (Version.getRunningProgram() == Version.COLIBRI)
			{
				try
				{
					this.outputStream = this.open(this.port, this.alias);
				}
				catch (Exception e)
				{
					// System.exit(-23);
				}
			}
		}
	}
	
	protected OutputStream open(String port, String alias) throws Exception
	{
		try
		{
			this.portId = CommPortIdentifier.getPortIdentifier(port);
		}
		catch (NoSuchPortException e)
		{
			this.useCustomerDisplay = false;
			MessageDialog.showSimpleDialog(
							null,
							Messages.getString("AbstractCustomerDisplay.Warnung_28"),
							Messages.getString("AbstractCustomerDisplay.Der_serielle_Port__26").concat(port)
											.concat(Messages.getString("AbstractCustomerDisplay._existiert_nicht._27")),
							0);
			throw e;
		}
		
		if (this.useCustomerDisplay)
		{
			if (this.portId.getPortType() == CommPortIdentifier.PORT_SERIAL)
			{
				try
				{
					this.serialPort = (SerialPort) this.portId.open(alias, 2000);
				}
				catch (PortInUseException e)
				{
					this.useCustomerDisplay = false;
					MessageDialog.showSimpleDialog(
									null,
									Messages.getString("AbstractCustomerDisplay.Warnung_33"),
									Messages.getString("AbstractCustomerDisplay.Der_serielle_Port__31")
													.concat(port)
													.concat(Messages.getString("AbstractCustomerDisplay._wird_bereits_verwendet._32")),
									0);
				}
				
				try
				{
					this.outputStream = this.serialPort.getOutputStream();
				}
				catch (IOException e)
				{
					this.useCustomerDisplay = false;
					MessageDialog.showSimpleDialog(
									null,
									Messages.getString("AbstractCustomerDisplay.Warnung_38"),
									Messages.getString("AbstractCustomerDisplay.Der_serielle_Port__36")
													.concat(port)
													.concat(Messages.getString("AbstractCustomerDisplay._kann_nicht_ge_u00F6ffnet_werden._37")),
									0);
				}
				
				try
				{
					this.serialPort.setSerialPortParams(this.baudrate, this.databits, this.stopbits, this.parity);
				}
				catch (UnsupportedCommOperationException e)
				{
					this.useCustomerDisplay = false;
				}
			}
		}
		return this.outputStream;
	}
	
	public void print(String s)
	{
		if (this.useCustomerDisplay)
		{
			int[] intArray = this.convert(s);
			try
			{
				for (int i = 0; i < intArray.length; i++)
				{
					this.outputStream.write(intArray[i]);
				}
			}
			catch (IOException e)
			{
			}
		}
		System.out.println(s);
	}
	
	public void print(String[] s)
	{
		for (int i = 0; i < s.length; i++)
		{
			this.print(s[i]);
		}
	}
	
	public void write(int n)
	{
		if (this.useCustomerDisplay)
		{
			try
			{
				this.outputStream.write(n);
			}
			catch (IOException e)
			{
			}
		}
	}
	
	public void write(byte text)
	{
		byte[] b = new byte[1];
		b[0] = text;
		this.write(b);
	}
	
	public void write(byte[] text)
	{
		if (this.useCustomerDisplay)
		{
			text = this.convert(text);
			try
			{
				this.outputStream.write(text);
			}
			catch (IOException e)
			{
			}
		}
		String s = new String(text);
		System.out.println("Kundendisplay: " + s);
	}
	
	public void write(String text)
	{
		this.write(text.getBytes());
	}
	
	public void write(String[] text)
	{
		for (int i = 0; i < Math.min(text.length, this.lineCount); i++)
		{
			this.write(text[i]);
		}
	}
	
	public void writeln(byte[] text)
	{
		this.write(text);
		this.moveCursorDown();
		this.moveCursorToLeftMostPosition();
	}
	
	public void writeln(String text)
	{
		this.writeln(text.getBytes());
	}
	
	public void writeln(String[] text)
	{
		this.moveCursorHome();
		int k = text.length > this.lineCount ? this.lineCount : text.length;
		for (int i = 0; i < k; i++)
		{
			this.writeln(text[i]);
		}
	}
	
	public void close()
	{
		if (this.useCustomerDisplay)
		{
			try
			{
				if (this.outputStream != null)
				{
					this.outputStream.close();
				}
				if (this.serialPort != null)
				{
					this.serialPort.close();
				}
				this.useCustomerDisplay = false;
			}
			catch (IOException e)
			{
			}
		}
	}
	
	protected int parse(String value, int presetValue)
	{
		int result = 0;
		try
		{
			result = new Integer(value).intValue();
		}
		catch (NumberFormatException e)
		{
			result = presetValue;
		}
		return result;
	}
	
	public void welcome(String[] defaultText)
	{
		this.writeln(defaultText);
	}
	
	public int getLineCount()
	{
		return this.lineCount;
	}
	
	public int getLineLength()
	{
		return this.lineLength;
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
						}
					}
				}
			}
			catch (FileNotFoundException e)
			{
			}
			catch (IOException e)
			{
			}
			finally
			{
			}
		}
	}
	
	public abstract void initialize();
	
	public abstract void clear();
	
	public abstract void setUpperLineMessageScrollContinuously(byte[] text);
	
	public abstract void selectInternationalCharacterSet(int characterSet);
	
	protected abstract void selectInternationalFont(int font);
	
	protected abstract void setDisplayEmulationMode(int mode);
	
	protected abstract void setOverwriteMode();
	
	protected abstract void setVerticalScrollMode();
	
	protected abstract void setHorizontalScrollMode();
	
	protected abstract void setStringDisplayModeAndWrite(int line, byte[] text);
	
	protected abstract void moveCursorLeft();
	
	protected abstract void moveCursorRight();
	
	protected abstract void moveCursorUp();
	
	protected abstract void moveCursorDown();
	
	public abstract void moveCursorHome();
	
	protected abstract void moveCursorToLeftMostPosition();
	
	protected abstract void moveCursorToRightMostPosition();
	
	protected abstract void moveCursorToBottomPosition();
	
	protected abstract void moveCursorToSpecifiedPosition(int x, int y);
	
	protected abstract void reset();
	
	protected abstract void clearCurrentLine();
	
	protected abstract void brightnessAdjustment(int n);
	
	protected abstract void setCursorOnOff(int mode);
	
	protected abstract void selectPeripheralDevice(int device);
	
	protected abstract void setUserDefinedCharacters(int begin, int end, int a);
	
	protected abstract void resetUserDefinedCharacterSet(int n);
	
	protected abstract void cancelUserDefinedCharacters(int n);
	
	protected boolean useCustomerDisplay = false;
	
	protected int baudrate = 9600;
	protected int flowcontrolin = 0;
	protected int flowcontrolout = 0;
	protected int databits = 8;
	protected int stopbits = 1;
	protected int parity = 0;
	
	protected int codetable = 50;
	protected int emulation = 0;
	
	protected int lineCount = 1;
	protected int lineLength = 20;
	
	protected String id = ""; //$NON-NLS-1$
	protected String name = "display"; //$NON-NLS-1$
	protected String alias = "display"; //$NON-NLS-1$
	protected String port;
	
	protected CommPortIdentifier portId = null;
	protected SerialPort serialPort = null;
	protected OutputStream outputStream = null;
	
	protected int[] conversionTable = new int[256];
	protected String charsetTable;
	protected Hashtable converter;
	
}
