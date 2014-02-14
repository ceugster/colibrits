package ch.eugster.pos.product;

import com4j.COM4J;

/**
 * Defines methods to create COM objects
 */
public abstract class GalserveFactory
{
	private GalserveFactory()
	{
	} // instanciation is not allowed
	
	public static ch.eugster.pos.product.Igdserve creategdserve()
	{
		return COM4J.createInstance(ch.eugster.pos.product.Igdserve.class, "{B94E7B40-4E78-11D8-B851-0002E3178697}");
	}
}
