package org.podval.album;

import java.util.Iterator;

import java.io.IOException;

import javax.xml.bind.JAXBException;


public class Main {

  public static void main(String[] args) throws IOException, JAXBException {
    int argNum = 0;

    if (args.length < 2) { usage(); return; }
    String path = args[argNum++];
    String command = args[argNum++];

    Album.setRoot("/mnt/extra/Photo", "/tmp/metadataDirectory", "/tmp/generatedDirectory");

    Album album = Album.getByPath(path);

    System.out.println("Path " + album.getPath() + " (" + path + ")");

    if ("view".equals(command)) viewAlbum(album); else
    if ("title".equals(command)) setAlbumTitle(album, args, argNum); else
    if ("add".equals(command)) addToAlbum(album, args, argNum); else
    if ("remove".equals(command)) removeFromAlbum(album, args, argNum); else
//  if ("rotate".equals(command)) ?????; else
      { System.out.println("Command not recognized: " + command); usage(); return; }

    album.save();
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


  private static void addToAlbum(Album album, String[] args, int argNum) {
    if (argNum+1 >= args.length) { usage(); return; }

    String fromPath = args[argNum++];
    for (; argNum < args.length;)
      album.addPictureReference(fromPath + ":" + args[argNum++]);
  }


  private static void removeFromAlbum(Album album, String[] args, int argNum) {
    if (argNum+1 >= args.length) { usage(); return; }

    String fromPath = args[argNum++];
    for (; argNum < args.length;)
      album.removePictureReference(fromPath + ":" + args[argNum++]);
  }
}
