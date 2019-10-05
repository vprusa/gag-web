/*
Copyright (c) 2018 Vojtěch Průša
*/
package cz.muni.fi.gag.web.common.filters

import java.util.Date
import java.util.logging.Logger

import cz.muni.fi.gag.web.common.Hand
import cz.muni.fi.gag.web.common.Hand.Hand
import cz.muni.fi.gag.web.common.recognition.Sensor
import cz.muni.fi.gag.web.common.recognition.Sensor.Sensor

import scala.collection.immutable.HashMap
import scala.collection.mutable.ArrayBuffer

/**
 * @author Vojtech Prusa
 *
 *         This class takes filepath as input and filters just necessary data
 *         out of it based on input parameters
 *
 *         Input & output file line format:
 *
 *         <YYYY-MM-DD_HH:mm:ss.SSS> <handId> <sensorId> <q0> <q1> <q2> <q3>
 *         <a0> <a1> <a2>
 *
 *         g.e.:
 *
 *         2019-01-08_11:55:39.386 * 1 0.70202637 -0.30603027 -0.31201172
 *         0.56225586 0.0 0.0 0.0
 *
 *         TODO fix samplesPerSensorPerSecond - its messed up and do not
 *         contains values reliable considering variables name.. rename it?
 *
 */
object RecordedDataFilter { // TODO add class variables for filtering data ... do not pass them via method
  // parameters ...
  val log = Logger.getLogger(classOf[RecordedDataFilter].getSimpleName)

  /*
  def main(args: String*) = if (args.length > 1 && new File(args(1)).exists) {
    val dataFilter = new RecordedDataFilter()
    // dataFilter.filter(); // TODO add parameters
  }
  else log.log(Level.SEVERE, "Existing files path must be as first atribute!")
   */
}

class DataLineFilterWrapper(val hand: Hand, val sensor: Sensor, val date: Date = new Date()) {

  def toFileString(): String = {
    // TODO stirngified
    ""
  }
}

class RecordedDataFilter() {

  class HistoryArrayList[T](val historyCount: Int = 25) extends ArrayBuffer[T] {
    def append(e: T): Unit = {
      if (this.size > historyCount) this.remove(this.size - 1)
      if(e!=null){
        val el:T = e;
        super.append(el)
      }
    }
  }

  class BothHandsWrapper(val historyCountPerSensorOnHand: Int) {
    var lastNDataOfEachSensorOfHand: HashMap[Hand, Map[Sensor, HistoryArrayList[DataLineFilterWrapper]]] = new HashMap[Hand, HashMap[Sensor, HistoryArrayList[DataLineFilterWrapper]]]()
    var lastFilteredDataOfEachSensorOfHand: HashMap[Hand, HashMap[Sensor, DataLineFilterWrapper]] = new HashMap[Hand, HashMap[Sensor, DataLineFilterWrapper]]()

    import scala.collection.JavaConversions._

    for (h <- Hand.values) {
      val lastNDataOfEachSensor = new HashMap[Sensor, HistoryArrayList[DataLineFilterWrapper]]
      lastNDataOfEachSensorOfHand.put(h, lastNDataOfEachSensor)
      val lastFilteredDataOfEachSensor: HashMap[Sensor, DataLineFilterWrapper] = new HashMap[Sensor, DataLineFilterWrapper]
      lastFilteredDataOfEachSensorOfHand.put(h, lastFilteredDataOfEachSensor)
      import scala.collection.JavaConversions._

      for (s <- Sensor.values) { // Map<Sensor,List<DataLineFilterWrapper>>
        lastNDataOfEachSensor.put(s, new HistoryArrayList[DataLineFilterWrapper](this.historyCountPerSensorOnHand))
        lastFilteredDataOfEachSensor.put(s, null)
      }
    }

    def add(line: DataLineFilterWrapper) = lastNDataOfEachSensorOfHand.get(line.hand).get(line.sensor).add(line)

    def getTop(h: Hand, s: Sensor): DataLineFilterWrapper = get(h, s, 0)

    def get(h: Hand, s: Sensor, i: Int): DataLineFilterWrapper = {
      val size = lastNDataOfEachSensorOfHand.get(h).get.get(s).size
      if (i >= 0 && size > i) return lastNDataOfEachSensorOfHand.get(h).get.get(s).get(size - 1 - i)
      null
    }

    def getLastFiltered(h: Hand, s: Sensor): DataLineFilterWrapper = bhw.lastFilteredDataOfEachSensorOfHand.get(h).get.get(s).get

    def setLastFiltered(line: DataLineFilterWrapper) = lastFilteredDataOfEachSensorOfHand.get(line.hand).get.put(line.sensor, line)
  }

  def getDataLine(): DataLineFilterWrapper = {
    // TODO ...
    val nullT : DataLineFilterWrapper = new DataLineFilterWrapper(Hand.LEFT, Sensor.INDEX)
    nullT
  }

  var filteredData: ArrayBuffer[DataLineFilterWrapper] = null
  var bhw: BothHandsWrapper = null

  /**
   * Pls document this method.. also with ideas, TODO, etc. Parametrize whatever
   * you seem useful Using float type for samplesPerSensorPerSecond so i could
   * have gestures with rate in minutes as well
   */
  def filter(samplesPerSensorPerSecond: Float, findEdges: Boolean /* parameters */) = { // TODO Lukrecias magic
    filteredData = new ArrayBuffer[DataLineFilterWrapper]
    // assuming that DataLineFilterWrapper time has +- equal distribution rate per second it
    // sould be enough to multiple samplesPerSensorPerSecond with constant derived
    // from this rate ... g.e. 25 * 1 where 1 is sensor?
    bhw = new BothHandsWrapper(samplesPerSensorPerSecond.toInt * 2)
    // use this.parseLine() for loading per line or anything else, although it would
    // be useful to have 2 implementation
    // 1. can consume whatever amount of ram
    // 2. should not load everything in memory at once but somehow store just last n
    // lines at most... for future real-time comparing? idk here
    var line: DataLineFilterWrapper = null
    while ( {
      (line = this.getDataLine()) != null
    }) {
      if (this.isLineValid(line, samplesPerSensorPerSecond, findEdges)) {
        filteredData.append(line)
        bhw.setLastFiltered(line)
      }
      bhw.add(line)
    }
    val i = 1
  }

  private def isLineValid(line: DataLineFilterWrapper, samplesPerSensorPerSecond: Float, findEdges: Boolean): Boolean = { // check if time matches filtered samples rate per sensor on hand
    val prevLine = bhw.getLastFiltered(line.hand, line.sensor)
    val timeToSkip = (1000.0f * samplesPerSensorPerSecond).toLong
    if (prevLine == null) return true
    val lineDate = line.date.getTime
    val prevLineDate = prevLine.date.getTime
    val diffDates = lineDate - prevLineDate
    if (diffDates > timeToSkip) return true
    // TODO check if this value is edge value because then this and/or last one in
    // history of this sensor of hand has to added as well
    // using:
    // bhw.lastNDataOfEachSensorOfHand ..BothHandsWrapper.
    // bhw.get()
    // bhw.getTop(h, s)
    false
  }

  // TODO add methods that call filter(<parameters>) and name them smth like
  // filter<Basic|Minimal|Maximal|TimeOnly|etc.>(<parameters>) based on parameters
  // example
  def filterBasic() = filter(0.5f, false)

  def saveFiltered() = {
    // TODO do something with saveFiltered
    filteredData
  }
}