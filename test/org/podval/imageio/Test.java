/* $Id$ */

package org.podval.imageio;

import javax.imageio.stream.FileImageInputStream;

import java.io.File;
import java.io.IOException;


public class Test {

  private static void dump(Reader reader) throws Exception {
//    reader.read(new DumpingHandler());
    SaxDumpingHandler.dump(reader);
  }


  private static Reader ciffReader(String path) throws Exception {
    return new CiffReader(new FileImageInputStream(new File(path)));
  }


  public static Reader exifReader(String path) throws Exception {
    return new ExifReader(ExifStream.fromJpegStream(new FileImageInputStream(new File(path))));
  }


  private static void extractCrwThumbnails(String path) throws IOException {
    File file = new File(path);

    CrwThumbnailExtractor.extractBig(file,
      new File("/tmp/" + file.getName() + ".big.jpeg"));

    CrwThumbnailExtractor.extractSmall(file,
      new File("/tmp/" + file.getName() + ".small.jpeg"));
  }


  public static void main(String[] args) throws Exception {
    dump(ciffReader("/mnt/extra/Photo/ORIGINALS/g2/2/2/2250.crw"));
//    dump(exifReader("/mnt/extra/Photo/ORIGINALS/g2/2/2/2249.jpg"));
//    extractCrwThumbnails("/mnt/extra/Photo/ORIGINALS/g2/2/2/2250.crw");
  }
}
