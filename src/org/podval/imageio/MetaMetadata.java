package org.podval.imageio;

import java.io.InputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;

import java.util.Iterator;

import java.lang.reflect.Method;

import javax.imageio.spi.IIORegistry;


public class MetaMetadata {

  public static void init() {
    /** @todo should register using jar manifest (also). */
    IIORegistry.getDefaultInstance().registerServiceProvider(new
      CiffImageReaderSpi());

    /** @todo where is the right place for this? */
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
      u.setValidating(true);

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
        /** @todo make MARKER derived from Directory and eliminate
         * this flavour of 'addEntry'? */
        Type type = Type.parse(markerXml.getType());
        result.addEntry(markerXml.getTag(), type, MakerNote.MARKER);
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

    Object o = xml.getCount();
    if (o != null)
      result.setCount((o instanceof Integer) ? ((Integer) o).intValue() : 0);

    if (xml.isSetVector())
      result.setIsVector(xml.isVector());

    result.setEnumeration(loadEnumeration(xml.getEnumeration()));
    result.setConversion(loadConversion(xml.getConversion()));
    result.setHandler(loadHandler(xml.getHandler()));

    int index = 0;
    for (Iterator i = xml.getFields().iterator(); i.hasNext();) {
      org.podval.imageio.jaxb.Field fieldXml =
        (org.podval.imageio.jaxb.Field) i.next();

      index = (fieldXml.isSetIndex()) ? fieldXml.getIndex() : index+1;

      result.addField(index, loadField(fieldXml));
    }

    /** @todo if count was set, check. If not, set. */

    if (xml.isSetSkip())
      result.setSkip(xml.isSkip());

    return result;
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
    result.setEnumeration(loadEnumeration(xml.getEnumeration()));
    result.setConversion(loadConversion(xml.getConversion()));
    for (Iterator i = xml.getSubfields().iterator(); i.hasNext();)
      result.addSubfield(loadField((org.podval.imageio.jaxb.Field) i.next()));
    if (xml.isSetSkip())
      result.setSkip(xml.isSkip());
    return result;
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


  public static Method loadConversion(String name) {
    Method result = null;

    if (name != null) {
      int dot = name.lastIndexOf('.');
      if (dot == -1)
        throw new IllegalArgumentException("Conversion method name " + name +
          " does not contain a '.'");
      String className = name.substring(0, dot);
      String methodName = name.substring(dot+1, name.length());
      Class cls;
      try {
        cls = Class.forName(className);
      } catch (ClassNotFoundException e) {
        throw new IllegalArgumentException("Class with conversion " + name +
          " not found");
      }
      /** @todo I can - and should - figure out argument type for the conversion method and ask for it directly... */
      Method[] methods = cls.getDeclaredMethods();
      for (int i = 0; i<methods.length; i++) {
        Method method = methods[i];
        if (method.getName().equals(methodName)) {
          result = method;
          break;
        }
      }
    }

    return result;
  }


  private static Class loadHandler(String name) {
    Class result = null;

    if (name != null) {
      try {
        result = Class.forName(name);
      } catch (ClassNotFoundException e) {
        throw new IllegalArgumentException("Class with conversion " + name +
          " not found");
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
