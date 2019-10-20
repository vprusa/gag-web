package cz.muni.fi.gag.web.scala.wrapper

import cz.muni.fi.gag.web.common.shared.LogT

/**
 * This class is a test for portable multi-module cross project
 * */
class Log extends LogT {
  override def dump(msg: Any): Unit = {
    if(msg != null) {
      System.out.println(msg.toString);
    }
  }

}
