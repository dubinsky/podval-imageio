package org.podval.imageio;

import java.util.Iterator;


/**
 */

public class RecordLoader {

  public static Record loadTopLevel(org.podval.imageio.jaxb.Record xml) {
    Record result = loadLocal(xml);
    Record.add(result);
    return result;
  }


  public static Record loadLocal(org.podval.imageio.jaxb.Record xml) {
    String name = xml.getName();
    /** @todo XXX: handle references differently from local definitions... */

    Record result = Record.get(name);
    if (result == null)
      result = new Record(name);

    TypedLoader.load(result, xml);
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
    Enumeration enumeration = EnumerationLoader.load(xml.getEnumeration());
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

      result.addField(index, FieldLoader.load(fieldXml));
    }
  }
}
