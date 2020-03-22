package cz.muni.fi.gag.scala.web.visualization

import org.denigma.threejs.{Color, Material, MeshBasicMaterialParameters, Object3D}

import scala.scalajs.js.annotation.{JSExport, ScalaJSDefined}

@ScalaJSDefined
class Object3DWithProps(var color: Color) extends Object3D {
}
