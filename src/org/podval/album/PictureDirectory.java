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
    ensureSubdirectoriesLoaded();
    return subdirectories.size();
  }


  public PictureDirectory getSubdirectory(String name) {
    ensureSubdirectoriesLoaded();
    return (PictureDirectory) subdirectories.get(name);
  }


  public Collection getSubdirectories() {
    ensureSubdirectoriesLoaded();
    return Collections.unmodifiableCollection(subdirectories.values());
  }


  public int getNumPictures() {
    ensurePicturesLoaded();
    return pictures.size();
  }


  public Picture getPicture(String name) {
    ensurePicturesLoaded();
    return (Picture) pictures.get(name);
  }


  public Collection getPictures() {
    ensurePicturesLoaded();
    return Collections.unmodifiableCollection(pictures.values());
  }


  private void ensureSubdirectoriesLoaded() {
    if (!subdirectoriesLoaded) {
      loadSubdirectories();
      subdirectoriesLoaded = true;
    }
  }


  private void ensurePicturesLoaded() {
    if (!picturesLoaded) {
      loadPictures();
      picturesLoaded = true;
    }
  }


  private void loadSubdirectories() {
    File[] files = originalsDirectory.listFiles();

    for (int i=0; i<files.length; i++) {
      File file = files[i];
      try {
        if (file.isDirectory())
          addSubdirectory(file);
      } catch (IOException e) {
        /** @todo XXXX */
      }
    }
  }


  private void loadPictures() {
    File[] files = originalsDirectory.listFiles();

    for (int i=0; i<files.length; i++) {
      File file = files[i];
      try {
        if (!file.isDirectory())
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
    int endName;
    int startName = 0;

    int lastDot = fileName.lastIndexOf('.');
    if (lastDot == -1) {
      startExtension = endExtension;
      endName = startExtension;
    } else {
      startExtension = lastDot+1;
      endName = lastDot;
    }

    String name      = fileName.substring(startName     , endName);
    String extension = fileName.substring(startExtension, endExtension);

    Picture picture = (Picture) pictures.get(name);

    if (picture == null) {
      picture = new Picture(name, generatedDirectory);
      pictures.put(name, picture);
    }

    picture.addFile(file, name, extension);
  }


  private final String name;


  private final File originalsDirectory;


  private final File generatedDirectory;


  private boolean subdirectoriesLoaded = false;


  private boolean picturesLoaded = false;


  /**
   * map<String name, PictureDirectory>
   */
  private final Map subdirectories = new TreeMap(String.CASE_INSENSITIVE_ORDER);


  /**
   * map<String name, Picture>
   */
  private final Map pictures = new TreeMap(String.CASE_INSENSITIVE_ORDER);
}
