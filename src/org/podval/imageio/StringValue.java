package org.podval.imageio;

import javax.imageio.stream.ImageInputStream;

import java.io.IOException;


public class StringValue extends SimpleValue {

  private static final int MAX_STRING_LENGTH = 64;


  public StringValue(String name, String value) {
    super(name);
    this.value = value;
  }


  public StringValue(String name, ImageInputStream in, long length) throws IOException {
    this(name, readString(in, (int) length));
  }


  protected String getValueAsString() {
    return value;
  }


  public String getValue() {
    return value;
  }


  public static String readString(ImageInputStream in, int length)
    throws IOException
  {
    // Length of 0 indicates 'indefinite'. We limit 'em here...
    if (length == 0)
      length = MAX_STRING_LENGTH;

    StringBuffer result = new StringBuffer(length);

    for (int i=0; ; i++) {
      int b = in.readByte();
      if (b == 0) break;
      if (i == length) {
////        result.append("|NO ZERO. TRUNCATED?");
        break;
      }
      result.append((char) b);
    }

    return new String(result).trim();
  }


  private final String value;
}
