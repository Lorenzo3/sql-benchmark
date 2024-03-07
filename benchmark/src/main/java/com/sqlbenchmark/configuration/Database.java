package com.sqlbenchmark.configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.sqlbenchmark.utility.Benchmarker;

public class Database {

   private final String URL;
   private final String USER;
   private final String PASSWORD;

   private Connection connection;

   private List<Benchmarker> benchmark = new ArrayList<>();

   // Costruttore privato per design pattern Builder
   private Database(String URL, String USER, String PASSWORD) {
      this.URL = URL;
      this.USER = USER;
      this.PASSWORD = PASSWORD;
   }

   // - PATTERN - BUILDER
   // In base alle configurazioni ritorno il database da collegare
   public static Database build(Configuration configuration) {
      Database database;

      database = new Database(
            configuration.getProperty("database.url"),
            configuration.getProperty("database.username"),
            configuration.getProperty("database.password"));

      return database;
   }

   public Connection getConnection() throws SQLException {
      if (this.connection != null) {
         return this.connection;
      }
      this.connection = DriverManager.getConnection(this.URL, this.USER, this.PASSWORD);
      return this.connection;
   }

   public List<Benchmarker> getBenchmark() {
      return benchmark;
   }

   public int[] executeBatchWithBenchmark(PreparedStatement preparedStatement)
         throws SQLException {

      Benchmarker bm = Benchmarker.build("execute Batch");
      bm.start();

      int[] result = preparedStatement.executeBatch();

      bm.end();
      this.benchmark.add(bm);

      return result;
   }

   public ResultSet executeQueryWithBenchmark(String sql) throws SQLException {

      Benchmarker bm = Benchmarker.build("execute Query");
      bm.start();

      ResultSet rs = this.connection.createStatement().executeQuery(sql);

      bm.end();
      this.benchmark.add(bm);

      return rs;
   }

}
