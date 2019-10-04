/*
Copyright (c) 2018 Vojtěch Průša
*/
package cz.muni.fi.gag.web.common.recognition

import cz.muni.fi.gag.web.common.Hand.Hand

trait HandGesture extends Gesture {
  /**
   * returns hand for which gesture matches
   *
   * @return Hand of gesture
   */
    def getHand(): Hand
}