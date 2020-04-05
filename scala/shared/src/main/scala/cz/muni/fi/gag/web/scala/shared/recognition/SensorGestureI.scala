/*
Copyright (c) 2018 Vojtěch Průša
*/
package cz.muni.fi.gag.web.scala.shared.recognition

import Sensor.Sensor

class SensorGestureI private[recognition](override val sensor: Sensor) extends SensorGestureA(sensor) {
  /*
        public float matches(HashMap<Date, Quaternion> data, Sensor sensor) { if
        (sensor != this.getSensor()) return 0; return matches(data); }
       */
  override def matchesBy(data: Nothing): Float = 0.5f
}

