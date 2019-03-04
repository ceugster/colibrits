/*
 * Created on 18.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.admin.area.coin;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import ch.eugster.pos.db.Coin;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class CoinViewerSorter extends ViewerSorter
{
	/**
	 * @param criteria
	 */
	public CoinViewerSorter()
	{
		super();
	}
	
	public int compare(Viewer viewer, Object element1, Object element2)
	{
		
		if (element1 instanceof Coin && element2 instanceof Coin)
		{
			Coin coin1 = (Coin) element1;
			Coin coin2 = (Coin) element2;
			
			return this.compare(coin1, coin2);
		}
		else
			return 0;
	}
	
	private int compare(Coin coin1, Coin coin2)
	{
		return new Double(coin1.value).compareTo(new Double(coin2.value));
	}
	
}