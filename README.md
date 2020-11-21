# Web app for (Gyro-Accelerometric|Gestures Automation) Glove

### Building/Running/Deploying

```
mvn wildfly:start wildfly:undeploy install wildfly:deploy
```

or

```
mvn wildfly:start wildfly:undeploy install wildfly:deploy -DskipTests=true -Dcheckstyle.skip | tee app.log
```

or to re-deploy on running server:

```
mvn wildfly:undeploy install wildfly:deploy -DskipTests=true -Dcheckstyle.skip 
```

#### Frontend 

In dir 
```
./app
```

#### Tests

##### WS DataLine on Running WS

```
mvn dependency:get -Ddest=./ -Dartifact=mysql:mysql-connector-java:8.0.17 && mvn clean wildfly:undeploy install wildfly:deploy -DskipTests=true -Dcheckstyle.skip && mvn test -Dtest=WebsocketDatalineEndpointTest -DfailIfNoTests=false -Dcheckstyle.skip | tee app2.log
```

#### zip 

```

zip gag-web.zip -r ./pom.xml  ./config presentation.odp ./persistence/pom.xml ./persistence/src/main/java/ ./persistence/src/main/resources/ ./services/pom.xml ./services/src/test/ ./services/src/main/resources/ ./services/src/main/java ./app/package* ./app/pom.xml ./app/gulpfile.js ./app/bower.json ./app/ ./app/live/gag-web-app-1.0-SNAPSHOT.WAR/ ./app/live/gag-web-app-1.0-SNAPSHOT.WAR/css/ ./app/live/gag-web-app-1.0-SNAPSHOT.WAR/index.html ./app/live/gag-web-app-1.0-SNAPSHOT.WAR/login ./app/live/gag-web-app-1.0-SNAPSHOT.WAR/js ./app/live/gag-web-app-1.0-SNAPSHOT.WAR/partials ./app/live/gag-web-app-1.0-SNAPSHOT.WAR/public  ./app/live/gag-web-app-1.0-SNAPSHOT.WAR/WEB-INF/ ./app/live/gag-web-app-1.0-SNAPSHOT.WAR/META-INF/ ./app/live/gag-web-app-1.0-SNAPSHOT.WAR/WEB-INF/beans.xml ./app/live/gag-web-app-1.0-SNAPSHOT.WAR/WEB-INF/jboss-web.xml ./app/live/gag-web-app-1.0-SNAPSHOT.WAR/WEB-INF/keycloak.json ./app/live/gag-web-app-1.0-SNAPSHOT.WAR/WEB-INF/web.xml ./app/live/gag-web-app-1.0-SNAPSHOT.WAR/components/ ./app/src/test/java/ ./app/src/test/resources ./app/src/main/resources/ ./app/src/main/webapp/ -x ./app/src/main/webapp/assets\* -x ./app/src/main/webapp/material-dashboard-master\* -x app/bower_components\* -x app/live/gag-web-app-1.0-SNAPSHOT.WAR/vendor\* -x app/live/gag-web-app-1.0-SNAPSHOT.WAR/assets\* -x app/node_modules\* -x \*WEB-INF/lib\* -x \*/target\* -x app/material-dashboard-master\* -x app/.settings\* -x app/src/main/webapp/vendor\* -x app/src/main/webapp/theme\* .gitignore .gitlab-ci.yml

```


```
cd ./persistence && mvn clean install -Dcheckstyle.skip -DskipTests=true && cd ../services && mvn clean install -Dcheckstyle.skip -DskipTests=true && cd ../app && mvn clean install -Dcheckstyle.skip -DskipTests=true && cd .. && mvn wildfly:undeploy install wildfly:deploy -Dcheckstyle.skip -DskipTests=true && mvn test -Dtest=WSDataLineEndpointTest -DfailIfNoTests=false 
```

### FAQ

Q: What?

A: This is not a web app, this is a monolithic desktop app that runs locally on Wildfly and scala's code could be used as library for other stuff (like VR on Android).

-------------------

Q: Why?

A: This began as web app so I would have another demo app and pass university's course and had backend for data.
