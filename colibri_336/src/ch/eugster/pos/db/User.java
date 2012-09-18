/*
 * Created on 13.03.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package ch.eugster.pos.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.util.collections.RemovalAwareCollection;
import org.jdom.Element;

import ch.eugster.pos.Messages;
import ch.eugster.pos.events.UserChangeEvent;
import ch.eugster.pos.events.UserChangeListener;
import ch.eugster.pos.util.XMLLoader;

/**
 * @author administrator
 */
public class User extends Table
{
	
	public String username = ""; //$NON-NLS-1$
	public String password = ""; //$NON-NLS-1$
	public Long posLogin = new Long(0L);
	public int status = User.USER_STATE_EMPLOYEE;
	public Boolean defaultUser = new Boolean(false);
	private boolean reverseReceipts;
	
	private RemovalAwareCollection userAccesses = new RemovalAwareCollection();
	
	public User()
	{
	}
	
	public boolean isRemovable()
	{
		return !Receipt.exist("userId", this.getId()); //$NON-NLS-1$
	}
	
	public boolean getReverseReceipts()
	{
		return this.reverseReceipts;
	}
	
	public void setReverseReceipts(boolean reverse)
	{
		this.reverseReceipts = reverse;
	}
	
	public static User selectById(Long pk)
	{
		User user = new User();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("id", pk); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(User.class, criteria);
		Collection users = Table.select(query);
		Iterator i = users.iterator();
		if (i.hasNext())
		{
			user = (User) i.next();
		}
		return user;
	}
	
	public static User selectByPosLogin(Long posLogin, boolean deletedToo)
	{
		User user = new User();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("posLogin", posLogin); //$NON-NLS-1$
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(User.class, criteria);
		Collection users = Table.select(query);
		Iterator i = users.iterator();
		if (i.hasNext())
		{
			user = (User) i.next();
		}
		return user;
	}
	
	public static User selectByUsername(String username, boolean deletedToo)
	{
		User user = new User();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("username", username); //$NON-NLS-1$
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(User.class, criteria);
		Collection users = Table.select(query);
		Iterator i = users.iterator();
		if (i.hasNext())
		{
			user = (User) i.next();
		}
		return user;
	}
	
	public static User[] selectAll(boolean deletedToo)
	{
		Criteria criteria = new Criteria();
		if (!deletedToo) criteria.addEqualTo("deleted", new Boolean(false)); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(User.class, criteria);
		Collection users = Table.select(query);
		return (User[]) users.toArray(new User[0]);
	}
	
	public static User selectDefaultUser()
	{
		if (User.defUser == null)
		{
			Criteria criteria = new Criteria();
			criteria.addEqualTo("defaultUser", new Boolean(true)); //$NON-NLS-1$
			Query query = QueryFactory.newQuery(User.class, criteria);
			Collection users = Table.select(query);
			Iterator i = users.iterator();
			if (i.hasNext())
			{
				User.defUser = (User) i.next();
			}
			else
			{
				if (Database.getCurrent().equals(Database.getStandard()))
				{
					Database.getCurrent().getBroker().beginTransaction();
					User user = User.selectById(new Long(1l));
					user.defaultUser = new Boolean(true);
					user.store();
					User.defUser = user;
					Database.getCurrent().getBroker().commitTransaction();
				}
			}
		}
		return User.defUser;
	}
	
	/*
	 * Wird vorlaeufig nur im Administrationstool benutzt.
	 */
	public static void setCurrentUser(User user)
	{
		User oldUser = User.currentUser;
		User.currentUser = user;
		User.fireUserChangedEvent(oldUser, User.currentUser);
	}
	
	public static User getCurrentUser()
	{
		return User.currentUser;
	}
	
	public static void setDefaultUser(User user)
	{
		User.defUser = user;
	}
	
	public static User getDefaultUser()
	{
		return User.defUser;
	}
	
	public static void readDBRecords()
	{
		User[] users = User.selectAll(false);
		for (int i = 0; i < users.length; i++)
		{
			User.put(users[i]);
		}
	}
	
	public static User getById(Long id)
	{
		return (User) User.records.get(id);
	}
	
	public static User getByPosLogin(Long posLogin, boolean deletedToo)
	{
		User user = (User) User.posLoginIndex.get(posLogin);
		if (deletedToo || user != null && !user.deleted)
		{
			return (User) User.posLoginIndex.get(posLogin);
		}
		return new User();
	}
	
	public static User getByUsername(String username)
	{
		return (User) User.usernameIndex.get(username);
	}
	
	private static void clearData()
	{
		User.records.clear();
		User.posLoginIndex.clear();
		User.usernameIndex.clear();
	}
	
	private static void put(User user)
	{
		User.records.put(user.getId(), user);
		User.posLoginIndex.put(user.posLogin, user);
		User.usernameIndex.put(user.username, user);
	}
	
	public static Element writeXMLRecords(Element root)
	{
		Element table = Database.getTemporary().getTable("user"); //$NON-NLS-1$
		if (table == null)
		{
			table = new Element("table"); //$NON-NLS-1$
			table.setAttribute("name", "user"); //$NON-NLS-1$ //$NON-NLS-2$
			root.addContent(table);
		}
		
		Enumeration entries = User.records.elements();
		while (entries.hasMoreElements())
		{
			User user = (User) entries.nextElement();
			Element record = user.getJDOMRecordAttributes();
			table.addContent(record);
		}
		return root;
	}
	
	protected Element getJDOMRecordAttributes()
	{
		Element record = super.getJDOMRecordAttributes();
		
		Element un = new Element("field"); //$NON-NLS-1$
		un.setAttribute("name", "username"); //$NON-NLS-1$ //$NON-NLS-2$
		un.setAttribute("value", this.username); //$NON-NLS-1$
		record.addContent(un);
		
		Element pw = new Element("field"); //$NON-NLS-1$
		pw.setAttribute("name", "password"); //$NON-NLS-1$ //$NON-NLS-2$
		pw.setAttribute("value", this.password); //$NON-NLS-1$
		record.addContent(pw);
		
		Element pl = new Element("field"); //$NON-NLS-1$
		pl.setAttribute("name", "pos-login"); //$NON-NLS-1$ //$NON-NLS-2$
		pl.setAttribute("value", this.posLogin.toString()); //$NON-NLS-1$
		record.addContent(pl);
		
		Element st = new Element("field"); //$NON-NLS-1$
		st.setAttribute("name", "status"); //$NON-NLS-1$ //$NON-NLS-2$
		st.setAttribute("value", Integer.toString(this.status)); //$NON-NLS-1$
		record.addContent(st);
		
		Element du = new Element("field"); //$NON-NLS-1$
		du.setAttribute("name", "default-user"); //$NON-NLS-1$ //$NON-NLS-2$
		du.setAttribute("value", this.defaultUser.toString()); //$NON-NLS-1$
		record.addContent(du);
		
		Element rr = new Element("field"); //$NON-NLS-1$
		rr.setAttribute("name", "reverse-receipts"); //$NON-NLS-1$ //$NON-NLS-2$
		rr.setAttribute("value", Boolean.toString(this.reverseReceipts)); //$NON-NLS-1$
		record.addContent(rr);
		
		return record;
	}
	
	protected void setData(Element record)
	{
		super.getData(record);
		
		List fields = record.getChildren("field"); //$NON-NLS-1$
		Iterator iter = fields.iterator();
		while (iter.hasNext())
		{
			Element field = (Element) iter.next();
			if (field.getAttributeValue("name").equals("username")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.username = field.getAttributeValue("value"); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("password")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.password = field.getAttributeValue("value"); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("pos-login")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.posLogin = new Long(XMLLoader.getLong(field.getAttributeValue("value"))); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("status")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.status = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("default-user")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.defaultUser = new Boolean(field.getAttributeValue("value")); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("reverse-receipts")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.reverseReceipts = new Boolean(field.getAttributeValue("value")).booleanValue(); //$NON-NLS-1$
			}
		}
	}
	
	public static void readXML()
	{
		User.clearData();
		Element[] elements = Database.getTemporary().getRecords("user"); //$NON-NLS-1$
		for (int i = 0; i < elements.length; i++)
		{
			User user = new User();
			user.setData(elements[i]);
			User.put(user);
		}
	}
	
	public static boolean addUserChangeListener(UserChangeListener l)
	{
		return User.userChangeListeners.add(l);
	}
	
	public static boolean removeUserChangeListener(UserChangeListener l)
	{
		return User.userChangeListeners.remove(l);
	}
	
	public static void fireUserChangedEvent(User oldUser, User newUser)
	{
		UserChangeEvent event = new UserChangeEvent(oldUser, newUser);
		UserChangeListener[] listeners = (UserChangeListener[]) User.userChangeListeners
						.toArray(new UserChangeListener[0]);
		for (int i = 0; i < listeners.length; i++)
		{
			listeners[i].userChanged(event);
		}
	}
	
	private static Hashtable records = new Hashtable();
	private static Hashtable posLoginIndex = new Hashtable();
	private static Hashtable usernameIndex = new Hashtable();
	
	private static User defUser = null;
	private static User currentUser = null;
	private static ArrayList userChangeListeners = new ArrayList();
	
	public static final int USER_STATE_ADMINISTRATOR = 0;
	public static final int USER_STATE_MANAGER = 1;
	public static final int USER_STATE_EMPLOYEE = 2;
	
	public static final Integer[] USER_STATE_VALUE =
	{ new Integer(User.USER_STATE_ADMINISTRATOR), new Integer(User.USER_STATE_MANAGER),
					new Integer(User.USER_STATE_EMPLOYEE) };
	public static final String[] USER_STATE_TEXT =
	{ Messages.getString("UserFieldEditorPage.Administrator_2"), Messages.getString("UserFieldEditorPage.Manager_3"),
					Messages.getString("UserFieldEditorPage.Benutzer_4") };
}
