/*
Copyright (c) 2018 Vojtěch Průša
*/
package cz.muni.fi.gag.web.common.recognition

import cz.muni.fi.gag.web.common.recognition.Sensor.Sensor

trait SensorGesture extends Gesture {
  def getSensor(): Sensor
}