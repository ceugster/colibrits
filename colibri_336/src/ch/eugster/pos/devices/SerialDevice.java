package ch.eugster.pos.devices;

import java.io.IOException;
import java.io.OutputStream;
import java.util.TooManyListenersException;

import javax.comm.CommPortIdentifier;
import javax.comm.CommPortOwnershipListener;
import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.comm.UnsupportedCommOperationException;

import ch.eugster.pos.Messages;

/**
 * A class that handles the details of a serial connection. Reads from one
 * TextArea and writes to a second TextArea. Holds the state of the connection.
 */
public class SerialDevice implements SerialPortEventListener, CommPortOwnershipListener
{
	
	/**
	 * Creates a SerialConnection object and initilizes variables passed in as
	 * params.
	 * 
	 * @param parameters
	 *            A SerialParameters object.
	 */
	public SerialDevice(SerialParameters parameters)
	{
		this.parameters = parameters;
		this.open = false;
	}
	
	/**
	 * Attempts to open a serial connection and streams using the parameters in
	 * the SerialParameters object. If it is unsuccesfull at any step it returns
	 * the port to a closed state, throws a
	 * <code>SerialConnectionException</code>, and returns.
	 * 
	 * Gives a timeout of 30 seconds on the portOpen to allow other applications
	 * to reliquish the port if have it open and no longer need it.
	 */
	public void openConnection() throws SerialDeviceException
	{
		
		// Obtain a CommPortIdentifier object for the port you want to open.
		try
		{
			this.portId = CommPortIdentifier.getPortIdentifier(this.parameters.getPortName());
		}
		catch (NoSuchPortException e)
		{
			throw new SerialDeviceException(e.getMessage());
		}
		
		// Open the port represented by the CommPortIdentifier object. Give
		// the open call a relatively long timeout of 30 seconds to allow
		// a different application to reliquish the port if the user
		// wants to.
		try
		{
			this.sPort = (SerialPort) this.portId.open(Messages.getString("SerialDevice.SerialPort_1"), 30000); //$NON-NLS-1$
		}
		catch (PortInUseException e)
		{
			throw new SerialDeviceException(e.getMessage());
		}
		
		// Set the parameters of the connection. If they won't set, close the
		// port before throwing an exception.
		try
		{
			this.setConnectionParameters();
		}
		catch (SerialDeviceException e)
		{
			this.sPort.close();
			throw e;
		}
		
		// Open the input and output streams for the connection. If they won't
		// open, close the port before throwing an exception.
		try
		{
			this.os = this.sPort.getOutputStream();
		}
		catch (IOException e)
		{
			this.sPort.close();
			throw new SerialDeviceException(Messages.getString("SerialDevice.Error_opening_i/o_streams_2")); //$NON-NLS-1$
		}
		
		// Add this object as an event listener for the serial port.
		try
		{
			this.sPort.addEventListener(this);
		}
		catch (TooManyListenersException e)
		{
			this.sPort.close();
			throw new SerialDeviceException(Messages.getString("SerialDevice.too_many_listeners_added_3")); //$NON-NLS-1$
		}
		
		// Set notifyOnDataAvailable to true to allow event driven input.
		this.sPort.notifyOnDataAvailable(true);
		
		// Set notifyOnBreakInterrup to allow event driven break handling.
		this.sPort.notifyOnBreakInterrupt(true);
		
		// Set receive timeout to allow breaking out of polling loop during
		// input handling.
		try
		{
			this.sPort.enableReceiveTimeout(30);
		}
		catch (UnsupportedCommOperationException e)
		{
		}
		
		// Add ownership listener to allow ownership event handling.
		this.portId.addPortOwnershipListener(this);
		
		this.open = true;
	}
	
	/**
	 * Sets the connection parameters to the setting in the parameters object.
	 * If set fails return the parameters object to origional settings and throw
	 * exception.
	 */
	public void setConnectionParameters() throws SerialDeviceException
	{
		
		// Save state of parameters before trying a set.
		int oldBaudRate = this.sPort.getBaudRate();
		int oldDatabits = this.sPort.getDataBits();
		int oldStopbits = this.sPort.getStopBits();
		int oldParity = this.sPort.getParity();
		
		// Set connection parameters, if set fails return parameters object
		// to original state.
		try
		{
			this.sPort.setSerialPortParams(this.parameters.getBaudRate(), this.parameters.getDatabits(),
							this.parameters.getStopbits(), this.parameters.getParity());
		}
		catch (UnsupportedCommOperationException e)
		{
			this.parameters.setBaudRate(oldBaudRate);
			this.parameters.setDatabits(oldDatabits);
			this.parameters.setStopbits(oldStopbits);
			this.parameters.setParity(oldParity);
			throw new SerialDeviceException(Messages.getString("SerialDevice.Unsupported_parameter_4")); //$NON-NLS-1$
		}
		
		// Set flow control.
		try
		{
			this.sPort.setFlowControlMode(this.parameters.getFlowControlIn() | this.parameters.getFlowControlOut());
		}
		catch (UnsupportedCommOperationException e)
		{
			throw new SerialDeviceException(Messages.getString("SerialDevice.Unsupported_flow_control_5")); //$NON-NLS-1$
		}
	}
	
	/**
	 * Close the port and clean up associated elements.
	 */
	public void closeConnection()
	{
		// If port is alread closed just return.
		if (!this.open)
		{
			return;
		}
		
		// Check to make sure sPort has reference to avoid a NPE.
		if (this.sPort != null)
		{
			try
			{
				this.os.close();
			}
			catch (IOException e)
			{
				System.err.println(e);
			}
			
			// Close the port.
			this.sPort.close();
			
			// Remove the ownership listener.
			this.portId.removePortOwnershipListener(this);
		}
		
		this.open = false;
	}
	
	/**
	 * Send a one second break signal.
	 */
	public void sendBreak()
	{
		this.sPort.sendBreak(1000);
	}
	
	/**
	 * Reports the open status of the port.
	 * 
	 * @return true if port is open, false if port is closed.
	 */
	public boolean isOpen()
	{
		return this.open;
	}
	
	/**
	 * Handles SerialPortEvents. The two types of SerialPortEvents that this
	 * program is registered to listen for are DATA_AVAILABLE and BI. During
	 * DATA_AVAILABLE the port buffer is read until it is drained, when no more
	 * data is availble and 30ms has passed the method returns. When a BI event
	 * occurs the words BREAK RECEIVED are written to the messageAreaIn.
	 */
	
	public void serialEvent(SerialPortEvent e)
	{
	}
	
	/**
	 * Handles ownership events. If a PORT_OWNERSHIP_REQUESTED event is received
	 * a dialog box is created asking the user if they are willing to give up
	 * the port. No action is taken on other types of ownership events.
	 */
	public void ownershipChange(int type)
	{
		if (type == CommPortOwnershipListener.PORT_OWNERSHIP_REQUESTED)
		{
		}
	}
	
	private SerialParameters parameters;
	private OutputStream os;
	
	private CommPortIdentifier portId;
	private SerialPort sPort;
	
	private boolean open;
	
}
