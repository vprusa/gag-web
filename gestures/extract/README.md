# gesture data extractor

Sample script for extracting data from gesture database

```
python gesture_data_extractor.py --host "localhost" --user "gagweb" --password "<password>" --database "gagweb" --gesture_id 24 --threshold 0.2 | tee gesture_data_extractor.py.`now_str`.log

python gesture_data_extractor.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id 43 --threshold-extraction 0.01 --threshold-recognition 0.2 --start --end | tee logs/gesture_data_extractor.py.`now_str`.log

python gesture_data_extractor.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id 44 --threshold-extraction 0.01 --threshold-recognition 0.8 --position 1 --suffix p_1_nsne_te_0.01 | tee logs/gesture_data_extractor.py.`now_str`.log

python gesture_data_extractor.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id 50 --threshold-extraction 0.01 --threshold-recognition 0.8 --position 1 --start --end --suffix p_1_wswe_te_0.01 | tee logs/gesture_data_extractor.py.`now_str`.log


```


## right hand switch

```
python gesture_data_extractor.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id 50 --threshold-extraction 0.01 --threshold-recognition 0.8 --position 1 --start --end --suffix p_1_wswe_te_0.01 | tee logs/gesture_data_extractor.py.`now_str`.log

python gesture_data_extractor.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id 61 --threshold-extraction 0.01 --threshold-recognition 0.2 --position 0 1 2 3 4 5 --start --end --suffix p_012345_wswe_te_0.01 | tee logs/gesture_data_extractor.py.`now_str`.log



for id in 64 65 66 67 68 69 70 71; do python gesture_data_extractor.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id $id --threshold-extraction 0.001 --threshold-recognition 0.2 --position 0 1 2 3 4 5 --start --end --suffix p_012345_wswe_te_0.01 | tee logs/gesture_data_extractor.py.`now_str`.log;  python gesture_data_extractor.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id $id --threshold-extraction 0.001 --threshold-recognition 0.2 --position 0 1 2 3 4 5 --start --suffix p_012345_wsne_te_0.01 | tee logs/gesture_data_extractor.py.`now_str`.log; done


for id in 61 62 63 64 65 66 67 68 69 70 71 ; do python gesture_data_extractor.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id $id --threshold-extraction 0.001 --threshold-recognition 0.2 --position 0 1 2 3 4 5 --start --end --suffix p_012345_wswe_te_0.01 | tee logs/gesture_data_extractor.py.`now_str`.log;  python gesture_data_extractor.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id $id --threshold-extraction 0.001 --threshold-recognition 0.2 --position 0 1 2 3 4 5 --start --end --align 1 --suffix p_012345_wsne_te_0.01 | tee logs/gesture_data_extractor.py.`now_str`.log; done


 python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id 61 --threshold-extraction 0.15 -v --threshold-recognition 0.2 --position 0 1 2 3 4 5 --start --end --align bottom:1 --suffix p_012345_wswe_te_0.15 | tee gesture_data_extractor.2.py.`now_str`.log

for id in 61 62 63 64 65 66 67 68 69 70 71 ; do python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id $i --threshold-extraction 0.15 -v --threshold-recognition 0.2 --position 0 1 2 3 4 5 --start --end --align bottom:1 --suffix p_012345_wswe_te_0.15 | tee gesture_data_extractor.2.py.`now_str`.log done


python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id 62 --min-points 1 -v --threshold-recognition 0.2 --position 0 1 2 3 4 5 --start --end --align nth:10 | tee logs-2025-03-28_17-11-15/gesture_data_extractor.2.py.`now_str`.log



```

# delete gesture by id

```
python delete_gesture_data.py --host "localhost" --user "gagweb" --password "<password>" --database "gagweb" --gesture_id 24 --threshold 0.2 | tee delete_gesture_data.py.`now_str`.log
```


# confusion matrix

```
python confusion_matrix.py --host "localhost" --user "gagweb" --password "<password>" --database "gagweb" --gesture_ids 28 29 30 31 32  | tee logs/confusion_matrix.py.`now_str`.log


python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_ids 52 53 54 --positions 0 | tee logs/confusion_matrix.py.`now_str`.log

python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gesture 74 --gestures 75 76 --class-threshold 0.5 | tee logs/confusion_matrix.py.`now_str`.log


python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gesture 74 --gestures 75 76 --class-threshold 0.5 | tee logs/confusion_matrix.py.`now_str`.log


python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gestures 74 75  --gestures 76 77 78 79 80 81 82 83 --actual-matches 1 1 1 1 1 1 0 1 --positions 1 --angular-diff --calc-threshold --calc-max | tee logs/confusion_matrix.py.`now_str`.log


```
## conf matrix and gesture gen

```
python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gestures 74 75 76  --gestures 77 78 79 80 81 82 83 --actual-matches 1 1 1 1 1 0 1 --positions 1 --angular-diff --calc-threshold --calc-max --save-ref-gesture x-r-index-avg--74--76 | tee logs/confusion_matrix.py.`now_str`.log


python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gestures  154 158 162  --gestures 153 155 156 157 159 160 161  --actual-matches 1 1 1 1 1 0 0 --positions 0 1 2 3 4 5 --angular-diff --calc-threshold --calc-max  | tee logs/confusion_matrix.py.`now_str`.log

python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gestures  154 158 162  --gestures 153 155 156 157 159 160 161  --actual-matches 1 1 1 1 1 0 0 --positions 0 1 2 3 4 5 --angular-diff --calc-threshold --calc-max --save-ref-gesture x-r-switch-avg-max--154-158-162 | tee logs/confusion_matrix.py.`now_str`.log



python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gestures 153 157 158 162  --gestures 154 155 156 159 160 161 163  --actual-matches 1 1 1 1 0 0 1 --positions 0 1 2 3 4 5 --angular-diff --calc-threshold --calc-max --save-ref-gesture x-r-switch-avg-max--153-157-158-162 | tee logs/confusion_matrix.py.`now_str`.log


```

# angular diff threshold

```
python max_angular_diff_threshold.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gestures 74 75 76 77 78 79 80 81 82 83 --positions 1 --rows 4 | tee logs/max_angular_diff_threshold.py.`now_str`.log

```
