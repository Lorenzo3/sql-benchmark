package com.sqlbenchmark.configuration;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Classe dedicata ai messaggi di output.
 * Inserendo questa parte in una classe apposita è possibile modificarne il
 * funzionamento senza andare a toccare i messaggi scritti all'interno del
 * progetto.
 * 
 */
public class OutMessage {

   public static void print(String message) {
      Date now = new Date();
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
      String formattedTime = simpleDateFormat.format(now);

      System.out.println(formattedTime + " - " + message);
   }
}
