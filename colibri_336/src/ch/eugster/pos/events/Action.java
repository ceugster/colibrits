/*
 * Created on 07.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.events;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.db.Key;
import ch.eugster.pos.db.Option;
import ch.eugster.pos.db.PaymentType;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.db.Table;
import ch.eugster.pos.db.Tax;
import ch.eugster.pos.db.TaxRate;

/**
 * @author ch. eugster
 * 
 *         Baseclass of various actionclasses used by the colibri ts
 *         pos-application
 * 
 *         the constructor sets the UserPanel and retrieves some information
 *         from the Key object. This baseclass informes listeners about specific
 *         actions, given by action types in combination with subclasses of
 *         <code>
 * Action</code>.
 * 
 *         subclasses of <code>Action</code> are instantiated by an instance of
 *         a <code>Key</code>-Class. Objects of that class are serialized in the
 *         database. The fields <code>CLASS_NAME</code>,
 *         <code>PARENT_CLASS_NAME</code>, <code>PARENT_ID</code> and
 *         <code>ACTION_TYPE</code> identify the type of action, where:
 * 
 *         <code>CLASS_NAME</code> represents the Class of the action, that is
 *         used <code>PARENT_CLASS_NAME</code> represents one of the following
 *         classes: <code>ch.eugster.pos.db.ProductGroup</code>
 *         <code>ch.eugster.pos.db.PaymentType</code>
 *         <code>ch.eugster.pos.db.TaxRate</code>
 *         <code>ch.eugster.pos.db.Option</code>
 *         <code>ch.eugster.pos.db.Function</code> <code>PARENT_ID</code> the id
 *         of the instantiated <code>PARENT_CLASS_NAME</CODE>
 *         <code>ACTION_TYPE</code> is a field of type <code>INTEGER</code> that
 *         specifies the type of action (for a list of valid action types see
 *         the public static final fields of this class.
 * 
 *         When a key is retrieved from the database and instantiated, it
 *         instantiates the action class specified by <code>CLASS_NAME</code>
 *         and, if a parent class is specified, the
 *         <code>PARENT_CLASS_NAME</code> by retrieving the object with the
 *         associated id given by <code>PARENT_ID</code>.
 * 
 */
public class Action extends AbstractAction
{
	private static final long serialVersionUID = 0l;
	
	/**
	 * @param context
	 * @param key
	 */
	public Action(UserPanel context, Key key)
	{
		super();
		this.init(context, key);
	}
	
	/**
	 * @param name
	 */
	public Action(UserPanel context, Key key, String name)
	{
		super(name);
		this.init(context, key);
	}
	
	/**
	 * @param name
	 * @param icon
	 */
	public Action(UserPanel context, Key key, String name, Icon icon)
	{
		super(name, icon);
		this.init(context, key);
	}
	
	private void init(UserPanel context, Key key)
	{
		this.context = context;
		this.key = key;
		this.putActionType(key.actionType);
		this.putValue("bgcolor", new Color(key.bgRed, key.bgGreen, key.bgBlue)); //$NON-NLS-1$
	}
	
	protected void putActionType(Integer actionType)
	{
		this.putValue(Action.POS_KEY_ACTION_TYPE, actionType);
	}
	
	public void putParent(String parentKey, String objectKey, Table parent)
	{
		this.putValue(parentKey, objectKey);
		this.putValue(objectKey, parent);
	}
	
	public Table getParent(String key)
	{
		return (Table) this.getValue(key);
	}
	
	public Integer getActionType()
	{
		return (Integer) this.getValue(Action.POS_KEY_ACTION_TYPE);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (!this.getActionType().equals(Action.POS_ACTION_NO_ACTION))
		{
			PosEvent p = new PosEvent(this);
			PosEventListener[] l = (PosEventListener[]) this.posEventListeners.toArray(new PosEventListener[0]);
			for (int i = 0; i < l.length; i++)
			{
				l[i].posEventPerformed(p);
			}
		}
	}
	
	public void initialize()
	{
	}
	
	public Key getKey()
	{
		return this.key;
	}
	
	public boolean addPosEventListener(PosEventListener listener)
	{
		if (this.posEventListeners.contains(listener))
		{
			return true;
		}
		else
		{
			return this.posEventListeners.add(listener);
		}
	}
	
	public boolean removePosEventListener(PosEventListener listener)
	{
		return this.posEventListeners.remove(listener);
	}
	
	protected UserPanel context;
	protected ArrayList posEventListeners = new ArrayList();
	protected Key key;
	
	/*
	 * Die folgenden Werte stehen fÃ¼r Aktionen. Sie werden von den Listeners je
	 * nach Bedarf abgefragt.
	 */
	public static final Integer POS_ACTION_NO_ACTION = new Integer(0);
	
	public static final Integer POS_ACTION_CLEAR = new Integer(1);
	
	public static final Integer POS_ACTION_STORE_RECEIPT = new Integer(100);
	public static final Integer POS_ACTION_STORE_RECEIPT_PUSH_DRAWER_1 = new Integer(101);
	public static final Integer POS_ACTION_STORE_RECEIPT_PUSH_DRAWER_2 = new Integer(102);
	
	public static final Integer POS_ACTION_SET_PRODUCT_GROUP = new Integer(200);
	public static final Integer POS_ACTION_SET_QUANTITY = new Integer(201);
	public static final Integer POS_ACTION_SET_PRICE = new Integer(202);
	public static final Integer POS_ACTION_SET_DISCOUNT = new Integer(203);
	public static final Integer POS_ACTION_SET_TAX = new Integer(204);
	public static final Integer POS_ACTION_SET_OPTION = new Integer(205);
	public static final Integer POS_ACTION_SET_UPDATE_CUSTOMER_ACCOUNT = new Integer(206);
	public static final Integer POS_ACTION_RETURN = new Integer(250);
	
	public static final Integer POS_ACTION_SET_AMOUNT = new Integer(300);
	public static final Integer POS_ACTION_SET_PAYMENT_TYPE = new Integer(301);
	public static final Integer POS_ACTION_SET_QUOTATION = new Integer(302);
	
	public static final Integer POS_ACTION_STORE_ENTRY = new Integer(400);
	public static final Integer POS_ACTION_DELETE_ENTRY = new Integer(401);
	
	public static final Integer POS_ACTION_PARK_RECEIPT = new Integer(600);
	public static final Integer POS_ACTION_SHOW_PARK_LIST = new Integer(601);
	public static final Integer POS_ACTION_SHOW_RECEIPT_LIST = new Integer(620);
	public static final Integer POS_ACTION_REVERSE = new Integer(700);
	
	public static final Integer POS_ACTION_CASHDRAWER_0 = new Integer(800);
	public static final Integer POS_ACTION_CASHDRAWER_1 = new Integer(801);
	
	public static final Integer POS_ACTION_SETTLE_DAY = new Integer(900);
	public static final Integer POS_ACTION_GET_SALES = new Integer(901);
	public static final Integer POS_ACTION_TEST_SETTLE_DAY = new Integer(902);
	
	public static final Integer POS_ACTION_CHOOSE_CUSTOMER = new Integer(1001);
	
	// Die folgenden Keys betreffen die verschiedenen möglichen Actions fest
	// Die Listener fragen nur diejenigen Keys/Value Paare ab, die fuer sie
	// relevant sind.
	public static final String POS_KEY_ACTION = "keyAction"; //$NON-NLS-1$
	public static final String POS_KEY_ACTION_EVENT = "keyActionEvent"; //$NON-NLS-1$
	public static final String POS_KEY_ACTION_TYPE = "keyActionType"; //$NON-NLS-1$
	public static final String POS_KEY_REQUESTED_STATE = "keyRequestedState"; //$NON-NLS-1$
	
	public static final String POS_KEY_CLASS_NAME = "keyClassName"; //$NON-NLS-1$
	public static final String POS_KEY_PRODUCT_GROUP = ProductGroup.class.getName();
	public static final String POS_KEY_TAX = Tax.class.getName();
	public static final String POS_KEY_TAX_RATE = TaxRate.class.getName();
	public static final String POS_KEY_OPTION = Option.class.getName();
	public static final String POS_KEY_PAYMENT_TYPE = PaymentType.class.getName();
	
	public static final String POS_KEY_QUANTITY = "keyQuantity"; //$NON-NLS-1$
	public static final String POS_KEY_PRICE = "keyPrice"; //$NON-NLS-1$
	public static final String POS_KEY_DISCOUNT = "keyDiscount"; //$NON-NLS-1$
	public static final String POS_KEY_VALUE = "keyValue"; //$NON-NLS-1$
	public static final String POS_KEY_AMOUNT = "keyAmount"; //$NON-NLS-1$
	public static final String POS_KEY_QUOTATION = "keyQuotation"; //$NON-NLS-1$
	public static final String POS_KEY_DRAWER_NUMBER = "keyDrawerNumber"; //$NON-NLS-1$
	
	protected static final String POS_KEY_ACTION_STATE = "keyActionState"; //$NON-NLS-1$
}
