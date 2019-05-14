#!/bin/bash
# execute all from ./generators/

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
echo $DIR

cd ../persistence/src/main/java/cz/muni/fi/gag/web/entity/

# cz.muni.fi.gag.web.rest.endpoint
REST_PKGS_DIR=$DIR"/../services/src/main/java/cz/muni/fi/gag/web/rest/endpoint"
#SERVICES_TEST_PKGS_DIR=$DIR"/../services/src/test/java/cz/muni/fi/gag/web/service/"
# batching  controller  rest  service  websocket
# batching/job
# rest/endpoint
# service/generic
# websocket/service
# websocket/endpoint

for f in `ls` ; do
  f=${f/.java/}

  #echo ../dao/${f}Dao.java
  if [[ $f == *"AbstractEntity"* ]] || [[ $f == *"FingerPosition"* ]] || [[ $f == *"UserType"* ]] || [[ $f == *"FingerDataLine"* ]] || [[ $f == *"SensorType"* ]] || [[ $f == *"User"* ]]; then
    echo "${f} - Skipping...";
    continue;
  fi
  echo "${f}";
  #continue;

  sed "s/{{LLtablename}}/${f,,}/g" ${DIR}/RestTemplate.notjava > ${REST_PKGS_DIR}/${f}Endpoint.java
  sed -i "s/{{Ltablename}}/${f,}/g" ${REST_PKGS_DIR}/${f}Endpoint.java
  sed -i "s/{{tablename}}/${f}/g" ${REST_PKGS_DIR}/${f}Endpoint.java

done
