/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.MetaMetaData;
import org.podval.imageio.metametadata.Heap;


public class Test {

  public static void main(String[] args) throws Exception {
    MetaMetaData metaMetaData = MetaMetaData.get("ciff");
    Heap heap = metaMetaData.getHeap("imageProperties");
    MetaMetaDataDumper.dump(heap, System.out);
  }
}
