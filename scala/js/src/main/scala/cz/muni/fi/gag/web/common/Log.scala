package cz.muni.fi.gag.web.common

import cz.muni.fi.gag.web.common.shared.LogT
//import scala.scalajs.js.Dynamic.{ global => g }
import scala.scalajs.js.Dynamic.{ global => g }

object Log extends LogT {
  override def dump(msg: Any): Unit = {
    //Predef.println("dump")
    //Predef.print(msg)
    //Predef.print(msg.toString)
    dumpJS(msg.asInstanceOf[scala.scalajs.js.Any])
  }

  def dumpJS(msg: scala.scalajs.js.Any): Unit = {
    g.console.log(msg)
  }

}
