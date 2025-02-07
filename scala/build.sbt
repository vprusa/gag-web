// shadow sbt-scalajs' crossProject and CrossType from Scala.js 0.6.x
// https://github.com/portable-scala/sbt-crossproject
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

//scalaVersion := "2.11.12"

val sharedSettings = Seq(
  scalaVersion := "2.12.12",
  name := "gag-web-scala",
  version := "1.0-SNAPSHOT",
  organization := "cz.muni.fi.gag.web.scala"
  //,resolvers +=
  //  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)



lazy val modules =
  // select supported platforms
  crossProject(JSPlatform, JVMPlatform, NativePlatform)
//  crossProject(JVMPlatform, NativePlatform)
    .crossType(CrossType.Full) // [Pure, Full, Dummy], default: CrossType.Full
    .in(file("."))
    .settings(sharedSettings)
    .settings(/**/)
    .jsSettings(
      /*{
        libraryDependencies += "org.querki" %%% "jstree-facade" % "0.5"
        libraryDependencies += "com.lihaoyi" %% "scalatags" % "0.6.7"
        libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.7"
        libraryDependencies += "org.scala-js" %% "scalajs-library" % "0.6.26"
        libraryDependencies += "org.scala-js" %% "scalajs-test-interface" % "0.6.26" % Test
      }*/
      ) // defined in sbt-scalajs-crossproject
    .jvmSettings(/* ... */)
    // configure Scala-Native settings
//    .nativeSettings(/* ... */) // defined in sbt-scala-native


// Optional in sbt 1.x (mandatory in sbt 0.13.x)
lazy val scalaJS     = modules.js
lazy val scalaJVM    = modules.jvm
//lazy val scalaNative = modules.native

/*
lazy val modules_ =
  crossProject(JSPlatform, JVMPlatform, NativePlatform)
    .settings(sharedSettings)
    .settings(
      // %%% now include Scala Native. It applies to all selected platforms
      //libraryDependencies += "org.example" %%% "foo" % "1.2.3"
    )

// Optional in sbt 1.x (mandatory in sbt 0.13.x)
lazy val fooJS = modules_.js
lazy val fooJVM = modules_.jvm
lazy val fooNative = modules_.native
*/