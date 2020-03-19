/*
Copyright (c) 2018 Vojtěch Průša
*/
package cz.muni.fi.gag.web.scala.shared.recognition

import cz.muni.fi.gag.web.scala.shared.Hand.Hand
import Sensor.Sensor

abstract class SensorHandGestureA private[recognition](override val hand: Hand, var sensor: Sensor) extends HandGestureA(hand) {
  def getSensor = sensor
}