package com.sqlbenchmark.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 
 * Classe di configurazione.
 * Si basa sul file configuration.properties presente nel percorso /com/resource
 */
public class Configuration {

   private Properties properties;

   public Configuration() {
      properties = new Properties();

      try (InputStream input = getClass().getResourceAsStream("/configuration.properties")) {
         properties.load(input);
      } catch (IOException e) {
         OutMessage.print("File di configurazione non trovato");
      }
   }

   public String getProperty(String key) {
      return properties.getProperty(key);
   }
}
