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


python generate_latex_dataline_table.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gestures 41 --positions 1 --bold 9359 9383 9419 9437 --split 3 --cursive_position 1 --info "9359:83-1.1" "9365:83-1.2" "9371:83-2.1" "9377:83-3.1" "9389:83-1.3" "9419:83-3" "9425:83-1.4"


python generate_latex_dataline_table.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gestures 41 --positions 1 --bold 9359 9383 9419 9437 --split 1 --cursive_position 1 --info "9359:83-1.1" "9365:83-1.2" "9371:83-2.1" "9377:83-3.1" "9389:83-1.3" "9419:83-3" "9425:83-1.4"


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


904K904K904K904K

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






alias_list=(
  "T-R-INDEX-U-D-3s"
  "T-R-SWITCH-3s"
  "T-R-SWITCH-BACK-3s"
  "T-R-OK-3s"
  "T-R-MAYEBE-M-L-M-R-M-3s"
  "T-R-SWITCH-FIST-RLS-SWITCH-3s"
  "t-r-bye-up-down-up-3s"
  "t-r-wave-up-m-l-m-r-m-3s"
)

for alias in "${alias_list[@]}"; do
  echo "Gesture userAlias: $alias"
  ids=$(mysql -u gagweb --password=password gagweb -se "
    SELECT GROUP_CONCAT(id SEPARATOR ',') FROM Gesture 
    WHERE userAlias REGEXP '^${alias}_[1-9]$|^${alias}_10$';
  ")
  echo "$ids"
  echo "-------------------------"
done


442,443,444,445,446,447,448,449,450,451,452,453



Gesture userAlias: T-R-INDEX-U-D-3s
412,413,414,415,416,417,418,419,420,421
-------------------------
Gesture userAlias: T-R-SWITCH-3s
422,423,424,425,426,427,428,429,430,431
-------------------------
Gesture userAlias: T-R-SWITCH-BACK-3s
432,433,434,435,436,437,438,439,440,441
-------------------------

Gesture userAlias: T-R-OK-3s
444,445,446,447,448,449,450,451,452,453
-------------------------
Gesture userAlias: T-R-MAYEBE-M-L-M-R-M-3s
402,403,404,405,406,407,408,409,410,411
-------------------------
Gesture userAlias: T-R-SWITCH-FIST-RLS-SWITCH-3s
388,389,390,391,392,393,394,395,396,397,398,399,400,401
-------------------------
Gesture userAlias: t-r-bye-up-down-up-3s
378,379,380,381,382,383,384,385,386,387
-------------------------
Gesture userAlias: t-r-wave-up-m-l-m-r-m-3s
368,369,370,371,372,373,374,375,376,377
-------------------------




Gesture userAlias: T-R-INDEX-U-D-3s
412,413,414,415,416,417,418,419,420,421
-------------------------
Gesture userAlias: T-R-SWITCH-3s
422,423,424,425,426,427,428,429,430,431
-------------------------
Gesture userAlias: T-R-SWITCH-BACK-3s
432,433,434,435,436,437,438,439,440,441
-------------------------

Gesture userAlias: T-R-OK-3s
444,445,446,447,448,449,450,451,452,453
-------------------------
Gesture userAlias: T-R-MAYEBE-M-L-M-R-M-3s
402,403,404,405,406,407,408,409,410,411
-------------------------
Gesture userAlias: t-r-bye-up-down-up-3s
378,379,380,381,382,383,384,385,386,387
-------------------------
Gesture userAlias: t-r-wave-up-m-l-m-r-m-3s
368,369,370,371,372,373,374,375,376,377
-------------------------



for id in 61 62 63 ; do python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id $id --min-points 1 -v --threshold-recognition 0.9 --position 1 --start --end --align middle:1 --suffix p_1_wswe_te_calc_tr_0.9_middle_1  | tee logs-2025-03-28_17-11-15/gesture_data_extractor.2.py.`now_str`.log ; done



python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id $id --min-points 1 -v --threshold-recognition 0.9 --position 1 --start --end --align middle:1 --suffix p_1_wswe_te_calc_tr_0.9_middle_1  | tee logs-2025-03-28_17-11-15/gesture_data_extractor.2.py.`now_str`.log



python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id  --min-points 1 -v --threshold-recognition 0.9 --position 1 --start --end --align middle:1 | tee logs/gesture_data_extractor.2.py.`now_str`.log



python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id  --min-points 1 -v --threshold-recognition 0.9 --position 1 --start --end --align middle:1 | tee logs/gesture_data_extractor.2.py.`now_str`.log





for alias in "${alias_list[@]}"; do
  echo "Gesture userAlias: $alias"
  ids=$(mysql -u gagweb --password=password gagweb -se "
    SELECT GROUP_CONCAT(id SEPARATOR ',') FROM Gesture 
    WHERE userAlias REGEXP '^${alias}_[1-9]$|^${alias}_10$';
  ")
  echo "$ids"

  first_id=$(echo "$ids" | cut -d',' -f1)



  if [ -z "$first_id" ]; then
    echo "No IDs found for alias: $alias. Skipping script execution."
  else
    echo "Executing gesture_data_extractor.2.py with gesture_id: $first_id"
    now_str=$(date '+%Y%m%d_%H%M%S')

    python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "$first_id" --min-points 1 -v --threshold-recognition 0.9 --position 1 --start --end --align xnth:1 | tee "logs/gesture_data_extractor.2.py.$now_str.log"
  fi

  echo "-------------------------"
  
done



python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "$first_id" --min-points 1 -v --threshold-recognition 0.9 --position 1 --start --end --align xnth:1 | tee "logs/gesture_data_extractor.2.py.$now_str.log"


python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "412" --min-points 1 -v --threshold-recognition 0.9 --position 1 --start --end --align xnth:1 | tee "logs/gesture_data_extractor.2.py.$now_str.log"





python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "444" --threshold-recognition 0.9 --position 0 1 2 3 4 5 --start --end --align xnth:1 --align-find top -v | tee logs/gesture_data_extractor.2.py.`now_str`.log


python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "402" --threshold-recognition 0.9 --position 0 1 2 3 4 5 --start --end --align xnth:1 --align-find top -v | tee logs/gesture_data_extractor.2.py.`now_str`.log


python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "388" --threshold-recognition 0.9 --position 0 1 2 3 4 5 --start --end --align xnth:1 --align-find top -v | tee logs/gesture_data_extractor.2.py.`now_str`.log

python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "378" --threshold-recognition 0.9 --position 0 1 2 3 4 5 --start --end --align xnth:1 --align-find top -v | tee logs/gesture_data_extractor.2.py.`now_str`.log

python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "368" --threshold-recognition 0.9 --position 0 1 2 3 4 5 --start --end --align xnth:1 --align-find top -v | tee logs/gesture_data_extractor.2.py.`now_str`.log





python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "444" --threshold-extraction 0.05 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v | tee logs/gesture_data_extractor.2.py.`now_str`.log

python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "402" --threshold-extraction 0.05 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v | tee logs/gesture_data_extractor.2.py.`now_str`.log

python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "392" --threshold-extraction 0.05 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v | tee logs/gesture_data_extractor.2.py.`now_str`.log

python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "378" --threshold-extraction 0.05 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v | tee logs/gesture_data_extractor.2.py.`now_str`.log

python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "368" --threshold-extraction 0.05 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v | tee logs/gesture_data_extractor.2.py.`now_str`.log




python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "444" --threshold-extraction 0.05 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_wswe_xnth3_te_calc_tr_0.9 | tee logs/gesture_data_extractor.2.py.`now_str`.log

python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "402" --threshold-extraction 0.05 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_wswe_xnth3_te_calc_tr_0.9 | tee logs/gesture_data_extractor.2.py.`now_str`.log

python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "392" --threshold-extraction 0.05 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_wswe_xnth3_te_calc_tr_0.9 | tee logs/gesture_data_extractor.2.py.`now_str`.log

python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "378" --threshold-extraction 0.05 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_wswe_xnth3_te_calc_tr_0.9 | tee logs/gesture_data_extractor.2.py.`now_str`.log

python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "368" --threshold-extraction 0.05 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_wswe_xnth3_te_calc_tr_0.9 | tee logs/gesture_data_extractor.2.py.`now_str`.log


python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "444" --threshold-recognition 0.3 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_wswe_xnth3_te_calc_tr_0.3 | tee logs/gesture_data_extractor.2.py.`now_str`.log

python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "402" --threshold-recognition 0.3 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_wswe_xnth3_te_calc_tr_0.3 | tee logs/gesture_data_extractor.2.py.`now_str`.log

python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "392" --threshold-recognition 0.3 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_wswe_xnth3_te_calc_tr_0.3 | tee logs/gesture_data_extractor.2.py.`now_str`.log

python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "378" --threshold-recognition 0.3 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_wswe_xnth3_te_calc_tr_0.3 | tee logs/gesture_data_extractor.2.py.`now_str`.log

python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "368" --threshold-recognition 0.3 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_wswe_xnth3_te_calc_tr_0.3 | tee logs/gesture_data_extractor.2.py.`now_str`.log





Gesture userAlias: T-R-OK-3s
444,445,446,447,448,449,450,451,452,453
-------------------------
Gesture userAlias: T-R-MAYEBE-M-L-M-R-M-3s
402,403,404,405,406,407,408,409,410,411
-------------------------
Gesture userAlias: t-r-bye-up-down-up-3s
378,379,380,381,382,383,384,385,386,387
-------------------------
Gesture userAlias: t-r-wave-up-m-l-m-r-m-3s
368,369,370,371,372,373,374,375,376,377
-------------------------




python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "444" --threshold-recognition 0.3 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_wswe_xnth3_te_calc_tr_0.3 | tee logs/gesture_data_extractor.2.py.`now_str`.log

python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "402" --threshold-recognition 0.3 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_wswe_xnth3_te_calc_tr_0.3 | tee logs/gesture_data_extractor.2.py.`now_str`.log

python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "392" --threshold-recognition 0.3 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_wswe_xnth3_te_calc_tr_0.3 | tee logs/gesture_data_extractor.2.py.`now_str`.log

python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "378" --threshold-recognition 0.3 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_wswe_xnth3_te_calc_tr_0.3 | tee logs/gesture_data_extractor.2.py.`now_str`.log

python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "368" --threshold-recognition 0.3 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_wswe_xnth3_te_calc_tr_0.3 | tee logs/gesture_data_extractor.2.py.`now_str`.log





http://localhost:8080/gagweb/#!/recognize?refGestureIds=85,86,87&inputGestureIds=41,42,43,44,45,46,47,48,49,50,61,62,63,64,65,67,68,69,70,71,358,359,360,361,362,363,364,365,366,367,368,369,370,371,372,373,374,375,376,377,378,379,380,381,382,383,384,385,386,387,390,391,392,393,394,395,396,397,398,399,400,401,402,403,404,405,406,407,408,409,410,411


http://localhost:8080/gagweb/#!/recognize?refGestureIds=455,456,457&inputGestureIds=


444,445,446,447,448,449,450,451,452,453
402,403,404,405,406,407,408,409,410,411
392,393,394,395,396,397,398,399,400,401
378,379,380,381,382,383,384,385,386,387
368,369,370,371,372,373,374,375,376,377


http://localhost:8080/gagweb/#!/recognize?refGestureIds=85,86,87&inputGestureIds=444,445,446,447,448,449,450,451,452,453,402,403,404,405,406,407,408,409,410,411,392,393,394,395,396,397,398,399,400,401,378,379,380,381,382,383,384,385,386,387,368,369,370,371,372,373,374,375,376,377



http://localhost:8080/gagweb/#!/recognize?refGestureIds=455,456,457&inputGestureIds=444,453,402,411,392,401,378,387,368,377



444,453,402,411,392,401,378,387,368,377



http://localhost:8080/gagweb/#!/recognize?refGestureIds=455,456,457&inputGestureIds=444,453,402,411,392,401,378,387,368,377


http://localhost:8080/gagweb/#!/recognize?refGestureIds=455,456,457&inputGestureIds=444,453,402,411,392,401,378,387,368,377







Gesture userAlias: T-R-OK-3s
444,445,446,447,448,449,450,451,452,453
-------------------------
Gesture userAlias: T-R-MAYEBE-M-L-M-R-M-3s
402,403,404,405,406,407,408,409,410,411
-------------------------
Gesture userAlias: t-r-bye-up-down-up-3s
378,379,380,381,382,383,384,385,386,387
-------------------------
Gesture userAlias: t-r-wave-up-m-l-m-r-m-3s
368,369,370,371,372,373,374,375,376,377
-------------------------




python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "444" --threshold-recognition 0.3 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_wswe_xnth3_te_calc_tr_0.3 | tee logs/gesture_data_extractor.2.py.`now_str`.log

python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "402" --threshold-recognition 0.3 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_wswe_xnth3_te_calc_tr_0.3 | tee logs/gesture_data_extractor.2.py.`now_str`.log

python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "378" --threshold-recognition 0.3 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_wswe_xnth3_te_calc_tr_0.3 | tee logs/gesture_data_extractor.2.py.`now_str`.log

python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "368" --threshold-recognition 0.3 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_wswe_xnth3_te_calc_tr_0.3 | tee logs/gesture_data_extractor.2.py.`now_str`.log






localhost:8080/gagweb/#!/recognize?refGestureIds=455,456,457,458&inputGestureIds=444,453,402,411,378,387,368,377


localhost:8080/gagweb/#!/recognize?refGestureIds=455,456,457,458&inputGestureIds=444,453,402,411,378,387,368,377




python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "444" --threshold-recognition 0.5 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_wswe_xnth3_te_calc_tr_0.5 | tee logs/gesture_data_extractor.2.py.`now_str`.log

python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "402" --threshold-recognition 0.5 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_wswe_xnth3_te_calc_tr_0.5 | tee logs/gesture_data_extractor.2.py.`now_str`.log

python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "378" --threshold-recognition 0.5 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_wswe_xnth3_te_calc_tr_0.5 | tee logs/gesture_data_extractor.2.py.`now_str`.log

python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "368" --threshold-recognition 0.5 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_wswe_xnth3_te_calc_tr_0.5 | tee logs/gesture_data_extractor.2.py.`now_str`.log





localhost:8080/gagweb/#!/recognize?refGestureIds=455,456,458,459&inputGestureIds=444,453,402,411,378,387,368,377


localhost:8080/gagweb/#!/recognize?refGestureIds=455,456,458,459&inputGestureIds=444,453,402,411,378,387,368,377

localhost:8080/gagweb/#!/recognize?refGestureIds=455,456,458,459,460,461,462,463&inputGestureIds=444,453,402,411,378,387,368,377


Gesture userAlias: T-R-OK-3s
444,445,446,447,448,449,450,451,452,453
-------------------------
Gesture userAlias: T-R-MAYEBE-M-L-M-R-M-3s
402,403,404,405,406,407,408,409,410,411
-------------------------
Gesture userAlias: t-r-bye-up-down-up-3s
378,379,380,381,382,383,384,385,386,387
-------------------------
Gesture userAlias: t-r-wave-up-m-l-m-r-m-3s
368,369,370,371,372,373,374,375,376,377
-------------------------

localhost:8080/gagweb/#!/recognize?refGestureIds=455,456,458,459,460,461,462,463&inputGestureIds=444,445,446,447,448,449,450,451,452,453,402,403,404,405,406,407,408,409,410,411,378,379,380,381,382,383,384,385,386,387,368,369,370,371,372,373,374,375,376,377







| 454 | 2025-04-20 21:49:23.000000 |     1 | NULL |        1 |          1 |         0.3 | T-R-OK-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.3                     |       1 |
| 455 | 2025-04-20 21:51:37.000000 |   0.1 | NULL |        0 |          1 |         0.3 | T-R-OK-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.3                     |       1 |
| 456 | 2025-04-20 21:51:56.000000 |   0.1 | NULL |        0 |          1 |         0.3 | T-R-MAYEBE-M-L-M-R-M-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.3       |       1 |
| 457 | 2025-04-20 21:52:04.000000 |   0.1 | NULL |        0 |          1 |         0.3 | T-R-SWITCH-FIST-RLS-SWITCH-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.3 |       1 |
| 458 | 2025-04-20 21:52:06.000000 |   0.1 | NULL |        0 |          1 |         0.3 | t-r-bye-up-down-up-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.3         |       1 |
| 459 | 2025-04-20 21:52:09.000000 |   0.1 | NULL |        0 |          1 |         0.3 | t-r-wave-up-m-l-m-r-m-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.3      |       1 |
| 460 | 2025-04-20 22:13:50.000000 |   0.1 | NULL |        0 |          1 |         0.5 | T-R-OK-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.5                     |       1 |
| 461 | 2025-04-20 22:13:52.000000 |   0.1 | NULL |        0 |          1 |         0.5 | T-R-MAYEBE-M-L-M-R-M-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.5       |       1 |
| 462 | 2025-04-20 22:13:54.000000 |   0.1 | NULL |        1 |          1 |         0.5 | t-r-bye-up-down-up-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.5         |       1 |
| 463 | 2025-04-20 22:13:57.000000 |   0.1 | NULL |        0 |          1 |         0.5 | t-r-wave-up-m-l-m-r-m-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.5      |       1 |





| 455 | 2025-04-20 21:51:37.000000 |   0.1 | NULL |        0 |          1 |         0.3 | T-R-OK-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.3                     |       1 |
| 456 | 2025-04-20 21:51:56.000000 |   0.1 | NULL |        0 |          1 |         0.3 | T-R-MAYEBE-M-L-M-R-M-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.3       |       1 |
| 458 | 2025-04-20 21:52:06.000000 |   0.1 | NULL |        0 |          1 |         0.3 | t-r-bye-up-down-up-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.3         |       1 |
| 459 | 2025-04-20 21:52:09.000000 |   0.1 | NULL |        0 |          1 |         0.3 | t-r-wave-up-m-l-m-r-m-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.3      |       1 |
| 460 | 2025-04-20 22:13:50.000000 |   0.1 | NULL |        0 |          1 |         0.5 | T-R-OK-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.5                     |       1 |
| 461 | 2025-04-20 22:13:52.000000 |   0.1 | NULL |        0 |          1 |         0.5 | T-R-MAYEBE-M-L-M-R-M-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.5       |       1 |
| 462 | 2025-04-20 22:13:54.000000 |   0.1 | NULL |        1 |          1 |         0.5 | t-r-bye-up-down-up-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.5         |       1 |
| 463 | 2025-04-20 22:13:57.000000 |   0.1 | NULL |        0 |          1 |         0.5 | t-r-wave-up-m-l-m-r-m-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.5      |       1 |


'T-R-OK-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.3' ('id' 455)
'T-R-MAYEBE-M-L-M-R-M-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.3' ('id' 456)
't-r-bye-up-down-up-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.3' ('id' 458)
't-r-wave-up-m-l-m-r-m-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.3' ('id' 459)
'T-R-OK-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.5' ('id' 460)
'T-R-MAYEBE-M-L-M-R-M-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.5' ('id' 461)
't-r-bye-up-down-up-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.5' ('id' 462)
't-r-wave-up-m-l-m-r-m-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.5' ('id' 463)




T-R-OK-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.3               
T-R-MAYEBE-M-L-M-R-M-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.3 
t-r-bye-up-down-up-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.3   
t-r-wave-up-m-l-m-r-m-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.3
T-R-OK-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.5               
T-R-MAYEBE-M-L-M-R-M-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.5 
t-r-bye-up-down-up-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.5   
t-r-wave-up-m-l-m-r-m-3s_1-p_012345_wswe_xnth3_te_calc_tr_0.5




# Z



python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "444" --threshold-recognition 0.3 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_wswe_xnth3_te_calc_tr_0.3 | tee logs/gesture_data_extractor.2.py.`now_str`.log

python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "444" --threshold-recognition 0.3 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_wswe_xnth3_te_calc_tr_0.3 | tee logs/gesture_data_extractor.2.py.`now_str`.log

|  465 | 2025-04-25 18:01:28.641000 |     0 | NULL |     NULL |          0 |           0 | T-R-INDEX-U-D_1                                                    |       1 |          164 |

|  515 | 2025-04-25 19:16:19.931000 |     0 | NULL |     NULL |          0 |           0 | T-R-EACH-FINGER-U-D_1                                              |       1 |          168 |

|  566 | 2025-04-25 22:22:55.892000 |     0 | NULL |     NULL |          0 |           0 | Z-R-WAVE-L45-M-R45-M_1                                             |       1 |          148 |

|  631 | 2025-04-25 23:16:23.180000 |     0 | NULL |     NULL |          0 |           0 | Z-R-SWITCH-Y180_1                                                  |       1 |          178 |

|  689 | 2025-04-26 12:58:09.537000 |     0 | NULL |     NULL |          0 |           0 | Z-R-OK_1                                                           |       1 |          165 |

|  739 | 2025-04-26 13:07:43.125000 |     0 | NULL |     NULL |          0 |           0 | Z-R-SWITCH-AND-BACK_1                                              |       1 |          166 |

|  806 | 2025-04-26 14:59:04.770000 |     0 | NULL |     NULL |          0 |           0 | Z-R-FIST_1                                                         |       1 |          189 |

|  866 | 2025-04-26 15:35:01.047000 |     0 | NULL |     NULL |          0 |           0 | Z-R-SWITCH-FIST_1                                                  |       1 |          191 |

|  916 | 2025-04-26 16:40:17.186000 |     0 | NULL |     NULL |          0 |           0 | Z-R-MAYBE-L-M-R-M_1                                                |       1 |          189 |

|  968 | 2025-04-26 16:48:06.512000 |     0 | NULL |     NULL |          0 |           0 | Z-R-VICTORIA_1                                                     |       1 |          180 




|  465 | 2025-04-25 18:01:28.641000 |     0 | NULL |     NULL |          0 |           0 | T-R-INDEX-U-D_1                                                    |       1 |          164 |
|  515 | 2025-04-25 19:16:19.931000 |     0 | NULL |     NULL |          0 |           0 | T-R-EACH-FINGER-U-D_1                                              |       1 |          168 |
|  566 | 2025-04-25 22:22:55.892000 |     0 | NULL |     NULL |          0 |           0 | Z-R-WAVE-L45-M-R45-M_1                                             |       1 |          148 |
|  631 | 2025-04-25 23:16:23.180000 |     0 | NULL |     NULL |          0 |           0 | Z-R-SWITCH-Y180_1                                                  |       1 |          178 |
|  689 | 2025-04-26 12:58:09.537000 |     0 | NULL |     NULL |          0 |           0 | Z-R-OK_1                                                           |       1 |          165 |
|  739 | 2025-04-26 13:07:43.125000 |     0 | NULL |     NULL |          0 |           0 | Z-R-SWITCH-AND-BACK_1                                              |       1 |          166 |
|  806 | 2025-04-26 14:59:04.770000 |     0 | NULL |     NULL |          0 |           0 | Z-R-FIST_1                                                         |       1 |          189 |
|  866 | 2025-04-26 15:35:01.047000 |     0 | NULL |     NULL |          0 |           0 | Z-R-SWITCH-FIST_1                                                  |       1 |          191 |
|  916 | 2025-04-26 16:40:17.186000 |     0 | NULL |     NULL |          0 |           0 | Z-R-MAYBE-L-M-R-M_1                                                |       1 |          189 |
|  968 | 2025-04-26 16:48:06.512000 |     0 | NULL |     NULL |          0 |           0 | Z-R-VICTORIA_1                                                     |       1 |          180 |


|  465 | 2025-04-25 18:01:28.641000 |     0 | NULL |     NULL |          0 |           0 | T-R-INDEX-U-D_1                                                    |       1 |          164 |
|  515 | 2025-04-25 19:16:19.931000 |     0 | NULL |     NULL |          0 |           0 | T-R-EACH-FINGER-U-D_1                                              |       1 |          168 |
|  566 | 2025-04-25 22:22:55.892000 |     0 | NULL |     NULL |          0 |           0 | Z-R-WAVE-L45-M-R45-M_1                                             |       1 |          148 |
|  631 | 2025-04-25 23:16:23.180000 |     0 | NULL |     NULL |          0 |           0 | Z-R-SWITCH-Y180_1                                                  |       1 |          178 |
|  689 | 2025-04-26 12:58:09.537000 |     0 | NULL |     NULL |          0 |           0 | Z-R-OK_1                                                           |       1 |          165 |
|  739 | 2025-04-26 13:07:43.125000 |     0 | NULL |     NULL |          0 |           0 | Z-R-SWITCH-AND-BACK_1                                              |       1 |          166 |
|  806 | 2025-04-26 14:59:04.770000 |     0 | NULL |     NULL |          0 |           0 | Z-R-FIST_1                                                         |       1 |          189 |
|  866 | 2025-04-26 15:35:01.047000 |     0 | NULL |     NULL |          0 |           0 | Z-R-SWITCH-FIST_1                                                  |       1 |          191 |
|  916 | 2025-04-26 16:40:17.186000 |     0 | NULL |     NULL |          0 |           0 | Z-R-MAYBE-L-M-R-M_1                                                |       1 |          189 |
|  968 | 2025-04-26 16:48:06.512000 |     0 | NULL |     NULL |          0 |           0 | Z-R-VICTORIA_1                                                     |       1 |          180 |






465 | T-R-INDEX-U-D_1        | 164
515 | T-R-EACH-FINGER-U-D_1  | 168
566 | Z-R-WAVE-L45-M-R45-M_1 | 148
631 | Z-R-SWITCH-Y180_1      | 178
689 | Z-R-OK_1               | 165
739 | Z-R-SWITCH-AND-BACK_1  | 166
806 | Z-R-FIST_1             | 189
866 | Z-R-SWITCH-FIST_1      | 191
916 | Z-R-MAYBE-L-M-R-M_1    | 189
968 | Z-R-VICTORIA_1         | 180




T-R-INDEX-U-D,T-R-EACH-FINGER-U-D,Z-R-WAVE-L45-M-R45-M,Z-R-SWITCH-Y180,Z-R-OK,Z-R-SWITCH-AND-BACK,Z-R-FIST,Z-R-SWITCH-FIST,Z-R-MAYBE-L-M-R-M,Z-R-VICTORIA


465 515 566 631 689 739 806 866 916 968

for i in 465 515 566 631 689 739 806 866 916 968 ; do echo $i ; done

python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "444" --threshold-recognition 0.3 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_wswe_xnth3_te_calc_tr_0.3 | tee logs/gesture_data_extractor.2.py.`now_str`.log


for i in 465 515 566 631 689 739 806 866 916 968 ; do echo $i ; python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "444" --threshold-recognition 0.3 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_wswe_xnth3_te_calc_tr_0.3 | tee logs/gesture_data_extractor.2.py.`now_str`.log ; done


for i in 465 515 566 631 689 739 806 866 916 968 ; do echo $i ; python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "$i" --threshold-recognition 0.3 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_xnth3_wswe_tr_0.3 | tee logs/gesture_data_extractor.2.py.`now_str`.log ; done


for i in 465 515 566 631 689 739 806 866 916 968 ; do echo $i ; python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "$i" --threshold-recognition 0.3 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v | tee logs/gesture_data_extractor.2.py.`now_str`.log ; done


for i in 465 515 566 631 689 739 806 866 916 968 ; do echo $i ; python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "$i" --threshold-recognition 0.4 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v | tee logs/gesture_data_extractor.2.py.`now_str`.log ; done





1027


for i in 465 515 566 631 689 739 806 866 916 968 ; do echo $i ; python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "$i" --threshold-recognition 0.3 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_xnth3_wswe_tr_0.3 | tee logs/gesture_data_extractor.2.py.`now_str`.log ; done


for i in {1018..1027} ; do echo -n "$i," ; done

1018,1019,1020,1021,1022,1023,1024,1025,1026,1027


for i in 465 515 566 631 689 739 806 866 916 968 ; do echo $i ; python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "$i" --threshold-recognition 0.4 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_xnth3_wswe_tr_0.4 | tee logs/gesture_data_extractor.2.py.`now_str`.log ; done

 for i in {1028..1037} ; do echo -n "$i," ; done

1028,1029,1030,1031,1032,1033,1034,1035,1036,1037

for i in 465 515 566 631 689 739 806 866 916 968 ; do echo $i ; python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "$i" --threshold-recognition 0.5 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_xnth3_wswe_tr_0.5 ; done | tee logs/gesture_data_extractor.2.py.`now_str`.log 

 for i in {1038..1047} ; do echo -n "$i," ; done

1038,1039,1040,1041,1042,1043,1044,1045,1046,1047


#


SELECT g.id
FROM Gesture g
INNER JOIN (
    SELECT 
        SUBSTRING_INDEX(userAlias, '_', 1) AS alias_prefix,
        MAX(id) AS latest_id
    FROM Gesture
    WHERE SUBSTRING_INDEX(userAlias, '_', 1) IN (
        'T-R-INDEX-U-D',
        'T-R-EACH-FINGER-U-D',
        'Z-R-WAVE-L45-M-R45-M',
        'Z-R-SWITCH-Y180',
        'Z-R-OK',
        'Z-R-SWITCH-AND-BACK',
        'Z-R-FIST',
        'Z-R-SWITCH-FIST',
        'Z-R-MAYBE-L-M-R-M',
        'Z-R-VICTORIA'
    )
    GROUP BY alias_prefix, userAlias
) latest ON g.id = latest.latest_id;



SELECT g.id
FROM Gesture g
INNER JOIN (
    SELECT 
        SUBSTRING_INDEX(userAlias, '_', 1) AS alias_prefix,
        MAX(id) AS latest_id
    FROM Gesture
    WHERE SUBSTRING_INDEX(userAlias, '_', 1) IN (
        'T-R-INDEX-U-D'
  
    ) 
    AND userAlias NOT LIKE '%xnth%'
    AND userAlias REGEXP '_[0-9]+$' -- ensure numeric suffix exists
    GROUP BY alias_prefix, userAlias
) latest ON g.id = latest.latest_id;



SELECT count(g.id)
FROM Gesture g
INNER JOIN (
    SELECT 
        SUBSTRING_INDEX(userAlias, '_', 1) AS alias_prefix,
        MAX(id) AS latest_id
    FROM Gesture
    WHERE SUBSTRING_INDEX(userAlias, '_', 1) IN (
        'T-R-INDEX-U-D'
  
    ) 
    AND userAlias NOT LIKE '%xnth%'
    AND userAlias REGEXP '_[0-9]+$' -- ensure numeric suffix exists
    GROUP BY alias_prefix, userAlias
) latest ON g.id = latest.latest_id;



SELECT g.id
FROM Gesture g
INNER JOIN (
    SELECT 
        SUBSTRING_INDEX(userAlias, '_', 1) AS alias_prefix,
        MAX(id) AS latest_id
    FROM Gesture
    WHERE SUBSTRING_INDEX(userAlias, '_', 1) IN (
        'T-R-INDEX-U-D'
  
    ) 
    AND userAlias NOT LIKE '%xnth%'
    AND userAlias REGEXP '_[0-9]+$' -- ensure numeric suffix exists
    GROUP BY alias_prefix, userAlias
) latest ON g.id = latest.latest_id order by g.id;




mysql -u gagweb --password=password gagweb -C "SELECT g.id
FROM Gesture g
INNER JOIN (
    SELECT 
        SUBSTRING_INDEX(userAlias, '_', 1) AS alias_prefix,
        MAX(id) AS latest_id
    FROM Gesture
    WHERE SUBSTRING_INDEX(userAlias, '_', 1) IN (
        'T-R-INDEX-U-D'
  
    ) 
    AND userAlias NOT LIKE '%xnth%'
    AND userAlias REGEXP '_[0-9]+$' -- ensure numeric suffix exists
    GROUP BY alias_prefix, userAlias
) latest ON g.id = latest.latest_id order by g.id;"







mysql -u gagweb --password=password gagweb -c "SELECT g.id
FROM Gesture g
INNER JOIN (
    SELECT 
        SUBSTRING_INDEX(userAlias, '_', 1) AS alias_prefix,
        MAX(id) AS latest_id
    FROM Gesture
    WHERE SUBSTRING_INDEX(userAlias, '_', 1) IN (
        'T-R-INDEX-U-D'
  
    ) 
    AND userAlias NOT LIKE '%xnth%'
    AND userAlias REGEXP '_[0-9]+$' -- ensure numeric suffix exists
    GROUP BY alias_prefix, userAlias
) latest ON g.id = latest.latest_id order by g.id;"



mysql -u gagweb --password=password gagweb -e "
SELECT 
    SUBSTRING_INDEX(userAlias, '_', 1) AS alias_prefix,
    COUNT(*) AS gesture_count
FROM Gesture
WHERE SUBSTRING_INDEX(userAlias, '_', 1) IN (
    'T-R-INDEX-U-D',
    'T-R-EACH-FINGER-U-D',
    'Z-R-WAVE-L45-M-R45-M',
    'Z-R-SWITCH-Y180',
    'Z-R-OK',
    'Z-R-SWITCH-AND-BACK',
    'Z-R-FIST',
    'Z-R-SWITCH-FIST',
    'Z-R-MAYBE-L-M-R-M',
    'Z-R-VICTORIA'
)
AND userAlias NOT LIKE '%xnth%'
AND userAlias REGEXP '_[0-9]+$'
GROUP BY alias_prefix
ORDER BY alias_prefix;
"


mysql -u gagweb --password=password gagweb -e "SELECT g.id
FROM Gesture g
INNER JOIN (
    SELECT 
        SUBSTRING_INDEX(userAlias, '_', 1) AS alias_prefix,
        MAX(id) AS latest_id
    FROM Gesture
    WHERE SUBSTRING_INDEX(userAlias, '_', 1) IN (
        'Z-R-FIST'
    ) 
    AND userAlias NOT LIKE '%xnth%'
    AND userAlias REGEXP '_[0-9]+$' -- ensure numeric suffix exists
    GROUP BY alias_prefix, userAlias
) latest ON g.id = latest.latest_id order by g.id;"



mysql -u gagweb --password=password gagweb -e "
SELECT g.userAlias, g.id, COUNT(dl.id) AS dataline_count
FROM Gesture g
LEFT JOIN DataLine dl ON dl.gesture_id = g.id
INNER JOIN (
    SELECT 
        SUBSTRING_INDEX(userAlias, '_', 1) AS alias_prefix,
        MAX(id) AS latest_id
    FROM Gesture
    WHERE SUBSTRING_INDEX(userAlias, '_', 1) IN (
          'T-R-INDEX-U-D',
    'T-R-EACH-FINGER-U-D',
    'Z-R-WAVE-L45-M-R45-M',
    'Z-R-SWITCH-Y180',
    'Z-R-OK',
    'Z-R-SWITCH-AND-BACK',
    'Z-R-FIST',
    'Z-R-SWITCH-FIST',
    'Z-R-MAYBE-L-M-R-M',
    'Z-R-VICTORIA'
      )
      AND userAlias NOT LIKE '%xnth%'
      AND userAlias REGEXP '_[0-9]+$'
    GROUP BY alias_prefix, userAlias
) latest ON g.id = latest.latest_id
GROUP BY g.id
ORDER BY g.id;
"





mysql -u gagweb --password=password gagweb -e "
SELECT g.userAlias, g.id, COUNT(dl.id) AS dataline_count
FROM Gesture g
LEFT JOIN DataLine dl ON dl.gesture_id = g.id
INNER JOIN (
    SELECT 
        SUBSTRING_INDEX(userAlias, '_', 1) AS alias_prefix,
        MAX(id) AS latest_id
    FROM Gesture
    WHERE SUBSTRING_INDEX(userAlias, '_', 1) IN (
          'T-R-INDEX-U-D',
    'T-R-EACH-FINGER-U-D',
    'Z-R-WAVE-L45-M-R45-M',
    'Z-R-SWITCH-Y180',
    'Z-R-OK',
    'Z-R-SWITCH-AND-BACK',
    'Z-R-FIST',
    'Z-R-SWITCH-FIST',
    'Z-R-MAYBE-L-M-R-M',
    'Z-R-VICTORIA'
      )
      AND userAlias NOT LIKE '%xnth%'
      AND userAlias REGEXP '_[0-9]+$'
    GROUP BY alias_prefix, userAlias
) latest ON g.id = latest.latest_id
GROUP BY g.id
ORDER BY g.id;
"



mysql -u gagweb --password=password gagweb -e "
SELECT g.userAlias, g.id, COUNT(dl.id) AS dataline_count
FROM Gesture g
LEFT JOIN DataLine dl ON dl.gesture_id = g.id
INNER JOIN (
    SELECT 
        SUBSTRING_INDEX(userAlias, '_', 1) AS alias_prefix,
        MAX(id) AS latest_id
    FROM Gesture
    WHERE SUBSTRING_INDEX(userAlias, '_', 1) IN (
          'T-R-INDEX-U-D',
    'T-R-EACH-FINGER-U-D',
    'Z-R-WAVE-L45-M-R45-M',
    'Z-R-SWITCH-Y180',
    'Z-R-OK',
    'Z-R-SWITCH-AND-BACK',
    'Z-R-FIST',
    'Z-R-SWITCH-FIST',
    'Z-R-MAYBE-L-M-R-M',
    'Z-R-VICTORIA'
      )
      AND userAlias NOT LIKE '%xnth%'
      AND userAlias REGEXP '_[0-9]+$'
    GROUP BY alias_prefix, userAlias
) latest ON g.id = latest.latest_id
GROUP BY g.id
ORDER BY g.id;
" | grep -v '\----' | grep -v 'id' | awk '{print $2}' | tr '\n' ','
465,466,467,468,469,470,471,472,473,474,475,476,477,478,479,480,481,482,483,484,485,486,487,488,489,490,491,492,493,494,495,496,497,498,499,500,501,502,503,504,505,506,507,508,509,510,511,512,513,514,515,516,517,518,519,520,521,522,523,524,525,526,527,528,529,530,531,532,533,534,535,536,537,538,539,540,541,542,543,544,545,546,547,548,549,550,551,552,553,554,555,556,557,558,559,560,561,562,563,564,566,567,570,571,572,573,577,578,581,582,583,584,585,586,587,588,589,593,594,595,596,597,598,599,600,601,605,606,607,610,611,612,613,614,615,616,617,618,619,620,621,622,623,624,625,626,627,628,629,630,635,636,637,638,639,640,641,642,643,644,645,646,647,648,649,650,651,652,653,654,659,660,661,662,663,664,665,666,667,668,669,670,671,672,673,674,675,676,677,678,679,680,681,682,683,684,685,686,687,688,689,690,691,692,693,694,695,696,697,698,699,700,701,702,703,704,705,706,707,708,709,710,711,712,713,714,715,716,717,718,719,720,721,722,723,724,725,726,727,728,729,730,731,732,733,734,735,736,737,738,739,740,741,742,743,744,745,746,747,748,749,750,751,752,753,754,755,756,757,758,766,767,768,769,770,771,772,773,774,775,776,777,778,779,780,781,782,783,784,785,786,787,788,789,790,791,792,793,794,795,806,807,808,809,810,811,812,813,814,815,816,817,818,819,820,821,822,823,824,825,826,827,828,829,830,831,832,833,834,835,836,837,838,839,840,841,842,843,844,845,846,847,848,849,850,851,852,853,854,855,866,867,868,869,870,871,872,873,874,875,876,877,878,879,880,881,882,883,884,885,886,887,888,889,890,891,892,893,894,895,896,897,898,899,900,901,902,903,904,905,906,907,908,909,910,911,912,913,914,915,916,917,918,919,920,921,922,923,924,925,926,927,928,929,930,931,932,933,934,935,936,937,938,939,940,941,942,943,944,945,946,947,948,949,950,951,952,953,954,955,956,957,958,959,960,961,962,963,964,965,968,969,970,971,972,973,974,975,976,977,978,979,980,981,982,983,984,985,986,987,988,989,990,991,992,993,994,995,996,997,998,999,1000,1001,1002,1003,1004,1005,1006,1007,1008,1009,1010,1011,1012,1013,1014,1015,1016,1017,





mysql -u gagweb --password=password gagweb -e "
WITH RankedGestures AS (
    SELECT 
        g.id,
        SUBSTRING_INDEX(g.userAlias, '_', 1) AS alias_prefix,
        COUNT(dl.id) AS dataline_count,
        ROW_NUMBER() OVER (PARTITION BY SUBSTRING_INDEX(g.userAlias, '_', 1) ORDER BY g.id) AS row_num
    FROM Gesture g
    LEFT JOIN DataLine dl ON dl.gesture_id = g.id
    WHERE SUBSTRING_INDEX(g.userAlias, '_', 1) IN (
        'T-R-INDEX-U-D',
        'T-R-EACH-FINGER-U-D',
        'Z-R-WAVE-L45-M-R45-M',
        'Z-R-SWITCH-Y180',
        'Z-R-OK',
        'Z-R-SWITCH-AND-BACK',
        'Z-R-FIST',
        'Z-R-SWITCH-FIST',
        'Z-R-MAYBE-L-M-R-M',
        'Z-R-VICTORIA'
    )
    AND g.userAlias NOT LIKE '%xnth%'
    AND g.userAlias REGEXP '_[0-9]+$'
    GROUP BY g.id
)
SELECT id, alias_prefix, dataline_count
FROM RankedGestures
WHERE row_num <= 5
ORDER BY alias_prefix, id;
"


mysql -u gagweb --password=password gagweb -e "
WITH LatestGestures AS (
    SELECT 
        SUBSTRING_INDEX(userAlias, '_', 1) AS alias_prefix,
        userAlias,
        MAX(id) AS latest_id
    FROM Gesture
    WHERE SUBSTRING_INDEX(userAlias, '_', 1) IN (
        'T-R-INDEX-U-D',
        'T-R-EACH-FINGER-U-D',
        'Z-R-WAVE-L45-M-R45-M',
        'Z-R-SWITCH-Y180',
        'Z-R-OK',
        'Z-R-SWITCH-AND-BACK',
        'Z-R-FIST',
        'Z-R-SWITCH-FIST',
        'Z-R-MAYBE-L-M-R-M',
        'Z-R-VICTORIA'
    )
    AND userAlias NOT LIKE '%xnth%'
    AND userAlias REGEXP '_[0-9]+$'
    GROUP BY alias_prefix, userAlias
),
RankedGestures AS (
    SELECT 
        g.id,
        lg.alias_prefix,
        COUNT(dl.id) AS dataline_count,
        ROW_NUMBER() OVER (PARTITION BY lg.alias_prefix ORDER BY g.id) AS row_num
    FROM LatestGestures lg
    INNER JOIN Gesture g ON g.id = lg.latest_id
    LEFT JOIN DataLine dl ON dl.gesture_id = g.id -- corrected column name
    GROUP BY g.id
)
SELECT id, alias_prefix, dataline_count
FROM RankedGestures
WHERE row_num <= 5
ORDER BY alias_prefix, id;
" | grep -v '\----' | grep -v 'id' | awk '{print $1}' | tr '\n' ','

515,516,517,518,519,465,466,467,468,469,806,807,808,809,810,916,917,918,919,920,689,690,691,692,693,739,740,741,742,743,866,867,868,869,870,635,636,637,638,639,968,969,970,971,972,566,567,570,571,572


##


http://localhost:8080/gagweb/#!/recognize?refGestureIds=455,456,457&inputGestureIds=444,453,402,411,392,401,378,387,368,377


http://localhost:8080/gagweb/#!/recognize?refGestureIds=455,456,457&inputGestureIds=515,516,517,518,519,465,466,467,468,469,806,807,808,809,810,916,917,918,919,920,689,690,691,692,693,739,740,741,742,743,866,867,868,869,870,635,636,637,638,639,968,969,970,971,972,566,567,570,571,572






mysql -u gagweb --password=password gagweb -e "
WITH LatestGestures AS (
    SELECT 
        SUBSTRING_INDEX(userAlias, '_', 1) AS alias_prefix,
        userAlias,
        MAX(id) AS latest_id
    FROM Gesture
    WHERE SUBSTRING_INDEX(userAlias, '_', 1) IN (
        'T-R-INDEX-U-D',
        'T-R-EACH-FINGER-U-D',
        'Z-R-WAVE-L45-M-R45-M',
        'Z-R-SWITCH-Y180',
        'Z-R-OK',
        'Z-R-SWITCH-AND-BACK',
        'Z-R-FIST',
        'Z-R-SWITCH-FIST',
        'Z-R-MAYBE-L-M-R-M',
        'Z-R-VICTORIA'
    )
    AND userAlias NOT LIKE '%xnth%'
    AND userAlias REGEXP '_[0-9]+$'
    GROUP BY alias_prefix, userAlias
),
RankedGestures AS (
    SELECT 
        g.id,025-03-17 19
        lg.alias_prefix,
        COUNT(dl.id) AS dataline_count,
        ROW_NUMBER() OVER (PARTITION BY lg.alias_prefix ORDER BY g.id) AS row_num
    FROM LatestGestures lg
    INNER JOIN Gesture g ON g.id = lg.latest_id
    LEFT JOIN DataLine dl ON dl.gesture_id = g.id -- corrected column name
    GROUP BY g.id
)
SELECT id, alias_prefix, dataline_count
FROM RankedGestures
WHERE row_num <= 2
ORDER BY alias_prefix, id;
" | grep -v '\----' | grep -v 'id' | awk '{print $1}' | tr '\n' ','


515,516,465,466,806,807,916,917,689,690,739,740,866,867,635,636,968,969,566,567

http://localhost:8080/gagweb/#!/recognize?refGestureIds=455,456,457&inputGestureIds=465,466,467,468,469,515,516,517,518,519,566,567,570,571,572,635,636,637,638,639,689,690,691,692,693,739,740,741,742,743,806,807,808,809,810,866,867,868,869,870,968,969,970,971,972,916,917,918,919,920







1018,1019,1020,1021,1022,1023,1024,1025,1026,1027
1028,1029,1030,1031,1032,1033,1034,1035,1036,1037
1038,1039,1040,1041,1042,1043,1044,1045,1046,1047


1018,1019,1020,1021,1022,1023,1024,1025,1026,1027
1028,1029,1030,1031,1032,1033,1034,1035,1036,1037
1038,1039,1040,1041,1042,1043,1044,1045,1046,1047


http://localhost:8080/gagweb/#!/recognize?refGestureIds=1018,1019,1020,1021,1022,1023,1024,1025,1026,1027&inputGestureIds=465,466,467,468,469,515,516,517,518,519,566,567,570,571,572,635,636,637,638,639,689,690,691,692,693,739,740,741,742,743,806,807,808,809,810,866,867,868,869,870,968,969,970,971,972,916,917,918,919,920


1018,1019,1020,1021,1022,1023,1024,1025,1026,1027
1028,1029,1030,1031,1032,1033,1034,1035,1036,1037
1038,1039,1040,1041,1042,1043,1044,1045,1046,1047

http://localhost:8080/gagweb/#!/recognize?refGestureIds=1018,1019,1020,1021,1022,1023,1024,1025,1026,1027&inputGestureIds=515,516,465,466,806,807,916,917,689,690,739,740,866,867,635,636,968,969,566,567


http://localhost:8080/gagweb/#!/recognize?refGestureIds=1018,1019,1020&inputGestureIds=515,739,806

http://localhost:8080/gagweb/#!/recognize?refGestureIds=1018,1019&inputGestureIds=515,739



http://localhost:8080/gagweb/#!/recognize?refGestureIds=1028,1029,1030,1031,1032,1033,1034,1035,1036,1037&inputGestureIds=465,466,467,468,469,515,516,517,518,519,566,567,570,571,572,635,636,637,638,639,689,690,691,692,693,739,740,741,742,743,806,807,808,809,810,866,867,868,869,870,968,969,970,971,972,916,917,918,919,920




#

for i in 465 515 566 631 689 739 806 866 916 968 ; do echo $i ; python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "$i" --threshold-recognition 0.3 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_xnth3_wswe_tr_0.3 | tee logs/gesture_data_extractor.2.py.`now_str`.log ; done


for i in {1018..1027} ; do echo -n "$i," ; done

1018,1019,1020,1021,1022,1023,1024,1025,1026,1027


for i in 465 515 566 631 689 739 806 866 916 968 ; do echo $i ; python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "$i" --threshold-recognition 0.4 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_xnth3_wswe_tr_0.4 | tee logs/gesture_data_extractor.2.py.`now_str`.log ; done

 for i in {1028..1037} ; do echo -n "$i," ; done

1028,1029,1030,1031,1032,1033,1034,1035,1036,1037

for i in 465 515 566 635 689 739 806 866 916 968 ; do echo $i ; python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "$i" --threshold-recognition 0.5 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_xnth3_wswe_tr_0.5 ; done | tee logs/gesture_data_extractor.2.py.`now_str`.log 

 for i in {1038..1047} ; do echo -n "$i," ; done

1038,1039,1040,1041,1042,1043,1044,1045,1046,1047


###

for i in 635 ; do echo $i ; python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "$i" --threshold-recognition 0.3 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_xnth3_wswe_tr_0.3 | tee logs/gesture_data_extractor.2.py.`now_str`.log ; done


for i in 635 ; do echo $i ; python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "$i" --threshold-recognition 0.4 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_xnth3_wswe_tr_0.4 | tee logs/gesture_data_extractor.2.py.`now_str`.log ; done


for i in 635 ; do echo $i ; python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "$i" --threshold-recognition 0.5 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_xnth3_wswe_tr_0.5 ; done | tee logs/gesture_data_extractor.2.py.`now_str`.log 



 select g.*, count(dl.id) from Gesture g join DataLine dl on g.id = dl.gesture_id where g.userAlias regexp 'Z-R-VICTORIA_1.*' group by g.id;



#

select g.*, count(dl.id) from Gesture g join DataLine dl on g.id = dl.gesture_id where g.userAlias like 'Z-R-SWITCH-AND-BACK_1*' group by g.id;
+-----+----------------------------+-------+------+----------+------------+-------------+-----------------------+---------+--------------+
| id  | dateCreated                | delay | exec | isActive | isFiltered | shouldMatch | userAlias             | user_id | count(dl.id) |
+-----+----------------------------+-------+------+----------+------------+-------------+-----------------------+---------+--------------+
| 739 | 2025-04-26 13:07:43.125000 |     0 | NULL |     NULL |          0 |           0 | Z-R-SWITCH-AND-BACK_1 |       1 |          166 |
+-----+----------------------------+-------+------+----------+------------+-------------+-----------------------+---------+--------------+




http://localhost:8080/gagweb/#!/recognize?refGestureIds=1018,1019,1020,1021,1022,1023,1024,1025,1026,1027&inputGestureIds=465,466,467,468,469,515,516,517,518,519,566,567,570,571,572,635,636,637,638,639,689,690,691,692,693,739,740,741,742,743,806,807,808,809,810,866,867,868,869,870,968,969,970,971,972,916,917,918,919,920







http://localhost:8080/gagweb/#!/recognize?refGestureIds=1023&inputGestureIds=739






http://localhost:8080/gagweb/#!/recognize?refGestureIds=1020,1021,1022,1023,1024,1025,1026,1027&inputGestureIds=465,466,467,468,469,515,516,517,518,519,566,567,570,571,572,635,636,637,638,639,689,690,691,692,693,739,740,741,742,743,806,807,808,809,810,866,867,868,869,870,968,969,970,971,972,916,917,918,919,920



http://localhost:8080/gagweb/#!/recognize?refGestureIds=1018,1019,1020,1021,1022,1023,1024,1025,1026,1027&inputGestureIds=465,466,467,468,469,515,516,517,518,519,566,567,570,571,572,635,636,637,638,639,689,690,691,692,693,739,740,741,742,743,806,807,808,809,810,866,867,868,869,870,968,969,970,971,972,916,917,918,919,920





1048,1049,1050

1021
1031
1041

1048,
1049,
1050,

#


mysql -u gagweb --password=password gagweb -e "
WITH LatestGestures AS (
    SELECT 
        SUBSTRING_INDEX(userAlias, '_', 1) AS alias_prefix,
        userAlias,
        MAX(id) AS latest_id
    FROM Gesture
    WHERE SUBSTRING_INDEX(userAlias, '_', 1) IN (
        'T-R-INDEX-U-D',
        'T-R-EACH-FINGER-U-D',
        'Z-R-WAVE-L45-M-R45-M',
        'Z-R-SWITCH-Y180',
        'Z-R-OK',
        'Z-R-SWITCH-AND-BACK',
        'Z-R-FIST',
        'Z-R-SWITCH-FIST',
        'Z-R-MAYBE-L-M-R-M',
        'Z-R-VICTORIA'
    )
    AND userAlias NOT LIKE '%xnth%'
    AND userAlias REGEXP '_[0-9]+$'
    GROUP BY alias_prefix, userAlias
),
RankedGestures AS (
    SELECT 
        g.id,
        lg.alias_prefix,
        COUNT(dl.id) AS dataline_count,
        ROW_NUMBER() OVER (PARTITION BY lg.alias_prefix ORDER BY g.id) AS row_num
    FROM LatestGestures lg
    INNER JOIN Gesture g ON g.id = lg.latest_id
    LEFT JOIN DataLine dl ON dl.gesture_id = g.id -- corrected column name
    GROUP BY g.id
)
SELECT id, alias_prefix, dataline_count
FROM RankedGestures
WHERE row_num <= 51
ORDER BY alias_prefix, id;
" | grep -v '\----' | grep -v 'id' | awk '{print $1}' | tr '\n' ','


#


http://localhost:8080/gagweb/#!/recognize?refGestureIds=1018,1019,1020,1048,1022,1023,1024,1025,1026,1027&inputGestureIds=465,466,467,468,469,515,516,517,518,519,566,567,570,571,572,635,636,637,638,639,689,690,691,692,693,739,740,741,742,743,806,807,808,809,810,866,867,868,869,870,968,969,970,971,972,916,917,918,919,920


select * from Gesture g where g.id in (1018,1019,1020,1048,1022,1023,1024,1025,1026,1027);


update Gesture g set delay = 0.01 where g.id in (1018,1019,1020,1048,1022,1023,1024,1025,1026,1027);


http://localhost:8080/gagweb/#!/recognize?refGestureIds=1018,1019,1020,1021,1022,1023,1024,1025,1026,1027&inputGestureIds=465,466,467,468,469,515,516,517,518,519,566,567,570,571,572,635,636,637,638,639,689,690,691,692,693,739,740,741,742,743,806,807,808,809,810,866,867,868,869,870,968,969,970,971,972,916,917,918,919,920


http://localhost:8080/gagweb/#!/recognize?refGestureIds=1048,1022,1023,1024,1025,1026,1027&inputGestureIds=465,466,467,468,469,515,516,517,518,519,566,567,570,571,572,635,636,637,638,639,689,690,691,692,693,739,740,741,742,743,806,807,808,809,810,866,867,868,869,870,968,969,970,971,972,916,917,918,919,920



http://localhost:8080/gagweb/#!/recognize?refGestureIds=1048,1022,1023,1024,1025,1026,1027&inputGestureIds=635,636,637,638,639



http://localhost:8080/gagweb/#!/recognize?refGestureIds=1018,1019,1020,1021,1048,1022,1023,1024,1025,1026,1027&inputGestureIds=515,516,517,518,519,520,521,522,523,524,525,526,527,528,529,530,531,532,533,534,535,536,537,538,539,540,541,542,543,544,545,546,547,548,549,550,551,552,553,554,555,556,557,558,559,560,561,562,563,564,465,466,467,468,469,470,471,472,473,474,475,476,477,478,479,480,481,482,483,484,485,486,487,488,489,490,491,492,493,494,495,496,497,498,499,500,501,502,503,504,505,506,507,508,509,510,511,512,513,514,806,807,808,809,810,811,812,813,814,815,816,817,818,819,820,821,822,823,824,825,826,827,828,829,830,831,832,833,834,835,836,837,838,839,840,841,842,843,844,845,846,847,848,849,850,851,852,853,854,855,916,917,918,919,920,921,922,923,924,925,926,927,928,929,930,931,932,933,934,935,936,937,938,939,940,941,942,943,944,945,946,947,948,949,950,951,952,953,954,955,956,957,958,959,960,961,962,963,964,965,689,690,691,692,693,694,695,696,697,698,699,700,701,702,703,704,705,706,707,708,709,710,711,712,713,714,715,716,717,718,719,720,721,722,723,724,725,726,727,728,729,730,731,732,733,734,735,736,737,738,739,740,741,742,743,744,745,746,747,748,749,750,751,752,753,754,755,756,757,758,766,767,768,769,770,771,772,773,774,775,776,777,778,779,780,781,782,783,784,785,786,787,788,789,790,791,792,793,794,795,866,867,868,869,870,871,872,873,874,875,876,877,878,879,880,881,882,883,884,885,886,887,888,889,890,891,892,893,894,895,896,897,898,899,900,901,902,903,904,905,906,907,908,909,910,911,912,913,914,915,635,636,637,638,639,640,641,642,643,644,645,646,647,648,649,650,651,652,653,654,659,660,661,662,663,664,665,666,667,668,669,670,671,672,673,674,675,676,677,678,679,680,681,682,683,684,685,686,687,688,968,969,970,971,972,973,974,975,976,977,978,979,980,981,982,983,984,985,986,987,988,989,990,991,992,993,994,995,996,997,998,999,1000,1001,1002,1003,1004,1005,1006,1007,1008,1009,1010,1011,1012,1013,1014,1015,1016,1017,566,567,570,571,572,573,577,578,581,582,583,584,585,586,587,588,589,593,594,595,596,597,598,599,600,601,605,606,607,610,611,612,613,614,615,616,617,618,619,620,621,622,623,624,625,626,627,628,629,630



http://localhost:8080/gagweb/#!/recognize?refGestureIds=1018,1019,1020,1021,1048,1022,1023,1024,1025,1026,1027&inputGestureIds=515,516,517,518,519,520,521,522,523,524,525,526,527,528,529,530,531,532,533,534,535,536,537,538,539,540,541,542,543,544,545,546,547,548,549,550,551,552,553,554,555,556,557,558,559,560,561,562,563,564,465,466,467,468,469,470,471,472,473,474,475,476,477,478,479,480,481,482,483,484,485,486,487,488,489,490,491,492,493,494,495,496,497,498,499,500,501,502,503,504,505,506,507,508,509,510,511,512,513,514,806,807,808,809,810,811,812,813,814,815,816,817,818,819,820,821,822,823,824,825,826,827,828,829,830,831,832,833,834,835,836,837,838,839,840,841,842,843,844,845,846,847,848,849,850,851,852,853,854,855,916,917,918,919,920,921,922,923,924,925,926,927,928,929,930,931,932,933,934,935,936,937,938,939,940,941,942,943,944,945,946,947,948,949,950,951,952,953,954,955,956,957,958,959,960,961,962,963,964,965,689,690,691,692,693,694,695,696,697,698,699,700,701,702,703,704,705,706,707,708,709,710,711,712,713,714,715,716,717,718,719,720,721,722,723,724,725,726,727,728,729,730,731,732,733,734,735,736,737,738,739,740,741,742,743,744,745,746,747,748,749,750,751,752,753,754,755,756,757,758,766,767,768,769,770,771,772,773,774,775,776,777,778,779,780,781,782,783,784,785,786,787,788,789,790,791,792,793,794,795,866,867,868,869,870,871,872,873,874,875,876,877,878,879,880,881,882,883,884,885,886,887,888,889,890,891,892,893,894,895,896,897,898,899,900,901,902,903,904,905,906,907,908,909,910,911,912,913,914,915,635,636,637,638,639,640,641,642,643,644,645,646,647,648,649,650,651,652,653,654,659,660,661,662,663,664,665,666,667,668,669,670,671,672,673,674,675,676,677,678,679,680,681,682,683,684,685,686,687,688,968,969,970,971,972,973,974,975,976,977,978,979,980,981,982,983,984,985,986,987,988,989,990,991,992,993,994,995,996,997,998,999,1000,1001,1002,1003,1004,1005,1006,1007,1008,1009,1010,1011,1012,1013,1014,1015,1016,1017,566,567,570,571,572,573,577,578,581,582,583,584,585,586,587,588,589,593,594,595,596,597,598,599,600,601,605,606,607,610,611,612,613,614,615,616,617,618,619,620,621,622,623,624,625,626,627,628,629,630



1018,1019,1020,1021,1048,1022,1023,1024,1025,1026,1027
1028,1029,1030,1031,1049,1032,1033,1034,1035,1036,1037
1038,1039,1040,1041,1050,1042,1043,1044,1045,1046,1047




1021
1031
1041

1048,
1049,
1050,


1018,1019,1020,1021,1048,1022,1023,1024,1025,1026,1027,1028,1029,1030,1031,1049,1032,1033,1034,1035,1036,1037,1038,1039,1040,1041,1050,1042,1043,1044,1045,1046,1047


http://localhost:8080/gagweb/#!/recognize?refGestureIds=1018,1019,1020,1021,1048,1022,1023,1024,1025,1026,1027,1028,1029,1030,1031,1049,1032,1033,1034,1035,1036,1037,1038,1039,1040,1041,1050,1042,1043,1044,1045,1046,1047&inputGestureIds=515,516,517,518,519,520,521,522,523,524,525,526,527,528,529,530,531,532,533,534,535,536,537,538,539,540,541,542,543,544,545,546,547,548,549,550,551,552,553,554,555,556,557,558,559,560,561,562,563,564,465,466,467,468,469,470,471,472,473,474,475,476,477,478,479,480,481,482,483,484,485,486,487,488,489,490,491,492,493,494,495,496,497,498,499,500,501,502,503,504,505,506,507,508,509,510,511,512,513,514,806,807,808,809,810,811,812,813,814,815,816,817,818,819,820,821,822,823,824,825,826,827,828,829,830,831,832,833,834,835,836,837,838,839,840,841,842,843,844,845,846,847,848,849,850,851,852,853,854,855,916,917,918,919,920,921,922,923,924,925,926,927,928,929,930,931,932,933,934,935,936,937,938,939,940,941,942,943,944,945,946,947,948,949,950,951,952,953,954,955,956,957,958,959,960,961,962,963,964,965,689,690,691,692,693,694,695,696,697,698,699,700,701,702,703,704,705,706,707,708,709,710,711,712,713,714,715,716,717,718,719,720,721,722,723,724,725,726,727,728,729,730,731,732,733,734,735,736,737,738,739,740,741,742,743,744,745,746,747,748,749,750,751,752,753,754,755,756,757,758,766,767,768,769,770,771,772,773,774,775,776,777,778,779,780,781,782,783,784,785,786,787,788,789,790,791,792,793,794,795,866,867,868,869,870,871,872,873,874,875,876,877,878,879,880,881,882,883,884,885,886,887,888,889,890,891,892,893,894,895,896,897,898,899,900,901,902,903,904,905,906,907,908,909,910,911,912,913,914,915,635,636,637,638,639,640,641,642,643,644,645,646,647,648,649,650,651,652,653,654,659,660,661,662,663,664,665,666,667,668,669,670,671,672,673,674,675,676,677,678,679,680,681,682,683,684,685,686,687,688,968,969,970,971,972,973,974,975,976,977,978,979,980,981,982,983,984,985,986,987,988,989,990,991,992,993,994,995,996,997,998,999,1000,1001,1002,1003,1004,1005,1006,1007,1008,1009,1010,1011,1012,1013,1014,1015,1016,1017,566,567,570,571,572,573,577,578,581,582,583,584,585,586,587,588,589,593,594,595,596,597,598,599,600,601,605,606,607,610,611,612,613,614,615,616,617,618,619,620,621,622,623,624,625,626,627,628,629,630





http://localhost:8080/gagweb/#!/recognize?refGestureIds=1022,1023,1024,1025,1026,1027,1028,1029,1030,1031,1049,1032,1033,1034,1035,1036,1037,1038,1039,1040,1041,1050,1042,1043,1044,1045,1046,1047&inputGestureIds=515,516,517,518,519,520,521,522,523,524,525,526,527,528,529,530,531,532,533,534,535,536,537,538,539,540,541,542,543,544,545,546,547,548,549,550,551,552,553,554,555,556,557,558,559,560,561,562,563,564,465,466,467,468,469,470,471,472,473,474,475,476,477,478,479,480,481,482,483,484,485,486,487,488,489,490,491,492,493,494,495,496,497,498,499,500,501,502,503,504,505,506,507,508,509,510,511,512,513,514,806,807,808,809,810,811,812,813,814,815,816,817,818,819,820,821,822,823,824,825,826,827,828,829,830,831,832,833,834,835,836,837,838,839,840,841,842,843,844,845,846,847,848,849,850,851,852,853,854,855,916,917,918,919,920,921,922,923,924,925,926,927,928,929,930,931,932,933,934,935,936,937,938,939,940,941,942,943,944,945,946,947,948,949,950,951,952,953,954,955,956,957,958,959,960,961,962,963,964,965,689,690,691,692,693,694,695,696,697,698,699,700,701,702,703,704,705,706,707,708,709,710,711,712,713,714,715,716,717,718,719,720,721,722,723,724,725,726,727,728,729,730,731,732,733,734,735,736,737,738,739,740,741,742,743,744,745,746,747,748,749,750,751,752,753,754,755,756,757,758,766,767,768,769,770,771,772,773,774,775,776,777,778,779,780,781,782,783,784,785,786,787,788,789,790,791,792,793,794,795,866,867,868,869,870,871,872,873,874,875,876,877,878,879,880,881,882,883,884,885,886,887,888,889,890,891,892,893,894,895,896,897,898,899,900,901,902,903,904,905,906,907,908,909,910,911,912,913,914,915,635,636,637,638,639,640,641,642,643,644,645,646,647,648,649,650,651,652,653,654,659,660,661,662,663,664,665,666,667,668,669,670,671,672,673,674,675,676,677,678,679,680,681,682,683,684,685,686,687,688,968,969,970,971,972,973,974,975,976,977,978,979,980,981,982,983,984,985,986,987,988,989,990,991,992,993,994,995,996,997,998,999,1000,1001,1002,1003,1004,1005,1006,1007,1008,1009,1010,1011,1012,1013,1014,1015,1016,1017,566,567,570,571,572,573,577,578,581,582,583,584,585,586,587,588,589,593,594,595,596,597,598,599,600,601,605,606,607,610,611,612,613,614,615,616,617,618,619,620,621,622,623,624,625,626,627,628,629,630


http://localhost:8080/gagweb/#!/recognize?refGestureIds=1038,1039,1040,1041,1050,1042,1043,1044,1045,1046,1047&inputGestureIds=515,516,517,518,519,520,521,522,523,524,525,526,527,528,529,530,531,532,533,534,535,536,537,538,539,540,541,542,543,544,545,546,547,548,549,550,551,552,553,554,555,556,557,558,559,560,561,562,563,564,465,466,467,468,469,470,471,472,473,474,475,476,477,478,479,480,481,482,483,484,485,486,487,488,489,490,491,492,493,494,495,496,497,498,499,500,501,502,503,504,505,506,507,508,509,510,511,512,513,514,806,807,808,809,810,811,812,813,814,815,816,817,818,819,820,821,822,823,824,825,826,827,828,829,830,831,832,833,834,835,836,837,838,839,840,841,842,843,844,845,846,847,848,849,850,851,852,853,854,855,916,917,918,919,920,921,922,923,924,925,926,927,928,929,930,931,932,933,934,935,936,937,938,939,940,941,942,943,944,945,946,947,948,949,950,951,952,953,954,955,956,957,958,959,960,961,962,963,964,965,689,690,691,692,693,694,695,696,697,698,699,700,701,702,703,704,705,706,707,708,709,710,711,712,713,714,715,716,717,718,719,720,721,722,723,724,725,726,727,728,729,730,731,732,733,734,735,736,737,738,739,740,741,742,743,744,745,746,747,748,749,750,751,752,753,754,755,756,757,758,766,767,768,769,770,771,772,773,774,775,776,777,778,779,780,781,782,783,784,785,786,787,788,789,790,791,792,793,794,795,866,867,868,869,870,871,872,873,874,875,876,877,878,879,880,881,882,883,884,885,886,887,888,889,890,891,892,893,894,895,896,897,898,899,900,901,902,903,904,905,906,907,908,909,910,911,912,913,914,915,635,636,637,638,639,640,641,642,643,644,645,646,647,648,649,650,651,652,653,654,659,660,661,662,663,664,665,666,667,668,669,670,671,672,673,674,675,676,677,678,679,680,681,682,683,684,685,686,687,688,968,969,970,971,972,973,974,975,976,977,978,979,980,981,982,983,984,985,986,987,988,989,990,991,992,993,994,995,996,997,998,999,1000,1001,1002,1003,1004,1005,1006,1007,1008,1009,1010,1011,1012,1013,1014,1015,1016,1017,566,567,570,571,572,573,577,578,581,582,583,584,585,586,587,588,589,593,594,595,596,597,598,599,600,601,605,606,607,610,611,612,613,614,615,616,617,618,619,620,621,622,623,624,625,626,627,628,629,630






| T-R-INDEX-U-D        | T-R-INDEX-U-D_1-p_012345_xnth3_wswe_tr_0.3        |      1018 |
| T-R-EACH-FINGER-U-D  | T-R-EACH-FINGER-U-D_1-p_012345_xnth3_wswe_tr_0.3  |      1019 |
| Z-R-WAVE-L45-M-R45-M | Z-R-WAVE-L45-M-R45-M_1-p_012345_xnth3_wswe_tr_0.3 |      1020 |
| Z-R-OK               | Z-R-OK_1-p_012345_xnth3_wswe_tr_0.3               |      1022 |
| Z-R-SWITCH-AND-BACK  | Z-R-SWITCH-AND-BACK_1-p_012345_xnth3_wswe_tr_0.3  |      1023 |
| Z-R-FIST             | Z-R-FIST_1-p_012345_xnth3_wswe_tr_0.3             |      1024 |
| Z-R-SWITCH-FIST      | Z-R-SWITCH-FIST_1-p_012345_xnth3_wswe_tr_0.3      |      1025 |
| Z-R-MAYBE-L-M-R-M    | Z-R-MAYBE-L-M-R-M_1-p_012345_xnth3_wswe_tr_0.3    |      1026 |
| Z-R-VICTORIA         | Z-R-VICTORIA_1-p_012345_xnth3_wswe_tr_0.3         |      1027 |
| T-R-INDEX-U-D        | T-R-INDEX-U-D_1-p_012345_xnth3_wswe_tr_0.4        |      1028 |
| T-R-EACH-FINGER-U-D  | T-R-EACH-FINGER-U-D_1-p_012345_xnth3_wswe_tr_0.4  |      1029 |
| Z-R-WAVE-L45-M-R45-M | Z-R-WAVE-L45-M-R45-M_1-p_012345_xnth3_wswe_tr_0.4 |      1030 |
| Z-R-OK               | Z-R-OK_1-p_012345_xnth3_wswe_tr_0.4               |      1032 |
| Z-R-SWITCH-AND-BACK  | Z-R-SWITCH-AND-BACK_1-p_012345_xnth3_wswe_tr_0.4  |      1033 |
| Z-R-FIST             | Z-R-FIST_1-p_012345_xnth3_wswe_tr_0.4             |      1034 |
| Z-R-SWITCH-FIST      | Z-R-SWITCH-FIST_1-p_012345_xnth3_wswe_tr_0.4      |      1035 |
| Z-R-MAYBE-L-M-R-M    | Z-R-MAYBE-L-M-R-M_1-p_012345_xnth3_wswe_tr_0.4    |      1036 |
| Z-R-VICTORIA         | Z-R-VICTORIA_1-p_012345_xnth3_wswe_tr_0.4         |      1037 |
| T-R-INDEX-U-D        | T-R-INDEX-U-D_1-p_012345_xnth3_wswe_tr_0.5        |      1038 |
| T-R-EACH-FINGER-U-D  | T-R-EACH-FINGER-U-D_1-p_012345_xnth3_wswe_tr_0.5  |      1039 |
| Z-R-WAVE-L45-M-R45-M | Z-R-WAVE-L45-M-R45-M_1-p_012345_xnth3_wswe_tr_0.5 |      1040 |
| Z-R-OK               | Z-R-OK_1-p_012345_xnth3_wswe_tr_0.5               |      1042 |
| Z-R-SWITCH-AND-BACK  | Z-R-SWITCH-AND-BACK_1-p_012345_xnth3_wswe_tr_0.5  |      1043 |
| Z-R-FIST             | Z-R-FIST_1-p_012345_xnth3_wswe_tr_0.5             |      1044 |
| Z-R-SWITCH-FIST      | Z-R-SWITCH-FIST_1-p_012345_xnth3_wswe_tr_0.5      |      1045 |
| Z-R-MAYBE-L-M-R-M    | Z-R-MAYBE-L-M-R-M_1-p_012345_xnth3_wswe_tr_0.5    |      1046 |
| Z-R-VICTORIA         | Z-R-VICTORIA_1-p_012345_xnth3_wswe_tr_0.5         |      1047 |
| Z-R-SWITCH-Y180      | Z-R-SWITCH-Y180_1-p_012345_xnth3_wswe_tr_0.3      |      1048 |
| Z-R-SWITCH-Y180      | Z-R-SWITCH-Y180_1-p_012345_xnth3_wswe_tr_0.4      |      1049 |
| Z-R-SWITCH-Y180      | Z-R-SWITCH-Y180_1-p_012345_xnth3_wswe_tr_0.5      |      1050 |



| T-R-INDEX-U-D        | T-R-INDEX-U-D_1-p_012345_xnth3_wswe_tr_0.3        |      1018
| T-R-EACH-FINGER-U-D  | T-R-EACH-FINGER-U-D_1-p_012345_xnth3_wswe_tr_0.3  |      1019
| Z-R-WAVE-L45-M-R45-M | Z-R-WAVE-L45-M-R45-M_1-p_012345_xnth3_wswe_tr_0.3 |      1020
| Z-R-OK               | Z-R-OK_1-p_012345_xnth3_wswe_tr_0.3               |      1022
| Z-R-SWITCH-AND-BACK  | Z-R-SWITCH-AND-BACK_1-p_012345_xnth3_wswe_tr_0.3  |      1023
| Z-R-FIST             | Z-R-FIST_1-p_012345_xnth3_wswe_tr_0.3             |      1024
| Z-R-SWITCH-FIST      | Z-R-SWITCH-FIST_1-p_012345_xnth3_wswe_tr_0.3      |      1025
| Z-R-MAYBE-L-M-R-M    | Z-R-MAYBE-L-M-R-M_1-p_012345_xnth3_wswe_tr_0.3    |      1026
| Z-R-VICTORIA         | Z-R-VICTORIA_1-p_012345_xnth3_wswe_tr_0.3         |      1027
| T-R-INDEX-U-D        | T-R-INDEX-U-D_1-p_012345_xnth3_wswe_tr_0.4        |      1028
| T-R-EACH-FINGER-U-D  | T-R-EACH-FINGER-U-D_1-p_012345_xnth3_wswe_tr_0.4  |      1029
| Z-R-WAVE-L45-M-R45-M | Z-R-WAVE-L45-M-R45-M_1-p_012345_xnth3_wswe_tr_0.4 |      1030
| Z-R-OK               | Z-R-OK_1-p_012345_xnth3_wswe_tr_0.4               |      1032
| Z-R-SWITCH-AND-BACK  | Z-R-SWITCH-AND-BACK_1-p_012345_xnth3_wswe_tr_0.4  |      1033
| Z-R-FIST             | Z-R-FIST_1-p_012345_xnth3_wswe_tr_0.4             |      1034
| Z-R-SWITCH-FIST      | Z-R-SWITCH-FIST_1-p_012345_xnth3_wswe_tr_0.4      |      1035
| Z-R-MAYBE-L-M-R-M    | Z-R-MAYBE-L-M-R-M_1-p_012345_xnth3_wswe_tr_0.4    |      1036
| Z-R-VICTORIA         | Z-R-VICTORIA_1-p_012345_xnth3_wswe_tr_0.4         |      1037
| T-R-INDEX-U-D        | T-R-INDEX-U-D_1-p_012345_xnth3_wswe_tr_0.5        |      1038
| T-R-EACH-FINGER-U-D  | T-R-EACH-FINGER-U-D_1-p_012345_xnth3_wswe_tr_0.5  |      1039
| Z-R-WAVE-L45-M-R45-M | Z-R-WAVE-L45-M-R45-M_1-p_012345_xnth3_wswe_tr_0.5 |      1040
| Z-R-OK               | Z-R-OK_1-p_012345_xnth3_wswe_tr_0.5               |      1042
| Z-R-SWITCH-AND-BACK  | Z-R-SWITCH-AND-BACK_1-p_012345_xnth3_wswe_tr_0.5  |      1043
| Z-R-FIST             | Z-R-FIST_1-p_012345_xnth3_wswe_tr_0.5             |      1044
| Z-R-SWITCH-FIST      | Z-R-SWITCH-FIST_1-p_012345_xnth3_wswe_tr_0.5      |      1045
| Z-R-MAYBE-L-M-R-M    | Z-R-MAYBE-L-M-R-M_1-p_012345_xnth3_wswe_tr_0.5    |      1046
| Z-R-VICTORIA         | Z-R-VICTORIA_1-p_012345_xnth3_wswe_tr_0.5         |      1047
| Z-R-SWITCH-Y180      | Z-R-SWITCH-Y180_1-p_012345_xnth3_wswe_tr_0.3      |      1048
| Z-R-SWITCH-Y180      | Z-R-SWITCH-Y180_1-p_012345_xnth3_wswe_tr_0.4      |      1049
| Z-R-SWITCH-Y180      | Z-R-SWITCH-Y180_1-p_012345_xnth3_wswe_tr_0.5      |      1050




http://localhost:8080/gagweb/#!/recognize?refGestureIds=1018,1019,1020,1048,1022,1023,1024,1025,1026,1027,1028,1029,1030,1049,1032,1033,1034,1035,1036,1037,1038,1039,1040,1050,1042,1043,1044,1045,1046,1047&inputGestureIds=515,516,517,518,519,520,521,522,523,524,525,526,527,528,529,530,531,532,533,534,535,536,537,538,539,540,541,542,543,544,545,546,547,548,549,550,551,552,553,554,555,556,557,558,559,560,561,562,563,564,465,466,467,468,469,470,471,472,473,474,475,476,477,478,479,480,481,482,483,484,485,486,487,488,489,490,491,492,493,494,495,496,497,498,499,500,501,502,503,504,505,506,507,508,509,510,511,512,513,514,806,807,808,809,810,811,812,813,814,815,816,817,818,819,820,821,822,823,824,825,826,827,828,829,830,831,832,833,834,835,836,837,838,839,840,841,842,843,844,845,846,847,848,849,850,851,852,853,854,855,916,917,918,919,920,921,922,923,924,925,926,927,928,929,930,931,932,933,934,935,936,937,938,939,940,941,942,943,944,945,946,947,948,949,950,951,952,953,954,955,956,957,958,959,960,961,962,963,964,965,689,690,691,692,693,694,695,696,697,698,699,700,701,702,703,704,705,706,707,708,709,710,711,712,713,714,715,716,717,718,719,720,721,722,723,724,725,726,727,728,729,730,731,732,733,734,735,736,737,738,739,740,741,742,743,744,745,746,747,748,749,750,751,752,753,754,755,756,757,758,766,767,768,769,770,771,772,773,774,775,776,777,778,779,780,781,782,783,784,785,786,787,788,789,790,791,792,793,794,795,866,867,868,869,870,871,872,873,874,875,876,877,878,879,880,881,882,883,884,885,886,887,888,889,890,891,892,893,894,895,896,897,898,899,900,901,902,903,904,905,906,907,908,909,910,911,912,913,914,915,635,636,637,638,639,640,641,642,643,644,645,646,647,648,649,650,651,652,653,654,659,660,661,662,663,664,665,666,667,668,669,670,671,672,673,674,675,676,677,678,679,680,681,682,683,684,685,686,687,688,968,969,970,971,972,973,974,975,976,977,978,979,980,981,982,983,984,985,986,987,988,989,990,991,992,993,994,995,996,997,998,999,1000,1001,1002,1003,1004,1005,1006,1007,1008,1009,1010,1011,1012,1013,1014,1015,1016,1017,566,567,570,571,572,573,577,578,581,582,583,584,585,586,587,588,589,593,594,595,596,597,598,599,600,601,605,606,607,610,611,612,613,614,615,616,617,618,619,620,621,622,623,624,625,626,627,628,629,630




1021
1031
1041

1048,
1049,
1050,



http://localhost:8080/gagweb/#!/recognize?refGestureIds=1046,1047&inputGestureIds=515,516,517,518,519,520,521,522,523,524,525,526,527,528,529,530,531,532,533,534,535,536,537,538,539,540,541,542,543,544,545,546,547,548,549,550,551,552,553,554,555,556,557,558,559,560,561,562,563,564,465,466,467,468,469,470,471,472,473,474,475,476,477,478,479,480,481,482,483,484,485,486,487,488,489,490,491,492,493,494,495,496,497,498,499,500,501,502,503,504,505,506,507,508,509,510,511,512,513,514,806,807,808,809,810,811,812,813,814,815,816,817,818,819,820,821,822,823,824,825,826,827,828,829,830,831,832,833,834,835,836,837,838,839,840,841,842,843,844,845,846,847,848,849,850,851,852,853,854,855,916,917,918,919,920,921,922,923,924,925,926,927,928,929,930,931,932,933,934,935,936,937,938,939,940,941,942,943,944,945,946,947,948,949,950,951,952,953,954,955,956,957,958,959,960,961,962,963,964,965,689,690,691,692,693,694,695,696,697,698,699,700,701,702,703,704,705,706,707,708,709,710,711,712,713,714,715,716,717,718,719,720,721,722,723,724,725,726,727,728,729,730,731,732,733,734,735,736,737,738,739,740,741,742,743,744,745,746,747,748,749,750,751,752,753,754,755,756,757,758,766,767,768,769,770,771,772,773,774,775,776,777,778,779,780,781,782,783,784,785,786,787,788,789,790,791,792,793,794,795,866,867,868,869,870,871,872,873,874,875,876,877,878,879,880,881,882,883,884,885,886,887,888,889,890,891,892,893,894,895,896,897,898,899,900,901,902,903,904,905,906,907,908,909,910,911,912,913,914,915,635,636,637,638,639,640,641,642,643,644,645,646,647,648,649,650,651,652,653,654,659,660,661,662,663,664,665,666,667,668,669,670,671,672,673,674,675,676,677,678,679,680,681,682,683,684,685,686,687,688,968,969,970,971,972,973,974,975,976,977,978,979,980,981,982,983,984,985,986,987,988,989,990,991,992,993,994,995,996,997,998,999,1000,1001,1002,1003,1004,1005,1006,1007,1008,1009,1010,1011,1012,1013,1014,1015,1016,1017,566,567,570,571,572,573,577,578,581,582,583,584,585,586,587,588,589,593,594,595,596,597,598,599,600,601,605,606,607,610,611,612,613,614,615,616,617,618,619,620,621,622,623,624,625,626,627,628,629,630


/workspace/p/gag-web/gestures/extract/confusion_matrix_html

python script.py --file yourfile.html \
--col_map '{
    "T-R-EACH-FINGER-U-D": "EACH",
    "T-R-INDEX-U-D": "INDEX",
    "Z-R-FIST": "FIST",
    "Z-R-MAYBE-L-M-R-M": "MAYBE",
    "Z-R-OK": "OK",
    "Z-R-SWITCH-AND-BACK": "SW-BACK",
    "Z-R-SWITCH-FIST": "SW-FIST",
    "Z-R-SWITCH-Y180": "SW",
    "Z-R-VICTORIA": "VICT",
    "Z-R-WAVE-L45-M-R45-M": "WAVE"
}' \
--row_map '{
    "T-R-EACH-FINGER-U-D": "EACH",
    "T-R-INDEX-U-D": "INDEX",
    "Z-R-FIST": "FIST",
    "Z-R-MAYBE-L-M-R-M": "MAYBE",
    "Z-R-OK": "OK",
    "Z-R-SWITCH-AND-BACK": "SW-BACK",
    "Z-R-SWITCH-FIST": "SW-FIST",
    "Z-R-SWITCH-Y180": "SW",
    "Z-R-VICTORIA": "VICT",
    "Z-R-WAVE-L45-M-R45-M": "WAVE"
}' --calc

python cm2tex.py --file data/html.snippet.2025-04-27_20-12-36.10x50.2.html \
--col_map '{
    "T-R-EACH-FINGER-U-D": "EACH",
    "T-R-INDEX-U-D": "INDEX",
    "Z-R-FIST": "FIST",
    "Z-R-MAYBE-L-M-R-M": "MAYBE",
    "Z-R-OK": "OK",
    "Z-R-SWITCH-AND-BACK": "SW-BACK",
    "Z-R-SWITCH-FIST": "SW-FIST",
    "Z-R-SWITCH-Y180": "SW",
    "Z-R-VICTORIA": "VICT",
    "Z-R-WAVE-L45-M-R45-M": "WAVE"
}' \
--row_map '{
    "T-R-EACH-FINGER-U-D": "EACH",
    "T-R-INDEX-U-D": "INDEX",
    "Z-R-FIST": "FIST",
    "Z-R-MAYBE-L-M-R-M": "MAYBE",
    "Z-R-OK": "OK",
    "Z-R-SWITCH-AND-BACK": "SW-BACK",
    "Z-R-SWITCH-FIST": "SW-FIST",
    "Z-R-SWITCH-Y180": "SW",
    "Z-R-VICTORIA": "VICT",
    "Z-R-WAVE-L45-M-R45-M": "WAVE"
}' --calc


python cm2tex.py --file data/html.snippet.2025-04-27_20-12-36.10x50.2.html \
--col_map '{
    "T-R-EACH-FINGER-U-D": "EACH",
    "T-R-INDEX-U-D": "INDEX",
    "Z-R-FIST": "FIST",
    "Z-R-MAYBE-L-M-R-M": "MAYBE",
    "Z-R-OK": "OK",
    "Z-R-SWITCH-AND-BACK": "SW-BACK",
    "Z-R-SWITCH-FIST": "SW-FIST",
    "Z-R-SWITCH-Y180": "SW",
    "Z-R-VICTORIA": "VICT",
    "Z-R-WAVE-L45-M-R45-M": "WAVE"
}' \
--row_map '{
    "T-R-EACH-FINGER-U-D*": "EACH",
    "T-R-INDEX-U-D*": "INDEX",
    "Z-R-FIST.*": "FIST",
    "Z-R-MAYBE-L-M-R-M.*": "MAYBE",
    "Z-R-OK.*": "OK",
    "Z-R-SWITCH-AND-BACK.*": "SW-BACK",
    "Z-R-SWITCH-FIST.*": "SW-FIST",
    "Z-R-SWITCH-Y180.*": "SW",
    "Z-R-VICTORIA.*": "VICT",
    "Z-R-WAVE-L45-M-R45-M.*": "WAVE"
}' --calc



###

http://localhost:8080/gagweb/#!/recognize?refGestureIds=1028,1029,1030,1049,1032,1033,1034,1035,1036,1037,1018,1019,1020,1048,1022,1023,1024,1025,1026,1027,1038,1039,1040,1050,1042,1043,1044,1045,1046,1047&inputGestureIds=515,516,517,518,519,520,521,522,523,524,525,526,527,528,529,530,531,532,533,534,535,536,537,538,539,540,541,542,543,544,545,546,547,548,549,550,551,552,553,554,555,556,557,558,559,560,561,562,563,564,465,466,467,468,469,470,471,472,473,474,475,476,477,478,479,480,481,482,483,484,485,486,487,488,489,490,491,492,493,494,495,496,497,498,499,500,501,502,503,504,505,506,507,508,509,510,511,512,513,514,806,807,808,809,810,811,812,813,814,815,816,817,818,819,820,821,822,823,824,825,826,827,828,829,830,831,832,833,834,835,836,837,838,839,840,841,842,843,844,845,846,847,848,849,850,851,852,853,854,855,916,917,918,919,920,921,922,923,924,925,926,927,928,929,930,931,932,933,934,935,936,937,938,939,940,941,942,943,944,945,946,947,948,949,950,951,952,953,954,955,956,957,958,959,960,961,962,963,964,965,689,690,691,692,693,694,695,696,697,698,699,700,701,702,703,704,705,706,707,708,709,710,711,712,713,714,715,716,717,718,719,720,721,722,723,724,725,726,727,728,729,730,731,732,733,734,735,736,737,738,739,740,741,742,743,744,745,746,747,748,749,750,751,752,753,754,755,756,757,758,766,767,768,769,770,771,772,773,774,775,776,777,778,779,780,781,782,783,784,785,786,787,788,789,790,791,792,793,794,795,866,867,868,869,870,871,872,873,874,875,876,877,878,879,880,881,882,883,884,885,886,887,888,889,890,891,892,893,894,895,896,897,898,899,900,901,902,903,904,905,906,907,908,909,910,911,912,913,914,915,635,636,637,638,639,640,641,642,643,644,645,646,647,648,649,650,651,652,653,654,659,660,661,662,663,664,665,666,667,668,669,670,671,672,673,674,675,676,677,678,679,680,681,682,683,684,685,686,687,688,968,969,970,971,972,973,974,975,976,977,978,979,980,981,982,983,984,985,986,987,988,989,990,991,992,993,994,995,996,997,998,999,1000,1001,1002,1003,1004,1005,1006,1007,1008,1009,1010,1011,1012,1013,1014,1015,1016,1017,566,567,570,571,572,573,577,578,581,582,583,584,585,586,587,588,589,593,594,595,596,597,598,599,600,601,605,606,607,610,611,612,613,614,615,616,617,618,619,620,621,622,623,624,625,626,627,628,629,630



###

python cm2tex.py --file data/html.snippet.2025-04-27_20-12-36.10x50.2.html \
--col_map '{
    "T-R-EACH-FINGER-U-D": "EACH",
    "T-R-INDEX-U-D": "INDEX",
    "Z-R-FIST": "FIST",
    "Z-R-MAYBE-L-M-R-M": "MAYBE",
    "Z-R-OK": "OK",
    "Z-R-SWITCH-AND-BACK": "SW-BACK",
    "Z-R-SWITCH-FIST": "SW-FIST",
    "Z-R-SWITCH-Y180": "SW",
    "Z-R-VICTORIA": "VICT",
    "Z-R-WAVE-L45-M-R45-M": "WAVE"
}' \
--row_map '{
    "T-R-EACH-FINGER-U-D.*": "EACH",
    "T-R-INDEX-U-D.*": "INDEX",
    "Z-R-FIST.*": "FIST",
    "Z-R-MAYBE-L-M-R-M.*": "MAYBE",
    "Z-R-OK.*": "OK",
    "Z-R-SWITCH-AND-BACK.*": "SW-BACK",
    "Z-R-SWITCH-FIST.*": "SW-FIST",
    "Z-R-SWITCH-Y180.*": "SW",
    "Z-R-VICTORIA.*": "VICT",
    "Z-R-WAVE-L45-M-R45-M.*": "WAVE"
}' --calc




python cm2tex.py --file data/html.snippet.2025-04-27_20-12-36.10x50.2.html \
--col_map '{
    "T-R-EACH-FINGER-U-D": "EACH",
    "T-R-INDEX-U-D": "INDEX",
    "Z-R-FIST": "FIST",
    "Z-R-MAYBE-L-M-R-M": "MAYBE",
    "Z-R-OK": "OK",
    "Z-R-SWITCH-AND-BACK": "SW-BACK",
    "Z-R-SWITCH-FIST": "SW-FIST",
    "Z-R-SWITCH-Y180": "SW",
    "Z-R-VICTORIA": "VICT",
    "Z-R-WAVE-L45-M-R45-M": "WAVE"
}' \
--row_map '{
    "T\-R\-EACH\-FINGER\-U\-D.*": "EACH",
    "T-R-INDEX-U-D.*": "INDEX",
    "Z-R-FIST.*": "FIST",
    "Z-R-MAYBE-L-M-R-M.*": "MAYBE",
    "Z-R-OK.*": "OK",
    "Z-R-SWITCH-AND-BACK.*": "SW-BACK",
    "Z-R-SWITCH-FIST.*": "SW-FIST",
    "Z-R-SWITCH-Y180.*": "SW",
    "Z-R-VICTORIA.*": "VICT",
    "Z-R-WAVE-L45-M-R45-M.*": "WAVE"
}' --calc


###




(.venv) vprusa@fedora:~/workspace/p/gag-web/gestures/extract/confusion_matrix_html$ python cm2tex.py --file data/html.snippet.2025-04-27_20-12-36.10x50.2.html \
--col_map '{
    "T-R-EACH-FINGER-U-D": "EACH",
    "T-R-INDEX-U-D": "INDEX",
    "Z-R-FIST": "FIST",
    "Z-R-MAYBE-L-M-R-M": "MAYBE",
    "Z-R-OK": "OK",
    "Z-R-SWITCH-AND-BACK": "SW-BACK",
    "Z-R-SWITCH-FIST": "SW-FIST",
    "Z-R-SWITCH-Y180": "SW-Y180",
    "Z-R-VICTORIA": "VICT",
    "Z-R-WAVE-L45-M-R45-M": "WAVE"
}' \
--row_map '{
    "T-R-INDEX-U-D.*": "INDEX",
    ".*EACH.*":"EACH",
    "Z-R-FIST.*": "FIST",
    "Z-R-MAYBE-L-M-R-M.*": "MAYBE",
    "Z-R-OK.*": "OK",
    "Z-R-SWITCH-AND-BACK": "SW-BACK",
    "Z-R-SWITCH-FIST": "SW-FIST",
    "Z-R-SWITCH-Y180": "SW-Y180",
    "Z-R-VICTORIA": "VICT",
    "Z-R-WAVE-L45-M-R45-M": "WAVE"
}' --calc --ignore_row '1041'


python cm2tex.py --file data/html.snippet.2025-04-27_20-12-36.10x50.2.html \
--col_map '{
    "T-R-EACH-FINGER-U-D": "EACH",
    "T-R-INDEX-U-D": "INDEX",
    "Z-R-FIST": "FIST",
    "Z-R-MAYBE-L-M-R-M": "MAYBE",
    "Z-R-OK": "OK",
    "Z-R-SWITCH-AND-BACK": "SW-BACK",
    "Z-R-SWITCH-FIST": "SW-FIST",
    "Z-R-SWITCH-Y180": "SW-Y180",
    "Z-R-VICTORIA": "VICT",
    "Z-R-WAVE-L45-M-R45-M": "WAVE"
}' \
--row_map '{
    "T-R-INDEX-U-D.*": "INDEX",
    ".*EACH.*":"EACH",
    "Z-R-FIST.*": "FIST",
    "Z-R-MAYBE-L-M-R-M.*": "MAYBE",
    "Z-R-OK.*": "OK",
    "Z-R-SWITCH-AND-BACK": "SW-BACK",
    "Z-R-SWITCH-FIST": "SW-FIST",
    "Z-R-SWITCH-Y180": "SW-Y180",
    "Z-R-VICTORIA": "VICT",
    "Z-R-WAVE-L45-M-R45-M": "WAVE"
}' --calc --ignore_row '1039'




####



http://localhost:8080/gagweb/#!/recognize?refGestureIds=1036,1037,1018,1019,1020,1048,1022,1023,1024,1025,1026,1027,1038,1039,1040,1050,1042,1043,1044,1045,1046,1047&inputGestureIds=515,516,517,518,519,520,521,522,523,524,525,526,527,528,529,530,531,532,533,534,535,536,537,538,539,540,541,542,543,544,545,546,547,548,549,550,551,552,553,554,555,556,557,558,559,560,561,562,563,564,465,466,467,468,469,470,471,472,473,474,475,476,477,478,479,480,481,482,483,484,485,486,487,488,489,490,491,492,493,494,495,496,497,498,499,500,501,502,503,504,505,506,507,508,509,510,511,512,513,514,806,807,808,809,810,811,812,813,814,815,816,817,818,819,820,821,822,823,824,825,826,827,828,829,830,831,832,833,834,835,836,837,838,839,840,841,842,843,844,845,846,847,848,849,850,851,852,853,854,855,916,917,918,919,920,921,922,923,924,925,926,927,928,929,930,931,932,933,934,935,936,937,938,939,940,941,942,943,944,945,946,947,948,949,950,951,952,953,954,955,956,957,958,959,960,961,962,963,964,965,689,690,691,692,693,694,695,696,697,698,699,700,701,702,703,704,705,706,707,708,709,710,711,712,713,714,715,716,717,718,719,720,721,722,723,724,725,726,727,728,729,730,731,732,733,734,735,736,737,738,739,740,741,742,743,744,745,746,747,748,749,750,751,752,753,754,755,756,757,758,766,767,768,769,770,771,772,773,774,775,776,777,778,779,780,781,782,783,784,785,786,787,788,789,790,791,792,793,794,795,866,867,868,869,870,871,872,873,874,875,876,877,878,879,880,881,882,883,884,885,886,887,888,889,890,891,892,893,894,895,896,897,898,899,900,901,902,903,904,905,906,907,908,909,910,911,912,913,914,915,635,636,637,638,639,640,641,642,643,644,645,646,647,648,649,650,651,652,653,654,659,660,661,662,663,664,665,666,667,668,669,670,671,672,673,674,675,676,677,678,679,680,681,682,683,684,685,686,687,688,968,969,970,971,972,973,974,975,976,977,978,979,980,981,982,983,984,985,986,987,988,989,990,991,992,993,994,995,996,997,998,999,1000,1001,1002,1003,1004,1005,1006,1007,1008,1009,1010,1011,1012,1013,1014,1015,1016,1017,566,567,570,571,572,573,577,578,581,582,583,584,585,586,587,588,589,593,594,595,596,597,598,599,600,601,605,606,607,610,611,612,613,614,615,616,617,618,619,620,621,622,623,624,625,626,627,628,629,630







python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gestures 301 --gestures 269 270 271 272 273 274 275 276 277 278  --actual-matches 1 1 1 1 1 1 1 --positions 0 1 2 3 4 5 --angular-diff | tee logs/confusion_matrix.py.x-r-switch.`now_str`.log

python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gestures 74 75 76 --gestures 77 78 79 80 81 82 83 --positions 1 --row-max-col-max



python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gestures 74 75 76 --gestures 77 78 79 80 81 82 83 --positions 1 --calc-all-thresholds



python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gestures 74 75 76 --gestures 77 78 79 80 81 82 83 --positions 1 --calc-all-thresholds


http://localhost:8080/gagweb/#!/recognize?refGestureIds=1036,1037,1018,1019,1020,1048,1022,1023,1024,1025,1026,1027,1038,1039,1040,1050,1042,1043,1044,1045,1046,1047&inputGestureIds=515,516,517,518,519,520,521,522,523,524,525,526,527,528,529,530,531,532,533,534,535,536,537,538,539,540,541,542,543,544,545,546,547,548,549,550,551,552,553,554,555,556,557,558,559,560,561,562,563,564,465,466,467,468,469,470,471,472,473,474,475,476,477,478,479,480,481,482,483,484,485,486,487,488,489,490,491,492,493,494,495,496,497,498,499,500,501,502,503,504,505,506,507,508,509,510,511,512,513,514,806,807,808,809,810,811,812,813,814,815,816,817,818,819,820,821,822,823,824,825,826,827,828,829,830,831,832,833,834,835,836,837,838,839,840,841,842,843,844,845,846,847,848,849,850,851,852,853,854,855,916,917,918,919,920,921,922,923,924,925,926,927,928,929,930,931,932,933,934,935,936,937,938,939,940,941,942,943,944,945,946,947,948,949,950,951,952,953,954,955,956,957,958,959,960,961,962,963,964,965,689,690,691,692,693,694,695,696,697,698,699,700,701,702,703,704,705,706,707,708,709,710,711,712,713,714,715,716,717,718,719,720,721,722,723,724,725,726,727,728,729,730,731,732,733,734,735,736,737,738,739,740,741,742,743,744,745,746,747,748,749,750,751,752,753,754,755,756,757,758,766,767,768,769,770,771,772,773,774,775,776,777,778,779,780,781,782,783,784,785,786,787,788,789,790,791,792,793,794,795,866,867,868,869,870,871,872,873,874,875,876,877,878,879,880,881,882,883,884,885,886,887,888,889,890,891,892,893,894,895,896,897,898,899,900,901,902,903,904,905,906,907,908,909,910,911,912,913,914,915,635,636,637,638,639,640,641,642,643,644,645,646,647,648,649,650,651,652,653,654,659,660,661,662,663,664,665,666,667,668,669,670,671,672,673,674,675,676,677,678,679,680,681,682,683,684,685,686,687,688,968,969,970,971,972,973,974,975,976,977,978,979,980,981,982,983,984,985,986,987,988,989,990,991,992,993,994,995,996,997,998,999,1000,1001,1002,1003,1004,1005,1006,1007,1008,1009,1010,1011,1012,1013,1014,1015,1016,1017,566,567,570,571,572,573,577,578,581,582,583,584,585,586,587,588,589,593,594,595,596,597,598,599,600,601,605,606,607,610,611,612,613,614,615,616,617,618,619,620,621,622,623,624,625,626,627,628,629,630



1028,1029,1030,1049,1032,1033,1034,1035,1036,1037,1018,1019,1020,1048,1022,1023,1024,1025,1026,1027,1038,1039,1040,1050,1042,1043,1044,1045,1046,1047


python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gestures 74 75 76 --gestures 77 78 79 80 81 82 83 --positions 1 --calc-all-thresholds



#

python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gestures 465,466,467,468,469 --gestures 77 78 79 80 81 82 83 --positions 1 --calc-all-thresholds

#


python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gestures 465,466,467,468,469 --gestures 77 78 79 80 81 82 83 --positions 1 --calc-all-thresholds

python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gestures 465,466,467,468,469 --gestures 470,471,472,473,474 --positions 0 1 2 3 4 5 --calc-all-thresholds



470,471,472,473,474,475,476,477,478,479,480,481,482,483,484,485,486,487,488,489,490,491,492,493,494,495,496,497,498,499,500,501,502,503,504,505,506,507,508,509,510,511,512,513,514

465,466,467,468,469,470,471,472,473,474,475,476,477,478,479,480,481,482,483,484,485,486,487,488,489,490,491,492,493,494,495,496,497,498,499,500,501,502,503,504,505,506,507,508,509,510,511,512,513,514

470,471,472,473,474,475,476,477,478,479,480,481,482,483,484,485,486,487,488,489,490,491,492,493,494,495,496,497,498,499,500,501,502,503,504,505,506,507,508,509,510,511,512,513,514

465,466,467,468,469,470,471,472,473,474,475,476,477,478,479,480,481,482,483,484,485,486,487,488,489,490,491,492,493,494,495,496,497,498,499,500,501,502,503,504,505,506,507,508,509,510,511,512,513,514,515,516,517,518,519,520,521,522,523,524,525,526,527,528,529,530,531,532,533,534,535,536,537,538,539,540,541,542,543,544,545,546,547,548,549,550,551,552,553,554,555,556,557,558,559,560,561,562,563,564,566,567,570,571,572,573,577,578,581,582,583,584,585,586,587,588,589,593,594,595,596,597,598,599,600,601,605,606,607,610,611,612,613,614,615,616,617,618,619,620,621,622,623,624,625,626,627,628,629,630,635,636,637,638,639,640,641,642,643,644,645,646,647,648,649,650,651,652,653,654,659,660,661,662,663,664,665,666,667,668,669,670,671,672,673,674,675,676,677,678,679,680,681,682,683,684,685,686,687,688,689,690,691,692,693,694,695,696,697,698,699,700,701,702,703,704,705,706,707,708,709,710,711,712,713,714,715,716,717,718,719,720,721,722,723,724,725,726,727,728,729,730,731,732,733,734,735,736,737,738,739,740,741,742,743,744,745,746,747,748,749,750,751,752,753,754,755,756,757,758,766,767,768,769,770,771,772,773,774,775,776,777,778,779,780,781,782,783,784,785,786,787,788,789,790,791,792,793,794,795,806,807,808,809,810,811,812,813,814,815,816,817,818,819,820,821,822,823,824,825,826,827,828,829,830,831,832,833,834,835,836,837,838,839,840,841,842,843,844,845,846,847,848,849,850,851,852,853,854,855,866,867,868,869,870,871,872,873,874,875,876,877,878,879,880,881,882,883,884,885,886,887,888,889,890,891,892,893,894,895,896,897,898,899,900,901,902,903,904,905,906,907,908,909,910,911,912,913,914,915,916,917,918,919,920,921,922,923,924,925,926,927,928,929,930,931,932,933,934,935,936,937,938,939,940,941,942,943,944,945,946,947,948,949,950,951,952,953,954,955,956,957,958,959,960,961,962,963,964,965,968,969,970,971,972,973,974,975,976,977,978,979,980,981,982,983,984,985,986,987,988,989,990,991,992,993,994,995,996,997,998,999,1000,1001,1002,1003,1004,1005,1006,1007,1008,1009,1010,1011,1012,1013,1014,1015,1016,1017,


#

python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gestures 465 466 467 468 469 --gestures 470 471 472 473 474 --positions 0 1 2 3 4 5 --calc-all-thresholds

{'row_avg_col_avg': {1: np.float64(0.19647021940595796)},
 'row_avg_col_max': {1: np.float64(0.265713209103347)},
 'row_max_col_avg': {1: np.float64(0.2893805958620389)},
 'row_max_col_max': {1: np.float64(0.3951823357221588)}}

📋 Summary of thresholds:
row_max_col_max -> avg: 0.395182, max: 0.395182
row_max_col_avg -> avg: 0.289381, max: 0.289381
row_avg_col_max -> avg: 0.265713, max: 0.265713
row_avg_col_avg -> avg: 0.196470, max: 0.196470


#


{'row_avg_col_avg': {0: np.float64(0.0434273317590069),
                     1: np.float64(0.1919215344302816),
                     2: np.float64(0.03721470518903328),
                     3: np.float64(0.12893637222857351),
                     4: np.float64(0.014723640738455215),
                     5: np.float64(0.025122426623409157)},
 'row_avg_col_max': {0: np.float64(0.3882699538563683),
                     1: np.float64(1.0415207724944024),
                     2: np.float64(0.053595039025063795),
                     3: np.float64(1.6827131393003447),
                     4: np.float64(0.02077169203842125),
                     5: np.float64(0.03777276604516164)},
 'row_max_col_avg': {0: np.float64(0.12217880626932899),
                     1: np.float64(0.4552045908976639),
                     2: np.float64(0.07270353800234741),
                     3: np.float64(0.21386155171630444),
                     4: np.float64(0.02334963018661211),
                     5: np.float64(0.03939530907462437)},
 'row_max_col_max': {0: np.float64(1.7360242966919406),
                     1: np.float64(2.6140085151640498),
                     2: np.float64(0.11878418643999862),
                     3: np.float64(2.823159209254023),
                     4: np.float64(0.03401813479758435),
                     5: np.float64(0.07907040153741643)}}

📋 Summary of thresholds:
row_max_col_max -> avg: 1.234177, max: 2.823159
row_max_col_avg -> avg: 0.154449, max: 0.455205
row_avg_col_max -> avg: 0.537441, max: 1.682713
row_avg_col_avg -> avg: 0.073558, max: 0.191922


python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gestures 465 466 467 468 469 --gestures 470 471 472 473 474 --positions 0 1 2 3 4 5 --calc-all-thresholds

#


python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gestures 465 466 467 468 469 --gestures 470 471 472 473 474 --positions 0 1 2 3 4 5 --row-avg-col-avg --save-ref-gesture TEST-465--469-1

465,466,467,468,469

for i in 470 471 472 473 474 ; do echo $i ; python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "$i" --threshold-recognition 0.1 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_xnth3_wswe_tr_0.1_for_avg ; done | tee logs/gesture_data_extractor.2.py.`now_str`.log 


for i in 470 471 472 473 474 ; do echo $i ; python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "$i" --threshold-recognition 0.1 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_xnth3_wswe_tr_0.1_for_avg ; done | tee logs/gesture_data_extractor.2.py.`now_str`.log 


for i in 470 471 472 473 474 ; do echo $i ; python gesture_data_extractor.2.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --gesture_id "$i" --threshold-recognition 0.1 --position 0 1 2 3 4 5 --start --end --align xnth:4 --align-find top -v --suffix p_012345_xnth3_wswe_tr_0.1_for_avg ; done | tee logs/gesture_data_extractor.2.py.`now_str`.log 


1052,1053,1054,1055,1056,1057,1058,1059,1060,1061

1052,1053,1054,1055,1056

1057 1058 1059 1060 1061


python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gestures 1052 1053 1054 1055 1056 --gestures 1057 1058 1059 1060 1061 --positions 0 1 2 3 4 5 --row-avg-col-avg --save-ref-gesture TEST-1052--1056-1



python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gestures 1052 1053 1054 1055 1056 --gestures 1057 1058 1059 1060 1061 --positions 0 1 2 3 4 5 --row-avg-col-avg --save-ref-gesture T-R-INDEX-U-D_1--10_1052--1056-1



python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gestures 1052 1053 1054 1055 1056 --gestures 1057 1058 1059 1060 1061 --positions 0 1 2 3 4 5 --row-avg-col-avg --save-ref-gesture T-R-INDEX-U-D_1--10_1052--1056

python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gestures 1052 1053 1054 1055 1056 --gestures 1057 1058 1059 1060 1061 --positions 0 1 2 3 4 5 --row-avg-col-max --save-ref-gesture T-R-INDEX-U-D_1--10_1052--1056

python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gestures 1052 1053 1054 1055 1056 --gestures 1057 1058 1059 1060 1061 --positions 0 1 2 3 4 5 --row-max-col-avg --save-ref-gesture T-R-INDEX-U-D_1--10_1052--1056

python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gestures 1052 1053 1054 1055 1056 --gestures 1057 1058 1059 1060 1061 --positions 0 1 2 3 4 5 --row-max-col-max --save-ref-gesture T-R-INDEX-U-D_1--10_1052--1056




465,466,467,468,469,470,471,472,473,474,


python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gestures 1052 1053 1054 1055 1056 --gestures 1057 1058 1059 1060 1061 --positions 0 1 2 3 4 5 --row-avg-col-avg --save-ref-gesture T-R-INDEX-U-D_1--10_1052--1056

{0: np.float64(0.027371410183569953),
 1: np.float64(0.28749848589105026),
 2: np.float64(0.019557375378325848),
 3: np.float64(0.04672735953684303),
 4: np.float64(0.21380534269497165),
 5: np.float64(0.02421296033368497)}
avg: 0.103195, max: 0.287498


python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gestures 1052 1053 1054 1055 1056 --gestures 1057 1058 1059 1060 1061 --positions 0 1 2 3 4 5 --row-avg-col-max --save-ref-gesture T-R-INDEX-U-D_1--10_1052--1056

{0: np.float64(0.028198285963699755),
 1: np.float64(1.087652919523505),
 2: np.float64(0.034415673431059765),
 3: np.float64(0.04793417542041917),
 4: np.float64(1.0266554567879003),
 5: np.float64(0.03743323042487592)}
avg: 0.377048, max: 1.087653


python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gestures 1052 1053 1054 1055 1056 --gestures 1057 1058 1059 1060 1061 --positions 0 1 2 3 4 5 --row-max-col-avg --save-ref-gesture T-R-INDEX-U-D_1--10_1052--1056

{0: np.float64(0.04468336966045801),
 1: np.float64(0.6636186381245919),
 2: np.float64(0.039515763012066746),
 3: np.float64(0.07858571603148626),
 4: np.float64(0.5388510505399333),
 5: np.float64(0.04625860239192554)}
avg: 0.235252, max: 0.663619


python confusion_matrix.py --host "localhost" --user "gagweb" --password "password" --database "gagweb" --ref-gestures 1052 1053 1054 1055 1056 --gestures 1057 1058 1059 1060 1061 --positions 0 1 2 3 4 5 --row-max-col-max --save-ref-gesture T-R-INDEX-U-D_1--10_1052--1056


{0: np.float64(0.04698220373368812),
 1: np.float64(2.682652545703201),
 2: np.float64(0.07922723611635628),
 3: np.float64(0.082280025638273),
 4: np.float64(2.610103992091962),
 5: np.float64(0.08669994903006124)}
avg: 0.931324, max: 2.682653


#
