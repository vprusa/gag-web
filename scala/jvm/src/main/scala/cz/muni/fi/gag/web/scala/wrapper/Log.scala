package cz.muni.fi.gag.web.scala.wrapper

import cz.muni.fi.gag.web.common.LogT

/**
 * This class is a test for portable multi-module cross project
 * */
class Log extends LogT {
  override def println(msg: String): Unit = {
    System.out.println(msg);
  }
}
