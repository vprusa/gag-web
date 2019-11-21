package cz.muni.fi.gag.scala.web.visualization

import org.denigma.binding.binders.GeneralBinder
import org.denigma.binding.extensions.sq
import org.denigma.binding.views.BindableView
import org.denigma.threejs.{Object3D, Quaternion}
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement

import scala.scalajs.js.annotation.JSExport

/**
 * Just a simple view for the whole app, if interested ( see https://github.com/antonkulaga/scala-js-binding )
 */
@JSExport("HandsVisualization")
object HandsVisualization extends BindableView with scalajs.js.JSApp {

  lazy val elem: HTMLElement = dom.document.body

  @JSExport("HandsVisualization.model")
  val model = VisualizationModel

  @JSExport("scene")
  var scene:VisualizationScene[Object3D, Quaternion] = null

  @JSExport
  def main(): Unit = {
    this.bindView()
    scene = model.activate()
  }

  @JSExport
  def load(content: String, into: String): Unit = {
    dom.document.getElementById(into).innerHTML = content
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
