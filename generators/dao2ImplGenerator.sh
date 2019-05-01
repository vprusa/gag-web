#!/bin/bash
# execute all from ./generators/

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

echo $DIR

cd ../persistence/src/main/java/cz/muni/fi/gag/web/entity/
for f in `ls` ; do
  f=${f/.java/}
  echo ${f};
  #echo ../dao/${f}Dao.java
  if [ -f ../dao/${f}Dao.java ] ; then
    echo "exists.. cont.." ;
    continue;
  fi;
  sed "s/{{tablename}}/${f}/g" ${DIR}/DaoTemplate.notjava > ./../dao/${f}Dao.java

  sed "s/{{tablename}}/${f}/g" ${DIR}/DaoImplTemplate.notjava > ./../dao/impl/${f}DaoImpl.java
done
