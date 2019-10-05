enablePlugins(ScalaJSPlugin, WorkbenchPlugin)

scalaVersion := "2.11.12"
//name := "gag-web-scala-js"
//version := "1.0-SNAPSHOT"
//organization := "cz.muni.fi.gag.web.scala"

libraryDependencies += "com.lihaoyi" %% "scalatags" % "0.6.7"
libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.7"
libraryDependencies += "org.scala-js" %% "scalajs-library" % "0.6.23"
libraryDependencies += "org.scala-js" %% "scalajs-test-interface" % "0.6.23" % Test

//libraryDependencies += "com.greencatsoft" %%% "scalajs-angular" % "0.8-SNAPSHOT"

resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases") //add resolver
libraryDependencies += "org.denigma" %%% "threejs-facade" % "0.0.77-0.1.8" //add dependency

//"org.denigma" %%% "codemirror-facade" % Versions.codemirrorFacade,

libraryDependencies += "org.denigma" %%% "binding" % "0.8.16"
libraryDependencies += "org.denigma" %%% "codemirror-facade" % "5.13.2-0.8" //add dependency
