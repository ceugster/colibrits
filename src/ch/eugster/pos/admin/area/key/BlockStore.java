/*
 * Created on 09.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.key;

import org.eclipse.jface.viewers.IStructuredSelection;

import ch.eugster.pos.db.Block;
import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class BlockStore extends PersistentDBStore
{
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.model.PersistentDBStore#load()
	 */
	protected void load()
	{
		Block block = (Block) this.element;
		this.putDefault(BlockFieldEditorPage.KEY_NAME, block.name);
		this.putDefault(BlockFieldEditorPage.KEY_FONT_SIZE, new Float(block.fontSize));
		this.putDefault(BlockFieldEditorPage.KEY_FONT_STYLE, new Integer(block.fontStyle));
		this.putToDefaults();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.model.PersistentDBStore#store()
	 */
	protected void store()
	{
		Block block = (Block) this.element;
		block.name = this.getString(BlockFieldEditorPage.KEY_NAME);
		block.fontSize = this.getDouble(BlockFieldEditorPage.KEY_FONT_SIZE).floatValue();
		block.fontStyle = this.getInt(BlockFieldEditorPage.KEY_FONT_STYLE).intValue();
		this.setDirty(false);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.admin.model.PersistentDBStore#getErrorMessage(ch.eugster
	 * .pos.db.DBResult)
	 */
	protected String getErrorMessage(DBResult result)
	{
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.model.Store#initialize()
	 */
	public void initialize()
	{
		this.setElement(new Block());
	}
	
	public void initialize(IStructuredSelection selection)
	{
		this.setElement(new Block());
	}
	
}
