/*
Copyright (c) 2018 Vojtěch Průša
*/
package cz.muni.fi.gag.web.common.recognition

import cz.muni.fi.gag.web.common.recognition.Sensor.Sensor

abstract class SensorGestureA private[recognition](val sensor: Sensor) extends SensorGesture {
  private val parser = null

  def getSensor(): Sensor = sensor

  override def matchesBy(data: Nothing): Float = 0.5f
}