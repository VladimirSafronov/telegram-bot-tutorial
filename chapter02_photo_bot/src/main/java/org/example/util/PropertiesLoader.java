package org.example.util;

import java.util.Map;

public class PropertiesLoader {

  private static final Map<String, String> ENV = System.getenv();

  public static String getProperty(String propertyName) {
    return ENV.get(propertyName);
  }
}
