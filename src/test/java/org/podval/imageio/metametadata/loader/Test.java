/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.MetaMetaData;
import org.podval.imageio.metametadata.Heap;


public class Test {

  public static void main(String[] args) throws Exception {
    Heap heap = MetaMetaData.get("ciff").getHeap("ciff-root");
//    Heap heap = MetaMetaData.get("exif").getHeap("exif-root");

    MetaMetaDataDumper.dump(heap, System.out);
//    EnumerationClassGenerator.generate(heap, "/tmp/g");
  }
}
