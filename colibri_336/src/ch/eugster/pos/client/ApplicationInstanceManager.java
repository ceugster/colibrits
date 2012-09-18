/*
 * Created on 2009 2 8
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class ApplicationInstanceManager
{
	private static ApplicationInstanceListener subListener;
	
	public static final int SINGLE_INSTANCE_NETWORK_SOCKET = 44331;
	
	public static final String SINGLE_INSTANCE_SHARED_KEY = "$$NewInstance$$\n";
	
	public static boolean registerInstance()
	{
		boolean returnValueOnError = true;
		
		try
		{
			final ServerSocket socket = new ServerSocket(
							ApplicationInstanceManager.SINGLE_INSTANCE_NETWORK_SOCKET, 10,
							InetAddress.getLocalHost());
			
			Thread instanceListenerThread = new Thread(new Runnable()
			{
				public void run()
				{
					boolean socketClosed = false;
					while (!socketClosed)
					{
						if (socket.isClosed())
						{
							socketClosed = true;
						}
						else
						{
							try
							{
								Socket client = socket.accept();
								BufferedReader in = new BufferedReader(new InputStreamReader(client
												.getInputStream()));
								String message = in.readLine();
								if (ApplicationInstanceManager.SINGLE_INSTANCE_SHARED_KEY.trim()
												.equals(message.trim()))
								{
									if (ApplicationInstanceManager.subListener != null)
										ApplicationInstanceManager.subListener.newInstanceCreated();
								}
								in.close();
								client.close();
							}
							catch (IOException e)
							{
								socketClosed = true;
							}
						}
					}
				}
			});
			instanceListenerThread.start();
		}
		catch (UnknownHostException e)
		{
			return returnValueOnError;
		}
		catch (IOException e)
		{
			try
			{
				Socket clientSocket = new Socket(InetAddress.getLocalHost(),
								ApplicationInstanceManager.SINGLE_INSTANCE_NETWORK_SOCKET);
				OutputStream out = clientSocket.getOutputStream();
				out.write(ApplicationInstanceManager.SINGLE_INSTANCE_SHARED_KEY.getBytes());
				out.close();
				clientSocket.close();
				return false;
			}
			catch (UnknownHostException e2)
			{
				return returnValueOnError;
			}
			catch (IOException e2)
			{
				return returnValueOnError;
			}
		}
		return true;
	}
	
	public static void setApplicationInstanceListener(ApplicationInstanceListener listener)
	{
		ApplicationInstanceManager.subListener = listener;
	}
}
