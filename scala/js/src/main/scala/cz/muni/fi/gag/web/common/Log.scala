package cz.muni.fi.gag.web.common

import cz.muni.fi.gag.web.common.shared.LogT

object Log extends LogT {
  override def println(msg: String): Unit = {
    println(msg)
  }
}
