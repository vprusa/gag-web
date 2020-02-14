#!/bin/bash
cp -r ./keycloak-wildfly-adapter/modules/* ./wildfly-18.0.0.Final/modules
./wildfly-18.0.0.Final/bin/jboss-cli.sh --file=./keycloak-wildfly-adapter/bin/adapter-install-offline.cli
#./keycloak-6.0.1/bin/standalone.sh & disown ;
#./wildfly-16.0.0.Final/bin/jboss-cli.sh --file=./keycloak-wildfly-adapter/bin/adapter-install-offline.cli

# data-source add --name=MySqlDS --jndi-name=java:/MySqlDS --driver-name=gag-web-app-1.0-SNAPSHOT.WAR_com.mysql.cj.jdbc.Driver_8_0 --connection-url=jdbc:mysql://localhost:3306/gagweb --user-name=gagweb --password=password
#./wildfly-16.0.0.Final/bin/jboss-cli.sh  -c 'data-source add --name=MySqlDS --jndi-name=java:/MySqlDS --driver-name=gag-web-app-1.0-SNAPSHOT.WAR_com.mysql.cj.jdbc.Driver_8_0 --connection-url=jdbc:mysql://localhost:3306/gagweb --user-name=gagweb --password=password'

DS_SNIPPET='
<datasource jndi-name="java:/MySqlDS" pool-name="MySqlDS">
    <connection-url>jdbc:mysql://localhost:3306/gagweb</connection-url>
    <driver-class>com.mysql.cj.jdbc.Driver</driver-class>
    <connection-property name="useUnicode">
        true
    </connection-property>
    <connection-property name="characterEncoding">
        utf8
    </connection-property>
    <connection-property name="useSSL">
        false
    </connection-property>
    <connection-property name="useLegacyDatetimeCode">
        false
    </connection-property>
    <connection-property name="serverTimezone">
        UTC
    </connection-property>
    <driver>gag-web-app-1.0-SNAPSHOT.WAR_com.mysql.cj.jdbc.Driver_8_0</driver>
    <security>
        <user-name>gagweb</user-name>
        <password>password</password>
    </security>
    <validation>
        <valid-connection-checker class-name="org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLValidConnectionChecker"/>
        <background-validation>true</background-validation>
        <exception-sorter class-name="org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLExceptionSorter"/>
    </validation>
</datasource>
';
'
<datasource jndi-name="java:/MariaDBDS" pool-name="MariaDBDS">
    <connection-url>jdbc:mariadb://localhost:3306/gagweb?allowPublicKeyRetrieval=true</connection-url>
    <driver-class>org.mariadb.jdbc.Driver</driver-class>
    <connection-property name="useUnicode">
        true
    </connection-property>
    <connection-property name="characterEncoding">
        utf8
    </connection-property>
    <connection-property name="useSSL">
        false
    </connection-property>
    <connection-property name="useLegacyDatetimeCode">
        false
    </connection-property>
    <connection-property name="serverTimezone">
        UTC
    </connection-property>
    <!--<driver>gag-web-app-1.0-SNAPSHOT.WAR_com.mysql.cj.jdbc.Driver_8_0</driver>-->
    <driver>mariadb-java-client-2.3.0.jar</driver>
    <security>
        <user-name>gagweb</user-name>
        <password>password</password>
    </security>
    <validation>
        <valid-connection-checker class-name="org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLValidConnectionChecker"/>
        <background-validation>true</background-validation>
        <exception-sorter class-name="org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLExceptionSorter"/>
    </validation>
</datasource>
'
