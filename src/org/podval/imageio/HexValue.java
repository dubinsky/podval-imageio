package org.podval.imageio;

import javax.imageio.stream.ImageInputStream;

import java.io.IOException;


public class HexValue extends SimpleValue {

  private static final int MAX_RECORD_LENGTH = 40;


  public static Entry read(String name, ImageInputStream in, long length)
    throws IOException
  {
    Entry result;

    if (length < MAX_RECORD_LENGTH) {
      byte[] value = new byte[(int) length];
      in.read(value);
      result = new HexValue(name, value);
    } else {
      result = new PointerValue(name, in.getStreamPosition(), length);
    }

    return result;
  }


  public HexValue(String name, byte[] value) {
    super(name);
    this.value = value;
  }


  private static final char[] HEX_DIGIT = new char[]
    { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};


  private static char toHexDigit(int d) {
    return HEX_DIGIT[d];
  }


  protected String getValueAsString() {
    StringBuffer result = new StringBuffer(2*value.length);
    for (int i=0; i<value.length; i++) {
      byte b = value[i];
      result.append(toHexDigit( b       & 0x0F));
      result.append(toHexDigit((b >> 4) & 0x0F));
    }
    return new String(result);
  }


  private final byte[] value;
}
