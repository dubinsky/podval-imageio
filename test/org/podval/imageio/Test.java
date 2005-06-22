/* $Id$ */

package org.podval.imageio;

import javax.imageio.stream.FileImageInputStream;

import java.io.File;
import java.io.IOException;


public class Test {

  private static void dump(Reader reader, MetaMetaDataNG metametadata) throws Exception {
//    reader.read(new DumpingHandler());
    SaxDumpingHandler.dump(reader, metametadata);
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
    System.out.println("CIFF:");
    MetaMetaDataNG metaMetaData = new MetaMetaDataNG();
    Heap ciffHeap = new Heap("org_podval_imageio_ciff_1.0");
    metaMetaData.registerInitialHeap(0, ciffHeap);
    dump(ciffReader("/mnt/extra/Photo/ORIGINALS/g2/2/2/2250.crw"), metaMetaData);

    System.out.println();

    System.out.println("EXIF:");
    dump(exifReader("/mnt/extra/Photo/ORIGINALS/g2/2/2/2249.jpg"), null);

    extractCrwThumbnails("/mnt/extra/Photo/ORIGINALS/g2/2/2/2250.crw");
  }
}
