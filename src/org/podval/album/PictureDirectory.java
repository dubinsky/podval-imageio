package org.podval.album;

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
  }


  public PictureDirectory getSubdirectory(String name) {
    ensureLoaded();
    return (PictureDirectory) subdirectories.get(name);
  }


  public Picture getPicture(String name) {
    ensureLoaded();
    return (Picture) pictures.get(name);
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

    /** @todo get rid of the non-picture pictures. */
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
}
