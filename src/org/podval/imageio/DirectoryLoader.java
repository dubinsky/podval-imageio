package org.podval.imageio;

import java.util.Iterator;


/**
 */

public class DirectoryLoader {

  public static Directory load(org.podval.imageio.jaxb.Directory xml) {
    Directory result = Directory.get(xml.getName());
    TypedLoader.load(result, xml);

    for (Iterator i = xml.getEntries().iterator(); i.hasNext();) {
      Object o = i.next();

      if (o instanceof org.podval.imageio.jaxb.SubDirectory) {
        org.podval.imageio.jaxb.SubDirectory directoryXml =
          (org.podval.imageio.jaxb.SubDirectory) o;
        result.addEntry(directoryXml.getTag(), DirectoryLoader.load(directoryXml));
      } else

      if (o instanceof org.podval.imageio.jaxb.MakerNoteMarker) {
        org.podval.imageio.jaxb.MakerNoteMarker markerXml =
          (org.podval.imageio.jaxb.MakerNoteMarker) o;
        int tag = markerXml.getTag();
        /** @todo introduce MakerNote.MARKER loading and
         *  eliminate this flavour of 'addEntry'? */
        Type type = Type.parse(markerXml.getType());
        result.addEntry(tag, type, MakerNote.MARKER);
      } else

      if (o instanceof org.podval.imageio.jaxb.SubRecord) {
        org.podval.imageio.jaxb.SubRecord recordXml =
          (org.podval.imageio.jaxb.SubRecord) o;
        result.addEntry(recordXml.getTag(), RecordLoader.loadLocal(recordXml));
      } else

        assert false : "Unknown directory entry " + o;
    }

    return result;
  }
}
