package cz.muni.fi.gag.scala.web.visualization

import org.denigma.threejs.{Geometry, Line, LineBasicMaterial}

import scala.scalajs.js.annotation.ScalaJSDefined

object LineWithProps {
  implicit def toLine(ext:LineWithProps): Line = ext.line
  implicit def toPropsJsTrait(ext:LineWithProps): PropsJsTrait = ext.props
}

@ScalaJSDefined
class LineWithProps(geometry: Geometry, material: LineBasicMaterial, _props: PropsJsTrait) extends scalajs.js.Object {
  var line: Line = new Line(geometry, material)
  var props: PropsJsTrait = _props
}
