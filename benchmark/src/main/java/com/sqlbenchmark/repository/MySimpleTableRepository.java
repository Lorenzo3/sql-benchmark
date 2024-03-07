package com.sqlbenchmark.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.sqlbenchmark.configuration.Database;
import com.sqlbenchmark.entities.MySimpleTable;

public class MySimpleTableRepository {

   private Database database;

   public static final String TABLE_NAME = "my_simple_table";

   // Injection
   public MySimpleTableRepository(Database database) {
      this.database = database;

   }

   private MySimpleTable serialize(ResultSet rs) throws SQLException {
      long id = rs.getLong("id");
      String description = rs.getString("description");
      return new MySimpleTable(id, description);
   }

   public boolean createTable() throws SQLException {
      final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS " + database.getConnection().getSchema() + "."
            + TABLE_NAME
            + "("
            + "id INTEGER NOT NULL,"
            + "description varchar(80), "
            + " PRIMARY KEY (id) "
            + ") ";
      int affected = this.database.getConnection().createStatement().executeUpdate(CREATE_STATEMENT);
      return affected > 0;
   }

   public boolean truncateTable() throws SQLException {
      final String TRUNCATE_STATEMENT = "TRUNCATE TABLE " + TABLE_NAME;
      int affected = this.database.getConnection().createStatement().executeUpdate(TRUNCATE_STATEMENT);
      return affected > 0;
   }

   public int insert(List<String> strings, int maxInsertAtSameTimeNumber) throws SQLException {
      final String INSERT_STATEMENT = "INSERT INTO " + TABLE_NAME + " (id, description) VALUES(?, ?)";

      int totalSuccessfulCommit = 0;
      PreparedStatement preparedStatement = this.database.getConnection().prepareStatement(INSERT_STATEMENT);

      long id = 0;
      long statementCount = 0;

      for (String row : strings) {
         id++;
         statementCount++;

         preparedStatement.setLong(1, id);
         preparedStatement.setString(2, row);
         preparedStatement.addBatch();

         if (maxInsertAtSameTimeNumber > 0 && statementCount == maxInsertAtSameTimeNumber) {
            totalSuccessfulCommit += this.database.executeBatchWithBenchmark(preparedStatement).length;
            preparedStatement.clearBatch();
            statementCount = 0;
         }
      }

      if (statementCount > 0) {
         totalSuccessfulCommit += this.database.executeBatchWithBenchmark(preparedStatement).length;
      }

      // Le righe inserite sono sempre 1 alla volta, quindi la lunghezza dell'array di
      // ritorno mi dà la somma di esse
      return totalSuccessfulCommit;

   }

   public MySimpleTable selectById(long inputId) throws SQLException {

      ResultSet rs = database
            .executeQueryWithBenchmark("select id, description from " + TABLE_NAME + " where id = " + inputId);

      if (rs.next()) {
         return this.serialize(rs);
      }

      return null;
   }
}
