package com.sqlbenchmark.entities;

/**
 * Classe per mappare i campi della tabella oggetto del benchmark
 */
public class MySimpleTable {
   private long id;
   private String description;

   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id = id;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public MySimpleTable(long id, String description) {
      this.id = id;
      this.description = description;
   }

}
