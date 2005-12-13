/* $Id$ */

package org.podval.imageio;

import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.FileImageInputStream;

import java.io.File;


public class Test {

  private static void testCiff() throws Exception {
    System.out.println("CIFF:");
    MetaMetaData metaMetaData = loadCiffMetaMetaData();
    ImageInputStream in = fromPath("/mnt/extra/Photo/ORIGINALS/g2/2/2/2250.crw");
    dump(new CiffReader(), in, metaMetaData);
    System.out.println();
  }


  private static void testExif() throws Exception {
    System.out.println("EXIF:");
    MetaMetaData metaMetaData = new MetaMetaData();
    ImageInputStream in = ExifStream.fromJpegStream(fromPath("/mnt/extra/Photo/ORIGINALS/g2/2/2/2249.jpg"));
    dump(new ExifReader(), in, metaMetaData);
    System.out.println();
  }


  private static ImageInputStream fromPath(String path) throws Exception {
    return new FileImageInputStream(new File(path));
  }


  private static void dump(Reader reader, ImageInputStream in, MetaMetaData metaMetaData) throws Exception {
    new SaxDumpingHandler(reader, in, metaMetaData).dump(System.out);
  }


  private static MetaMetaData loadCiffMetaMetaData() throws Exception {
    MetaMetaData result = new MetaMetaData();
    Loader.load("/home/dub/Projects/imageio/xml/ciff.xml", result);
//    Loader.load("/home/dub/Projects/imageio/xml/canon-ciff.xml", result);
    return result;
  }


  public static void main(String[] args) throws Exception {
    testExif();
    testCiff();
  }
}
