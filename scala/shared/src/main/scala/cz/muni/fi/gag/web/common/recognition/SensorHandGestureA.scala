/*
Copyright (c) 2018 Vojtěch Průša
*/
package cz.muni.fi.gag.web.common.recognition

import cz.muni.fi.gag.web.common.Hand.Hand
import cz.muni.fi.gag.web.common.recognition.Sensor.Sensor

abstract class SensorHandGestureA private[recognition](override val hand: Hand, var sensor: Sensor) extends HandGestureA(hand) {
  def getSensor = sensor
}