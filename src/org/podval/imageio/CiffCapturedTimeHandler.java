package org.podval.imageio;

import java.util.Date;
import java.util.TimeZone;


/** @todo revisit
 * I wanted to introduce field numbers for ...Handler.addField(),
 * but that is a bad idea: I have access to the names!
 * It is possible to introduce a 'bind' stage, when the handler finds
 * the fields it needs in the record by their names and saves references to them.
 * This requires reuse of the handlers, and is unlikely to improve anything...
 */

public class CiffCapturedTimeHandler extends FilteringMetadataHandler {
//  <field type="U32" name="timeCount"/> <!-- Seconds from Epoch: 1/1/1970 -->
//  <field type="S32" name="timeZoneCode"/> <!-- Zone offset in seconds. -->
//  <field type="U32" name="timeZoneInfo"/> <!-- If MSB is 0, treat zone code as 0. -->

  public void startGroup(Typed typed) {
  }


  public void addField(Field field, Object value) {
    if (field.getName().equals("timeCount")) {
      long secondsFromEpoch = ((Long) value).longValue();
      long millisFromEpoch = secondsFromEpoch*1000;
      int timezoneOffset = TimeZone.getDefault().getOffset(millisFromEpoch);
      result = new Date(millisFromEpoch-timezoneOffset);
    }
  }


  public void endGroup() {
    super.addField(new Field("dateTime"), result);
  }


  private Date result;
}
