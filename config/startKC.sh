#!/bin/bash
./keycloak-6.0.1/bin/standalone.sh -Djboss.socket.binding.port-offset=100 -Dkeycloak.migration.action=import -Dkeycloak.migration.provider=singleFile -Dkeycloak.migration.file=/home/vprusa/workspace/p/notes/work/projects/arduino/gag/gag-web/config/google-identity-provider-realm.json -Dkeycloak.migration.strategy=OVERWRITE_EXISTING
