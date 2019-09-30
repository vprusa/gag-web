// shadow sbt-scalajs' crossProject and CrossType from Scala.js 0.6.x
// https://github.com/portable-scala/sbt-crossproject
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

val sharedSettings = Seq(scalaVersion := "2.11.12")

lazy val modules =
  // select supported platforms
  crossProject(JSPlatform, JVMPlatform, NativePlatform)
    .crossType(CrossType.Pure) // [Pure, Full, Dummy], default: CrossType.Full
    .settings(sharedSettings)
    .settings(/**/)
    .jsSettings(/* ... */) // defined in sbt-scalajs-crossproject
    .jvmSettings(/* ... */)
    // configure Scala-Native settings
    .nativeSettings(/* ... */) // defined in sbt-scala-native


// Optional in sbt 1.x (mandatory in sbt 0.13.x)
lazy val scalaJS     = modules.js
lazy val scalaJVM    = modules.jvm
lazy val scalaNative = modules.native

/*
lazy val multiprojectOld =
  crossProject(JSPlatform, JVMPlatform, NativePlatform)
    .settings(sharedSettings)
    .settings(
      // %%% now include Scala Native. It applies to all selected platforms
      //libraryDependencies += "org.example" %%% "foo" % "1.2.3"
    )

// Optional in sbt 1.x (mandatory in sbt 0.13.x)
lazy val fooJS = multiprojectOld.js
lazy val fooJVM = multiprojectOld.jvm
lazy val fooNative = multiprojectOld.native
*/