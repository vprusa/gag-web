#!/bin/bash



# If GESTURE_IDS is not empty, it should be an array of strings, 
# each string is space-separated IDs for each group (can be more than REF_GESTURES_CNT).
# Example:
# GESTURE_IDS=(
#   "101 102 103 104 105 106 107"
#   "201 202 203 204 205 206"
#   ...
# )
# GESTURE_IDS=()


#gesture_groups=(
#  "T-R-INDEX-U-D"
#)


# gesture_groups=(
#   "T-R-EACH-FINGER-U-D"
#   "T-R-INDEX-U-D"
#   "Z-R-FIST"
#   "Z-R-MAYBE-L-M-R-M"
#   "Z-R-OK"
#   "Z-R-SWITCH-AND-BACK"
#   "Z-R-SWITCH-FIST"
#   "Z-R-SWITCH-Y180"
#   "Z-R-VICTORIA"
#   "Z-R-WAVE-L45-M-R45-M"
# )

# REF_GESTURES_CNT=3


# GESTURE_IDS=(
#   "515 516 517"
#   "465 466 467"
#   "806 807 808"
#   "916 917 918"
#   "689 690 691"
#   "739 740 741"
#   "866 867 868"
#   "635 636 637"
#   "968 969 970"
#   "566 567 570"
# )


gesture_groups=(
  "T-R-INDEX-U-D"
  "Z-R-SWITCH-FIST"
  "Z-R-VICTORIA"
)

REF_GESTURES_CNT=3

# 1264: T-R-INDEX-U-D_p_all_xnth3_wswe_1258__1260_row-avg-col-avg 
# 1299: Z-R-SWITCH-FIST_p_all_xnth3_wswe_1293__1295_row-avg-col-avg 
# 1313: Z-R-VICTORIA_p_all_xnth3_wswe_1307__1309_row-avg-col-avg 

GESTURE_IDS=(
  "467 468 469"
  "868 870 872"
  "968 969 974"
)

DBHOST="localhost"
DBUSER="gagweb"
DBPASS="password"
DBNAME="gagweb"

POSITIONS="0 1 2 3 4 5"
THRESHOLD_EXTRACTION=0.01
THRESHOLD_RECOGNITION=0.5

for idx in "${!gesture_groups[@]}" ; do
  prefix="${gesture_groups[$idx]}"

  # If GESTURE_IDS is set, use it; else do MySQL query
  if [[ ${#GESTURE_IDS[@]} -gt 0 ]]; then
    # The string for this group
    all_ids_string="${GESTURE_IDS[$idx]}"
    # Take only first $REF_GESTURES_CNT ids
    gesture_ids=$(echo $all_ids_string | awk -v N=$REF_GESTURES_CNT '{for(i=1;i<=NF && i<=N;i++) printf "%s ", $i}')
    gesture_ids=$(echo $gesture_ids) # remove trailing space
  else
    gesture_ids=$(mysql -u $DBUSER --password=$DBPASS $DBNAME -N -e \
      "SELECT id FROM Gesture g1 WHERE userAlias REGEXP '^${prefix}_[0-9]+$' AND id = (SELECT MAX(id) FROM Gesture g2 WHERE g2.userAlias = g1.userAlias) ORDER BY CAST(SUBSTRING_INDEX(userAlias, '_', -1) AS UNSIGNED) ASC LIMIT $REF_GESTURES_CNT;")
  fi

  if [[ -z "$gesture_ids" ]]; then
    echo "No gestures found for group: $prefix"
    continue
  fi

  echo "Processing group: $prefix   IDs: $gesture_ids"

  new_gesture_ids=()
  for gid in $gesture_ids; do
    newsuffix="${prefix}_$gid"
    new_id=$(python gesture_data_extractor.2.py \
      --host "$DBHOST" \
      --user "$DBUSER" \
      --password "$DBPASS" \
      --database "$DBNAME" \
      --gesture_id $gid \
      --threshold-extraction $THRESHOLD_EXTRACTION \
      --threshold-recognition $THRESHOLD_RECOGNITION \
      --position $POSITIONS \
      --suffix "$newsuffix" \
      --start --end \
      --align "xnth:4" \
      --align-find "top" | grep -oP 'extracted points under new gesture ID: \K[0-9]+')
    if [[ -n "$new_id" ]]; then
      new_gesture_ids+=("$new_id")
      echo "  Processed $gid -> New ID $new_id"
    else
      echo "  Processing $gid failed or did not create new gesture."
    fi
  done

  if (( ${#new_gesture_ids[@]} < 2 )); then
    echo "Not enough processed gestures for group: $prefix"
    continue
  fi

  min_new=${new_gesture_ids[0]}
  max_new=${new_gesture_ids[-1]}
  save_suffix="${prefix}_p_all_xnth3_wswe_${min_new}__${max_new}"

  echo "Creating referential gesture for group $prefix:"
  echo "python utils.py --host \"$DBHOST\" --user \"$DBUSER\" --password \"$DBPASS\" --database \"$DBNAME\" --ref-gestures ${new_gesture_ids[*]} --positions $POSITIONS --calc-all-thresholds --use-max-threshold -v --save-ref-gesture $save_suffix"
  python utils.py --host "$DBHOST" --user "$DBUSER" --password "$DBPASS" --database "$DBNAME" \
    --ref-gestures ${new_gesture_ids[*]} \
    --positions $POSITIONS \
    --calc-all-thresholds --use-max-threshold -v \
    --save-ref-gesture "$save_suffix"
done
