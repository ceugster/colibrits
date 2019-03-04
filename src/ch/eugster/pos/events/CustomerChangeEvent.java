/*
 * Created on 30.06.2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.events;

import ch.eugster.pos.db.Customer;

/**
 * @author ceugster
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CustomerChangeEvent {

	private Customer customer;
	/**
	 * 
	 */
	public CustomerChangeEvent() {
		super();
		customer = new Customer();
	}
	/**
	 * 
	 */
	public CustomerChangeEvent(Customer customer) {
		super();
		this.customer = customer;
	}
	
	public String getCustomerId() {
		return customer.getId();
	}
	
	public Customer getCustomer() {
		return customer;
	}
}
