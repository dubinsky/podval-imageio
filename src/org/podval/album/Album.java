package org.podval.album;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;

import java.util.StringTokenizer;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import org.podval.imageio.MetaMetadata;


/**
 */

public class Album {

  public static void setRoot(String originalsRoot, String metadataRoot, String generatedRoot) {
    /** @todo check that directories exist (and have proper permissions) - or else what? */
    root = new Album(originalsRoot, metadataRoot, generatedRoot);

    MetaMetadata.init();
  }


  public static Album getByPath(String path) {
    if (root == null)
      throw new NullPointerException("Root is not set!");

    if (path == null)
      throw new NullPointerException("Path is null!");

    if (!path.startsWith("/"))
      throw new IllegalArgumentException("Path does not start with '/'!");

    Album result = root;

    StringTokenizer tokenizer = new StringTokenizer(path, "/");
    while (tokenizer.hasMoreTokens() && (result != null))
      result = ((Album) result).getSubalbum(tokenizer.nextToken());

    if (result == null)
      throw new NullPointerException("No album at " + path);

    return result;
  }


  private static Album root;


  public Album(String originalsRoot, String metadataRoot, String generatedRoot) {
    /** @todo check that directories exist (and have proper permissions) - or else what? */
    this.parent = null;
    this.name = "";
    this.originalsDirectory = new File(originalsRoot);
    this.metadataDirectory = new File(metadataRoot);
    this.generatedDirectory = new File(generatedRoot);
  }


  public Album(Album parent, String name) {
    if (parent == null)
      throw new NullPointerException("Directory parent is null.");

    if (name == null)
      throw new NullPointerException("Directory name is null.");

    this.parent = parent;
    this.name = name;
  }


  public Album getParent() {
    return parent;
  }


  public String getName() {
    return name;
  }


  public String getPath() {
    /** @todo for the root, result is incorrect ("" instead of "/"). Is this important? */
    return (parent != null) ? parent.getPath() + "/" + name : name;
  }


  public void setTitle(String value) {
    ensureAlbumMetadataLoaded();
    title = value;
    albumMetadataChanged = true;
  }


  public String getTitle() {
    ensureAlbumMetadataLoaded();
    return (title != null) ? title : name;
  }


  public int getNumSubalbums() {
    ensureSubalbumsLoaded();
    return subalbums.size();
  }


  public Album getSubalbum(String name) {
    ensureSubalbumsLoaded();
    return (Album) subalbums.get(name);
  }


  public Collection getSubalbums() {
    ensureSubalbumsLoaded();
    return Collections.unmodifiableCollection(subalbums.values());
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
    return Collections.unmodifiableCollection(sortedPictures);
  }


  public File getOriginalsDirectory() {
    if (originalsDirectory == null) {
      originalsDirectory = new File(parent.getOriginalsDirectory(), name);
      checkDirectoryExists(originalsDirectory);
    }

    return originalsDirectory;
  }


  public File getGeneratedDirectory() {
    if (generatedDirectory == null) {
      /** @todo now it would be nice to be able to place generated directory inside the originals one ... */
      generatedDirectory = new File(parent.getGeneratedDirectory(), name);
      ensureDirectoryExists(generatedDirectory);
      //checkCanWrite(generatedDirectory);
    }

    return generatedDirectory;
  }


  public File getMetadataDirectory() {
    if (metadataDirectory == null) {
      /** @todo alternative policy is - try originals directory first... */
      metadataDirectory = new File(parent.getMetadataDirectory(), name);
      ensureDirectoryExists(metadataDirectory);
    }

    return metadataDirectory;
  }


  private static void checkDirectoryExists(File file) {
    if (!file.exists())
      throw new IllegalArgumentException(file + " does not exist.");

    if (!file.isDirectory())
      throw new IllegalArgumentException(file + " is not a directory.");
  }


  private static void ensureDirectoryExists(File directory) {
    if (directory.exists()) {
      if (!directory.isDirectory())
        throw new IllegalArgumentException(directory + " is not a directory.");
    } else {
      if (!directory.mkdirs())
        throw new IllegalArgumentException("Can not create " + directory);
    }
  }


  private void ensureAlbumMetadataLoaded() {
    if (!albumMetadataLoaded) {
      albumMetadataLoaded = true;
      loadAlbumMetadata();
      albumMetadataChanged = false;
    }
  }


  private void ensureSubalbumsLoaded() {
    if (!subalbumsLoaded) {
      loadSubalbums();
      subalbumsLoaded = true;
    }
  }


  private void ensurePicturesLoaded() {
    if (!picturesLoaded) {
      loadPictures();
      picturesLoaded = true;
      loadPictureReferences();
      pictureReferencesChanged = false;
      sortPicturesByDateTime();
    }
  }


  private void loadAlbumMetadata() {
    try {
      File file = getAlbumMetadataFile(false);
      if (file != null) {
        org.podval.album.jaxb.Album xml =
          Metadata.unmarshallAlbum(file);

        loadAlbumMetadata(xml);
      }
    } catch (JAXBException e) {
      /** @todo  */
    }
  }


  private void loadAlbumMetadata(org.podval.album.jaxb.Album xml) {
    setTitle(xml.getTitle());
  }


  private void loadSubalbums() {
    File[] files = getOriginalsDirectory().listFiles();

    for (int i=0; i<files.length; i++) {
      File file = files[i];
      try {
        if (file.isDirectory())
          addSubalbum(file);
      } catch (IOException e) {
        /** @todo XXXX */
      }
    }
  }


  private void loadPictures() {
    File[] files = getOriginalsDirectory().listFiles();

    for (int i=0; i<files.length; i++) {
      File file = files[i];
      try {
        if (!file.isDirectory())
          addFile(file);
      } catch (IOException e) {
        /** @todo XXXX */
      }
    }

    removeNonPictures();
  }


  private void removeNonPictures() {
    for (Iterator i = pictures.entrySet().iterator(); i.hasNext(); ) {
      Map.Entry entry = (Map.Entry) i.next();
      PictureLocal picture = (PictureLocal) entry.getValue();
      if (!picture.isPicture())
        i.remove();
    }
  }


  private void loadPictureReferences() {
    try {
      File file = getPictureReferencesFile(false);
      if (file != null) {
        org.podval.album.jaxb.PictureReferences xml =
          Metadata.unmarshallPictureReferences(file);

        loadPictureReferences(xml);
      }
    } catch (JAXBException e) {
      /** @todo  */
    }
  }


  private void loadPictureReferences(org.podval.album.jaxb.PictureReferences xml) {
    for (Iterator references = xml.getReferences().iterator(); references.hasNext();) {
      org.podval.album.jaxb.PictureReferences.Picture reference =
        (org.podval.album.jaxb.PictureReferences.Picture) references.next();
      addPictureReference(reference.getPath());
    }
  }


  private void sortPicturesByDateTime() {
    sortedPictures.addAll(pictures.values());
  }


  private void addSubalbum(File file) throws IOException {
    String name = file.getName();

    if (subalbums.get(name) != null)
      throw new IOException("Duplicate case-sensitive subalbum name " + name);

    subalbums.put(name, new Album(this, name));
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

    PictureLocal picture = (PictureLocal) pictures.get(name);

    if (picture == null) {
      picture = new PictureLocal(this, name);
      pictures.put(name, picture);
    }

    picture.addFile(file, name, extension);
  }


  public void addPictureReference(String path) {
    ensurePicturesLoaded();
    /** @todo fail fast if referrent is null? Why didn't it throw in the process??? */
    PictureRef picture = new PictureRef(this, path);
    pictures.put(picture.getName(), picture);
    pictureReferencesChanged = true;
  }


  public void removePictureReference(String name) {
    ensurePicturesLoaded();

    Picture result = (Picture) pictures.get(name);
    if (result != null) {
      if (!(result instanceof PictureRef))
        throw new IllegalArgumentException(
          "Only REFERENCES to pictures can be removed!");
      pictures.remove(name);

      pictureReferencesChanged = true;
    }
  }


  public void save() throws FileNotFoundException, IOException, JAXBException {
    if (albumMetadataChanged) {
      saveAlbumMetadata();
      albumMetadataChanged = false;
    }
    if (pictureReferencesChanged) {
      savePictureReferences();
      pictureReferencesChanged = false;
    }
  }


  private void saveAlbumMetadata()
    throws FileNotFoundException, IOException, JAXBException
  {
    File file = getAlbumMetadataFile(true);
    if (file != null) {
      org.podval.album.jaxb.Album result = Metadata.createAlbum();

      if (title!=null)
        result.setTitle(title);

      Metadata.marshallAlbum(result, file);
    }
  }


  private void savePictureReferences()
    throws FileNotFoundException, IOException, JAXBException
  {
    File file = getPictureReferencesFile(true);
    if (file != null) {
      org.podval.album.jaxb.PictureReferences result = Metadata.
        createPictureReferences();

      for (Iterator i = getPictures().iterator(); i.hasNext(); ) {
        Picture picture = (Picture) i.next();
        if (picture instanceof PictureRef) {
          org.podval.album.jaxb.PictureReferences.PictureType xml =
            Metadata.createPictureReference();
          xml.setPath(picture.getName());

          result.getReferences().add(xml);
        }
      }

      Metadata.marshallPictureReferences(result, file);
    }
  }


  private File getAlbumMetadataFile(boolean write) {
    return getMetadataFile("album", write);
  }


  private File getPictureReferencesFile(boolean write) {
    return getMetadataFile("pictures", write);
  }


  private File getMetadataFile(String name, boolean write) {
    name = name + ".xml";

    File result = null;

    if (!write) {
      if (result == null)
        result = ifCanRead(new File(getMetadataDirectory(), name));
      if (result == null)
        result = ifCanRead(new File(getOriginalsDirectory(), name));

    } else {
      if (result == null)
        result = ifCanWrite(new File(getOriginalsDirectory(), name));
      if (result == null)
        result = ifCanWrite(new File(getMetadataDirectory(), name));
    }

    return result;
  }


  private File ifCanRead(File file) {
    return (file.canRead()) ? file : null;
  }


  private File ifCanWrite(File file) {
    File result = null;
    try {
      file.createNewFile();
      if (file.canWrite())
        result = file;
    } catch (IOException e) {
    }
    return result;
  }


  private final Album parent;


  private final String name;


  private String title;


  private File originalsDirectory;


  private File metadataDirectory;


  private File generatedDirectory;


  private boolean albumMetadataLoaded;


  private boolean subalbumsLoaded;


  private boolean picturesLoaded;


  /**
   * map<String name, Album>
   */
  private final Map subalbums = new TreeMap(String.CASE_INSENSITIVE_ORDER);


  /**
   * map<String name, Picture>
   */
  private final Map pictures = new TreeMap(String.CASE_INSENSITIVE_ORDER);


  private final Set sortedPictures = new TreeSet();


  private boolean albumMetadataChanged;


  private boolean pictureReferencesChanged;
}
