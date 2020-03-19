package cz.muni.fi.gag.web.scala.shared.visualization

import cz.muni.fi.gag.web.scala.shared.Hand
import cz.muni.fi.gag.web.scala.shared.recognition.Sensor
import cz.muni.fi.gag.web.scala.shared.common.VisualizationContextT

/**
 * Contains Hand visualization data wrapper
 * */
class HandVisualization[GeomType, QuaternionType](override val hi: Hand.Hand, override val app: VisualizationContextT[GeomType, QuaternionType])
  extends VisualizationBase(hi, app) {
  var thumbVis = new FingerVisualization[GeomType, QuaternionType](hi, this, app, 50, 50, 50, 50)
  var indexVis = new FingerVisualization[GeomType, QuaternionType](hi, this, app, 25, 112, 60, 45, 30)
  var middleVis = new FingerVisualization[GeomType, QuaternionType](hi, this, app,  0, 120, 70, 50, 35)
  var ringVis = new FingerVisualization[GeomType, QuaternionType](hi, this, app, -25, 112, 65, 48, 30)
  var littleVis = new FingerVisualization[GeomType, QuaternionType](hi, this, app, -50, 102, 45, 35, 25)

  def draw() = {
    thumbVis.draw()
    indexVis.draw()
    middleVis.draw()
    ringVis.draw()
    littleVis.draw()
    if(hi == Hand.LEFT){
      rotateY(Math.PI.toFloat);
    }
    this
  }

  def drawWholeHand(p: GeomType) = {
    setPivot(app._add(p, 0,0,0))
    app._point(0,0,0, pivot)
    draw()
    this
  }

  def getBy(s: Sensor.Sensor): VisualizationBase[GeomType, QuaternionType] = {
    s match {
      case Sensor.WRIST => {
        this
      }
      case Sensor.THUMB => {
        this.thumbVis
      }
      case Sensor.INDEX => {
        this.indexVis
      }
      case Sensor.MIDDLE => {
        this.middleVis
      }
      case Sensor.RING => {
        this.ringVis
      }
      case Sensor.LITTLE => {
        this.littleVis
      }
    }
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
    //if(q!=null) log(q)
    super.rotate(q)
    /*thumbVis.rotate(q)
    indexVis.rotate(q)
    middleVis.rotate(q)
    ringVis.rotate(q)
    littleVis.rotate(q)*/
  }

}