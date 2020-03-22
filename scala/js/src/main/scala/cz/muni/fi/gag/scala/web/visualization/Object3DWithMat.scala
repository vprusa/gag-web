package cz.muni.fi.gag.scala.web.visualization

import org.denigma.threejs.{Material, MeshBasicMaterialParameters, Object3D}

import scala.scalajs.js.annotation.{JSExport, ScalaJSDefined}

@ScalaJSDefined
class Object3DWithMat(var mat: MeshBasicMaterialParameters) extends Object3D {
}
