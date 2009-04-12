package org.podval.imageio.metametadata;

import java.io.File;
import java.io.PrintStream;
import java.io.IOException;

import java.util.List;
import java.util.Collection;


public class EnumerationClassGenerator {

  public static void generate(Heap heap, String path)
    throws IOException
  {
    File directory = new File(path);
    generateHeap(heap, directory);
  }


  private static void generateHeap(Heap heap, File directory)
    throws IOException
  {
    for (Key key : heap.getKeys()) {
      Entry entry = heap.getEntry(key);

      if (entry instanceof Heap) {
        generateHeap((Heap) entry, directory);
      } else
      if (entry instanceof Record) {
        Record record = (Record) entry;
        for (Field field : record.getFields()) {
          if (field != null) {
            generateField(field, directory);
          }
        }
      }
    }
  }


  private static void generateField(Field field, File directory)
    throws IOException
  {
    Enumeration enumeration = field.getEnumeration();
    if (enumeration != null) {
      String className = enumeration.getClassName();
      if (className != null) {
        PrintStream os = getOutputStream(directory, className);
        generateEnumeration(enumeration, os);
        os.close();
      }
    }

    List<Field> subFields = field.getSubFields();
    if (subFields != null) {
      for (Field subField : subFields) {
        generateField(subField, directory);
      }
    }
  }


  private static void generateEnumeration(Enumeration enumeration, PrintStream os) {
    String className = enumeration.getClassName();
    int dot = className.lastIndexOf(".");
    String packageName = className.substring(0, dot);
    className = className.substring(dot+1, className.length());

    os.println("package " + packageName + ";");
    os.println();
    os.println();
    os.println("public enum " + className + " {");
    os.println();
    Collection<EnumerationItem> items = enumeration.getItems();
    int numItem = 0;
    for (EnumerationItem item : items) {
      String terminator = (numItem < items.size()-1) ? "," : ";";
      os.println("  " + item.name + "(\"" + item.description + "\")" + terminator);
      numItem++;
    }
    os.println();
    os.println();
    os.println("  private " + className + "(String description) {");
    os.println("    this.description = description;");
    os.println("  }");
    os.println();
    os.println();
    os.println("  public final String toString() {");
    os.println("    return description;");
    os.println("  }");
    os.println();
    os.println();
    os.println("  private final String description;");
    os.println("}");
  }


  private static PrintStream getOutputStream(File directory, String className)
    throws IOException
  {
    String[] names = className.split("\\.");
    for (int i = 0; i < names.length-1; i++) {
      directory = new File(directory, names[i]);
    }
    directory.mkdirs();

    return new PrintStream(new File(directory, names[names.length-1] + ".java"));
  }
}
