/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.MetaMetaData;


public class Test {

  public static void main(String[] args) throws Exception {
    MetaMetaDataDumper.dump(MetaMetaData.get("ciff"), "ciff-root", System.out);
  }
}
