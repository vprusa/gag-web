#!/bin/bash
# execute all from ./generators/

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
echo $DIR

cd ../persistence/src/main/java/cz/muni/fi/gag/web/entity/

SERVICES_PKGS_DIR=$DIR"/../services/src/main/java/cz/muni/fi/gag/web/"

# batching  controller  rest  service  websocket
# batching/job
# rest/endpoint
# service/generic
# websocket/service
# websocket/endpoint

for f in `ls` ; do
  f=${f/.java/}
  echo ${f};
  #echo ../dao/${f}Dao.java

  #lower
  sed "s/{{Ltablename}}/${f,}/g" ${DIR}/ServiceTemplate.notjava > ${SERVICES_PKGS_DIR}/service/${f}Service.java
  sed "s/{{Ltablename}}/${f,}/g" ${DIR}/ServiceImplTemplate.notjava > ${SERVICES_PKGS_DIR}/service/impl/${f}ServiceImpl.java

  # default with first capital..
  sed -i "s/{{tablename}}/${f}/g" ${SERVICES_PKGS_DIR}/service/${f}Service.java #> ./../dao/${f}Dao.java
  sed -i "s/{{tablename}}/${f}/g" ${SERVICES_PKGS_DIR}/service/impl/${f}ServiceImpl.java #> ./../dao/impl/${f}DaoImpl.java
done
