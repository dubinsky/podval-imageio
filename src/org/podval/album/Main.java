package org.podval.album;

import java.util.Iterator;

import java.io.IOException;

import javax.xml.bind.JAXBException;


public class Main {

  public static void main(String[] args) throws IOException, JAXBException {
    int argNum = 0;

    if (args.length < 5) { usage(); return; }

    String originalsRoot = args[argNum++];
    String metadataRoot = args[argNum++];
    String generatedRoot = args[argNum++];
    String command = args[argNum++];
    String path = args[argNum++];

    Album.setRoot(originalsRoot, metadataRoot, generatedRoot);

    Album album = Album.getByPath(path);
    if (album == null) {
      System.out.println("No album at path " + path);
    } else {
      System.out.println("Operating on album at path " + path);

      if ("view".equals(command)) viewAlbum(album); else
      if ("title".equals(command)) setAlbumTitle(album, args, argNum); else
      if ("add".equals(command)) addToAlbum(album, args, argNum); else
      if ("remove".equals(command)) removeFromAlbum(album, args, argNum); else
//    if ("rotate".equals(command)) ?????; else
        { System.out.println("Command not recognized: " + command); usage(); return; }

      album.save();
    }
  }


  private static void usage() {
    System.out.println("Usage: <album path> <command> <command-specific arguments>");
    System.out.println();
    System.out.println("Recognized commands:");
    System.out.println("  view - displays album information");
    System.out.println("  title <title> - sets album title");
    System.out.println("  add <from-album path> <name>+ - adds pictures from another album");
    System.out.println("  remove <name>+ - removes reference-pictures from the album");
////    System.err.println("  rotate - ?????");
  }


  private static void viewAlbum(Album album) throws IOException {
    System.out.println("Title: " + album.getTitle());
    System.out.println(album.getNumSubdirectories() + " subalbums:");
    for (Iterator i=album.getSubdirectories().iterator(); i.hasNext();) {
      Album subalbum = (Album) i.next();
      System.out.println("  " + subalbum.getTitle());
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


  private static void addToAlbum(Album album, String[] args, int argNum) {
    if (argNum+1 >= args.length) { usage(); return; }

    String fromPath = args[argNum++];
    for (; argNum < args.length;)
      album.addPicture(fromPath, args[argNum++]);
  }


  private static void removeFromAlbum(Album album, String[] args, int argNum) {
    if (argNum >= args.length) { usage(); return; }

    for (; argNum < args.length;)
      album.removePicture(args[argNum++]);
  }
}
