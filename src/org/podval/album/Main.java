package org.podval.album;

import java.util.Iterator;

import java.io.IOException;


public class Main {

  public static void main(String[] args) throws IOException {
    int argNum = 0;

    if (args.length < 4) {
      usage();
      return;
    }

    String originalsRoot = args[0];
    String metadataRoot = args[1];
    String generatedRoot = args[2];

    argNum = 3;

    Album.setRoot(originalsRoot, metadataRoot, generatedRoot);

    String command = "album";

    for (; argNum < args.length; argNum++) {
      String arg = args[argNum];
      if (arg.startsWith("-")) {
        arg = arg.substring(1, arg.length());
        command = arg;
      } else
        doCommand(command, arg);
    }
  }


  private static void usage() {
    System.err.println("Usage: <command> <file>+");
    System.err.println();
    System.err.println("Recognized commands:");
  }


  private static void doCommand(String command, String arg) throws IOException {
    if ("album".equals(command)) viewAlbum(arg); else
      usage();
  }


  private static void viewAlbum(String path) throws IOException {
    System.out.print("Album at " + path);
    Album album = Album.getByPath(path);
    System.out.println(": " + album.getTitle());
    System.out.println(album.getNumSubdirectories() + " subdirectories:");
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
}
