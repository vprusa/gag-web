#!/bin/bash
THIS_DIR=`dirname $0`
source ${THIS_DIR}/DBInfo.sh
DB_DUMP_PATH=${THIS_DIR}/gagweb.dump.sql
cat ${DB_DUMP_PATH} | mysql -u ${DB_USER} --password=${DB_PASSWORD} -h localhost ${DB_NAME}
