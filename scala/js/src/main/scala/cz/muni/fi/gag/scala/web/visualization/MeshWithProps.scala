package cz.muni.fi.gag.scala.web.visualization

import org.denigma.threejs.{Mesh, MeshBasicMaterial, SphereGeometry}

import scala.scalajs.js.annotation.ScalaJSDefined

object MeshWithProps {
  implicit def toMesh(ext: MeshWithProps): Mesh = ext.mesh

  implicit def toPropsJsTrait(ext: MeshWithProps): PropsJsTrait = ext.props
}

@ScalaJSDefined
class MeshWithProps(geometry: SphereGeometry, material: MeshBasicMaterial, _props: PropsJsTrait) extends scalajs.js.Object {
  var mesh: Mesh = new Mesh(geometry, material)
  var props: PropsJsTrait = _props

}
