package com.sqlbenchmark;

import java.util.ArrayList;
import java.util.List;

import com.sqlbenchmark.configuration.Configuration;
import com.sqlbenchmark.configuration.DatabaseWithBenchmark;
import com.sqlbenchmark.configuration.OutMessage;
import com.sqlbenchmark.repository.MySimpleTableRepository;

public class Main {
    public static void main(String[] args) {

        // Lettura della configurazione presente in configuration.properties
        Configuration config = new Configuration();

        // Ricavo i dati di configurazione del benchmark
        long testInsertNumber = Long.parseLong(config.getProperty("benchmark.test_insert_number"));
        long testSelectPKNumber = Long.parseLong(config.getProperty("benchmark.test_insert_select_pk"));
        int maxInsertAtSameTime = Integer.parseInt(config.getProperty("benchmark.max_insert_at_same_time"));

        if (testInsertNumber <= 0) {
            OutMessage.print("Il numero di insert da misurare deve essere > 0");
            return;
        }

        if (testSelectPKNumber <= 0) {
            OutMessage.print("Il numero di select su chiave primaria da misurare deve essere > 0");
            return;
        }

        OutMessage.print("----------------------");
        OutMessage.print("CONFIGURAZIONI DI TEST");
        OutMessage.print("----------------------");
        OutMessage.print("");
        OutMessage.print("Numero di insert: " + testInsertNumber + " (" + maxInsertAtSameTime + ") simultanei");
        OutMessage.print("Numero di select tramite primary key: " + testSelectPKNumber);
        OutMessage.print("");

        // Istanzio il database configurato in base ai parametri del file
        // configuration.properties
        DatabaseWithBenchmark db = DatabaseWithBenchmark.build(config);

        // Istanzio il repository che rappresenta le operazioni sulla tabella del
        // database
        MySimpleTableRepository repo = new MySimpleTableRepository(db);

        OutMessage.print("--------------");
        OutMessage.print("CREAZIONE DATI");
        OutMessage.print("--------------");
        OutMessage.print("");

        try {
            OutMessage.print("Creazione tabella");

            // Creo la tabella (se non esiste)
            repo.createTable();

            OutMessage.print("Cancellazione record");

            // Elimino eventuali dati presenti in tabella
            repo.truncateTable();

            OutMessage.print("");
            OutMessage.print("Inserimento record per test...");

            // Inizio ad inserire stringhe semplici per un numero di record letto nelle
            // configurazioni
            List<String> dizionario = new ArrayList<String>();
            for (int i = 0; i < testInsertNumber; i++) {
                dizionario.add("abc" + i);
            }
            repo.insert(dizionario, maxInsertAtSameTime);

            OutMessage.print("Fine inserimento record");
            OutMessage.print("");
            OutMessage.print("Selezione record per test...");

            // Inizio ad eseguire operazioni di select su chiave primaria per un numero di
            // volte letto nelle configurazioni
            for (int i = 0; i < testSelectPKNumber; i++) {
                repo.selectById(i);
            }

            OutMessage.print("Fine selezione record");
            OutMessage.print("");

            OutMessage.print("----------------------");
            OutMessage.print("ELABORAZIONE BENCHMARK");
            OutMessage.print("----------------------");

            // Genero l'output del benchmark
            db.getInsertBenchMark().report("Insert Statements");
            db.getSelectPkBenchMark().report("Select with PK Statements");

        } catch (Exception e) {
            OutMessage.print(e.getMessage());
        }

    }

}