package org.podval.album;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.HashSet;

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

public class AlbumLocal extends Album {

  /**
   * This constructor is used for the root only.
   *
   * @param originalsRoot
   * @param metadataRoot
   * @param generatedRoot
   */
  public AlbumLocal(String originalsRoot, String metadataRoot, String generatedRoot) {
    /** @todo check that directories exist (and have proper permissions) - or else what? */
    this.directory = new File(originalsRoot);
    this.metadataDirectory = new File(metadataRoot);
    this.generatedDirectory = new File(generatedRoot);
  }


  protected AlbumLocal(AlbumLocal parent, String name) {
    super(parent, name);
  }


  public String getPath() {
    String result;

    if (getParent().getParent() == null)
      result = "/" + getName();
    else
    if (getParent() == null)
      result = "/";
    else
      result = getParent().getPath() + "/" + getName();

    return result;
  }


  protected String getDefaultTitle() {
    return getName();
  }


  public int getNumSubalbums() {
    loadSubalbums();
    return subalbums.size();
  }


  public Album getSubalbum(String name) {
    loadSubalbums();
    return (Album) subalbums.get(name);
  }


  public Collection getSubalbums() {
    loadSubalbums();
    return Collections.unmodifiableCollection(subalbums.values());
  }


  public int getNumPictures() {
    loadPictures();
    return pictures.size();
  }


  public Picture getPicture(String name) {
    loadPictures();
    return (Picture) pictures.get(name);
  }


  public Collection getPictures() {
    loadPictures();
    return Collections.unmodifiableCollection(sortedPictures);
  }


  protected void loadMetadata() {
    if (!metadataChanged) {
      File file = getMetadataReadFile();
      if (file != null) {
        long lastModified = file.lastModified();
        if (metadataLoaded < lastModified) {
          metadataLoaded = lastModified;
          loadMetadata(file);
          metadataChanged = false;
        }
      }
    }
  }


  private void loadMetadata(File file) {
    try {
      org.podval.album.jaxb.Album xml = JAXB.unmarshallAlbum(file);
      title = xml.getTitle();
    } catch (JAXBException e) {
    /** @todo  */
    }
  }


  protected void metadataChanged() {
    metadataChanged = true;
    changed();
  }


  private void saveMetadata() {
    try {
      File file = getMetadataWriteFile();
      if (file != null) {
        org.podval.album.jaxb.Album result =
          JAXB.getObjectFactory().createAlbum();

        if (title != null)
          result.setTitle(title);

        JAXB.marshall(result, file);
      }
    } catch (FileNotFoundException e) {
      /** @todo  */
    } catch (IOException e) {
      /** @todo  */
    } catch (JAXBException e) {
      /** @todo  */
    }
  }


  private void loadSubalbums() {
    File originalsDirectory = getDirectory();
    long lastModified = originalsDirectory.lastModified();

    if (subalbumsLoaded < lastModified) {
      subalbumsLoaded = lastModified;
      loadSubalbums(originalsDirectory);
    }
  }


  private void loadPictures() {
    if (!pictureReferencesChanged) {
      File originalsDirectory = getDirectory();

      if (pictureReferencesReadFile == null)
        pictureReferencesReadFile = getMetadataReadFile(PICTURE_REFERENCES_FILE_NAME);

      long lastModified = originalsDirectory.lastModified();
      if (pictureReferencesReadFile != null)
        lastModified = Math.max(lastModified, pictureReferencesReadFile.lastModified());

      if (picturesLoaded < lastModified) {
        picturesLoaded = lastModified;
        loadPictures(originalsDirectory, pictureReferencesReadFile);
        pictureReferencesChanged = false;
      }
    }
  }


  private void loadSubalbums(File directory) {
    subalbums.clear();

    File[] files = directory.listFiles();

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


  private void loadPictures(File originalsDirectory, File pictureReferencesReadFile) {
    clearPictures();
    loadPictures(originalsDirectory);
    removeNonPictures();
    if (pictureReferencesReadFile != null)
      loadPictureReferences(pictureReferencesReadFile);
    sortPicturesByDateTime();
  }


  private void clearPictures() {
    savePictures();
    pictures.clear();
    sortedPictures.clear();
  }


  private void savePictures() {
    for (Iterator i = sortedPictures.iterator(); i.hasNext();)
      ((Picture) i.next()).save();
  }


  private void loadPictures(File directory) {
    File[] files = directory.listFiles();

    for (int i=0; i < files.length; i++) {
      File file = files[i];
      try {
        if (!file.isDirectory())
          addFile(file);
      } catch (IOException e) {
        /** @todo XXXX */
      }
    }
  }


  private void removeNonPictures() {
    for (Iterator i = pictures.entrySet().iterator(); i.hasNext(); ) {
      Map.Entry entry = (Map.Entry) i.next();
      PictureLocal picture = (PictureLocal) entry.getValue();
      if (!picture.isPicture())
        i.remove();
    }
  }


  private void loadPictureReferences(File file) {
    try {
      org.podval.album.jaxb.PictureReferences xml =
        JAXB.unmarshallPictureReferences(file);

      for (Iterator references = xml.getReferences().iterator(); references.hasNext();) {
        PictureReference picture = PictureReference.load(
          (org.podval.album.jaxb.PictureReferences.Picture) references.next()
        );

        addPicture(picture);
      }
    } catch (JAXBException e) {
      /** @todo  */
    }
  }


  private void savePictureReferences() {
    try {
      File file = getPictureReferencesWriteFile();
      if (file != null) {
        org.podval.album.jaxb.PictureReferences result =
          JAXB.getObjectFactory().createPictureReferences();

        for (Iterator i = getPictures().iterator(); i.hasNext();) {
          Picture picture = (Picture) i.next();
          if (picture instanceof PictureReference) {
            PictureReference reference = (PictureReference) picture;
            result.getReferences().add(reference.toXml());
          }
        }

        JAXB.marshall(result, file);
      }
    } catch (FileNotFoundException e) {
      /** @todo  */
    } catch (IOException e) {
      /** @todo  */
    } catch (JAXBException e) {
      /** @todo  */
    }
  }


  private void sortPicturesByDateTime() {
    sortedPictures.addAll(pictures.values());
  }


  private void addSubalbum(File file) throws IOException {
    String name = file.getName();

    if (subalbums.get(name) != null)
      throw new IOException("Duplicate case-sensitive subalbum name " + name);

    subalbums.put(name, new AlbumLocal(this, name));
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
      picture = new PictureLocal(name);
      picture.setAlbum(this);
      pictures.put(picture.getName(), picture);
    }

    picture.addFile(file, name, extension);
  }


  public void changed(Picture picture) {
    if (picture instanceof PictureReference)
      pictureReferencesChanged();
    else
      picturesChanged = true;

    changed();
  }


  public void addPictureReference(String path) {
    loadPictures();
    addPicture(new PictureReference(path));
    pictureReferencesChanged();
  }

  private void addPicture(Picture picture) {
    picture.setAlbum(this);
    pictures.put(picture.getName(), picture);
    sortedPictures.add(picture);
  }


  public void removePictureReference(String name) {
    loadPictures();

    Picture result = (Picture) pictures.get(name);
    if (result != null) {
      if (!(result instanceof PictureReference))
        throw new IllegalArgumentException("Not a reference picture!");

      pictures.remove(name);
      sortedPictures.remove(result);
      pictureReferencesChanged();
    }
  }


  public void save() {
    if (picturesChanged) {
      savePictures();
      picturesChanged = false;
    }

    if (metadataChanged) {
      saveMetadata();
      metadataChanged = false;
    }

    if (pictureReferencesChanged) {
      savePictureReferences();
      pictureReferencesChanged = false;
    }
  }


  private static final String METADATA_FILE_NAME = "album.xml";


  private File getMetadataReadFile() {
    if (metadataReadFile == null)
      metadataReadFile = getMetadataReadFile(METADATA_FILE_NAME);
    return metadataReadFile;
  }


  private File getMetadataWriteFile() {
    return getMetadataWriteFile(METADATA_FILE_NAME);
  }


  private static final String PICTURE_REFERENCES_FILE_NAME = "pictures.xml";


  private File getPictureReferencesWriteFile() {
    return getMetadataWriteFile(PICTURE_REFERENCES_FILE_NAME);
  }


  public File getMetadataReadFile(String name) {
    File result = null;

    if (result == null)
      result = ifCanRead(new File(getMetadataDirectory(), name));
    if (result == null)
      result = ifCanRead(new File(getDirectory(), name));

    return result;
  }


  public File getMetadataWriteFile(String name) {
    File result = null;

    if (result == null)
      result = ifCanWrite(new File(getDirectory(), name));
    if (result == null)
      result = ifCanWrite(new File(getMetadataDirectory(), name));

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


  public File getDirectory() {
    if (directory == null) {
      directory = new File(getParent().getDirectory(), getName());
      checkDirectoryExists(directory);
    }

    return directory;
  }


  public File getGeneratedDirectory() {
    if (generatedDirectory == null) {
      /** @todo ability to make generated directory a subdirectory of originals one? */
      generatedDirectory = new File(getParent().getGeneratedDirectory(), getName());
      ensureDirectoryExists(generatedDirectory);
    }

    return generatedDirectory;
  }


  public File getMetadataDirectory() {
    if (metadataDirectory == null) {
      metadataDirectory = new File(getParent().getMetadataDirectory(), getName());
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


  protected static void ensureDirectoryExists(File directory) {
    if (directory.exists()) {
      if (!directory.isDirectory())
        throw new IllegalArgumentException(directory + " is not a directory.");
    } else {
      if (!directory.mkdirs())
        throw new IllegalArgumentException("Can not create " + directory);
    }
  }


  public void pictureReferencesChanged() {
    pictureReferencesChanged = true;
    changed();
  }


  protected void changed() {
    synchronized (changedAlbums) {
      changedAlbums.add(this);
    }
  }


  public static void saveChanged() {
    synchronized (changedAlbums) {
      for (Iterator albums = changedAlbums.iterator(); albums.hasNext();) {
        AlbumLocal album = (AlbumLocal) albums.next();
        album.save();
      }
      changedAlbums.clear();
    }
  }


  private static final Set changedAlbums = new HashSet();


  private File directory;


  private File metadataDirectory;


  private File generatedDirectory;


  private File metadataReadFile;


  private long metadataLoaded = 0;


  private boolean metadataChanged;


  private long subalbumsLoaded;


  private File pictureReferencesReadFile;


  private long picturesLoaded = 0;


  private boolean picturesChanged;


  private boolean pictureReferencesChanged;


  /**
   * map<String name, Album>
   */
  private final Map subalbums = new TreeMap(String.CASE_INSENSITIVE_ORDER);


  /**
   * map<String name, Picture>
   */
  private final Map pictures = new TreeMap(String.CASE_INSENSITIVE_ORDER);


  private final Set sortedPictures = new TreeSet();
}
