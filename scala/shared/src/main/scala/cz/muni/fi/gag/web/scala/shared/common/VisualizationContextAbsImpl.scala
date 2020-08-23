package cz.muni.fi.gag.web.scala.shared.common

import scala.collection.mutable.ArrayBuffer

trait VisualizationContextT[GeomType, QuaternionType] {

  // TODO ColorType setter/getter?

  def _add(geom: GeomType, x: Float, y: Float, z: Float): Option[GeomType]

  def _point(fl: Float, fl1: Float, fl2: Float, geomHolder: Option[GeomType]): GeomType

  def _line(sx: Float, sy: Float, sz: Float, ex: Float, ey: Float, ez: Float, geomHolder: Option[GeomType]): GeomType

  def _rotateGeoms(q: QuaternionType, pivot: Option[GeomType]): Unit

  def _rotateGeoms(angle: Float, pivot: Option[GeomType], axis: Axis.AxisableVal): Unit

}

abstract class VisualizationContextAbsImpl[GeomType, QuaternionType] extends VisualizationContextT[GeomType, QuaternionType] {

  def _lineWithDot(x1: Float, y1: Float, z1: Float, x2: Float, y2: Float, z2: Float, geomHolder: Option[GeomType]): ArrayBuffer[GeomType] = {
    val line = _line(x1, y1, z1, x2, y2, z2, geomHolder)
    val point = _point(x2, y2, z2, geomHolder)
    ArrayBuffer(line, point)
  }

  // https://discourse.threejs.org/t/how-do-you-rotate-a-group-of-objects-around-an-arbitrary-axis/3433/10
  // https://stackoverflow.com/questions/44287255/whats-the-right-way-to-rotate-an-object-around-a-point-in-three-js
  def _rotateGeomsX(angle: Float, pivot: Option[GeomType]): Unit = {
    _rotateGeoms(angle, pivot, Axis.X)
  }

  def _rotateGeomsY(angle: Float, pivot: Option[GeomType]): Unit = {
    _rotateGeoms(angle, pivot, Axis.Y)
  }

  def _rotateGeomsZ(angle: Float, pivot: Option[GeomType]): Unit = {
    _rotateGeoms(angle, pivot, Axis.Z)
  }

}

// https://www.scala-lang.org/old/node/11184
sealed trait Axisable {}

object Axis extends Enumeration {
  type Axises = AxisableVal

  val X = AxisableVal()
  val Y = AxisableVal()
  val Z = AxisableVal()

  class AxisableVal private[Axis]() extends Val with Axisable {}

  private def AxisableVal() = new AxisableVal()
}

