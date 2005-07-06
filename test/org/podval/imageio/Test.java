/* $Id$ */

package org.podval.imageio;

import javax.imageio.stream.FileImageInputStream;

import java.io.File;
import java.io.IOException;


public class Test {

  private static void dump(Reader reader, MetaMetaDataNG metametadata) throws Exception {
//    reader.read(new DumpingHandler());
    new SaxDumpingHandler(reader, metametadata).dump(System.out);
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


  private static void testCiff() throws Exception {
    System.out.println("CIFF:");
    MetaMetaDataNG metaMetaData = loadCiffMetaMetaData();
    dump(ciffReader("/mnt/extra/Photo/ORIGINALS/g2/2/2/2250.crw"), metaMetaData);
    System.out.println();
  }


  private static MetaMetaDataNG loadCiffMetaMetaData() throws Exception {
    MetaMetaDataNG result = new MetaMetaDataNG();
//    Loader.load("/home/dub/Projects/imageio/xml/ciff.xml", result);
//    Loader.load("/home/dub/Projects/imageio/xml/canon-ciff.xml", result);
    return result;
  }


  private static void testExif() throws Exception {
    System.out.println("EXIF:");
    MetaMetaDataNG metaMetaData = new MetaMetaDataNG();
    dump(exifReader("/mnt/extra/Photo/ORIGINALS/g2/2/2/2249.jpg"), metaMetaData);
    System.out.println();
  }


  public static void main(String[] args) throws Exception {
//    testExif();
    testCiff();

//    extractCrwThumbnails("/mnt/extra/Photo/ORIGINALS/g2/2/2/2250.crw");
  }
}
