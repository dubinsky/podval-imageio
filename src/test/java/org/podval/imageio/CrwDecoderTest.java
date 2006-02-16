/* $Id$ */

package org.podval.imageio;

import junit.framework.TestCase;

public class CrwDecoderTest extends TestCase {

  public void test() {
    CrwDecoder decoder = CrwDecoder.getInstance(0, true);
    testDecode(0x04, decoder, false, false);
    testDecode(0x03, decoder, false, true , false);
    testDecode(0x05, decoder, false, true , true );
    testDecode(0x06, decoder, true , false, false);
    testDecode(0x02, decoder, true , false, true );
    testDecode(0x07, decoder, true , true , false, false);
    testDecode(0x01, decoder, true , true , false, true );
    testDecode(0x08, decoder, true , true , true , false, false);
    testDecode(0x09, decoder, true , true , true , false, true );
    testDecode(0x00, decoder, true , true , true , true , false);
    testDecode(0x0a, decoder, true , true , true , true , true , false);
    testDecode(0x0b, decoder, true , true , true , true , true , true , false);
    testDecode(0xff, decoder, true , true , true , true , true , true , true );
  }


  private void testDecode(int value, CrwDecoder decoder, boolean... bits) {
    assertEquals(value, decode(decoder, bits));
  }


  private int decode(CrwDecoder decoder, boolean[] bits) {
    for (boolean bit : bits) {
      assertTrue(decoder.hasNext());
      decoder = decoder.getNext(bit);
    }
    return decoder.getValue();
  }
}
