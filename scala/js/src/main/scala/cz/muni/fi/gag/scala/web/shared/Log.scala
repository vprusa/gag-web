package cz.muni.fi.gag.scala.web.shared

import cz.muni.fi.gag.scala.web.shared.Log.Level.ALL
import cz.muni.fi.gag.web.scala.shared.common.LogT

import scala.scalajs.js.Dynamic.{global => g}

object Log extends LogT {

  object Level {
    trait LogLevel
    case object NONE extends LogLevel
    case object VIS_NONE extends LogLevel
    case object VIS_MATRIX_STACK  extends LogLevel
    case object VIS_CONTEXT extends LogLevel
    case object ALL extends LogLevel
  }

  var logLevel: Level.LogLevel = Level.NONE

  def dump(msg: Any, lvl: Level.LogLevel): Unit = {
    if(lvl == logLevel || logLevel == ALL || logLevel.isInstanceOf[lvl.type]){dump(msg)}
  }

  override def dump(msg: Any): Unit = {
    dumpJS(msg.asInstanceOf[scala.scalajs.js.Any])
  }

  def dumpJS(msg: scala.scalajs.js.Any): Unit = {
    g.console.log(msg)
  }

}
