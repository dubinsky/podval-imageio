package org.podval.album;

import java.util.Iterator;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.Handler;
import java.util.logging.ConsoleHandler;

import java.io.IOException;

import javax.xml.bind.JAXBException;


public class Main {

  public static void main(String[] args) throws IOException, JAXBException {
    Handler handler = new ConsoleHandler();
    handler.setLevel(Level.ALL);
    Logger logger = Logger.getLogger("");
    logger.setLevel(Level.ALL);
    logger.addHandler(handler);

    int argNum = 0;

    if (args.length < 2) { usage(); return; }
    String path = args[argNum++];
    String command = args[argNum++];

    Album.setRoot("/mnt/extra/Photo", "/tmp/metadataDirectory", "/tmp/generatedDirectory");

    Album album = null;
    Picture picture = null;

    boolean isAlbum = (path.indexOf(':') == -1);

    if (isAlbum)
      album = Album.getByPath(path);
    else
      picture = Picture.getByPath(path);

    System.out.println(((isAlbum) ? "Album " : "Pciture ") +  path);

    if (isAlbum) {
      if ("view".equals(command))
        viewAlbum(album);
      else
      if ("title".equals(command))
        setAlbumTitle(album, args, argNum);
      else
      if ("add".equals(command))
        addToAlbum((AlbumLocal) album, args, argNum);
      else
      if ("remove".equals(command))
        removeFromAlbum((AlbumLocal) album, args, argNum);
      else {
        System.out.println("Command not recognized: "+command);
        usage();
      }

    } else {
      if ("view".equals(command))
        viewPicture(picture);
      else
      if ("title".equals(command))
        setPictureTitle(picture, args, argNum);
      else
      if ("left".equals(command))
        picture.rotateLeft();
      else
      if ("right".equals(command))
        picture.rotateRight();

      else {
        System.out.println("Command not recognized: "+command);
        usage();
      }
    }

    AlbumLocal.saveChanged();
  }


  private static void usage() {
    System.out.println("Usage: <album path> <command> <command-specific arguments>");
    System.out.println();
    System.out.println("Recognized album commands:");
    System.out.println("  view - display album information");
    System.out.println("  title <title> - set album title");
    System.out.println("  add <from-album path> <name>+ - adds pictures from another album");
    System.out.println("  remove <name>+ - removes reference-pictures from the album");
    System.out.println("Recognized picture commands:");
    System.out.println("  view - display album information");
    System.out.println("  title <title> - set picture title");
    System.out.println("  left - rotate picture 270 degrees");
    System.out.println("  right - rotate picture 90 degrees");
  }


  private static void viewAlbum(Album album) throws IOException {
    System.out.println("Title: " + album.getTitle());
    System.out.println(album.getNumSubalbums() + " subalbums:");
    for (Iterator i=album.getSubalbums().iterator(); i.hasNext();) {
      Album subalbum = (Album) i.next();
      System.out.println("  " + subalbum.getName() + " (" + subalbum.getTitle() + ")");
    }
    System.out.println(album.getNumPictures() + " pictures:");
    for (Iterator i=album.getPictures().iterator(); i.hasNext();) {
      Picture picture = (Picture) i.next();
      picture.getThumbnailFile();
      System.out.println("  " + picture.getName() + " (" + picture.getDateTimeString() + ")");
    }
  }


  private static void setAlbumTitle(Album album, String[] args, int argNum) {
    if (argNum >= args.length) { usage(); return; }

    String title = args[argNum++];
    album.setTitle(title);
  }


  private static void addToAlbum(AlbumLocal album, String[] args, int argNum) {
    if (argNum+1 >= args.length) { usage(); return; }

    String fromPath = args[argNum++];
    for (; argNum < args.length;)
      album.addPictureReference(fromPath + ":" + args[argNum++]);
  }


  private static void removeFromAlbum(AlbumLocal album, String[] args, int argNum) {
    if (argNum+1 >= args.length) { usage(); return; }

    String fromPath = args[argNum++];
    for (; argNum < args.length;)
      album.removePictureReference(fromPath + ":" + args[argNum++]);
  }


  private static void viewPicture(Picture picture) throws IOException {
    System.out.println("Title: " + picture.getTitle());
    System.out.println("Orientation: " + picture.getOrientation());
    System.out.println("Timestamp: " + picture.getDateTimeString());
    picture.getThumbnailFile();
    picture.getScreensizedFile();
  }


  private static void setPictureTitle(Picture picture, String[] args, int argNum) {
    if (argNum >= args.length) { usage(); return; }

    String title = args[argNum++];
    picture.setTitle(title);
  }
}
