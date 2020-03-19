package cz.muni.fi.gag.web.scala.shared.recognition

import cz.muni.fi.gag.web.scala.shared.Hand.Hand

class WholeHandGestureI private[recognition](override val hand: Hand, val data: Object) extends HandGestureA(hand) {
  var sensoresGestrues: Array[SensorOnHandGestureI] = new Array[SensorOnHandGestureI](Sensor.lenght())

  //val parser = new Nothing(file, classOf[Nothing])
  var parser = new Object()

  var i = 0
  while (i < sensoresGestrues.length) {
    sensoresGestrues(i) = new SensorOnHandGestureI(hand, Sensor.values(i), parser) {
      i += 1;
    }
  }

  /**
   * Calculate average match of matches for all sensors
   *
   * (non-Javadoc)
   *
   * @see ProcessingApplet.Basic.Gesture#matchesBy(java.util.HashMap)
   */
  @Override def matchesBy(data: Nothing) = { // because I have not used lambda in some time..
    // for (SensorOnHandGestureI sensorGesture : sensoresGestrues) {
    // float sensorMatch = sensorGesture.matches(data);
    sensoresGestrues.toStream.map((x) => x.matchesBy(data)).sum / (sensoresGestrues.length.toFloat)
  }
}