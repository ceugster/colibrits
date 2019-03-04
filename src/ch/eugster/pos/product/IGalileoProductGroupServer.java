/*
 * Created on 09.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.product;

/**
 * @author ceugster
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface IGalileoProductGroupServer {
	public abstract boolean connect(String path);
	public abstract void disconnect();
	public abstract String[] getProductGroupCodeList();
	public abstract Object getProductGroup(String code);
	public abstract String getProductGroupName();
}