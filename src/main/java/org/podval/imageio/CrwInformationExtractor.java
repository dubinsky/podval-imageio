/* $Id$ */

package org.podval.imageio;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.stream.ImageInputStream;


public class CrwInformationExtractor {

  public CrwInformationExtractor(ImageInputStream in)
    throws IOException
  {
    new CiffReader().read(in, new ReaderHandler() {

      public boolean startFolder(int tag, String name) {
        boolean result =
          ((level == 0) && (tag ==  0)) || // ciff
          ((level == 1) && (tag == 10)) || // imageProperties
          ((level == 2) && (tag == 11));   // canonRawProperties

        if (level == 3) {

          if (tag == 49) { // sensor
            result = true;
            inSensor = true;
          } else

          if (tag == 53) { // decodeTable
            result = true;
            inDecodeTable = true;
          }
        }

        if (result) {
          level++;
        }

        return result;
      }


      public void endFolder() {
        level--;

        inSensor = false;
        inDecodeTable = false;
      }


      public ValueAction atValue(int tag, String name, int count) {
        ValueAction result = ValueAction.SKIP;

        if (isWidthValue(tag) || isHeightValue(tag) || isDecodeTableValue(tag) || isBytesToSkip(tag)) {
          result = ValueAction.VALUE;
        } else

        if ((level == 1) && (tag == 5)) {
          result = ValueAction.RAW;
        }

        return result;
      }


      public void handleValue(int tag, String name, int count, Object value) {
        if (isWidthValue(tag)) {
          width = (Integer) value;
        } else

        if (isHeightValue(tag)) {
          height = (Integer) value;
        } else

        if (isDecodeTableValue(tag)) {
          decodeTableNumber = (Integer) value;
        } else

        if (isBytesToSkip(tag)) {
          bytesToSkip = (Integer) value;
        }
      }


      public void handleRawValue(int tag, String name, int count, ImageInputStream is)
        throws IOException
      {
        crwOffset = is.getStreamPosition();
      }


      public String getMake() {
        return null;
      }
    });

    in.close();
  }


  private boolean isWidthValue(int tag) {
    return inSensor && (tag == 1);
  }


  private boolean isHeightValue(int tag) {
    return inSensor && (tag == 2);
  }


  private boolean isDecodeTableValue(int tag) {
    return inDecodeTable && (tag == 0); // 1?
  }


  private boolean isBytesToSkip(int tag) {
    return inDecodeTable && (tag == 2);
  }


  private int level = 0;


  private boolean inSensor = false;


  private boolean inDecodeTable = false;


  public int width;


  public int height;


  public int decodeTableNumber;


  public long crwOffset;


  public int bytesToSkip;
}
