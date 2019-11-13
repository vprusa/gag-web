package cz.muni.fi.gag.web.common.visualization

import cz.muni.fi.gag.web.common.Hand
import cz.muni.fi.gag.web.common.shared.VisualizationContextT

/**
 * Contains Finger visualization data wrapper
 * */
class FingerVisualization[GeomType, QuaternionType](override val hi: Hand.Hand, val hand: VisualizationBase[GeomType, QuaternionType],
                                    override val app: VisualizationContextT[GeomType, QuaternionType],
                                    var sideOffsetIn: Int, var length1: Int, var length2offset: Int, var length3offset: Int,
                                    var length4offset: Int) extends VisualizationBase[GeomType, QuaternionType](hi, app) {
  var length2 = 0
  var length3 = 0
  var length4 = 0
  var offsetY = 0
  var sideOffset = 0

  // for thumb
  def this(hi: Hand.Hand, hand:VisualizationBase[GeomType, QuaternionType], app: VisualizationContextT[GeomType, QuaternionType],
           sideOffset: Int, length1: Int, length2offset: Int, length3offset: Int) {
    this(hi,hand,app, sideOffset, length1, length2offset, length3offset, -1)
    recalcLenghts(length1, length2offset, length3offset, length4offset)
  }

  private[visualization] def recalcLenghts(length1: Int, length2offset: Int, length3offset: Int, length4offset: Int) = {
    this.length1 = length1
    this.length2 = length2offset
    this.length3 = length2 + length3offset
    if (length4offset != -1) this.length4 =  length3 + length4offset
    else this.length4 = -1
  }

  def drawStaticPart() = {
    val l = app._line(0, offsetY, 0, sideOffsetIn, this.length1, 0, hand.pivot)
    val p = app._point(sideOffsetIn, this.length1, 0, hand.pivot)
    setPivot(app._add(p, sideOffsetIn, this.length1, 0))
  }

  def drawParts() = {
    app._line(sideOffset, 0, 0, sideOffset, this.length2, 0, pivot)
    app._point(sideOffset, this.length2, 0, pivot)
    if (this.length4 == -1 || this.length4offset == -1) {
      this.app._line(sideOffset, this.length2, 0, sideOffset, this.length3, 0, pivot)
    } else {
      app._line(sideOffset, this.length2, 0, sideOffset, this.length3, 0, pivot)
      app._point(sideOffset, this.length3, 0, pivot)
      this.app._line(sideOffset, this.length3, 0, sideOffset, this.length4, 0, pivot)
    }
  }

  def draw() = {
    drawStaticPart()
    recalcLenghts(this.length1, length2offset, length3offset, length4offset)
    drawParts()
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

  override def rotate(q: QuaternionType) = {
    super.rotate(q)
  }

}