# Semplice Benchmark di un database tramite JDBC

## Obiettivo

L'applicazione Java esegue un benchmark su un database compatibile con JDBC, valutando:

-  il tempo minimo/massimo/medio delle istruzioni INSERT
-  il tempo minimo/massimo/medio delle istruzioni SELECT (utilizzando il PK della COLONNA)

Per la valutazione dell'insert, l'applicazione si basa sul tempo impiegato richieste DML che usano l'API PreparedStatement effettuando "commit" ogni X dichiarazioni.
Il tempo è calcolato in millisecondi.
L'output del progetto è un file jar eseguibile dove verrà stampato il risultato del benchmark, generato nella cartella /target come di default.
Il nome della tabella su cui verranno effettuate le prove di valutazione è "my_simple_table".

## Configurazioni

Il progetto utilizza Maven come modello di progetto. Le configurazioni possibili nel file configuration.properties sono:

-  **database.url**: rappresenta la stringa di connessione al database utilizzato
-  **database.username**: nome utente di collegamento al database
-  **database.password**: password di collegamento al database
-  **benchmark.test_insert_number**: numero di insert per ricavare le tempistiche di benchmark
-  **benchmark.test_insert_select_pk**: numero di selezioni sulla chiave primaria di una tabella
-  **benchmark.max_insert_at_same_time**: numero di statement simultanei da eseguire tramite prepare

## Requisiti di sistema

Per poter eseguire la funzione sono necessari:

-  Java RE versione 17 (o successiva)
-  Database PostgreSQL (o altro tipo compatibile con JDBC)
