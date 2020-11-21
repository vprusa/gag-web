#!/bin/bash
# prepare db

THIS_DIR=`dirname $0`

source ${THIS_DIR}/

sudo yum install mariadb-server -y
sudo systemctl start mariadb
sudo systemctl enable mariadb
#sudo systemctl status mariadb

mvn dependency:get -Ddest=./ -Dartifact=mysql:mysql-connector-java:8.0.17

echo "[mysql]" > ~/.my.cfg
echo "user=${DB_NAME}" >> ~/.my.cfg
echo "database=${DB_USER}" >> ~/.my.cfg
echo "password=${DB_PASSWORD}" >> ~/.my.cfg

Q_CREATE_USER="GRANT ALL PRIVILEGES ON ${DB_NAME}.* TO '${DB_USER}'@'localhost' IDENTIFIED BY '${DB_PASSWORD}';"
echo "Q_CREATE_USER: ${Q_CREATE_USER}"
Q_CREATE_DB="CREATE DATABASE ${DB_NAME};"
echo "Q_CREATE_DB: ${Q_CREATE_DB}"

mysql -u root -e "${Q_CREATE_USER}"
mysql -u root -e "${Q_CREATE_DB}"
