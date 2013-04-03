package ch.eugster.pos.product  ;

import com4j.*;

/**
 * Defines methods to create COM objects
 */
public abstract class WgserveFactory {
  private WgserveFactory() {} // instanciation is not allowed


  public static ch.eugster.pos.product.Iwgserve createwgserve() {
    return COM4J.createInstance( ch.eugster.pos.product.Iwgserve.class, "{9C3F93A0-270D-11D8-B851-0002E3178697}" );
  }
}
