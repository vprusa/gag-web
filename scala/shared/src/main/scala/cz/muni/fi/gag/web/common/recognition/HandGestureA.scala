/*
Copyright (c) 2018 Vojtěch Průša
*/
package cz.muni.fi.gag.web.common.recognition

import cz.muni.fi.gag.web.common.Hand.Hand

// public abstract class GestureA implements Gesture {}
abstract class HandGestureA private[recognition](val hand: Hand) extends HandGesture {
  def getHand(): Hand = {hand}
}