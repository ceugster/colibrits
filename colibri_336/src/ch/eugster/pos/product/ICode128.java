/*
 * Created on 19.12.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.product;

import java.util.Date;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface ICode128
{
	public abstract Date getDate(String code);
	
	public abstract int getSupplier(String code);
	
	public abstract String getTax(String code);
	
	public abstract int getQualifier(String code);
	
	public abstract double getOrdinalPrice(String code);
	
	public abstract double getNetPrice(String code);
	
	public abstract String getProductGroup(String code);
	
	public abstract String getArticleCode(String code);
	
	public abstract boolean searchGalileo();
}