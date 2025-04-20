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

python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id 62 --threshold-recognition 0.2 --position 0 1 2 3 4 5 --start --end --align middle:1 | tee logs-2025-03-28_17-11-15/gesture_data_extractor.2.py.`now_str`.log

2025-03-28_18:58:49 for id in 61 62 63 ; do python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id $id --min-points 1 -v --threshold-recognition 0.9 --position 1 --start --end --align middle:1 --suffix p_1_wswe_te_calc_tr_0.9_middle_1  | tee logs-2025-03-28_17-11-15/gesture_data_extractor.2.py.`now_str`.log ; done



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

## conf matrix switch hands
```

python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gestures 269 270 271  --gestures 272 273 274 275 276 277 278  --actual-matches 1 1 1 1 1 1 1 --positions 0 1 2 3 4 5 --angular-diff --calc-threshold --calc-max --save-ref-gesture x-r-switch--max--269--271 | tee logs/confusion_matrix.py.x-r-switch.`now_str`.log

```

# angular diff threshold

```
python max_angular_diff_threshold.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gestures 74 75 76 77 78 79 80 81 82 83 --positions 1 --rows 4 | tee logs/max_angular_diff_threshold.py.`now_str`.log

```


# Generate latex table

```
python generate_latex_dataline_table.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gestures 41 --positions 0 1 2 3 4 5 --bold 9359 9383 9419 9437 --split 3 
```

```
python generate_latex_dataline_table.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gestures 41 --positions 0 1 2 3 4 5 --bold 9359 9383 9419 9437 --split 3 --cursive_position 1 --info "9359:83-1.1" "9365:83-1.2" "9371:83-2.1" "9377:83-3.1" "9389:83-1.3" "9419:83-3" "9425:83-1.4"

```

# WEB automation

```

http://localhost:8080/gagweb/#!/recognize?refGestureIds=75,76,77&inputGestureIds=41,42,43,44,45,46,47,48,49,50

http://localhost:8080/gagweb/#!/recognize?refGestureIds=85,86,87&inputGestureIds=41,42,43,44,45,46,47,48,49,50

http://localhost:8080/gagweb/#!/recognize?refGestureIds=85,86,87&inputGestureIds=41,42,43

http://localhost:8080/gagweb/#!/recognize?refGestureIds=301,338,339&inputGestureIds=61,62,63,64,65,66,67,68,69,70,71


http://localhost:8080/gagweb/#!/recognize?refGestureIds=266,267,268,269,270,271,272,273,274,275&inputGestureIds=61,62,63,64,65,66,67,68,69,70,71

http://localhost:8080/gagweb/#!/recognize?refGestureIds=268&inputGestureIds=61

```
@misc{kim2019imu,
  author = {Minwoo Kim, Jaechan Cho, Seongjoo Lee, Yunho Jung},
  title = {IMU Sensor-Based Hand Gesture Recognition for Human-Machine Interfaces},
  url = {https://doi.org/10.3390/s19183827}
}

@misc{saggio2020sign,
  author = {Giovanni Saggio, Pietro Cavallo, Mariachiara Ricci, Vito Errico, Jonathan Zea, Marco E. Benalcázar},
  title = {Sign Language Recognition Using Wearable Electronics: Implementing k-Nearest Neighbors with Dynamic Time Warping and Convolutional Neural Network Algorithms},
  url = {https://doi.org/10.3390/s20143879}
}

@misc{maharani2018hand,
  author = {Devira Anggi Maharani, Hanif Fakhrurroja, Riyanto, C. Machbub},
  title = {Hand gesture recognition using K-means clustering and Support Vector Machine},
  url = {https://doi.org/10.1109/ISCAIE.2018.8405435}
}

@misc{ameliasari2021evaluation,
  author = {Maya Ameliasari, Aji Gautama Putrada, Rizka Reza Pahlevi},
  title = {An Evaluation of SVM in Hand Gesture Detection Using IMU-Based Smartwatches for Smart Lighting Control},
  url = {https://doi.org/10.20895/infotel.v13i2.656}
}

@misc{wang2023hmm,
  author = {Danping Wang, Jina Wang, Yang Liu, Xianming Meng},
  title = {HMM-based IMU data processing for arm gesture classification and motion tracking},
  url = {https://doi.org/10.1504/IJMIC.2023.128767}
}

@misc{jiang2022multi,
  author = {Yujian Jiang, Lin Song, Junming Zhang, Yang Song, Ming Yan},
  title = {Multi-Category Gesture Recognition Modeling Based on sEMG and IMU Signals},
  url = {https://doi.org/10.3390/s22155855}
}

X-R-INDE-CLICK__41-50

X-R-SWITCH__61-71


T-R-OK-3S__357-367 


T-R-WARE-UP-M-L-M-R-M_5S__368-377


T-R-BYE-UP-DOWN-UP_3S__378-387



for range in "1..5" "10..15"; do
    for num in $(eval echo {$range}); do
        echo "$num"
    done
done


REF_GESTS="85,,86,87"
echo -e "http://localhost:8080/gagweb/#!/recognize?refGestureIds=$REF_GESTS&inputGestureIds"
for range in "41..50" "61..71" "357..367" "368..377" "378..387" ; do
  #echo -e "http://localhost:8080/gagweb/#!/recognize?refGestureIds=266,267,268,269,270,271,272,273,274,275&inputGestureIds=61,62,63,64,65,66,67,68,69,70,71"
  for num in $(eval echo {$range}); do
    echo -n $num
  done
done


REF_GESTS="85,,86,87"
echo -e "http://localhost:8080/gagweb/#!/recognize?refGestureIds=$REF_GESTS&inputGestureIds"
for range in "41..50" "61..71" "357..367" "368..377" "378..387" ; do
  for num in $(eval echo {$range}); do
    echo -n $num
  done
done



REF_GESTS="85,86,87" ; echo -e "http://localhost:8080/gagweb/#!/recognize?refGestureIds=$REF_GESTS&inputGestureIds" for range in "41..50" "61..71" "357..367" "368..377" "378..387" ; do for num in $(eval echo {$range}); do echo -n $num ; done ; done


REF_GESTS="85,86,87" ; echo -e 'http://localhost:8080/gagweb/#!/recognize?refGestureIds='$REF_GESTS'&inputGestureIds' ; for range in "41..50" "61..71" "357..367" "368..377" "378..387" ; do for num in $(eval echo {$range}); do echo -e $num ; done ; done
REF_GESTS="85,86,87" ; echo -n 'http://localhost:8080/gagweb/#!/recognize?refGestureIds='$REF_GESTS'&inputGestureIds' ; for range in "41..50" "61..71" "357..367" "368..377" "378..387" ; do for num in $(eval echo {$range}); do echo -n "${num}," ; done ; done


http://localhost:8080/gagweb/#!/recognize?refGestureIds=85,86,87&inputGestureIds41,42,43,44,45,46,47,48,49,50,61,62,63,64,65,66,67,68,69,70,71,357,358,359,360,361,362,363,364,365,366,367,368,369,370,371,372,373,374,375,376,377,378,379,380,381,382,383,384,385,386,387

http://localhost:8080/gagweb/#!/recognize?refGestureIds=85,86,87&inputGestureIds41,42,43,44,45,46,47,48,49,50,61,62,63,64,65,66,67,68,69,70,71,357,358,359,360,361,362,363,364,365,366,367,368,369,370,371,372,373,374,375,376,377,378,379,380,381,382,383,384,385,386,387


http://localhost:8080/gagweb/#!/recognize?refGestureIds=85,86&inputGestureIds=41,42,43,44,45,46,47,48,49,50,61,62,63,64,65,66,67,68,69,70,71,357,358,359,360,361,362,363,364,365,366,367,368,369,370,371,372,373,374,375,376,377,378,379,380,381,382,383,384,385,386,387

http://localhost:8080/gagweb/#!/recognize?refGestureIds=85,86&inputGestureIds=41,61,357,358,368,376

http://localhost:8080/gagweb/#!/recognize?refGestureIds=85,86,87&inputGestureIds=41,42,43,44,45,46,47,48,49,50,61,62,63,64,65,66,67,68,69,70,71,357,358,359,360,361,362,363,364,365,366,367,368,369,370,371,372,373,374,375,376,377,378,379,380,381,382,383,384,385,386,387


http://localhost:8080/gagweb/#!/recognize?refGestureIds=85,86,87&inputGestureIds=41,42,43,44,45,46,47,48,49,50,61,62,63,64,65,67,68,69,70,71,357,358,359,360,361,362,363,364,365,366,367,368,369,370,371,372,373,374,375,376,377,378,379,380,381,382,383,384,385,386,387


http://localhost:8080/gagweb/#!/recognize?refGestureIds=85,86,87&inputGestureIds=41,42,43,44,45,46,47,48,49,50,61,62,63,64,65,67,68,69,70,71,358,359,360,361,362,363,364,365,366,367,368,369,370,371,372,373,374,375,376,377,378,379,380,381,382,383,384,385,386,387


http://localhost:8080/gagweb/#!/recognize?refGestureIds=85,86,87&inputGestureIds=41,42,43,44,45,46,47,48,49,50,61,62,63,64,65,67,68,69,70,71,358,359,360,361,362,363,364,365,366,367,368,369,370,371,372,373,374,375,376,377,378,379,380,381,382,383,384,385,386,387,390,391,392,393,394,395,396,397,398,399,400,401,402,403,404,405,406,407,408,409,410,411






T-R-INDEX-U-D-3s

T-R-SWITCH-3s

T-R-SWITCH-BACK-3s

T-R-OK-3s






T-R-INDEX-U-D-3s

T-R-SWITCH-3s

T-R-SWITCH-BACK-3s

T-R-OK-3s


T-R-MAYEBE-M-L-M-R-M-3s

T-R-SWITCH-FIST-RLS-SWITCH-3s

t-r-bye-up-down-up-3s

t-r-wave-up-m-l-m-r-m-3s



python gesture_data_extractor.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id 50 --threshold-extraction 0.01 --threshold-recognition 0.8 --position 1 --start --end --suffix p_1_wswe_te_0.01 | tee logs/gesture_data_extractor.py.`now_str`.log





mysql -u gagweb --password=password gagweb -se "SELECT id FROM Gesture WHERE userAlias REGEXP '^T-R-INDEX-U-D-3s_[1-9]$|^T-R-INDEX-U-D-3s_10$';"




T-R-INDEX-U-D-3s

T-R-SWITCH-3s

T-R-SWITCH-BACK-3s

T-R-OK-3s

T-R-MAYEBE-M-L-M-R-M-3s

T-R-SWITCH-FIST-RLS-SWITCH-3s

t-r-bye-up-down-up-3s

t-r-wave-up-m-l-m-r-m-3s


python gesture_data_extractor.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id 50 --threshold-extraction 0.01 --threshold-recognition 0.8 --position 1 --start --end | tee logs/gesture_data_extractor.py.`now_str`.log



T-R-OK-3s

453,452,541,540,449,448,447,446,445,444




# T-R-INDEX-U-D-3s
mysql -u gagweb --password=password gagweb -se "SELECT id FROM Gesture WHERE userAlias REGEXP '^T-R-INDEX-U-D-3s_[1-9]$|^T-R-INDEX-U-D-3s_10$';"

# T-R-SWITCH-3s
mysql -u gagweb --password=password gagweb -se "SELECT id FROM Gesture WHERE userAlias REGEXP '^T-R-SWITCH-3s_[1-9]$|^T-R-SWITCH-3s_10$';"

# T-R-SWITCH-BACK-3s
mysql -u gagweb --password=password gagweb -se "SELECT id FROM Gesture WHERE userAlias REGEXP '^T-R-SWITCH-BACK-3s_[1-9]$|^T-R-SWITCH-BACK-3s_10$';"

# T-R-OK-3s
mysql -u gagweb --password=password gagweb -se "SELECT id FROM Gesture WHERE userAlias REGEXP '^T-R-OK-3s_[1-9]$|^T-R-OK-3s_10$';"

# T-R-MAYEBE-M-L-M-R-M-3s
mysql -u gagweb --password=password gagweb -se "SELECT id FROM Gesture WHERE userAlias REGEXP '^T-R-MAYEBE-M-L-M-R-M-3s_[1-9]$|^T-R-MAYEBE-M-L-M-R-M-3s_10$';"

# T-R-SWITCH-FIST-RLS-SWITCH-3s
mysql -u gagweb --password=password gagweb -se "SELECT id FROM Gesture WHERE userAlias REGEXP '^T-R-SWITCH-FIST-RLS-SWITCH-3s_[1-9]$|^T-R-SWITCH-FIST-RLS-SWITCH-3s_10$';"

# t-r-bye-up-down-up-3s
mysql -u gagweb --password=password gagweb -se "SELECT id FROM Gesture WHERE userAlias REGEXP '^t-r-bye-up-down-up-3s_[1-9]$|^t-r-bye-up-down-up-3s_10$';"
# t-r-wave-up-m-l-m-r-m-3s
mysql -u gagweb --password=password gagweb -se "SELECT id FROM Gesture WHERE userAlias REGEXP '^t-r-wave-up-m-l-m-r-m-3s_[1-9]$|^t-r-wave-up-m-l-m-r-m-3s_10$';"




echo T-R-INDEX-U-D-3s
mysql -u gagweb --password=password gagweb -se "SELECT id FROM Gesture WHERE userAlias REGEXP '^T-R-INDEX-U-D-3s_[1-9]$|^T-R-INDEX-U-D-3s_10$';"
echo T-R-SWITCH-3s
mysql -u gagweb --password=password gagweb -se "SELECT id FROM Gesture WHERE userAlias REGEXP '^T-R-SWITCH-3s_[1-9]$|^T-R-SWITCH-3s_10$';"
echo T-R-SWITCH-BACK-3s
mysql -u gagweb --password=password gagweb -se "SELECT id FROM Gesture WHERE userAlias REGEXP '^T-R-SWITCH-BACK-3s_[1-9]$|^T-R-SWITCH-BACK-3s_10$';"
echo T-R-OK-3s
mysql -u gagweb --password=password gagweb -se "SELECT id FROM Gesture WHERE userAlias REGEXP '^T-R-OK-3s_[1-9]$|^T-R-OK-3s_10$';"
echo T-R-MAYEBE-M-L-M-R-M-3s
mysql -u gagweb --password=password gagweb -se "SELECT id FROM Gesture WHERE userAlias REGEXP '^T-R-MAYEBE-M-L-M-R-M-3s_[1-9]$|^T-R-MAYEBE-M-L-M-R-M-3s_10$';"
echo T-R-SWITCH-FIST-RLS-SWITCH-3s
mysql -u gagweb --password=password gagweb -se "SELECT id FROM Gesture WHERE userAlias REGEXP '^T-R-SWITCH-FIST-RLS-SWITCH-3s_[1-9]$|^T-R-SWITCH-FIST-RLS-SWITCH-3s_10$';"
echo t-r-bye-up-down-up-3s
mysql -u gagweb --password=password gagweb -se "SELECT id FROM Gesture WHERE userAlias REGEXP '^t-r-bye-up-down-up-3s_[1-9]$|^t-r-bye-up-down-up-3s_10$';"
echo t-r-wave-up-m-l-m-r-m-3s
mysql -u gagweb --password=password gagweb -se "SELECT id FROM Gesture WHERE userAlias REGEXP '^t-r-wave-up-m-l-m-r-m-3s_[1-9]$|^t-r-wave-up-m-l-m-r-m-3s_10$';"






for id in 61 62 63 ; do python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id $id --min-points 1 -v --threshold-recognition 0.9 --position 1 --start --end --align middle:1 --suffix p_1_wswe_te_calc_tr_0.9_middle_1  | tee logs-2025-03-28_17-11-15/gesture_data_extractor.2.py.`now_str`.log ; done



python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id $id --min-points 1 -v --threshold-recognition 0.9 --position 1 --start --end --align middle:1 --suffix p_1_wswe_te_calc_tr_0.9_middle_1  | tee logs-2025-03-28_17-11-15/gesture_data_extractor.2.py.`now_str`.log



python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id  --min-points 1 -v --threshold-recognition 0.9 --position 1 --start --end --align middle:1 | tee logs/gesture_data_extractor.2.py.`now_str`.log


