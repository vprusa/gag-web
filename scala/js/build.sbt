enablePlugins(ScalaJSPlugin, WorkbenchPlugin)

//name := "Example"

//version := "0.2-SNAPSHOT"

//scalaVersion := "2.13.0"
//scalaVersion := "2.11.0"
scalaVersion := "2.11.12"
//name := "gag-web-scala"
//version := "1.0-SNAPSHOT"
//organization := "cz.muni.fi.gag.web.scala"

//lazy val common = (project in file("common"))
// "2.11.12
/*
libraryDependencies += "com.lihaoyi" %% "scalatags" % "0.6.7"
libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.7"
libraryDependencies += "org.scala-js" %% "scalajs-library" % "0.6.26"
libraryDependencies += "org.scala-js" %% "scalajs-test-interface" % "0.6.26" % Test
*/
/*
libraryDependencies += "org.querki" %%% "jstree-facade" % "0.5" withSources() withJavadoc()
libraryDependencies += "com.lihaoyi" %% "scalatags" % "0.6.7" withSources() withJavadoc()
libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.7" withSources() withJavadoc()
libraryDependencies += "org.scala-js" %% "scalajs-library" % "0.6.23" withSources() withJavadoc()
libraryDependencies += "org.scala-js" %% "scalajs-test-interface" % "0.6.23" % Test withSources() withJavadoc()
*/
resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases") //add resolver
libraryDependencies += "org.denigma" %%% "threejs-facade" % "0.0.77-0.1.8" //add dependency
libraryDependencies += "com.lihaoyi" %% "scalatags" % "0.6.7"
libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.7"
libraryDependencies += "org.scala-js" %% "scalajs-library" % "0.6.23"
libraryDependencies += "org.scala-js" %% "scalajs-test-interface" % "0.6.23" % Test

//libraryDependencies += "com.greencatsoft" %%% "scalajs-angular" % "0.7"
//libraryDependencies += "com.greencatsoft" %%% "scalajs-angular" % "0.8-SNAPSHOT"

resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

