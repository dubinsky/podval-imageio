package org.podval.album;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import java.io.File;
import java.io.IOException;


/**
 */

public class PictureDirectory {

  public PictureDirectory(String name, File originalsDirectory, File generatedDirectory) {
    this.name = name;
    this.originalsDirectory = originalsDirectory;
    this.generatedDirectory = generatedDirectory;

    if (name == null)
      throw new NullPointerException("Directory name is null.");

    if (originalsDirectory == null)
      throw new NullPointerException("Originals directory is null.");

    if (generatedDirectory == null)
      throw new NullPointerException("Generated directory is null.");

    if (!originalsDirectory.exists())
      throw new IllegalArgumentException("Directory does not exist: " + originalsDirectory);

    if (!originalsDirectory.isDirectory())
      throw new IllegalArgumentException("Not a directory: " + originalsDirectory);

//    if (!generatedDirectory.canRead())
//      throw new IllegalArgumentException("Can not read: " + originalsDirectory);

    if (generatedDirectory.exists()) {
      if (!generatedDirectory.isDirectory())
        throw new IllegalArgumentException("Not a directory: " + generatedDirectory);

//      if (!generatedDirectory.canWrite())
//        throw new IllegalArgumentException("Can not write to: " + generatedDirectory);
    } else {
      if (!generatedDirectory.mkdirs())
        throw new IllegalArgumentException("Can not create: " + generatedDirectory);
    }
  }


  public String getName() {
    return name;
  }


  public int getNumSubdirectories() {
    ensureLoaded();
    return subdirectories.size();
  }


  public PictureDirectory getSubdirectory(String name) {
    ensureLoaded();
    return (PictureDirectory) subdirectories.get(name);
  }


  public Collection getSubdirectories() {
    ensureLoaded();
    return Collections.unmodifiableCollection(subdirectories.values());
  }


  public int getNumPictures() {
    ensureLoaded();
    return pictures.size();
  }


  public Picture getPicture(String name) {
    ensureLoaded();
    return (Picture) pictures.get(name);
  }


  public Collection getPictures() {
    ensureLoaded();
    return Collections.unmodifiableCollection(pictures.values());
  }


  private void ensureLoaded() {
    if (!loaded) {
      load();
      loaded = true;
    }
  }


  private void load() {
    File[] files = originalsDirectory.listFiles();

    for (int i=0; i<files.length; i++) {
      File file = files[i];
      try {
        if (file.isDirectory())
          addSubdirectory(file);
        else
          addFile(file);
      } catch (IOException e) {
        /** @todo XXXX */
      }
    }

    for (Iterator i=pictures.entrySet().iterator(); i.hasNext();) {
      Map.Entry entry = (Map.Entry) i.next();
      Picture picture = (Picture) entry.getValue();
      if (!picture.isPicture())
        i.remove();
    }

    /** @todo sort pictures by date. */
  }


  private void addSubdirectory(File file) throws IOException {
    String name = file.getName();

    if (subdirectories.get(name) != null)
      throw new IOException("Duplicate case-sensitive subdirectory name " + name);

    /** @todo now it would be nice to be able to place generated directory inside the originals one ... */
    PictureDirectory subdirectory = new PictureDirectory(name,
      new File(originalsDirectory, name),
      new File(generatedDirectory, name)
    );

    subdirectories.put(name, subdirectory);
  }


  private void addFile(File file) throws IOException {
    String fileName = file.getName();

    int endExtension = fileName.length();
    int startExtension;
    int endModifier;
    int endName;
    int startModifier;
    int startName = 0;

    int lastDot = fileName.lastIndexOf('.');
    if (lastDot == -1) {
      startExtension = endExtension;
      endModifier = endExtension;
    } else {
      startExtension = lastDot+1;
      endModifier = lastDot;
    }

    int firstDash = fileName.indexOf('-');
    if (firstDash == -1) {
      startModifier = endModifier;
      endName = endModifier;
    } else {
      startModifier = firstDash+1;
      endName = firstDash;
    }

    String name      = fileName.substring(startName     , endName);
    String modifier  = fileName.substring(startModifier , endModifier);
    String extension = fileName.substring(startExtension, endExtension);

//    System.err.println(file + ": '" + name + "' '" + modifier + "' '" + extension + "'");

    Picture picture = (Picture) pictures.get(name);

    if (picture == null) {
      picture = new Picture(name, originalsDirectory, generatedDirectory);
      pictures.put(name, picture);
    }

    picture.addFile(file, name, modifier, extension);
  }


  private final String name;


  private final File originalsDirectory;


  private final File generatedDirectory;


  private boolean loaded = false;


  /**
   * map<String name, PictureDirectory>
   */
  private final Map subdirectories = new TreeMap(String.CASE_INSENSITIVE_ORDER);


  /**
   * map<String name, Picture>
   */
  private final Map pictures = new TreeMap(String.CASE_INSENSITIVE_ORDER);




  public static void main(String[] args) throws IOException {
    PictureDirectory root = new PictureDirectory("", new File("/tmp/crw"), new File("/tmp/crw-generated"));
    for (Iterator pictures = root.getPictures().iterator(); pictures.hasNext();) {
      Picture picture = (Picture) pictures.next();
      System.err.print(picture);
      System.err.println();
    }
    root.getPicture("0001").getScaled(300, 300);
  }
}
