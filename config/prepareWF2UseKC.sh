#!/bin/bash
cp -r ./keycloak-wildfly-adapter/modules/* ./wildfly-16.0.0.Final/modules 
./wildfly-16.0.0.Final/bin/jboss-cli.sh --file=./keycloak-wildfly-adapter/bin/adapter-install-offline.cli
#./keycloak-6.0.1/bin/standalone.sh & disown ;
