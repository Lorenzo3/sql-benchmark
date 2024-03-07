package com.sqlbenchmark.utility;

public class Benchmarker {

   private String method;

   private long start;
   private long duration;

   private Benchmarker(String method) {
      this.method = method;
   }

   public static Benchmarker build(String method) {
      return new Benchmarker(method);
   }

   public String getMethod() {
      return method;
   }

   public long getDuration() {
      return duration;
   }

   public void start() {
      this.start = System.currentTimeMillis();
      // this.start = System.nanoTime();

   }

   public void end() {
      this.duration = System.currentTimeMillis() - this.start;
      // this.duration = System.nanoTime() - this.start;
   }

}
