package cz.muni.fi.gag.scala.web.visualization

import org.denigma.binding.binders.GeneralBinder
import org.denigma.binding.extensions.sq
import org.denigma.binding.views.BindableView
import org.denigma.threejs.{Material, Object3D, Quaternion}
import org.scalajs.dom.raw.HTMLElement

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}


/**
 * Just a simple view for the whole app, if interested ( see https://github.com/antonkulaga/scala-js-binding )
 */
@JSExport("HandsVisualization")
object HandsVisualization extends BindableView with scalajs.js.JSApp {

  lazy val elem: HTMLElement = org.scalajs.dom.document.body

//  type GeomType = Object3D
  type GeomType = Object3DWithMat
  type QuaternionType = Quaternion
//  GeomType<:Object3DWithMat, QuaternionType<:Quaternion

  // Object3DWithMat Object3D
  // TODO rename to more self-explanatory name in given context
  @JSExport("VisualizationModel")
  object VisualizationModel extends VisualizationData {
    def activate(): VisualizationScene[GeomType, QuaternionType] = {
      val el: HTMLElement = org.scalajs.dom.document.getElementById("container").asInstanceOf[HTMLElement]
      val demo = new VisualizationScene[GeomType, QuaternionType](el, 400, 250)
      demo.render()
      demo
    }
  }

  @JSExport("HandsVisualization.model")
  val model = VisualizationModel

  @JSExport("scene")
  var scene:VisualizationScene[GeomType, QuaternionType] = null
  //  var scene:VisualizationScene[Object3D, QuaternionType] = null


  @JSExport
  def main(): Unit = {
    this.bindView()
    scene = model.activate()
  }

  @JSExport
  def load(content: String, into: String): Unit = {
    org.scalajs.dom.document.getElementById(into).innerHTML = content
  }

  @JSExport
  def moveInto(from: String, into: String): Unit = {
    for {
      ins <- sq.byId(from)
      intoElement <- sq.byId(into)
    } {
      this.loadElementInto(intoElement, ins.innerHTML)
      ins.parentNode.removeChild(ins)
    }
  }

  withBinder(new GeneralBinder(_))

}
