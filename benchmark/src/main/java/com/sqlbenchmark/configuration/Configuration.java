package com.sqlbenchmark.configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

   private Properties properties;

   public Configuration() {
      properties = new Properties();
      try (InputStream input = getClass().getResourceAsStream("/configuration.properties")) {
         if (input != null) {
            properties.load(input);
         } else {
            throw new FileNotFoundException("File configuration.properties not found");
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public String getProperty(String key) {
      return properties.getProperty(key);
   }
}
