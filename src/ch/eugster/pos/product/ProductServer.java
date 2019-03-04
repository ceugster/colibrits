/*
 * Created on 08.09.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.product;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import ch.eugster.pos.db.Setting;
import ch.eugster.pos.events.ShowMessageEvent;
import ch.eugster.pos.events.ShowMessageListener;
import ch.eugster.pos.events.ShutdownListener;
import ch.eugster.pos.events.StateChangeListener;
import ch.eugster.pos.util.Config;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public abstract class ProductServer implements IProductServer, ShutdownListener
{
	
	protected static ProductServer productServer = null;
	protected static String productServerClassName = null;
	protected static boolean isInitialized = false;
	
	protected static boolean doUseProductServer = ProductServer.initialize();
	
	protected boolean active = false;
	protected boolean open = false;
	protected String path = null;
	protected int update = 0;
	protected boolean readCd = false;
	protected String cdPath = null;
	
	protected String message = ""; // 10221
	
	protected ProductServer()
	{
	}
	
	public static ProductServer getInstance()
	{
		if (ProductServer.productServer == null)
		{
			ProductServer.productServer = ProductServer.instantiate();
		}
		return ProductServer.productServer;
	}
	
	public static boolean isInitialized()
	{
		return ProductServer.isInitialized;
	}
	
	public static boolean initialize()
	{
		boolean useServer = Setting.getInstance().getComServer().isUse();
		ProductServer.productServerClassName = Setting.getInstance().getComServer().getClassname();
		
		return useServer;
	}
	
	protected abstract void init();
	
	private static ProductServer instantiate()
	{
		ProductServer server = null;
		try
		{
			Class a = Class.forName(ProductServer.productServerClassName);
			Class[] params = new Class[0];
			// params[0] = String.class;
			Constructor c = a.getDeclaredConstructor(params);
			Object[] p = new Object[0];
			// p[0] = properties;
			server = (ProductServer) c.newInstance(p);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
			return null;
		}
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
			return null;
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
			return null;
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
			return null;
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
			return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		ProductServer.isInitialized = true;
		return server;
	}
	
	public void shutdownPerformed()
	{
		this.finalize();
	}
	
	protected abstract void finalize();
	
	public static boolean accept(String value)
	{
		boolean accept = true;
		if (value.equals("")) { //$NON-NLS-1$
			accept = false;
		}
		else if (!(value.indexOf(".") == -1)) { //$NON-NLS-1$
			accept = false;
		}
		else if (value.length() < 7)
		{
			accept = false;
		}
		return accept;
	}
	
	// 10221
	public String getMessage()
	{
		return this.message;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	// 10221
	
	public static String validateISBN(String isbn)
	{
		// String i = isbn;
		return isbn;
	}
	
	public boolean isActive()
	{
		if (!ProductServer.isInitialized)
		{
			ProductServer.getInstance();
		}
		return this.active;
	}
	
	public abstract boolean isStockManagement();
	
	public int getUpdate()
	{
		if (!ProductServer.isInitialized)
		{
			ProductServer.getInstance();
		}
		return this.update;
	}
	
	public static boolean isUsed()
	{
		return ProductServer.doUseProductServer;
	}
	
	public static void setUsed(boolean use)
	{
		ProductServer.doUseProductServer = use;
	}
	
	public void setActive(boolean active)
	{
		this.active = active;
		ProductServer.fireStateChangePerformed();
	}
	
	public boolean isOpen()
	{
		return this.open;
	}
	
	public String getPath()
	{
		return this.path == null ? "" : this.path;
	}
	
	public boolean isReadCd()
	{
		return this.readCd;
	}
	
	public String getCdPath()
	{
		return this.cdPath;
	}
	
	public boolean testPath()
	{
		return new File(this.path).exists();
	}
	
	public boolean testBibwinIniPath()
	{
		if (Config.getInstance().getGalileoSearchCd())
		{
			return new File(Config.getInstance().getGalileoCdPath()).exists();
		}
		return true;
	}
	
	public static boolean addStateChangeListener(StateChangeListener listener)
	{
		return ProductServer.stateChangeListeners.add(listener);
	}
	
	public static boolean removeStateChangeListener(StateChangeListener listener)
	{
		return ProductServer.stateChangeListeners.remove(listener);
	}
	
	protected static void fireStateChangePerformed()
	{
		StateChangeListener[] listeners = (StateChangeListener[]) ProductServer.stateChangeListeners.toArray(new StateChangeListener[0]);
		for (int i = 0; i < listeners.length; i++)
		{
			listeners[i].updateStates();
		}
	}
	
	public static void setMessageListener(ShowMessageListener listener)
	{
		ProductServer.messageListener = listener;
	}
	
	public static int sendMessage(ShowMessageEvent event)
	{
		if (ProductServer.messageListener != null)
		{
			return ProductServer.messageListener.showMessage(event);
		}
		else
		{
			return 0;
		}
	}
	
	private static ShowMessageListener messageListener;
	private static ArrayList stateChangeListeners = new ArrayList();
}
