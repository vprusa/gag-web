enablePlugins(ScalaJSPlugin, WorkbenchPlugin)

name := "Example"

version := "0.2-SNAPSHOT"

//scalaVersion := "2.13.0"
scalaVersion := "2.11.0"


//libraryDependencies += "org.scala-js" %% "scalajs-library" % "1.0.0-M8"
// https://mvnrepository.com/artifact/org.scala-js/scalajs-compiler
//libraryDependencies += "org.scala-js" %% "scalajs-compiler" % "1.0.0-M8"
// https://mvnrepository.com/artifact/org.scala-js/scalajs-compiler
//libraryDependencies += "org.scala-js" %% "scalajs-compiler" % "1.0.0-M8"
// https://mvnrepository.com/artifact/org.scala-js/scalajs-compiler
//libraryDependencies += "org.scala-js" %% "scalajs-compiler" % "0.6.27"
// https://mvnrepository.com/artifact/org.scala-js/scalajs-compiler
//libraryDependencies += "org.scala-js" %% "scalajs-compiler" % "0.6.13"

/*
libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.7",
  "com.lihaoyi" %%% "scalatags" % "0.7.0"
)
*/

//libraryDependencies ++= Seq(
  //"org.scala-js" %%% "scalajs-dom" % "1.0.0-M8"//,
  //"com.lihaoyi" %%% "scalatags" % "1.0.0-M8"
//)
// https://mvnrepository.com/artifact/com.scalatags/scalatags
//libraryDependencies += "com.scalatags" %% "scalatags" % "0.4.3-M3"
// https://mvnrepository.com/artifact/com.lihaoyi/scalatags
libraryDependencies += "com.lihaoyi" %% "scalatags" % "0.6.7"
// https://mvnrepository.com/artifact/org.scala-js/scalajs-dom
libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.7"
libraryDependencies += "org.scala-js" %% "scalajs-library" % "0.6.26"
//libraryDependencies += "org.scala-js" %% "scalajs-compiler" % "0.6.26"
//libraryDependencies += "org.scala-js" %% "scalajs-compiler" % "0.6.28"
libraryDependencies += "org.scala-js" %% "scalajs-test-interface" % "0.6.26" % Test

/*
libraryDependencies += "org.scala-js" %% "scalajs-compiler" % "0.6.13"
libraryDependencies += "org.scala-js" %% "scalajs-test-interface" % "1.0.0-M8" % Test
*/