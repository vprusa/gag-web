package cz.muni.fi.gag.web.common.visualization

import cz.muni.fi.gag.web.common.Hand
import cz.muni.fi.gag.web.common.recognition.Sensor
import cz.muni.fi.gag.web.common.shared.VisualizationContextT

/**
 * Contains Hand visualization data wrapper
 * */
class HandVisualization[GeomType](override val hi: Hand.Hand, override val app: VisualizationContextT[GeomType])
  extends VisualizationBase(hi, app) {
  var thumbVis = new FingerVisualization[GeomType](hi, this, app, 50, 50, 50, 50)
  var indexVis = new FingerVisualization[GeomType](hi, this, app, 25, 112, 60, 45, 30)
  var middleVis = new FingerVisualization[GeomType](hi, this, app,  0, 120, 70, 50, 35)
  var ringVis = new FingerVisualization[GeomType](hi, this, app, -25, 112, 65, 48, 30)
  var littleVis = new FingerVisualization[GeomType](hi, this, app, -50, 102, 45, 35, 25)

  def draw() = {
    thumbVis.draw()
    indexVis.draw()
    middleVis.draw()
    ringVis.draw()
    littleVis.draw()
    this
  }

  def drawWholeHand(p: GeomType) = {
    setPivot(app._add(p, 0,0,0))
    app._point(0,0,0, pivot)
    draw()
    this
  }

  def getBy(s: Sensor.Sensor): VisualizationBase[GeomType] = {
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

}