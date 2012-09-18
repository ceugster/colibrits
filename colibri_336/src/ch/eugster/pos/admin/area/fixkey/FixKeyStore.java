/*
 * Created on 29.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.fixkey;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.RGB;

import ch.eugster.pos.db.DBResult;
import ch.eugster.pos.db.FixKey;
import ch.eugster.pos.swt.PersistentDBStore;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class FixKeyStore extends PersistentDBStore
{
	
	public void initialize()
	{
		this.setElement(new FixKey());
	}
	
	public void initialize(IStructuredSelection selection)
	{
		this.setElement(new FixKey());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.Store#load()
	 */
	protected void load()
	{
		FixKey key = (FixKey) this.element;
		RGB fg = new RGB(key.fgRed, key.fgGreen, key.fgBlue);
		RGB bg = new RGB(key.bgRed, key.bgGreen, key.bgBlue);
		// 10052
		// Hintergrundfarbe für Failover
		RGB bgf = new RGB(key.getBgRedFailover(), key.getBgGreenFailover(), key.getBgBlueFailover());
		this.putDefault(FixKeyFieldEditorPage.KEY_NAME, key.text);
		this.putDefault(FixKeyFieldEditorPage.KEY_SIZE, new Float(key.fontSize));
		this.putDefault(FixKeyFieldEditorPage.KEY_STYLE, new Integer(key.fontStyle));
		this.putDefault(FixKeyFieldEditorPage.KEY_HORZ_POS, new Integer(key.align));
		this.putDefault(FixKeyFieldEditorPage.KEY_VERT_POS, new Integer(key.valign));
		this.putDefault(FixKeyFieldEditorPage.KEY_FG, fg);
		this.putDefault(FixKeyFieldEditorPage.KEY_BG, bg);
		this.putDefault(FixKeyFieldEditorPage.KEY_IMAGE_PATH, key.imagepath);
		this.putDefault(FixKeyFieldEditorPage.KEY_TEXT_HORZ_POS, new Integer(key.relHorizontalTextPos));
		this.putDefault(FixKeyFieldEditorPage.KEY_TEXT_VERT_POS, new Integer(key.relVerticalTextPos));
		// 10052
		// Hintergrundfarbe für Failover
		this.putDefault(FixKeyFieldEditorPage.KEY_BG_FAILOVER, bgf);
		this.putToDefaults();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.admin.framework.Store#load()
	 */
	protected void store()
	{
		FixKey key = (FixKey) this.element;
		RGB fg = (RGB) this.getValue(FixKeyFieldEditorPage.KEY_FG);
		RGB bg = (RGB) this.getValue(FixKeyFieldEditorPage.KEY_BG);
		// 10052
		// Hintergrundfarbe für Failover
		RGB bgf = (RGB) this.getValue(FixKeyFieldEditorPage.KEY_BG_FAILOVER);
		key.text = this.getString(FixKeyFieldEditorPage.KEY_NAME);
		key.fontSize = this.getDouble(FixKeyFieldEditorPage.KEY_SIZE).floatValue();
		key.fontStyle = this.getInt(FixKeyFieldEditorPage.KEY_STYLE).intValue();
		key.align = this.getInt(FixKeyFieldEditorPage.KEY_HORZ_POS).intValue();
		key.valign = this.getInt(FixKeyFieldEditorPage.KEY_VERT_POS).intValue();
		key.fgRed = new Integer(fg.red).intValue();
		key.fgGreen = new Integer(fg.green).intValue();
		key.fgBlue = new Integer(fg.blue).intValue();
		key.bgRed = new Integer(bg.red).intValue();
		key.bgGreen = new Integer(bg.green).intValue();
		key.bgBlue = new Integer(bg.blue).intValue();
		// 10052
		// Hintergrundfarbe für Failover
		key.setBgRedFailover(bgf.red);
		key.setBgGreenFailover(bgf.green);
		key.setBgBlueFailover(bgf.blue);
		// 10052
		key.imagepath = this.getString(FixKeyFieldEditorPage.KEY_IMAGE_PATH);
		key.relHorizontalTextPos = this.getInt(FixKeyFieldEditorPage.KEY_TEXT_HORZ_POS).intValue();
		key.relVerticalTextPos = this.getInt(FixKeyFieldEditorPage.KEY_TEXT_VERT_POS).intValue();
		this.setDirty(false);
	}
	
	public String getErrorMessage(DBResult result)
	{
		return ""; //$NON-NLS-1$
	}
	
}
