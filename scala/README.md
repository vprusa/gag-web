# Scala module

Q: Why Scala?
A: So there is a possibility to execute same code and algorithms in Backend and Frontend

Usefull commands:

```
mvn clean install -Dcheckstyle.skip | tee app.log
```

```
sbt compile fastOptJS package publishM2
```

```
sbt fastOptJS
```

```
sbt makePom
```

```
sbt publishM2
```

# TODO

- Modularize: split shared to projects according to requirements of higher-level usage with regard to code size for JS and Native libraries