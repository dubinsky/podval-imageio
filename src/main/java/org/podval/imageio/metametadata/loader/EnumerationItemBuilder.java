/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.xml.sax.Attributes;


public class EnumerationItemBuilder extends Builder {

  public EnumerationItemBuilder(Builder previous) {
    super(previous);
  }


  public Builder startElement(String name, Attributes attributes) {
    return null;
  }


  public String toString() {
    /** @todo ??? */
    return "<item/>";
  }
}
