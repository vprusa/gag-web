# gesture_groups=(
# "T-R-EACH-FINGER-U-D"
# "T-R-INDEX-U-D"
# "Z-R-FIST"
# "Z-R-MAYBE-L-M-R-M"
# "Z-R-OK"
# "Z-R-SWITCH-AND-BACK"
# "Z-R-SWITCH-FIST"
# "Z-R-SWITCH-Y180"
# "T-R-VICTORIA"
# "Z-R-WAVE-L45-M-R45-M"
# )

gesture_groups=(
  "T-R-EACH-FINGER-U-D"
)

# --- DB login info ---
DBHOST="localhost"
DBUSER="gagweb"
DBPASS="password"
DBNAME="gagweb"

POSITIONS="0 1 2 3 4 5"
THRESHOLD_EXTRACTION=0.01
THRESHOLD_RECOGNITION=0.8

for prefix in "${gesture_groups[@]}"; do
  # 1. Get first 5 gesture ids for the group
  gesture_ids=$(mysql -u $DBUSER --password=$DBPASS $DBNAME -N -e \
    "SELECT id FROM Gesture g1 WHERE userAlias REGEXP '^${prefix}_[0-9]+$' AND id = (SELECT MAX(id) FROM Gesture g2 WHERE g2.userAlias = g1.userAlias) ORDER BY CAST(SUBSTRING_INDEX(userAlias, '_', -1) AS UNSIGNED) ASC LIMIT 5;")
  
  if [[ -z "$gesture_ids" ]]; then
    echo "No gestures found for group: $prefix"
    continue
  fi

  echo "Processing group: $prefix   Original IDs: $gesture_ids"

  new_gesture_ids=()
  # 2. For each gesture id, create processed gesture and collect new IDs
  for gid in $gesture_ids; do
    # Suffix for each processed gesture
    newsuffix="${prefix}_$gid"
    # Run gesture_data_extractor.2.py and capture the new gesture id from its output
    new_id=$(source .venv/bin/activate && python gesture_data_extractor.2.py \
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
      --align-find "top" | \
      grep -oP 'extracted points under new gesture ID: \K[0-9]+')
    if [[ -n "$new_id" ]]; then
      new_gesture_ids+=("$new_id")
      echo "  Processed $gid -> New ID $new_id"
    else
      echo "  Processing $gid failed or did not create new gesture."
    fi
  done

  # If less than 2 processed gestures, skip
  if (( ${#new_gesture_ids[@]} < 2 )); then
    echo "Not enough processed gestures for group: $prefix"
    continue
  fi

  # 3. Build suffix for referential gesture
  min_new=${new_gesture_ids[0]}
  max_new=${new_gesture_ids[-1]}
  save_suffix="${prefix}_p_all_xnth3_wswe_${min_new}__${max_new}"

  # 4. Call utils.py to create referential gesture
  echo "Creating referential gesture for group $prefix:"
  echo "python utils.py --host \"$DBHOST\" --user \"$DBUSER\" --password \"$DBPASS\" --database \"$DBNAME\" --ref-gestures ${new_gesture_ids[*]} --positions $POSITIONS --calc-all-thresholds -v --save-ref-gesture $save_suffix"
  source .venv/bin/activate && python utils.py --host "$DBHOST" --user "$DBUSER" --password "$DBPASS" --database "$DBNAME" \
    --ref-gestures ${new_gesture_ids[*]} \
    --positions $POSITIONS \
    --calc-all-thresholds -v \
    --save-ref-gesture "$save_suffix"
done

