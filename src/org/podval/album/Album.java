package org.podval.album;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.StringTokenizer;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Validator;
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
    Album result = root;

    if (result == null)
      throw new NullPointerException("Root is not set");

//    if (!path.startsWith("/"))
//      throw new IllegalArgumentException("Path does not start with '/'.");

    StringTokenizer tokenizer = new StringTokenizer(path, "/");
    while (tokenizer.hasMoreTokens() && (result != null)) {
      result = ((Album) result).getSubdirectory(tokenizer.nextToken());
    }

    if (result == null)
      throw new NullPointerException("No album at path: " + path);

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
    this.parent = parent;
    this.name = name;

    if (parent == null)
      throw new NullPointerException("Directory parent is null.");

    if (name == null)
      throw new NullPointerException("Directory name is null.");
  }


  public Album getParent() {
    return parent;
  }


  public String getName() {
    return name;
  }


  public void setTitle(String value) {
    title = value;
    metadataChanged = true;
  }


  public String getTitle() {
    return (title != null) ? title : name;
  }


  public int getNumSubdirectories() {
    ensureSubdirectoriesLoaded();
    return subdirectories.size();
  }


  public Album getSubdirectory(String name) {
    ensureSubdirectoriesLoaded();
    return (Album) subdirectories.get(name);
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


  public File getOriginalsDirectory() {
    if (originalsDirectory == null) {
      originalsDirectory = new File(parent.getOriginalsDirectory(), name);

      if (!originalsDirectory.exists())
        throw new IllegalArgumentException("Directory does not exist: " + originalsDirectory);

      if (!originalsDirectory.isDirectory())
        throw new IllegalArgumentException("Not a directory: " + originalsDirectory);
    }

    return originalsDirectory;
  }


  public File getGeneratedDirectory() {
    if (generatedDirectory == null) {
      /** @todo now it would be nice to be able to place generated directory inside the originals one ... */
      generatedDirectory = new File(parent.getGeneratedDirectory(), name);

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

    return generatedDirectory;
  }


  public File getMetadataDirectory() {
    if (metadataDirectory == null) {
      /** @todo alternative policy is - try originals directory first... */
      metadataDirectory = new File(parent.getMetadataDirectory(), name);

      if (metadataDirectory.exists()) {
        if (!metadataDirectory.isDirectory())
          throw new IllegalArgumentException("Not a directory: " + metadataDirectory);

//      if (!metadataDirectory.canWrite())
//        throw new IllegalArgumentException("Can not write to: " + metadataDirectory);
      } else {
        if (!metadataDirectory.mkdirs())
          throw new IllegalArgumentException("Can not create: " + metadataDirectory);
      }
    }

    return metadataDirectory;
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
      loadMetadata();
    }
  }


  private void loadSubdirectories() {
    File[] files = getOriginalsDirectory().listFiles();

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

    for (Iterator i = pictures.entrySet().iterator(); i.hasNext();) {
      Map.Entry entry = (Map.Entry) i.next();
      PictureLocal picture = (PictureLocal) entry.getValue();
      if (!picture.isPicture())
        i.remove();
    }

    /** @todo sort pictures by date. */
  }


  private void addSubdirectory(File file) throws IOException {
    String name = file.getName();

    if (subdirectories.get(name) != null)
      throw new IOException("Duplicate case-sensitive subdirectory name " + name);

    subdirectories.put(name, new Album(this, name));
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


  private File getMetadataFile() {
    return new File(getMetadataDirectory(), "album.xml");
  }


  private void loadMetadata() {
    try {
      File file = getMetadataFile();
      if (file.exists()) {
        InputStream in = new FileInputStream(file);
        JAXBContext jc = JAXBContext.newInstance("org.podval.album.jaxb");
        Unmarshaller u = jc.createUnmarshaller();
        u.setValidating(true);

        org.podval.album.jaxb.Album xml =
          (org.podval.album.jaxb.Album) u.unmarshal(in);

        loadMetadata(xml);
      }
    } catch (IOException e) {
      /** @todo  */
    } catch (JAXBException e) {
      /** @todo  */
    }
  }


  private void loadMetadata(org.podval.album.jaxb.Album xml) {
    title = xml.getTitle();
    for (Iterator pictureRefs = xml.getPictures().iterator(); pictureRefs.hasNext();)
      loadPictureRef((org.podval.album.jaxb.PictureRef) pictureRefs.next());
  }


  private void loadPictureRef(org.podval.album.jaxb.PictureRef xml) {
    String path = xml.getAlbum();
    String referentName = xml.getName();
    String name = xml.getLocalName();
    addPicture(path, referentName, name);
  }


  public void save() throws FileNotFoundException, IOException, JAXBException {
    if (metadataChanged) {
      JAXBContext jc = JAXBContext.newInstance("org.podval.album.jaxb");
      org.podval.album.jaxb.Album metadata = buildMetadata();

      OutputStream out = new FileOutputStream(getMetadataFile());
      Validator v = jc.createValidator();
      boolean valid = v.validateRoot(metadata);
      Marshaller m = jc.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
      m.marshal(metadata, out);
      out.close();
    }
  }


  private org.podval.album.jaxb.Album buildMetadata() throws JAXBException {
    org.podval.album.jaxb.ObjectFactory of = new org.podval.album.jaxb.ObjectFactory();
    org.podval.album.jaxb.Album result = of.createAlbum();

    if (title != null)
      result.setTitle(title);

    for (Iterator i = getPictures().iterator(); i.hasNext();) {
      Picture picture = (Picture) i.next();
      if (picture instanceof PictureRef) {
        PictureRef pictureRef = (PictureRef) picture;
        org.podval.album.jaxb.PictureRef xml = of.createAlbumTypePicture();
        xml.setAlbum(pictureRef.getPath());
        String referentName = pictureRef.getReferentName();
        xml.setName(referentName);
        String name = pictureRef.getName();
        if (!name.equals(referentName))
          xml.setLocalName(name);

        result.getPictures().add(xml);
      }
    }
    return result;
  }


  public void addPicture(String path, String referentName) {
    addPicture(path, referentName, null);
  }


  public void addPicture(String path, String referentName, String name) {
    ensurePicturesLoaded();

    if (name == null)
      name = referentName;

    /** @todo I do not have to put references into the Map,
     * since there is no need to be able to reference them,
     * and their names may conflict with the others, but FOR NOW....
     * Besides, maybe I do have to - I need to be able to remove them!
     *  */
    while (pictures.get(name) != null) name += "_";

    Picture picture = new PictureRef(this, path, referentName, name);

    pictures.put(name, picture);

    metadataChanged = true;
  }


  public void removePicture(String name) {
    ensurePicturesLoaded();

    Picture result = (Picture) pictures.get(name);
    if (result != null) {
      if (!(result instanceof PictureRef))
        throw new IllegalArgumentException(
          "Only REFERENCES to pictures can be removed!");
      pictures.remove(name);

      metadataChanged = true;
    }
  }


  private final Album parent;


  private final String name;


  private String title;


  private File originalsDirectory;


  private File metadataDirectory;


  private File generatedDirectory;


  private boolean subdirectoriesLoaded = false;


  private boolean picturesLoaded = false;


  /**
   * map<String name, Album>
   */
  private final Map subdirectories = new TreeMap(String.CASE_INSENSITIVE_ORDER);


  /**
   * map<String name, Picture>
   */
  private final Map pictures = new TreeMap(String.CASE_INSENSITIVE_ORDER);


  private boolean metadataChanged = false;
}
