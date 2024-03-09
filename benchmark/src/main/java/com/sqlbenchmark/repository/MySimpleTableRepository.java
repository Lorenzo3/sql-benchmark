package com.sqlbenchmark.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.sqlbenchmark.configuration.DatabaseWithBenchmark;
import com.sqlbenchmark.entities.MySimpleTable;

/**
 * Classe Repository dedicata alle operazioni SQL effettuate sulla tabella
 * utilizzata per il benchmark
 */
public class MySimpleTableRepository {

   // Database già configurato dopo la costruzione dell'istanza
   private DatabaseWithBenchmark database;

   public static final String TABLE_NAME = "my_simple_table";

   /**
    * Costruttore del repository per la tabella tramite l'injection del database da
    * utilizzare
    */
   public MySimpleTableRepository(DatabaseWithBenchmark database) {
      this.database = database;
   }

   /**
    * Script di creazione della tabella (se non esiste)
    * 
    * @throws SQLException
    */
   public void createTable() throws SQLException {
      final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS " + database.getConnection().getSchema() + "."
            + TABLE_NAME
            + "("
            + "id INTEGER NOT NULL,"
            + "description varchar(80), "
            + " PRIMARY KEY (id) "
            + ") ";
      // Il metodo executeUpdate() ritorna un numero di righe DML modificate, ma nel
      // caso della creazione ritornerà 0 anche se la creazione ha avuto esito
      // positivo, quindi utilizzo il metodo come void e lascio gestire eventuali
      // errori all'eccezione SQLException
      this.database.getConnection().createStatement().executeUpdate(CREATE_STATEMENT);
   }

   /**
    * Metodo che esegue la truncate della tabella
    * 
    * @return True se sono stati eliminati record, false altrimenti
    * @throws SQLException
    */
   public boolean truncateTable() throws SQLException {
      final String TRUNCATE_STATEMENT = "TRUNCATE TABLE " + TABLE_NAME;
      int affected = this.database.getConnection().createStatement().executeUpdate(TRUNCATE_STATEMENT);
      return affected > 0;
   }

   /**
    * Metodo per l'inserimento di una lista di stringhe nella tabella
    * Il campo ID verrà popolato con un progressivo per ogni stringa inserita
    * Il campo descrizione è il valore della stringa stessa
    * 
    * @param strings                   è la lista di stringhe da inserire
    * @param maxInsertAtSameTimeNumber è il numero di insert da effettuare
    *                                  simultaneamente tramite preparedStatement
    * @return ritorna il numero totale di righe inserite
    * @throws SQLException
    */
   public int insert(List<String> strings, int maxInsertAtSameTimeNumber) throws SQLException {
      final String INSERT_STATEMENT = "INSERT INTO " + TABLE_NAME + " (id, description) VALUES(?, ?)";

      int totalSuccessfulCommit = 0;
      PreparedStatement preparedStatement = this.database.getConnection().prepareStatement(INSERT_STATEMENT);

      // Ricavo il massimo ID della tabella
      long id = this.selectMaxId();
      long statementCount = 0;

      for (String row : strings) {
         id++;
         statementCount++;

         preparedStatement.setLong(1, id);
         preparedStatement.setString(2, row);
         preparedStatement.addBatch();

         // Se ho deciso di effettuare un numero di insert simultanei e ho raggiunto quel
         // numero, eseguo gli statement che ho caricato fino ad ora, quindi svuoto lo
         // stack degli statement e inizio a contare di nuovo
         if (maxInsertAtSameTimeNumber > 0 && statementCount == maxInsertAtSameTimeNumber) {
            totalSuccessfulCommit += this.database.executeBatchWithBenchmark(preparedStatement).length;
            preparedStatement.clearBatch();
            statementCount = 0;
         }
      }

      // Se ci sono statement residui nello stack di esecuzione, li eseguo alla fine
      if (statementCount > 0) {
         totalSuccessfulCommit += this.database.executeBatchWithBenchmark(preparedStatement).length;
      }

      // Le righe inserite sono sempre 1 alla volta, quindi la lunghezza dell'array di
      // ritorno mi dà la somma di esse
      return totalSuccessfulCommit;

   }

   /**
    * Metodo che serve a mappare il ResultSet in un oggetto di tipo MySimpleTable
    */
   private MySimpleTable serialize(ResultSet rs) throws SQLException {
      long id = rs.getLong("id");
      String description = rs.getString("description");
      return new MySimpleTable(id, description);
   }

   /**
    * Metodo per ricavare il massimo ID presente nella tabella
    * 
    * @return valore del max(id) nella tabella, ritorna 0 se non ci sono record
    */
   public long selectMaxId() throws SQLException {

      ResultSet rs = database.getConnection().createStatement()
            .executeQuery("select max(id) as maxid from " + TABLE_NAME);

      if (rs.next()) {
         return rs.getLong("maxid");
      }

      return 0;
   }

   /**
    * Metodo per effetuare la selezione in una tabella per un certo ID
    * 
    * @param inputId id selezionato
    * @return Oggetto serializzato della classe MySimpleTable per l'id dato in
    *         input. Ritorna null se l'id non è presente in tabella.
    */
   public MySimpleTable selectById(long inputId) throws SQLException {

      ResultSet rs = database
            .executeQueryWithBenchmark("select id, description from " + TABLE_NAME + " where id = " + inputId);

      if (rs.next()) {
         return this.serialize(rs);
      }

      return null;
   }
}
