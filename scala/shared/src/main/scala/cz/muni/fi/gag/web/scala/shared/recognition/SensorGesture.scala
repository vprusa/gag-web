/*
Copyright (c) 2018 Vojtěch Průša
*/
package cz.muni.fi.gag.web.scala.shared.recognition

import Sensor.Sensor

trait SensorGesture extends Gesture {
  def getSensor(): Sensor
}