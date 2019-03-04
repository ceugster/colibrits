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

import java.util.Date;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.graphics.RGB;

import ch.eugster.pos.db.CurrentTax;
import ch.eugster.pos.db.PaymentTypeGroup;
import ch.eugster.pos.db.Tax;

/**
 * The <code>IStore</code> interface represents a table mapping named attributes
 * to values. If there is no value for a given name, then that attributes's
 * default value is returned; and if there is no default value for that
 * attribute, then a default-default value is returned. The default-default
 * values for the primitive types are as follows:
 * <ul>
 * <li><code>boolean</code> = <code>false</code></li>
 * <li><code>double</code> = <code>0.0</code></li>
 * <li><code>float</code> = <code>0.0f</code></li>
 * <li><code>int</code> = <code>0</code></li>
 * <li><code>long</code> = <code>0l</code></li>
 * <li><code>String</code> = <code>""</code> (the empty string)</li>
 * </ul>
 * <p>
 * Thus a store maintains two values for each of a set of names: a current value
 * and a default value. The typical usage is to establish the defaults for all
 * known attributes and then restore previously stored values for attributes
 * whose values were different from their defaults. After the current values of
 * the attributes have been modified, it is a simple matter to write out only
 * those attributes whose values are different from their defaults. This
 * two-tiered approach to saving and restoring attribute setting minimized the
 * number of attributes that need to be persisted; indeed, the normal starting
 * state does not require storing any attributes at all.
 * </p>
 * <p>
 * A property change event is reported whenever a attributes current value
 * actually changes (whether through <code>setValue</code>,
 * <code>setToDefault</code>, or other unspecified means). Note, however, that
 * manipulating default values (with <code>setDefault</code>) does not cause
 * such events to be reported.
 * </p>
 * <p>
 * Clients who need a attribute store may implement this interface or
 * instantiate the standard implementation <code>Store</code>.
 * </p>
 * 
 * @see Store
 */
public interface IStore extends IPropertyChangeListener
{
	
	/**
	 * The default-default value for boolean attributes (<code>false</code>).
	 */
	public static final Boolean BOOLEAN_DEFAULT = new Boolean(false);
	
	/**
	 * The default-default value for double attributes (<code>0.0</code>).
	 */
	public static final Double DOUBLE_DEFAULT = new Double(0.0);
	
	/**
	 * The default-default value for float attributes (<code>0.0f</code>).
	 */
	public static final Float FLOAT_DEFAULT = new Float(0.0f);
	
	/**
	 * The default-default value for int attributes (<code>0</code>).
	 */
	public static final Integer INT_DEFAULT = new Integer(0);
	
	/**
	 * The default-default value for long attributes (<code>0L</code>).
	 */
	public static final Long LONG_DEFAULT = new Long(0L);
	
	/**
	 * The default-default value for String attributes (<code>""</code>).
	 */
	public static final String STRING_DEFAULT = ""; //$NON-NLS-1$
	
	/**
	 * The default-default value for Tax (<code>new Tax()</code>).
	 */
	public static final Tax TAX_DEFAULT = new Tax();
	/**
	 * The default-default value for RGB (<code>new RGB(0, 0, 0)</code>).
	 */
	public static final RGB RGB_DEFAULT = new RGB(0, 0, 0);
	/**
	 * The default-default value for PaymentTypeGroup (
	 * <code>new PaymentTypeGroup()</code>).
	 */
	public static final PaymentTypeGroup PAYMENT_TYPE_GROUP_DEFAULT = new PaymentTypeGroup();
	/**
	 * The default-default value for CurrentTax (<code>new CurrentTax()</code>).
	 */
	public static final CurrentTax CURRENT_TAX_DEFAULT = new CurrentTax();
	/**
	 * The default-default value for CurrentTax (<code>new CurrentTax()</code>).
	 */
	public static final Date DATE_DEFAULT = new Date();
	
	/**
	 * Adds a property change listener to this attribute store.
	 * 
	 * @param listener
	 *            a property change listener
	 */
	public void addPropertyChangeListener(IPropertyChangeListener listener);
	
	/**
	 * Returns whether the named attribute is known to this attribute store.
	 * 
	 * @param name
	 *            the name of the attribute
	 * @return <code>true</code> if either a current value or a default value is
	 *         known for the named attribute, and <code>false</code>otherwise
	 */
	public boolean contains(String name);
	
	/**
	 * Fires a property change event corresponding to a change to the current
	 * value of the attribute with the given name.
	 * <p>
	 * This method is provided on this interface to simplify the implementation
	 * of decorators. There is normally no need to call this method since
	 * <code>setValue</code> and <code>setToDefault</code> report such events in
	 * due course. Implementations should funnel all attribute changes through
	 * this method.
	 * </p>
	 * 
	 * @param name
	 *            the name of the attribute, to be used as the property in the
	 *            event object
	 * @param oldValue
	 *            the old value
	 * @param newValue
	 *            the new value
	 */
	public void firePropertyChangeEvent(String name, Object oldValue, Object newValue);
	
	/**
	 * Returns the current value of the boolean-valued attribute with the given
	 * name. Returns the default-default value (<code>false</code>) if there is
	 * no attribute with the given name, or if the current value cannot be
	 * treated as a boolean.
	 * 
	 * @param name
	 *            the name of the attribute
	 * @return the boolean-valued attribute
	 */
	public Boolean getBoolean(String name);
	
	/**
	 * Returns the current value of the double-valued attribute with the given
	 * name. Returns the default-default value (<code>0.0</code>) if there is no
	 * attribute with the given name, or if the current value cannot be treated
	 * as a double.
	 * 
	 * @param name
	 *            the name of the attribute
	 * @return the double-valued attribute
	 */
	public Double getDouble(String name);
	
	/**
	 * Returns the current value of the float-valued attribute with the given
	 * name. Returns the default-default value (<code>0.0f</code>) if there is
	 * no attribute with the given name, or if the current value cannot be
	 * treated as a float.
	 * 
	 * @param name
	 *            the name of the attribute
	 * @return the float-valued attribute
	 */
	public Float getFloat(String name);
	
	/**
	 * Returns the current value of the integer-valued attribute with the given
	 * name. Returns the default-default value (<code>0</code>) if there is no
	 * attribute with the given name, or if the current value cannot be treated
	 * as an integter.
	 * 
	 * @param name
	 *            the name of the attribute
	 * @return the int-valued attribute
	 */
	public Integer getInt(String name);
	
	/**
	 * Returns the current value of the long-valued attribute with the given
	 * name. Returns the default-default value (<code>0L</code>) if there is no
	 * attribute with the given name, or if the current value cannot be treated
	 * as a long.
	 * 
	 * @param name
	 *            the name of the attribute
	 * @return the long-valued attribute
	 */
	public Long getLong(String name);
	
	/**
	 * Returns the current value of the attribute with the given name. Returns
	 * the default-default value (the empty string <code>""</code>) if there is
	 * no attribute with the given name, or if the current value cannot be
	 * treated as a string.
	 * 
	 * @param name
	 *            the name of the attribute
	 * @return the attribute
	 */
	public String getString(String name);
	
	/**
	 * Returns the current value of the attribute with the given name. Returns
	 * the default-default value (the empty string <code>""</code>) if there is
	 * no attribute with the given name, or if the current value cannot be
	 * treated as a string.
	 * 
	 * @param name
	 *            the name of the attribute
	 * @return the attribute
	 */
	public Tax getTax(String name);
	
	/**
	 * Returns whether the current values in this property store require saving.
	 * 
	 * @return <code>true</code> if at least one of the attributes known to this
	 *         store has a current value different from its default value, and
	 *         <code>false</code> otherwise
	 */
	public boolean needsSaving();
	
	/**
	 * Sets the current value of the attribute with the given name to the given
	 * string value.
	 * <p>
	 * This method is provided on this interface to simplify the implementation
	 * of decorators, and does not report a property change event. Normal
	 * clients should instead call <code>setValue</code>.
	 * </p>
	 * 
	 * @param name
	 *            the name of the attribute
	 * @param value
	 *            the new current value of the attribute
	 */
	// public void putValue(String name, String value);
	/**
	 * Removes the given listener from this attribute store. Has no affect if
	 * the listener is not registered.
	 * 
	 * @param listener
	 *            a property change listener
	 */
	public void removePropertyChangeListener(IPropertyChangeListener listener);
	
	/**
	 * Sets the current value of the double-valued attribute with the given
	 * name.
	 * <p>
	 * A property change event is reported if the current value of the attribute
	 * actually changes from its previous value. In the event object, the
	 * property name is the name of the attribute, and the old and new values
	 * are wrapped as objects.
	 * </p>
	 * <p>
	 * Note that the preferred way of re-initializing a attribute to its default
	 * value is to call <code>setToDefault</code>.
	 * </p>
	 * 
	 * @param name
	 *            the name of the attribute
	 * @param value
	 *            the new current value of the attribute
	 */
	public void setValue(String name, Double value);
	
	/**
	 * Sets the current value of the float-valued attribute with the given name.
	 * <p>
	 * A property change event is reported if the current value of the attribute
	 * actually changes from its previous value. In the event object, the
	 * property name is the name of the attribute, and the old and new values
	 * are wrapped as objects.
	 * </p>
	 * <p>
	 * Note that the preferred way of re-initializing a attribute to its default
	 * value is to call <code>setToDefault</code>.
	 * </p>
	 * 
	 * @param name
	 *            the name of the attribute
	 * @param value
	 *            the new current value of the attribute
	 */
	public void setValue(String name, Float value);
	
	/**
	 * Sets the current value of the integer-valued attribute with the given
	 * name.
	 * <p>
	 * A property change event is reported if the current value of the attribute
	 * actually changes from its previous value. In the event object, the
	 * property name is the name of the attribute, and the old and new values
	 * are wrapped as objects.
	 * </p>
	 * <p>
	 * Note that the preferred way of re-initializing a attribute to its default
	 * value is to call <code>setToDefault</code>.
	 * </p>
	 * 
	 * @param name
	 *            the name of the attribute
	 * @param value
	 *            the new current value of the attribute
	 */
	public void setValue(String name, Integer value);
	
	/**
	 * Sets the current value of the long-valued attribute with the given name.
	 * <p>
	 * A property change event is reported if the current value of the attribute
	 * actually changes from its previous value. In the event object, the
	 * property name is the name of the attribute, and the old and new values
	 * are wrapped as objects.
	 * </p>
	 * <p>
	 * Note that the preferred way of re-initializing a attribute to its default
	 * value is to call <code>setToDefault</code>.
	 * </p>
	 * 
	 * @param name
	 *            the name of the attribute
	 * @param value
	 *            the new current value of the attribute
	 */
	public void setValue(String name, Long value);
	
	/**
	 * Sets the current value of the attribute with the given name.
	 * <p>
	 * A property change event is reported if the current value of the attribute
	 * actually changes from its previous value. In the event object, the
	 * property name is the name of the attribute, and the old and new values
	 * are wrapped as objects.
	 * </p>
	 * <p>
	 * Note that the preferred way of re-initializing a attribute to its default
	 * value is to call <code>setToDefault</code>.
	 * </p>
	 * 
	 * @param name
	 *            the name of the attribute
	 * @param value
	 *            the new current value of the attribute
	 */
	public void setValue(String name, String value);
	
	/**
	 * Sets the current value of the boolean-valued attribute with the given
	 * name.
	 * <p>
	 * A property change event is reported if the current value of the attribute
	 * actually changes from its previous value. In the event object, the
	 * property name is the name of the attribute, and the old and new values
	 * are wrapped as objects.
	 * </p>
	 * <p>
	 * Note that the preferred way of re-initializing a attribute to its default
	 * value is to call <code>setToDefault</code>.
	 * </p>
	 * 
	 * @param name
	 *            the name of the attribute
	 * @param value
	 *            the new current value of the attribute
	 */
	public void setValue(String name, Boolean value);
	
	/**
	 * Sets the current value of the tax-valued attribute with the given name.
	 * <p>
	 * A property change event is reported if the current value of the attribute
	 * actually changes from its previous value. In the event object, the
	 * property name is the name of the attribute, and the old and new values
	 * are wrapped as objects.
	 * </p>
	 * <p>
	 * Note that the preferred way of re-initializing a attribute to its default
	 * value is to call <code>setToDefault</code>.
	 * </p>
	 * 
	 * @param name
	 *            the name of the attribute
	 * @param value
	 *            the new current value of the attribute
	 */
	public void setValue(String name, Tax value);
	
}
