package ch.eugster.pos.product;

import com4j.COM4J;

/**
 * Defines methods to create COM objects
 */
public abstract class KundenserverFactory
{
	private KundenserverFactory()
	{
	} // instanciation is not allowed
	
	/**
	 * kundenserver.kundenserver
	 */
	public static ch.eugster.pos.product.Ikundenserver createkundenserver()
	{
		return COM4J.createInstance(ch.eugster.pos.product.Ikundenserver.class,
						"{1DAA0DEE-0086-4FB7-8587-B66D13E75AC3}");
	}
}
