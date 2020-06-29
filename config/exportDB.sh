#!/bin/bash
THIS_DIR=`dirname $0`

source ${THIS_DIR}/DBInfo.sh

DB_DUMP_PATH=${DB_NAME}.dump.sql
mysqldump -u gagweb --password=${DB_PASSWORD} ${DB_NAME} > ${DB_DUMP_PATH}
DB_DUMPS_DIR=${DB_NAME}-db-dumps
NOW_NAME=`date "+%Y-%M-%d_%H-%M-%S"`
mkdir ${DB_DUMPS_DIR}
cp -r ${DB_DUMP_PATH} ${DB_DUMPS_DIR}/${DB_NAME}-${NOW_NAME}.dump.sql
