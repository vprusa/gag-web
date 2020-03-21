/*
Copyright (c) 2018 Vojtěch Průša
*/
package cz.muni.fi.gag.web.scala.shared.recognition

import cz.muni.fi.gag.web.scala.shared.Hand

class BothHandsGesture(val data: Object) extends Gesture {
  var left = new WholeHandGestureI(Hand.LEFT, data)
  var right = new WholeHandGestureI(Hand.RIGHT, data)

  @Override def matchesBy(data: Nothing) = 0.5f
}