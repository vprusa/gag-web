/*
Copyright (c) 2018 Vojtěch Průša
*/
package cz.muni.fi.gag.web.scala.shared.recognition

import cz.muni.fi.gag.web.scala.shared.Hand.Hand
import Sensor.Sensor

class SensorOnHandGestureI private[recognition](override val hand: Hand, override val sensor: Sensor, val parser: Object) extends SensorOnHandGestureA(hand, sensor, parser) {
  override def matchesBy(data: Nothing) = {
    // TODO Auto-generated method stub
    0
  }

  /**
   * Instead of parent method with this one I do not know if to filter
   *
   * public float matches(HashMap<Date, Quaternion> data, Sensor sensor) { //
   * actually values for other sensors should be filtered in parent method
   * matches() if (sensor != this.getSensor()) return 0; return matches(data); }
   */
}