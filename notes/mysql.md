# Mysql notes

## Export

```
mysqldump -u gagweb --password=password gagweb > gagweb.dump.sql

cat ./gagweb.dump.sql | mysql -u gagweb --password=password -h localhost gagweb
```

## Delete DataLines

```
delete dl, fdl from FingerDataLine as fdl inner join DataLine as dl on fdl.id = dl.id where dl.gesture_id < 15;

delete g from gesture as g where id < 15;
```

## Diff gesture's max & min timestamp

```
select min(dl.timestamp), max(dl.timestamp), max(dl.timestamp) - min(dl.timestamp) from DataLine as dl where dl.gesture_id = 27 order by dl.timestamp;
```

```
select * from DataLine as dl where dl.gesture_id = 27 and dl.timestamp in (select min(dl2.timestamp) from DataLine as dl2 where dl2.gesture_id = 27);
select * from DataLine as dl where dl.gesture_id = 27 and dl.timestamp in (select max(dl2.timestamp) from DataLine as dl2 where dl2.gesture_id = 27);
```