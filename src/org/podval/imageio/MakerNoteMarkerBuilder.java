/* $Id$ */

package org.podval.imageio;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


public class MakerNoteMarkerBuilder extends Builder {

  public MakerNoteMarkerBuilder(Builder previous, MakerNoteMarker makerNoteMarker) throws SAXException {
    super(previous);
    this.makerNoteMarker = makerNoteMarker;
  }


  public Builder startElement(String name, Attributes attributes)
    throws SAXException
  {
    return null;
  }


  public String toString() {
    return "<makerNoteMarker name=\"" + makerNoteMarker.getName() + "\"/>";
  }


  private final MakerNoteMarker makerNoteMarker;
}
