package com.sqlbenchmark.configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.sqlbenchmark.utility.Benchmarker;

/**
 * Classe per utilizzare un database tramite JDBC con benckmark dedicati a
 * insert e select su chiavi primarie
 */
public class DatabaseWithBenchmark {

   // Credenziali e stringa di connessione privati
   private final String URL;
   private final String USER;
   private final String PASSWORD;

   // Mantengo l'oggetto connessione per istanziarlo solo la prima volta che lo
   // invoco
   private Connection connection;

   // Oggetti dedicati al benchmark per gli statement di insert e di select con
   // chiave primaria
   private Benchmarker insertBenchMark;
   private Benchmarker selectPkBenchMark;

   // Pattern builder
   private DatabaseWithBenchmark(String URL, String USER, String PASSWORD) {
      this.URL = URL;
      this.USER = USER;
      this.PASSWORD = PASSWORD;

      insertBenchMark = Benchmarker.build();
      selectPkBenchMark = Benchmarker.build();
   }

   /**
    * Ottiene un'istanza della classe che permette di connettersi al database
    * scelto in base alla configurazione inettata
    *
    * @param configuration Configurazione per connessione al database
    * @return ritorna un'istanza della classe configurata e pronta per la
    *         connessione
    */
   public static DatabaseWithBenchmark build(Configuration configuration) {
      DatabaseWithBenchmark database;

      database = new DatabaseWithBenchmark(
            configuration.getProperty("database.url"),
            configuration.getProperty("database.username"),
            configuration.getProperty("database.password"));

      return database;
   }

   /**
    * @return Ritorna l'oggetto benchmark dedicato agli statement di insert
    */
   public Benchmarker getInsertBenchMark() {
      return insertBenchMark;
   }

   /**
    * @return Ritorna l'oggetto benchmark dedicato agli statement di select su
    *         chiave primaria
    */
   public Benchmarker getSelectPkBenchMark() {
      return selectPkBenchMark;
   }

   /**
    * @return Ritorna l'oggetto Connection JDBC per effettuare gli statement sul
    *         database già configurato
    */
   public Connection getConnection() throws SQLException {
      if (this.connection == null) {
         this.connection = DriverManager.getConnection(this.URL, this.USER, this.PASSWORD);
      }
      return this.connection;
   }

   /**
    * Metodo che esegue un preparedStatement già pronto, effettua il calcolo delle
    * tempistiche e aggiorna la statistica sull'oggetto di benchmark dedicato agli
    * statement di insert
    * 
    * @return Ritorna un array intero che rappresenta le righe modificate per ogni
    *         statement eseguito
    */
   public int[] executeBatchWithBenchmark(PreparedStatement preparedStatement)
         throws SQLException {

      insertBenchMark.start();

      int[] result = preparedStatement.executeBatch();

      insertBenchMark.end();

      return result;
   }

   /**
    * Metodo che esegue una query SQL di selezione, effettua il calcolo delle
    * tempistiche e aggiorna la statistica sull'oggetto di benchmark dedicato agli
    * statement di select con chiave primaria
    * 
    * @return Ritorna un oggetto ResultSet che rappresenta il cursore della query
    */
   public ResultSet executeQueryWithBenchmark(String sql) throws SQLException {

      selectPkBenchMark.start();

      ResultSet rs = this.connection.createStatement().executeQuery(sql);

      selectPkBenchMark.end();

      return rs;
   }

}
