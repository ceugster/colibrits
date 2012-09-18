/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package ch.eugster.pos.admin.preference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.ListenerList;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.RGB;
import org.jdom.Element;

import ch.eugster.pos.db.Setting;
import ch.eugster.pos.util.Config;

/**
 * A concrete preference store implementation based on an internal
 * <code>java.util.Properties</code> object, with support for persisting the
 * non-default preference values to files or streams.
 * <p>
 * This class was not designed to be subclassed.
 * </p>
 * 
 * @see IPreferenceStore
 */
public class PreferenceStore implements IPersistentPreferenceStore
{
	
	/**
	 * List of registered listeners (element type:
	 * <code>IPropertyChangeListener</code>). These listeners are to be informed
	 * when the current value of a preference changes.
	 */
	private ListenerList listeners = new ListenerList();
	
	/**
	 * The mapping from preference name to preference value (represented as
	 * strings).
	 */
	private Properties properties;
	
	/**
	 * The mapping from preference name to default preference value (represented
	 * as strings); <code>null</code> if none.
	 */
	private Properties defaultProperties;
	
	/**
	 * Indicates whether a value as been changed by <code>setToDefault</code> or
	 * <code>setValue</code>; initially <code>false</code>.
	 */
	private boolean dirty = false;
	
	/**
	 * Creates an empty preference store.
	 * <p>
	 * Use the methods <code>load(InputStream)</code> and
	 * <code>save(InputStream)</code> to load and store this preference store.
	 * </p>
	 * 
	 * @see #load(InputStream)
	 * @see #save(OutputStream, String)
	 */
	public PreferenceStore()
	{
		this.defaultProperties = new Properties();
		this.properties = new Properties(this.defaultProperties);
		this.load();
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public void addPropertyChangeListener(IPropertyChangeListener listener)
	{
		this.listeners.add(listener);
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public boolean contains(String name)
	{
		return this.properties.containsKey(name) || this.defaultProperties.containsKey(name);
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public void firePropertyChangeEvent(String name, Object oldValue, Object newValue)
	{
		final Object[] listeners = this.listeners.getListeners();
		// Do we need to fire an event.
		if (listeners.length > 0 && (oldValue == null || !oldValue.equals(newValue)))
		{
			final PropertyChangeEvent pe = new PropertyChangeEvent(this, name, oldValue, newValue);
			
			// FIXME: need to do this without dependency on
			// org.eclipse.core.runtime
			//		Platform.run(new SafeRunnable(JFaceResources.getString("PreferenceStore.changeError")) { //$NON-NLS-1$
			// public void run() {
			for (int i = 0; i < listeners.length; ++i)
			{
				IPropertyChangeListener l = (IPropertyChangeListener) listeners[i];
				l.propertyChange(pe);
			}
			// }
			// });
		}
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public boolean getBoolean(String name)
	{
		return this.getBoolean(this.properties, name);
	}
	
	/**
	 * Helper function: gets boolean for a given name.
	 */
	private boolean getBoolean(Properties p, String name)
	{
		String value = p != null ? p.getProperty(name) : null;
		if (value == null) return IPreferenceStore.BOOLEAN_DEFAULT_DEFAULT;
		if (value.equals(IPreferenceStore.TRUE)) return true;
		return false;
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public boolean getDefaultBoolean(String name)
	{
		return this.getBoolean(this.defaultProperties, name);
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public double getDefaultDouble(String name)
	{
		return this.getDouble(this.defaultProperties, name);
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public float getDefaultFloat(String name)
	{
		return this.getFloat(this.defaultProperties, name);
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public int getDefaultInt(String name)
	{
		return this.getInt(this.defaultProperties, name);
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public long getDefaultLong(String name)
	{
		return this.getLong(this.defaultProperties, name);
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public String getDefaultString(String name)
	{
		return this.getString(this.defaultProperties, name);
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public double getDouble(String name)
	{
		return this.getDouble(this.properties, name);
	}
	
	/**
	 * Helper function: gets double for a given name.
	 */
	private double getDouble(Properties p, String name)
	{
		String value = p != null ? p.getProperty(name) : null;
		if (value == null) return IPreferenceStore.DOUBLE_DEFAULT_DEFAULT;
		double ival = IPreferenceStore.DOUBLE_DEFAULT_DEFAULT;
		try
		{
			ival = new Double(value).doubleValue();
		}
		catch (NumberFormatException e)
		{
		}
		return ival;
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public float getFloat(String name)
	{
		return this.getFloat(this.properties, name);
	}
	
	/**
	 * Helper function: gets float for a given name.
	 */
	private float getFloat(Properties p, String name)
	{
		String value = p != null ? p.getProperty(name) : null;
		if (value == null) return IPreferenceStore.FLOAT_DEFAULT_DEFAULT;
		float ival = IPreferenceStore.FLOAT_DEFAULT_DEFAULT;
		try
		{
			ival = new Float(value).floatValue();
		}
		catch (NumberFormatException e)
		{
		}
		return ival;
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public int getInt(String name)
	{
		return this.getInt(this.properties, name);
	}
	
	/**
	 * Helper function: gets int for a given name.
	 */
	private int getInt(Properties p, String name)
	{
		String value = p != null ? p.getProperty(name) : null;
		if (value == null) return IPreferenceStore.INT_DEFAULT_DEFAULT;
		int ival = 0;
		try
		{
			ival = Integer.parseInt(value);
		}
		catch (NumberFormatException e)
		{
		}
		return ival;
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public long getLong(String name)
	{
		return this.getLong(this.properties, name);
	}
	
	/**
	 * Helper function: gets long for a given name.
	 */
	private long getLong(Properties p, String name)
	{
		String value = p != null ? p.getProperty(name) : null;
		if (value == null) return IPreferenceStore.LONG_DEFAULT_DEFAULT;
		long ival = IPreferenceStore.LONG_DEFAULT_DEFAULT;
		try
		{
			ival = Long.parseLong(value);
		}
		catch (NumberFormatException e)
		{
		}
		return ival;
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public String getString(String name)
	{
		return this.getString(this.properties, name);
	}
	
	/**
	 * Helper function: gets string for a given name.
	 */
	private String getString(Properties p, String name)
	{
		String value = p != null ? p.getProperty(name) : null;
		if (value == null) return IPreferenceStore.STRING_DEFAULT_DEFAULT;
		return value;
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public boolean isDefault(String name)
	{
		return !this.properties.containsKey(name) && this.defaultProperties.containsKey(name);
	}
	
	/**
	 * Prints the contents of this preference store to the given print stream.
	 * 
	 * @param out
	 *            the print stream
	 */
	public void list(PrintStream out)
	{
		this.properties.list(out);
	}
	
	/**
	 * Prints the contents of this preference store to the given print writer.
	 * 
	 * @param out
	 *            the print writer
	 */
	public void list(PrintWriter out)
	{
		this.properties.list(out);
	}
	
	/**
	 * Loads this preference store from the file established in the constructor
	 * <code>PreferenceStore(java.lang.String)</code> (or by
	 * <code>setFileName</code>). Default preference values are not affected.
	 * 
	 * @exception java.io.IOException
	 *                if there is a problem loading this store
	 */
	public void load()
	{
		Config cfg = Config.getInstance();
		
		this.setDefault("logging.level", cfg.getLoggingLevel()); //$NON-NLS-1$
		this.setDefault("logging.max", cfg.getLoggingMax()); //$NON-NLS-1$
		this.setDefault("logging.trace", cfg.getLoggingTrace()); //$NON-NLS-1$
		// 10156
		this.setDefault("logging.receipts", cfg.isLoggingReceipts()); //$NON-NLS-1$
		// 10156
		// 10158
		this.setDefault("logging.compress", cfg.isLoggingCompress()); //$NON-NLS-1$
		// 10158
		
		this.setDefault("database.default", cfg.getDatabaseDefault()); //$NON-NLS-1$
		
		RGB rgb = cfg.getTabPanelRGBBackground();
		this.setDefault("database.standard.name", cfg.getDatabaseStandardName()); //$NON-NLS-1$
		this.setDefault("database.standard.active", cfg.getDatabaseStandardActive()); //$NON-NLS-1$
		this
						.setDefault(
										"database.standard.connection.jcd-alias", cfg.getDatabaseStandardConnection().getAttributeValue("jcd-alias")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.standard.connection.default-connection", cfg.getDatabaseStandardConnection().getAttributeValue("default-connection")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.standard.connection.platform", cfg.getDatabaseStandardConnection().getAttributeValue("platform")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.standard.connection.jdbc-level", cfg.getDatabaseStandardConnection().getAttributeValue("jdbc-level")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.standard.connection.driver", cfg.getDatabaseStandardConnection().getAttributeValue("driver")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.standard.connection.protocol", cfg.getDatabaseStandardConnection().getAttributeValue("protocol")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.standard.connection.subprotocol", cfg.getDatabaseStandardConnection().getAttributeValue("subprotocol")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.standard.connection.host", cfg.getDatabaseStandardConnection().getAttributeValue("host")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.standard.connection.port", cfg.getDatabaseStandardConnection().getAttributeValue("port")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.standard.connection.database", cfg.getDatabaseStandardConnection().getAttributeValue("database")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.standard.connection.options", cfg.getDatabaseStandardConnection().getAttributeValue("options")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.standard.connection.username", cfg.getDatabaseStandardConnection().getAttributeValue("username")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.standard.connection.password", cfg.getDatabaseStandardConnection().getAttributeValue("password")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.standard.connection.batch-mode", cfg.getDatabaseStandardConnection().getAttributeValue("batch-mode")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.standard.connection.use-auto-commit", cfg.getDatabaseStandardConnection().getAttributeValue("use-auto-commit")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.setDefault("database.temporary.name", cfg.getDatabaseTemporaryName()); //$NON-NLS-1$
		this.setDefault("database.temporary.active", cfg.getDatabaseTemporaryActive()); //$NON-NLS-1$
		this
						.setDefault(
										"database.temporary.connection.jcd-alias", cfg.getDatabaseTemporaryConnection().getAttributeValue("jcd-alias")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.temporary.connection.default-connection", cfg.getDatabaseTemporaryConnection().getAttributeValue("default-connection")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.temporary.connection.platform", cfg.getDatabaseTemporaryConnection().getAttributeValue("platform")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.temporary.connection.jdbc-level", cfg.getDatabaseTemporaryConnection().getAttributeValue("jdbc-level")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.temporary.connection.driver", cfg.getDatabaseTemporaryConnection().getAttributeValue("driver")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.temporary.connection.protocol", cfg.getDatabaseTemporaryConnection().getAttributeValue("protocol")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.temporary.connection.subprotocol", cfg.getDatabaseTemporaryConnection().getAttributeValue("subprotocol")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.temporary.connection.host", cfg.getDatabaseTemporaryConnection().getAttributeValue("host")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.temporary.connection.port", cfg.getDatabaseTemporaryConnection().getAttributeValue("port")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.temporary.connection.database", cfg.getDatabaseTemporaryConnection().getAttributeValue("database")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.temporary.connection.options", cfg.getDatabaseTemporaryConnection().getAttributeValue("options")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.temporary.connection.username", cfg.getDatabaseTemporaryConnection().getAttributeValue("username")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.temporary.connection.password", cfg.getDatabaseTemporaryConnection().getAttributeValue("password")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.temporary.connection.batch-mode", cfg.getDatabaseTemporaryConnection().getAttributeValue("batch-mode")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.temporary.connection.use-auto-commit", cfg.getDatabaseTemporaryConnection().getAttributeValue("use-auto-commit")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.setDefault("database.tutorial.name", cfg.getDatabaseTutorialName()); //$NON-NLS-1$
		this.setDefault("database.tutorial.active", cfg.getDatabaseTutorialActive()); //$NON-NLS-1$
		this
						.setDefault(
										"database.tutorial.connection.jcd-alias", cfg.getDatabaseTutorialConnection().getAttributeValue("jcd-alias")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.tutorial.connection.default-connection", cfg.getDatabaseTutorialConnection().getAttributeValue("default-connection")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.tutorial.connection.platform", cfg.getDatabaseTutorialConnection().getAttributeValue("platform")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.tutorial.connection.jdbc-level", cfg.getDatabaseTutorialConnection().getAttributeValue("jdbc-level")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.tutorial.connection.driver", cfg.getDatabaseTutorialConnection().getAttributeValue("driver")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.tutorial.connection.protocol", cfg.getDatabaseTutorialConnection().getAttributeValue("protocol")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.tutorial.connection.subprotocol", cfg.getDatabaseTutorialConnection().getAttributeValue("subprotocol")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.tutorial.connection.host", cfg.getDatabaseTutorialConnection().getAttributeValue("host")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.tutorial.connection.port", cfg.getDatabaseTutorialConnection().getAttributeValue("port")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.tutorial.connection.database", cfg.getDatabaseTutorialConnection().getAttributeValue("database")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.tutorial.connection.options", cfg.getDatabaseTutorialConnection().getAttributeValue("options")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.tutorial.connection.username", cfg.getDatabaseTutorialConnection().getAttributeValue("username")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.tutorial.connection.password", cfg.getDatabaseTutorialConnection().getAttributeValue("password")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.tutorial.connection.batch-mode", cfg.getDatabaseTutorialConnection().getAttributeValue("batch-mode")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"database.tutorial.connection.use-auto-commit", cfg.getDatabaseTutorialConnection().getAttributeValue("use-auto-commit")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.setDefault("salespoint.id", cfg.getSalespointId()); //$NON-NLS-1$
		this.setDefault("salespoint.force-settlement", cfg.getSalespointForceSettlement()); //$NON-NLS-1$
		this.setDefault("salespoint.export", cfg.getSalespointExport()); //$NON-NLS-1$
		this.setDefault("salespoint.export.path", cfg.getSalespointExportPath()); //$NON-NLS-1$
		
		this.setDefault("input-default.quantity", cfg.getInputDefaultQuantity()); //$NON-NLS-1$
		this.setDefault("input-default.tax", cfg.getInputDefaultTax()); //$NON-NLS-1$
		this.setDefault("input-default.option", cfg.getInputDefaultOption()); //$NON-NLS-1$
		this.setDefault("input-default.max-quantity-range", cfg.getInputDefaultMaxQuantityRange()); //$NON-NLS-1$
		this.setDefault("input-default.max-quantity-amount", cfg.getInputDefaultMaxQuantityAmount()); //$NON-NLS-1$
		this.setDefault("input-default.max-price-range", cfg.getInputDefaultMaxPriceRange()); //$NON-NLS-1$
		this.setDefault("input-default.max-price-amount", cfg.getInputDefaultMaxPriceAmount()); //$NON-NLS-1$
		this.setDefault("input-default.max-payment-range", cfg.getInputDefaultMaxPaymentRange()); //$NON-NLS-1$
		this.setDefault("input-default.max-payment-amount", cfg.getInputDefaultMaxPaymentAmount()); //$NON-NLS-1$
		this.setDefault("input-default.clear-price", cfg.getInputDefaultClearPrice()); //$NON-NLS-1$
		
		this.setDefault("com-server.use", cfg.getProductServerUse()); //$NON-NLS-1$ 
		this.setDefault("com-server.class", cfg.getProductServerClass()); //$NON-NLS-1$ 
		this.setDefault("com-server.hold", cfg.getProductServerHold()); //$NON-NLS-1$ 
		
		this.setDefault("galileo.update", cfg.getGalileoUpdate()); //$NON-NLS-1$
		this.setDefault("galileo.path", cfg.getGalileoPath()); //$NON-NLS-1$
		this.setDefault("galileo.show-add-customer-message", cfg.getGalileoShowAddCustomerMessage()); //$NON-NLS-1$
		this.setDefault("galileo.search-cd", cfg.getGalileoSearchCd()); //$NON-NLS-1$ 
		this.setDefault("galileo.cd-path", cfg.getGalileoCdPath()); //$NON-NLS-1$ 
		
		this.setDefault("layout.left", cfg.getLayoutLeftWidth()); //$NON-NLS-1$
		this.setDefault("layout.total-block", cfg.getLayoutTotalBlock()); //$NON-NLS-1$
		this.setDefault("layout.top-left", cfg.getLayoutTopLeft()); //$NON-NLS-1$
		this.setDefault("layout.top-right", cfg.getLayoutTopRight()); //$NON-NLS-1$
		this.setDefault("layout.bottom-left", cfg.getLayoutBottomLeft()); //$NON-NLS-1$
		this.setDefault("layout.bottom-right", cfg.getLayoutBottomRight()); //$NON-NLS-1$
		
		this.setDefault("locale.language", cfg.getLocaleLanguage()); //$NON-NLS-1$
		this.setDefault("locale.country", cfg.getLocaleCountry()); //$NON-NLS-1$
		
		this.setDefault("currency.default", cfg.getCurrencyDefault()); //$NON-NLS-1$
		this.setDefault("currency.roundfactor.amount", cfg.getCurrencyRoundFactorAmount()); //$NON-NLS-1$
		this.setDefault("currency.roundfactor.tax", cfg.getCurrencyRoundFactorTax()); //$NON-NLS-1$
		
		this.setDefault("look-and-feel", cfg.getLookAndFeelClass()); //$NON-NLS-1$
		
		Element font = cfg.getTabPanelFont();
		this.setDefault("tab-panel.font.name", font.getAttributeValue("name")); //$NON-NLS-1$ //$NON-NLS-2$
		this.setDefault("tab-panel.font.size", font.getAttributeValue("size")); //$NON-NLS-1$ //$NON-NLS-2$
		this.setDefault("tab-panel.font.style", font.getAttributeValue("style")); //$NON-NLS-1$ //$NON-NLS-2$
		rgb = cfg.getTabPanelRGBForeground();
		this.setDefault("tab-panel.fgcolor.red", rgb.red); //$NON-NLS-1$
		this.setDefault("tab-panel.fgcolor.green", rgb.green); //$NON-NLS-1$
		this.setDefault("tab-panel.fgcolor.blue", rgb.blue); //$NON-NLS-1$
		rgb = cfg.getTabPanelRGBBackground();
		this.setDefault("tab-panel.bgcolor.red", rgb.red); //$NON-NLS-1$
		this.setDefault("tab-panel.bgcolor.green", rgb.green); //$NON-NLS-1$
		this.setDefault("tab-panel.bgcolor.blue", rgb.blue); //$NON-NLS-1$
		
		this.setDefault("total-block.show-always", cfg.getTotalBlock().getAttributeValue("show-always")); //$NON-NLS-1$ //$NON-NLS-2$
		this.setDefault("total-block.hold-values", cfg.getTotalBlock().getAttributeValue("hold-values")); //$NON-NLS-1$ //$NON-NLS-2$
		font = cfg.getTotalBlockFont();
		this.setDefault("total-block.font.name", font.getAttributeValue("name")); //$NON-NLS-1$ //$NON-NLS-2$
		this.setDefault("total-block.font.size", font.getAttributeValue("size")); //$NON-NLS-1$ //$NON-NLS-2$
		this.setDefault("total-block.font.style", font.getAttributeValue("style")); //$NON-NLS-1$ //$NON-NLS-2$
		rgb = cfg.getTotalBlockRGBForeground();
		this.setDefault("total-block.fgcolor.red", rgb.red); //$NON-NLS-1$
		this.setDefault("total-block.fgcolor.green", rgb.green); //$NON-NLS-1$
		this.setDefault("total-block.fgcolor.blue", rgb.blue); //$NON-NLS-1$
		rgb = cfg.getTotalBlockRGBBackground();
		this.setDefault("total-block.bgcolor.red", rgb.red); //$NON-NLS-1$
		this.setDefault("total-block.bgcolor.green", rgb.green); //$NON-NLS-1$
		this.setDefault("total-block.bgcolor.blue", rgb.blue); //$NON-NLS-1$
		
		font = cfg.getDetailBlockListFont();
		this.setDefault("detail-block-list.font.name", font.getAttributeValue("name")); //$NON-NLS-1$ //$NON-NLS-2$
		this.setDefault("detail-block-list.font.size", font.getAttributeValue("size")); //$NON-NLS-1$ //$NON-NLS-2$
		this.setDefault("detail-block-list.font.style", font.getAttributeValue("style")); //$NON-NLS-1$ //$NON-NLS-2$
		rgb = cfg.getDetailBlockListNormalRGBForeground();
		this.setDefault("detail-block-list.normal-color.fgcolor.red", rgb.red); //$NON-NLS-1$
		this.setDefault("detail-block-list.normal-color.fgcolor.green", rgb.green); //$NON-NLS-1$
		this.setDefault("detail-block-list.normal-color.fgcolor.blue", rgb.blue); //$NON-NLS-1$
		rgb = cfg.getDetailBlockListBackRGBForeground();
		this.setDefault("detail-block-list.back-color.fgcolor.red", rgb.red); //$NON-NLS-1$
		this.setDefault("detail-block-list.back-color.fgcolor.green", rgb.green); //$NON-NLS-1$
		this.setDefault("detail-block-list.back-color.fgcolor.blue", rgb.blue); //$NON-NLS-1$
		rgb = cfg.getDetailBlockListExpenseRGBForeground();
		this.setDefault("detail-block-list.expense-color.fgcolor.red", rgb.red); //$NON-NLS-1$
		this.setDefault("detail-block-list.expense-color.fgcolor.green", rgb.green); //$NON-NLS-1$
		this.setDefault("detail-block-list.expense-color.fgcolor.blue", rgb.blue); //$NON-NLS-1$
		
		font = cfg.getDetailBlockFont();
		this.setDefault("detail-block.font.name", font.getAttributeValue("name")); //$NON-NLS-1$ //$NON-NLS-2$
		this.setDefault("detail-block.font.size", font.getAttributeValue("size")); //$NON-NLS-1$ //$NON-NLS-2$
		this.setDefault("detail-block.font.style", font.getAttributeValue("style")); //$NON-NLS-1$ //$NON-NLS-2$
		
		rgb = cfg.getDetailBlockRGBForeground();
		this.setDefault("detail-block.fgcolor.red", rgb.red); //$NON-NLS-1$
		this.setDefault("detail-block.fgcolor.green", rgb.green); //$NON-NLS-1$
		this.setDefault("detail-block.fgcolor.blue", rgb.blue); //$NON-NLS-1$
		
		rgb = cfg.getDetailBlockRGBBackground();
		this.setDefault("detail-block.bgcolor.red", rgb.red); //$NON-NLS-1$
		this.setDefault("detail-block.bgcolor.green", rgb.green); //$NON-NLS-1$
		this.setDefault("detail-block.bgcolor.blue", rgb.blue); //$NON-NLS-1$
		
		font = cfg.getDisplayFont();
		this.setDefault("display.font.name", font.getAttributeValue("name")); //$NON-NLS-1$ //$NON-NLS-2$
		this.setDefault("display.font.size", font.getAttributeValue("size")); //$NON-NLS-1$ //$NON-NLS-2$
		this.setDefault("display.font.style", font.getAttributeValue("style")); //$NON-NLS-1$ //$NON-NLS-2$
		rgb = cfg.getDisplayRGBForeground();
		this.setDefault("display.fgcolor.red", rgb.red); //$NON-NLS-1$
		this.setDefault("display.fgcolor.green", rgb.green); //$NON-NLS-1$
		this.setDefault("display.fgcolor.blue", rgb.blue); //$NON-NLS-1$
		rgb = cfg.getDisplayRGBBackground();
		this.setDefault("display.bgcolor.red", rgb.red); //$NON-NLS-1$
		this.setDefault("display.bgcolor.green", rgb.green); //$NON-NLS-1$
		this.setDefault("display.bgcolor.blue", rgb.blue); //$NON-NLS-1$
		
		this.setDefault("receipt.automatic-print", cfg.getReceipt().getAttributeValue("automatic-print")); //$NON-NLS-1$
		this.setDefault("receipt.take-back-print-twice", cfg.getReceipt().getAttributeValue("take-back-print-twice")); //$NON-NLS-1$
		this
						.setDefault(
										"receipt.take-back-print-signature", cfg.getReceipt().getAttributeValue("take-back-print-signature")); //$NON-NLS-1$
		this.setDefault("receipt.header.text", cfg.getReceiptHeaderText()); //$NON-NLS-1$
		this.setDefault("receipt.header.number.length", cfg.getReceiptHeaderNumberLength()); //$NON-NLS-1$
		this.setDefault("receipt.header.row.col.align", cfg.getReceiptHeaderTextAlign()); //$NON-NLS-1$
		this.setDefault("receipt.footer.text", cfg.getReceiptFooterText()); //$NON-NLS-1$
		this.setDefault("receipt.footer.row.col.align", cfg.getReceiptFooterTextAlign()); //$NON-NLS-1$
		this.setDefault("receipt.footer.print.user", cfg.getReceiptFooterPrintUser()); //$NON-NLS-1$
		// 10157
		this.setDefault("receipt.customer.text", cfg.getReceiptCustomerText()); //$NON-NLS-1$
		this.setDefault("receipt.customer.row.col.align", cfg.getReceiptCustomerTextAlign()); //$NON-NLS-1$
		// 10157
		List children = cfg.getReceiptPosition().getChildren("row");
		Iterator rows = children.iterator();
		while (rows.hasNext())
		{
			Element row = (Element) rows.next();
			List columns = row.getChildren("col");
			Iterator cols = columns.iterator();
			while (cols.hasNext())
			{
				Element col = (Element) cols.next();
				if (col.getText().equals("position.galileoid") || col.getText().equals("position.productid")
								|| col.getText().equals("position.productgroup"))
				{
					this.setDefault("receipt.position.row.col.item-name", col.getText());
				}
			}
		}
		
		Element printer = cfg.getPosPrinter();
		String id = printer.getAttributeValue("id"); //$NON-NLS-1$
		this
						.setDefault(
										"periphery.pos-printer." + id + ".use", new Boolean(printer.getAttributeValue("use")).booleanValue()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.setDefault("periphery.pos-printer." + id + ".name", printer.getAttributeValue("name")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.setDefault("periphery.pos-printer." + id + ".class", printer.getAttributeValue("class")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		Element device = printer.getChild("device"); //$NON-NLS-1$
		this
						.setDefault(
										"periphery.pos-printer." + id + "." + device.getName() + ".port", device.getAttributeValue("port")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		this
						.setDefault(
										"periphery.pos-printer." + id + "." + device.getName() + ".alias", device.getAttributeValue("alias")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		this
						.setDefault(
										"periphery.pos-printer." + id + "." + device.getName() + ".charset", device.getAttributeValue("charset")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		this
						.setDefault(
										"periphery.pos-printer." + id + "." + device.getName() + ".characterset", Integer.valueOf(device.getAttributeValue("charactertable")).intValue()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		Element serial = device.getChild("serial"); //$NON-NLS-1$
		this
						.setDefault(
										"periphery.pos-printer." + id + "." + device.getName() + "." + serial.getName() + ".baudrate", Integer.valueOf(serial.getAttributeValue("baudrate")).intValue()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		this
						.setDefault(
										"periphery.pos-printer." + id + "." + device.getName() + "." + serial.getName() + ".flowcontrolin", Integer.valueOf(serial.getAttributeValue("flowcontrolin")).intValue()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		this
						.setDefault(
										"periphery.pos-printer." + id + "." + device.getName() + "." + serial.getName() + ".flowcontrolout", Integer.valueOf(serial.getAttributeValue("flowcontrolout")).intValue()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		this
						.setDefault(
										"periphery.pos-printer." + id + "." + device.getName() + "." + serial.getName() + ".databits", Integer.valueOf(serial.getAttributeValue("databits")).intValue()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		this
						.setDefault(
										"periphery.pos-printer." + id + "." + device.getName() + "." + serial.getName() + ".stopbits", Integer.valueOf(serial.getAttributeValue("stopbits")).intValue()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		this
						.setDefault(
										"periphery.pos-printer." + id + "." + device.getName() + "." + serial.getName() + ".parity", Integer.valueOf(serial.getAttributeValue("parity")).intValue()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		List cashdrawers = printer.getChildren("cashdrawer"); //$NON-NLS-1$
		for (Iterator j = cashdrawers.iterator(); j.hasNext();)
		{
			Element cashdrawer = (Element) j.next();
			String cashdrawerId = cashdrawer.getAttributeValue("id"); //$NON-NLS-1$
			this
							.setDefault(
											"periphery.pos-printer." + id + ".cashdrawer." + cashdrawerId + ".use", new Boolean(cashdrawer.getAttributeValue("use")).booleanValue()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			this
							.setDefault(
											"periphery.pos-printer." + id + ".cashdrawer." + cashdrawerId + ".pin", Integer.valueOf(cashdrawer.getAttributeValue("pin")).intValue()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			this
							.setDefault(
											"periphery.pos-printer." + id + ".cashdrawer." + cashdrawerId + ".pulseon", Integer.valueOf(cashdrawer.getAttributeValue("pulseon")).intValue()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			this
							.setDefault(
											"periphery.pos-printer." + id + ".cashdrawer." + cashdrawerId + ".pulseoff", Integer.valueOf(cashdrawer.getAttributeValue("pulseoff")).intValue()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			this
							.setDefault(
											"periphery.pos-printer." + id + ".cashdrawer." + cashdrawerId + ".currency", cashdrawer.getAttributeValue("currency")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
		
		Element customerDisplay = cfg.getCustomerDisplay();
		id = customerDisplay.getAttributeValue("id"); //$NON-NLS-1$
		this
						.setDefault(
										"periphery.customer-display." + id + ".use", new Boolean(customerDisplay.getAttributeValue("use")).booleanValue()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.setDefault("periphery.customer-display." + id + ".name", customerDisplay.getAttributeValue("name")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.setDefault("periphery.customer-display." + id + ".class", customerDisplay.getAttributeValue("class")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this
						.setDefault(
										"periphery.customer-display." + id + ".emulation", Integer.valueOf(customerDisplay.getAttributeValue("emulation")).intValue()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this
						.setDefault(
										"periphery.customer-display." + id + ".line-count", Integer.valueOf(customerDisplay.getAttributeValue("line-count")).intValue()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this
						.setDefault(
										"periphery.customer-display." + id + ".line-length", Integer.valueOf(customerDisplay.getAttributeValue("line-length")).intValue()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		device = customerDisplay.getChild("device"); //$NON-NLS-1$
		this
						.setDefault(
										"periphery.customer-display." + id + "." + device.getName() + ".port", device.getAttributeValue("port")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		this
						.setDefault(
										"periphery.customer-display." + id + "." + device.getName() + ".alias", device.getAttributeValue("alias")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		this
						.setDefault(
										"periphery.customer-display." + id + "." + device.getName() + ".charset", device.getAttributeValue("charset")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		this
						.setDefault(
										"periphery.customer-display." + id + "." + device.getName() + ".characterset", Integer.valueOf(device.getAttributeValue("charactertable")).intValue()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		serial = device.getChild("serial"); //$NON-NLS-1$
		this
						.setDefault(
										"periphery.customer-display." + id + "." + device.getName() + "." + serial.getName() + ".baudrate", Integer.valueOf(serial.getAttributeValue("baudrate")).intValue()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		this
						.setDefault(
										"periphery.customer-display." + id + "." + device.getName() + "." + serial.getName() + ".flowcontrolin", Integer.valueOf(serial.getAttributeValue("flowcontrolin")).intValue()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		this
						.setDefault(
										"periphery.customer-display." + id + "." + device.getName() + "." + serial.getName() + ".flowcontrolout", Integer.valueOf(serial.getAttributeValue("flowcontrolout")).intValue()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		this
						.setDefault(
										"periphery.customer-display." + id + "." + device.getName() + "." + serial.getName() + ".databits", Integer.valueOf(serial.getAttributeValue("databits")).intValue()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		this
						.setDefault(
										"periphery.customer-display." + id + "." + device.getName() + "." + serial.getName() + ".stopbits", Integer.valueOf(serial.getAttributeValue("stopbits")).intValue()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		this
						.setDefault(
										"periphery.customer-display." + id + "." + device.getName() + "." + serial.getName() + ".parity", Integer.valueOf(serial.getAttributeValue("parity")).intValue()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		
		Element welcome = cfg.getCustomerDisplayTextElement("welcome"); //$NON-NLS-1$
		if (welcome == null)
		{
			welcome = new Element("text"); //$NON-NLS-1$
			welcome.setAttribute("id", "welcome"); //$NON-NLS-1$ //$NON-NLS-2$
			cfg.getCustomerDisplay().addContent(welcome);
		}
		this.setDefault("customer-display.welcome-text", welcome.getText()); //$NON-NLS-1$
		this.setDefault("customer-display.welcome-text.scroll", welcome.getAttributeValue("scroll")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"customer-display.timer", new Boolean(customerDisplay.getAttributeValue("timer")).booleanValue()); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"customer-display.seconds", Integer.valueOf(customerDisplay.getAttributeValue("seconds")).intValue()); //$NON-NLS-1$ //$NON-NLS-2$
		
		Element closed = cfg.getCustomerDisplayTextElement("closed"); //$NON-NLS-1$
		if (closed == null)
		{
			closed = new Element("text"); //$NON-NLS-1$
			closed.setAttribute("id", "closed"); //$NON-NLS-1$ //$NON-NLS-2$
			cfg.getCustomerDisplay().addContent(closed);
		}
		this.setDefault("customer-display.closed-text", closed.getText()); //$NON-NLS-1$
		this.setDefault("customer-display.closed-text.scroll", closed.getAttributeValue("scroll")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.setDefault("voucher.printlogo", cfg.getVoucher().getAttributeValue("printlogo")); //$NON-NLS-1$ //$NON-NLS-2$
		this.setDefault("voucher.logo", cfg.getVoucher().getAttributeValue("logo")); //$NON-NLS-1$ //$NON-NLS-2$
		this.setDefault("voucher.logomode", cfg.getVoucher().getAttributeValue("logomode")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.setDefault("receipt.header.printlogo", cfg.getReceiptHeader().getAttributeValue("printlogo")); //$NON-NLS-1$ //$NON-NLS-2$
		this.setDefault("receipt.header.logo", cfg.getReceiptHeader().getAttributeValue("logo")); //$NON-NLS-1$ //$NON-NLS-2$
		this.setDefault("receipt.header.logomode", cfg.getReceiptHeader().getAttributeValue("logomode")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this
						.setDefault(
										"receipt.position.print-second-line", cfg.getReceiptPosition().getAttributeValue("print-second-line")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this
						.setDefault(
										"settlement.admit-test-settlement", cfg.getSettlement().getAttributeValue("admit-test-settlement")); //$NON-NLS-1$ //$NON-NLS-2$
		this
						.setDefault(
										"settlement.print-payment-quantity", cfg.getSettlement().getAttributeValue("print-payment-quantity")); //$NON-NLS-1$ //$NON-NLS-2$
		
		this.dirty = false;
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public boolean needsSaving()
	{
		return this.dirty;
	}
	
	/**
	 * Returns an enumerationeration of all preferences known to this store
	 * which have current values other than their default value.
	 * 
	 * @return an array of preference names
	 */
	public String[] preferenceNames()
	{
		ArrayList list = new ArrayList();
		Enumeration enumerationeration = this.properties.propertyNames();
		while (enumerationeration.hasMoreElements())
		{
			list.add(enumerationeration.nextElement());
		}
		return (String[]) list.toArray(new String[list.size()]);
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public void putValue(String name, String value)
	{
		String oldValue = this.getString(name);
		if (oldValue == null || !oldValue.equals(value))
		{
			this.setValue(this.properties, name, value);
			this.dirty = true;
		}
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public void removePropertyChangeListener(IPropertyChangeListener listener)
	{
		this.listeners.remove(listener);
	}
	
	/**
	 * Saves the non-default-valued preferences known to this preference store
	 * to the file from which they were originally loaded.
	 * 
	 * @exception java.io.IOException
	 *                if there is a problem saving this store
	 */
	public void save()
	{
		
		Config cfg = Config.getInstance();
		cfg.setLoggingLevel(this.getString("logging.level")); //$NON-NLS-1$
		cfg.setLoggingMax(this.getInt("logging.max")); //$NON-NLS-1$
		cfg.setLoggingTrace(this.getBoolean("logging.trace")); //$NON-NLS-1$
		// 10156
		cfg.setLoggingReceipts(this.getBoolean("logging.receipts")); //$NON-NLS-1$
		// 10156
		// 10158
		cfg.setLoggingCompress(this.getBoolean("logging.compress"));
		// 10158
		cfg.setDatabaseDefault(this.getString("database.default")); //$NON-NLS-1$
		
		cfg.setDatabaseStandardName(this.getString("database.standard.name")); //$NON-NLS-1$
		cfg.setDatabaseStandardActive(this.getBoolean("database.standard.active")); //$NON-NLS-1$
		
		Element element = new Element("connection"); //$NON-NLS-1$
		element.setAttribute("jcd-alias", this.getString("database.standard.connection.jcd-alias")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("default-connection", this.getString("database.standard.connection.default-connection")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("platform", this.getString("database.standard.connection.platform")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("jdbc-level", this.getString("database.standard.connection.jdbc-level")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("driver", this.getString("database.standard.connection.driver")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("protocol", this.getString("database.standard.connection.protocol")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("subprotocol", this.getString("database.standard.connection.subprotocol")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("host", this.getString("database.standard.connection.host")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("port", this.getString("database.standard.connection.port")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("database", this.getString("database.standard.connection.database")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("options", this.getString("database.standard.connection.options")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("username", this.getString("database.standard.connection.username")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("password", this.getString("database.standard.connection.password")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("batch-mode", this.getString("database.standard.connection.batch-mode")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("use-auto-commit", this.getString("database.standard.connection.use-auto-commit")); //$NON-NLS-1$ //$NON-NLS-2$
		cfg.setDatabaseStandardConnection(element);
		
		cfg.setDatabaseTemporaryName(this.getString("database.temporary.name")); //$NON-NLS-1$
		cfg.setDatabaseTemporaryActive(this.getBoolean("database.temporary.active")); //$NON-NLS-1$
		
		element = new Element("connection"); //$NON-NLS-1$
		element.setAttribute("jcd-alias", this.getString("database.temporary.connection.jcd-alias")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("default-connection", this.getString("database.temporary.connection.default-connection")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("platform", this.getString("database.temporary.connection.platform")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("jdbc-level", this.getString("database.temporary.connection.jdbc-level")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("driver", this.getString("database.temporary.connection.driver")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("protocol", this.getString("database.temporary.connection.protocol")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("subprotocol", this.getString("database.temporary.connection.subprotocol")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("host", this.getString("database.temporary.connection.host")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("port", this.getString("database.temporary.connection.port")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("database", this.getString("database.temporary.connection.database")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("options", this.getString("database.temporary.connection.options")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("username", this.getString("database.temporary.connection.username")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("password", this.getString("database.temporary.connection.password")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("batch-mode", this.getString("database.temporary.connection.batch-mode")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("use-auto-commit", this.getString("database.temporary.connection.use-auto-commit")); //$NON-NLS-1$ //$NON-NLS-2$
		cfg.setDatabaseTemporaryConnection(element);
		
		cfg.setDatabaseTutorialName(this.getString("database.tutorial.name")); //$NON-NLS-1$
		cfg.setDatabaseTutorialActive(this.getBoolean("database.tutorial.active")); //$NON-NLS-1$
		
		element = new Element("connection"); //$NON-NLS-1$
		element.setAttribute("jcd-alias", this.getString("database.tutorial.connection.jcd-alias")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("default-connection", this.getString("database.tutorial.connection.default-connection")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("platform", this.getString("database.tutorial.connection.platform")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("jdbc-level", this.getString("database.tutorial.connection.jdbc-level")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("driver", this.getString("database.tutorial.connection.driver")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("protocol", this.getString("database.tutorial.connection.protocol")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("subprotocol", this.getString("database.tutorial.connection.subprotocol")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("host", this.getString("database.tutorial.connection.host")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("port", this.getString("database.tutorial.connection.port")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("database", this.getString("database.tutorial.connection.database")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("options", this.getString("database.tutorial.connection.options")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("username", this.getString("database.tutorial.connection.username")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("password", this.getString("database.tutorial.connection.password")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("batch-mode", this.getString("database.tutorial.connection.batch-mode")); //$NON-NLS-1$ //$NON-NLS-2$
		element.setAttribute("use-auto-commit", this.getString("database.tutorial.connection.use-auto-commit")); //$NON-NLS-1$ //$NON-NLS-2$
		cfg.setDatabaseTutorialConnection(element);
		
		cfg.setSalespointId(this.getInt("salespoint.id")); //$NON-NLS-1$
		cfg.getSalespoint().setAttribute(
						"force-settlement", String.valueOf(this.getBoolean("salespoint.force-settlement"))); //$NON-NLS-1$ //$NON-NLS-2$
		cfg.getSalespoint().setAttribute("export", String.valueOf(this.getBoolean("salespoint.export"))); //$NON-NLS-1$ //$NON-NLS-2$
		cfg.getSalespoint().setAttribute("path", this.getString("salespoint.export.path")); //$NON-NLS-1$ //$NON-NLS-2$
		
		cfg.setInputDefaultQuantity(this.getInt("input-default.quantity")); //$NON-NLS-1$
		cfg.setInputDefaultTax(this.getLong("input-default.tax")); //$NON-NLS-1$
		cfg.setInputDefaultOption(this.getString("input-default.option")); //$NON-NLS-1$
		cfg.setInputDefaultMaxQuantityRange(this.getInt("input-default.max-quantity-range")); //$NON-NLS-1$
		cfg.setInputDefaultMaxQuantityAmount(this.getInt("input-default.max-quantity-amount")); //$NON-NLS-1$
		cfg.setInputDefaultMaxPriceRange(this.getDouble("input-default.max-price-range")); //$NON-NLS-1$
		cfg.setInputDefaultMaxPriceAmount(this.getDouble("input-default.max-price-amount")); //$NON-NLS-1$
		cfg.setInputDefaultMaxPaymentRange(this.getDouble("input-default.max-payment-range")); //$NON-NLS-1$
		cfg.setInputDefaultMaxPaymentAmount(this.getDouble("input-default.max-payment-amount")); //$NON-NLS-1$
		cfg.setInputDefaultClearPrice(this.getBoolean("input-default.clear-price")); //$NON-NLS-1$
		
		cfg.setProductServerUse(this.getBoolean("com-server.use")); //$NON-NLS-1$ 
		cfg.setProductServerClass(this.getString("com-server.class")); //$NON-NLS-1$
		cfg.setProductServerHold(this.getBoolean("com-server.hold")); //$NON-NLS-1$ 
		
		cfg.setGalileoUpdate(this.getInt("galileo.update")); //$NON-NLS-1$
		cfg.setGalileoPath(this.getString("galileo.path")); //$NON-NLS-1$
		cfg.setGalileoShowAddCustomerMessage(this.getBoolean("galileo.show-add-customer-message")); //$NON-NLS-1$
		cfg.setGalileoSearchCd(this.getBoolean("galileo.search-cd")); //$NON-NLS-1$ 
		cfg.setGalileoCdPath(this.getString("galileo.cd-path")); //$NON-NLS-1$ 
		
		cfg.setLayoutLeftWidth(this.getInt("layout.left"));
		cfg.setLayoutTotalBlock(this.getString("layout.total-block")); //$NON-NLS-1$
		cfg.setLayoutTopLeft(this.getString("layout.top-left")); //$NON-NLS-1$
		cfg.setLayoutTopRight(this.getString("layout.top-right")); //$NON-NLS-1$
		cfg.setLayoutBottomLeft(this.getString("layout.bottom-left")); //$NON-NLS-1$
		cfg.setLayoutBottomRight(this.getString("layout.bottom-right")); //$NON-NLS-1$
		
		cfg.setLocaleLanguage(this.getString("locale.language")); //$NON-NLS-1$
		cfg.setLocaleCountry(this.getString("locale.country")); //$NON-NLS-1$
		
		cfg.setCurrencyDefault(this.getString("currency.default")); //$NON-NLS-1$
		cfg.setCurrencyRoundfactorAmount(this.getDouble("currency.roundfactor.amount")); //$NON-NLS-1$
		cfg.setCurrencyRoundfactorTax(this.getDouble("currency.roundfactor.tax")); //$NON-NLS-1$
		
		cfg.setLookAndFeelClass(this.getString("look-and-feel")); //$NON-NLS-1$
		
		cfg.getTabPanelFont().setAttribute("name", this.getString("tab-panel.font.name")); //$NON-NLS-1$ //$NON-NLS-2$
		cfg.getTabPanelFont().setAttribute("size", String.valueOf(this.getFloat("tab-panel.font.size"))); //$NON-NLS-1$ //$NON-NLS-2$
		cfg.getTabPanelFont().setAttribute("style", String.valueOf(this.getInt("tab-panel.font.style"))); //$NON-NLS-1$ //$NON-NLS-2$
		RGB fg = new RGB(
						this.getInt("tab-panel.fgcolor.red"), this.getInt("tab-panel.fgcolor.green"), this.getInt("tab-panel.fgcolor.blue")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		RGB bg = new RGB(
						this.getInt("tab-panel.bgcolor.red"), this.getInt("tab-panel.bgcolor.green"), this.getInt("tab-panel.bgcolor.blue")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		cfg.setTabPanelRGBForeground(fg);
		cfg.setTabPanelRGBBackground(bg);
		
		cfg.getTotalBlock().setAttribute("show-always", Boolean.toString(this.getBoolean("total-block.show-always"))); //$NON-NLS-1$ //$NON-NLS-2$
		cfg.getTotalBlock().setAttribute("hold-values", Boolean.toString(this.getBoolean("total-block.hold-values"))); //$NON-NLS-1$ //$NON-NLS-2$
		cfg.getTotalBlockFont().setAttribute("name", this.getString("total-block.font.name")); //$NON-NLS-1$ //$NON-NLS-2$
		cfg.getTotalBlockFont().setAttribute("size", String.valueOf(this.getFloat("total-block.font.size"))); //$NON-NLS-1$ //$NON-NLS-2$
		cfg.getTotalBlockFont().setAttribute("style", String.valueOf(this.getInt("total-block.font.style"))); //$NON-NLS-1$ //$NON-NLS-2$
		fg = new RGB(
						this.getInt("total-block.fgcolor.red"), this.getInt("total-block.fgcolor.green"), this.getInt("total-block.fgcolor.blue")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		bg = new RGB(
						this.getInt("total-block.bgcolor.red"), this.getInt("total-block.bgcolor.green"), this.getInt("total-block.bgcolor.blue")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		cfg.setTotalBlockRGBForeground(fg);
		cfg.setTotalBlockRGBBackground(bg);
		
		cfg.getDetailBlockListFont().setAttribute("size", String.valueOf(this.getFloat("detail-block-list.font.size"))); //$NON-NLS-1$ //$NON-NLS-2$
		cfg.getDetailBlockListFont().setAttribute("style", String.valueOf(this.getInt("detail-block-list.font.style"))); //$NON-NLS-1$ //$NON-NLS-2$
		fg = new RGB(
						this.getInt("detail-block-list.normal-color.fgcolor.red"), this.getInt("detail-block-list.normal-color.fgcolor.green"), this.getInt("detail-block-list.normal-color.fgcolor.blue")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		cfg.setDetailBlockListNormalRGBForeground(fg);
		fg = new RGB(
						this.getInt("detail-block-list.back-color.fgcolor.red"), this.getInt("detail-block-list.back-color.fgcolor.green"), this.getInt("detail-block-list.back-color.fgcolor.blue")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		cfg.setDetailBlockListBackRGBForeground(fg);
		fg = new RGB(
						this.getInt("detail-block-list.expense-color.fgcolor.red"), this.getInt("detail-block-list.expense-color.fgcolor.green"), this.getInt("detail-block-list.expense-color.fgcolor.blue")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		cfg.setDetailBlockListExpenseRGBForeground(fg);
		
		cfg.getDetailBlockFont().setAttribute("name", this.getString("detail-block.font.name")); //$NON-NLS-1$ //$NON-NLS-2$
		cfg.getDetailBlockFont().setAttribute("size", String.valueOf(this.getFloat("detail-block.font.size"))); //$NON-NLS-1$ //$NON-NLS-2$
		cfg.getDetailBlockFont().setAttribute("style", String.valueOf(this.getInt("detail-block.font.style"))); //$NON-NLS-1$ //$NON-NLS-2$
		fg = new RGB(
						this.getInt("detail-block.fgcolor.red"), this.getInt("detail-block.fgcolor.green"), this.getInt("detail-block.fgcolor.blue")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		cfg.setDetailBlockRGBForeground(fg);
		
		fg = new RGB(
						this.getInt("detail-block.bgcolor.red"), this.getInt("detail-block.bgcolor.green"), this.getInt("detail-block.bgcolor.blue")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		cfg.setDetailBlockRGBBackground(fg);
		
		cfg.getDisplayFont().setAttribute("name", this.getString("display.font.name")); //$NON-NLS-1$ //$NON-NLS-2$
		cfg.getDisplayFont().setAttribute("size", String.valueOf(this.getFloat("display.font.size"))); //$NON-NLS-1$ //$NON-NLS-2$
		cfg.getDisplayFont().setAttribute("style", String.valueOf(this.getInt("display.font.style"))); //$NON-NLS-1$ //$NON-NLS-2$
		fg = new RGB(
						this.getInt("display.fgcolor.red"), this.getInt("display.fgcolor.green"), this.getInt("display.fgcolor.blue")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		bg = new RGB(
						this.getInt("display.bgcolor.red"), this.getInt("display.bgcolor.green"), this.getInt("display.bgcolor.blue")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		cfg.setDisplayRGBForeground(fg);
		cfg.setDisplayRGBBackground(bg);
		
		Element printer = cfg.getPosPrinter();
		String id = printer.getAttributeValue("id"); //$NON-NLS-1$
		printer.setAttribute("use", String.valueOf(this.getBoolean("periphery.pos-printer." + id + ".use"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		printer.setAttribute("name", String.valueOf(this.getString("periphery.pos-printer." + id + ".name"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		printer.setAttribute("class", String.valueOf(this.getString("periphery.pos-printer." + id + ".class"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		Element device = printer.getChild("device"); //$NON-NLS-1$
		device.setAttribute("port", this.getString("periphery.pos-printer." + id + "." + device.getName() + ".port")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		device.setAttribute("alias", this.getString("periphery.pos-printer." + id + "." + device.getName() + ".alias")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		device.setAttribute(
						"charset", this.getString("periphery.pos-printer." + id + "." + device.getName() + ".charset")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		device
						.setAttribute(
										"charactertable", String.valueOf(this.getInt("periphery.pos-printer." + id + "." + device.getName() + ".charactertable"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		Element serial = device.getChild("serial"); //$NON-NLS-1$
		serial
						.setAttribute(
										"baudrate", String.valueOf(this	.getInt("periphery.pos-printer." + id + "." + device.getName() + "." + serial.getName() + ".baudrate"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		serial
						.setAttribute(
										"flowcontrolin", String.valueOf(this.getInt("periphery.pos-printer." + id + "." + device.getName() + "." + serial.getName() + ".flowcontrolin"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		serial
						.setAttribute(
										"flowcontrolout", String.valueOf(this.getInt("periphery.pos-printer." + id + "." + device.getName() + "." + serial.getName() + ".flowcontrolout"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		serial
						.setAttribute(
										"databits", String.valueOf(this	.getInt("periphery.pos-printer." + id + "." + device.getName() + "." + serial.getName() + ".databits"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		serial
						.setAttribute(
										"stopbits", String.valueOf(this	.getInt("periphery.pos-printer." + id + "." + device.getName() + "." + serial.getName() + ".stopbits"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		serial
						.setAttribute(
										"parity", String.valueOf(this	.getInt("periphery.pos-printer." + id + "." + device.getName() + "." + serial.getName() + ".parity"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		List cashdrawers = printer.getChildren("cashdrawer"); //$NON-NLS-1$
		for (Iterator j = cashdrawers.iterator(); j.hasNext();)
		{
			Element cashdrawer = (Element) j.next();
			String cashdrawerId = cashdrawer.getAttributeValue("id"); //$NON-NLS-1$
			cashdrawer
							.setAttribute(
											"use", this		.getString("periphery.pos-printer." + id + ".cashdrawer." + cashdrawerId + ".use")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			cashdrawer
							.setAttribute(
											"pin", String	.valueOf(this	.getInt("periphery.pos-printer." + id + ".cashdrawer." + cashdrawerId + ".pin"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			cashdrawer
							.setAttribute(
											"pulseon", String.valueOf(this	.getInt("periphery.pos-printer." + id + ".cashdrawer." + cashdrawerId + ".pulseon"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			cashdrawer
							.setAttribute(
											"pulseoff", String.valueOf(this	.getInt("periphery.pos-printer." + id + ".cashdrawer." + cashdrawerId + ".pulseoff"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			cashdrawer
							.setAttribute(
											"currency", this.getString("periphery.pos-printer." + id + ".cashdrawer." + cashdrawerId + ".currency")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
		
		Element customerDisplay = cfg.getCustomerDisplay();
		id = customerDisplay.getAttributeValue("id"); //$NON-NLS-1$
		customerDisplay.setAttribute(
						"use", String.valueOf(this.getBoolean("periphery.customer-display." + id + ".use"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		customerDisplay.setAttribute("name", this.getString("periphery.customer-display." + id + ".name")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		customerDisplay.setAttribute("class", this.getString("periphery.customer-display." + id + ".class")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		customerDisplay.setAttribute(
						"emulation", String.valueOf(this.getInt("periphery.customer-display." + id + ".emulation"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		customerDisplay.setAttribute(
						"line-count", String.valueOf(this.getInt("periphery.customer-display." + id + ".line-count"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		customerDisplay
						.setAttribute(
										"line-length", String.valueOf(this.getInt("periphery.customer-display." + id + ".line-length"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		device = customerDisplay.getChild("device"); //$NON-NLS-1$
		device.setAttribute(
						"port", this.getString("periphery.customer-display." + id + "." + device.getName() + ".port")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		device
						.setAttribute(
										"alias", this	.getString("periphery.customer-display." + id + "." + device.getName() + ".alias")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		device
						.setAttribute(
										"charset", this	.getString("periphery.customer-display." + id + "." + device.getName() + ".charset")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		device
						.setAttribute(
										"charactertable", String.valueOf(this.getInt("periphery.customer-display." + id + "." + device.getName() + ".charactertable"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		serial = device.getChild("serial"); //$NON-NLS-1$
		serial
						.setAttribute(
										"baudrate", String.valueOf(this	.getInt("periphery.customer-display." + id + "." + device.getName() + "." + serial.getName() + ".baudrate"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		serial
						.setAttribute(
										"flowcontrolin", String.valueOf(this.getInt("periphery.customer-display." + id + "." + device.getName() + "." + serial.getName() + ".flowcontrolin"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		serial
						.setAttribute(
										"flowcontrolout", String.valueOf(this.getInt("periphery.customer-display." + id + "." + device.getName() + "." + serial.getName() + ".flowcontrolout"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		serial
						.setAttribute(
										"databits", String.valueOf(this	.getInt("periphery.customer-display." + id + "." + device.getName() + "." + serial.getName() + ".databits"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		serial
						.setAttribute(
										"stopbits", String.valueOf(this	.getInt("periphery.customer-display." + id + "." + device.getName() + "." + serial.getName() + ".stopbits"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		serial
						.setAttribute(
										"parity", String.valueOf(this	.getInt("periphery.customer-display." + id + "." + device.getName() + "." + serial.getName() + ".parity"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		customerDisplay.setAttribute("timer", String.valueOf(this.getBoolean("customer-display.timer"))); //$NON-NLS-1$ //$NON-NLS-2$ 
		customerDisplay.setAttribute("seconds", String.valueOf(this.getInt("customer-display.seconds"))); //$NON-NLS-1$ //$NON-NLS-2$ 
		cfg
						.setCustomerDisplayTextElement(
										"welcome", new Boolean(this.getBoolean("customer-display.welcome-text.scroll")), this.getString("customer-display.welcome-text")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		cfg
						.setCustomerDisplayTextElement(
										"closed", new Boolean(this.getBoolean("customer-display.closed-text.scroll")), this.getString("customer-display.closed-text")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		cfg.getReceipt().setAttribute("automatic-print",
						new Boolean(this.getBoolean("receipt.automatic-print")).toString());
		cfg.getReceipt().setAttribute("take-back-print-twice",
						new Boolean(this.getBoolean("receipt.take-back-print-twice")).toString());
		cfg.getReceipt().setAttribute("take-back-print-signature",
						new Boolean(this.getBoolean("receipt.take-back-print-signature")).toString());
		cfg.setReceiptHeaderText(this.getString("receipt.header.text")); //$NON-NLS-1$
		cfg.setReceiptHeaderNumberLength(this.getString("receipt.header.number.length")); //$NON-NLS-1$
		cfg.setReceiptHeaderTextAlign(this.getString("receipt.header.row.col.align")); //$NON-NLS-1$
		cfg.setReceiptFooterText(this.getString("receipt.footer.text")); //$NON-NLS-1$
		cfg.setReceiptFooterTextAlign(this.getString("receipt.footer.row.col.align")); //$NON-NLS-1$
		cfg.setReceiptFooterPrintUser(this.getString("receipt.footer.print.user")); //$NON-NLS-1$
		cfg.setReceiptFooterPrintUserAlign(this.getString("receipt.footer.row.col.align")); //$NON-NLS-1$
		// 10157
		cfg.setReceiptCustomerText(this.getString("receipt.customer.text")); //$NON-NLS-1$
		cfg.setReceiptCustomerTextAlign(this.getString("receipt.customer.row.col.align")); //$NON-NLS-1$
		// 10157
		cfg.setReceiptHeaderPrintLogo(this.getBoolean("receipt.header.printlogo")); //$NON-NLS-1$
		cfg.setReceiptHeaderLogo(this.getInt("receipt.header.logo")); //$NON-NLS-1$
		cfg.setReceiptHeaderLogoMode(this.getInt("receipt.header.logomode")); //$NON-NLS-1$
		
		List children = cfg.getReceiptPosition().getChildren("row");
		Iterator rows = children.iterator();
		while (rows.hasNext())
		{
			Element row = (Element) rows.next();
			List columns = row.getChildren("col");
			Iterator cols = columns.iterator();
			while (cols.hasNext())
			{
				Element col = (Element) cols.next();
				if (col.getText().equals("position.galileoid") || col.getText().equals("position.productid")
								|| col.getText().equals("position.productgroup"))
				{
					col.setText(this.getString("receipt.position.row.col.item-name"));
				}
			}
		}
		
		cfg.setReceiptPositionPrintSecondLine(this.getBoolean("receipt.position.print-second-line")); //$NON-NLS-1$
		
		cfg.setVoucherPrintLogo(this.getBoolean("voucher.printlogo")); //$NON-NLS-1$
		cfg.setVoucherLogo(this.getInt("voucher.logo")); //$NON-NLS-1$
		cfg.setVoucherLogoMode(this.getInt("voucher.logomode")); //$NON-NLS-1$
		
		cfg.setSettlementAdmitTestSettlement(this.getBoolean("settlement.admit-test-settlement")); // 10182
		cfg.setSettlementPrintPaymentQuantity(this.getBoolean("settlement.print-payment-quantity"));
		
		cfg.save();
		Setting.getInstance().store();
	}
	
	/**
	 * Saves this preference store to the given output stream. The given string
	 * is inserted as header information.
	 * 
	 * @param out
	 *            the output stream
	 * @param header
	 *            the header
	 * @exception java.io.IOException
	 *                if there is a problem saving this store
	 */
	public void save(OutputStream out, String header) throws IOException
	{
		this.properties.store(out, header);
		this.dirty = false;
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public void setDefault(String name, double value)
	{
		this.setValue(this.defaultProperties, name, value);
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public void setDefault(String name, float value)
	{
		this.setValue(this.defaultProperties, name, value);
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public void setDefault(String name, int value)
	{
		this.setValue(this.defaultProperties, name, value);
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public void setDefault(String name, long value)
	{
		this.setValue(this.defaultProperties, name, value);
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public void setDefault(String name, String value)
	{
		this.setValue(this.defaultProperties, name, value);
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public void setDefault(String name, boolean value)
	{
		this.setValue(this.defaultProperties, name, value);
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public void setToDefault(String name)
	{
		Object oldValue = this.properties.get(name);
		this.properties.remove(name);
		this.dirty = true;
		Object newValue = null;
		if (this.defaultProperties != null) newValue = this.defaultProperties.get(name);
		this.firePropertyChangeEvent(name, oldValue, newValue);
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public void setValue(String name, double value)
	{
		double oldValue = this.getDouble(name);
		if (oldValue != value)
		{
			this.setValue(this.properties, name, value);
			this.dirty = true;
			this.firePropertyChangeEvent(name, new Double(oldValue), new Double(value));
		}
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public void setValue(String name, float value)
	{
		float oldValue = this.getFloat(name);
		if (oldValue != value)
		{
			this.setValue(this.properties, name, value);
			this.dirty = true;
			this.firePropertyChangeEvent(name, new Float(oldValue), new Float(value));
		}
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public void setValue(String name, int value)
	{
		int oldValue = this.getInt(name);
		if (oldValue != value)
		{
			this.setValue(this.properties, name, value);
			this.dirty = true;
			this.firePropertyChangeEvent(name, new Integer(oldValue), new Integer(value));
		}
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public void setValue(String name, long value)
	{
		long oldValue = this.getLong(name);
		if (oldValue != value)
		{
			this.setValue(this.properties, name, value);
			this.dirty = true;
			this.firePropertyChangeEvent(name, new Long(oldValue), new Long(value));
		}
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public void setValue(String name, String value)
	{
		String oldValue = this.getString(name);
		if (oldValue == null || !oldValue.equals(value))
		{
			this.setValue(this.properties, name, value);
			this.dirty = true;
			this.firePropertyChangeEvent(name, oldValue, value);
		}
	}
	
	/*
	 * (non-Javadoc) Method declared on IPreferenceStore.
	 */
	public void setValue(String name, boolean value)
	{
		boolean oldValue = this.getBoolean(name);
		if (oldValue != value)
		{
			this.setValue(this.properties, name, value);
			this.dirty = true;
			this.firePropertyChangeEvent(name, new Boolean(oldValue), new Boolean(value));
		}
	}
	
	/**
	 * Helper method: sets string for a given name.
	 */
	private void setValue(Properties p, String name, double value)
	{
		Assert.isTrue(p != null);
		p.put(name, Double.toString(value));
	}
	
	/**
	 * Helper method: sets string for a given name.
	 */
	private void setValue(Properties p, String name, float value)
	{
		Assert.isTrue(p != null);
		p.put(name, Float.toString(value));
	}
	
	/**
	 * Helper method: sets string for a given name.
	 */
	private void setValue(Properties p, String name, int value)
	{
		Assert.isTrue(p != null);
		p.put(name, Integer.toString(value));
	}
	
	/**
	 * Helper method: sets string for a given name.
	 */
	private void setValue(Properties p, String name, long value)
	{
		Assert.isTrue(p != null);
		p.put(name, Long.toString(value));
	}
	
	/**
	 * Helper method: sets string for a given name.
	 */
	private void setValue(Properties p, String name, String value)
	{
		Assert.isTrue(p != null && value != null);
		p.put(name, value);
	}
	
	/**
	 * Helper method: sets string for a given name.
	 */
	private void setValue(Properties p, String name, boolean value)
	{
		Assert.isTrue(p != null);
		p.put(name, value == true ? IPreferenceStore.TRUE : IPreferenceStore.FALSE);
	}
}
