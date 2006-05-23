/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.MetaMetaData;
import org.podval.imageio.metametadata.MetaMetaDataException;

import org.xml.sax.Attributes;


public abstract class Builder<T> {

  protected Builder(Builder<?> previous, T thing) {
    this.previous = previous;
    this.thing = thing;
  }


  public abstract Builder<?> startElement(String name, Attributes attributes)
    throws MetaMetaDataException;


  protected void check() throws MetaMetaDataException {
  }


  protected final MetaMetaData getMetaMetaData() {
    Builder candidate = this;
    while (!(candidate instanceof RootBuilder)) {
      candidate = candidate.previous;
    }
    return ((RootBuilder) candidate).thing;
  }


  protected static String getName(Attributes attributes) throws MetaMetaDataException {
    return getRequiredAttribute("name", attributes);
  }


  /** @todo eliminate; throw exceptions from the MetaMetaData objects themselves... */
  protected static String getRequiredAttribute(String name, Attributes attributes)
    throws MetaMetaDataException
  {
    String result = getAttribute(name, attributes);

    if (result == null) {
      throw new MetaMetaDataException("Missing required attribute " + name);
    }

    return result;
  }


  protected static String getAttribute(String name, Attributes attributes) {
    return attributes.getValue(name);
  }


  protected final boolean getBooleanAttribute(String name, Attributes attributes) {
    return Boolean.valueOf(getAttribute(name, attributes));
  }


  protected final int getIntegerAttribute(String name, Attributes attributes)
    throws MetaMetaDataException
  {
    try {
      String value = getAttribute(name, attributes);
      return (value == null) ? 0 : Integer.valueOf(value);
    } catch (NumberFormatException e) {
      throw new MetaMetaDataException(e);
    }
  }


  public final String toString() {
    return thing.toString();
  }


  public final Builder previous;


  public final T thing;
}
