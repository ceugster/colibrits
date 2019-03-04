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
package ch.eugster.pos.swt;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.ListenerList;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.RGB;

import ch.eugster.pos.db.CurrentTax;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.PaymentTypeGroup;
import ch.eugster.pos.db.Tax;

/**
 * A concrete attribute store implementation based on an internal
 * <code>java.util.Hashtable</code> object, with support for persisting the
 * non-default attribute values to files or streams.
 * <p>
 * This class was not designed to be subclassed.
 * </p>
 * 
 * @see IStore
 */
public abstract class Store implements IStore
{
	
	/**
	 * List of registered listeners (element type:
	 * <code>IPropertyChangeListener</code>). These listeners are to be informed
	 * when the current value of a attribute changes.
	 */
	protected ListenerList listeners = new ListenerList();
	
	/**
	 * The mapping from attribute name to attribute value (represented as
	 * strings).
	 */
	protected Hashtable values;
	protected Hashtable defaultValues;
	
	/**
	 * Indicates whether a value as been changed by <code>setToDefault</code> or
	 * <code>setValue</code>; initially <code>false</code>.
	 */
	protected boolean dirty = false;
	
	/**
	 * Creates an empty store.
	 * <p>
	 * Use the methods <code>load(InputStream)</code> and
	 * <code>save(InputStream)</code> to load and store this store.
	 * </p>
	 * 
	 * @see #load(InputStream)
	 * @see #save(OutputStream, String)
	 */
	public Store()
	{
		this.values = new Hashtable();
		this.defaultValues = new Hashtable();
		this.initialize();
	}
	
	public abstract void initialize();
	
	/*
	 * (non-Javadoc) Method declared on IStore.
	 */
	public void addPropertyChangeListener(IPropertyChangeListener listener)
	{
		this.listeners.add(listener);
	}
	
	/*
	 * (non-Javadoc) Method declared on IStore.
	 */
	public boolean contains(String name)
	{
		return this.values.containsKey(name);
	}
	
	/*
	 * (non-Javadoc) Method declared on IStore.
	 */
	public void firePropertyChangeEvent(String name, Object oldValue, Object newValue)
	{
		final Object[] listeners = this.listeners.getListeners();
		// Do we need to fire an event.
		if (listeners.length > 0 && (oldValue == null || !oldValue.equals(newValue)))
		{
			final PropertyChangeEvent pe = new PropertyChangeEvent(this, name, oldValue, newValue);
			for (int i = 0; i < listeners.length; ++i)
			{
				IPropertyChangeListener l = (IPropertyChangeListener) listeners[i];
				l.propertyChange(pe);
			}
		}
	}
	
	public Object getValue(String name)
	{
		return this.getValue(this.values, name);
	}
	
	public Object getValue(Hashtable p, String name)
	{
		return p.get(name);
	}
	
	public Boolean getBoolean(String name)
	{
		return this.getBoolean(this.values, name);
	}
	
	/**
	 * Helper function: gets boolean for a given name.
	 */
	protected Boolean getBoolean(Hashtable p, String name)
	{
		Object o = p.get(name);
		if (o instanceof Boolean)
		{
			return (Boolean) o;
		}
		return IStore.BOOLEAN_DEFAULT;
	}
	
	/*
	 * (non-Javadoc) Method declared on IStore.
	 */
	public Double getDouble(String name)
	{
		return this.getDouble(this.values, name);
	}
	
	/**
	 * Helper function: gets double for a given name.
	 */
	protected Double getDouble(Hashtable p, String name)
	{
		Object o = p.get(name);
		if (o instanceof Double)
		{
			return (Double) o;
		}
		return IStore.DOUBLE_DEFAULT;
	}
	
	/*
	 * (non-Javadoc) Method declared on IStore.
	 */
	public Float getFloat(String name)
	{
		return this.getFloat(this.values, name);
	}
	
	/**
	 * Helper function: gets float for a given name.
	 */
	protected Float getFloat(Hashtable p, String name)
	{
		Object o = p.get(name);
		if (o instanceof Float)
		{
			return (Float) o;
		}
		return IStore.FLOAT_DEFAULT;
	}
	
	/*
	 * (non-Javadoc) Method declared on IStore.
	 */
	public Integer getInt(String name)
	{
		return this.getInt(this.values, name);
	}
	
	/**
	 * Helper function: gets int for a given name.
	 */
	protected Integer getInt(Hashtable p, String name)
	{
		Object o = p.get(name);
		if (o instanceof Integer)
		{
			return (Integer) o;
		}
		return IStore.INT_DEFAULT;
	}
	
	/*
	 * (non-Javadoc) Method declared on IStore.
	 */
	public Long getLong(String name)
	{
		return this.getLong(this.values, name);
	}
	
	/**
	 * Helper function: gets long for a given name.
	 */
	protected Long getLong(Hashtable p, String name)
	{
		Object o = p.get(name);
		if (o instanceof Long)
		{
			return (Long) o;
		}
		return IStore.LONG_DEFAULT;
	}
	
	/*
	 * (non-Javadoc) Method declared on IStore.
	 */
	public String getString(String name)
	{
		return this.getString(this.values, name);
	}
	
	/**
	 * Helper function: gets string for a given name.
	 */
	protected String getString(Hashtable p, String name)
	{
		Object o = p.get(name);
		if (o instanceof String)
		{
			return (String) o;
		}
		return IStore.STRING_DEFAULT;
	}
	
	public Tax getTax(String name)
	{
		return this.getTax(this.values, name);
	}
	
	protected Tax getTax(Hashtable p, String name)
	{
		Object o = p.get(name);
		if (o instanceof Tax)
		{
			return (Tax) o;
		}
		return IStore.TAX_DEFAULT;
	}
	
	public RGB getRGB(String name)
	{
		return this.getRGB(this.values, name);
	}
	
	protected RGB getRGB(Hashtable p, String name)
	{
		Object o = p.get(name);
		if (o instanceof RGB)
		{
			return (RGB) o;
		}
		return IStore.RGB_DEFAULT;
	}
	
	public PaymentTypeGroup getPaymentTypeGroup(String name)
	{
		return this.getPaymentTypeGroup(this.values, name);
	}
	
	protected PaymentTypeGroup getPaymentTypeGroup(Hashtable p, String name)
	{
		Object o = p.get(name);
		if (o instanceof PaymentTypeGroup)
		{
			return (PaymentTypeGroup) o;
		}
		return IStore.PAYMENT_TYPE_GROUP_DEFAULT;
	}
	
	public ForeignCurrency getForeignCurrency(String name)
	{
		return this.getForeignCurrency(this.values, name);
	}
	
	protected ForeignCurrency getForeignCurrency(Hashtable p, String name)
	{
		return (ForeignCurrency) p.get(name);
	}
	
	public CurrentTax getCurrentTax(String name)
	{
		return this.getCurrentTax(this.values, name);
	}
	
	protected CurrentTax getCurrentTax(Hashtable p, String name)
	{
		Object o = p.get(name);
		if (o instanceof CurrentTax)
		{
			return (CurrentTax) o;
		}
		return IStore.CURRENT_TAX_DEFAULT;
	}
	
	public Date getDate(String name)
	{
		return this.getDate(this.values, name);
	}
	
	protected Date getDate(Hashtable p, String name)
	{
		Object o = p.get(name);
		if (o instanceof Date)
		{
			return (Date) o;
		}
		return IStore.DATE_DEFAULT;
	}
	
	public boolean needsSaving()
	{
		return this.dirty;
	}
	
	/**
	 * Returns an enumerationeration of all attributes known to this store which
	 * have current values other than their default value.
	 * 
	 * @return an array of attribute names
	 */
	public String[] names()
	{
		ArrayList list = new ArrayList();
		Enumeration enumeration = this.values.keys();
		while (enumeration.hasMoreElements())
		{
			list.add(enumeration.nextElement());
		}
		return (String[]) list.toArray(new String[list.size()]);
	}
	
	/**
	 * puts a value without generating a property change event
	 * 
	 * @param name
	 *            the key
	 * @param value
	 *            the value
	 */
	public void putValue(String name, Object value)
	{
		this.values.put(name, value);
	}
	
	/**
	 * puts a default value without generating a property change event
	 * 
	 * @param name
	 * @param value
	 */
	public void putDefault(String name, Object value)
	{
		this.defaultValues.put(name, value);
	}
	
	/**
	 * moves the default values
	 */
	public void putToDefaults()
	{
		Enumeration keys = this.defaultValues.keys();
		while (keys.hasMoreElements())
		{
			Object key = keys.nextElement();
			this.values.put(key, this.defaultValues.get(key));
		}
		this.dirty = false;
	}
	
	/*
	 * (non-Javadoc) Method declared on IStore.
	 */
	public Object putToDefault(String name)
	{
		Object oldValue = this.values.get(name);
		Object newValue = this.defaultValues.get(name);
		this.values.put(name, newValue);
		return oldValue;
	}
	
	/*
	 * (non-Javadoc) Method declared on IStore.
	 */
	public void setToDefault(String name)
	{
		Object oldValue = this.values.get(name);
		Object newValue = this.defaultValues.get(name);
		this.values.put(name, newValue);
		this.dirty = true;
		this.firePropertyChangeEvent(name, oldValue, newValue);
	}
	
	/*
	 * (non-Javadoc) Method declared on IStore.
	 */
	public void removePropertyChangeListener(IPropertyChangeListener listener)
	{
		this.listeners.remove(listener);
	}
	
	public void setDirty(boolean dirty)
	{
		this.dirty = dirty;
	}
	
	public void setValue(String name, Object value)
	{
		Object oldValue = this.getValue(name);
		if (!oldValue.equals(value))
		{
			this.setValue(this.values, name, value);
			this.dirty = true;
			this.firePropertyChangeEvent(name, oldValue, value);
		}
	}
	
	/*
	 * (non-Javadoc) Method declared on IStore.
	 */
	public void setValue(String name, Double value)
	{
		Double oldValue = this.getDouble(name);
		if (oldValue != value)
		{
			this.setValue(this.values, name, value);
			this.dirty = true;
			this.firePropertyChangeEvent(name, oldValue, value);
		}
	}
	
	/*
	 * (non-Javadoc) Method declared on IStore.
	 */
	public void setValue(String name, Float value)
	{
		Float oldValue = this.getFloat(name);
		if (oldValue != value)
		{
			this.setValue(this.values, name, value);
			this.dirty = true;
			this.firePropertyChangeEvent(name, oldValue, value);
		}
	}
	
	/*
	 * (non-Javadoc) Method declared on IStore.
	 */
	public void setValue(String name, Integer value)
	{
		Integer oldValue = this.getInt(name);
		if (oldValue != value)
		{
			this.setValue(this.values, name, value);
			this.dirty = true;
			this.firePropertyChangeEvent(name, oldValue, value);
		}
	}
	
	/*
	 * (non-Javadoc) Method declared on IStore.
	 */
	public void setValue(String name, Long value)
	{
		Long oldValue = this.getLong(name);
		if (!oldValue.equals(value))
		{
			this.setValue(this.values, name, value);
			this.dirty = true;
			this.firePropertyChangeEvent(name, oldValue, value);
		}
	}
	
	/*
	 * (non-Javadoc) Method declared on IStore.
	 */
	public void setValue(String name, String value)
	{
		String oldValue = this.getString(name);
		if (oldValue == null || !oldValue.equals(value))
		{
			this.setValue(this.values, name, value);
			this.dirty = true;
			this.firePropertyChangeEvent(name, oldValue, value);
		}
	}
	
	/*
	 * (non-Javadoc) Method declared on IStore.
	 */
	public void setValue(String name, Boolean value)
	{
		Boolean oldValue = this.getBoolean(name);
		if (oldValue != value)
		{
			this.setValue(this.values, name, value);
			this.dirty = true;
			this.firePropertyChangeEvent(name, oldValue, value);
		}
	}
	
	public void setValue(String name, Tax value)
	{
		Tax oldValue = this.getTax(name);
		if (!oldValue.equals(value))
		{
			this.setValue(this.values, name, value);
			this.dirty = true;
			this.firePropertyChangeEvent(name, oldValue, value);
		}
	}
	
	public void setValue(String name, RGB value)
	{
		RGB oldValue = this.getRGB(name);
		if (!oldValue.equals(value))
		{
			this.setValue(this.values, name, value);
			this.dirty = true;
			this.firePropertyChangeEvent(name, oldValue, value);
		}
	}
	
	public void setValue(String name, PaymentTypeGroup value)
	{
		PaymentTypeGroup oldValue = this.getPaymentTypeGroup(name);
		if (!oldValue.equals(value))
		{
			this.setValue(this.values, name, value);
			this.dirty = true;
			this.firePropertyChangeEvent(name, oldValue, value);
		}
	}
	
	public void setValue(String name, ForeignCurrency value)
	{
		ForeignCurrency oldValue = this.getForeignCurrency(name);
		if (!oldValue.equals(value))
		{
			this.setValue(this.values, name, value);
			this.dirty = true;
			this.firePropertyChangeEvent(name, oldValue, value);
		}
	}
	
	public void setValue(String name, CurrentTax value)
	{
		CurrentTax oldValue = this.getCurrentTax(name);
		if (!oldValue.equals(value))
		{
			this.setValue(this.values, name, value);
			this.dirty = true;
			this.firePropertyChangeEvent(name, oldValue, value);
		}
	}
	
	public void setValue(String name, Date value)
	{
		Date oldValue = this.getDate(name);
		if (!oldValue.equals(value))
		{
			this.setValue(this.values, name, value);
			this.dirty = true;
			this.firePropertyChangeEvent(name, oldValue, value);
		}
	}
	
	/*
	 * (non-Javadoc) Method declared on IStore.
	 */
	public void setDefault(String name, Object value)
	{
		this.setValue(this.defaultValues, name, value);
	}
	
	/*
	 * (non-Javadoc) Method declared on IStore.
	 */
	public void setDefault(String name, Double value)
	{
		this.setValue(this.defaultValues, name, value);
	}
	
	/*
	 * (non-Javadoc) Method declared on IStore.
	 */
	public void setDefault(String name, Float value)
	{
		this.setValue(this.defaultValues, name, value);
	}
	
	/*
	 * (non-Javadoc) Method declared on IStore.
	 */
	public void setDefault(String name, Integer value)
	{
		this.setValue(this.defaultValues, name, value);
	}
	
	/*
	 * (non-Javadoc) Method declared on IStore.
	 */
	public void setDefault(String name, Long value)
	{
		this.setValue(this.defaultValues, name, value);
	}
	
	/*
	 * (non-Javadoc) Method declared on IStore.
	 */
	public void setDefault(String name, String value)
	{
		this.setValue(this.defaultValues, name, value);
	}
	
	/*
	 * (non-Javadoc) Method declared on IStore.
	 */
	public void setDefault(String name, Boolean value)
	{
		this.setValue(this.defaultValues, name, value);
	}
	
	public void setDefault(String name, Tax value)
	{
		this.setValue(this.defaultValues, name, value);
	}
	
	public void setDefault(String name, RGB value)
	{
		this.setValue(this.defaultValues, name, value);
	}
	
	public void setDefault(String name, PaymentTypeGroup value)
	{
		this.setValue(this.defaultValues, name, value);
	}
	
	public void setDefault(String name, ForeignCurrency value)
	{
		this.setValue(this.defaultValues, name, value);
	}
	
	public void setDefault(String name, CurrentTax value)
	{
		this.setValue(this.defaultValues, name, value);
	}
	
	public void setDefault(String name, Date value)
	{
		this.setValue(this.defaultValues, name, value);
	}
	
	/**
	 * Helper method: sets string for a given name.
	 */
	protected void setValue(Hashtable p, String name, Object value)
	{
		Assert.isTrue(p != null);
		p.put(name, value);
	}
	
	/**
	 * Helper method: sets string for a given name.
	 */
	protected void setValue(Hashtable p, String name, Double value)
	{
		Assert.isTrue(p != null);
		p.put(name, value);
	}
	
	/**
	 * Helper method: sets string for a given name.
	 */
	protected void setValue(Hashtable p, String name, Float value)
	{
		Assert.isTrue(p != null);
		p.put(name, value);
	}
	
	/**
	 * Helper method: sets string for a given name.
	 */
	protected void setValue(Hashtable p, String name, Integer value)
	{
		Assert.isTrue(p != null);
		p.put(name, value);
	}
	
	/**
	 * Helper method: sets string for a given name.
	 */
	protected void setValue(Hashtable p, String name, Long value)
	{
		Assert.isTrue(p != null);
		p.put(name, value);
	}
	
	/**
	 * Helper method: sets string for a given name.
	 */
	protected void setValue(Hashtable p, String name, String value)
	{
		Assert.isTrue(p != null && value != null);
		p.put(name, value);
	}
	
	/**
	 * Helper method: sets string for a given name.
	 */
	protected void setValue(Hashtable p, String name, Boolean value)
	{
		Assert.isTrue(p != null);
		p.put(name, value);
	}
	
	protected void setValue(Hashtable p, String name, Tax value)
	{
		Assert.isTrue(p != null);
		p.put(name, value);
	}
	
	protected void setValue(Hashtable p, String name, RGB value)
	{
		Assert.isTrue(p != null);
		p.put(name, value);
	}
	
	protected void setValue(Hashtable p, String name, PaymentTypeGroup value)
	{
		Assert.isTrue(p != null);
		p.put(name, value);
	}
	
	protected void setValue(Hashtable p, String name, ForeignCurrency value)
	{
		Assert.isTrue(p != null);
		p.put(name, value);
	}
	
	protected void setValue(Hashtable p, String name, CurrentTax value)
	{
		Assert.isTrue(p != null);
		p.put(name, value);
	}
	
	protected void setValue(Hashtable p, String name, Date value)
	{
		Assert.isTrue(p != null);
		p.put(name, value);
	}
	
	public Object getDefaultValue(String name)
	{
		if (this.getValue(this.defaultValues, name) != null)
		{
			return this.getValue(this.defaultValues, name);
		}
		return new Object();
	}
	
	public Boolean getDefaultBoolean(String name)
	{
		if (this.getBoolean(this.defaultValues, name) != null)
		{
			return this.getBoolean(this.defaultValues, name);
		}
		return IStore.BOOLEAN_DEFAULT;
	}
	
	/**
	 * Returns the default value for the double-valued attribute with the given
	 * name. Returns the default-default value (<code>0.0</code>)
	 * 
	 * @param name
	 *            the name of the attribute
	 * @return the default value of the named attribute
	 */
	public Double getDefaultDouble(String name)
	{
		if (this.getDouble(this.defaultValues, name) != null)
		{
			return this.getDouble(this.defaultValues, name);
		}
		return IStore.DOUBLE_DEFAULT;
	}
	
	/**
	 * Returns the default value for the float-valued attribute with the given
	 * name. Returns the default-default value (<code>0.0f</code>)
	 * 
	 * @param name
	 *            the name of the attribute
	 * @return the default value of the named attribute
	 */
	public Float getDefaultFloat(String name)
	{
		if (this.getFloat(this.defaultValues, name) != null)
		{
			return this.getFloat(this.defaultValues, name);
		}
		return IStore.FLOAT_DEFAULT;
	}
	
	/**
	 * Returns the default value for the integer-valued attribute with the given
	 * name. Returns the default-default value (<code>0</code>)
	 * 
	 * @param name
	 *            the name of the attribute
	 * @return the default value of the named attribute
	 */
	public Integer getDefaultInt(String name)
	{
		if (this.getInt(this.defaultValues, name) != null)
		{
			return this.getInt(this.defaultValues, name);
		}
		return IStore.INT_DEFAULT;
	}
	
	/**
	 * Returns the default value for the long-valued attribute with the given
	 * name. Returns the default-default value (<code>0L</code>)
	 * 
	 * @param name
	 *            the name of the attribute
	 * @return the default value of the named attribute
	 */
	public Long getDefaultLong(String name)
	{
		if (this.getLong(this.defaultValues, name) != null)
		{
			return this.getLong(this.defaultValues, name);
		}
		return IStore.LONG_DEFAULT;
	}
	
	/**
	 * Returns the default value for the String-valued attribute with the given
	 * name. Returns the default-default value (the empty string <code>""</code>
	 * )
	 * 
	 * @param name
	 *            the name of the attribute
	 * @return the default value of the named attribute
	 */
	public String getDefaultString(String name)
	{
		if (this.getString(this.defaultValues, name) != null)
		{
			return this.getString(this.defaultValues, name);
		}
		return IStore.STRING_DEFAULT;
	}
	
	/**
	 * Returns the default value for the tax-valued attribute with the given
	 * name. Returns the default-default value (<code>TAX_DEFAULT</code>)
	 * 
	 * @param name
	 *            the name of the attribute
	 * @return the default value of the named attribute
	 */
	public Tax getDefaultTax(String name)
	{
		if (this.getTax(this.defaultValues, name) != null)
		{
			return this.getTax(this.defaultValues, name);
		}
		return IStore.TAX_DEFAULT;
	}
	
	/**
	 * Returns the default value for the RGB-valued attribute with the given
	 * name. Returns the default-default value (<code>RGB_DEFAULT</code>)
	 * 
	 * @param name
	 *            the name of the attribute
	 * @return the default value of the named attribute
	 */
	public RGB getDefaultRGB(String name)
	{
		if (this.getRGB(this.defaultValues, name) != null)
		{
			return this.getRGB(this.defaultValues, name);
		}
		return IStore.RGB_DEFAULT;
	}
	
	/**
	 * Returns the default value for the RGB-valued attribute with the given
	 * name. Returns the default-default value (<code>RGB_DEFAULT</code>)
	 * 
	 * @param name
	 *            the name of the attribute
	 * @return the default value of the named attribute
	 */
	public PaymentTypeGroup getDefaultPaymentTypeGroup(String name)
	{
		if (this.getPaymentTypeGroup(this.defaultValues, name) != null)
		{
			return this.getPaymentTypeGroup(this.defaultValues, name);
		}
		return IStore.PAYMENT_TYPE_GROUP_DEFAULT;
	}
	
	/**
	 * Returns the default value for the Currency-valued attribute with the
	 * given name. Returns the default-default value (
	 * <code>CURRENCY_DEFAULT</code>)
	 * 
	 * @param name
	 *            the name of the attribute
	 * @return the default value of the named attribute
	 */
	public ForeignCurrency getDefaultForeignCurrency(String name)
	{
		if (this.getForeignCurrency(this.defaultValues, name) != null)
		{
			return this.getForeignCurrency(this.defaultValues, name);
		}
		return ForeignCurrency.getDefaultCurrency();
	}
	
	/**
	 * Returns the default value for the CurrentTax-valued attribute with the
	 * given name. Returns the default-default value (
	 * <code>CURRENT_TAX_DEFAULT</code>)
	 * 
	 * @param name
	 *            the name of the attribute
	 * @return the default value of the named attribute
	 */
	public CurrentTax getDefaultCurrentTax(String name)
	{
		if (this.getCurrentTax(this.defaultValues, name) != null)
		{
			return this.getCurrentTax(this.defaultValues, name);
		}
		return IStore.CURRENT_TAX_DEFAULT;
	}
	
	/**
	 * Returns the default value for the Date-valued attribute with the given
	 * name. Returns the default-default value (<code>DATE_DEFAULT</code>)
	 * 
	 * @param name
	 *            the name of the attribute
	 * @return the default value of the named attribute
	 */
	public Date getDefaultDate(String name)
	{
		if (this.getDate(this.defaultValues, name) != null)
		{
			return this.getDate(this.defaultValues, name);
		}
		return IStore.DATE_DEFAULT;
	}
}
