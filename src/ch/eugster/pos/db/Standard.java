/*
 * Created on 16.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.jdom.Element;

import ch.eugster.pos.util.Config;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Standard extends Connection
{
	
	/**
	 * 
	 */
	public Standard()
	{
		super();
	}
	
	/**
	 * @param Element
	 */
	public Standard(Element element)
	{
		super(element);
	}
	
	/**
	 * @param priority
	 * @param active
	 * @param name
	 */
	public Standard(String id, String name)
	{
		super(id, name);
	}
	
	protected Element getConnectionData()
	{
		return Config.getInstance().getDatabaseStandardConnection();
	}
	
	public String getCode()
	{
		return "std";
	}
	
	public void updateSalespointStocks()
	{
		/**
		 * Für die hier gesammelten Zahlungsarten benötigen wir Kassenstocks
		 */
		PaymentType[] pt = PaymentType.selectAll(false);
		Collection currencies = new ArrayList();
		for (int i = 0; i < pt.length; i++)
		{
			if (!currencies.contains(pt[i].getForeignCurrency()))
			{
				currencies.add(pt[i].getForeignCurrency());
			}
		}
		/**
		 * Jetzt durchlaufen wir die Zahlungsarten, für die jede Kasse einen
		 * Stock haben muss
		 */
		Iterator iterator = currencies.iterator();
		while (iterator.hasNext())
		{
			this.testForStock((ForeignCurrency) iterator.next());
		}
	}
	
	public void testForStock(ForeignCurrency currency)
	{
		Salespoint[] salespoints = Salespoint.selectAll(false);
		for (int i = 0; i < salespoints.length; i++)
		{
			this.salespointHasStock(currency, salespoints[i]);
		}
	}
	
	private void salespointHasStock(ForeignCurrency currency, Salespoint salespoint)
	{
		Collection stocks = salespoint.getStocks();
		Iterator iterator = stocks.iterator();
		while (iterator.hasNext())
		{
			Stock stock = (Stock) iterator.next();
			if (stock.getForeignCurrency().getId().equals(currency.getId())) return;
		}
		/**
		 * Keinen passenden Stock gefunden: Einfügen
		 */
		this.insertStock(currency, salespoint);
	}
	
	private void insertStock(ForeignCurrency currency, Salespoint salespoint)
	{
		Stock stock = new Stock(salespoint, currency);
		stock.store();
	}
}
