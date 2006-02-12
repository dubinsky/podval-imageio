/* $Id$ */

package org.podval.imageio.metametadata.loader;


public abstract class ThingBuilder<T> extends Builder {

  protected ThingBuilder(Builder previous, T thing) {
    super(previous);
    this.thing = thing;
  }


  public final String toString() {
    return thing.toString();
  }


  public final T thing;
}
