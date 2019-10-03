package example
import cz.muni.fi.gag.web.common.LogT

object Log extends LogT{
  override def println(msg: String): Unit = {
    println("Hello Scala.js") // In ES6: console.log("Hello Scala.js");
  }
}
