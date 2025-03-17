# gesture data extractor

Sample script for extracting data from gesture database

```
python gesture_data_extractor.py --host "localhost" --user "gagweb" --password "<password>" --database "gagweb" --gesture_id 24 --threshold 0.2 | tee gesture_data_extractor.py.`now_str`.log

python gesture_data_extractor.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id 43 --threshold-extraction 0.01 --threshold-recognition 0.2 --start --end | tee logs/gesture_data_extractor.py.`now_str`.log

python gesture_data_extractor.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id 44 --threshold-extraction 0.01 --threshold-recognition 0.8 --position 1 --suffix p_1_nsne_te_0.01 | tee logs/gesture_data_extractor.py.`now_str`.log
```

# delete gesture by id

```
python delete_gesture_data.py --host "localhost" --user "gagweb" --password "<password>" --database "gagweb" --gesture_id 24 --threshold 0.2 | tee delete_gesture_data.py.`now_str`.log
```


# confusion matrix

```
python confusion_matrix.py --host "localhost" --user "gagweb" --password "<password>" --database "gagweb" --gesture_ids 28 29 30 31 32  | tee logs/confusion_matrix.py.`now_str`.log


python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_ids 52 53 54 --positions 0 | tee logs/confusion_matrix.py.`now_str`.log


```
