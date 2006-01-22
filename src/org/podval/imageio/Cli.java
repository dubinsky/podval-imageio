/* $Id$ */

package org.podval.imageio;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.FileImageInputStream;


public final class Cli {

  public static void main(String[] args) throws Exception {
    if (args.length < 2) {
      throw new Exception("Too few arguments!");
    }

    String command = args[0];

    if (command.equals("date")) {

    } else

    if (command.equals("thumbnails")) {
      for (int i=1; i<args.length; i++) {
        extractThumbnails(args[i]);
      }
    } else

    if (command.equals("decomress")) {

    } else

    if (command.equals("dump")) {
      for (int i=1; i<args.length; i++) {
        dump(args[i]);
      }

    } else {

      throw new Exception("Unknown command " + command + " not one of date, thumbnails, decompress, dump");
    }
  }


  private static void extractThumbnails(String path) throws IOException {
    int dot = path.lastIndexOf(".");
    String name;
    String extension;
    if (dot == -1) {
      name = path;
      extension = "";
    } else {
      name = path.substring(0, dot);
      extension = path.substring(dot);
    }

    CrwThumbnailExtractor.extract(
      new File(name + extension),
      new File(name + ".thumb.jpg"),
      new File(name + ".proof.jpg")
    );
  }


  private static void dump(String path) throws Exception {
    boolean crw = path.endsWith(".crw");

    ImageInputStream is = new FileImageInputStream(new File(path));

    SaxDumpingHandler.dump(
      crw ? new CiffReader() : new ExifReader(),
      crw ? is : ExifStream.fromJpegStream(is),
      Loader.get(crw ? "ciff" : "exif"),
      System.out
    );
  }


  private Cli() {
  }
}
