/*
Copyright (c) 2018 Vojtěch Průša
*/
package cz.muni.fi.gag.web.scala.shared.recognition

import cz.muni.fi.gag.web.scala.shared.Hand.Hand
import Sensor.Sensor

/**
 * @author Vojtech Prusa
 *
 *         Used to extend class for loading data from reference file and recognizing hand gesture for each sensor
 *
 */
abstract class SensorOnHandGestureA (val hand: Hand, val sensor: Sensor, parser: Object) extends SensorGesture with HandGesture {
  // TODO !!!
  //private var parser = null

  var data = null
  loadData()

  /*def this(hand: Nothing, sensor: Nothing, parser: Nothing) {
  //  this(hand, sensor)
    //this.parser = parser
    loadData()
  }*/

  def getSensor = sensor

  @Override def getHand = this.hand

  @Override def matchesBy(data: Nothing) = { // actually i should read from all values just those related to current
    // sensor...
    // data.
    0.5f
  }

  def loadData() = {
    /*
    parser.reset
    System.out.println("Loading ref data")
    data = new Nothing
    var rl = null
    while ( {
      (rl = parser.parseLine) != null
    }) if ((rl.sensor eq sensor) && (rl.hand eq hand)) data.add(rl)
    */
  }
}