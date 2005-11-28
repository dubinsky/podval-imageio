/* $Id$ */

package org.podval.imageio;

import javax.imageio.stream.FileImageInputStream;

import java.io.File;


public class Test {

  private static void testCiff() throws Exception {
    System.out.println("CIFF:");
    MetaMetaData metaMetaData = loadCiffMetaMetaData();
    dump(ciffReader("/mnt/extra/Photo/ORIGINALS/g2/2/2/2250.crw"), metaMetaData);
    System.out.println();
  }


  private static void testExif() throws Exception {
    System.out.println("EXIF:");
    MetaMetaData metaMetaData = new MetaMetaData();
    dump(exifReader("/mnt/extra/Photo/ORIGINALS/g2/2/2/2249.jpg"), metaMetaData);
    System.out.println();
  }


  private static void dump(Reader reader, MetaMetaData metaMetaData) throws Exception {
//    reader.read(new DumpingHandler());
    new SaxDumpingHandler(reader, metaMetaData).dump(System.out);
  }


  private static Reader ciffReader(String path) throws Exception {
    return new CiffReader(new FileImageInputStream(new File(path)));
  }


  public static Reader exifReader(String path) throws Exception {
    return new ExifReader(ExifStream.fromJpegStream(new FileImageInputStream(new File(path))));
  }


  private static MetaMetaData loadCiffMetaMetaData() throws Exception {
    MetaMetaData result = new MetaMetaData();
    Loader.load("/home/dub/Projects/imageio/xml/ciff.xml", result);
//    Loader.load("/home/dub/Projects/imageio/xml/canon-ciff.xml", result);
    return result;
  }


  public static void main(String[] args) throws Exception {
//    testExif();
    testCiff();
  }
}
