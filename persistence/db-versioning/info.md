# DB versioning

## flyway migration example

before:

```
mysql -u gagweb --password=password -h localhost gagweb -e 'show tables;'
```

result:

```
+------------------+
| Tables_in_gagweb |
+------------------+
| DataLine         |
| FingerDataLine   |
| Gesture          |
| HandDevice       |
| SensorOffset     |
| User             |
| Users            |
| WristDataLine    |
| gesture          |
| handDevice       |
+------------------+
```

migrate (from ./persistence/):

```bash
mvn -Pflywaydb clean flyway:migrate -Dflyway.configFiles=./db-versioning/myFlywayConfig.conf -Dflyway.baselineOnMigrate=true
```

after:

```bash
mysql -u gagweb --password=password -h localhost gagweb -e 'show tables;'
```

result with new table ```employee```:

```
+-----------------------+
| Tables_in_gagweb      |
+-----------------------+
| DataLine              |
| FingerDataLine        |
| Gesture               |
| HandDevice            |
| SensorOffset          |
| User                  |
| Users                 |
| WristDataLine         |
| employee              |
| flyway_schema_history |
| gesture               |
| handDevice            |
+-----------------------+
```
