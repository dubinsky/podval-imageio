package org.podval.imageio;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import java.util.Date;

import java.io.IOException;


/**
 */

public class Conversions {

  // DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
  private static final DateFormat dateTimeFormat =
    new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");


  /*
     Date in the YYYY:MM:DD HH:MM:SS format.
     When unknown, everything but colons may be filled with spaces.
  */
  public static Date parseExifDate(String value) throws IOException {
    Date result = null;

    try {
      result = dateTimeFormat.parse(value);
    } catch (ParseException e) {
      throw new IOException("Bad date format.");
    }

    return result;
  }


  /* Typical values:
     Av   F      Tv  Exp. time
      0   1      -5  30
      1   1.4    -4  15
      2   2      -3   8
      3   2.8    -2   4
      4   4      -1   2
      5   5.6     0   1
      6   8       1   1/2
      7  11       2   1/4
      8  16       3   1/8
      9  22       4   1/15
     10  32       5   1/30
                  6   1/60
                  7   1/125
                  8   1/250
                  9   1/500
                 10   1/1000
                 11   1/2000
   */


  public float convertApexApertureValueToFNumber(float value) {
     return (float) Math.pow(2.0, value/2);
  }


  public float convertApexShutterSpeedValueToExposureTime(float value) {
    return (float) (1.0f / Math.pow(2.0, value));
  }


//  public float getExposure() {
//    float result = 0;
//    if ((brightness != 0) && (iso != 0)) {
//      float sensitivity = (float) (Math.log(iso/3.125f) / Math.log(2));
//      result = brightness + sensitivity;
//    }
//    return result;
//  }
}
