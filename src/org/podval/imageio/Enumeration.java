package org.podval.imageio;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class Enumeration {

  public void addDescription(int value, String description) {
    String oldDescription = getRawDescription(value);
    if (oldDescription != null)
      throw new IllegalArgumentException(
        "Attempt to change description for " + value +
        " from " + oldDescription +
        " to " + description
      );

    descriptions.put(new Integer(value), description);
  }


  private String getRawDescription(int value) {
    return (String) descriptions.get(new Integer(value));
  }


  public String getDescription(int value) {
    String result = getRawDescription(value);

    if (result == null) {
      result = "unknown-" + value;
      addDescription(value, result);
    }

    return result;
  }


  private final Map descriptions = new HashMap();
}
