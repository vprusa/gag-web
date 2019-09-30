enablePlugins(ScalaJSPlugin, WorkbenchPlugin)

name := "Example"

version := "0.2-SNAPSHOT"

//scalaVersion := "2.13.0"
scalaVersion := "2.11.0"

lazy val common = (project in file("common"))

libraryDependencies += "com.lihaoyi" %% "scalatags" % "0.6.7"
libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.7"
libraryDependencies += "org.scala-js" %% "scalajs-library" % "0.6.26"
libraryDependencies += "org.scala-js" %% "scalajs-test-interface" % "0.6.26" % Test

//libraryDependencies += "com.greencatsoft" %%% "scalajs-angular" % "0.7"
libraryDependencies += "com.greencatsoft" %%% "scalajs-angular" % "0.8-SNAPSHOT"

resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

