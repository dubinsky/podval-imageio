/* @(#)$Id$ */

package org.podval.imageio.metametadata;


public class EnumerationItem {

  public EnumerationItem(String name, String description)
    throws MetaMetaDataException
  {
    if ((name == null) && (description == null)) {
      throw new MetaMetaDataException("Name or description must be present");
    }

    this.name = name;
    this.description = description;
  }


  public EnumerationItem(int tag) {
    this.name = null;
    this.description = "unknown-" + tag;
    /** @todo mark as 'learned' */
  }


  public final String name;


  public final String description;
}
