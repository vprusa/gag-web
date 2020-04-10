package cz.muni.fi.gag.web.scala.shared.visualization

import cz.muni.fi.gag.web.scala.shared.Hand
import cz.muni.fi.gag.web.scala.shared.common.{LogT, VisualizationContextT}

/**
 * Vis base
 *
 * @see cz.muni.fi.gag.web.scala.shared.visualization.HandVisualization
 **/
class VisualizationBase[GeomType, QuaternionType](val hi: Hand.Hand, val app: VisualizationContextT[GeomType, QuaternionType]) {

  var pivot: Option[GeomType] = Option.empty[GeomType]

  def rotate(x: Float, y: Float, z: Float) = {
    rotateX(x)
    rotateY(y)
    rotateZ(z)
  }

  def rotate(q: QuaternionType) = {
    app._rotateGeoms(q, pivot)
  }

  def rotateX(angle: Float) = {
    app._rotateGeomsX(angle, pivot)
  }

  def rotateY(angle: Float) = {
    app._rotateGeomsY(angle, pivot)
  }

  def rotateZ(angle: Float) = {
    app._rotateGeomsZ(angle, pivot)
  }

  def setPivot(pivot: GeomType): Unit = {
    setPivot(Option(pivot))
  }

  def setPivot(pivot: Option[GeomType]): Unit = {
    this.pivot = pivot
  }

  var log: Option[LogT] = Option.empty[LogT]

  def getLog(): Option[LogT] = {
    log
  }

  def setLog(log: LogT) = {
    this.log = Option(log)
  }

  def log(msg: Any): Unit = {
    if (!getLog().isEmpty) {
      getLog().get.dump(msg)
    }
  }

}