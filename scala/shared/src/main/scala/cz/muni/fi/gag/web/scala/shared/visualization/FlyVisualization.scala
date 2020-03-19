package cz.muni.fi.gag.web.scala.shared.visualization

import cz.muni.fi.gag.web.scala.shared.Hand
import cz.muni.fi.gag.web.scala.shared.common.VisualizationContextT

/**
 * Contains Hand visualization data wrapper
 * */
class FlyVisualization[GeomType, QuaternionType](override val hi: Hand.Hand, override val app: VisualizationContextT[GeomType, QuaternionType])
  extends VisualizationBase(hi, app) {

  def draw() = {
    if(hi == Hand.LEFT){
      rotateY(Math.PI.toFloat);
    }
    this
  }

  def drawWholeFly(p: GeomType) = {
    setPivot(app._add(p, 0,0,0))
    app._point(0,0,0, pivot)
    draw()
    this
  }

  // has to be defined otherwise not inherited via ScalaJS to JS ..
  override def rotateX(angle: Float) = {
    super.rotateX(angle)
  }

  override def rotateY(angle: Float) = {
    super.rotateY(angle)
  }

  override def rotateZ(angle: Float) = {
    super.rotateZ(angle)
  }

  override def rotate(x: Float, y: Float, z :Float) = {
    super.rotate(x,y,z)
  }
  override def rotate(q:QuaternionType) = {
    super.rotate(q)
  }

}