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

select * from DataLine dl join FingerDataLine fdl on dl.id = fdl.id join WristDataLine wdl on dl.id = wdl.id where dl.gesture_id = 7;

```

## Upgrade

1.
```
select fdl.id, fdl.position, dl.position, dl.hand, fdl.hand, fdl.side from FingerDataLine as fdl join DataLine as dl on dl.id = fdl.id where gesture_id = 21;

update DataLine as dl set dl.position = 5 where dl.position = null and dl.gesture_id = 21;
update DataLine as dl set dl.hand = 1 where dl.hand is null and dl.gesture_id = 21;
alter table FingerDataLine drop column position;
alter table FingerDataLine drop column hand;
alter table FingerDataLine drop column side;
```

2.
```
TODO
```

## Filters

### Get N Datalines

```

```
