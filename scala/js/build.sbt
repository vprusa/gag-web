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

libraryDependencies += "org.denigma" %%% "binding" % "0.8.16"
//resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases") //add resolver
//libraryDependencies += "org.denigma" %%% "binding-controls" % "0.0.27" // to depend on html controls that are based on scala-js-binding lib

libraryDependencies += "org.denigma" %%% "codemirror-facade" % "5.13.2-0.8" //add dependency

// publish generated js file to right directory (because WF does not handle soft links well.. at all.)
lazy val copyjs = TaskKey[Unit]("copyjs", "Copy javascript files to target directory")
copyjs := {
  val outDir = baseDirectory.value / "../../app/live/gag-web-app-1.0-SNAPSHOT.WAR/public/"
  val inDir = baseDirectory.value / "target/scala-2.11"
  //val files = Seq("gag-web-scala-fastopt.js", "gag-web-scala-fastopt.js.map", "gag-web-scala-jsdeps.js") map { p =>   (inDir / p, outDir / p) }
  val files = Seq("gag-web-scala-fastopt.js", "gag-web-scala-fastopt.js.map") map { p =>   (inDir / p, outDir / p) }
  IO.copy(files, CopyOptions().withOverwrite(true))
}

//addCommandAlias("runWithJS", ";fastOptJS;copyjs;run")
addCommandAlias("fastOptJSO", ";fastOptJS;copyjs")
