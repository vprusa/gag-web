#!/bin/bash
# prepare db

DB_NAME="gagweb"
DB_USER="gagweb"
DB_PASSWORD="password"

mvn dependency:get -Ddest=./ -Dartifact=mysql:mysql-connector-java:8.0.17

sudo yum install mariadb-server
sudo systemctl start mariadb
sudo systemctl enable mariadb
sudo systemctl status mariadb

echo "[mysql]" > ~/.my.cfg
echo "user=${DB_NAME}" >> ~/.my.cfg
echo "database=${DB_USER}" >> ~/.my.cfg
echo "password=${DB_PASSWORD}" >> ~/.my.cfg

Q_CREATE_USER="GRANT ALL PRIVILEGES ON *.* TO '${DB_PASSWORD}'@'localhost' IDENTIFIED BY '${DB_PASSWORD}';"
echo "Q_CREATE_USER: ${Q_CREATE_USER}"
Q_CREATE_DB="CREATE DATABASE ${DB_NAME};"
echo "Q_CREATE_DB: ${Q_CREATE_DB}"

mysql -u gagweb -e "${Q_CREATE_USER}"
mysql -u gagweb -e "${Q_CREATE_DB}"
