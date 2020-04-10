package cz.muni.fi.gag.scala.web.visualization

import org.denigma.threejs.{Object3D}

import scala.scalajs.js.annotation.{ScalaJSDefined}

object Object3DWithProps {
  implicit def toObject3D(ext: Object3DWithProps): Object3D = ext.o3D

  implicit def toPropsJsTrait(ext: Object3DWithProps): PropsJsTrait = ext.props
}

@ScalaJSDefined
class Object3DWithProps(_props: PropsJsTrait) extends scalajs.js.Object {
  var o3D: Object3D = new Object3D()
  var props: PropsJsTrait = _props

}
