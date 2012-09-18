/*
 * Created on 20.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.db;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.jdom.Element;

import ch.eugster.pos.client.gui.PosButton;
import ch.eugster.pos.client.gui.UserPanel;
import ch.eugster.pos.events.Action;
import ch.eugster.pos.util.XMLLoader;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class FixKey extends Key
{
	
	public String block = ""; //$NON-NLS-1$
	
	private FixKeyGroup fixKeyGroup;
	private Long fixKeyGroupId;
	// 10052
	// Hintergrundfarbe für Failover
	private int bgRedFailover = 255;
	private int bgGreenFailover = 222;
	private int bgBlueFailover = 222;
	
	/**
	 * 
	 */
	public FixKey()
	{
		super();
	}
	
	public void setFixKeyGroup(FixKeyGroup group)
	{
		this.fixKeyGroup = group;
		this.fixKeyGroupId = group.getId();
	}
	
	public FixKeyGroup getFixKeyGroup()
	{
		return this.fixKeyGroup;
	}
	
	public Long getFixKeyGroupId()
	{
		return this.fixKeyGroupId;
	}
	
	public PosButton createButton(UserPanel context)
	{
		Action action = this.getPosAction(context);
		if (action == null)
		{
			return null;
		}
		PosButton button = new PosButton(context, action);
		Table.addDatabaseErrorListener(button);
		button.setFont(button.getFont().deriveFont(this.fontStyle, (float) this.fontSize));
		button.setHorizontalAlignment(this.align);
		button.setVerticalAlignment(this.valign);
		button.setForeground(new Color(this.fgRed, this.fgGreen, this.fgBlue));
		button.setBackground(new Color(this.bgRed, this.bgGreen, this.bgBlue));
		button.setFailoverBackgroundColor(new Color(this.bgRedFailover, this.bgGreenFailover, this.bgBlueFailover));
		button.setText(this.text);
		button.setActionCommand(this.command);
		File file = new File(this.imagepath);
		if (file.exists())
		{
			button.setIcon(new ImageIcon(file.getAbsolutePath()));
		}
		return button;
	}
	
	protected Action getPosAction(UserPanel context)
	{
		Action action = null;
		action = this.createAction(context);
		action.putValue(Action.POS_KEY_CLASS_NAME, ""); //$NON-NLS-1$
		return action;
	}
	
	public boolean isRemovable()
	{
		return true;
	}
	
	public static FixKey selectById(Long pk)
	{
		FixKey key = new FixKey();
		Criteria criteria = new Criteria();
		criteria.addEqualTo("id", pk); //$NON-NLS-1$
		Query query = QueryFactory.newQuery(FixKey.class, criteria);
		Collection keys = Table.select(query);
		Iterator i = keys.iterator();
		if (i.hasNext())
		{
			key = (FixKey) i.next();
		}
		return key;
	}
	
	public static FixKey[] selectByBlock(String block)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("block", block); //$NON-NLS-1$
		//		criteria.addOrderBy("row", true); //$NON-NLS-1$
		//		criteria.addOrderBy("column", true); //$NON-NLS-1$
		QueryByCriteria query = QueryFactory.newQuery(FixKey.class, criteria);
		query.addOrderBy("row", true); //$NON-NLS-1$
		query.addOrderBy("column", true); //$NON-NLS-1$
		Collection keys = Table.select(query);
		return (FixKey[]) keys.toArray(new FixKey[0]);
	}
	
	public static FixKey[] selectByGroup(FixKeyGroup group)
	{
		Criteria criteria = new Criteria();
		criteria.addEqualTo("fixKeyGroupId", group.getId()); //$NON-NLS-1$
		//		criteria.addOrderBy("row", true); //$NON-NLS-1$
		//		criteria.addOrderBy("column", true); //$NON-NLS-1$
		QueryByCriteria query = QueryFactory.newQuery(FixKey.class, criteria);
		query.addOrderBy("row", true); //$NON-NLS-1$
		query.addOrderBy("column", true); //$NON-NLS-1$
		Collection keys = Table.select(query);
		return (FixKey[]) keys.toArray(new FixKey[0]);
	}
	
	public static FixKey[] selectAll()
	{
		Criteria criteria = new Criteria();
		Query query = QueryFactory.newQuery(FixKey.class, criteria);
		Collection keys = Table.select(query);
		return (FixKey[]) keys.toArray(new FixKey[0]);
	}
	
	public static void readDBRecords()
	{
		FixKey[] fixKeys = FixKey.selectAll();
		for (int i = 0; i < fixKeys.length; i++)
		{
			FixKey.put(fixKeys[i]);
		}
	}
	
	public static FixKey getById(Long id)
	{
		return (FixKey) FixKey.records.get(id);
	}
	
	public static FixKey[] getByBlock(String block)
	{
		ArrayList keys = new ArrayList();
		Enumeration enumeration = FixKey.records.elements();
		while (enumeration.hasMoreElements())
		{
			FixKey key = (FixKey) enumeration.nextElement();
			if (key.block.equals(block))
			{
				keys.add(key);
			}
		}
		return (FixKey[]) keys.toArray(new FixKey[0]);
	}
	
	public static FixKey[] getByGroup(FixKeyGroup group)
	{
		ArrayList keys = new ArrayList();
		Enumeration enumeration = FixKey.records.elements();
		while (enumeration.hasMoreElements())
		{
			FixKey key = (FixKey) enumeration.nextElement();
			if (key.getFixKeyGroupId().equals(group.getId()))
			{
				keys.add(key);
			}
		}
		return (FixKey[]) keys.toArray(new FixKey[0]);
	}
	
	public static FixKey[] getAll()
	{
		ArrayList keys = new ArrayList();
		Enumeration enumeration = FixKey.records.elements();
		while (enumeration.hasMoreElements())
		{
			keys.add(enumeration.nextElement());
		}
		return (FixKey[]) keys.toArray(new FixKey[0]);
	}
	
	private static void clearData()
	{
		FixKey.records.clear();
	}
	
	private static void put(FixKey fixKey)
	{
		FixKey.records.put(fixKey.getId(), fixKey);
	}
	
	public static Element writeXMLRecords(Element root)
	{
		Element table = Database.getTemporary().getTable("fix-key"); //$NON-NLS-1$
		if (table == null)
		{
			table = new Element("table"); //$NON-NLS-1$
			table.setAttribute("name", "fix-key"); //$NON-NLS-1$ //$NON-NLS-2$
			root.addContent(table);
		}
		
		Enumeration entries = FixKey.records.elements();
		while (entries.hasMoreElements())
		{
			FixKey rec = (FixKey) entries.nextElement();
			Element record = rec.getJDOMRecordAttributes();
			table.addContent(record);
		}
		return root;
	}
	
	protected Element getJDOMRecordAttributes()
	{
		Element record = super.getJDOMRecordAttributes();
		
		Element bl = new Element("field"); //$NON-NLS-1$
		bl.setAttribute("name", "block"); //$NON-NLS-1$ //$NON-NLS-2$
		bl.setAttribute("value", this.block); //$NON-NLS-1$
		record.addContent(bl);
		
		Element pi = new Element("field"); //$NON-NLS-1$
		pi.setAttribute("name", "fix-key-group-id"); //$NON-NLS-1$ //$NON-NLS-2$
		pi.setAttribute("value", this.getFixKeyGroupId().toString()); //$NON-NLS-1$
		record.addContent(pi);
		
		// 10052
		Element r = new Element("field"); //$NON-NLS-1$
		r.setAttribute("name", "fix-key-red"); //$NON-NLS-1$ //$NON-NLS-2$
		r.setAttribute("value", Integer.toString(this.bgRedFailover)); //$NON-NLS-1$
		record.addContent(r);
		
		Element g = new Element("field"); //$NON-NLS-1$
		g.setAttribute("name", "fix-key-green"); //$NON-NLS-1$ //$NON-NLS-2$
		g.setAttribute("value", Integer.toString(this.bgGreenFailover)); //$NON-NLS-1$
		record.addContent(g);
		
		Element b = new Element("field"); //$NON-NLS-1$
		b.setAttribute("name", "fix-key-blue"); //$NON-NLS-1$ //$NON-NLS-2$
		b.setAttribute("value", Integer.toString(this.bgBlueFailover)); //$NON-NLS-1$
		record.addContent(b);
		// 10052
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
			if (field.getAttributeValue("name").equals("block")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.block = field.getAttributeValue("value"); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("fix-key-group-id")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.setFixKeyGroup(FixKeyGroup.getById(new Long(XMLLoader.getLong(field.getAttributeValue("value"))))); //$NON-NLS-1$
			}
			// 10052
			else if (field.getAttributeValue("name").equals("fix-key-red")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.bgRedFailover = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("fix-key-green")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.bgGreenFailover = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
			}
			else if (field.getAttributeValue("name").equals("fix-key-blue")) { //$NON-NLS-1$ //$NON-NLS-2$
				this.bgBlueFailover = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
			}
			// 10052
		}
	}
	
	// 10052
	/**
	 * @return
	 */
	public int getBgBlueFailover()
	{
		return this.bgBlueFailover;
	}
	
	/**
	 * @return
	 */
	public int getBgGreenFailover()
	{
		return this.bgGreenFailover;
	}
	
	/**
	 * @return
	 */
	public int getBgRedFailover()
	{
		return this.bgRedFailover;
	}
	
	/**
	 * @param integer
	 */
	public void setBgBlueFailover(int blue)
	{
		this.bgBlueFailover = blue;
	}
	
	/**
	 * @param integer
	 */
	public void setBgGreenFailover(int green)
	{
		this.bgGreenFailover = green;
	}
	
	/**
	 * @param integer
	 */
	public void setBgRedFailover(int red)
	{
		this.bgRedFailover = red;
	}
	
	// 10052
	
	public static void readXML()
	{
		FixKey.clearData();
		Element[] elements = Database.getTemporary().getRecords("fix-key"); //$NON-NLS-1$
		for (int i = 0; i < elements.length; i++)
		{
			FixKey key = new FixKey();
			key.setData(elements[i]);
			FixKey.put(key);
		}
	}
	
	private static Hashtable records = new Hashtable();
	
}
