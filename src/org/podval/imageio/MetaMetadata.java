package org.podval.imageio;

import java.io.InputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;

import java.util.Iterator;


public class MetaMetadata {

  static {
    load();
  }


  private static void load() {
    if (!loaded) {
      Class cls = org.podval.imageio.MetaMetadata.class;
      load(cls, "xml/canon-maker-note-1.xml");
      load(cls, "xml/canon-maker-note-4.xml");
      load(cls, "xml/ciff.xml");
      load(cls, "xml/canon-ciff.xml");
      load(cls, "xml/exif-root.xml");
      load(cls, "xml/exif.xml");
      load(cls, "xml/canon-maker-note.xml");
      loaded = true;
    }
  }


  private static boolean loaded = false;


  public static void load(Class cls, String resourceName) {
    try {
      load(cls.getResourceAsStream(resourceName));
    } catch (Throwable e) {
      System.err.println(
        "Can't load meta-metadata resource " + resourceName +
        " for class " + cls +
        ": " + e
      );
    }
  }


  public static void load(InputStream in) throws IOException, JAXBException {
    if (in == null)
      return;

      JAXBContext jc = JAXBContext.newInstance("org.podval.imageio.jaxb");
      Unmarshaller u = jc.createUnmarshaller();
      /**
       * @todo XXX:
       * Bug ID: 4800069 Validation fails with unexpected attribute for extension type.
       * Marked as fixed; information is not available.
       * */
//      u.setValidating(true);

      org.podval.imageio.jaxb.MetaMetadata xml =
        (org.podval.imageio.jaxb.MetaMetadata) u.unmarshal(in);

      load(xml);
  }


  private static void load(org.podval.imageio.jaxb.MetaMetadata xml) {
    for (Iterator i = xml.getDescriptors().iterator(); i.hasNext();) {
      Object descriptor = i.next();

      /**
       * @todo While .jaxb.MakerNote is derived from .jaxb.Directory,
       * order of alternatives is important... */
      if (descriptor instanceof org.podval.imageio.jaxb.MakerNote)
        MakerNote.load((org.podval.imageio.jaxb.MakerNote) descriptor);
      else
      if (descriptor instanceof org.podval.imageio.jaxb.Directory)
        Directory.load((org.podval.imageio.jaxb.Directory) descriptor);
      else
      if (descriptor instanceof org.podval.imageio.jaxb.Record)
        Record.loadTopLevel((org.podval.imageio.jaxb.Record) descriptor);
      else
        assert false : "Unknown top-level descriptor " + descriptor;
    }
  }


  public static void setDecodeUnknown(boolean value) {
    isDecodeUnknown = value;
  }


  public static boolean isDecodeUnknown() {
    return isDecodeUnknown;
  }


  private static boolean isDecodeUnknown = true;
}
