#!/bin/bash
THIS_DIR=`dirname $0`

sudo dnf install maven nodejs -y

SCALA_PROJ_HOME=${THIS_DIR}/../scala/

SBT_VERSION=`cat ${SCALA_PROJ_HOME}/project/build.properties | sed 's/sbt.version=//'`

curl https://bintray.com/sbt/rpm/rpm | sudo tee /etc/yum.repos.d/bintray-sbt-rpm.repo
sudo dnf remove sbt -y # uninstalling sbt if sbt 0.13 was installed (may not be necessary)
sudo dnf --enablerepo=bintray--sbt-rpm install sbt -y

SCALA_VERSION=`cat ${THIS_DIR}/build.sbt | grep -v '//' | grep scalaVersion | head -n1 | sed 's/.*\"\(.*\)\",/\1/'`
#export SCALA_VERSION="2.11.12"
wget https://downloads.lightbend.com/scala/${SCALA_VERSION}/scala-${SCALA_VERSION}.rpm
sudo dnf install scala-${SCALA_VERSION}.rpm -y

#cd ${THIS_DIR}/../scala/
#sbt clean fastOptJSO

${THIS_DIR}/prepareDB.sh
