/*
 * Created on 09.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.product;

import java.util.Date;

import ch.eugster.pos.db.Customer;
import ch.eugster.pos.db.Position;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public interface IProductServer
{
	/**
	 * 
	 * @param path
	 *            path to the ProductServer
	 * @return <code>true</code> if connection has been established
	 *         successfully, else <code>false</code>
	 * 
	 *         instantiate the ProductServer and test if the connection works by
	 *         opening and eventually closing the connection
	 */
	public abstract boolean connect(String path);
	
	/**
	 * close the connection to the ProductServer
	 * 
	 */
	public abstract void disconnect();
	
	/**
	 * open the connection to the ProductServer
	 * 
	 */
	public abstract void open();
	
	/**
	 * close the connection to the ProductServer
	 * 
	 */
	public abstract void close();
	
	/**
	 * 
	 * @param code
	 * @return <code>true</code> if an item has been found, else
	 *         <code>false</code>
	 * 
	 *         look up an item that has the key <code>code</code>
	 */
	public abstract boolean getItem(String code);
	
	/**
	 * 
	 * @return <code>true</code> if the last getItem(String code) has been
	 *         successfully
	 */
	public abstract boolean found();
	
	/**
	 * 
	 * @param p
	 *            Position to be filled with the data of the current item
	 * @return <code>true</code> if the data has been successfully set, else
	 *         <code>false</code>
	 */
	public abstract boolean setData(Position p);
	
	/**
	 * 
	 * @return String return author
	 */
	public abstract String getAuthor();
	
	/**
	 * 
	 * @return String return title
	 */
	public abstract String getTitle();
	
	/**
	 * 
	 * @return String return publisher
	 */
	public abstract String getPublisher();
	
	/**
	 * 
	 * @return ProductGroup return ProductGroup
	 */
	public abstract String getProductGroup();
	
	/**
	 * 
	 * @return Double return price
	 */
	public abstract double getPrice();
	
	/**
	 * 
	 * @return String return tax code
	 */
	public abstract String getTax();
	
	/**
	 * 
	 * @return String return isbn code (ISBN)
	 */
	public abstract String getISBN();
	
	/**
	 * 
	 * @return String return bz number (BZ-Nummer, 7-stellig)
	 */
	public abstract String getBZNumber();
	
	/**
	 * 
	 * @return String return order id (Bestellnummer)
	 */
	public abstract String getOrderId();
	
	/**
	 * 
	 * @return Integer return quantity (Menge)
	 */
	public abstract int getQuantity();
	
	/**
	 * 
	 * @return String return stock
	 */
	public abstract String getStock();
	
	/**
	 * 
	 * @return boolean return <code>true</code> if stock management is set, else
	 *         <code>false</code>
	 */
	public abstract boolean isStockManagement();
	
	/**
	 * 
	 * @return Date return date of last cash sale
	 */
	public abstract Date getLastCashSaleDate();
	
	/**
	 * 
	 * @return Boolean return <code>true</code> if update has been successfully,
	 *         else <code>false</code>
	 */
	public abstract boolean update(int receiptState, Position position);
	
	/**
	 * 
	 * @return boolean return <code>true</code> if <code>eanCode</code> is a
	 *         customer's code, else <code>false</code>
	 */
	public abstract boolean isCustomer(String eanCode);
	
	/**
	 * 
	 * @return Boolean return <code>true</code> if item is taken from stock,
	 *         else <code>false</code>
	 */
	public abstract boolean isFromStock();
	
	/**
	 * 
	 * @return Boolean return <code>true</code> if item is ordered by a
	 *         customer, else <code>false</code>
	 */
	public abstract boolean isOrdered();
	
	// 10157
	/**
	 * 
	 * @return boolean return <code>true</code> if the customer with the id
	 *         <code>customerId</code> has been found, else <code>false</code>
	 */
	public abstract boolean getCustomer(Integer customerId);
	
	/**
	 * 
	 * @return Customer return the customer as object
	 */
	public abstract Customer getCustomerObject();
	
	/**
	 * 
	 * @return Integer return the customer's id
	 */
	public abstract Integer getCustomerId();
	
	/**
	 * 
	 * @return String return the customer's name
	 */
	public abstract String getCustomerName();
	
	/**
	 * 
	 * @return Boolean return <code>true</code> if the customer has a customer
	 *         card, else <code>false</code>
	 */
	public abstract boolean customerHasCard();
	
	/**
	 * 
	 * @return Double return the customer's current account
	 */
	public abstract Double getCustomerAccount();
	
	// 10157
	/**
	 * 
	 * @return boolean return <code>true</code> if the <code>position</code> has
	 *         been successfully set to the transferred data, else
	 *         <code>false</code>
	 */
	public abstract boolean setDataToTransferredPosition(Position position);
	
	/**
	 * 
	 * set the ordered data in <code>position</code>
	 */
	public abstract void setOrderedData(Position position);
	
	/**
	 * 
	 * @return boolean return <code>true</code> if the order is valid, else
	 *         <code>false</code>
	 */
	public abstract boolean isOrderValid(String code);
}