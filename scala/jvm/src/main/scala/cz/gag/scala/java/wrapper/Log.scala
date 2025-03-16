package cz.gag.scala.java.wrapper

import cz.gag.web.scala.shared.common.LogT

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
