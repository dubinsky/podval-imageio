/* @(#)$Id$ */

package org.podval.imageio.metametadata;


public class EnumerationItem {

  public EnumerationItem(String name, String value) throws MetaMetaDataException {
    if ((name == null) && (value == null)) {
      throw new MetaMetaDataException("Name or value must be present");
    }

    this.name = name;
    this.value = value;
  }


  public EnumerationItem(int tag) {
    this.name = null;
    this.value = "unknown-" + tag;
    /** @todo mark as 'learned' */
  }


  public final String name;


  public final String value;
}
