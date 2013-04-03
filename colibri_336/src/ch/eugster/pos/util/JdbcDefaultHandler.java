/*
 * Created on 23.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.output.XMLOutputter;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class JdbcDefaultHandler
{
	
	protected JdbcDefaultHandler()
	{
		this(Path.getInstance().jcdFile);
	}
	
	protected JdbcDefaultHandler(String path)
	{
		this(new File(path));
	}
	
	protected JdbcDefaultHandler(File file)
	{
		this.init(file);
	}
	
	protected void init(File file)
	{
		JdbcDefaultHandler.jdbcDefaultHandler = this;
		try
		{
			JdbcDefaultHandler.doc = XMLLoader.getDocument(file, true);
		}
		catch (JDOMException e)
		{
			//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			System.exit(-18);
		}
		catch (IOException e)
		{
			//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			System.exit(-18);
		}
	}
	
	public static JdbcDefaultHandler getJdbcDefaultHandler()
	{
		if (JdbcDefaultHandler.jdbcDefaultHandler == null)
		{
			//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Looking for path of " + Path.getInstance().jcdFile + "..."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			JdbcDefaultHandler.jdbcDefaults = new File(Path.getInstance().jcdFile);
			//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Loading file " + Path.getInstance().jcdFile + "..."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			JdbcDefaultHandler.jdbcDefaultHandler = JdbcDefaultHandler
							.loadJdbcDefaultHandler(JdbcDefaultHandler.jdbcDefaults);
		}
		return JdbcDefaultHandler.jdbcDefaultHandler;
	}
	
	protected static JdbcDefaultHandler loadJdbcDefaultHandler(String fileName)
	{
		JdbcDefaultHandler c = null;
		File file = new File(fileName);
		//		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Testing if file " + file.getName() + " exists..."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if (file.exists())
		{
			//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(file.getName() + " exists."); //$NON-NLS-1$ //$NON-NLS-2$
			c = JdbcDefaultHandler.loadJdbcDefaultHandler(file);
		}
		return c;
	}
	
	protected static JdbcDefaultHandler loadJdbcDefaultHandler(File file)
	{
		//		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Loading file " + file.getAbsolutePath() + "..."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return new JdbcDefaultHandler(file);
	}
	
	public Document getJdbcDefaults()
	{
		return JdbcDefaultHandler.doc;
	}
	
	public void saveJdbcDefaults()
	{
		this.saveJdbcDefaults(JdbcDefaultHandler.doc);
	}
	
	public void saveJdbcDefaults(Document doc)
	{
		OutputStream out = null;
		try
		{
			out = new FileOutputStream(JdbcDefaultHandler.jdbcDefaults);
			XMLOutputter xmlOut = new XMLOutputter();
			xmlOut.output(doc, out);
		}
		catch (FileNotFoundException e)
		{
			
		}
		catch (IOException e)
		{
			
		}
	}
	
	public void close()
	{
		JdbcDefaultHandler.doc = null;
	}
	
	public void dispose()
	{
		JdbcDefaultHandler.doc = null;
	}
	
	protected static File jdbcDefaults;
	protected static JdbcDefaultHandler jdbcDefaultHandler = null;
	protected static Document doc = null;
}
