package cz.muni.fi.gag.web.common.visualization

import cz.muni.fi.gag.web.common.Hand
import cz.muni.fi.gag.web.common.shared.VisualizationContextT

/**
 * Vis base
 * */
class VisualizationBase[GeomType](val hi: Hand.Hand, val app: VisualizationContextT[GeomType]){

  var pivot: Option[GeomType] = Option.empty[GeomType]

  def rotate(x: Float, y: Float, z: Float) = {
    rotateX(x)
    rotateY(y)
    rotateZ(z)
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

  def setPivot(pivot: Option[GeomType]): Unit ={
    this.pivot = pivot
  }

}