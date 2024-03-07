package com.sqlbenchmark;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.sqlbenchmark.configuration.Configuration;
import com.sqlbenchmark.configuration.Database;
import com.sqlbenchmark.repository.MySimpleTableRepository;
import com.sqlbenchmark.utility.Benchmarker;

public class Main {
    public static void main(String[] args) throws SQLException {

        Database db = Database.build(new Configuration());

        MySimpleTableRepository repo = new MySimpleTableRepository(db);

        // Creazione della tabella o eventuale cancellazione
        if (!repo.createTable()) {
            repo.truncateTable();
        }

        List<String> dizionario = new ArrayList<String>();
        for (int i = 0; i < 10000; i++) {
            dizionario.add("abc" + i);
        }
        repo.insert(dizionario, 3);

        for (int i = 0; i < 10000; i++) {
            repo.selectById(i);
        }

        reportBenchmark(db);
    }

    private static void reportBenchmark(Database db) {
        Set<String> distinctMethods = new HashSet<>();
        for (Benchmarker benchmark : db.getBenchmark()) {
            distinctMethods.add(benchmark.getMethod());
        }

        List<Benchmarker> benchmarkers = db.getBenchmark();

        for (String method : distinctMethods) {
            long min = Integer.MAX_VALUE;
            long max = 0;
            double sum = 0;
            long count = 0;
            double avg = 0;
            for (Benchmarker benchmarker : benchmarkers) {
                if (benchmarker.getMethod() == method) {
                    count++;
                    sum += benchmarker.getDuration();
                    if (min > benchmarker.getDuration()) {
                        min = benchmarker.getDuration();
                    }
                    if (max < benchmarker.getDuration()) {
                        max = benchmarker.getDuration();
                    }
                }
            }
            avg = sum / count;
            System.out.println("------------------");
            System.out.println(method);
            System.out.println("------------------");
            System.out.println("statements: " + count);
            System.out.println("min: " + min);
            System.out.println("max: " + max);
            System.out.println("avg: " + avg);
        }
    }
}