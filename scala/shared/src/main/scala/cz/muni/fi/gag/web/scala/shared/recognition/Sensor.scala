/*
Copyright (c) 2018 Vojtěch Průša
*/
package cz.muni.fi.gag.web.scala.shared.recognition

import cz.muni.fi.gag.web.scala.shared.recognition

object Sensor extends Enumeration {
  type Sensor = Value
  val THUMB, INDEX, MIDDLE, RING, LITTLE, WRIST = Value
  def values(i: Int): Sensor = {
    i match {
      case 0 =>  Sensor.THUMB
      case 1 =>  Sensor.INDEX
      case 2 =>  Sensor.MIDDLE
      case 3 =>  Sensor.RING
      case 4 =>  Sensor.LITTLE
      case 5 =>  Sensor.WRIST
      case _ => throw new IllegalArgumentException
    }
  }

  override def values: ValueSet = super.values

  def lenght(): Int = {6}

}