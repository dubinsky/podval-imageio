package org.podval.imageio;

import java.io.InputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;

import java.util.Iterator;


public class MetaMetadata {

  public static void load() {
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


  private static void load(Class cls, String resourceName) {
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


  private static void load(InputStream in) throws IOException, JAXBException {
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
        loadMakerNote((org.podval.imageio.jaxb.MakerNote) descriptor);
      else
      if (descriptor instanceof org.podval.imageio.jaxb.Directory)
        loadDirectory((org.podval.imageio.jaxb.Directory) descriptor);
      else
      if (descriptor instanceof org.podval.imageio.jaxb.Record) {
        Record record = loadRecord((org.podval.imageio.jaxb.Record) descriptor);
        Record.register(record);
      }
      else
        assert false : "Unknown top-level descriptor " + descriptor;
    }
  }


  public static void loadTyped(Typed result, org.podval.imageio.jaxb.Typed xml) {
    if (xml.getType() != null)
      result.setType(Type.parse(xml.getType()));
  }


  public static Directory loadDirectory(org.podval.imageio.jaxb.Directory xml) {
    Directory result = Directory.get(xml.getName());
    loadTyped(result, xml);

    for (Iterator i = xml.getEntries().iterator(); i.hasNext();) {
      Object o = i.next();

      /** @todo tag extraction and loading should be unified... */
      if (o instanceof org.podval.imageio.jaxb.SubDirectory) {
        org.podval.imageio.jaxb.SubDirectory directoryXml =
          (org.podval.imageio.jaxb.SubDirectory) o;
        result.addEntry(directoryXml.getTag(), loadDirectory(directoryXml));
      } else

      if (o instanceof org.podval.imageio.jaxb.MakerNoteMarker) {
        org.podval.imageio.jaxb.MakerNoteMarker markerXml =
          (org.podval.imageio.jaxb.MakerNoteMarker) o;
        int tag = markerXml.getTag();
        /** @todo make MARKER derived from Directory and eliminate
         * this flavour of 'addEntry'? */
        Type type = Type.parse(markerXml.getType());
        result.addEntry(tag, type, MakerNote.MARKER);
      } else

      if (o instanceof org.podval.imageio.jaxb.SubRecord) {
        org.podval.imageio.jaxb.SubRecord recordXml =
          (org.podval.imageio.jaxb.SubRecord) o;
        result.addEntry(recordXml.getTag(), loadRecord(recordXml));
      } else

        assert false : "Unknown directory entry " + o;
    }

    return result;
  }


  public static Record loadRecord(org.podval.imageio.jaxb.Record xml) {
    String name = xml.getName();
    /** @todo XXX: handle references differently from local definitions... */

    Record result = Record.get(name);
    if (result == null)
      result = new Record(name);

    loadTyped(result, xml);
    loadIsVector(result, xml);
    loadCount(result, xml);
    loadEnumeration(result, xml);
    loadFields(result, xml);

    return result;
  }


  private static void loadIsVector(Record result, org.podval.imageio.jaxb.Record xml) {
    if (xml.isSetVector())
      result.setIsVector(xml.isVector());
  }


  private static void loadCount(Record result, org.podval.imageio.jaxb.Record xml) {
    Object o = xml.getCount();
    if (o != null)
      result.setCount((o instanceof Integer) ? ((Integer) o).intValue() : 0);
  }


  private static void loadEnumeration(Record result, org.podval.imageio.jaxb.Record xml) {
    Enumeration enumeration = loadEnumeration(xml.getEnumeration());
    if (enumeration != null) {
      /** @todo check the count */
      Field field = result.createDefaultField();
      field.setEnumeration(enumeration);
      result.addField(1, field);
    }
  }


  private static void loadFields(Record result, org.podval.imageio.jaxb.Record xml) {
    int index = 0;
    for (Iterator i = xml.getFields().iterator(); i.hasNext();) {
      org.podval.imageio.jaxb.Field fieldXml =
        (org.podval.imageio.jaxb.Field) i.next();

      index = (fieldXml.isSetIndex()) ? fieldXml.getIndex() : index+1;

      result.addField(index, loadField(fieldXml));
    }
  }


  public static void loadMakerNote(org.podval.imageio.jaxb.MakerNote xml) {
    String make = xml.getMake();
    String signature = xml.getSignature();
    Directory directory = loadDirectory(xml);
    MakerNote.register(make, signature, directory);
  }


  public static Field loadField(org.podval.imageio.jaxb.Field xml) {
    String name = xml.getName();
    Field result = new Field(name);
    loadTyped(result, xml);
    loadEnumeration(result, xml);
    loadSubfields(result, xml);
    return result;
  }


  private static void loadEnumeration(Field result, org.podval.imageio.jaxb.Field xml) {
    result.setEnumeration(loadEnumeration(xml.getEnumeration()));
  }


  private static void loadSubfields(Field result, org.podval.imageio.jaxb.Field xml) {
    for (Iterator i = xml.getSubfields().iterator(); i.hasNext();)
      result.addSubfield(loadField((org.podval.imageio.jaxb.Field) i.next()));
  }


  public static Enumeration loadEnumeration(org.podval.imageio.jaxb.Enumeration xml) {
    Enumeration result = null;

    if (xml != null) {
      result = new Enumeration();

      for (Iterator i = xml.getItems().iterator(); i.hasNext(); ) {
        org.podval.imageio.jaxb.EnumItem item =
          (org.podval.imageio.jaxb.EnumItem) i.next();

        result.addDescription(item.getValue(), item.getDescription());
      }
    }

    return result;
  }


  public static void setDecodeUnknown(boolean value) {
    isDecodeUnknown = value;
  }


  public static boolean isDecodeUnknown() {
    return isDecodeUnknown;
  }


  private static boolean isDecodeUnknown = true;
}
