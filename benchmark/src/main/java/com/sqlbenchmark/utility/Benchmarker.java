package com.sqlbenchmark.utility;

import java.util.ArrayList;
import java.util.List;

import com.sqlbenchmark.configuration.OutMessage;

/**
 * Classe che esegue un benchmark per un obiettivo specifico
 */
public class Benchmarker {

   // Lista delle misurazioni di tempo effettuate
   private List<Long> durations;

   private long lastStart;

   private Benchmarker() {
   }

   public static Benchmarker build() {
      return new Benchmarker();
   }

   public List<Long> getDuration() {
      return durations;
   }

   /**
    * Metodo che inizia la misurazione del tempo in millisecondi
    */
   public void start() {
      if (this.durations == null) {
         this.durations = new ArrayList<>();
      }
      this.lastStart = System.currentTimeMillis();
      // this.start = System.nanoTime();

   }

   /**
    * Metodo che termina la misurazione di tempo iniziata
    */
   public void end() {
      this.durations.add(System.currentTimeMillis() - this.lastStart);
      // this.duration = System.nanoTime() - this.start;
   }

   /**
    * Emette l'output di min, max e media in millisecondi per la lista di
    * misurazioni effettuata
    * 
    * @param title Titolo per questa istanza di benchmark
    */
   public void report(String title) {

      long min = Integer.MAX_VALUE;
      long max = 0;
      double sum = 0;
      long count = 0;
      double avg = 0;

      for (long duration : this.durations) {
         count++;
         sum += duration;
         if (min > duration) {
            min = duration;
         }
         if (max < duration) {
            max = duration;
         }
      }

      avg = sum / count;

      final String um = "(ms)";

      OutMessage.print("");
      OutMessage.print(title);
      OutMessage.print("   Min " + um + ": " + min);
      OutMessage.print("   Max " + um + ": " + max);
      OutMessage.print("   Media " + um + ": " + avg);

   }

}
