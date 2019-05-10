# Web app for (Gyro-Accelerometric|Gestures Automation) Glove

### Building/Running/Deploying

```
mvn  wildfly:start wildfly:undeploy install wildfly:deploy
```

or

```
mvn  wildfly:start wildfly:undeploy install wildfly:deploy -DskipTests=true -Dcheckstyle.skip | tee app.log
```

#### Frontend 

In dir 
```
./app
```


Dir 
```
./frontend/*
```
is deprecated 

